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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.tools.shell.Global;
import org.springframework.web.servlet.ModelAndView;

public class NativeFunctionModelAndViewHandler extends
		AbstractNativeFunctionHandler {

	private String viewName = null;
	
	public NativeFunctionModelAndViewHandler(NativeFunction atmosFunction, Global global) {
		super(atmosFunction, global);
	}	

	@SuppressWarnings("unchecked")
	@Override
	public Object handle(HttpServletRequest request,
			HttpServletResponse response) {
		Object result = callNativeFunction(request, response);

		ModelAndView mv = new ModelAndView();
		
		if (result instanceof Undefined) {
			
		} else {
			if (result instanceof NativeObject) {
				// in case that return type is Javascript object
				Iterator<Entry<Object, Object>> i = ((NativeObject) result)
						.entrySet().iterator();
				while (i.hasNext()) {
					Entry<Object, Object> e = i.next();
					String key = e.getKey().toString();
					mv.addObject(key, e.getValue());
				}
			} else if (result instanceof NativeJavaObject) {
				// in case that return type is Java object
				ObjectMapper om = new ObjectMapper();
				Map<String, Object> convertedResult = om.convertValue(
						((NativeJavaObject) result).unwrap(), Map.class);
				mv.getModelMap().mergeAttributes(convertedResult);
			} else {
				// in case of Java Bean object
				ObjectMapper om = new ObjectMapper();
				Map<String, Object> convertedResult = om.convertValue(result,
						Map.class);
				mv.getModelMap().mergeAttributes(convertedResult);
			}
		}
		

		mv.setViewName(viewName);

		// TODO considering order of priority
		if (getRedirectPath() != null) {
			mv.setViewName("redirect:" + getRedirectPath());
		} else if (getForwardPath() != null) {
			mv.setViewName("forward:" + getForwardPath());
		}

		return mv;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

}
