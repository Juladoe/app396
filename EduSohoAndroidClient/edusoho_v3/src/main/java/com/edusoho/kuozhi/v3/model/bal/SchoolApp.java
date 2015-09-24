package com.edusoho.kuozhi.v3.model.bal;

import com.edusoho.kuozhi.R;

import java.io.Serializable;

/**
 * Created by Melomelon on 2015/7/6.
 */
public class SchoolApp implements Serializable {
    public int id;
    public String code;
    public String name;
    public String title;
    public String about;
    public String avatar;
    public String callback;

    public boolean isTop = false;
    public boolean isBottom = false;

    public static SchoolApp createArticleApp() {
        SchoolApp app = new SchoolApp();
        app.name = "资讯";
        app.title = "";
        app.id = 2;
        app.avatar = "R.drawable.article_app_icon";
        app.code = "news";

        return app;
    }
}
