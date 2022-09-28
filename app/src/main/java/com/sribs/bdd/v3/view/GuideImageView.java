package com.sribs.bdd.v3.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.sribs.bdd.v3.util.LogUtils;

/**
 * create time: 2022/9/26
 * author: bruce
 * description:
 */
public class GuideImageView extends androidx.appcompat.widget.AppCompatImageView {

    private int lastX;
    private int lastY;
    private int oriLeft,oriRight,oriTop,oriBottom;

    //初始的旋转角度
    private float oriRotation = 0;

    private String mGuide = "北";

    private int mRotate = 0;

    public GuideImageView(Context context) {
        super(context);

    }

    public GuideImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public GuideImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
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
            case MotionEvent.ACTION_MOVE:
                Point center = new Point(oriLeft+(oriRight-oriLeft)/2,oriTop+(oriBottom-oriTop)/2);
                Point first = new Point(lastX,lastY);
                Point second = new Point((int) event.getRawX(),(int) event.getRawY());
                oriRotation += angle(center,first,second);
                int rotate = (int) Math.abs(oriRotation % 360);
                if(oriRotation>0){
                    mRotate = rotate;
                    if(rotate == 0){
                        mGuide = "北";
                        mRotate = 0;
                    }else if(rotate>0 && rotate<90){
                        mGuide = "东北"+rotate+"°";
                    }else if(rotate==90){
                        mGuide = "东";
                    }else if(rotate>90 && rotate<180){
                        mGuide = "东南"+rotate+"°";
                    }else if(rotate==180){
                        mGuide = "南";
                    }else if(rotate>180 && rotate<270){
                        mGuide = "西南"+(180-Math.abs(rotate-180))+"°";
                    }else if(rotate == 270){
                        mGuide = "西";
                    }else if(rotate > 270 && rotate<360){
                        mGuide = "西北"+(90-Math.abs(rotate-270))+"°";
                    }
                }else {
                    mRotate = Math.abs(rotate-360);
                    rotate = Math.abs(rotate);
                    if(rotate == 0){
                        mGuide = "北";
                        mRotate = 0;
                    }else if(rotate>0 && rotate<90){
                        mGuide = "西北"+rotate+"°";
                    }else if(rotate==90){
                        mGuide = "西";
                    }else if(rotate>90 && rotate<180){
                        mGuide = "西南"+rotate+"°";
                    }else if(rotate==180){
                        mGuide = "南";
                    }else if(rotate>180 && rotate<270){
                        mGuide = "东南"+(180-Math.abs(rotate-180))+"°";
                    }else if(rotate == 270){
                        mGuide = "东";
                    }else if(rotate > 270 && rotate<360){
                        mGuide = "东北"+(90-Math.abs(rotate-270))+"°";
                    }
                }
                LogUtils.INSTANCE.d("mGuide: "+mGuide+" ; mRotate: "+mRotate);
                if(mGuideCallback != null){
                    mGuideCallback.onChange(mGuide,mRotate);
                }
                setRotation(oriRotation);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
        }
        return true;
    }

    public float angle(Point cen, Point first, Point second) {
        float dx1, dx2, dy1, dy2;
        dx1 = first.x - cen.x;
        dy1 = first.y - cen.y;
        dx2 = second.x - cen.x;
        dy2 = second.y - cen.y;

        // 计算三边的平方
        float ab2 = (second.x - first.x) * (second.x - first.x) + (second.y - first.y) * (second.y - first.y);
        float oa2 = dx1*dx1 + dy1*dy1;
        float ob2 = dx2 * dx2 + dy2 *dy2;

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

    private GuideCallback mGuideCallback;

    public void setCallback(GuideCallback mGuideCallback) {
        this.mGuideCallback = mGuideCallback;
    }

    public interface GuideCallback{
        void onChange(String text,int rotate);
    }
}
