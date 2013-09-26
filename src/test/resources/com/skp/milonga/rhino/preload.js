function Response(body) {
	this.body = body;
};

Response.prototype = {
	cookie: {},
	
	getBody: function() {
		return this.body;
	},
	
	setBody: function(body) {
		this.body = body;
	},
	
	setCookie: function(name, value) {
		this.cookie[name] = value;
	}
};