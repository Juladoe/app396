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

	exports.saveUserInfo = function(username,token)
	{
		appStore.setItem("username", username);
		appStore.setItem("token", token);
	}

	exports.clearUserInfo = function()
	{
		appStore.removeItem("username");
		appStore.removeItem("token");
	}

	exports.loadUserInfo = function()
	{
		username = appStore.getItem("username");
		var token = _getStoreItem("token", "");
		setTimeout(function(){
			$.jsonP(
			{
				url:webRoot + "/checktoken" + '?callback=?&token=' + token,
				success:function(data){
					if (!data.token) {
						appStore.removeItem("token");
						return;
					}
					window.loginUserName = username;
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
		var addschool_td = '<td><button onclick="load_schoollist_page()" class="topcoat-tab-bar__button">'
						 + '<i class="fa fa-2x fa-plus big"></i><div class="onsen_tab-bar__label">选择添加网校</div>'
                         + '</button></td>';

		//var addschool_td = "<td><a class='icon add Big' onclick='load_schoollist_page();'>选择添加网校</a></td>";
		$("#grid").empty();
		if (school_list_str) {
			var t_td_1, t_td_2;
			var school_list = $.parseJSON(school_list_str);
			school_list.splice(0, 1, school_list[0], {"name" : "add", "cover" : "", "url" : ""});
			for (var i=0; i < school_list.length; i = i+2) {
				t_td_1 = _replaceSchoolItem(school_list[i], tmpl_text, addschool_td, ishide);
				t_td_2 = _replaceSchoolItem(school_list[i+1], tmpl_text, addschool_td, ishide);
				grid_content += "<tr>" + t_td_1 + t_td_2 + "</tr>";
			}
			$("#grid").html(grid_content);
			init_model.bindSchoolTap(ishide);
		} else {
			$("#grid").html("<tr><td></td>"  + addschool_td + "</tr>");
		}
	}

	function _replaceSchoolItem(item, tmpl_text, addschool_td, ishide)
	{
		if (! item) {
			return "";
		}
		if (item.name == "add") {
			temp_str = addschool_td;
		} else {
			temp_str = tmpl_text.replace(/\${cover}/g, item.cover);
			temp_str = temp_str.replace(/\${url}/g, item.url);
			temp_str = temp_str.replace(/\${name}/g, item.name);
			temp_str = temp_str.replace(/\${ishide}/g, ishide ? "display:none;": "");
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