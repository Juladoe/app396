package com.edusoho.liveplayer.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;

import com.pili.pldroid.player.widget.PLVideoView;

/**
 * Created by suju on 16/10/24.
 */
public class LiveVideoView extends PLVideoView {

    public LiveVideoView(android.content.Context context) {
        super(context);
    }

    public LiveVideoView(android.content.Context context, android.util.AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public LiveVideoView(android.content.Context context, android.util.AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override
    public void pause() {
        super.pause();
        clearDraw();
    }

    public void clearDraw(){
        Canvas canvas = null;
        try{
            canvas = getSurfaceView().getHolder().lockCanvas(null);
            if (canvas == null) {
                getSurfaceView().setBackgroundColor(Color.BLACK);
            } else {
                canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
            }
        }catch(Exception e){
            Log.d("LiveVideoView", "clear error");
        }finally{
            if(canvas != null){
                getSurfaceView().getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }
}
