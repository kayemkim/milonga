package com.km.milonga.servlet;

import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

/**
 * HttpServletResponse replacement class
 * 
 * @author kminkim
 * 
 */
public class AtmosResponse extends NativeObject {

	public static final String COOKIE = "cookie";
	public static final String REDIRECT_PATH = "redirect";
	public static final String FORWARD_PATH = "forward";

	private static final long serialVersionUID = -2400217300316282572L;

	@Override
	public Object get(String name, Scriptable start) {
		Object value = super.get(name, start);
		if (COOKIE.equals(name)) {
			return getCookie(value);
		}
		return value;
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
