package com.edusoho.kuozhi.v3.plugin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.ui.WebViewActivity;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.FragmentNavigationDrawer;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.OpenLoginUtil;
import com.edusoho.kuozhi.v3.util.VolleySingleton;
import com.edusoho.kuozhi.v3.util.annotations.JsAnnotation;
import com.edusoho.kuozhi.v3.util.volley.StringVolleyRequest;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.edusoho.kuozhi.v3.view.dialog.PopupInputDialog;
import com.edusoho.kuozhi.v3.view.webview.bridge.CoreBridge;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;

/**
 * Created by JesseHuang on 15/6/2.
 */
public class MenuClickPlugin extends CoreBridge {

    @JsAnnotation
    public void showInput(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String title = args.getString(0);
        String content = args.getString(1);
        String type = args.getString(2);
        final PopupInputDialog dlg = PopupInputDialog.create(mActivity, title, content, type);
        dlg.setOkListener(new PopupDialog.PopupClickListener() {
            @Override
            public void onClick(int button) {
                callbackContext.success(dlg.getInputString());
            }
        });
        dlg.show();
    }

    @JsAnnotation
    public void openDrawer(JSONArray args, CallbackContext callbackContext) throws JSONException {
        String message = args.getString(0);
        if (message.equals("open")) {
            EdusohoApp.app.sendMsgToTarget(Const.MAIN_MENU_OPEN, null, FragmentNavigationDrawer.class);
        }
    }

    @JsAnnotation
    public void openPlatformLogin(JSONArray args, CallbackContext callbackContext) throws JSONException {
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

    @JsAnnotation
    public void backWebView(JSONArray args, CallbackContext callbackContext) throws JSONException {
        mActivity.app.sendMsgToTarget(WebViewActivity.BACK, null, cordova.getActivity());
    }

    @JsAnnotation
    public void openWebView(JSONArray args, CallbackContext callbackContext) throws JSONException {
        final String strUrl = args.getString(0);
        mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mActivity, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(WebViewActivity.URL, strUrl);
            }
        });
    }

    @JsAnnotation
    public void closeWebView(JSONArray args, CallbackContext callbackContext) throws JSONException {
        mActivity.app.sendMsgToTarget(WebViewActivity.CLOSE, null, cordova.getActivity());
    }

    @JsAnnotation
    public JSONObject getUserToken(JSONArray args, CallbackContext callbackContext) throws JSONException {
        JSONObject result = new JSONObject();
        User user = EdusohoApp.app.loginUser;
        if (user != null) {
            result.put("user", new JSONObject(mActivity.gson.toJson(user)));
            result.put("token", EdusohoApp.app.token);
        }

        return result;
    }

    @JsAnnotation
    public void post(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String url = args.getString(0);
        JSONObject heads = args.getJSONObject(1);
        JSONObject params = args.getJSONObject(2);

        final RequestUrl requestUrl = new RequestUrl(url);

        Iterator<String> itor = heads.keys();
        while (itor.hasNext()) {
            String key = itor.next();
            requestUrl.heads.put(key, heads.getString(key));
        }

        itor = params.keys();
        while (itor.hasNext()) {
            String key = itor.next();
            requestUrl.params.put(key, params.getString(key));
        }

        VolleySingleton volley = VolleySingleton.getInstance(mActivity.getBaseContext());
        volley.getRequestQueue();

        StringVolleyRequest request = new StringVolleyRequest(
                Request.Method.POST, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callbackContext.error(error.getMessage());
            }
        });
        request.setTag(requestUrl.url);
        volley.addToRequestQueue(request);
    }

    @JsAnnotation
    public void saveUserToken(JSONArray args, CallbackContext callbackContext) throws JSONException {

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

    @JsAnnotation
    public void share(JSONArray args, CallbackContext callbackContext) throws JSONException {
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

    @JsAnnotation
    public void pay(JSONArray args, CallbackContext callbackContext) throws JSONException {
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

    @JsAnnotation
    public void showKeyInput(JSONArray args, CallbackContext callbackContext) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @JsAnnotation
    public void learnCourseLesson(JSONArray args, CallbackContext callbackContext) throws JSONException {
        final int courseId = args.getInt(0);
        final int lessonId = args.getInt(1);
        final int[] lessonArray = coverJsonArrayToIntArray(args.getJSONArray(2));
        EdusohoApp.app.mEngine.runNormalPlugin(
                LessonActivity.TAG, mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.COURSE_ID, courseId);
                        startIntent.putExtra(Const.LESSON_ID, lessonId);
                        startIntent.putExtra(LessonActivity.LESSON_IDS, lessonArray);
                    }
                }
        );
    }

    @JsAnnotation
    public void showImages(JSONArray args, CallbackContext callbackContext) throws JSONException {
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

    @JsAnnotation
    public void clearUserToken(JSONArray args, CallbackContext callbackContext) throws JSONException {
        mActivity.app.removeToken();
        mActivity.app.sendMessage(Const.LOGOUT_SUCCESS, null);
        mActivity.app.sendMsgToTarget(Const.MAIN_MENU_CLOSE, null, FragmentNavigationDrawer.class);
    }

    @JsAnnotation
    public void showDownLesson(JSONArray args, CallbackContext callbackContext) throws JSONException {
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

    @JsAnnotation
    public void startAppView(JSONArray args, CallbackContext callbackContext) throws JSONException {

        String name = args.getString(0);
        JSONObject data = args.getJSONObject(1);
        String type = args.getString(2);

        Bundle bundle = new Bundle();
        Iterator<String> iterator = data.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = data.get(key);

            if (value instanceof Integer) {
                bundle.putInt(key, (Integer) value);
            } else if (value instanceof Double) {
                bundle.putInt(key, ((Double) value).intValue());
            } else {
                bundle.putString(key, value.toString());
            }
        }
        if ("Fragment".equals(type)) {
            mActivity.app.mEngine.runPluginWithFragmentByBundle(name + "Fragment", mActivity, bundle);
        } else {
            mActivity.app.mEngine.runNormalPluginWithBundle(name + "Activity", mActivity, bundle);
        }
    }

    private int[] coverJsonArrayToIntArray(JSONArray jsonArray) {
        int length = jsonArray.length();
        int[] array = new int[length];
        for (int i=0; i < length; i++) {
            try {
                array[i] = jsonArray.getInt(i);
            } catch (Exception e) {
                array[i] = 0;
            }
        }

        return array;
    }
}
