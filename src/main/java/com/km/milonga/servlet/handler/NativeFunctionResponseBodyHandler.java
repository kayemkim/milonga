package com.km.milonga.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeJavaObject;
import org.springframework.web.bind.annotation.ResponseBody;

public class NativeFunctionResponseBodyHandler extends
		AbstractNativeFunctionHandler {

	public NativeFunctionResponseBodyHandler(NativeFunction atmosFunction) {
		super(atmosFunction);
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
