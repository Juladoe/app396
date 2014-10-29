package com.edusoho.kuozhi.model.Question;

import java.io.Serializable;

/**
 * Created by hby on 14-9-18.
 */
public class EntireReply implements Serializable {

    /**
     * 是否第一个回复者
     */
    public boolean isFirstReply;
    public ReplyModel replyModel;


    public EntireReply() {
        this(false, null);
    }

    /**
     *
     * @param isFirstReply
     * @param replyModel
     */
    public EntireReply(boolean isFirstReply, ReplyModel replyModel) {
        this.replyModel = replyModel;
        this.isFirstReply = isFirstReply;
    }

}
