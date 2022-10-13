package com.sribs.bdd.v3.ui.check.obd.fm

import android.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.radaee.reader.PDFLayoutView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckObliquedeformationBinding
import com.sribs.bdd.v3.bean.CheckBSMainBean
import com.sribs.bdd.v3.bean.CheckOBDMainBean
import com.sribs.bdd.v3.ui.check.obd.CheckObliqueDeformationActivity
import com.sribs.bdd.v3.module.CheckMenuModule
import com.sribs.bdd.v3.module.FloorDrawingModule
import com.sribs.bdd.v3.popup.ChoosePicPopupWindow
import com.sribs.bdd.v3.ui.check.bs.CheckBuildStructureActivity
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.bdd.v3.view.CheckMenuView
import com.sribs.bdd.v3.view.CheckMenuView2
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean
import java.util.*
import kotlin.collections.ArrayList

@Route(path = ARouterPath.CHECK_OBLIQUE_DEFORMATION_FRAGMENT)
class CheckOBDFragment : BaseFragment(R.layout.fragment_check_obliquedeformation){

    val mBinding : FragmentCheckObliquedeformationBinding by bindView()

    private var mMenuList : ArrayList<CheckMenuModule> = ArrayList()

    private var mDrawingV3Bean:ArrayList<DrawingV3Bean>? = ArrayList()

    private var mChoosePicPopupWindow:ChoosePicPopupWindow?=null

    override fun deinitView() {

    }

    override fun initView() {

        var popupWidth = resources.getDimensionPixelOffset(R.dimen._70sdp)
        if(mChoosePicPopupWindow == null){
            mChoosePicPopupWindow = ChoosePicPopupWindow(context,popupWidth,mDrawingV3Bean,object: ChoosePicPopupWindow.PopupCallback{
                override fun onSelect(mDrawingV3Bean: DrawingV3Bean) {
                    (activity as CheckObliqueDeformationActivity).choosePDF(mDrawingV3Bean)
                }

            })
        }
        mBinding.checkSelectIndex.setOnClickListener {
            mChoosePicPopupWindow?.notifyAdapter()
            mChoosePicPopupWindow?.showAsDropDown(mBinding.checkSelectIndex,-popupWidth-10,-mBinding.checkSelectIndex.height)
        }

        mBinding.checkFab.setOnClickListener {
            (activity as CheckObliqueDeformationActivity).startFabPDF()
        }

        mBinding.checkGuideImg.setCallback { text, rotate ->
            (activity as CheckObliqueDeformationActivity).mGuideText = text
            (activity as CheckObliqueDeformationActivity).mGuideRotate = rotate
            mBinding.checkGuideHint.text = text
        }

    }

    /**
     * 初始化选择窗口图纸数据
     */
    fun initFloorDrawData(checkOBDMainBean: List<CheckOBDMainBean>){
        LogUtils.d("initFloorDrawData： "+checkOBDMainBean)
        mDrawingV3Bean!!.clear()
        checkOBDMainBean.forEach {
            mDrawingV3Bean!!.addAll(it.drawing!!)
        }
        if(mDrawingV3Bean != null && mDrawingV3Bean!!.size>0) {
            mBinding.checkSelectIndex.text = mDrawingV3Bean!![0].fileName
        }
        LogUtils.d("initFloorDrawData： "+mDrawingV3Bean)
    }

    /**
     * 设置损伤列表
     */
    fun setDamage(damageBean:ArrayList<DamageV3Bean>?){
        LogUtils.d("设置损伤列表： "+damageBean)
        mMenuList.clear()
        mMenuList.add(CheckMenuModule().also {
            it.name = "倾斜测量点位列表"
            it.menu.add(CheckMenuModule.Item().also {
                it.name = "点位"
            })
        })
        if(damageBean != null) {
            LogUtils.d("设置损伤列表个数： "+damageBean!!.size)
            damageBean.forEach {
                when (it.type) {
                    (activity as CheckObliqueDeformationActivity).mCurrentDamageType[0] -> { // 点位
                        mMenuList!![0].menu!![0].item!!.add(CheckMenuModule.Item.Mark().also { b->
                            b.name = it.pointName
                            b.damage = it
                        })
                    }
                }
            }
        }

        mBinding.toolLayout.setMenuModuleList(mMenuList)
            .setCheckMenuCallback(object : CheckMenuView2.CheckMenuCallback {
                override fun onClick(v: View?, damageType:String?) {
                    (activity as CheckObliqueDeformationActivity).setAddAnnotReF(-1L)
                    (activity as CheckObliqueDeformationActivity).resetDamageInfo(null,damageType)
                }

                override fun onMarkClick(v: View?,damage: DamageV3Bean?,damageType:String?) {
                    (activity as CheckObliqueDeformationActivity).resetDamageInfo(damage,damageType)
                }

                override fun onMarkRemoveClick(v: View?, damage: DamageV3Bean?, damageType: String?) {
                    AlertDialog.Builder(activity).setTitle("提示")
                        .setMessage("是否移除当前损伤记录")
                        .setPositiveButton(R.string.dialog_ok) { dialog, which ->
                            (activity as CheckObliqueDeformationActivity).removeDamage(damage,damageType)
                        }.setNegativeButton(
                            R.string.dialog_cancel
                        ) { dialog, which ->

                        }
                        .show()
                }

            })!!.build()

    }

    /**
     * 设置指南方向
     */
    fun setGuide(drawingV3Bean:DrawingV3Bean?){
        LogUtils.d("setGuide() "+drawingV3Bean)
        if(drawingV3Bean == null){
            mBinding.checkGuideImg.rotation = 0F
            mBinding.checkGuideHint.text="北"
        }else{
            if(drawingV3Bean.direction.isNullOrEmpty()){
                mBinding.checkGuideHint.text="北"
            }else{
                mBinding.checkGuideHint.text= drawingV3Bean.direction
            }
            mBinding.checkGuideImg.rotation = drawingV3Bean.rotate!!.toFloat()

        }
    }

    fun getPDFView(): PDFLayoutView {
        return mBinding.pdfLayout
    }

    fun getPDFParentView(): RelativeLayout {
        return mBinding.pdfRoot
    }

}