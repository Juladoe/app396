package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.edusoho.listener.NormalCallback;
import com.edusoho.listener.ResultCallback;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorInflater;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.Collections;

/**
 * Created by howzhi on 14-8-22.
 */
public class ButtonWidget extends Button {
    private Context mContext;
    private NormalCallback mClickCallback;

    public ButtonWidget(Context context) {
        super(context);
        mContext = context;
    }

    public ButtonWidget(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    private void initView(android.util.AttributeSet attrs)
    {

    }

    public void setActionMode(boolean actionMode)
    {
        if (actionMode) {
            setEnabled(true);
            setText("注  册");
            return;
        }

        setEnabled(false);
        setText("提交中...");
    }

    public void showLoading()
    {
        ObjectAnimator startAnim = (ObjectAnimator) AnimatorInflater.loadAnimator(mContext, R.anim.button_widget_bg);
        startAnim.setTarget(this);
        startAnim.setEvaluator(new ArgbEvaluator());
        startAnim.start();
    }
}
