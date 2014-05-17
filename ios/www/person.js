define(function(require, exports){
	var className = "person";

	exports.extend = [
		'jsobject'
	];

	function include()
	{
		while (!window.jsobject) {
			
		};
	}

	window["new"+className] = function()
	{
		//include();
		var parent = window['super'](exports.extend);
		return parent;
	}

	exports.constructor = function()
	{

	}
});