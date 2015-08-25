package com.edusoho.test.friend;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.edusoho.kuozhi.v3.adapter.FriendFragmentAdapter;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.provider.FriendProvider;
import com.edusoho.kuozhi.v3.model.provider.ProviderListener;
import com.edusoho.kuozhi.v3.model.result.FriendResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.fragment.FriendFragment;
import com.edusoho.test.base.BaseFragmentTestCase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by howzhi on 15/8/24.
 */

@LargeTest
public class FriendFragmentTest extends BaseFragmentTestCase<FriendFragmentTest.TestFriendFragment> {

    public FriendFragmentTest()
    {
        super(TestFriendFragment.class);
    }

    @UiThreadTest
    public void testGetFragment() {
        mFragment = getFragment();
        assertNotNull(mFragment);
    }

    @UiThreadTest
    public void testViewLayout() {
        Activity activity = getActivity();
        mInstrumentation.callActivityOnResume(activity);

        mFragment = getFragment();
        View rootView = mFragment.getView();
        ListView friendList = (ListView) rootView.findViewById(com.edusoho.kuozhi.R.id.friends_list);
        assertNotNull(friendList);

        HeaderViewListAdapter headAdapter = (HeaderViewListAdapter)friendList.getAdapter();
        ListAdapter adapter = headAdapter.getWrappedAdapter();
        assertNotNull(adapter);

        assertEquals(2, adapter.getCount());
        SchoolApp app = (SchoolApp) adapter.getItem(1);
        assertNotNull(app);
        assertEquals("edusoho", app.name);
    }

    public static class TestFriendFragment extends FriendFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setFriendProvider();
        }

        private void setFriendProvider() {
            try {
                Field field = FriendFragment.class.getDeclaredField("mFriendProvider");
                field.setAccessible(true);
                field.set(this, new FriendProvider(mContext) {

                    @Override
                    public ProviderListener getSchoolApps(RequestUrl requestUrl) {
                        final ProviderListener<List<SchoolApp>> responseListener = new ProviderListener<List<SchoolApp>>(){};

                        ArrayList<SchoolApp> list = new ArrayList<SchoolApp>();
                        SchoolApp app = new SchoolApp();
                        app.name = "edusoho";
                        app.avatar = "avatar";
                        app.id = 1;

                        list.add(app);
                        responseListener.onResponse(list);
                        return responseListener;
                    }

                    @Override
                    public ProviderListener getFriend(RequestUrl requestUrl) {
                        final ProviderListener<FriendResult> responseListener = new ProviderListener<FriendResult>(){};
                        responseListener.onResponse(new FriendResult());
                        return responseListener;
                    }
                });
            }catch (Exception e) {

            }
        }
    }
}
