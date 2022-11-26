package com.sribs.bdd.v3.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sribs.bdd.R;
import com.sribs.bdd.v3.module.FloorDrawingModule;
import com.sribs.bdd.v3.popup.FloorDrawingSpinnerPopupWindow;
import com.sribs.bdd.v3.util.LogUtils;
import com.sribs.common.bean.db.DrawingV3Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * create time: 2022/9/20
 * author: bruce
 * description:
 */
public class FloorDrawingSpinnerView extends LinearLayout {

    private Context mContext;

    private List<FloorDrawingModule> mData;

    private FloorDrawingSpinnerPopupWindow.FloorDrawItemClickCallback mFloorDrawItemClickCallback;

    public FloorDrawingSpinnerView(Context context) {
        super(context);
        init(context);
    }

    public FloorDrawingSpinnerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FloorDrawingSpinnerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setOrientation(VERTICAL);
    }

    public FloorDrawingSpinnerView setData(List<FloorDrawingModule> mData) {
        this.mData = mData;
        return this;
    }

    public FloorDrawingSpinnerView setCallback(FloorDrawingSpinnerPopupWindow.FloorDrawItemClickCallback mFloorDrawItemClickCallback) {
        this.mFloorDrawItemClickCallback = mFloorDrawItemClickCallback;
        return this;
    }

    private List<View> mView = new ArrayList<>();

    private List<TextView> mTextViews = new ArrayList<>();

    private LinearLayout.LayoutParams layoutParams;

    public void build() {
        removeAllViews();
        mView.clear();
        mTextViews.clear();
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mContext.getResources().getDimensionPixelSize(R.dimen._20sdp));

        for (int i = 0; i < mData.size(); i++) {

            LinearLayout mSpinnerLayout = new LinearLayout(mContext);
            mSpinnerLayout.setOrientation(VERTICAL);
            mSpinnerLayout.setGravity(Gravity.CENTER_HORIZONTAL);

            FloorDrawingModule menuData = mData.get(i);

            View menuLayout = View.inflate(mContext, R.layout.floor_drawing_spinner_item, null);
            mView.add(menuLayout);
            TextView menuName = menuLayout.findViewById(R.id.spinner_name);
            mTextViews.add(menuName);
            ImageView menuImg = menuLayout.findViewById(R.id.spinner_img);
            menuName.setText(menuData.getMFloorName());
            if (i > 0) {
                mSpinnerLayout.setVisibility(GONE);
                menuImg.setRotation(-90);
            }

            menuLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetSpinnerItemBg();
                    menuLayout.setBackgroundColor(Color.parseColor("#FF005B82"));
                    menuName.setTextColor(Color.WHITE);
                    if (mSpinnerLayout.getVisibility() == VISIBLE) {
                        menuImg.setRotation(-90);
                        mSpinnerLayout.setVisibility(GONE);
                    } else {
                        menuImg.setRotation(0);
                        mSpinnerLayout.setVisibility(VISIBLE);
                    }
                    if (mFloorDrawItemClickCallback != null) {
                        mFloorDrawItemClickCallback.onClick(null);
                    }
                }
            });

            addView(menuLayout, layoutParams);

            for (int k = 0; k < menuData.getMNameList().size(); k++) {
                DrawingV3Bean drawingV3Bean = menuData.getMNameList().get(k);
                drawingV3Bean.setFloorName(menuData.getMFloorName());
                View mSpinnerItemView = View.inflate(mContext, R.layout.floor_drawing_spinner_child_item, null);
                TextView spinnerItem = mSpinnerItemView.findViewById(R.id.spinner_item_name);
                spinnerItem.setText(drawingV3Bean.getFileName());
                mTextViews.add(spinnerItem);
                mView.add(mSpinnerItemView);
                mSpinnerItemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetSpinnerItemBg();
                        mSpinnerItemView.setBackgroundColor(Color.parseColor("#FF005B82"));
                        spinnerItem.setTextColor(Color.WHITE);
                        if (mFloorDrawItemClickCallback != null) {
                            mFloorDrawItemClickCallback.onClick(drawingV3Bean);
                        }
                    }
                });
                mSpinnerLayout.addView(mSpinnerItemView, layoutParams);
                if (k == 0) { // 默认打开第一个
                    LogUtils.INSTANCE.d("默认设置第一个");
                    mSpinnerItemView.setBackgroundColor(Color.parseColor("#FF005B82"));
                    spinnerItem.setTextColor(Color.WHITE);
                }
            }

            addView(mSpinnerLayout);
        }
    }

    private void resetSpinnerItemBg() {
        for (View view : mView) {
            view.setBackgroundColor(Color.WHITE);
        }
        for (TextView view : mTextViews) {
            view.setTextColor(Color.parseColor("#FF323334"));
        }
    }
}

