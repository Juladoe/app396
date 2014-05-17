define(function(require, exports){

	exports.bindSchoolTap = function(istap)
	{
		$(".school_div").bind("longTap",function(){
			if (window.isClick) {
				window.isClick = false;
				$(".school_div").unbind("tap");
				$.ui.updateNavbarElements("delbtn_footer");
				$.ui.toggleNavMenu();
		        $(".school_delbtn").show();
		        clearTapStatus();
			}
		   
		});
		if (istap && istap == true) {
			$(".school_div").bind("tap",function(){
				if (window.isClick) {
					window.isClick = false;
					var url = $(this).attr("school-url");
					var name = $(this).attr("school-name");
					setSchoolHost(url, name);
					appstore_model.setStoreCache("defaultSchool", url);
					appstore_model.setStoreCache("defaultSchoolName", name);
					load_courselist_page();
					clearTapStatus();
				}
			});
		}
	}

});