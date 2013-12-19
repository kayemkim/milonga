package com.skp.milonga.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.skp.milonga.servlet.handler.AtmosRequestMappingHandlerMapping;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.skp.milonga"})
public class MilongaTestConfig extends WebMvcConfigurerAdapter {
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }
    
    @Bean
    public AtmosRequestMappingHandlerMapping AtmosRequestMappingHandlerMapping() {
    	AtmosRequestMappingHandlerMapping handlerMapping = new AtmosRequestMappingHandlerMapping();
    	handlerMapping.setUserSourceLocation("WEB-INF/js-test");
    	return handlerMapping;
    }
    
    @Bean
    public InternalResourceViewResolver InternalResourceViewResolver() {
    	InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    	viewResolver.setOrder(1);
    	viewResolver.setPrefix("/WEB-INF/views/");
    	viewResolver.setSuffix(".jsp");
    	return viewResolver;
    }

}
