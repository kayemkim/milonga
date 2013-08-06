package com.km.milonga.servlet.checker;

import org.mozilla.javascript.NativeFunction;

public abstract class FunctionArgumentConditionValidator {

	public abstract boolean isValidated(NativeFunction atmosFunction);

	public abstract Class<?> getHandlerType();

}
