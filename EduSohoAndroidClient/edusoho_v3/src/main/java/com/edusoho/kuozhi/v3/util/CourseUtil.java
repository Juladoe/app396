package com.edusoho.kuozhi.v3.util;

import android.content.Intent;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zhang on 2016/12/14.
 */

public class CourseUtil {

    public static void collectCourse(String courseId, final OnCollectSucceeListener onCollectSucceeListener) {
        if (EdusohoApp.app.loginUser == null) {
            notLogin();
            return;
        }
        EdusohoApp.app.getUrl(EdusohoApp.app.bindUrl(Const.FAVORITE + "?courseId=" + courseId, true)
                , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response == null) {
                            if (EdusohoApp.app.loginUser == null) {
                                notLogin();
                                return;
                            }
                        } else if (response.equals("true")) {
                            if (onCollectSucceeListener != null) {
                                onCollectSucceeListener.onCollectSuccee();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CommonUtil.shortToast(EdusohoApp.app, "网络异常");
                    }
                });
    }

    public static void uncollectCourse(String courseId, final OnCollectSucceeListener onCollectSucceeListener) {
        if (EdusohoApp.app.loginUser == null) {
            notLogin();
            return;
        }
        EdusohoApp.app.getUrl(EdusohoApp.app.bindUrl(Const.UNFAVORITE + "?courseId=" + courseId, true)
                , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response == null) {
                            if (EdusohoApp.app.loginUser == null) {
                                notLogin();
                                return;
                            }
                        } else if (response.equals("true")) {
                            if (onCollectSucceeListener != null) {
                                onCollectSucceeListener.onCollectSuccee();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CommonUtil.shortToast(EdusohoApp.app, "网络异常");
                    }
                });
    }

    public static class CourseParamsBuilder {
        Map<String, String> params = new HashMap<>();
        private String payment;
        private String payPassword;
        private String totalPrice;
        private String couponCode;
        private String targetType;
        private String targetId;

        public CourseParamsBuilder setPayment(String payment) {
            this.payment = payment;
            params.put("payment", payment);
            return this;
        }

        public CourseParamsBuilder setPayPassword(String payPassword) {
            this.payPassword = payPassword;
            params.put("payPassword", payPassword);
            return this;
        }

        public CourseParamsBuilder setTotalPrice(String totalPrice) {
            this.totalPrice = totalPrice;
            params.put("totalPrice", totalPrice);
            return this;
        }

        public CourseParamsBuilder setCouponCode(String couponCode) {
            this.couponCode = couponCode;
            params.put("couponCode", couponCode);
            return this;
        }

        public CourseParamsBuilder setTargetType(String targetType) {
            this.targetType = targetType;
            params.put("targetType", targetType);
            return this;
        }

        public CourseParamsBuilder setTargetId(String targetId) {
            this.targetId = targetId;
            params.put("targetId", targetId);
            return this;
        }

        public Map<String, String> build() {
            return params;
        }
    }

    public static void addCourse(final CourseParamsBuilder builder, final OnAddCourseListener
            onAddCourseListener) {
        if (EdusohoApp.app.loginUser == null) {
            notLogin();
            return;
        }
        RequestUrl url = EdusohoApp.app.bindUrl(Const.CREATE_ORDER, true);
        url.setParams(builder.build());
        EdusohoApp.app.postUrl(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null) {
                    notLogin();
                    return;
                }
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String paid = jsonObject.getString("paid");
                        if (paid.equals("false")) {
                            final String url = String.format(
                                    Const.MOBILE_APP_URL,
                                    EdusohoApp.app.schoolHost,
                                    String.format("main#/coursepay/%s/%s",
                                            builder.targetId
                                            , "course")
                            );
                            EdusohoApp.app.mEngine.runNormalPlugin("WebViewActivity"
                                    , EdusohoApp.app.mActivity, new PluginRunCallback() {
                                        @Override
                                        public void setIntentDate(Intent startIntent) {
                                            startIntent.putExtra(Const.WEB_URL, url);
                                        }
                                    });
                        }
                        if (status.equals("ok")) {
                            if (onAddCourseListener != null) {
                                onAddCourseListener.onAddCourseSuccee(response);
                            }
                        } else {
                            if (onAddCourseListener != null) {
                                onAddCourseListener.onAddCourseError(status);
                            }
                        }
                    } catch (JSONException e) {
                        if (onAddCourseListener != null) {
                            onAddCourseListener.onAddCourseError("JsonError");
                        }
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (onAddCourseListener != null) {
                    onAddCourseListener.onAddCourseError("volleyError");
                }
            }
        });
    }

    public interface OnCollectSucceeListener {
        void onCollectSuccee();
    }

    public interface OnAddCourseListener {
        void onAddCourseSuccee(String response);

        void onAddCourseError(String response);
    }

    private static void notLogin() {

        EdusohoApp.app.mEngine.runNormalPluginWithAnim("LoginActivity", EdusohoApp.app
                , null, new NormalCallback() {
                    @Override
                    public void success(Object obj) {
                        EdusohoApp.app.mActivity.overridePendingTransition(R.anim.down_to_up, R.anim.none);
                    }
                });
    }
}
