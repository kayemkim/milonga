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
