define(function(require, exports){

	var func_text = new String(function(){
		/*
			<div id="audio_layout" class="audioplaylayout card">
				<table>
					<tr>
						<td>
							<div class="play_status">
								<i id="play_btn" onclick="audio_model.changePlayStatus();" class="fa play_btn fa-play"></i>
								<h5 id="play_title"></h5>
							</div>
						</td>
						<td style="width:100%;">
						</td>
					</tr>
				</table>
				<div class="play_progress_layout" id="play_progress_bar">
					<span class="play_progress_pressed" id="play_progress"></spn>
				</div>
				<span class="playtime" id="play_currentTime"></span>
				<span class="playtime" id="play_TotalTime"></span>
				<img style="display:none;" src="images/star.png" onload="audio_model.init();" />
			</div>
		*/
	});

	exports.text = func_text.substring(func_text.indexOf("/*") + 2, func_text.lastIndexOf("*/"));
	$("body").append('<audio id="audioPlayer" src=""></audio>');

	exports.totalTime = 0;
	var audioPlayer = null;
	var max = 300;
	var progress_limit = 0;
	exports.defaultProgressWidth = 5;
	exports.mediaUri = "http://bcs.duapp.com/bimbucket/sj.mp3";

	exports.audioplayer = function(mediaUri)
	{
		if (mediaUri) {
			exports.mediaUri = mediaUri;
		}
		return exports.text;
	}

	exports.changePlayStatus = function(btn)
	{
		if (audioPlayer.paused) {
			audioPlayer.play();
		} else {
			audioPlayer.pause();
		}
	}

	exports.init = function()
	{
		max = $("#play_progress_bar").width();

		audioPlayer = document.getElementById("audioPlayer");
		audioPlayer.src = exports.mediaUri;
		audioPlayer.load();
		audioPlayer.play();

		audioPlayer.addEventListener("loadedmetadata",function(){
			progress_limit = max / audioPlayer.duration;
			exports.totalTime = audioPlayer.duration;
			$("#play_title").text();
			$("#play_currentTime").attr("max", audioPlayer.duration);
			$("#play_TotalTime").text("/" + getDate(audioPlayer.duration));
			$("#play_progress").css("width", exports.defaultProgressWidth);
		});

		audioPlayer.addEventListener("timeupdate",function(){
			$("#play_currentTime").text(getDate(audioPlayer.currentTime));
			$("#play_progress").css(
				"width", exports.defaultProgressWidth + audioPlayer.currentTime * progress_limit);
		});

		audioPlayer.addEventListener("play",function(){
			$("#play_btn").removeClass("fa-play");
			$("#play_btn").addClass("fa-pause");
		}); 

		audioPlayer.addEventListener("ended",function(){
			$("#play_btn").removeClass("fa-pause");
			$("#play_btn").addClass("fa-play");
		}); 

		audioPlayer.addEventListener("pause",function(){
			$("#play_btn").removeClass("fa-pause");
			$("#play_btn").addClass("fa-play");
		}); 

		if ($.os.ios) {
			$("#play_progress_bar").bind("touchstart", function(evt){
				var touch = evt.touches[0]; //获取第一个触点  
	            var x = Number(touch.pageX); //页面触点X坐标
	            audio_model.seek(x - 15);
			});

			$("#play_progress_bar").bind("touchmove", function(evt){
				var touch = evt.touches[0]; //获取第一个触点  
	            var x = Number(touch.pageX); //页面触点X坐标
	            audio_model.seek(x - 15);
			});
		} else {
			$("#play_progress_bar").bind("click", function(evt){
				audio_model.seek(evt.offsetX);
			});
		}
	}

	function getDate(time)
	{
		var date = new Date(time * 1000 + 3600 * 1000 * 16);
		return format(date, "hh:mm:ss");
	}

	function format(date, format)
	{ 
		var o = { 
			"M+" : date.getMonth()+1, //month 
			"d+" : date.getDate(), //day 
			"h+" : date.getHours(), //hour 
			"m+" : date.getMinutes(), //minute 
			"s+" : date.getSeconds(), //second 
			"q+" : Math.floor((date.getMonth()+3)/3), //quarter 
			"S" : date.getMilliseconds() //millisecond 
		} 

		if(/(y+)/.test(format)) { 
			format = format.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length)); 
		} 

		for(var k in o) { 
			if(new RegExp("("+ k +")").test(format)) { 
			format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length)); 
			} 
		} 
		return format; 
	} 

	exports.stop = function()
	{
		if (audioPlayer) {
			audioPlayer.pause();
		}
	}

	exports.seek = function(y)
	{
		audioPlayer.currentTime = y * (audioPlayer.duration / max);
	}
});