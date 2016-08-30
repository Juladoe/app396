package com.edusoho.kuozhi.v3.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.Classroom;
import com.edusoho.kuozhi.v3.model.bal.ClassroomMember;
import com.edusoho.kuozhi.v3.model.bal.ClassroomMemberResult;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.provider.ClassRoomProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.ClassroomDiscussDataSource;
import com.edusoho.kuozhi.v3.util.sql.NewDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by JesseHuang on 15/10/27.
 */
public class ClassroomDetailActivity extends ChatItemBaseDetail {

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            CommonUtil.longToast(mContext, "获取班级信息失败");
            return;
        }
        mFromId = intent.getIntExtra(Const.FROM_ID, 0);
        mConvNo = intent.getStringExtra(CONV_NO);
        setBackMode(BACK, intent.getStringExtra(Const.ACTIONBAR_TITLE));

        RequestUrl requestUrl = app.bindNewUrl(String.format(Const.CLASSROOM_MEMBERS, mFromId), true);
        ajaxGet(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ClassroomMemberResult memberResult = parseJsonValue(response, new TypeToken<ClassroomMemberResult>() {
                });
                int total;
                if (memberResult != null) {
                    total = memberResult.total;
                    tvMemberSum.setText(String.format("%s(%d)", getString(R.string.classroom_all_members), total));
                    if (memberResult.resources != null) {
                        MemberAvatarAdapter adapter = new MemberAvatarAdapter(Arrays.asList(memberResult.resources));
                        gvMemberAvatar.setAdapter(adapter);
                    }
                } else {
                    CommonUtil.longToast(mContext, "获取班级信息失败");
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
        if (v.getId() == R.id.rl_announcement) {
            app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.CLASSROOM_ANNOUNCEMENT, mFromId));
                    startIntent.putExtra(Const.WEB_URL, url);
                }
            });
        } else if (v.getId() == R.id.rl_entry) {
            app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.CLASSROOM_COURSES, mFromId));
                    startIntent.putExtra(Const.WEB_URL, url);
                }
            });
        } else if (v.getId() == R.id.rl_clear_record) {
            PopupDialog popupDialog = PopupDialog.createMuilt(mContext, "提示", "删除聊天记录？", new PopupDialog.PopupClickListener() {
                @Override
                public void onClick(int button) {
                    if (button == PopupDialog.OK) {
                        User user = getAppSettingProvider().getCurrentUser();
                        ConvEntity convEntity = IMClient.getClient().getConvManager()
                                .getConvByTypeAndId(Destination.CLASSROOM, mFromId, user.id);
                        if (convEntity == null) {
                            return;
                        }
                        IMClient.getClient().getMessageManager().deleteByConvNo(convEntity.getConvNo());
                        IMClient.getClient().getConvManager().clearLaterMsg(mConvNo);
                        MessageEngine.getInstance().sendMsgToTaget(
                                ClassroomDiscussActivity.CLEAR, null, ClassroomDiscussActivity.class);
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
                                    newDataSource.delete(mFromId + "", PushUtil.ChatUserType.CLASSROOM, app.loginUser.id + "");
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(Const.FROM_ID, mFromId);
                                    app.sendMsgToTarget(Const.REFRESH_LIST, bundle, NewsFragment.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.news_course_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.news_course_profile) {
            final LoadDialog loadDialog = LoadDialog.create(mContext);
            loadDialog.setTextVisible(View.GONE);
            loadDialog.show();

            new ClassRoomProvider(mContext).getClassRoom(mFromId)
                    .success(new NormalCallback<Classroom>() {
                        @Override
                        public void success(Classroom classroom) {
                            loadDialog.dismiss();
                            if (classroom == null) {
                                return;
                            }
                            String url = app.host + "/course/" + mFromId;
                            String title = classroom.title;
                            String about = classroom.about == null ? "" : AppUtil.coverCourseAbout(classroom.about.toString());
                            String pic = classroom.middlePicture;

                            final ShareTool shareTool = new ShareTool(mActivity, url, title, about, pic);
                            new Handler((mActivity.getMainLooper())).post(new Runnable() {
                                @Override
                                public void run() {
                                    shareTool.shardCourse();
                                }
                            });
                            if (classroom != null) {

                            } else {
                                CommonUtil.longToast(mContext, "获取课程信息失败");
                            }
                        }
                    }).fail(new NormalCallback<VolleyError>() {
                @Override
                public void success(VolleyError obj) {
                    loadDialog.dismiss();
                    CommonUtil.longToast(mContext, "获取班级信息失败");
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    public class MemberAvatarAdapter extends BaseAdapter {
        public List<ClassroomMember> mList;
        private DisplayImageOptions mOptions;

        public MemberAvatarAdapter(List<ClassroomMember> mList) {
            this.mList = mList;
            mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).
                    showImageForEmptyUri(R.drawable.default_avatar).
                    showImageOnFail(R.drawable.default_avatar).build();
        }

        @Override
        public int getCount() {
            if (mList != null) {
                return mList.size() + 1;
            }
            return 1;
        }

        @Override
        public ClassroomMember getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_member_avatar, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (getCount() - 1 != position) {
                final ClassroomMember member = mList.get(position);
                viewHolder.ivAvatar.setBackground(null);
                ImageLoader.getInstance().displayImage(member.user.avatar, viewHolder.ivAvatar, mOptions);
                viewHolder.tvMemberName.setText(member.user.nickname);
                viewHolder.ivAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.USER_PROFILE, member.user.id));
                                startIntent.putExtra(Const.WEB_URL, url);
                            }
                        });
                    }
                });
            } else {
                viewHolder.ivAvatar.setBackgroundResource(R.drawable.group_member_more_bg);
                viewHolder.tvMemberName.setText("更多");
                viewHolder.ivAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.CLASSROOM_MEMBER_LIST, mFromId));
                                startIntent.putExtra(Const.WEB_URL, url);
                            }
                        });
                    }
                });
            }
            return convertView;
        }
    }
}
