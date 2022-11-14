package com.sribs.bdd.v3.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sribs.bdd.R;
import com.sribs.bdd.v3.util.LogUtils;


public class DrawAndTextView extends RelativeLayout {

    private DrawView mDrawView;

    private Context mContext;

    //初始的旋转角度
    private float oriRotation = 0;

    private String mContent;
    private TextView mMarkView;
    private int mTextViewHeight = 0;
    private int mDrawViewHeight = 0;
    private int mDrawViewWidth = 0;
    private TextView mTopTextView;
    private float mZoom = 1.0f;

    public DrawAndTextView(Context context) {
        super(context);
        this.mContext = context;

    }

    public DrawAndTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

    }

    public DrawAndTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public float getOriRotation() {
        return oriRotation;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public void setTextViewHeight(int viewHeight) {
        this.mTextViewHeight = viewHeight;
    }

    public void resetView(float angle) {
        removeAllViews();
        this.oriRotation = angle;
        init();
        setRotation(oriRotation);
        mMarkView.setText(mContent);
    }

    public void setZoom(float zoom){
        this.mZoom = zoom;
    }

    public void init() {

        mDrawView = new DrawView(mContext);
        mDrawView.setZoom(mZoom);
        mDrawViewWidth = (int)(30*mZoom);
        LayoutParams drawParams = new LayoutParams(mDrawViewWidth == 0 ? 30 : mDrawViewWidth, mDrawViewHeight == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : mDrawViewHeight);
        drawParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mDrawView, drawParams);

        mMarkView = new TextView(mContext);
        mMarkView.setText(mContent);
        mMarkView.setTextColor(Color.BLACK);
        mMarkView.setTextSize(getResources().getDimensionPixelSize(R.dimen._2ssp)*mZoom);
        mMarkView.setLines(1);
        mMarkView.setBackgroundResource(R.drawable.retancgle_drawable);
        mMarkView.setGravity(Gravity.CENTER);
        LayoutParams textParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textParams.topMargin = getHeight()==0?mTextViewHeight / 4:getHeight()/4;
        addView(mMarkView, textParams);

    }

    public void setMarkView(String text){

        if(mMarkView != null){
            mMarkView.setVisibility(GONE);
          /*  mMarkView.setText(text);
            LayoutParams textParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

            textParams.topMargin = getHeight()==0?mTextViewHeight / 3:getHeight()/3;
            mMarkView.setTextColor(Color.parseColor("#FF005B82"));

            mMarkView.setBackground(getTextBgDrawableRes());
            mMarkView.setLayoutParams(textParams);
            mMarkView.setRotation(-getRotation());*/
        }
    }

    private GradientDrawable getTextBgDrawableRes(){

        GradientDrawable drawable = new GradientDrawable();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            int padding = (int) (getResources().getDimensionPixelSize(R.dimen._2sdp)*mZoom);
            drawable.setPadding(padding,padding/2,padding,padding/2);
        }
        drawable.setStroke((int) (2*mZoom), Color.parseColor("#FF005B82"));

        drawable.setColor(Color.parseColor("#80FFFFFF"));

        return drawable;
    }


    private void move(MotionEvent event) {
        Point center = new Point(getWidth() / 2, getHeight() / 2);
        Point first = new Point((int) startX, (int) startY);
        Point second = new Point((int) event.getX(), (int) event.getY());
        float startAngle = angle(center, first, second);
        if (Math.abs(startAngle) > 10) {
            if(startAngle<0){
                oriRotation -= 15;
            }else {
                oriRotation += 15;
            }
            setRotation(oriRotation);
        }
    }

    public void addTopView(String text) {
        LayoutParams textParams2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        int margin = getHeight()==0?mTextViewHeight / 4:getHeight()/4;
        textParams2.topMargin = margin;

        mTopTextView = new TextView(mContext);
        mTopTextView.setTextColor(Color.parseColor("#FF005B82"));
        mTopTextView.setText(text);
        mTopTextView.setTextSize(getResources().getDimensionPixelSize(R.dimen._2sdp)*mZoom);

        mTopTextView.setBackground(getTextBgDrawableRes());
    //    mTopTextView.setBackgroundResource(R.drawable.retancgle_blue_drawable);
        mTopTextView.setGravity(Gravity.CENTER);
        mTopTextView.setRotation(-getRotation());
        addView(mTopTextView, textParams2);
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
        boolean isClockwise = ((first.x - cen.x) * (second.y - cen.y) - (first.y - cen.y) * (second.x - cen.x) > 0);

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

    private float startX, startY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                move(event);
                break;
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;
        }
        return true;
    }


}





