package com.edusoho.kuozhi.model.HtmlApp;

/**
 * Created by howzhi on 14/11/30.
 */
public class Menu {
    public String name;
    public int icon;
    public String action;
    public Menu[] item;

    @Override
    public String toString() {
        return "Menu{" +
                "name='" + name + '\'' +
                ", icon=" + icon +
                ", action='" + action + '\'' +
                '}';
    }
}
