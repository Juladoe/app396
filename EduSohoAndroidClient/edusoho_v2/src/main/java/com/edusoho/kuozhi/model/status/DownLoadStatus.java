package com.edusoho.kuozhi.model.status;

/**
 * Created by howzhi on 14/12/12.
 */
public enum DownLoadStatus {
    STARTING, FINISH, NONE, BEGIN;

    public static DownLoadStatus value(int status)
    {
        switch (status) {
            case 0:
                return NONE;
            case 1:
                return BEGIN;
            case 2:
                return STARTING;
            case 3:
                return STARTING;
        }
        return NONE;
    }
}
