package com.edusoho.kuozhi.v3.ui.friend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PromiseCallback;
import com.edusoho.kuozhi.v3.model.bal.DiscussionGroup;
import com.edusoho.kuozhi.v3.model.provider.DiscussionGroupProvider;
import com.edusoho.kuozhi.v3.model.result.DiscussionGroupResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.Promise;
import com.edusoho.kuozhi.v3.view.SideBar;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Melomelon on 2015/9/10.
 */
public class GroupListActivity extends ActionBarBaseActivity {

    private ListView mListView;

    private GroupListAdapter mAdapter;
    private LayoutInflater mLayoutInflater;

    private SideBar mSidebar;
    private TextView mCharTextView;
    private FrameLayout mEmptyNotice;
    private FrameLayout mLoading;

    private DiscussionGroupProvider mDiscussionGroupProvider;

    private CharacterParser characterParser;
    private GroupComparator groupComparator;

//    private ArrayList<DiscussionGroup> mGroupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackMode(BACK, "讨论组");
        setContentView(R.layout.group_list_layout);
        mListView = (ListView) findViewById(R.id.group_listview);
        mSidebar = (SideBar) findViewById(R.id.group_list_sidebar);
        mCharTextView = (TextView) findViewById(R.id.sidebar_char_hint);
        mEmptyNotice = (FrameLayout) findViewById(R.id.empty_discussion_group);
        mLoading = (FrameLayout) findViewById(R.id.discussion_group_loading);
        mSidebar.setTextView(mCharTextView);
        mSidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChangedListener(String string) {
                int position = mAdapter.getPositionForSection(string.charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }
            }
        });
        characterParser = CharacterParser.getInstance();
        groupComparator = new GroupComparator();

        mDiscussionGroupProvider = new DiscussionGroupProvider(mContext);

        mAdapter = new GroupListAdapter();
        mListView.setAdapter(mAdapter);
        mEmptyNotice.setVisibility(View.GONE);
        mLoading.setVisibility(View.VISIBLE);
        loadGroup().then(new PromiseCallback() {
            @Override
            public Promise invoke(Object obj) {
                mLoading.setVisibility(View.GONE);
                return null;
            }
        });
    }

    private Promise loadGroup() {

        RequestUrl requestUrl = app.bindNewUrl(Const.DISCUSSION_GROUP, true);
        StringBuffer stringBuffer = new StringBuffer(requestUrl.url);
        stringBuffer.append("?start=0&limit=10000/");
        requestUrl.url = stringBuffer.toString();

        final Promise promise = new Promise();
        mDiscussionGroupProvider.getClassrooms(requestUrl).success(new NormalCallback<DiscussionGroupResult>() {
            @Override
            public void success(DiscussionGroupResult discussionGroupResult) {
                if (discussionGroupResult.resources.length != 0) {
                    DiscussionGroup[] groups = discussionGroupResult.resources;
                    List<DiscussionGroup> groupsList = Arrays.asList(groups);
                    setSortChar(groupsList);
                    Collections.sort(groupsList, groupComparator);
                    mAdapter.addGroupList(groupsList);

                    promise.resolve(groupsList);

                } else {
                    mEmptyNotice.setVisibility(View.VISIBLE);
                }
            }
        });
        return promise;
    }

    public void setSortChar(List<DiscussionGroup> groupList) {
        for (DiscussionGroup discussionGroup : groupList) {
            String pinyin = characterParser.getSelling(discussionGroup.title);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                discussionGroup.setSortLetters(sortString.toUpperCase());
            } else {
                discussionGroup.setSortLetters("#");
            }
        }
    }


    public class GroupListAdapter extends BaseAdapter {

        public GroupListAdapter() {
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        private ArrayList<DiscussionGroup> mGroupList = new ArrayList<DiscussionGroup>();

        @Override
        public int getCount() {
            return mGroupList.size();
        }

        @Override
        public Object getItem(int i) {
            return mGroupList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void addGroupItem(DiscussionGroup group) {
            mGroupList.add(group);
        }

        public void addGroupList(List<DiscussionGroup> list) {
            mGroupList.addAll(list);
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            GroupItemHolder groupItemHolder;
            if (view == null) {
                groupItemHolder = new GroupItemHolder();
                view = mLayoutInflater.inflate(R.layout.group_list_item_layout, null);
//                groupItemHolder.groupAvatar = (EduSohoGroupAvatar) view.findViewById(R.id.group_avatar);
                groupItemHolder.groupAvatar = (ImageView) view.findViewById(R.id.group_avatar);
                groupItemHolder.groupName = (TextView) view.findViewById(R.id.group_name);
                groupItemHolder.catalog = (TextView) view.findViewById(R.id.group_item_catalog);
                groupItemHolder.dividerLine = view.findViewById(R.id.divider_line);

                view.setTag(groupItemHolder);
            } else {
                groupItemHolder = (GroupItemHolder) view.getTag();
            }

            DiscussionGroup group = mGroupList.get(position);

//            generateGroupAvatar(groupItemHolder, position + 2);
            if (!group.picture.equals("")){
                ImageLoader.getInstance().displayImage(group.picture,groupItemHolder.groupAvatar,app.mOptions);
            }else {
                groupItemHolder.groupAvatar.setImageResource(R.drawable.default_avatar);
            }
            groupItemHolder.groupName.setText(group.title);

            int section = getSectionForPosition(position);
            if (position == getPositionForSection(section)) {
                groupItemHolder.catalog.setVisibility(View.VISIBLE);
                groupItemHolder.catalog.setText(group.getSortLetters());
            } else {
                groupItemHolder.catalog.setVisibility(View.GONE);
            }

            if (position != mGroupList.size() - 1) {
                if (getSectionForPosition(position) != getSectionForPosition(position + 1)) {
                    groupItemHolder.dividerLine.setVisibility(View.GONE);
                } else {
                    groupItemHolder.dividerLine.setVisibility(View.VISIBLE);
                }
            } else {
                groupItemHolder.dividerLine.setVisibility(View.GONE);
            }

            return view;
        }

//        private void generateGroupAvatar(GroupItemHolder groupItemHolder, int i) {
//            for (int count = 0; count < i; count++) {
//                groupItemHolder.groupAvatar.addAvatar(R.drawable.default_avatar);
//            }
//        }

        public int getSectionForPosition(int position) {
            DiscussionGroup group = mGroupList.get(position);
            return group.getSortLetters().charAt(0);

        }

        public int getPositionForSection(int section) {
            for (int i = 0; i < mGroupList.size(); i++) {
                DiscussionGroup group = mGroupList.get(i);
                String sortLetter = group.getSortLetters();
                char firstLettar = sortLetter.toUpperCase().charAt(0);
                if (firstLettar == section) {
                    return i;
                }
            }
            return -1;
        }

        private class GroupItemHolder {
            //            EduSohoGroupAvatar groupAvatar;
            ImageView groupAvatar;
            TextView groupName;
            TextView catalog;
            View dividerLine;
        }
    }
}
