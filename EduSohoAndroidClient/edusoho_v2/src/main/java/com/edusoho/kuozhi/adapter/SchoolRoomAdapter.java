package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.SchoolRoom.SchoolRoomEnum;
import com.edusoho.kuozhi.model.SchoolRoom.SchoolRoomItem;
import com.edusoho.kuozhi.model.SchoolRoom.SchoolRoomResult;
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
            holder.tvContent = (ESTextView) convertView.findViewById(R.id.tv_content);
            holder.tvTime = (ESTextView) convertView.findViewById(R.id.tv_time);
            holder.tvMsgSum = (TextView) convertView.findViewById(R.id.tv_msg_sum);
            holder.llInterval = (LinearLayout) convertView.findViewById(R.id.ll_interval);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //暂时最后一项私信需要间隔，需修改
        if (position == mList.size() - 1) {
            holder.llInterval.setVisibility(View.VISIBLE);
        } else {
            holder.llInterval.setVisibility(View.GONE);
        }
        SchoolRoomResult schoolRoomModel = (SchoolRoomResult) mList.get(position);
        SchoolRoomItem item = schoolRoomModel.data;
        holder.tvTitle.setText(schoolRoomModel.title);
        holder.tvContent.setText(removeHtmlSpace(Html.fromHtml(removeImgTagFromString(item.content)).toString()));
        holder.tvTime.setText(AppUtil.getPostDays(item.time));
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
        public ESTextView tvContent;
        public ESTextView tvTime;
        public TextView tvMsgSum;
        public LinearLayout llInterval;
    }

    /**
     * 去掉所有<Img>标签
     *
     * @param content
     * @return
     */
    private String removeImgTagFromString(String content) {
        Matcher m = Pattern.compile("(<img src=\".*?\" .>)").matcher(content);
        new StringBuffer().append("1");
        while (m.find()) {
            content = content.replace(m.group(1), "");
        }
        return content;
    }

    /**
     * 去掉字符串中的\n\t
     *
     * @param content
     * @return
     */
    private String removeHtmlSpace(String content) {
        Matcher m = Pattern.compile("\\t|\\n").matcher(content);
        while (m.find()) {
            content = content.replace(m.group(0), "");
        }
        return content;
    }
}
