package com.edusoho.kuozhi.clean.module.course.info;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.widget.ESBottomDialog;

/**
 * Created by JesseHuang on 2017/4/12.
 */

public class ServicesDialog extends ESBottomDialog implements ESBottomDialog.BottomDialogContentView {

    private static final String SERVICE_DATA = "service_data";
    private CourseProject.Service[] mServices;
    private RecyclerView mServiceContent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mServices = (CourseProject.Service[]) getArguments().getSerializable(SERVICE_DATA);
        }
        setContent(this);
    }

    public static ServicesDialog newInstance(CourseProject.Service[] services) {
        Bundle args = new Bundle();
        args.putSerializable(SERVICE_DATA, services);
        ServicesDialog fragment = new ServicesDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getContentView(ViewGroup parentView) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_services, parentView, false);
        mServiceContent = (RecyclerView) view.findViewById(R.id.rv_content);
        mServiceContent.setLayoutManager(new LinearLayoutManager(getActivity()));
        ServiceDialogAdapter adapter = new ServiceDialogAdapter(getActivity());
        mServiceContent.setAdapter(adapter);
        return view;
    }

    @Override
    public void setButtonState(TextView btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServicesDialog.this.dismiss();
            }
        });
    }

    @Override
    public boolean showConfirm() {
        return true;
    }

    public class ServiceDialogAdapter extends RecyclerView.Adapter<ViewHolder> {

        private Context mContext;

        public ServiceDialogAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_dialog_services, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tvShortName.setText(mServices[position].short_name);
            holder.tvFullName.setText(mServices[position].full_name);
            holder.tvSummary.setText(mServices[position].summary);
        }

        @Override
        public int getItemCount() {
            return mServices.length;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvShortName;
        public TextView tvFullName;
        public TextView tvSummary;

        public ViewHolder(View view) {
            super(view);
            tvShortName = (TextView) view.findViewById(R.id.tv_service_short_name);
            tvFullName = (TextView) view.findViewById(R.id.tv_service_full_name);
            tvSummary = (TextView) view.findViewById(R.id.tv_service_summary);
        }
    }
}
