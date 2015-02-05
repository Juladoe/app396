package com.soooner.EplayerPluginLibary.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.soooner.EplayerPluginLibary.EplayerPluginBaseActivity;
import com.soooner.EplayerPluginLibary.R;
import com.soooner.EplayerPluginLibary.util.StringUtils;
import com.soooner.EplayerPluginLibary.util.TaskType;
import com.soooner.EplayerPluginLibary.util.ToastUtil;
import com.soooner.source.common.util.DeviceUtil;
import com.soooner.source.entity.SessionData.VoteMsgInfo;
import com.soooner.source.entity.SessionData.VoteStatisticMsgInfo;
import com.soooner.source.entity.SessionEmun.VoteType;
import com.soooner.ws.net.Sender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoxu2014 on 14-12-5.
 */
public class VoteControllerView extends LinearLayout {
    public static  final  int HIDE_DELAY_TIME=1000*20;
    EplayerPluginBaseActivity activity;
    public static int DEVICE_TYPE = DeviceUtil.DEVICE_TYPE_PHONE;
    View view_top;
    TextView tv_enter,tv_cancel, tv_vote_style1_bt1, tv_vote_style1_bt1_desc, tv_vote_style1_bt2, tv_vote_style1_bt2_desc, tv_vote_style1_bt3, tv_vote_style1_bt3_desc, tv_vote_style1_bt4, tv_vote_style1_bt4_desc, tv_vote_style2_bt1, tv_vote_style2_bt1_desc, tv_vote_style2_bt2, tv_vote_style2_bt2_desc, tv_count_num_hint;
    LinearLayout li_vote_style1, li_vote_style2,li_vote_all;

    TextView tv_desc;

    Animation face_enter, face_exit;
    VoteMsgInfo msgInfo = null;

    private static  final int PADDEAFULTSELECTPOSTION=-1;
    int padSelectPostion=PADDEAFULTSELECTPOSTION;//标识pad选中了那个按钮
    enum  VoteState{
        VoteStateNoraml,VoteStateSendMessage,VoteStateSendMesssageScuess
    }
    VoteState voteState=VoteState.VoteStateNoraml;

    List<TextView> style1Bts = new ArrayList<TextView>();
    List<TextView> style1Descs = new ArrayList<TextView>();
    List<TextView> style2Bts = new ArrayList<TextView>();
    List<TextView> style2Descs = new ArrayList<TextView>();

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TaskType.MESSAGE_VOTE_HIDE:{
                    hideView();
                    break;
                }

            }
            super.handleMessage(msg);
        }
    };

    public VoteControllerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VoteControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VoteControllerView(Context context) {
        super(context);

    }

    public void init(EplayerPluginBaseActivity activity, int DEVICE_TYPE) {
        this.activity = activity;
        this.DEVICE_TYPE = DEVICE_TYPE;

        face_enter = AnimationUtils.loadAnimation(activity, R.anim.face_enter);
        face_exit = AnimationUtils.loadAnimation(activity, R.anim.face_exit);
        View view = null;
        switch (DEVICE_TYPE) {
            case DeviceUtil.DEVICE_TYPE_PHONE: {
                view = View.inflate(activity, R.layout.votecontroller_phone, null);
                break;
            }
            case DeviceUtil.DEVICE_TYPE_PAD: {
                view = View.inflate(activity, R.layout.votecontroller_pad, null);

                break;
            }
        }

        initView(view);

        this.addView(view);
    }


    private void initView(View view) {
        tv_desc= (TextView) view.findViewById(R.id.tv_desc);

        view_top = view.findViewById(R.id.view_top);
        tv_enter= (TextView) view.findViewById(R.id.tv_enter);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_vote_style1_bt1 = (TextView) view.findViewById(R.id.tv_vote_style1_bt1);
        tv_vote_style1_bt1_desc = (TextView) view.findViewById(R.id.tv_vote_style1_bt1_desc);
        tv_vote_style1_bt2 = (TextView) view.findViewById(R.id.tv_vote_style1_bt2);
        tv_vote_style1_bt2_desc = (TextView) view.findViewById(R.id.tv_vote_style1_bt2_desc);
        tv_vote_style1_bt3 = (TextView) view.findViewById(R.id.tv_vote_style1_bt3);
        tv_vote_style1_bt3_desc = (TextView) view.findViewById(R.id.tv_vote_style1_bt3_desc);
        tv_vote_style1_bt4 = (TextView) view.findViewById(R.id.tv_vote_style1_bt4);
        tv_vote_style1_bt4_desc = (TextView) view.findViewById(R.id.tv_vote_style1_bt4_desc);
        tv_vote_style2_bt1 = (TextView) view.findViewById(R.id.tv_vote_style2_bt1);
        tv_vote_style2_bt1_desc = (TextView) view.findViewById(R.id.tv_vote_style2_bt1_desc);
        tv_vote_style2_bt2 = (TextView) view.findViewById(R.id.tv_vote_style2_bt2);
        tv_vote_style2_bt2_desc = (TextView) view.findViewById(R.id.tv_vote_style2_bt2_desc);


        tv_cancel.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View view) {
                hideView();
            }
        });

        if(DEVICE_TYPE == DeviceUtil.DEVICE_TYPE_PAD){
            tv_enter.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View view) {
                    if(padSelectPostion==PADDEAFULTSELECTPOSTION){
                        ToastUtil.showStringToast(activity,"您还没有做出选择");
                        return;
                    }
                    switch (msgInfo.voteType) {
                        case VoteType1:
                        case VoteType2:
                            sendMessage(msgInfo.voteKey, msgInfo.voteType, padSelectPostion+1);
                            break;
                        case VoteType3:
                        case VoteType4:
                        case VoteType5:
                            if (padSelectPostion == 0) {
                                sendMessage(msgInfo.voteKey, msgInfo.voteType, 1);
                            } else {
                                sendMessage(msgInfo.voteKey, msgInfo.voteType, 3);
                            }
                            break;

                    }
                }
            });
        }

        style1Bts.add(tv_vote_style1_bt1);
        style1Bts.add(tv_vote_style1_bt2);
        style1Bts.add(tv_vote_style1_bt3);
        style1Bts.add(tv_vote_style1_bt4);
        style1Descs.add(tv_vote_style1_bt1_desc);
        style1Descs.add(tv_vote_style1_bt2_desc);
        style1Descs.add(tv_vote_style1_bt3_desc);
        style1Descs.add(tv_vote_style1_bt4_desc);

        style2Bts.add(tv_vote_style2_bt1);
        style2Bts.add(tv_vote_style2_bt2);
        style2Descs.add(tv_vote_style2_bt1_desc);
        style2Descs.add(tv_vote_style2_bt2_desc);

        for (int i = 0; i < style1Bts.size(); i++) {
            final int postion = i;
            final TextView tv = style1Bts.get(postion);
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (DEVICE_TYPE == DeviceUtil.DEVICE_TYPE_PHONE) {
                        sendMessage(msgInfo.voteKey, msgInfo.voteType, postion + 1);
                        tv.setBackgroundResource(R.drawable.vote_bt_select);
                        tv.setTextColor(activity.getResources().getColor(R.color.white));
                    }else{
                        padSelectPostion=postion;
                        for (int j = 0; j < style1Bts.size(); j++) {
                            TextView tvtemp = style1Bts.get(j);
                            if(tvtemp==view){
                                tvtemp.setBackgroundResource(R.drawable.vote_pad_bt_select);
                            }else{
                                tvtemp.setBackgroundResource(R.drawable.vote_pad_bt_unselect);
                            }

                        }

                    }

                }
            });
        }

        for (int i = 0; i < style2Bts.size(); i++) {
            final int postion = i;
            final TextView tv =style2Bts.get(postion);
            tv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (DEVICE_TYPE == DeviceUtil.DEVICE_TYPE_PHONE) {
                        if (null != msgInfo) {
                            if (postion == 0) {
                                sendMessage(msgInfo.voteKey, msgInfo.voteType, 1);
                            } else {
                                sendMessage(msgInfo.voteKey, msgInfo.voteType, 3);
                            }
                        }
                        tv.setBackgroundResource(R.drawable.vote_bt_select);
                        tv.setTextColor(activity.getResources().getColor(R.color.white));
                    } else {
                        padSelectPostion=postion;
                        for (int j = 0; j < style2Bts.size(); j++) {
                            TextView tvtemp = style2Bts.get(j);
                            if(tvtemp==view){
                                tvtemp.setBackgroundResource(R.drawable.vote_pad_bt_select);
                            }else{
                                tvtemp.setBackgroundResource(R.drawable.vote_pad_bt_unselect);
                            }

                        }
                    }
                }
            });
        }


        tv_count_num_hint = (TextView) view.findViewById(R.id.tv_count_num_hint);
        li_vote_style1 = (LinearLayout) view.findViewById(R.id.li_vote_style1);
        li_vote_style2 = (LinearLayout) view.findViewById(R.id.li_vote_style2);
        li_vote_all= (LinearLayout) view.findViewById(R.id.li_vote_all);
        li_vote_all.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {

            }
        });
    }


    public void sendMessage(String voteKey, VoteType voteType, int voteValue) {
        if(null==msgInfo){
            return;
        }
        voteState =VoteState.VoteStateSendMessage;
        Sender.voteReq(msgInfo.voteKey, msgInfo.voteType, voteValue);

        for (int i = 0; i < style1Bts.size(); i++) {
            style1Bts.get(i).setEnabled(false);
        }
        for (int i = 0; i < style2Bts.size(); i++) {
            style2Bts.get(i).setEnabled(false);
        }

        if(DEVICE_TYPE == DeviceUtil.DEVICE_TYPE_PAD){
            tv_enter.setVisibility(View.INVISIBLE);
        }

    }

    //投票成功，过一小会儿自动隐藏
    public void sendMessageScuess(boolean success){
        if(success) {
            voteState=VoteState.VoteStateSendMesssageScuess;
            handler.sendEmptyMessageDelayed(TaskType.MESSAGE_VOTE_HIDE, HIDE_DELAY_TIME);
            view_top.setVisibility(View.VISIBLE);
            view_top.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (voteState==VoteState.VoteStateSendMesssageScuess) {
                        hideView();
                    }

                }
            });
        }
    }
    public void voteStatistic(VoteStatisticMsgInfo msgInfo) {
        if(this.getVisibility()!=View.VISIBLE){
            return;
        }
        if(voteState!=VoteState.VoteStateSendMesssageScuess){
            return;
        }
        //todo

        tv_desc.setVisibility(View.GONE);
        if(DEVICE_TYPE == DeviceUtil.DEVICE_TYPE_PHONE){
            tv_count_num_hint.setText(StringUtils.getHtmlStringByKey(activity,R.string.vote_countenmame_hint,msgInfo.sumInfo));
        }else{
            tv_count_num_hint.setText(StringUtils.getHtmlStringByKey(activity,R.string.vote_pad_countenmame_hint,msgInfo.sumInfo));
        }

        tv_count_num_hint.setVisibility(View.VISIBLE);
        List<Integer> percentList= msgInfo.percentList;
        switch (msgInfo.voteType) {
            case VoteType1:
            case VoteType2:

                 for(int i=0;i<style1Descs.size();i++){
                     TextView tv=style1Descs.get(i);
                     tv.setText(percentList.get(i)+"%");
                     tv.setVisibility(View.VISIBLE);
                 }

                break;
            case VoteType3:
            case VoteType4:
            case VoteType5:
                for (int i = 0; i < style2Descs.size(); i++) {
                    TextView tv = style2Descs.get(i);
                    tv.setText(percentList.get(i) + "%");
                    tv.setVisibility(View.VISIBLE);
                }
                break;
        }


    }
    public void voteReq(VoteMsgInfo msgInfo) {
        this.msgInfo = msgInfo;
        if (msgInfo.action) {
            // 发起问答
            voteState =VoteState.VoteStateNoraml;
            view_top.setOnClickListener(null);
            handler.removeMessages(TaskType.MESSAGE_VOTE_HIDE);
            padSelectPostion=PADDEAFULTSELECTPOSTION;
            if(DEVICE_TYPE == DeviceUtil.DEVICE_TYPE_PAD){
                tv_enter.setVisibility(View.VISIBLE);
            }

            switch (msgInfo.voteType) {
                case VoteType1: {
                    li_vote_style1.setVisibility(View.VISIBLE);
                    li_vote_style2.setVisibility(View.GONE);
                    for (TextView tv : style1Bts) {
                        if(DEVICE_TYPE == DeviceUtil.DEVICE_TYPE_PHONE){
                            tv.setBackgroundResource(R.drawable.vote_bt_unselect);
                            tv.setTextColor(activity.getResources().getColor(R.color.black));
                        }else{
                            tv.setBackgroundResource(R.drawable.vote_pad_bt_unselect);
                        }

                        tv.setEnabled(true);
                    }
                    tv_vote_style1_bt1.setText("A");
                    tv_vote_style1_bt2.setText("B");
                    tv_vote_style1_bt3.setText("C");
                    tv_vote_style1_bt4.setText("D");

                    for(TextView tv:style1Descs){
                        tv.setVisibility(View.GONE);
                    }

                    break;
                }
                case VoteType2: {
                    li_vote_style1.setVisibility(View.VISIBLE);
                    li_vote_style2.setVisibility(View.GONE);
                    for (TextView tv : style1Bts) {
                        if (DEVICE_TYPE == DeviceUtil.DEVICE_TYPE_PHONE) {
                            tv.setBackgroundResource(R.drawable.vote_bt_unselect);
                            tv.setTextColor(activity.getResources().getColor(R.color.black));
                        } else {
                            tv.setBackgroundResource(R.drawable.vote_pad_bt_unselect);
                        }
                        tv.setEnabled(true);
                    }
                    tv_vote_style1_bt1.setText("1");
                    tv_vote_style1_bt2.setText("2");
                    tv_vote_style1_bt3.setText("3");
                    tv_vote_style1_bt4.setText("4");

                    for(TextView tv:style1Descs){
                        tv.setVisibility(View.GONE);
                    }
                    break;
                }
                case VoteType3: {
                    li_vote_style1.setVisibility(View.GONE);
                    li_vote_style2.setVisibility(View.VISIBLE);
                    for (TextView tv : style2Bts) {
                        if (DEVICE_TYPE == DeviceUtil.DEVICE_TYPE_PHONE) {
                            tv.setBackgroundResource(R.drawable.vote_bt_unselect);
                            tv.setTextColor(activity.getResources().getColor(R.color.black));
                        } else {
                            tv.setBackgroundResource(R.drawable.vote_pad_bt_unselect);
                        }
                        tv.setEnabled(true);
                    }
                    tv_vote_style2_bt1.setText("对");
                    tv_vote_style2_bt2.setText("错");

                    for(TextView tv:style2Descs){
                        tv.setVisibility(View.GONE);
                    }
                    break;
                }
                case VoteType4: {
                    li_vote_style1.setVisibility(View.GONE);
                    li_vote_style2.setVisibility(View.VISIBLE);
                    for (TextView tv : style2Bts) {
                        if (DEVICE_TYPE == DeviceUtil.DEVICE_TYPE_PHONE) {
                            tv.setBackgroundResource(R.drawable.vote_bt_unselect);
                            tv.setTextColor(activity.getResources().getColor(R.color.black));
                        } else {
                            tv.setBackgroundResource(R.drawable.vote_pad_bt_unselect);
                        }
                        tv.setEnabled(true);
                    }
                    tv_vote_style2_bt1.setText("YES");
                    tv_vote_style2_bt2.setText("NO");
                    for(TextView tv:style2Descs){
                        tv.setVisibility(View.GONE);
                    }
                    break;
                }
                case VoteType5: {
                    li_vote_style1.setVisibility(View.GONE);
                    li_vote_style2.setVisibility(View.VISIBLE);
                    for (TextView tv : style2Bts) {
                        if (DEVICE_TYPE == DeviceUtil.DEVICE_TYPE_PHONE) {
                            tv.setBackgroundResource(R.drawable.vote_bt_unselect);
                            tv.setTextColor(activity.getResources().getColor(R.color.black));
                        } else {
                            tv.setBackgroundResource(R.drawable.vote_pad_bt_unselect);
                        }
                        tv.setEnabled(true);
                    }
                    tv_vote_style2_bt1.setText("听明白了");
                    tv_vote_style2_bt2.setText("没听明白");
                    for(TextView tv:style2Descs){
                        tv.setVisibility(View.GONE);
                    }
                    break;
                }
            }
            tv_desc.setVisibility(View.VISIBLE);
            tv_count_num_hint.setVisibility(View.GONE);
            if (this.getVisibility() != View.VISIBLE) {
                this.setVisibility(View.VISIBLE);
                this.startAnimation(face_enter);
                view_top.setVisibility(View.INVISIBLE);
            }
        } else {
            //取消问答
            hideView();
        }
    }

    public void hideView(){
        handler.removeMessages(TaskType.MESSAGE_VOTE_HIDE);
        if (this.getVisibility() == View.VISIBLE) {
            this.setVisibility(View.GONE);
            this.startAnimation(face_exit);
        }
    }

}
