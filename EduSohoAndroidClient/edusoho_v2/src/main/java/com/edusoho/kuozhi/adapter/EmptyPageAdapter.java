package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.fragment.RegistFragment;

import ch.boye.httpclientandroidlib.util.TextUtils;

/**
 * Created by JesseHuang on 15/1/28.
 * 空适配器，用于用户未登录或者空数控的时候显示
 */
public class EmptyPageAdapter<T> extends EmptyAdapter<T> {

    private int mLogoutIcon;
    private int mNoDataIcon;
    private String[] mLogoutText;
    private String[] mLoginText;
    private ActionBarBaseActivity mActivity;


    public EmptyPageAdapter(Context context, int resource) {
        super(context, resource);
    }

    public EmptyPageAdapter(Context context, ActionBarBaseActivity activity, int resource, String[] logoutText, String[] loginText, int logoutIcon, int noDataIcon, boolean isLogin) {
        super(context, resource);
        mActivity = activity;
        mLogoutIcon = logoutIcon;
        mNoDataIcon = noDataIcon;
        mLogoutText = logoutText;
        mLoginText = loginText;
        mIsLogin = isLogin;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return super.getItemId(i);
    }

    @Override
    public Object getItem(int i) {
        return super.getItem(i);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            holder = new ViewHolder();
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_empty_icon);
            holder.tvSecondHeader = (TextView) convertView.findViewById(R.id.tv_second_header_text);
            holder.tvHeader = (TextView) convertView.findViewById(R.id.tv_header_text);
            holder.tvReg = (TextView) convertView.findViewById(R.id.tv_register);
            holder.tvLogin = (TextView) convertView.findViewById(R.id.tv_login);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        setViewStatus(holder);
        holder.tvReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.app.mEngine.runNormalPlugin("RegisterActivity", mActivity, null);
            }
        });
        holder.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.startForResult(mActivity);
            }
        });
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
        if (mParentHeight != MATCH_PARENT) {
            layoutParams.height = parent.getHeight();
        }

        convertView.setLayoutParams(layoutParams);
        return convertView;
    }

    public void setViewStatus(ViewHolder holder) {
        holder.ivIcon.setImageDrawable(mIsLogin ? mContext.getResources().getDrawable(mNoDataIcon) : mContext.getResources().getDrawable(mLogoutIcon));
        holder.tvHeader.setText(mIsLogin ? mLoginText[0] : mLogoutText[0]);
        holder.tvSecondHeader.setText(mIsLogin ? mLoginText[1] : mLogoutText[1]);
        holder.tvReg.setVisibility(mIsLogin ? View.GONE : View.VISIBLE);
        holder.tvLogin.setVisibility(mIsLogin ? View.GONE : View.VISIBLE);
    }

    public static class ViewHolder {
        public ImageView ivIcon;
        public TextView tvHeader;
        public TextView tvSecondHeader;
        public TextView tvReg;
        public TextView tvLogin;
    }
}
