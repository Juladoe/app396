package com.edusoho.kuozhi.v3.ui.friend;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PromiseCallback;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.provider.FriendProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.Promise;
import com.edusoho.kuozhi.v3.view.EduSohoRoundCornerImage;
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
    private FrameLayout mLoading;

    private ArrayList<SchoolApp> mServiceList = new ArrayList<SchoolApp>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackMode(BACK, "服务号");
        setContentView(R.layout.service_list_layout);
        serviceList = (ListView) findViewById(R.id.service_list);
        mLoading = (FrameLayout) findViewById(R.id.service_list_loading);
        mAdapter = new ServiceListAdapter();
        serviceList.setAdapter(mAdapter);
        mProvider = new FriendProvider(mContext);

        if (mServiceList.size() != 0) {
            mAdapter.clearList();
        }
        if (!app.getNetIsConnect()) {
            mLoading.setVisibility(View.GONE);
            Toast.makeText(mContext, "无网络连接", Toast.LENGTH_LONG).show();
        }
        loadSchoolApps().then(new PromiseCallback() {
            @Override
            public Promise invoke(Object obj) {
                mLoading.setVisibility(View.GONE);
                return null;
            }
        });

        serviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SchoolApp schoolApp = (SchoolApp) adapterView.getItemAtPosition(i);
                switch (schoolApp.code) {
                    case "announcement":
                        app.mEngine.runNormalPlugin("BulletinActivity", mActivity, null);
                        break;
                    case "news":
                        Bundle bundle = new Bundle();
                        bundle.putString(FragmentPageActivity.FRAGMENT, "ArticleFragment");
                        app.mEngine.runNormalPluginWithBundle("FragmentPageActivity", mActivity, bundle);
                        break;
                }
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

        public void clearList() {
            mServiceList.clear();
            notifyDataSetChanged();
        }

        public void addSchoolAppList(List<SchoolApp> list) {
            mServiceList.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            final SchoolAppHolder schoolAppHolder;
            if (view == null) {
                view = mLayoutInflater.inflate(R.layout.item_type_school_app, null);
                schoolAppHolder = new SchoolAppHolder();
                schoolAppHolder.SchoolAppName = (TextView) view.findViewById(R.id.friend_name);
                schoolAppHolder.schoolAppAvatar = (EduSohoRoundCornerImage) view.findViewById(R.id.friend_avatar);
                schoolAppHolder.dividerLine = view.findViewById(R.id.divider_line);
                view.setTag(schoolAppHolder);
            } else {
                schoolAppHolder = (SchoolAppHolder) view.getTag();
            }

            final SchoolApp schoolApp = mServiceList.get(position);

            if (!TextUtils.isEmpty(schoolApp.avatar)) {
                ImageLoader.getInstance().displayImage(app.host + "/" + schoolApp.avatar, schoolAppHolder.schoolAppAvatar, app.mOptions);
            } else {
                displayAppIcon(schoolAppHolder.schoolAppAvatar, schoolApp);
            }
            schoolAppHolder.SchoolAppName.setText(schoolApp.name);

            if (position != mServiceList.size() - 1) {
                schoolAppHolder.dividerLine.setVisibility(View.VISIBLE);
            } else {
                schoolAppHolder.dividerLine.setVisibility(View.GONE);
            }

            return view;
        }

        private void displayAppIcon(ImageView schoolAppAvatar, SchoolApp schoolApp) {
            switch (schoolApp.code) {
                case "announcement":
                    ImageLoader.getInstance().displayImage(app.host + "/" + schoolApp.avatar, schoolAppAvatar, app.mOptions);
                    break;
                case "news":
                    schoolAppAvatar.setBackgroundColor(mContext.getResources().getColor(R.color.blue_alpha));
                    schoolAppAvatar.setPadding(10, 10, 10, 10);
                    schoolAppAvatar.setImageResource(R.drawable.article_app_icon);
                    break;
            }
        }

        private class SchoolAppHolder {
            private EduSohoRoundCornerImage schoolAppAvatar;
            private TextView SchoolAppName;
            private View dividerLine;
        }
    }
}
