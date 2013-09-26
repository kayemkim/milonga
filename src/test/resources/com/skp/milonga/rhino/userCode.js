//var dependant = require('dependant');
//require('dependant.js');

var before = function(param) {
	
};

var define = before;

var testFunc = function( param ) {
	var writer = new java.io.FileWriter(param);
	writer.write("contents");
	writer.close();
};

var returnResponse = function() {
	var response = new Response('Hello, Response!');
	response.setCookie('user', 'kmkim');
	return response;
};
