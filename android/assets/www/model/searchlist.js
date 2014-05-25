define(function(require, exports){

	var func_text = new String(function(){
	/*
	<div title="年兄网" id="searchlist" class="panel" data-header="search_header" data-footer="none">
		<div id="scroller">
		<!-- templ input list模板 -->
			<textarea id="ns_list_item" style="display:none;">
				<li class="card-bg">
				<a class="card-bg-a" onclick="load_courseinfo_page('${id}');">
					<table style="width:98%;">
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
				</a>
				</li>
			</textarea>
		<!-- templ input list end -->
		<ul class="list card-ul-bg ul_bg_null" id="search_list" offset="0">
			
		</ul>
		</div>
	</div>
	*/
	});

	var text = func_text.substring(func_text.indexOf("/*") + 2, func_text.lastIndexOf("*/"));
	$.ui.addContentDiv("searchlist", text, "");

	exports.clear = function() {
		$("#global_search").val('');
		$("#search_list").empty();
	}
	var refresh_div = "<div id='bottom_refresh_div' class='bottom_refresh_div'><img src='images/loading.gif' >加载中...</div>";

	exports.isRefresh = false;
	exports.scroller = null;

	/**
	* page == offset
	*/
	exports.init_searchlist_data = function(isappend, sort, showLoading, callback)
	{
		exports.sort = sort;
		exports.isRefresh = false;

		var offset = isappend == true ? $("#search_list").attr("offset"): 0;
		var search = $("#global_search").val();
		if (search == "") {
			$("#afui").popup("请输入搜索内容");
			return;
		}
		simpleJsonP(
			schoolHost + "/courselist" + '?callback=?&page=' + offset + "&search=" + search,
			function(data){
				if (data.courses.length == 0) {
					$("#search_list").html("<div style='text-align:center;'>没有搜索到相关内容</div>");
					return;
				}
				list_str = zy_tmpl($("#ns_list_item").val(), data.courses, zy_tmpl_count(data.courses), function(a, b) {
					switch (b[1]){
						case "middlePicture":
							if (a.middlePicture == null || a.middlePicture == "") {
								return "images/img1.jpg";
							}
							return a.middlePicture;	
						case "teacher":
							return data.users[a["teacherIds"][0]].nickname;
						default:
							return templ_handler(a, b);
					}
				});
				if (data.total_page - data.page > 1) {
					exports.isRefresh = true;
					$("#search_list").attr("offset", data.page + 1);
					//list_str += "<li id='bottom_refresh_div' style='text-align:center;' onclick='searchlist_model.init_searchlist_data(true);'>加载更多</li>";
				}

				if (isappend) {
					//$("#search_list").find("#bottom_refresh_div").remove();
					$("#search_list").html($("#search_list").html() + list_str);
				} else {
					searchlist_model.scroller.scrollToTop(100);
					$("#search_list").html(list_str);
				}

				if (callback) {
					callback();
				}
			}, showLoading);
	}

	initScroll("searchlist", {
		"scrollerCallback": function(scroller) {
			if (!searchlist_model.isRefresh) {
				scroller.clearInfinite();
				return;
			}
			$("#search_list").append(refresh_div);
	        $.bind(scroller, "infinite-scroll-end", function () {
	        	applog("infinite-scroll-end");
		        $.unbind(scroller, "infinite-scroll-end");
		        scroller.scrollToBottom(1);
		        searchlist_model.init_searchlist_data(true, exports.sort, true, function(){
		        	$("#search_list").find("#bottom_refresh_div").remove();
		            scroller.clearInfinite();
		        });
		    });
		},
		"init" : function(scroller){
			exports.scroller = scroller;
		}
	});
});
	