package com.km.milonga.servlet.handler;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.debug.Debugger;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.shell.Global;
import org.springframework.beans.BeansException;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import com.km.milonga.rhino.AtmosRequestMappingInfoStorage;
import com.km.milonga.rhino.debug.RhinoDebuggerFactory;
import com.km.milonga.servlet.checker.AtmosFunctionChecker;

public class AtmosUrlHandlerMapping extends AbstractUrlHandlerMapping {

	/*
	 * storage of url-handler mapping infos
	 */
	private AtmosRequestMappingInfoStorage requestMappingInfo;

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
	public void setRequestMappingInfo(AtmosRequestMappingInfoStorage requestMappingInfo) {
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
			//url = url + ".*";
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
			FileReader atmosReader = new FileReader(path);
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

						AtmosRequestMappingInfoStorage armi = WebApplicationContextUtils
								.getWebApplicationContext(getServletContext())
								.getBean(AtmosRequestMappingInfoStorage.class);
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
	
	
	/**
	 * Look up a handler instance for the given URL path.
	 * <p>Supports direct matches, e.g. a registered "/test" matches "/test",
	 * and various Ant-style pattern matches, e.g. a registered "/t*" matches
	 * both "/test" and "/team". For details, see the AntPathMatcher class.
	 * <p>Looks for the most exact pattern, where most exact is defined as
	 * the longest path pattern.
	 * @param urlPath URL the bean is mapped to
	 * @param request current HTTP request (to expose the path within the mapping to)
	 * @return the associated handler instance, or {@code null} if not found
	 * @see #exposePathWithinMapping
	 * @see org.springframework.util.AntPathMatcher
	 */
	@Override
	protected Object lookupHandler(String urlPath, HttpServletRequest request) throws Exception {
		// Direct match?
		Object handler = getHandlerMap().get(urlPath);
		if (handler != null) {
			// Bean name or resolved handler?
			if (handler instanceof String) {
				String handlerName = (String) handler;
				handler = getApplicationContext().getBean(handlerName);
			}
			validateHandler(handler, request);
			return buildPathExposingHandler(handler, urlPath, urlPath, null);
		}
		// Pattern match?
		List<String> matchingPatterns = new ArrayList<String>();
		for (String registeredPattern : getHandlerMap().keySet()) {
			if (getPathMatcher().match(registeredPattern, urlPath)) {
				matchingPatterns.add(registeredPattern);
			}
		}
		String bestPatternMatch = null;
		Comparator<String> patternComparator = getPathMatcher().getPatternComparator(urlPath);
		if (!matchingPatterns.isEmpty()) {
			Collections.sort(matchingPatterns, patternComparator);
			if (logger.isDebugEnabled()) {
				logger.debug("Matching patterns for request [" + urlPath + "] are " + matchingPatterns);
			}
			bestPatternMatch = matchingPatterns.get(0);
		}
		if (bestPatternMatch != null) {
			handler = getHandlerMap().get(bestPatternMatch);
			// Bean name or resolved handler?
			if (handler instanceof String) {
				String handlerName = (String) handler;
				handler = getApplicationContext().getBean(handlerName);
			}
			validateHandler(handler, request);
			String pathWithinMapping = getPathMatcher().extractPathWithinPattern(bestPatternMatch, urlPath);

			// There might be multiple 'best patterns', let's make sure we have the correct URI template variables
			// for all of them
			Map<String, String> uriTemplateVariables = new LinkedHashMap<String, String>();
			for (String matchingPattern : matchingPatterns) {
				if (patternComparator.compare(bestPatternMatch, matchingPattern) == 0) {
					
					boolean hasSuffix = bestPatternMatch.indexOf('.') != -1;
					if (!hasSuffix && getPathMatcher().match(bestPatternMatch + ".*", urlPath)) {
						matchingPattern += ".*";
					}
					
					//Map<String, String> vars = getPathMatcher().extractUriTemplateVariables(matchingPattern, urlPath);
					Map<String, String> vars = getPathMatcher().extractUriTemplateVariables(matchingPattern, urlPath);
					Map<String, String> decodedVars = getUrlPathHelper().decodePathVariables(request, vars);
					uriTemplateVariables.putAll(decodedVars);
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("URI Template variables for request [" + urlPath + "] are " + uriTemplateVariables);
			}
			return buildPathExposingHandler(handler, bestPatternMatch, pathWithinMapping, uriTemplateVariables);
		}
		// No handler found...
		return null;
	}
	
	
	
}
