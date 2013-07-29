function Atmos() {
	
};

Atmos.prototype = {
	define: function(url, handler) {
		mappingInfo.put(url, handler);
	}
};

var Atmos = new Atmos();


function Response(content) {
	this.content = content;
};

Response.prototype = {
	cookie: {},
	
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