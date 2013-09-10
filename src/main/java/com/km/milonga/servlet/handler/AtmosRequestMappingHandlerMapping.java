package com.km.milonga.servlet.handler;

import java.lang.reflect.Method;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class AtmosRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
	
	@Override
	protected void initHandlerMethods() {
		// TODO Auto-generated method stub
		super.initHandlerMethods();
	}

	@Override
	protected RequestMappingInfo getMappingForMethod(Method method,
			Class<?> handlerType) {
		return super.getMappingForMethod(method, handlerType);
		
	}
	
	

}
