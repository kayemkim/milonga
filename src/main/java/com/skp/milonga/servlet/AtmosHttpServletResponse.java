package com.skp.milonga.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class AtmosHttpServletResponse extends HttpServletResponseWrapper {
	
	public AtmosHttpServletResponse(HttpServletResponse response) {
		super(response);
	}

	private Map<String, String> cookie = new HashMap<String, String>();
	
	private String redirect;
	
	private String forward;
	
	public void setCookie(String name, String value) {
		cookie.put(name, value);
	}
	
	public Map<String, String> getCookie() {
		return cookie;
	}
	
	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}
	
	public String getRedirect() {
		return redirect;
	}
	
	public void setForward(String forward) {
		this.forward = forward;
	}
	
	public String getForward() {
		return forward;
	}
}
