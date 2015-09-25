package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.provider.SystemProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.NewsCourseDataSource;
import com.edusoho.kuozhi.v3.util.sql.ServiceProviderDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by howzhi on 15/9/23.
 */
public class ServiceProfileFragment extends BaseFragment {

    public static final String SERVICE_ID = "id";

    private SystemProvider mSystemProvider;
    private ImageView mServiceIconView;
    private CheckBox mSPDonotDisturbView;
    private TextView mServiceNameView;
    private TextView mServiceTitleView;
    private TextView mClearView;

    private int mSchoolProfileId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.service_profile_layout);
        ModelProvider.init(mContext, this);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mServiceIconView = (ImageView) view.findViewById(R.id.sp_icon);
        mServiceNameView = (TextView) view.findViewById(R.id.sp_name);
        mServiceTitleView = (TextView) view.findViewById(R.id.sp_title);
        mSPDonotDisturbView = (CheckBox) view.findViewById(R.id.sp_icon_do_not_disturb);
        mClearView = (TextView) view.findViewById(R.id.sp_icon_clear_message);
        initServiceProfile();
    }

    private void initServiceProfile() {

        Bundle bundle = getArguments();
        mSchoolProfileId = bundle != null ? bundle.getInt(SERVICE_ID, 0) : 0;
        RequestUrl requestUrl = app.bindNewUrl(String.format(Const.GET_SCHOOL_APP, mSchoolProfileId), true);
        mSystemProvider.getSchoolApp(requestUrl).success(new NormalCallback<SchoolApp>() {
            @Override
            public void success(SchoolApp schoolApp) {
                if (schoolApp == null) {
                    CommonUtil.longToast(mContext, "获取服务号内容失败");
                    return;
                }
                initSchoolApp(schoolApp);
            }
        });
        mSPDonotDisturbView.setChecked(app.getMsgDisturbFromCourseId(mSchoolProfileId) == 0);
    }

    private void initSchoolApp(SchoolApp schoolApp) {

        mServiceNameView.setText(schoolApp.name);
        mServiceTitleView.setText(schoolApp.title);
        mClearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupDialog.createMuilt(mActivity, "消息记录", "清除历史消息", new PopupDialog.PopupClickListener() {
                    @Override
                    public void onClick(int button) {
                        if (button == PopupDialog.OK) {
                            clearHistory();
                        }
                    }
                }).show();
            }
        });
        mSPDonotDisturbView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.saveMsgDisturbConfig(mSchoolProfileId, mSPDonotDisturbView.isChecked() ? 0 : 3);
            }
        });
        ImageLoader.getInstance().displayImage(schoolApp.avatar, mServiceIconView, app.mOptions);
    }

    private void clearHistory() {
        ServiceProviderDataSource dataSource = new ServiceProviderDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        dataSource.deleteBySPId(mSchoolProfileId);

        Bundle bundle = new Bundle();
        bundle.putInt(SERVICE_ID, mSchoolProfileId);
        app.sendMessage(Const.CLEAR_HISTORY, bundle);
    }
}
