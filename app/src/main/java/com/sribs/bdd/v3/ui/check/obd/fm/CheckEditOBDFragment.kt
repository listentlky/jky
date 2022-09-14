package com.sribs.bdd.v3.ui.check.obd.fm

import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckObliquedeformationEditBinding
import com.sribs.bdd.v3.ui.check.obd.CheckObliqueDeformationActivity
import com.sribs.common.ARouterPath

@Route(path = ARouterPath.CHECK_OBLIQUE_DEFORMATION_Edit_FRAGMENT)
class CheckEditOBDFragment : BaseFragment(R.layout.fragment_check_obliquedeformation_edit){

    private val mBinding : FragmentCheckObliquedeformationEditBinding by bindView()

    override fun deinitView() {

    }

    override fun initView() {
        mBinding.checkEditH1.checkEditName.text="测量高度1(m)"
        mBinding.checkEditH1.checkEdit.hint="请输入测量高度"
        mBinding.checkEditH2.checkEditName.text="测量高度2(m)"
        mBinding.checkEditH2.checkEdit.hint="请输入测量高度"
        mBinding.checkEditQx1.checkEditName.text="倾斜量1(mm)"
        mBinding.checkEditQx1.checkEdit.hint="请输入倾斜量"
        mBinding.checkEditQx2.checkEditName.text="倾斜量2(mm)"
        mBinding.checkEditQx2.checkEdit.hint="请输入倾斜量"

        mBinding.checkObdDoubleRadio.isSelected = true
        mBinding.checkObdOnlyRadio.isSelected = false

        mBinding.checkObdMenuLayout.checkObdMenuScale.setOnClickListener {
            (activity as CheckObliqueDeformationActivity).setVpCurrentItem(0)
        }
        mBinding.checkObdMenuLayout.checkObdMenuClose.setOnClickListener {
            (activity as CheckObliqueDeformationActivity).setVpCurrentItem(0)
        }
        mBinding.checkObdOnlyRadio.setOnClickListener {
            mBinding.checkObdOnlyRadio.isSelected = true
            mBinding.checkObdDoubleRadio.isSelected = false
            mBinding.checkEditH2.checkEdit.setText("")
            mBinding.checkEditH2.checkEdit.isEnabled = false
            mBinding.checkEditQx2.checkEdit.setText("")
            mBinding.checkEditQx2.checkEdit.isEnabled = false
        }
        mBinding.checkObdDoubleRadio.setOnClickListener {
            mBinding.checkObdOnlyRadio.isSelected = false
            mBinding.checkObdDoubleRadio.isSelected = true

            mBinding.checkEditH2.checkEdit.isEnabled = true
            mBinding.checkEditQx2.checkEdit.isEnabled = true
        }
    }
}