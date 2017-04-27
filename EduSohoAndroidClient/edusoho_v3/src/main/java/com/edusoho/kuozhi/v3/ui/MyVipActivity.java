package com.edusoho.kuozhi.v3.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.view.circleImageView.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyVipActivity extends ActionBarBaseActivity {

    private TextView title;
    private TextView tvName;
    private TextView tvUserType;
    private CircleImageView ivAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vip);
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        setBackMode(null, "我的会员");
        initView();
    }

    private void initView(){
        title = (TextView) mActionBar.getCustomView().findViewById(R.id.tv_action_bar_title);
        title.setTextColor(getResources().getColor(R.color.primary_font_color));
        tvName = (TextView) findViewById(R.id.tv_name);
        tvUserType = (TextView) findViewById(R.id.tv_avatar_type);
        ivAvatar = (CircleImageView) findViewById(R.id.iv_avatar);
        initUserInfo();

    }

    private void initUserInfo(){
        if(app.loginUser != null){
            tvName.setText(app.loginUser.nickname);
            tvUserType.setText(app.loginUser.userRole2String());
            ImageLoader.getInstance().displayImage(app.loginUser.getMediumAvatar(), ivAvatar, app.mAvatarOptions);
        }
    }

}
