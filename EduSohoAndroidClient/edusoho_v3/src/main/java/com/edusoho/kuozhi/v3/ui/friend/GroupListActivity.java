package com.edusoho.kuozhi.v3.ui.friend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.bal.DiscussionGroup;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.view.EduSohoGroupAvatar;
import com.edusoho.kuozhi.v3.view.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Melomelon on 2015/9/10.
 */
public class GroupListActivity extends ActionBarBaseActivity {

    private ListView groupList;

    private GroupListAdapter mAdapter;
    private LayoutInflater mLayoutInflater;

    private SideBar mSidebar;
    private TextView mCharTextView;

    private CharacterParser characterParser;
    private GroupComparator groupComparator;

    private ArrayList<DiscussionGroup> tmpList;//模拟数据用。以后采用接口取数据的时候可能就不需要了

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackMode(BACK, "讨论组");
        setContentView(R.layout.group_list_layout);
        groupList = (ListView) findViewById(R.id.group_listview);
        mSidebar = (SideBar) findViewById(R.id.group_list_sidebar);
        mCharTextView = (TextView) findViewById(R.id.sidebar_char_hint);
        mSidebar.setTextView(mCharTextView);
        mSidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChangedListener(String string) {
                int position = mAdapter.getPositionForSection(string.charAt(0));
                if (position != -1) {
                    groupList.setSelection(position);
                }
            }
        });
        characterParser = CharacterParser.getInstance();
        groupComparator = new GroupComparator();

        mAdapter = new GroupListAdapter();
        groupList.setAdapter(mAdapter);
        loadGroup();
    }

    private void loadGroup() {
        tmpList = new ArrayList<DiscussionGroup>();
        for (int i = 2; i < 10; i++) {
            tmpList.add(new DiscussionGroup("今晚打dao了 " + i + " 只老虎"));
        }
        for (int i = 5; i < 7; i++) {
            tmpList.add(new DiscussionGroup("明天 " + i + " 只老虎"));
        }
        tmpList.add(new DiscussionGroup("MMMMMMMMMM"));
        tmpList.add(new DiscussionGroup("6435fgahsgg"));
        tmpList.add(new DiscussionGroup("baslkdjf5"));
        tmpList.add(new DiscussionGroup("lKVad54adBb87E"));
        tmpList.add(new DiscussionGroup("sd5fb13v5awbff"));
        tmpList.add(new DiscussionGroup("vdf9wuv"));


        setSortChar(tmpList);
        Collections.sort(tmpList,groupComparator);
        mAdapter.addGroupList(tmpList);
    }

    public void setSortChar(List<DiscussionGroup> groupList){
        for (DiscussionGroup discussionGroup:groupList){
            String pinyin = characterParser.getSelling(discussionGroup.groupName);
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

        public void addGroupList(ArrayList<DiscussionGroup> list){
            mGroupList.addAll(list);
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            GroupItemHolder groupItemHolder;
            if (view == null) {
                groupItemHolder = new GroupItemHolder();
                view = mLayoutInflater.inflate(R.layout.group_list_item_layout, null);
                groupItemHolder.groupAvatar = (EduSohoGroupAvatar) view.findViewById(R.id.group_avatar);
                groupItemHolder.groupName = (TextView) view.findViewById(R.id.group_name);
                groupItemHolder.catalog = (TextView) view.findViewById(R.id.group_item_catalog);

                view.setTag(groupItemHolder);
            } else {
                groupItemHolder = (GroupItemHolder) view.getTag();
            }

            DiscussionGroup group = mGroupList.get(position);

            generateGroupAvatar(groupItemHolder, position + 2);
            groupItemHolder.groupName.setText(group.groupName);

            int section = getSectionForPosition(position);
            if (position == getPositionForSection(section)) {
                groupItemHolder.catalog.setVisibility(View.VISIBLE);
                groupItemHolder.catalog.setText(group.getSortLetters());
            } else {
                groupItemHolder.catalog.setVisibility(View.GONE);
            }

            return view;
        }

        private void generateGroupAvatar(GroupItemHolder groupItemHolder, int i) {
            for (int count = 0; count < i; count++) {
                groupItemHolder.groupAvatar.addAvatar(R.drawable.default_avatar);
            }
        }

        public int getSectionForPosition(int position) {
            DiscussionGroup group = mGroupList.get(position);
            return group.getSortLetters().charAt(0);

        }

        public int getPositionForSection(int section) {
            for (int i = 0; i <mGroupList.size(); i++){
                DiscussionGroup group = mGroupList.get(i);
                String  sortLetter = group.getSortLetters();
                char firstLettar = sortLetter.toUpperCase().charAt(0);
                if (firstLettar == section){
                    return i;
                }
            }
            return -1;
        }

        private class GroupItemHolder {
            EduSohoGroupAvatar groupAvatar;
            TextView groupName;
            TextView catalog;
        }
    }
}
