package com.edusoho.plugin;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.suju.videoplus.CustomPlayActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


/**
 * 视频播放插件
 * @author suju
 *
 */
public class VideoPlayerPlugin_Old extends Plugin {

	public static final int REQUEST_PLAY = 0001;
	public static final int RESULT_STOP = 0002;
	
	/**请求方法名称 */
	public static final String NAVITE_ACTION = "playVideo";
	private int current_time;
	
	/**
	 * 同步对象
	 */
	private Object synObj = new Object();
	
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		String playurl = null;
		//action 请求方法名称
		if (NAVITE_ACTION.equals(action)) {
			try {
				//data为js包装的请求方法参数
				playurl = data.getString(0);
				Context mContext = ctx.getContext();
				Intent intent = new Intent(mContext, CustomPlayActivity.class);
				intent.putExtra("url", playurl);
				ctx.startActivityForResult(this, intent, REQUEST_PLAY);
				//挂起线程，以响应新启动的activity
				sleep();
				//返回插件结果
				return new PluginResult(PluginResult.Status.OK, current_time);
			} catch (JSONException e) {
				//e.printStackTrace();
			}
		}
		return new PluginResult(PluginResult.Status.ERROR, "不支持该 " + playurl);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == REQUEST_PLAY && resultCode == RESULT_STOP) {
			if (intent != null && intent.hasExtra("data")) {
				Bundle data = intent.getBundleExtra("data");
				current_time = data.getInt("current_time");
			}
		}
		wakeup();
	}
	
	/**
	 * 暂停线程
	 */
	private void sleep()
	{
		synchronized (synObj) {
			try {
				synObj.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 唤醒线程
	 */
	private void wakeup()
	{
		synchronized (synObj) {
			synObj.notify();
		}
	}
}
