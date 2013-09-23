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
import org.springframework.web.context.request.ServletWebRequest;

import com.km.milonga.servlet.AtmosCookie;
import com.km.milonga.servlet.AtmosRequest;
import com.km.milonga.servlet.AtmosResponse;

/**
 * Atmos Function is converted to this handler to be used as Spring handler.
 * This handler handles Spring Model and View.
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
		Context context = Context.enter();
		Scriptable scope = context.initStandardObjects();

		ServletWebRequest servletWebRequest = new ServletWebRequest(request);
		AtmosRequest atmosRequest = new AtmosRequest(servletWebRequest);
		AtmosResponse atmosResponse = new AtmosResponse();

		Object result = atmosFunction.call(context, scope, scope, new Object[] {
				atmosRequest, atmosResponse });

		processCookie(atmosResponse, response, scope);
		processSession(atmosRequest, request, scope);
		processRedirectOrForwardPath(atmosResponse, scope);

		return result;
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
