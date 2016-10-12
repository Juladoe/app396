package com.edusoho.kuozhi.v3.util;

import android.os.Bundle;
import android.text.TextUtils;

import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;

/**
 * Created by su on 2016/2/3.
 */
public class RequestUtil {

    public static String handleRequestError(String jsonStr) {
        LinkedHashMap errorResult = null;
        try {
            errorResult = new Gson().fromJson(jsonStr, LinkedHashMap.class);
        } catch (Exception e) {
        }
        if (errorResult == null) {
            return jsonStr;
        }
        if (errorResult.containsKey("error")) {
            LinkedHashMap<String, String> errorMap = (LinkedHashMap<String, String>) errorResult.get("error");

            if ("not_login".equals(errorMap.get("name"))) {
                MessageEngine.getInstance().sendMsg(Const.TOKEN_LOSE, new Bundle());
                return null;
            }

            if (errorMap.containsKey("message") && errorMap.containsKey("name")) {
                return errorMap.get("message");
            }
        }

        if (errorResult.containsKey("code")) {
            String message = errorResult.containsKey("message") ? errorResult.get("message").toString() : "";
            if (!TextUtils.isEmpty(message) && message.startsWith("API Token")) {
                MessageEngine.getInstance().sendMsg(Const.TOKEN_LOSE, new Bundle());
                return null;
            }

            return jsonStr;
        }

        return jsonStr;
    }

    public static String handleRequestHttpError(String data) {
        try {
            LinkedHashMap errorResult = null;
            try {
                errorResult = new Gson().fromJson(data, LinkedHashMap.class);
            } catch (Exception e) {
            }
            if (errorResult == null) {
                return data;
            }
            if (errorResult.containsKey("code")) {
                String message = errorResult.containsKey("message") ? errorResult.get("message").toString() : "";
                String webServerCode = new DecimalFormat("0").format(errorResult.get("code"));
                if (!TextUtils.isEmpty(message) && "500".equals(webServerCode)) {
                    return message;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    public static String handleRequestError(byte[] data) {
        String jsonStr = null;
        try {
            jsonStr = handleRequestError(new String(data, "utf-8"));
        } catch (Exception e) {
        }

        return jsonStr;
    }
}
