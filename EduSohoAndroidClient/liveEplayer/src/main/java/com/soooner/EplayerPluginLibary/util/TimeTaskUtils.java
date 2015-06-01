package com.soooner.EplayerPluginLibary.util;


import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaoxu2014 on 15/5/28.
 */
public class TimeTaskUtils {
    public static  final  int TASK_LOGIN=1000;
    public static  final  int TASK_LOAD=1001;
    public static  final  int TASK_ERROR=1002;

    public enum  TimeTaskType{
        TIMETASK_LOGIN  ,TIMETASK_LOAD,TIMETASK_ERROR;
    }

    TimeTaskListener listener;
    Map<Integer,TaskBean>map=new HashMap<Integer,TaskBean>();

    public TimeTaskUtils(TimeTaskListener listener) {
        this.listener = listener;
        map=new HashMap<Integer,TaskBean>();
    }

    public void setListener(TimeTaskListener listener) {
        this.listener = listener;
    }

    Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            TaskBean tkbean= (TaskBean) msg.obj;
            if(null==tkbean){
                return;
            }
            tkbean.retryCount= tkbean.retryCount-1;

            switch (msg.what){
                case TASK_LOGIN:{
                    if(null!=listener){
                        if(tkbean.retryCount<=0){
                            listener.handlerTimeTaskLooping(TASK_LOGIN, true);
                        }else{
                            listener.handlerTimeTaskLooping(TASK_LOGIN,false);
                            Message message=Message.obtain();
                            message.what=TASK_LOGIN;
                            message.obj=tkbean;
                            handler.sendMessageDelayed(message,tkbean.millisecond);
                        }

                    }
                    break;
                }
                case TASK_LOAD:{
                    if(null!=listener){

                        if(tkbean.retryCount<=0){
                            listener.handlerTimeTaskLooping(TASK_LOAD, true);
                        }else{
                            listener.handlerTimeTaskLooping(TASK_LOAD,false);
                            Message message=Message.obtain();
                            message.what=TASK_LOAD;
                            message.obj=tkbean;
                            handler.sendMessageDelayed(message,tkbean.millisecond);
                        }
                    }
                    break;
                }

                case TASK_ERROR:{
                    if(null!=listener){


                        if(tkbean.retryCount<=0){
                          //  LogUtil.d("streamEOF","tkbean.retryCount:"+tkbean.retryCount);
                            listener.handlerTimeTaskLooping(TASK_ERROR,true);
                        }else{
                          //  LogUtil.d("streamEOF","tkbean.retryCount:"+tkbean.retryCount);
                            listener.handlerTimeTaskLooping(TASK_ERROR,false);

                            Message message=Message.obtain();
                            message.what=TASK_ERROR;
                            message.obj=tkbean;
                            handler.sendMessageDelayed(message,tkbean.millisecond);
                        }
                    }
                    break;
                }

            }
            super.handleMessage(msg);
        }
    };





    public void addErrorTask(TimeTaskType type, int retryCount, long millisecond, boolean cancelAll, boolean single){
        if(!single) {
            addTask(type, retryCount, millisecond, cancelAll);
         //   LogUtil.d("streamEOF","!single"+single);
        }else{
         //  LogUtil.d("streamEOF","single"+single);
            if(type==TimeTaskType.TIMETASK_ERROR){
                if(null==map.get(TASK_ERROR)){
                    addTask(type, retryCount, millisecond, cancelAll);
                    LogUtil.d("streamEOF","add");

                }
            }

        }
    }

    public void addTask(TimeTaskType type,int retryCount,long millisecond, boolean cancelAll){
       if(cancelAll){
           clearAllTask();
       }
        Message message=Message.obtain();
        TaskBean tkbean=new TaskBean();
        tkbean.millisecond=millisecond;
        tkbean.retryCount=retryCount;
        message.obj=tkbean;
        switch (type){
            case  TIMETASK_LOGIN :
            {
                handler.removeMessages(TASK_LOGIN);
                message.what=TASK_LOGIN;

                break;
            }
            case TIMETASK_LOAD:{
                handler.removeMessages(TASK_LOAD);
                message.what=TASK_LOAD;
                break;
            }
            case TIMETASK_ERROR:{
                message.what=TASK_ERROR;
                break;
            }

        }
        map.put(message.what,tkbean);
        handler.sendMessageDelayed(message,millisecond);
    }

    public void clearTask(TimeTaskType type){
        switch (type){
            case  TIMETASK_LOGIN :
            {
                handler.removeMessages(TASK_LOGIN);
                map.remove(TASK_LOGIN);

                break;
            }
            case TIMETASK_LOAD:{
                handler.removeMessages(TASK_LOAD);
                map.remove(TASK_LOAD);
                break;
            }
            case TIMETASK_ERROR:{
                handler.removeMessages(TASK_ERROR);
                map.remove(TASK_ERROR);
                break;
            }

        }
    }

    public void clearAllTask(){
        clearTask(TimeTaskType.TIMETASK_LOGIN);
        clearTask(TimeTaskType.TIMETASK_LOAD);
        clearTask(TimeTaskType.TIMETASK_ERROR);
    }
    static class TaskBean {
        int retryCount;
        long millisecond;
    }

    public interface TimeTaskListener  {
        public void handlerTimeTaskLooping(int taskType,boolean isEnd);//定时周期回调,已经结束时传true
    }

}
