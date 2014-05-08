define(function(require, exports){

	var func_txt = new String(function(){
	/*
	<div title="在学" id="learning" class="panel" data-footer='learn_footer' data-tab="learn_footer">
		<!-- templ input list模板 -->
			<textarea id="learning_list_item" style="display:none;">
				<!-- list item -->
				<li class="card-bg">
				<a onclick="load_courseinfo_page('${id}');">
				<table style="width:98%;">
					<tr>
						<td style="width:140px;"><img src="${middlePicture}" width="120" height="80" /></td>
						<td style="text-align:left;">
						<p style="color:#0066FF;">${title}</p>
						<p>课时数：${lessonNum}</p>
						</td>
					</tr>
					<tr>
						<td>
						学习到第 ${memberLearnedNum} 课时
						</td>
						<td style="float:right;">
							<a style="color:#FF6600;border-width: 1px;border-style: solid;border-radius:3px;padding:5px;" class="">继续学习</a>
							<a class="icon check" style="color:#2087FC;"></a>
						</td>
					</tr>
				</table>
				</a>
				</li>
				<!-- list item end -->
			</textarea>
		<!-- templ input list end -->
		<ul class="list card-ul-bg" id="learn_list">
			
		</ul>
	</div>
	*/
	});
	var text = func_txt.substring(func_txt.indexOf("/*") + 2, func_txt.lastIndexOf("*/"));
	$.ui.addContentDiv("learning", text, "在学");

	exports.init_learn_data = function(isappend)
	{
		var token = appstore_model.getToken();
		simpleJsonP(
			schoolHost + "/learncourse" + '?callback=?&token=' + token,
			function(data){
					if (data.status == "success") {
						list_str = zy_tmpl($("#learning_list_item").val(), data.learnCourses, zy_tmpl_count(data.learnCourses),templ_handler);
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