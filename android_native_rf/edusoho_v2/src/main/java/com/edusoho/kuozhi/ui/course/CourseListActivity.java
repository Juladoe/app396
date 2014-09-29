package com.edusoho.kuozhi.ui.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.fragment.CourseFragment;

/**
 * Created by howzhi on 14-8-25.
 */
public class CourseListActivity extends ActionBarBaseActivity {

    public static final String TITLE = "title";
    public static final String CATEGORY_ID = "categoryId";
    public static final String SEARCH_TEXT = "search";
    public static final String TYPE = "type";

    public static final int RECOMMEND= 0001;
    public static final int LASTEST= 0002;

    private String mTitle;
    private int mCategoryId;
    private int mType;
    private String mSearchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_layout);
        initView();
    }

    private void initView()
    {
        Intent data = getIntent();
        if (data != null) {
            mSearchText = data.getStringExtra(SEARCH_TEXT);
            mType = data.getIntExtra(TYPE, RECOMMEND);
            mTitle = data.hasExtra(TITLE) ? data.getStringExtra(TITLE) : "课程列表";
            mCategoryId = data.getIntExtra(CATEGORY_ID, 0);
        }
        setBackMode(BACK, mTitle);

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment = app.mEngine.runPluginWithFragment(
                "CourseFragment", mActivity, new PluginFragmentCallback() {
            @Override
            public void setArguments(Bundle bundle) {
                bundle.putInt(CATEGORY_ID, mCategoryId);
                bundle.putString(SEARCH_TEXT, mSearchText);
                bundle.putString(CourseFragment.TITLE, mTitle);
            }
        });

        fragmentTransaction.add(R.id.fragment_layout, fragment);
        fragmentTransaction.commit();
    }
}
