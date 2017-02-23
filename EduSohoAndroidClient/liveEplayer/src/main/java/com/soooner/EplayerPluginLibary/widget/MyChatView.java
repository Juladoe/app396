package com.soooner.EplayerPluginLibary.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.soooner.EplayerPluginLibary.EplayerPluginBaseActivity;
import com.soooner.EplayerPluginLibary.R;
import com.soooner.EplayerPluginLibary.data.AnimationDrawableUtil;
import com.soooner.EplayerPluginLibary.entity.AnimationInfo;
import com.soooner.EplayerPluginLibary.util.LogUtil;
import com.soooner.EplayerPluginLibary.util.StringUtils;
import com.soooner.EplayerPluginLibary.util.TaskType;
import com.soooner.EplayerPluginLibary.util.ToastUtil;
import com.soooner.EplayerPluginLibary.util.VibratorUtil;
import com.soooner.source.common.util.DeviceUtil;
import com.soooner.source.entity.Prainse;
import com.soooner.source.entity.SessionEmun.MessageChatType;
import com.soooner.ws.net.Sender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoxu2014 on 15-1-12.
 */
public class MyChatView extends LinearLayout {
    double BASE_SCREEN_WIDTH_SCALE,BASE_SCREEN_HEIGHT_SCALE;
    public  static final  int PRAINSE_TIME_DELAYE=1000*60*2;
    Context context;
    EplayerPluginBaseActivity activity;
    public static int DEVICE_TYPE = DeviceUtil.DEVICE_TYPE_PHONE;
    Animation shake,face_enter,face_exit;
    InputMethodManager inputMethodManager;
    LinearLayout li_bottom_speak ;
    ImageView img_zhan;
    EditText et_bottom_speak;
    ImageView img_face,img_keyborad;
    LinearLayout li_face_area;
    LinearLayout li_face_tab1,li_face_tab2,li_face_tab3;
    LinearLayout li_tab1,li_tab2;
    List<LinearLayout> linearLayoutList=new ArrayList<LinearLayout>();

    boolean praiseEnable=true;//能否点赞

    LinearLayout  li_speak_li1 ,li_speak_li2;
    ImageView li_speak_li1_img,li_speak_li2_img;
    TextView li_speak_li1_tv,li_speak_li2_tv;


    //表情的
    List<LinearLayout> tab1liList=new ArrayList<LinearLayout>();
    //打赏的
    List<LinearLayout>tab2LiList=new ArrayList<LinearLayout>();
    List<ImageView>tab2ImgList=new ArrayList<ImageView>();
    List<TextView>tab2tvList=new ArrayList<TextView>();

    public MyChatView(Context context) {
        super(context);
    }

    public MyChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyChatView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    enum SpeakState{
        STATE_SPEAK,STATE_QUESTION
    }

    SpeakState speakState=SpeakState.STATE_SPEAK;//当前是发言还是提问的标识

    enum FaceState{
        STATE_FACE,STATE_ANIMATION
    }
    FaceState faceState=FaceState.STATE_FACE;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TaskType.MESSAGE_CHATVIEW_SHOWFACEEREA:{
                    li_face_area.setVisibility(View.VISIBLE);
                    li_face_area.startAnimation(face_enter);
                    break;
                }
                case TaskType.MESSAGE_PRAISE_RESETSTATE:{
                    img_zhan.setAlpha((float)1);
                    praiseEnable=true;
                    break;
                }

            }
            super.handleMessage(msg);
        }
    };

    /*
     处理点赞结果
     */
    public void prainseRes( Prainse prainse){
        if(prainse.isSuccee()){
            //todo 现在的逻辑是用户点赞成功后，2分钟不能再点赞了
            img_zhan.setAlpha((float)0.5);
            praiseEnable=false;
            handler.removeMessages(TaskType.MESSAGE_PRAISE_RESETSTATE);
            handler.sendEmptyMessageDelayed(TaskType.MESSAGE_PRAISE_RESETSTATE,PRAINSE_TIME_DELAYE);
        }else{
            //todo
            LogUtil.e("点赞失败，请检查...");
        }
    }


    public void init(EplayerPluginBaseActivity act, int dt,double BASE_SCREEN_WIDTH_SCALE,double BASE_SCREEN_HEIGHT_SCALE) {
        this.BASE_SCREEN_WIDTH_SCALE=BASE_SCREEN_WIDTH_SCALE;
        this.BASE_SCREEN_HEIGHT_SCALE=BASE_SCREEN_HEIGHT_SCALE;
        init(act,dt);
    }

   //phone无需调节大小，直接调用
    public void init(EplayerPluginBaseActivity activity, int DEVICE_TYPE) {
        this.activity = activity;
        this.context=activity;
        this.DEVICE_TYPE = DEVICE_TYPE;

        shake= AnimationUtils.loadAnimation(activity, R.anim.shake);
        face_enter= AnimationUtils.loadAnimation(activity, R.anim.face_enter);
        face_exit= AnimationUtils.loadAnimation(activity, R.anim.face_exit);
        inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = null;

        switch (DEVICE_TYPE) {
            case DeviceUtil.DEVICE_TYPE_PHONE: {
                view = View.inflate(activity, R.layout.chatcontrol_phone, this);
                break;
            }
            case DeviceUtil.DEVICE_TYPE_PAD: {
                view = View.inflate(activity, R.layout.chatcontrol_pad, this);
                break;
            }
        }
        this.setBackgroundResource(R.color.red);
        initView(view);
    }


    private void initView(View view) {
        li_bottom_speak = (LinearLayout) view.findViewById(R.id.li_bottom_speak);
        img_zhan = (ImageView) view.findViewById(R.id.img_zhan);
        et_bottom_speak = (EditText) view.findViewById(R.id.et_bottom_speak);
        img_face = (ImageView) view.findViewById(R.id.img_face);
        img_keyborad= (ImageView) view.findViewById(R.id.img_keyborad);
        li_face_area = (LinearLayout) view.findViewById(R.id.li_face_area);
        et_bottom_speak.setOnEditorActionListener(onEditorActionListener);
        et_bottom_speak.setOnKeyListener(onKeyListener);


        li_tab1= (LinearLayout) view.findViewById(R.id.li_tab1);
        li_tab2= (LinearLayout) view.findViewById(R.id.li_tab2);
        li_face_tab1 = (LinearLayout) view.findViewById(R.id.li_face_tab1);
        li_face_tab2 = (LinearLayout) view.findViewById(R.id.li_face_tab2);
        li_face_tab3 = (LinearLayout) view.findViewById(R.id.li_face_tab3);
        initData();
        switch (DEVICE_TYPE) {
            case DeviceUtil.DEVICE_TYPE_PHONE: {
                filePhoneData();
                break;
            }
            case DeviceUtil.DEVICE_TYPE_PAD: {
                initViewSize(view);
                filePadData();
                break;
            }
        }

        LinearLayout tab1li=tab1liList.get(tab1liList.size()-1);
        tab1li.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
               deleteBtn();
            }
        });


        img_zhan.setAlpha(1);
        img_zhan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.isChatForbid()){
                    ToastUtil.showStringToast(context, "您已被禁言，暂无法给老师打赞");
                    return;
                }

                if (praiseEnable) {
                    Sender.praiseReq();
                    Sender.chatReq(AnimationDrawableUtil.ANIMATION_A_ZAN, MessageChatType.MessageChatTypeReward.value());
                } else {
                    ToastUtil.showStringToast(context, "您刚刚已经点过赞了，请2分钟后再试");
                }

            }
        });
        et_bottom_speak.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    img_keyborad.setVisibility(View.GONE);
                    img_face.setVisibility(View.VISIBLE);
                    if(li_face_area.getVisibility()==View.VISIBLE){
                        li_face_area.setVisibility(View.GONE);
                        li_face_area.startAnimation(face_exit);
                    }
                }
            }
        });
        img_keyborad.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {

//                if(activity.isPersonChatForbid()||((activity.isAllChatForbid()&&speakState==SpeakState.STATE_SPEAK))){//禁言时，点击无效
//                    return;
//                }
                if(activity.isChatForbid()){//禁言时，点击无效
                    return;
                }


                if(img_face.getVisibility()!=View.VISIBLE){


                    et_bottom_speak.requestFocus();
                    inputMethodManager.showSoftInput(et_bottom_speak, InputMethodManager.SHOW_FORCED);

                    img_keyborad.setVisibility(View.GONE);
                    img_face.setVisibility(View.VISIBLE);

                    li_face_area.setVisibility(View.GONE);
                    li_face_area.startAnimation(face_exit);
                }

            }
        });
        img_face.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View view) {
//                if(activity.isPersonChatForbid()||((activity.isAllChatForbid()&&speakState==SpeakState.STATE_SPEAK))){//禁言时，点击无效
//                    return;
//                }
                if(activity.isChatForbid()){//禁言时，点击无效
                    return;
                }

                inputMethodManager.hideSoftInputFromWindow(et_bottom_speak.getApplicationWindowToken(), 0);

                img_keyborad.setVisibility(View.VISIBLE);
                li_bottom_speak.requestFocus();
                img_face.setVisibility(View.GONE);
                if(li_face_area.getVisibility()==View.VISIBLE){
                    return;
                } else {

                    handler.sendEmptyMessageDelayed(TaskType.MESSAGE_CHATVIEW_SHOWFACEEREA,100);



                }


            }
        });



        linearLayoutList.add(li_face_tab1);
        linearLayoutList.add(li_face_tab2);
        li_face_tab1.setOnClickListener(liListener);
        li_face_tab2.setOnClickListener(liListener);
        li_face_tab3.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View view) {
              sendMessage();
            }
        });
        initSelectTabBg(li_face_tab1.getId());
    }

    /*
     表情里的删除和输入法里的删除
     */

    private void deleteBtn() {
        //todo
        String content = StringUtils.getEditTextText(et_bottom_speak);
        if (!StringUtils.isValid(content)) {
            return;
        }

        for(int i=0;i<faceList.size();i++){
            String face="["+faceList.get(i)+"]";
            if(content.endsWith(face)){
                et_bottom_speak.setText(content.substring(0,content.length()-face.length()));
                return;
            }
        }
        et_bottom_speak.setText(content.substring(0,content.length()-1));


    }


    OnClickListener liListener=new OnClickListener(){
        @Override
        public void onClick(View view) {
            initSelectTabBg(view.getId());
        }
    };




    public void initSelectTabBg(int liid) {
        for(int i=0;i<linearLayoutList.size();i++){
            LinearLayout litemp=linearLayoutList.get(i);
            if(litemp.getId()==liid){
                litemp.setBackgroundResource(R.color.eplayer_activity_bg);
            }else{
                litemp.setBackgroundResource(R.color.eplayer_face_unselect);
            }
        }
        if(li_face_tab1.getId()==liid){
            //todo
            li_tab1.setVisibility(View.VISIBLE);
            li_tab2.setVisibility(View.GONE);
        }else if(li_face_tab2.getId()==liid){
            //todo
            li_tab2.setVisibility(View.VISIBLE);
            li_tab1.setVisibility(View.GONE);
        }
    }
    List<String>faceList=new ArrayList<String>();
    List<AnimationInfo>animationList=new ArrayList<AnimationInfo>();
    public void initData(){

        faceList.add("e_ciya");
        faceList.add("e_jiyan");
        faceList.add("e_buding");
        faceList.add("e_shangxin");
        faceList.add("e_daxiao");
        faceList.add("e_jingya");
        faceList.add("e_qinqin");
        faceList.add("e_weixiao");
        faceList.add("e_tushe");
        faceList.add("e_tianshi");
        faceList.add("e_shuaku");
        faceList.add("e_daidai");
        faceList.add("e_daku");
        faceList.add("e_moshu");
        faceList.add("e_yaoguai");
        faceList.add("e_xingxing");


        AnimationInfo info=new AnimationInfo("guzhang","鼓掌");
        animationList.add(info);

        AnimationInfo info1=new AnimationInfo("xianhua","送花");
        animationList.add(info1);

        AnimationInfo info2=new AnimationInfo("xinkule","辛苦了");
        animationList.add(info2);

        AnimationInfo info3=new AnimationInfo("hongxin","红心");
        animationList.add(info3);

        AnimationInfo info4=new AnimationInfo("ganxie","感谢老师");
        animationList.add(info4);

        AnimationInfo info5=new AnimationInfo("jiayou","加油");
        animationList.add(info5);

        AnimationInfo info6=new AnimationInfo("runhoutang","润喉糖");
        animationList.add(info6);

        AnimationInfo info7=new AnimationInfo("laoshihao","老师好");
        animationList.add(info7);
    }
   public void filePhoneData(){

       tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row1_col1));
       tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row1_col2));
       tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row1_col3));
       tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row1_col4));
       tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row1_col5));
       tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row1_col6));
       tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row2_col1));
       tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row2_col2));
       tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row2_col3));
       tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row2_col4));
       tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row2_col5));
       tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row2_col6));
       tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row3_col1));
       tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row3_col2));
       tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row3_col3));
       tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row3_col4));
       tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row3_col6));

       for(int i=0;i<tab1liList.size()-1;i++){
           LogUtil.d("tab1liList.size():"+i);
           LinearLayout tab1li=tab1liList.get(i);
          final String contentDescription=tab1li.getContentDescription().toString();
           tab1li.setOnClickListener(new OnClickListener(){
               @Override
               public void onClick(View v) {
                   et_bottom_speak.append("["+contentDescription+"]");
               }
           });
       }



       tab2ImgList.add((ImageView) li_tab2.findViewById(R.id.img_animation_row1_col1));
       tab2ImgList.add((ImageView) li_tab2.findViewById(R.id.img_animation_row1_col2));
       tab2ImgList.add((ImageView) li_tab2.findViewById(R.id.img_animation_row1_col3));
       tab2ImgList.add((ImageView) li_tab2.findViewById(R.id.img_animation_row1_col4));
       tab2ImgList.add((ImageView) li_tab2.findViewById(R.id.img_animation_row2_col1));
       tab2ImgList.add((ImageView) li_tab2.findViewById(R.id.img_animation_row2_col2));
       tab2ImgList.add((ImageView) li_tab2.findViewById(R.id.img_animation_row2_col3));
       tab2ImgList.add((ImageView) li_tab2.findViewById(R.id.img_animation_row2_col4));

       tab2tvList.add((TextView) li_tab2.findViewById(R.id.tv_tab_item_row1_col1));
       tab2tvList.add((TextView) li_tab2.findViewById(R.id.tv_tab_item_row1_col2));
       tab2tvList.add((TextView) li_tab2.findViewById(R.id.tv_tab_item_row1_col3));
       tab2tvList.add((TextView) li_tab2.findViewById(R.id.tv_tab_item_row1_col4));
       tab2tvList.add((TextView) li_tab2.findViewById(R.id.tv_tab_item_row2_col1));
       tab2tvList.add((TextView) li_tab2.findViewById(R.id.tv_tab_item_row2_col2));
       tab2tvList.add((TextView) li_tab2.findViewById(R.id.tv_tab_item_row2_col3));
       tab2tvList.add((TextView) li_tab2.findViewById(R.id.tv_tab_item_row2_col4));

       tab2LiList.add((LinearLayout) li_tab2.findViewById(R.id.li_row1_col1));
       tab2LiList.add((LinearLayout) li_tab2.findViewById(R.id.li_row1_col2));
       tab2LiList.add((LinearLayout) li_tab2.findViewById(R.id.li_row1_col3));
       tab2LiList.add((LinearLayout) li_tab2.findViewById(R.id.li_row1_col4));
       tab2LiList.add((LinearLayout) li_tab2.findViewById(R.id.li_row2_col1));
       tab2LiList.add((LinearLayout) li_tab2.findViewById(R.id.li_row2_col2));
       tab2LiList.add((LinearLayout) li_tab2.findViewById(R.id.li_row2_col3));
       tab2LiList.add((LinearLayout) li_tab2.findViewById(R.id.li_row2_col4));


       for(int i=0;i<animationList.size();i++){
           AnimationInfo temp_info= animationList.get(i);
           AnimationDrawable animationDrawable = AnimationDrawableUtil.getAnimationDrawable(context, temp_info.getDrawableName());
           tab2tvList.get(i).setText(temp_info.getName());
           if (null != animationDrawable) {
               tab2ImgList.get(i).setImageDrawable(animationDrawable);
               animationDrawable.start();

           }
       }
       for(int i=0;i<tab2LiList.size();i++){
           LinearLayout litemp=tab2LiList.get(i);
           final AnimationInfo animationInfo=animationList.get(i);
           litemp.setOnClickListener(new OnClickListener(){

               @Override
               public void onClick(View v) {

                  Sender.chatReq(animationInfo.getDrawableName(), MessageChatType.MessageChatTypeReward.value());
               }
           });
       }
   }

    public void filePadData(){

        tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row1_col1));
        tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row1_col2));
        tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row1_col3));
        tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row1_col4));
        tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row1_col5));
        tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row1_col6));
        tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row1_col7));
        tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row1_col8));
        tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row1_col9));
        tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row1_col10));
        tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row2_col1));
        tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row2_col2));
        tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row2_col3));
        tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row2_col4));
        tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row2_col5));
        tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row2_col6));
        tab1liList.add((LinearLayout) li_tab1.findViewById(R.id.li_tab1_row2_col10));


        for(int i=0;i<tab1liList.size()-1;i++){
            LogUtil.d("tab1liList.size():"+i);
            LinearLayout tab1li=tab1liList.get(i);

            final String contentDescription=tab1li.getContentDescription().toString();
            tab1li.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    et_bottom_speak.append("["+contentDescription+"]");
                }
            });
        }




        tab2ImgList.add((ImageView) li_tab2.findViewById(R.id.img_animation_row1_col1));
        tab2ImgList.add((ImageView) li_tab2.findViewById(R.id.img_animation_row1_col2));
        tab2ImgList.add((ImageView) li_tab2.findViewById(R.id.img_animation_row1_col3));
        tab2ImgList.add((ImageView) li_tab2.findViewById(R.id.img_animation_row1_col4));
        tab2ImgList.add((ImageView) li_tab2.findViewById(R.id.img_animation_row1_col5));
        tab2ImgList.add((ImageView) li_tab2.findViewById(R.id.img_animation_row1_col6));
        tab2ImgList.add((ImageView) li_tab2.findViewById(R.id.img_animation_row1_col7));
        tab2ImgList.add((ImageView) li_tab2.findViewById(R.id.img_animation_row1_col8));

        tab2tvList.add((TextView) li_tab2.findViewById(R.id.tv_tab_item_row1_col1));
        tab2tvList.add((TextView) li_tab2.findViewById(R.id.tv_tab_item_row1_col2));
        tab2tvList.add((TextView) li_tab2.findViewById(R.id.tv_tab_item_row1_col3));
        tab2tvList.add((TextView) li_tab2.findViewById(R.id.tv_tab_item_row1_col4));
        tab2tvList.add((TextView) li_tab2.findViewById(R.id.tv_tab_item_row1_col5));
        tab2tvList.add((TextView) li_tab2.findViewById(R.id.tv_tab_item_row1_col6));
        tab2tvList.add((TextView) li_tab2.findViewById(R.id.tv_tab_item_row1_col7));
        tab2tvList.add((TextView) li_tab2.findViewById(R.id.tv_tab_item_row1_col8));

        tab2LiList.add((LinearLayout) li_tab2.findViewById(R.id.li_row1_col1));
        tab2LiList.add((LinearLayout) li_tab2.findViewById(R.id.li_row1_col2));
        tab2LiList.add((LinearLayout) li_tab2.findViewById(R.id.li_row1_col3));
        tab2LiList.add((LinearLayout) li_tab2.findViewById(R.id.li_row1_col4));
        tab2LiList.add((LinearLayout) li_tab2.findViewById(R.id.li_row1_col5));
        tab2LiList.add((LinearLayout) li_tab2.findViewById(R.id.li_row1_col6));
        tab2LiList.add((LinearLayout) li_tab2.findViewById(R.id.li_row1_col7));
        tab2LiList.add((LinearLayout) li_tab2.findViewById(R.id.li_row1_col8));


        for(int i=0;i<animationList.size();i++){
            AnimationInfo temp_info= animationList.get(i);
            AnimationDrawable animationDrawable = AnimationDrawableUtil.getAnimationDrawable(context,  temp_info.getDrawableName() );
            tab2tvList.get(i).setText(temp_info.getName());
            if (null != animationDrawable) {
                tab2ImgList.get(i).setImageDrawable(animationDrawable);
                animationDrawable.start();

            }
        }
        for(int i=0;i<tab2LiList.size();i++){
            LinearLayout litemp=tab2LiList.get(i);
            final AnimationInfo animationInfo=animationList.get(i);
            litemp.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View v) {

                    Sender.chatReq(animationInfo.getDrawableName(), MessageChatType.MessageChatTypeReward.value());
                }
            });
        }
    }

    OnKeyListener onKeyListener=new OnKeyListener(){

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(keyCode == KeyEvent.KEYCODE_DEL) {
                deleteBtn();
               return true;
            }
                return false;
        }
    };
    TextView.OnEditorActionListener onEditorActionListener=new TextView.OnEditorActionListener(){
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if(actionId == EditorInfo.IME_ACTION_SEND){
                LogUtil.d("onEditorAction", "EditorInfo.IME_ACTION_SEND");
                sendMessage();

                return true;
            }
            return false;
        }
    };

    //发送聊天信息
    public void sendMessage(){
        if(activity.isPersonChatForbid()){
            //被禁言时聊天，提问不受影响
            ToastUtil.showStringToast(context, "您已经被禁言，暂时无法发送聊天信息");

       // }else if(activity.isAllChatForbid()&&(speakState==SpeakState.STATE_SPEAK)){
        }else if(activity.isAllChatForbid()){
            ToastUtil.showStringToast(context, "老师发起了全体禁言，您暂时无法发送聊天信息");
        }else{
            String edit_content= StringUtils.getEditTextText(et_bottom_speak);
            if(StringUtils.isValid(edit_content)){
                if(speakState==SpeakState.STATE_SPEAK){
                    Sender.chatReq(edit_content, MessageChatType.MessageChatTypeMsg.value());
                }else{
                    Sender.chatReq(edit_content,MessageChatType.MessageChatTypeAsk.value());
                }
                et_bottom_speak.setText("");




                if(inputMethodManager.isActive()){
                    inputMethodManager.hideSoftInputFromWindow(et_bottom_speak.getApplicationWindowToken(), 0);

                }
                li_face_area.setVisibility(View.GONE);

            }else{
                ToastUtil.showStringToast(context,"内容不能为空");
                et_bottom_speak.startAnimation(shake);
                VibratorUtil.Vibrate(context, 1000);
            }
        }
    }

    public String changeSpeakState(){
        String hintStr="";
        initSelectTabBg(R.id.li_face_tab1);
        switch (speakState){
            case STATE_SPEAK:{
                speakState=SpeakState.STATE_QUESTION;
                li_face_tab2.setVisibility(View.INVISIBLE);
                hintStr="讨论";
                break;
            }
            case STATE_QUESTION:{
                speakState=SpeakState.STATE_SPEAK;
                li_face_tab2.setVisibility(View.VISIBLE);
                hintStr="提问";
                break;
            }
        }
        initSpeakEditText(true);
        return  hintStr;

    }

    public void initChatForbid(boolean clearContent){

      //  if (activity.isPersonChatForbid() || ((activity.isAllChatForbid() && speakState == SpeakState.STATE_SPEAK))) {
        if(activity.isChatForbid()){
            inputMethodManager.hideSoftInputFromWindow(et_bottom_speak.getApplicationWindowToken(), 0);
            if (li_face_area.getVisibility() == View.VISIBLE) {
                li_face_area.setVisibility(View.GONE);
                li_face_area.startAnimation(face_exit);
            }

        }
        initSpeakEditText(clearContent);
    }

    private void initViewSize(View view) {
        li_speak_li1 = (LinearLayout) view.findViewById(R.id.li_speak_li1);
        li_speak_li2 = (LinearLayout) view.findViewById(R.id.li_speak_li2);
        li_speak_li1_img = (ImageView) view.findViewById(R.id.li_speak_li1_img);
        li_speak_li2_img = (ImageView) view.findViewById(R.id.li_speak_li2_img);
        li_speak_li1_tv= (TextView) view.findViewById(R.id.li_speak_li1_tv);
        li_speak_li2_tv= (TextView) view.findViewById(R.id.li_speak_li2_tv);

        li_speak_li1.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View view) {
                initSpeakEditText(SpeakState.STATE_QUESTION,true);
            }
        });
        li_speak_li2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                initSpeakEditText(SpeakState.STATE_SPEAK, true);
            }
        });
        initSpeakEditText(SpeakState.STATE_SPEAK, true);

        Resources res=context.getResources();
        int pad_li_bottom_speak_height=(int)res.getDimension(R.dimen.pad_li_bottom_speak_height);
        LayoutParams li_bottom_speakLayoutParams= (LayoutParams) li_bottom_speak.getLayoutParams();
        li_bottom_speakLayoutParams.height=(int)(pad_li_bottom_speak_height*BASE_SCREEN_HEIGHT_SCALE);

        int pad_li_speak_li_width=(int)res.getDimension(R.dimen.pad_li_speak_li_width);
        LayoutParams li_speak_li1LayoutParams= (LayoutParams) li_speak_li1.getLayoutParams();
        li_speak_li1LayoutParams.width=(int)(pad_li_speak_li_width*BASE_SCREEN_WIDTH_SCALE);
        LayoutParams li_speak_li2LayoutParams= (LayoutParams) li_speak_li2.getLayoutParams();
        li_speak_li2LayoutParams.width=(int)(pad_li_speak_li_width*BASE_SCREEN_WIDTH_SCALE);


        int pad_li_speak_li_img_width= (int) res.getDimension(R.dimen.pad_li_speak_li_img_width);
        int pad_li_speak_li_img_height=(int) res.getDimension(R.dimen.pad_li_speak_li_img_height);
        LayoutParams li_speak_li1_imgLayoutParams = (LayoutParams) li_speak_li1_img.getLayoutParams();
        li_speak_li1_imgLayoutParams.width=(int)(pad_li_speak_li_img_width*BASE_SCREEN_WIDTH_SCALE);
        li_speak_li1_imgLayoutParams.height=(int)(pad_li_speak_li_img_height*BASE_SCREEN_WIDTH_SCALE);

        LayoutParams li_speak_li2_imgLayoutParams = (LayoutParams) li_speak_li2_img.getLayoutParams();
        li_speak_li2_imgLayoutParams.width=(int)(pad_li_speak_li_img_width*BASE_SCREEN_WIDTH_SCALE);
        li_speak_li2_imgLayoutParams.height=(int)(pad_li_speak_li_img_height*BASE_SCREEN_WIDTH_SCALE);

        int bt_bottom_speak_height= (int) res.getDimension(R.dimen.pad_et_bottom_speak_height);


        LayoutParams et_bottom_speakLayoutParams= (LayoutParams) et_bottom_speak.getLayoutParams();
        et_bottom_speakLayoutParams.height=(int)(bt_bottom_speak_height*BASE_SCREEN_HEIGHT_SCALE);


        int chatview_zhan_width= (int) res.getDimension(R.dimen.chatview_zhan_width);
        int chatview_zhan_height= (int) res.getDimension(R.dimen.chatview_zhan_height);
        ViewGroup.LayoutParams lp= img_zhan.getLayoutParams();
        lp.width= (int) (chatview_zhan_width*BASE_SCREEN_WIDTH_SCALE);
        lp.height=(int) (chatview_zhan_height*BASE_SCREEN_WIDTH_SCALE);


        int chatview_face_width_height= (int) res.getDimension(R.dimen.chatview_face_width_height);
        ViewGroup.LayoutParams img_faceLayoutParams=img_face.getLayoutParams();
        ViewGroup.LayoutParams img_keyboradLayoutParams=img_keyborad.getLayoutParams();
        int width_height= (int) (chatview_face_width_height*BASE_SCREEN_WIDTH_SCALE);
        img_faceLayoutParams.width=width_height;
        img_faceLayoutParams.height=width_height;
        img_keyboradLayoutParams.width=width_height;
        img_keyboradLayoutParams.height=width_height;

    }

   public void hideChat(){
        //et_bottom_speak.setText("");
        li_bottom_speak.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_bottom_speak.getWindowToken(), 0);
        this.setVisibility(View.GONE);

    }
    public void outFocuse()
    {
        li_bottom_speak.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_bottom_speak.getWindowToken(), 0);
        li_face_area.setVisibility(View.GONE);

    }

    private  void initSpeakEditText(SpeakState state,boolean clearContent){
        speakState=state;
        initSelectTabBg(R.id.li_face_tab1);
        switch (speakState){
            case STATE_SPEAK:{
                li_face_tab2.setVisibility(View.VISIBLE);

                li_speak_li1_img.setBackgroundResource(R.drawable.pad_speak1);
                li_speak_li2_img.setBackgroundResource(R.drawable.pad_discuss);
                li_speak_li1_tv.setTextColor(context.getResources().getColor(R.color.white));
                li_speak_li2_tv.setTextColor(context.getResources().getColor(R.color.eplayer_pad_title_bg));

                break;
            }
            case  STATE_QUESTION:{

                li_face_tab2.setVisibility(View.INVISIBLE);

                li_speak_li1_img.setBackgroundResource(R.drawable.pad_speak);
                li_speak_li2_img.setBackgroundResource(R.drawable.pad_discuss1);
                li_speak_li1_tv.setTextColor(context.getResources().getColor(R.color.eplayer_pad_title_bg2));
                li_speak_li2_tv.setTextColor(context.getResources().getColor(R.color.white));
                break;
            }
        }

        initSpeakEditText(clearContent);
    }


    public  void initSpeakEditText(boolean clearContent){
        switch (speakState){
            case STATE_SPEAK:{
                if(activity.isChatForbid()){
                    et_bottom_speak.setEnabled(false);

                }else{
                    et_bottom_speak.setEnabled(true);
                }
                if(clearContent){
                    et_bottom_speak.setText("");
                }

                et_bottom_speak.setHint(R.string.edit_hint_speak);
                break;
            }
            case  STATE_QUESTION:{
                if(activity.isChatForbid()){
                    et_bottom_speak.setEnabled(false);
                }else{
                    et_bottom_speak.setEnabled(true);
                }

                if(clearContent){
                    et_bottom_speak.setText("");
                }
                et_bottom_speak.setHint(R.string.edit_hint_question);
                break;
            }
        }

    }


}
