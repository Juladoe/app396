define(function(require, exports){

var func_txt = new String(function(){
/*
<div title="收藏的课程" id="favorite" class="panel" data-footer='none' data-header="normal_header">
				<!-- templ input list模板 -->
				<textarea id="favorite_list_item" style="display:none;">
					<!-- list item -->
					<li id="favorite_course_${id}" class="card-bg">
					<a class="card-bg-a" onclick="load_courseinfo_page('${id}');">
						<table style="width:100%;" border="0" cellpadding="0" cellspacing="0">	
							<tr class="card-bg-line" valign="top">
								<td style="width:160px;">
									<img src="${cb:middlePicture}" width="160" height="90" />
								</td>
								<td style="text-align:left;" class="list_content">
									<h4 class="custom_normal_color">${title}</h4>
									<p>教师:${cb:teacher}</p>
								</td>
								<td>

								</td>
							</tr>
							<tr>
								<td colspan = "2">
									<div class="course_list_bottom">
										<table style="width:100%;">
											<tr valign="middle">
												<td align="left" width="33%">
													${cb:rating}
												</td>
												<td align="center" width="33%" class="course_price">
													${cb:price}
												</td>
												<td align="right" width="33%">
													学员数:${studentNum}
												</td>
											<tr>
										</table>
										
									</div>
								</td>
							</tr>
						</table>
					<!--
					<table style="width:100%;">
						<tr valign="top">
							<td><img src="${middlePicture}" width="100%" height="120" /></td>
						</tr>
						<tr>
							<td style="width:120px;">
								<img src="${middlePicture}" width="110" height="80" /></td>
							<td style="text-align:left;" class="list_content">
								<h4 class="custom_normal_color">${title}</h4>
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
								<a class="learn_btn" onclick="favorite_model.unFavorite('${id}',event);">取消收藏</a>
							</td>
						</tr>
					</table>
					-->
					</a>
					</li>
					<!-- list item end -->
				</textarea>
			<!-- templ input list end -->
				<ul class="list card-ul-bg ul_bg_null" id="favorite_list">
						
				</ul>
			</div>
*/
});

var text = func_txt.substring(func_txt.indexOf("/*") + 2, func_txt.lastIndexOf("*/"));
$.ui.addContentDiv("favorite", text, "收藏的课程");


exports.title = "收藏的课程";
exports.noData = "<div class='noData'>暂无收藏课程</div>";

exports.unFavorite = function(course_id, event)
{
	var token = appstore_model.getToken();
	simpleJsonP(
		schoolHost + "/courses/" + course_id + "/unfavorite" + '?callback=?&token=' + token,
		function(data){
			if (data.error) {
				showToast(data.message);
				return;
			}
			if (data) {
				showToast("取消收藏成功!");
				$("#favorite_course_" + course_id).remove();
				$("#favorite_radio").removeAttr("checked");
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
		schoolHost + "/courses/" + course_id + "/favorite" + '?callback=?&token=' + token,
		function(data){
			if (data.error) {
				showToast(data.message);
				return;
			}
			if (data) {
				showToast("收藏成功");
				$("#favorite_radio").attr("checked", "checked");
			}
		}
	);
}

exports.init_favorite_data = function(isappend)
{
	$.ui.showMask('加载中...	');
	var token = appstore_model.getToken();
	simpleJsonP(
		webRoot + "/me/favorite_courses" + '?callback=?&token=' + token,
		function(data){
			if (data.error) {
				$("#afui").popup(data.message);
				$.ui.goBack();
				return;
			}
			if (data.data.length == 0) {
				$("#favorite_list").html(exports.noData);
				return;
			}
			list_str = zy_tmpl($("#favorite_list_item").val(), data.data, zy_tmpl_count(data.data),function(a, b) {
				switch (b[1]){
					case "middlePicture":
						if (a.middlePicture == null || a.middlePicture == "") {
							return "images/img1.jpg";
						}
						return a.middlePicture;
					case "teacher":
						return data.user ? data.user.nickname : "";
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
		}
	);
}

initScroll("favorite");

});
