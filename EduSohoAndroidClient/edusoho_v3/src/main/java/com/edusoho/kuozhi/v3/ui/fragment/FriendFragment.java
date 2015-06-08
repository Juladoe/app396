package com.edusoho.kuozhi.v3.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ListView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.FriendFragmentAdapter;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.view.EduSohoAnimWrap;
import com.edusoho.kuozhi.v3.view.EduToolBar;

/**
 * Created by JesseHuang on 15/4/26.
 */
public class FriendFragment extends BaseFragment {

    private ListView mFriendList;
    private FriendFragmentAdapter mFriendAdapter;
    private EduToolBar mEduToolBar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_friends);
        mActivity.setTitle(getString(R.string.title_friends));
        mEduToolBar = ((DefaultPageActivity)mActivity).getToolBar();
        setHasOptionsMenu(true);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mFriendList = (ListView) mContainerView.findViewById(R.id.friends_list);
        mFriendAdapter = new FriendFragmentAdapter(mContext,R.layout.item_type_friend_head);
        mFriendAdapter.setHeadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                int i = v.getId();
                if(i == R.id.search_friend_btn){
                    ObjectAnimator animator = ObjectAnimator.ofInt(new EduSohoAnimWrap(mEduToolBar), "height", mEduToolBar.getHeight(), 0);
                    animator.setDuration(300);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
//                    animator.setInterpolator(new AccelerateInterpolator());
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            SearchDialogFragment searchDialogFragment = new SearchDialogFragment();
                            searchDialogFragment.show(getChildFragmentManager(),"searchDialog");
                            searchDialogFragment.getToolBar(mEduToolBar);
//                            searchDialogFragment.editTextFocus();
                        }
                    });

                    animator.start();
                }
                else if (i == R.id.item_add_phone_friend){
                    app.mEngine.runNormalPlugin("AddPhoneContactActivity",mActivity,null);
                }else if(i == R.id.item_add_lesson_friend){
                    System.out.println("课程好友----》");

                }else if(i == R.id.item_add_class_friend){
                    System.out.println("班级好友---》");

                }
            }
        });
        mFriendList.setAdapter(mFriendAdapter);

        loadFriend();
    }

    public void loadFriend(){
        mFriendAdapter.setListViewLayout(R.layout.item_type_friend);

        mFriendAdapter.addItem(new Friend(R.drawable.sample_avatar_1,"花非花"));
        mFriendAdapter.addItem(new Friend(R.drawable.sample_avatar_2,"扫地神僧"));
        mFriendAdapter.addItem(new Friend(R.drawable.sample_avatar_3,"独孤求败"));
        mFriendAdapter.addItem(new Friend(R.drawable.sample_avatar_4,"阮玲玉"));
        mFriendAdapter.addItem(new Friend(R.drawable.sample_avatar_5,"西门吹雪"));
        mFriendAdapter.addItem(new Friend(R.drawable.sample_avatar_6,"虚竹"));
        mFriendAdapter.addItem(new Friend(R.drawable.sample_avatar_7,"段誉"));
        mFriendAdapter.addItem(new Friend(R.drawable.sample_avatar_8,"乔峰"));
        mFriendAdapter.addItem(new Friend(R.drawable.sample_avatar_9,"风清扬"));
        mFriendAdapter.addItem(new Friend(R.drawable.sample_avatar_10,"山鸡"));
        mFriendAdapter.addItem(new Friend(R.drawable.sample_avatar_11,"陈浩南"));
        mFriendAdapter.addItem(new Friend(R.drawable.sample_avatar_12,"王小二"));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);
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
        if(item.getItemId() == R.id.friends_search){
            //TODO 跳转到搜索页面
            System.out.println("搜索");
            app.mEngine.runNormalPlugin("", mActivity, null);
        }else if(item.getItemId() == R.id.friends_news){
            //TODO 好友验证列表页面
            System.out.println("好友验证");
        }
        return super.onOptionsItemSelected(item);
    }

}
