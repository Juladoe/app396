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
		schoolHost + "/courses/" + course_id + '/items?callback=?&token=' + token,
		function(data){
			applog(data);
			if (data) {
				$("#course_lesson_menu").empty();
				var list_str = "";
				for (var name in data) {
					var lessonItem = data[name];
					//章节
					if (lessonItem.itemType == "chapter") {
						list_str += "<li class='divider'>" + lessonItem.number + " " + lessonItem.title + "</li>";
						continue;
					} 
					//default video
					var item_type = "fa-youtube-play";
					switch (lessonItem.type) {
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
					if (lesson_id == lessonItem.id) {
						loadLesson(course_lesson_list_model.courseId, lesson_id);
					}
					list_str += "<li id='lesson_" 
							+ lessonItem.id + "'"
							+ " type='" + lessonItem.type + "'"
							+ " title='" + lessonItem.title + "'"
							+ " mediaUri='" + lessonItem.mediaUri + "'"
							+ " onclick='course_lesson_list_model.selCourseLessonMenu(" + lessonItem.id  + ");'><a class='" 
							+ sel_class 
							+ "'>"
							+ "<i class='course_lesson_type_normal_color fa " + item_type + "'></i>&nbsp;"
							+ lessonItem.title 
							+ "</a><textarea class='tab_hide content'>" + lessonItem.content + "</textarea></li>";
				}
				$("#course_lesson_menu").html(list_str);
			}
		}
	);
}

function loadLesson(courseId, lessonId)
{
	var token = appstore_model.getToken();
	simpleJsonP(
		schoolHost + "/courses/" + courseId + "/lessons/" + lessonId + "?callback=?&token=" + token,
		function(lessonItem){
			var li = $("#course_lesson_menu").find("#lesson_" + lessonId);
			$("#course_lesson_menu").find("a").removeClass("li_sel li_sel_color");
			$(li).find("a").addClass("li_sel li_sel_color");
			$("#course_lesson_title").text(lessonItem.title);

			setLessonContent(lessonItem.content, lessonItem.type, lessonId, lessonItem.mediaUri);
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
		schoolHost + "/courses/" + course_lesson_list_model.courseId + "/lessons/" + course_lesson_list_model.lessonId + "/learn?callback=?&token=" + token,
		function(data){
			if (data.error) {
				$("#afui").popup(data.message);
				return;
			}
			if (data) {
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
		schoolHost + "/courses/" + course_lesson_list_model.courseId + "/lessons/" + course_lesson_list_model.lessonId + "/unlearn?callback=?&token=" + token,
		function(data){
			if (data.error) {
				$("#afui").popup(data.message);
				return;
			}
			if (data) {
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
	loadLesson(exports.courseId, lesson_id);
	$.ui.toggleAsideMenu();
}

exports.learn_status = function(lessonId)
{
	if (appstore_model.checkIsLogin()) {
		course_lesson_list_model.lessonId = lessonId;
		var token = appstore_model.getToken();
		simpleJsonP(
			schoolHost + "/courses/" + course_lesson_list_model.courseId + "/lessons/" + lessonId + "/learn_status" + '?callback=?&token=' + token,
			function(data){
				if (data.error) {
					$("#afui").popup(data.message);
					return;
				}
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
			//content = '<table><tr valign="middle"><td onclick="nativePlay({params});" class="lesson_content_table"><img class="lesson_content_center" src="images/play.png" /><td></tr></table>';
			content = '<div style="padding:3px;"><video onclick="nativePlay({params})"; id="playvideo" src="' + mediaUri +'" width="100%" height="60%" controls="controls" autoplay="autoplay">不支持 video 标签。</video></div>';
			content = content.replace("{params}", "'" + mediaUri + "'," + course_lesson_list_model.courseId + "," + course_lesson_list_model.lessonId);
	}
	$("#course_lesson_content").html(content);
	course_lesson_list_model.learn_status(lesson_id);
}

});
