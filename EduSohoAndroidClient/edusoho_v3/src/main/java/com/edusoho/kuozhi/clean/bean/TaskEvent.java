package com.edusoho.kuozhi.clean.bean;

import com.edusoho.kuozhi.clean.bean.innerbean.TaskResult;

/**
 * Created by JesseHuang on 2017/4/27.
 */

public class TaskEvent {
    public TaskResult result;
    public String event;
    public CourseTask nextTask;
    public String lastTime;
    public String completionRate;
}
