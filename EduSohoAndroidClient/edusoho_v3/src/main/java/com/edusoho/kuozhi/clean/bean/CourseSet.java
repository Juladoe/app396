package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;

/**
 * Created by JesseHuang on 2017/3/27.
 */

public class CourseSet implements Serializable {

    public String summary;
    public Cover cover;

    public static class Cover implements Serializable {
        public String large;
        public String middle;
        public String small;
    }
}
