package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;

/**
 * Created by JesseHuang on 2017/3/23.
 * 计划任务Bean
 */

public class CourseTask implements Serializable {
    public String chapterNum;
    public String chapterTitle;
    public String itemType;
    public String taskType;
    public String taskTitle;
    public String taskTime;
    public String isTaskFree;
}
