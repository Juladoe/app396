package com.edusoho.kuozhi.v3.entity.lesson;

import java.io.Serializable;

/**
 * Created by JesseHuang on 16/3/31.
 * 解析 LessonItem 中 mediaUri 的信息
 */
public class StreamInfo implements Serializable {
    public String level;
    public String src;
    public String name;
    public int bandwidth;
}
