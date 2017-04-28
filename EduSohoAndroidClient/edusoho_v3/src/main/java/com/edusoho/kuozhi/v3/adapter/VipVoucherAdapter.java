package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.bal.VipVoucher;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by tree on 2017/4/27.
 */

public class VipVoucherAdapter extends BaseAdapter{

    private Context mContext;
    private List<VipVoucher> mList;

    public VipVoucherAdapter(Context context){
        this.mContext = context;
        this.mList = new ArrayList<>();
    }

    public void setData(List<VipVoucher> list){
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
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final VipVoucher voucher = mList.get(position);
        holder.mVoucherName.setText(voucher.name);
        holder.mVoucherPrice.setText(voucher.price + "");
        holder.mUserDesc.setText(voucher.desc);
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
        TextView mVoucherName;
        TextView mVoucherPrice;
        TextView mUseDescArrow;
        TextView mUserDesc;
        View ll_use_desc;

        public ViewHolder(View view){
            mVoucherName = (TextView) view.findViewById(R.id.tv_voucher_name);
            mVoucherPrice = (TextView) view.findViewById(R.id.tv_voucher_price);
            mUseDescArrow = (TextView) view.findViewById(R.id.tv_desc_arrow);
            mUserDesc = (TextView) view.findViewById(R.id.tv_use_desc);
            ll_use_desc =  view.findViewById(R.id.ll_use_desc);
        }

    }
}
