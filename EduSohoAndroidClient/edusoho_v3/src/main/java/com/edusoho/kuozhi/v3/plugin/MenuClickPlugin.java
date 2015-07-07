package com.edusoho.kuozhi.v3.plugin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

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
import com.google.gson.reflect.TypeToken;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JesseHuang on 15/6/2.
 */
public class MenuClickPlugin extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("openDrawer")) {
            String message = args.getString(0);
            if (message.equals("open")) {
                EdusohoApp.app.sendMsgToTarget(Const.MAIN_MENU_OPEN, null, FragmentNavigationDrawer.class);
            }
        } else if (action.equals("openPlatformLogin")) {
            String type = args.getString(0);
            OpenLoginUtil openLoginUtil = OpenLoginUtil.getUtil((ActionBarBaseActivity) cordova.getActivity());
            openLoginUtil.setLoginHandler(new NormalCallback<UserResult>() {
                @Override
                public void success(UserResult obj) {
                    cordova.getActivity().finish();
                }
            });
            openLoginUtil.login(type);
        } else if (action.equals("backWebView")) {
            EdusohoApp.app.sendMsgToTarget(WebViewActivity.BACK, null, cordova.getActivity());
        } else if (action.equals("openWebView")) {
            final String strUrl = args.getString(0);
            EdusohoApp.app.mEngine.runNormalPlugin("WebViewActivity", cordova.getActivity(), new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(WebViewActivity.URL, strUrl);
                }
            });
        } else if (action.equals("closeWebView")) {
            EdusohoApp.app.sendMsgToTarget(WebViewActivity.CLOSE, null, cordova.getActivity());
        } else if (action.equals("getUserToken")) {
            JSONObject result = new JSONObject();
            if (EdusohoApp.app.loginUser != null) {
                result.put("user", EdusohoApp.app.loginUser);
                result.put("token", EdusohoApp.app.token);
            }
            callbackContext.success(result);
        } else if (action.equals("saveUserToken")) {
            BaseActivity baseActivity = (BaseActivity) EdusohoApp.app.mActivity;
            UserResult userResult = new UserResult();
            userResult.token = args.length() > 1 ? args.getString(1) : "";
            userResult.user = baseActivity.parseJsonValue(args.getJSONObject(0).toString(), new TypeToken<User>() {
            });
            EdusohoApp.app.saveToken(userResult);
            EdusohoApp.app.sendMessage(Const.LOGIN_SUCCESS, null);
            Bundle bundle = new Bundle();
            bundle.putString(Const.BIND_USER_ID, userResult.user.id + "");
            EdusohoApp.app.pushRegister(bundle);
        } else if (action.equals("share")) {
            String url = args.getString(0);
            String title = args.getString(1);
            String about = args.getString(2);
            String pic = args.getString(3);

            final ShareTool shareTool = new ShareTool(cordova.getActivity(), url, title, about, pic);
            new Handler((cordova.getActivity().getMainLooper())).post(new Runnable() {
                @Override
                public void run() {
                    shareTool.shardCourse();
                }
            });

        } else if (action.equals("payCourse")) {
            final String mTitle = args.getString(0);
            final String payUrl = args.getString(1);
            EdusohoApp.app.mEngine.runNormalPlugin("FragmentPageActivity", cordova.getActivity(), new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(FragmentPageActivity.FRAGMENT, "AlipayFragment");
                    startIntent.putExtra(Const.ACTIONBAR_TITLE, mTitle);
                    startIntent.putExtra("payurl", payUrl);
                }
            });
        } else if (action.equals("learnCourseLesson")) {
            final int courseId = args.getInt(0);
            final int lessonId = args.getInt(1);
            final BaseActivity baseActivity = (BaseActivity) EdusohoApp.app.mActivity;
            RequestUrl requestUrl = EdusohoApp.app.bindUrl(Const.COURSELESSON, true);
            requestUrl.setParams(new String[]{
                    "courseId", courseId + "",
                    "lessonId", lessonId + ""
            });
            baseActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {
                    final LessonItem lessonItem = baseActivity.parseJsonValue(response, new TypeToken<LessonItem>() {
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
                        EdusohoApp.app.mEngine.runNormalPluginWithBundle("FragmentPageActivity", cordova.getActivity(), bundle);
                    } else {
                        EdusohoApp.app.mEngine.runNormalPlugin(
                                LessonActivity.TAG, cordova.getActivity(), new PluginRunCallback() {
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
        } else if (action.equals("showImages")) {
            int index = args.getInt(0);
            JSONArray imageArray = args.getJSONArray(1);
            Bundle bundle = new Bundle();
            bundle.putInt("index", index);
            String[] imgPaths = new String[imageArray.length()];
            for (int i = 0; i < imageArray.length(); i++) {
                imgPaths[i] = imageArray.getString(i);
            }
            bundle.putStringArray("images", imgPaths);
            EdusohoApp.app.mEngine.runNormalPluginWithBundle("ViewPagerActivity", cordova.getActivity(), bundle);
        } else if (action.equals("clearUserToken")) {
            EdusohoApp.app.removeToken();
            EdusohoApp.app.sendMessage(Const.LOGOUT_SUCCESS, null);
            EdusohoApp.app.sendMsgToTarget(Const.MAIN_MENU_CLOSE, null, FragmentNavigationDrawer.class);
        } else if (action.equals("showDownLesson")) {
            final int courseId = args.getInt(0);
            EdusohoApp.app.mEngine.runNormalPlugin(
                    "LessonDownloadingActivity", cordova.getActivity(), new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(Const.COURSE_ID, courseId);
                        }
                    }
            );
        }
        return super.execute(action, args, callbackContext);
    }
}
