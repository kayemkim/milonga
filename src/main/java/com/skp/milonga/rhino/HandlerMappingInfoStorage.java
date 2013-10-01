package com.skp.milonga.rhino;

import java.util.Map;

public interface HandlerMappingInfoStorage {

	/**
	 * Store url-handler mapping info
	 * 
	 * @param url
	 * @param handler
	 */
	public abstract void putHandler(String url, Object handler);

	/**
	 * Store handler mapping info. This handler returns view page.
	 * 
	 * @param url
	 * @param handler
	 */
	public abstract void putHandlerWithView(String url, Object handler);

	/**
	 * store mapping info of url and view page .
	 * 
	 * @param url
	 * @param viewName
	 */
	public abstract void putViewName(String url, String viewName);

	/**
	 * return url - handler mapping info
	 * 
	 * @return
	 */
	public abstract Map<String, Object> getHandlerMappingInfos();

	/**
	 * return mapping info of url - handler with view page
	 *   
	 * @return
	 */
	public abstract Map<String, Object> getHandlerWithViewMappingInfos();

	/**
	 * return view page name matching with url
	 * 
	 * @param url
	 * @return
	 */	
	public abstract String getViewName(String url);

}