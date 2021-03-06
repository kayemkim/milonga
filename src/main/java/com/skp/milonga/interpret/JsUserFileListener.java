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

package com.skp.milonga.interpret;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import com.skp.milonga.servlet.handler.AtmosRequestMappingHandlerMapping;

public class JsUserFileListener extends WebApplicationObjectSupport implements FileListener {

	@Override
	public void fileCreated(FileChangeEvent event) throws Exception {
		logger.info(event.getFile().getName()
				+ " file is created in Milonga JS sources.");
		reRegisterHandlerMethods();
	}

	@Override
	public void fileDeleted(FileChangeEvent event) throws Exception {
		logger.info(event.getFile().getName()
				+ " file is deleted from Milonga JS sources.");
		reRegisterHandlerMethods();
	}

	@Override
	public void fileChanged(FileChangeEvent event) throws Exception {
		logger.info(event.getFile().getName()
				+ " file is changed in Milonga JS sources.");
		reRegisterHandlerMethods();
	}
	
	@SuppressWarnings("unchecked")
	private void reRegisterHandlerMethods() {
		AtmosRequestMappingHandlerMapping handlerMapping = getApplicationContext()
				.getBean(AtmosRequestMappingHandlerMapping.class);
		try {
			Field fieldHandlerMethods = AbstractHandlerMethodMapping.class
					.getDeclaredField("handlerMethods");
			fieldHandlerMethods.setAccessible(true);
			Map<RequestMappingInfo, HandlerMethod> map = (Map<RequestMappingInfo, HandlerMethod>) fieldHandlerMethods
					.get(handlerMapping);
			map.clear();

			Field fieldUrlMap = AbstractHandlerMethodMapping.class
					.getDeclaredField("urlMap");
			fieldUrlMap.setAccessible(true);
			MultiValueMap<String, RequestMappingInfo> urlMap = (MultiValueMap<String, RequestMappingInfo>) fieldUrlMap
					.get(handlerMapping);
			urlMap.clear();

			handlerMapping.getHandlerMappingInfoStorage()
					.getHandlerMappingInfos().clear();
			handlerMapping.getHandlerMappingInfoStorage()
					.getHandlerWithViewMappingInfos().clear();
			
			handlerMapping.reInitHandlerMethods();
		} catch (Exception e) {
			logger.error("[Milonga] Refreshing Javascript is failed.", e);
		}
	}
}
