package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.Service.EdusohoMainService;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.MyInfoPlugin;
import com.edusoho.kuozhi.model.UserRole;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.course.CourseDetailsTabActivity;
import com.edusoho.kuozhi.ui.widget.LearnStatusWidget;
import com.edusoho.kuozhi.ui.widget.MyInfoPluginListView;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.annotations.ViewUtil;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.edusoho.kuozhi.view.plugin.CircularImageView;

/**
 * Created by howzhi on 14-8-14.
 */
public class MyInfoFragment extends BaseFragment {

    public String mTitle = "我的学习";

    @ViewUtil("myinfo_plugin_list")
    private MyInfoPluginListView mMyInfoPluginListView;

    @ViewUtil("myinfo_learnStatusWidget")
    private LearnStatusWidget mLearnStatusWidget;

    @ViewUtil("myinfo_user_layout")
    private View mUserLayout;

    @ViewUtil("myinfo_logo")
    private CircularImageView mUserLogo;

    @ViewUtil("myinfo_name")
    private TextView mUserName;

    @ViewUtil("myinfo_group")
    private TextView mUserGroup;

    @ViewUtil("myinfo_content")
    private TextView mUserContent;

    @ViewUtil("myinfo_status_layout")
    private FrameLayout mStatusLayout;

    public static final int REFRESH = 0010;
    public static final int LOGINT_WITH_TOKEN = 0020;
    public static final int LOGOUT = 0021;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.myinfo_layout);
    }

    private void setStatusLoginLayout()
    {
        mStatusLayout.removeAllViews();
        View view = LayoutInflater.from(mContext).inflate(R.layout.no_login_layout, null);
        mStatusLayout.addView(view);
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
        switch (message.type.code) {
            case REFRESH:
                Log.d(null, "REFRESH->");
                setUserStatus();
                break;
            case LOGINT_WITH_TOKEN:
                Log.d(null, "LOGINT_WITH_TOKEN->");
                setUserStatus();
                break;
            case LOGOUT:
                setUserStatus();
                break;
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(REFRESH, source),
                new MessageType(LOGOUT, source),
                new MessageType(LOGINT_WITH_TOKEN, source)
        };
        return messageTypes;
    }

    public void setUserStatus()
    {
        Log.d(null, "setUserStatus->");
        if (app.loginUser == null) {
            setStatusLoginLayout();
            mUserLogo.setImageResource(R.drawable.myinfo_default_face);
            mUserLayout.setEnabled(true);
            mUserLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginActivity.startForResult(mActivity);
                }
            });
            return;
        }

        mStatusLayout.removeAllViews();
        mUserLayout.setEnabled(false);
        mUserName.setText(app.loginUser.nickname);
        mUserGroup.setText(UserRole.coverRoleToStr(app.loginUser.roles));
        mUserContent.setText(app.loginUser.title);
        AQuery aQuery = new AQuery(mActivity);
        aQuery.id(mUserLogo).image(
                app.loginUser.mediumAvatar, false, true, 200, R.drawable.myinfo_default_face);

        if (app.loginUser != null) {
            mLearnStatusWidget.initialise(mActivity);
        }
    }

    @Override
    protected void initView(View view) {
        viewInject(view);

        mMyInfoPluginListView.initFromLocal(mActivity);
        mMyInfoPluginListView.setItemOnClick(new MyInfoPluginListView.PluginItemClick() {
            @Override
            public void onClick(final MyInfoPlugin plugin) {
                switch (plugin.action) {
                    case QUESTION:
                        redirectToMyQuestion();
                        break;
                    case COURSE:
                        showMyCourse();
                        break;
                    case TEST:
                        break;
                    case DISCUSS:
                        break;
                    case NOTE:
                        break;
                }
            }
        });

        if (app.loginUser == null && !"".equals(app.token)) {
            Log.d(null, "checkout token->");
            mActivity.getService().sendMessage(EdusohoMainService.LOGIN_WITH_TOKEN, null);
            return;
        }

        setUserStatus();
    }

    private void redirectToMyQuestion() {
        PluginRunCallback callback = new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {

            }
        };
        app.mEngine.runNormalPlugin("QuestionActivity", mActivity, callback);
    }

    private void showMyCourse()
    {
        PluginRunCallback callback = new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(CourseDetailsTabActivity.FRAGMENT_DATA, new Bundle());
                startIntent.putExtra(CourseDetailsTabActivity.LISTS, Const.MY_COURSE_FRAGMENT);
                startIntent.putExtra(CourseDetailsTabActivity.TITLES, Const.MY_COURSE_TITLE);
                startIntent.putExtra(Const.ACTIONBAT_TITLE, "我的课程");
                startIntent.putExtra(
                        CourseDetailsTabActivity.FRAGMENT, "");
            }
        };
        app.mEngine.runNormalPlugin("CourseDetailsTabActivity", mActivity, callback);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (app.loginUser != null) {
            mLearnStatusWidget.initialise(mActivity);
        }
    }

    @Override
    public String getTitle() {
        return mTitle;
    }
}
