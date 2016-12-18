package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.ClassCatalogueAdapter;

/**
 * Created by DF on 2016/12/15.
 */

public class ClassCatalogFragment extends Fragment {


    private LinearLayout llCall;
    private RecyclerView rlClass;

    public ClassCatalogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_catalog, container, false);
        rlClass = (RecyclerView) view.findViewById(R.id.rv_class);
        llCall = (LinearLayout) view.findViewById(R.id.ll_join_course);

        initView();
        return view;

    }

    private void initView() {
        //判断用户是否加入了班级,未加入显示加入框
        if(!true){
            llCall.setVisibility(View.INVISIBLE);
        }
        ClassCatalogueAdapter classAdapter = new ClassCatalogueAdapter(getActivity());
        rlClass.setLayoutManager(new LinearLayoutManager(getActivity()));
        rlClass.setAdapter(classAdapter);
    }


}
