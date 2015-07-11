package com.edusoho.kuozhi;

import com.edusoho.kuozhi.ui.DefaultPageActivity;

/**
 * Created by howzhi on 15/1/22.
 */
public class HowzhiDefaultPageActivity extends DefaultPageActivity {

    @Override
    protected void initNavSelected() {
        if (app.token == null || "".equals(app.token)) {
            mSelectBtn = R.id.nav_schoolroom_btn;
        } else {
            mSelectBtn = R.id.nav_me_btn;
        }

        selectNavBtn(mSelectBtn);
    }
}