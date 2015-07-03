package com.edusoho.kuozhi.v3.model.bal.push;

/**
 * Created by JesseHuang on 15/7/2.
 */
public enum ChatTypeEnum {
    FRIEND("FRIEND", 1), TEACHER("TEACHER", 2),
    COURSE("COURSE", 3), TEXT("TEXT", 4), SOUND("SOUND", 5), IMAGE("IMAGE", 6);

    private String name;
    private int index;

    private ChatTypeEnum(String n, int i) {
        this.name = n;
        this.index = i;
    }

    public String getName() {
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }

    public static ChatTypeEnum getName(int i) {
        for (ChatTypeEnum type : ChatTypeEnum.values()) {
            if (i == type.index) {
                return type;
            }
        }
        return null;
    }

    public static ChatTypeEnum getName(String n) {
        for (ChatTypeEnum type : ChatTypeEnum.values()) {
            if (n.equals(type.getName())) {
                return type;
            }
        }
        return null;
    }
}
