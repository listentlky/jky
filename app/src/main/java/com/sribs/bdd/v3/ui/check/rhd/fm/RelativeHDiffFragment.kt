package com.sribs.bdd.v3.ui.check.rhd.fm

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentRelativeHDiffBinding
import com.sribs.bdd.v3.popup.ChoosePicPopupWindow
import java.util.*
import kotlin.collections.ArrayList

/**
 * create time: 2022/9/7
 * author: bruce
 * description:
 */
@Route(path = com.sribs.common.ARouterPath.CHECK_RELATIVE_H_DIFF_FRAGMENT)
class RelativeHDiffFragment : BaseFragment(R.layout.fragment_relative_h_diff){

    private val mBinding : FragmentRelativeHDiffBinding by bindView()

    private var mPicList:ArrayList<String>? = ArrayList()

    private var mChoosePicPopupWindow:ChoosePicPopupWindow?=null

    override fun deinitView() {

    }

    override fun initView() {
       mBinding.checkLeftMenu.setOnClickListener {
           when(mBinding.checkLeftLayout.visibility){
               View.VISIBLE->{
                   mBinding.checkLeftLayout.visibility = View.GONE
                   mBinding.checkLeftMenu.isSelected = false
               }
               View.GONE->{
                   mBinding.checkLeftLayout.visibility = View.VISIBLE
                   mBinding.checkLeftMenu.isSelected = true
               }
           }
       }
        var popupWidth = resources.getDimensionPixelOffset(R.dimen._50sdp)
        mPicList!!.addAll(Arrays.asList("第一张",
            "第二张","第三张"))
        mBinding.checkSelectIndex.setOnClickListener {
            if(mChoosePicPopupWindow == null){
                mChoosePicPopupWindow = ChoosePicPopupWindow(context,popupWidth,mPicList,object:ChoosePicPopupWindow.PopupCallback{
                    override fun onSelect(position: Int) {
                        mBinding.checkSelectIndex.text=mPicList!![position]
                    }

                })
            }
            mChoosePicPopupWindow!!.showAsDropDown(mBinding.checkSelectIndex,-popupWidth-10,-mBinding.checkSelectIndex.height)
        }
    }
}