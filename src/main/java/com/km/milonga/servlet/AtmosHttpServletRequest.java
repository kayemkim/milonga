package com.km.milonga.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * HttpServletRequestWrapper class
 * 
 * @author kminkim
 * 
 */
public class AtmosHttpServletRequest extends HttpServletRequestWrapper {
	
	
	protected NativeWebRequest nativeWebRequest;
	
	
	public AtmosHttpServletRequest(HttpServletRequest request) {
		super(request);
		nativeWebRequest = new ServletWebRequest(request);
	}
	
	
	public Object bindObject(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class<?> clazz = Class.forName(className);
		Object object = clazz.newInstance();
		WebRequestDataBinder binder = new WebRequestDataBinder(object);
		binder.bind(nativeWebRequest);
		return binder.getTarget();
	}
	
}
