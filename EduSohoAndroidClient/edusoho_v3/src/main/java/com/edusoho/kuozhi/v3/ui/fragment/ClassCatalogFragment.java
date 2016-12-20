package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.ClassCatalogueAdapter;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.model.bal.Classroom;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.ui.base.BaseNoTitleActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.FixHeightListView;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by DF on 2016/12/15.
 */

public class ClassCatalogFragment extends BaseFragment {
    public boolean isJoin = false;
    public String mClassRoomId = "0";
    private FixHeightListView mLvClass;
    private List<Classroom> mClassCatalogue;


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
        RequestUrl requestUrl = ((BaseNoTitleActivity) getActivity()).app.bindNewUrl(Const.CLASS_CATALOG + "?classRoomId=" + mClassRoomId, false);
        ((BaseNoTitleActivity) getActivity()).app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mClassCatalogue = ((BaseNoTitleActivity) getActivity()).parseJsonValue(response, new TypeToken<List<Classroom>>() {
                });
                if (mClassCatalogue != null && mClassCatalogue.size() > 0) {
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
        ClassCatalogueAdapter classAdapter = new ClassCatalogueAdapter(getActivity(), mClassCatalogue);
        mLvClass.setAdapter(classAdapter);
        mLvClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (TextUtils.isEmpty(app.token)) {
                    CoreEngine.create(getContext()).runNormalPlugin("LoginActivity", getContext(), null);
                    return;
                }
                if (!isJoin) {
                    CommonUtil.shortCenterToast(getActivity(), "请先加入班级");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("course_id", mClassCatalogue.get(position).lessonNum+"");
                CoreEngine.create(getContext()).runNormalPluginWithBundle("CourseActivity", getContext(), bundle);
                return;
            }
        });
    }



    public void reFreshView(){}
}
