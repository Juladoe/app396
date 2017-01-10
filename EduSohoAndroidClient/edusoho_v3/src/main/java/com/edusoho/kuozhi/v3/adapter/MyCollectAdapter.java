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
import com.edusoho.kuozhi.v3.entity.course.LearningCourse;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.CourseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.edusoho.kuozhi.v3.view.dialog.MoreDialog;
import com.edusoho.kuozhi.v3.view.dialog.SureDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remilia on 2017/1/5.
 */
public class MyCollectAdapter extends BaseAdapter {

    private Context mContext;
    private List<Course> mLists = new ArrayList<>();
    private int mPage = 0;
    private boolean mCanLoadLive = true;
    private boolean mCanLoadNor = true;


    public MyCollectAdapter(Context context) {
        this.mContext = context;
        initData();
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == getCount() - 1 && (mCanLoadLive || mCanLoadNor)) {
            mPage++;
            addData();
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_collect, null, false);
            viewHolder = new ViewHolder();
            viewHolder.ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);
            viewHolder.tvAddNum = (TextView) convertView.findViewById(R.id.tv_add_num);
            viewHolder.tvMore = (TextView) convertView.findViewById(R.id.tv_more);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Course course = mLists.get(position);
        ImageLoader.getInstance().displayImage(course.largePicture
                , viewHolder.ivPic, EdusohoApp.app
                        .mOptions);
        viewHolder.tvAddNum.setText(String.format("%s人参与", course.hitNum));
        viewHolder.tvTitle.setText(String.valueOf(course.title));
        viewHolder.tvMore.setTag(position);
        viewHolder.tvMore.setOnClickListener(mOnClickListener);
        convertView.setTag(R.id.tv_title,position);
        convertView.setOnClickListener(mViewOnClickListener);
        return convertView;
    }

    private View.OnClickListener mViewOnClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag(R.id.tv_title);
                    final Course course = mLists.get(position);
                    EdusohoApp.app.mEngine.runNormalPlugin("CourseActivity"
                            , mContext, new PluginRunCallback() {
                                @Override
                                public void setIntentDate(Intent startIntent) {
                                    startIntent.putExtra(CourseActivity.COURSE_ID, course.id + "");
                                }
                            });
                }
            };

    private View.OnClickListener mOnClickListener
            = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            final Course course = mLists.get(position);
            MoreDialog dialog = new MoreDialog(mContext);
            dialog.init("取消收藏", new MoreDialog.MoreCallBack() {
                @Override
                public void onMoveClick(View v, final Dialog dialog) {
                    new SureDialog(mContext).init("是否确定取消收藏！", new SureDialog.CallBack() {
                        @Override
                        public void onSureClick(View v, final Dialog dialog2) {
                            CourseUtil.uncollectCourse(String.valueOf(course.id)
                                    , new CourseUtil.OnCollectSucceeListener() {
                                        @Override
                                        public void onCollectSuccee() {
                                            CommonUtil.shortToast(mContext, "取消收藏成功");
                                            mLists.remove(course);
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
                }

                @Override
                public void onCancelClick(View v, Dialog dialog) {
                    dialog.dismiss();
                }
            }).show();
        }
    };

    private static ViewHolder viewHolder;

    private class ViewHolder {
        ImageView ivPic;
        TextView tvAddNum;
        TextView tvTitle;
        TextView tvMore;
    }

    public void initData() {
        mLists.clear();
        notifyDataSetChanged();
        CourseDetailModel.getLiveCourses(10, 10 * mPage, new ResponseCallbackListener<LearningCourse>() {
            @Override
            public void onSuccess(LearningCourse data) {
                mLists.addAll(data.getData());
                if (data.getData().size() < 10) {
                    mCanLoadLive = false;
                } else {
                    mCanLoadLive = true;
                }
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(String code, String message) {

            }
        });
        CourseDetailModel.getNormalCollect(10, 10 * mPage, new ResponseCallbackListener<LearningCourse>() {
            @Override
            public void onSuccess(LearningCourse data) {
                mLists.addAll(data.getData());
                if (data.getData().size() < 10) {
                    mCanLoadNor = false;
                } else {
                    mCanLoadNor = true;
                }
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(String code, String message) {

            }
        });
    }

    private void addData() {
        if (mCanLoadNor) {
            CourseDetailModel.getNormalCollect(10, 10 * mPage, new ResponseCallbackListener<LearningCourse>() {
                @Override
                public void onSuccess(LearningCourse data) {
                    mLists.addAll(data.getData());
                    if (data.getData().size() < 10) {
                        mCanLoadNor = false;
                    } else {
                        mCanLoadNor = true;
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(String code, String message) {

                }
            });
        }
        if (mCanLoadLive) {
            CourseDetailModel.getLiveCourses(10, 10 * mPage, new ResponseCallbackListener<LearningCourse>() {
                @Override
                public void onSuccess(LearningCourse data) {
                    mLists.addAll(data.getData());
                    if (data.getData().size() < 10) {
                        mCanLoadLive = false;
                    } else {
                        mCanLoadLive = true;
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(String code, String message) {

                }
            });
        }
    }
}
