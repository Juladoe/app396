package com.edusoho.kuozhi.ui.classRoom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.ClassRoom;
import com.edusoho.kuozhi.model.ClassRoomDetailsResult;
import com.edusoho.kuozhi.model.Member;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.PayStatus;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.shard.ShareHandler;
import com.edusoho.kuozhi.shard.ShareUtil;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.common.PayCourseActivity;
import com.edusoho.kuozhi.ui.course.CoursePaperActivity;
import com.edusoho.kuozhi.ui.fragment.course.CourseIntroductionFragment;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.FixHeightViewPager;
import com.edusoho.kuozhi.view.dialog.ExitCoursePopupDialog;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import extensions.PagerSlidingTabStrip;

/**
 * Created by howzhi on 15/7/13.
 */
public class ClassRoomPaperActivity extends CoursePaperActivity {

    protected int mClassRoomId;
    protected ViewStub mClassRoomServiceLayout;
    private ClassRoomDetailsResult mClassRoomDetailsResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titles = new String[]{ "班级课程", "班级简介" };
    }

    protected void initIntentData() {
        Intent data = getIntent();
        if (data != null) {
            mTitle = data.getStringExtra(Const.ACTIONBAR_TITLE);
            mCoursePic = data.getStringExtra(COURSE_PIC);
            mClassRoomId = data.getIntExtra(Const.ID, 0);
        }

        setBackMode(BACK, TextUtils.isEmpty(mTitle) ? "课程标题" : mTitle);
    }

    protected void initFragmentPaper() {
        mPayBtn = findViewById(R.id.course_pay_btn);
        mRootView = (RelativeLayout) findViewById(R.id.course_root_view);
        mCoursePicView = (ImageView) findViewById(R.id.course_pic);
        mCourseScrollView = (RelativeLayout) findViewById(R.id.course_scroolview);
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.course_info_column_tabs);
        mFragmentPager = (FixHeightViewPager) findViewById(R.id.course_info_column_pager);
        mClassRoomServiceLayout = (ViewStub) findViewById(R.id.classroom_service_layout);

        //mTabs.setAlpha(0.87f);
        mPayBtn.setAlpha(0.8f);
        mPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayBtnAnim(0, 135);
                showPayBtn(mPayBtn);
            }
        });

        loadClassRoomInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.classroom_learning_menu, menu);
        return true;
    }

    @Override
    public void invoke(WidgetMessage message) {
        int type = message.type.code;
        switch (type) {
            case PayCourseActivity.PAY_SUCCESS:
                longToast("支付完成");
                loadClassRoomMember(null);
                break;
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(PayCourseActivity.PAY_SUCCESS, source),
                new MessageType(PayCourseActivity.PAY_EXIT, source)
        };
        return messageTypes;
    }

    @Override
    protected void shardCourse() {

        ClassRoom classRoom = mClassRoomDetailsResult.classRoom;
        StringBuilder stringBuilder = new StringBuilder(app.host);
        stringBuilder
                .append(Const.SHARD_CLASSROOM_URL)
                .append(classRoom.id);
        ShareUtil shareUtil = ShareUtil.getShareUtil(mActivity);
        shareUtil.initShareParams(
                R.drawable.icon,
                classRoom.title,
                stringBuilder.toString(),
                AppUtil.coverCourseAbout(classRoom.title + "：" + classRoom.about + "地址：" + stringBuilder.toString()),
                AQUtility.getCacheFile(AQUtility.getCacheDir(mContext), classRoom.largePicture),
                app.host
        );
        shareUtil.show(new ShareHandler() {
            @Override
            public void handler(String type) {
                //朋友圈
                int wxType = SendMessageToWX.Req.WXSceneTimeline;
                if ("Wechat".equals(type)) {
                    wxType = SendMessageToWX.Req.WXSceneSession;
                }
                shardClassRoomToMM(mClassRoomDetailsResult.classRoom, mContext, wxType);
            }
        });
    }

    protected boolean shardClassRoomToMM(ClassRoom classRoom, Context context, int type) {
        String APP_ID = getResources().getString(R.string.app_id);
        IWXAPI wxApi;
        wxApi = WXAPIFactory.createWXAPI(context, APP_ID, true);
        wxApi.registerApp(APP_ID);
        WXTextObject wXTextObject = new WXTextObject();
        wXTextObject.text = "分享班级";
        WXWebpageObject wxobj = new WXWebpageObject();
        StringBuilder stringBuilder = new StringBuilder(app.host);
        stringBuilder
                .append(Const.SHARD_CLASSROOM_URL)
                .append(classRoom.id);

        wxobj.webpageUrl = stringBuilder.toString();
        WXMediaMessage wXMediaMessage = new WXMediaMessage();
        wXMediaMessage.mediaObject = wxobj;
        wXMediaMessage.description = AppUtil.coverCourseAbout(classRoom.about);
        wXMediaMessage.title = classRoom.title;
        wXMediaMessage.setThumbImage(ImageLoader.getInstance().loadImageSync(mCoursePic, new ImageSize(50, 50)));

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.scene = type;
        req.transaction = System.currentTimeMillis() + "";
        req.message = wXMediaMessage;
        return wxApi.sendReq(req);
    }

    @Override
    protected void unLearnCourse() {
        ExitCoursePopupDialog.create(mActivity, new ExitCoursePopupDialog.PopupClickListener() {
            @Override
            public void onClick(int button, int position, String selStr) {
                if (button == ExitCoursePopupDialog.CANCEL) {
                    return;
                }

                RequestUrl requestUrl = app.bindUrl(Const.UN_LEARN_CLASSROOM, true);
                requestUrl.setParams(new String[]{
                        Const.CLASSROOM_ID, String.valueOf(mClassRoomId),
                        "reason", selStr,
                        "targetType", "classroom"
                });
                mActivity.ajaxPost(requestUrl, new ResultCallback() {
                    @Override
                    public void callback(String url, String object, AjaxStatus ajaxStatus) {
                        Log.d(null, "exit classroom->");
                        boolean result = mActivity.parseJsonValue(
                                object, new TypeToken<Boolean>() {
                                }
                        );
                        if (result) {
                            mClassRoomDetailsResult.member = null;
                            showCourseInfo();
                            mFragmentPager.setCurrentItem(0, false);
                            mPayBtn.setVisibility(View.VISIBLE);
                        } else {
                            mActivity.longToast("退出学习失败");
                        }
                    }
                });
            }
        }).show();
    }

    private void learnClassRoom() {
        final LoadDialog loadDialog = LoadDialog.create(mContext);
        loadDialog.show();

        RequestUrl url = app.bindUrl(Const.PAYCLASSROOM, true);
        url.setParams(new String[]{
                "targetType", "classroom",
                "targetId", String.valueOf(mClassRoomId)
        });

        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                PayStatus payStatus = parseJsonValue(
                        object, new TypeToken<PayStatus>() {
                        }
                );

                if (payStatus == null) {
                    loadDialog.dismiss();
                    longToast("加入学习失败！");
                    return;
                }

                if (!Const.RESULT_OK.equals(payStatus.status)) {
                    loadDialog.dismiss();
                    longToast(payStatus.message);
                    return;
                }

                //免费课程
                if (payStatus.paid) {
                    loadClassRoomMember(loadDialog);
                    return;
                }
                loadDialog.dismiss();
                payClassRoom(payStatus.payUrl);
            }
        });
    }

    private void payClassRoom(final String payUrl) {
        app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(FragmentPageActivity.FRAGMENT, "AlipayFragment");
                startIntent.putExtra(Const.ACTIONBAR_TITLE, "支付班级-" + mTitle);
                startIntent.putExtra("payurl", payUrl);
            }
        });
    }

    protected void loadClassRoomMember(final LoadDialog loadDialog) {
        RequestUrl url = app.bindUrl(Const.CLASSROOM_MEMBER, true);
        url.setParams(new String[]{
                "classRoomId", String.valueOf(mClassRoomId)
        });

        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                if (loadDialog != null) {
                    loadDialog.dismiss();
                }
                Member member = parseJsonValue(
                        object, new TypeToken<Member>() {
                        }
                );

                Log.d(null, "ClassRoom Member->" + member);
                mClassRoomDetailsResult.member = member;
                invalidateOptionsMenu();//刷新菜单
                if (member != null) {
                    selectLessonFragment();
                }
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mClassRoomDetailsResult != null) {
            MenuItem exitItem = menu.findItem(R.id.course_details_menu_exit);
            if (exitItem != null && (mClassRoomDetailsResult.member == null
                    || mClassRoomDetailsResult.member.role == Member.Role.teacher)) {
                exitItem.setVisible(false);
            }
        } else {
            menu.findItem(R.id.course_details_menu_more).setVisible(false);
        }
        return true;
    }

    @Override
    protected Bundle getBundle(String fragmentName) {
        ClassRoom classRoom = mClassRoomDetailsResult.classRoom;
        Bundle bundle = new Bundle();
        if (fragmentName.equals("CourseIntroductionFragment")) {
            bundle.putStringArray(CourseIntroductionFragment.TITLES, new String[]{
                    "班级介绍"
            });
            bundle.putStringArray(CourseIntroductionFragment.CONTENTS, new String[]{
                    classRoom.about
            });
        } else if (fragmentName.equals("ClassRoomCourseFragment")) {
            bundle.putInt(Const.CLASSROOM_ID, classRoom.id);
        }

        return bundle;
    }

    @Override
    protected void initPayBtn(View contentView) {
        TextView vipLearnBtn = (TextView) contentView.findViewById(R.id.course_details_vip_learnbtn);
        TextView learnBtn = (TextView) contentView.findViewById(R.id.course_details_learnbtn);

        final ClassRoom classRoom = mClassRoomDetailsResult.classRoom;
        String vipLevelName = getVipLevelName(classRoom.vipLevelId, mClassRoomDetailsResult.vipLevels);

        if (classRoom.price <= 0) {
            learnBtn.setText("加入学习");
        }
        if (classRoom.vipLevelId != 0 && !TextUtils.isEmpty(vipLevelName)) {
            vipLearnBtn.setText(vipLevelName + "免费学");
        } else {
            vipLearnBtn.setVisibility(View.GONE);
        }

        learnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (app.loginUser == null) {
                    LoginActivity.startForResult(mActivity);
                    return;
                }
                learnClassRoom();
            }
        });
        vipLearnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (app.loginUser != null) {
                    learnClassRoomByVip();
                } else {
                    LoginActivity.startForResult(mActivity);
                }
            }
        });
    }

    private void learnClassRoomByVip() {
        RequestUrl url = app.bindUrl(Const.VIP_LEARN_CLASSROOM, true);
        url.setParams(new String[]{
                "classRoomId", String.valueOf(mClassRoomId)
        });
        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                boolean status = parseJsonValue(
                        object, new TypeToken<Boolean>() {
                        }
                );

                if (status) {
                    LoadDialog loadDialog = LoadDialog.create(mActivity);
                    loadDialog.show();
                    loadClassRoomMember(loadDialog);
                    longToast("加入学习成功!");
                }
            }
        });
    }

    private void loadClassRoomInfo() {
        final View mLoadView = getLoadView();
        mRootView.addView(
                mLoadView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        RequestUrl url = app.bindUrl(Const.CLASSROOM, true);
        url.setParams(new String[]{
                "id", String.valueOf(mClassRoomId)
        });

        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                parseRequestData(object);
                mLoadView.setVisibility(View.GONE);
            }

            @Override
            public void update(String url, String object, AjaxStatus ajaxStatus) {
            }
        });

    }

    protected void parseRequestData(String object) {
        addScrollListener();
        mClassRoomDetailsResult = mActivity.parseJsonValue(
                object, new TypeToken<ClassRoomDetailsResult>() {
                }
        );

        if (mClassRoomDetailsResult == null || mClassRoomDetailsResult.classRoom == null) {
            longToast("加载班级信息出现错误！请尝试重新打开班级！");
            return;
        }

        invalidateOptionsMenu();
        if (TextUtils.isEmpty(mCoursePic)) {
            mCoursePic = mClassRoomDetailsResult.classRoom.largePicture;
            loadCoursePic();
        }

        mClassRoomServiceLayout.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub viewStub, View view) {
                LinearLayout layout = (LinearLayout) view;
                AppUtil.createClassRoomServiceView(
                        mContext,
                        layout,
                        mClassRoomDetailsResult.classRoom.service
                );
            }
        });
        mClassRoomServiceLayout.inflate();

        if (TextUtils.isEmpty(mTitle)) {
            mTitle = mClassRoomDetailsResult.classRoom.title;
            setTitle(mTitle);
        }

        fragmentArrayList = new String[]{
                "ClassRoomCourseFragment", "CourseIntroductionFragment"
        };

        fragmentAdapter = new MyPagerAdapter(
                mActivity.getSupportFragmentManager(), fragmentArrayList, titles);
        mTabs.setIndicatorColorResource(R.color.action_bar_bg);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mFragmentPager.setPageMargin(pageMargin);
        mFragmentPager.setOffscreenPageLimit(fragmentArrayList.length);

        mFragmentPager.setAdapter(fragmentAdapter);
        mTabs.setViewPager(mFragmentPager);

        changeColor(currentColor);
        initClassRoomInfo();
    }

    private void initClassRoomInfo() {
        mCourseTitleView = (TextView) findViewById(R.id.course_title);
        mCourseStudentNumView = (TextView) findViewById(R.id.course_student_num);
        mCourseStarView = (TextView) findViewById(R.id.course_student_star);
        mCoursePriceView = (TextView) findViewById(R.id.course_student_price);

        ClassRoom classRoom = mClassRoomDetailsResult.classRoom;
        mCourseTitleView.setText(classRoom.title);
        if (!"opened".equals(classRoom.showStudentNumType)) {
            mCourseStudentNumView.setVisibility(View.GONE);
        }
        mCourseStudentNumView.setText(classRoom.studentNum);
        mCourseStarView.setText(String.format("%.1f分", classRoom.rating));
        mCoursePriceView.setText(classRoom.price > 0 ? String.format("%.2f", classRoom.price) : "免费");

        Member member = mClassRoomDetailsResult.member;
        if (member != null) {
            selectLessonFragment();
        }
    }

    protected void selectLessonFragment() {
        mFragmentPager.setCurrentItem(1, false);
        handler.postAtTime(new Runnable() {
            @Override
            public void run() {
                hideCourseInfo();
            }
        }, SystemClock.uptimeMillis() + 240);

        if (mPayPopupWindow != null) {
            mPayPopupWindow.dismiss();
        }
        mPayBtn.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAY_COURSE_REQUEST && resultCode == PAY_COURSE_SUCCESS) {
            loadClassRoomMember(null);
            return;
        }

        if (requestCode == LoginActivity.LOGIN && resultCode == LoginActivity.OK) {
            loadClassRoomMember(null);
        }
    }
}
