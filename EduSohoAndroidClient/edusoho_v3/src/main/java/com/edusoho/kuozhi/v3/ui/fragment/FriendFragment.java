package com.edusoho.kuozhi.v3.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.FriendFragmentAdapter;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.result.FriendResult;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EduSohoAnimWrap;
import com.edusoho.kuozhi.v3.view.EduToolBar;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by JesseHuang on 15/4/26.
 */
public class FriendFragment extends BaseFragment {

    private ListView mFriendList;
    private View mFootView;
    private TextView mFriendCount;
    private FriendFragmentAdapter mFriendAdapter;
    private EduToolBar mEduToolBar;

    private LoadDialog mLoadDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_friends);
        mActivity.setTitle(getString(R.string.title_friends));
        mEduToolBar = ((DefaultPageActivity) mActivity).getToolBar();
        setHasOptionsMenu(true);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mFootView = mActivity.getLayoutInflater().inflate(R.layout.friend_list_foot, null);
        mFriendList = (ListView) mContainerView.findViewById(R.id.friends_list);
        mFriendAdapter = new FriendFragmentAdapter(mContext, R.layout.item_type_friend_head, app);
        mFriendAdapter.setHeadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.search_friend_btn) {
                    ObjectAnimator animator = ObjectAnimator.ofInt(new EduSohoAnimWrap(mEduToolBar), "height", mEduToolBar.getHeight(), 0);
                    animator.setDuration(300);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            SearchDialogFragment searchDialogFragment = new SearchDialogFragment();
                            searchDialogFragment.show(getChildFragmentManager(), "searchDialog");
                            searchDialogFragment.getToolBar(mEduToolBar);
                        }
                    });

                    animator.start();


//                } else if (i == R.id.item_add_phone_friend) {
//                    app.mEngine.runNormalPlugin("AddPhoneContactActivity", mActivity, null);
//
//                } else if (i == R.id.item_add_lesson_friend) {
//                    ChooseClassDialogFragment chooseClassDialogFragment = new ChooseClassDialogFragment();
//                    Bundle arg = new Bundle();
//                    arg.putInt("Type",ChooseClassDialogFragment.TYPE_LESSEN);
//                    chooseClassDialogFragment.setArguments(arg);
//                    chooseClassDialogFragment.show(getChildFragmentManager(),"chooseClassDialogFragment");
//
//                } else if (i == R.id.item_add_class_friend) {
//                    ChooseClassDialogFragment chooseClassDialogFragment = new ChooseClassDialogFragment();
//                    Bundle arg = new Bundle();
//                    arg.putInt("Type",ChooseClassDialogFragment.TYPE_CLASS);
//                    chooseClassDialogFragment.setArguments(arg);
//                    chooseClassDialogFragment.show(getChildFragmentManager(),"chooseClassDialogFragment");
//                } else if (i == R.id.item_service_qiqiuyu){

                }
            }
        });
        mFriendList.addFooterView(mFootView);
        mFriendList.setAdapter(mFriendAdapter);

        mLoadDialog = LoadDialog.create(mActivity);
        mLoadDialog.setMessage("正在载入数据");
        mLoadDialog.show();
        loadSchoolApps();

        mFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 1) {
                    final Friend friend = (Friend) parent.getAdapter().getItem(position);
                    app.mEngine.runNormalPlugin("ChatActivity", mActivity, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(ChatActivity.FROM_ID, friend.id);
                            startIntent.putExtra(ChatActivity.TITLE, friend.nickname);
                        }
                    });
                } else {
                    final SchoolApp shcoolApp = (SchoolApp) parent.getAdapter().getItem(position);
                    app.mEngine.runNormalPlugin("ChatActivity", mActivity, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(ChatActivity.FROM_ID, shcoolApp.id);
                            startIntent.putExtra(ChatActivity.TITLE, shcoolApp.name);
                        }
                    });
                }

            }
        });

        mFriendCount = (TextView) mFootView.findViewById(R.id.friends_count);
    }

    public void loadSchoolApps() {
        mFriendAdapter.setListViewLayout(R.layout.item_type_school_app);

        mFriendAdapter.clearList();
        if (!app.getNetIsConnect()) {
            mLoadDialog.dismiss();
            Toast.makeText(mContext, "无网络连接", Toast.LENGTH_LONG).show();
        }
        RequestUrl requestUrl = app.bindNewUrl(Const.SCHOOL_APPS, true);
        StringBuffer stringBuffer = new StringBuffer(requestUrl.url);
        requestUrl.url = stringBuffer.toString();
        mActivity.ajaxGet(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SchoolApp[] schoolAppResult = mActivity.parseJsonValue(response, new TypeToken<SchoolApp[]>() {
                });
                if (schoolAppResult.length != 0) {
                    mFriendAdapter.setSchoolListSize(schoolAppResult.length);

                    List<SchoolApp> list = Arrays.asList(schoolAppResult);
                    mFriendAdapter.addSchoolList(list);
                    loadFriend();
                } else {
                    loadFriend();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }

    public void loadFriend() {
        mFriendAdapter.setListViewLayout(R.layout.item_type_friend);

        RequestUrl requestUrl = app.bindNewUrl(Const.MY_FRIEND, true);
        StringBuffer stringBuffer = new StringBuffer(requestUrl.url);
        stringBuffer.append("?start=0&limit=1000/");
        requestUrl.url = stringBuffer.toString();
        mActivity.ajaxGet(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                FriendResult friendResult = mActivity.parseJsonValue(response, new TypeToken<FriendResult>() {
                });
                if (friendResult.data.length != 0) {

                    List<Friend> list = Arrays.asList(friendResult.data);
                    mFriendAdapter.addFriendList(list);
                    mLoadDialog.dismiss();
                } else {
                    mLoadDialog.dismiss();
                }
                setmFriendCount(friendResult.total);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }

    public void setmFriendCount(String count) {
        mFriendCount.setText("共有" + count + "位好友");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            mActivity.setTitle(getString(R.string.title_friends));
        }
        super.onHiddenChanged(hidden);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.friends_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.friends_news) {
            app.mEngine.runNormalPlugin("FriendNewsActivity", mActivity, null);
//        }else if (item.getItemId() == R.id.friends_search) {
//            //TODO 跳转到搜索页面 暂时不做
//            System.out.println("搜索");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (messageType.type.equals(Const.LOGIN_SUCCESS)) {
            loadSchoolApps();
        }
        if (messageType.type.equals(Const.REFRESH_FRIEND_LIST)) {
            loadSchoolApps();
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        MessageType[] messageTypes = {new MessageType(Const.LOGIN_SUCCESS), new MessageType(Const.REFRESH_FRIEND_LIST)};
        return messageTypes;
    }
}
