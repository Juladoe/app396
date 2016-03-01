package com.edusoho.tcmmook.v3.ui;

import com.edusoho.R;
import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;

/** 微信客户端回调activity示例 */
public class CustomDefaultPageActivity extends DefaultPageActivity {

    protected void switchTabButton(int id) {
        selectDownTab(R.id.nav_tab_find);
    }
}