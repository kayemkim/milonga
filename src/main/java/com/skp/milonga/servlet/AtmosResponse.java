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

package com.skp.milonga.servlet;

import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * HttpServletResponse replacement class
 * 
 * @author kminkim
 * 
 */
public class AtmosResponse extends NativeObject {

	public static final String REDIRECT_FUNCTION_NAME = "redirect";
	public static final String FORWARD_FUNCTION_NAME = "forward";
	
	public static final String COOKIE = "cookie";
	public static final String REDIRECT_PATH = "redirectPath";
	public static final String FORWARD_PATH = "forwardPath";

	private static final long serialVersionUID = -2400217300316282572L;
	
	public AtmosResponse() {
		super();
		this.defineFunctionProperties(new String[] { REDIRECT_FUNCTION_NAME,
				FORWARD_FUNCTION_NAME }, this.getClass(),
				ScriptableObject.DONTENUM);
	}
	
	@Override
	public Object get(String name, Scriptable start) {
		Object value = super.get(name, start);
		if (COOKIE.equals(name)) {
			return getCookie(value);
		}
		return value;
	}
	
	public void redirect(String redirectPath) {
		put(REDIRECT_PATH, this, redirectPath);
	}
	
	public void forward(String forwardPath) {
		put(FORWARD_PATH, this, forwardPath);
	}

	private Object getCookie(Object value) {
		if (value.equals(Scriptable.NOT_FOUND)) {
			AtmosCookie cookie = new AtmosCookie();
			defineProperty(COOKIE, cookie, 0);
			return cookie;
		}
		return value;
	}

}
