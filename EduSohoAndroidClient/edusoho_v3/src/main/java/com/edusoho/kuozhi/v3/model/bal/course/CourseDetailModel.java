package com.edusoho.kuozhi.v3.model.bal.course;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.course.ClassroomDetail;
import com.edusoho.kuozhi.v3.entity.course.CourseDetail;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.http.ModelDecor;
import com.edusoho.kuozhi.v3.model.base.ApiResponse;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Zhang on 2016/12/13.
 */

public class CourseDetailModel implements Serializable {

    public static void getCourseDetail(String courseId,
                                       final ResponseCallbackListener<CourseDetail> callbackListener) {
        String url = String.format(Const.COURSE_GETCOURSE, courseId);
        RequestUrl requestUrl = EdusohoApp.app.bindUrl(url, true);
        EdusohoApp.app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("课程不存在")) {
                    callbackListener.onFailure("Error", response);
                    return;
                }
                CourseDetail apiResponse = ModelDecor.getInstance().
                        decor(response, new TypeToken<CourseDetail>() {
                        });
                if (apiResponse != null) {
                    callbackListener.onSuccess(apiResponse);
                } else if (apiResponse != null) {
                    callbackListener.onFailure("Error", response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callbackListener.onFailure("Error", error.getMessage());
            }
        });
    }

    public static void getClassroomDetail(String classroomId,
                                          final ResponseCallbackListener<ClassroomDetail> callbackListener) {
        String url = String.format(Const.COURSE_GETCLASSROOM, classroomId);
        RequestUrl requestUrl = EdusohoApp.app.bindUrl(url, true);
        EdusohoApp.app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("班级不存在")) {
                    callbackListener.onFailure("Error", response);
                    return;
                }
                ClassroomDetail apiResponse = ModelDecor.getInstance().
                        decor(response, new TypeToken<ClassroomDetail>() {
                        });
                if (apiResponse != null) {
                    callbackListener.onSuccess(apiResponse);
                } else if (apiResponse != null) {
                    callbackListener.onFailure("Error", response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callbackListener.onFailure("Error", error.getMessage());
            }
        });
    }

    public static void getCourseReviews(String courseId, String limit, String start,
                                        final ResponseCallbackListener<CourseReviewDetail> callbackListener) {
        String url = String.format(Const.COURSE_GETREVIEWS, courseId, limit, start);
        RequestUrl requestUrl = EdusohoApp.app.bindUrl(url, true);
        EdusohoApp.app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    CourseReviewDetail apiResponse = ModelDecor.getInstance().
                            decor(response, new TypeToken<CourseReviewDetail>() {
                            });
                    if (apiResponse != null) {
                        callbackListener.onSuccess(apiResponse);
                    } else if (apiResponse != null) {
                        callbackListener.onFailure("Error", response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callbackListener.onFailure("Error", error.getMessage());
            }
        });
    }

    public static void getClassroomReviews(String courseId, String limit, String start,
                                           final ResponseCallbackListener<ClassroomReviewDetail> callbackListener) {
        String url = String.format(Const.CLASSROOM_GETREVIEWS, courseId, limit, start);
        RequestUrl requestUrl = EdusohoApp.app.bindUrl(url, true);
        EdusohoApp.app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ClassroomReviewDetail apiResponse = ModelDecor.getInstance().
                            decor(response, new TypeToken<ClassroomReviewDetail>() {
                            });
                    if (apiResponse != null) {
                        callbackListener.onSuccess(apiResponse);
                    } else if (apiResponse != null) {
                        callbackListener.onFailure("Error", response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callbackListener.onFailure("Error", error.getMessage());
            }
        });
    }

    public static void getCourseMember(String courseId,
                                       final ResponseCallbackListener<List<CourseMember>> callbackListener) {
        String url = String.format(Const.COURSE_GETMEMBER, courseId);
        RequestUrl requestUrl = EdusohoApp.app.bindNewApiUrl(url, true);
        EdusohoApp.app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ApiResponse<CourseMember> apiResponse =
                            ModelDecor.getInstance().decor(response,
                                    new TypeToken<ApiResponse<CourseMember>>() {
                                    });
                    if (apiResponse != null) {
                        callbackListener.onSuccess(apiResponse.resources);
                    } else if (apiResponse != null) {
                        callbackListener.onFailure("Error", response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callbackListener.onFailure("Error", error.getMessage());
            }
        });
    }

    public static void getClassroomMember(String classroomId,
                                          final ResponseCallbackListener<List<ClassroomMember>> callbackListener) {
        String url = String.format(Const.CLASSROOM_GETMEMBER, classroomId);
        RequestUrl requestUrl = EdusohoApp.app.bindNewApiUrl(url, true);
        EdusohoApp.app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ApiResponse<ClassroomMember> apiResponse =
                            ModelDecor.getInstance().decor(response,
                                    new TypeToken<ApiResponse<ClassroomMember>>() {
                                    });
                    if (apiResponse != null) {
                        callbackListener.onSuccess(apiResponse.resources);
                    } else if (apiResponse != null) {
                        callbackListener.onFailure("Error", response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callbackListener.onFailure("Error", error.getMessage());
            }
        });
    }
}
