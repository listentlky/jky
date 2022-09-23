package com.sribs.bdd.v3.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sribs.bdd.R;

import java.util.List;

/**
 * create time: 2022/9/8
 * author: bruce
 * description:
 */
public class OneChoosePopupAdapter extends RecyclerView.Adapter<OneChoosePopupAdapter.ItemViewHolder> {

    private Context mContext;

    private List<String> datas;

    private int select = 0;

    private int mTextGravity = Gravity.CENTER;

    private int mTextLeftMargin = 0;

    private ItemClickCallback mItemClickCallback;

    public OneChoosePopupAdapter(Context mContext, List<String> datas) {
        this.mContext = mContext;
        this.datas = datas;
        this.select = -1;
    }

    public OneChoosePopupAdapter(Context mContext, List<String> datas,int defaultSelect,int mTextGravity) {
        this.mContext = mContext;
        this.datas = datas;
        this.select = defaultSelect;
        this.mTextGravity = mTextGravity;
    }

    public OneChoosePopupAdapter(Context mContext, List<String> datas,int defaultSelect,int mTextGravity,ItemClickCallback mItemClickCallback) {
        this.mContext = mContext;
        this.datas = datas;
        this.select = defaultSelect;
        this.mTextGravity = mTextGravity;
        this.mItemClickCallback = mItemClickCallback;
    }

    public OneChoosePopupAdapter(Context mContext, List<String> datas,int defaultSelect,
                                 int mTextGravity,int textLeftMargin,ItemClickCallback mItemClickCallback) {
        this.mContext = mContext;
        this.datas = datas;
        this.select = defaultSelect;
        this.mTextGravity = mTextGravity;
        this.mTextLeftMargin = textLeftMargin;
        this.mItemClickCallback = mItemClickCallback;
    }

    public OneChoosePopupAdapter setItemClickCallback(ItemClickCallback mItemClickCallback) {
        this.mItemClickCallback = mItemClickCallback;
        return this;
    }

     public void setSelect(int position){
        this.select = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.popup_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            String s = datas.get(position);
            holder.text.setText(s);
            if(mTextLeftMargin != 0){
                holder.text.setPadding(mTextLeftMargin,0,0,0);
            }
            holder.text.setGravity(mTextGravity);
            if(select != -1 && select == position){
                holder.text.setSelected(true);
            }else {
                holder.text.setSelected(false);
            }
            if (mItemClickCallback != null) {
                holder.text.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                            case MotionEvent.ACTION_UP:
                                select = position;
                                holder.text.setSelected(true);
                                notifyDataSetChanged();
                                mItemClickCallback.onItemClick(s);
                                break;
                        }
                        return false;
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
        private TextView text;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.popup_item_text);
        }
    }

   public interface ItemClickCallback {
        void onItemClick(String text);
    }
}
