package com.edusoho.kuozhi.model.course;

/**
 * Created by howzhi on 14-9-25.
 */
public class CourseCode {
    public Code useable;
    public String message;
    public double afterAmount;
    public double decreaseAmount;

    public static enum Code
    {
        yes, no;
    }
}
