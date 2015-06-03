package com.edusoho.kuozhi.v3.model.bal.news;

/**
 * Created by JesseHuang on 15/6/3.
 */
public enum NewsEnum {
    FRIEND("校友", 1), TEACHER("教师", 2), COURSE("课程", 3), LIVE_COURSE("直播", 4);

    private String name;
    private int index;

    private NewsEnum(String n, int i) {
        this.name = n;
        this.index = i;
    }

    public static int getIndex(String name) {
        for (NewsEnum s : NewsEnum.values()) {
            if (s.name.equals(name)) {
                return s.index;
            }
        }
        return 0;
    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
