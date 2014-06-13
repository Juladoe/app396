define(function(require, exports){

	var func_txt = new String(function(){
	/*
	<div title="在学" id="learning" data-load="learning_model.load_data" class="panel" data-header="normal_header" data-footer='none' >
		<!-- templ input list模板 -->
			<textarea id="learning_list_item" style="display:none;">
				<!-- list item -->
				<li class="card-bg">
				<a class="card-bg-a" onclick="load_courseinfo_page('${id}', 0);">
				<table style="width:100%;" border="0" cellpadding="0" cellspacing="0">
					<tr valign="top">
						<td><img class="learn_course_pic" src="${cb:middlePicture}" width="100%" height="120" /></td>
					</tr>
					<tr>
						<td style="padding:10px;">
							<h3>${title}</h3>
							<h5 class="learnnum" style="margin-top:5px;">${subtitle}</h5>
						</td>
					</tr>
					<tr>
						<td style="padding:10px;">
							<div style="height:5px;" class="play_progress_layout">
								<span style="height:5px;width:${cb:learnProgress};" class="play_progress_pressed learn_progress"></spn>
							</div>
							<h5 class="learnnum">${cb:memberLearnedNum}</h5>
						</td>
					</tr>
				</table>
				</a>
				</li>
				<!-- list item end -->
			</textarea>
		<!-- templ input list end -->
		<ul class="list card-ul-bg ul_bg_null" id="learn_list" start="0">
			
		</ul>
	</div>
	*/
	});
	var text = func_txt.substring(func_txt.indexOf("/*") + 2, func_txt.lastIndexOf("*/"));
	$.ui.addContentDiv("learning", text, "在学");

	exports.title = "在学";
	exports.scroller = null;
	//是否显示动态加载
	exports.isRefresh = false;

	exports.errorData = '<div class="noData">网络访问异常,请重新尝试<p><span onclick="learning_model.init_learn_data();" class="button white refresh_btn">刷新</span></p></div>';
	exports.noData = "<div class='noData'>暂无在学课程</div>";
	var refresh_div = "<div id='bottom_refresh_div' class='bottom_refresh_div'><img src='images/loading.gif' >加载中...</div>";

	exports.load_data = function(isappend, showLoading, callback)
	{
		if (exports.firstStart) {
			return;
		}
		exports.isRefresh = false;
		var token = appstore_model.getToken();
		var start = isappend == true ? $("#data_list").attr("start"): 0;
		simpleJsonP(
			schoolHost + "/me/learning_courses" + '?callback=?&token=' + token + "&start=" + start,
			function(data){
					if (data && data.error == "not_login") {
						$("#afui").popup(data.message);
						$.ui.goBack();
						return;
					}
					if (data.data.length == 0) {
							$("#learn_list").html(exports.noData);
							return;
						}
					list_str = zy_tmpl($("#learning_list_item").val(), data.data, zy_tmpl_count(data.data),function(a, b) {
						switch(b[1]) {
							case "middlePicture":
								if (a.middlePicture == null || a.middlePicture == "") {
									return "images/img1.jpg";
								}
								return a.middlePicture;

							case "memberLearnedNum":
								if (a.memberLearnedNum == a.lessonNum) {
									return "已学完";
								}
								return "已学" + a.memberLearnedNum + "课时";
							case "learnProgress":
								var memberLearnedNum = a.memberLearnedNum;
								var lessonNum = a.lessonNum;
								var progress = (memberLearnedNum / lessonNum) * 100;
								return progress + "%";
							default:
								return templ_handler(a, b);
						}
					});
					if (((data.start + 1) * normalLimit) <= data.total) {
						$("#data_list").attr("start", data.start + 1);
						exports.isRefresh = true;
						//refresh_div = "<li id='bottom_refresh_div' class='bottom_refresh_div' onclick='courselist_model.init_courselist_data(true, \"" + sort + "\");'>加载中...</li>";
					}
					
					if (isappend && isappend == true) {
						$("#learn_list").html($("#learn_list").html() + list_str);
					} else {
						learning_model.scroller.scrollToTop(10);
						$("#learn_list").html(list_str);
					}

					 $(".learn_course_pic").each(function(){
				                        var width = $(this).width();
				                        var height = width /480 * 270;
				                        $(this).height(height);
				               });
					 
					if (callback) {
						callback();
					}
			}, 
			showLoading,
			function(){
				$("#learn_list").html(exports.errorData);
			}
		);
	}

	exports.firstStart = true;

	exports.init_learn_data = function(isappend, showLoading, callback)
	{
		exports.firstStart = false;
		exports.load_data(isappend, showLoading, callback);
	}

	initScroll("learning", {
		"scrollerCallback": function(scroller) {
			if (!learning_model.isRefresh) {
				scroller.clearInfinite();
				return;
			}
			$("#learn_list").append(refresh_div);
	        $.bind(scroller, "infinite-scroll-end", function () {
	        	applog("infinite-scroll-end");
		        $.unbind(scroller, "infinite-scroll-end");
		        scroller.scrollToBottom(1);
		        learning_model.init_learn_data(true, true, function(){
		        	$("#learn_list").find("#bottom_refresh_div").remove();
		            scroller.clearInfinite();
		        });
		    });
		},
		"init" : function(scroller){
			exports.scroller = scroller;
		}
	});

});