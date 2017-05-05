package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.CouponBatchs;
import com.edusoho.kuozhi.v3.model.bal.MyCouponListResult;
import com.edusoho.kuozhi.v3.model.bal.VipCoupon;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.ActivityUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by tree on 2017/4/28.
 */

public class MyCardPackActivity extends ActionBarBaseActivity {

    public static final String COUPON_NAME = "coupon_name";
    public static final String COUPON_PRICE = "coupon_price";
    public static final String COUPON_DEADLINE = "coupon_deadline";
    public static final String COUPON_QRCODE = "coupon_qrcode";
    public static final String COUPON_URL = "coupon_url";
    public static final String COUPON_DESC = "coupon_desc";

    private TextView mTitle;
    private ListView mCouponList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_card_pack);
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        ActivityUtil.setStatusViewBackgroud(this,Color.BLACK);
        setBackMode(null, "我的卡包");
        initView();
        initData();
    }

    private void initView() {
        mTitle = (TextView) mActionBar.getCustomView().findViewById(R.id.tv_action_bar_title);
        mTitle.setTextColor(getResources().getColor(R.color.primary_font_color));
        mCouponList = (ListView) findViewById(R.id.lv_voucher_list);
    }

    private void initData() {
        final LoadDialog dialog = LoadDialog.create(MyCardPackActivity.this);
        dialog.show();
        final List<VipCoupon> vipCoupons = new ArrayList<>();
        final List<CouponBatchs> batchses = new ArrayList<>();
        RequestUrl requestUrl = app.bindNewUrl(Const.MY_VIP_COUPON_LIST, true);
        mActivity.ajaxGet(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                MyCouponListResult myCouponListResult = parseJsonValue(response, new TypeToken<MyCouponListResult>() {
                });
                if (myCouponListResult != null) {
                    vipCoupons.addAll(myCouponListResult.coupons);
                    batchses.addAll(myCouponListResult.batchs);
                    for (int i = 0; i < vipCoupons.size(); i++) {
                        VipCoupon vipCoupon = vipCoupons.get(i);
                        for (int j = 0; j < batchses.size(); j++) {
                            CouponBatchs batchs = batchses.get(j);
                            if (vipCoupon.batchId.equals(batchs.id)) {
                                vipCoupon.name = batchs.name;
                                vipCoupon.description = batchs.description == null ? "默认说明哦" : batchs.description;
                                break;
                            }
                        }
                    }
                    Collections.reverse(vipCoupons);
                    CardPackAdapter adapter = new CardPackAdapter(vipCoupons);
                    mCouponList.setAdapter(adapter);
                    onItemListener();
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

    private void onItemListener() {
        mCouponList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                String deadline = ((VipCoupon) parent.getAdapter().getItem(position)).deadline;
                if (Long.parseLong(deadline) < System.currentTimeMillis() / 1000) {
                    return;
                } else {
                    mActivity.app.mEngine.runNormalPlugin("CouponDetailActivity",
                            mContext, new PluginRunCallback() {
                                @Override
                                public void setIntentDate(Intent startIntent) {
                                    VipCoupon coupon = (VipCoupon) parent.getAdapter().getItem(position);
                                    startIntent.putExtra(COUPON_NAME, coupon.name);
                                    startIntent.putExtra(COUPON_PRICE, coupon.rate);
                                    startIntent.putExtra(COUPON_DEADLINE, coupon.deadline);
                                    startIntent.putExtra(COUPON_QRCODE, coupon.code);
                                    startIntent.putExtra(COUPON_URL, coupon.url);
                                    startIntent.putExtra(COUPON_DESC, coupon.description);
                            }
                    });
                }
            }
        });
    }


    public class CardPackAdapter extends BaseAdapter {

        private List<VipCoupon> mList;

        public CardPackAdapter(List<VipCoupon> list) {
            this.mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_card_pack, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            VipCoupon coupon = mList.get(position);
            holder.mName.setText(coupon.name);
            holder.mPrice.setText((int) Double.parseDouble(coupon.rate) + "");
            if (Long.parseLong(coupon.deadline) > System.currentTimeMillis() / 1000) {
                holder.mBg.setBackgroundResource(R.drawable.voucher_bg);
            } else {
                holder.mBg.setBackgroundResource(R.drawable.voucher_expired_bg);
            }
            holder.mDeadline.setText("有效期至" + getDeadline(coupon.deadline));
            return convertView;
        }
    }

    private String getDeadline(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    public static class ViewHolder {

        TextView mName;
        TextView mPrice;
        TextView mDeadline;
        View mBg;

        public ViewHolder(View v) {
            mName = (TextView) v.findViewById(R.id.tv_name);
            mPrice = (TextView) v.findViewById(R.id.tv_price);
            mDeadline = (TextView) v.findViewById(R.id.tv_deadline);
            mBg = v.findViewById(R.id.rl_bg);
        }
    }
}
