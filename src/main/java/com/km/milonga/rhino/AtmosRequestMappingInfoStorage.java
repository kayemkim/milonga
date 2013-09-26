package com.km.milonga.rhino;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Store url-handler infos into memory.
 * 
 * @author kminkim
 * 
 */
public class AtmosRequestMappingInfoStorage {

	Map<String, Object> mappingInfoStorage = new ConcurrentHashMap<String, Object>();
	
	Map<String, Object> mappingInfoForViewStorage = new ConcurrentHashMap<String, Object>();
	
	Map<String, String> mappingInfoForViewNameStorage = new ConcurrentHashMap<String, String>();

	/**
	 * Retrieve handler by url
	 * 
	 * @param url
	 * @return
	 */
	public Object get(String url) {
		return mappingInfoStorage.get(url);
	}
	
	/**
	 * Retrieve handler for view by url
	 * 
	 * @param url
	 * @return
	 */
	public Object getHandlerForView(String url) {
		return mappingInfoForViewStorage.get(url);
	}
	
	public String getViewName(String url) {
		return mappingInfoForViewNameStorage.get(url);
	}

	/**
	 * Store url-handler mapping info
	 * 
	 * @param url
	 * @param handler
	 */
	public void putHandler(String url, Object handler) {
		mappingInfoStorage.put(url, handler);
	}
	
	/**
	 * Store handler mapping info. This handler returns view page.
	 * 
	 * @param url
	 * @param handler
	 */
	public void putHandlerForView(String url, Object handler) {
		mappingInfoForViewStorage.put(url, handler);
	}
	
	public void putViewName(String url, String viewName) {
		mappingInfoForViewNameStorage.put(url, viewName);
	}
	
	public void removeHandler(String url) {
		mappingInfoStorage.remove(url);
	}
	
	public void removeHandlerForView(String url) {
		mappingInfoForViewStorage.remove(url);
		mappingInfoForViewNameStorage.remove(url);
	}

	/**
	 * return iterator of Map
	 * 
	 * @return
	 */
	public Iterator<Entry<String, Object>> iterator() {
		return mappingInfoStorage.entrySet().iterator();
	}
	
	public Map<String, Object> getResponseBodyMappingInfos() {
		return mappingInfoStorage;
	}
	
	public Map<String, Object> getModelAndViewMappingInfos() {
		return mappingInfoForViewStorage;
	}

}
