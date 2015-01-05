package com.edusoho.kuozhi;

import com.edusoho.handler.ClientVersionHandler;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.PopupDialog;

/**
 * Created by howzhi on 14-10-26.
 */
public class CustomClientVersionHandler extends ClientVersionHandler {

    private ActionBarBaseActivity mActivity;

    public CustomClientVersionHandler(ActionBarBaseActivity activity)
    {
        this.mActivity = activity;
    }

    @Override
    public boolean execute(String min, String max, String version) {
        int result = AppUtil.compareVersion(version, min);

        if (result == Const.LOW_VERSIO) {
            PopupDialog popupDialog = PopupDialog.createMuilt(
                    mActivity,
                    "网校提示",
                    "您的客户端版本过低，无法登录该网校，请立即更新至最新版本!",
                    new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            if (button == PopupDialog.OK) {
                                String code = mActivity.getResources().getString(R.string.app_code);
                                String updateUrl = String.format(
                                        "%s%s?code=%s",
                                        mActivity.app.schoolHost,
                                        Const.DOWNLOAD_URL,
                                        code
                                );
                                mActivity.app.startUpdateWebView(updateUrl);
                            }
                        }
                    });

            popupDialog.setOkText("立即下载");
            popupDialog.show();
            return false;
        }

        result = AppUtil.compareVersion(version, max);
        if (result == Const.HEIGHT_VERSIO) {
            PopupDialog.createNormal(
                    mActivity,
                    "网校提示",
                    "网校服务器版本过低，无法继续登录！请重新尝试。"
            ).show();
            return false;
        }

        return true;
    }
}
