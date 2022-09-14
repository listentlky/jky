package com.sribs.bdd.v3.ui.floor.fm.p

import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.common.module.BasePresenter

/**
 * create time: 2022/9/6
 * author: bruce
 * description:
 */
class FloorListPImpl : BasePresenter(),FloorListContrast.FloorListP{

    private var mView: FloorListContrast.FloorListV?=null

    /**
     * 加载楼栋单元列表
     */
    override fun getFloorList() {

    }

    override fun bindView(v: IBaseView) {
        mView = v as FloorListContrast.FloorListV
    }

    override fun unbindView() {
        dispose()
        mView=null
    }
}