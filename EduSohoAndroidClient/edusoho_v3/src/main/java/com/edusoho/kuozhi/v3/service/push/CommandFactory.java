package com.edusoho.kuozhi.v3.service.push;

/**
 * Created by JesseHuang on 15/9/11.
 */
public class CommandFactory {

    public static PushCommand Make(String command, Pusher pusher) {
        PushCommand pushCommand = null;
        switch (command) {
            case "friend":
            case "teacher":
                pushCommand = new PushMsgCommand(pusher);
                break;
            case "bulletin":
                pushCommand = new PushBulletinCommand(pusher);
                break;
            case "verified":
                pushCommand = new PushVerifiedCommand(pusher);
                break;
        }
        return pushCommand;
    }
}
