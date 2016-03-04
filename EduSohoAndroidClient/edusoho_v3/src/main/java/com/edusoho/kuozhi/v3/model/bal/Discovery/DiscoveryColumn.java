package com.edusoho.kuozhi.v3.model.bal.Discovery;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by su on 2016/2/24.
 */
public class DiscoveryColumn {

    public int id;
    public String title;
    public String type;
    public int categoryId;
    public String orderType;
    public int showCount;
    public String seq;
    public String createTime;
    public String updateTime;

    public List<DiscoveryCardProperty> data;

    public void setDiscoveryCardProperty(DiscoveryCardProperty[] discoveryCardProperties) {
        data = new ArrayList<>();
        for (DiscoveryCardProperty discoveryCardProperty : discoveryCardProperties) {
            data.add(discoveryCardProperty);
        }
        try {
            if (discoveryCardProperties.length % 2 != 0) {
                if (discoveryCardProperties[0] instanceof DiscoveryCourse) {
                    data.add(new DiscoveryCourse(true));
                } else if (discoveryCardProperties[0] instanceof DiscoveryClassroom) {
                    data.add(new DiscoveryClassroom(true));
                }
            }
        } catch (Exception ex) {
            Log.d("DiscoveryColumn", "setDiscoveryCardProperty: " + ex.getMessage());
        }
    }
}
