package com.edusoho.kuozhi.view;

import android.view.View;

/**
 * Created by howzhi on 14-7-18.
 */
public class EdusohoAnimWrap {
    private View mTarget;

    public EdusohoAnimWrap(View target)
    {
        this.mTarget = target;
    }

    public int getWidth() {
        return mTarget.getLayoutParams().width;
    }

    public void setWidth(int width) {
        mTarget.getLayoutParams().width = width;
        mTarget.requestLayout();
    }

    public int getHeight() {
        return mTarget.getLayoutParams().height;
    }

    public void setHeight(int height) {
        mTarget.getLayoutParams().height = height;
        mTarget.requestLayout();
    }
}
