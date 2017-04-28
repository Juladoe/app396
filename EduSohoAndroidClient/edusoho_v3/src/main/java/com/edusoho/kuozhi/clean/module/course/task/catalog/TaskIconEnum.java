package com.edusoho.kuozhi.clean.module.course.task.catalog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JesseHuang on 2017/4/7.
 */

public enum TaskIconEnum {
    TEXT("text"), VIDEO("video"), AUDIO("audio"), LIVE("live"), DISCUSS("discuss"),
    FLASH("flash"), DOC("doc"), PPT("ppt"), TESTPAPER("testpaper"), HOMEWORK("homework"),
    EXERCISE("exercise"), DOWNLOAD("download");

    private String mName;

    TaskIconEnum(String name) {
        this.mName = name;
    }

    private static final Map<String, TaskIconEnum> stringToEnum = new HashMap<>();

    static {
        for (TaskIconEnum taskIconEnum : values()) {
            stringToEnum.put(taskIconEnum.toString(), taskIconEnum);
        }
    }

    public static TaskIconEnum fromString(String name) {
        return stringToEnum.get(name);
    }

    @Override
    public String toString() {
        return mName;
    }
}
