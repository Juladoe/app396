package com.edusoho.kuozhi.v3.listener;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

/**
 * Created by howzhi on 15/11/2.
 */
public interface LessonPluginCallback {

    public void initPlugin(BaseAdapter adapter, int postion);

    public boolean click(AdapterView<?> parent, View view, int position);
}
