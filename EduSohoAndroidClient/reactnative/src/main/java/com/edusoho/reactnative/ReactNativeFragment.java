package com.edusoho.reactnative;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;

/**
 * Created by howzhi on 15/10/9.
 */
public class ReactNativeFragment extends Fragment implements DefaultHardwareBackBtnHandler {

    private ReactInstanceManager mReactInstanceManager;
    private ReactRootView mReactRootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mReactRootView = new ReactRootView(getActivity());

        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getActivity().getApplication())
                .setBundleAssetName("index.android.bundle")
                .setJSMainModuleName("UIExplorerApp.android")
                .addPackage(new MainReactPackage())
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();

        mReactRootView.startReactApplication(mReactInstanceManager, "UIExplorerApp", null);
        return mReactRootView;
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        getActivity().onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onResume(this);
        }
    }
}
