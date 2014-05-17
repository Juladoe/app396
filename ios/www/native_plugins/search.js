var QRSearch = {   
    QRSearch: function (success, fail, param) {   
      return cordova.exec( success, fail,   
                           "QRSearchPlugin",   
                           "QRSearch", [param]);   
    }   
};

function QRSearchPluginResultHandler (result) {
	if (result[0] = result[1]) {
		alert("学习完成");
	}
}

function QRSearchPluginErrorHandler (error) {
	alert("ERROR: \r\n"+error ); 
}
