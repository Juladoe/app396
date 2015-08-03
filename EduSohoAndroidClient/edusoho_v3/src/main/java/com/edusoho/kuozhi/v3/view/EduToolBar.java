package com.edusoho.kuozhi.v3.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

/**
 * Created by JesseHuang on 15/5/5.
 */
public class EduToolBar extends Toolbar {
    private TextView tvCenterTitle;
    private RelativeLayout mLayout;
    private ImageView mImageView;

    private String centerTitle;
    private int centerTitleSize;
    private int centerTitleColor;

    private boolean rotationFlag = true;


    private final static int TITLE_ID = 0x001;

    private Context mContext;

    public EduToolBar(Context context) {
        super(context);
    }

    public EduToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EduToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mContext = getContext();
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.EduToolBar);
        centerTitle = typedArray.getString(R.styleable.EduToolBar_centerTitle);
        centerTitleSize = (int) typedArray.getDimension(R.styleable.EduToolBar_centerTitleSize, 14);
        centerTitleColor = typedArray.getColor(R.styleable.EduToolBar_centerTitleColor, Color.WHITE);
        this.setNavigationIcon(R.drawable.buy_vip_icon_normal);
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);
    }

    @Override
    protected void onFinishInflate() {
        findViews();
    }

    private void findViews() {
        mLayout = new RelativeLayout(mContext);

        View view = LayoutInflater.from(mContext).inflate(R.layout.toolbar_custom, null);

        addView(view, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        tvCenterTitle = (TextView) findViewById(R.id.centerTitle);
        //tvCenterTitle.setOnClickListener(new TitleClickListener());

        //mImageView = (ImageView) findViewById(R.id.titleArrow);

        tvCenterTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, centerTitleSize);
        tvCenterTitle.setTextColor(centerTitleColor);
        tvCenterTitle.setText(centerTitle);
    }

    public void setCenterTitle(CharSequence text) {
        tvCenterTitle.setText(text);
        setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }

    public void setTitleVisibility(int visibility) {
        tvCenterTitle.setVisibility(visibility);
        //mImageView.setVisibility(visibility);
    }

//    private class TitleClickListener implements OnClickListener {
//        @Override
//        public void onClick(View v) {
//            if (rotationFlag) {
//                rotation(mImageView, 0, -180);
//
//            } else {
//                rotation(mImageView, -180, 0);
//            }
//            rotationFlag = !rotationFlag;
//
//        }
//    }
//
//    private void rotation(View view, float start, float end) {
//        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", start, end);
//        objectAnimator.setDuration(180).setInterpolator(new LinearInterpolator());
//        objectAnimator.start();
//    }
}
