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

import java.util.ArrayList;

/**
 * Created by Melomelon on 2015/9/10.
 */
public class GroupListActivity extends ActionBarBaseActivity {

    private ListView groupList;

    private GroupListAdapter mAdapter;
    private LayoutInflater mLayoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackMode(BACK, "讨论组");
        setContentView(R.layout.group_list_layout);
        groupList = (ListView) findViewById(R.id.group_listview);
        mAdapter = new GroupListAdapter();
        groupList.setAdapter(mAdapter);
        loadGroup();
    }

    private void loadGroup() {
        for (int i = 2; i < 10; i++) {
            mAdapter.addGroupItem(new DiscussionGroup("今晚打dao了 " + i + " 只老虎"));
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

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            GroupItemHolder groupItemHolder;
            if (view == null) {
                groupItemHolder = new GroupItemHolder();
                view = mLayoutInflater.inflate(R.layout.group_list_item_layout, null);
                groupItemHolder.groupAvatar = (EduSohoGroupAvatar) view.findViewById(R.id.group_avatar);
                groupItemHolder.groupName = (TextView) view.findViewById(R.id.group_name);

                view.setTag(groupItemHolder);
            } else {
                groupItemHolder = (GroupItemHolder) view.getTag();
            }

            DiscussionGroup group = mGroupList.get(i);

            generateGroupAvatar(groupItemHolder, i + 2);
            groupItemHolder.groupName.setText(group.groupName);

            return view;
        }

        private void generateGroupAvatar(GroupItemHolder groupItemHolder, int i) {
            for (int count = 0; count < i; count++) {
                groupItemHolder.groupAvatar.addAvatar(R.drawable.default_avatar);
            }
        }

        private class GroupItemHolder {
            EduSohoGroupAvatar groupAvatar;
            TextView groupName;
        }
    }
}
