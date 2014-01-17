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

Atmos.prototype = {
		
	route: function(url) {
		return new Route(url);
	},
	
	url: function(url) {
		return new Route(url);
	},
	
	handler: function(url, handler) {
		var httpMethods = getHandlerHttpMethods(arguments);
		var handlerDefinition = new com.skp.milonga.servlet.handler.HandlerDefinition(handler, httpMethods);
		mappingInfo.putHandler(url, handlerDefinition);
		return new Handler(url, handlerDefinition);
	}
};

function getHandlerHttpMethods(handlerArgs) {
	var httpMethods = new Array();
	if (handlerArgs.length > 2) {
		for (var i = 2 ; i < handlerArgs.length ; i++) {
			httpMethods.push(handlerArgs[i]);
		}
	}
	else {
		httpMethods.push('GET');
	}
	
	return httpMethods;
};

function Atmos() {
	addMethod(this, "handler", function(context){
		function handler(url, handler) {
			var httpMethods = getHandlerHttpMethods(arguments);
			var handlerDefinition = new com.skp.milonga.servlet.handler.HandlerDefinition(handler, httpMethods);
			mappingInfo.putHandler(url, handlerDefinition);
			return new Handler(url, handlerDefinition, httpMethods);
		}
		return handler;
	});
};

//addMethod - By John Resig (MIT Licensed)
function addMethod(object, name, fn){
	var old = object[name];
	object[name] = function(){
		if (fn.length == arguments.length) 
			return fn.apply(this, arguments);
		else 
			if (typeof old == 'function') 
				return old.apply(this, arguments);
	};
}

var Atmos = new Atmos();



/**
 * Define a pair of url and handler.
 * This mapping info is stored in memory.
 */
/*
exports.define = function(url, handler) {
	mappingInfo.put(url, handler);
};
*/

function route(url) {
	return new Route(url);
};

function Route(url) {
	this.url = url;
};

Route.prototype = {
	content: function(content) {
		Atmos.handler(this.url, function(request) {
			return new Response(content);
		});
	},
	json: function(jsonObj) {
		Atmos.handler(this.url, function(request) {
			return jsonObj;
		});
	},
	response: function(response) {
		Atmos.handler(this.url, function(request) {
			return response;
		});
	},
	define: function(process) {
		Atmos.handler(this.url, process);
	},
	defineView: function(process, viewName) {
		Handler.defineView(this.url, process, viewName);
	}
	
};


/**
 * Javascript handler representing Spring MVC handler
 * 
 * @param url					url path
 * @param handlerDefinition		handler method definition
 * @returns
 */
function Handler(url, handlerDefinition) {
	this.url = url;
	this.handlerDefinition = handlerDefinition;
};

Handler.prototype = {
	/**
	 * return ModelAndView 
	 * 
	 * @param viewName	view page name
	 */
	toView: function(viewName) {
		this.defineView(this.url, this.handlerDefinition, viewName);
	},
	
	redirect: function(viewName) {
		this.defineView(this.url, this.handlerDefinition, 'redirect:' + viewName);
	},
	
	forward: function(viewName) {
		this.defineView(this.url, this.handlerDefinition, 'forward:' + viewName);
	},
	
	defineView: function(url, handlerDefinition, viewName) {
		mappingInfo.putHandlerWithView(url, handlerDefinition);
		if(viewName != undefined) {
			mappingInfo.putViewName(url, viewName);
		}
	}
};

