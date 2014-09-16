package com.edusoho.kuozhi.model.Question;

import java.io.Serializable;

/**
 * Created by hby on 14-9-15.
 */
public class LastestPostUser implements Serializable {
    private int id;
    private String email;
    private String uri;
    private String nickname;
    private String title;
    private String type;
    private String smallAvatar;
    private String mediumAvatar;
    private String largeAvatar;
    private String emailVerified;
    private int setup;
    private String[] roles;
    private int locked;
    private String createdTime;
    private Vip vip;
}
