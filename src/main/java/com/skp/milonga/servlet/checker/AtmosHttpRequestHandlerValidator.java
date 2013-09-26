package com.skp.milonga.servlet.checker;

import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.debug.DebuggableScript;

import com.skp.milonga.servlet.handler.AtmosHttpRequestHandler;

public class AtmosHttpRequestHandlerValidator extends
		FunctionArgumentConditionValidator {

	@Override
	public boolean isValidated(NativeFunction atmosFunction) {
		DebuggableScript dScript = atmosFunction.getDebuggableView();
		return dScript.getParamCount() == 1
				&& dScript.getParamOrVarName(0).equals("request");
	}

	@Override
	public Class<?> getHandlerType() {
		return AtmosHttpRequestHandler.class;
	}

}
