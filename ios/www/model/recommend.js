define(function(require, exports){

	var func_txt = new String(function(){
/*
<div title="推荐课程" id="recommend" class="panel" data-footer='courselist_footer' data-tab="navbar_setting">
	<!-- templ input list模板 -->
	<textarea id="recommend_list_item" style="display:none;">
		<!-- list item -->
		<li>
		<a onclick="load_courseinfo_page('${id}');">
		<table style="width:98%;">
			<tr>
				<td style="width:140px;">
					<img src="${smallPicture}" width="120" height="80" /></td>
				<td style="text-align:left;">
					<p style="color:#0066FF;">${title}</p>
					<p>${teacher}</p>
					<p>${studentNum}</p>
				</td>
			</tr>
			<tr>
				<td>
				${cb:rating}
				</td>
				<td style="float:right;">${cb:price}</td>
			</tr>
			<tr>
				<td>
				${localtion}
				</td>
				<td style="float:right;">
					<a style="color:#FF6600;border-width: 1px;border-style: solid;border-radius:3px;padding:5px;" class="">加入学习</a>
				</td>
			</tr>
		</table>
		</a>
		</li>
		<!-- list item end -->
	</textarea>
<!-- templ input list end -->
	<ul class="list" id="recommend_list" offset="0">
			
	</ul>
</div>
*/
});
var text = func_txt.substring(func_txt.indexOf("/*") + 2, func_txt.lastIndexOf("*/"));
$.ui.addContentDiv("recommend", text, "推荐课程");

exports.init_recommend_data = function(isappend)
{
	$.ui.showMask('加载中...	');
	var offset = isappend ? $("#data_list").attr("offset"): 0;
	$.jsonP(
	{
		url:schoolHost + "/recommendschool" + '?callback=?&offset=' + offset,
		success:function(data){
			list_str = zy_tmpl($("#recommend_list_item").val(), data.list, zy_tmpl_count(data.list),templ_handler);
			if (data.page - data.offset > 1) {
				$("#data_list").attr("offset", data.offset + 1);
				list_str += "<li id='bottom_refresh_div' style='text-align:center;' onclick='recommend_model.init_recommend_data(true);'>加载更多</li>";
			}
			if (isappend) {
				$("#recommend_list").find("#bottom_refresh_div").remove();
				$("#recommend_list").html($("#recommend_list").html() + list_str);
			} else {
				$("#recommend_list").html(list_str);
			}
			$.ui.hideMask();
			appstore_model.setCache("recommend", "cache");
		}
	});
}

initScroll("recommend");

});
