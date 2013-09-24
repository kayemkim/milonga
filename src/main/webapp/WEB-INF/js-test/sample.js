/**
 * path variable
 */
Atmos.url('/foo/{bar1}/{bar2}').define(function(request, response) {
	return bar1 + bar2;
});


/**
 * binding object
 */
Atmos.url('/binding_object').define(function(request, response) {
	var foo = request.bindAs('com.sample.Foo');
	
	//var foo = Atmos.bind('com.sample.Foo', request.params);
	//foo = request.bindAs('com.'); //call Atmos.bind(request.params, 'com.')
	
	/*
	var foo = binder('com.sample.Foo').bind(request);
	var foo = binder['com.sample.Foo'];
	var foo = request.bind('com.sample.Foo');
	var foo = request.binder['com.sample.Foo'];
	*/
	
	return foo;
});


/**
 * cookie
 */
Atmos.url('/cookie').define(function(request, response) {
	response.cookie.mail = 'abc@sk.com';
});


/**
 * session
 */
Atmos.url('/session').define(function(request, response) {
	request.session.userId = 'abc123';
});


/**
 * redirect path
 */
Atmos.url('/redirect').define(function(request, response) {
	response.redirect = '/foo/bar';
});


/**
 * forward path
 */
Atmos.url('/forward').define(function(request, response) {
	response.forward = '/foo/bar';
});


/**
 * get request attribute
 */
Atmos.url('/get_attribute').define(function(request, response) {
	var foo = request.foo;
});