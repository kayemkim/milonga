function Atmos() {
	
};

Atmos.prototype = {
	define: function(url, handler) {
		mappingInfo.putHandler(url, handler);
		mappingInfo.removeHandlerForView(url);
	},
	
	route: function(url) {
		return new Route(url);
	},
	
	url: function(url) {
		return new Route(url);
	},
	
	defineView: function(url, handler, viewName) {
		mappingInfo.putHandlerForView(url, handler);
		if(viewName != undefined) {
			mappingInfo.putViewName(url, viewName);
		}
		mappingInfo.removeHandler(url);
	},
	
	handler: function(url, handler) {
		this.define(url, handler);
		return new Handler(url, handler);
	}
};

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
		Atmos.define(this.url, function(request) {
			return new Response(content);
		});
	},
	json: function(jsonObj) {
		Atmos.define(this.url, function(request) {
			return jsonObj;
		});
	},
	response: function(response) {
		Atmos.define(this.url, function(request) {
			return response;
		});
	},
	define: function(process) {
		Atmos.define(this.url, process);
	},
	defineView: function(process, viewName) {
		Atmos.defineView(this.url, process, viewName);
	}
	
};


/**
 * Javascript handler representing Spring MVC handler
 * 
 * @param url		url path
 * @param process	handler method
 * @returns
 */
function Handler(url, process) {
	this.url = url;
	this.process = process;
};

Handler.prototype = {
	/**
	 * return ModelAndView 
	 * 
	 * @param viewName	view page name
	 */
	toView: function(viewName) {
		Atmos.defineView(this.url, this.process, viewName);
	}
};

