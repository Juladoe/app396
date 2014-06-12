define(function(require, exports){

	var setting_text = new String(function(){
/*
<div title="个人中心" id="setting" class="panel" data-footer='set_footer' data-header="setting_header" data-tab="navbar_setting">
				<ul class="list normal_list" id="setting_list">
					<li class="setting_single_card">
						<span id="loginuser_layout">
							<table>
								<tr>
									<td><img id="login_user_avatar" style="border-radius: 50px;margin:5px;" src="images/avatar.png" /></td>
									<td style="padding:10px;">
										<h2 id="login_user_name"></h2>
										<p id="login_user_info"></p>
									</td>
								</tr>
							</table>
						</span>
					</li>
					<li class="setting_single_card"><a onclick="load_favorite_page();">收藏的课程</a></li>
					
					<div class="setting_mulit_card">
						<li style="padding-bottom: 5px !important;padding-top: 5px !important;">
							<div style="height:30px;">
								<span class="auto_enter_sch_title">自动进入网校</span>
								<input data-sel="off" id="toggle" type="checkbox" name="toggle" value="0" class="toggle">
								<label onclick="setting_model.startWithSchoolToogle();" style="left:0px;float:right;margin:0px;" for="toggle" data-on="" data-off="">
								<span></span>
							</div>
						</label>
						</li>
						<li onclick="SettingJumpToMain();">选择其他网校</li>
					</div>
					
					<div class="setting_mulit_card">
						<li><a onclick="load_notification_page();">系统通知</a></li>
						<li onclick="clearCache();">清理缓存</li>
						<li><a  onclick="load_about_page();">关于网校</a></li>
					</div>
					<li id="exitBtn" style="text-align:center;padding-right:1px;">
						<span onclick="logout();" class="button white custom_button_red">退出登录</span>
					</li>
				</ul>
			</div>
			*/
		});
var text = setting_text.substring(setting_text.indexOf("/*") + 2, setting_text.lastIndexOf("*/"));
$.ui.addContentDiv("setting", text, "个人中心");

exports.init_setting_data = function()
{	
	var username = "点击登录";
	var title = "";
	var avatar = "images/avatar.png";
	if (appstore_model.checkIsLogin()) {
		$("#loginuser_layout").unbind("click");
		username = window.loginUser.nickname;
		title = window.loginUser.title;
		avatar = window.loginUser.mediumAvatar;
		$("#exitBtn").show();
	} else {
		$("#loginuser_layout").bind("click", function(){
			setHistoryAction();
			appconfig.page = "login";
			$.ui.loadContent('login',false,false,'slide');
		});
		$("#exitBtn").hide();
	}
	$("#login_user_name").text(username);
	$("#login_user_info").text(title);
	$("#login_user_avatar").attr("src", avatar);

	var startWithSchool = appstore_model.getStoreCache("startWithSchool", "true");
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
