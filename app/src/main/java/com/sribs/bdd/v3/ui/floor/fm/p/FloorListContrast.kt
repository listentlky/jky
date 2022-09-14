package com.sribs.bdd.v3.ui.floor.fm.p

import com.cbj.sdk.libui.mvp.moudles.IBasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView

/**
 * create time: 2022/9/6
 * author: bruce
 * description:
 */
interface FloorListContrast {

    interface FloorListP:IBasePresenter{
        fun getFloorList()
    }

    interface FloorListV:IBaseView{
        fun setView()
    }

}