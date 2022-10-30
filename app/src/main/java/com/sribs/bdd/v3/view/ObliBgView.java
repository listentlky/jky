package com.sribs.bdd.v3.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

public class ObliBgView extends View {

    private Paint bgPaint;

    private Paint centerPaint;

    public ObliBgView(Context context) {
        super(context);
        init();
    }

    public ObliBgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ObliBgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        bgPaint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));

        centerPaint = new Paint();
        centerPaint.setAntiAlias(true);
        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setColor(Color.parseColor("#FF005B82"));

        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.parseColor("#FF005B82"));

        canvas.drawCircle(getWidth()/2,getHeight()/2,getWidth()/2,bgPaint);

        canvas.drawCircle(getWidth()/2,getHeight()/2,15,centerPaint);
    }
}
