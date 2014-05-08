require.config({
	baseUrl: "model",
	paths: {
		'about': "about",
		'courseinfo': "courseinfo",
		'learning': "learning",
		'learned': "learned",
		'schoolpage': "schoolpage",
		'course_lesson_list': "course_lesson_list",
		'favorite': "favorite",
		'notification' : 'notification',
		'recommend' : 'recommend',
		'setting' : 'setting',
		'searchlist' : 'searchlist',
	}
});

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
		['../object_plugins/jsobject'],
		function(jsobject){
			window.jsobject = true;
		}
);