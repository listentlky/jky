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
import com.sribs.bdd.v3.module.FloorDrawingModule;

import java.util.ArrayList;
import java.util.List;

/**
 * create time: 2022/9/20
 * author: bruce
 * description:
 */
class FloorDrawingSpinnerAdapter extends RecyclerView.Adapter<FloorDrawingSpinnerAdapter.FloorDrawingViewHolder> {

    private List<FloorDrawingModule> mData = new ArrayList<>();

    private Context mContext;

    public FloorDrawingSpinnerAdapter(List<FloorDrawingModule> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public FloorDrawingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FloorDrawingViewHolder(LayoutInflater.from(mContext).inflate(R.layout.floor_drawing_spinner_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FloorDrawingViewHolder holder, int position) {
        if(holder instanceof FloorDrawingViewHolder){
            FloorDrawingModule floorDrawingModule = mData.get(position);
            holder.mSpinnerText.setText(floorDrawingModule.getMMenuName());
            holder.mChooseSpinner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.mSpinnerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData != null?mData.size():0;
    }

    class FloorDrawingViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout mSpinnerLayout;

        private ImageView mChooseSpinner;

        private TextView mSpinnerText;

        public FloorDrawingViewHolder(@NonNull View itemView) {
            super(itemView);
         //   mSpinnerLayout = itemView.findViewById(R.id.spinner_layout);
            mChooseSpinner = itemView.findViewById(R.id.spinner_img);
            mSpinnerText = itemView.findViewById(R.id.spinner_name);
        }
    }
}
