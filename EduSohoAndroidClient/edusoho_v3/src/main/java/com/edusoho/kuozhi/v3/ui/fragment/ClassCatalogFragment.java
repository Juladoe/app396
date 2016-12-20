package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.ClassCatalogueAdapter;
import com.edusoho.kuozhi.v3.entity.lesson.ClassCatalogue;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.ClassroomActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseNoTitleActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.FixHeightListView;
import com.google.gson.reflect.TypeToken;

/**
 * Created by DF on 2016/12/15.
 */

public class ClassCatalogFragment extends Fragment {

    public String mClassRoomId = "0";
    private FixHeightListView mLvClass;
    private ClassCatalogue mClassCatalogue;


    public ClassCatalogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_catalog, container, false);
        mLvClass = (FixHeightListView) view.findViewById(R.id.lv_catalog);
        initData();
        return view;
    }

    private void initData() {
        mClassRoomId = getArguments().getString("id");
        RequestUrl requestUrl = ((ClassroomActivity) getActivity()).app.bindNewUrl(Const.CLASS_CATALOG + "?classRoomId=" + mClassRoomId, false);
        ((BaseNoTitleActivity) getActivity()).app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("test", response);
                mClassCatalogue = ((BaseNoTitleActivity) getActivity()).parseJsonValue(response, new TypeToken<ClassCatalogue>() {
                });
                if (mClassCatalogue != null && mClassCatalogue.getList() != null) {
                    initView();
                } else {
                    CommonUtil.shortCenterToast(getActivity(), "该班级没有课程");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void initView() {
        ClassCatalogueAdapter classAdapter = new ClassCatalogueAdapter(getActivity(), mClassCatalogue.getList());
        mLvClass.setAdapter(classAdapter);
    }

    public void reFreshView(){}
}
