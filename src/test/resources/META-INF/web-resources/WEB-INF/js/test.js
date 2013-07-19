var Atmos = require('atmos');

Atmos.define('/login', function(request, response) {
	var id = request.getParameter("id");
	var password = request.getParameter("password");
	if(password != '1111') {
		request.setAttribute("result", "failed");
	} else {
		request.setAttribute("result", "successful");
	}
});

Atmos.define('/platform', function(request, response) {
	request.setAttribute("platform", "Atmos Code");
});

Atmos.define('/library', function(request) {
	return {"library" : "rhino"};
});