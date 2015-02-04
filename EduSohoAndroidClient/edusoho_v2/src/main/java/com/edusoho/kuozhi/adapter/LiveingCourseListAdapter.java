package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.LiveingCourse;
import com.edusoho.kuozhi.model.LiveingCourseResult;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by onewoman on 2015/1/30.
 */
public class LiveingCourseListAdapter extends ListBaseAdapter<LiveingCourse>{
    private DisplayImageOptions mDisplayImageOptions;
    public LiveingCourseListAdapter(Context context, int resource) {
        super(context, resource);
        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(false)
                .cacheInMemory(true)
                .build();
    }

    @Override
    public void addItems(ArrayList<LiveingCourse> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHost viewHost = new ViewHost();
        if(view == null){
            view = inflater.inflate(mResource,null);
            viewHost.liveingCourseImage = (ImageView) view.findViewById(R.id.liveing_course_icon);
            viewHost.tvLiveingCourseTitle = (TextView) view.findViewById(R.id.liveing_course_title);
            viewHost.tvLiveingCourseTime = (TextView) view.findViewById(R.id.liveing_course_time);
            view.setTag(viewHost);
        }else{
            viewHost = (ViewHost) view.getTag();
        }
        LiveingCourse liveingCourseData = mList.get(i);
        ImageLoader.getInstance().displayImage(liveingCourseData.largePicture, viewHost.liveingCourseImage,
                mDisplayImageOptions);

        viewHost.tvLiveingCourseTitle.setText(liveingCourseData.title);
        if("".equals(liveingCourseData.liveLessonTitle)){
            viewHost.tvLiveingCourseTime.setText("暂时没有要开始的直播");
        }else{
            //todo
            long nowTime = System.currentTimeMillis();
            String liveingCourseTime;
            if(Integer.valueOf(liveingCourseData.liveStartTime) > nowTime){
                liveingCourseTime = String.format("距离直播%s开始还有", liveingCourseData.liveLessonTitle);
                long diffTime = Integer.valueOf(liveingCourseData.liveStartTime) - nowTime;
                if(diffTime < 60 && diffTime > 0){
                    liveingCourseTime = liveingCourseTime + diffTime + "秒";
                }else if(diffTime < 60 * 60){
                    liveingCourseTime = liveingCourseTime + diffTime / 60 + "分钟";
                }else if(diffTime < 60 * 60 * 24){
                    liveingCourseTime = liveingCourseTime +  diffTime / (60 * 60) + "小时";
                }else{
                    liveingCourseTime = liveingCourseTime +  diffTime / (60 * 60 * 24) + "天";
                }
            }else{
                liveingCourseTime = String.format("正在直播:%s", liveingCourseData.liveLessonTitle);
            }
            viewHost.tvLiveingCourseTime.setText(liveingCourseTime);
        }
        return view;
    }

    private class ViewHost{
        public ImageView liveingCourseImage;
        public TextView tvLiveingCourseTitle;
        public TextView tvLiveingCourseTime;
    }
}
