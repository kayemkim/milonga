package com.km.milonga.servlet;

import java.util.HashMap;
import java.util.Map;

public class AtmosResponse {
	
	private Map<String, String> cookie = new HashMap<String, String>();
	
	public void setCookie(String name, String value) {
		cookie.put(name, value);
	}
	
	public Map<String, String> getCookie() {
		return cookie;
	}

}
