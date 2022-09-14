package com.sribs.bdd.bean.data.recycler

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
//import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
* Created by leon on 2022/03/11.
* desc: 项目
*/

data class DrawingsLauoutItem(
    var id: Int,//1-1000为楼层号；50000-50003，东南西北面墙；50004,底层/总平面
    var floorName: String,//楼层名称
    var drawingList: String,//图纸名称，不带路径，以“，”分隔
    var actionName: String,
    var copyFloor: String,
    var drawingsUriList: ArrayList<Uri>?,
    var isCached: Boolean
) {
    companion object {
        @JvmStatic fun areTheSame(
            left: DrawingsLauoutItem,
            right: DrawingsLauoutItem
        ): Boolean {
            return left.id == right.id
        }

        @JvmStatic fun areContentsTheSame(
            left: DrawingsLauoutItem,
            right: DrawingsLauoutItem
        ): Boolean {
            return left.id == right.id &&
                    left.actionName?.equals(right.actionName) &&
                    left.drawingList?.equals(right.drawingList) &&
                    left.copyFloor?.equals(right.copyFloor) &&
                    left.drawingsUriList?.equals(right.drawingsUriList) == true
        }
    }
}