package com.km.milonga.servlet.handler;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Undefined;
import org.springframework.web.servlet.ModelAndView;

public class NativeFunctionModelAndViewHandler extends AbstractNativeFunctionHandler {

	public NativeFunctionModelAndViewHandler(NativeFunction atmosFunction) {
		super(atmosFunction);
	}

	@Override
	public Object handle(HttpServletRequest request,
			HttpServletResponse response) {
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
		
		if (getRedirectPath() != null) {
			mv.setViewName("redirect:" + getRedirectPath());
		}
		
		if (getForwardPath() != null) {
			mv.setViewName("forward:" + getForwardPath());
		}
		
		return mv;
	}

}
