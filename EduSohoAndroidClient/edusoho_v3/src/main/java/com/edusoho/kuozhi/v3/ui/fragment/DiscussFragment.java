package com.edusoho.kuozhi.v3.ui.fragment;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.view.EduSohoNewIconView;
import com.edusoho.kuozhi.v3.view.FixCourseListView;

/**
 * Created by DF on 2017/1/4.
 */

public class DiscussFragment extends BaseFragment {


    private String mCouseId;
    private View view;
    private FixCourseListView lvDiscuss;
    private EduSohoNewIconView tvEdit;

    public DiscussFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_discuss, container, true);
        mCouseId = getArguments().getString("id");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget();
        initData();
    }

    private void initWidget() {
        lvDiscuss = (FixCourseListView) view.findViewById(R.id.lv_discuss);
        tvEdit = (EduSohoNewIconView) view.findViewById(R.id.tv_edit_topic);
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });
    }



    private void initData() {

    }



    private void showPopup() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_discuss_publish, null);
        PopupWindow popupWindow = new PopupWindow(view);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        int[] location = new int[2];
        tvEdit.getLocationOnScreen(location);
        popupWindow.showAtLocation(tvEdit, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow.getHeight());
    }
}
