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
    private TextView tvUserName;
    private TextView tvlogout;
    private TextView tvSignature;
    private TextView tvVip;
    private View mUserLayout;

    private LinearLayout mQuestion;
    private LinearLayout mDiscussion;
    private LinearLayout mNote;
    private LinearLayout mTestPaper;
    private TextView tvQuestionNum;
    private TextView tvDiscussionNum;
    private TextView tvNoteNum;
    private TextView tvTestPaperNum;

    private RelativeLayout mDownloadedCourse;
    private RelativeLayout mMyFavorite;
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
    protected void initView(View view) {
        super.initView(view);

        mUserLogo = (CircularImageView) view.findViewById(R.id.myinfo_logo);
        tvUserName = (TextView) view.findViewById(R.id.tv_nickname);
        tvlogout = (TextView) view.findViewById(R.id.tv_logout);
        tvSignature = (TextView) view.findViewById(R.id.myinfo_signature);
        tvVip = (TextView) view.findViewById(R.id.vip_icon);
        mUserLayout = view.findViewById(R.id.myinfo_user_layout);
        //mStatusLayout = (FrameLayout) view.findViewById(R.id.myinfo_status_layout);

        mQuestion = (LinearLayout) view.findViewById(R.id.myinfo_question);
        mDiscussion = (LinearLayout) view.findViewById(R.id.myinfo_discusion);
        mNote = (LinearLayout) view.findViewById(R.id.myinfo_note);
        mTestPaper = (LinearLayout) view.findViewById(R.id.myInfo_testpaper);
        tvQuestionNum = (TextView) view.findViewById(R.id.myinfo_question_num);
        tvDiscussionNum = (TextView) view.findViewById(R.id.myinfo_discusion_num);
        tvNoteNum = (TextView) view.findViewById(R.id.myinfo_note_num);
        tvTestPaperNum = (TextView) view.findViewById(R.id.myInfo_testpaper_num);

        mDownloadedCourse = (RelativeLayout) view.findViewById(R.id.course_downloaded);
        mMyFavorite = (RelativeLayout) view.findViewById(R.id.my_favorite);
        mNotification = (RelativeLayout) view.findViewById(R.id.my_notification);
        mSetting = (RelativeLayout) view.findViewById(R.id.my_setting);
        mFeedback = (RelativeLayout) view.findViewById(R.id.my_feedback);

        mQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showMyQuestionOrDiscuss("我的问答", "question", "暂无提问", R.drawable.icon_question);
                showLiveingCoure();
            }
        });

        mDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyQuestionOrDiscuss("我的讨论", "discussion", "暂无讨论", R.drawable.icon_discussion);
            }
        });

        mNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyNote();
            }
        });

        mTestPaper.setOnClickListener(new View.OnClickListener() {
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

        mMyFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "FavoriteCourseFragmentHorizontal");
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, "收藏");
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
                startActivityWithBundle("FragmentPageActivity", bundle);
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

        mDownloadedCourse.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                app.mEngine.runNormalPlugin("LocalCoruseActivity", mContext, null);
            }
        });

        if (app.loginUser == null && !TextUtils.isEmpty(app.token)) {
            Log.d(null, "checkout token->");
            mActivity.getService().sendMessage(EdusohoMainService.LOGIN_WITH_TOKEN, null);
            return;
        }


        setUserStatus();
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
            tvQuestionNum.setText("0");
            tvDiscussionNum.setText("0");
            tvNoteNum.setText("0");
            tvTestPaperNum.setText("0");
            tvUserName.setVisibility(View.GONE);
            tvSignature.setVisibility(View.GONE);
            tvlogout.setVisibility(View.VISIBLE);
            tvVip.setVisibility(View.GONE);
            tvSignature.setText("");

            mUserLayout.setOnClickListener(mLoginListener);
            mUserLogo.setOnClickListener(mLoginListener);

        } else {
            //登录状态
            tvUserName.setText(app.loginUser.nickname);
            if (TextUtils.isEmpty(app.loginUser.signature)) {
                tvSignature.setText("暂无个性签名");
            } else {
                tvSignature.setText(app.loginUser.signature);
            }
            returnObjectFormUserdata();
            if (app.loginUser.vip == null) {
                tvVip.setVisibility(View.GONE);
            } else {
                tvVip.setVisibility(View.VISIBLE);
            }
            tvUserName.setVisibility(View.VISIBLE);
            tvSignature.setVisibility(View.VISIBLE);
            tvlogout.setVisibility(View.GONE);
            if (mAQuery == null) {
                mAQuery = new AQuery(mActivity);
            }
            mAQuery.id(mUserLogo).image(
                    app.loginUser.mediumAvatar, false, true, 200, R.drawable.myinfo_default_face);

            mUserLogo.setOnClickListener(mUserInfoClickListener);
            mUserLayout.setOnClickListener(mUserInfoClickListener);

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
            Bundle bundle = new Bundle();
            bundle.putString(FragmentPageActivity.FRAGMENT, "ProfileFragment");
            bundle.putString(Const.ACTIONBAR_TITLE, "详细资料");
            app.mEngine.runNormalPluginWithBundle("FragmentPageActivity", mActivity, bundle);
            //app.mEngine.runNormalPlugin("TestActivity", mActivity, null);
        }
    };


    public void returnObjectFormUserdata() {
        RequestUrl url = app.bindUrl(Const.USER_DATA_NUMBER, true);
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                mUserDataNum = mActivity.parseJsonValue(
                        object, new TypeToken<UserDataNum>() {
                        });
                tvQuestionNum.setText(mUserDataNum.thread);
                tvDiscussionNum.setText(mUserDataNum.discussion);
                tvNoteNum.setText(mUserDataNum.note);
                tvTestPaperNum.setText(mUserDataNum.test);
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
                startIntent.putExtra(Const.QUESTION_URL, Const.QUESTION);
            }
        };
        app.mEngine.runNormalPlugin("QuestionNewActivity", mActivity, callback);
    }

    private void showLiveingCoure() {
        if (app.loginUser == null) {
            LoginActivity.start(mActivity);
            return;
        }
        PluginRunCallback callback = new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {

            }
        };
        app.mEngine.runNormalPlugin("liveingCourseActivity", mActivity, callback);
    }

    @Override
    public String getTitle() {
        return mTitle;
    }
}
