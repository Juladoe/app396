package com.edusoho.kuozhi.v3.plugin.appview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.edusoho.kuozhi.v3.ui.live.LessonLivePlayerActivity;
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
        Intent intent = new Intent(mActivity, LessonLivePlayerActivity.class);
        intent.putExtras(bundle);
        mActivity.startActivity(intent);
    }
}
