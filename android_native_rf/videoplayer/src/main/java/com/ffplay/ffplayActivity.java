package com.ffplay;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;

public class ffplayActivity extends Activity {
	
	private ffplayAndroid 	mPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String filePath = "http://bcs.duapp.com/bimbucket/test.mp4";
		if(filePath == null) {
			finish();
		} else {
			mPlayer = new ffplayAndroid(this);
			try {
				mPlayer.setVideoPath(filePath);
			} catch (IOException e) {
			}
			setContentView(mPlayer);
		}
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		return mPlayer.pause();
	}
	
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		mPlayer.resume();
	}

	@Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
}
