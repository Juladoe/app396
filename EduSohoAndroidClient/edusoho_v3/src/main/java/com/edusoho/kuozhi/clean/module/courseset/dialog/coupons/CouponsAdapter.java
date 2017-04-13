package com.edusoho.kuozhi.clean.module.courseset.dialog.coupons;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.edusoho.kuozhi.clean.bean.OrderInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DF on 2017/4/14.
 */

public class CouponsAdapter extends RecyclerView.Adapter {

    private List<OrderInfo.AvailableCouponsBean> mCoupons;
    private Context mContext;

    public CouponsAdapter(Context mContext) {
        this.mCoupons = new ArrayList<>();
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mCoupons.size();
    }

//    public static class CouponsHolder extends Holder
}
