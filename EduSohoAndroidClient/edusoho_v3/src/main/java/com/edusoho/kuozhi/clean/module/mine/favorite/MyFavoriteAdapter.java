package com.edusoho.kuozhi.clean.module.mine.favorite;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.module.courseset.CourseUnLearnActivity;
import com.edusoho.kuozhi.clean.module.mine.me.MineFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.view.dialog.MoreDialog;
import com.edusoho.kuozhi.v3.view.dialog.SureDialog;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by JesseHuang on 2017/2/7.
 */

public class MyFavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int EMPTY = 0;
    private static final int NOT_EMPTY = 1;
    private int mCurrentDataStatus;

    private List<CourseSet> courseList;
    private Context mContext;

    MyFavoriteAdapter(Context context) {
        courseList = new ArrayList<>();
        mContext = context;
    }

    public void setData(List<CourseSet> list) {
        courseList.clear();
        courseList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mCurrentDataStatus;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NOT_EMPTY) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_collect, parent, false);
            return new MyFavoriteFragment.FavoriteViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_empty, parent, false);
            return new MineFragment.EmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (mCurrentDataStatus == NOT_EMPTY) {
            final CourseSet courseSet = courseList.get(position);
            MyFavoriteFragment.FavoriteViewHolder favoriteViewHolder = (MyFavoriteFragment.FavoriteViewHolder) viewHolder;
            ImageLoader.getInstance().displayImage(courseSet.cover.large
                    , favoriteViewHolder.ivPic, EdusohoApp.app
                            .mOptions);
            favoriteViewHolder.tvAddNum.setText(String.format("%s人参与", courseSet.studentNum));
            favoriteViewHolder.tvTitle.setText(String.valueOf(courseSet.title));
            favoriteViewHolder.recyclerViewItem.setTag(courseSet.id);
            favoriteViewHolder.recyclerViewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CourseUnLearnActivity.launch(mContext, courseSet.id);
                }
            });
            favoriteViewHolder.tvMore.setTag(courseSet);
            favoriteViewHolder.tvMore.setOnClickListener(mMoreClickListener);
            if (courseSet.type.equals("live")) {
                favoriteViewHolder.layoutLive.setVisibility(View.VISIBLE);
            } else {
                favoriteViewHolder.layoutLive.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (courseList != null && courseList.size() != 0) {
            mCurrentDataStatus = NOT_EMPTY;
            return courseList.size();
        }
        mCurrentDataStatus = EMPTY;
        return 1;
    }

    private View.OnClickListener mMoreClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final CourseSet courseSet = (CourseSet) v.getTag();
            MoreDialog dialog = new MoreDialog(mContext);
            dialog.init(mContext.getString(R.string.cancel_favorite_text), new MoreDialog.MoreCallBack() {
                @Override
                public void onMoveClick(View v, final Dialog dialog) {
                    new SureDialog(mContext).init(mContext.getString(R.string.cancel_favorite_hint), new SureDialog.CallBack() {
                        @Override
                        public void onSureClick(View v, final Dialog dialog2) {
                            HttpUtils.getInstance()
                                    .addTokenHeader(EdusohoApp.app.token)
                                    .createApi(UserApi.class)
                                    .cancelFavoriteCourseSet(courseSet.id)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Subscriber<JsonObject>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                        }

                                        @Override
                                        public void onNext(JsonObject jsonObject) {
                                            if (jsonObject != null && jsonObject.get("success").getAsBoolean()) {
                                                CommonUtil.shortToast(mContext, mContext.getString(R.string.cancel_favorite));
                                                courseList.remove(courseSet);
                                                notifyDataSetChanged();
                                                dialog.dismiss();
                                                dialog2.dismiss();
                                            }
                                        }
                                    });
                        }

                        @Override
                        public void onCancelClick(View v, Dialog dialog2) {
                            dialog2.dismiss();
                        }
                    }).show();
                }

                @Override
                public void onShareClick(View v, Dialog dialog) {
                    // TODO: 2017/5/11 分享
                    final ShareTool shareTool =
                            new ShareTool(mContext
                                    , EdusohoApp.app.host + "/courseSet/" + courseSet.id
                                    , courseSet.title
                                    , courseSet.summary.length() > 20 ?
                                      courseSet.summary.substring(0, 20)
                                    : courseSet.summary
                                    , courseSet.cover.middle);
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
}
