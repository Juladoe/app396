package com.broov.player;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.os.PowerManager;

import com.edusoho.plugin.videoplayer.R;

public class VideoPlayer extends Activity {

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		seekBarUpdater.stopIt();
		demoRenderer.exitApp();
        finish();
	}

	@Override
	protected void onStop() 
	{
		if (wakeLock != null) {
			wakeLock.release();
			wakeLock = null;
		}

		super.onStop();
	}


	PhoneStateListener phoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				//Incoming call: Pause music
				Log.d(null, "Video call state ringing");
				//Pause the video, only if video is playing 
				if ((demoRenderer != null) && (!paused)) { 
					Log.d(null, "Triggered");
					demoRenderer.nativePlayerPlay();
				}

				//seekBarUpdater = new Updater();
				//mHandler.postDelayed(seekBarUpdater, 500);
			} else if(state == TelephonyManager.CALL_STATE_IDLE) {
				//Not in call: Play music
				Log.d(null, "Video call state idle");
				//do not resume, if already paused by User  
				if ((demoRenderer != null) && (!paused)) {
					Log.d(null, "Triggered");
					demoRenderer.nativePlayerPause();
				}
				//seekBarUpdater.stopIt();
			} else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
				//A call is dialing, active or on hold
				Log.d(null, "Video call state offhook");
				
				if ((demoRenderer != null) && (!paused)) {
					Log.d(null, "Triggered");
					demoRenderer.nativePlayerPlay();
				}
				//seekBarUpdater = new Updater();
				//mHandler.postDelayed(seekBarUpdater, 500);
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Globals.setNativeVideoPlayer(false);
		Log.d(null, "VideoPlayer onCreate");
		paused = false;

		// fullscreen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);		

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	    setContentView(R.layout.video_player);
		i = getIntent();

		if (i!= null) {
			Uri uri = i.getData();
			if (uri!= null) {
				openfileFromBrowser = uri.getEncodedPath();

				//Change from 1.6
				String decodedOpenFileFromBrowser = null;
				try {
					decodedOpenFileFromBrowser = URLDecoder.decode(openfileFromBrowser,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				if (decodedOpenFileFromBrowser != null)
				{
					openfileFromBrowser = decodedOpenFileFromBrowser; 
				}
			}	
		}
		Log.d(null, "openfileFromBrowser:"+openfileFromBrowser);

		if(FileManager.isVideoFile(openfileFromBrowser)){
			Globals.setFileName(openfileFromBrowser);	
			Log.d(null, "================openfileFromBrowser:"+openfileFromBrowser+"=============");			

		}	
		else {
			Bundle extras = i.getExtras();
			if (extras != null) {
                String tmpFileName = null;
                if (extras.containsKey("videofilenames")) {
                    ArrayList<String> fileNames = extras.getStringArrayList("videofilenames");
                    Globals.setFileList(fileNames);
                    tmpFileName = fileNames.isEmpty() ? "" : fileNames.get(0);
                } else {
                    tmpFileName = extras.getString("videofilename");
                }

				if (FileManager.isVideoFile(tmpFileName)) {
					Globals.setFileName(tmpFileName);
					Log.d(null, "================extras.getString videofilename:"+tmpFileName+"============");
				}
			}
		}

		Log.d(null, "=======================Playing filename:" + Globals.fileName);

		mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		Log.d(null, "TelephoneManager : "+mgr);
		if(mgr != null) {
			Log.d(null, "telephonemanager start");
			mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
			
		}

		// Find the views whose visibility will change
		mSeekBar = (SeekBar) findViewById(R.id.progressbar);

		currentTime = (TextView) findViewById(R.id.currenttime);     
		totalTime = (TextView) findViewById(R.id.totaltime);        
		controlPanel = (TableLayout) findViewById(R.id.controlPanel);
		controlPanel.getBackground().setAlpha(85);

		imgPlay = findViewById(R.id.img_vp_play);
		imgForward = findViewById(R.id.img_vp_forward);
		imgBackward = findViewById(R.id.img_vp_backward);
		imgAspectRatio = findViewById(R.id.fs_shadow);

		mHideContainer = findViewById(R.id.hidecontainer);
		mHideContainer.setOnClickListener(mVisibleListener);
		
		mControlPanelContainer = findViewById(R.id.controlPanel);
		mControlPanelContainer.setOnClickListener(mControlPanelListener);

		imgAspectRatio.setOnTouchListener(imgAspectRatioTouchListener);
		imgPlay.setOnTouchListener(imgPlayTouchListener);
		imgForward.setOnTouchListener(imgForwardTouchListener);
		imgBackward.setOnTouchListener(imgBackwardTouchListener);
		mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);

		initSDL();
	}

	public void initSDL()
	{
		//Wake lock code
		try {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			//wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, Globals.ApplicationName);
			wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, Globals.ApplicationName);
			
			wakeLock.acquire();
		} catch (Exception e) {
			Log.d(null, "Inside wake lock exception" + e.toString());
		}
		Log.d(null, "Acquired wakeup lock");

		//Native libraries loading code
		Globals.LoadNativeLibraries();
		Log.d(null, "native libraries loaded");

		//Audio thread initializer
		mAudioThread = new AudioThread(this);
		Log.d(null, "Audio thread initialized");

		GLSurfaceView_SDL surfaceView = (GLSurfaceView_SDL) findViewById(R.id.glsurfaceview);
		Log.d(null, "got the surface view:");

		surfaceView.setOnClickListener(mGoneListener);

		DemoRenderer demoRenderer = new DemoRenderer(this);
		this.demoRenderer = demoRenderer;
		surfaceView.setRenderer(demoRenderer); 
		Log.d(null, "Set the surface view renderer");

		SurfaceHolder holder = surfaceView.getHolder();
		holder.addCallback(surfaceView);
		Log.d(null, "Added the holder callback");
		holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
		Log.d(null, "Hold type set");

		surfaceView.setFocusable(true);
		surfaceView.requestFocus();

		totalDuration = demoRenderer.nativePlayerTotalDuration();
		totalTime.setText(Utils.formatTime(totalDuration));

		mHandler.postDelayed(seekBarUpdater, 100);
	}

	public void restartUpdater() {
		seekBarUpdater.stopIt();
		seekBarUpdater = new Updater();
		mHandler.postDelayed(seekBarUpdater, 100);
	}

	private class Updater implements Runnable {
		private boolean stop;

		public void stopIt() {
			Log.d(null, "Stopped updater");
			stop = true;
		}

		@Override
		public void run() {
			//Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			if(currentTime != null && demoRenderer != null) {
				long playedDuration = demoRenderer.nativePlayerDuration();				
				currentTime.setText(Utils.formatTime(playedDuration));
				totalDuration = demoRenderer.nativePlayerTotalDuration();
				if(totalDuration != 0) {
					int progress = (int)((1000 * playedDuration) / totalDuration);
					mSeekBar.setProgress(progress);							
					totalTime.setText(Utils.formatTime(totalDuration));
				}						
				if (demoRenderer.fileInfoUpdated) {
					demoRenderer.fileInfoUpdated = false;
				}
			}

			if(!stop) {
				if (Globals.fileName != null) {
					//Restart the updater if file is still playing
					mHandler.postDelayed(seekBarUpdater, 500);
				}
			}
		}
	}



	OnClickListener mGoneListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			Log.d(null, "Inside mGone Click");
			if ((mHideContainer.getVisibility() == View.INVISIBLE) ||
					(mHideContainer.getVisibility() == View.GONE))
			{
				mHideContainer.setVisibility(View.VISIBLE);
				restartUpdater();
			}	else 
			{
				mHideContainer.setVisibility(View.INVISIBLE);
				seekBarUpdater.stopIt();
			}
		}
	};



	OnClickListener mVisibleListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			if ((mHideContainer.getVisibility() == View.GONE) ||
					(mHideContainer.getVisibility() == View.INVISIBLE)) 
			{
				mHideContainer.setVisibility(View.VISIBLE);
				restartUpdater();
			} else 
			{
				mHideContainer.setVisibility(View.INVISIBLE);
				seekBarUpdater.stopIt();
			}
		}
	};
	
	OnClickListener mControlPanelListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			//Do not hide the control panel par, when clicked
			//Log.d(null, "CONTROL PANEL  LISTENER ONCLICK ");
		}
	};

	OnTouchListener imgAspectRatioTouchListener = new OnTouchListener() {			
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			ImageView img = (ImageView) v;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				//Do nothing for now
			}
			else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (current_aspect_ratio_type == 3) {
					img.setImageResource(R.drawable.fs_shadow_4_3);
					demoRenderer.nativePlayerSetAspectRatio(0);
					current_aspect_ratio_type = 1;
				} else if (current_aspect_ratio_type == 1) {
					img.setImageResource(R.drawable.fs_shadow);
					demoRenderer.nativePlayerSetAspectRatio(3);
					current_aspect_ratio_type = 2;
				} else if (current_aspect_ratio_type == 2) {
					img.setImageResource(R.drawable.fs_shadow_16_9);
					demoRenderer.nativePlayerSetAspectRatio(2);
					current_aspect_ratio_type = 3;

				}
			}						
			//resetAutoHider();
			return true;
		}
	};

	OnTouchListener imgPlayTouchListener = new OnTouchListener() {			
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			ImageView img = (ImageView) v;				

			if (event.getAction() == MotionEvent.ACTION_DOWN ) {	
				Log.d(null, "Down paused:" + paused);
				if(paused) {
					img.setImageResource(R.drawable.vp_play);
				}
				else {
					img.setImageResource(R.drawable.vp_pause);
				}
			}
			else if (event.getAction() == MotionEvent.ACTION_UP ) {
				Log.d(null, "Up paused:" + paused);		  
				Log.d(null, "Total:" + demoRenderer.nativePlayerTotalDuration() + "---Current:" + demoRenderer.nativePlayerDuration());
				if(paused) {
					demoRenderer.nativePlayerPause();
					seekBarUpdater = new Updater();
					mHandler.postDelayed(seekBarUpdater, 500);
					img.setImageResource(R.drawable.vp_pause_shadow);
				}
				else {
					demoRenderer.nativePlayerPlay();
					seekBarUpdater.stopIt();						
					img.setImageResource(R.drawable.vp_play_shadow);
				}		        	
				paused = !paused;
			}				
			//resetAutoHider();
			return true;
		}
	};

	OnTouchListener imgForwardTouchListener = new OnTouchListener() {			
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			ImageView img = (ImageView) v;
			if (event.getAction() == MotionEvent.ACTION_DOWN ) {		            		            
				img.setImageResource(R.drawable.vp_forward_glow);
			}
			else if (event.getAction() == MotionEvent.ACTION_UP ) {		        	
				img.setImageResource(R.drawable.vp_forward);										
				demoRenderer.nativePlayerForward();
			}							
			//resetAutoHider();
			return true;
		}
	};

	OnTouchListener imgBackwardTouchListener = new OnTouchListener() {			
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			ImageView img = (ImageView) v;
			if (event.getAction() == MotionEvent.ACTION_DOWN ) {		            		            
				img.setImageResource(R.drawable.vp_backward_glow_80x60);
			}
			else if (event.getAction() == MotionEvent.ACTION_UP ) {		        	
				img.setImageResource(R.drawable.vp_backward);										
				demoRenderer.nativePlayerRewind();
			}						
			//resetAutoHider();
			return true;
		}
	};

	OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			int progress = seekBar.getProgress();
			demoRenderer.nativePlayerSeek(progress);
			if (!paused) {
				restartUpdater();
			} 
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			//	// TODO Auto-generated method stub
			//	trScrolledTime.setVisibility(View.VISIBLE);
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if(fromUser) {
				long currentSecsMoved = (long)((totalDuration * ((float) (progress / 10F ))) / 100);
				String timeMoved = Utils.formatTime(currentSecsMoved);
				currentTime.setText(timeMoved);
			}
		}
	};

	View mHideContainer;
	View mControlPanelContainer;

	View imgPlay; 
	View imgBackward; View imgForward;
	View imgAspectRatio;
	SeekBar mSeekBar;
	TextView currentTime; TextView totalTime; 

	long totalDuration;

	DemoRenderer demoRenderer;
	TableLayout controlPanel;

	private AudioThread 		  mAudioThread = null;
	private PowerManager.WakeLock wakeLock     = null;
	private Handler mHandler = new Handler();

	private Updater seekBarUpdater = new Updater();
	private static int current_aspect_ratio_type=1;
	//Default Aspect Ratio of the file
	private static boolean paused;
	String openfileFromBrowser = "";
	Intent i = getIntent();
	
	TelephonyManager mgr;

}
