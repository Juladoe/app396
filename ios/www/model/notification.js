define(function(require, exports){

	var func_txt = new String(function(){
/*
<div title="系统通知" id="notification" data-header="notification_header" class="panel" data-footer='none' data-tab="navbar_setting">
		<textarea id="notification_list_item" style="display:none;">
			
			<li class="card-bg notify_li">
				<div style="float:left;padding-right: 10px;">
				<i class="fa notify_icon fa-comment course_lesson_type_normal_color"></i>
				</div>
				<a style="line-height:30px;" id="message_content" class="card-bg-a">${cb:message}</a>
				<div style="float:right;margin-bottom:1px;font-size:12px;">${cb:createdTime}</div>
			</li>
			
		</textarea>
	
	<ul class="list card-ul-bg ul_bg_null" id="notification_list">
		
	</ul>
</div>
*/
});

	var text = func_txt.substring(func_txt.indexOf("/*") + 2, func_txt.lastIndexOf("*/"));
	$.ui.addContentDiv("notification", text, "系统通知");

	exports.noData = "<div class='noData'>暂无系统通知</div>";

	exports.init_notification_data = function()
	{
		var token = appstore_model.getToken();
		simpleJsonP(
			schoolHost + "/me/notifications" + '?callback=?&token=' + token,
			function(data){
				if (data.error) {
					$("#afui").popup(data.message);
					$.ui.goBack();
					return;
				}
				if (data.length == 0) {
					$("#notification_list").html(exports.noData);
					return;
				}
				list_str = zy_tmpl(
					$("#notification_list_item").val(), 
					data, 
					zy_tmpl_count(data),
					function(a,b) {
						switch (b[1]) {
							case "message":
							message = a.content.message;
							if (message) {
								message = message.replace(/<[^>]+>/g, "");
								return message;
							}
							switch(a.content.threadType) {
								case "question":
								message = a.content.threadUserNickname
								+ " 在课程 "
								+ a.content.courseTitle
								+ " 发表了问题 "
								+ a.content.threadTitle;
								return message;
							}
							case "createdTime":
							return a.createdTime.substring(0, 10);
						}
					}
					);
				$("#notification_list").html(list_str);
			}
			);
	}

});