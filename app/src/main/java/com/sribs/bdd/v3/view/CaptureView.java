package com.sribs.bdd.v3.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sribs.bdd.R;



/**
 * author : lulingfeng
 * e-mail : lingfeng.lu@xipu.com
 * date   : 2022/10/1816:44
 * desc   :
 * version: 1.0
 */
public class CaptureView extends RelativeLayout {

    private DrawView mDrawView;
    private DrawView mDrawView2;
    private TextView mTextView;
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

    public CaptureView(Context context) {
        super(context);
        this.mContext = context;
    }

    public CaptureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public CaptureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    public CaptureView build() {
        removeAllViews();
        init();
        return this;
    }

    private void init() {

    }


    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }



}
