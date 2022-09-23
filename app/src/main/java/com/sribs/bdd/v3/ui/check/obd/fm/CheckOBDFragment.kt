package com.sribs.bdd.v3.ui.check.obd.fm

import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckObliquedeformationBinding
import com.sribs.bdd.v3.ui.check.obd.CheckObliqueDeformationActivity
import com.sribs.bdd.v3.module.CheckMenuModule
import com.sribs.bdd.v3.popup.ChoosePicPopupWindow
import com.sribs.bdd.v3.view.CheckMenuView
import com.sribs.bdd.v3.view.CheckMenuView2
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.DamageV3Bean
import java.util.*
import kotlin.collections.ArrayList

@Route(path = ARouterPath.CHECK_OBLIQUE_DEFORMATION_FRAGMENT)
class CheckOBDFragment : BaseFragment(R.layout.fragment_check_obliquedeformation){

    private val mBinding : FragmentCheckObliquedeformationBinding by bindView()

    private var mMenuList : ArrayList<CheckMenuModule> ?= ArrayList()

    private var mPicList:ArrayList<String>? = ArrayList()

    private var mChoosePicPopupWindow:ChoosePicPopupWindow?=null

    override fun deinitView() {

    }

    override fun initView() {
        mMenuList?.add(CheckMenuModule().also {
            it.name =  "倾斜测量点位列表"
            it.menu?.add(CheckMenuModule.Item().also {
                it.name = "点位"
               /* it.item?.add(CheckMenuModule.Item.Mark().also {
                    it.name = "1.斜率1，斜率2"
                })*/
            })
        })
        Log.d("addItemView","mMenuList: "+mMenuList.toString());

        mBinding.toolLayout.setMenuModuleList(mMenuList!!).setCheckMenuCallback(object :CheckMenuView2.CheckMenuCallback{
            override fun onClick(v: View?, damageType:String?) {
                 (activity as CheckObliqueDeformationActivity).setVpCurrentItem(1)
            }

            override fun onMarkClick(v: View?,damage: DamageV3Bean?,damageType:String?) {
                TODO("Not yet implemented")
            }

        })!!.build()

        var popupWidth = resources.getDimensionPixelOffset(R.dimen._50sdp)
        mPicList!!.addAll(
            Arrays.asList("第一张",
            "第二张","第三张"))
        mBinding.checkSelectIndex.setOnClickListener {
            if(mChoosePicPopupWindow == null){
                mChoosePicPopupWindow = ChoosePicPopupWindow(context,popupWidth,mPicList,object: ChoosePicPopupWindow.PopupCallback{
                    override fun onSelect(position: Int) {
                        mBinding.checkSelectIndex.setText(mPicList!!.get(position))
                    }

                })
            }
            mChoosePicPopupWindow!!.showAsDropDown(mBinding.checkSelectIndex,-popupWidth-10,-mBinding.checkSelectIndex.height)

        }

    }
}