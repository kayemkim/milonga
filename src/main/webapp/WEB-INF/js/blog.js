/*
 * test view for JSON data
 */
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


/*
 * for login action
 */
Atmos.define('/login', function(request, response) {
	var userId = request.getParameter("userId");
	var password = request.getParameter("password");
	
	var loginResult = new Object();
	
	if (userId == 'abc@sk.com') {
		if (password != '1111') {
			loginResult.result = "failed";
		} else {
			loginResult.result = "succeeded";
			// Redirect
			response.setRedirect("/blog");
		}
	} else {
		loginResult.result = "not available";
	}
	
	// Cookie
	response.setCookie("mail", userId);
	
	// Session
	request.getSession().setAttribute("userId", userId);
	
	return loginResult;
});


/*
 * blog list view
 */
Atmos.define('/blog', function(request, response) {
	var result = new Object();
	
	var blog1 = {"title" : "Template Usage", "content" : "This is test blog #1."};
	var blog2 = {"title" : "Customizing the columns", "content" : "This is test blog #2."};
	var blog3 = {"title" : "More Template Information", "content" : "This is test blog #3."};
	
	result.bloggs = [blog1, blog2, blog3];
	
	return result;
});


Atmos.url('/user/{id}').define(function(request, response) {
	var id = request.resolvePathVariable('id');
	
	var result = new Object();
	result.id = id;
	
	return result;
});


Atmos.url('/blog/{id}').define(function(request, response) {
	var id = request.resolvePathVariable('id');
	
	var blog = new com.km.milonga.externals.blog.model.Blog();
	blog.setId(id);
	blog.setTitle("This is " + id + "'s blog.");
	
	var result = new Object();
	result.blog = blog;
	
	//return blog;
	return result;
});


//Atmos.url('/library').response({"library" : "rhino"});
//
//
//
//Atmos.url('/create_response').response(new Response("Hello Response!"));
//
//
//
//Atmos.url('/add_cookie').define(function(request) {
//	var response = new Response();
//	response.cookie.userId = "metsmania";
//	var message = "User ID : " +response.cookie.userId;
//	response.setContent(message.toString());
//	return response;
//});