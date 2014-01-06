package com.skp.milonga.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.tools.shell.Global;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.ResponseBody;

public class NativeFunctionResponseBodyHandler extends
		AbstractNativeFunctionHandler {

	public NativeFunctionResponseBodyHandler(NativeFunction atmosFunction,
			ApplicationContext applicationContext, Global global) {
		super(atmosFunction, applicationContext, global);
	}

	@ResponseBody
	@Override
	public Object handle(HttpServletRequest request,
			HttpServletResponse response) {
		Object result = callNativeFunction(request, response);
		
		if (result instanceof NativeJavaObject) {
			return ((NativeJavaObject) result).unwrap();
		}
		
		return result;
	}

}
