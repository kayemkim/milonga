package com.skp.milonga.config.annotation;

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.skp.milonga.servlet.handler.AtmosRequestMappingHandlerMapping;

@Configuration
public class DelegatingMilongaConfiguration {
	
	private String userSourceLocation;
	
	private boolean autoRefreshable;	
	
	@Bean
    public AtmosRequestMappingHandlerMapping AtmosRequestMappingHandlerMapping() {
		setAnnotationAttributes();
		AtmosRequestMappingHandlerMapping handlerMapping = new AtmosRequestMappingHandlerMapping();
    	handlerMapping.setUserSourceLocation(userSourceLocation);
    	handlerMapping.setAutoRefreshable(autoRefreshable);
    	return handlerMapping;
    }
	
	private void setAnnotationAttributes() {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
		scanner.addIncludeFilter(new AnnotationTypeFilter(EnableMilonga.class));
		
		for (BeanDefinition bd : scanner.findCandidateComponents("")) {
			try {
				Annotation milongaAnnotation = AnnotationUtils.getAnnotation(Class.forName(bd.getBeanClassName()), EnableMilonga.class);
				
				Object userSourceLocationValue = AnnotationUtils.getValue(milongaAnnotation, "userSourceLocation");
				if (userSourceLocationValue != null) {
					userSourceLocation = (String) userSourceLocationValue;
				}
				Object autoRefreshableValue = AnnotationUtils.getValue(milongaAnnotation, "autoRefreshable");
				if (autoRefreshableValue != null) {
					autoRefreshable = (Boolean) autoRefreshableValue;
				}
				
			} catch (ClassNotFoundException e) {
				// cannot reach here
				e.printStackTrace();
			}
		}
	} 

}
