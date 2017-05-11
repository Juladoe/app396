package com.edusoho.kuozhi.clean.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by JesseHuang on 2017/5/7.
 */

public class CourseSetting implements Serializable {

    public static final String COURSE_SETTING = "course_setting";
    public static final String SHOW_STUDENT_NUM_ENABLED_KEY = "show_student_num_enabled_key";
    public static final String CHAPTER_NAME_KEY = "chapter_name_key";
    public static final String PART_NAME_KEY = "part_name_key";

    @SerializedName("show_student_num_enabled")
    public String showStudentNumEnabled;
    @SerializedName("chapter_name")
    public String chapterName;
    @SerializedName("part_name")
    public String partName;
}
