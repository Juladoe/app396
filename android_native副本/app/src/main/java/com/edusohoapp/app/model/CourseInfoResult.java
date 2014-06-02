package com.edusohoapp.app.model;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by howzhi on 14-5-24.
 */
public class CourseInfoResult {
    public Course course;
    public LinkedHashMap<String, LessonItem> items;
    public Review[] reviews;
    public boolean userIsStudent;
    public boolean userFavorited;
    public HashMap<Integer, String> userLearns;
}
