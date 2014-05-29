define(function(require, exports){

	window.webRoot = "http://try3.edusoho.cn/mapi";
	window.defalut_avatar = "images/avatar.png";
	window.debug = true;
	window.appconfig = {};
	window.schoolHost = "";
	window.schoolName = "";
	window.normalLimit = 10;
	window.testToken = "15isuvjja7s0k4k4gk08w4ggogo880g";

	exports.init = function()
	{
		var maskDiv = $.create("div", {
		    id: "afui_toast",
		    className: "ui-loader",
		    html: "<h1>Loading Content</h1>"
		}).css({
		    "z-index": 20000,
		    display: "none"
		});
		document.body.appendChild(maskDiv.get(0));

		require(
            ['../appstore', '../init', 'courselist', 'splash'],
            function(appstore, init, courselist, splash){
                window.splash_model = splash;
                window.courselist_model = courselist;
                window.init_model = init;
                window.appstore_model = appstore;

                appstore_model.loadUserInfo();
                //_load_carousel();
                loadDefaultSchool();
                //appstore_model.loadSchoolList(true);
            }
        );

		require(
				['courseinfo', 'learning', 'schoolpage', 'course_lesson_list', 'searchlist'],
				function(courseinfo, learning, schoolpage, course_lesson_list, searchlist){
					window.course_lesson_list_model = course_lesson_list;
					window.schoolpage_model = schoolpage;
					window.learning_model = learning;
					window.courseinfo_model = courseinfo;
					window.searchlist_model = searchlist;
				}
		);

		require(
				['about', 'favorite', 'notification', 'recommend', 'setting', 'learned'],
				function(about, favorite, notification, recommend, setting, learned){
					window.setting_model = setting;
					window.recommend_model = recommend;
					window.notification_model = notification;
					window.favorite_model = favorite;
					window.about_model = about;
					window.learned_model = learned;
				}
		);

		require(
				['../plugins/audio'],
				function(audio){
					window.audio_model = audio;
				}
		);
	}

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

	window.playIframeVideo = function(mediaUri)
	{
		if ($.ui.android) {
			alert(2);
			cordova.exec(
				function(result) {
					
				},
				function(error) {
					$("#afui").popup("播放错误: " + error);
				},
		         "IFramePlayerPlugin",
		         "playVideo",
		         [mediaUri]);
		}
	}

	window.showToast = function(text) {
	    $.query("#afui_toast>h1").html(text);
	    toastblockUI(0.5);
	    $.query("#afui_toast").show();
	    setTimeout(hideToast, 1000);
	}

	window.goback = function()
	{
		$.ui.goBack();
	}

	window.clearHistory = function(name)
	{
		var history = $.ui.history;
		for (var i in history) {
			var target = history[i].target;
			if (target == ("#" + name)) {
				$.ui.history.splice(i, 1);
				break;
			}
		}
	}

	window.hideToast = function()
	{
		unToastblockUI();
	    $.query("#afui_toast").hide();
	}

	window.setSchoolHost = function(url, name)
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

	window.applog = function(str)
	{
		if (debug) {
			console.log(str);
		}
	}

	window.loadDefaultSchool = function()
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

	window.setTitle = function(title)
	{
		$("#header").find("#pageTitle").text(title);
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
			wrap:true
		});
	}

	window.simpleJsonP = function(url_path, success_func, showLoading)
	{
		if (!showLoading) {
			$.ui.showMask('加载中...');
		}
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
	window.load_learn_page = function()
	{
		if (appstore_model.checkIsLogin()) {
			$.ui.loadContent('learning',false,false,'slide');
			learning_model.init_learn_data();
		} else {
			setHistoryAction(window.load_learn_page);
			$.ui.loadContent('login',false,false,'slide');
		}
		setTitle(learning_model.title);
	}

	//load_learned_page
	window.load_learned_page = function()
	{
		if (appstore_model.checkIsLogin()) {
			$.ui.loadContent('learned',false,false,'slide');
			appconfig.page = "learned";
			learned_model.init_learned_data();
		} else {
			setHistoryAction(window.load_learned_page);
			$.ui.loadContent('login',false,false,'slide');
		}
	}

	window.load_regist_page = function()
	{
		$.ui.loadContent('regist',false,false,'slide');
		appconfig.page = "regist";
		setTitle("注册网校");
	}

	window.load_notification_page = function()
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
		setTitle(notification_model.title);
	}

	window.load_favorite_page = function()
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

		setTitle(favorite_model.title);
	}

	window.load_courselist_page = function()
	{
		appconfig.page = "courselist";
		$.ui.loadContent('courselist',false,false,'slide');
		courselist_model.init_courselist_data();
		$.ui.clearHistory();
	}

	window.load_about_page = function()
	{
		appconfig.page = "about";
		$.ui.loadContent('about',false,false,'slide');
		about_model.init_about_data();
	}

	window.load_mian = function()
	{
		appconfig.page = "main";
		$.ui.loadContent('main',false,false,'slide');
	}

	window.load_search_page = function()
	{
		appconfig.page = "search";
		$.ui.loadContent('searchlist',false,false,'slide');
		searchlist_model.clear();
	}

	window.load_courseinfo_page = function(course_id)
	{
		appconfig.page = "courseinfo";
		courseinfo_model.init_courseinfo_data(course_id);
	}

	window.load_schoollist_page = function()
	{
		appconfig.page = "addschool";
		$.ui.loadContent('addschool',false,false,'pop');
		setTitle(schoolpage_model.title);
	}

	window.load_recommend_page = function()
	{
		appconfig.page = "recommend";
		$.ui.loadContent('recommend',false,false,'pop');
		if (appstore_model.hasCache("recommend")) return;
		recommend_model.init_recommend_data();
	}

	window.load_course_lesson_page = function(id, lesson_id)
	{
		appconfig.page = "course_lesson_list";
		$.ui.loadContent('course_lesson_list',false,false,'pop');
		course_lesson_list_model.init_course_lesson_data(id, lesson_id);
	}

	window.load_setting_page = function()
	{
		appconfig.page = "setting";
		$.ui.loadContent('setting',false,false,'pop');
		setting_model.init_setting_data();
	}

	window.load_main_page = function()
	{
		$.ui.toggleNavMenu();
		$(".school_delbtn").hide();
		$(".school_div").bind("tap",function(){
			if (window.isClick) {
				isClick = false;
	    		load_courselist_page();
	    		clearTapStatus();
	    	}
		});
	}

	window.toggleTextCourseMenu = function()
	{
		//$("#text_course_menu").toggle();
		$.ui.toggleAsideMenu();
	}

	window.initScroll = function(element, callback)
	{
		scroller = $("#" + element).scroller(); //Fetch the scroller from cache
		if (!$.os.ios) {
			scroller.ispull = true;
			scroller.refreshContent = "";
			scroller.refreshingStr = "";
			scroller.addPullToRefresh();
		}

		scroller.runCB=true;
		scroller.addInfinite();
		
	    scroller.enable();
		$.bind(scroller, "infinite-scroll", function () {
	        var self = this;
	        console.log("infinite triggered");
	        if (callback) {
	        	callback.scrollerCallback(self);
	        }
	    });
		$("#" + element).css("overflow", "auto");
		if (callback) {
	    	callback.init(scroller);
	    }
	}

	window.clearTapStatus = function()
	{
		setTimeout(function(){
			window.isClick = true;
		}, 500);
	}

	window.stringTimes = function(str, n)
	{
		return Array.prototype.join.call({length:n+1}, str);
	}

	//模板回调函数
	window.templ_handler = function(a, b)
	{
		var result = "";
		var star_templ = '<i class="rating_color fa fa-star"></i>';
		var star_off_tmpl = '<i class="rating_color fa fa-star-o"></i>';
		//类型变量  //cb:xxx
		switch (b[1]) {
			case "rating":
				var num = parseInt(a["rating"]);
				if (!num) {
					num = 0;
				}
				var offnum = 5 - num;
				var stars = stringTimes(star_templ, num);
				var star_offs = stringTimes(star_off_tmpl, offnum);
				return stars + star_offs;
			case "price":
				var price = parseFloat(a["price"]);
				return price == 0 ? "免费" :price + "元" ;
		}
		return "";
	}

	window.logout = function()
	{
		$.ui.showMask('连接服务器...	');
		var token = appstore_model.getToken();
		$.jsonP(
		{
			url:webRoot + "/logout" + '?callback=?&token=' + token,
			success:function(data){
				if (data) {
					appstore_model.clearUserInfo();
					load_setting_page();
				}
				$.ui.hideMask();
			}
		});
	}

	window.regist = function()
	{
		email = $("#loginEmailInput").val();
		name = $("#loginNameInput").val();
		pass = $("#loginPassInput").val();

		if (email.length < 4 || name.length < 4 || pass.length < 4) {
			$("#afui").popup("账号，密码或者昵称长度不正确!");
			return;
		}
		
		simpleJsonP(
			webRoot + "/user_register" + '?callback=?&email=' + email + "&nickname=" + name + "&password=" + pass,
			function(data){
				if (data && data.error) {
					$("#afui").popup(data.error.message);
					return;
				}
				$("#afui").popup(
				{
					title:"注册成功",
					message: "注册成功！同时生成了第二课堂的账号，您可以用它来登录其他网校.<br> 邮箱：" + email + "<br> 账号：" + name,
    				cancelText: "确定",
					doneText: "返回",
					cancelCallback: function () {
			        	appstore_model.saveUserInfo(data.user, data.token);
			        	load_setting_page();
			        },
			        doneCallback: function () {
			        	appstore_model.saveUserInfo(data.user, data.token);
			        	load_setting_page();
			        }
				});
			}
		);
	}

	window.login = function()
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
				if (data.error) {
					$("#afui").popup(data.error.message);
					return;
				}
				window.loginUser = data.user;
				appstore_model.saveUserInfo(data.user, data.token);
				if (window.historyAction) {
					window.historyAction(window.historyActionParams);
					clearHistoryAction();
					clearHistory("login");
				} else {
					load_setting_page();
				}
			}
		);
	}

	function androidQrSearch(callback, successCallback)
	{
		cordova.exec(
				function(result) {
					callback();
                	if (result.replace(/(^\s*)|(\s*$)/g,"") == ""){
                		return;
                	}
                	var isStart = false;
                	var isCancel = false;

                	var pop = $("#afui").popup({
				        title: "扫描结果",
				        message: "正在进入网校...<img src='images/sch_load.gif' >",
				        cancelText: "取消",
				        cancelCallback: function () {
				        	isCancel = true;
				        	pop.hide();
				        	callback("");
				            applog("qr search cancelled");
				        },
				        cancelOnly: true
				    });
				    setTimeout(function(){
						if (isStart || isCancel) {
							return;
						}
				    	successCallback(result);
				    	pop.hide();
			    	},3000);
				},
				function(error) {
					$("#afui").popup("扫描错误: " + error);
				},
		         "QrPlugin",
                     "qrsearch",
		         [""]);
	}

	//扫描二维码
	window.nativeSearch = function(callback, successCallback)
	{
		if ($.os.ios) {
			var scanner = window.cordova.require("native_plugins/BarcodeScanner");
		        scanner.scan(
		                function (result) {
		                	callback();
		                	if (result.text.replace(/(^\s*)|(\s*$)/g,"") == ""){
		                		return;
		                	}
		                	var isStart = false;
		                	var isCancel = false;

		                	var pop = $("#afui").popup({
						        title: "扫描结果",
						        message: "正在进入网校...<img src='images/sch_load.gif' >",
						        cancelText: "取消",
						        cancelCallback: function () {
						        	isCancel = true;
						        	pop.hide();
						            applog("qr search cancelled");
						        },
						        cancelOnly: true
						    });
						    setTimeout(function(){
								if (isStart || isCancel) {
									return;
								}
						    	successCallback(result.text);
						    	pop.hide();
					    	},3000);
		                },
		                function (error) {
		                	$("#afui").popup("扫描错误: " + error);
		                }
		    );
		} else {
			androidQrSearch(callback, successCallback);
		}

	}

	window.nativePlay =function(url, course_id, lesson_id)
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
	 	   return;
		}
		VideoPlayer.playVideo( nativePluginResultHandler, nativePluginErrorHandler, url); 
	}

	window.setHistoryAction =function(func, params)
	{
		window.historyAction = func;
		window.historyActionParams = params ? params : undefined;
	}

	window.clearHistoryAction = function()
	{
		window.historyAction = undefined;
		window.historyActionParams = undefined;
	}

	window.clearCache = function()
	{
		$.ui.showMask('清除缓存中...	');
		setTimeout(function(){
			appstore_model.destoryCache();
			appstore_model.delStore("showSplash");
			$.ui.hideMask();
		}, 1000);
	}

	window.exitSchool = function(school_name, event)
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

	window.favorite = function(favorite_btn)
	{
		if (appstore_model.checkIsLogin()) {
			var ischeck = $("#favorite_radio").attr("checked");
			if (ischeck == "checked") {
				favorite_model.unFavorite($("#favorite_btn").attr("courseId"));
			} else {
				favorite_model.favorite($("#favorite_btn").attr("courseId"));
			}
			
		} else {
			setHistoryAction(window.load_courseinfo_page, $("#favorite_btn").attr("courseId"));
			$.ui.loadContent('login',false,false,'slide');
		}
	}

	window.refundCourse = function(course_id, reason)
	{
		var token = appstore_model.getToken();
		if (token) {
			simpleJsonP(
				schoolHost + "/courses/" + course_id + "/refund" + '?callback=?&token=' + token +"&reason=" + reason,
					function(data){
						if (data.error) {
							$("#afui").popup(data.error);
							$.ui.goBack();
							return;
						}
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

	window.load_pay_page = function(payurl, course_id)
	{
		if ($.os.ios || $.os.android) {
			cordova.exec(
				function(result) {
					$("#afui").popup({
				        title: "支付结果",
				        message: "确认支付结果？",
				        cancelText: "取消",
				        cancelCallback: function () {
				            console.log("cancelled");
				            load_courseinfo_page(course_id);
				        },
				        doneText: "确定",
				        doneCallback: function () {
				        	load_courseinfo_page(course_id);
				        },
				        cancelOnly: false
				    });
				},
				function(error) {
					alert("支付失败");
				},
	             "AlipayPlugin",
	             "showPay",
	             [payurl]
	        );
		}
	}

	window.buyCourse = function(payment, course_id)
	{
		var token = appstore_model.getToken();
		if (token) {
			simpleJsonP(
				schoolHost + "/courses/" + course_id + "/pay" + '?payment=alipay&callback=?&token=' + token,
					function(data){
						if (data.status == "ok") {
							if (data.paid == true) {
								load_courseinfo_page(course_id);
							} else {
								load_pay_page(data.payUrl, course_id);
							}
						}
					}
			);
		} else {
			setHistoryAction(window.load_courseinfo_page, course_id);
			$.ui.loadContent('login',false,false,'slide');
		}
	}

	window.shard = function()
	{
		$("#afui").popup("分享");
	}

	window.searchCourse = function()
	{
		load_search_page();
	}

});