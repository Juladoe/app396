package com.edusoho.kuozhi.v3.ui;


import android.os.Bundle;
import android.text.TextUtils;

/**
 * Created by howzhi on 15/9/18.
 */
public class ServiceProviderActivity extends FragmentPageActivity {

    public static final String ARTICLE = "Article";
    public static final String COURSE = "Course";
    public static String SERVICE_NAME;

    @Override
    protected void loadFragment(String fragmentName, Bundle bundle) {
        SERVICE_NAME = getServiceName(fragmentName);
        super.loadFragment(fragmentName, bundle);
    }

    private String getServiceName(String fragmentName) {

        if (TextUtils.isEmpty(fragmentName)) {
            return null;
        }
        int serviceNameStart = fragmentName.lastIndexOf("Fragment");
        return fragmentName.substring(0, serviceNameStart);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SERVICE_NAME = null;
    }
}
