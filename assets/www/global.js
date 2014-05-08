//var webRoot = "http://www.edusoho-dev.com/mapi";
var webRoot = "http://192.168.12.17/mapi";
var defalut_avatar = "images/avatar.png";
var debug = true;
var appconfig = {};
var schoolHost = "";
var schoolName = "";
var testToken = "c17c0a8hgv4koscgssscc40k084skks";


var maskDiv = $.create("div", {
	    id: "afui_toast",
	    className: "ui-loader",
	    html: "<h1>Loading Content</h1>"
	}).css({
	    "z-index": 20000,
	    display: "none"
	});
document.body.appendChild(maskDiv.get(0));

var uiToastBlocked = false;
function toastblockUI (opacity) {
    if (uiToastBlocked)
        return;
    opacity = opacity ? " style='opacity:" + opacity + ";'" : "";
    $.query("BODY").prepend($("<div id='toastmask'" + opacity + "></div>"));
    $.query("BODY DIV#toastmask").bind("touchstart", function (e) {
        e.preventDefault();
    });
    $.query("BODY DIV#toastmask").bind("touchmove", function (e) {
        e.preventDefault();
    });
    uiToastBlocked = true;
};

function unToastblockUI() {
    uiToastBlocked = false;
    $.query("BODY DIV#toastmask").unbind("touchstart");
    $.query("BODY DIV#toastmask").unbind("touchmove");
    $("BODY DIV#toastmask").remove();
};

function showToast(text) {
    $.query("#afui_toast>h1").html(text);
    toastblockUI(0.5);
    $.query("#afui_toast").show();
    setTimeout(hideToast, 1000);
}

function hideToast()
{
	unToastblockUI();
    $.query("#afui_toast").hide();
}

function setSchoolHost(url, name)
{
	if (url.indexOf("http://") == -1) {
		url = "http://" + url ;
	}
	if (url[url.length - 1] == "/") {
		url = url.substring(0, url.length - 1);
	}
	schoolHost = url + "/mapi";
	schoolName = name;
}

function applog(str)
{
	if (debug) {
		console.log(str);
	}
}

function loadedPanel() {
	if (!window.appstore_model) {
		require(
			['appstore', 'init', 'model/courselist', 'model/splash'],
			function(appstore, init, courselist, splash){
				window.splash_model = splash;
				window.courselist_model = courselist;
				window.init_model = init;
				window.appstore_model = appstore;
				appstore_model.loadUserInfo();
				_load_carousel();
				var showSplash = appstore_model.getStoreCache("showSplash");
				if (showSplash != "false") {
					splash_model.load(function(){
						loadDefaultSchool();
					});
				} else {
					loadDefaultSchool();
				}
				appstore_model.loadSchoolList(true);
			}
		);
	} else {
		appstore_model.loadSchoolList(true);
	}
	appconfig.page = "main";
}

function loadDefaultSchool()
{
	var startWithSchool = appstore_model.getStoreCache("startWithSchool");
	if (startWithSchool == "true") {
		defaultSchool = appstore_model.getStoreCache("defaultSchool");
		defaultSchoolName = appstore_model.getStoreCache("defaultSchoolName");
		setSchoolHost(defaultSchool, defaultSchoolName);
		load_courselist_page();
	} else {
		$.ui.loadContent('main',false,false,'slide');
	}
}

function _load_carousel()
{
	$.jsonP(
	{
		url: webRoot + "/carousel" + '?callback=?',
		success:function(data){
			appstore_model.setStoreCache("carousel", _carousel2str(data));
			_init_carousel();
		},
		timeout:"5000",
		error: function(){
			$("#afui").popup("网络异常！请重新尝试");
		}
	});
}

function _carousel2str(data)
{
	carousel_json_template = '{"image":"{image}", "action" : "{action}", "title" : "{title}"}';
	temp_str = "[";
	for (var i in data) {
		temp_str += carousel_json_template.replace("{image}", data[i].image)
					.replace("{action}", data[i].action)
					.replace("{title}", data[i].title);
		if (i < data.length -1 ) {
			temp_str += ",";
		} 
	}
	temp_str += "]";
	return temp_str;
}

function _init_carousel() {
	if (window.appstore_model && appstore_model.hasStoreCache("carousel")) {
		var carousel_div = '<div id="carousel" style="display:block;height:180px;width:100%;">{carousel}</div> <div id="carousel_dots"></div>';
		$("#carouselDiv").empty();
		//$("#carousel_dots").innerHtml = "";
		var cache = appstore_model.getStoreCache("carousel");
		var carousel_template = '<div style="background: url(\'{image}\');">{title}</div>';
		var cacheData = $.parseJSON(cache);
		var carousel_str = "";
		for (var i in cacheData) {
			carousel_str += carousel_template.replace("{image}", cacheData[i].image)
							.replace("{title}", cacheData[i].title);
		}
		new_div = $.create(carousel_div.replace("{carousel}", carousel_str));
		$("#carouselDiv").append(new_div);
	} 
	carousel=$("#carousel").carousel({
		pagingDiv: "carousel_dots",
		pagingCssName: "carousel_paging2",
		pagingCssNameSelected: "carousel_paging2_selected",
		preventDefaults:false,
		wrap:true //Set to false to disable the wrap around
	});
}

function simpleJsonP(url_path, success_func)
{
	$.ui.showMask('加载中...');
	$.jsonP(
	{
		url: url_path,
		success:function(data){
			success_func(data);
			$.ui.hideMask();
		},
		timeout:"5000",
		error: function(){
			$.ui.hideMask();
			$("#afui").popup("网络异常！请重新尝试");
		}
	});
}

//check radio and show tab_content
function changeTab(radio){
	$(".tab_radio").each(function(e){
		$(".tab_content").hide();
	});
	$("#" + $(radio).attr("data-v")).show();
}


//load_learn_page
function load_learn_page()
{
	if (appstore_model.checkIsLogin()) {
		$.ui.loadContent('learning',false,false,'slide');
		window.learning_model.init_learn_data();
	} else {
		setHistoryAction(window.load_learn_page);
		$.ui.loadContent('login',false,false,'slide');
	}
}

//load_learned_page
function load_learned_page()
{
	if (appstore_model.checkIsLogin()) {
		$.ui.loadContent('learned',false,false,'slide');
		window.learned_model.init_learned_data();
	} else {
		setHistoryAction(window.load_learned_page);
		$.ui.loadContent('login',false,false,'slide');
	}
}

function load_notification_page()
{
	if (appstore_model.checkIsLogin()) {
		appconfig.page = "notification";
		$.ui.loadContent('notification',false,false,'slide');
		notification_model.init_notification_data();
	} else {
		setHistoryAction(window.load_notification_page);
		appconfig.page = "login";
		$.ui.loadContent('login',false,false,'slide');
	}
}

function load_favorite_page()
{
	if (appstore_model.checkIsLogin()) {
		appconfig.page = "favorite";
		$.ui.loadContent('favorite',false,false,'slide');
		favorite_model.init_favorite_data();
	} else {
		setHistoryAction(window.load_favorite_page);
		appconfig.page = "login";
		$.ui.loadContent('login',false,false,'slide');
	}
}

function load_courselist_page()
{
	appconfig.page = "courselist";
	$.ui.loadContent('courselist',false,false,'slide');
	courselist_model.init_courselist_data();
}

function load_about_page()
{
	appconfig.page = "about";
	$.ui.loadContent('about',false,false,'slide');
	about_model.init_about_data();
}

function load_search_page()
{
	appconfig.page = "search";
	$.ui.loadContent('searchlist',false,false,'slide');
	searchlist_model.clear();
}

function load_courseinfo_page(course_id)
{
	$.ui.loadContent('courseinfo',false,false,'pop');
	courseinfo_model.init_courseinfo_data(course_id);
}

function load_schoollist_page()
{
	appconfig.page = "addschool";
	$.ui.loadContent('addschool',false,false,'pop');

	schoolpage_model.init_schoollist_data();
}

function load_recommend_page()
{
	appconfig.page = "recommend";
	$.ui.loadContent('recommend',false,false,'pop');
	if (appstore_model.hasCache("recommend")) return;
	recommend_model.init_recommend_data();
}

function load_course_lesson_page(id, lesson_id)
{
	appconfig.page = "course_lesson_list";
	$.ui.loadContent('course_lesson_list',false,false,'pop');
	course_lesson_list_model.init_course_lesson_data(id, lesson_id);
}

function load_setting_page()
{
	appconfig.page = "setting";
	$.ui.loadContent('setting',false,false,'pop');
	setting_model.init_setting_data();
}

function load_main_page()
{
	$.ui.updateNavbarElements("footerui");
	$(".school_delbtn").hide();
	$(".school_div").bind("tap",function(){
		if (window.isClick) {
			isClick = false;
    		load_courselist_page();
    		clearTapStatus();
    	}
	});
}

function toggleTextCourseMenu()
{
	//$("#text_course_menu").toggle();
	$.ui.toggleAsideMenu();
}

function showCourseInfo(type, id, mediaUri, lesson_id)
{
	switch (type) {
		case "video":
			nativePlay(mediaUri, id, lesson_id);
			break;
		case "text":
			load_course_lesson_page(id, lesson_id);
			break;
		case "testpaper":
			
			break;	
	}
}

function initScroll(element)
{
	if ($.os.ios) {
		return;
	}
	scroller = $("#" + element).scroller(); //Fetch the scroller from cache
	scroller.ispull = true;
	scroller.refreshContent = "";
	scroller.refreshingStr = "";
	scroller.addPullToRefresh();
	scroller.runCB=true;
	scroller.enable();
	$("#" + element).css("overflow", "auto");
}

function clearTapStatus()
{
	setTimeout(function(){
		window.isClick = true;
	}, 500);
}

//模板回调函数
function templ_handler(a, b)
{
	var result = "";
	var star_templ = '<span class="stars-{num}"></span>';
	//类型变量  //cb:xxx
	switch (b[1]) {
		case "rating":
			var num = parseInt(a["rating"]);
			if (!num) {
				num = 1;
			}
			return star_templ.replace("{num}", num);
		case "price":
			var price = parseInt(a["price"]);
			return price == 0 ? "免费" :price + "元" ;
	}
	return "";
}

function logout()
{
	$.ui.showMask('连接服务器...	');
	var token = appstore_model.getToken();
	$.jsonP(
	{
		url:webRoot + "/logout" + '?callback=?&token=' + token,
		success:function(data){
			appstore_model.clearUserInfo();
			load_setting_page();
			$.ui.hideMask();
		}
	});
}

function regist()
{
	email = $("#loginEmailInput").val();
	name = $("#loginNameInput").val();
	pass = $("#loginPassInput").val();

	if (email.length < 4 || name.length < 4 || pass.length < 4) {
		$("#afui").popup("账号，密码或者昵称长度不正确!");
		return;
	}
	
	simpleJsonP(
		webRoot + "/regist" + '?callback=?&email=' + email + "&nickname=" + name + "&password=" + pass,
		function(data){
			if (data.status == "success") {
				$("#afui").popup(
					{
						title:"注册成功",
						message: "注册成功！同时生成了第二课堂的账号，您可以用它来登录其他网校.<br> 邮箱：" + email + "<br> 账号：" + name,
        				cancelText: "确定",
						doneText: "返回",
						cancelCallback: function () {
				        	appstore_model.saveUserInfo(name, data.token);
				        	load_setting_page();
				        },
				        doneCallback: function () {
				        	appstore_model.saveUserInfo(name, data.token);
				        	load_setting_page();
				        }
					}
				);
			} else {
				$("#afui").popup(data.message ? data.message : "注册失败!");
			}
			$.ui.hideMask();
		}
	);
}

function login()
{
	account = $("#accountInput").val();
	password = $("#passwordInput").val();
	if (account.length < 4 || password.length < 4) {
		$("#afui").popup("账号或密码长度不正确!");
		return;
	}

	simpleJsonP(
		webRoot + "/login" + '?callback=?&_username=' + account + "&_password=" + password,
		function(data){
			if (data && data.status == "success") {
				appstore_model.saveUserInfo(account, data.token);
				if (window.historyAction) {
					window.historyAction(window.historyActionParams);
					clearHistoryAction();
				} else {
					load_setting_page();
				}
			} else {
				$("#afui").popup("账号或密码错误!");
			}
		}
	);
}

function nativePlay(url, course_id, lesson_id)
{
	if ($.os.ios) {
		cordova.exec(
			function(array) {
				totalTime = array[1];
				currentTime = array[0];
				if ( (totalTime - currentTime) <= 5000) {
					var token = appstore_model.getToken();
					simpleJsonP(
						schoolHost + "/lessonfinish/" + course_id + "/" + lesson_id + '?callback=?&token=' + token,
						function(data){
							if (data.status == "success") {
								applog("已学");
							}
						}
					);
				}
			},
			function(error) {
				alert(error);
			},
                 "VideoPlugin",
                 "playvideo",
                 [url]);
		/*
		var playvideo = document.getElementById("playvideo");
	    playvideo.src = "http://hlstest.qiniudn.com/dahuangya.mp4";
	    playvideo.load();
	    playvideo.play();
 	   return;
 	   */
 	   return;
	}
	VideoPlayer.playVideo( nativePluginResultHandler, nativePluginErrorHandler, url); 
}

function setHistoryAction(func, params)
{
	window.historyAction = func;
	window.historyActionParams = params ? params : undefined;
}

function clearHistoryAction()
{
	window.historyAction = undefined;
	window.historyActionParams = undefined;
}

function clearCache()
{
	$.ui.showMask('清除缓存中...	');
	setTimeout(function(){
		appstore_model.destoryCache();
		appstore_model.delStore("showSplash");
		$.ui.hideMask();
	}, 1000);
}

function exitSchool(school_name, event)
{
	 $("#afui").popup({
        title: "退出网校",
        message: "确定要退出 " + school_name + " 吗？",
        cancelText: "取消",
        cancelCallback: function () {
            console.log("cancelled");
        },
        doneText: "确定",
        doneCallback: function () {
        	if (appstore_model.delSchool(school_name)) {
        		appstore_model.loadSchoolList(false);
        	}
            console.log("Done for!");
        },
        cancelOnly: false
    });
	//阻止事件冒泡
	event.stopPropagation();
}

function favorite(favorite_btn)
{
	if (appstore_model.checkIsLogin()) {
		favorite_model.favorite($("#favorite_btn").attr("courseId"));
	} else {
		setHistoryAction(window.load_courseinfo_page, $("#favorite_btn").attr("courseId"));
		$.ui.loadContent('login',false,false,'slide');
	}
}

function refundCourse(course_id, reason)
{
	var token = appstore_model.getToken();
	if (token) {
		simpleJsonP(
			schoolHost + "/refundcourse/" + course_id + '?callback=?&token=' + token +"&reason=" + reason,
				function(data){
					if (data.status == "success") {
						load_courseinfo_page(course_id);
					} else {
						$("#afui").popup(data.message ? data.message : "退出学习失败！");
					}
				}
		);
	} else {
		setHistoryAction(window.load_courseinfo_page, course_id);
		$.ui.loadContent('login',false,false,'slide');
	}
}

function buyCourse(payment, course_id)
{
	var token = appstore_model.getToken();
	if (token) {
		simpleJsonP(
			schoolHost + "/paycourse" + '?payment=alipay&callback=?&token=' + token + "&courseId=" + course_id,
				function(data){
					if (data.status == "success") {
						load_courseinfo_page(course_id);
					} else {
						$("#afui").popup(data.message ? data.message : "加入学习失败！");
					}
				}
		);
	} else {
		setHistoryAction(window.load_courseinfo_page, course_id);
		$.ui.loadContent('login',false,false,'slide');
	}
}

function shard()
{
	$("#afui").popup("分享");
}

function searchCourse()
{
	load_search_page();
}

//$.ui.customClickHandler = function(theTag, evt){}