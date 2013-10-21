package com.skp.milonga.servlet.handler;

import java.io.IOException;
import java.util.Enumeration;
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
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.skp.milonga.servlet.AtmosHttpServletRequest;
import com.skp.milonga.servlet.AtmosHttpServletResponse;

/**
 * Atmos Function is converted to this handler to be used as Spring
 * handler. This handler handles Spring Model and View.
 * 
 * @author kminkim
 * 
 */
public class AtmosFunctionHandler {
	
	public static final String RETURN_RESPONSE_BODY_METHOD = "handleResponseBody";
	public static final String RETURN_MODEL_AND_VIEW_METHOD = "handleModelAndView";
	
	private NativeFunction atmosFunction;
	
	private String redirectPath = null;
	private String forwardPath = null;
	
	
	public AtmosFunctionHandler(NativeFunction atmosFunction) {
		this.atmosFunction = atmosFunction;
	}
	
	/**
	 * Handler method for ModelAndView
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public Object handleModelAndView(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		Object result = callNativeFunction(request, response);
		
		ModelAndView mv = new ModelAndView();
		
		if (result instanceof Undefined) {
			// request attributes to Model
			Enumeration<String> attributeNames = request.getAttributeNames();
			while (attributeNames.hasMoreElements()) {
				String attributeName = attributeNames.nextElement();
				mv.addObject(attributeName, request.getAttribute(attributeName));
			}
		} else if (result instanceof NativeObject) {
			// in case that return type is Javascript object
			Iterator<Entry<Object, Object>> i = ((NativeObject) result).entrySet().iterator();
			while(i.hasNext()) {
				Entry<Object, Object> e = i.next();
				String key = e.getKey().toString();
				mv.addObject(key, e.getValue());
			}
		} else if (result instanceof NativeJavaObject) {
			// in case that return type is Java object
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> convertedResult = om.convertValue(((NativeJavaObject) result).unwrap(), Map.class);
			mv.getModelMap().mergeAttributes(convertedResult);
		}
		
		if (redirectPath != null) {
			mv.setViewName("redirect:" + redirectPath);
		}
		
		if (forwardPath != null) {
			mv.setViewName("forward:" + forwardPath);
		}
		
		return mv;
	}
	
	/**
	 * Handler method for ResponseBody
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@ResponseBody
	public Object handleResponseBody(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		Object result = callNativeFunction(request, response);
		
		if (result instanceof NativeJavaObject) {
			return ((NativeJavaObject) result).unwrap();
		}
		
		return result;
	}
	
	/**
	 * Call Javascript Native Function
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private Object callNativeFunction(HttpServletRequest request, HttpServletResponse response) {
		Context context = Context.enter();
		Scriptable scope = context.initStandardObjects();
		
		AtmosHttpServletRequest atmosRequest = new AtmosHttpServletRequest(request);
		AtmosHttpServletResponse atmosResponse = new AtmosHttpServletResponse(response);
		
		
		Object result = atmosFunction.call(context, scope, scope, new Object[] {
				atmosRequest, atmosResponse });
		
		setCookie(atmosResponse, response);
		
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
}
