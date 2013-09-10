package com.km.milonga.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.LastModified;

/**
 * Adapter for Atmos handler
 * 
 * @author kminkim
 * 
 */
public class AtmosHandlerAdapter implements HandlerAdapter {

	@Override
	public boolean supports(Object handler) {
		/*return (handler instanceof AtmosHttpRequestHandler)
				|| (handler instanceof AtmosControllerHandler);*/
		return handler instanceof AtmosFunctionHandler;
	}

	@Override
	public ModelAndView handle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		/*if (handler instanceof AtmosHttpRequestHandler) {
			((AtmosHttpRequestHandler) handler).handleRequest(request, response);
			return null;
		}
		else if (handler instanceof AtmosControllerHandler) {
			return ((AtmosControllerHandler) handler).handleRequest(request, response);
		}
		return null;*/
		return ((AtmosFunctionHandler) handler).handleRequest(request, response);
	}

	@Override
	public long getLastModified(HttpServletRequest request, Object handler) {
		if (handler instanceof LastModified) {
			return ((LastModified) handler).getLastModified(request);
		}
		return -1L;
	}
	
}
