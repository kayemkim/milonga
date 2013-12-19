package com.skp.milonga.core;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.WebApplicationInitializer;

public class MilongaContextLoaderInitializer implements
		WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		System.out.println("Milonga Config!!!");
	}

}
