package com.km.milonga.rhino;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.NativeFunction;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * This controller takes charge of processing the requests registered with
 * javascript handler.
 * 
 * @author kminkim
 * 
 */
public class AtmosController implements Controller {

	NativeFunction atmosHandler;

	public AtmosController(NativeFunction atmosHandler) {
		this.atmosHandler = atmosHandler;
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ApplicationContext context = WebApplicationContextUtils
				.getWebApplicationContext(request.getSession()
						.getServletContext());
		ArgumentChecker argumentChecker = context
				.getBean(ArgumentChecker.class);
		return argumentChecker.checkAndProcess(atmosHandler, request, response);
	}

}
