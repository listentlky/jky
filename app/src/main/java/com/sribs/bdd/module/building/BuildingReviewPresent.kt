package com.sribs.bdd.module.building

import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.module.project.IProjectContrast
import com.sribs.common.module.BasePresenter

class BuildingReviewPresent:BasePresenter(), IBuildingContrast.IBuildingReviewPresent {

    private var mView: IBuildingContrast.IBuildingReviewView?=null


    override fun bindView(v: IBaseView) {
        mView = v as IBuildingContrast.IBuildingReviewView
    }

    override fun unbindView() {
        mView = null
    }
}