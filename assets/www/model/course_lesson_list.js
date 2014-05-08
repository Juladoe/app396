define(function(require, exports){

var func_text = new String(function(){
/*
<div title="章节1课时1" id="course_lesson_list" data-footer="lesson_footer" class="panel" data-header="course_lesson_list_header">
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
				for (var i in data) {
					var lesson_list = data[i].course_lesson_list;
					var chapters = "";
					if (data[i].number != 0) {
						chapters = "章节" + data[i].number;
					}
					
					list_str += "<li class='divider'>" + chapters + " " + data[i].title + "</li>";
					for (var j in lesson_list) {
						if (lesson_list[j].type != "text") {
							continue;
						}
						//li id == course_lesson `s id
						var sel_class = "li_a_item";
						if (lesson_id == lesson_list[j].id) {
							sel_class = "li_sel li_sel_color li_a_item";
							$("#course_lesson_title").text(lesson_list[j].title);
							var content = lesson_list[j].content;
							content = content.replace(/href=[^=]+\s/g, "href='javascript:void();'");
							$("#course_lesson_content").html(content);
							course_lesson_list_model.learn_status(lesson_id);
						}
						list_str += "<li id='lesson_" 
								+ lesson_list[j].id 
								+ "' onclick='course_lesson_list_model.selCourseLessonMenu(" + lesson_list[j].id  + ");'><a class='" 
								+ sel_class 
								+ "'>" 
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
			}
		}
	);
}

exports.selCourseLessonMenu = function(lesson_id)
{
	var li = $("#course_lesson_menu").find("#lesson_" + lesson_id);
	$("#course_lesson_menu").find("a").removeClass("li_sel li_sel_color");
	$(li).find("a").addClass("li_sel li_sel_color");
	$("#course_lesson_title").text($(li).find("a").get(0).innerText);
	$("#course_lesson_content").html($(li).find(".content").get(0).value);
	course_lesson_list_model.learn_status(lesson_id);
	$.ui.toggleAsideMenu();
}

exports.learn_status = function(lessonId)
{
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
}

});
