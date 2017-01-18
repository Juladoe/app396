package com.gensee.fragement;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gensee.media.VODPlayer;
import com.gensee.player.R;
import com.gensee.view.GSDocViewEx;
import com.gensee.view.GSDocViewGx;

@SuppressLint("ValidFragment")
public class VodDocFragment extends Fragment {

	private VODPlayer mPlayer;
	private View mView;
	private View mLoadView;
	private View mDocEnptyView;
	private GSDocViewGx mGlDocView;

	public VodDocFragment(VODPlayer player) {
		this.mPlayer = player;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mView = inflater.inflate(R.layout.imdoc, null);
		mGlDocView = (GSDocViewGx) mView.findViewById(R.id.imGlDocView);
		mLoadView = mView.findViewById(R.id.iv_live_progressbar);
		mDocEnptyView = mView.findViewById(R.id.tv_doc_empty);
		mPlayer.setGSDocViewGx(mGlDocView);
		mGlDocView.showFillView();

		mGlDocView.setOnPageOpenListener(new GSDocViewEx.OnPageOpenListener() {
			@Override
			public void onPageOpenComplete(int i, int i1) {
				showDocView();
			}

			@Override
			public void onPageOpenFileFailure(int i) {
			}
		});
		return mView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		showLoadView();
	}

	public void showLoadView() {
		mLoadView.setVisibility(View.VISIBLE);
		mDocEnptyView.setVisibility(View.GONE);
	}

	public void showDocView() {
		mDocEnptyView.setVisibility(View.VISIBLE);
		mLoadView.setVisibility(View.GONE);
	}
}
