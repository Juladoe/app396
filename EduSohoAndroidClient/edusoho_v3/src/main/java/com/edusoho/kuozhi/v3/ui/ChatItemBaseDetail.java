package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.GroupMember;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EduSohoGridView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by JesseHuang on 15/12/10.
 */
public class ChatItemBaseDetail extends ActionBarBaseActivity implements View.OnClickListener {
    protected int mFromId;

    protected EduSohoGridView gvMemberAvatar;
    protected TextView tvMemberSum;
    protected TextView tvClassroomAnnouncement;
    protected TextView tvEntryClassroom;
    protected TextView tvClearChatRecord;
    protected Button btnDelRecordAndQuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_detail);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        initData();
    }

    protected void initView() {
        gvMemberAvatar = (EduSohoGridView) findViewById(R.id.gv_member);
        tvMemberSum = (TextView) findViewById(R.id.tv_all_member);
        tvClassroomAnnouncement = (TextView) findViewById(R.id.tv_classroom_announcement);
        tvEntryClassroom = (TextView) findViewById(R.id.tv_entry_classroom);
        tvClearChatRecord = (TextView) findViewById(R.id.clear_record);
        btnDelRecordAndQuit = (Button) findViewById(R.id.btn_del_and_quit);
        tvClassroomAnnouncement.setOnClickListener(this);
        tvEntryClassroom.setOnClickListener(this);
        tvClearChatRecord.setOnClickListener(this);
        btnDelRecordAndQuit.setOnClickListener(this);
    }

    protected void initData() {

    }

    @Override
    public void onClick(View v) {

    }

    public class MemberAvatarAdapter extends BaseAdapter {
        public List<GroupMember> mList;
        private DisplayImageOptions mOptions;

        public MemberAvatarAdapter(List<GroupMember> mList) {
            this.mList = mList;
            mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).
                    showImageForEmptyUri(R.drawable.default_avatar).
                    showImageOnFail(R.drawable.default_avatar).build();
        }

        @Override
        public int getCount() {
            if (mList != null) {
                return mList.size() + 1;
            }
            return 1;
        }

        @Override
        public GroupMember getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_member_avatar, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (getCount() - 1 != position) {
                final GroupMember member = mList.get(position);
                viewHolder.ivAvatar.setBackground(null);
                ImageLoader.getInstance().displayImage(member.user.avatar, viewHolder.ivAvatar, mOptions);
                viewHolder.tvMemberName.setText(member.user.nickname);
                viewHolder.ivAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.USER_PROFILE, member.user.id));
                                startIntent.putExtra(WebViewActivity.URL, url);
                            }
                        });
                    }
                });
            } else {
                viewHolder.ivAvatar.setBackgroundResource(R.drawable.group_member_more_bg);
                viewHolder.tvMemberName.setText("更多");
                viewHolder.ivAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.CLASSROOM_MEMBER_LIST, mFromId));
                                startIntent.putExtra(WebViewActivity.URL, url);
                            }
                        });
                    }
                });
            }
            return convertView;
        }
    }

    public static class ViewHolder {
        public RoundedImageView ivAvatar;
        public TextView tvMemberName;

        public ViewHolder(View view) {
            ivAvatar = (RoundedImageView) view.findViewById(R.id.iv_member_avatar);
            tvMemberName = (TextView) view.findViewById(R.id.tv_member_name);
        }
    }
}
