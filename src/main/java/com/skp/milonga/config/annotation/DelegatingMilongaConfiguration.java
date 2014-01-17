/*
 * Copyright 2014 K.M. Kim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skp.milonga.config.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.skp.milonga.servlet.handler.AtmosRequestMappingHandlerMapping;

@Configuration
public class DelegatingMilongaConfiguration {
	
	private List<String> userSourceLocations = new ArrayList<String>();
	
	private boolean autoRefreshable;	
	
	@Bean
    public AtmosRequestMappingHandlerMapping AtmosRequestMappingHandlerMapping() {
		setAnnotationAttributes();
		AtmosRequestMappingHandlerMapping handlerMapping = new AtmosRequestMappingHandlerMapping();
		String[] userSourceLocationArray = new String[userSourceLocations.size()];
    	handlerMapping.setUserSourceLocations(userSourceLocations.toArray(userSourceLocationArray));
    	handlerMapping.setAutoRefreshable(autoRefreshable);
    	return handlerMapping;
    }
	
	private void setAnnotationAttributes() {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
		scanner.addIncludeFilter(new AnnotationTypeFilter(EnableMilonga.class));
		
		for (BeanDefinition bd : scanner.findCandidateComponents("")) {
			try {
				Annotation milongaAnnotation = AnnotationUtils.getAnnotation(Class.forName(bd.getBeanClassName()), EnableMilonga.class);
				
				String[] userSourceLocationValues = (String[]) AnnotationUtils.getValue(milongaAnnotation, "locations");
				if (userSourceLocationValues != null) {
					for (String value : userSourceLocationValues) {
						userSourceLocations.add(value);
					}
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
