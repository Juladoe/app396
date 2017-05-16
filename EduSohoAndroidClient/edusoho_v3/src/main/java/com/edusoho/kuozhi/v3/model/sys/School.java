package com.edusoho.kuozhi.v3.model.sys;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by howzhi on 14-5-25.
 */
public class School {
    public String name;
    public String url;
    public String host;
    public String logo;
    public HashMap<String, String> apiVersionRange;
    public String[] splashs;

    public String getDomain() {
        try {
            URL url = new URL(host);
            return url.getHost();
        } catch (MalformedURLException e) {
        }

        return "";
    }

    @Override
    public String toString() {
        return "School{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", host='" + host + '\'' +
                ", logo='" + logo + '\'' +
                ", apiVersionRange=" + apiVersionRange +
                ", splashs=" + Arrays.toString(splashs) +
                '}';
    }
}
