package com.km.milonga.rhino;

import java.io.File;
import java.io.FileReader;
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
import org.springframework.web.servlet.mvc.Controller;

import com.km.milonga.rhino.debug.RhinoDebuggerFactory;

/**
 * Extended urlHandlerMapping which registers user-defined url-handler mapping
 * infos. The location of user-scripted sources can be configured. This
 * handlerMapping reads those source and registers handlerMappings.
 * 
 * @author kminkim
 * 
 */
public class AtmosHandlerMapping extends AbstractUrlHandlerMapping {

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
			NativeFunction jsFunction = (NativeFunction) requestMappingInfo
					.get(url);
			Controller handler = new AtmosController(jsFunction);
			registerHandler(url, handler);
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
			
			String path = getServletContextPath() + atmosLibraryLocation + "/atmos.js";
			FileReader atmosReader = new FileReader(getServletContextPath() + atmosLibraryLocation + "/atmos.js");
			cx.evaluateReader(global, atmosReader, "atmos.js", 1, null);
			
			/*
			 * execute all user scripting javascript files in configured
			 * location, then url-handler informations gotta be stored in memory.
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

}
