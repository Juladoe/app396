package com.soooner.EplayerPluginLibary.util;

import java.util.Timer;
import java.util.TimerTask;

public class SingleTimerTask {
    private static final String TAG = SingleTimerTask.class.getSimpleName();

    private Timer timer;
    private long currentStaticTimeMillis;
    private MutilCancelTimerTask timerTask;


    private MutilCancelTimerListener listener;



    public static interface MutilCancelTimerListener {
        public void run();
    }

    public SingleTimerTask(MutilCancelTimerListener listener){
        super();
        this.listener = listener;
    }


    public void startTimer(long time) {
        this.cancelTimer();

        timer = new Timer();
        timerTask = new MutilCancelTimerTask(SingleTimerTask.this.currentStaticTimeMillis);
        timer.schedule(timerTask, time);

    }

    public void cancelTimer() {
        SingleTimerTask.this.currentStaticTimeMillis = System.currentTimeMillis();
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    public class MutilCancelTimerTask extends TimerTask {

        private long currentTimeMillis;

        public MutilCancelTimerTask(long currentTimeMillis) {
            this.currentTimeMillis = currentTimeMillis;
        }

        @Override
        public void run() {

            long currentStaticTimeMillis = SingleTimerTask.this.currentStaticTimeMillis;
            if (currentTimeMillis == currentStaticTimeMillis) {

                LogUtil.d(TAG, "SingleTimerTask excute");

                if(SingleTimerTask.this.listener!=null){
                    SingleTimerTask.this.listener.run();
                }

            } else {

                LogUtil.d(TAG, "SingleTimerTask igron");
            }
        }
    }

}
