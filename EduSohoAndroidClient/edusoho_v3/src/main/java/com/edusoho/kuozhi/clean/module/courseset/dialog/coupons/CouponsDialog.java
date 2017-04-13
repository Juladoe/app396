package com.edusoho.kuozhi.clean.module.courseset.dialog.coupons;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.widget.ESBottomDialog;

/**
 * Created by DF on 2017/4/14.
 */

public class CouponsDialog extends ESBottomDialog
        implements ESBottomDialog.BottomDialogContentView{

    private RecyclerView mCoupons;
    private CouponsAdapter mAdapter;

    @Override
    public View getContentView(ViewGroup parentView) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_coupons, parentView, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mCoupons = (RecyclerView) view.findViewById(R.id.rv_content);
        mCoupons.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CouponsAdapter(getContext());
        mCoupons.setAdapter(mAdapter);
    }

    @Override
    public void setButtonState(TextView btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean showConfirm() {
        return true;
    }
}
