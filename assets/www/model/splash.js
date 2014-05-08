define(function(require, exports){

	var func_txt = new String(function(){
	/*
	<div data-load="splash_model.loadedPanel" id="splash_div" class="panel" data-header="none" data-footer="none">
            <div id="splash" style="display:block;height:90%;width:100%;text-align: center;">
                
                <div style="background: url('images/img1.jpg');"></div>
                <div style="background: url('images/img2.jpg');"></div>
                <div style="background: url('images/img3.jpg');">
                	<span onclick="splash_model.changeSplashStatus();" class="button white custom_splash_button">进入网校</span>
                </div>
            </div>
            <div id="splash_dots">
            </div>
	</div>
	*/
	});
	var text = func_txt.substring(func_txt.indexOf("/*") + 2, func_txt.lastIndexOf("*/"));
	$.ui.addContentDiv("splash_div", text, "启动页");

	exports.callback = null;

	exports.loadedPanel = function()
	{
		$("#splash").height($("#splash_div").height());
		var splash_carousel=$("#splash").carousel({
			pagingDiv: "splash_dots",
			pagingCssName: "splash_paging2",
			pagingCssNameSelected: "splash_paging2_selected",
			preventDefaults:false,
			wrap:true //Set to false to disable the wrap around
		});
	}

	exports.load = function(callback){
		$.ui.loadContent('splash_div',false,false,'slide');
		splash_model.callback = callback;
	}

	exports.changeSplashStatus = function()
	{
		appstore_model.setStoreCache("showSplash", false);
		splash_model.callback();
	}
});