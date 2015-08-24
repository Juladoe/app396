package com.edusoho.test;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.ContextThemeWrapper;
import android.widget.ListView;

import com.edusoho.kuozhi.v3.ui.fragment.FriendFragment;
import com.edusoho.kuozhi.v3.view.SideBar;


/**
 * Created by Melomelon on 2015/8/21.
 */
public class FriendFragmentTestCase extends ActivityInstrumentationTestCase2<FragmentTestActivity> {

    private FriendFragment mFragment;
    private FriendFragment mFriendFragment;
    private Intent mLaunchIntent;
    private Instrumentation mInstrumentation;

    public FriendFragmentTestCase() {
        super(FragmentTestActivity.class);
    }

    @UiThreadTest
    public void testGetFragment(){
        mFriendFragment = getFragment();
        assertNotNull(mFriendFragment);
    }

    @UiThreadTest
    public void testInitUI(){
        mFriendFragment = getFragment();
        ListView listView = (ListView) mFriendFragment.getActivity().findViewById(R.id.friends_list);
        assertNotNull(listView);
        SideBar sideBar = (SideBar) mFriendFragment.getActivity().findViewById(R.id.sidebar);
        assertNotNull(sideBar);


    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mInstrumentation = getInstrumentation();
        mLaunchIntent = new Intent(mInstrumentation.getTargetContext(),
                FragmentTestActivity.class);
    }

    public FriendFragment getFragment() {
        if (mFragment == null) {
            FragmentTestActivity activity = super.getActivity();
            mFragment = (FriendFragment)activity.loadFragment(FriendFragment.class.getName(), mLaunchIntent.getExtras());

            mInstrumentation.callActivityOnStart(getActivity());
            mInstrumentation.callActivityOnResume(getActivity());
        }
        return mFragment;
    }
}
