package com.sribs.bdd.ui.building;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sribs.bdd.R;


class ItemViewHolder extends RecyclerView.ViewHolder {

    final View rootView;
    final TextView tvRefItem;
    final TextView tvDescItem;

    ItemViewHolder(@NonNull View view) {
        super(view);

        rootView = view;
        tvRefItem = view.findViewById(R.id.tv_ref);
        tvDescItem = view.findViewById(R.id.tv_desc);
    }
}
