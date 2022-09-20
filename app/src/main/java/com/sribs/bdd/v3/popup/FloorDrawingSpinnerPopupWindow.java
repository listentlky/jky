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

import java.util.ArrayList;
import java.util.List;

/**
 * create time: 2022/9/20
 * author: bruce
 * description:
 */
public class FloorDrawingSpinnerPopupWindow extends PopupWindow {

    private FloorDrawingSpinnerView mFloorDrawingSpinnerView;

    private List<FloorDrawingModule> mData = new ArrayList<>();

    private String mName,mPath;

    public FloorDrawingSpinnerPopupWindow(Context context, int width,
                                          List<FloorDrawingModule> data,
                                          FloorDrawItemClickCallback callback) {
        super(context);
        if(data != null){
            mData.addAll(data);
        }

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
                if(TextUtils.isEmpty(mPath) || TextUtils.isEmpty(mName)){
                    Toast.makeText(context,"未选择图纸",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(callback != null){
                    callback.onClick(mPath,mName);
                }
                dismiss();
            }
        });
        mFloorDrawingSpinnerView.setData(mData).setCallback(new FloorDrawItemClickCallback() {
            @Override
            public void onClick(String path, String name) {
                mName = name;
                mPath = path;
            }
        }).build();
    }

    public interface FloorDrawItemClickCallback{
        void onClick(String path,String name);
    }
}
