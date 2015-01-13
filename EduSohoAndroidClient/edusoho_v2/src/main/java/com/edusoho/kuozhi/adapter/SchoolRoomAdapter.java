package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.SchoolRoom.SchoolRoomEnum;
import com.edusoho.kuozhi.model.SchoolRoom.SchoolRoomItem;
import com.edusoho.kuozhi.model.SchoolRoom.SchoolRoomResult;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.view.ESTextView;
import com.edusoho.kuozhi.view.EduSohoTextBtn;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JesseHuang on 14/12/24.
 */
public class SchoolRoomAdapter<T> extends ListBaseAdapter<T> {

    public SchoolRoomAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return super.getItemId(i);
    }

    @Override
    public Object getItem(int i) {
        if (mList != null && mList.size() > i) {
            return mList.get(i);
        }
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            holder = new ViewHolder();
            holder.ivLogo = (EduSohoTextBtn) convertView.findViewById(R.id.iv_logo);
            holder.tvTitle = (ESTextView) convertView.findViewById(R.id.tv_title);
            holder.tvTitleCenter = (ESTextView) convertView.findViewById(R.id.tv_title_center);
            holder.tvContent = (ESTextView) convertView.findViewById(R.id.tv_content);
            holder.tvTime = (ESTextView) convertView.findViewById(R.id.tv_time);
            holder.tvMsgSum = (TextView) convertView.findViewById(R.id.tv_msg_sum);
            holder.llInterval = (LinearLayout) convertView.findViewById(R.id.ll_interval);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SchoolRoomResult schoolRoomModel = (SchoolRoomResult) mList.get(position);
        holder.tvTitle.setText(schoolRoomModel.title);
        holder.tvTitleCenter.setText(schoolRoomModel.title);
        if (position == mList.size() - 1) {
            holder.llInterval.setVisibility(View.VISIBLE);
        } else {
            holder.llInterval.setVisibility(View.GONE);
        }
        if (EdusohoApp.app.loginUser != null) {
            SchoolRoomItem item = schoolRoomModel.data;
            if (item != null) {
                holder.tvContent.setText(AppUtil.removeHtmlSpace(Html.fromHtml(AppUtil.removeImgTagFromString(item.content)).toString()));
                holder.tvTime.setText(AppUtil.getPostDays(item.time));
                changeItemUI(holder, View.VISIBLE);
            } else {
                changeItemUI(holder, View.GONE);
            }
        } else {
            changeItemUI(holder, View.GONE);
        }
        getImageIndex(schoolRoomModel.title, holder.ivLogo);
        return convertView;
    }

    private void getImageIndex(String type, EduSohoTextBtn btn) {
        int index = SchoolRoomEnum.getIndex(type);
        if (index != 0) {
            switch (index) {
                case 1:
                    //在学课程
                    btn.setIcon(R.string.schoolroom_course);
                    btn.setBackgroundColor(mContext.getResources().getColor(R.color.schoolroom_course));
                    break;
                case 2:
                    //问答
                    btn.setIcon(R.string.schoolroom_question);
                    btn.setBackgroundColor(mContext.getResources().getColor(R.color.schoolroom_question));
                    break;
                case 3:
                    //讨论
                    btn.setIcon(R.string.schoolroom_discussion);
                    btn.setBackgroundColor(mContext.getResources().getColor(R.color.schoolroom_discussion));
                    break;
                case 4:
                    //笔记
                    btn.setIcon(R.string.schoolroom_note);
                    btn.setBackgroundColor(mContext.getResources().getColor(R.color.schoolroom_note));
                    break;
                case 5:
                    //私信
                    btn.setIcon(R.string.schoolroom_letter);
                    btn.setBackgroundColor(mContext.getResources().getColor(R.color.schoolroom_letter));
                    break;
            }
        }
    }

    /**
     * @param holder
     * @param visibility
     */
    private void changeItemUI(ViewHolder holder, int visibility) {
        holder.tvContent.setVisibility(visibility);
        holder.tvTime.setVisibility(visibility);
        holder.tvTitle.setVisibility(visibility);
        holder.tvTitleCenter.setVisibility(visibility == View.GONE ? View.VISIBLE : View.GONE);
        //holder.tvMsgSum.setVisibility(visibility);
        //holder.llInterval.setVisibility(visibility);
    }

    @Override
    public void addItems(ArrayList<T> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addItems(List<T> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(T item) {
        mList.add(0, item);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        public EduSohoTextBtn ivLogo;
        public ESTextView tvTitle;
        public ESTextView tvTitleCenter;
        public ESTextView tvContent;
        public ESTextView tvTime;
        public TextView tvMsgSum;
        public LinearLayout llInterval;
    }


}
