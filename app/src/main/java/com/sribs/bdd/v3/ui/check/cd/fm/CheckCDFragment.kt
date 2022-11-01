package com.sribs.bdd.v3.ui.check.cd.fm

import android.app.AlertDialog
import android.graphics.Canvas
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.radaee.reader.PDFLayoutView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckComponentdetectionBinding
import com.sribs.bdd.databinding.FragmentCheckObliquedeformationBinding
import com.sribs.bdd.v3.bean.CheckCDMainBean
import com.sribs.bdd.v3.module.CheckMenuModule
import com.sribs.bdd.v3.module.FloorDrawingModule
import com.sribs.bdd.v3.popup.FloorDrawingSpinnerPopupWindow
import com.sribs.bdd.v3.ui.check.cd.CheckComponentDetectionActivity
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.bdd.v3.view.CheckMenuView2
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean


@Route(path = ARouterPath.CHECK_COMPONENT_DETECTION_FRAGMENT)
class CheckCDFragment : BaseFragment(R.layout.fragment_check_componentdetection),
    FloorDrawingSpinnerPopupWindow.FloorDrawItemClickCallback {

     val mBinding: FragmentCheckComponentdetectionBinding by bindView()

    private var mMenuList: ArrayList<CheckMenuModule> = ArrayList()

    private var mChooseFloorDraw: FloorDrawingSpinnerPopupWindow? = null

    private var mFloorDrawModule: ArrayList<FloorDrawingModule>? = ArrayList()

    override fun deinitView() {

    }


    override fun initView() {
        var popupWidth = resources.getDimensionPixelOffset(R.dimen._80sdp)
        mBinding.checkSelectIndex.setOnClickListener {
            if (mChooseFloorDraw == null) {
                mChooseFloorDraw = FloorDrawingSpinnerPopupWindow(
                    activity,
                    popupWidth, mFloorDrawModule, this
                )
            }
            LogUtils.d("showAsDropDown")
            mChooseFloorDraw!!.showAsDropDown(
                mBinding.checkSelectIndex,
                -popupWidth - 10,
                -mBinding.checkSelectIndex.height
            )
        }


        mBinding.checkFab.setOnClickListener {
            (activity as CheckComponentDetectionActivity).startFabPDF()
        }
    }

    /**
     * 初始化选择窗口图纸数据
     */
    fun initFloorDrawData(checkMainBean: List<CheckCDMainBean>) {
        mFloorDrawModule!!.clear()
        checkMainBean.forEach {
            mFloorDrawModule!!.add(FloorDrawingModule().also { b ->
                b.mId = it.id
                b.mFloorName = it.floorName
                it.drawing!!.forEach { c ->
                    b.mNameList!!.add(c)
                    /*b.mNameList!!.add(FloorDrawingModule.Item().also { d->
                        d.name = c.fileName
                        d.path = c.localAbsPath
                        d.drawingID = c.drawingID
                        d.floorName = it.floorName
                    })*/
                }
            })
        }
        if (mFloorDrawModule != null && mFloorDrawModule!!.size > 0) {
            mBinding.checkSelectIndex.text = mFloorDrawModule!![0].mNameList!![0].fileName
        }
    }

    /**
     * 设置损伤列表
     */
    fun setDamage(damageBean: ArrayList<DamageV3Bean>?) {
        LogUtils.d("设置损伤列表： " + damageBean)
        mMenuList.clear()
        mMenuList.add(CheckMenuModule().also {
            it.name = "构件检测列表"
            it.menu.add(CheckMenuModule.Item().also {
                it.name = "梁"
            })
            it.menu.add(CheckMenuModule.Item().also {
                it.name = "柱"
            })
            it.menu.add(CheckMenuModule.Item().also {
                it.name = "墙"
            })
            it.menu.add(CheckMenuModule.Item().also {
                it.name = "板"
            })
        })
        if (damageBean != null) {
            LogUtils.d("设置损伤列表个数： " + damageBean!!.size)
            damageBean.forEach {
                when (it.type) {
                    (activity as CheckComponentDetectionActivity).mCurrentDamageType[0] -> { // 梁
                        mMenuList!![0].menu!![0].item!!.add(CheckMenuModule.Item.Mark().also { b ->
                            if (it.beamAxisNote.isNullOrEmpty()){
                                b.name = it.beamName+" "+ it.beamAxisNoteList!!.get(0)+"/"+it!!.beamAxisNoteList!!.get(1)+"-"+it.beamAxisNoteList!!.get(2)

                            }else{
                                b.name = it.beamName+" "+ it.beamAxisNote

                            }
                            b.damage = it
                        })
                    }
                    (activity as CheckComponentDetectionActivity).mCurrentDamageType[1] -> { //柱
                        mMenuList!![0].menu!![1].item!!.add(CheckMenuModule.Item.Mark().also { c ->

                            if (it.columnAxisNote.isNullOrEmpty()){
                                c.name = it.columnName+" "+  it.columnAxisNoteList!!.get(0)+"/"+it.columnAxisNoteList!!.get(1)

                            }else{
                                c.name = it.columnName+" "+  it.columnAxisNote

                            }
                            c.damage = it
                        })
                    }
                    (activity as CheckComponentDetectionActivity).mCurrentDamageType[2] -> { //墙
                        mMenuList!![0].menu!![2].item!!.add(CheckMenuModule.Item.Mark().also { c ->
                            if (it.axisSingleNote.isNullOrEmpty()){
                                c.name = it.plateName +" "+it.axisPlateNoteList!!.get(0)+"/"+it.axisPlateNoteList!!.get(1)+"-"+it.axisPlateNoteList!!.get(2)
                            }else{
                                c.name = it.plateName+" "+it.axisSingleNote
                            }

                            c.damage = it
                        })
                    }
                    (activity as CheckComponentDetectionActivity).mCurrentDamageType[3] -> { //板
                        mMenuList!![0].menu!![3].item!!.add(CheckMenuModule.Item.Mark().also { c ->
                            if (it.axisSingleNote.isNullOrEmpty()){
                                c.name = it.plateName +" "+it.axisPlateNoteList!!.get(0)+"/"+it.axisPlateNoteList!!.get(1)+"-"+it.axisPlateNoteList!!.get(2)+"-"+it.axisPlateNoteList!!.get(3)
                            }else{
                                c.name = it.plateName+" "+it.axisSingleNote
                            }
                            c.damage = it
                        })
                    }
                }
            }
        }

        mBinding.toolLayout.setMenuModuleList(mMenuList)
            .setCheckMenuCallback(object : CheckMenuView2.CheckMenuCallback {
                override fun onClick(v: View?, damageType: String?) {
                    (activity as CheckComponentDetectionActivity).setAddAnnotReF(-1L)
                    (activity as CheckComponentDetectionActivity).resetDamageInfo(null, damageType,false,false)
                }

                override fun onMarkClick(v: View?, damage: DamageV3Bean?, damageType: String?) {
                    (activity as CheckComponentDetectionActivity).resetDamageInfo(damage, damageType,false,false)
                }

                override fun onMarkRemoveClick(
                    v: View?,
                    damage: DamageV3Bean?,
                    damageType: String?
                ) {
                    AlertDialog.Builder(activity).setTitle("提示")
                        .setMessage("是否移除当前损伤记录")
                        .setPositiveButton(R.string.dialog_ok) { dialog, which ->
                            (activity as CheckComponentDetectionActivity).removeDamage(
                                damage,
                                damageType
                            )
                        }.setNegativeButton(
                            R.string.dialog_cancel
                        ) { dialog, which ->

                        }
                        .show()
                }

            })!!.build()

    }

    fun getPDFView(): PDFLayoutView {
        return mBinding.pdfLayout
    }

    fun getPDFParentView(): RelativeLayout {
        return mBinding.pdfRoot
    }

    override fun onClick(data: DrawingV3Bean) {
        (activity as CheckComponentDetectionActivity).choosePDF(data)
    }
}