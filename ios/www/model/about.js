define(function(require, exports){

	var func_txt = new String(function(){
	/*
	<div title="关于我们" id="about" class="panel" data-header="about_header" data-footer='none' data-tab="navbar_setting">
	</div>
	*/
});
	var text = func_txt.substring(func_txt.indexOf("/*") + 2, func_txt.lastIndexOf("*/"));
	$.ui.addContentDiv("about", text, "关于我们");

	exports.errorData = '<div class="noData">网络访问异常,请重新尝试<p><span onclick="about_model.init_about_data();" class="button white refresh_btn">刷新</span></p></div>';
	
	exports.init_about_data = function()
	{
		simpleJsonP(
			schoolHost + "/about" + '?callback=?',
			function(data){
				$("#about").html(data);
			},
			false,
			function()
			{
				$("#about").html(exports.errorData);
			}
		);
	}

});