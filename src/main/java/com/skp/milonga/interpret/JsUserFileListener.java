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
			
			handlerMapping.reInitHandlerMethods();
		} catch (Exception e) {
			logger.error("[Milonga] Refreshing Javascript is failed.", e);
		}
	}
}
