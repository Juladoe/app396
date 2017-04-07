package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;

/**
 * Created by JesseHuang on 2017/4/1.
 */

public class DataPageResult<T> implements Serializable {
    public T[] data;
    public Page paging;


    public static class Page implements Serializable {
        public int total;
        public int offset;
        public int limit;
    }
}
