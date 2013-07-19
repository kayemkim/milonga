/**
 * Define a pair of url and handler.
 * This mapping info is stored in memory.
 */
exports.define = function(url, handler) {
	mappingInfo.put(url, handler);
};