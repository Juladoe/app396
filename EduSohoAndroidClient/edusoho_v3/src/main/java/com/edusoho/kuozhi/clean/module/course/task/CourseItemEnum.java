package com.edusoho.kuozhi.clean.module.course.task;

/**
 * Created by JesseHuang on 2017/4/6.
 */

public enum CourseItemEnum {
    CHAPTER("chapter", 0), UNIT("unit", 1), TASK("task", 2), LESSON("lesson", 3);

    private String mName;
    private int mIndex;

    CourseItemEnum(String name, int index) {
        mName = name;
        mIndex = index;
    }

    public String toString() {
        return mName;
    }

    public int getIndex() {
        return mIndex;
    }
}
