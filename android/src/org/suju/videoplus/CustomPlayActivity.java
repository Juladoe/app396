package org.suju.videoplus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.edusoho.htmlapp.R;
import com.edusoho.plugin.VideoPlayerPlugin_Old;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * ���Ž�����
 * @author suju
 *
 */
public class CustomPlayActivity extends Activity implements VideoPlayerCallback{

	/** ��ǰ����ʱ��*/
	private TextView current_tiem;
	
	/** ������ʱ��*/
	private TextView total_time;
	
	/** ���Ű�ť*/
	private ImageView play_btn;
	/** ������ť*/
	private ImageView vol_btn;
	/** ��Ļ�л���ť */
	private ImageView screen_btn;
	private View tools;
	/**
	 * ���Ž�����
	 */
	private SeekBar play_seekbar;
	
	private SurfaceView video_sv;
	private PlayCore pc;
	private String url;
	private Timer hideTimer;
	private Context mContext;
	private int hide_speed = 4;
	
	private static final int HIDE = 0001;

	//ʱ���1970-0-0 08:00:00��ʼ
	private static final int DEFAULT_TIME = 3600 * 1000 * 16;
	private static SimpleDateFormat dateFromat = new SimpleDateFormat("HH:mm:ss");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video);
		mContext = this;
		hideTimer = new Timer();
		initView();
		initParams();
		pc = new PlayCore(mContext, video_sv, play_seekbar, this);
		autoPlay();
		//���ع�����
		hideTimer.schedule(new TimerTask() {
			public void run() {
				hideHandler.obtainMessage(HIDE).sendToTarget();
			};
		}, 0, 1000);
	}
	
	/**
	 * ��ʼ������
	 */
	private void initParams()
	{
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		screen_btn.setImageResource(R.drawable.normal);
		getUrl();
	}
	
	/**
	 * �첽���ع�����
	 */
	private Handler hideHandler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			switch (msg.what) {
				case HIDE:
					if (hide_speed > 0 && --hide_speed == 0) {
						hideTools();
					}
					break;
			}
		}
	};
	
	private void hideTools()
	{
		Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.hidetools);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				tools.setVisibility(View.GONE);
			}
		});
		tools.startAnimation(anim);
	}
	
	/**
	 * ��ȡurl
	 */
	private void getUrl()
	{
		Intent intent = getIntent();
		if (intent.hasExtra("url")) {
			url = intent.getStringExtra("url");
		}else {
			url = "http://hlstest.qiniudn.com/dahuangya.mp4";
		}
	}
	
	/**
	 * ��ʼ����Ļ���
	 */
	private void initView()
	{
		BtnClick bc = new BtnClick();
		
		tools = findViewById(R.id.tools);
		video_sv = (SurfaceView) findViewById(R.id.video_sv);
		current_tiem = (TextView) findViewById(R.id.current_time);
		total_time = (TextView) findViewById(R.id.total_time);
		play_btn = (ImageView) findViewById(R.id.play_btn);
		vol_btn = (ImageView) findViewById(R.id.vol_btn);
		screen_btn = (ImageView) findViewById(R.id.screen_btn);
		play_seekbar = (SeekBar) findViewById(R.id.play_seekbar);
		
		vol_btn.setOnClickListener(bc);
		screen_btn.setOnClickListener(bc);
		play_btn.setOnClickListener(bc);
		
		//���ô�����ʾ������
		video_sv.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				//�ж��Ƿ��Ѿ�����
				if (hide_speed ==0) {
					Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.showtools);
					tools.setVisibility(View.VISIBLE);
					tools.startAnimation(anim);
					//���������ٶ�
					hide_speed = 4;
				} else {
					//�ٶ�Ϊ0��������
					hide_speed = 0;
					hideTools();
				}
				
				return false;
			}
		});
		
		//����seekbar����
		play_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
				//�Ƿ��û��϶�
				if (fromUser) {
					//���������ٶ�
					hide_speed = 4;
					pc.mp.seekTo(progress);
				}
			}
		});
	}
	
	private void autoPlay()
	{
		pc.playVideo(url);
		play_btn.setImageResource(R.drawable.pause);
	}
	
	/**
	 * 
	 * @author suju
	 *
	*/
	private class BtnClick implements OnClickListener
	{
		@Override
		public void onClick(View v) {
			//���������ٶ�
			hide_speed = 4;
			switch (v.getId()) {
				case R.id.play_btn:
					if (pc.mp.isPlaying()) {
						play_btn.setImageResource(R.drawable.play);
						pc.pause();
					} else {
						play_btn.setImageResource(R.drawable.pause);
						pc.play();
					}
					break;
				case R.id.vol_btn:
					//�ж��Ƿ���
					if (pc.volume == 0.0f) {
						pc.voice();
						vol_btn.setImageResource(R.drawable.vol);
					} else {
						pc.mute();
						vol_btn.setImageResource(R.drawable.vol_close);
					}
					break;
				case R.id.screen_btn:
					int orien = getRequestedOrientation();
					//����
					if (orien == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
						//ǿ��Ϊ����
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
						screen_btn.setImageResource(R.drawable.large);
					} else {
						//ǿ��Ϊ����
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						screen_btn.setImageResource(R.drawable.normal);
					}
					break;
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent resultData = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("current_time", pc.mp.getCurrentPosition());
			resultData.putExtra("data", bundle);
			setResult(VideoPlayerPlugin_Old.RESULT_STOP, resultData);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		pc.close();
	}

	@Override
	public void setTotalTime(int time) {
		total_time.setText(dateFromat.format(new Date(time + DEFAULT_TIME)));
	}

	@Override
	public void setPlayTime(int time) {
		current_tiem.setText(dateFromat.format(new Date(time + DEFAULT_TIME)));
	}
}
