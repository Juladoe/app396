package com.edusoho.kuozhi.clean.module.courseset.dialog.coupons;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.OrderInfo;
import com.edusoho.kuozhi.clean.bean.OrderInfo.Coupon;

import java.util.List;

/**
 * Created by DF on 2017/4/14.
 */

public class CouponsAdapter extends BaseAdapter {

    private static final String MINUS = "minus";
    private static final String ALL_STATION_USE = "all";

    private List<OrderInfo.Coupon> mCoupons;
    private Context mContext;

    public CouponsAdapter(Context mContext, List<Coupon> list) {
        this.mCoupons = list;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mCoupons == null ? 0 : mCoupons.size();
    }

    @Override
    public Object getItem(int position) {
        return mCoupons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CouponsHolder mHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_coupons_layout, parent, false);
            mHolder = new CouponsHolder(convertView);
            convertView.setTag(mHolder);
        }else {
            mHolder = (CouponsHolder) convertView.getTag();
        }
        Coupon availableCouponsBean = mCoupons.get(position);
        renderView(mHolder, availableCouponsBean);
        return convertView;
    }

    private void renderView(CouponsHolder holder, Coupon coupon) {
        holder.mType.setText(MINUS.equals(coupon.type) ? String.format(mContext.getString(R.string.yuan), coupon.rate)
                : String.format(mContext.getString(R.string.discount_price), coupon.rate));
        holder.mDate.setText(String.format(mContext.getString(R.string.valid_date), coupon.deadline.split("T")[0]));
        holder.mApplicationType.setText(ALL_STATION_USE.equals(coupon.targetType) ? R.string.all_station_use
                            : R.string.course_application);
        holder.mIsSelector.setBackgroundResource(coupon.isSelector ? R.drawable.item_selector
                                            : R.drawable.item_unselector);
    }

    private static class CouponsHolder{

        private final TextView mType;
        private final TextView mDate;
        private final TextView mApplicationType;
        private final ImageView mIsSelector;

        private CouponsHolder(View view) {
            mType = (TextView) view.findViewById(R.id.tv_type);
            mDate = (TextView) view.findViewById(R.id.tv_validity);
            mApplicationType = (TextView) view.findViewById(R.id.tv_application_type);
            mIsSelector = (ImageView) view.findViewById(R.id.iv_selector);
        }
    }

}
