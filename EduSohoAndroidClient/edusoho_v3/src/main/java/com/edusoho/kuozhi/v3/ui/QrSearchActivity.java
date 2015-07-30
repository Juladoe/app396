package com.edusoho.kuozhi.v3.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.Token;
import com.edusoho.kuozhi.v3.ui.fragment.AboutFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.edusoho.kuozhi.v3.view.qr.CaptureActivity;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by howzhi on 15/7/11.
 */
public class QrSearchActivity extends CaptureActivity {

    private List<URLMatchType> mURLMatchTypes;

    public static Pattern TYPE_PAT = Pattern.compile(
            "/([^/]+)/?(.+)",
            Pattern.DOTALL
    );

    @Override
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        String msg = rawResult.getText();
        if (TextUtils.isEmpty(msg)) {
            ToastUtils.show(mContext, "没有扫描到任何东西!");
            return;
        }

        initURLMatchType(msg);
        if (parseResult(msg)) {
            finish();
        }
    }

    private void initURLMatchType(String result) {
        mURLMatchTypes = new ArrayList<URLMatchType>();
        mURLMatchTypes.add(new LoginURLMatchType(result));
    }

    protected boolean parseResult(String result) {
        if (! (result.startsWith("http://") || result.startsWith("https://")) ) {
            showDataInWebView(result);
            return true;
        }
        URL resultUrl = null;
        try {
            resultUrl = new URL(result);
        } catch (Exception e) {
            e.printStackTrace();
            showDataInWebView(result);
            return true;
        }

        if (! resultUrl.getHost().equals(app.domain)) {
            showUrlInWebView(result);
            return true;
        }

        Matcher typeMatcher = TYPE_PAT.matcher(resultUrl.getPath());
        if (typeMatcher.find() && urlcanMatch(typeMatcher)) {
            return false;
        }

        showUrlInESWebView(result);
        return true;
    }

    private void showUrlInESWebView(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(WebViewActivity.URL, url);
        app.mEngine.runNormalPluginWithBundle("WebViewActivity", mActivity, bundle);
    }

    private void showUrlInWebView(String url) {
        //webview打开
        Bundle bundle = new Bundle();
        bundle.putString(AboutFragment.URL, url);
        bundle.putString(Const.ACTIONBAR_TITLE, "扫描结果");
        bundle.putString(FragmentPageActivity.FRAGMENT, "AboutFragment");
        app.mEngine.runNormalPluginWithBundle(
                "FragmentPageActivity", mContext, bundle);
    }

    private void showDataInWebView(String data) {
        //webview打开
        Bundle bundle = new Bundle();
        bundle.putString(AboutFragment.CONTENT, data);
        bundle.putString(Const.ACTIONBAR_TITLE, "扫描结果");
        bundle.putInt(AboutFragment.TYPE, AboutFragment.FROM_STR);
        bundle.putString(FragmentPageActivity.FRAGMENT, "AboutFragment");
        app.mEngine.runNormalPluginWithBundle(
                "FragmentPageActivity", mContext, bundle);
    }

    private class LoginURLMatchType extends URLMatchType {

        public LoginURLMatchType(String url) {
            super(url);
            this.apiType = "mapi_v2";
            this.apiMethod = "User/loginWithToken";
        }

        @Override
        public void match() {
            loginWithUrl();
        }

        private void loginWithUrl() {
            final LoadDialog loading = LoadDialog.create(mContext);
            loading.show();

            RequestUrl requestUrl = new RequestUrl(this.mUrl);
            ajaxGet(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    loading.dismiss();
                    try {
                        final UserResult userResult = app.gson.fromJson(
                                response, new TypeToken<UserResult>() {
                                }.getType());

                        if (userResult == null) {
                            ToastUtils.show(mContext, "二维码信息错误!");
                            return;
                        }

                        if (TextUtils.isEmpty(userResult.token)) {
                            ToastUtils.show(mContext, "二维码登录信息已过期或失效");
                        } else {
                            app.saveToken(userResult);
                            app.sendMessage(Const.LOGIN_SUCCESS, null);
                        }

                        RequestUrl requestUrl = app.bindUrl(Const.GET_API_TOKEN, false);
                        app.getUrl(requestUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Token token = parseJsonValue(response, new TypeToken<Token>() {
                                });
                                if (token != null) {
                                    app.saveApiToken(token.token);
                                    Bundle bundle = new Bundle();
                                    bundle.putString(Const.BIND_USER_ID, String.valueOf(userResult.user.id));
                                    app.pushRegister(bundle);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "无法获取网校Token");
                            }
                        });
                        mActivity.finish();
                    } catch (Exception e) {
                        CommonUtil.longToast(mActivity, "二维码信息错误!");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading.dismiss();
                    CommonUtil.longToast(mActivity, "二维码访问错误!");
                }
            });
        }
    }

    private boolean urlcanMatch(Matcher typeMatcher) {
        String apiType = typeMatcher.group(1);
        String apiMethod = typeMatcher.group(2);
        for (URLMatchType matchType : mURLMatchTypes) {
            if (matchType.handle(apiType, apiMethod) ) {
                return true;
            }
        }

        return false;
    }

    private abstract class URLMatchType {
        protected String apiType;
        protected String apiMethod;
        protected String mUrl;

        public URLMatchType(String url) {
            this.mUrl = url;
        }

        public boolean handle(String apiType, String apiMethod) {
            if (apiType.equals(this.apiType) && apiMethod.equals(this.apiMethod)) {
                match();
                return true;
            }
            return false;
        }
        public abstract void match();
    }
}
