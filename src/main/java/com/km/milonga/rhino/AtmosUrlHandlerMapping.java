package com.km.milonga.rhino;

import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;

public class AtmosUrlHandlerMapping extends SimpleUrlHandlerMapping {
	
	@Bean
	public AtmosRequestMappingInfo jsRequestMapping() {
		
		try {
			Context cx = Context.enter();
			Scriptable scope = cx.initStandardObjects();
			
			// Require Setting
			RequireBuilder rb = new RequireBuilder();
			List<String> modulePath = new ArrayList<String>();
			modulePath.add("/Users/kminkim/Documents/workspace-sts/milonga/js/atmos");
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
	
	/**
	 * Register all handlers specified in the URL map for the corresponding paths.
	 * @param urlMap Map with URL paths as keys and handler beans or bean names as values
	 * @throws BeansException if a handler couldn't be registered
	 * @throws IllegalStateException if there is a conflicting handler registered
	 */
	@Override
	protected void registerHandlers(Map<String, Object> urlMap) throws BeansException {
		AtmosRequestMappingInfo  requestMapping = jsRequestMapping();
		
		Iterator<Entry<String, Object>> iterator = requestMapping.iterator(); 
		while(iterator.hasNext()) {
			String url = iterator.next().getKey();
			Function jsFunction = (Function) requestMapping.get(url);
			Controller handler = new AtmosController(jsFunction);
			registerHandler(url, handler);
		}
	}
	
	
	
	/*@Override
	public void initApplicationContext() throws BeansException {
		super.initApplicationContext();
		registerAtmosHandlers();
	}

	protected void registerAtmosHandlers() {
		AtmosRequestMappingInfo  requestMapping = jsRequestMapping();
		
		Iterator<Entry<String, Object>> iterator = requestMapping.iterator(); 
		while(iterator.hasNext()) {
			String url = iterator.next().getKey();
			Function jsFunction = (Function) requestMapping.get(url);
			Controller handler = new AtmosController(jsFunction);
			registerHandler(url, handler);
		}
	}*/

}
