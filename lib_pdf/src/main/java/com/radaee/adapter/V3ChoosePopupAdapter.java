package com.radaee.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
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

    private int color = 0;

    private int mGravity = Gravity.CENTER;

    private V3ChoosePopupAdapter.ItemClickCallback mItemClickCallback;

    public V3ChoosePopupAdapter(Context mContext, List<String> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    public void setGravity(int mGravity) {
        this.mGravity = mGravity;
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
            holder.layout.setGravity(mGravity);
            if(mGravity == Gravity.CENTER_VERTICAL){
                holder.layout.setPadding(dp2px(mContext,10),0,0,0);
            }
            if (select != -1 && select == position) {
                holder.text.setSelected(true);
               /* switch (s){
                    case "层高":
                        holder.choose.setBackgroundResource(R.drawable.circle_green);
                        break;
                    case "轴网":
                        holder.choose.setBackgroundResource(R.drawable.circle_orange);
                        break;
                    case "点位":
                        holder.choose.setBackgroundResource(R.drawable.circle_red);
                        break;
                    case "梁":
                        holder.choose.setBackgroundResource(R.drawable.circle_yellow);
                        break;
                    case "柱":
                        holder.choose.setBackgroundResource(R.drawable.circle_blue);
                        break;
                    case "墙":
                        holder.choose.setBackgroundResource(R.drawable.circle_pink);
                        break;
                    case "板":
                        holder.choose.setBackgroundResource(R.drawable.circle_indigo);
                        break;
                }*/
                holder.choose.setBackgroundResource(R.drawable.circle_blue800);
            } else {
                holder.choose.setBackgroundResource(R.drawable.circle_gray);
                holder.text.setSelected(false);
            }
            /*switch (s){
                case "层高":
                    color = 65408;
                    break;
                case "轴网":
                    color = 16742912;
                    break;
                case "点位":
                    color = 16711680;
                    break;
                case "梁":
                    color = 16776960;
                    break;
                case "柱":
                    color = 255;
                    break;
                case "墙":
                    color = 16711935;
                    break;
                case "板":
                    color = 16751359;
                    break;
            }*/
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

    /**
     * dp 2 px
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    public interface ItemClickCallback {
        void onItemClick(int position,int color);
    }
}
