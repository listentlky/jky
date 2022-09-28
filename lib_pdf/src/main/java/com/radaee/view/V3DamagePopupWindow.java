package com.radaee.view;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.radaee.adapter.V3ChoosePopupAdapter;
import com.radaee.viewlib.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create time: 2022/9/20
 * author: bruce
 * description:
 */
public class V3DamagePopupWindow extends android.widget.PopupWindow {

    private RecyclerView mRecyclerView;
    private Context mContext;

    private int mChoosePosition = 0;

    private int mChooseColor = 0;

    private V3ChoosePopupAdapter mChoosePicPopupAdapter;

    private V3DamagePopupWindow.PopupCallback mPopupCallback;

    private List<String> datas = new ArrayList<>();

    public V3DamagePopupWindow(Context context, int width, List<String> list, V3DamagePopupWindow.PopupCallback popupCallback) {
        super(context);
        this.mContext = context;
        this.mPopupCallback = popupCallback;
        if(list != null){
            this.datas.addAll(list);
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.v3_popup_layout,null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        setContentView(view);
        setBackgroundDrawable(null);
        setOutsideTouchable(true);
        setWidth(width);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.popup_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mChoosePicPopupAdapter = new V3ChoosePopupAdapter(mContext,datas).setmItemClickCallback(new V3ChoosePopupAdapter.ItemClickCallback() {
            @Override
            public void onItemClick(int position,int color) {
                mChoosePosition = position;
                mChooseColor = color;
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
                    mPopupCallback.onSelect(mChoosePosition,mChooseColor);
                }
                dismiss();
            }
        });
    }

    public void initFirstColor(int color){
        this.mChooseColor = color;
    }

    public interface PopupCallback{
        void onSelect(int position,int color);
    }
}
