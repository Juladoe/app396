package com.edusohoapp.app.entity;

import java.util.ArrayList;

public class RecommendSchoolItem {

    public static final int ADDITEM = 0001;
    public static final int SCHOOLITEM = 0002;
	public String title;
	public String info;
	public String logo;
	public String url;
    public int type = SCHOOLITEM;

	public static RecommendSchoolItem createAddItem()
    {
        RecommendSchoolItem item = new RecommendSchoolItem();
        item.title = "添加网校";
        item.type = ADDITEM;
        item.url = "add";
        return item;
    }
}
