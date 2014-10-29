package com.edusoho.kuozhi.model.Question;

import java.io.Serializable;

/**
 * Created by hby on 14-9-15.
 */
public class ReplyUser implements Serializable {
    public int id;
    public String email;
    public String uri;
    public String nickname;
    public String title;
    public String type;
    public String smallAvatar;
    public String mediumAvatar;
    public String largeAvatar;
    public String emailVerified;
    public int setup;
    public String[] roles;
    public int locked;
    public String createdTime;
    public Vip vip;
}
