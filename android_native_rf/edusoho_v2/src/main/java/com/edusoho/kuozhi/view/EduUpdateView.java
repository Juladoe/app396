package com.edusoho.kuozhi.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import java.util.ArrayList;
import java.util.HashMap;

import cn.trinea.android.common.util.ImageUtils;

/**
 * Created by howzhi on 14-9-5.
 */
public class EduUpdateView extends TextView {

    private boolean mIsUpdate;
    private int mIcon;
    private HashMap<String, Object> notifyTypes;

    private Context mContext;

    public EduUpdateView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public EduUpdateView(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init()
    {
        notifyTypes = new HashMap<String, Object>();
    }

    public void setUpdate(boolean isUpdate)
    {
        mIsUpdate = isUpdate;
        invalidate();
    }

    public boolean getUpdateMode()
    {
        return mIsUpdate;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsUpdate) {
            addTip(canvas);
        }
    }

    public void addNotifyType(String type)
    {
        notifyTypes.put(type, null);
    }

    public void addNotifyTypes(String[] types)
    {
        for (String type : types) {
            addNotifyType(type);
        }
    }

    public boolean hasNotify(String type){
        return notifyTypes.containsKey(type);
    }

    private void addTip(Canvas canvas)
    {
        int width = getWidth();
        int height = getHeight();
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        if (mIcon == 0) {
            canvas.drawCircle((width * 0.9f), height * 0.5f, 8, paint);
        }
    }

    public void setUpdateIcon(int res)
    {
        mIcon = res;
        Drawable drawable = getResources().getDrawable(mIcon);
        setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    public void clearUpdateIcon()
    {
        mIcon = 0;
        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }
}
