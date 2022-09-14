package com.sribs.bdd.bean.data.recycler

import android.view.View
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatCheckedTextView
import com.afollestad.recyclical.ViewHolder
import com.sribs.bdd.R

class FloorCopyDrawingViewHolder(itemView: View) : ViewHolder(itemView) {
//    val cbxItemCtv: AppCompatCheckBox = itemView.findViewById(R.id.item_ctv)
    val tvId: TextView = itemView.findViewById(R.id.tv_id)
    val tvName: TextView = itemView.findViewById(R.id.tv_name)
    val ctvFloor: AppCompatCheckedTextView = itemView.findViewById(R.id.ctv_floor)
}