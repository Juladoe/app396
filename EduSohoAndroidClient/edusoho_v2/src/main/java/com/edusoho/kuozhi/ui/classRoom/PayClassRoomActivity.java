package com.edusoho.kuozhi.ui.classRoom;

import android.content.Intent;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.PayStatus;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.common.PayCourseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

/**
 * Created by howzhi on 15/7/14.
 */
public class PayClassRoomActivity extends PayCourseActivity {

    protected int mClassRoomId;
    @Override
    protected void initIntentData() {
        Intent data = getIntent();
        mTitle = data.getStringExtra("title");
        mClassRoomId = data.getIntExtra(Const.CLASSROOM_ID, 0);
        mPrice = data.getDoubleExtra("price", 0.0);

        setBackMode(BACK, "购买班级");
    }

    @Override
    protected void payBtnClick() {
        final LoadDialog loadDialog = LoadDialog.create(mActivity);
        loadDialog.show();

        RequestUrl url = app.bindUrl(Const.PAYCLASSROOM, true);
        url.setParams(new String[]{
                "targetType", "classroom",
                "couponCode", mCodeView.getText().toString(),
                "targetId", String.valueOf(mClassRoomId)
        });

        ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                loadDialog.dismiss();
                final PayStatus payStatus = parseJsonValue(
                        object, new TypeToken<PayStatus>() {
                        });

                if (payStatus == null) {
                    longToast("购买课程失败！！");
                    return;
                }
                app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "AlipayFragment");
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, "支付班级-" + mTitle);
                        startIntent.putExtra("payurl", payStatus.payUrl);
                    }
                });
            }
        });
    }
}
