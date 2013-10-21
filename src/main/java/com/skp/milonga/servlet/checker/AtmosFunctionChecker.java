package com.skp.milonga.servlet.checker;

import java.lang.reflect.Constructor;
import java.util.List;

import org.mozilla.javascript.NativeFunction;

public class AtmosFunctionChecker {

	private List<Class<? extends FunctionArgumentConditionValidator>> validatorList;

	public void setValidatorList(
			List<Class<? extends FunctionArgumentConditionValidator>> validatorList) {
		this.validatorList = validatorList;
	}

	public Class<?> checkAndProcess(NativeFunction atmosFunction)
			throws Exception {
		for (Class<? extends FunctionArgumentConditionValidator> validator : validatorList) {
			Constructor<? extends FunctionArgumentConditionValidator> validatorConst = validator
					.getConstructor();
			FunctionArgumentConditionValidator validatorObj = validatorConst
					.newInstance();
			if (validatorObj.isValidated(atmosFunction)) {
				return validatorObj.getHandlerType();
			}
		}
		return null;
	}

}
