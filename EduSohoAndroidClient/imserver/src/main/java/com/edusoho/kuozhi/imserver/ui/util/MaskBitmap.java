package com.edusoho.kuozhi.imserver.ui.util;

import android.graphics.Bitmap;

/**
 * Created by suju on 16/9/5.
 */
public class MaskBitmap {

    public boolean isMask;
    public Bitmap target;

    public MaskBitmap(Bitmap target) {
        this.target = target;
    }
}
