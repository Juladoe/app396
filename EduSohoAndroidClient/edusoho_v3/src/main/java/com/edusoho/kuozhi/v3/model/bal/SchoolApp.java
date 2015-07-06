package com.edusoho.kuozhi.v3.model.bal;

import java.io.Serializable;

/**
 * Created by Melomelon on 2015/7/6.
 */
public class SchoolApp implements Serializable {
    public int id;
    public String name;
    public String title;
    public String about;
    public String avatar;
    public String callback;

    public boolean isTop = false;
}
