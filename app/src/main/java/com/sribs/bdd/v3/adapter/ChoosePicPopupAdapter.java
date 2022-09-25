package com.sribs.bdd.v3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sribs.bdd.R;
import com.sribs.bdd.v3.util.LogUtils;
import com.sribs.common.bean.db.DrawingV3Bean;

import java.util.List;

public class ChoosePicPopupAdapter  extends RecyclerView.Adapter<ChoosePicPopupAdapter.ItemViewHolder> {

    private Context mContext;

    private List<DrawingV3Bean> datas;

    private int select = 0;

    private ChoosePicPopupAdapter.ItemClickCallback mItemClickCallback;

    public ChoosePicPopupAdapter(Context mContext, List<DrawingV3Bean> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    public ChoosePicPopupAdapter setmItemClickCallback(ChoosePicPopupAdapter.ItemClickCallback mItemClickCallback) {
        this.mItemClickCallback = mItemClickCallback;
        return this;
    }

    @NonNull
    @Override
    public ChoosePicPopupAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChoosePicPopupAdapter.ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.popup_choose_pic_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChoosePicPopupAdapter.ItemViewHolder holder, int position) {
        if (holder instanceof ChoosePicPopupAdapter.ItemViewHolder) {
            DrawingV3Bean drawingV3Bean = datas.get(position);
            holder.text.setText(drawingV3Bean.getFileName());
            if(select != -1 && select == position){
                holder.choose.setBackgroundResource(R.drawable.circle_blue800);
                holder.text.setSelected(true);
            }else {
                holder.choose.setBackgroundResource(R.drawable.circle_gray);
                holder.text.setSelected(false);
            }
            if (mItemClickCallback != null) {
                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        select = position;
                        mItemClickCallback.onItemClick(drawingV3Bean);
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
        void onItemClick(DrawingV3Bean drawingV3Bean);
    }
}
