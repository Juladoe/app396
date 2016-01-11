package com.edusoho.kuozhi.v3.ui.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PromiseCallback;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.model.bal.SearchFriendResult;
import com.edusoho.kuozhi.v3.model.provider.FriendProvider;
import com.edusoho.kuozhi.v3.model.result.FollowResult;
import com.edusoho.kuozhi.v3.model.sys.*;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.Promise;
import com.edusoho.kuozhi.v3.view.EduSohoAnimWrap;
import com.edusoho.kuozhi.v3.view.EduSohoRoundCornerImage;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Melomelon on 2015/6/3.
 */
public class SearchDialogFragment extends DialogFragment {

    private EditText mSearchFrame;
    private TextView mCancel;
    private ActionBarBaseActivity mActivity;
    private Context mContext;
    private EdusohoApp mApp;
    private View view;
    private View mToolbarView;

    private String searchStr;
    private SearchFriendAdapter mAdapter;

    private ListView mList;
    private TextView mNotice;
    private ArrayList<Friend> mResultList;
    private Integer[] friendIds = new Integer[15];
    private int count;

    private FrameLayout mLoading;

    private LayoutInflater mLayoutInflater;
    private FriendProvider mFriendProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (ActionBarBaseActivity) activity;
        mContext = mActivity.getBaseContext();
        mApp = mActivity.app;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.search_dialog, container, false);
        mSearchFrame = (EditText) view.findViewById(R.id.search_dialog_frame);
        mSearchFrame.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        mSearchFrame.setCompoundDrawablePadding(20);

        mCancel = (TextView) view.findViewById(R.id.cancel_search_btn);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mSearchFrame.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    searchStr = mSearchFrame.getText().toString();
                    searchFriend(searchStr);
                    return true;
                }
                return false;
            }
        });
        mSearchFrame.setFocusableInTouchMode(true);
        mSearchFrame.requestFocus();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mSearchFrame, 0);
            }
        }, 300);
        initResultView(view);
        return view;
    }

    private void initResultView(View view) {

        mList = (ListView) view.findViewById(R.id.search_friend_list);
        mNotice = (TextView) view.findViewById(R.id.search_friend_empty);
        mLoading = (FrameLayout) view.findViewById(R.id.search_friend_loading);
        mResultList = new ArrayList<Friend>();
        mAdapter = new SearchFriendAdapter();
        mList.setAdapter(mAdapter);

        mFriendProvider = new FriendProvider(mContext);
    }

    public void closeInput(){
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void clearList(){
        mResultList.clear();
        mAdapter.notifyDataSetChanged();
    }

    public void searchFriend(final String searchStr) {
        if (TextUtils.isEmpty(searchStr)) {
            Toast.makeText(getActivity(), "请输入搜索内容！", Toast.LENGTH_SHORT).show();
            return;
        } else {
            closeInput();
            mLoading.setVisibility(View.VISIBLE);
            if (mResultList != null) {
                clearList();
            }
            loadSearchResult().then(new PromiseCallback() {
                @Override
                public Promise invoke(Object obj) {
                    return getRelationship();
                }
            }).then(new PromiseCallback() {
                @Override
                public Promise invoke(Object obj) {
                    mLoading.setVisibility(View.GONE);
                    return null;
                }
            });
        }
    }


    public void setToolBarView(View view) {
        this.mToolbarView = view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mToolbarView.getVisibility() == View.GONE) {
        }
        int height = (Integer) mToolbarView.getTag();
        ObjectAnimator animator = ObjectAnimator.ofInt(new EduSohoAnimWrap(mToolbarView), "height", 0, height);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(300);
        animator.start();
    }


    public class SearchFriendAdapter extends BaseAdapter {

        public SearchFriendAdapter() {
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mResultList.size();
        }

        @Override
        public Object getItem(int position) {
            return mResultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder holder;
            if (convertView == null) {
                holder = new ItemHolder();
                convertView = mLayoutInflater.inflate(R.layout.add_friend_item, null);
                holder.image = (RoundedImageView) convertView.findViewById(R.id.add_friend_image);
                holder.name = (TextView) convertView.findViewById(R.id.add_friend_name);
                holder.state = (ImageView) convertView.findViewById(R.id.add_friend_state);
                convertView.setTag(holder);
            } else {
                holder = (ItemHolder) convertView.getTag();
            }

            final Friend friend = mResultList.get(position);
            if (friend.mediumAvatar.equals("")) {
                holder.image.setImageResource(R.drawable.default_avatar);
            } else {
                ImageLoader.getInstance().displayImage(friend.mediumAvatar, holder.image, mActivity.app.mOptions);
            }
            holder.name.setText(friend.nickname);
            if (friend.friendship == null) {
                return convertView;
            }
            switch (friend.friendship) {
                case Const.HAVE_ADD_TRUE:
                    holder.state.setImageResource(R.drawable.have_add_friend_true);
                    break;
                case Const.HAVE_ADD_FALSE:
                    holder.state.setImageResource(R.drawable.add_friend_selector);
                    break;
                case Const.HAVE_ADD_WAIT:
                    holder.state.setImageResource(R.drawable.have_add_friend_wait);
                    break;
            }
            holder.state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    followUser(friend);
                }
            });
            if (!(friend.friendship.equals(Const.HAVE_ADD_TRUE) || friend.friendship.equals(Const.HAVE_ADD_WAIT))) {
                holder.state.setClickable(true);
            } else {
                holder.state.setClickable(false);
            }
            return convertView;
        }

        public void addItem(Friend friend) {
            mResultList.add(friend);
            notifyDataSetChanged();
        }

        private class ItemHolder {
            RoundedImageView image;
            TextView name;
            ImageView state;
        }
    }

    public Promise loadSearchResult() {
        final Promise promise = new Promise();
        RequestUrl requestUrl = mApp.bindNewUrl(Const.USERS, false);
        requestUrl.setGetParams(new String[]{"q", searchStr});
        mFriendProvider.getSearchFriend(requestUrl).success(new NormalCallback<SearchFriendResult>() {
            @Override
            public void success(SearchFriendResult searchFriendResult) {
                if ((searchFriendResult.mobile.length == 0) && (searchFriendResult.nickname.length == 0) && (searchFriendResult.qq.length == 0)) {
                    mList.setVisibility(View.GONE);
                    mNotice.setVisibility(View.VISIBLE);
                    mLoading.setVisibility(View.GONE);
                } else {
                    mList.setVisibility(View.VISIBLE);
                    mNotice.setVisibility(View.GONE);

                    Arrays.fill(friendIds, 0);
                    count = 0;
                    if (searchFriendResult.mobile.length != 0) {
                        for (Friend friend : searchFriendResult.mobile) {
                            if (friend.id == mApp.loginUser.id) {
                                continue;
                            }
                            mAdapter.addItem(friend);
                            friendIds[count] = friend.id;
                            count++;

                        }
                    }
                    if (searchFriendResult.qq.length != 0) {
                        for (Friend friend : searchFriendResult.qq) {
                            if ((Arrays.asList(friendIds).contains(friend.id)) || (friend.id == mApp.loginUser.id)) {
                                continue;
                            } else {
                                friendIds[count] = friend.id;
                                mAdapter.addItem(friend);
                                count++;
                            }
                        }
                    }
                    if (searchFriendResult.nickname.length != 0) {
                        for (Friend friend : searchFriendResult.nickname) {
                            if ((Arrays.asList(friendIds).contains(friend.id)) || (friend.id == mApp.loginUser.id)) {
                                continue;
                            } else {
                                friendIds[count] = friend.id;
                                mAdapter.addItem(friend);
                                count++;
                            }
                        }
                    }

                    promise.resolve(searchFriendResult);
                }
            }
        });

        return promise;
    }

    public Promise getRelationship() {
        final Promise promise = new Promise();
        RequestUrl requestUrl = setRelationParams(mResultList);
        mFriendProvider.loadRelationships(requestUrl).success(new NormalCallback<String[]>() {
            @Override
            public void success(String[] relationReults) {
                for (int i = 0; i < mResultList.size(); i++) {
                    mResultList.get(i).friendship = relationReults[i];
                }
                mAdapter.notifyDataSetChanged();
                promise.resolve(relationReults);
            }
        });

        return promise;

    }

    public Promise followUser(Friend friend) {
        final Promise promise = new Promise();

        RequestUrl requestUrl = mApp.bindNewUrl(Const.USERS, true);
        StringBuffer stringBuffer = new StringBuffer(requestUrl.url);
        stringBuffer.append(friend.id + "/followers");
        requestUrl.url = stringBuffer.toString();
        final HashMap<String, String> params = requestUrl.getParams();
        params.put("method", "follow");
        params.put("userId", mApp.loginUser.id + "");

        mFriendProvider.followUsers(requestUrl).success(new NormalCallback<FollowResult>() {
            @Override
            public void success(FollowResult followResult) {
                if (followResult == null) {
                    CommonUtil.longToast(mContext, "出错了");
                }
                if (followResult.success) {
                    CommonUtil.longToast(mContext, "关注用户成功");
                    mApp.sendMessage(Const.REFRESH_FRIEND_LIST, null);
                    getRelationship();
                } else {
                    CommonUtil.longToast(mContext, "关注用户失败");
                }
                promise.resolve(followResult);
            }
        });
        return promise;
    }

    public RequestUrl setRelationParams(ArrayList<Friend> list) {
        RequestUrl requestUrl = mApp.bindNewUrl(Const.USERS, false);
        StringBuffer sb = new StringBuffer(requestUrl.url.toString());
        sb.append(mApp.loginUser.id + "/" + "friendship?toIds=");
        for (Friend friend : list) {
            sb.append(friend.id + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        requestUrl.url = sb.toString();

        return requestUrl;
    }

}
