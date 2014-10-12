package com.edusoho.kuozhi.model;


/**
 * Created by howzhi on 14-10-10.
 */
public class LasterLearnStatus<T> {
    public T data;
    public Progress progress;

    public class Progress
    {
        public String percent;
        public int number;
        public int total;
    }
}
