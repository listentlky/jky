package com.sribs.bdd.v3.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sribs.bdd.R;
import com.sribs.bdd.v3.adapter.ChoosePicPopupAdapter;
import com.sribs.bdd.v3.adapter.OneChoosePopupAdapter;
import com.sribs.common.bean.db.DrawingV3Bean;

import java.util.ArrayList;
import java.util.List;

public class ChoosePicPopupWindow extends android.widget.PopupWindow {

    private RecyclerView mRecyclerView;
    private Context mContext;

    private DrawingV3Bean mDrawingV3Bean = null;

    private ChoosePicPopupAdapter mChoosePicPopupAdapter;

    private ChoosePicPopupWindow.PopupCallback mPopupCallback;

    public ChoosePicPopupWindow(Context context, int width, List<DrawingV3Bean> list, ChoosePicPopupWindow.PopupCallback popupCallback) {
        super(context);
        this.mContext = context;
        this.mPopupCallback = popupCallback;
        View view = LayoutInflater.from(mContext).inflate(R.layout.popup_choose_pic_layout,null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        setContentView(view);
        setBackgroundDrawable(null);
        setOutsideTouchable(true);
        setWidth(width);
        if(list.size()>0) {
            mDrawingV3Bean = list.get(0);
        }

        mRecyclerView = (RecyclerView)view.findViewById(R.id.popup_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mChoosePicPopupAdapter = new ChoosePicPopupAdapter(mContext,list).setmItemClickCallback(new ChoosePicPopupAdapter.ItemClickCallback() {
            @Override
            public void onItemClick(DrawingV3Bean drawingV3Bean) {
                mDrawingV3Bean = drawingV3Bean;
            }
        });
        mRecyclerView.setAdapter(mChoosePicPopupAdapter);
        view.findViewById(R.id.popup_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        view.findViewById(R.id.popup_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPopupCallback != null){
                    mPopupCallback.onSelect(mDrawingV3Bean);
                }
                dismiss();
            }
        });
    }

    public void notifyAdapter(){
        if(mChoosePicPopupAdapter != null){
            mChoosePicPopupAdapter.notifyDataSetChanged();
        }
    }

    public interface PopupCallback{
        void onSelect(DrawingV3Bean drawingV3Bean);
    }
}

