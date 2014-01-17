/*
 * @PathVariable example
 */
Atmos.handler('/pathvariablee/{varName}', function() {
	var result = new Object();
	result['pathVariable'] = varName;
	return result;
}).toView();
