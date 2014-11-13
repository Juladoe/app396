package com.edusoho.kuozhi.model;

/**
 * Created by howzhi on 14-9-17.
 */
public enum MaterialType{
    VIDEO, IMAGE, DOCUMENT, AUDIO, PPT, OTHER, EMPTY;

    public static MaterialType value(String typeName)
    {
        MaterialType type;
        try {
            type =  valueOf(typeName.toUpperCase());
        }catch (Exception e) {
            type = EMPTY;
        }
        return type;
    }
}
