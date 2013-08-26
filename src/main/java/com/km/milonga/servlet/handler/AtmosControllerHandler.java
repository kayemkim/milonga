package com.km.milonga.servlet.handler;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.springframework.web.servlet.ModelAndView;

import com.github.mustachejava.TemplateFunction;
import com.google.common.base.Function;
import com.km.milonga.servlet.AtmosHttpServletRequest;
import com.km.milonga.servlet.AtmosHttpServletResponse;

/**
 * Atmos Function is converted to this controller to be used like Spring
 * Controller. This controller handles Spring Model and View.
 * 
 * @author kminkim
 * 
 */
public class AtmosControllerHandler {
	
	private NativeFunction atmosFunction;
	
	public AtmosControllerHandler(NativeFunction atmosHandler) {
		this.atmosFunction = atmosHandler;
	}
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ModelAndView mv = new ModelAndView();
		
		Context context = Context.enter();
		Scriptable scope = context.initStandardObjects();
		
		AtmosHttpServletResponse atmosResponse = new AtmosHttpServletResponse(response);
		AtmosHttpServletRequest atmosRequest = new AtmosHttpServletRequest(request);
		
		Object result = atmosFunction.call(context, scope, scope, new Object[] {
				atmosRequest, atmosResponse });
		
		setCookie(atmosResponse, response);
		
		if (result instanceof Undefined) {
			// request attributes to Model
			Enumeration<String> attributeNames = atmosRequest.getAttributeNames();
			while (attributeNames.hasMoreElements()) {
				String attributeName = attributeNames.nextElement();
				mv.addObject(attributeName, atmosRequest.getAttribute(attributeName));
			}
		} else if (result instanceof NativeObject) {
			Iterator<Entry<Object, Object>> i = ((NativeObject) result).entrySet().iterator();
			while(i.hasNext()) {
				Entry<Object, Object> e = i.next();
				String key = e.getKey().toString();
				mv.addObject(e.getKey().toString(), e.getValue());
			}
		}
		
		if (atmosResponse.getRedirect() != null) {
			mv.setViewName("redirect:" + atmosResponse.getRedirect());
		}
		
		if (atmosResponse.getForward() != null) {
			mv.setViewName("forward:" + atmosResponse.getForward());
		}
		
		return mv;
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
}
