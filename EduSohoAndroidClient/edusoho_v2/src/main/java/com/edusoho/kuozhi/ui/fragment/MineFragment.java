package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.Service.EdusohoMainService;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.UserDataNum;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.plugin.CircularImageView;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import ch.boye.httpclientandroidlib.util.TextUtils;

/**
 * Created by Melomelon on 2014/12/26.
 */
public class MineFragment extends BaseFragment {

    public static final String TAG = "MineFragment";
    public String mTitle = "我";

    public static final int REFRESH = 0010;
    public static final int LOGINT_WITH_TOKEN = 0020;
    public static final int LOGOUT = 0021;

    private UserDataNum mUserDataNum;

    private CircularImageView mUserLogo;
    private TextView mUserName;
    private TextView mlogout;
    private TextView mSignature;
    private TextView mVip;
    private View mUserLayout;

    private LinearLayout mQuestion;
    private LinearLayout mDiscussion;
    private LinearLayout mNote;
    private LinearLayout mTestpaper;
    private TextView mQuestionNum;
    private TextView mDiscussionNum;
    private TextView mNoteNum;
    private TextView mTestpaperNum;

    private RelativeLayout mDownloadedCourse;
    private RelativeLayout mCollect;
    private RelativeLayout mNotification;
    private RelativeLayout mSetting;
    private RelativeLayout mFeedback;
    private AQuery mAQuery;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setContainerView(R.layout.mine_layout);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.me_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.me_menu_search) {
            app.mEngine.runNormalPlugin("QrSchoolActivity", mActivity, null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
        switch (message.type.code) {
            case REFRESH:
                setUserStatus();
                break;
            case LOGINT_WITH_TOKEN:
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

    public void setUserStatus() {
        if (app.loginUser == null) {
            //未登录状态
            mUserLogo.setImageResource(R.drawable.myinfo_default_face);
            mQuestionNum.setText("0");
            mDiscussionNum.setText("0");
            mNoteNum.setText("0");
            mTestpaperNum.setText("0");
            mUserName.setVisibility(View.GONE);
            mSignature.setVisibility(View.GONE);
            mlogout.setVisibility(View.VISIBLE);
            mSignature.setText("");

            mUserLayout.setOnClickListener(mLoginListener);
            mUserLogo.setOnClickListener(mLoginListener);

            mUserLayout.setEnabled(true);
        } else {
            //登录状态
            mUserName.setText(app.loginUser.nickname);
            mSignature.setText(app.loginUser.signature);
            returnObjectFormUserdata();
            if (app.loginUser.vip == null) {
                mVip.setVisibility(View.GONE);
            } else {
                mVip.setVisibility(View.VISIBLE);
            }
            mUserName.setVisibility(View.VISIBLE);
            mSignature.setVisibility(View.VISIBLE);
            mlogout.setVisibility(View.GONE);
            if (mAQuery == null) {
                mAQuery = new AQuery(mActivity);
            }
            mAQuery.id(mUserLogo).image(
                    app.loginUser.mediumAvatar, false, true, 200, R.drawable.myinfo_default_face);

            mUserLogo.setOnClickListener(mUserInfoClickListener);

            mUserLayout.setEnabled(false);
        }
    }

    private View.OnClickListener mLoginListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            LoginActivity.startForResult(mActivity);
        }
    };

    private View.OnClickListener mUserInfoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(FragmentPageActivity.FRAGMENT, "PersonalDetialsFragment");
                    startIntent.putExtra(Const.ACTIONBAR_TITLE, "详细资料");
                }
            });
        }
    };

    @Override
    protected void initView(View view) {
        super.initView(view);

        mUserLogo = (CircularImageView) view.findViewById(R.id.myinfo_logo);
        mUserName = (TextView) view.findViewById(R.id.tv_nickname);
        mlogout = (TextView) view.findViewById(R.id.tv_logout);
        mSignature = (TextView) view.findViewById(R.id.myinfo_signature);
        mVip = (TextView) view.findViewById(R.id.vip_icon);
        mUserLayout = view.findViewById(R.id.myinfo_user_layout);
        //mStatusLayout = (FrameLayout) view.findViewById(R.id.myinfo_status_layout);

        mQuestion = (LinearLayout) view.findViewById(R.id.myinfo_question);
        mDiscussion = (LinearLayout) view.findViewById(R.id.myinfo_discusion);
        mNote = (LinearLayout) view.findViewById(R.id.myinfo_note);
        mTestpaper = (LinearLayout) view.findViewById(R.id.myInfo_testpaper);
        mQuestionNum = (TextView) view.findViewById(R.id.myinfo_question_num);
        mDiscussionNum = (TextView) view.findViewById(R.id.myinfo_discusion_num);
        mNoteNum = (TextView) view.findViewById(R.id.myinfo_note_num);
        mTestpaperNum = (TextView) view.findViewById(R.id.myInfo_testpaper_num);

        mDownloadedCourse = (RelativeLayout) view.findViewById(R.id.my_downloaded_course);
        mCollect = (RelativeLayout) view.findViewById(R.id.my_collect);
        mNotification = (RelativeLayout) view.findViewById(R.id.my_notification);
        mSetting = (RelativeLayout) view.findViewById(R.id.my_setting);
        mFeedback = (RelativeLayout) view.findViewById(R.id.my_feedback);

        mQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyQuestionOrDiscuss("我的问答", "question", "暂无提问", R.drawable.icon_question);
            }
        });

        mDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyQuestionOrDiscuss("我的话题", "discussion", "暂无讨论", R.drawable.icon_discussion);
            }
        });

        mNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyNote();
            }
        });

        mTestpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyTestpaper();
            }
        });

        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "SettingFragment");
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, "设置");
                    }
                });
            }
        });

        mFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(FragmentPageActivity.FRAGMENT, "SuggestionFragment");
                bundle.putString(Const.ACTIONBAR_TITLE, "意见反馈");
                startAcitivityWithBundle("FragmentPageActivity", bundle);
            }
        });

        mNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (app.loginUser == null) {
                    LoginActivity.start(mActivity);
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString(Const.ACTIONBAR_TITLE, "消息");
                bundle.putString(FragmentPageActivity.FRAGMENT, "MessageFragment");
                app.mEngine.runNormalPluginWithBundle("FragmentPageActivity", mActivity, bundle);
            }
        });

        if (app.loginUser == null && !TextUtils.isEmpty(app.token)) {
            Log.d(null, "checkout token->");
            mActivity.getService().sendMessage(EdusohoMainService.LOGIN_WITH_TOKEN, null);
            return;
        }


        setUserStatus();
    }

    public void returnObjectFormUserdata() {
        RequestUrl url = app.bindUrl(Const.USER_DATA_NUMBER, true);
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                mUserDataNum = mActivity.parseJsonValue(
                        object, new TypeToken<UserDataNum>() {
                        });
                mQuestionNum.setText(mUserDataNum.thread);
                mDiscussionNum.setText(mUserDataNum.discussion);
                mNoteNum.setText(mUserDataNum.note);
                mTestpaperNum.setText(mUserDataNum.test);
            }
        });
    }


    private void showMyTestpaper() {
        if (app.loginUser == null) {
            LoginActivity.start(mActivity);
            return;
        }
        app.mEngine.runNormalPlugin(
                "FragmentPageActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "MyTestFragment");
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, "我的考试");
                    }
                });
    }

    public void showMyNote() {
        if (app.loginUser == null) {
            LoginActivity.start(mActivity);
            return;
        }
        PluginRunCallback callback = new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {

            }
        };
        app.mEngine.runNormalPlugin("NoteListActivity", mActivity, callback);
    }

    private void showMyQuestionOrDiscuss(final String title, final String type, final String emptyText, final int emptyIcon) {
        if (app.loginUser == null) {
            LoginActivity.start(mActivity);
            return;
        }
        PluginRunCallback callback = new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(Const.ACTIONBAR_TITLE, title);
                startIntent.putExtra(Const.QUESTION_TYPE, type);
                startIntent.putExtra("empty_text", emptyText);
                startIntent.putExtra("empty_icon", emptyIcon);
            }
        };
        app.mEngine.runNormalPlugin("QuestionNewActivity", mActivity, callback);
    }

    @Override
    public String getTitle() {
        return mTitle;
    }
}
