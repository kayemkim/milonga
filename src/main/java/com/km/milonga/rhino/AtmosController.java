package com.km.milonga.rhino;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
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
	
	Function atmosHandler;
	
	public AtmosController(Function atmosHandler) {
		this.atmosHandler = atmosHandler;
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		// prepare with processing javascript handler
		//Object[] args = {(Object) request, (Object) response};
		//NativeObject jsonObj = new NativeObject();
		Model model = new Model();
		Object[] args = {model};
    	Context context = Context.enter();
    	ScriptableObject scope = context.initStandardObjects();
    	Scriptable that = context.newObject(scope);
    	
    	// processing javascript handler
    	atmosHandler.call(context, scope, that, args);
    	
    	ModelAndView mav = new ModelAndView();
    	
    	mav.addAllObjects(model.getAllObjects());
    	
    	/*Enumeration<String> attributeNames = request.getAttributeNames();
    	while(attributeNames.hasMoreElements()) {
    		String attributeName = attributeNames.nextElement();
    		mav.addObject(attributeName, request.getAttribute(attributeName));
    	}*/
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
