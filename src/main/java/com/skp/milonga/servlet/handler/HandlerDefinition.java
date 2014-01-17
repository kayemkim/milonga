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

package com.skp.milonga.servlet.handler;

public class HandlerDefinition {
	
	private Object handler;
	
	private String[] httpMethods;
	
	public HandlerDefinition(Object handler, String[] httpMethods) {
		this.handler = handler;
		this.httpMethods = httpMethods;
	}

	public Object getHandler() {
		return handler;
	}	

	public String[] getHttpMethods() {
		return httpMethods;
	}	
}
