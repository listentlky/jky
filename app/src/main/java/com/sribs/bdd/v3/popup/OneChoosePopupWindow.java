package com.sribs.bdd.v3.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sribs.bdd.R;
import com.sribs.bdd.v3.adapter.OneChoosePopupAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * create time: 2022/9/8
 * author: bruce
 * description:
 */
public class OneChoosePopupWindow extends android.widget.PopupWindow {

    private RecyclerView mRecyclerView;

    private Context mContext;

    private OneChoosePopupAdapter mOneChoosePopupAdapter;

    private List<String> datas = new ArrayList<>();

    public OneChoosePopupWindow(Context context, int width, List<String> list,int defaultSelect,
                                int mTextGravity,int textLeftMargin,PopupCallback popupCallback) {
        super(context);
        this.mContext = context;
        if(list != null){
            this.datas.addAll(list);
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.popup_layout,null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        setContentView(view);
        setBackgroundDrawable(null);
        setOutsideTouchable(true);
        setWidth(width);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.popup_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mOneChoosePopupAdapter = new OneChoosePopupAdapter(mContext, datas, defaultSelect, mTextGravity,textLeftMargin, new OneChoosePopupAdapter.ItemClickCallback() {
            @Override
            public void onItemClick(String text) {
                if(popupCallback != null){
                    popupCallback.onSelect(text);
                }
                dismiss();
            }
        });
        mRecyclerView.setAdapter(mOneChoosePopupAdapter);
    }

    public void setSelect(int position){
       if(mOneChoosePopupAdapter != null){
           mOneChoosePopupAdapter.setSelect(position);
       }
    }

   public interface PopupCallback{
        void onSelect(String text);
    }
}
