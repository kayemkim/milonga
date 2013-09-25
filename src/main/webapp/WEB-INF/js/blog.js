/*
 * test view for JSON data
 */
Atmos.handler('/platform', function() {
	
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
Atmos.handler('/login', function(req, res) {
	var userId = req.userId;
	var password = req.password;
	
	var loginResult = new Object();
	
	if (userId == 'abc@sk.com') {
		if (password != '1111') {
			loginResult.result = "failed";
		} else {
			loginResult.result = "succeeded";
			// Redirect
			res.redirect = "/blog";
		}
	} else {
		loginResult.result = "not available";
	}
	
	// Cookie
	res.cookie.mail = userId;
	
	// Session
	req.session.userId = userId;
	
	return loginResult;
}).toView();


/*
 * blog list view
 */
Atmos.handler('/blog', function() {
	var result = new Object();
	
	var blog1 = {"title" : "Template Usage", "content" : "This is test blog #1."};
	var blog2 = {"title" : "Customizing the columns", "content" : "This is test blog #2."};
	var blog3 = {"title" : "More Template Information", "content" : "This is test blog #3."};
	
	result.bloggs = [blog1, blog2, blog3];
	
	return result;
}).toView('blog');


Atmos.handler('/user/{id}', function(req) {
	var result = new Object();
	result.id = id;
	
	return result;
});


Atmos.handler('/blog/{id}', function(req) {
	var blog = new com.km.milonga.externals.blog.model.Blog();
	blog.setId(id);
	blog.setTitle("This is " + id + "'s blog.");
	
	var result = new Object();
	result.blog = blog;
	
	return blog;
});


Atmos.handler('/end', function() {
	return 'the end.';
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