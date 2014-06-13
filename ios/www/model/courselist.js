define(function(require, exports){

	var courselist_text = new String(function(){
    /*
     <div title="" id="courselist" class="panel" data-header="courselist_header"
data-footer="courselist_footer" data-height="80">
        <div id="scroller">
                <!-- templ input list模板 -->
                <textarea id="list_item" style="display:none;">
                        <li class="card-bg">
                                <a class="card-bg-a" onclick="load_courseinfo_page('${id}', 1);">
                                        <table style="width:100%;" border="0" cellpadding="0" cellspacing="0">
                                                <tr class="card-bg-line" valign="top">
                                                        <td style="width:48%;">
                                                                <img class="course_pic" src="${cb:middlePicture}" width="100%" height="90" />
                                                        </td>
                                                        <td style="text-align:left;" class="list_content">
                                                                <h4 class="custom_normal_color course_title">
                                                                        ${title}
                                                                </h4>
                                                                <p class="course_teacher">
                                                                        教师:${cb:teacher}
                                                                </p>
                                                        </td>
                                                        <td>
                                                        </td>
                                                </tr>
                                                <tr>
                                                        <td colspan="2">
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
                                                                                                <span class="course_teacher">
                                                                                                        ${cb:studentNum}
                                                                                                </span>
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
                <ul style="padding-top:0px;" class="list card-ul-bg ul_bg_null" id="data_list" start="0">
                </ul>
        </div>
</div>
     */
 });

var text = courselist_text.substring(courselist_text.indexOf("/*") + 2, courselist_text.lastIndexOf("*/"));
$.ui.addContentDiv("courselist", text, "");

exports.errorData = '<div class="noData">网络访问异常,请重新尝试<p><span onclick="courselist_model.init_courselist_data();" class="button white refresh_btn">刷新</span></p></div>';
var refresh_div = "<div id='bottom_refresh_div' class='bottom_refresh_div'><img src='images/loading.gif' >加载中...</div>";
       //是否显示动态加载
       exports.isRefresh = false;
       exports.sort = "";
       exports.scroller = null;
       exports.isShow = false;
       
       /**
        * page == offset
        */
        exports.init_courselist_data = function(isappend, sort, showLoading, callback)
        {
        	exports.sort = sort;
        	exports.isRefresh = false;
            exports.isShow = true;
        	$("#currentSchoolName").text(schoolName);
        	var offset = isappend == true ? $("#data_list").attr("start"): 0;
        	simpleJsonP(
        		schoolHost + "/courses" + '?callback=?&start=' + offset + "&sort=" + sort,
        		function(data){
        			list_str = zy_tmpl($("#list_item").val(), data.data, zy_tmpl_count(data.data), function(a, b) {
        				switch (b[1]){
        					case "middlePicture":
        					if (a.middlePicture == null || a.middlePicture == "") {
        						return "images/img1.jpg";
        					}
        					return a.middlePicture;

        					case "teacher":
        					return a.teachers[0].nickname;

        					default:
        					return templ_handler(a, b);
        				}
        			});
        			var start = (data.start + 1) * normalLimit;
        			if (start < data.total) {
        				$("#data_list").attr("start", start);
        				exports.isRefresh = true;
                   
               }
               if (isappend) {
                   $("#data_list").html($("#data_list").html() + list_str);
               } else {
               		courselist_model.scroller.scrollToTop(10);
               		$("#data_list").html(list_str);
               }
               $(".course_title").each(function(){
               		wrapText(this);
               });

               $(".course_pic").each(function(){
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
            $("#data_list").html(exports.errorData);
           }
      );
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
