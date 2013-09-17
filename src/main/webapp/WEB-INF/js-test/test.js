/*
 * @PathVariable example
 */
Atmos.url('/pathvariable/{varName}').defineView(function(request, response) {
	var pathVar = request['varName'];
	var result = new Object();
	result['pathVariable'] = pathVar;
	return result;
});


/*
 * Class Object Binding example
 */
Atmos.url('/binding').defineView(function(request, response) {
	var data = request['com.km.milonga.rhino.Player'];
	var result = new Object();
	result['playerName'] = data.getPlayerName();
	return result;
});


Atmos.url('/json/{id}').define(function(request, response) {
	//var id = request.resolvePathVariable('id');
	var id = request['id'];
	var player = new com.km.milonga.rhino.Player();
	player.setPlayerName(id);
	return player;
});


/*
 * test view for JSON data
 */
Atmos.url('/platform').defineView(function(request, response) {
	
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
Atmos.defineView('/login', function(request, response) {
	var userId = request.userId;
	var password = request.password;
	
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
	//request.getSession().setAttribute("userId", userId);
	request.session['userId'] = userId;
	
	return loginResult;
});


Atmos.url('/jsStyleBinding/{foo}/{foo2}').define(function(request, response) {
	return request['foo'] + request.foo2;
});

Atmos.url('/jsStyleJavaObjectBinding').define(function(request, response) {
	return request['com.km.milonga.rhino.Player'];
});