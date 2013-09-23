/**
 * path variable
 */
Atmos.url('/foo/{bar1}/{bar2}').define(function(request, response) {
	var bar1 = request.bar1;
	var bar2 = request.bar2;
	return bar1 + bar2;
});


/**
 * binding object
 */
Atmos.url('/binding_object').define(function(request, response) {
	var foo = request['com.sample.Foo'];
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