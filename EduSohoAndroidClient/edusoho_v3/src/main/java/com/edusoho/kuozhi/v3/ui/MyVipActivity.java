package com.edusoho.kuozhi.v3.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.VipCouponAdapter;
import com.edusoho.kuozhi.v3.model.bal.CouponInfo;
import com.edusoho.kuozhi.v3.model.bal.CouponListResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.ChildListView;
import com.edusoho.kuozhi.v3.view.circleImageView.CircleImageView;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by tree on 2017/4/27.
 */
public class MyVipActivity extends ActionBarBaseActivity {

    private TextView mTitle;
    private TextView mTvName;
    private TextView mTvUserType;
    private TextView mTvCouponListTitle;
    private TextView mTvCouponNum;
    private Button mBtnOpened;
    private CircleImageView mIvAvatar;
    private ChildListView mClvCoupon;
    private VipCouponAdapter mAdapter;
    private View mLlCoupon;
    private LinearLayout mLlBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vip);
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
//        ActivityUtil.setStatusViewBackgroud(this, getResources().getColor(R.color.disabled2_hint_color));
        setBackMode(null, "我的会员");
        initView();
        initUserInfo();
        initCouponCount();
        initData();

    }

    private void initView(){
        mTitle = (TextView) mActionBar.getCustomView().findViewById(R.id.tv_action_bar_title);
        mTitle.setTextColor(getResources().getColor(R.color.primary_font_color));
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvUserType = (TextView) findViewById(R.id.tv_avatar_type);
        mTvCouponListTitle = (TextView) findViewById(R.id.tv_coupon_list_title);
        mIvAvatar = (CircleImageView) findViewById(R.id.iv_avatar);
        mTvCouponNum = (TextView) findViewById(R.id.tv_voucher_num);
        mLlCoupon = findViewById(R.id.ll_voucher);
        mLlCoupon.setOnClickListener(couponClickListener);
        mBtnOpened = (Button) findViewById(R.id.btn_opened);
        mBtnOpened.setOnClickListener(openClickListener);
        mLlBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        mClvCoupon = (ChildListView) findViewById(R.id.clv_voucher);
        mClvCoupon.setFocusable(false);
        mAdapter = new VipCouponAdapter(mContext);
        mClvCoupon.setAdapter(mAdapter);
    }

    private void initUserInfo(){
        if(app.loginUser != null){
            mTvName.setText(app.loginUser.nickname);
            mTvUserType.setText(app.loginUser.userRole2String());
            ImageLoader.getInstance().displayImage(app.loginUser.getMediumAvatar(), mIvAvatar, app.mAvatarOptions);
        }
        if(app.loginUser.vip != null){
            mLlBottom.setVisibility(View.GONE);
        } else {
            mLlBottom.setVisibility(View.VISIBLE);
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                mTvCouponNum.setText((String)msg.obj);
            }
        }
    };

    private void initCouponCount(){
        RequestUrl requestUrl = app.bindNewUrl(Const.MY_COUPON_COUNT, true);
        mActivity.ajaxGet(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CouponInfo couponInfo = parseJsonValue(response, new TypeToken<CouponInfo>(){});
                if(couponInfo != null){
                    Message msg = handler.obtainMessage();
                    msg.obj = couponInfo.count;
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonUtil.longToast(mContext, "获取优惠券数目失败");
            }
        });
    }

    private void initData(){
        final LoadDialog dialog = LoadDialog.create(MyVipActivity.this);
        dialog.show();
        RequestUrl requestUrl = app.bindNewUrl(Const.VIP_COUPON_LIST, true);
        mActivity.ajaxGet(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                CouponListResult couponListResult = parseJsonValue(response, new TypeToken<CouponListResult>(){});
                if(couponListResult != null){
                    mTvCouponListTitle.setText(couponListResult.title);
                    if(couponListResult.resourcs != null){
                        mAdapter.setData(couponListResult.resourcs);
                    }
                } else {
                    CommonUtil.longToast(mContext, "获取优惠券信息失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                CommonUtil.longToast(mContext, "获取优惠券信息失败");
            }
        });
    }

    private View.OnClickListener couponClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.app.mEngine.runNormalPlugin("MyCardPackActivity", mContext, null);
        }
    };

    private View.OnClickListener openClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.app.mEngine.runNormalPlugin("ConfirmPayActivity", mContext, null);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        initUserInfo();
    }
}
