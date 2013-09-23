package com.km.milonga.servlet;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import org.mozilla.javascript.NativeObject;


/**
 * HttpSession replacement class
 * 
 * @author kminkim
 *
 */
public class AtmosSession extends NativeObject {

	private static final long serialVersionUID = -4176914732381592790L;
	
	public AtmosSession(HttpSession session) {
		super();
		storeSessionAttributes(session);
	}
	
	private void storeSessionAttributes(HttpSession session) {
		@SuppressWarnings("unchecked")
		Enumeration<String> enumeration = session.getAttributeNames();
		while(enumeration.hasMoreElements()) {
			String propertyName = enumeration.nextElement(); 
			defineProperty(propertyName, session.getAttribute(propertyName), 0);
		}
	}

}
