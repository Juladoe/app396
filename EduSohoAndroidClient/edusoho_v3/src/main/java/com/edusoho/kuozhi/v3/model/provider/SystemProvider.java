package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.model.sys.SchoolBanner;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.SchoolUtil;
import com.edusoho.kuozhi.v3.util.volley.BaseVolleyRequest;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by howzhi on 15/9/24.
 */
public class SystemProvider extends ModelProvider {

    public SystemProvider(Context context) {
        super(context);
    }

    public ProviderListener<SchoolApp> getSchoolApp(RequestUrl requestUrl) {
        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<SchoolApp>(){});

        requestOption.getRequest().setCacheUseMode(BaseVolleyRequest.ALWAYS_USE_CACHE);
        return requestOption.build();
    }

    public ProviderListener getSchoolBanners(RequestUrl requestUrl) {
        ProviderListener<List<SchoolBanner>> responseListener = new ProviderListener<List<SchoolBanner>>() {
        };
        addRequest(requestUrl, new TypeToken<List<SchoolBanner>>() {
        }, responseListener, responseListener);
        return responseListener;
    }

    public ProviderListener getImServerHosts() {
        Map<String,String> tokenMap = ApiTokenUtil.getToken(mContext);
        String token = tokenMap.get("token");
        School school = SchoolUtil.getDefaultSchool(mContext);
        RequestUrl requestUrl = new RequestUrl(school.host + "/api/im/me/login");
        HashMap<String, String> params = getPlatformInfo();
        params.put("tag", "mobile");
        requestUrl.setParams(params);
        requestUrl.getHeads().put("Auth-Token", token);

        final ProviderListener<LinkedHashMap> stringResponseListener = new ProviderListener<LinkedHashMap>(){};
        ProviderListener<LinkedHashMap> responseListener = new ProviderListener<LinkedHashMap>(){
        };

        responseListener.success(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap hashMap) {
                if (hashMap == null || !hashMap.containsKey("servers")) {
                    return;
                }
                LinkedHashMap hostList = (LinkedHashMap) hashMap.get("servers");
                stringResponseListener.onResponse(hostList);
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                stringResponseListener.onErrorResponse(obj);
            }
        });

        addPostRequest(requestUrl, new TypeToken<LinkedHashMap>() {
        }, responseListener, responseListener);
        return stringResponseListener;
    }

    public HashMap<String, String> getPlatformInfo() {
        HashMap<String, String> params = new HashMap<String, String>();
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        params.put("deviceToken", telephonyManager.getDeviceId());
        params.put("desiceName", "Android " + Build.MODEL);
        params.put("deviceVersion", Build.VERSION.SDK);
        params.put("deviceKernel", Build.VERSION.RELEASE);

        return params;
    }
}
