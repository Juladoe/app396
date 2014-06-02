package com.edusohoapp.app.view;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.RadioButton;
import android.widget.TextView;

import com.edusohoapp.app.EdusohoApp;

/**
 * Created by howzhi on 14-5-12.
 */
public class EduSohoTabRadio extends RadioButton{

    private Context mContext;

    public EduSohoTabRadio(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public EduSohoTabRadio(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView()
    {
        String typeTag = getTag().toString();
        if ("left".equals(typeTag)) {
            setBackgroundResource(EdusohoApp.tabLeftBtnSel);
        } else if ("right".equals(typeTag)) {
            setBackgroundResource(EdusohoApp.tabRightBtnSel);
        }
    }
}
