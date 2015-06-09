package com.edusoho.kuozhi.v3.model.bal;

/**
 * Created by Melomelon on 2015/6/2.
 */
public class Friend {
    public Friend(String name) {
        this.name = name;
    }

    public Friend(int avatarID, String name) {
        this.avatarID = avatarID;
        this.name = name;
    }

    public Friend(int avatarID, String name, int state) {
        this.avatarID = avatarID;
        this.name = name;
        this.state = state;
    }

    public Friend(int avatarID, String name, int state,boolean isTeacher) {
        this.avatarID = avatarID;
        this.name = name;
        this.state = state;
        this.isTeacher = isTeacher;
    }

    public int avatarID;
    public String avatar;
    public String name;
    public int state;
    public boolean isTeacher;
}
