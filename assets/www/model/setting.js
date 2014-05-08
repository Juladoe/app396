define(function(require, exports){

var setting_text = new String(function(){
/*
<div title="我的设置" id="setting" class="panel" data-footer='courselist_footer' data-header="setting_header" data-tab="navbar_setting">
				<ul class="list">
					<li class="divider divider_none">课程</li>
					<li><a onclick="load_favorite_page();">收藏的课程</a></li>
					<li class="divider divider_none">设置</li>
					<li>
					<div style="height:30px;">
						<label style="width:auto;padding-left:0px;">自动进入上次退出时的网校</label>
							<input data-sel="off" id="toggle" type="checkbox" name="toggle" value="0" class="toggle">
							  <label onclick="setting_model.startWithSchoolToogle();" style="left:0px;float:right;" for="toggle" data-on="开" data-off="关">
								 <span></span>
					</div>
					</label>
					</li>
					<li><a onclick="load_notification_page();">系统通知</a></li>
					<li onclick="clearCache();">清理缓存</li>
					<li><a  onclick="load_about_page();">关于我们</a></li>
					<li id="exitBtn" class="divider" style="text-align:center;padding-right:1px;display:none;">
						<span onclick="logout();" class="button white custom_button_red">退出登陆</span>
					</li>
				</ul>
			</div>
*/
});
var text = setting_text.substring(setting_text.indexOf("/*") + 2, setting_text.lastIndexOf("*/"));
$.ui.addContentDiv("setting", text, "我的设置");

exports.init_setting_data = function()
{
	if (appstore_model.checkIsLogin()) {
		$("#exitBtn").show();
	} else {
		$("#exitBtn").hide();
	}
	var startWithSchool = appstore_model.getStoreCache("startWithSchool");
	if (startWithSchool == "true") {
		$("#toggle").attr("checked", "");
		$("#toggle").attr("data-sel", "on");
	}
}

exports.startWithSchoolToogle = function()
{
	var checked = $("#toggle").attr("data-sel");
	if (checked == "on") {
		$("#toggle").attr("data-sel", "off");
		appstore_model.setStoreCache("startWithSchool", false);
	} else {
		$("#toggle").attr("data-sel", "on");
		appstore_model.setStoreCache("startWithSchool", true);
	}
}

});
