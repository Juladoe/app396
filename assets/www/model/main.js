define(function(require, exports){

	var func_txt = new String(function(){
	/*
		<textarea id="school_templ_text" style="display:none;">
			<td>
				<div style="height:80px;width:120px;margin:0 auto;" class="school_div">
				<img height="50" width="80" src="${cover}" />
				<span style="display:block;color: rgba(82,155,234,255);font-size: 12px;position: relative;bottom:0px;">
						${name}</span>
				<div class="school_delbtn" onclick="exitSchool('${name}',event);" style="${ishide}background-image: url('images/remove.png');height:20px;width:20px;position: relative;top:-70px;left: 100px;" ></div>
				</div>
			</td>
		</textarea>
	   <h2>
		   <div title="Carousel" id="webcarousel" scrolling="no">
				<div id="carousel"
					style="overflow: hidden; height: 160px; width: 318px;margin:0 auto;border-bottom: 1px solid rgba(158,158,158,255);">
					
					<div class="banner"
						style="float: left; width: 318px; height: 300px; background: url('images/img1.jpg');">Edusoho广告位</div>
					<div class="banner"
						style="float: left; width: 318px; height: 300px;  background: url('images/img2.jpg');">Edusoho广告位</div>
					<div class="banner"
						style="float: left; width: 318px; height: 300px;  background: url('images/img3.jpg');">Edusoho广告位</div>
				</div>
				<div id="carousel_dots"
					style="text-align: center; margin-left: auto; margin-right: auto; clear: both; position: relative; top: -20px; z-index: 200">
				</div>
			</div>

			<table id="grid" style="width:100%;margin: 0 auto;" id="item_tb">
				<tr>
					<td>
						
					</td>
					<td>
					<a class="icon add Big" onclick="load_schoollist_page();">选择添加网校</a>
					</td>
				</tr>
			</table>
		</h2>
	*/
	});

	function loadedPanel() {
		require(
				['../appstore', '../init']
				,function(){
					loadUserInfo();
					loadSchoolList(true);
				}
		);
	}

	function _init_carousel() {
		//屏幕宽度;
		var sw = $(document).width() -20;
		$("#webcarousel").css("width",sw + "px");
		$("#carousel").css("width",sw + "px");
		$(".banner").css("width",sw + "px");
		$("#carousel").carousel({
			pagingDiv: "carousel_dots",
			totalPages: 4,
			pagingCssName: "carousel_paging2",
			pagingCssNameSelected: "carousel_paging2_selected"
		});
	}
	//window.addEventListener("load", init_carousel, false);

	var text = func_txt.substring(func_txt.indexOf("/*") + 2, func_txt.lastIndexOf("*/"));
	//$.ui.addContentDiv("main", text, "第二课堂");
	$.ui.updateContentDiv("#main",text);
	loadedPanel();
	_init_carousel();

});
