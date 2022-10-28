package com.sribs.bdd.v3.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.sribs.bdd.R;
import com.sribs.bdd.v3.util.LogUtils;

public class DrawView extends View {

    private static int mTrangleheight = 30;
    private static int mTranglebottom = 10;

    private final int NONE = -1;
    private final int SCALE = 1;

    private float lastX;
    private float lastY;
    private int oriLeft, oriRight, oriTop, oriBottom;
    private String mTopText;
    private boolean isFirstDraw =true;


    //初始的旋转角度
    private float currentOriRotation = 0;

    private int mRotate = 0;
    private Context mContext;

    @SuppressLint("ResourceAsColor")
    private Paint paint = new Paint(){
        {
            setColor(Color.parseColor("#FF005B82"));
            setAntiAlias(true);
            setStrokeWidth(4.0f);
        }
    };

    private Paint textPaint = new Paint(){
        {
            setAntiAlias(true);
            setTextSize(20);
            setStrokeWidth(20);
            setTextAlign(Align.CENTER);
        }
    };

    private Paint rectPaint = new Paint(){
        {
            setStyle(Style.STROKE);
            setColor(Color.parseColor("#FF005B82"));
            setStrokeJoin(Paint.Join.ROUND);
        }
    };

    private Paint rectPaint2 = new Paint(){
        {
            setStyle(Paint.Style.FILL);
            setColor(Color.WHITE);
            setStrokeJoin(Paint.Join.ROUND);
        }
    };

    private boolean mIsShowTopText=false;
    private boolean mIsResetTopTextLocation=false;




    public DrawView(Context context) {
        super(context);
        mContext= context;
    }

    public DrawView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    public void setTopText(String topText){
        this.mTopText =topText;
}

    private float mLineEndX,mLineEndY,mLineTrangleEndX,mLineTrangleEndY;
    private float mLineDefaultX,mLineDefault2;

    private float mAngle=0;

    public void setAngle(float mAngle) {
        this.mAngle = mAngle;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Log.e("bruce", "开始绘制view " );

        if(mAngle==0 ){
            mLineEndX = getWidth()/2;
            mLineEndY = 0;

            mLineTrangleEndX =  getWidth()/2 ;
            mLineTrangleEndY = 0;
        }else {

            mAngle-=90;

            float radian = (float) Math.toRadians(mAngle);

            float linexr = getWidth()/2;

            float lineyr = getHeight()/2;

            float tranglexr = getWidth()/2;

            float trangleyr = getHeight()/2;

            LogUtils.INSTANCE.d("linexr： "+linexr);
            LogUtils.INSTANCE.d("lineyr： "+lineyr);
            LogUtils.INSTANCE.d("tranglexr： "+tranglexr);
            LogUtils.INSTANCE.d("trangleyr： "+trangleyr);

            mLineEndX = (float) (linexr + Math.cos(radian)* linexr);

            mLineEndY = (float)(lineyr + Math.sin(radian)* lineyr);

            LogUtils.INSTANCE.d("mLineEndX： "+mLineEndX);
            LogUtils.INSTANCE.d("mLineEndY： "+mLineEndY);

            mLineTrangleEndX = (float) (tranglexr+ Math.cos(radian)*tranglexr);

            mLineTrangleEndY = (float)(trangleyr + Math.sin(radian)*trangleyr);
            LogUtils.INSTANCE.d("mLineTrangleEndX： "+mLineTrangleEndX);
            LogUtils.INSTANCE.d("mLineTrangleEndY： "+mLineTrangleEndY);


            Log.e("bruce", "onDraw:角度 "+mAngle );
            Log.e("bruce", "onDraw:宽高 "+getWidth()+"//"+getHeight() );


        }

        canvas.drawLine(getWidth()/2, getHeight()/2, mLineEndX, mLineEndY, paint);
        drawTrangle(canvas, paint, getWidth()/2, getHeight()/2, mLineTrangleEndX, mLineTrangleEndY, mTrangleheight, mTranglebottom);
        //   drawTrangle(canvas, paint, getWidth()/2, getHeight()/2,  mLineTrangleEndX, mLineTrangleEndY, mTrangleheight, mTranglebottom);
    //    canvas.drawLine(getWidth()/2, getHeight()/2, getWidth()/2, 0, paint);

    }



    public float[] getDefaultLocation(){
        return new float[]{getWidth()/2,0};
    }

    public void setIsShowTopText(boolean mIsShowTopText) {
        this.mIsShowTopText = mIsShowTopText;
    }

    public void resetView(float x, float y){
        Log.e("bruce", "重绘制view " );
        mIsShowTopText = true;
        this.lastX = x;
        this.lastY = y;
    //    invalidate();
    }



    /**
     * 绘制三角
     * @param canvas
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @param height
     * @param bottom
     */
    private void drawTrangle(Canvas canvas, Paint paintLine, float fromX, float fromY, float toX, float toY, int height, int bottom){
        try{
            float juli = (float) Math.sqrt((toX - fromX) * (toX - fromX)
                    + (toY - fromY) * (toY - fromY));// 获取线段距离
            float juliX = toX - fromX;// 有正负，不要取绝对值
            float juliY = toY - fromY;// 有正负，不要取绝对值
            float dianX = toX - (height / juli * juliX);
            float dianY = toY - (height / juli * juliY);
            float dian2X = fromX + (height / juli * juliX);
            float dian2Y = fromY + (height / juli * juliY);
            //终点的箭头
            Path path = new Path();
            path.moveTo(toX, toY);// 此点为三边形的起点
            path.lineTo(dianX + (bottom / juli * juliY), dianY
                    - (bottom / juli * juliX));
            path.lineTo(dianX - (bottom / juli * juliY), dianY
                    + (bottom / juli * juliX));
            path.close(); // 使这些点构成封闭的三边形
            canvas.drawPath(path, paintLine);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }




    public float angle(Point cen, Point first, Point second) {
        float dx1, dx2, dy1, dy2;
        dx1 = first.x - cen.x;
        dy1 = first.y - cen.y;
        dx2 = second.x - cen.x;
        dy2 = second.y - cen.y;

        // 计算三边的平方
        float ab2 = (second.x - first.x) * (second.x - first.x) + (second.y - first.y) * (second.y - first.y);
        float oa2 = dx1 * dx1 + dy1 * dy1;
        float ob2 = dx2 * dx2 + dy2 * dy2;

        // 根据两向量的叉乘来判断顺逆时针
        boolean isClockwise = ((first.x - cen.x) * (second.y - cen.y) - (first.y - cen.y) * (second.x - cen.x)) > 0;

        // 根据余弦定理计算旋转角的余弦值
        double cosDegree = (oa2 + ob2 - ab2) / (2 * Math.sqrt(oa2) * Math.sqrt(ob2));

        // 异常处理，因为算出来会有误差绝对值可能会超过一
        if (cosDegree > 1) {
            cosDegree = 1;
        } else if (cosDegree < -1) {
            cosDegree = -1;
        }

        // 计算弧度
        double radian = Math.acos(cosDegree);

        // 计算旋转过的角度，顺时针为正，逆时针为负
        return (float) (isClockwise ? Math.toDegrees(radian) : -Math.toDegrees(radian));

    }

  /*  @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                move(event);
                break;
            case MotionEvent.ACTION_DOWN:
                lastY = (int) event.getRawY();
                lastX = (int) event.getRawX();
                oriLeft = getLeft();
                oriRight = getRight();
                oriTop = getTop();
                oriBottom = getBottom();
                lastY = (int) event.getRawY();
                lastX = (int) event.getRawX();
                break;
        }
        return true;
        //return super.onTouchEvent(event);
    }*/





}

