package com.edusoho.kowzhi.view;

import android.content.Context;
import android.widget.TextView;

import com.edusoho.kowzhi.EdusohoApp;

/**
 * Created by howzhi on 14-5-12.
 */
public class EduSohoPopBtn extends TextView{

    private Context mContext;

    public EduSohoPopBtn(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public EduSohoPopBtn(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView()
    {
        String typeTag = getTag().toString();
        if ("left".equals(typeTag)) {
            setBackgroundResource(EdusohoApp.popLeftBtnSel);
        } else if ("right".equals(typeTag)) {
            setBackgroundResource(EdusohoApp.popRightBtnSel);
        }
    }
}
