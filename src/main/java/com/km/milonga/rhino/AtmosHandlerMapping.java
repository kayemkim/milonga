package com.km.milonga.rhino;

import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;
import org.springframework.beans.BeansException;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;

public class AtmosHandlerMapping extends AbstractUrlHandlerMapping {
	
	private String atmosLibraryLocation;
	
	private String userSourceLocation;
	
	public void setAtmosLibraryLocation(String atmosLibraryLocation) {
		this.atmosLibraryLocation = atmosLibraryLocation;
	}
	
	public void setUserSourceLocation(String userSourceLocation) {
		this.userSourceLocation = userSourceLocation;
	}

	public AtmosRequestMappingInfo jsRequestMapping() {
		
		try {
			Context cx = Context.enter();
			Scriptable scope = cx.initStandardObjects();
			
			// Require Setting
			RequireBuilder rb = new RequireBuilder();
			List<String> modulePath = new ArrayList<String>();
			modulePath.add(atmosLibraryLocation);
			List<URI> uris = new ArrayList<URI>();
	        if (modulePath != null) {
	            for (String path : modulePath) {
	                try {
	                    URI uri = new URI(path);
	                    if (!uri.isAbsolute()) {
	                        // call resolve("") to canonify the path
	                        uri = new File(path).toURI().resolve("");
	                    }
	                    if (!uri.toString().endsWith("/")) {
	                        // make sure URI always terminates with slash to
	                        // avoid loading from unintended locations
	                        uri = new URI(uri + "/");
	                    }
	                    uris.add(uri);
	                } catch (URISyntaxException usx) {
	                    throw new RuntimeException(usx);
	                }
	            }
	        }
	        rb.setModuleScriptProvider(
	                new SoftCachingModuleScriptProvider(
	                        new UrlModuleSourceProvider(uris, null)));
	        Require require = rb.createRequire(cx, scope);
	        
	        require.install(scope);
	        
			File jsFile = new File("/Users/kminkim/Documents/workspace-sts/milonga/js/test.js");
			FileReader reader = new FileReader(jsFile);
			
			cx.evaluateReader(scope, reader, "test.js", 1, null);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return AtmosRequestMappingInfo.getInstance();
	}
	
	@Override
	public void initApplicationContext() throws BeansException {
		super.initApplicationContext();
		registerAtmosHandlers();
	}
	
	/**
	 * Register all handlers which have pairs of urls and Atmos handlers. 
	 * @throws BeansException if a handler couldn't be registered
	 */
	protected void registerAtmosHandlers() throws BeansException {
		AtmosRequestMappingInfo  requestMapping = jsRequestMapping();
		
		Iterator<Entry<String, Object>> iterator = requestMapping.iterator(); 
		while(iterator.hasNext()) {
			String url = iterator.next().getKey();
			Function jsFunction = (Function) requestMapping.get(url);
			Controller handler = new AtmosController(jsFunction);
			registerHandler(url, handler);
		}
	}

}
