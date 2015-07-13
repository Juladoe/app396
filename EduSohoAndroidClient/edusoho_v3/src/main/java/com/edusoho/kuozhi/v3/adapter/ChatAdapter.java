package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by JesseHuang on 15/6/3.
 */
public class ChatAdapter extends BaseAdapter {

    private Context mContext;
    private List<Chat> mList;
    private static long TIME_INTERVAL = 60 * 5;

    private static final int TYPE_COUNT = 6;
    private static final int SEND = 0;
    private static final int RECEIVE = 1;
    private static final int MSG_SEND_TEXT = 2;
    private static final int MSG_RECEIVE_TEXT = 3;
    private static final int MSG_SEND_IMAGE = 4;
    private static final int MSG_RECEIVE_IMAGE = 5;
    private static final int MSG_SEND_AUDIO = 5;
    private static final int MSG_RECEIVE_AUDIO = 6;


    public void addItems(ArrayList<Chat> list) {
        mList.addAll(0, list);
        notifyDataSetChanged();
    }

    public ChatAdapter(Context ctx, List<Chat> list) {
        mContext = ctx;
        mList = list;
    }

    public void addOneChat(Chat chat) {
        mList.add(chat);
        notifyDataSetChanged();
    }

    public void updateItemByCreatedTime(Chat chat) {
        int pos = 0;
        Iterator<Chat> iterator = mList.iterator();
        while (iterator.hasNext()) {
            Chat tmpChat = iterator.next();
            if (tmpChat.chatId == chat.chatId) {
                mList.remove(tmpChat);
                mList.add(pos, tmpChat);
                notifyDataSetChanged();
                break;
            }
            pos++;
        }
    }

    public void clear() {
        if (mList.size() > 0) {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        Chat msg = mList.get(position);
        int type = -1;
        if (msg.direct == Chat.Direct.SEND) {
            switch (msg.fileType) {
                case TEXT:
                    type = MSG_SEND_TEXT;
                    break;
                case IMAGE:
                    type = MSG_SEND_IMAGE;
                    break;
                case AUDIO:
                    type = MSG_SEND_AUDIO;
                    break;
            }
        } else {
            switch (msg.fileType) {
                case TEXT:
                    type = MSG_RECEIVE_TEXT;
                    break;
                case IMAGE:
                    type = MSG_RECEIVE_IMAGE;
                    break;
                case AUDIO:
                    type = MSG_RECEIVE_AUDIO;
                    break;
            }
        }
        return type;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int type = getItemViewType(position);
        if (convertView == null) {
            convertView = createViewByType(type);
            holder = new ViewHolder(convertView, type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (type) {
            case MSG_SEND_TEXT:
                handleMsgText(holder, position);
                break;
            case MSG_RECEIVE_TEXT:
                handleMsgText(holder, position);
                break;
            case MSG_SEND_IMAGE:
                handlerSendImage(holder, position);
                break;
            case MSG_RECEIVE_IMAGE:
                handlerReceiveMsgImage(holder, position);
                break;
        }
        return convertView;
    }

    private void handleMsgText(ViewHolder holder, int position) {
        Chat model = mList.get(position);
        holder.tvSendTime.setVisibility(View.GONE);
        if (position > 0) {
            if (model.createdTime - mList.get(position - 1).createdTime > TIME_INTERVAL) {
                holder.tvSendTime.setVisibility(View.VISIBLE);
                holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
            }
        } else {
            holder.tvSendTime.setVisibility(View.VISIBLE);
            holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
        }
        holder.tvSendContent.setText(model.content);
        ImageLoader.getInstance().displayImage(model.headimgurl, holder.ciPic, EdusohoApp.app.mOptions);
    }

    private void handlerSendImage(final ViewHolder holder, int position) {
        final Chat model = mList.get(position);
        if (position > 0) {
            if (model.createdTime - mList.get(position - 1).createdTime > TIME_INTERVAL) {
                holder.tvSendTime.setVisibility(View.VISIBLE);
                holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
            } else {
                holder.tvSendTime.setVisibility(View.GONE);
            }
        } else {
            holder.tvSendTime.setVisibility(View.VISIBLE);
            holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
        }
        switch (model.getDelivery()) {
            case SUCCESS:
                holder.pbLoading.setVisibility(View.GONE);
                holder.ivStateError.setVisibility(View.GONE);
                break;
            case UPLOADING:
                holder.pbLoading.setVisibility(View.VISIBLE);
                holder.ivStateError.setVisibility(View.GONE);
                break;
            case FAILED:
                holder.pbLoading.setVisibility(View.GONE);
                holder.ivStateError.setVisibility(View.VISIBLE);
                holder.ivStateError.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
        }
        holder.ivMsgImage.setOnClickListener(new ImageMsgClick("file://" + model.content));
        ImageLoader.getInstance().displayImage("file://" + getThumbFromOriginalImagePath(model.content), holder.ivMsgImage, EdusohoApp.app.mOptions);
        ImageLoader.getInstance().displayImage(model.headimgurl, holder.ciPic, EdusohoApp.app.mOptions);
    }

    private void handlerReceiveMsgImage(final ViewHolder holder, int position) {
        final Chat model = mList.get(position);
        final MyImageLoadingListener mMyImageLoadingListener = new MyImageLoadingListener(holder);
        if (position > 0) {
            if (model.createdTime - mList.get(position - 1).createdTime > TIME_INTERVAL) {
                holder.tvSendTime.setVisibility(View.VISIBLE);
                holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
            } else {
                holder.tvSendTime.setVisibility(View.GONE);
            }
        } else {
            holder.tvSendTime.setVisibility(View.VISIBLE);
            holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
        }
        holder.ivStateError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageLoader.getInstance().displayImage(model.content, holder.ivMsgImage, EdusohoApp.app.mOptions, mMyImageLoadingListener);
            }
        });
        ImageLoader.getInstance().displayImage(model.headimgurl, holder.ciPic, EdusohoApp.app.mOptions);

        File receiveImage = ImageLoader.getInstance().getDiskCache().get(model.content);
        holder.ivMsgImage.setOnClickListener(new ImageMsgClick(model.content));
        if (receiveImage.exists()) {
            String thumbImagePath = getThumbFromImageName(receiveImage.getName());
            File thumbImage = new File(thumbImagePath);
            if (thumbImage.exists()) {
                holder.pbLoading.setVisibility(View.GONE);
                holder.ivStateError.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage("file://" + thumbImagePath, holder.ivMsgImage);
                return;
            }
        }
        ImageLoader.getInstance().displayImage(model.content, holder.ivMsgImage, EdusohoApp.app.mOptions, mMyImageLoadingListener);
    }

    /**
     * 获取缩略图文件路径
     *
     * @param imagePath
     * @return
     */
    private String getThumbFromOriginalImagePath(String imagePath) {
        return imagePath.replace(Const.UPLOAD_IMAGE_CACHE_FILE, Const.UPLOAD_IMAGE_CACHE_THUMB_FILE);
    }

    private String getThumbFromImageName(String imageName) {
        return EdusohoApp.app.getWorkSpace() + Const.UPLOAD_IMAGE_CACHE_THUMB_FILE + "/" + imageName;
    }

    private class ImageMsgClick implements View.OnClickListener {
        private String mImageUrl;

        public ImageMsgClick(String url) {
            this.mImageUrl = url;
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putInt("index", 1);
            bundle.putStringArray("images", new String[]{mImageUrl});
            EdusohoApp.app.mEngine.runNormalPluginWithBundle("ViewPagerActivity", mContext, bundle);
        }
    }

    private class MyImageLoadingListener implements ImageLoadingListener {
        private ViewHolder holder;

        public MyImageLoadingListener(ViewHolder h) {
            this.holder = h;
        }

        @Override
        public void onLoadingStarted(String s, View view) {
            holder.pbLoading.setVisibility(View.VISIBLE);
            holder.ivStateError.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingFailed(String s, View view, FailReason failReason) {
            holder.pbLoading.setVisibility(View.GONE);
            holder.ivStateError.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
            //接受到以后进行分辨率压缩并缓存
            if (bitmap.getWidth() > EdusohoApp.app.screenW * 0.4f) {
                bitmap = AppUtil.scaleImage(bitmap, EdusohoApp.app.screenW * 0.4f, 0);
            }
            File receiveFile = ImageLoader.getInstance().getDiskCache().get(s);
            try {
                AppUtil.convertBitmap2File(bitmap, EdusohoApp.getWorkSpace() + Const.UPLOAD_IMAGE_CACHE_THUMB_FILE + "/" + receiveFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }

            holder.pbLoading.setVisibility(View.GONE);
            holder.ivStateError.setVisibility(View.GONE);
            ((ImageView) view).setImageBitmap(bitmap);
        }

        @Override
        public void onLoadingCancelled(String s, View view) {

        }
    }

    private View createViewByType(int type) {
        View convertView = null;
        switch (type) {
            case MSG_SEND_TEXT:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_msg_send_text, null);
                break;
            case MSG_RECEIVE_TEXT:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_msg_receive_text, null);
                break;
            case MSG_SEND_IMAGE:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_msg_send_image, null);
                break;
            case MSG_RECEIVE_IMAGE:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_msg_receive_image, null);
                break;
        }
        return convertView;
    }

    private static class ViewHolder {
        public TextView tvSendTime;
        public TextView tvSendContent;
        public CircleImageView ciPic;
        public ImageView ivMsgImage;
        public ProgressBar pbLoading;
        public ImageView ivStateError;

        public ViewHolder(View view, int type) {
            switch (type) {
                case MSG_SEND_TEXT:
                case MSG_RECEIVE_TEXT:
                    tvSendTime = (TextView) view.findViewById(R.id.tv_send_time);
                    tvSendContent = (TextView) view.findViewById(R.id.tv_send_content);
                    ciPic = (CircleImageView) view.findViewById(R.id.ci_send_pic);
                    break;
                case MSG_SEND_IMAGE:
                case MSG_RECEIVE_IMAGE:
                    tvSendTime = (TextView) view.findViewById(R.id.tv_send_time);
                    tvSendContent = (TextView) view.findViewById(R.id.tv_send_content);
                    ciPic = (CircleImageView) view.findViewById(R.id.ci_send_pic);
                    ivMsgImage = (ImageView) view.findViewById(R.id.iv_msg_image);
                    pbLoading = (ProgressBar) view.findViewById(R.id.sendProgressPar);
                    ivStateError = (ImageView) view.findViewById(R.id.msg_status);
                    break;
            }
        }
    }
}
