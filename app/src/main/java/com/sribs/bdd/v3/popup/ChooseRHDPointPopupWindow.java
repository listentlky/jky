package com.sribs.bdd.v3.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sribs.bdd.R;
import com.sribs.bdd.v3.adapter.ChooseRHDPointAdapter;
import com.sribs.bdd.v3.bean.CheckRHDChoosePointBean;
import com.sribs.common.bean.db.RelativeHDiffPointBean;

import java.util.ArrayList;
import java.util.List;

/**
 * create time: 2022/9/27
 * author: bruce
 * description:
 */
public class ChooseRHDPointPopupWindow extends PopupWindow {

    private RecyclerView mRecyclerView;

    private ChooseRHDPointAdapter mChooseRHDPointAdapter;

    private List<RelativeHDiffPointBean> datas;

    public ChooseRHDPointPopupWindow(Context context,int width, List<RelativeHDiffPointBean> list,
                                     PopupCallback popupCallback) {
        super(context);
        this.datas = list;
        View view = LayoutInflater.from(context).inflate(R.layout.popup_layout,null);
        view.setBackgroundResource(R.mipmap.popup_left_portrait_bg);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width,
                width*2);
        view.setLayoutParams(layoutParams);
        setContentView(view);
        setBackgroundDrawable(null);
        setOutsideTouchable(true);
        setWidth(width);
        setHeight(width*2);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.popup_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mChooseRHDPointAdapter = new ChooseRHDPointAdapter(context, datas, new ChooseRHDPointPopupWindow.PopupCallback() {
            @Override
            public void onSelect(String text) {
                if(popupCallback != null){
                    popupCallback.onSelect(text);
                }
                dismiss();
            }
        });
        mRecyclerView.setAdapter(mChooseRHDPointAdapter);
    }

    public void notifyAdapter(){
        mChooseRHDPointAdapter.notifyDataSetChanged();
    }

    public interface PopupCallback{
        void onSelect(String text);
    }
}
