package com.edusoho.kuozhi.v3.plugin;

import android.content.Intent;
import android.util.Log;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.ui.WebViewActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.FragmentNavigationDrawer;
import com.edusoho.kuozhi.v3.util.Const;
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
        Log.d("MenuClickPlugin-->", "MenuClickPlugin");
        if (action.equals("openDrawer")) {
            String message = args.getString(0);
            if (message.equals("open")) {
                EdusohoApp.app.sendMsgToTarget(Const.MAIN_MENU_OPEN, null, FragmentNavigationDrawer.class);
            }
        } else if (action.equals("openWebView")) {
            final String jsonObject = args.getString(0);
            EdusohoApp.app.mEngine.runNormalPlugin("WebViewActivity", cordova.getActivity(), new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(WebViewActivity.URL, jsonObject);
                }
            });
        } else if (action.equals("closeWebView")) {
            EdusohoApp.app.sendMsgToTarget(WebViewActivity.CLOSE, null, WebViewActivity.class);
        } else if (action.equals("getUserToken")) {
            JSONObject result = new JSONObject();
            if (EdusohoApp.app.loginUser != null) {
                result.put("user", EdusohoApp.app.loginUser);
                result.put("token", EdusohoApp.app.token);
            }
            callbackContext.success(result);
        } else if (action.equals("saveUserToken")) {
            BaseActivity baseActivity = (BaseActivity) EdusohoApp.app.mActivity;
            EdusohoApp.app.loginUser = baseActivity.parseJsonValue(args.getJSONObject(0).getString("user"), new TypeToken<User>() {
            });
            EdusohoApp.app.token = args.length() > 1 ? args.getString(1) : "";
        } else if (action.equals("share")) {
            String id = args.getString(0);
            String title = args.getString(1);
            String about = args.getString(2);
            String pic = args.getString(3);
            new ShareTool(cordova.getActivity(), id, title, about, pic).shardCourse();
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
            EdusohoApp.app.mEngine.runNormalPlugin(
                    LessonActivity.TAG, cordova.getActivity(), new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(Const.COURSE_ID, courseId);
                            startIntent.putExtra(Const.IS_LEARN, lessonId);
                        }
                    }
            );
        }
        return super.execute(action, args, callbackContext);
    }
}
