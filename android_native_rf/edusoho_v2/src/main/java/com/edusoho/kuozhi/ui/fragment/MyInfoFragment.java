package com.edusoho.kuozhi.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.MyInfoPlugin;
import com.edusoho.kuozhi.model.UserRole;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.course.CourseDetailsTabActivity;
import com.edusoho.kuozhi.ui.widget.LearnStatusWidget;
import com.edusoho.kuozhi.ui.widget.MyInfoPluginListView;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.plugin.CircularImageView;

/**
 * Created by howzhi on 14-8-14.
 */
public class MyInfoFragment extends BaseFragment {

    public String mTitle = "我的学习";
    private MyInfoPluginListView mMyInfoPluginListView;
    private LearnStatusWidget mLearnStatusWidget;
    private View mUserLayout;
    private CircularImageView mUserLogo;
    private TextView mUserName;
    private TextView mUserGroup;
    private TextView mUserContent;

    public static final int REFRESH = 0010;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.myinfo_layout);

        if (app.loginUser == null) {
            LoginActivity.startForResult(mActivity);
        }
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
        switch (message.type.code) {
            case REFRESH:
                loadUser();
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(REFRESH, source)
        };
        return messageTypes;
    }

    public void loadUser()
    {
        if (app.loginUser == null) {
            mUserLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginActivity.startForResult(mActivity);
                }
            });
            return;
        }

        mUserLayout.setEnabled(false);
        mUserName.setText(app.loginUser.nickname);
        mUserGroup.setText(UserRole.coverRoleToStr(app.loginUser.roles));
        mUserContent.setText(app.loginUser.title);

        AQuery aQuery = new AQuery(mActivity);
        aQuery.id(mUserLogo).image(app.loginUser.mediumAvatar, false, true, 200, R.drawable.myinfo_default_face);
        //mLearnStatusWidget.initialise(mActivity, "", null);
    }

    @Override
    protected void initView(View view) {
        mUserLogo = (CircularImageView) view.findViewById(R.id.myinfo_logo);
        mUserName = (TextView) view.findViewById(R.id.myinfo_name);
        mUserGroup = (TextView) view.findViewById(R.id.myinfo_group);
        mUserContent = (TextView) view.findViewById(R.id.myinfo_content);
        mUserLayout = view.findViewById(R.id.myinfo_user_layout);
        mMyInfoPluginListView = (MyInfoPluginListView) view.findViewById(R.id.myinfo_plugin_list);
        mLearnStatusWidget = (LearnStatusWidget) view.findViewById(R.id.myinfo_learnStatusWidget);

        mMyInfoPluginListView.initFromLocal(mActivity);
        mMyInfoPluginListView.setItemOnClick(new MyInfoPluginListView.PluginItemClick() {
            @Override
            public void onClick(final MyInfoPlugin plugin) {
                switch (plugin.action) {
                    case QUESTION:
                    case COURSE:
                        showMyCourse();
                    case TEST:
                    case DISCUSS:
                    case NOTE:
                }
            }
        });
        loadUser();
    }

    private void showMyCourse()
    {
        PluginRunCallback callback = new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(CourseDetailsTabActivity.FRAGMENT_DATA, new Bundle());
                startIntent.putExtra(CourseDetailsTabActivity.LISTS, Const.MY_COURSE_FRAGMENT);
                startIntent.putExtra(CourseDetailsTabActivity.TITLES, Const.MY_COURSE_TITLE);
                startIntent.putExtra(CourseDetailsTabActivity.TITLE, "我的课程");
                startIntent.putExtra(
                        CourseDetailsTabActivity.FRAGMENT, "");
            }
        };
        app.mEngine.runNormalPlugin("CourseDetailsTabActivity", mActivity, callback);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public String getTitle() {
        return mTitle;
    }
}
