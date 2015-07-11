package com.edusoho.kuozhi.v3.model.bal.push;

/**
 * Created by JesseHuang on 15/7/2.
 */
public enum ObjectType {
    FRIEND("friend", 1), TEACHER("teacher", 2),
    COURSE("course", 3);

    private String name;
    private int index;

    private ObjectType(String n, int i) {
        this.name = n;
        this.index = i;
    }

    public String getName() {
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }

    public static ObjectType getName(int i) {
        for (ObjectType type : ObjectType.values()) {
            if (i == type.index) {
                return type;
            }
        }
        return null;
    }

    public static ObjectType getName(String n) {
        for (ObjectType type : ObjectType.values()) {
            if (n.equals(type.getName())) {
                return type;
            }
        }
        return null;
    }
}
