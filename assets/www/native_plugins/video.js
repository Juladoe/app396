var VideoPlayer = {   
    playVideo: function (success, fail, playurl) {   
      return cordova.exec( success, fail,   
                           "VideoPlayerPlugin",   
                           "playVideo", [playurl]);   
    }   
};

function nativePluginResultHandler (result) {
	if (result[0] = result[1]) {
		alert("学习完成");
	}
}

function nativePluginErrorHandler (error) {
	alert("ERROR: \r\n"+error ); 
}
