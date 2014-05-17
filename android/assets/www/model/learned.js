define(function(require, exports){

	var func_txt = new String(function(){
	/*
	<div title="在学" id="learned" class="panel" data-footer='learn_footer' data-tab="learn_footer">
		<!-- templ input list模板 -->
			<textarea id="learned_list_item" style="display:none;">
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
						
						</td>
						<td style="float:right;">
							<a style="color:#FF6600;border-width: 1px;border-style: solid;border-radius:3px;padding:5px;">已学完</a>
						</td>
					</tr>
				</table>
				</a>
				</li>
				<!-- list item end -->
			</textarea>
		<!-- templ input list end -->
		<ul class="list card-ul-bg" id="learned_list">
			
		</ul>
	</div>
	*/
	});
	var text = func_txt.substring(func_txt.indexOf("/*") + 2, func_txt.lastIndexOf("*/"));
	$.ui.addContentDiv("learned", text, "已学完");

	exports.init_learned_data = function(isappend)
	{
		var token = appstore_model.getToken();
		simpleJsonP(
			schoolHost + "/learnedcourse" + '?callback=?&token=' + token,
			function(data){
					if (data.status == "success") {
						list_str = zy_tmpl($("#learned_list_item").val(), data.learnedCourses, zy_tmpl_count(data.learnedCourses),templ_handler);
						if (data.count - data.page > 1) {
							$("#learned_list").attr("offset", data.page + 1);
							list_str += "<li id='bottom_refresh_div' style='text-align:center;' onclick='learning_model.init_learned_data(true);'>加载更多</li>";
						}
						if (isappend) {
							$("#learned_list").find("#bottom_refresh_div").remove();
							$("#learned_list").html($("#learned_list").html() + list_str);
						} else {
							$("#learned_list").html(list_str);
						}
						appstore_model.setCache("learned", "cache");
					}
			}
		);
	}

	initScroll("learned");

});