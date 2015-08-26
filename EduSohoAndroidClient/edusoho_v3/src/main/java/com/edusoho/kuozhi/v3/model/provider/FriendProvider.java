package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;

import com.edusoho.kuozhi.v3.model.bal.FollowerNotificationResult;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.result.FriendResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by howzhi on 15/8/24.
 */
public class FriendProvider extends ModelProvider {

    public FriendProvider(Context context) {
        super(context);
    }

    public ProviderListener getSchoolApps(RequestUrl requestUrl) {
        ProviderListener<List<SchoolApp>> responseListener = new ProviderListener<List<SchoolApp>>() {
        };
        addRequest(requestUrl, new TypeToken<List<SchoolApp>>() {
        }, responseListener, responseListener);
        return responseListener;
    }

    public ProviderListener getFriend(RequestUrl requestUrl) {
        ProviderListener<FriendResult> responseListener = new ProviderListener<FriendResult>() {
        };
        addRequest(requestUrl, new TypeToken<FriendResult>() {
        }, responseListener, responseListener);
        return responseListener;
    }

    public ProviderListener loadNotifications(RequestUrl requestUrl) {
        ProviderListener<FollowerNotificationResult> responseListener = new ProviderListener<FollowerNotificationResult>() {
        };
        addRequest(requestUrl, new TypeToken<FollowerNotificationResult>() {
        }, responseListener, responseListener);
        return responseListener;
    }

    public ProviderListener laodRelationships(RequestUrl requestUrl) {
        ProviderListener<String[]> responseListener = new ProviderListener<String[]>() {
        };
        addRequest(requestUrl, new TypeToken<String[]>() {
        }, responseListener, responseListener);
        return responseListener;
    }

}
