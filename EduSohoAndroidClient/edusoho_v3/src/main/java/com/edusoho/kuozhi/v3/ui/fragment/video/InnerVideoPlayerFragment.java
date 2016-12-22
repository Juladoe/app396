package com.edusoho.kuozhi.v3.ui.fragment.video;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.videoplayer.ui.VideoPlayerFragment;


/**
 * Created by suju on 16/12/16.
 */

public class InnerVideoPlayerFragment extends VideoPlayerFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem moreItem = menu.findItem(R.id.menu_more);
        if (moreItem != null) {
            moreItem.setVisible(false);
        }
    }

    private void initFragmentSize() {
        int height = getContext().getResources().getDimensionPixelOffset(com.edusoho.videoplayer.R.dimen.video_height);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        setVideoSize(width, height);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFragmentSize();
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams)lp).gravity = Gravity.CENTER;
            view.setLayoutParams(lp);
        }
    }

    @Override
    protected void changeScreenLayout(int orientation) {
        if (orientation == getResources().getConfiguration().orientation) {
            return;
        }
        View playView = getView();
        ViewParent viewParent = playView.getParent();
        if (viewParent == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) viewParent.getParent();

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        ViewGroup.LayoutParams lp = parent.getLayoutParams();
        lp.height = orientation == Configuration.ORIENTATION_LANDSCAPE ?
                wm.getDefaultDisplay().getHeight() : getContext().getResources().getDimensionPixelOffset(com.edusoho.videoplayer.R.dimen.video_height);
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        parent.setLayoutParams(lp);
    }
}
