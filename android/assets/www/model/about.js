define(function(require, exports){

	var func_txt = new String(function(){
	/*
	<div title="关于我们" id="about" class="panel" data-header="about_header" data-footer='none' data-tab="navbar_setting">
	</div>
	*/
	});
	var text = func_txt.substring(func_txt.indexOf("/*") + 2, func_txt.lastIndexOf("*/"));
	$.ui.addContentDiv("about", text, "关于我们");

	exports.init_about_data = function()
	{
		simpleJsonP(
			schoolHost + "/about" + '?callback=?',
			function(data){
				$("#about").html(data.info);
				appstore_model.setCache("about", "cache");
			});
	}

});