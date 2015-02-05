package com.soooner.EplayerPluginLibary.entity;

/**
 * Created by zhaoxu2014 on 15-1-12.
 */
public class AnimationInfo {
    public String name;
    public String drawableName;

    public AnimationInfo(String drawableName,String name) {
        this.name = name;
        this.drawableName = drawableName;
    }

    public String getName() {
        return name;
    }

    public String getDrawableName() {
        return drawableName;
    }
}
