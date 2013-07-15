var Atmos = require('atmos');

Atmos.define('/test', function(request, response) {
	request.setAttribute("newyork", "mets");
	return true;
});

Atmos.define('/test1', function(request, response) {
	return true;
});
