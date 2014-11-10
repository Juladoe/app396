package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.CourseNotice;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.html.EduHtml;

import java.util.ArrayList;

/**
 * Created by onewoman on 14-11-10.
 */
public class CourseNoticeListAdapter extends ListBaseAdapter<CourseNotice>{

    public CourseNoticeListAdapter(Context context, int resource, boolean iscache){
        super(context, resource,iscache);
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
        if(view != null){
            return view;
        }
        if(view == null){
            view = inflater.inflate(mResource,null);
        }
        courseNoticeContent = (TextView) view.findViewById(R.id.course_notice_content);
        courseNoticeissueTime = (TextView) view.findViewById(R.id.course_notice_issue_time);

        CourseNotice courseNotice = mList.get(position);
        //ToDo
        courseNoticeissueTime.setText(AppUtil.getPostDays(courseNotice.createdTime));
        SpannableStringBuilder spanned = EduHtml.coverHtmlImages(courseNotice.content, courseNoticeContent, mContext);
        courseNoticeContent.setText(spanned);

        setCacheView(position,view);
        return view;
    }
}
