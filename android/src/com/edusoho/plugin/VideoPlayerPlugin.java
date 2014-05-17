package com.edusoho.plugin;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.suju.videoplus.CustomPlayActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 视频播放插件
 * 
 * @author suju
 * 
 */
public class VideoPlayerPlugin extends CordovaPlugin {

	public static final int REQUEST_PLAY = 0001;
	public static final int RESULT_STOP = 0002;

	/** 请求方法名称 */
	public static final String NAVITE_ACTION = "playVideo";
	private int current_time;

	private CallbackContext callbackContext;
	
	@Override
	public boolean execute(String action, JSONArray data,
			CallbackContext callbackContext) throws JSONException {
		String playurl = null;
		if (NAVITE_ACTION.equals(action)) {
			playurl = data.getString(0);
			Activity mContext = cordova.getActivity();
			Intent intent = new Intent(mContext, CustomPlayActivity.class);
			intent.putExtra("url", playurl);
			cordova.startActivityForResult(this, intent, REQUEST_PLAY);
			this.callbackContext = callbackContext;
			return true;
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == REQUEST_PLAY && resultCode == RESULT_STOP) {
			if (intent != null && intent.hasExtra("data")) {
				Bundle data = intent.getBundleExtra("data");
				current_time = data.getInt("current_time");
				callbackContext.success(current_time);
			}
		}
	}
}
