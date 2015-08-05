package com.edusoho.kuozhi.v3.model.bal;

/**
 * Created by Melomelon on 2015/6/2.
 */
public class Friend {

    public int id;
    public int avatarID;
    public String largeAvatar;
    public String mediumAvatar;
    public String smallAvatar;
    public String nickname;
    public String title;
    public String[] roles;
    public String friendship;
    public boolean isTeacher;

    public boolean isTop = false;
    public boolean isBottom = false;

    private String sortLetters;

    public String getSortLetters() {
        return sortLetters;
    }
    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
