package com.edusoho.kuozhi.core.model;

/**
 * Created by howzhi on 14-6-25.
 */
public class PluginModel {
    public String name;
    public String version;
    public String packAge;

    public PluginModel(String _name, String _version, String _pageAge)
    {
        this.name = _name;
        this.version = _version;
        this.packAge = _pageAge;
    }
}
