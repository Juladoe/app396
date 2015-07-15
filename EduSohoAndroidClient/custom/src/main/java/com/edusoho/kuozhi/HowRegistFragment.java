package com.edusoho.kuozhi;

import com.edusoho.kuozhi.model.TokenResult;
import com.edusoho.kuozhi.ui.fragment.MineFragment;
import com.edusoho.kuozhi.ui.fragment.RegistFragment;
import com.edusoho.kuozhi.ui.fragment.SchoolRoomFragment;

/**
 * Created by howzhi on 15/7/15.
 */
public class HowRegistFragment extends RegistFragment {

    @Override
    protected void saveUserToken(TokenResult tokenResult) {
        app.saveToken(tokenResult);
        mActivity.setResult(RegistFragment.OK);
        mActivity.finish();
        app.sendMsgToTarget(HowMineFragment.REFRESH, null, HowMineFragment.class);
        app.sendMsgToTarget(SchoolRoomFragment.REFRESH, null, SchoolRoomFragment.class);
    }
}
