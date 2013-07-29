package com.km.milonga.rhino;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.debug.DebuggableScript;
import org.springframework.web.servlet.ModelAndView;

public class JsonStyleCheckPolicy extends ArgumentCheckPolicy {

	public JsonStyleCheckPolicy(int argumentsCount, Object[] args) {
		super(argumentsCount, args);
	}

	@Override
	public boolean isValidated(NativeFunction atmosHandler) {
		DebuggableScript dScript = atmosHandler.getDebuggableView();
		if (dScript.getParamCount() == 1
				&& dScript.getParamOrVarName(0).equals("request")) {
			return true;
		}
		return false;
	}

	@Override
	public ModelAndView apply(NativeFunction atmosHandler,
			HttpServletResponse response) {
		ModelAndView mav = null;

		NativeObject result = (NativeObject) atmosHandler.call(context, scope,
				scope, args);

		Object responseContent = result.get("content");
		// processing javascript handler
		if (responseContent != null) {
			mav = new ModelAndView();
			mav.setView(null);
			try {

				response.getWriter().write((String) responseContent);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			mav = new ModelAndView();
			mav.addAllObjects((Map<String, Object>) result);
		}

		setCookie(result, response);

		return mav;
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

}
