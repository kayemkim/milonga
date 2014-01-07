package com.skp.milonga.servlet.handler;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.debug.Debugger;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.shell.Global;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.skp.milonga.interpret.JsUserFileListener;
import com.skp.milonga.rhino.debug.RhinoDebuggerFactory;

/**
 * Milonga Core Bean
 * 
 * @author kminkim
 *
 */
public class AtmosRequestMappingHandlerMapping extends
		RequestMappingHandlerMapping {

	public static final String ATMOS_JS_FILE_NAME = "atmos.js";

	/*
	 * storage of url-handler mapping infos
	 */
	private HandlerMappingInfoStorage handlerMappingInfoStorage = new AtmosRequestMappingInfoStorage();

	/*
	 * Atmos library file stream.
	 */
	private InputStream atmosLibraryStream = getClass().getClassLoader()
			.getResourceAsStream(ATMOS_JS_FILE_NAME);

	/*
	 * Location of user-scripting javascript files. This should be directory.
	 */
	private String[] userSourceLocations;
	
	/*
	 * state of source code auto-refreshable
	 */
	private boolean autoRefreshable;
	
	private Debugger debugger;
	
	private Global global;

	@Override
	protected void initHandlerMethods() {
		if (logger.isDebugEnabled()) {
			logger.debug("Looking for request mappings in application context: "
					+ getApplicationContext());
		}
		
		detectHandlerMethods();
		
		handlerMethodsInitialized(getHandlerMethods());
		
		processAutoRefresh();
	}	
	
	public void reInitHandlerMethods() {
		detectHandlerMethods();
		handlerMethodsInitialized(getHandlerMethods());
		logger.info("[Milonga] Refreshing Javascript source is done. All handler methods re-registered.");
	}

	/**
	 * read user-defined javascript files in configured directory, and register
	 * handler methods in those files as Spring MVC handler.
	 */
	protected void detectHandlerMethods() {

		processAtmostRequestMappingInfo();

		try {
			registerNativeFunctionHandlers(
					handlerMappingInfoStorage.getHandlerMappingInfos(),
					NativeFunctionResponseBodyHandler.class);
			registerNativeFunctionHandlers(
					handlerMappingInfoStorage.getHandlerWithViewMappingInfos(),
					NativeFunctionModelAndViewHandler.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * register handlers
	 * 
	 * @param mappingInfos
	 * @param handlerClassType
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	private void registerNativeFunctionHandlers(
			Map<String, HandlerDefinition> mappingInfos,
			Class<? extends AbstractNativeFunctionHandler> handlerClassType)
			throws SecurityException, NoSuchMethodException {

		Iterator<Entry<String, HandlerDefinition>> iterator = mappingInfos.entrySet()
				.iterator();

		while (iterator.hasNext()) {
			Entry<String, HandlerDefinition> mappingInfo = iterator.next();
			
			String url = mappingInfo.getKey();
			HandlerDefinition handlerDefinition = mappingInfo.getValue();
			
			registerNativeFunctionHandler(url, handlerDefinition, handlerClassType);
		}
	}
	
	/**
	 * register handler
	 * @param url
	 * @param handlerDefinition
	 * @param handlerClassType
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	private void registerNativeFunctionHandler(String url,
			HandlerDefinition handlerDefinition,
			Class<? extends AbstractNativeFunctionHandler> handlerClassType)
			throws SecurityException, NoSuchMethodException {
		NativeFunction atmosFunction = (NativeFunction) handlerDefinition
				.getHandler();
		Object atmosHandler = getHandler(atmosFunction, handlerClassType);
		Class<?> handlerType = (atmosHandler instanceof String) ? getApplicationContext()
				.getType((String) atmosHandler) : atmosHandler.getClass();

		if (atmosHandler instanceof NativeFunctionModelAndViewHandler) {
			((NativeFunctionModelAndViewHandler) atmosHandler)
					.setViewName(handlerMappingInfoStorage.getViewName(url));
		}

		final Class<?> userType = ClassUtils.getUserClass(handlerType);

		Method method = userType.getMethod(
				AbstractNativeFunctionHandler.HANDLER_METHOD_NAME,
				HttpServletRequest.class, HttpServletResponse.class);

		RequestMethodsRequestCondition requestMethodsRequestCondition = getRequestMethodsRequestCondition(handlerDefinition
				.getHttpMethods());

		RequestMappingInfo mapping = new RequestMappingInfo(
				new PatternsRequestCondition(url),
				requestMethodsRequestCondition, null, null, null,
				/* new ProducesRequestCondition("application/xml") */null, null);

		registerHandlerMethod(atmosHandler, method, mapping);
	}
	
	/**
	 * convert httpMethods String array to RequestMethod array
	 * 
	 * @param httpMethods
	 * @return
	 */
	private RequestMethodsRequestCondition getRequestMethodsRequestCondition(
			String[] httpMethods) {
		RequestMethod[] requestMethods = new RequestMethod[httpMethods.length];
		for (int i = 0; i < requestMethods.length; i++) {
			requestMethods[i] = RequestMethod.valueOf(httpMethods[i]);
		}
		return new RequestMethodsRequestCondition(requestMethods);
	}
	
	/**
	 * Process all user scripting javascript files in configured location, then
	 * url-handler mapping infos gotta be stored in memory.
	 */
	private void processAtmostRequestMappingInfo() {

		Context cx = Context.enter();
		global = new Global(cx);

		// javascript library loading
		/*
		 * List<String> modulePath = new ArrayList<String>();
		 * modulePath.add(getServletContextPath() + atmosLibraryLocation);
		 * global.installRequire(cx, modulePath, false);
		 */

		try {
			// optimization level -1 means interpret mode
			cx.setOptimizationLevel(-1);
			if (debugger == null) {
				debugger = RhinoDebuggerFactory.create();
			}
			//Debugger debugger = RhinoDebuggerFactory.create();
			cx.setDebugger(debugger, new Dim.ContextData());
			
			atmosLibraryStream = getClass().getClassLoader()
					.getResourceAsStream(ATMOS_JS_FILE_NAME);

			InputStreamReader isr = new InputStreamReader(atmosLibraryStream);
			
			// define Spring application context to context variable
			global.defineProperty("context", getApplicationContext(), 0);

			cx.evaluateReader(global, isr, ATMOS_JS_FILE_NAME, 1, null);

			/*
			 * execute all user scripting javascript files in configured
			 * location, then url-handler informations gotta be stored in
			 * memory.
			 */
			for (String userSourceLocation : userSourceLocations) {
				File dir = new File(getServletContextPath() + userSourceLocation);
				if (dir.isDirectory()) {
					String[] fileArray = dir.list();
					for (String fileName : fileArray) {
						File jsFile = new File(dir.getAbsolutePath() + "/"
								+ fileName);
						if (jsFile.isFile()) {
							FileReader reader = new FileReader(jsFile);

							global.defineProperty("mappingInfo",
									handlerMappingInfoStorage, 0);

							cx.evaluateReader(global, reader, fileName, 1, null);
						}
					}
				}
				else {
					FileReader reader = new FileReader(dir);

					global.defineProperty("mappingInfo",
							handlerMappingInfoStorage, 0);

					cx.evaluateReader(global, reader, dir.getName(), 1, null);
				}
			}
			atmosLibraryStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String getServletContextPath() {
		return getServletContext().getRealPath("/") + "/";
	}

	/**
	 * Convert Javascript function to Spring handler and return it.
	 * 
	 * @param atmosFunction
	 *            Javascript function
	 * @param handlerTypeClass
	 *            handler type to return
	 * @return
	 */
	private Object getHandler(NativeFunction atmosFunction,
			Class<? extends AbstractNativeFunctionHandler> handlerTypeClass) {
		Object handler = null;

		try {
			Constructor<?> handlerConst = handlerTypeClass
					.getConstructor(NativeFunction.class, Global.class);
			handler = handlerConst.newInstance(atmosFunction, global);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return handler;
	}
	
	private void processAutoRefresh() {
		if (autoRefreshable) {
			launchJsFileMonitor();
			logger.info("[Milonga] Javascript code auto-refreshable enabled.");
		} else {
			logger.info("[Milonga] Javascript code auto-refreshable disabled.");
		}
	}
	
	private void launchJsFileMonitor() {
		try {
			FileSystemManager fsManager = VFS.getManager();
			JsUserFileListener fileListener = new JsUserFileListener();
			fileListener.setApplicationContext(getApplicationContext());
			DefaultFileMonitor fileMonitor = new DefaultFileMonitor(fileListener);
			
			for (String userSourceLocation : userSourceLocations) {
				FileObject listenDir = fsManager.resolveFile(getServletContextPath() + userSourceLocation);
				fileMonitor.setRecursive(true);
				fileMonitor.addFile(listenDir);
			}
			
			fileMonitor.start();
			
		} catch (FileSystemException e) {
			logger.error(
					"[Milonga] Launching javascript source watcher is failed. Interpreter mode is not available.",
					e);
		}
	}

	/**
	 * Setter of requestMappingInfo
	 */
	public void setHandlerMappingInfoStorage(
			HandlerMappingInfoStorage handlerMappingInfoStorage) {
		this.handlerMappingInfoStorage = handlerMappingInfoStorage;
	}
	
	public HandlerMappingInfoStorage getHandlerMappingInfoStorage() {
		return handlerMappingInfoStorage;
	}

	/**
	 * Setter of userSourceLocation
	 */
	public void setUserSourceLocations(String[] userSourceLocations) {
		this.userSourceLocations = userSourceLocations;
	}
	
	public String[] getUserSourceLocations() {
		return userSourceLocations;
	}
	
	public void setAutoRefreshable(boolean autoRefreshable) {
		this.autoRefreshable = autoRefreshable;
	}

}
