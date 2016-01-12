package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.view.EduSohoGridView;
import com.makeramen.roundedimageview.RoundedImageView;

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
    protected View vAnnouncement;
    protected View vEntry;
    protected View vClearChatRecord;
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

        findViewById(R.id.rl_announcement).setOnClickListener(this);
        findViewById(R.id.rl_entry).setOnClickListener(this);
        findViewById(R.id.rl_clear_record).setOnClickListener(this);
        btnDelRecordAndQuit.setOnClickListener(this);
    }

    protected void initData() {

    }

    @Override
    public void onClick(View v) {

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
