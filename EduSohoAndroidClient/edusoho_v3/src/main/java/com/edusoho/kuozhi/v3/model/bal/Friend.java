package com.edusoho.kuozhi.v3.model.bal;

/**
 * Created by Melomelon on 2015/6/2.
 */
public class Friend {
    public Friend(String name) {
        this.nickname = name;
    }

    public Friend(int avatarID, String name) {
        this.avatarID = avatarID;
        this.nickname = name;
    }

    public Friend(int avatarID, String name, String state) {
        this.avatarID = avatarID;
        this.nickname = name;
        this.friendship = state;
    }

    public Friend(int avatarID, String name, String state,boolean isTeacher) {
        this.avatarID = avatarID;
        this.nickname = name;
        this.friendship = state;
        this.isTeacher = isTeacher;
    }

    public int id;
    public int avatarID;
    public String largeAvatar;
    public String mediumAvatar;
    public String smallAvatar;
    public String nickname;
    public String title;
    public UserRole[] roles;
    public String friendship;
    public boolean isTeacher;

    public boolean isTop = false;
}
