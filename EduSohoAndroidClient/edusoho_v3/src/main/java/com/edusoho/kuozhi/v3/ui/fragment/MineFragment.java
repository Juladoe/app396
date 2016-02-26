package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.AvatarLoadingListener;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by JesseHuang on 15/12/8.
 */
public class MineFragment extends BaseFragment {

    private TextView tvNickname;
    private TextView tvTitle;
    private RoundedImageView rivAvatar;
    private View vUserInfoLayout;

    private final int mViewIds[] = {
            R.id.rl_my_1,
            R.id.rl_my_2,
            R.id.rl_my_3,
            R.id.rl_my_4,
            R.id.rl_my_5,
    };

    private final View[] mViews = new View[mViewIds.length];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_mine);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        setHasOptionsMenu(true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initData();
        }
    }

    @Override
    protected void initView(View view) {
        tvNickname = (TextView) view.findViewById(R.id.tv_nickname);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        rivAvatar = (RoundedImageView) view.findViewById(R.id.riv_avatar);
        vUserInfoLayout = view.findViewById(R.id.rl_user_info);

        vUserInfoLayout.setOnClickListener(mUserInfoClickListener);
        for (int i = 0; i < mViews.length; i++) {
            mViews[i] = view.findViewById(mViewIds[i]);
            mViews[i].setOnClickListener(mRadioBtnClickListener);
        }
    }

    private void initData() {
        if (app.loginUser == null || TextUtils.isEmpty(app.token)) {
            return;
        }
        tvNickname.setText(app.loginUser.nickname);
        tvTitle.setText(app.loginUser.title);
        ImageLoader.getInstance().displayImage(app.loginUser.mediumAvatar, rivAvatar, new AvatarLoadingListener(app.getCurrentUserRole()));
    }

    private View.OnClickListener mRadioBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.rl_my_1) {
                mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, Const.MY_LEARN);
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
            } else if (v.getId() == R.id.rl_my_2) {
                mActivity.app.mEngine.runNormalPlugin("DownloadManagerActivity", mContext, null);
            } else if (v.getId() == R.id.rl_my_3) {
                mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, Const.VIP_LIST);
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
            } else if (v.getId() == R.id.rl_my_4) {
                mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, Const.MY_FAVORITE);
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
            } else if (v.getId() == R.id.rl_my_5) {
                mActivity.app.mEngine.runNormalPlugin("SettingActivity", mContext, null);
            }
        }
    };

    private View.OnClickListener mUserInfoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (app.loginUser == null) {
                return;
            }

            mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, Const.MY_INFO);
                    startIntent.putExtra(Const.WEB_URL, url);
                }
            });
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }
}
