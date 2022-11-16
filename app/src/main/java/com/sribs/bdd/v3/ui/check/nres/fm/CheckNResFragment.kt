package com.sribs.bdd.v3.ui.check.nres.fm

import android.app.AlertDialog
import android.view.View
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.radaee.reader.PDFLayoutView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckNonResBinding
import com.sribs.bdd.v3.bean.CheckBSMainBean
import com.sribs.bdd.v3.bean.CheckNResMainBean
import com.sribs.bdd.v3.module.CheckMenuModule
import com.sribs.bdd.v3.module.FloorDrawingModule
import com.sribs.bdd.v3.popup.FloorDrawingSpinnerPopupWindow
import com.sribs.bdd.v3.ui.check.nres.CheckNonResidentsActivity
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.bdd.v3.view.CheckMenuView2
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean

/**
 * create time: 2022/11/15
 * author: bruce
 * description:
 */
@Route(path = com.sribs.common.ARouterPath.CHECK_NON_RESIDENTS_FRAGMENT)
class CheckNResFragment : BaseFragment(R.layout.fragment_check_non_res),
    FloorDrawingSpinnerPopupWindow.FloorDrawItemClickCallback {

    val mBinding: FragmentCheckNonResBinding by bindView()

    private var mMenuList: ArrayList<CheckMenuModule> = ArrayList()

    private var mChooseFloorDraw: FloorDrawingSpinnerPopupWindow? = null

    private var mFloorDrawModule: ArrayList<FloorDrawingModule>? = ArrayList()

    var mIsViewCreated = false

    override fun deinitView() {
        mIsViewCreated = false
    }

    override fun initView() {
        mIsViewCreated = true

        var popupWidth = resources.getDimensionPixelOffset(R.dimen._80sdp)
        mBinding.checkSelectIndex.setOnClickListener {
            if(mChooseFloorDraw == null){
                mChooseFloorDraw = FloorDrawingSpinnerPopupWindow(activity,
                    popupWidth,mFloorDrawModule,this)
            }
            LogUtils.d("showAsDropDown")
            mChooseFloorDraw!!.showAsDropDown(mBinding.checkSelectIndex,-popupWidth-10,-mBinding.checkSelectIndex.height)
        }

        mBinding.checkFab.setOnClickListener {
            (activity as CheckNonResidentsActivity).startFabPDF()
        }

    }

    /**
     * 初始化选择窗口图纸数据
     */
    fun initFloorDrawData(checkMainBean: List<CheckNResMainBean>){
        mFloorDrawModule!!.clear()
        checkMainBean.forEach {
            mFloorDrawModule!!.add(FloorDrawingModule().also { b->
                b.mId = it.id
                b.mFloorName = it.floorName
                it.drawing!!.forEach { c->
                    b.mNameList!!.add(c)
                }
            })
        }
        if(mFloorDrawModule != null && mFloorDrawModule!!.size>0) {
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
            it.name = "损伤结果列表"
            (activity as CheckNonResidentsActivity).mCurrentDamageType.forEach { damageType ->
                it.menu.add(CheckMenuModule.Item().also {
                    it.name = damageType
                })
            }
        })

        damageBean?.forEach { damage->
            mMenuList?.forEach { menu->
                menu.menu.forEach { itemDmage->
                    if(damage.type == itemDmage.name){ // 匹配对应类型损伤
                        itemDmage.item.add(CheckMenuModule.Item.Mark().also { b->
                            var nameText:String?=""

                            if(damage.axisNote.isNullOrEmpty()){
                                damage.axisNoteList?.forEachIndexed { index, s ->
                                    if(index == 0){
                                        nameText+=s
                                    }else if(index == 2){
                                        nameText+="/"+s
                                    }else{
                                        nameText+="-"+s
                                    }
                                }
                            }else{
                                nameText = damage.axisNote
                            }
                            b.name = nameText
                            b.damage = damage
                        })
                    }
                }

            }

        }

        mBinding.toolLayout.setMenuModuleList(mMenuList)
            .setCheckMenuCallback(object : CheckMenuView2.CheckMenuCallback {
                override fun onClick(v: View?, damageType:String?) {
                    (activity as CheckNonResidentsActivity).setAddAnnotReF(-1L)
                    (activity as CheckNonResidentsActivity).addPDFDamageMark = false
                    (activity as CheckNonResidentsActivity).resetDamageInfo(null,damageType,false,false)
                }

                override fun onMarkClick(v: View?, damage: DamageV3Bean?, damageType:String?) {
                    (activity as CheckNonResidentsActivity).addPDFDamageMark = false
                    (activity as CheckNonResidentsActivity).resetDamageInfo(damage,damageType,false,false)
                }

                override fun onMarkRemoveClick(v: View?, damage: DamageV3Bean?, damageType: String?) {
                    AlertDialog.Builder(activity).setTitle("提示")
                        .setMessage("是否移除当前损伤记录")
                        .setPositiveButton(R.string.dialog_ok) { dialog, which ->
                            (activity as CheckNonResidentsActivity).removeDamage(damage,damageType)
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
        (activity as CheckNonResidentsActivity).choosePDF(data)
    }


}