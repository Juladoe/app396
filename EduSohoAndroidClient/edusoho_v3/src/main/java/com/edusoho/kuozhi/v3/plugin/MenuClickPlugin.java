package com.edusoho.kuozhi.v3.plugin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.android.volley.Response;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.Lesson.LessonItem;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.ui.WebViewActivity;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.FragmentNavigationDrawer;
import com.edusoho.kuozhi.v3.ui.fragment.lesson.LiveLessonFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.OpenLoginUtil;
import com.edusoho.kuozhi.v3.view.webview.bridge.CoreBridge;
import com.google.gson.reflect.TypeToken;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JesseHuang on 15/6/2.
 */
public class MenuClickPlugin extends CoreBridge {

    @JavascriptInterface
    public void openDrawer() throws JSONException {
        String message = args.getString(0);
        if (message.equals("open")) {
            EdusohoApp.app.sendMsgToTarget(Const.MAIN_MENU_OPEN, null, FragmentNavigationDrawer.class);
        }
    }

    @JavascriptInterface
    public void openPlatformLogin() throws JSONException {
        String type = args.getString(0);
        OpenLoginUtil openLoginUtil = OpenLoginUtil.getUtil((ActionBarBaseActivity) mActivity);
        openLoginUtil.setLoginHandler(new NormalCallback<UserResult>() {
            @Override
            public void success(UserResult obj) {
                mActivity.finish();
            }
        });
        openLoginUtil.login(type);
    }

    @JavascriptInterface
    public void backWebView() throws JSONException {
        mActivity.app.sendMsgToTarget(WebViewActivity.BACK, null, mActivity);
    }

    @JavascriptInterface
    public void openWebView() throws JSONException {
        final String strUrl = args.getString(0);
        mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mActivity, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(WebViewActivity.URL, strUrl);
            }
        });
    }

    @JavascriptInterface
    public void closeWebView() throws JSONException {
        mActivity.app.sendMsgToTarget(WebViewActivity.CLOSE, null, mActivity);
    }

    @JavascriptInterface
    public JSONObject getUserToken() throws JSONException {
        JSONObject result = new JSONObject();
        if (EdusohoApp.app.loginUser != null) {
            result.put("user", EdusohoApp.app.loginUser);
            result.put("token", EdusohoApp.app.token);
        }

        return result;
    }

    @JavascriptInterface
    public void saveUserToken() throws JSONException {

        UserResult userResult = new UserResult();
        userResult.token = args.length() > 1 ? args.getString(1) : "";
        userResult.user = mActivity.parseJsonValue(args.getJSONObject(0).toString(), new TypeToken<User>() {
        });
        mActivity.app.saveToken(userResult);
        mActivity.app.sendMessage(Const.LOGIN_SUCCESS, null);
        Bundle bundle = new Bundle();
        bundle.putString(Const.BIND_USER_ID, userResult.user.id + "");
        mActivity.app.pushRegister(bundle);
    }

    @JavascriptInterface
    public void share() throws JSONException {
        String url = args.getString(0);
        String title = args.getString(1);
        String about = args.getString(2);
        String pic = args.getString(3);

        final ShareTool shareTool = new ShareTool(mActivity, url, title, about, pic);
        new Handler((mActivity.getMainLooper())).post(new Runnable() {
            @Override
            public void run() {
                shareTool.shardCourse();
            }
        });
    }

    @JavascriptInterface
    public void pay() throws JSONException {
        final String mTitle = args.getString(0);
        final String payUrl = args.getString(1);
        mActivity.app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(FragmentPageActivity.FRAGMENT, "AlipayFragment");
                startIntent.putExtra(Const.ACTIONBAR_TITLE, mTitle);
                startIntent.putExtra("payurl", payUrl);
            }
        });
    }

    @JavascriptInterface
    public void learnCourseLesson() throws JSONException {
        final int courseId = args.getInt(0);
        final int lessonId = args.getInt(1);

        RequestUrl requestUrl = EdusohoApp.app.bindUrl(Const.COURSELESSON, true);
        requestUrl.setParams(new String[]{
                "courseId", courseId + "",
                "lessonId", lessonId + ""
        });
        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                final LessonItem lessonItem = mActivity.parseJsonValue(response, new TypeToken<LessonItem>() {
                });
                if (lessonItem.type.equals("live")) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Const.ACTIONBAR_TITLE, lessonItem.title);
                    bundle.putLong(LiveLessonFragment.STARTTIME, Integer.valueOf(lessonItem.startTime) * 1000L);
                    bundle.putLong(LiveLessonFragment.ENDTIME, Integer.valueOf(lessonItem.endTime) * 1000L);
                    bundle.putInt(Const.COURSE_ID, lessonItem.courseId);
                    bundle.putInt(Const.LESSON_ID, lessonItem.id);
                    bundle.putString(LiveLessonFragment.SUMMARY, lessonItem.summary);
                    bundle.putString(LiveLessonFragment.REPLAYSTATUS, lessonItem.replayStatus);
                    bundle.putString(FragmentPageActivity.FRAGMENT, "LiveLessonFragment");
                    EdusohoApp.app.mEngine.runNormalPluginWithBundle("FragmentPageActivity", mActivity, bundle);
                } else {
                    EdusohoApp.app.mEngine.runNormalPlugin(
                            LessonActivity.TAG, mActivity, new PluginRunCallback() {
                                @Override
                                public void setIntentDate(Intent startIntent) {
                                    final String lessonJson = response;
                                    startIntent.putExtra(LessonActivity.LESSON_JSON, lessonJson);
                                    startIntent.putExtra(LessonActivity.LESSON_MODEL, lessonItem);
                                }
                            }
                    );
                }
            }
        }, null);
    }

    @JavascriptInterface
    public void showImages() throws JSONException {
        int index = args.getInt(0);
        JSONArray imageArray = args.getJSONArray(1);
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        String[] imgPaths = new String[imageArray.length()];
        for (int i = 0; i < imageArray.length(); i++) {
            imgPaths[i] = imageArray.getString(i);
        }
        bundle.putStringArray("images", imgPaths);
        mActivity.app.mEngine.runNormalPluginWithBundle("ViewPagerActivity", mActivity, bundle);
    }

    @JavascriptInterface
    public void clearUserToken() throws JSONException {
        mActivity.app.removeToken();
        mActivity.app.sendMessage(Const.LOGOUT_SUCCESS, null);
        mActivity.app.sendMsgToTarget(Const.MAIN_MENU_CLOSE, null, FragmentNavigationDrawer.class);
    }

    @JavascriptInterface
    public void showDownLesson() throws JSONException {
        final int courseId = args.getInt(0);
        mActivity.app.mEngine.runNormalPlugin(
                "LessonDownloadingActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.COURSE_ID, courseId);
                    }
                }
        );
    }

}
