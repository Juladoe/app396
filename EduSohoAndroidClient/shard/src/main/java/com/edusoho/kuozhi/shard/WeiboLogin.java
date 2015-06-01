package com.edusoho.kuozhi.shard;

import android.content.Context;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

/**
 * Created by JesseHuang on 15/5/31.
 */
public class WeiboLogin {
    private Context mContext;
    private Platform mWeiboPlatform;
    private static WeiboLogin mWeiboLogin;

    private WeiboLogin(Context context) {
        mContext = context;
        ShareSDK.initSDK(context);
        mWeiboPlatform = ShareSDK.getPlatform(context, SinaWeibo.NAME);
    }

    public static WeiboLogin getInstance(Context context) {
        if (mWeiboLogin == null) {
            mWeiboLogin = new WeiboLogin(context);
        }
        return mWeiboLogin;
    }

    public void login(PlatformActionListener pl) {
        mWeiboPlatform.setPlatformActionListener(pl);
        mWeiboPlatform.SSOSetting(false);
        //mWeiboPlatform.authorize();
        mWeiboPlatform.showUser(null);
    }

    public void loginOut() {
        if (mWeiboPlatform != null && mWeiboPlatform.isValid()) {
            mWeiboPlatform.removeAccount(true);
        }
    }
}
