package com.sribs.bdd.v3.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.stetho.common.LogUtil;
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


    //初始的旋转角度
    private float oriRotation = 0;

    private int mRotate = 0;

    private Paint paint = new Paint() {
        {
            setColor(Color.RED);
            setAntiAlias(true);
            setStrokeWidth(4.0f);
        }
    };
    private Activity mActivity;
    private String mContent;
    private TextView mTextView;
    private DrawView2 drawView2;
    private int mType= -1;
    private int mViewHeight= -1;


    public DrawAndTextView(Context context) {
        super(context);
        this.mContext = context;
     //   init();
    }
    public DrawAndTextView(Context context,  AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
     //   init();
    }

    public DrawAndTextView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    //    init();
    }


    public void setContent(String content) {
        this.mContent = content;
    }
    public void setDrawViewType(int type){
        this.mType =type;
    }
    public void setViewHeight(int viewHeight){
        this.mViewHeight =viewHeight;
    }


    public void setOriRotation(float oriRotation) {
        this.oriRotation = oriRotation;
    }

    public void init() {
        if (mType==-1){
            mDrawView = new DrawView(mContext);
            LayoutParams drawParams = new LayoutParams(30, ViewGroup.LayoutParams.WRAP_CONTENT);
            drawParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        
            addView(mDrawView,drawParams);
        }else {
            mDrawView2 = new DrawView2(mContext);
            LayoutParams drawParams = new LayoutParams(30, ViewGroup.LayoutParams.WRAP_CONTENT);
            drawParams.addRule(RelativeLayout.CENTER_IN_PARENT);

            addView(mDrawView2,drawParams);
        }


        mTextView = new TextView(mContext);
        mTextView.setText(mContent);
        mTextView.setTextSize(10);
        mTextView.setBackgroundResource(R.drawable.retancgle_drawable);
        mTextView.setGravity(Gravity.CENTER);
        LayoutParams textParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textParams.topMargin = mViewHeight/4;
        addView(mTextView,textParams);

    }


    private void move(MotionEvent event) {
        Point center = new Point(oriLeft/2 + (oriRight - oriLeft) / 2, oriTop/2 + (oriBottom - oriTop) / 2);
        Point first = new Point(lastX, lastY);
        Point second = new Point((int) event.getRawX(), (int) event.getRawY());

        oriRotation +=angle(center,first,second);

        Float rotate =  (oriRotation % 360);
        if (oriRotation > 0) {
        //    mRotate = rotate;
        } else {
          //  mRotate = Math.abs(rotate - 360);
        }
        Log.d("bruce", "oriRotation-----------"+oriRotation);
     //   Log.d("bruce", "rotate"+rotate);
        setRotation(oriRotation);


        lastX = (int) event.getRawX();
        lastY = (int) event.getRawY();
    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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




    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

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

