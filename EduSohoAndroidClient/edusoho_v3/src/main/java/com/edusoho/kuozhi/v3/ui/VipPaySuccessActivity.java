package com.edusoho.kuozhi.v3.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.VipCouponAdapter;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.ActivityUtil;
import com.edusoho.kuozhi.v3.view.ChildListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tree on 2017/5/2.
 */

public class VipPaySuccessActivity extends ActionBarBaseActivity {

    private TextView mActionBarTitle;
    private Button mBtnBackVip;
    private Button mBtnCheckPack;
    private ChildListView mClvCouponList;
    private CouponNameAdapter mAdapter;
    private View mCheckRules;
    private List<String> mNameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_pay_success);
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        ActivityUtil.setStatusViewBackgroud(this,Color.BLACK);
        setBackMode(null, "会员订购");
        initView();
    }

    private void initView() {
        mActionBarTitle = (TextView) mActionBar.getCustomView().findViewById(R.id.tv_action_bar_title);
        mActionBarTitle.setTextColor(getResources().getColor(R.color.primary_font_color));
        mBtnBackVip = (Button) findViewById(R.id.btn_back_vip);
        mBtnBackVip.setOnClickListener(backVipClickListener);
        mBtnCheckPack = (Button) findViewById(R.id.btn_check_pack);
        mBtnCheckPack.setOnClickListener(checkPackClickListener);
        mCheckRules = findViewById(R.id.rl_check_rules);
        mCheckRules.setOnClickListener(mRulesClickListener);
        mClvCouponList = (ChildListView) findViewById(R.id.clv_coupon_list);
        if(VipCouponAdapter.mCouponNameList != null) {
            mNameList.clear();
            mNameList.addAll(VipCouponAdapter.mCouponNameList);
        }
        mAdapter = new CouponNameAdapter(mContext);
        mClvCouponList.setAdapter(mAdapter);
    }

    private View.OnClickListener backVipClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            app.mEngine.runNormalPlugin("MyVipActivity", mContext, null, Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
    };

    private View.OnClickListener checkPackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            app.mEngine.runNormalPlugin("MyCardPackActivity", mContext, null);
        }
    };

    private View.OnClickListener mRulesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };


    public class CouponNameAdapter extends BaseAdapter{

        private Context mContext;

        public CouponNameAdapter(Context context){
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mNameList.size() >5 ? 5 : mNameList.size();
        }

        @Override
        public Object getItem(int position) {
            return mNameList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                ListView.LayoutParams params = new ListView.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView textView = new TextView(VipPaySuccessActivity.this);
                textView.setLayoutParams(params);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(13);
                textView.setTextColor(Color.parseColor("#2B333B"));
                convertView = textView;
            }
            if(convertView instanceof TextView){
                ((TextView) convertView).setText(mNameList.get(position));
            }
            convertView.setTag(position);
            return convertView;
        }
    }
}
