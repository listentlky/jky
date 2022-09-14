package com.sribs.common.ui.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @date 2021/6/25
 * @author elijah
 * @Description
 */
class CommonLinearDividerItemDecoration(var spaceH: Int, var spaceV: Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        var manager = parent.layoutManager as LinearLayoutManager

//        LOG.I("123","pos=${parent.getChildAdapterPosition(view) % span} span=$span  $spaceH")

        outRect.left = spaceH
        outRect.right = spaceH
        outRect.top = spaceV
    }
}