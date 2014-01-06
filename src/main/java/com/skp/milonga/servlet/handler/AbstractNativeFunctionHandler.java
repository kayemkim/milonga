package com.skp.milonga.servlet.handler;

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
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerMapping;

import com.skp.milonga.servlet.AtmosCookie;
import com.skp.milonga.servlet.AtmosRequest;
import com.skp.milonga.servlet.AtmosResponse;

/**
 * Atmos Function is converted to this handler to be used as Spring handler.
 * This handler handles Spring Model and View.
 * 
 * @author kminkim
 * 
 */
public abstract class AbstractNativeFunctionHandler implements ApplicationContextAware {

	public static final String HANDLER_METHOD_NAME = "handle";

	private NativeFunction atmosFunction;

	private String redirectPath = null;
	private String forwardPath = null;
	
	private ApplicationContext applicationContext;
	
	private Global global;

	public AbstractNativeFunctionHandler(NativeFunction atmosFunction, ApplicationContext applicationContext, Global global) {
		this.atmosFunction = atmosFunction;
		this.applicationContext = applicationContext;
		this.global = global;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	protected ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * Handler method
	 * 
	 * @param request
	 * @param response
	 * @return handler return value
	 */
	public abstract Object handle(HttpServletRequest request,
			HttpServletResponse response);
	
	protected String getRedirectPath() {
		return redirectPath;
	}

	protected String getForwardPath() {
		return forwardPath;
	}

	/**
	 * Call Javascript Native Function
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	protected Object callNativeFunction(HttpServletRequest request,
			HttpServletResponse response) {
		
		ServletWebRequest servletWebRequest = new ServletWebRequest(request);
		AtmosRequest atmosRequest = new AtmosRequest(servletWebRequest);
		AtmosResponse atmosResponse = new AtmosResponse();
		
		Context context = Context.enter();
		ScriptableObject scope = (ScriptableObject) context.initStandardObjects(global);
		
		injectPathVariables(servletWebRequest, scope);
		
		atmosFunction.setParentScope(scope);
		Object result = atmosFunction.call(context, scope, atmosFunction, new Object[] {
				atmosRequest, atmosResponse });
		
		processCookie(atmosResponse, response, scope);
		processSession(atmosRequest, request, scope);
		processRedirectOrForwardPath(atmosResponse, scope);

		return result;
	}
	
	@SuppressWarnings("unchecked")
	private void injectPathVariables(ServletWebRequest servletWebRequest, ScriptableObject scope) {
		Map<String, String> uriTemplateVars = (Map<String, String>) servletWebRequest
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE,
						RequestAttributes.SCOPE_REQUEST);
		Iterator<Map.Entry<String, String>> iterator = uriTemplateVars
				.entrySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next().getKey();
			String value = uriTemplateVars.get(key);
			scope.defineProperty(key, value, ScriptableObject.DONTENUM);
		}
	}

	private void processCookie(AtmosResponse atmosResponse,
			HttpServletResponse response, Scriptable start) {
		@SuppressWarnings("unchecked")
		Map<String, String> cookieMap = (AtmosCookie) atmosResponse.get(
				AtmosResponse.COOKIE, start);
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

	private void processSession(AtmosRequest atmosRequest,
			HttpServletRequest request, Scriptable start) {
		@SuppressWarnings("unchecked")
		Map<String, Object> sessionMap = (NativeObject) atmosRequest.get(
				AtmosRequest.SESSION, start);

		if (sessionMap == null) {
			return;
		}

		Iterator<Entry<String, Object>> iterator = sessionMap.entrySet()
				.iterator();

		while (iterator.hasNext()) {
			String name = iterator.next().getKey();
			request.getSession().setAttribute(name, sessionMap.get(name));
		}
	}

	private void processRedirectOrForwardPath(AtmosResponse atmosResponse,
			Scriptable start) {
		Object redirectVal = atmosResponse.get(AtmosResponse.REDIRECT_PATH,
				start);
		Object forwardVal = atmosResponse
				.get(AtmosResponse.FORWARD_PATH, start);

		if (!redirectVal.equals(Scriptable.NOT_FOUND)) {
			redirectPath = (String) redirectVal;
		}

		if (!forwardVal.equals(Scriptable.NOT_FOUND)) {
			forwardPath = (String) forwardVal;
		}
	}

}
