package com.edusoho.kuozhi.v3.service.push;

/**
 * Created by JesseHuang on 15/12/30.
 */
public class PushThreadPostCommand extends PushCommand {

    public PushThreadPostCommand(Pusher pusher) {
        super(pusher);
    }

    @Override
    public void execute() {
        mPusher.pushThreadPost();
    }
}
