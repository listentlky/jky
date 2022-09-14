package com.sribs.bdd.bean.data.recycler

import android.view.View
import android.widget.TextView
import com.afollestad.recyclical.ViewHolder
import com.sribs.bdd.R

class FloorDrawingViewHolder(itemView: View) : ViewHolder(itemView) {
    val tvId: TextView = itemView.findViewById(R.id.floor_id)
    val tvDrawings: TextView = itemView.findViewById(R.id.tv_drawings)
    val tvUploadDrawing: TextView = itemView.findViewById(R.id.tv_upload_drawing)
    val tvFloorCopy: TextView = itemView.findViewById(R.id.tv_floor_copy)
}