package com.edusoho.kuozhi.model;

/**
 * Created by howzhi on 14-8-19.
 */

public enum UserRole {
    ROLE_USER, ROLE_SUPER_ADMIN, ROLE_TEACHER, ROLE_ADMIN;

    public static String coverRoleToStr(UserRole[] userRoles)
    {
        StringBuilder stringBuilder = new StringBuilder();

        for (UserRole role : userRoles) {
            switch (role) {
                case ROLE_USER:
                    stringBuilder.append("普通用户");
                    break;
                case ROLE_ADMIN:
                    stringBuilder.append("管理员");
                    break;
                case ROLE_TEACHER:
                    stringBuilder.append("教师");
                    break;
                case ROLE_SUPER_ADMIN:
                    stringBuilder.append("超级管理员");
                    break;
            }
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }
}
