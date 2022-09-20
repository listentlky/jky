package com.sribs.bdd.v3.ui.check.bs.fm

import android.util.Log
import android.view.View
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.radaee.reader.PDFLayoutView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckBuildStructureBinding
import com.sribs.bdd.v3.bean.CheckBSMainBean
import com.sribs.bdd.v3.module.CheckMenuModule
import com.sribs.bdd.v3.module.FloorDrawingModule
import com.sribs.bdd.v3.popup.FloorDrawingSpinnerPopupWindow
import com.sribs.bdd.v3.ui.check.bs.CheckBuildStructureActivity
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.bdd.v3.view.CheckMenuView2
import com.sribs.common.ARouterPath

/**
 * create time: 2022/9/6
 * author: bruce
 * description:
 */
@Route(path = ARouterPath.CHECK_BUILD_STRUCTURE_FRAGMENT)
class CheckBSFragment : BaseFragment(R.layout.fragment_check_build_structure),
    FloorDrawingSpinnerPopupWindow.FloorDrawItemClickCallback {

    private val mBinding: FragmentCheckBuildStructureBinding by bindView()

    private var mMenuList: ArrayList<CheckMenuModule>? = ArrayList()

    private var mChooseFloorDraw: FloorDrawingSpinnerPopupWindow ? = null

    private var mFloorDrawModule:ArrayList<FloorDrawingModule>?= ArrayList()

    override fun deinitView() {

    }

    override fun initView() {
        mMenuList?.add(CheckMenuModule().also {
            it.name = "建筑结构列表"
            it.menu?.add(CheckMenuModule.Item().also {
                it.name = "层高"
               /* it.item?.add(CheckMenuModule.Item.Mark().also {
                    it.name = "轴线"
                })*/
            })
            it.menu?.add(CheckMenuModule.Item().also {
                it.name = "轴网"
               /* it.item?.add(CheckMenuModule.Item.Mark().also {
                    it.name = "轴线2"
                })*/
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
        var popupWidth = resources.getDimensionPixelOffset(R.dimen._90sdp)
        mBinding.checkSelectIndex.setOnClickListener {
            if(mChooseFloorDraw == null){
                mChooseFloorDraw = FloorDrawingSpinnerPopupWindow(activity,
                    popupWidth,mFloorDrawModule,this)
            }
            LogUtils.d("showAsDropDown")
            mChooseFloorDraw!!.showAsDropDown(mBinding.checkSelectIndex,-popupWidth-10,-mBinding.checkSelectIndex.height)
        }


        mBinding.checkFab.setOnClickListener {
            (activity as CheckBuildStructureActivity).startFabPDF()
        }

    }

    fun initFloorDrawData(checkMainBean: List<CheckBSMainBean>){
        mFloorDrawModule!!.clear()
        checkMainBean.forEach {
            mFloorDrawModule!!.add(FloorDrawingModule().also { b->
                b.mMenuName = it.floorName
                it.drawing!!.forEach { c->
                    b.mNameList!!.add(FloorDrawingModule.Item().also { d->
                        d.name = c.fileName
                        d.path = c.localAbsPath
                    })
                }
            })
        }
        if(mFloorDrawModule != null && mFloorDrawModule!!.size>0) {
            mBinding.checkSelectIndex.text = mFloorDrawModule!![0].mNameList!![0].name
        }
    }

    fun getPDFView():PDFLayoutView{
        return mBinding.pdfLayout
    }

    fun getPDFParentView():RelativeLayout{
        return mBinding.pdfRoot
    }

    override fun onClick(path: String?,name:String?) {
        mBinding.checkSelectIndex.text = name
        (activity as CheckBuildStructureActivity).openPDF(path!!)
    }

}