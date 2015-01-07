package com.edusoho.kuozhi.model.SchoolRoom;

import java.io.Serializable;

/**
 * Created by JesseHuang on 14/12/24.
 * 学堂接口返回实体类
 */
public class SchoolRoomResult implements Serializable {
    public String title;
    public SchoolRoomItem data;

    public SchoolRoomResult(String t, SchoolRoomItem d) {
        title = t;
        data = d;
    }
}
