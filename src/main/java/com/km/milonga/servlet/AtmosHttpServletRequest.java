package com.km.milonga.servlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerMapping;

/**
 * HttpServletRequestWrapper class
 * 
 * @author kminkim
 * 
 */
public class AtmosHttpServletRequest extends HttpServletRequestWrapper {
	
	
	protected NativeWebRequest nativeWebRequest;
	
	
	/**
	 * Constructor
	 * 
	 * @param request	HttpServletRequest
	 */
	public AtmosHttpServletRequest(HttpServletRequest request) {
		super(request);
		nativeWebRequest = new ServletWebRequest(request);
	}
	
	
	/**
	 * getting data object whose type is bean-like class.
	 * Data from request will be bound to this object.
	 * 
	 * @param className		object class type name
	 * @return				binding object
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public Object bindObject(String className) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		Class<?> clazz = Class.forName(className);
		Object object = clazz.newInstance();
		WebRequestDataBinder binder = new WebRequestDataBinder(object);
		binder.setIgnoreUnknownFields(false);
		binder.bind(nativeWebRequest);
		return binder.getTarget();
	}
	
	
	/**
	 * getting PathVarible value in URI
	 * 
	 * @param varName	PathVairalbe name
	 * @return			PathVariable value
	 */
	@SuppressWarnings("unchecked")
	public Object resolvePathVariable(String varName) {
		Map<String, String> uriTemplateVars = (Map<String, String>) nativeWebRequest
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE,
						RequestAttributes.SCOPE_REQUEST);
		return (uriTemplateVars != null) ? uriTemplateVars.get(varName) : null;
	}
	
}
