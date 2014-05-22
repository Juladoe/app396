define(function(require, exports){

	var func_txt = new String(function(){
	/*
	<div title="在学" id="learning" class="panel" data-header="normal_header" data-footer='none' >
		<!-- templ input list模板 -->
			<textarea id="learning_list_item" style="display:none;">
				<!-- list item -->
				<li class="card-bg">
				<a class="card-bg-a" onclick="load_courseinfo_page('${id}');">
				<table style="width:100%;" border="0" cellpadding="0" cellspacing="0">
					<tr valign="top">
						<td><img src="${cb:middlePicture}" width="100%" height="120" /></td>
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
		<ul class="list card-ul-bg ul_bg_null" id="learn_list">
			
		</ul>
	</div>
	*/
	});
	var text = func_txt.substring(func_txt.indexOf("/*") + 2, func_txt.lastIndexOf("*/"));
	$.ui.addContentDiv("learning", text, "在学");

	exports.title = "在学";
	exports.noData = "<div class='noData'>暂无在学课程</div>";

	exports.init_learn_data = function(isappend)
	{
		var token = appstore_model.getToken();
		simpleJsonP(
			schoolHost + "/learncourse" + '?callback=?&token=' + token,
			function(data){
					if (data.status == "success") {
						if (data.learnCourses.length == 0) {
							$("#learn_list").html(exports.noData);
							return;
						}
						list_str = zy_tmpl($("#learning_list_item").val(), data.learnCourses, zy_tmpl_count(data.learnCourses),function(a, b) {
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
						if (data.count - data.page > 1) {
							$("#learn_list").attr("offset", data.page + 1);
							list_str += "<li id='bottom_refresh_div' style='text-align:center;' onclick='learning_model.init_learn_data(true);'>加载更多</li>";
						}
						
						if (isappend) {
							$("#learn_list").find("#bottom_refresh_div").remove();
							$("#learn_list").html($("#learn_list").html() + list_str);
						} else {
							$("#learn_list").html(list_str);
						}
						appstore_model.setCache("learning", "cache");
					}
			}
		);
	}

	initScroll("learning");

});