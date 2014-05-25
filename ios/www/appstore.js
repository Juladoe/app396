define(function(require, exports){

	var appStore = window.localStorage;
	var appCache = window.sessionStorage;

	exports.setCache = function(name, value)
	{
		appCache.setItem(name,value);
	}

	exports.getToken = function()
	{
		return _getStoreItem("token", "");
	}

	exports.delStore = function(name)
	{
		appStore.removeItem(name);
	}

	exports.setStoreCache = function(name, value)
	{
		appStore.setItem(name,value);
	}

	exports.hasCache = function(name) 
	{
		return appCache.getItem(name) ? true: false;
	}

	exports.hasStoreCache = function(name) 
	{
		return appStore.getItem(name) ? true: false;
	}

	exports.getStoreCache = function(name)
	{
		return _getStoreItem(name, "");
	}

	function _getStoreItem(name, default_value)
	{
		temp = appStore.getItem(name);
		if (!temp & default_value != undefined) {
			return default_value;
		}
		return temp;
	}

	exports.saveUserInfo = function(user,token)
	{
		window.loginUser = user;
		appStore.setItem("username", user.nickname);
		appStore.setItem("usertitle", user.title);
		appStore.setItem("userid", user.id);
		appStore.setItem("userAvatar", user.mediumAvatar);
		appStore.setItem("token", token);
	}

	exports.clearUserInfo = function()
	{
		window.loginUser = null;
		appStore.removeItem("username");
		appStore.removeItem("usertitle");
		appStore.removeItem("userid");
		appStore.removeItem("userAvatar");
		appStore.removeItem("token");
	}

	exports.loadUserInfo = function()
	{
		username = appStore.getItem("username");
		var token = _getStoreItem("token", "");
		setTimeout(function(){
			$.jsonP(
			{
				url:webRoot + "/login_with_token" + '?callback=?&token=' + token,
				success:function(data){
					if (data.error) {
						//token异常
						appStore.removeItem("token");
						return;
					}
					appstore_model.saveUserInfo(data.user, data.token);
					window.loginUserName = username;
					window.loginUser = data.user;
				}
			});
		},10);
	}

	exports.destoryCache = function()
	{
		appCache.clear();
	}

	exports.checkIsLogin = function()
	{
		return appStore.getItem("token") ? true: false;
	}

	exports.loadSchoolList = function(ishide)
	{
		var tmpl_text = $("#school_templ_text").val();
		var school_list_str = appStore.getItem("school_list");
		var grid_content = "";
		var school_list = "";

		//var addschool_td = "<td><a class='icon add Big' onclick='load_schoollist_page();'>选择添加网校</a></td>";
		$("#grid").empty();
		if (school_list_str) {
			var t_td_1, t_td_2;
			var school_list = $.parseJSON(school_list_str);
			school_list.splice(0, 1, school_list[0], {"name" : "选择添加网校", "cover" : "", "url" : "add"});
		} else {
			school_list = new Array({"name" : "选择添加网校", "cover" : "", "url" : "add"});
		}
		
		for (var i=0; i < school_list.length; i = i+2) {
			t_td_1 = _replaceSchoolItem(school_list[i], tmpl_text, ishide);
			t_td_2 = _replaceSchoolItem(school_list[i+1], tmpl_text, ishide);
			grid_content += "<tr>" + t_td_1 + t_td_2 + "</tr>";
		}
		$("#grid").html(grid_content);
		init_model.bindSchoolTap(ishide);
	}

	function _replaceSchoolItem(item, tmpl_text, ishide)
	{
		if (! item) {
			return "";
		}

		temp_str = tmpl_text.replace(/\${name}/g, item.name);
		temp_str = temp_str.replace(/\${ishide}/g, ishide ? "display:none;": "");
		if (item.url == "add") {
			temp_str = temp_str.replace(/\${cover}/g, '<i class="school_logo fa fa-2x fa-plus-circle custom_normal_color"></i>');
			temp_str = temp_str.replace(/\${action}/g, "onclick='load_schoollist_page();'");
		} else {
			var imgTag = '<img class="school_logo" src="' + item.cover + '" />';
			temp_str = temp_str.replace(/\${cover}/g, imgTag);
			temp_str = temp_str.replace(/\${url}/g, item.url);
			temp_str = temp_str.replace(/\${class}/g, 'school_delbtn');
			temp_str = temp_str.replace(/\${school_div}/g, 'school_div');
		}
		return temp_str;
	}

	exports.delSchool = function(name)
	{
		var school_list;
		var school_list_str = appStore.getItem("school_list");
		if (!school_list_str) return;

		school_list = $.parseJSON(school_list_str);
		for (var i in school_list) {
			var item = school_list[i];
			if (item.name == name) {
				//del array item in pos i;
				school_list.splice(i, 1);
				if (school_list.length == 0) {
					appStore.removeItem("school_list");
				} else {
					appStore.setItem("school_list", _array2string(school_list));
				}
				return true;
			}
		}
		return false;
	}

	exports.saveSchool = function(name, cover, url)
	{
		setSchoolHost(url, name);
		appstore_model.setStoreCache("defaultSchool", url);
		appstore_model.setStoreCache("defaultSchoolName", name);
		load_courselist_page();
	}

	function saveSchoolToLocal(name, cover, url)
	{
		var school_list;
		var school_list_str = appStore.getItem("school_list");
		if (school_list_str) {
			school_list = $.parseJSON(school_list_str);
			if (!_isExistsSchool(school_list, name)) {
				var length = school_list.length;
				school_list[length] = {"name" : name, "cover" : cover, "url" : url};
				appStore.setItem("school_list", _array2string(school_list));
				$("#afui").popup("添加成功");
			} else {
				$("#afui").popup("该网校已添加！");
			}
		} else {
			school_list = new Array();
			school_list[0] = {"name" : name, "cover" : cover, "url" : url};
			appStore.setItem("school_list", _array2string(school_list));
			$("#afui").popup("添加成功");
		}
	}

	function _array2string(array)
	{
		var t_str = "[";
		var length = array.length;
		for (var i in array) {
			t_str += '{"name":"' + array[i].name + '","cover":"' + array[i].cover + '","url":"' + array[i].url + '"}';
			if (i < length - 1) {
				t_str += ",";
			}
		}
		t_str += "]";
		return t_str;
	}

	function _isExistsSchool(school_list, name) 
	{
		for (var i in school_list) {
			var item = school_list[i];
			if (item && item.name == name) {
				return true;
			}
		}
		return false;
	}

});