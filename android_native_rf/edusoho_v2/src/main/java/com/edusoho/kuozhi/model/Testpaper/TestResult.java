package com.edusoho.kuozhi.model.Testpaper;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-10-9.
 */
public class TestResult {
    /**
     * wrong,
     * noAnswer
     * right
     * partRight
     */
    public int id;
    public int itemId;
    public int testId;
    public int testPaperResultId;
    public int userId;
    public int questionId;
    public String status;
    public String teacherSay;
    public ArrayList<String> answer;
    public double score;
}
