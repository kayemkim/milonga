package com.km.milonga.rhino;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * This controller takes charge of processing the requests registered with javascript handler.
 * @author kminkim
 *
 */
public class AtmosController implements Controller {
	
	NativeFunction atmosHandler;
	
	public AtmosController(NativeFunction atmosHandler) {
		this.atmosHandler = atmosHandler;
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// prepare with processing javascript handler
		Context context = Context.enter();
    	ScriptableObject scope = context.initStandardObjects();
    	Scriptable that = context.newObject(scope);
    	
    	ModelAndView mav = new ModelAndView();
    	
    	String encodedSource = atmosHandler.getEncodedSource();
    	// arguments : request, response
    	if(atmosHandler.getLength() == 2 && encodedSource.indexOf("request") < 10 && encodedSource.indexOf("response") < 20) {
    		Object[] args = {(Object) request, (Object) response};
    		// processing javascript handler
        	atmosHandler.call(context, scope, that, args);
    		Enumeration<String> attributeNames = request.getAttributeNames();
        	while(attributeNames.hasMoreElements()) {
        		String attributeName = attributeNames.nextElement();
        		mav.addObject(attributeName, request.getAttribute(attributeName));
        	}
    	}
    	// arguments : model
    	else if (atmosHandler.getLength() == 1 && encodedSource.indexOf("model") < 10) {
    		String object = atmosHandler.getEncodedSource();
    		boolean has = atmosHandler.has("model", atmosHandler);
    		
    		Model model = new Model();
    		Object[]args = {model};
    		// processing javascript handler
        	atmosHandler.call(context, scope, that, args);
    		mav.addAllObjects(model.getAllObjects());
    	}
    	
    	return mav;
	}
	
	public class Model {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		public void setJson(Object json) {
			if(json instanceof Map)
				paramMap.putAll((Map<? extends String, ? extends Object>) json);
		}
		
		public Map<String, Object> getAllObjects() {
			return paramMap;
		}
	}

}
