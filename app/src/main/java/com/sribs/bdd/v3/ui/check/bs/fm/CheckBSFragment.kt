package com.sribs.bdd.v3.ui.check.bs.fm

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Route
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
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean

/**
 * create time: 2022/9/6
 * author: bruce
 * description:
 */
@Route(path = ARouterPath.CHECK_BUILD_STRUCTURE_FRAGMENT)
class CheckBSFragment : BaseFragment(R.layout.fragment_check_build_structure),
    FloorDrawingSpinnerPopupWindow.FloorDrawItemClickCallback {

    val mBinding: FragmentCheckBuildStructureBinding by bindView()

    private var mMenuList: ArrayList<CheckMenuModule> = ArrayList()

    private var mChooseFloorDraw: FloorDrawingSpinnerPopupWindow ? = null

    private var mFloorDrawModule:ArrayList<FloorDrawingModule>?= ArrayList()

    var mIsViewCreated = false

    override fun deinitView() {
        mIsViewCreated = false
    }
    override fun initView() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
            (activity as CheckBuildStructureActivity).startFabPDF()
        }

    }

    /**
     * ?????????????????????????????????
     */
    fun initFloorDrawData(checkMainBean: List<CheckBSMainBean>){
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
     * ??????????????????
     */
    fun setDamage(damageBean:ArrayList<DamageV3Bean>?){
        LogUtils.d("????????????????????? "+damageBean)
        mMenuList.clear()
        mMenuList.add(CheckMenuModule().also {
            it.name = "??????????????????"
            it.menu.add(CheckMenuModule.Item().also {
                it.name = "??????"
            })
            it.menu.add(CheckMenuModule.Item().also {
                it.name = "??????"
            })
        })
        if(damageBean != null) {
            LogUtils.d("??????????????????????????? "+damageBean!!.size)
            damageBean.forEach {
                when (it.type) {
                    (activity as CheckBuildStructureActivity).mCurrentDamageType[0] -> { // ??????
                        mMenuList!![0].menu!![0].item!!.add(CheckMenuModule.Item.Mark().also { b->
                            var nameText:String?=""
                            if(it.axisNote.isNullOrEmpty()){
                                it.axisNoteList?.forEachIndexed { index, s ->
                                    if(index == 0){
                                        nameText+=s
                                    }else if(index == 2){
                                        nameText+="/"+s
                                    }else{
                                        nameText+="-"+s
                                    }
                                }
                            }else{
                                nameText = it.axisNote
                            }
                            b.name = nameText
                            b.damage = it
                        })
                    }
                    (activity as CheckBuildStructureActivity).mCurrentDamageType[1] -> { //??????
                        mMenuList!![0].menu!![1].item!!.add(CheckMenuModule.Item.Mark().also { c->
                            var nameText:String?=""
                            if(it.axisNote.isNullOrEmpty()){
                                it.axisNoteList?.forEachIndexed { index, s ->
                                    if(index == 0){
                                        nameText+=s
                                    }else if(index == 1){
                                        nameText+="/"+s
                                    }else{
                                        nameText+="-"+s
                                    }
                                }
                            }else{
                                nameText = it.axisNote
                            }
                            c.name = nameText
                            c.damage = it
                        })
                    }
                }
            }
        }

        mBinding.toolLayout.setMenuModuleList(mMenuList)
            .setCheckMenuCallback(object : CheckMenuView2.CheckMenuCallback {
                override fun onClick(v: View?, damageType:String?) {
                    (activity as CheckBuildStructureActivity).setAddAnnotReF(-1L)
                    (activity as CheckBuildStructureActivity).addPDFDamageMark = false
                    (activity as CheckBuildStructureActivity).resetDamageInfo(null,damageType,false,false)
                }

                override fun onMarkClick(v: View?,damage: DamageV3Bean?,damageType:String?) {
                    (activity as CheckBuildStructureActivity).addPDFDamageMark = false
                    (activity as CheckBuildStructureActivity).resetDamageInfo(damage,damageType,false,false)
                }

                override fun onMarkRemoveClick(v: View?, damage: DamageV3Bean?, damageType: String?) {
                    AlertDialog.Builder(activity).setTitle("??????")
                        .setMessage("??????????????????????????????")
                        .setPositiveButton(R.string.dialog_ok) { dialog, which ->
                            (activity as CheckBuildStructureActivity).removeDamage(damage,damageType)
                        }.setNegativeButton(
                            R.string.dialog_cancel
                        ) { dialog, which ->

                        }
                        .show()
                }

            })!!.build()

    }

    fun getPDFView():PDFLayoutView{
        return mBinding.pdfLayout
    }

    fun getPDFParentView():RelativeLayout{
        return mBinding.pdfRoot
    }

    override fun onClick(data: DrawingV3Bean) {
        (activity as CheckBuildStructureActivity).choosePDF(data)
    }

}