package com.soooner.EplayerPluginLibary.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.soooner.EplayerPluginLibary.EplayerPluginBaseActivity;
import com.soooner.EplayerPluginLibary.R;
import com.soooner.EplayerPluginLibary.data.AnimationDrawableUtil;
import com.soooner.source.common.util.DeviceUtil;
import com.soooner.source.entity.SessionData.SocketMessage;
import com.soooner.source.entity.SessionEmun.MessageChatType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhaoxu2014 on 14-8-20.
 */
public class SpeakAdapter  extends BaseAdapter{
    EplayerPluginBaseActivity epActivity;
    int MAX_ITEM_NUM=Integer.MAX_VALUE;

    List<SocketMessage> speakAllList=new ArrayList<SocketMessage>();
    List<SocketMessage> speakList=new ArrayList<SocketMessage>();

    private LayoutInflater mInflater;
    public List<String> blackList=new ArrayList<String>();//黒名单

    public SpeakAdapter(EplayerPluginBaseActivity epActivity) {
        this.epActivity = epActivity;
        mInflater= LayoutInflater.from(epActivity);
    }


    /**
     *
     * @param bist  列表
     * @param personchatForbid 禁言吗，
     */

    public void updateBlackList(List<String> bist,boolean personchatForbid) {
        if (null == bist ||bist.size()<=0) {
           return;
        }

        if(personchatForbid){
            for(int i=0;i<bist.size();i++){
                blackList.add(bist.get(i));
            }
        }else{
            for(int i=0;i<bist.size();i++){
                String temp=bist.get(i);
                if(blackList.contains(temp)){
                    blackList.remove(temp);
                }

            }
        }
        refresh();
        this.notifyDataSetChanged();
    }

    /**
     * 全体禁言
     *
     * @param allchatForbid
     */
    public void updateBlackList(boolean allchatForbid){
        if(allchatForbid) {
            speakAllList = new ArrayList<SocketMessage>();
            speakList = new ArrayList<SocketMessage>();
            this.notifyDataSetChanged();
        }
    }

    public void refresh(){
        speakList=new ArrayList<SocketMessage>();

        Iterator<SocketMessage> iter = speakAllList.iterator();
        while(iter.hasNext()){
            SocketMessage  socketMessage = iter.next();
            boolean isContains=false;
            for(int k=0;k<blackList.size();k++){
                String userkey=blackList.get(k);
                if(socketMessage.userKey.equals(userkey)){
                    isContains=true;
                    break;
                }
            }
            if(!isContains){
                speakList.add(socketMessage);
            }else{//个人被禁言后，其对应的聊天记录完全删除，本地不保留
                iter.remove();
            }
        }



    }

    public void addSpeakList(List<SocketMessage> list) {
        if(null==list||list.size()<=0){
            return;
        }
        for(int i=0;i<list.size();i++){
            if(speakAllList.size()<MAX_ITEM_NUM){
                speakAllList.add(0,list.get(i));
            }else{
                speakAllList.remove(speakAllList.size()-1);
                speakAllList.add(0,list.get(i));
            }

        }
        refresh();


    }

    @Override
    public int getCount() {
        return speakList.size();
    }

    @Override
    public Object getItem(int i) {
        return speakList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder holder;

        if (null == convertView) {
            view = mInflater.inflate(R.layout.eplayer_list_item, null);
            holder = new ViewHolder();
            holder.tv_nickname = (TextView) view.findViewById(R.id.tv_nickname);
            holder.tv_content = (TextView) view.findViewById(R.id.tv_content);
            holder.img_animation= (ImageView) view.findViewById(R.id.img_animation);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
            holder.img_animation.setImageDrawable(null);
        }

        holder.tv_nickname.setText("");
        holder.tv_content.setText("");


        SocketMessage socketMessage= (SocketMessage) getItem(position);


        holder.tv_nickname.setTextColor(socketMessage.getNickNameColor());
        if(socketMessage.userKey.equals( DeviceUtil.getUDID())){
            holder.tv_nickname.setText("我：");
        }else{
            holder.tv_nickname.setText(socketMessage.nickname+"：");
        }
        if(socketMessage.chatType== MessageChatType.MessageChatTypeReward){
            holder.img_animation.setVisibility(View.VISIBLE);
            holder.tv_content.setVisibility(View.GONE);
            AnimationDrawable animationDrawable= AnimationDrawableUtil.getAnimationDrawable(epActivity,socketMessage.content);
            if(null!=animationDrawable) {
                holder.img_animation.setImageDrawable(animationDrawable);
                animationDrawable.start();
            }

        }else{
            holder.img_animation.setVisibility(View.GONE);
            holder.tv_content.setVisibility(View.VISIBLE);
            SpannableString spannableString= AnimationDrawableUtil.getExpressionString(epActivity,socketMessage.content);
            holder.tv_content.setText(spannableString);
        }






        return view;
    }
    static class ViewHolder {
        TextView tv_nickname;
        TextView tv_content;
        ImageView img_animation ;
    }
}
