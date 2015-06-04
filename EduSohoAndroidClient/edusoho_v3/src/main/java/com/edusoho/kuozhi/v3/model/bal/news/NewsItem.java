package com.edusoho.kuozhi.v3.model.bal.news;

import java.io.Serializable;

/**
 * Created by JesseHuang on 15/6/3.
 */
public class NewsItem implements Serializable {
    public int position;
    public int unread;
    public String srcUrl;
    public NewsEnum type;
    public String title;
    public String content;
    public String postTime;
    public int isTop;
}
