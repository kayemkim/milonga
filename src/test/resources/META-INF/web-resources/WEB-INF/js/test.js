var Atmos = require('atmos');

Atmos.define('/test', function(model) {
	var data = {"newyork" : "mets"};
	model.setJson(data);
});

Atmos.define('/test1', function(model) {
	model.setJson({"newyork" : "knicks"});
});

Atmos.define('/test2', function(request, response) {
	request.setAttribute("newyork", "mets");
});
