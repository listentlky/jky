package com.sribs.bdd.module.building

import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.module.building.IBuildingContrast

class DrawWhitePresent:IBuildingContrast.IDrawWhitePresent {

    private var mView:IBuildingContrast.IDrawWhiteView?=null

    override fun bindView(v: IBaseView) {
        mView = v as IBuildingContrast.IDrawWhiteView
    }

    override fun unbindView() {
        mView = null
    }
}