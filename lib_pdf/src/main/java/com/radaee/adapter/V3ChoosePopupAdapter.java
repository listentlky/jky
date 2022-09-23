package com.radaee.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.radaee.viewlib.R;

import java.util.List;

/**
 * create time: 2022/9/20
 * author: bruce
 * description:
 */
public class V3ChoosePopupAdapter  extends RecyclerView.Adapter<V3ChoosePopupAdapter.ItemViewHolder> {

    private Context mContext;

    private List<String> datas;

    private int select = 0;

    private int color = 16742912;

    private V3ChoosePopupAdapter.ItemClickCallback mItemClickCallback;

    public V3ChoosePopupAdapter(Context mContext, List<String> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    public V3ChoosePopupAdapter setmItemClickCallback(V3ChoosePopupAdapter.ItemClickCallback mItemClickCallback) {
        this.mItemClickCallback = mItemClickCallback;
        return this;
    }

    @NonNull
    @Override
    public V3ChoosePopupAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new V3ChoosePopupAdapter.ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.v3_popup_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull V3ChoosePopupAdapter.ItemViewHolder holder, int position) {
        if (holder instanceof V3ChoosePopupAdapter.ItemViewHolder) {
            String s = datas.get(position);
            holder.text.setText(s);
            if (select != -1 && select == position) {
                holder.choose.setSelected(true);
                holder.text.setSelected(true);
            } else {
                holder.choose.setSelected(false);
                holder.text.setSelected(false);
            }
            switch (s){
                case "轴网":
                    holder.choose.setBackgroundResource(R.drawable.circle_orange);
                    color = 16742912;
                    break;
                case "层高":
                    holder.choose.setBackgroundResource(R.drawable.circle_green);
                    color = 65408;
                    break;
            }
            if (mItemClickCallback != null) {
                int finalColor = color;
                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        select = position;
                        mItemClickCallback.onItemClick(position, finalColor);
                        notifyDataSetChanged();
                    }
                });
            }
        }
    }
    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layout;
        private ImageView choose;
        private TextView text;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.popup_choose_pic_item_layout);
            choose = itemView.findViewById(R.id.popup_choose);
            text = itemView.findViewById(R.id.popup_item_text);
        }
    }

    public interface ItemClickCallback {
        void onItemClick(int position,int color);
    }
}
