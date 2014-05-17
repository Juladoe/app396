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
							<td style="width:134px;">
								<img src="${cb:smallPicture}" width="124" height="70" />
							</td>
							<td style="text-align:left;" class="list_content">
								<h5 class="custom_normal_color">${title}</h5>
								<p>教师:${cb:teacher}</p>
								<p>学员数:${studentNum}</p>
								<p>${cb:rating}<span class="system_normal" style="float:right;">${cb:price}</span></p>
							</td>
						</tr>
						
					</table>
				</a>
				</li>
			</textarea>
		<!-- templ input list end -->
		<ul class="list card-ul-bg ul_bg_null" id="data_list" offset="0">
				
		</ul>
		</div>
	</div>
	*/
	});

	var text = courselist_text.substring(courselist_text.indexOf("/*") + 2, courselist_text.lastIndexOf("*/"));
	$.ui.addContentDiv("courselist", text, "");

	var refresh_div = "<div id='bottom_refresh_div' class='bottom_refresh_div'><img src='images/loading.gif' >加载中...</div>";
	//是否显示动态加载
	exports.isRefresh = false;
	exports.sort = "";
	exports.scroller = null;

	/**
	* page == offset
	*/
	exports.init_courselist_data = function(isappend, sort, showLoading, callback)
	{
		exports.sort = sort;
		exports.isRefresh = false;
		$("#currentSchoolName").text(schoolName);
		var offset = isappend == true ? $("#data_list").attr("offset"): 0;
		simpleJsonP(
			schoolHost + "/courselist" + '?callback=?&page=' + offset + "&sort=" + sort,
			function(data){
				list_str = zy_tmpl($("#ns_list_item").val(), data.courses, zy_tmpl_count(data.courses), function(a, b) {
					switch (b[1]){
						case "smallPicture":
							if (a.smallPicture == null || a.smallPicture == "") {
								return "images/img1.jpg";
							}
							return a.smallPicture;
						case "teacher":
							return data.users[a["teacherIds"][0]].nickname;
						default:
							return templ_handler(a, b);
					}
				});
				if (data.total_page - data.page > 1) {
					$("#data_list").attr("offset", data.page + 1);
					exports.isRefresh = true;
					//refresh_div = "<li id='bottom_refresh_div' class='bottom_refresh_div' onclick='courselist_model.init_courselist_data(true, \"" + sort + "\");'>加载中...</li>";
				}
				if (isappend) {
					//$("#data_list").find("#bottom_refresh_div").remove();
					$("#data_list").html($("#data_list").html() + list_str);
				} else {
					courselist_model.scroller.scrollToTop(100);
					$("#data_list").html(list_str);
				}
				if (callback) {
					callback();
				}
			}, showLoading);
	}

	initScroll("courselist", {
		"scrollerCallback": function(scroller) {
			if (!courselist_model.isRefresh) {
				scroller.clearInfinite();
				return;
			}
			$("#data_list").append(refresh_div);
	        $.bind(scroller, "infinite-scroll-end", function () {
	        	applog("infinite-scroll-end");
		        $.unbind(scroller, "infinite-scroll-end");
		        scroller.scrollToBottom(1);
		        courselist_model.init_courselist_data(true, exports.sort, true, function(){
		        	$("#data_list").find("#bottom_refresh_div").remove();
		            scroller.clearInfinite();
		        });
		    });
		},
		"init" : function(scroller){
			exports.scroller = scroller;
		}
	});
});
	