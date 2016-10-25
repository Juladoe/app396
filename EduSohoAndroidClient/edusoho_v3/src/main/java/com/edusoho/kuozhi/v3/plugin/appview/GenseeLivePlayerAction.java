package com.edusoho.kuozhi.v3.plugin.appview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.util.AppUtil;
import java.io.File;

/**
 * Created by 菊 on 2016/4/11.
 */
public class GenseeLivePlayerAction {

    private Activity mActivity;

    public GenseeLivePlayerAction(Activity activity)
    {
        this.mActivity = activity;
    }

    public void invoke(Bundle bundle) {
        Intent intent = new Intent();
        intent.setClassName(mActivity.getPackageName(), "com.gensee.player.GenseeLivePlayActivity");
        if (checkLiveAppIsExist(intent)) {
            installLiveApp();
            return;
        }
        intent.putExtras(bundle);
        mActivity.startActivity(intent);
    }

    private void installLiveApp() {
        File installDir = AppUtil.getAppInstallStorage();
        CoreEngine.create(mActivity).installApkFromAssetByPlugin(installDir.getAbsolutePath());
        installApk(new File(installDir, "genseeLivePlayer-1.0.apk").getAbsolutePath());
    }

    private boolean checkLiveAppIsExist(Intent intent) {
        return mActivity.getBaseContext().getPackageManager().resolveActivity(intent, 0) == null;
    }

    public void installApk(String file) {
        if (file == null || "".equals(file)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        intent.setDataAndType(Uri.parse("file://" + file),
                "application/vnd.android.package-archive");
        mActivity.getBaseContext().startActivity(intent);
    }
}
