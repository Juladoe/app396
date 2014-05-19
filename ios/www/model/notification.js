define(function(require, exports){

var func_txt = new String(function(){
/*
<div title="系统通知" id="notification" data-header="notification_header" class="panel" data-footer='courselist_footer' data-tab="navbar_setting">
		<textarea id="notification_list_item" style="display:none;">
			
			<li>
				<a id="message_content" class="icon message">${cb:message}</a>
				<div style="float:right;margin-bottom:1px;font-size:12px;">${createdTime}</div>
			</li>
			
		</textarea>
	
	<ul class="list" id="notification_list">
		
	</ul>
</div>
*/
});

var text = func_txt.substring(func_txt.indexOf("/*") + 2, func_txt.lastIndexOf("*/"));
$.ui.addContentDiv("notification", text, "系统通知");

exports.init_notification_data = function()
{
	var token = appstore_model.getToken();
	simpleJsonP(
		schoolHost + "/notices" + '?callback=?&token=' + token,
		function(data){
				if (data.status == "success") {
					list_str = zy_tmpl(
						$("#notification_list_item").val(), 
						data.notifications, 
						zy_tmpl_count(data.notifications),
						function(a,b) {
							switch (b[1]) {
								case "message":
									message = a.content.message;
									message = message.replace(/<[^>]+>/g, "");
									return message;
							}
						}
					);
					$("#notification_list").html(list_str);
					//appstore_model.setCache("notification", "cache");
				}
			}
		);
}

});