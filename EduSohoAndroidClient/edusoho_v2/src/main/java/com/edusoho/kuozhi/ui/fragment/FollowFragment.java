package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.FollowAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

import library.PullToRefreshBase;

/**
 * Created by JesseHuang on 15/1/13.
 */
public class FollowFragment extends BaseFragment {
    private RefreshListWidget mFollowList;
    public static final String FOLLOW_USER = "follow_user";
    public static final String FOLLOWING = "following";
    public static final String FOLLOWER = "follower";
    public static final String FOLLOW_TYPE = "follow_type";

    /**
     * Previous页面类型
     */
    private static String mType;
    private int mStart = 0;
    private User mFollowUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.follow_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        Bundle bundle = mActivity.getIntent().getExtras();
        mFollowUser = (User) bundle.getSerializable(FOLLOW_USER);
        mType = bundle.getString(FOLLOW_TYPE);
        mFollowList = (RefreshListWidget) view.findViewById(R.id.lv_follow);
        mFollowList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mFollowList.setAdapter(new FollowAdapter<User>(mContext, R.layout.follow_item, mActivity));
        mFollowList.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        mFollowList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) parent.getAdapter().getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString(Const.ACTIONBAR_TITLE, user.nickname);
                bundle.putString(FragmentPageActivity.FRAGMENT, "ProfileFragment");
                bundle.putSerializable(ProfileFragment.FOLLOW_USER, user);
                bundle.putString(FollowFragment.FOLLOW_TYPE, mType);
                app.mEngine.runNormalPluginWithBundle("FragmentPageActivity", mActivity, bundle);
            }
        });
        loadFollows();
    }

    private void loadFollows() {
        String url;
        if (mType.equals(FOLLOWING)) {
            url = Const.FOLLOWING;
        } else {
            url = Const.FOLLOWER;
        }
        RequestUrl requestUrl = mActivity.app.bindUrl(url, false);
        HashMap<String, String> params = requestUrl.getParams();
        if (mFollowUser != null) {
            params.put("userId", mFollowUser.id + "");
        } else {
            params.put("userId", app.loginUser.id + "");
        }
        params.put("start", mStart + "");
        params.put("limit", Const.LIMIT + "");
        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mFollowList.onRefreshComplete();
                ArrayList<User> userList = mActivity.parseJsonValue(object, new TypeToken<ArrayList<User>>() {
                });
                if (userList == null) {
                    return;
                }
                mFollowList.pushData(userList);
                mFollowList.setStart(mStart + Const.LIMIT);
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                super.error(url, ajaxStatus);
            }
        });

    }

    @Override
    public String getTitle() {
        return mTitle;
    }
}
