package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.WebViewActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by JesseHuang on 15/12/8.
 */
public class MineFragment extends BaseFragment {

    private TextView tvNickname;
    private TextView tvTitle;
    private RoundedImageView rivAvatar;
    private View vUserInfoLayout;

    private final int mRadioIds[] = {
            R.id.radio0,
            R.id.radio1,
            R.id.radio2,
            R.id.radio3,
            R.id.radio4,
    };

    private final RadioButton[] mRadioButtons = new RadioButton[mRadioIds.length];

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
    protected void initView(View view) {
        //mActivity.setTitle(getString(R.string.title_mine));
        tvNickname = (TextView) view.findViewById(R.id.tv_nickname);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        rivAvatar = (RoundedImageView) view.findViewById(R.id.riv_avatar);
        vUserInfoLayout = view.findViewById(R.id.rl_user_info);

        vUserInfoLayout.setOnClickListener(mUserInfoClickListener);
        for (int i = 0; i < mRadioButtons.length; i++) {
            mRadioButtons[i] = (RadioButton) view.findViewById(mRadioIds[i]);
            mRadioButtons[i].setOnClickListener(mRadioBtnClickListener);
        }
    }

    private void initData() {
        if (app.loginUser == null || TextUtils.isEmpty(app.token)) {
            return;
        }
        tvNickname.setText(app.loginUser.nickname);
        tvTitle.setText(app.loginUser.title);
    }

    private View.OnClickListener mRadioBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.radio0) {
                mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, Const.MY_LEARN);
                        startIntent.putExtra(WebViewActivity.URL, url);
                    }
                });
            } else if (v.getId() == R.id.radio1) {
                mActivity.app.mEngine.runNormalPlugin("DownloadManagerActivity", mContext, null);
            } else if (v.getId() == R.id.radio2) {
                mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, Const.VIP_LIST);
                        startIntent.putExtra(WebViewActivity.URL, url);
                    }
                });
            } else if (v.getId() == R.id.radio3) {
                mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, Const.MY_FAVORITE);
                        startIntent.putExtra(WebViewActivity.URL, url);
                    }
                });
            } else if (v.getId() == R.id.radio4) {
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
                    startIntent.putExtra(WebViewActivity.URL, url);
                }
            });
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.layout.);
    }
}
