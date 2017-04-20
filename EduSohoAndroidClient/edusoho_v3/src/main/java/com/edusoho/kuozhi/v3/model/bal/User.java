package com.edusoho.kuozhi.v3.model.bal;

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
    public String mediumAvatar;
    public String createdTime;
    public String about;
    public String role;
    public Avatar avatar;

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

    public class Avatar {
        public String medium;
    }

    public String getMediumAvatar() {
        int schemIndex = avatar.medium.lastIndexOf("http://");
        if (schemIndex != -1) {
            return avatar.medium.substring(schemIndex);
        }
        if (avatar.medium.startsWith("//")) {
            return "http:" + avatar.medium;
        }
        return avatar.medium;
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
