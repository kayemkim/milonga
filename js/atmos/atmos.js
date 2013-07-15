var AtmosRequestMappingInfo = com.km.milonga.rhino.AtmosRequestMappingInfo;

exports.define = function(url, handler) {
	var armi = AtmosRequestMappingInfo.getInstance();
	armi.put(url, handler);
};