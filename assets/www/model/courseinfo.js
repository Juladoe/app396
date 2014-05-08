define(function(require, exports){

var courseinfo_text = new String(function(){
/*
<div title="课程详情" id="courseinfo" class="panel" data-header='info_header' data-footer="courselist_footer">
	<!-- templ input list模板 -->
	<textarea id="courseinfo_cb_course_list" style="display:none;">
		<tr>
			<td style='width:70%;'>
				<p><a onclick="showCourseInfo('${type}','${courseId}','http://bcs.duapp.com/bimbucket/test.mp4','${id}');">${title}</a></p>
			</td>
			<td align="right" style='width:30%;'>
				<div>
				<span>图文</span>
				${cb:type}
				${cb:islearn}	
				</div>
			</td>
		</tr>
	</textarea>
	<textarea id="courseinfo_cb_course_comment" style="display:none;">
		<tr class="course_comment_table">
			<td style="width:60px;" valign="top">
				<img class="small-avatar" src="${cb:avatar}" />
			</td>
			<td>
				<table style="width:100%;">
					<tr>
						<td>
							<span class="nickname">${cb:nickname}</span>
						</td>
						<td align="right">
							${createdTime}
						</td>
					</tr>
					<tr>
						<td colspan="2">
							${cb:rating}
						</td>
					</tr>
					<tr>
						<td colspan="2">
							${content}
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</textarea>
	<textarea id="courseinfo_list_item" style="display:none;">
		<div id="course_header">
			<img style="width:100%;height:160px;" src="${couse_introduction.largePicture}"/>
			<input type="hidden" id="course_payment" value="alipay" />
			<input type="hidden" id="course_title" value="${couse_introduction.title}" />
			<input type="hidden" id="course_courseId" value="${couse_introduction.id}" />
			<input type="hidden" id="course_price" value="${couse_introduction.price}" />
		</div>
		<!-- 课程tab -->
		<div style="width:98%;margin:0 auto;">
			<div class="button-grouped flex tabbed">
					<a class="button tab_radio" data-v="one" data-ignore-pressed="true" onclick="changeTab(this);">课时列表</a>
					<a class="button tab_radio pressed" data-v="two" data-ignore-pressed="true" onclick="changeTab(this);">课程介绍</a>
					<a class="button tab_radio" data-v="three" data-ignore-pressed="true" onclick="changeTab(this);">课程评价</a>
			</div>
			<!--评论 -->
			<div style="padding:3px;">
				<div id="one" class="ui-body-d ui-content tab_content tab_hide">
					<table style="width:100%;">
						${cb:course_list}
					</table>
				</div>
				<div id="two" class="ui-body-d ui-content tab_content">
					<div class="card">
						<p class="card-head">
							${couse_introduction.title}
						</p>
						<table style="width:100%">
							<tbody>
								<tr style="height:30px;">
									<th style="float:left;">
										${cb:rating}
									</th>
									<th style="float:right;">
										<p>
											来自${cb:location}
										</p>
									</th>
								</tr>
								<tr style="height:30px;">
									<td style="float:left;">
										<p>
											教师:${cb:teacher}
										</p>
									</td>
									<td style="float:right;">
										<p>
											${cb:price}
										</p>
									</td>
								</tr>
								<tr style="height:30px;">
									<td style="float:left;">
										<p>
											学员数:${couse_introduction.studentNum}
										</p>
									</td>
									<td style="float:right;">
										<p>
										${cb:islearn}
										</p>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
					<div class="card">
						<p class="card-head">
							课程介绍
						</p>
						<p class="umh4">
							${couse_introduction.about}
						</p>
					</div>
					<div class="card">
						<p class="card-head">课程目标</p>
						<ul style="margin-left:30px;">
							${cb:goals}
						</ul>
					</div>
					<div class="card">
						<p class="card-head">适合人群</p>
							<ul style="margin-left:30px;">
								${cb:audiences}
							</ul>
						</p>
					</div>
				</div>
				<div id="three" class="ui-body-d ui-content tab_content tab_hide">
					<table style="width:100%;border-collapse:collapse;" id="course_comment_table">
						${cb:course_comment}
					</table>
					<div class="${cb:ishide}" style="text-align:center;padding-right:1px;margin-top:20px;">
						<span onclick="courseinfo_model.addComment();" class="button white custom_button_blue">评 论</span>
					</div>
				</div>
				
			</div>
		</div>
		<!--评论end-->
	</textarea>
	<!-- templ input list end -->

	<div id="course_content">
		
	</div>
</div>
*/
});

var text = courseinfo_text.substring(courseinfo_text.indexOf("/*") + 2, courseinfo_text.lastIndexOf("*/"));
$.ui.addContentDiv("courseinfo", text, "课程详情");

var content = '请打分:<span id="commentStar" style="cursor: pointer; width: 100px;">' 
				+ '<img onclick="courseinfo_model.changeCommentStar(this);" src="images/star-off.png" alt="1" title="很差">&nbsp;' 
				+ '<img onclick="courseinfo_model.changeCommentStar(this);" src="images/star-off.png" alt="2" title="较差">&nbsp;' 
				+ '<img onclick="courseinfo_model.changeCommentStar(this);" src="images/star-off.png" alt="3" title="还行">&nbsp;' 
				+ '<img onclick="courseinfo_model.changeCommentStar(this);" src="images/star-off.png" alt="4" title="推荐">&nbsp;' 
				+ '<img onclick="courseinfo_model.changeCommentStar(this);" src="images/star-off.png" alt="5" title="力荐">' 
				+ '<input id="commentScore" type="hidden" name="score"></span>'
				+ '<textarea id="commentContent" rows="6" placeholder="评价内容" class="pressed">{content}</textarea>'
				+ '<div class="formGroupHead quick_comment_wrap">快捷回复:</div>'
				+ '<input readonly size="2" type="text" rating="3" value="不错" class="quick_comment">'
				+ '<input readonly size="5" type="text" rating="4" value="感觉很好" class="quick_comment">'
				+ '<input readonly size="10" type="text" rating="5" value="课程内容很好" class="quick_comment">';

exports.quick_comment = function(input)
{
	var rating = $(input).attr("rating");
	var content = $(input).val();
	var courseId = $("#course_courseId").val();
	var token = appstore_model.getToken();
	simpleJsonP(
		schoolHost + "/commentcourse/" + courseId + '?callback=?&token=' + token 
				+ "&rating=" + rating + "&content=" + content,
		function(data){
			if (data.status == "success") {
				courseinfo_model.getComments();
			}
		}
	);
}

exports.addComment = function()
{
	var message = "";
	var CommentContent = courseinfo_model.loginUserCommentContent ? courseinfo_model.loginUserCommentContent : "";
	var rating = courseinfo_model.rating ? courseinfo_model.rating  : 0;
	message = content.replace("{content}", CommentContent);
	for(i=0; i < rating; i++) {
		message = message.replace("star-off", "star-on");
	}
	
	$("#afui").popup({
		id : "addCommentPopup",
		title:"评价课程",
		message: message,
		cancelText: "取消",
		doneText: "评价",
        doneCallback: function () {
        	var courseId = $("#course_courseId").val();
        	var rating = $("#commentScore").val();
        	var content = $("#commentContent").val();
        	var token = appstore_model.getToken();
        	simpleJsonP(
        		schoolHost + "/commentcourse/" + courseId + '?callback=?&token=' + token 
        				+ "&rating=" + rating + "&content=" + content,
        		function(data){
        			if (data.status == "success") {
        				courseinfo_model.getComments();
        			}
        		}
        	);
        },
        onShow: function()
        {
        	var popupThis = this;
        	$("#addCommentPopup").find(".quick_comment").bind("click", function() {
        		courseinfo_model.quick_comment(this);
        		popupThis.hide();
        	});
        }
	});
}

exports.getComments = function()
{
	var courseId = $("#course_courseId").val();
	var token = appstore_model.getToken();
	simpleJsonP(
		schoolHost + "/commentlist/" + courseId + '?callback=?&token=' + token,
		function(data){
			if (data.status == "success") {
				var course_comment_templ = $("#courseinfo_cb_course_comment").val();
				var users = data['commentUsers'];
				list_str = zy_tmpl(
					course_comment_templ, data['course_comment'], 
					zy_tmpl_count(data['course_comment']), 
					function(a,b) {
						return course_comment_handler(a, b, users);
					}
				);
				$("#course_comment_table").html(list_str);
			} else {
				$("#afui").popup("课程不存在或已关闭!");
			}
		}
	);
}

exports.changeCommentStar = function(img)
{
	var star = img.alt;
	$("#commentScore").val(star);
	$("#commentStar").find("img").each(function(){
		var temp_alt = this.alt;
		if (temp_alt <= star) {
			this.src = "images/star-on.png";
		} else {
			this.src = "images/star-off.png";
		}
	});
}


exports.init_courseinfo_data = function(course_id)
{
	var token = appstore_model.getToken();
	simpleJsonP(
		schoolHost + "/course/" + course_id + '?callback=?&token=' + token,
		function(data){
			if (data.status == "success") {
				list_str = zy_tmpl($("#courseinfo_list_item").val(), data.courseinfo, zy_tmpl_count(data.courseinfo),function(a, b) {
					switch (b[1]) {
						case "location":
							return schoolName;
						case "teacher":
							var teacher = a.couse_introduction['teacherIds'][0];
							return a.teacherUsers[teacher].nickname;
					}
					return templ_courseinfo_handler(a, b);
				});

				if (data.courseinfo[0].favoriteStatus == true) {
					$("#favorite_btn").addClass("favorite");
				} else {
					$("#favorite_btn").removeClass("favorite");
				}
				$("#course_content").html(list_str);
				$("#favorite_btn").attr("courseId", course_id);
			} else {
				$("#afui").popup("课程不存在或已关闭!");
			}
		}
	);
}
exports.refundDialog = function()
{
	var courseId = $("#course_courseId").val();
	if (! appstore_model.checkIsLogin()) {
		setHistoryAction(window.load_courseinfo_page, courseId);
		$.ui.loadContent('login',false,false,'slide');
		return;
	}
	$("#afui").popup({
        title: "退出学习",
        message: '退学原因:<select id="course_refundselect" style="-webkit-appearance: listbox;" class="pressed">'
                 + '<option value="课程内容质量差">课程内容质量差</option><option value="老师服务态度不好">老师服务态度不好</option>'
                 + '<option value="其他">其他</option></select>',

        cancelText: "取消",
        cancelCallback: function () {},
        doneText: "退出学习",
        doneCallback: function () {
            refundCourse(courseId, $("#course_refundselect").val());
        },
        cancelOnly: false
    });
}

exports.buyDialog = function()
{
	var courseId = $("#course_courseId").val();
	if (! appstore_model.checkIsLogin()) {
		setHistoryAction(window.load_courseinfo_page, courseId);
		$.ui.loadContent('login',false,false,'slide');
		return;
	}
	var payment = $("#course_payment").val();
	var title = $("#course_title").val();
	var price = $("#course_price").val();
	var message = "<p>课程名称:" + title + "</p>";
	var doneBtn = "加入学习";

	if (price <= 0) {
		message += "<p>此课程为免费课程，无需购买，可直接加入学习。</p>";
	} else {
		doneBtn = "购买课程";
		message += "<p>价格：" + price + "</p><p>支付方式:支付宝</p>";
	}

	$("#afui").popup({
        title: "加入学习",
        message: message,
        cancelText: "取消",
        cancelCallback: function () {},
        doneText: doneBtn,
        doneCallback: function () {
            buyCourse(payment, courseId);
        },
        cancelOnly: false
    });
}

//模板回调函数
function templ_courseinfo_handler(a, b)
{
	var result = "";
	var star_templ = '<span class="stars-{num}"></span>';
	var courseinfo_templ = $("#courseinfo_cb_course_list").val();
	//类型变量  //cb:xxx
	switch (b[1]) {
		case "ishide":
			return appstore_model.checkIsLogin() ? "" : "hide";
		case "course_comment":
			var course_comment_templ = $("#courseinfo_cb_course_comment").val();
			var users = a['users'];
			return zy_tmpl(
					course_comment_templ, 
					a['course_comment'], 
					zy_tmpl_count(a['course_comment']), 
					function(a, b) {
						return course_comment_handler(a, b, users);
					}
				);

		case "course_list":
			var learnStatuses = a['learnStatuses'];
			return zy_tmpl(
				courseinfo_templ, 
				a['course_list'], 
				zy_tmpl_count(a['course_list']),
				function(a, b) {
					return templ_courselist_handler(a, b, learnStatuses);
				}
			);

		case "rating":
			var num = parseInt(a["couse_introduction"].rating);
			if (!num) {
				num = 1;
			}
			return star_templ.replace("{num}", num);

		case "islearn":
			if (checkIsTeacher(a, window.loginUserName)) {
				return "";
			}
			var t_btn = "";
			if (a["member"]) {
				t_btn =  '<a onclick="courseinfo_model.refundDialog();" class="learn_btn">退出学习</a>';
			} else {
				var buyParames= "'alipay','" + a["couse_introduction"].id + "'";
				var price = parseInt(a["couse_introduction"].price);
				if (price && price > 0) {
					t_btn = '<a onclick="courseinfo_model.buyDialog();" class="learn_btn">购买课程</a>';
				} else {
					t_btn = '<a onclick="courseinfo_model.buyDialog();" class="learn_btn">加入学习</a>';
				}
			}
			return t_btn;
		case "price":
			var price = parseInt(a["couse_introduction"].price);
			return price == 0 ? "免费" :price + "元";

		case "goals":
			var course_target = a["couse_introduction"].goals
			for (var i in course_target) {
				result += '<li><p>' + course_target[i] + '</p></li>';
			}
			return result;

		case "audiences":
			var course_notice = a["couse_introduction"].audiences
			for (var i in course_notice) {
				result += '<li><p>' + course_notice[i] + '</p></li>';
			}
			return result;
	}
	return '';
}

function checkIsTeacher(a, loginUser)
{
	var teacherIds = a.couse_introduction["teacherIds"]; 
	var  teacherUsers = a["teacherUsers"];
	for (var i in teacherIds) {
		var teacher = teacherUsers[teacherIds[i]];
		if (teacher && teacher.nickname == loginUser) {
			return true;
		}
	}
	return false;
}

function course_comment_handler(a, b, users) 
{

	switch (b[1]){
			case "nickname":
				var user = users[a['userId']];
				if (user && user.nickname == window.loginUserName) {
					courseinfo_model.loginUserCommentContent = a["content"];
					courseinfo_model.rating = a["rating"];
				}
				return user ? user.nickname : "" ;
			case "avatar":
				var smallAvatar = users[a['userId']].smallAvatar;
				if (smallAvatar) {;
					return smallAvatar;
				} 
				return defalut_avatar;
			default:
				return templ_handler(a, b);
		}
}

//课时列表模板回调函数
function templ_courselist_handler(a, b, learnStatuses)
{
	//类型变量  //cb:xxx
	switch (b[1]) {
		case "islearn":
			var status = learnStatuses[a['id']];
			if (status == "finished") {
				return '<img src="images/isread.png" align="top" />';
			} 
			break;
		case "type":
			switch (a["type"]) {
				case "testpaper":
					return '<img src="images/book_label.png" align="top" />';
				case "video":
					return '<img src="images/video_label.png" align="top" />';
				case "text":
					return '<img src="images/pic_label.png" align="top" />';
			}
			return "";
	}
	return "";
}

});
