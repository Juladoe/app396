package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JesseHuang on 2017/4/14.
 */

public class CourseLearningProgress implements Serializable {

    public int taskCount;
    public float progress;
    public int taskResultCount;
    public int taskPerDay;
    public int planStudyTaskCount;
    public int planProgressProgress;

    public CourseMember member;
    public CourseTask nextTask;
}
