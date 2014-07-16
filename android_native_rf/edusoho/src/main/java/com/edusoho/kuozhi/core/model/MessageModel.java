package com.edusoho.kuozhi.core.model;

/**
 * Created by howzhi on 14-7-14.
 */
public class MessageModel {
    public int what;
    public Object obj;

    public MessageModel(Object obj){
        this.obj = obj;
    }

    public MessageModel(int what)
    {
        this.what = what;
    }

    public MessageModel(int what, Object obj)
    {
        this(obj);
        this.what = what;
    }
}
