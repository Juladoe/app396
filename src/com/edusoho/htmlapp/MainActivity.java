package com.edusoho.htmlapp;

import org.apache.cordova.DroidGap;
import org.suju.videoplus.VideoUtl;





import com.edusoho.htmlapp.R;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MainActivity extends DroidGap {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		WebView appView = (WebView) findViewById(R.id.appView);
		WebChromeClient wc = new WebChromeClient(){
			@Override
			public void onShowCustomView(View view, CustomViewCallback callback) {
				super.onShowCustomView(view, callback);
			}
		};
		
		appView.setWebChromeClient(wc);
		//super.loadUrl("http://www.howzhi.com");
		super.loadUrl("file:///android_asset/www/index.html");
	}
}
