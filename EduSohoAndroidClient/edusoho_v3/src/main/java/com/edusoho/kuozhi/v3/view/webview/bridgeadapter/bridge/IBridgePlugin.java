package com.edusoho.kuozhi.v3.view.webview.bridgeadapter.bridge;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by su on 2015/12/9.
 */
public interface IBridgePlugin {

    public String getName();

    public void initialize(BridgePluginContext pluginContext);

    public Object executeAnsy(String action, JSONArray args);

    public boolean execute(
            String action, JSONArray args, BridgeCallback callbackContext) throws JSONException;
}
