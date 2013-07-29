package com.km.milonga.rhino;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.debug.DebuggableScript;
import org.springframework.web.servlet.ModelAndView;

public class JsgiStyleCheckPolicy extends ArgumentCheckPolicy {

	public JsgiStyleCheckPolicy(int argumentsCount, Object[] args) {
		super(argumentsCount, args);
	}

	@Override
	public boolean isValidated(NativeFunction atmosHandler) {
		DebuggableScript dScript = atmosHandler.getDebuggableView();
		if (dScript.getParamCount() == 2
				&& dScript.getParamOrVarName(0).equals("request")
				&& dScript.getParamOrVarName(1).equals("response")) {
			return true;
		}
		return false;
	}

	@Override
	public ModelAndView apply(NativeFunction atmosHandler, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();

		atmosHandler.call(context, scope, scope, args);
		HttpServletRequest request = (HttpServletRequest) args[0];
		// HttpServletResponse response = (HttpServletResponse) args[1];
		Enumeration<String> attributeNames = request.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attributeName = attributeNames.nextElement();
			mav.addObject(attributeName, request.getAttribute(attributeName));
		}

		return mav;
	}

}
