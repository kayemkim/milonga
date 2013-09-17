package com.km.milonga.servlet.handler;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerMapping;

import com.km.milonga.servlet.AtmosHttpServletRequest;
import com.km.milonga.servlet.AtmosHttpServletResponse;
import com.km.milonga.servlet.AtmosRequest;
import com.sun.org.apache.bcel.internal.generic.RETURN;

/**
 * Atmos Function is converted to this handler to be used as Spring
 * handler. This handler handles Spring Model and View.
 * 
 * @author kminkim
 * 
 */
public abstract class AbstractNativeFunctionHandler {
	
	public static final String HANDLER_METHOD_NAME = "handle";
	
	private NativeFunction atmosFunction;
	
	private String redirectPath = null;
	private String forwardPath = null;
	
	
	public AbstractNativeFunctionHandler(NativeFunction atmosFunction) {
		this.atmosFunction = atmosFunction;
	}
	
	/**
	 * Handler method
	 * 
	 * @param request
	 * @param response
	 * @return	handler return value
	 */
	public abstract Object handle(HttpServletRequest request,
			HttpServletResponse response);
	
	
	
	/**
	 * Call Javascript Native Function
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	protected Object callNativeFunction(HttpServletRequest request, HttpServletResponse response) {
		Context context = Context.enter();
		Scriptable scope = context.initStandardObjects();
		
		AtmosHttpServletResponse atmosResponse = new AtmosHttpServletResponse(response);
		
		ServletWebRequest servletWebRequest = new ServletWebRequest(request);
		AtmosRequest atmosRequest = new AtmosRequest(servletWebRequest);
		
		Object result = atmosFunction.call(context, scope, scope, new Object[] {
				atmosRequest, atmosResponse });
		
		setCookie(atmosResponse, response);
		setSession(atmosRequest, request, scope);
		
		redirectPath = atmosResponse.getRedirect();
		forwardPath = atmosResponse.getForward();
		
		return result;
	}
	
	
	private void setCookie(AtmosHttpServletResponse atmosResponse, HttpServletResponse response) {
		Map<String, String> cookieMap = atmosResponse.getCookie();
		if (cookieMap == null) {
			return;
		}

		Iterator<Entry<String, String>> iterator = cookieMap.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			String name = iterator.next().getKey();
			Cookie cookie = new Cookie(name, cookieMap.get(name));
			response.addCookie(cookie);
		}
	}
	
	
	private void setSession(AtmosRequest atmosRequest, HttpServletRequest request, Scriptable start) {
		@SuppressWarnings("unchecked")
		Map<String, Object> sessionMap = (NativeObject) atmosRequest.get("session", start);
		
		if (sessionMap == null) {
			return;
		}
		
		Iterator<Entry<String, Object>> iterator = sessionMap.entrySet()
				.iterator();
		
		while(iterator.hasNext()) {
			String name = iterator.next().getKey();
			request.getSession().setAttribute(name, sessionMap.get(name));
		}
	}
	
	protected String getRedirectPath() {
		return redirectPath;
	}
	
	protected String getForwardPath() {
		return forwardPath;
	}
	
}
