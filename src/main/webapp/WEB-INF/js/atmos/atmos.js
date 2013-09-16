function Atmos() {
	
};

Atmos.prototype = {
	define: function(url, handler) {
		mappingInfo.put(url, handler);
	},
	
	route: function(url) {
		return new Route(url);
	},
	
	url: function(url) {
		return new Route(url);
	},
	
	defineView: function(url, handler) {
		mappingInfo.putHandlerForView(url, handler);
	}

};

var Atmos = new Atmos();


function Response(content) {
	this.content = content;
};

Response.prototype = {
	cookie: {
		
	},
	
	getContent: function() {
		return this.content;
	},
	
	setContent: function(content) {
		this.content = content;
	},
	
	setCookie: function(name, value) {
		this.cookie[name] = value;
	}
};


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
	defineView: function(process) {
		Atmos.defineView(this.url, process);
	}
	
};


