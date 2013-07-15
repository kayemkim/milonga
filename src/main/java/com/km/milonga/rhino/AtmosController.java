package com.km.milonga.rhino;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class AtmosController implements Controller {
	
	Function jsHandler;
	
	public AtmosController(Function jsHandler) {
		this.jsHandler = jsHandler;
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mv = new ModelAndView();
		
		// Javascript Handler 실행을 위한 준비
		Object[] args = {(Object) request, (Object) response};
    	Context context = Context.enter();
    	ScriptableObject scope = context.initStandardObjects();
    	Scriptable that = context.newObject(scope);
    	
    	// Javascript Handler 실행
    	Object result = jsHandler.call(context, scope, that, args);
		
    	return mv;
	}

}
