define(function(require, exports){

	var courselist_text = new String(function(){
	/*
	<div title="" id="courselist" class="panel" data-header="courselist_header" data-footer="courselist_footer" data-height="80">
		<div id="scroller">
		<!-- templ input list模板 -->
			<textarea id="ns_list_item" style="display:none;">
				<li class="card-bg">
				<a onclick="load_courseinfo_page('${id}');">
					<table style="width:98%;">
						<tr>
							<td style="width:140px;"><img src="${smallPicture}" width="120" height="80" /></td>
							<td style="text-align:left;">
							<p style="color:#0066FF;">${title}</p>
							<p>教师:${cb:teacher}</p>
							<p>学员数:${studentNum}</p>
							</td>
						</tr>
						<tr>
						<td>
						${cb:rating}
						</td>
						<td style="float:right;">${cb:price}</td>
						</tr>
					</table>
				</a>
				</li>
			</textarea>
		<!-- templ input list end -->
		<ul class="list card-ul-bg" id="data_list" offset="0">
				
		</ul>
		</div>
	</div>
	*/
	});

	var text = courselist_text.substring(courselist_text.indexOf("/*") + 2, courselist_text.lastIndexOf("*/"));
	$.ui.addContentDiv("courselist", text, "");
	/**
	* page == offset
	*/
	exports.init_courselist_data = function(isappend, sort)
	{
		$("#currentSchoolName").text(schoolName);
		var offset = isappend == true ? $("#data_list").attr("offset"): 0;
		simpleJsonP(
			schoolHost + "/courselist" + '?callback=?&page=' + offset + "&sort=" + sort,
			function(data){
				list_str = zy_tmpl($("#ns_list_item").val(), data.courses, zy_tmpl_count(data.courses), function(a, b) {
					switch (b[1]){
						case "teacher":
							return data.users[a["teacherIds"][0]].nickname;
						default:
							return templ_handler(a, b);
					}
				});
				if (data.total_page - data.page > 1) {
					$("#data_list").attr("offset", data.page + 1);
					list_str += "<li id='bottom_refresh_div' style='text-align:center;' onclick='courselist_model.init_courselist_data(true, \"" + sort + "\");'>加载更多</li>";
				}
				if (isappend) {
					$("#data_list").find("#bottom_refresh_div").remove();
					$("#data_list").html($("#data_list").html() + list_str);
				} else {
					$("#data_list").html(list_str);
				}
			});
	}
	initScroll("courselist");
});
	