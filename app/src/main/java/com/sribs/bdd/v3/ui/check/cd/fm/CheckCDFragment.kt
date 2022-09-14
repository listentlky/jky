package com.sribs.bdd.v3.ui.check.cd.fm

import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckObliquedeformationBinding
import com.sribs.bdd.v3.module.CheckMenuModule
import com.sribs.bdd.v3.ui.check.cd.CheckComponentDetectionActivity
import com.sribs.bdd.v3.view.CheckMenuView2
import com.sribs.common.ARouterPath

@Route(path = ARouterPath.CHECK_COMPONENT_DETECTION_FRAGMENT)
class CheckCDFragment : BaseFragment(R.layout.fragment_check_componentdetection) {

    private val mBinding: FragmentCheckObliquedeformationBinding by bindView()

    private var mMenuList: ArrayList<CheckMenuModule>? = ArrayList()

    override fun deinitView() {

    }

    override fun initView() {
        mMenuList?.add(CheckMenuModule().also {
            it.name = "构件检测列表"
            it.menu?.add(CheckMenuModule.Item().also {
                it.name = "梁"
                it.item?.add(CheckMenuModule.Item.Mark().also {
                    it.name = "轴线"
                })
            })
            it.menu?.add(CheckMenuModule.Item().also {
                it.name = "柱"
            })
            it.menu?.add(CheckMenuModule.Item().also {
                it.name = "墙"
            })
            it.menu?.add(CheckMenuModule.Item().also {
                it.name = "板"
            })
        })
        Log.d("addItemView", "mMenuList: " + mMenuList.toString());
        mBinding.toolLayout.setMenuModuleList(mMenuList!!)
            .setCheckMenuCallback(object : CheckMenuView2.CheckMenuCallback {
                override fun onClick(v: View?, pos: Int?) {
                    when (pos){
                        0->   (activity as CheckComponentDetectionActivity).setVpCurrentItem(1)
                        1->   (activity as CheckComponentDetectionActivity).setVpCurrentItem(2)
                        2->   (activity as CheckComponentDetectionActivity).setVpCurrentItem(3)
                        3->   (activity as CheckComponentDetectionActivity).setVpCurrentItem(4)
                    }
                }

            })!!.build()

        /*mBinding.toolLayout.setMenuModuleList(mMenuList).setCheckMenuCallback {
            (activity as CheckObliqueDeformationActivity).setVpCurrentItem(1)
        }.build()*/

    }

}