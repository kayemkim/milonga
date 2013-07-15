package com.km.milonga.rhino;

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
		Object[] args = {(Object) request, (Object) response};
    	Context context = Context.enter();
    	ScriptableObject scope = context.initStandardObjects();
    	Scriptable that = context.newObject(scope);
    	
    	// processing javascript handler
    	atmosHandler.call(context, scope, that, args);
		
    	return new ModelAndView();
	}

}
