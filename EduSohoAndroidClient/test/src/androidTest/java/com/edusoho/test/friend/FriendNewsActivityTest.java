package com.edusoho.test.friend;

import android.os.Bundle;
import android.test.UiThreadTest;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.edusoho.kuozhi.v3.model.bal.FollowerNotification;
import com.edusoho.kuozhi.v3.model.bal.FollowerNotificationResult;
import com.edusoho.kuozhi.v3.model.provider.FriendProvider;
import com.edusoho.kuozhi.v3.model.provider.ProviderListener;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.friend.FriendNewsActivity;
import com.edusoho.test.base.BaseActivityUnitTestCase;

import java.lang.reflect.Field;

/**
 * Created by Melomelon on 2015/8/26.
 */
public class FriendNewsActivityTest extends BaseActivityUnitTestCase<FriendNewsActivity> {

    public FriendNewsActivityTest() {
        super(FriendNewsActivity.class);
    }

    @UiThreadTest
    public void testGetActivity() {
        FriendNewsActivity mFiendNewsActivity = getActivity();
        assertNotNull(mFiendNewsActivity);
    }

    @UiThreadTest
    public void testListView() {
        FriendNewsActivity mFiendNewsActivity = getActivity();
        ListView notificationList = (ListView) mFiendNewsActivity.findViewById(com.edusoho.kuozhi.R.id.friend_news_list);
        assertNotNull(notificationList);
    }

    @UiThreadTest
    public void testListViewItem(){
        FriendNewsActivity mFiendNewsActivity = getActivity();
        ListView notificationList = (ListView) mFiendNewsActivity.findViewById(com.edusoho.kuozhi.R.id.friend_news_list);
        ListAdapter adapter = notificationList.getAdapter();
        assertNotNull(adapter);

        FollowerNotification notification = (FollowerNotification) adapter.getItem(0);
        assertNotNull(notification);
        assertEquals("2015-08-05T09:14:27+00:00", notification.createdTime);
        assertEquals("0",notification.id);
        assertEquals("1",notification.userId);
        assertEquals("follow",notification.content.opration);
        assertEquals("avatar",notification.content.avatar);
        assertEquals("username",notification.content.userName);

        notification.content.userName = "username";
    }


    public class TestFriendNewsActivity extends FriendNewsActivity {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setTestProvider();
        }

        public void setTestProvider() {
            try {
                Field field = mActivity.getClass().getDeclaredField("mFriendProvider");
                field.setAccessible(true);
                field.set(this, new FriendProvider(mContext) {
                    @Override
                    public ProviderListener loadNotifications(RequestUrl requestUrl) {
                        ProviderListener<FollowerNotificationResult> providerListener = new ProviderListener<FollowerNotificationResult>() {
                        };
                        FollowerNotificationResult result = new FollowerNotificationResult();
                        result.data = new FollowerNotification[10];
                        result.total = "10";
                        for (int i = 0; i < 10; i++) {
                            FollowerNotification notification = new FollowerNotification();
                            notification.createdTime = "2015-08-05T09:14:27+00:00";
                            notification.id = i + "";
                            notification.type = "user-follow";
                            notification.userId = i + 1 + "";
                            notification.content.opration = "follow";
                            notification.content.avatar = "avatar";
                            notification.content.userId = i+"";
                            notification.content.userName = "username";
                            result.data[i] = notification;
                        }
                        providerListener.onResponse(result);
                        return providerListener;
                    }

                    @Override
                    public ProviderListener laodRelationships(RequestUrl requestUrl) {
                        ProviderListener<String[]> providerListener = new ProviderListener<String[]>() {
                        };
                        String[] result = new String[10];
                        for (int i = 1; i < 11; i++) {
                            result[i] = "follower";
                        }
                        providerListener.onResponse(result);
                        return providerListener;
                    }
                });

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }
}
