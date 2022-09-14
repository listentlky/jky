package com.sribs.bdd.v3.ui.check.bs.fm

import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckBuildStructureGridBinding
import com.sribs.common.ARouterPath

/**
 * create time: 2022/9/6
 * author: bruce
 * description:
 */
@Route(path = ARouterPath.CHECK_BUILD_STRUCTURE_GRID_FRAGMENT)
class CheckBSGridFragment : BaseFragment(R.layout.fragment_check_build_structure_grid){

    private val mBinding : FragmentCheckBuildStructureGridBinding by bindView()

    override fun deinitView() {

    }

    override fun initView() {

    }
}