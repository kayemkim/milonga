package com.skp.milonga.servlet.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Store url-handler infos into memory.
 * 
 * @author kminkim
 * 
 */
public class AtmosRequestMappingInfoStorage implements HandlerMappingInfoStorage {

	private Map<String, HandlerDefinition> handlerMappingStorage = new ConcurrentHashMap<String, HandlerDefinition>();

	private Map<String, HandlerDefinition> handlerWithViewMappingInfoStorage = new ConcurrentHashMap<String, HandlerDefinition>();

	private Map<String, String> viewNameMappingInfoStorage = new ConcurrentHashMap<String, String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.skp.milonga.rhino.MappingInfoStorage#putHandler(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void putHandler(String url, HandlerDefinition handler) {
		handlerMappingStorage.put(url, handler);
		removeHandlerWithView(url);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.skp.milonga.rhino.MappingInfoStorage#putHandlerWithView(java.lang
	 * .String, java.lang.Object)
	 */
	@Override
	public void putHandlerWithView(String url, HandlerDefinition handler) {
		handlerWithViewMappingInfoStorage.put(url, handler);
		removeHandler(url);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.skp.milonga.rhino.MappingInfoStorage#putViewName(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void putViewName(String url, String viewName) {
		viewNameMappingInfoStorage.put(url, viewName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.skp.milonga.rhino.MappingInfoStorage#getHandlerMappingInfos()
	 */
	@Override
	public Map<String, HandlerDefinition> getHandlerMappingInfos() {
		return handlerMappingStorage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.skp.milonga.rhino.MappingInfoStorage#getHandlerWithViewMappingInfos()
	 */
	@Override
	public Map<String, HandlerDefinition> getHandlerWithViewMappingInfos() {
		return handlerWithViewMappingInfoStorage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.skp.milonga.rhino.MappingInfoStorage#getViewName(java.lang.String)
	 */
	@Override
	public String getViewName(String url) {
		return viewNameMappingInfoStorage.get(url);
	}

	private void removeHandler(String url) {
		handlerMappingStorage.remove(url);
	}

	private void removeHandlerWithView(String url) {
		handlerWithViewMappingInfoStorage.remove(url);
		viewNameMappingInfoStorage.remove(url);
	}

}
