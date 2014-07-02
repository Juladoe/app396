
package com.ffplay;
   
import java.io.IOException;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

public class ffplayAndroid extends SurfaceView {
	private Thread							mRenderThread;
	private Context							mContext;
	private SurfaceHolder					mSurfaceHolder;
	private MediaController					mMediaController;
	private boolean							mPlaying;

	private static boolean sLoaded = false;	
	
	public static final String[] LIBS = new String[] {
		"ffplay",
		"ffmpeg"
	};
	
    private static boolean loadLibs() {
    	if(sLoaded)
    		return true;

    	boolean err = false;
    	for(int i=0;i<LIBS.length;i++) {
    		try {
    			System.loadLibrary(LIBS[i]);
    		} catch(UnsatisfiedLinkError e) {
    			// fatal error, we can't load some our libs
    			Log.d("FFMpeg", "Couldn't load lib: " + LIBS[i] + " - " + e.getMessage());
    			err = true;
    		}
		}
    	if(!err) {
    		sLoaded = true;
    	}
    	return sLoaded;
    }
    
	public ffplayAndroid(Context context) {
        super(context);
        initVideoView(context);
    }
    
    public ffplayAndroid(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initVideoView(context);
    }
    
    public ffplayAndroid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initVideoView(context);
    }
    
    private void initVideoView(Context context) {
    	loadLibs();
		sLoaded = true;
    	native_avcodec_register_all();
		native_av_register_all();

    	mContext = context;
    	getHolder().addCallback(mSHCallback);
    }

    private void attachMediaController() {
    	mMediaController = new MediaController(mContext);
        View anchorView = this.getParent() instanceof View ?
                    (View)this.getParent() : this;
        mMediaController.setMediaPlayer(mMediaPlayerControl);
        mMediaController.setAnchorView(anchorView);
        mMediaController.setEnabled(true);
    }
    
    private void openVideo() {  // initzialize player
    	try {
    		nativeEnableErrorCallback();
			nativeInitVideo();
			nativeInitAudio();

			nativeSetSurface(mSurfaceHolder.getSurface());
		} catch (IOException e) {
		}
    }
    
    private void startVideo() {     // starts playing of video
		mPlaying = true;		

		if(mRenderThread == null) { // we hasn't run player thread so we are launching	
			attachMediaController();
			mRenderThread = new Thread() {	
				
				public void run() {
					try {
						nativePlay();
					} catch (IOException e) {
						mPlaying = false;						
					}					
				}
			};
			mRenderThread.start();
			toggleMediaControlsVisiblity();
		}
    }
    
    public void setVideoPath(String filePath) throws IOException {
    	nativeSetInputFile(filePath);
	}
    
    public boolean pause() {
    	mPlaying = false;
    	return nativePause(true);
    }
    
    public boolean resume() {
    	mPlaying = true;
    	return nativePause(false);
    }
    
    public void decodeAudio(boolean decode) {
    	nativeDecodeAudio(decode);
    }
    
    public void stop() throws InterruptedException {
    	if(!mPlaying)
    		return;

    	mPlaying = false;
    	
    	nativeStop();
    	if(mRenderThread != null)
    		mRenderThread.join();

    	mRenderThread = null;
    }

    private void release() { // release all allocated objects by player   	
    	try {
			stop();
		}catch (InterruptedException e) {			
		}		 

    	nativeRelease();    	
    }

    private void toggleMediaControlsVisiblity() {
        if (mMediaController.isShowing()) {
            mMediaController.hide();
        } else {
            mMediaController.show(3000);
        }
    }

	SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
//            int mSurfaceWidth = w;
//            int mSurfaceHeight = h;
            startVideo();
        }

        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            openVideo();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
			release();
            mSurfaceHolder = null;
        }
    };

    MediaPlayerControl mMediaPlayerControl = new MediaPlayerControl() {

        @Override
        public int getAudioSessionId() {
            return 0;
        }

        public void start() {
			ffplayAndroid.this.startVideo();
		}
		
		public void seekTo(int pos) {
			//Log.d(TAG, "want seek to");
		}
		
		public void pause() {
			ffplayAndroid.this.pause();
		}
		
		public boolean isPlaying() {
			return mPlaying;
		}
		
		public int getDuration() {
			return 3000;//mInputVideo.getDurationInMiliseconds();
		}
		
		public int getCurrentPosition() {
			//Log.d(TAG, "want get current position");
			return 0;
		}
		
		public int getBufferPercentage() {
			//Log.d(TAG, "want buffer percentage");
			return 0;
		}

//		@Override  // ysl 20110315
		public boolean canPause() {
			// TODO Auto-generated method stub
			return false;
		}

//		@Override  // ysl 20110315
		public boolean canSeekBackward() {
			// TODO Auto-generated method stub
			return false;
		}

//		@Override  // ysl 20110315
		public boolean canSeekForward() {
			// TODO Auto-generated method stub
			return false;
		}
	};

	@Override
    protected void finalize() throws Throwable {
    	sLoaded = false;
    }

	private native void native_avcodec_register_all();
	
	private native void native_av_register_all();
	
	private native void nativeSetInputFile(String filePath) throws IOException;

	private native void nativeInitAudio() throws IOException;	
	private native void nativeInitVideo() throws IOException;
	
	private native void nativeEnableErrorCallback();
	
	public native boolean nativePause(boolean pause);	
	private native void   nativePlay() throws IOException;	
	private native void   nativeStop();

	private native void   nativeDecodeAudio(boolean decode);	
	public native boolean isDecodingAudio();

	private native void   nativeSetSurface(Surface surface);
	
	private native void   nativeRelease();
}
