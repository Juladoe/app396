package com.edusoho.kuozhi.v3.entity.lesson;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.ui.adapter.MessageRecyclerListAdapter;

/**
 * Created by DF on 2017/1/5.
 */

public class QuestionAnswerAdapter extends MessageRecyclerListAdapter {

    private View VIEW_HEADER;
    //Type
    private static final int TYPE_HEADER = 1001;

    public QuestionAnswerAdapter(Context context) {
        super(context);
    }
//    @Override
//    public int getItemViewType(int position) {
//        MessageBody messageBody = new MessageBody(mMessageList.get(position));
//        String type = messageBody.getType();
//        if (isHeaderView(position)) {
//            return TYPE_HEADER;
//        } else {
//            switch (type) {
//                case PushUtil.ChatMsgType.TEXT:
//                    return RECEIVE_TEXT;
//                case PushUtil.ChatMsgType.AUDIO:
//                    return RECEIVE_AUDIO;
//                case PushUtil.ChatMsgType.IMAGE:
//                    return RECEIVE_IMAGE;
//                case PushUtil.ChatMsgType.PUSH:
//                case PushUtil.ChatMsgType.MULTI:
//                    return RECEIVE_MULTI;
//                case PushUtil.ChatMsgType.LABEL:
//                    return LABEL;
//            }
//            return RECEIVE_TEXT;
//        }
//        if (isHeaderView(position)) {
//            return TYPE_HEADER;
//        } else {
//            return super.getItemViewType(position);
//        }
//    }

//    @Override
//    protected View getItemView(int type) {
//        View contentView = null;
//        switch (type) {
//            case RECEIVE_TEXT:
//                contentView = createTextView(false);
//                break;
//            case RECEIVE_AUDIO:
//                contentView = createAudioView(false);
//                break;
//            case RECEIVE_IMAGE:
//                contentView = createImageView(false);
//                break;
//            case RECEIVE_MULTI:
//                contentView = createMultiView(false);
//                break;
//            case TYPE_HEADER:
//                contentView = createHeadView();
//                break;
//            case LABEL:
//                contentView = createLabelView();
//        }
//        return contentView;
//    }

    public View createHeadView(){
        return LayoutInflater.from(mContext).inflate(R.layout.thread_discuss_head_layout, null);
    }

    public void addHeaderView(View headerView) {
        if (haveHeaderView()) {
            throw new IllegalStateException("hearview has already exists!");
        } else {
            //避免出现宽度自适应
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            headerView.setLayoutParams(params);
            VIEW_HEADER = headerView;
            notifyItemInserted(0);
        }

    }

    @Override
    public int getItemCount() {
        int count = (mMessageList == null ? 0 : mMessageList.size());
        if (VIEW_HEADER != null) {
            count++;
        }
        return count;
    }

    private boolean haveHeaderView() {
        return VIEW_HEADER != null;
    }

    private boolean isHeaderView(int position) {
        return VIEW_HEADER != null && position == 0;
    }


//        resourcesBean = mList.get(position);
//        ImageLoader.getInstance().displayImage(resourcesBean.getUser().getAvatar(), viewHolder.ivUserIcon);
//        viewHolder.tvName.setText(resourcesBean.getUser().getNickname());
//        viewHolder.tvTime.setText(resourcesBean.getCreatedTime().split("T")[0]);
//        viewHolder.ivContent.setImageResource(R.drawable.oval_white_bg);
//        viewHolder.tvContent.setVisibility(View.GONE);
//        viewHolder.ivContent.setVisibility(View.GONE);
//        if (resourcesBean.getContent().contains("<img alt=")) {
//            final String url;
//            viewHolder.ivContent.setVisibility(View.VISIBLE);
//            if (resourcesBean.getContent().contains("<p>")) {
//                url = ((BaseActivity) mcontext).app.host + resourcesBean.getContent().split("\"")[3];
//                ImageLoader.getInstance().displayImage(url, viewHolder.ivContent);
//            } else {
//                url = resourcesBean.getContent().split("\"")[3];
//                ImageLoader.getInstance().displayImage(url, viewHolder.ivContent);
//            }
//            viewHolder.ivContent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showBigImage(url);
//                }
//            });
//        } else if (resourcesBean.getContent().contains("{\"f\":\"http://")) {
//            viewHolder.ivContent.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.tvContent.setVisibility(View.VISIBLE);
//            viewHolder.tvContent.setText(resourcesBean.getContent());
//        }
//        return convertView;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 2;
//    }
//
//    public static class ViewHolder{
//
//        private final ImageView ivUserIcon;
//        private final TextView tvName;
//        private final TextView tvTime;
//        private final TextView tvContent;
//        private final ImageView ivContent;
//
//        public ViewHolder(View view) {
//            ivUserIcon = (ImageView) view.findViewById(R.id.iv_user_icon);
//            tvName = (TextView) view.findViewById(R.id.tv_user_name);
//            tvTime = (TextView) view.findViewById(R.id.tv_time);
//            tvContent = (TextView) view.findViewById(R.id.tv_content);
//            ivContent = (ImageView) view.findViewById(R.id.iv_content);
//        }
//    }
//
//    public boolean isAdd;
//    private void showBigImage(String url) {
//        if (!isAdd) {
//            isAdd = true;
//            dialog = new Dialog(mcontext, R.style.dialog_big_image);
//            View dialogView = LayoutInflater.from(mcontext).inflate(R.layout.dialog_image, null);
//            ivBig = (ImageView) dialogView.findViewById(R.id.iv_big);
//            dialogView.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                    ivBig.setImageResource(R.drawable.oval_white_bg);
//                }
//            });
//            dialog.setContentView(dialogView);
//            dialog.setCancelable(false);
//        }
//        ImageLoader.getInstance().displayImage(url, ivBig);
//        dialog.show();
//    }
}
