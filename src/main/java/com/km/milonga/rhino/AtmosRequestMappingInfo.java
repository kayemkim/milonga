package com.km.milonga.rhino;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RequestMapping 정보를 저장
 * @author kminkim
 *
 */
public class AtmosRequestMappingInfo {
	
	Map<String, Object> mappingInfoStorage = new ConcurrentHashMap<String, Object>();
	
	// Singleton
	private AtmosRequestMappingInfo() {
		
	}
	
	private static class SingletonHolder {
		static final AtmosRequestMappingInfo single = new AtmosRequestMappingInfo();
	}
	
	public static AtmosRequestMappingInfo getInstance() {
		return SingletonHolder.single;
	}
	
	public Object get(String url) {
		return mappingInfoStorage.get(url);
	}
	
	public void put(String url, Object handler) {
		mappingInfoStorage.put(url, handler);
	}
	
	public Iterator<Entry<String, Object>> iterator() {
		return mappingInfoStorage.entrySet().iterator();
	}

}
