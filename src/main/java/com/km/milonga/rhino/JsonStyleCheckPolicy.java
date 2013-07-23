package com.km.milonga.rhino;

import java.util.Map;

import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.debug.DebuggableScript;
import org.springframework.web.servlet.ModelAndView;

public class JsonStyleCheckPolicy extends ArgumentCheckPolicy {

	public JsonStyleCheckPolicy(int argumentsCount, Object[] args) {
		super(argumentsCount, args);
	}

	@Override
	public boolean isValidated(NativeFunction atmosHandler) {
		DebuggableScript dScript = atmosHandler.getDebuggableView();
		if (dScript.getParamCount() == 1
				&& dScript.getParamOrVarName(0).equals("request")) {
			return true;
		}
		return false;
	}

	@Override
	public ModelAndView apply(NativeFunction atmosHandler) {
		ModelAndView mav = new ModelAndView();

		// processing javascript handler
		Map<String, Object> result = (Map<String, Object>) atmosHandler.call(
				context, scope, scope, args);
		mav.addAllObjects(result);

		return mav;
	}

}
