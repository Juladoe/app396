package com.edusoho.kuozhi.model.m3u8;

import java.util.ArrayList;

/**
 * Created by howzhi on 14/12/10.
 */
public class M3U8File {
    public static int STREAM_LIST = 1;
    public static int PLAY_LIST = 2;
    public static int STREAM = 3;

    public int type;
    public boolean allowCache;
    public int targetDuration;

    public String content;

    public ArrayList<String> keyList;
    public ArrayList<String> urlList;
    public ArrayList<M3U8ListItem> m3u8List;

    public M3U8File()
    {
        this.keyList = new ArrayList<String>();
        this.urlList = new ArrayList<String>();
    }
}
