package com.edusoho.kuozhi.v3.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.SystemInfo;
import com.edusoho.kuozhi.v3.model.provider.IMServiceProvider;
import com.edusoho.kuozhi.v3.model.result.SchoolResult;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.Error;
import com.edusoho.kuozhi.v3.model.sys.ErrorResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.model.sys.Token;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseNoTitleActivity;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.SchoolUtil;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.edusoho.kuozhi.v3.view.dialog.NetSchoolDialog;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.edusoho.kuozhi.v3.view.photo.SchoolSplashActivity;
import com.edusoho.kuozhi.v3.view.qr.CaptureActivity;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JesseHuang on 15/5/6.
 * 扫描网校界面
 */
public class QrSchoolActivity extends BaseNoTitleActivity implements Response.ErrorListener {
    private View mQrSearchBtn;
    private ViewGroup mSearchLayout;
    private ViewGroup mSearchAllLayout;
    private TextView mSearchTv;
    private View mBackground;
    private ViewGroup mBottomLayout;
    private ListView mLoginNearLv;
    private ViewGroup mLoginNearLlayout;
    private NetSchoolDialog netSchoolDialog;
    public final static int REQUEST_QR = 0001;
    public final static int RESULT_QR = 0002;
    protected LoadDialog mLoading;
    private List<Map<String, Object>> mList = new ArrayList<>();
    private EnterSchoolAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_school);

        initView();
        initAnim();
    }

    public static void start(Activity context) {
        Activity qrSchoolActivity = EdusohoApp.runTask.get("QrSchoolActivity");
        if (qrSchoolActivity != null) {
            qrSchoolActivity.finish();
        }
        Intent intent = new Intent();
        intent.setClass(context, QrSchoolActivity.class);
        context.startActivity(intent);
    }

    protected void initView() {
        super.initView();
        mQrSearchBtn = findViewById(R.id.qr_search_btn);
        mQrSearchBtn.setOnClickListener(mSearchClickListener);
        mSearchLayout = (ViewGroup) findViewById(R.id.search_layout);
        mSearchLayout.setOnClickListener(mOtherClickListener);
        mSearchAllLayout = (ViewGroup) findViewById(R.id.search_all_layout);
        mSearchTv = (TextView) findViewById(R.id.search_tv);
        mBackground = findViewById(R.id.background);
        mBottomLayout = (ViewGroup) findViewById(R.id.bottom_layout);
        mLoginNearLlayout = (ViewGroup) findViewById(R.id.login_near_layout);
        mLoginNearLv = (ListView) findViewById(R.id.login_near_lv);
        netSchoolDialog = new NetSchoolDialog(QrSchoolActivity.this);

        List<Map<String, Object>> list = SchoolUtil.loadEnterSchool(mContext);
        if (list != null && list.size() != 0) {
            Collections.reverse(list);
            mLoginNearLlayout.setVisibility(View.VISIBLE);
        } else {
            mLoginNearLlayout.setVisibility(View.GONE);
        }
        mList = list;
        mAdapter = new EnterSchoolAdapter(this);
        mLoginNearLv.setAdapter(mAdapter);
    }

    private ValueAnimator mAnimatorUp;
    private AnimatorSet mAnimatorUpSet;
    private ValueAnimator mAnimatorDown;
    private AnimatorSet mAnimatorDownSet;

    private void initAnim() {
        mAnimatorUp = ValueAnimator
                .ofInt(AppUtil.dp2px(this, 230), AppUtil.dp2px(this, 52));
        mAnimatorDown = ValueAnimator
                .ofInt(AppUtil.dp2px(this, 52), AppUtil.dp2px(this, 230));
        mAnimatorUp.addUpdateListener(mBottomListener);
        mAnimatorDown.addUpdateListener(mBottomListener);
        mAnimatorUp.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                netSchoolDialog.show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimatorDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mSearchLayout.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mSearchLayout.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimatorUpSet = new AnimatorSet();
        mAnimatorDownSet = new AnimatorSet();
        mAnimatorUpSet.setDuration(300);
        mAnimatorDownSet.setDuration(300);
        mAnimatorUpSet.play(mAnimatorUp);
        mAnimatorDownSet.play(mAnimatorDown);
    }

    ValueAnimator.AnimatorUpdateListener mBottomListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    ViewGroup.LayoutParams params =
                            mBackground.getLayoutParams();
                    params.height = value;
                    mBackground.setLayoutParams(params);
                    float scale = (float) (AppUtil.px2dp(QrSchoolActivity.this, value) - 52) / 178f;
                    mBottomLayout.setAlpha(scale);
                    RelativeLayout.LayoutParams searchParams =
                            (RelativeLayout.LayoutParams) mSearchAllLayout.getLayoutParams();
                    float bottom = (1f - scale) * 40f;
                    searchParams.height = AppUtil
                            .dp2px(QrSchoolActivity.this, 34f + scale * 26f);
                    searchParams.setMargins(AppUtil.dp2px(QrSchoolActivity.this, 13),
                            0,
                            AppUtil.dp2px(QrSchoolActivity.this, 13f + (1f - scale) * 42f),
                            AppUtil.dp2px(QrSchoolActivity.this, bottom - 30f));
                    mSearchAllLayout.setLayoutParams(searchParams);
                    RelativeLayout.LayoutParams txtParams =
                            (RelativeLayout.LayoutParams) mSearchTv.getLayoutParams();
                    txtParams.setMargins(
                            AppUtil.dp2px(QrSchoolActivity.this, 13f * scale),0,0,0);
                    mSearchTv.setLayoutParams(txtParams);
                }
            };

    private View.OnClickListener mSearchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent qrIntent = new Intent();
            qrIntent.setClass(QrSchoolActivity.this, CaptureActivity.class);
            startActivityForResult(qrIntent, REQUEST_QR);
        }
    };

    private View.OnClickListener mOtherClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            MobclickAgent.onEvent(mContext,"i_mySetting_otherWaysEnter");
//            app.mEngine.runNormalPlugin("NetSchoolActivity", mContext, null);
            mAnimatorUpSet.start();
            mSearchLayout.setEnabled(false);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_QR && resultCode == RESULT_QR) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result");
                new SchoolChangeHandler(mActivity).change(result + "&version=2");
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mLoading.dismiss();
        if (error.networkResponse == null) {
            CommonUtil.longToast(mContext, mContext.getResources().getString(R.string.request_failed));
        } else {
            CommonUtil.longToast(mContext, mContext.getResources().getString(R.string.request_fail_text));
        }
    }

    public static class SchoolChangeHandler {
        private EdusohoApp mApp;
        private BaseActivity mActivity;
        private LoadDialog mLoading;

        private Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mLoading.dismiss();
                CommonUtil.longToast(mActivity.getBaseContext(), "二维码信息错误!");
            }
        };

        public SchoolChangeHandler(BaseActivity activity) {
            this.mActivity = activity;
            this.mApp = mActivity.app;
        }

        private void showSchSplash(String schoolName, String[] splashs) {
            if (splashs == null || splashs.length == 0) {
                mApp.mEngine.runNormalPlugin("DefaultPageActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    }
                });
            }
            SchoolSplashActivity.start(mActivity.getBaseContext(), schoolName, splashs);
            mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            mActivity.finish();
        }

        protected void startSchoolActivity(School site) {
            mLoading.dismiss();
            showSchSplash(site.name, site.splashs);
        }

        protected AppSettingProvider getAppSettingProvider() {
            return FactoryManager.getInstance().create(AppSettingProvider.class);
        }

        protected void bindApiToken(final UserResult userResult) {
            School school = userResult.site;

            RequestUrl requestUrl = new RequestUrl(school.host + Const.GET_API_TOKEN);
            Map<String, String> tokenMap = ApiTokenUtil.getToken(mActivity.getBaseContext());
            requestUrl.heads.put("Auth-Token", tokenMap.get("token"));
            mApp.getUrl(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Token token = mActivity.parseJsonValue(response, new TypeToken<Token>() {
                    });
                    if (token != null) {
                        mApp.saveApiToken(token.token);
                        selectSchool(userResult);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CommonUtil.longToast(mActivity.getBaseContext(), "获取网校信息失败");
                }
            });
        }

        public void selectSchool(UserResult userResult) {
            IMClient.getClient().destory();
            School site = userResult.site;
            mApp.setCurrentSchool(site);
            mApp.registDevice(null);

            if (userResult.token == null || "".equals(userResult.token)) {
                //未登录二维码
                mApp.removeToken();
                getAppSettingProvider().setUser(null);
                mApp.sendMessage(Const.LOGOUT_SUCCESS, null);
            } else {
                //扫描登录用户二维码
                mApp.saveToken(userResult);
                new IMServiceProvider(mActivity.getApplicationContext()).bindServer(userResult.user.id, userResult.user.nickname);
                mApp.sendMessage(Const.LOGIN_SUCCESS, null);
            }
            startSchoolActivity(site);
        }

        public void change(String url) {
            mLoading = LoadDialog.create(mActivity);
            mLoading.show();

            RequestUrl requestUrl = new RequestUrl(url);
            mActivity.ajaxGet(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        final UserResult userResult = mApp.gson.fromJson(
                                response, new TypeToken<UserResult>() {
                                }.getType());

                        if (userResult == null) {
                            CommonUtil.longToast(mActivity.getBaseContext(), "二维码信息错误!");
                            return;
                        }

                        School site = userResult.site;
                        if (site == null) {
                            CommonUtil.longToast(mActivity.getBaseContext(), "没有识别到网校信息!");
                            return;
                        }
                        if (!checkMobileVersion(site, site.apiVersionRange)) {
                            return;
                        }

                        bindApiToken(userResult);

                    } catch (Exception e) {
                        mLoading.dismiss();
                        CommonUtil.longToast(mActivity.getBaseContext(), "二维码信息错误!");
                    }
                }
            }, errorListener);
        }

        private boolean checkMobileVersion(final School site, HashMap<String, String> versionRange) {
            String min = versionRange.get("min");
            String max = versionRange.get("max");

            int result = AppUtil.compareVersion(mApp.apiVersion, min);
            if (result == Const.LOW_VERSIO) {
                PopupDialog dlg = PopupDialog.createMuilt(
                        mActivity,
                        "网校提示",
                        "您的客户端版本过低，无法登录该网校，请立即更新至最新版本。",
                        new PopupDialog.PopupClickListener() {
                            @Override
                            public void onClick(int button) {
                                if (button == PopupDialog.OK) {
                                    String code = mActivity.getResources().getString(R.string.app_code);
                                    String updateUrl = String.format(
                                            "%s/%s?code=%s",
                                            site.url,
                                            Const.DOWNLOAD_URL,
                                            code
                                    );
                                    mApp.startUpdateWebView(updateUrl);
                                }
                            }
                        });
                dlg.setOkText("立即下载");
                dlg.show();
                return false;
            }

            result = AppUtil.compareVersion(mApp.apiVersion, max);
            if (result == Const.HEIGHT_VERSIO) {
                PopupDialog.createNormal(
                        mActivity,
                        "网校提示",
                        "网校服务器版本过低，无法继续登录！请重新尝试。"
                ).show();
                return false;
            }

            return true;
        }
    }

    private class EnterSchoolAdapter extends BaseAdapter {
        Context context;

        public EnterSchoolAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return mList.size() > 4 ? 4 : mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                ListView.LayoutParams params = new ListView.LayoutParams(-1,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView view = new TextView(QrSchoolActivity.this);
                view.setLayoutParams(params);
                view.setGravity(Gravity.CENTER);
                TextPaint tp = view.getPaint();
                tp.setFakeBoldText(true);
                view.setPadding(0, AppUtil.dp2px(QrSchoolActivity.this, 10),
                        0, AppUtil.dp2px(QrSchoolActivity.this, 10));
                view.setTextSize(13);
                view.setTextColor(Color.parseColor("#BDC2C6"));
                convertView = view;
                convertView.setOnClickListener(mOnClickListener);
            }
            if (convertView instanceof TextView) {
                ((TextView) convertView).
                        setText(mList.get(position).get("schoolname").toString());
            }
            convertView.setTag(position);
            return convertView;
        }

        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                HashMap map = (HashMap) mList.get(position);
                String schoolhost = map.get("schoolhost").toString();
                searchSchool(schoolhost);
            }
        };

    }

    private void searchSchool(String searchStr) {
        if (TextUtils.isEmpty(searchStr)) {
            CommonUtil.longToast(mContext, "请输入网校url");
            return;
        }
        String url = "http://" + searchStr + Const.VERIFYVERSION;
        mLoading = LoadDialog.create(mContext);
        mLoading.show();

        RequestUrl requestUrl = new RequestUrl(url);
        app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SystemInfo systemInfo = parseJsonValue(response, new TypeToken<SystemInfo>() {
                });

                if (systemInfo == null || TextUtils.isEmpty(systemInfo.mobileApiUrl)) {
                    mLoading.dismiss();
                    PopupDialog.createNormal(mContext, "提示信息", "没有搜索到网校").show();
                    return;
                }

                getSchoolApi(systemInfo);
            }
        }, this);
    }

    protected void getSchoolApi(SystemInfo systemInfo) {
        final RequestUrl schoolApiUrl = new RequestUrl(systemInfo.mobileApiUrl + Const.VERIFYSCHOOL);
        app.getUrl(schoolApiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SchoolResult schoolResult = app.gson.fromJson(
                        response, new TypeToken<SchoolResult>() {
                        }.getType());

                if (schoolResult == null
                        || schoolResult.site == null) {
                    handlerError(response);
                    return;
                }

                School site = schoolResult.site;
                if (!SchoolUtil.checkMobileVersion(QrSchoolActivity.this
                        , site, site.apiVersionRange)) {
                    return;
                }
                bindApiToken(site);
            }
        }, this);
    }

    private void handlerError(String errorStr) {
        try {
            ErrorResult result = app.gson.fromJson(errorStr, new TypeToken<ErrorResult>() {
            }.getType());
            if (result != null) {
                Error error = result.error;
                PopupDialog.createNormal(mContext, "系统提示", error.message).show();
            }
        } catch (Exception e) {
            PopupDialog.createNormal(mContext, "提示信息", "没有搜索到网校").show();
        }
    }

    protected void bindApiToken(final School site) {
        StringBuffer sb = new StringBuffer(site.host);
        sb.append(Const.GET_API_TOKEN);
        RequestUrl requestUrl = new RequestUrl(sb.toString());
        app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mLoading.dismiss();
                Token token = parseJsonValue(response, new TypeToken<Token>() {
                });
                if (token == null || TextUtils.isEmpty(token.token)) {
                    CommonUtil.longToast(mContext, "获取网校信息失败");
                    return;
                }
                app.setCurrentSchool(site);
                app.removeToken();
                app.registDevice(null);
                app.saveApiToken(token.token);
                getAppSettingProvider().setUser(null);
                IMClient.getClient().destory();
                saveSchoolHistory(site);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                app.setCurrentSchool(site);
                app.removeToken();
                app.registDevice(null);
                getAppSettingProvider().setUser(null);
                IMClient.getClient().destory();
                saveSchoolHistory(site);
            }
        });
    }

    private void saveSchoolHistory(School site) {
        SimpleDateFormat nowfmt = new SimpleDateFormat("登录时间：yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String loginTime = nowfmt.format(date);
        SchoolUtil.saveEnterSchool(mContext, site.name, loginTime, "登录账号：未登录", app.domain);
        startSchoolActivity(site);
    }

    private void startSchoolActivity(School site) {
        mLoading.dismiss();
        showSchSplash(site.name, site.splashs);
    }

    private void showSchSplash(String schoolName, String[] splashs) {
        if (splashs == null || splashs.length == 0) {
            app.mEngine.runNormalPlugin("DefaultPageActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }
            });
        }
        SchoolSplashActivity.start(mContext, schoolName, splashs);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 需解耦
     */
    //@Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent() {
        mAnimatorDownSet.start();
    }
}
