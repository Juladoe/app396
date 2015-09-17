package com.edusoho.kuozhi.v3.service.push;

/**
 * Created by JesseHuang on 15/9/16.
 */
public class PushLessonNewCommand extends PushCommand {

    public PushLessonNewCommand(Pusher pusher) {
        super(pusher);
    }

    @Override
    public void execute() {
        mPusher.pushLessonNew();
    }
}
