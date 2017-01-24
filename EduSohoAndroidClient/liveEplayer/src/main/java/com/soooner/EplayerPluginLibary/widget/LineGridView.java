package com.soooner.EplayerPluginLibary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import com.soooner.EplayerPluginLibary.R;
import com.soooner.EplayerPluginLibary.util.LogUtil;

/**
 * Created by zhaoxu2014 on 14-11-6.
 */
public class LineGridView extends GridView {
    public LineGridView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public LineGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public LineGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    int numColumns=5;


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        LogUtil.d("LineGridView2", "  width :" + widthMeasureSpec + " height:" + heightMeasureSpec);
    }

    @Override
    protected void dispatchDraw(Canvas canvas){
        super.dispatchDraw(canvas);

        View localView1 = getChildAt(0);
        if (null != localView1) {
           // int column = getWidth() / localView1.getWidth();
            int childCount = getChildCount();
            int row=childCount%numColumns==0?childCount/numColumns:childCount/numColumns+1;

            Paint localPaint;
            localPaint = new Paint();
            localPaint.setStyle(Paint.Style.STROKE);
            localPaint.setColor(getContext().getResources().getColor(R.color.grid_line_bg));

            for (int i = 0; i < row; i++) {
                View cellView = getChildAt(i*numColumns);

                canvas.drawLine(cellView.getLeft(), cellView.getBottom(), getWidth(), cellView.getBottom(), localPaint);

            }
        }

//        for(int i = 0;i < childCount;i++){
//            View cellView = getChildAt(i);
//            if((i + 1) % column == 0){
//                canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
//            }else if((i + 1) > (childCount - (childCount % column))){
//                canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
//            }else{
//                canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
//                canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
//            }
//        }
//        if(childCount % column != 0){
//            for(int j = 0 ;j < (column-childCount % column) ; j++){
//                View lastView = getChildAt(childCount - 1);
//                canvas.drawLine(lastView.getRight() + lastView.getWidth() * j, lastView.getTop(), lastView.getRight() + lastView.getWidth()* j, lastView.getBottom(), localPaint);
//            }
//        }
    }
}