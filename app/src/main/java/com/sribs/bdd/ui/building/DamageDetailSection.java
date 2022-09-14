package com.sribs.bdd.ui.building;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sribs.bdd.R;
import com.sribs.bdd.bean.data.DamageSectionDetail;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class DamageDetailSection extends Section {

    private final String title;
    private final List<DamageSectionDetail> list;
    private final ClickListener clickListener;

    private boolean expanded = true;

    DamageDetailSection(@NonNull final String title, final List<DamageSectionDetail> list,
                        @NonNull final ClickListener clickListener) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_content_view)
                .headerResourceId(R.layout.section_damage_header)
                .build());

        this.title = title;
        this.list = list;
        this.clickListener = clickListener;
//        Log.i("leon", "DamageDetailSection title=" + this.title);
//        if(list == null)
//            Log.i("leon", "DamageDetailSection list is null");
//        else{
//            Log.i("leon", "DamageDetailSection list length=" + list.size());
//        }
    }

    @Override
    public int getContentItemsTotal() {
        int size = 0;
        if(list == null) {
            size = 0;
//            Log.i("leon", "this section title="+title+", list is null");
        }
        else
            size = list.size();
//        Log.i("leon", "this section expanded="+expanded+",size="+size);
        if(expanded)
            return size;
        else
            return 0;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(final View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder itemHolder = (ItemViewHolder) holder;

        DamageSectionDetail dmgDetail = list.get(position);

//        itemHolder.tvRefItem.setText(dmgDetail.getDamageRef());
        itemHolder.tvDescItem.setText(dmgDetail.getDamageDesc());

        itemHolder.rootView.setOnClickListener(v ->
                clickListener.onItemRootViewClicked(this, itemHolder.getAdapterPosition())
        );
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
//        Log.i("leon", "DamageDetailSection onBindHeaderViewHolder title="+title);
        headerHolder.tvTitle.setText(title);
        headerHolder.imgArrow.setImageResource(
                expanded ? R.mipmap.icon_shouqi_w : R.mipmap.icon_xiala_w
        );

        headerHolder.rootView.setOnClickListener(v -> clickListener.onHeaderRootViewClicked(this));
    }

    boolean isExpanded() {
        return expanded;
    }

    void setExpanded(final boolean expanded) {
        this.expanded = expanded;
    }

    interface ClickListener {

        void onHeaderRootViewClicked(@NonNull final DamageDetailSection section);

        void onItemRootViewClicked(@NonNull final DamageDetailSection section, final int itemAdapterPosition);
    }

    public List<DamageSectionDetail> getDamageDetailList(){
        return list;
    }

    public String getTitle(){
        return title;
    }
}
