package com.edusoho.kuozhi.v3.ui.friend;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.provider.FriendProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.Promise;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Melomelon on 2015/9/10.
 */
public class ServiceListActivity extends ActionBarBaseActivity {

    private ListView serviceList;

    private LayoutInflater mLayoutInflater;
    private ServiceListAdapter mAdapter;
    private FriendProvider mProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackMode(BACK, "服务号");
        setContentView(R.layout.service_list_layout);
        serviceList = (ListView) findViewById(R.id.service_list);
        mAdapter = new ServiceListAdapter();
        serviceList.setAdapter(mAdapter);
        mProvider = new FriendProvider(mContext);

        loadSchoolApps();

        serviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                app.mEngine.runNormalPlugin("BulletinActivity", mActivity, null);
            }
        });
    }

    public Promise loadSchoolApps() {
        mAdapter.clearList();
        RequestUrl requestUrl = app.bindNewUrl(Const.SCHOOL_APPS, true);
        StringBuffer stringBuffer = new StringBuffer(requestUrl.url);
        requestUrl.url = stringBuffer.toString();

        final Promise promise = new Promise();
        mProvider.getSchoolApps(requestUrl)
                .success(new NormalCallback<List<SchoolApp>>() {
                    @Override
                    public void success(List<SchoolApp> schoolAppResult) {
                        if (schoolAppResult.size() != 0) {
                            mAdapter.addSchoolAppList(schoolAppResult);
                        }
                        promise.resolve(schoolAppResult);
                    }
                });

        return promise;
    }

    public class ServiceListAdapter extends BaseAdapter {

        private ArrayList<SchoolApp> mServiceList = new ArrayList<SchoolApp>();

        public ServiceListAdapter() {
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mServiceList.size();
        }

        @Override
        public Object getItem(int i) {
            return mServiceList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void clearList(){
            mServiceList.clear();
            notifyDataSetChanged();
        }

        public void addSchoolAppList(List<SchoolApp> list) {
            mServiceList.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final SchoolAppHolder schoolAppHolder;
            if (view == null) {
                view = mLayoutInflater.inflate(R.layout.item_type_school_app, null);
                schoolAppHolder = new SchoolAppHolder();
                schoolAppHolder.SchoolAppName = (TextView) view.findViewById(R.id.friend_name);
                schoolAppHolder.schoolAppAvatar = (ImageView) view.findViewById(R.id.friend_avatar);
                schoolAppHolder.dividerLine = view.findViewById(R.id.divider_line);
                view.setTag(schoolAppHolder);
            } else {
                schoolAppHolder = (SchoolAppHolder) view.getTag();
            }

            final SchoolApp schoolApp = mServiceList.get(i);

            if (!TextUtils.isEmpty(schoolApp.avatar)) {
                ImageLoader.getInstance().displayImage(app.host + "/" + schoolApp.avatar, schoolAppHolder.schoolAppAvatar, app.mOptions);
            }
            schoolAppHolder.SchoolAppName.setText(schoolApp.name);
            return view;
        }

        private class SchoolAppHolder {
            private ImageView schoolAppAvatar;
            private TextView SchoolAppName;
            private View dividerLine;
        }
    }
}
