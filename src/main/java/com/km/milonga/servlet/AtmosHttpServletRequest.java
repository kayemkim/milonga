package com.km.milonga.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class AtmosHttpServletRequest extends HttpServletRequestWrapper {

	public AtmosHttpServletRequest(HttpServletRequest request) {
		super(request);
	}
	
}
