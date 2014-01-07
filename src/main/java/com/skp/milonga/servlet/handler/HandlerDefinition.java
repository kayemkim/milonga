package com.skp.milonga.servlet.handler;

public class HandlerDefinition {
	
	private Object handler;
	
	private String[] httpMethods;
	
	public HandlerDefinition(Object handler, String[] httpMethods) {
		this.handler = handler;
		this.httpMethods = httpMethods;
	}

	public Object getHandler() {
		return handler;
	}	

	public String[] getHttpMethods() {
		return httpMethods;
	}	
}
