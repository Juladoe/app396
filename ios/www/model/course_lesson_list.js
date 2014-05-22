define(function(require, exports){

var func_text = new String(function(){
/*
<div title="章节1课时1" id="course_lesson_list" data-unload="audio_model.stop" data-footer="lesson_footer" class="panel" data-header="course_lesson_list_header">
	<div id="course_lesson_content">
	
	</div>
</div>
*/
});
var text = func_text.substring(func_text.indexOf("/*") + 2, func_text.lastIndexOf("*/"));
$.ui.addContentDiv("course_lesson_list", text, "章节1课时1");

exports.courseId = 0;
exports.lessonId = 0;

exports.init_course_lesson_data = function(course_id, lesson_id)
{
	var token = appstore_model.getToken();
	course_lesson_list_model.courseId = course_id;
	simpleJsonP(
		schoolHost + "/courselesson/" + course_id + '?callback=?&token=' + token,
		function(data){
			applog(data);
			if (data) {
				$("#course_lesson_menu").empty();
				var list_str = "";
				var chapters = data.chapters;
				for (var i in chapters) {
					var lesson_list = chapters[i].course_lesson_list;
					var chapter = "";
					if (chapters[i].number != 0) {
						chapter = "章节" + chapters[i].number;
					}
					
					list_str += "<li class='divider'>" + chapter + " " + chapters[i].title + "</li>";
					for (var j in lesson_list) {
						//default video
						var item_type = "fa-youtube-play";
						switch (lesson_list[j].type) {
							case "text":
								item_type = "fa-picture-o";
								break;
							case "testpaper":
								item_type = "fa-file-text-o";
								break;
							case "audio":
								item_type = "fa-microphone";
								break;
						}
						//li id == course_lesson `s id
						var sel_class = "li_a_item";
						if (lesson_id == lesson_list[j].id) {
							sel_class = "li_sel li_sel_color li_a_item";
							$("#course_lesson_title").text(lesson_list[j].title);
							setLessonContent(
								lesson_list[j].content, lesson_list[j].type, lesson_id, lesson_list[j].mediaUri);
						}
						list_str += "<li id='lesson_" 
								+ lesson_list[j].id + "'"
								+ " type='" + lesson_list[j].type + "'"
								+ " title='" + lesson_list[j].title + "'"
								+ " mediaUri='" + lesson_list[j].mediaUri + "'"
								+ " onclick='course_lesson_list_model.selCourseLessonMenu(" + lesson_list[j].id  + ");'><a class='" 
								+ sel_class 
								+ "'>"
								+ "<i class='course_lesson_type_normal_color fa " + item_type + "'></i>&nbsp;"
								+ lesson_list[j].title 
								+ "</a><textarea class='tab_hide content'>" + lesson_list[j].content + "</textarea></li>";
					}
				}
				$("#course_lesson_menu").html(list_str);
			}
		}
	);
}

exports.finishclick = function()
{
	var status = $("#finish_lesson_btn").attr("data-status");
	if (status == "finished") {
		course_lesson_list_model.cancel_lesson();
	} else {
		course_lesson_list_model.finish_lesson();
	}
}

exports.finish_lesson = function()
{
	var token = appstore_model.getToken();
	simpleJsonP(
		schoolHost + "/lessonfinish/" + course_lesson_list_model.courseId + "/" + course_lesson_list_model.lessonId + '?callback=?&token=' + token,
		function(data){
			if (data.status == "success") {
				$("#finish_lesson_btn").attr("data-status", "finished");
				$("#finish_lesson_btn").addClass("pressed");
			} else {
				$("#finish_lesson_btn").attr("data-status", "");
				$("#finish_lesson_btn").removeClass("pressed");
				$("#afui").popup(data.message);
			}
		}
	);
}

exports.cancel_lesson = function()
{
	var token = appstore_model.getToken();
	simpleJsonP(
		schoolHost + "/learncancel/" + course_lesson_list_model.courseId + "/" + course_lesson_list_model.lessonId + '?callback=?&token=' + token,
		function(data){
			if (data.status == "success") {
				$("#finish_lesson_btn").attr("data-status", "");
				$("#finish_lesson_btn").removeClass("pressed");
			} else {
				$("#finish_lesson_btn").attr("data-status", "");
				$("#finish_lesson_btn").removeClass("pressed");
				$("#afui").popup(data.message);
			}
		}
	);
}

exports.selCourseLessonMenu = function(lesson_id)
{
	var li = $("#course_lesson_menu").find("#lesson_" + lesson_id);
	$("#course_lesson_menu").find("a").removeClass("li_sel li_sel_color");
	$(li).find("a").addClass("li_sel li_sel_color");
	$("#course_lesson_title").text($(li).attr("title"));
	setLessonContent(
		$(li).find(".content").get(0).value,
		$(li).attr("type"),
		lesson_id,
		$(li).attr("mediaUri")
	);
	$.ui.toggleAsideMenu();
}

exports.learn_status = function(lessonId)
{
	if (appstore_model.checkIsLogin()) {
		course_lesson_list_model.lessonId = lessonId;
		var token = appstore_model.getToken();
		simpleJsonP(
			schoolHost + "/learnstatus/" + course_lesson_list_model.courseId + "/" + lessonId + '?callback=?&token=' + token,
			function(status){
				if (status == "finished") {
					$("#finish_lesson_btn").addClass("pressed");
					$("#finish_lesson_btn").attr("data-status", "finished");
				} else {
					$("#finish_lesson_btn").attr("data-status", "");
					$("#finish_lesson_btn").removeClass("pressed");
				}
			}
		);
	} else {
		setHistoryAction(window.load_courseinfo_page, course_lesson_list_model.courseId);
		$.ui.loadContent('login',false,false,'slide');
	}
}

function setLessonContent(content, type, lesson_id, mediaUri)
{
	audio_model.stop();
	switch (type) {
		case "text":
			content = content.replace(/href=[^=]+\s/g, "href='javascript:void();'");
			break;
		case "testpaper":
			content = "暂不支持此功能";
			break;
		case "audio":
			content = audio_model.audioplayer(mediaUri);
			break;
		case "video":
			content = '<table><tr valign="middle"><td onclick="nativePlay({params});" class="lesson_content_table"><img class="lesson_content_center" src="images/play.png" /><td></tr></table>';
			//content = '<div style="padding:3px;"><video id="playvideo" src="http://bcs.duapp.com/bimbucket/test.mp4" width="100%" height="60%" controls="controls">不支持 video 标签。</video></div>';
			content = content.replace("{params}", "'" + mediaUri + "'," + course_lesson_list_model.courseId + "," + course_lesson_list_model.lessonId);
	}
	$("#course_lesson_content").html(content);
	course_lesson_list_model.learn_status(lesson_id);
}

});
