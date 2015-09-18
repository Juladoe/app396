package com.edusoho.kuozhi.v3.service.push;

/**
 * Created by JesseHuang on 15/9/11.
 */
public class CommandFactory {

    /**
     * 老格式处理
     */
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

    /**
     * 3.1版本格式处理
     */
    public static PushCommand V2Make(Pusher pusher) {
        PushCommand pushCommand = null;
        switch (pusher.getV2CustomContent().getBody().getType()) {
            case "lesson.publish":
                pushCommand = new PushLessonNewCommand(pusher);
                break;
            case "testpaper.reviewed":
                break;
            case "lesson.live_start":
                break;
            case "news.create":
                pushCommand = new PushArticleCreateCommand(pusher);
                break;
            case "text":
            case "audio":
            case "image":
                pusher.convertWrapperMessage2V2();
                pushCommand = new PushMsgCommand(pusher);
                break;
        }
        return pushCommand;
    }
}
