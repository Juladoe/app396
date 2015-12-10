package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.MemberResult;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.ClassroomDiscussDataSource;
import com.edusoho.kuozhi.v3.util.sql.NewDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by JesseHuang on 15/10/27.
 */
public class ClassroomDetailActivity extends ChatItemBaseDetail {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            CommonUtil.longToast(mContext, "获取班级信息失败");
            return;
        }
        mFromId = intent.getIntExtra(Const.FROM_ID, 0);
        setBackMode(BACK, intent.getStringExtra(Const.ACTIONBAR_TITLE) + "详情");

        RequestUrl requestUrl = app.bindNewUrl(String.format(Const.CLASSROOM_ALL_MEMBERS, mFromId), true);
        ajaxGet(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MemberResult memberResult = parseJsonValue(response, new TypeToken<MemberResult>() {
                });
                int total;
                if (memberResult != null) {
                    total = Integer.parseInt(memberResult.total);
                    tvMemberSum.setText(getString(R.string.classroom_all_members) + "(" + total + ")");
                }
                if (memberResult.resources != null) {
                    MemberAvatarAdapter adapter = new MemberAvatarAdapter(Arrays.asList(memberResult.resources));
                    gvMemberAvatar.setAdapter(adapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonUtil.longToast(mContext, "获取班级信息失败");
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_classroom_announcement) {
            app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.CLASSROOM_ANNOUNCEMENT, mFromId));
                    startIntent.putExtra(WebViewActivity.URL, url);
                }
            });
        } else if (v.getId() == R.id.tv_entry_classroom) {
            app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.CLASSROOM_COURSES, mFromId));
                    startIntent.putExtra(WebViewActivity.URL, url);
                }
            });
        } else if (v.getId() == R.id.clear_record) {
            PopupDialog popupDialog = PopupDialog.createMuilt(mContext, "提示", "删除聊天记录？", new PopupDialog.PopupClickListener() {
                @Override
                public void onClick(int button) {
                    if (button == PopupDialog.OK) {
                        ClassroomDiscussDataSource classroomDiscussDataSource = new ClassroomDiscussDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
                        classroomDiscussDataSource.delete(mFromId, app.loginUser.id);
                        NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
                        New newModel = newDataSource.getNew(mFromId, app.loginUser.id);
                        newModel.content = "";
                        newDataSource.update(newModel);
                        Bundle bundle = new Bundle();
                        bundle.putInt(Const.FROM_ID, mFromId);
                        app.sendMsgToTarget(NewsFragment.REFRESH_LIST, bundle, NewsFragment.class);
                    }
                }
            });
            popupDialog.setOkText("清空");
            popupDialog.show();
        } else if (v.getId() == R.id.btn_del_and_quit) {
            PopupDialog popupDialog = PopupDialog.createMuilt(mContext, "提示", "退出班级？", new PopupDialog.PopupClickListener() {
                @Override
                public void onClick(int button) {
                    if (button == PopupDialog.OK) {
                        RequestUrl requestUrl = app.bindUrl(Const.CLASSROOM_UNLEARN, true);
                        HashMap<String, String> params = requestUrl.getParams();
                        params.put("classRoomId", mFromId + "");
                        params.put("targetType", "classroom");
                        ajaxPost(requestUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.equals("true")) {
                                    ClassroomDiscussDataSource classroomDiscussDataSource = new ClassroomDiscussDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
                                    classroomDiscussDataSource.delete(mFromId, app.loginUser.id);
                                    NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
                                    newDataSource.delete("FROMID = ? AND BELONGID = ? AND TYPE = ?",
                                            mFromId + "", app.loginUser.id + "", PushUtil.ChatUserType.CLASSROOM);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(Const.FROM_ID, mFromId);
                                    app.sendMsgToTarget(NewsFragment.REFRESH_LIST, bundle, NewsFragment.class);
                                    app.mEngine.runNormalPlugin("DefaultPageActivity", mActivity, new PluginRunCallback() {
                                        @Override
                                        public void setIntentDate(Intent startIntent) {
                                            startIntent.putExtra(Const.SWITCH_NEWS_TAB, true);
                                        }
                                    });
                                } else {
                                    CommonUtil.shortToast(mContext, "退出失败");
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                CommonUtil.shortToast(mContext, "退出失败");
                            }
                        });
                    }
                }
            });
            popupDialog.setOkText("确定");
            popupDialog.show();
        }
    }
}
