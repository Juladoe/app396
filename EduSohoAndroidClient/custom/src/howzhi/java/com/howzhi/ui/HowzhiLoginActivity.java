package com.howzhi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.edusoho.kuozhi.v3.ui.LoginActivity;
import com.edusoho.kuozhi.v3.ui.QrSchoolActivity;
import com.edusoho.kuozhi.v3.view.qr.CaptureActivity;

/**
 * Created by KZ on 2015/12/16.
 */
public class HowzhiLoginActivity extends LoginActivity{

    private Button mQrSearchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HowzhiLoginActivity", "HowzhiLoginActivity");
        int qrSearchBtnId = mContext.getResources().getIdentifier(
                "qr_search_btn", "id", mContext.getPackageName());
        mQrSearchBtn = (Button) findViewById(qrSearchBtnId);
        mQrSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("mQrSearchBtn", "mQrSearchBtn");
                Intent qrIntent = new Intent();
                qrIntent.setClass(HowzhiLoginActivity.this, CaptureActivity.class);
                startActivityForResult(qrIntent, QrSchoolActivity.REQUEST_QR);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QrSchoolActivity.REQUEST_QR && resultCode == QrSchoolActivity.RESULT_QR) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result");
                new QrSchoolActivity.SchoolChangeHandler(mActivity).change(result + "&version=2");
            }
        }
    }
}
