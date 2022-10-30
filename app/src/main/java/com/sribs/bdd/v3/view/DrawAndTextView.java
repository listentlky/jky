package com.sribs.bdd.v3.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sribs.bdd.R;
import com.sribs.bdd.v3.util.LogUtils;


public class DrawAndTextView extends RelativeLayout {

    private static int height = 30;
    private static int bottom = 10;

    private DrawView mDrawView;
    private DrawView2 mDrawView2;

    private int mode;
    private final int NONE = -1;
    private final int SCALE = 1;
    private float oldDist = 1;

    private int nOldLeft;
    private int nOldTop;
    private int nOldRight;
    private int nOldBottom;
    private int points;
    private float rotation = 0;

    private float newDist;
    private Paint mPaint;

    private int lastX;
    private int lastY;
    private int oriLeft, oriRight, oriTop, oriBottom;
    private Context mContext;
    private String mTopText;
    private float currentRotation;
    private Canvas mCanvas;


    //初始的旋转角度
    private float oriRotation = 0;

    private int mRotate = 0;

    @SuppressLint("ResourceAsColor")
    private Paint bgPaint = new Paint() {
        {
            setColor(R.color.blue_800);
            setAntiAlias(true);
            setStrokeWidth(4.0f);
            setStyle(Style.FILL);
        }
    };
    private Activity mActivity;
    private String mContent;
    private TextView mMarkView;
    private TextView mDefaultMarkView;
    private int mType = -1;
    private int mTextViewHeight = 0;
    private int mDrawViewHeight = 0;
    private int mDrawViewWidth = 0;
    private int mTextSize = -1;
    private boolean mIsShowTopText = false;
    private boolean isFirstDraw;
    private TextView mTopTextView;
    private float defaultRotation = 0;
    private float mAngle = 0;


    public DrawAndTextView(Context context) {
        super(context);
        this.mContext = context;
        //   init();
    }

    public DrawAndTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        //   init();
    }

    public DrawAndTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        //    init();
    }

    public float getOriRotation() {
        return oriRotation;
    }

    public void setIsShowTopText(boolean mIsShowTopText) {
        this.mIsShowTopText = mIsShowTopText;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public void setDrawViewType(int type) {
        this.mType = type;
    }

    public void setTextViewHeight(int viewHeight) {
        this.mTextViewHeight = viewHeight;
    }

    public void setDrawViewHeight(int mDrawViewHeight) {
        this.mDrawViewHeight = mDrawViewHeight;
    }

    public void setOriRotation(float oriRotation) {
        this.oriRotation = oriRotation;
    }

    public void setDrawViewWidth(int mDrawViewWidth) {
        this.mDrawViewWidth = mDrawViewWidth;
    }

    public void setTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
    }


    public DrawView getDrawView() {
        return mDrawView;
    }


    public DrawView2 getDrawView2() {
        return mDrawView2;
    }

    public TextView getMarkTextView() {
        return mMarkView;
    }
    public void resetView(float angle){
        removeAllViews();
        if (mType==-1){
            setOriRotation(angle);
            init();

        }else {
            setOriRotation(angle);
            setRotation(angle);
            init();
        }
        invalidate();
    }
    public void init() {
        if (mType == -1) {
            mDrawView = new DrawView(mContext);
            mDrawView.setTopText(mTopText);
            LayoutParams drawParams = new LayoutParams(mDrawViewWidth == 0 ? 30 : mDrawViewWidth, mDrawViewHeight == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : mDrawViewHeight);
            drawParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            addView(mDrawView, drawParams);
        } else {
            mDrawView2 = new DrawView2(mContext);
            mDrawView2.setTopText(mTopText);
            LayoutParams drawParams = new LayoutParams(mDrawViewWidth == 0 ? 30 : mDrawViewWidth, mDrawViewHeight == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : mDrawViewHeight);
            drawParams.addRule(RelativeLayout.CENTER_IN_PARENT);

            addView(mDrawView2, drawParams);
        }


        mMarkView = new TextView(mContext);
        mMarkView.setText(mContent);
        mMarkView.setTextColor(Color.BLACK);
        mMarkView.setTextSize(mTextSize == -1 ? 10 : mTextSize);
        mMarkView.setBackgroundResource(R.drawable.retancgle_drawable);
        mMarkView.setGravity(Gravity.CENTER);
        LayoutParams textParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textParams.topMargin = mTextViewHeight / 4- mMarkView.getHeight();
        addView(mMarkView, textParams);

    }


    public void addMarkView(float angle) {
        angle -= 90;
        float radian = (float) Math.toRadians(angle);

        float x = (float) (250 + Math.cos(radian) * 250);

        float y = (float) (250 + Math.sin(radian) * 250);

        removeView(mMarkView);

        mMarkView = new TextView(mContext);
        mMarkView.setText(mContent);
        mMarkView.setTextColor(Color.BLACK);
        mMarkView.setTextSize(mTextSize == -1 ? 10 : mTextSize);
        mMarkView.setBackgroundResource(R.drawable.retancgle_blue_drawable);
        mMarkView.setGravity(Gravity.CENTER);
        LayoutParams textParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mMarkView, textParams);
        if (x >= 250) {
            mMarkView.setTranslationX((x - 250) / 2);
        } else {
            mMarkView.setTranslationX(-(250 - x) / 2);
        }

        if (y >= 250) {
            mMarkView.setTranslationY((y - 250) / 3);

        } else {
            mMarkView.setTranslationY(-(250 - y) / 3);

        }

        mMarkView.setRotation(angle + 90);

    }

    public void setTopText(String text) {
        this.mTopText = text;
    }

    private float getAngle(float xTouch, float yTouch) {
        double x = xTouch - getWidth()/2;
        double y = yTouch - getHeight()/2;
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    private void move(MotionEvent event) {
    //    Point center = new Point(oriLeft / 2 + (oriRight - oriLeft) / 2, oriTop / 2 + (oriBottom - oriTop) / 2);
        Point center = new Point(getWidth()/2, getHeight()/2);
        Point first = new Point((int) startX, (int) startY);
        Point second = new Point((int) event.getX(), (int) event.getY());


   //     float endAngle = getAngle(event.getX(),event.getY());


   //     LogUtils.INSTANCE.d("angle： "+startAngle+" ; "+endAngle);

        float startAngle = angle(center, first, second);
        if(Math.abs(startAngle)>1) {
            oriRotation +=startAngle;
            setRotation(oriRotation);
        }


        LogUtils.INSTANCE.d("oriRotation1： "+angle(center, first, second));

        LogUtils.INSTANCE.d("oriRotation2： "+oriRotation);

     /*   Float rotate = (oriRotation % 360);
        if (oriRotation > 0) {
            //    mRotate = rotate;
        } else {
            //  mRotate = Math.abs(rotate - 360);
        }
        Log.d("bruce", "oriRotation-----------" + oriRotation);
        //   Log.d("bruce", "rotate"+rotate);*/

    }

    public Canvas getCanvas() {
        return mCanvas;
    }

    public void addTopView(float angle,boolean isNeedRotation) {
        if (isNeedRotation){
            angle -= 90;
        }else {
            angle -= 90;
        }


        float radian = (float) Math.toRadians(angle);

        float x = (float) (250 + Math.cos(radian) * 250);

        float y = (float) (250 + Math.sin(radian) * 250);
        LayoutParams textParams2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams2.addRule(ALIGN_PARENT_LEFT);
        mTopTextView = new TextView(mContext);
        mTopTextView.setTextColor(Color.BLACK);
        mTopTextView.setText(mTopText);
        mTopTextView.setTextSize(getResources().getDimensionPixelSize(R.dimen._6sdp));
        mTopTextView.setBackgroundResource(R.drawable.retancgle_blue_drawable);
        mTopTextView.setGravity(Gravity.CENTER);


        if (x>425){
            mTopTextView.setTranslationX(x-150);
        }else if (x>150){
            mTopTextView.setTranslationX(x-75);
        }   else{
            mTopTextView.setTranslationX(x);
        }
        if (y>425){
            mTopTextView.setTranslationY(y-50);
        }else{
            mTopTextView.setTranslationY(y);

        }

            addView(mTopTextView, textParams2);
    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
 /*       canvas.drawRect(0,0,getWidth(),getHeight(), bgPaint);
        canvas.drawCircle(getWidth()/2-rect.width(),1,getWidth()/2+rect.width(),rect.height()*2+5,rectPaint);
        canvas.drawText(mTopText, getWidth()/2,rect.height()*2, textPaint);*/
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

        // - (double)((first.y - cen.y) * (second.x - cen.x)))
        // 根据两向量的叉乘来判断顺逆时针
        boolean isClockwise = ((first.x - cen.x) * (second.y - cen.y) -(first.y - cen.y) * (second.x - cen.x)  > 0);
     //   boolean isClockwise = (first.x <second.x || first.y <second.y);


        LogUtils.INSTANCE.d("isClockwise： "+isClockwise);


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


    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    private float startX,startY,endX,endY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                move(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = SCALE;
                oldDist = spacing(event);
                nOldLeft = getLeft();
                nOldTop = getTop();
                nOldRight = getRight();
                nOldBottom = getBottom();
                break;
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();

                lastY = (int) event.getRawY();
                lastX = (int) event.getRawX();
                oriLeft = getLeft();
                oriRight = getRight();
                oriTop = getTop();
                oriBottom = getBottom();
                lastY = (int) event.getRawY();
                lastX = (int) event.getRawX();
                break;
            case MotionEvent.ACTION_UP:
                endX = event.getX();
                endY = event.getY();
                mode = NONE;
                break;
        }
        return true;
        //return super.onTouchEvent(event);
    }

    // 触碰两点间距离
    private float spacing(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.abs(Math.sqrt(x * x + y * y));
        } else {
            return newDist;
        }
    }


}





