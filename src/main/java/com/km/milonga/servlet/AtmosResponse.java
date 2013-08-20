package com.km.milonga.servlet;

import java.util.HashMap;
import java.util.Map;

public class AtmosResponse {
	
	private Map<String, String> cookie = new HashMap<String, String>();
	
	private String redirect;
	
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

}
