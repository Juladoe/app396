package com.plugin.edusoho.bdvideoplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.baidu.cyberplayer.utils.VersionManager;
import com.baidu.cyberplayer.utils.ZipUtils;

import java.io.File;

/**
 * Created by howzhi on 14-6-18.
 */
public class BdPlayerManager {

    private String mSoLibDir;
    private Context mContext;
    private String mPlayerSoVerson;

    private static final String PLAYER_VERSION = "playerVersion";
    private static final String SO_LIB_DIR = "soLibDir";
    public static final String NORMAL_LIB_DIR = "normal";
    private static final String PLAYER_VERSION_CONFIG = "player_version_config";

    private static final int TIMEOUT = 3000;

    private VersionManager mVersionManager;
    public String ak = "6ZB2kShzunG7baVCPLWe7Ebc";
    public String sk = "wt18pcUSSryXdl09jFvGvsuNHhGCZTvF";

    public BdPlayerManager(Context context)
    {
        mContext = context;
        mVersionManager = VersionManager.getInstance();
        loadPlayerVersion();
    }

    private void loadPlayerVersion()
    {
        String version = mContext.getResources().getString(R.string.player_version);
        SharedPreferences sp = mContext.getSharedPreferences(PLAYER_VERSION_CONFIG, mContext.MODE_PRIVATE);
        if (sp.contains(PLAYER_VERSION)) {
            version = sp.getString(PLAYER_VERSION, version);
        }

        mSoLibDir = NORMAL_LIB_DIR;
        if (sp.contains(SO_LIB_DIR)) {
            mSoLibDir = sp.getString(SO_LIB_DIR, NORMAL_LIB_DIR);
        }

        mPlayerSoVerson = version;
    }

    private boolean compareVersion(String version)
    {
        if (mPlayerSoVerson.toUpperCase().equals(version.toUpperCase())) {
            return true;
        }
        return false;
    }

    private void downPlayerSoLib(String url, final DownLoacCallback callback)
    {
        AQuery aq = new AQuery(mContext);
        final File fileDir = mContext.getFilesDir();
        File ext = new File(fileDir, url);
        final File libDir = new File(fileDir, "armeabi-v7a");
        if (!libDir.exists()) {
            libDir.mkdir();
        }
        aq.download(url, ext, new AjaxCallback<File>(){
            @Override
            public void callback(String url, File soFile, AjaxStatus status) {
                super.callback(url, soFile, status);
                if (soFile != null) {
                    try {
                        ZipUtils.getInstance().unZip(
                                mContext,
                                soFile.getAbsolutePath(),
                                libDir.getAbsolutePath() + "/"
                        );
                        callback.success(libDir.getAbsolutePath());
                    } catch (Exception e) {
                        //file write error
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void playerVideo(String url)
    {
        checkPlayerVersion(new CheckPlayerVersionCallback() {
            @Override
            public void success() {
                Intent startIntent = new Intent(mContext, BdVideoPlayerActivity.class);
                String url = "http://devimages.apple.com/iphone/samples/bipbop/gear4/prog_index.m3u8";
                startIntent.setData(Uri.parse(url));
                startIntent.putExtra("soLibDir", mSoLibDir);
                System.out.println("solib->" + mSoLibDir);
                mContext.startActivity(startIntent);
            }

            @Override
            public void fail() {

            }
        });
    }

    private interface DownLoacCallback
    {
        public void success(String dirPath);
    }

    private interface CheckPlayerVersionCallback
    {
        public void success();
        public void fail();
    }

    private void savePlayerVersion(String version, String libDriPath)
    {
        SharedPreferences sp = mContext.getSharedPreferences(PLAYER_VERSION_CONFIG, mContext.MODE_APPEND);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PLAYER_VERSION, version);
        editor.putString(SO_LIB_DIR, libDriPath);
        editor.commit();
    }

    public void checkPlayerVersion(final CheckPlayerVersionCallback checkPlayerVersionCallback)
    {
        mVersionManager.getCurrentSystemCpuTypeAndFeature(
                TIMEOUT,
                ak,
                sk,
                new VersionManager.RequestCpuTypeAndFeatureCallback() {
                    @Override
                    public void onComplete(final VersionManager.CPU_TYPE cpu_type, int result) {
                        if (result != VersionManager.RET_SUCCESS) {
                            checkPlayerVersionCallback.fail();
                            return;
                        }
                        System.out.println("cpu->" + cpu_type.name());
                        if (!compareVersion(cpu_type.name())) {
                            mVersionManager.getDownloadUrlForCurrentVersion(
                                    TIMEOUT,
                                    cpu_type,
                                    ak,
                                    sk,
                                    new VersionManager.RequestDownloadUrlForCurrentVersionCallback() {
                                        @Override
                                        public void onComplete(String url, int i) {
                                            downPlayerSoLib(url, new DownLoacCallback() {
                                                @Override
                                                public void success(String dirPath) {
                                                    System.out.println("download");
                                                    savePlayerVersion(cpu_type.name(), dirPath);
                                                    checkPlayerVersionCallback.success();
                                                }
                                            });
                                        }
                            });
                        } else {
                            System.out.println("no_download");
                            checkPlayerVersionCallback.success();
                        }
                    }
                });
    }
}
