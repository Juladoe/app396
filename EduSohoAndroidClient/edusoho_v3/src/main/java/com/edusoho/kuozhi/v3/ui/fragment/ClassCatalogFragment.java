package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.ClassCatalogueAdapter;
import com.edusoho.kuozhi.v3.entity.lesson.ClassCatalogue;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.CourseActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;

/**
 * Created by DF on 2016/12/15.
 */

public class ClassCatalogFragment extends BaseFragment{

    public String mClassId = "1";

    private LinearLayout mLlCall;
    private RecyclerView mRlClass;
    private ClassCatalogue mClassCatalogue;
    private ClassCatalogue mClassCatalogue1;


    public ClassCatalogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_class_catalog);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mRlClass = (RecyclerView) view.findViewById(R.id.rv_class);
        mLlCall = (LinearLayout) view.findViewById(R.id.ll_join_course);
        initData();
    }

    private void initData() {
        RequestUrl requestUrl = app.bindNewUrl(Const.LESSON_CATALOG + "?courseId=" + mClassId, false);
        app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mClassCatalogue1 = ((CourseActivity) getActivity()).parseJsonValue(response, new TypeToken<ClassCatalogue>() {
                });
                initView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void initView() {
        //判断用户是否加入了班级,未加入显示加入框
        if(!true){
            mLlCall.setVisibility(View.INVISIBLE);
        }
        ClassCatalogueAdapter classAdapter = new ClassCatalogueAdapter(getActivity());
        mRlClass.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRlClass.setAdapter(classAdapter);
    }


}
