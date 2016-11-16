package com.edusoho.kuozhi.v3.plugin.appview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;

/**
 * Created by 菊 on 2016/4/11.
 */
public class LonginusLivePlayerAction {

    private Activity mActivity;

    public LonginusLivePlayerAction(Activity activity)
    {
        this.mActivity = activity;
    }

    public void invoke(Bundle bundle) {
        Intent intent = new Intent();
        intent.setClassName(mActivity.getPackageName(), "com.edusoho.longinus.ui.LessonLivePlayerActivity");
        if (checkLiveAppIsExist(intent)) {
            CommonUtil.shortToast(mActivity.getApplicationContext(), "客户端暂时不支持播放此直播类型");
            return;
        }
        intent.putExtras(bundle);
        mActivity.startActivity(intent);
    }

    private boolean checkLiveAppIsExist(Intent intent) {
        return mActivity.getBaseContext().getPackageManager().resolveActivity(intent, 0) == null;
    }
}
