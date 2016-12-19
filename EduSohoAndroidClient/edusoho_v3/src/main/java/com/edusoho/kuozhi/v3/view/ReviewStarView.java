package com.edusoho.kuozhi.v3.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhang on 2016/12/13.
 */

public class ReviewStarView extends LinearLayout {
    public ReviewStarView(Context context) {
        super(context);
        init();
    }

    public ReviewStarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReviewStarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int rating = 0;
    private List<EduSohoNewIconView> stars = new ArrayList<>();

    public void setRating(int rating) {
        this.rating = rating;
        for (int i = 0; i < rating; i++) {
            stars.get(i).setTextColor(getResources().getColor(R.color.secondary2_color));
        }
        for (int i = rating; i < 5; i++) {
            stars.get(i).setTextColor(getResources().getColor(R.color.disabled_hint_color));
        }
    }

    private void init() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, AppUtil.dp2px(getContext(), 2), 0);
        for (int i = 0; i < 5; i++) {
            EduSohoNewIconView view = new EduSohoNewIconView(getContext());
            view.setTextSize(13);
            view.setText(R.string.new_font_star_grade);
            view.setTextColor(getResources().getColor(R.color.disabled_hint_color));
            view.setLayoutParams(params);
            stars.add(view);
            addView(view);
        }
    }
}
