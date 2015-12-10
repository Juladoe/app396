package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by JesseHuang on 15/12/10.
 */
public class CourseDetailActivity extends ChatItemBaseDetail {

    private CourseDetailsResult mCourseResult;
    private Course mCourseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        if (intent == null) {
            CommonUtil.longToast(mContext, "获取课程信息失败");
            return;
        }
        mFromId = intent.getIntExtra(Const.FROM_ID, 0);
        setBackMode(BACK, intent.getStringExtra(Const.ACTIONBAR_TITLE) + "详情");
        tvClassroomAnnouncement.setText(getString(R.string.course_announcement));
        tvEntryClassroom.setText(getString(R.string.entry_course));
        btnDelRecordAndQuit.setText(getString(R.string.del_record_and_quit_course));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_classroom_announcement) {
            app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.COURSE_ANNOUNCEMENT, mFromId));
                    startIntent.putExtra(WebViewActivity.URL, url);
                }
            });
        } else if (v.getId() == R.id.tv_entry_classroom) {
            app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.USER_LEARN_COURSE, mFromId));
                    startIntent.putExtra(WebViewActivity.URL, url);
                }
            });
        } else if (v.getId() == R.id.clear_record) {
//            PopupDialog popupDialog = PopupDialog.createMuilt(mContext, "提示", "删除聊天记录？", new PopupDialog.PopupClickListener() {
//                @Override
//                public void onClick(int button) {
//                    if (button == PopupDialog.OK) {
//                        ClassroomDiscussDataSource classroomDiscussDataSource = new ClassroomDiscussDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
//                        classroomDiscussDataSource.delete(mFromId, app.loginUser.id);
//                        NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
//                        New newModel = newDataSource.getNew(mFromId, app.loginUser.id);
//                        newModel.content = "";
//                        newDataSource.update(newModel);
//                        Bundle bundle = new Bundle();
//                        bundle.putInt(Const.FROM_ID, mFromId);
//                        app.sendMsgToTarget(NewsFragment.REFRESH_LIST, bundle, NewsFragment.class);
//                    }
//                }
//            });
//            popupDialog.setOkText("清空");
//            popupDialog.show();
        } else if (v.getId() == R.id.btn_del_and_quit) {
//            PopupDialog popupDialog = PopupDialog.createMuilt(mContext, "提示", "退出学习？", new PopupDialog.PopupClickListener() {
//                @Override
//                public void onClick(int button) {
//                    if (button == PopupDialog.OK) {
//                        RequestUrl requestUrl = app.bindUrl(Const.CLASSROOM_UNLEARN, true);
//                        HashMap<String, String> params = requestUrl.getParams();
//                        params.put("classRoomId", mFromId + "");
//                        params.put("targetType", "classroom");
//                        ajaxPost(requestUrl, new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                if (response.equals("true")) {
//                                    ClassroomDiscussDataSource classroomDiscussDataSource = new ClassroomDiscussDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
//                                    classroomDiscussDataSource.delete(mFromId, app.loginUser.id);
//                                    NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
//                                    newDataSource.delete("FROMID = ? AND BELONGID = ? AND TYPE = ?",
//                                            mFromId + "", app.loginUser.id + "", PushUtil.ChatUserType.CLASSROOM);
//                                    Bundle bundle = new Bundle();
//                                    bundle.putInt(Const.FROM_ID, mFromId);
//                                    app.sendMsgToTarget(NewsFragment.REFRESH_LIST, bundle, NewsFragment.class);
//                                    app.mEngine.runNormalPlugin("DefaultPageActivity", mActivity, new PluginRunCallback() {
//                                        @Override
//                                        public void setIntentDate(Intent startIntent) {
//                                            startIntent.putExtra(Const.SWITCH_NEWS_TAB, true);
//                                        }
//                                    });
//                                } else {
//                                    CommonUtil.shortToast(mContext, "退出失败");
//                                }
//                            }
//                        }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                CommonUtil.shortToast(mContext, "退出失败");
//                            }
//                        });
//                    }
//                }
//            });
//            popupDialog.setOkText("确定");
//            popupDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.news_course_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.news_course_profile) {
            RequestUrl requestUrl = app.bindUrl(Const.COURSE, false);
            HashMap<String, String> params = requestUrl.getParams();
            params.put("courseId", mFromId + "");
            app.postUrl(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mCourseResult = parseJsonValue(response, new TypeToken<CourseDetailsResult>() {
                    });
                    mCourseInfo = mCourseResult.course;
                    if (mCourseInfo != null) {
                        String url = app.host + "/course/" + mFromId;
                        String title = mCourseInfo.title;
                        String about = mCourseInfo.about;
                        String pic = mCourseInfo.middlePicture;

                        final ShareTool shareTool = new ShareTool(mActivity, url, title, about, pic);
                        new Handler((mActivity.getMainLooper())).post(new Runnable() {
                            @Override
                            public void run() {
                                shareTool.shardCourse();
                            }
                        });
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });


        }
        return super.onOptionsItemSelected(item);
    }
}
