package com.gensee.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gensee.entity.UserInfo;
import com.gensee.player.OnChatListener;
import com.gensee.player.Player;
import com.gensee.player.R;
import com.gensee.view.GSImplChatView;
import com.gensee.view.xlistview.XListView;

@SuppressLint("ValidFragment")
public class ChatFragment extends Fragment {

	private Player mPlayer;
	private GSImplChatView mGSImplChatView;
	private View mView;
	private XListView mChatView;

	public ChatFragment(Player player) {

		this.mPlayer = player;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mView = inflater.inflate(R.layout.imchat, null);
		mGSImplChatView = (GSImplChatView) mView.findViewById(R.id.impchatview);
		mChatView = (XListView) mGSImplChatView.findViewById(R.id.gs_talkingcontext);
		mPlayer.setGSChatView(mGSImplChatView);
		mChatView.getAdapter().registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
					@Override
					public void run() {
						mChatView.smoothScrollToPosition(mChatView.getAdapter().getCount() - 1);
					}
				}, 100);
			}
		});

		mPlayer.setOnChatListener(new OnChatListener() {

			@Override
			public void onChatWithPerson(long userId, String sSendName, int senderRole, String text, String rich, int onChatID) {
				mGSImplChatView.onChatWithPerson(userId, sSendName, senderRole, text, rich, onChatID);
			}

			@Override
			public void onChatWithPublic(long userId, String sSendName, int senderRole, String text, String rich, int onChatID) {
				mGSImplChatView.onChatWithPublic(userId, sSendName, senderRole, text, rich, onChatID);
			}

			@Override
			public void onMute(boolean isMute) {
				mGSImplChatView.onMute(isMute);
			}

			@Override
			public void onRoomMute(boolean isMute) {
				mGSImplChatView.onMute(isMute);
				mGSImplChatView.onRoomMute(isMute);
			}

			@Override
			public void onReconnection() {
				mGSImplChatView.onReconnection();
			}

			@Override
			public void onPublish(boolean isPlay) {
				mGSImplChatView.onPublish(isPlay);
			}
		});
		return mView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
}
