package com.edusoho.kuozhi.model.m3u8;

/**
 * Created by howzhi on 14/12/10.
 */
public class M3U8DbModle {

    public int id;
    public int finish;
    public String host;
    public int lessonId;
    public int totalNum;
    public int downloadNum;
    public String playList;

    @Override
    public String toString() {
        return "M3U8DbModle{" +
                "id=" + id +
                ", finish=" + finish +
                ", host='" + host + '\'' +
                ", lessonId=" + lessonId +
                ", totalNum=" + totalNum +
                ", downloadNum=" + downloadNum +
                ", playList='" + playList + '\'' +
                '}';
    }
}
