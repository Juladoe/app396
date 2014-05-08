define(function(require, exports){

var func_txt = new String(function(){
/*
<div title="收藏的课程" id="favorite" class="panel" data-footer='courselist_footer' data-tab="navbar_setting">
				<!-- templ input list模板 -->
				<textarea id="favorite_list_item" style="display:none;">
					<!-- list item -->
					<li id="favorite_course_${id}" class="card-bg">
					<a onclick="load_courseinfo_page('${id}');">
					<table style="width:98%;">
						<tr>
							<td style="width:140px;">
								<img src="${middlePicture}" width="120" height="80" /></td>
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
							<td style="float:right;">${comment}</td>
						</tr>
						<tr>
							<td>
							${location}
							</td>
							<td style="float:right;">
								<a style="color:#FF6600;border-width: 1px;border-style: solid;border-radius:3px;padding:5px;" onclick="favorite_model.unFavorite('${id}',event);">取消收藏</a>
							</td>
						</tr>
					</table>
					</a>
					</li>
					<!-- list item end -->
				</textarea>
			<!-- templ input list end -->
				<ul class="list card-ul-bg" id="favorite_list">
						
				</ul>
			</div>
*/
});
var text = func_txt.substring(func_txt.indexOf("/*") + 2, func_txt.lastIndexOf("*/"));
$.ui.addContentDiv("favorite", text, "收藏的课程");

exports.unFavorite = function(course_id, event)
{
	var token = appstore_model.getToken();
	simpleJsonP(
		schoolHost + "/unfavorite" + '?callback=?&token=' + token + "&course_id=" + course_id,
		function(data){
			if (data.status == "success") {
				showToast("取消收藏成功!");
				$("#favorite_course_" + course_id).remove();
			}
		}
	);
	//阻止事件冒泡
	event.stopPropagation();
}

exports.favorite = function(course_id)
{
	var token = appstore_model.getToken();
	simpleJsonP(
		webRoot + "/favorite" + '?callback=?&token=' + token + "&course_id=" + course_id,
		function(data){
			//showToast("2222");
			if (data.status == "success") {
				showToast("收藏成功");
			} else {
				showToast(data.message ? data.message : "收藏失败!");
			}
		}
	);
}

exports.init_favorite_data = function(isappend)
{
	$.ui.showMask('加载中...	');
	var token = appstore_model.getToken();
	simpleJsonP(
		webRoot + "/favoritecourse" + '?callback=?&token=' + token,
		function(data){
			if (data.status == "success") {
				list_str = zy_tmpl($("#favorite_list_item").val(), data.favoriteCourses, zy_tmpl_count(data.favoriteCourses),function(a, b) {
					switch (b[1]){
						case "teacher":
							var user = data.users[a["teacherIds"][0]];
							return user ? user.nickname : "";
						default:
							return templ_handler(a, b);
					}
				});
				if (data.count - data.page > 1) {
					$("#data_list").attr("offset", data.page + 1);
					list_str += "<li id='bottom_refresh_div' style='text-align:center;' onclick='init_favorite_data(true);'>加载更多</li>";
				}
				
				if (isappend) {
					$("#favorite_list").find("#bottom_refresh_div").remove();
					$("#favorite_list").html($("#favorite_list").html() + list_str);
				} else {
					$("#favorite_list").html(list_str);
				}
				appstore_model.setCache("favorite", "cache");
			}
			$.ui.hideMask();
		}
	);
}

initScroll("favorite");

});
