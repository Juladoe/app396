package com.gensee.fragement;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gensee.player.Player;
import com.gensee.player.R;
import com.gensee.view.GSVideoView;

@SuppressLint("ValidFragment")
public class ViedoFragment extends Fragment implements OnClickListener {

	public static final int LIVE = 0;
	public static final int PAUSE = 1;
	public static final int CLOSE = 2;
	public static final int BUFFERING = 3;
	public static final int RECONNECTING = 4;
	public static final int ERROR = 5;
	public static final int NO_START = 6;

	private Player mPlayer;
	private View mView;
	private ProgressBar mLoadView;
	private TextView mTitleView;
	private ImageView mStatusView;
	private GSVideoView mGSViedoView;
	private TextView txtVideo, txtAudio,txtMic,txtHand;
	private Runnable handRun = null;

	public ViedoFragment(Player player) {
		this.mPlayer = player;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mView = inflater.inflate(R.layout.imviedo, null);
		mGSViedoView = (GSVideoView) mView.findViewById(R.id.imvideoview);
		mLoadView = (ProgressBar) mView.findViewById(R.id.iv_live_progressbar);
		mTitleView = (TextView) mView.findViewById(R.id.tv_live_load_title);
		mStatusView = (ImageView) mView.findViewById(R.id.iv_live_status);
		mGSViedoView = (GSVideoView) mView.findViewById(R.id.imvideoview);
		mGSViedoView.setRenderMode(GSVideoView.RenderMode.RM_FILL_CENTER_CROP);
		mPlayer.setGSVideoView(mGSViedoView);
		return mView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mGSViedoView.setOnClickListener(this);
		setPlayStatus(BUFFERING);
	}

	private Handler mUpdateHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case RECONNECTING:
					setPlayBufferingStatus("正在重连...");
					break;
				case BUFFERING:
					setPlayBufferingStatus("正在加载...");
					break;
				case LIVE:
					setPlayLiveStatus();
					mGSViedoView.setBackgroundColor(Color.TRANSPARENT);
					break;
				case PAUSE:
					setPlayPauseStatus();
					mGSViedoView.setDefColor(Color.BLACK);
					Bitmap bitmap = Bitmap.createBitmap(mGSViedoView.getWidth(), mGSViedoView.getHeight(), Bitmap.Config.ALPHA_8);
					Canvas canvas = new Canvas(bitmap);
					canvas.drawColor(Color.BLACK);
					mGSViedoView.renderDrawble(bitmap, true);
					break;
				case CLOSE:
					break;
				case ERROR:
					setPlayErrorStatus();
					break;
				case NO_START:
					setPlayNoStartStatus();
			}
		}
	};

	public void setPlayStatus(int status) {
		mUpdateHandler.sendEmptyMessage(status);
	}

	private void setPlayBufferingStatus(String title) {
		mLoadView.setVisibility(View.VISIBLE);
		mTitleView.setVisibility(View.VISIBLE);
		mTitleView.setText(title);
	}

	private void setPlayErrorStatus() {
		mLoadView.setVisibility(View.GONE);
		mTitleView.setVisibility(View.VISIBLE);
		mTitleView.setText("加载失败");
	}

	private void setPlayNoStartStatus() {
		mStatusView.setVisibility(View.VISIBLE);
		mStatusView.setImageResource(R.drawable.icon_live_status);
		mLoadView.setVisibility(View.GONE);
		mTitleView.setVisibility(View.VISIBLE);
		mTitleView.setText("直播尚未开始");
	}

	private void setPlayPauseStatus() {
		mStatusView.setVisibility(View.VISIBLE);
		mStatusView.setImageResource(R.drawable.icon_live_status);
		mLoadView.setVisibility(View.GONE);
		mTitleView.setVisibility(View.VISIBLE);
		mTitleView.setText("休息时间");
	}

	private void setPlayLiveStatus() {
		mStatusView.setVisibility(View.GONE);
		mLoadView.setVisibility(View.GONE);
		mTitleView.setVisibility(View.GONE);
	}

	private void showController() {
		txtVideo = (TextView) mView.findViewById(R.id.txtVideo);
		txtAudio = (TextView) mView.findViewById(R.id.txtAudio);
		txtMic = (TextView) mView.findViewById(R.id.txtMic);
		txtHand = (TextView) mView.findViewById(R.id.txtHand);
		txtHand.setText("举手");

		txtVideo.setOnClickListener(this);
		txtAudio.setOnClickListener(this);
		txtMic.setOnClickListener(this);
		txtHand.setOnClickListener(this);
	}
	
	public void onJoin(boolean isJoined) {
		if(txtAudio != null){
			txtAudio.setEnabled(isJoined);
			txtVideo.setEnabled(isJoined);
		}
	}

	public void setVideoViewVisible(boolean bVisible) {
		if (isAdded()) {
			if (bVisible) {
				mGSViedoView.setVisibility(View.VISIBLE);
			} else {
				mGSViedoView.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.imvideoview) {
			ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
			if (actionBar == null) {
				return;
			}
			if (actionBar.isShowing()) {
				actionBar.hide();
			} else {
				actionBar.show();
			}

		} else if (id == R.id.txtAudio) {
			if (mPlayer != null) {
				// isSelect 代表关闭状态，默认非关闭状态 即false
				if (v.isSelected()) {
					mPlayer.audioSet(false);
					v.setSelected(false);
					txtAudio.setText(R.string.audio_close);

				} else {
					mPlayer.audioSet(true);
					v.setSelected(true);
					txtAudio.setText(R.string.audio_open);
				}
			}
		} else if (id == R.id.txtVideo) {
			if (mPlayer != null) {
				// isSelect 代表关闭状态，默认非关闭状态 即false
				if (v.isSelected()) {
					mPlayer.videoSet(false);
					v.setSelected(false);
					txtVideo.setText(R.string.video_close);

				} else {
					mPlayer.videoSet(true);
					v.setSelected(true);
					txtVideo.setText(R.string.video_open);
				}
			}
		} else if (id == R.id.txtMic) {
			if (mPlayer != null) {
				mPlayer.openMic(getActivity(), false, null);
				mPlayer.inviteAck((Integer) v.getTag(), false, null);
			}

		} else if (id == R.id.txtHand) {
			if(handRun != null){
				txtHand.removeCallbacks(handRun);
			}
			if(!v.isSelected()){
				mPlayer.handUp(true, null);
				txtHand.setSelected(true);
				handRun = new Runnable() {
					private int time = 60;
					@Override
					public void run() {
						txtHand.setText("举手("+ time + ")");
						time --;
						if(time < 0){
							handDown();
						}else{
							txtHand.postDelayed(this, 1000);
						}
					}
				};
				txtHand.post(handRun);
			} else{
				handDown();
			}
		}
	}
	
	private void handDown(){
		txtHand.setText("举手");
		mPlayer.handUp(false, null);
		txtHand.setSelected(false);
	}

	public void onMicColesed() {
		txtMic.setVisibility(View.GONE);
	}

	public void onMicOpened(int inviteMediaType) {
		txtMic.setTag(inviteMediaType);
		txtMic.setVisibility(View.VISIBLE);
	}
}