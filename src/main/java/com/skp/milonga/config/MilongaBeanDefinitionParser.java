package com.skp.milonga.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.skp.milonga.servlet.handler.AtmosRequestMappingHandlerMapping;

public class MilongaBeanDefinitionParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		Object source = parserContext.extractSource(element);
		RootBeanDefinition handlerMappingDef = new RootBeanDefinition(AtmosRequestMappingHandlerMapping.class);
		handlerMappingDef.setSource(source);
		handlerMappingDef.getPropertyValues().add("userSourceLocation", "WEB-INF/js");
		String handlerMappingName = parserContext.getReaderContext().registerWithGeneratedName(handlerMappingDef);
		parserContext.registerComponent(new BeanComponentDefinition(handlerMappingDef, handlerMappingName));
		
		return null;
	}

}
