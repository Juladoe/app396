package com.edusoho.kuozhi.v3.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.ui.MessageListFragment;
import com.edusoho.kuozhi.imserver.ui.listener.MessageControllerListener;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.PromiseCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.provider.CourseProvider;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.Promise;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.trinea.android.common.util.ToastUtils;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by JesseHuang on 15/9/16.
 */
public class NewsCourseActivity extends ActionBarBaseActivity {

    public static final int CLEAR = 0x10;
    public static final int SEND_IMAGE = 1;
    public static final int SEND_CAMERA = 2;

    public static final String COURSE_ID = "course_id";
    public static final String SHOW_TYPE = "show_type";
    public static final String CONV_NO = "conv_no";

    public static final int DISCUSS_TYPE = 0;
    public static final int LEARN_TYPE = 1;

    private static final String mFragmentTags[] = {"DiscussFragment", "CourseStudyFragment", "TeachFragment"};
    private static final String mEntranceType[] = {"Discuss", "StudyOrTeacher"};
    private static final String mRadioButtonTitle[] = {"学习", "教学"};

    private int mCourseId;
    private String mFragmentType;
    private String mCurrentFragmentTag;
    private String mUserTypeInCourse;
    private Course mCourse;
    private Handler mHandler;
    private MessageControllerListener.PhotoSelectCallback mPhotoSelectCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_course);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(mNewFragment2UpdateItemBadgeRunnable, 500);
    }

    private void initData() {
        mHandler = new Handler();
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mCourseId = intent.getIntExtra(COURSE_ID, 0);
        if (mCourseId == 0) {
            ToastUtils.show(mContext, "课程信息不存在!");
            return;
        }
        mFragmentType = getFragmentType(intent.getIntExtra(SHOW_TYPE, LEARN_TYPE));

        final LoadDialog loadDialog = LoadDialog.create(this);
        loadDialog.show();
        new CourseProvider(mContext).getCourse(mCourseId)
        .success(new NormalCallback<CourseDetailsResult>() {
            @Override
            public void success(CourseDetailsResult courseDetailsResult) {
                loadDialog.dismiss();
                if (courseDetailsResult == null || courseDetailsResult.course == null) {
                    ToastUtils.show(mContext, "课程信息不存在!");
                    return;
                }
                mCourse = courseDetailsResult.course;
                int userId = getAppSettingProvider().getCurrentUser().id;
                checkUserRole(userId);
            }
        });
    }

    private void checkUserRole(int userId) {
        getRoleInCourse(mCourseId, userId, new NormalCallback<String>() {
            @Override
            public void success(String role) {
                mUserTypeInCourse = role;
                if (PushUtil.ChatUserType.STUDENT.equals(mUserTypeInCourse)) {
                    initSwitchButton(BACK, mRadioButtonTitle[0], mOnCheckedChangeListener);
                    setRadioButtonChecked(mFragmentType.equals(mEntranceType[0]) ? R.id.rb_discuss : R.id.rb_study);
                } else if (PushUtil.ChatUserType.TEACHER.equals(mUserTypeInCourse)) {
                    initSwitchButton(BACK, mRadioButtonTitle[1], mOnCheckedChangeListener);
                    setRadioButtonChecked(mFragmentType.equals(mEntranceType[0]) ? R.id.rb_discuss : R.id.rb_study);
                } else {
                    CommonUtil.longToast(mContext, "您不是该课程的学生");
                    finish();
                }
            }
        });
    }

    private void showFragment(String tag) {
        Fragment fragment;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment currentFragment = mFragmentManager.findFragmentByTag(mCurrentFragmentTag);
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }
        fragment = mFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            fragmentTransaction.show(fragment);
        } else {
            if (tag.equals(mFragmentTags[0])) {
                fragment = app.mEngine.runPluginWithFragment(tag, mActivity, mStudyPluginFragmentCallback);
                mMessageListFragment = (MessageListFragment) fragment;
                initChatRoomController((MessageListFragment) fragment);
            } else if (tag.equals(mFragmentTags[1])) {
                fragment = app.mEngine.runPluginWithFragment(tag, mActivity, mStudyPluginFragmentCallback);
            } else if (tag.equals(mFragmentTags[2])) {
                fragment = app.mEngine.runPluginWithFragment(tag, mActivity, mTeachPluginFragmentCallback);
            }
            fragmentTransaction.add(R.id.fragment_container, fragment, tag);
        }
        fragmentTransaction.commit();
        mCurrentFragmentTag = tag;
        if (mCurrentFragmentTag.equals(mFragmentTags[0])) {
            setSwitchBadgeViewVisible(View.INVISIBLE);
        }
    }

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rb_study) {
                if (PushUtil.ChatUserType.STUDENT.equals(mUserTypeInCourse)) {
                    showFragment(mFragmentTags[1]);
                } else {
                    showFragment(mFragmentTags[2]);
                }
            } else if (checkedId == R.id.rb_discuss) {
                showFragment(mFragmentTags[0]);
            }
        }
    };

    private PluginFragmentCallback mTeachPluginFragmentCallback = new PluginFragmentCallback() {
        @Override
        public void setArguments(Bundle bundle) {
            String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.TEACHER_MANAGERMENT, mCourseId));
            bundle.putString(Const.WEB_URL, url);
        }
    };

    private PluginFragmentCallback mStudyPluginFragmentCallback = new PluginFragmentCallback() {
        @Override
        public void setArguments(Bundle bundle) {
            bundle.putString(MessageListFragment.CONV_NO, getIntent().getStringExtra(CONV_NO));
            bundle.putInt(MessageListFragment.TARGET_ID, mCourseId);
            bundle.putInt(Const.COURSE_ID, mCourseId);
            bundle.putString(MessageListFragment.TARGET_TYPE, Destination.COURSE);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.news_course_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.news_course_profile) {
            app.mEngine.runNormalPlugin("CourseDetailActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(Const.FROM_ID, mCourseId);
                    startIntent.putExtra(Const.FROM_ID, mCourseId);
                    startIntent.putExtra(ChatItemBaseDetail.CONV_NO, getIntent().getStringExtra(CONV_NO));
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        return new MessageType[]{
                new MessageType(Const.ADD_COURSE_DISCUSS_MSG, source),
                new MessageType(Const.TOKEN_LOSE),
                new MessageType(CLEAR)
        };
    }

    @Override
    public void invoke(WidgetMessage message) {
        processMessage(message);
        if (message.type.code == CLEAR) {
            mMessageListFragment.reload();
        }
    }

    private String getFragmentType(int showType) {
        if (showType == LEARN_TYPE) {
            return mEntranceType[1];
        }

        return mEntranceType[0];
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }

    private Runnable mNewFragment2UpdateItemBadgeRunnable = new Runnable() {
        @Override
        public void run() {
            Bundle bundle = new Bundle();
            bundle.putInt(Const.FROM_ID, mCourseId);
            bundle.putString(Const.NEWS_TYPE, PushUtil.ChatUserType.COURSE);
            app.sendMsgToTarget(NewsFragment.UPDATE_UNREAD_MSG, bundle, NewsFragment.class);
        }
    };

    private void getRoleInCourse(int courseId, int userId, final NormalCallback<String> normalCallback) {
        RequestUrl requestUrl = app.bindNewUrl(String.format(Const.ROLE_IN_COURSE, courseId, userId), true);
        ajaxGetWithCache(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.contains("membership")) {
                        JSONObject jsonObject = new JSONObject(response);
                        if ("teacher".equals(jsonObject.getString("membership"))) {
                            normalCallback.success("teacher");
                        } else if ("student".equals(jsonObject.getString("membership"))) {
                            normalCallback.success("student");
                        } else {
                            normalCallback.success("none");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    /*
        MessageControllerListener
     */
    private MessageListFragment mMessageListFragment;

    private void initChatRoomController(MessageListFragment messageListFragment) {
        messageListFragment.setMessageControllerListener(getMessageControllerListener());
    }

    private MessageControllerListener getMessageControllerListener() {
        return new MessageControllerListener() {
            @Override
            public void createConvNo(final ConvNoCreateCallback callback) {
                final LoadDialog loadDialog = LoadDialog.create(mActivity);
                loadDialog.show();
                createChatConvNo().then(new PromiseCallback<String>() {
                    @Override
                    public Promise invoke(String convNo) {
                        loadDialog.dismiss();
                        callback.onCreateConvNo(convNo);
                        return null;
                    }
                });
            }

            @Override
            public void createRole(RoleUpdateCallback callback) {
                createTargetRole(callback);
            }

            @Override
            public Map<String, String> getRequestHeaders() {
                HashMap map = new HashMap();
                map.put("Auth-Token", app.apiToken);
                return map;
            }

            @Override
            public void onShowImage(int index, ArrayList<String> imageList) {
                Bundle bundle = new Bundle();
                bundle.putInt("index", index);
                bundle.putStringArrayList("imageList", imageList);
                CoreEngine.create(mContext).runNormalPluginWithBundle("ViewPagerActivity", mContext, bundle);
            }

            @Override
            public void onShowUser(Role role) {
                Bundle bundle = new Bundle();
                School school = getAppSettingProvider().getCurrentSchool();
                bundle.putString(Const.WEB_URL, String.format(
                        Const.MOBILE_APP_URL,
                        school.url + "/",
                        String.format(Const.USER_PROFILE, role.getRid()))
                );
                CoreEngine.create(mContext).runNormalPluginWithBundle("WebViewActivity", mContext, bundle);
            }

            @Override
            public void onShowWebPage(String url) {
                Bundle bundle = new Bundle();
                bundle.putString(Const.WEB_URL, url);
                CoreEngine.create(mContext).runNormalPluginWithBundle("WebViewActivity", mContext, bundle);
            }

            @Override
            public void selectPhoto(PhotoSelectCallback callback) {
                mPhotoSelectCallback = callback;
                openPictureFromLocal();
            }

            @Override
            public void takePhoto(PhotoSelectCallback callback) {
                mPhotoSelectCallback = callback;
                openPictureFromCamera();
            }
        };
    }

    protected Promise createChatConvNo() {
        final Promise promise = new Promise();
        User currentUser = getAppSettingProvider().getCurrentUser();
        if (currentUser == null || currentUser.id == 0) {
            ToastUtils.show(getBaseContext(), "用户未登录");
            promise.resolve(null);
            return promise;
        }

        new CourseProvider(mContext).getCourse(mCourseId)
                .success(new NormalCallback<CourseDetailsResult>() {
                    @Override
                    public void success(CourseDetailsResult courseDetailsResult) {
                        if (courseDetailsResult == null
                                || courseDetailsResult.course == null
                                || courseDetailsResult.course.conversationId != null
                                ) {
                            ToastUtils.show(mContext, "创建聊天失败!");
                            return;
                        }
                        mCourse = courseDetailsResult.course;
                        promise.resolve(courseDetailsResult.course.conversationId);
                    }
                });

        return promise;
    }

    protected void createTargetRole(MessageControllerListener.RoleUpdateCallback callback) {
        Role role = new Role();
        role.setRid(mCourse.id);
        role.setAvatar(mCourse.middlePicture);
        role.setType(Destination.COURSE);
        role.setNickname(mCourse.title);
        callback.onCreateRole(role);
    }

    /**
     * 从图库获取图片
     */
    protected void openPictureFromLocal() {
        Intent intent = new Intent(getBaseContext(), MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 5);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        startActivityForResult(intent, SEND_IMAGE);
    }

    protected void openPictureFromCamera() {
        Intent intent = new Intent(getBaseContext(), MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_TAKE_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        startActivityForResult(intent, SEND_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == SEND_IMAGE) {
            List<String> pathList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (mPhotoSelectCallback != null) {
                mPhotoSelectCallback.onSelected(pathList);
            }
        }
    }
}
