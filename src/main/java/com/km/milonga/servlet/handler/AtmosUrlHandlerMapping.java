package com.km.milonga.servlet.handler;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.debug.Debugger;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.shell.Global;
import org.springframework.beans.BeansException;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import com.km.milonga.rhino.AtmosRequestMappingInfo;
import com.km.milonga.rhino.debug.RhinoDebuggerFactory;
import com.km.milonga.servlet.checker.AtmosFunctionChecker;

public class AtmosUrlHandlerMapping extends AbstractUrlHandlerMapping {

	/*
	 * storage of url-handler mapping infos
	 */
	private AtmosRequestMappingInfo requestMappingInfo;

	/*
	 * Atmos library file location.
	 */
	private String atmosLibraryLocation;

	/*
	 * Location of user-scripting javascript files. This should be directory.
	 */
	private String userSourceLocation;

	/*
	 * Checker for checking javascript function signature 
	 */
	private AtmosFunctionChecker functionChecker;

	/**
	 * Setter of requestMappingInfo
	 */
	public void setRequestMappingInfo(AtmosRequestMappingInfo requestMappingInfo) {
		this.requestMappingInfo = requestMappingInfo;
	}

	/**
	 * Setter of atmosLibraryLocation
	 */
	public void setAtmosLibraryLocation(String atmosLibraryLocation) {
		this.atmosLibraryLocation = atmosLibraryLocation;
	}

	/**
	 * Setter of userSourceLocation
	 */
	public void setUserSourceLocation(String userSourceLocation) {
		this.userSourceLocation = userSourceLocation;
	}

	/**
	 * Setter of functionChecker
	 */
	public void setFunctionChecker(AtmosFunctionChecker functionChecker) {
		this.functionChecker = functionChecker;
	}

	@Override
	public void initApplicationContext() throws BeansException {
		super.initApplicationContext();
		registerAtmosHandlers();
	}

	/**
	 * Register all handlers which have pairs of urls and Atmos handlers.
	 * 
	 * @throws BeansException
	 *             if a handler couldn't be registered
	 */
	protected void registerAtmosHandlers() throws BeansException {
		processAtmostRequestMappingInfo();

		Iterator<Entry<String, Object>> iterator = requestMappingInfo
				.iterator();
		while (iterator.hasNext()) {
			String url = iterator.next().getKey();
			NativeFunction atmosFunction = (NativeFunction) requestMappingInfo
					.get(url);

			registerJsFunctionAsHandler(url, atmosFunction);
		}
	}

	/**
	 * Process all user scripting javascript files in configured location, then
	 * url-handler mapping infos gotta be stored in memory.
	 */
	private void processAtmostRequestMappingInfo() {

		Context cx = Context.enter();
		Global global = new Global(cx);

		// javascript library loading
		List<String> modulePath = new ArrayList<String>();

		modulePath.add(getServletContextPath() + atmosLibraryLocation);
		global.installRequire(cx, modulePath, false);

		try {
			// optimization level -1 means interpret mode
			cx.setOptimizationLevel(-1);
			Debugger debugger = RhinoDebuggerFactory.create();
			cx.setDebugger(debugger, new Dim.ContextData());

			String path = getServletContextPath() + atmosLibraryLocation
					+ "/atmos.js";
			FileReader atmosReader = new FileReader(getServletContextPath()
					+ atmosLibraryLocation + "/atmos.js");
			cx.evaluateReader(global, atmosReader, "atmos.js", 1, null);

			/*
			 * execute all user scripting javascript files in configured
			 * location, then url-handler informations gotta be stored in
			 * memory.
			 */
			File dir = new File(getServletContextPath() + userSourceLocation);
			if (dir.isDirectory()) {
				String[] fileArray = dir.list();
				for (String fileName : fileArray) {
					File jsFile = new File(dir.getAbsolutePath() + "/"
							+ fileName);
					if (jsFile.isFile()) {
						FileReader reader = new FileReader(jsFile);

						AtmosRequestMappingInfo armi = WebApplicationContextUtils
								.getWebApplicationContext(getServletContext())
								.getBean(AtmosRequestMappingInfo.class);
						global.defineProperty("mappingInfo", armi, 0);

						cx.evaluateReader(global, reader, fileName, 1, null);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String getServletContextPath() {
		return getServletContext().getRealPath("/") + "/";
	}

	/**
	 * Register javascript function as a Spring handler for specific URL.
	 * 
	 * @param url
	 *            Handler URL
	 * @param atmosFunction
	 *            Javascript function to be handler
	 */
	private void registerJsFunctionAsHandler(String url,
			NativeFunction atmosFunction) {

		try {

			Class<?> handlerTypeClass = functionChecker
					.checkAndProcess(atmosFunction);
			Constructor<?> handlerConst = handlerTypeClass
					.getConstructor(NativeFunction.class);
			Object handler = handlerConst.newInstance(atmosFunction);
			registerHandler(url, handler);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
