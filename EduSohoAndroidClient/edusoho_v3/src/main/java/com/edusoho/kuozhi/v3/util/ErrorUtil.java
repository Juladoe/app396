package com.edusoho.kuozhi.v3.util;

import android.content.Context;

import com.google.gson.Gson;
import java.util.LinkedHashMap;

/**
 * Created by suju on 16/10/28.
 */
public class ErrorUtil {

    public static void showErrorMessage(Context context, String errorStr) {
        LinkedHashMap errorResult = null;
        try {
            errorResult = new Gson().fromJson(errorStr, LinkedHashMap.class);
        } catch (Exception e) {
        }
        if (errorResult == null) {
            return;
        }

        if (errorResult.containsKey("error")) {
            LinkedHashMap<String, String> errorMap = (LinkedHashMap<String, String>) errorResult.get("error");
            if (errorMap != null && errorMap.containsKey("message")) {
                CommonUtil.longToast(context, errorMap.get("message"));
            }
        }
    }
}
