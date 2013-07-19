package com.km.milonga.rhino;

import java.util.Map;

import org.mozilla.javascript.NativeFunction;
import org.springframework.web.servlet.ModelAndView;

public class JsonStyleCheckPolicy extends ArgumentCheckPolicy {

	public JsonStyleCheckPolicy(int argumentsCount, Object[] args) {
		super(argumentsCount, args);
	}

	@Override
	public boolean isOk(NativeFunction atmosHandler) {
		String encodedSource = atmosHandler.getEncodedSource();
		return atmosHandler.getLength() == 1
				&& encodedSource.indexOf("request") < 10;
	}

	@Override
	public ModelAndView result(NativeFunction atmosHandler) {
		ModelAndView mav = new ModelAndView();

		// processing javascript handler
		Map<String, Object> result = (Map<String, Object>) atmosHandler.call(
				context, scope, scope, args);
		mav.addAllObjects(result);

		return mav;
	}

}
