package com.skp.milonga.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.skp.milonga.servlet.handler.AtmosRequestMappingHandlerMapping;

public class MilongaBeanDefinitionParser implements BeanDefinitionParser {
	
	public static final String PROPERTY_USER_SOURCE_LOCATION = "userSourceLocation";
	public static final String PROPERTY_AUTO_REFRESHABLE = "autoRefreshable";
	
	public static final String DEFAULT_USER_SOURCE_LOCATION = "WEB-INF/js";
	public static final String DEFAULT_AUTO_REFRESHABLE = "true";
	
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition handlerMappingDef = new RootBeanDefinition(
				AtmosRequestMappingHandlerMapping.class);
		handlerMappingDef.setSource(parserContext.extractSource(element));
		
		String userSourceLocation = element.getAttribute(PROPERTY_USER_SOURCE_LOCATION);
		if (StringUtils.isEmpty(userSourceLocation)) {
			userSourceLocation = DEFAULT_USER_SOURCE_LOCATION;
		}
		
		String autoRefreshable = element.getAttribute(PROPERTY_AUTO_REFRESHABLE);
		if (StringUtils.isEmpty(autoRefreshable)) {
			autoRefreshable = DEFAULT_AUTO_REFRESHABLE;
		}
		
		handlerMappingDef.getPropertyValues().addPropertyValue(
				PROPERTY_USER_SOURCE_LOCATION,
				userSourceLocation);
		handlerMappingDef.getPropertyValues().addPropertyValue(
				PROPERTY_AUTO_REFRESHABLE,
				autoRefreshable);
		String handlerMappingName = parserContext.getReaderContext()
				.registerWithGeneratedName(handlerMappingDef);
		BeanDefinitionHolder handlerMappingBeanDefinitionHolder = new BeanDefinitionHolder(
				handlerMappingDef, handlerMappingName);
		BeanDefinitionReaderUtils
				.registerBeanDefinition(handlerMappingBeanDefinitionHolder,
						parserContext.getRegistry());

		RootBeanDefinition propertySourceConfigurerDef = new RootBeanDefinition(
				PropertySourcesPlaceholderConfigurer.class);
		String propertySourceConfigurerName = parserContext.getReaderContext()
				.registerWithGeneratedName(propertySourceConfigurerDef);
		BeanDefinitionHolder propertySourceConfigurerBeanDefinitionHolder = new BeanDefinitionHolder(
				propertySourceConfigurerDef, propertySourceConfigurerName);
		BeanDefinitionReaderUtils.registerBeanDefinition(
				propertySourceConfigurerBeanDefinitionHolder,
				parserContext.getRegistry());

		return handlerMappingDef;
	}

}
