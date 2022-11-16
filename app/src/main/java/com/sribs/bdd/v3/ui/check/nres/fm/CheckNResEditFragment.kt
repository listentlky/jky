package com.sribs.bdd.v3.ui.check.nres.fm

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckBuildStructureFloorBinding
import com.sribs.bdd.databinding.FragmentCheckNonResEditBinding
import com.sribs.common.bean.db.DamageV3Bean

/**
 * create time: 2022/11/15
 * author: bruce
 * description:
 */
@Route(path = com.sribs.common.ARouterPath.CHECK_NON_RESIDENTS_EDIT_FRAGMENT)
class CheckNResEditFragment: BaseFragment(R.layout.fragment_check_non_res_edit){

    private val mBinding: FragmentCheckNonResEditBinding by bindView()

    override fun deinitView() {

    }

    override fun initView() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


    }

    /**
     * 根据是否为null来设置数据
     */
    fun resetView(damageV3Bean: DamageV3Bean?) {

    }
}