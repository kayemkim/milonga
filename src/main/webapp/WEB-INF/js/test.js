//var Atmos = require('atmos');

Atmos.define('/login', function(request, response) {
	var userId = request.getParameter("userId");
	var password = request.getParameter("password");
	
	var loginResult = new Object();
	//loginResult.userId = userId;
	
	if (userId == 'abc@sk.com') {
		if (password != '1111') {
			loginResult.result = "failed";
		} else {
			loginResult.result = "succeeded";
			response.setRedirect("/blog");
		}
	} else {
		loginResult.result = "not available";
	}
	
	response.setCookie("mail", userId);
	request.getSession().setAttribute("userId", userId);
	
	return loginResult;
});



Atmos.define('/blog', function(request, response) {
	var result = new Object();
	
	var blog1 = {"title" : "Template Usage", "content" : "This is test blog #1."};
	var blog2 = {"title" : "Customizing the columns", "content" : "This is test blog #2."};
	var blog3 = {"title" : "More Template Information", "content" : "This is test blog #3."};
	
	result.bloggs = [blog1, blog2, blog3];
	
	return result;
});


Atmos.url('/platform').define(function(request, response) {
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

Atmos.url('/library').response({"library" : "rhino"});

Atmos.url('/create_response').response(new Response("Hello Response!"));

Atmos.url('/add_cookie').define(function(request) {
	var response = new Response();
	response.cookie.userId = "metsmania";
	var message = "User ID : " +response.cookie.userId;
	response.setContent(message.toString());
	return response;
});