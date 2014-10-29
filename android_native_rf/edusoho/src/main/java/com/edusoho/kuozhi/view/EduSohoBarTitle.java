package com.edusoho.kuozhi.view;

import android.content.Context;
import android.widget.TextView;

/**
 * Created by howzhi on 14-5-11.
 */
public class EduSohoBarTitle extends TextView implements android.support.v7.view.CollapsibleActionView {

    public EduSohoBarTitle(Context context) {
        super(context);
    }

    public EduSohoBarTitle(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onActionViewCollapsed() {

    }

    @Override
    public void onActionViewExpanded() {

    }
}
