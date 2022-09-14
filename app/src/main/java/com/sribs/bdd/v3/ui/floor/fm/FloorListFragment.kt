package com.sribs.bdd.v3.ui.floor.fm

import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentFloorListBinding
import com.sribs.bdd.v3.ui.floor.fm.p.FloorListPImpl

/**
 * create time: 2022/9/6
 * author: bruce
 * description:
 */
class FloorListFragment : BaseFragment(R.layout.fragment_floor_list){

    val mBinding : FragmentFloorListBinding by bindView()

    val mFloorListP by lazy {
        FloorListPImpl()
    }

    override fun deinitView() {

    }

    override fun initView() {

    }
}