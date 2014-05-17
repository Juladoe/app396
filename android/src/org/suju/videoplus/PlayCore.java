package org.suju.videoplus;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;

public class PlayCore implements SurfaceHolder.Callback,
		OnBufferingUpdateListener, MediaPlayer.OnPreparedListener,
		MediaPlayer.OnErrorListener, OnCompletionListener {

	public static final int VIDEO_CACHE_READY = 0003;
	public static final int VIDEO_CACHE_UPDATE = 0004;
	public static final int VIDEO_CACHE_END = 0005;

	private VideoUtl vu;
	public MediaPlayer mp;
	private SurfaceHolder sh;
	private SeekBar seekbar;
	private Timer updateTimer;
	private VideoPlayerCallback callback;

	private static final int SET_TOTAL = 0001;
	private static final int UPDATE = 0002;

	private boolean isErrorPause = false;
	private Context mContext;
	private boolean isRead = false;

	public PlayCore(Context context, SurfaceView sv, SeekBar seekbar,
			VideoPlayerCallback callback) {
		this.mContext = context;
		this.callback = callback;
		this.seekbar = seekbar;
		sh = sv.getHolder();
		// 添加回调接口
		sh.addCallback(this);
		// 设置surfaceholder模式
		sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		updateTimer = new Timer();
		updateTimer.schedule(updateTask, 0, 1000);
	}

	/**
	 * 定时器任务
	 */
	private TimerTask updateTask = new TimerTask() {

		@Override
		public void run() {
			if (mp != null) {
				Message msg = updateHandler.obtainMessage(UPDATE);
				msg.arg1 = mp.getCurrentPosition();
				msg.sendToTarget();
			}
		}
	};

	private void startPlay()
	{
		try {
			mp.setDataSource(cache.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新handler
	 */
	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			// 设置视频播放时间
			case SET_TOTAL:
				callback.setTotalTime(msg.arg1);
				seekbar.setMax(msg.arg1);
				seekbar.setProgress(0);
				break;
			// 更新播放时间
			case UPDATE:
				callback.setPlayTime(msg.arg1);
				seekbar.setProgress(msg.arg1);
				break;
			case VIDEO_CACHE_READY:
				if (!isRead) {
					System.out.println("VIDEO_CACHE_READY");
					try {
						startPlay();
						mp.prepare();
						hideLoadDialog();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case VIDEO_CACHE_UPDATE:
				if (isErrorPause) {
					startPlay();
					mp.start();
					hideLoadDialog();
					isErrorPause = false;
				}
				break;
			case VIDEO_CACHE_END:
				System.out.println("VIDEO_CACHE_END");
				if (isErrorPause) {
					try {
						startPlay();
						mp.prepare();
						hideLoadDialog();
					} catch (Exception e) {
						e.printStackTrace();
					}
					isErrorPause = false;
					return;
				}
				if (!isRead) {
					try {
						startPlay();
						mp.prepare();
						hideLoadDialog();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			}
		}
	};

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			System.out.println("surfaceCreated");
			mp = new MediaPlayer();
			mp.setDisplay(sh);
			// 设置声音类型
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mp.setOnBufferingUpdateListener(this);
			mp.setOnPreparedListener(this);
			mp.setOnErrorListener(this);
		} catch (Exception e) {
			//
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	@Override
	public void onBufferingUpdate(MediaPlayer mediaplayer, int progress) {
		seekbar.setSecondaryProgress(progress);
	}

	@Override
	public void onPrepared(MediaPlayer mediaPlayer) {
		mediaPlayer.start();
		int total_time = mediaPlayer.getDuration();
		Message msg = updateHandler.obtainMessage(SET_TOTAL);
		msg.arg1 = total_time;
		msg.sendToTarget();
		isRead = true;
		System.out.println("perpared");
	}

	/**
	 * 声音
	 */
	public float volume = 0.0f;

	public void mute() {
		volume = 0.0f;
		mp.setVolume(volume, volume);
	}

	public void voice() {
		volume = 1.0f;
		mp.setVolume(volume, volume);
	}

	/**
	 * 暂停
	 */
	public void pause() {
		if (mp != null) {
			mp.pause();
		}
	}

	/**
	 * 播放
	 */
	public void play() {
		if (mp != null) {
			mp.start();
		}
	}

	private File cache;
	
	/**
	 * 播放视频
	 * 
	 * @param url
	 */
	public void playVideo(final String url) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 等待mp初始化
					while (true) {
						if (mp == null) {
							continue;
						}
						mp.reset();
						vu = new VideoUtl(mContext, "edusoho", updateHandler,
								mp);
						cache = vu.getCacheFile(url);
						mp.setDataSource(cache.getAbsolutePath());
						mp.prepareAsync();
						vu.downLoad(cache, url);
						break;
					}
					System.out.println("play");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		System.out.println("completion");
	}

	/**
	 * 关闭播放
	 */
	public void close() {
		updateTimer.cancel();
		if (mp != null) {
			mp.release();
			mp = null;
		}
		vu.close();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (!isErrorPause) {
			showLoadDialog();
			isErrorPause = true;
			System.out.println("error");
		}
		return true;
	}

	private static ProgressDialog dialog;
	
	private void hideLoadDialog()
	{
		if (dialog != null) {
			dialog.cancel();
		}
	}
	
	private void showLoadDialog()
	{
		dialog = new ProgressDialog(mContext, ProgressDialog.STYLE_SPINNER);
		dialog.setTitle("加载中");
		dialog.setMessage("正在加载...");
		System.out.println("show load");
		dialog.show();
	}
}
