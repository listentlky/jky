package com.sribs.bdd.v3.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.sribs.bdd.R;
import com.sribs.bdd.v3.util.LogUtils;


public class DrawView extends View {

    private static int mArrowHeight = 0;
    private static int mArrowBottom = 0;

    private Context mContext;

    private Paint mPaint;

    private float mZoom = 1.0f;

    public DrawView(Context context) {
        super(context);
        init(context);
    }

    public DrawView(Context context,  AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.mContext = context;
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#FF005B82"));
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(context.getResources().getDimensionPixelOffset(R.dimen._1sdp));

        mArrowHeight = context.getResources().getDimensionPixelOffset(R.dimen._6sdp);
        mArrowBottom = context.getResources().getDimensionPixelOffset(R.dimen._2sdp);

    }

    private float mLineEndX,mLineEndY, mLineArrowEndX, mLineArrowEndY;

    private float mAngle=0;

    public void setAngle(float mAngle) {
        this.mAngle = mAngle;
        invalidate();
    }

    public void setZoom(float zoom){
        this.mZoom = zoom;
        mPaint.setStrokeWidth(mContext.getResources().getDimensionPixelOffset(R.dimen._1sdp)*zoom);
        mArrowHeight = (int) (mContext.getResources().getDimensionPixelOffset(R.dimen._6sdp)*zoom);
        mArrowBottom = (int) (mContext.getResources().getDimensionPixelOffset(R.dimen._2sdp)*zoom);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Log.e("bruce", "????????????view "+mAngle );

        Path path = new Path();

        if(mAngle==0){
            mLineEndX = getWidth()/2;
            mLineEndY = 0;

            mLineArrowEndX =  getWidth()/2 ;
            mLineArrowEndY = 0;

        }else {

            mAngle-=90;

            float radian = (float) Math.toRadians(mAngle);

            float linexr = getWidth()/2;

            float lineyr = getHeight()/2;

            float tranglexr = getWidth()/2;

            float trangleyr = getHeight()/2;

            mLineEndX = (float) (linexr + Math.cos(radian)* linexr);

            mLineEndY = (float)(lineyr + Math.sin(radian)* lineyr);

            mLineArrowEndX = (float) (tranglexr+ Math.cos(radian)*tranglexr);

            mLineArrowEndY = (float)(trangleyr + Math.sin(radian)*trangleyr);

        }

        canvas.drawLine(getWidth()/2, getHeight()/2, mLineEndX, mLineEndY, mPaint);

        drawArrow(canvas, mPaint, getWidth()/2, getHeight()/2, mLineArrowEndX, mLineArrowEndY, mArrowHeight, mArrowBottom);

    }
    
    /**
     * ????????????
     * @param canvas
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @param height
     * @param bottom
     */
    private void drawArrow(Canvas canvas, Paint paintLine, float fromX, float fromY, float toX, float toY, int height, int bottom){
        try{

            float juli = (float) Math.sqrt((toX - fromX) * (toX - fromX)
                    + (toY - fromY) * (toY - fromY));// ??????????????????
            float juliX = toX - fromX;// ??????????????????????????????
            float juliY = toY - fromY;// ??????????????????????????????
            float dianX = toX - (height / juli * juliX);
            float dianY = toY - (height / juli * juliY);
            float dian2X = fromX + (height / juli * juliX);
            float dian2Y = fromY + (height / juli * juliY);
            //???????????????
            Path path = new Path();
            path.moveTo(toX, toY);// ???????????????????????????
            path.lineTo(dianX + (bottom / juli * juliY), dianY
                    - bottom / juli * juliX);
            path.lineTo(dianX - (bottom / juli * juliY), dianY
                    + (bottom / juli * juliX));
            path.close(); // ????????????????????????????????????
            canvas.drawPath(path, paintLine);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}

