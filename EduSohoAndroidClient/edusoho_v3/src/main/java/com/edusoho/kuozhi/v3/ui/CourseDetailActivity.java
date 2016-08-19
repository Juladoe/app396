package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.managar.IMConvManager;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.bal.course.CourseMember;
import com.edusoho.kuozhi.v3.model.bal.course.CourseMemberResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.CourseDiscussDataSource;
import com.edusoho.kuozhi.v3.util.sql.NewDataSource;
import com.edusoho.kuozhi.v3.util.sql.NewsCourseDataSource;
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
 * Created by JesseHuang on 15/12/10.
 */
public class CourseDetailActivity extends ChatItemBaseDetail {

    private CourseDetailsResult mCourseResult;

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
        setBackMode(BACK, intent.getStringExtra(Const.ACTIONBAR_TITLE));
        tvClassroomAnnouncement.setText(getString(R.string.course_announcement));
        tvEntryClassroom.setText(getString(R.string.entry_course));
        btnDelRecordAndQuit.setText(getString(R.string.del_record_and_quit_course));

        RequestUrl requestUrl = app.bindNewUrl(String.format(Const.COURSE_MEMBERS, mFromId), true);
        ajaxGet(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CourseMemberResult courseMemberResult = parseJsonValue(response, new TypeToken<CourseMemberResult>() {
                });
                int total;
                if (courseMemberResult != null) {
                    total = courseMemberResult.total;
                    tvMemberSum.setText(getString(R.string.classroom_all_members) + "（" + total + "）");
                    if (courseMemberResult.resources != null) {
                        CourseMemberAvatarAdapter adapter = new CourseMemberAvatarAdapter(Arrays.asList(courseMemberResult.resources));
                        gvMemberAvatar.setAdapter(adapter);
                    }
                } else {
                    CommonUtil.longToast(mContext, "获取课程信息失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonUtil.longToast(mContext, "获取课程信息失败");
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_announcement) {
            app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.COURSE_ANNOUNCEMENT, mFromId));
                    startIntent.putExtra(Const.WEB_URL, url);
                }
            });
        } else if (v.getId() == R.id.rl_entry) {
            app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.MOBILE_WEB_COURSE, mFromId));
                    startIntent.putExtra(Const.WEB_URL, url);
                }
            });
        } else if (v.getId() == R.id.rl_clear_record) {
            PopupDialog popupDialog = PopupDialog.createMuilt(mContext, "提示", "删除聊天记录？", new PopupDialog.PopupClickListener() {
                @Override
                public void onClick(int button) {
                    if (button == PopupDialog.OK) {
                        IMConvManager imConvManager = IMClient.getClient().getConvManager();
                        ConvEntity convEntity = imConvManager.getConvByTypeAndId(Destination.COURSE, mFromId);
                        if (convEntity == null) {
                            return;
                        }
                        IMClient.getClient().getMessageManager().deleteByConvNo(convEntity.getConvNo());
                    }
                }
            });
            popupDialog.setOkText("清空");
            popupDialog.show();
        } else if (v.getId() == R.id.btn_del_and_quit) {
            PopupDialog popupDialog = PopupDialog.createMuilt(mContext, "提示", "退出学习？", new PopupDialog.PopupClickListener() {
                @Override
                public void onClick(int button) {
                    if (button == PopupDialog.OK) {
                        RequestUrl requestUrl = app.bindUrl(Const.UN_LEARN_COURSE, true);
                        HashMap<String, String> params = requestUrl.getParams();
                        params.put("courseId", mFromId + "");
                        ajaxPost(requestUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.equals("true")) {
                                    SqliteChatUtil chatUtil = SqliteChatUtil.getSqliteChatUtil(mContext, app.domain);
                                    new NewsCourseDataSource(chatUtil).delete(mFromId, app.loginUser.id);
                                    new CourseDiscussDataSource(chatUtil).delete(mFromId, app.loginUser.id);
                                    new NewDataSource(chatUtil).delete(mFromId, PushUtil.CourseType.TYPE, app.loginUser.id);
                                    app.sendMsgToTarget(Const.REFRESH_LIST, new Bundle(), NewsFragment.class);
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
            RequestUrl requestUrl = app.bindUrl(Const.COURSE, false);
            HashMap<String, String> params = requestUrl.getParams();
            params.put("courseId", mFromId + "");
            app.postUrl(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    loadDialog.dismiss();
                    mCourseResult = parseJsonValue(response, new TypeToken<CourseDetailsResult>() {
                    });
                    if (mCourseResult != null && mCourseResult.course != null) {
                        String url = app.host + "/course/" + mFromId;
                        String title = mCourseResult.course.title;
                        String about = mCourseResult.course.about;
                        String pic = mCourseResult.course.middlePicture;

                        final ShareTool shareTool = new ShareTool(mActivity, url, title, about, pic);
                        new Handler((mActivity.getMainLooper())).post(new Runnable() {
                            @Override
                            public void run() {
                                shareTool.shardCourse();
                            }
                        });
                    } else {
                        CommonUtil.longToast(mContext, "获取课程信息失败");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loadDialog.dismiss();
                    CommonUtil.longToast(mContext, "获取课程信息失败");
                }
            });


        }
        return super.onOptionsItemSelected(item);
    }

    public class CourseMemberAvatarAdapter extends BaseAdapter {
        public List<CourseMember> mList;
        protected DisplayImageOptions mOptions;

        public CourseMemberAvatarAdapter(List<CourseMember> mList) {
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
        public CourseMember getItem(int position) {
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
                final CourseMember member = mList.get(position);
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
                                String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.COURSE_MEMBER_LIST, mFromId));
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
