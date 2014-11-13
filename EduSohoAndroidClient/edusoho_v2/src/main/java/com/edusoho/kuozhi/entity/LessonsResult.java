package com.edusoho.kuozhi.entity;

import com.edusoho.kuozhi.model.LessonItem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by howzhi on 14-9-15.
 */
public class LessonsResult {
    public ArrayList<LessonItem> lessons;
    public HashMap<Integer, LearnStatus> learnStatuses;
}
