define(function(require, exports){

var courseinfo_text = new String(function(){
/*
<div title="课程详情" data-load="courseinfo_model.load_data" id="courseinfo" class="panel" data-header='info_header' data-footer="none" style="padding:0px;">
	<!-- templ input list模板 -->
	<textarea id="courseinfo_cb_course_list" style="display:none;">
		<tr class="course_lesson_table ${cb:lesson_bg}" valign="middle" onclick="courseinfo_model.showCourseInfo('${type}','${courseId}','http://bcs.duapp.com/bimbucket/test.mp4','${id}');">
			<td align="left" valign="middle">
			<div>
				${cb:number}
				${cb:learnStatus}
				<span class="lesson_title">${cb:title}</span>
			</div>
			</td>
		</tr>
	</textarea>
	<textarea id="courseinfo_cb_course_comment" style="display:none;">
		<tr class="course_comment_table" id="${id}">
			<td style="width:60px;" valign="middle">
 <img class="small-avatar" src="${cb:avatar}" />
			</td>
			<td>
				<table style="width:100%;">
					<tr>
						<td>
							<span class="nickname">${cb:nickname}</span>
						</td>
						<td align="right">
							<span class="createdTime">${cb:createdTime}</span>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<span class="rating">${cb:rating}</span>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<span class="content">${content}</span>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</textarea>
	<textarea id="courseinfo_list_item" style="display:none;">
		<div id="course_header">
			<img style="width:100%;height:160px;" src="${cb:largePicture}"/>
			<input type="hidden" id="course_payment" value="alipay" />
			<input type="hidden" id="course_title" value="${course.title}" />
			<input type="hidden" id="course_courseId" value="${course.id}" />
			<input type="hidden" id="course_price" value="${course.price}" />
		</div>
		<!-- 课程tab -->
		<div style="width:100%;margin:0 auto;">
			<div class="button-grouped flex tabbed">
					<a class="button tab_radio" data-v="0" data-ignore-pressed="true" onclick="courseinfo_model.changeTab(this);">课时列表</a>
					<a class="button tab_radio pressed" data-v="1" data-ignore-pressed="true" onclick="courseinfo_model.changeTab(this);">课程介绍</a>
					<a class="button tab_radio" data-v="2" data-ignore-pressed="true" onclick="courseinfo_model.changeTab(this);">课程评价</a>
			</div>
			<!--评论 -->
			<div style="padding:0px;">
				<div id="courseInfoCarousel" style="display:block;height:auto;width:100%;">
				
				<div id="0" class="ui-body-d ui-content tab_content">
					<table style="width:100%;border-collapse:collapse;" border="0" cellpadding="0" cellspacing="0">
						${cb:items}
					</table>
				</div>
				<div id="1" class="ui-body-d ui-content tab_content">
					<div class="card course_card">
						<p class="card-head">	
							${course.title}
						</p>
						<table style="width:100%">
							<tbody>
								<tr style="height:30px;">
									<th style="float:left;">
										${cb:rating} &nbsp;&nbsp;<span class="rating_color">${cb:ratingValue}</span>
									</th>
									<th style="float:right;">
										
									</th>
								</tr>
								<tr style="height:30px;">
									<td style="float:left;">
										<p>
											教师:${cb:teacher}
											<span>
												学员数:${course.studentNum}
											</span>
										</p>
										
									</td>
									<td style="float:right;">
										
									</td>
								</tr>
								<tr style="height:30px;">
									<td style="float:left;">
										<p>
											费用:${cb:price}
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
					
					${cb:about}
					${cb:goals}
					${cb:audiences}
					
				</div>
				<div id="2" class="ui-body-d ui-content tab_content" style="padding:5px;">
					<table style="width:100%;border-collapse:collapse;" id="course_comment_table">
						${cb:reviews}
					</table>
					<div class="${cb:ishide}" style="text-align:center;padding-right:1px;margin-top:20px;">
						<span onclick="courseinfo_model.addComment();" class="button white custom_button_blue">评 论</span>
					</div>
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
				+ '<textarea id="commentContent" rows="4" placeholder="评价内容" class="pressed">{content}</textarea>'
				+ '<div class="formGroupHead quick_comment_wrap">快捷回复:</div>'
				+ '<input readonly size="4" type="text" rating="3" value="不错" class="quick_comment">'
				+ '<input readonly size="7" type="text" rating="4" value="感觉很好" class="quick_comment">'
				+ '<input readonly size="12" type="text" rating="5" value="课程内容很好" class="quick_comment">';

exports.isTeacher = false;
exports.loginUserReview = null
exports.courseId = 0;
exports.currentIndex = -1;

exports.quick_comment = function(input)
{
	var rating = $(input).attr("rating");
	var content = $(input).val();
	var courseId = $("#course_courseId").val();
	var token = appstore_model.getToken();
	simpleJsonP(
		schoolHost + "/courses/" + courseId + '/review_create?callback=?&token=' + token 
				+ "&rating=" + rating + "&content=" + content,
		function(data){
			if (data && !data.error) {
				if (! data.error) {
					courseinfo_model.getComments();
				} else {
					$("#afui").popup(data.message);
				}
			}
		}
	);
}

exports.changeTab = function(radio)
{
	index = $(radio).attr("data-v");
	exports.courseCarousel.onMoveIndex(index);
}

exports.addComment = function()
{
	var message = "";
	var CommentContent = courseinfo_model.loginUserReview ? courseinfo_model.loginUserReview.content : "";

	var rating = courseinfo_model.loginUserReview ? courseinfo_model.loginUserReview.rating  : 0;
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
        		schoolHost + "/courses/" + courseId + '/review_create?callback=?&token=' + token 
        				+ "&rating=" + rating + "&content=" + content,
        		function(data){
        			if (data) {
						if (! data.error) {
							courseinfo_model.getComments();
						} else {
							$("#afui").popup(data.message);
						}
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

exports.setComment = function(review)
{
	setReviewContent(review);
	$(".ui-content").find(".course_comment_table").each(function() {
		var id = $(this).attr("id");
		if (id == review.id) {
			$(this).find(".content").text(review.content);
			$(this).find(".rating").html(setRating(review.rating));
			return;
		}
	});
}

exports.getComments = function()
{
	var courseId = $("#course_courseId").val();
	var token = appstore_model.getToken();
	simpleJsonP(
		schoolHost + "/courses/" + courseId + '/reviews?callback=?&token=' + token,
		function(data){
			$(".ui-content").find(".no_course_content").hide();
			var course_comment_templ = $("#courseinfo_cb_course_comment").val();
			list_str = zy_tmpl(
				course_comment_templ, data.data, 
				zy_tmpl_count(data.data), 
				function(a,b) {
					return course_comment_handler(a, b);
				}
			);

			$(".ui-content").find("#course_comment_table").html(list_str);
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

exports.courseCarousel = null;
exports.isStudent = false;

exports.load_data = function()
{
	if (exports.firstStart) {
		return;
	}
	var course_id = exports.courseId;
	var token = appstore_model.getToken();
	simpleJsonP(
		schoolHost + "/courses/" + course_id + '?callback=?&token=' + token,
		function(data){
			if (data && data.error == "not_found") {
				$("#afui").popup(data.error.message);
				$.ui.goBack();
				return;
			}
			var list = new Array();
			list[0] = data;
			list_str = zy_tmpl(
						$("#courseinfo_list_item").val(), 
						list, 
						zy_tmpl_count(list),function(a, b) {
							switch (b[1]) {
								case "largePicture":
									if (a.course.largePicture == null
										|| a.course.largePicture == "") {
										return "images/img1.jpg";
									}
									return a.course.largePicture;
								case "location":
									return schoolName;
								case "teacher":
									var teacher = a.course.teachers[0];
									return teacher.nickname;
							}
							return templ_courseinfo_handler(a, b);
						});

				if (data.userFavorited == true) {
					$("#favorite_radio").attr("checked", "checked");
				} else {
					$("#favorite_radio").removeAttr("checked");
				}
				$("#course_content").html(list_str);
				$("#favorite_btn").attr("courseId", course_id);

				$("#course_content").find(".");
				exports.courseCarousel=$("#courseInfoCarousel").carousel({
					pagingDiv: "none",
					pagingCssName: "carousel_paging2",
					pagingCssNameSelected: "carousel_paging2_selected",
					preventDefaults:false,
					wrap:true
				});

				$.bind(exports.courseCarousel, 'movestop' , function(carousel){
					exports.currentIndex = carousel.carouselIndex;
					setRadioStatus(carousel.carouselIndex);
				});
				var moveIndex = exports.currentIndex == -1 ? 1 : exports.currentIndex;
				exports.courseCarousel.onMoveIndex(moveIndex);
				setRadioStatus(moveIndex);
		}
	);
}

function setRadioStatus(carouselIndex)
{
	$(".tab_radio").each(function(e){
		index = $(this).attr("data-v");
		if (index == carouselIndex) {
			$(this).addClass("pressed");
			return;
		}
		$(this).removeClass("pressed");
	});
}

exports.firstStart = true;

exports.init_data = function(course_id)
{
	exports.currentIndex = -1;
	exports.courseId = course_id;
	exports.firstStart = false;
}

exports.init_courseinfo_data = function(course_id)
{
	exports.init_data(course_id);
	$.ui.loadContent('courseinfo',false,false,'pop');
	exports.load_data(course_id);
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

//显示课程详情
exports.showCourseInfo = function(type, id, mediaUri, lesson_id)
{
	if (!exports.isStudent && !exports.isTeacher ) {
		$("#afui").popup("请加入学习");
		return;
	}
	load_course_lesson_page(id, lesson_id);
	return;
	switch (type) {
		case "video":
			nativePlay(mediaUri, id, lesson_id);
			break;
		case "text":
			load_course_lesson_page(id, lesson_id);
			break;
		case "testpaper":
			//show paper
			break;	
	}
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

function setRating(rating)
{
	var star_templ = '<i class="rating_color fa fa-star"></i>';
	var star_off_tmpl = '<i class="rating_color fa fa-star-o"></i>';
	var num = parseInt(rating);
	if (!num) {
		num = 0;
	}
	var offnum = 5 - num;
	var stars = stringTimes(star_templ, num);
	var star_offs = stringTimes(star_off_tmpl, offnum);
	return stars + star_offs;
}

//模板回调函数
function templ_courseinfo_handler(a, b)
{
	var result = "";
	var star_templ = '<i class="rating_color fa fa-star"></i>';
	var star_off_tmpl = '<i class="rating_color fa fa-star-o"></i>';
	var courseinfo_templ = $("#courseinfo_cb_course_list").val();
	//类型变量  //cb:xxx
	switch (b[1]) {
		case "ishide":
			return a.userIsStudent ? "" : "hide";
		case "reviews":
			var course_comment_templ = $("#courseinfo_cb_course_comment").val();
			var users = a['users'];
			if (a.reviews && a.reviews.length <= 0) {
				return "<div class='no_course_content' style='text-align:center;'>暂无课程评价</div>";
			}
			return zy_tmpl(
					course_comment_templ, 
					a.reviews, 
					zy_tmpl_count(a.reviews), 
					function(a, b) {
						return course_comment_handler(a, b, users);
					}
				);

		case "items":
			var userLearns = a.userLearns;
			if (a.items && a.items.length <= 0) {
				return "<div style='text-align:center;'>课程暂无课时内容</div>";
			}
			return zy_tmpl(
				courseinfo_templ, 
				a.items, 
				zy_tmpl_count(a.items),
				function(a, b) {
					return templ_courselist_handler(a, b, userLearns);
				}
			);

		case "ratingValue":
			var rating = parseFloat(a.course.rating);
			if (rating <= 0) {
				return 0;
			}
			fixedV1 = rating.toFixed(1);
			fixedV0 = rating.toFixed(0);
			if ((fixedV1 - fixedV0) == 0) {
				return fixedV0;
			}
			return fixedV1;

		case "rating":
			return setRating(a.course.rating);

		case "islearn":
			if (checkIsTeacher(a, window.loginUser)) {
				return "";
			}
			var t_btn = "";
			exports.isStudent = a.userIsStudent;
			if (a["member"] && exports.isStudent) {
				t_btn =  '<a onclick="courseinfo_model.refundDialog();" class="learn_btn">退出学习</a>';
			} else {
				var buyParames= "'alipay','" + a.course.id + "'";
				var price = parseFloat(a.course.price);
				if (price && price > 0) {
					t_btn = '<a onclick="courseinfo_model.buyDialog();" class="learn_btn">购买课程</a>';
				} else {
					t_btn = '<a onclick="courseinfo_model.buyDialog();" class="learn_btn">加入学习</a>';
				}
			}
			return t_btn;
		case "price":
			var price = parseFloat(a.course.price);
			return price == 0 ? "免费" :price + "元";

		case "about":
			var html = '<div class="card course_card"> <p class="card-head"> 课程介绍 </p>'
						+ '<p class="umh4"> {about} </p></div>';
			var about =a.course.about;
			if (about == "") {
				return "";
			}
			return html.replace("{about}", about);

		case "goals":
			var course_target = a.course.goals
			if (course_target && course_target.length <= 0) {
				return "";
			}
			var html = '<div class="card course_card"><p class="card-head">课程目标</p>'
						+ '<ul style="margin-left:30px;">{goals}</ul></div>';

			for (var i in course_target) {
				result += '<li><p>' + course_target[i] + '</p></li>';
			}
			return html.replace("{goals}", result);

		case "audiences":
			var course_notice = a.course.audiences;
			if (course_notice && course_notice.length <= 0) {
				return "";
			}
			var html = '<div class="card course_card"><p class="card-head">适合人群</p>'
						+ '<ul style="margin-left:30px;"> {audiences} </ul> </p> </div>';

			for (var i in course_notice) {
				result += '<li><p>' + course_notice[i] + '</p></li>';
			}
			return html.replace("{audiences}", result);
	}
	return '';
}

function checkIsTeacher(a, loginUser)
{
	if (! loginUser) {
		return false;
	} 
	var  teacherUsers = a.course.teachers;
	for (var i in teacherUsers) {
		var teacher = teacherUsers[i];
		if (teacher && teacher.nickname == loginUser.nickname) {
			exports.isTeacher = true;
			return true;
		}
	}
	return false;
}

function setReviewContent(review)
{
	var id = review.user.id;
	if(window.loginUser && loginUser.id == id) {
		courseinfo_model.loginUserReview = review;
	}
}

function course_comment_handler(a, b, users) 
{
	setReviewContent(a);
	switch (b[1]){
			case "createdTime":
				return a.createdTime.substring(0, 10);
			case "nickname":
				return a.user ? a.user.nickname : "" ;
			case "avatar":
				return a.user ? a.user.avatar : "" ;
			default:
				return templ_handler(a, b);
		}
}

//课时列表模板回调函数
function templ_courselist_handler(a, b, userLearns)
{
	//类型变量 //cb:xxx
	switch (b[1]) {
		case "learnStatus":
			if (a.type == "chapter" || a.type == "unit") {
				return "";
			}
			
			var status = userLearns[a.id];
			switch (status) {
				case "finished":
					return '<span class="learn-status learn-status-finished"><em></em></span>';
				case "learning":
					return '<span class="learn-status learn-status-learning"><em></em></span>';
				default:
					return '<span class="learn-status"><em></em></span>';
			}

		case "lesson_bg":
			if (a.type == "chapter" || a.type == "unit") {
				return "lesson_bordr";
			}
			return "lesson_bg";

		case "islearn":
			var status = learnStatuses[a['id']];
			if (status == "finished") {
				return '<img src="images/isread.png" align="top" />';
			} 
			break;
		case "number":
			if (a.type == "chapter" || a.type == "unit") {
				return "";
			}
			return "<span class='lesson_number'>" + a.number + "</span>";
		case "type":
			switch (a["type"]) {
				case "testpaper":
					return '<i class="fa lesson_item_fa fa-pencil-square course_lesson_type_normal_color"></i>';
				case "video":
					return '<i class="fa lesson_item_fa fa-play-circle course_lesson_type_normal_color"></i>';
				case "text":
					return '<i class="fa lesson_item_fa fa-picture-o course_lesson_type_normal_color"></i>';
				case "audio":
					return '<i class="fa lesson_item_fa fa-microphone course_lesson_type_normal_color"></i>';
				
			}
			return "";
		case "title":
			switch (a.type) {
				case "chapter":
					return "第" + a.number + "章&nbsp;&nbsp;" + a.title;
				case "unit":
					return "&nbsp;&nbsp;&nbsp;&nbsp;第" + a.number + "节&nbsp;&nbsp;" + a.title;
			}
			return a.title;

		case "type_text":
			switch (a["type"]) {
				case "testpaper":
					return '试卷';
				case "video":
					return '视频';
				case "text":
					return '图文';
				case "audio":
					return "音频";
			}
			return "";
	}
	return "";
}

});
