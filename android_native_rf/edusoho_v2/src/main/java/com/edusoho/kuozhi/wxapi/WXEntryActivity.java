package com.edusoho.kuozhi.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * Created by howzhi on 14-10-14.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "分享成功!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        String result = "发送成功";

        switch (resp.errCode)
        {
            case BaseResp.ErrCode.ERR_OK:
                result = "发送成功！";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送失败！";
                break;
            default:
                break;
        }

        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {

            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
    }
}
