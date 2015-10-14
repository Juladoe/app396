package com.edusoho.liveplayer;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by howzhi on 15/10/12.
 */
public class ProxyBaseActivity extends Activity {

    protected boolean mIsProxy;
    protected Activity mActivity;
    public static final String ISPROXY = "isProxy";
    protected AssetManager mAssetManager;
    protected Resources mResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mIsProxy = savedInstanceState.getBoolean(ISPROXY, false);
        }

        if (! mIsProxy) {
            super.onCreate(savedInstanceState);
            mActivity = this;
        } else {
            loadResource();
        }
    }

    protected void loadResource() {
        
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            File pluginDir = new File(mActivity.getBaseContext().getFilesDir(), "plugin");
            File pluginFile = new File(pluginDir, "liveEplayer-edusoho-release.apk");

            addAssetPath.invoke(assetManager, pluginFile.getAbsolutePath());
            mAssetManager = assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Resources superRes = mActivity.getResources();
        mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(),
                superRes.getConfiguration());
        Resources.Theme mTheme = mResources.newTheme();
        mTheme.setTo(mActivity.getTheme());
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }

    @Override
    public Resources getResources() {
        return mResources == null ? super.getResources() : mResources;
    }

    public void setActivity(Activity activity) {
        this.mActivity = activity;
        Log.d("LivePlayerActivity", "activity:" + activity);
    }
}
