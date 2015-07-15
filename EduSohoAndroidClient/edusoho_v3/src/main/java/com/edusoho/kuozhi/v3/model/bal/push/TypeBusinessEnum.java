package com.edusoho.kuozhi.v3.model.bal.push;

/**
 * Created by JesseHuang on 15/7/7.
 */
public enum TypeBusinessEnum {
    NORMAL("normal", 1), BULLETIN("bulletin", 2), VERIFIED("verified", 3);

    private String name;
    private int index;

    public String getName() {
        return this.name;
    }

    private TypeBusinessEnum(String n, int i) {
        this.name = n;
        this.index = i;
    }

    public static TypeBusinessEnum getName(int i) {
        for (TypeBusinessEnum type : TypeBusinessEnum.values()) {
            if (i == type.index) {
                return type;
            }
        }
        return null;
    }

    public static TypeBusinessEnum getName(String n) {
        for (TypeBusinessEnum type : TypeBusinessEnum.values()) {
            if (n.equals(type.getName())) {
                return type;
            }
        }
        return null;
    }
}
