package com.edusoho.kuozhi.model;


/**
 * Created by howzhi on 14-8-7.
 */
public class SchoolBanner {
    public String url;
    public String action;
    public String params;

    public static SchoolBanner def()
    {
        SchoolBanner schoolBanner = new SchoolBanner();
        schoolBanner.action = "";
        schoolBanner.url = "localRes";
        schoolBanner.params = "";
        return schoolBanner;
    }
}
