var AtmosRequestMappingInfo = com.km.milonga.rhino.AtmosRequestMappingInfo;

/**
 * Define a pair of url and handler.
 * This mapping info is stored in memory.
 */
exports.define = function(url, handler) {
	var armi = AtmosRequestMappingInfo.getInstance();
	armi.put(url, handler);
};