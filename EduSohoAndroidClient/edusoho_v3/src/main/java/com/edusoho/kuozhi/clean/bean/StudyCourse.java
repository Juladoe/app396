package com.edusoho.kuozhi.clean.bean;

import com.edusoho.kuozhi.clean.bean.innerbean.Study;

import java.io.Serializable;

/**
 * Created by DF on 2017/5/11.
 */

public class StudyCourse implements Serializable {
    public int id;
    public String title;
    public int publishedTaskNum;
    public int learnedNum;
    public Study courseSet;
    public int studentNum;

}
