package com.sribs.bdd.ui.building

import android.content.Context
import android.view.View
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.databinding.ActivityPdfReviewBinding
import com.sribs.bdd.module.building.BuildingReviewPresent
import com.sribs.bdd.module.building.IBuildingContrast

class BuildingReviewActivity:BaseActivity(), IBuildingContrast.IBuildingReviewView {

    private val mPresent: BuildingReviewPresent by lazy { BuildingReviewPresent() }

    private val mBinding:ActivityPdfReviewBinding by inflate()

    override fun deinitView() {
        mPresent.unbindView()
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
       bindPresenter()
    }

    override fun getContext(): Context? = this

    override fun bindPresenter() {
       mPresent.bindView(this)
    }

    override fun onMsg(msg: String) {
       showToast(msg)
    }

    override fun unbindPresenter() {
       mPresent.unbindView()
    }
}