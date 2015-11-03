package com.edusoho.kuozhi.v3.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.edusoho.kuozhi.v3.model.sys.School;

import java.util.Map;

/**
 * Created by howzhi on 15/11/2.
 */
public class SchoolUtil {

    public static School getDefaultSchool(Context context) {

        School item = null;
        SharedPreferences sp = context.getSharedPreferences("defaultSchool", context.MODE_PRIVATE);
        Map<String, String> map = (Map<String, String>) sp.getAll();
        if (!map.isEmpty()) {
            item = new School();
            item.name = map.get("name");
            item.url = map.get("url");
            item.host = map.get("host");
            item.logo = map.get("logo");
            item.url = checkSchoolUrl(item.url);
        }

        return item;
    }

    public static void saveSchool(Context context, School school) {
        SharedPreferences sp = context.getSharedPreferences("defaultSchool", context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("name", school.name);
        edit.putString("url", school.url);
        edit.putString("host", school.host);
        edit.putString("logo", school.logo);
        edit.commit();
    }

    private static String checkSchoolUrl(String url) {
        if (url.endsWith("mapi_v1")) {
            String newUrl = url.substring(0, url.length() - 1);
            return newUrl + "2";
        }
        return url;
    }
}
