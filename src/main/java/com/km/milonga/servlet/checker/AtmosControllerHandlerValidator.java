package com.km.milonga.servlet.checker;

import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.debug.DebuggableScript;

import com.km.milonga.servlet.handler.AtmosControllerHandler;

public class AtmosControllerHandlerValidator extends
		FunctionArgumentConditionValidator {

	@Override
	public boolean isValidated(NativeFunction atmosFunction) {
		DebuggableScript dScript = atmosFunction.getDebuggableView();
		return dScript.getParamCount() == 2
				&& dScript.getParamOrVarName(0).equals("request")
				&& dScript.getParamOrVarName(1).equals("response");
	}

	@Override
	public Class<?> getHandlerType() {
		return AtmosControllerHandler.class;
	}

}
