var Atmos = require('atmos');

Atmos.define('/test', function(model) {
	var data = {"newyork" : "mets"};
	model.setJson(data);
	//request.setAttribute("newyork", "mets");
});

Atmos.define('/test1', function(model) {
	model.setJson({"newyork" : "knicks"});
});
