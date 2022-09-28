package com.sribs.bdd.v3.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sribs.bdd.R;
import com.sribs.bdd.v3.bean.CheckRHDChoosePointBean;
import com.sribs.bdd.v3.popup.ChooseRHDPointPopupWindow;
import com.sribs.common.bean.db.RelativeHDiffPointBean;

import java.util.List;

/**
 * create time: 2022/9/27
 * author: bruce
 * description:
 */
public class ChooseRHDPointAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private List<RelativeHDiffPointBean> datas;

    private ViewGroup parent;

    private ChooseRHDPointPopupWindow.PopupCallback mPopupCallback;

    public ChooseRHDPointAdapter(Context context, List<RelativeHDiffPointBean> datas,ChooseRHDPointPopupWindow.PopupCallback mPopupCallback) {
        this.context = context;
        this.datas = datas;
        this.mPopupCallback = mPopupCallback;
    }


    @Override
    public int getItemCount() {
        return datas != null ?datas.size():0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        if(viewType == 0){
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.popup_item,parent,false));
        }else {
            return new ViewMenuHolder(LayoutInflater.from(context).inflate(R.layout.chooserhd_point_down_item,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RelativeHDiffPointBean relativeHDiffPointBean = datas.get(position);
        if(holder instanceof ViewHolder){
            ((ViewHolder) holder).item.setText(relativeHDiffPointBean.getName());

            ((ViewHolder) holder).item.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            return true;
                        case MotionEvent.ACTION_UP:
                            if(mPopupCallback != null){
                                mPopupCallback.onSelect(relativeHDiffPointBean.getName());
                            }
                            break;
                    }
                    return false;
                }
            });

        }else if(holder instanceof ViewMenuHolder){
            ((ViewMenuHolder) holder).menuText.setText(relativeHDiffPointBean.getName());
            ((ViewMenuHolder) holder).menuArrow.setRotation(-90);
            ((ViewMenuHolder) holder).itemLayout.setVisibility(View.GONE);

            for (RelativeHDiffPointBean.Item item:relativeHDiffPointBean.getMenu()) {
                View view = LayoutInflater.from(context).inflate(R.layout.popup_item,parent,false);
                TextView tv = view.findViewById(R.id.popup_item_text);
                tv.setText(item.getName());

                tv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                return true;
                            case MotionEvent.ACTION_UP:
                                if(mPopupCallback != null){
                                    mPopupCallback.onSelect(relativeHDiffPointBean.getName()+"-"+item.getName());
                                }
                                break;
                        }
                        return false;
                    }
                });
                ((ViewMenuHolder) holder).itemLayout.addView(tv);
            }

            ((ViewMenuHolder) holder).menuLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            return true;
                        case MotionEvent.ACTION_UP:
                            if(((ViewMenuHolder) holder).itemLayout.getVisibility() == View.VISIBLE){
                                ((ViewMenuHolder) holder).itemLayout.setVisibility(View.GONE);
                                ((ViewMenuHolder) holder).menuArrow.setRotation(-90);
                            }else {
                                ((ViewMenuHolder) holder).itemLayout.setVisibility(View.VISIBLE);
                                ((ViewMenuHolder) holder).menuArrow.setRotation(0);
                            }
                            break;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return 0;
        }else {
            return 1;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.popup_item_text);
        }
    }

    class ViewMenuHolder extends RecyclerView.ViewHolder{

        private LinearLayout menuLayout;

        private ImageView menuArrow;

        private TextView menuText;

        private LinearLayout itemLayout;

        public ViewMenuHolder(@NonNull View itemView) {
            super(itemView);
            menuLayout = itemView.findViewById(R.id.spinner_layout);
            menuArrow = itemView.findViewById(R.id.spinner_img);
            menuText = itemView.findViewById(R.id.spinner_name);
            itemLayout = itemView.findViewById(R.id.item_layout);
        }
    }
}
