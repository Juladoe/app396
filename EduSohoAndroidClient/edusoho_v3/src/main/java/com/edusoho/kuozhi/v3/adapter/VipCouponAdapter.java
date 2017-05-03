package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.bal.VipCoupon;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by tree on 2017/4/27.
 */

public class VipCouponAdapter extends BaseAdapter{

    private Context mContext;
    private List<VipCoupon> mList;
    public static List<String> mCouponNameList;

    public VipCouponAdapter(Context context){
        this.mContext = context;
        this.mList = new ArrayList<>();
        this.mCouponNameList = new ArrayList<>();
    }

    public void setData(List<VipCoupon> list){
        mList.addAll(list);
        notifyDataSetChanged();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_voucher_month, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            mCouponNameList.add(mList.get(position).rate + "元" +mList.get(position).name);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final VipCoupon voucher = mList.get(position);
        holder.mCouponName.setText(voucher.name);
        holder.mCouponPrice.setText(voucher.rate);
        holder.mUserDesc.setText(voucher.detail);
        if(voucher.isShow){
            holder.mUserDesc.setVisibility(View.VISIBLE);
            holder.mUseDescArrow.setText(mContext.getResources().getString(R.string.new_font_fold));
        } else {
            holder.mUserDesc.setVisibility(View.GONE);
            holder.mUseDescArrow.setText(mContext.getResources().getString(R.string.new_font_unfold));
        }
        holder.ll_use_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voucher.isShow = !voucher.isShow;
                notifyDataSetChanged();
            }
        });
        return convertView;
    }


    public static class ViewHolder{
        TextView mCouponName;
        TextView mCouponPrice;
        TextView mUseDescArrow;
        TextView mUserDesc;
        View ll_use_desc;

        public ViewHolder(View view){
            mCouponName = (TextView) view.findViewById(R.id.tv_voucher_name);
            mCouponPrice = (TextView) view.findViewById(R.id.tv_voucher_price);
            mUseDescArrow = (TextView) view.findViewById(R.id.tv_desc_arrow);
            mUserDesc = (TextView) view.findViewById(R.id.tv_use_desc);
            ll_use_desc =  view.findViewById(R.id.ll_use_desc);
        }

    }
}
