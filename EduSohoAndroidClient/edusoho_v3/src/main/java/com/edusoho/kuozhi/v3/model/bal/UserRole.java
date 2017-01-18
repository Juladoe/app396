package com.edusoho.kuozhi.v3.model.bal;

import com.edusoho.kuozhi.v3.util.json.GsonEnum;

/**
 * Created by howzhi on 14-8-19.
 */

public enum UserRole implements GsonEnum<UserRole> {
    ROLE_USER, ROLE_SUPER_ADMIN, ROLE_TEACHER, ROLE_ADMIN, NO_SUPPORT;

    @Override
    public UserRole deserialize(String jsonEnum) {
        switch (jsonEnum) {
            case "ROLE_ADMIN":
            case "ROLE_SUPER_ADMIN":
            case "ROLE_TEACHER":
            case "ROLE_USER":
                return valueOf(jsonEnum);
        }
        return NO_SUPPORT;
    }

    @Override
    public String serialize() {
        return name();
    }
}
