package com.edusoho.kuozhi.model;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by howzhi on 14-8-27.
 */
public class GsonType<T> {

    private TypeToken<T> tTypeToken;
    private String parseObj;

    public GsonType(String parseObj)
    {
        this.parseObj = parseObj;
        tTypeToken = new TypeToken<T>(){};
    }

    public String getJson()
    {
        return parseObj;
    }
}
