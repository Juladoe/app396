package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ErrorAdapter;
import com.edusoho.kuozhi.adapter.FollowAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
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
    private View mLoadView;
    public static final String FOLLOW_USER = "follow_user";
    public static final String FOLLOWING = "following";
    public static final String FOLLOWER = "follower";
    public static final String OTHER = "other";
    public static final String FOLLOW_TYPE = "follow_type";
    public static final int FOLLOW_REFRESH = 0x01;
    public FollowAdapter<User> mFollowAdapter;

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
        mLoadView = view.findViewById(R.id.load_layout);
        mFollowList.setMode(PullToRefreshBase.Mode.BOTH);
        mFollowAdapter = new FollowAdapter<User>(mContext, R.layout.follow_item, mActivity);
        mFollowList.setAdapter(mFollowAdapter);
        if (mType.equals(FollowFragment.FOLLOWING)) {
            mFollowList.setEmptyText(new String[]{"暂时没有关注任何人"});
        } else {
            mFollowList.setEmptyText(new String[]{"暂时没有任何粉丝"});
        }
        mFollowList.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                loadFollows(mStart, true);
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                loadFollows(0, false);
            }
        });
        mFollowList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final User user = (User) parent.getAdapter().getItem(position);
//                final Bundle bundle = new Bundle();
//                bundle.putString(Const.ACTIONBAR_TITLE, user.nickname);
//                bundle.putString(FragmentPageActivity.FRAGMENT, "ProfileFragment");
//                bundle.putSerializable(ProfileFragment.FOLLOW_USER, user);
//                bundle.putString(FollowFragment.FOLLOW_TYPE, mType);
                app.mEngine.runNormalPluginForResult("FragmentPageActivity", mActivity, FOLLOW_REFRESH, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, user.nickname);
                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "ProfileFragment");
                        startIntent.putExtra(ProfileFragment.FOLLOW_USER, user);
                        startIntent.putExtra(FollowFragment.FOLLOW_TYPE, mType);
                    }
                });
            }
        });
        loadFollows(0, false);
    }

    private void loadFollows(final int start, final boolean isRefresh) {
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
        params.put("start", start + "");
        params.put("limit", Const.LIMIT + "");
        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mFollowList.onRefreshComplete();
                mLoadView.setVisibility(View.GONE);
                ArrayList<User> userList = mActivity.parseJsonValue(object, new TypeToken<ArrayList<User>>() {
                });
                if (userList == null) {
                    return;
                }
                if (isRefresh) {
                    mFollowAdapter.addItems(userList);
                } else {
                    mFollowList.pushData(userList);
                }
                mStart = mFollowList.getAdapter().getCount();
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                if (ajaxStatus.getCode() != 200) {
                    mLoadView.setVisibility(View.GONE);
                    mFollowList.setMode(PullToRefreshBase.Mode.BOTH);
                    ErrorAdapter<String> errorAdapter = new ErrorAdapter<String>(mContext, new String[]{"加载失败，请点击重试"},
                            R.layout.list_error_layout, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFollowList.setMode(PullToRefreshBase.Mode.BOTH);
                            loadFollows(0, true);
                        }
                    });
                    mFollowList.setAdapter(errorAdapter);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadFollows(0, false);
    }

    @Override
    public String getTitle() {
        return mTitle;
    }
}
