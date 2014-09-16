package com.edusoho.kuozhi.model.Question;

import java.io.Serializable;

/**
 * Created by hby on 14-9-15.
 * 界面元素对象
 */
public class QuestionUnused implements Serializable {
    /**
     * 问题标题
     */
    public String questionTitle;

    /**
     * 问题回复总数
     */
    public int replyAmount;

    /**
     * 如果有最新回复，是否已阅读
     */
    public boolean haveRead;

    public boolean teacherRead;

    public String largeImageUrl;

    public String middleImageUrl;

    public String smallImageUrl;

    /**
     * 问题课时
     */
    public String questionLesson;


}
