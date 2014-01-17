/*
 * @PathVariable example
 */
Atmos.handler('/pathvariable/{varName}', function() {
	context.getBean(com.skp.milonga.servlet.handler.AtmosRequestMappingHandlerMapping);
	var result = new Object();
	result['pathVariable'] = varName;
	return result;
}).toView();


/*
 * Class Object Binding example
 */
Atmos.handler('/binding', function(req) {
	var data = req.bindAs('com.skp.milonga.test.model.Player');
	return data;
}).toView();


Atmos.handler('/json/{id}', function() {
	var player = new com.skp.milonga.test.model.Player();
	player.setPlayerName(id);
	return player;
});


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
}).toView();


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
			res.redirect('/blog');
		}
	} else {
		loginResult.result = "not available";
	}
	
	// Cookie
	res.cookie['mail'] = userId;
	
	// Session
	req.session['userId'] = userId;
	
	return loginResult;
}).toView();


Atmos.url('/jsStyleBinding/{foo}/{foo2}').define(function() {
	return foo + foo2;
});

Atmos.url('/jsStyleJavaObjectBinding').define(function(req) {
	return req.bindAs('com.skp.milonga.test.model.Player');
});

Atmos.handler('/redirectTest', function(req, res) {
	
}).redirect('http://www.google.com');

Atmos.handler(context)('/applicationContextTest', function() {
	var bean = context.getBean(org.springframework.web.servlet.view.InternalResourceViewResolver);
	return bean.getClass().getName();
});

Atmos.handler('/httpMethodTest', function(req) {
	var data = req.bindAs('com.skp.milonga.test.model.Player');
	return data.homerun();
}, 'POST', 'GET');