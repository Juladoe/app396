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
				<input value="try3.edusoho.cn" id="searchWord" type="text" placeholder="输入域名添加网校 如(www.name.com)" style="width:90%;">
				
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
			var versionCheckResult = checkMobileVersion(school.versionRange);
			if (versionCheckResult == window.HEIGHT_VERSION) {
				$("#afui").popup({
					title: school.name + "-网校提示",
					message: "当前网校服务器版本较低。请联系网校管理员更新版本！",
					cancelText: "取消",
					cancelCallback: function() {
						console.log("cancelled");
					},
					doneText: "提醒管理员",
					doneCallback: function() {
						$.jsonP({
							url: schoolurl + "/notify_mobile_version?callback=?"
						});
					},
					cancelOnly: false
				});
				$.ui.hideMask();
				return;
			}

			if (versionCheckResult == window.LOW_VERSION) {
				$("#afui").popup({
					title: school.name  + "-网校提示",
					message: "当前网校服务器版本已更新。请更新客户端版本！",
					cancelText: "取消",
					cancelCallback: function() {
						console.log("cancelled");
					},
					doneText: "更新",
					doneCallback: function() {
						//更新
					},
					cancelOnly: false
				});
				$.ui.hideMask();
				return;
			}

			cordova.exec(
		                function(version) {
		                    //success
		                },
		                function(error) {
		                    //error
		                },
		                 "WelcomePlugin",
		                 "showWelcomeImages",
		                 [school.splashs]);
			/*
			var isStart = false;
			var isCancel = false;

			var pop = $("#afui").popup({
				title: "扫描结果",
				message: "正在进入网校...<img src='images/sch_load.gif' >",
				cancelText: "取消",
				cancelCallback: function() {
					isCancel = true;
					pop.hide();
					applog("qr search cancelled");
				},
				cancelOnly: true
			});
			setTimeout(function() {
				if (isStart || isCancel) {
					return;
				}
				appstore_model.saveSchool(school.title, school.logo, school.url);
				pop.hide();
			},
			3000);
			*/
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
			function(data) {
				if (data.mobileVersion == 0) {
					$("#afui").popup("当前网校不支持移动端访问!");
					return;
				}

				if (data.mobileVersion == 1) {
					loginSchoolWithSite(search);
				}
			},
			function(data){
				$("#afui").popup("访问服务器失败!");
			}
		);

		
	}

	function loginSchoolWithSite(search)
	{
		$.jsonP(
		{
			url: search + "/mapi/login_with_site" + '?callback=?',
			success:function(data){
				var versionCheckResult = checkMobileVersion(data.site.versionRange);
				if (versionCheckResult == window.HEIGHT_VERSION) {
					showMuiltDialog({
						title: "网校提示",
						message: "当前网校服务器版本较低。请联系网校管理员更新版本！",
						doneText: "提醒管理员",
						doneCallback: function() {
							$.jsonP({
								url: data.site.url + "/notify_mobile_version?callback=?"
							});
						}
					});
					$.ui.hideMask();
					return;
				}

				if (versionCheckResult == window.LOW_VERSION) {
					showMuiltDialog({
						title: "网校提示",
						message: "当前网校服务器版本已更新。请更新客户端版本！",
						doneText: "提醒管理员",
						doneCallback: function() {
							
						}
					});
					$.ui.hideMask();
					return;
				}

				alert(data.site.splashs);
				cordova.exec(
			                function(version) {
			                    //success
			                },
			                function(error) {
			                    //error
			                },
			                 "WelcomePlugin",
			                 "showWelcomeImages",
			                 [data.site.splashs]
			                 );
				/*
				var school = data.site;
				var school_info = "正在进入... <b>" 
						+ school.name 
						+ "</b><p></p><img src='images/sch_load.gif' >";

				var isStart = false;
				var isCancel = false;
				var pop = $("#afui").popup(
				{
					title:"<span/>",
					message: school_info,
    				cancelText: "取消",
					cancelCallback: function () {
			        	isCancel = true;
			        	pop.hide();
			        },
			        cancelOnly: true
				});
				setTimeout(function(){
					if (isStart || isCancel) {
						return;
					}
			    	appstore_model.saveSchool(school.name, school.logo, school.url);
			    	pop.hide();
			    },3000);
				$.ui.hideMask();
			*/
			},
			timeout:"5000",
			error: function(){
				$.ui.hideMask();
				$("#afui").popup("网校不存在!");
			}
		});
	}

	function checkMobileVersion(versionRange)
	{
		var min = versionRange.min;
		var max = versionRange.max;
		if (window.version < min) {
			return window.LOW_VERSION;
		}

		if (window.version > max) {
			return window.HEIGHT_VERSION;
		}

		return window.NORMAL_VERSION;
	}

});