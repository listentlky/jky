package com.sribs.common.ui.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @date 2021/6/25
 * @author elijah
 * @Description
 */
class CommonGridDividerItemDecoration(var spaceH: Int, var spaceV: Int): RecyclerView.ItemDecoration() {

    private var mEnablePadding = true

    constructor(spaceH: Int, spaceV: Int,enablePadding:Boolean):this(spaceH,spaceV){
        mEnablePadding = enablePadding
    }




    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        var manager = parent.layoutManager as GridLayoutManager
        var span = manager.spanCount
//        LOG.I("123","pos=${parent.getChildAdapterPosition(view) % span} span=$span  $spaceH")
        when{
            parent.getChildAdapterPosition(view) % span == 0 -> {//最左
                if (mEnablePadding)
                    outRect.left = spaceH
                outRect.right = spaceH/2
            }
            parent.getChildAdapterPosition(view) % span == (span -1) -> {//最右
                if (mEnablePadding)
                    outRect.right = spaceH
                outRect.left = spaceH/2
            }
            else -> {
                outRect.left = spaceH/2
                outRect.right = spaceH/2
            }
        }
//        LOG.I("123","size=${parent.size} ${ manager.childCount} span=${span}")

        if (parent.getChildAdapterPosition(view) >= span){ //中间
            outRect.top = spaceV/2
            outRect.bottom =  spaceV/2
        } else {                                               //第一排
            if (mEnablePadding)
                outRect.top = spaceV
            outRect.bottom =  spaceV/2
        }
    }
}