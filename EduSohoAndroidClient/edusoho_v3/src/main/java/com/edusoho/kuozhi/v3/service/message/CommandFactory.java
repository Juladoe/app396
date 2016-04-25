package com.edusoho.kuozhi.v3.service.message;

import android.content.Context;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.util.PushUtil;

/**
 * Created by 菊 on 2016/4/25.
 */
public class CommandFactory {

    public static AbstractCommand create(Context context, V2CustomContent v2CustomContent) {

        String toType = v2CustomContent.getTo().getType();
        String bodyType = v2CustomContent.getBody().getType();

        switch (bodyType) {
            case PushUtil.ChatMsgType.AUDIO:
            case PushUtil.ChatMsgType.IMAGE:
            case PushUtil.ChatMsgType.TEXT:
            case PushUtil.ChatMsgType.MULTI:
                if (PushUtil.ChatUserType.CLASSROOM.equals(toType)) {
                    return null;
                } else if (PushUtil.ChatUserType.USER.equals(toType)) {
                    return new MessageCommand(context, v2CustomContent);
                } else if (PushUtil.ChatUserType.COURSE.equals(toType)) {
                    return new DiscussMsgCommand(context, v2CustomContent);
                }
                break;
        }
        return null;
    }
}
