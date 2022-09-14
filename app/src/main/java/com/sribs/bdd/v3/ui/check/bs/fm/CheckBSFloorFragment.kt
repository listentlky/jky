package com.sribs.bdd.v3.ui.check.bs.fm

import android.view.Gravity
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckBuildStructureFloorBinding
import com.sribs.bdd.v3.popup.OneChoosePopupWindow
import com.sribs.bdd.v3.ui.check.bs.CheckBuildStructureActivity
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.bdd.v3.util.LogUtils.d
import com.sribs.bdd.v3.view.OneChooseSpinnerView
import com.sribs.common.ARouterPath
import kotlinx.android.synthetic.main.spinner_layout.view.*
import java.util.*

/**
 * create time: 2022/9/6
 * author: bruce
 * description:
 */
@Route(path = ARouterPath.CHECK_BUILD_STRUCTURE_FLOOR_FRAGMENT)
class CheckBSFloorFragment : BaseFragment(R.layout.fragment_check_build_structure_floor){

    private val mBinding : FragmentCheckBuildStructureFloorBinding by bindView()

    private var mJgZgList:ArrayList<String>? = ArrayList()

    private var mSelectPosition = 0

    override fun deinitView() {

    }

    override fun initView() {
        mJgZgList!!.addAll(Arrays.asList("净高", "总高"))
        mBinding.checkBsFloorSpinner.setSpinnerData(mJgZgList)
            .setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback {
                position: Int ->
                mSelectPosition = position
                d("当前选择：$position")
                if(position == 0) {
                    mBinding.checkBsFloorMchd.checkEdit.setText("")
                    mBinding.checkBsFloorMchd.checkEdit.isEnabled = false
                }else{
                    mBinding.checkBsFloorMchd.checkEdit.isEnabled = true
                }
        }.build()
        mBinding.checkBsChooseBut.setOnClickListener {
            when(mBinding.checkBsFloorZxEdit.visibility){
                View.GONE->{
                    mBinding.checkBsFloorEdit1.root.visibility = View.INVISIBLE
                    mBinding.checkBsFloorFj.visibility = View.INVISIBLE
                    mBinding.checkBsFloorEdit2.root.visibility = View.INVISIBLE
                    mBinding.checkBsFloorZxEdit.visibility = View.VISIBLE
                }
                View.VISIBLE->{
                    mBinding.checkBsFloorEdit1.root.visibility = View.VISIBLE
                    mBinding.checkBsFloorFj.visibility = View.VISIBLE
                    mBinding.checkBsFloorEdit2.root.visibility = View.VISIBLE
                    mBinding.checkBsFloorZxEdit.visibility = View.GONE
                }
            }
        }

        mBinding.checkBsFloorCgsj.checkEditName.text = "层高设计值(mm)"
        mBinding.checkBsFloorCgsj.checkEdit.hint="请输入层高设计值"

        mBinding.checkBsFloorCgsc.checkEditName.text ="层高实测值(mm)"
        mBinding.checkBsFloorCgsc.checkEdit.hint="请输入层高实测值"

        mBinding.checkBsFloorSjbh.checkEditName.text ="设计板厚(mm)"
        mBinding.checkBsFloorSjbh.checkEdit.hint="请输入设计板厚"

        mBinding.checkBsFloorMchd.checkEditName.text ="装饰面层厚度(mm)"
        mBinding.checkBsFloorMchd.checkEdit.hint="请输入装饰面层厚度"
        mBinding.checkBsFloorMchd.checkEdit.isEnabled = false

        mBinding.checkMenuLayout.checkObdMenuClose.setOnClickListener {
            (context as CheckBuildStructureActivity).setVpCurrentItem(0)
        }

    }
}