package com.edusoho.kuozhi.v3.util;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.sys.School;

import java.util.HashMap;

/**
 * Created by suju on 17/4/14.
 */

public class ReportUtil {

    public static HashMap<String, String> getReportInfoWithMap(Context context) {
        HashMap<String, String> params = new HashMap<String, String>();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        params.put("deviceSn", telephonyManager.getDeviceId());
        params.put("platform", "Android " + Build.MODEL);
        params.put("version", Build.VERSION.SDK);
        params.put("screenresolution", displayMetrics.widthPixels + "x" + displayMetrics.heightPixels);
        params.put("kernel", Build.VERSION.RELEASE);
        params.put("edusohoVersion", context.getString(R.string.api_version));

        User user = getAppSettingProvider().getCurrentUser();
        if (user != null) {
            params.put("user", user.toString());
        }
        School school = getAppSettingProvider().getCurrentSchool();
        if (school != null) {
            params.put("school", school.toString());
        }
        return params;
    }

    public static String getReportInfo(Context context, String... more) {
        HashMap<String, String> params = getReportInfoWithMap(context);
        StringBuffer stringBuffer = new StringBuffer();

        for (String key : params.keySet()) {
            stringBuffer.append(key)
                    .append(":")
                    .append(params.get(key))
                    .append(";");
        }
        if (more != null) {
            for (String info : more) {
                stringBuffer.append(info).append(";");
            }
        }
        return stringBuffer.toString();
    }

    protected static AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }
}
