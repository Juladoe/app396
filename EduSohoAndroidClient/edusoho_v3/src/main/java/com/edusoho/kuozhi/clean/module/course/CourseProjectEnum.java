package com.edusoho.kuozhi.clean.module.course;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public enum CourseProjectEnum {
    INFO("CourseProjectInfoFragment", "简介", 0), TASKS("CourseTasksFragment", "任务", 1), RATE("CourseProjectRatesFragment", "评价", 2);

    private String mModuleName;
    private String mModuleTitle;
    private int mPosition;

    CourseProjectEnum(String moduleName, String moduleTitle, int position) {
        mModuleName = moduleName;
        mModuleTitle = moduleTitle;
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }

    public String getModuleTitle() {
        return mModuleTitle;
    }

    public String getModuleName() {
        return mModuleName;
    }

    public static String getModuleNameByPosition(int position) {
        for (CourseProjectEnum modele : values()) {
            if (modele.getPosition() == position) {
                return modele.getModuleName();
            }
        }
        return "";
    }

    public static String getModuleTitleByPosition(int position) {
        for (CourseProjectEnum modele : values()) {
            if (modele.getPosition() == position) {
                return modele.getModuleTitle();
            }
        }
        return "";
    }

}
