package com.edusoho.kuozhi.model;

/**
 * Created by howzhi on 14-8-14.
 */
public class Category {
    public int id;
    public String code;
    public String name;
    public String icon;
    public String path;
    public String weight;
    public int groupId;
    public int parentId;
    public String description;
    public int depth;

    public static final String GROUP = "group";
}
