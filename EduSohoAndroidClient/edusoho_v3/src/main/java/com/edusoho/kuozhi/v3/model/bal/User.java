package com.edusoho.kuozhi.v3.model.bal;

import com.edusoho.kuozhi.clean.bean.innerbean.Avatar;

import java.io.Serializable;

/**
 * Created by howzhi on 14-5-25.
 */
public class User implements Serializable {
    public String nickname;
    public String email;
    public String password;
    public int id;
    public UserRole[] roles;
    public String uri;
    public String title;
    public String type;
    public String point;
    public String createdTime;
    public String about;
    public String role;
    public Avatar userAvatar;

    /**
     * 关注
     */
    public String following;
    /**
     * 粉丝
     */
    public String follower;

    public Vip vip;

    public String thirdParty;

    public String getMediumAvatar() {
        int schemIndex = userAvatar.medium.lastIndexOf("http://");
        if (schemIndex != -1) {
            return userAvatar.medium.substring(schemIndex);
        }
        if (userAvatar.medium.startsWith("//")) {
            return "http:" + userAvatar.medium;
        }
        return userAvatar.medium;
    }

    public String userRole2String() {
        if (roles == null || roles.length == 0) {
            return "学员";
        }

        StringBuilder sb = new StringBuilder();
        for (UserRole userRole : roles) {
            if (userRole != null) {
                sb.append(userRole.getRoleName()).append(" ");
            }
        }

        return sb.toString();
    }
}
