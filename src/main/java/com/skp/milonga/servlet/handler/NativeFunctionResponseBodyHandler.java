/*
 * Copyright 2014 K.M. Kim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skp.milonga.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.tools.shell.Global;
import org.springframework.web.bind.annotation.ResponseBody;

public class NativeFunctionResponseBodyHandler extends
		AbstractNativeFunctionHandler {

	public NativeFunctionResponseBodyHandler(NativeFunction atmosFunction, Global global) {
		super(atmosFunction, global);
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
