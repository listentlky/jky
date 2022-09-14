package com.sribs.bdd.v3.ui.check.bs.fm

import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckBuildStructureBinding
import com.sribs.bdd.v3.module.CheckMenuModule
import com.sribs.bdd.v3.ui.check.bs.CheckBuildStructureActivity
import com.sribs.bdd.v3.view.CheckMenuView2
import com.sribs.common.ARouterPath

/**
 * create time: 2022/9/6
 * author: bruce
 * description:
 */
@Route(path = ARouterPath.CHECK_BUILD_STRUCTURE_FRAGMENT)
class CheckBSFragment : BaseFragment(R.layout.fragment_check_build_structure) {

    private val mBinding: FragmentCheckBuildStructureBinding by bindView()

    private var mMenuList: ArrayList<CheckMenuModule>? = ArrayList()

    override fun deinitView() {

    }

    override fun initView() {
        mMenuList?.add(CheckMenuModule().also {
            it.name = "建筑结构列表"
            it.menu?.add(CheckMenuModule.Item().also {
                it.name = "层高"
                it.item?.add(CheckMenuModule.Item.Mark().also {
                    it.name = "轴线"
                })
            })
            it.menu?.add(CheckMenuModule.Item().also {
                it.name = "轴网"
                it.item?.add(CheckMenuModule.Item.Mark().also {
                    it.name = "轴线2"
                })
            })
        })
        Log.d("addItemView", "mMenuList: " + mMenuList.toString());
        mBinding.toolLayout.setMenuModuleList(mMenuList!!)
            .setCheckMenuCallback(object : CheckMenuView2.CheckMenuCallback {
                override fun onClick(v: View?, pos: Int?) {
                    when (pos) {
                        0 -> { //层高
                            (activity as CheckBuildStructureActivity).setVpCurrentItem(1)
                        }
                        1 -> { //轴网
                            (activity as CheckBuildStructureActivity).setVpCurrentItem(2)
                        }
                    }

                }

            })!!.build()

        (activity as CheckBuildStructureActivity).openPDF(mBinding.pdfRoot,mBinding.pdfLayout,"111.pdf")

    }

}