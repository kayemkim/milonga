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
public class AtmosRequestMappingInfo {

	Map<String, Object> mappingInfoStorage = new ConcurrentHashMap<String, Object>();

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
	 * Store url-handler mapping info
	 * 
	 * @param url
	 * @param handler
	 */
	public void put(String url, Object handler) {
		mappingInfoStorage.put(url, handler);
	}

	/**
	 * return iterator of Map
	 * 
	 * @return
	 */
	public Iterator<Entry<String, Object>> iterator() {
		return mappingInfoStorage.entrySet().iterator();
	}

}
