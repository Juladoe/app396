package com.edusoho.kuozhi.model;

/**
 * Created by howzhi on 14-7-18.
 */
public class CourseMenu {

    public static final int BACK = 0001;
    public static final int ALL = 0002;
    public String name;
    public String type;
    public String parentId;
    public int action;

    public CourseMenu(String type, String name, String parentId)
    {
        this.parentId = parentId;
        this.name = name;
        this.type = type;
    }

    public CourseMenu(String type, String name)
    {
        this.name = name;
        this.type = type;
    }

    public CourseMenu(CourseMenu src, String parentId, int action)
    {
        this(src.type, src.name);
        this.parentId = parentId;
        this.action = action;
    }
}
