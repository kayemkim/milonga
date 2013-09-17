package com.km.milonga.servlet;

import java.util.Iterator;
import java.util.Map;

import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerMapping;

public class AtmosRequest extends NativeObject {

	private static final long serialVersionUID = -3554241802444383767L;
	
	private ServletWebRequest servletWebRequest;
	
	
	public AtmosRequest(ServletWebRequest servletWebRequest) {
		super();
		this.servletWebRequest = servletWebRequest;
		initializePathVariable();
		initializeRequestParameters();
	}
	

	@Override
	public Object get(String name, Scriptable start) {
		Object value = super.get(name, start);
		
		if("session".equals(name)) {
			return getSession(value);
		}
		
		if(value.equals(Scriptable.NOT_FOUND)) {
			return getBindingObject(value, name);
		}
		
		return value;
	}
	
	
	private void initializePathVariable() {
		@SuppressWarnings("unchecked")
		Map<String, String> uriTemplateVars = (Map<String, String>) servletWebRequest
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE,
						RequestAttributes.SCOPE_REQUEST);
		Iterator<Map.Entry<String, String>> iterator = uriTemplateVars.entrySet().iterator();
		while(iterator.hasNext()) {
			String key = iterator.next().getKey();
			String value = uriTemplateVars.get(key);
			defineProperty(key, value, 0);
		}
	}
	
	
	private void initializeRequestParameters() {
		@SuppressWarnings("unchecked")
		Map<String, String[]> parameterMap = servletWebRequest.getRequest().getParameterMap();
		Iterator<Map.Entry<String, String[]>> parameterIterator = parameterMap.entrySet().iterator();
		while(parameterIterator.hasNext()) {
			String key = parameterIterator.next().getKey();
			String[] value = parameterMap.get(key);
			defineProperty(key, value[0], 0);
		}
	}	
	
	
	
	private Object getSession(Object value) {
		if (value.equals(Scriptable.NOT_FOUND)) {
			AtmosSession session = new AtmosSession();
			session.storeSessionAttributes(servletWebRequest.getRequest()
					.getSession());
			this.defineProperty("session", session, 0);
			return session;
		}
		return value;
	}
	
	
	private Object getBindingObject(Object value, String name) {
		try {
			Class<?> clazz = Class.forName(name);
			Object object = clazz.newInstance();
			WebRequestDataBinder binder = new WebRequestDataBinder(object);
			binder.setIgnoreUnknownFields(false);
			binder.bind(servletWebRequest);
			value = binder.getTarget();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	

}
