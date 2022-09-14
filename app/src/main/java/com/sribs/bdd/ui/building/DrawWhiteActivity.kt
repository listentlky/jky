package com.sribs.bdd.ui.building

import android.content.Context
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.R
import com.sribs.bdd.databinding.ActivityDrawWhiteBinding
import com.sribs.bdd.module.building.DrawWhitePresent
import com.sribs.bdd.module.building.IBuildingContrast
import com.sribs.common.ARouterPath
import com.sribs.common.databinding.LayoutCommonToolbarBinding


@Route(path= ARouterPath.DRAW_WHITE)
class DrawWhiteActivity:BaseActivity(),IBuildingContrast.IDrawWhiteView{


    private val mPresent: DrawWhitePresent by lazy { DrawWhitePresent() }

    private val mBinding:ActivityDrawWhiteBinding by inflate()

    private var fragment:DrawWhiteFragment?=null

    private val mToolbarBinding : LayoutCommonToolbarBinding by lazy {
        LayoutCommonToolbarBinding.bind(mBinding.toolbar.root)
    }

    override fun deinitView() {

    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        bindPresenter()
        fragment = ARouter.getInstance().build(com.sribs.common.ARouterPath.FRAGMENT_DRAW_WHITE).navigation() as DrawWhiteFragment
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment!!).commit()

        mToolbarBinding.tbTitle.text = "新建白板"
        mToolbarBinding.tb.setNavigationIcon(R.mipmap.icon_back)
        mToolbarBinding.tb.setNavigationOnClickListener {
            fragment!!.showSaveDialog()
        }

    }

    override fun onBackPressed() {
        fragment?.showSaveDialog()
    }



    override fun getContext(): Context? = applicationContext

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