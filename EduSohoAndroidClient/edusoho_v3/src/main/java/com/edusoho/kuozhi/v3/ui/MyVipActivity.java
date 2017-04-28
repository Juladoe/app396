package com.edusoho.kuozhi.v3.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.VipVoucherAdapter;
import com.edusoho.kuozhi.v3.model.bal.VipVoucher;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.view.ChildListView;
import com.edusoho.kuozhi.v3.view.circleImageView.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class MyVipActivity extends ActionBarBaseActivity {

    private TextView mTitle;
    private TextView mTvName;
    private TextView mTvUserType;
    private CircleImageView mIvAvatar;
    private ChildListView mLvVoucher;
    private VipVoucherAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vip);
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        setBackMode(null, "我的会员");
        initView();
    }

    private void initView(){
        mTitle = (TextView) mActionBar.getCustomView().findViewById(R.id.tv_action_bar_title);
        mTitle.setTextColor(getResources().getColor(R.color.primary_font_color));
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvUserType = (TextView) findViewById(R.id.tv_avatar_type);
        mIvAvatar = (CircleImageView) findViewById(R.id.iv_avatar);
        initUserInfo();

        mLvVoucher = (ChildListView) findViewById(R.id.lv_voucher);
        mLvVoucher.setFocusable(false);
        mAdapter = new VipVoucherAdapter(mContext);
        List<VipVoucher> list = new ArrayList<>();
        list.add(new VipVoucher("111111",1,"111111111111111111"));
        list.add(new VipVoucher("222222",2,"222222222222222222"));
        list.add(new VipVoucher("333333",3,"333333"));
        list.add(new VipVoucher("333333",4,"333333"));
        list.add(new VipVoucher("333333",5,"333333"));
        list.add(new VipVoucher("333333",6,"333333"));
        mAdapter.setData(list);
        mLvVoucher.setAdapter(mAdapter);
    }

    private void initUserInfo(){
        if(app.loginUser != null){
            mTvName.setText(app.loginUser.nickname);
            mTvUserType.setText(app.loginUser.userRole2String());
            ImageLoader.getInstance().displayImage(app.loginUser.getMediumAvatar(), mIvAvatar, app.mAvatarOptions);
        }
    }

}
