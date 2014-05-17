define(function(require, exports){

	var className = "jsobject";
	// Private array objectf chars to use
  	var CHARS = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');

  	exports.extend = {};

  	exports.parent = null;

	exports.info = null;

	exports.fields = null;

	exports.methods = {
		"toString" : exports.toString
	};

	exports.getMethods = function()
	{
		return exports.methods;
	}

	exports.toString = function()
	{
		console.log(exports.info.uuid);
		return exports.info.uuid;
	}

	window['super'] = function(extend)
	{
		var object = {};
		for(var index in extend) {
			object = window[extend[index]]();
			if (object) {
				object = object.constructor();
			}
		}
		return object;
	}

	window[className] = function()
	{
		exports = exports.constructor();
		return exports;
	}

	exports.constructor = function()
	{
		var parent = window['super'](exports.extend);
		if (parent) {
			for (var i in parent.fields) {
				exports.fields[i] = parent.fields[i];
			}
			for (var i in parent.methods) {
				exports.methods[i] = parent.methods[i];
			}
		}
	
		exports.parent = parent;
		exports.info = {
			"uuid" : uuid()
		};
		exports.fields = {};
		return exports;
	}

	function uuid(len, radix) {
	    var chars = CHARS, uuid = [], i;
	    radix = radix || chars.length;
	 
	    if (len) {
	      // Compact form
	      for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random()*radix];
	    } else {
	      // rfc4122, version 4 form
	      var r;
	 
	      // rfc4122 requires these characters
	      uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
	      uuid[14] = '4';
	 
	      // Fill in random data.  At i==19 set the high bits of clock sequence as
	      // per rfc4122, sec. 4.1.5
	      for (i = 0; i < 36; i++) {
	        if (!uuid[i]) {
	          r = 0 | Math.random()*16;
	          uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
	        }
	      }
	    }
	 
	    return uuid.join('');
	};

});