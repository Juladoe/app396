package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.CourseNotice;
import com.edusoho.kuozhi.util.AppUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by onewoman on 14-11-10.
 */
public class CourseNoticeListAdapter extends ListBaseAdapter<CourseNotice> {
    private static int mContentImageSize = 0;
    private static final float GRIDVIEW_CONTENT_PROPORTION = 0.65f;
    private static final int GRIDVIEW_SPACING = 10;
    private DisplayImageOptions mOptions;

    public CourseNoticeListAdapter(Context context, int resource, boolean iscache) {
        super(context, resource, iscache);
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    }

    @Override
    public void addItems(ArrayList<CourseNotice> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        TextView courseNoticeContent;
        TextView courseNoticeissueTime;

        view = getCacheView(position);
        if (view != null) {
            return view;
        }

        if (view == null) {
            view = inflater.inflate(mResource, null);
        }

        courseNoticeContent = (TextView) view.findViewById(R.id.course_notice_content);
        courseNoticeissueTime = (TextView) view.findViewById(R.id.course_notice_issue_time);
        ImageView noticeImage = (ImageView) view.findViewById(R.id.course_notice_image);

        CourseNotice courseNotice = mList.get(position);
        String content = AppUtil.coverCourseAbout(courseNotice.content);
        content = content.replace("\n", "");
        courseNoticeContent.setText(content);
        courseNoticeissueTime.setText(AppUtil.getPostDays(courseNotice.createdTime));

        ArrayList<String> mUrlList = convertUrlStringList(courseNotice.content);
        if (mUrlList.size() > 0) {
            ImageLoader.getInstance().displayImage(mUrlList.get(0), noticeImage);
        }

        setCacheView(position, view);
        return view;
    }

    private ArrayList<String> convertUrlStringList(String content) {
        ArrayList<String> urlLits = new ArrayList<String>();
        Matcher m = Pattern.compile("(img src=\".*?\")").matcher(content);
        while (m.find()) {
            String[] s = m.group(1).split("src=");
            String strUrl = s[1].toString().substring(1, s[1].length() - 1);
            urlLits.add(strUrl);
        }
        return urlLits;
    }

    private void addGridView(GridView gvImage, ViewGroup parent, int imageNum) {
        int horizontalSpacingNum = 2;
        if (imageNum < 3) {
            horizontalSpacingNum = imageNum % 3 - 1;
        }
        int verticalSapcingNum = (int) Math.ceil(imageNum / 3.0) - 1;

        int gridviewWidth = (int) ((EdusohoApp.screenW - 15 * 2) * GRIDVIEW_CONTENT_PROPORTION + horizontalSpacingNum * GRIDVIEW_SPACING);
        int gridviewHeight = (int) ((EdusohoApp.screenW - 15 * 2) * GRIDVIEW_CONTENT_PROPORTION / 3 + verticalSapcingNum * GRIDVIEW_SPACING);

        mContentImageSize = gridviewWidth / 3;

        RelativeLayout.LayoutParams gvLayout = new RelativeLayout.LayoutParams(gridviewWidth,
                gridviewHeight);

        gvLayout.setMargins(0, 15, 0, 0);
        gvImage.setLayoutParams(gvLayout);
        gvImage.setVerticalScrollBarEnabled(false);
        gvImage.setNumColumns(3);
        gvImage.setVerticalSpacing(GRIDVIEW_SPACING);
        gvImage.setHorizontalSpacing(GRIDVIEW_SPACING);
        gvImage.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        parent.addView(gvImage);
    }
}
