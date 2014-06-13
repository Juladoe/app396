define(function(require, exports){

	var schoolpage_txt = new String(function(){
	/*
	<div title="添加网校" id="addschool" class="panel" data-header="normal_header" data-footer="none" >
		<textarea id="rsch_list_item" style="display:none;">
			<li class="rsch_bg" onclick="appstore_model.saveSchool(${cb:params});">
			<table border="0" cellpadding="0" cellspacing="0" >
				<tr>
					<td>
						<img src="${logo}" class="rsch_logo" >
					</td>
					<td>
					<td valign="top">
						<div style="padding:5px;">
							<h4 class="rsch_title">${title}</h4>
							<h6 class="rsch_info">${info}</h6>
						</div>
					</td>
				</tr>
			</table>
			
			</li>
		</textarea>
		 <ul class="list">
			<li class="divider" style="text-align:center;padding-right:1px;">
				<input value="" id="searchWord" type="text" placeholder="输入域名添加网校 如(www.name.com)" style="width:90%;">
				
			</li>
			<li class="divider" style="text-align:center;padding-right:1px;">
				<span onclick="schoolpage_model.searchSchool();" class="button white custom_button_blue">确 定</span>
			</li>
		 </ul>
		 
	</div>
	*/
	});
	
	exports.title = "添加网校";
	var text = schoolpage_txt.substring(schoolpage_txt.indexOf("/*") + 2, schoolpage_txt.lastIndexOf("*/"));
	$.ui.addContentDiv("addschool", text, "添加网校");

	exports.isStartQRSearch = false;

	exports.qrSearch = function()
	{
		if (exports.isStartQRSearch) {
			return;
		}
		exports.isStartQRSearch = true;
		nativeSearch(
			function(){
				exports.isStartQRSearch = false;
			},
			function(text) {
				if (text != "") {
					schoolpage_model.searchSchoolForQr(text);
				}
		});
	}

	exports.init_schoollist_data = function()
	{
		simpleJsonP(
			webRoot + "/recommendschool" + '?callback=?',
			function(data){
				var list_str = "";
				var schoollist_templ = '<li><span class="list_span_color"></span></li>';
				
				var list_str = zy_tmpl($("#rsch_list_item").val(), data, zy_tmpl_count(data), function(a, b) {
					switch (b[1]){
						case "params":
							var saveParames = "'" + a.title + "',"
								+ "'" + a.logo + "',"
								+ "'" + a.url + "'";
							return saveParames;
					}
				});
				$("#school_list").html(list_str);
		});
	}

	exports.searchSchoolForQr = function(url)
	{
		simpleJsonP(url, function(data){
			if (data.error) {
				$("#afui").popup(data.error.message);
				return;
			}
			if (data.token) {
				appstore_model.saveUserInfo(data.user, data.token);
			}

			var school = data.site;
			var apiVersionRange = data.site.apiVersionRange;
			var versionCheckResult = comparVersion(apiVersion, apiVersionRange.min);
			if (versionCheckResult == window.LOW_VERSION) {
				$("#afui").popup({
					title: school.name + "-网校提示",
					message: "您的客户端版本过低，无法登录，请立即更新至最新版本。",
					cancelText: "取消",
					cancelCallback: function() {
						console.log("cancelled");
					},
					doneText: "立即下载",
					doneCallback: function() {
						checkToUpdataApp();
					},
					cancelOnly: false
				});
				$.ui.hideMask();
				return;
			}

			versionCheckResult = comparVersion(apiVersion, apiVersionRange.max);
			if (versionCheckResult == window.HEIGHT_VERSION) {
				$("#afui").popup({
					title: school.name  + "-网校提示",
					message: "服务器维护中，请稍后再试",
					cancelText: "取消",
					cancelCallback: function() {
						console.log("cancelled");
					},
					cancelOnly: true
				});
				$.ui.hideMask();
				return;
			}

			appstore_model.saveSchoolToStore(school.name, "", school.url);
			cordova.exec(
		                function(version) {
		                    //success
		                },
		                function(error) {
		                    //error
		                },
		                 "WelcomePlugin",
		                 "showWelcomeImages",
		                 [school.splashs]
		            );

		});
	}

	exports.searchSchool = function()
	{
		var search = $("#searchWord").val();
		if (search.length < 3) {
			$("#afui").popup("请输入网校网址!");
			return;
		}

		if (search.indexOf("http://") == -1) {
			search = "http://" + search ;
		}
		if (search[search.length - 1] == "/") {
			search = search.substring(0, search.length - 1);
		}
		$.ui.showMask('加载中...');
		verifyMobileVersion(
			search,
			function(data) {
				if (data.mobileApiUrl) {
					loginSchoolWithSite(data.mobileApiUrl);
				} else {
					$("#afui").popup("当前网校不支持移动端访问!");
				}
			},
			function(data){
				$("#afui").popup("没有搜索到网校!");
			}
		);
	}

	function loginSchoolWithSite(url)
	{
		$.jsonP(
		{
			url: url + "/login_with_site" + '?callback=?',
			success:function(data){
				var apiVersionRange = data.site.apiVersionRange;
				var versionCheckResult = comparVersion(apiVersion,  apiVersionRange.min);
				if (versionCheckResult == window.LOW_VERSION) {
					showMuiltDialog({
						title: "网校提示",
						message: "您的客户端版本过低，无法登录，请立即更新至最新版本。",
						doneText: "立即下载",
						doneCallback: function() {
							checkToUpdataApp();
						}
					});
					$.ui.hideMask();
					return;
				}

				versionCheckResult = comparVersion(apiVersion,  apiVersionRange.max);
				if (versionCheckResult == window.HEIGHT_VERSION) {
					$("#afui").popup("服务器维护中，请稍后再试。");
					$.ui.hideMask();
					return;
				}

				var school = data.site;

				cordova.exec(
			                function(version) {
			                    //success
			                },
			                function(error) {
			                    //error
			                },
			                 "WelcomePlugin",
			                 "showWelcomeImages",
			                 [school.splashs]
			            );
				appstore_model.saveSchoolToStore(school.name, school.logo, url);
				
			},
			timeout:"5000",
			error: function(){
				$.ui.hideMask();
				$("#afui").popup("没有搜索到网校!");
			}
		});
	}

	function checkMobileVersion(apiVersionRange)
	{
		var min = parseFloat(apiVersionRange.min);
		var max = parseFloat(apiVersionRange.max);
		if (window.version < min) {
			return window.LOW_VERSION;
		}

		if (window.version > max) {
			return window.HEIGHT_VERSION;
		}

		return window.NORMAL_VERSION;
	}

});