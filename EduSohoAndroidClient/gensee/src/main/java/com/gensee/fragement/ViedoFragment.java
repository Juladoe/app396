package com.gensee.fragement;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gensee.player.Player;
import com.gensee.playerdemo.R;
import com.gensee.view.GSVideoView;

@SuppressLint("ValidFragment")
public class ViedoFragment extends Fragment implements OnClickListener {

	private Player mPlayer;
	private View mView;
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
		txtVideo = (TextView) mView.findViewById(R.id.txtVideo);
		txtAudio = (TextView) mView.findViewById(R.id.txtAudio);
		txtMic = (TextView) mView.findViewById(R.id.txtMic);
		txtHand = (TextView) mView.findViewById(R.id.txtHand);
		txtHand.setText("举手");
		
		mGSViedoView = (GSVideoView) mView.findViewById(R.id.imvideoview);
		mGSViedoView.setOnClickListener(this);
		txtVideo.setOnClickListener(this);
		txtAudio.setOnClickListener(this);
		txtMic.setOnClickListener(this);
		txtHand.setOnClickListener(this);

		mPlayer.setGSVideoView(mGSViedoView);
		return mView;
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
			int orientation = getActivity().getRequestedOrientation();
			if (orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
					|| orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
			} else {
				orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
			}
			getActivity().setRequestedOrientation(orientation);
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