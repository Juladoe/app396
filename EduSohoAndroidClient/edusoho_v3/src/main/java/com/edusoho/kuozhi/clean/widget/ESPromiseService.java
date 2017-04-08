package com.edusoho.kuozhi.clean.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.util.AppUtil;

/**
 * Created by JesseHuang on 2017/4/7.
 */

public class ESPromiseService extends LinearLayout {
    private Context mContext;

    private ESIconView mPromiseIcon;
    private TextView mPromiseText;

    public ESPromiseService(Context context, String text) {
        super(context);
        mContext = context;

        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);

        LayoutParams lp = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        mPromiseIcon = new ESIconView(mContext);
        mPromiseIcon.setText(mContext.getString(R.string.course_project_promise_icon));
        mPromiseIcon.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.font_m));
        mPromiseIcon.setTextColor(mContext.getResources().getColor(R.color.primary_color));
        lp.rightMargin = AppUtil.dp2px(mContext, 5);
        mPromiseIcon.setLayoutParams(lp);

        addView(mPromiseIcon);

        mPromiseText = new TextView(mContext);
        mPromiseText.setText(text);
        mPromiseText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.font_s));
        mPromiseText.setTextColor(mContext.getResources().getColor(R.color.secondary_font_color));
        mPromiseText.setLayoutParams(lp);

        addView(mPromiseText);

    }

    public ESPromiseService(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }
}
