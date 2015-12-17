package com.edusoho.kuozhi.v3.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.internal.widget.DecorToolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.FriendFragmentAdapter;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.PromiseCallback;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.bal.UserRole;
import com.edusoho.kuozhi.v3.model.provider.FriendProvider;
import com.edusoho.kuozhi.v3.model.result.FriendResult;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.ui.friend.CharacterParser;
import com.edusoho.kuozhi.v3.ui.friend.FriendComparator;
import com.edusoho.kuozhi.v3.ui.friend.FriendNewsActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.Promise;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.view.EduSohoAnimWrap;
import com.edusoho.kuozhi.v3.view.SideBar;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by JesseHuang on 15/4/26.
 */
public class FriendFragment extends BaseFragment {

    public static int REQUEST_CODE = 1;
    public static boolean isNews = false;
    private ListView mFriendList;
    private View mFootView;
    private TextView mFriendCount;
    private FriendFragmentAdapter mFriendAdapter;
    private SideBar mSidebar;
    private CharacterParser characterParser;
    private FriendComparator friendComparator;
    private TextView dialog;
    private FrameLayout mLoading;

    private FriendProvider mFriendProvider;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_friends);
        mActivity.setTitle(getString(R.string.title_friends));
        mFriendProvider = new FriendProvider(mContext);
        setHasOptionsMenu(true);
        isNews = app.config.newVerifiedNotify;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        characterParser = CharacterParser.getInstance();
        friendComparator = new FriendComparator();

        mLoading = (FrameLayout) view.findViewById(R.id.friend_fragment_loading);
        mFootView = mActivity.getLayoutInflater().inflate(R.layout.friend_list_foot, null);
        mFriendList = (ListView) mContainerView.findViewById(R.id.friends_list);
        mSidebar = (SideBar) mContainerView.findViewById(R.id.sidebar);
        dialog = (TextView) mContainerView.findViewById(R.id.dialog);
        mSidebar.setTextView(dialog);
        mSidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChangedListener(String string) {
                int postion = mFriendAdapter.getPositionForSection(string.charAt(0));
                if (postion != -1) {
                    mFriendList.setSelection(postion + 1);
                }
            }
        });
        mFriendAdapter = new FriendFragmentAdapter(mContext, app);
        mFriendAdapter.setHeadView(getFriendListHeadView());
        mFriendList.addFooterView(mFootView, null, false);
        mFriendList.setAdapter(mFriendAdapter);
        mFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Friend friend = (Friend) parent.getAdapter().getItem(position);
                app.mEngine.runPluginFromFragmentForResultWithCallback("ChatActivity",getInstance(), REQUEST_CODE, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(ChatActivity.FROM_ID, friend.id);
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, friend.nickname);
                        startIntent.putExtra(Const.NEWS_TYPE, CommonUtil.inArray(UserRole.ROLE_TEACHER.name(), friend.roles) ?
                                PushUtil.ChatUserType.TEACHER : PushUtil.ChatUserType.FRIEND);
                        startIntent.putExtra(ChatActivity.HEAD_IMAGE_URL, friend.mediumAvatar);
                    }
                });
            }
        });

        mFriendCount = (TextView) mFootView.findViewById(R.id.friends_count);
        initViewData();
    }

    public FriendFragment getInstance(){
        return this;
    }

    private View getFriendListHeadView() {

        View headView = LayoutInflater.from(mContext).inflate(R.layout.item_type_friend_head, null);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.search_friend_btn) {
                    showSearchDialog();
                }
                if (i == R.id.discussion_group) {
                    app.mEngine.runNormalPlugin("GroupListActivity", mActivity, null);
                }
                if (i == R.id.service) {
                    app.mEngine.runNormalPlugin("ServiceListActivity", mActivity, null);

                }
            }
        };
        headView.findViewById(R.id.search_friend_btn).setOnClickListener(onClickListener);
        headView.findViewById(R.id.discussion_group).setOnClickListener(onClickListener);
        headView.findViewById(R.id.service).setOnClickListener(onClickListener);

        return headView;
    }

    private void showSearchDialog() {
        final View toolbarView = getToolbarView();
        ObjectAnimator animator = ObjectAnimator.ofInt(new EduSohoAnimWrap(toolbarView), "height", toolbarView.getHeight(), 0);
        toolbarView.setTag(toolbarView.getHeight());
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                SearchDialogFragment searchDialogFragment = new SearchDialogFragment();
                searchDialogFragment.show(getChildFragmentManager(), "searchDialog");
                searchDialogFragment.setToolBarView(toolbarView);
            }
        });

        animator.start();
    }

    private void initViewData() {
        mLoading.setVisibility(View.VISIBLE);
        if (!app.getNetIsConnect()) {
            mLoading.setVisibility(View.GONE);
            Toast.makeText(mContext, "无网络连接", Toast.LENGTH_LONG).show();
        } else {
            mFriendAdapter.clearList();
        }
        loadFriend().then(new PromiseCallback() {
            @Override
            public Promise invoke(Object obj) {
                mLoading.setVisibility(View.GONE);
                return null;
            }
        });
    }

    public Promise loadSchoolApps() {
        mFriendAdapter.clearList();
        RequestUrl requestUrl = app.bindNewUrl(Const.SCHOOL_APPS, true);
        StringBuffer stringBuffer = new StringBuffer(requestUrl.url);
        requestUrl.url = stringBuffer.toString();

        final Promise promise = new Promise();
        mFriendProvider.getSchoolApps(requestUrl)
                .success(new NormalCallback<List<SchoolApp>>() {
                    @Override
                    public void success(List<SchoolApp> schoolAppResult) {
                        if (schoolAppResult.size() != 0) {
                            mFriendAdapter.addSchoolList(schoolAppResult);
                        }
                        promise.resolve(schoolAppResult);
                    }
                });

        return promise;
    }

    public Promise loadFriend() {
        RequestUrl requestUrl = app.bindNewUrl(Const.MY_FRIEND, true);
        StringBuffer stringBuffer = new StringBuffer(requestUrl.url);
        stringBuffer.append("?start=0&limit=10000/");
        requestUrl.url = stringBuffer.toString();

        final Promise promise = new Promise();
        mFriendProvider.getFriend(requestUrl)
                .success(new NormalCallback<FriendResult>() {
                    @Override
                    public void success(FriendResult friendResult) {
                        if (friendResult.data.length != 0) {
                            List<Friend> list = Arrays.asList(friendResult.data);
                            setChar(list);
                            Collections.sort(list, friendComparator);
                            mFriendAdapter.clearList();
                            mFriendAdapter.addFriendList(list);
                        }
                        setFriendsCount(friendResult.data.length + "");
                        promise.resolve(friendResult);
                    }
                });

        return promise;
    }

    public void setChar(List<Friend> list) {
        for (Friend friend : list) {
            String pinyin = characterParser.getSelling(friend.nickname);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                friend.setSortLetters(sortString.toUpperCase());
            } else {
                friend.setSortLetters("#");
            }
        }
    }

    public void setFriendsCount(String count) {
        mFriendCount.setText(count + "位好友");
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
    public void onPrepareOptionsMenu(Menu menu) {
        if (isNews == true) {
            menu.findItem(R.id.friends_news).setIcon(R.drawable.icon_menu_notification_news);
        } else {
            menu.findItem(R.id.friends_news).setIcon(R.drawable.icon_menu_notification);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.friends_news) {
            isNews = false;
            app.config.newVerifiedNotify = false;
            app.saveConfig();
            item.setIcon(R.drawable.icon_menu_notification);
            mActivity.supportInvalidateOptionsMenu();
            app.mEngine.runNormalPlugin("FriendNewsActivity", mActivity, null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (messageType.type.equals(Const.LOGIN_SUCCESS)) {
            initViewData();
        }
        if (messageType.type.equals(Const.REFRESH_FRIEND_LIST)) {
            initViewData();
        }
        if (messageType.type.equals(Const.THIRD_PARTY_LOGIN_SUCCESS)) {
            initViewData();
        }
        if (messageType.code == Const.NEW_FANS) {
            isNews = true;
            FriendNewsActivity.isNews = true;
            mActivity.supportInvalidateOptionsMenu();
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = {new MessageType(Const.LOGIN_SUCCESS)
                , new MessageType(Const.REFRESH_FRIEND_LIST)
                , new MessageType(Const.NEW_FANS, source)
                , new MessageType(Const.THIRD_PARTY_LOGIN_SUCCESS)};
        return messageTypes;
    }

    public View getToolbarView() {

        View view = null;
        try {
            ActionBar actionBar = mActivity.getSupportActionBar();
            Field toolbarField = actionBar.getClass().getDeclaredField("mDecorToolbar");
            toolbarField.setAccessible(true);
            DecorToolbar toolbar = (DecorToolbar) toolbarField.get(actionBar);
            view = toolbar.getViewGroup();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == ChatActivity.RESULT_CODE_UPDATE){
            loadFriend().then(new PromiseCallback() {
                @Override
                public Promise invoke(Object obj) {
                    mFriendAdapter.notifyDataSetChanged();
                    return null;
                }
            });

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
