package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.edusoho.kuozhi.v3.listener.ChatDownloadListener;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.ClassroomDiscussEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by JesseHuang on 15/10/16.
 */
public class ClassroomDiscussAdapter extends BaseAdapter implements ChatDownloadListener {
    private ArrayList<ClassroomDiscussEntity> mList;
    private Context mContext;
    private ErrorClick mErrorClick;

    public ClassroomDiscussAdapter(ArrayList<ClassroomDiscussEntity> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
    }

    public void setErrorClick(ErrorClick errorClick) {
        this.mErrorClick = errorClick;
    }

    public void addItems(ArrayList<ClassroomDiscussEntity> list) {
        mList.addAll(0, list);
        notifyDataSetChanged();
    }

    public void clear() {
        if (mList.size() > 0) {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public ClassroomDiscussEntity getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public void updateVoiceDownloadStatus(long downId) {

    }

    @Override
    public HashMap<Long, Integer> getDownloadList() {
        return null;
    }

    public interface ErrorClick {
        public void uploadMediaAgain(File file, ClassroomDiscussEntity model, Chat.FileType type, String strType);

        public void sendMsgAgain(ClassroomDiscussEntity model);
    }
}
