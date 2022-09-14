package com.sribs.bdd.v3.ui.check.rhd.fm

import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentRelativeHDiffEditBinding
import com.sribs.bdd.v3.ui.check.rhd.RelativeHDiffActivity

/**
 * create time: 2022/9/7
 * author: bruce
 * description:
 */
@Route(path = com.sribs.common.ARouterPath.CHECK_RELATIVE_H_DIFF_EDIT_FRAGMENT)
class RelativeHDiffEditFragment :BaseFragment(R.layout.fragment_relative_h_diff_edit){

    val mBinding:FragmentRelativeHDiffEditBinding by bindView()

    override fun deinitView() {

    }

    override fun initView() {
        mBinding.checkRhdRightLayout.setOnClickListener {
            (context as RelativeHDiffActivity).setVpCurrentItem(0)
        }
    }
}