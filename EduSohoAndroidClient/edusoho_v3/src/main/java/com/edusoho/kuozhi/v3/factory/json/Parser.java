package com.edusoho.kuozhi.v3.factory.json;

import java.lang.reflect.Type;

/**
 * Created by su on 2016/1/4.
 */
public interface Parser {

    public <T> T fromJson(String json, Class<T> tClass);

    public <T> T fromJson(String json, Type type);

    public String jsonToString(Object jsonObj);
}
