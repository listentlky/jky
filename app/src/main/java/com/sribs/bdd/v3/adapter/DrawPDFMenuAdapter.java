package com.sribs.bdd.v3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sribs.bdd.R;
import com.sribs.bdd.v3.module.DrawPDFMenuModule;
import com.sribs.bdd.v3.util.LogUtils;

import java.util.List;

/**
 * create time: 2022/9/15
 * author: bruce
 * description:
 */
public class DrawPDFMenuAdapter extends BaseAdapter {

    private Context mContext;

    private List<DrawPDFMenuModule> mData;

    private int mSelect = -1;

    private boolean mChecked;

    public DrawPDFMenuAdapter(Context mContext, List<DrawPDFMenuModule> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public void notifyChecked(int position){
        this.mChecked = false;
        if(mItemClickListener != null){
            mItemClickListener.onItemClick(mSelect,mChecked);
        }
        this.mSelect = position;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.draw_pdf_menu_item, parent, false);
            vh = new ViewHolder();
            vh.root = convertView.findViewById(R.id.root);
            vh.text = convertView.findViewById(R.id.text);
            vh.img = convertView.findViewById(R.id.image);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        DrawPDFMenuModule drawPDFMenuModule = mData.get(position);
        vh.text.setText(drawPDFMenuModule.getText());
        vh.img.setBackgroundResource(drawPDFMenuModule.getIcon());

      /*  if(!mChecked){
            vh.root.setEnabled(true);
            vh.root.setBackgroundResource(R.drawable.draw_menu_item_no_bg);
        }else {
            if (mSelect != -1 && mSelect == position) {
                vh.root.setBackgroundResource(R.drawable.draw_menu_item_yes_bg);
            }else {
                vh.root.setEnabled(false);
            }
        }*/

        if (mSelect != -1 && mSelect == position) {
            if(mChecked){
                vh.root.setBackgroundResource(R.drawable.draw_menu_item_yes_bg);
            }else {
                vh.root.setBackgroundResource(R.drawable.draw_menu_item_no_bg);
            }
        } else {
            vh.root.setBackgroundResource(R.drawable.draw_menu_item_no_bg);
        }

        if(mItemClickListener != null){
            vh.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mChecked = true;
                    mSelect = position;
                    notifyDataSetChanged();
                    mItemClickListener.onItemClick(position,mChecked);
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        private LinearLayout root;
        private ImageView img;
        private TextView text;
    }

    private ItemClickListener mItemClickListener;

    public void setItemClickListener(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface ItemClickListener {

        void onItemClick(int position,boolean checked);

    }
}
