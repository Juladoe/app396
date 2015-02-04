package com.edusoho.kuozhi.model.SchoolRoom;

/**
 * Created by JesseHuang on 14/12/24.
 */
public enum SchoolRoomEnum {
    LIVE_COURSE("在学直播", 1), COURSE("在学课程", 2), QUESTION("问答", 3), DISCUSSION("讨论", 4), NOTE("笔记", 5),
    LETTER("私信", 6);

    private String name;
    private int index;

    private SchoolRoomEnum(String n, int i) {
        this.name = n;
        this.index = i;
    }

    public static int getIndex(String name) {
        for (SchoolRoomEnum s : SchoolRoomEnum.values()) {
            if (s.name.equals(name)) {
                return s.index;
            }
        }
        return 0;
    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
