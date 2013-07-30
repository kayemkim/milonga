package com.km.milonga.servlet.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * Atmos Function is converted to this controller to be used like Spring
 * Controller. This controller handles {@link HttpServletResponse}.
 * 
 * @author kminkim
 *
 */
public class AtmosHttpRequestHandler {

	private NativeFunction atmosFunction;
	
	public AtmosHttpRequestHandler(NativeFunction atmosHandler) {
		this.atmosFunction = atmosHandler;
	}

	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Context context = Context.enter();
		Scriptable scope = context.initStandardObjects();

		NativeObject result = (NativeObject) atmosFunction.call(context, scope,
				scope, new Object[] { request });
		
		PrintWriter out = response.getWriter();
		if(isResponse(result)) {
			Object responseContent = result.get("content");
			out.print((String) responseContent);
			
			setCookie(result, response);
		}
		else {
			response.setContentType("application/json");
			ObjectMapper om = new ObjectMapper();
			String jsonStr = om.writeValueAsString(result);
			out.print(jsonStr);
		}
		out.flush();
	}
	
	
	/**
	 * set cookie from result of handler to HttpServletResponse
	 * 
	 * @param result
	 *            return object of handler
	 * @param response
	 *            HttpServletResponse
	 */
	private void setCookie(NativeObject result, HttpServletResponse response) {
		Map<String, String> cookieMap = ScriptableObject.getTypedProperty(
				result, "cookie", Map.class);
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
	
	
	private boolean isResponse(NativeObject result) {
		Object[] members = new Object[] { "cookie", "getContent", "setContent",
				"setCookie" };
		return Arrays.equals(result.getPrototype().getIds(), members);
	}

}
