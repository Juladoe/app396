package com.edusoho.kuozhi.v3.view;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TextView;

<<<<<<< HEAD
/**
 * Created by howzhi on 14-5-12.
 */

=======
<<<<<<< HEAD
<<<<<<< HEAD
=======
/**
 * Created by howzhi on 14-5-12.
 */
>>>>>>> 4ecd4ad251d95ccba685b18f74f2d837882d72fc
=======
>>>>>>> 2a9b512c31a1e3073222af6bca5f4e7ff9d0013d
>>>>>>> feature/17898-course-frame
public class EduSohoNewIconView extends TextView {

    private Context mContext;

    public EduSohoNewIconView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public EduSohoNewIconView(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        Typeface iconfont = Typeface.createFromAsset(mContext.getAssets(), "iconfont_new.ttf");
        setTypeface(iconfont);
        setGravity(Gravity.CENTER);
    }
}
