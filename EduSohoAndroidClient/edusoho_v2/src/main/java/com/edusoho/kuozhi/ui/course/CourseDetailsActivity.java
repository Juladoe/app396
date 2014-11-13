package com.edusoho.kuozhi.ui.course;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ShardListAdapter;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.core.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseDetailsResult;
import com.edusoho.kuozhi.model.Member;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.PayStatus;
import com.edusoho.kuozhi.model.Vip;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.fragment.CourseDetailsFragment;
import com.edusoho.kuozhi.ui.fragment.MyCourseBaseFragment;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.nineoldandroids.animation.ObjectAnimator;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


import java.util.ArrayList;
import java.util.List;

import menudrawer.MenuDrawer;
import menudrawer.Position;

/**
 * Created by howzhi on 14-8-26.
 */
public class CourseDetailsActivity extends ActionBarBaseActivity
        implements MessageEngine.MessageCallback {

    public static final String CACHE = "cache";
    public static final String COURSE_PIC = "picture";
    public static final String TAG = "CourseDetailsActivity";
    public static final String FRAGMENT = "fragment";
    public static final int HIDE_COURSE_PIC = 0001;
    public static final int SHOW_COURSE_PIC = 0002;
    public static final int SET_LEARN_BTN = 0003;
    public static final int CHANGE_FRAGMENT = 0004;
    public static final int RELOAD_DATA = 0007;

    public static final int PAY_COURSE_SUCCESS = 0005;
    public static final int PAY_COURSE_REQUEST = 0006;

    private String mTitle;
    private int mCourseId;
    private String mCoursePic;
    private CourseDetailsResult mCourseDetailsResult;
    private String mCurrentFragment;
    private Class mCurrentFragmentClass;

    private View mBtnLayout;
    private ViewGroup mRootContent;
    private View mLoadView;
    private ImageView mCoursePicView;
    private FrameLayout mFragmentLayout;
    private Button mVipLearnBtn;
    private Button mLearnBtn;
    private int mVipLevelId;
    private int mCoursePicHeight;
    private double mPrice;

    protected MenuDrawer mMenuDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMenuDrawer();
        initView();
        app.registMsgSource(this);
    }

    private void initMenuDrawer() {
        mMenuDrawer = MenuDrawer.attach(
                mActivity, MenuDrawer.Type.OVERLAY, Position.RIGHT, MenuDrawer.MENU_DRAG_WINDOW);
        mMenuDrawer.setContentView(R.layout.course_details);
        mMenuDrawer.setMenuSize((int) (EdusohoApp.screenW * 0.8f));
        mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);

        mMenuDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == MenuDrawer.STATE_OPEN) {
                    mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
                } else if (newState == MenuDrawer.STATE_CLOSED) {
                    mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
            }
        });
    }

    public MenuDrawer getMenuDrawer() {
        return mMenuDrawer;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.course_details_menu_shard) {
            Log.d(null, "shard->");
            shardCourse();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mMenuDrawer.isMenuVisible()) {
            mMenuDrawer.closeMenu();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void invoke(WidgetMessage message) {
        int type = message.type.code;
        Bundle data = message.data;
        switch (type) {
            case RELOAD_DATA:
                reloadCoruseInfo();
                break;
            case SHOW_COURSE_PIC:
                AppUtil.animForHeight(
                        new EdusohoAnimWrap(mCoursePicView), 0, mCoursePicHeight, 320);
                break;
            case CHANGE_FRAGMENT:
                loadCoureDetailsFragment(data.getString(FRAGMENT));
                break;
            case HIDE_COURSE_PIC:
                Log.d(null, "mCoursePicHeight->" + mCoursePicHeight);
                mCoursePicHeight = mCoursePicView.getHeight();
                AppUtil.animForHeight(
                        new EdusohoAnimWrap(mCoursePicView), mCoursePicHeight, 0, 420);
                break;
            case SET_LEARN_BTN:
                mVipLevelId = data.getInt("vipLevelId", 0);
                mPrice = data.getDouble("price", 0);
                String vipLevelName = data.getString("vipLevelName");

                if (mPrice <= 0) {
                    mLearnBtn.setText("加入学习");
                }
                if (mVipLevelId != 0 && !TextUtils.isEmpty(vipLevelName)) {
                    mVipLearnBtn.setText(vipLevelName + "免费学");
                } else {
                    mVipLearnBtn.setVisibility(View.GONE);
                }

                showBtnLayout();
                break;
        }
    }

    private void showBtnLayout() {
        mBtnLayout.measure(0, 0);
        int height = mBtnLayout.getMeasuredHeight();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(
                new EdusohoAnimWrap(mBtnLayout), "height", 0, height);
        objectAnimator.setDuration(240);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.start();
    }

    private void hideBtnLayout() {
        int height = mBtnLayout.getHeight();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(
                new EdusohoAnimWrap(mBtnLayout), "height", height, 0);
        objectAnimator.setDuration(200);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.start();
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(SET_LEARN_BTN, source),
                new MessageType(HIDE_COURSE_PIC, source),
                new MessageType(CHANGE_FRAGMENT, source),
                new MessageType(SHOW_COURSE_PIC, source),
                new MessageType(RELOAD_DATA, source),
                new MessageType(Const.LOGING_SUCCESS),
        };
        return messageTypes;
    }

    private void initView() {
        Intent data = getIntent();
        if (data != null) {
            mTitle = data.getStringExtra(Const.ACTIONBAT_TITLE);
            mCoursePic = data.getStringExtra(COURSE_PIC);
            mCourseId = data.getIntExtra(Const.COURSE_ID, 0);
        }

        setBackMode(BACK, TextUtils.isEmpty(mTitle) ? "课程标题" : mTitle);

        mRootContent = (ViewGroup) findViewById(R.id.course_details_content);
        mCoursePicView = (ImageView) findViewById(R.id.course_details_header);
        mFragmentLayout = (FrameLayout) findViewById(android.R.id.list);
        mBtnLayout = findViewById(R.id.course_details_btn_layouts);
        mVipLearnBtn = (Button) findViewById(R.id.course_details_vip_learnbtn);
        mLearnBtn = (Button) findViewById(R.id.course_details_learnbtn);

        if (!TextUtils.isEmpty(mCoursePic)) {
            loadCoursePic();
        }
        loadCourseInfo();
        bindBtnClick();
    }

    public View getCoursePic() {
        return mCoursePicView;
    }

    public void addCoursePic() {
        ViewGroup coursePicParent = (ViewGroup) mCoursePicView.getParent();
        if (coursePicParent == null) {
            Log.d(null, "add course pic->");
            mRootContent.addView(mCoursePicView);
        }
    }

    private void loadCoursePic() {
        AQuery aQuery = new AQuery(mCoursePicView);
        aQuery.image(mCoursePic, false, true, 0, R.drawable.noram_course);
    }

    /**
     * 分享
     */
    private void shardCourse() {

        /*
        Course course = mCourseDetailsResult.course;
        StringBuilder stringBuilder = new StringBuilder(app.schoolHost);
        stringBuilder
                .append(Const.SHARD_COURSE_URL)
                .append("?courseId=")
                .append(course.id);
        ShareUtil shareUtil = new ShareUtil(mContext);
        shareUtil.initShareParams(
                R.drawable.icon,
                course.title,
                stringBuilder.toString(),
                AppUtil.coverCourseAbout(course.about),
                AQUtility.getCacheFile(AQUtility.getCacheDir(mContext), course.largePicture).getAbsolutePath(),
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
                shardToMM(mCourseDetailsResult.course, mContext, wxType);
            }
        });
        */
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");

        PackageManager pManager = getPackageManager();
        List<ResolveInfo> list = pManager.queryIntentActivities(
                intent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        list = filterShardList(list);
        if (list.isEmpty()) {
            longToast("系统没有可分享的应用!");
            return;
        }
        ListView listView = new ListView(mContext);
        ShardListAdapter adapter = new ShardListAdapter(mContext, list, R.layout.shard_list_item);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog alertDialog = builder
                .setTitle("分享课程")
                .setView(listView)
                .create();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //SendMessageToWX.Req.WXSceneTimeline
                ResolveInfo info = (ResolveInfo) adapterView.getItemAtPosition(i);
                int wxType = SendMessageToWX.Req.WXSceneTimeline;
                if ("com.tencent.mm.ui.tools.ShareImgUI".equals(info.activityInfo.name)) {
                    wxType = SendMessageToWX.Req.WXSceneSession;
                }
                if (shardToMM(mCourseDetailsResult.course, mContext, wxType)) {
                    if (wxType == SendMessageToWX.Req.WXSceneSession) {
                        //longToast("分享成功!");
                    }
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }

    private List<ResolveInfo> filterShardList(List<ResolveInfo> list) {
        List<ResolveInfo> newList = new ArrayList<ResolveInfo>();
        for (ResolveInfo info : list) {
            String packageName = info.activityInfo.packageName;
            if ("com.tencent.mm".equals(packageName)) {
                newList.add(info);
            }
        }

        return newList;
    }

    private boolean shardToMM(Course course, Context context, int type) {
        String APP_ID = getResources().getString(R.string.app_id);
        IWXAPI wxApi;
        wxApi = WXAPIFactory.createWXAPI(context, APP_ID, true);
        wxApi.registerApp(APP_ID);
        WXTextObject wXTextObject = new WXTextObject();
        wXTextObject.text = "分享课程";
        WXWebpageObject wxobj = new WXWebpageObject();
        StringBuilder stringBuilder = new StringBuilder(app.schoolHost);
        stringBuilder
                .append(Const.SHARD_COURSE_URL)
                .append("?courseId=")
                .append(course.id);

        wxobj.webpageUrl = stringBuilder.toString();
        WXMediaMessage wXMediaMessage = new WXMediaMessage();
        wXMediaMessage.mediaObject = wxobj;
        wXMediaMessage.description = AppUtil.coverCourseAbout(course.about);
        wXMediaMessage.title = course.title;
        wXMediaMessage.setThumbImage(app.query.getCachedImage(mCoursePic, 99));

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.scene = type;
        req.transaction = System.currentTimeMillis() + "";
        req.message = wXMediaMessage;
        return wxApi.sendReq(req);
    }

    private void reloadCoruseInfo()
    {
        RequestUrl url = app.bindUrl(Const.COURSE, true);
        url.setParams(new String[]{
                "courseId", mCourseId + ""
        });

        setProgressBarIndeterminateVisibility(true);
        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                if (ajaxStatus.getCode() != Const.CACHE_CODE) {
                    setProgressBarIndeterminateVisibility(false);
                }

                mCourseDetailsResult = mActivity.parseJsonValue(
                        object, new TypeToken<CourseDetailsResult>() {
                });

                if (mCourseDetailsResult == null) {
                    return;
                }

                app.sendMsgToTarget(CourseDetailsFragment.DATA_UPDATE, null, CourseDetailsFragment.class);
            }

            @Override
            public void update(String url, String object, AjaxStatus ajaxStatus) {
                setProgressBarIndeterminateVisibility(false);
            }
        });
    }

    private void loadCourseInfo() {
        mLoadView = getLoadView();
        mFragmentLayout.addView(mLoadView);

        RequestUrl url = app.bindUrl(Const.COURSE, true);
        url.setParams(new String[]{
                "courseId", mCourseId + ""
        });

        setProgressBarIndeterminateVisibility(true);
        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                if (ajaxStatus.getCode() != Const.CACHE_CODE) {
                    setProgressBarIndeterminateVisibility(false);
                }

                parseRequestData(object);
            }

            @Override
            public void update(String url, String object, AjaxStatus ajaxStatus) {
                setProgressBarIndeterminateVisibility(false);
                updateRequestData(object);
            }
        });
    }

    private void updateRequestData(String object) {
        mCourseDetailsResult = mActivity.parseJsonValue(
                object, new TypeToken<CourseDetailsResult>() {
        });
        if (mCourseDetailsResult == null || mCourseDetailsResult.course == null) {
            longToast("加载课程信息出现错误！请尝试重新打开课程！");
            return;
        }

        Member member = mCourseDetailsResult.member;
        String fragment = member != null
                ? "CourseLearningFragment" : "CourseDetailsFragment";

        if (fragment.equals(mCurrentFragment)) {
            app.sendMsgToTarget(BaseFragment.DATA_UPDATE, null, mCurrentFragmentClass);
            return;
        }
        loadCoureDetailsFragment(fragment);
    }

    private void parseRequestData(String object) {
        mLoadView.setVisibility(View.GONE);
        mCourseDetailsResult = mActivity.parseJsonValue(
                object, new TypeToken<CourseDetailsResult>() {
        });
        if (mCourseDetailsResult == null || mCourseDetailsResult.course == null) {
            longToast("加载课程信息出现错误！请尝试重新打开课程！");
            return;
        }

        if (TextUtils.isEmpty(mCoursePic)) {
            mCoursePic = mCourseDetailsResult.course.largePicture;
            loadCoursePic();
        }

        if (TextUtils.isEmpty(mTitle)) {
            mTitle = mCourseDetailsResult.course.title;
            setTitle(mTitle);
        }
        Member member = mCourseDetailsResult.member;
        String fragment = member != null
                ? "CourseLearningFragment" : "CourseDetailsFragment";
        loadCoureDetailsFragment(fragment);
    }

    private void bindBtnClick() {
        mLearnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (app.loginUser == null) {
                    LoginActivity.startForResult(mActivity);
                    return;
                }
                if (mPrice > 0) {
                    app.mEngine.runNormalPluginForResult(
                            "PayCourseActivity", mActivity, PAY_COURSE_REQUEST, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra("price", mPrice);
                            startIntent.putExtra("title", mTitle);
                            startIntent.putExtra("payurl", mTitle);
                            startIntent.putExtra("courseId", mCourseId);
                        }
                    });
                } else {
                    learnCourse();
                }
            }
        });

        mVipLearnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (app.loginUser != null) {
                    Course course = mCourseDetailsResult.course;
                    Vip userVip = app.loginUser.vip;
                    if (userVip == null) {
                        longToast("不是会员！无法使用会员服务！");
                        return;
                    }
                    if (userVip.levelId < course.vipLevelId) {
                        longToast("会员等级不够！");
                        return;
                    }
                    learnCourseByVip();
                } else {
                    LoginActivity.start(mActivity);
                }
            }
        });
    }

    private void learnCourseByVip() {
        setProgressBarIndeterminateVisibility(true);
        RequestUrl url = app.bindUrl(Const.VIP_LEARN_COURSE, true);
        url.setParams(new String[]{
                "courseId", mCourseId + ""
        });
        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                setProgressBarIndeterminateVisibility(false);
                boolean status = parseJsonValue(
                        object, new TypeToken<Boolean>() {
                });

                if (status) {
                    loadCoureDetailsFragment("CourseLearningFragment");
                }
            }
        });
    }

    private void learnCourse() {
        setProgressBarIndeterminateVisibility(true);
        RequestUrl url = app.bindUrl(Const.PAYCOURSE, true);
        url.setParams(new String[]{
                "payment", "alipay",
                "courseId", mCourseId + ""
        });
        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                setProgressBarIndeterminateVisibility(false);
                PayStatus payStatus = parseJsonValue(
                        object, new TypeToken<PayStatus>() {
                });

                if (payStatus == null) {
                    longToast("加入学习失败！");
                    return;
                }

                if (!Const.RESULT_OK.equals(payStatus.status)) {
                    longToast(payStatus.message);
                    return;
                }

                if (payStatus.paid) {
                    //免费课程
                    loadCoureDetailsFragment("CourseLearningFragment");
                }
            }
        });
    }

    public CourseDetailsResult getCourseDetailsInfo() {
        return mCourseDetailsResult;
    }

    private View getLoadView() {
        View loadView = LayoutInflater.from(mContext).inflate(R.layout.loading_layout, null);
        loadView.findViewById(R.id.load_text).setVisibility(View.GONE);
        return loadView;
    }

    private void loadCoureDetailsFragment(String fragmentName) {
        hideBtnLayout();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment = app.mEngine.runPluginWithFragment(
                fragmentName, mActivity, new PluginFragmentCallback() {
            @Override
            public void setArguments(Bundle bundle) {
                bundle.putInt(Const.COURSE_ID, mCourseId);
                bundle.putString(Const.ACTIONBAT_TITLE, mTitle);
            }
        });
        fragmentTransaction.replace(android.R.id.list, fragment);
        fragmentTransaction.commit();

        mCurrentFragment = fragmentName;
        mCurrentFragmentClass = fragment.getClass();
    }

    @Override
    protected void onDestroy() {
        app.sendMessage(MyCourseBaseFragment.RELOAD, null);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAY_COURSE_REQUEST && resultCode == PAY_COURSE_SUCCESS) {
            loadCoureDetailsFragment("CourseLearningFragment");
            return;
        }

        if (requestCode == LoginActivity.LOGIN && resultCode == LoginActivity.OK) {
            checkUserMember();
        }
    }

    private void checkUserMember()
    {
        final LoadDialog loadDialog = LoadDialog.create(mActivity);
        loadDialog.setMessage("正在获取课程信息...");
        loadDialog.show();

        RequestUrl requestUrl = app.bindUrl(Const.COURSE_MEMBER, true);
        requestUrl.setParams(new String[] {
                Const.COURSE_ID, String.valueOf(mCourseId)
        });

        ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                loadDialog.dismiss();
                Member member = mActivity.parseJsonValue(object, new TypeToken<Member>(){});
                String fragment = member != null
                        ? "CourseLearningFragment" : "CourseDetailsFragment";
                loadCoureDetailsFragment(fragment);
            }
       });
    }
}
