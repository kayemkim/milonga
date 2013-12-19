package com.skp.milonga.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class MilongaNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("milonga", new MilongaBeanDefinitionParser());
	}

}
