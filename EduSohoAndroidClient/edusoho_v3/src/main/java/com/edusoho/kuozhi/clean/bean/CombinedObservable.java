package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;

/**
 * Created by JesseHuang on 2017/4/3.
 */

public class CombinedObservable<T, R> implements Serializable {
    public T T;
    public R r;

    public CombinedObservable(T t, R r) {
        T = t;
        this.r = r;
    }
}
