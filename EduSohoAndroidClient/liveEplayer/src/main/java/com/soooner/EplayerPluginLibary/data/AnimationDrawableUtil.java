package com.soooner.EplayerPluginLibary.data;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.soooner.EplayerPluginLibary.R;
import com.soooner.EplayerPluginLibary.util.ResourceUtil;
import com.soooner.EplayerPluginLibary.widget.VerticalImageSpan;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhaoxu2014 on 15-1-8.
 */
public class AnimationDrawableUtil {
    public static   final String ANIMATION_A_GANXIE="ganxie";
    public static   final String ANIMATION_A_GUZHANG="guzhang";
    public static   final String ANIMATION_A_HONGXIN="hongxin";
    public static   final String ANIMATION_A_JIAYOU="jiayou";
    public static   final String ANIMATION_A_LAOSHIHAO="laoshihao";
    public static   final String ANIMATION_A_RUNHOUTANG="runhoutang";
    public static   final String ANIMATION_A_XIANHUA="xianhua";
    public static   final String ANIMATION_A_XINKULE="xinkule";
    public static   final String ANIMATION_A_ZAN="zan";



    /**
     * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
     */
    private static void dealExpression(Context context,
                                SpannableString spannableString, Pattern patten, int start)
            throws Exception {
        int chat_size_with_hight= (int) context.getResources().getDimension(R.dimen.chat_size_with_hight);
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            if (matcher.start() < start) {
                continue;
            }
            String findresult=matcher.group();
            int result_start=matcher.start();
            int result_end=matcher.end();
            int drawableid= ResourceUtil.getResId(context, findresult.substring(1,findresult.length()-1), "drawable");
            if(drawableid>0){

                Drawable drawable = context.getResources().getDrawable(drawableid);
                drawable.setBounds(0, 0, chat_size_with_hight, chat_size_with_hight);//这里设置图片的大小
                ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
               // VerticalImageSpan imageSpan = new VerticalImageSpan(context, drawableid);

                spannableString.setSpan(imageSpan,result_start, result_end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

            }

            if (result_end < spannableString.length()) {
                dealExpression(context, spannableString, patten, result_end);
            }
        }
    }



    /**
     * 判断传入str里是否有图片，有图片就表情图片代
     *
     */
    public static SpannableString getExpressionString(Context context, String str) {


        String zhengze="\\[e_[a-z]+\\]";
        SpannableString spannableString = new SpannableString(str);
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
        try {
            dealExpression(context, spannableString, sinaPatten, 0);
        } catch (Exception e) {
        }
        return spannableString;
    }




    public static AnimationDrawable getAnimationDrawable(Context context,String drawableName){


        if (drawableName.equals(ANIMATION_A_GANXIE)) {
            return get_Ganxie(context);
        }else if (drawableName.equals(ANIMATION_A_GUZHANG)) {
            return get_Guzhang(context);
        }else if (drawableName.equals(ANIMATION_A_HONGXIN)) {
            return get_Hongxin(context);
        }else if (drawableName.equals(ANIMATION_A_JIAYOU)) {
            return get_Jiayou(context);
        }else if (drawableName.equals(ANIMATION_A_LAOSHIHAO)) {
            return get_Laoshihao(context);
        }else if (drawableName.equals(ANIMATION_A_RUNHOUTANG)) {
            return get_Runhoutang(context);
        }else if (drawableName.equals(ANIMATION_A_XIANHUA)) {
            return get_Xianhua(context);
        }else if (drawableName.equals(ANIMATION_A_XINKULE)) {
            return get_Xinkule(context);
        }else if (drawableName.equals(ANIMATION_A_ZAN)) {
            return get_Zan(context);
        }

        return null;
    }

    public static AnimationDrawable get_Zan(Context context){
        Resources res=context.getResources();
        Drawable drawable=res.getDrawable(R.drawable.bq_zhan);
        AnimationDrawable  animationDrawable = new AnimationDrawable();
        animationDrawable.setOneShot(true);
        animationDrawable.addFrame(drawable, -1);

        return animationDrawable;
    }

    public static AnimationDrawable get_Ganxie(Context context){
        Resources res=context.getResources();
        AnimationDrawable  animationDrawable = new AnimationDrawable();
        animationDrawable.setOneShot(false);

        int count=4;
        int duration=getDuration(count);
        for(int i =1;i<=count;i++){
            int id =getid(context,"a_ganxie_",i);
            animationDrawable.addFrame(res.getDrawable(id), duration);
        }
        return animationDrawable;
    }
    public static AnimationDrawable get_Guzhang(Context context){
        Resources res=context.getResources();
        AnimationDrawable  animationDrawable = new AnimationDrawable();
        animationDrawable.setOneShot(false);

        int count=7;
        int duration=getDuration(count);
        for(int i =1;i<=count;i++){
            int id =getid(context,"a_guzhang_",i);
            animationDrawable.addFrame(res.getDrawable(id), duration);
        }
        return animationDrawable;
    }

    public static AnimationDrawable get_Hongxin(Context context){
        Resources res=context.getResources();
        AnimationDrawable  animationDrawable = new AnimationDrawable();
        animationDrawable.setOneShot(false);

        int count=9;
        int duration=getDuration(count);
        for(int i =1;i<=count;i++){
            int id =getid(context,"a_hongxin_",i);
            animationDrawable.addFrame(res.getDrawable(id), duration);
        }
        return animationDrawable;
    }
    public static AnimationDrawable get_Jiayou(Context context){
        Resources res=context.getResources();
        AnimationDrawable  animationDrawable = new AnimationDrawable();
        animationDrawable.setOneShot(false);

        int count=2;
        int duration=getDuration(count);
        for(int i =1;i<=count;i++){
            int id =getid(context,"a_jiayou_",i);
            animationDrawable.addFrame(res.getDrawable(id), duration);
        }
        return animationDrawable;
    }
    public static AnimationDrawable get_Laoshihao(Context context){
        Resources res=context.getResources();
        AnimationDrawable  animationDrawable = new AnimationDrawable();
        animationDrawable.setOneShot(false);

        int count=10;
        int duration=getDuration(count);
        for(int i =1;i<=count;i++){
            int id =getid(context,"a_laoshihao_",i);
            animationDrawable.addFrame(res.getDrawable(id), duration);
        }
        return animationDrawable;
    }

    public static AnimationDrawable get_Runhoutang(Context context){
        Resources res=context.getResources();
        AnimationDrawable  animationDrawable = new AnimationDrawable();
        animationDrawable.setOneShot(false);

        int count=10;
        int duration=getDuration(count);
        for(int i =1;i<=count;i++){
            int id =getid(context,"a_runhoutang_",i);
            animationDrawable.addFrame(res.getDrawable(id), duration);
        }
        return animationDrawable;
    }


    public static AnimationDrawable get_Xianhua(Context context){
        Resources res=context.getResources();
        AnimationDrawable  animationDrawable = new AnimationDrawable();
        animationDrawable.setOneShot(false);


        int count=9;
        int duration=getDuration(count);
        for(int i =1;i<=count;i++){
            int id =getid(context,"a_xianhua_",i);
            animationDrawable.addFrame(res.getDrawable(id), duration);
        }
        return animationDrawable;
    }
    public static AnimationDrawable get_Xinkule(Context context){
        Resources res=context.getResources();
        AnimationDrawable  animationDrawable = new AnimationDrawable();
        animationDrawable.setOneShot(false);

        int count=10;
        int duration=getDuration(count,1000);
        for(int i =1;i<=count;i++){
            int id =getid(context, "a_xinkule_", i);
            animationDrawable.addFrame(res.getDrawable(id), duration);
        }
        return animationDrawable;
    }

    public static int getid(Context context,String prefixName,int i){
        String content="";
        if(i>=10){
            content=prefixName+"100"+i;
        }else{
            content=prefixName+"1000"+i;
        }
        int id =context.getResources().getIdentifier(content, "drawable", context.getPackageName());
        return id;
    }

    public static int getDuration(int count){
       int ALL_DURATION=500;
        int duration=ALL_DURATION%count==0?ALL_DURATION/count:ALL_DURATION/count+1;
        return duration;
    }
    public static int getDuration(int count,int doution_time){
        int ALL_DURATION=doution_time;
        int duration=ALL_DURATION%count==0?ALL_DURATION/count:ALL_DURATION/count+1;
        return duration;
    }
}
