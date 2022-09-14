package com.sribs.bdd.bean.data.recycler

import android.view.View
import android.widget.TextView
import com.afollestad.recyclical.ViewHolder
import com.sribs.bdd.R

class FacadeDrawingViewHolder(itemView: View) : ViewHolder(itemView) {
    val tvFacade: TextView = itemView.findViewById(R.id.tv_facade)
    val tvDrawings: TextView = itemView.findViewById(R.id.tv_drawings)
}