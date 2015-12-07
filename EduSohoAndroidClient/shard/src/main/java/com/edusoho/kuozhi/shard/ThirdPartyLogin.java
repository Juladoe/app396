package com.edusoho.kuozhi.shard;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

/**
 * Created by JesseHuang on 15/5/31.
 */
public class ThirdPartyLogin {
    private Context mContext;
    private static ThirdPartyLogin mLogin;
    private static Map<String, Platform> mPlats;

    private ThirdPartyLogin(Context context) {
        mContext = context;
        new ShareSDKUtil().initSDK(context);
        mPlats = new HashMap<>();
    }

    public static ThirdPartyLogin getInstance(Context context) {
        if (mLogin == null) {
            mLogin = new ThirdPartyLogin(context);
        }
        return mLogin;
    }

    public void login(PlatformActionListener pl, String name) {
        Platform mPlatform = ShareSDK.getPlatform(mContext, name);
        mPlats.put(name, mPlatform);
        mPlatform.setPlatformActionListener(pl);
        mPlatform.SSOSetting(false);
        mPlatform.showUser(null);
    }

    public void loginOut(String name) {
        Platform mPlatform = mPlats.get(name);
        if (mPlatform != null && mPlatform.isValid()) {
            mPlatform.removeAccount(true);
            mPlats.remove(name);
        }
    }
}
