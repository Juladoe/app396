package com.edusoho.kuozhi.model;

import android.graphics.drawable.Drawable;

/**
 * Created by onewoman on 2014/12/3.
 */
public class LessioningItemData {
    public Drawable itemImg;
    public String itemTitle;
    public String itemContent;
    public String itemProgress;

    public LessioningItemData(Drawable itemImg, String itemTitle, String itemContent, String itemProgress){
        this.itemImg = itemImg;
        this.itemTitle = itemTitle;
        this.itemContent = itemContent;
        this.itemProgress = itemProgress;
    }
}
