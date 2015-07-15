package com.edusoho.kuozhi;

import com.edusoho.kuozhi.model.TokenResult;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.fragment.LoginFragment;
import com.edusoho.kuozhi.ui.fragment.SchoolRoomFragment;
import com.edusoho.kuozhi.util.Const;

/**
 * Created by howzhi on 14-8-21.
 */
public class HowzhiLoginFragment extends LoginFragment {

    protected void saveUserToken(TokenResult result) {
        app.saveToken(result);
        mActivity.setResult(LoginActivity.OK);
        mActivity.finish();
        app.sendMessage(Const.LOGING_SUCCESS, null);

        app.sendMsgToTarget(HowMineFragment.REFRESH, null, HowMineFragment.class);
        app.sendMsgToTarget(SchoolRoomFragment.REFRESH, null, SchoolRoomFragment.class);
    }
}
