package com.edusoho.kuozhi.view.plugin;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import com.edusoho.kuozhi.R;

/**
 * Created by JesseHuang on 15/2/2.
 */
public class EduSohoCompoundButton extends RadioGroup {
    public EduSohoCompoundButton(Context context) {
        super(context);
    }

    public EduSohoCompoundButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        changeButtonsImages();
    }

    private void changeButtonsImages() {
//        int count = super.getChildCount();
//
//        if (count > 1) {
//            super.getChildAt(0).setBackgroundColor(R.color.gray);
//            for (int i = 1; i < count - 1; i++) {
//                super.getChildAt(i).setBackgroundResource(R.drawable.segment_radio_middle);
//            }
//            super.getChildAt(count - 1).setBackgroundResource(R.drawable.segment_radio_right);
//        } else if (count == 1) {
//            super.getChildAt(0).setBackgroundResource(R.color.gray);
//        }
    }
}
