define(function(require, exports){

	var schoolpage_txt = new String(function(){
	/*
	<div title="添加网校" id="addschool" class="panel" data-header="normal_header" data-footer="none" >
		<textarea id="rsch_list_item" style="display:none;">
			<li class="rsch_bg" onclick="appstore_model.saveSchool(${cb:params});">
			<div style="display:table;">
				<span style="float:left;">
				<img src="${logo}" width="120" height="60" style="" >
				</span>
				<div style="width:200px;margin:5px;">
					<h4 style="display:block;">${title}</h4>
					<h6>${info}</h6>
				</div>
				
			</div>
			</li>
		</textarea>
		 <ul class="list">
			<li class="divider" style="text-align:center;padding-right:1px;">
				<input id="searchWord" type="text" placeholder="输入域名添加网校 如(www.name.com)" style="width:80%;">
				<button onclick="schoolpage_model.qrSearch();" class="topcoat-tab-bar__button header_btn_right">
                  <i class="fa fa-2x fa-qrcode"></i>
                </button>
			</li>
			<li class="divider" style="text-align:center;padding-right:1px;">
				<span onclick="schoolpage_model.searchSchool();" class="button white custom_button_blue">确 定</span>
			</li>
		 </ul>
		 
		 <h2>推荐网校</h2>
		 <ul class="list ul_bg_null" id="school_list">
			
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
		nativeSearch(function(text) {
			exports.isStartQRSearch = false;
			if (text != "") {
				$("#searchWord").val(text);
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
							return a.saveParames;
					}
				});
				$("#school_list").html(list_str);
				//appstore_model.setCache("addschool", "cache");
		});
	}

	exports.searchSchoolForQr = function(url)
	{
		simpleJsonP(url, function(data){
			if (data && data.status == "success") {
				if (data.token) {
					appstore_model.saveUserInfo(data.user, data.token);
				}
				var school = data.school;
				appstore_model.saveSchool(school.title, school.logo, school.url);
			}
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
		$.jsonP(
		{
			url: search + "/mapi/verifyschool" + '?callback=?',
			success:function(data){
				if (data.status == "error") {
					$("#afui").popup("网校不存在!");
				} else {
					var school_info = "网校名称:" + data.school.title
								+ "<br>网站副标题:" + data.school.info
								+ "<br>网站域名:" + data.school.url;
					$("#afui").popup(
						{
							title:"搜索结果",
							message: school_info,
	        				cancelText: "取消",
							doneText: "查看",
					        doneCallback: function () {
					        	
					        }
						}
					);
				}
				$.ui.hideMask();
			},
			timeout:"5000",
			error: function(){
				$.ui.hideMask();
				$("#afui").popup("网校不存在!");
			}
		});
	}

});