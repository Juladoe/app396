package com.edusoho.kuozhi.ui.fragment.appPlugin;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.adapter.appPlugin.AppPluginAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.appPlugin.AppPlugin;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.htmlView.EduHtmlAppActivity;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by howzhi on 15/1/8.
*/
public class EduAppPluginFragment extends BaseFragment {

    private EduSohoListView mAppListView;

    @Override
    public String getTitle() {
        return "应用中心";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.app_plugin_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mAppListView = (EduSohoListView) view.findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mAppListView.setLayoutManager(linearLayoutManager);
        mAppListView.addItemDecoration();

        AppPluginAdapter adapter = new AppPluginAdapter(mActivity, R.layout.app_plugin_list_item);
        adapter.setOnItemClick(new RecyclerViewListBaseAdapter.RecyclerItemClick() {
            @Override
            public void onItemClick(Object obj, int position) {
                AppPlugin appPlugin = (AppPlugin) obj;
                Bundle bundle = new Bundle();
                bundle.putString(EduHtmlAppActivity.APP_URL, appPlugin.url);
                startAcitivityWithBundle("EduHtmlAppActivity", bundle);
            }
        });
        mAppListView.setAdapter(adapter);

        RequestUrl url = app.bindUrl(Const.SCHOOL_APP, true);
        url.setParams(new String[] {
                "clientVersion", mActivity.app.getApkVersion()
        });

        mAppListView.setLoadAdapter();
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                ArrayList<AppPlugin> list = mActivity.parseJsonValue(
                        object, new TypeToken<ArrayList<AppPlugin>>(){});
                if (list == null) {
                    return;
                }
                mAppListView.pushData(list);
            }
        });
    }
}
