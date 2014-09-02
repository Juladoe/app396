package com.edusoho.kuozhi.ui.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.core.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.widget.ScrollWidget;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by howzhi on 14-8-26.
 */
public class CourseDetailsActivity extends ActionBarBaseActivity
        implements MessageEngine.MessageCallback {

    public static final String TITLE = "title";
    public static final String COURSE_ID = "courseID";
    public static final String TAG = "CourseDetailsActivity";
    public static final int SHOWHEAD = 0001;
    public static final int HIDEHEAD = 0002;
    public static final int SET_LEARN_BTN = 0003;

    private String mTitle;
    private String mCourseId;

    private View mCoursePic;
    private TextView mHeadView;
    private Button mVipLearnBtn;
    private Button mLearnBtn;
    private int mVipLevelId;
    private double mPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_details);
        initView();
        app.registMsgSource(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.course_details_menu_shard) {
            Log.d(null, "shard->");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void invoke(WidgetMessage message) {
        int type = message.type.code;
        switch (type) {
            case SHOWHEAD:
                mHeadView.setVisibility(View.VISIBLE);
                mHeadView.setText(message.data.getString("text"));
                break;
            case HIDEHEAD:
                mHeadView.setVisibility(View.GONE);
                break;
            case SET_LEARN_BTN:
                Bundle data = message.data;
                mVipLevelId = data.getInt("vipLevelId", 0);
                mPrice = data.getDouble("price", 0);
                String vipLevelName = data.getString("vipLevelName");

                if (mPrice <= 0) {
                    mLearnBtn.setText("加入学习");
                }
                if (mVipLevelId != 0) {
                    mVipLearnBtn.setText(vipLevelName + "免费学");
                } else {
                    mVipLearnBtn.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(SHOWHEAD, source)
        };
        return messageTypes;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initView()
    {
        Intent data = getIntent();
        if (data != null && data.hasExtra(TITLE)) {
            mTitle = data.getStringExtra(TITLE);
            mCourseId = data.getStringExtra(COURSE_ID);
        }

        setBackMode(BACK, mTitle);
        mVipLearnBtn = (Button) findViewById(R.id.course_details_vip_learnbtn);
        mLearnBtn = (Button) findViewById(R.id.course_details_learnbtn);
        mHeadView = (TextView) findViewById(R.id.course_details_head_label);
        mCoursePic = findViewById(R.id.course_details_pic);
        loadCoureDetailsFragment();
    }

    private void loadCoureDetailsFragment()
    {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment = app.mEngine.runPluginWithFragment(
                "CourseDetailsFragment", mActivity, new PluginFragmentCallback() {
            @Override
            public void setArguments(Bundle bundle) {
                bundle.putString(COURSE_ID, mCourseId);
                bundle.putString(TITLE, mTitle);
            }
        });
        fragmentTransaction.add(R.id.course_details_fragment, fragment);
        fragmentTransaction.commit();
    }
}
