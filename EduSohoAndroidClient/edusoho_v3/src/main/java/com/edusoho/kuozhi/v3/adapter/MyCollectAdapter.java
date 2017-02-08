package com.edusoho.kuozhi.v3.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.CourseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.edusoho.kuozhi.v3.view.dialog.MoreDialog;
import com.edusoho.kuozhi.v3.view.dialog.SureDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by hby on 2017/1/20.
 */
public class MyCollectAdapter extends BaseAdapter {
    private List<Course> mCourseList;
    private Context mContext;

    public MyCollectAdapter(Context context, List<Course> list) {
        mContext = context;
        this.mCourseList = list;
    }

    @Override
    public int getCount() {
        if (mCourseList.size() == 0) {
            //为了显示空记录图标
            return 1;
        }
        return mCourseList.size();
    }

    @Override
    public Course getItem(int position) {
        return mCourseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (mCourseList.size() == 0) {
            return LayoutInflater.from(mContext).inflate(R.layout.view_empty, null, false);
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_collect, null, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Course course = mCourseList.get(position);
        ImageLoader.getInstance().displayImage(course.getLargePicture()
                , viewHolder.ivPic, EdusohoApp.app
                        .mOptions);
        viewHolder.tvAddNum.setText(String.format("%s人参与", course.hitNum));
        viewHolder.tvTitle.setText(String.valueOf(course.title));
        convertView.setTag(R.id.tv_title, position);
        viewHolder.tvMore.setTag(position);
        viewHolder.tvMore.setOnClickListener(mMoreClickListener);
        convertView.setOnClickListener(mViewOnClickListener);
        if (course.type.equals("live")) {
            viewHolder.layoutLive.setVisibility(View.VISIBLE);
            if (course.liveState == 1) {
                viewHolder.tvLive.setText(R.string.lesson_living);
                viewHolder.tvLiveIcon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvLive.setText("直播");
                viewHolder.tvLiveIcon.setVisibility(View.GONE);
            }
        } else {
            viewHolder.layoutLive.setVisibility(View.GONE);
        }
        if (position == getCount() - 1) {
            viewHolder.vLine.setVisibility(View.GONE);
        } else {
            viewHolder.vLine.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private View.OnClickListener mViewOnClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag(R.id.tv_title);
                    final Course course = mCourseList.get(position);
                    EdusohoApp.app.mEngine.runNormalPlugin("CourseActivity"
                            , mContext, new PluginRunCallback() {
                                @Override
                                public void setIntentDate(Intent startIntent) {
                                    startIntent.putExtra(CourseActivity.COURSE_ID, course.id + "");
                                }
                            });
                }
            };

    private View.OnClickListener mMoreClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            final Course course = mCourseList.get(position);
            MoreDialog dialog = new MoreDialog(mContext);
            dialog.init("取消收藏", new MoreDialog.MoreCallBack() {
                @Override
                public void onMoveClick(View v, final Dialog dialog) {
                    new SureDialog(mContext).init("是否确定取消收藏！", new SureDialog.CallBack() {
                        @Override
                        public void onSureClick(View v, final Dialog dialog2) {
                            CourseUtil.uncollectCourse(course.id
                                    , new CourseUtil.OnCollectSuccessListener() {
                                        @Override
                                        public void onCollectSuccess() {
                                            CommonUtil.shortToast(mContext, "取消收藏成功");
                                            mCourseList.remove(course);
                                            notifyDataSetChanged();
                                            dialog.dismiss();
                                            dialog2.dismiss();
                                        }
                                    }
                            );
                        }

                        @Override
                        public void onCancelClick(View v, Dialog dialog2) {
                            dialog2.dismiss();
                        }
                    }).show();
                }

                @Override
                public void onShareClick(View v, Dialog dialog) {
                    final ShareTool shareTool =
                            new ShareTool(mContext
                                    , EdusohoApp.app.host + "/course/" + course.id
                                    , course.title
                                    , course.about.length() > 20 ?
                                    course.about.substring(0, 20)
                                    : course.about
                                    , course.middlePicture);
                    new Handler((mContext.getMainLooper())).post(new Runnable() {
                        @Override
                        public void run() {
                            shareTool.shardCourse();
                        }
                    });
                    dialog.dismiss();
                }

                @Override
                public void onCancelClick(View v, Dialog dialog) {
                    dialog.dismiss();
                }
            }).show();
        }
    };

    public static class ViewHolder {
        public ImageView ivPic;
        public TextView tvAddNum;
        public TextView tvTitle;
        public TextView tvMore;
        public View layoutLive;
        public TextView tvLiveIcon;
        public TextView tvLive;
        public View vLine;

        public ViewHolder(View view) {
            ivPic = (ImageView) view.findViewById(R.id.iv_pic);
            tvAddNum = (TextView) view.findViewById(R.id.tv_add_num);
            tvMore = (TextView) view.findViewById(R.id.tv_more);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            vLine = view.findViewById(R.id.v_line);
            layoutLive = view.findViewById(R.id.layout_live);
            tvLiveIcon = (TextView) view.findViewById(R.id.tv_live_icon);
            tvLive = (TextView) view.findViewById(R.id.tv_live);
            view.setTag(this);
        }
    }
}
