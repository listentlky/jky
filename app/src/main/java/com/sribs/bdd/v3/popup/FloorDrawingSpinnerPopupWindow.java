package com.sribs.bdd.v3.popup;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.sribs.bdd.R;
import com.sribs.bdd.v3.module.FloorDrawingModule;
import com.sribs.bdd.v3.view.FloorDrawingSpinnerView;
import com.sribs.common.bean.db.DamageV3Bean;
import com.sribs.common.bean.db.DrawingV3Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * create time: 2022/9/20
 * author: bruce
 * description:
 */
public class FloorDrawingSpinnerPopupWindow extends PopupWindow {

    private FloorDrawingSpinnerView mFloorDrawingSpinnerView;

    private DrawingV3Bean mSelectData;

    public FloorDrawingSpinnerPopupWindow(Context context, int width,
                                          List<FloorDrawingModule> data,
                                          FloorDrawItemClickCallback callback) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.floor_drawing_spinner,null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        setContentView(view);
        setBackgroundDrawable(null);
        setOutsideTouchable(true);
        setWidth(width);

        mFloorDrawingSpinnerView = view.findViewById(R.id.floor_drawing_spinner_view);
        view.findViewById(R.id.popup_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        view.findViewById(R.id.popup_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectData == null){
                    if(data != null && data.size()>0 && data.get(0).getMNameList() !=null && data.get(0).getMNameList().size()>0){
                        mSelectData = data.get(0).getMNameList().get(0);
                    }
                }
                if(mSelectData == null){
                    Toast.makeText(context,"未选择图纸",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(callback != null){
                    callback.onClick(mSelectData);
                }
                dismiss();
            }
        });
        mFloorDrawingSpinnerView.setData(data).setCallback(new FloorDrawItemClickCallback() {
            @Override
            public void onClick(DrawingV3Bean data) {
                mSelectData = data;
            }
        }).build();
    }

    public interface FloorDrawItemClickCallback{
        void onClick(DrawingV3Bean data);
    }
}
