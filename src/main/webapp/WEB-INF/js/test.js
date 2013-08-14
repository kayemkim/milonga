//var Atmos = require('atmos');

Atmos.define('/login', function(request, response) {
	var id = request.getParameter("id");
	var password = request.getParameter("password");
	if(password != '1111') {
		request.setAttribute("result", "failed");
	} else {
		request.setAttribute("result", "successful");
	}
	response.setCookie("userId", request.getParameter("login"));
	var user = new Object();
	user.login = request.getParameter("login");
	user.result = request.getAttribute("result");
	return user;
	
});

route('/platform').define(function(request, response) {
	return {
				"platform" : "Atmos Code",
				"developer" : {
								"name" : "km",
								"company" : {
											  "companyName" : "SKP"
								},
								"age" : 33,
								"family" : ["wife", "father", "mother", "brother"]
				},
				"users" : ["Tom", "John"]
			}; 
});

route('/library').response({"library" : "rhino"});

route('/create_response').response(new Response("Hello Response!"));

route('/add_cookie').define(function(request) {
	var response = new Response();
	response.cookie.userId = "metsmania";
	var message = "User ID : " +response.cookie.userId;
	response.setContent(message.toString());
	return response;
});