package com.sribs.bdd.v3.ui.check.rhd.fm

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.core.widget.addTextChangedListener
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.radaee.reader.PDFLayoutView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentRelativeHDiffBinding
import com.sribs.bdd.v3.bean.CheckHDiffMainBean
import com.sribs.bdd.v3.popup.ChoosePicPopupWindow
import com.sribs.bdd.v3.ui.check.rhd.RelativeHDiffActivity
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.bean.db.RelativeHDiffPointBean
import kotlin.collections.ArrayList

/**
 * create time: 2022/9/7
 * author: bruce
 * description:
 */
@Route(path = com.sribs.common.ARouterPath.CHECK_RELATIVE_H_DIFF_FRAGMENT)
class RelativeHDiffFragment : BaseFragment(R.layout.fragment_relative_h_diff) {

    val mBinding: FragmentRelativeHDiffBinding by bindView()

    private var mDrawingV3Bean: ArrayList<DrawingV3Bean>? = ArrayList()

    private var mChoosePicPopupWindow: ChoosePicPopupWindow? = null

    var mIsViewCreated = false

    override fun deinitView() {
        mIsViewCreated = false
        LogUtils.d("deinitView")
        if (mBinding.checkTableInfo != null)
            mBinding.checkTableInfo.removeAllViews()
    }

    override fun initView() {
        LogUtils.d("initView")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mIsViewCreated = true
        LogUtils.d("onViewCreated "+mBinding)
        mBinding.checkLeftMenuRoot.setOnClickListener {
            when (mBinding.checkLeftLayout.visibility) {
                View.VISIBLE -> {
                    mBinding.checkLeftLayout.visibility = View.GONE
                    mBinding.checkLeftMenu.isSelected = false
                }
                View.GONE -> {
                    mBinding.checkLeftLayout.visibility = View.VISIBLE
                    mBinding.checkLeftMenu.isSelected = true
                }
            }
        }
        var popupWidth = resources.getDimensionPixelOffset(R.dimen._80sdp)
        if (mChoosePicPopupWindow == null) {
            mChoosePicPopupWindow = ChoosePicPopupWindow(
                context,
                popupWidth,
                mDrawingV3Bean,
                object : ChoosePicPopupWindow.PopupCallback {
                    override fun onSelect(mDrawingV3Bean: DrawingV3Bean) {
                        (activity as RelativeHDiffActivity).choosePDF(mDrawingV3Bean)
                    }

                })
        }
        mBinding.checkSelectIndex.setOnClickListener {
            mChoosePicPopupWindow?.notifyAdapter()
            mChoosePicPopupWindow?.showAsDropDown(
                mBinding.checkSelectIndex,
                -popupWidth - 10,
                -mBinding.checkSelectIndex.height
            )
        }

        mBinding.checkFab.setOnClickListener {
            (activity as RelativeHDiffActivity).startFabPDF()
        }

        //计算平差
        mBinding.checkButJspcz.setOnClickListener {
            mBinding.checkTableInfo.calculate(true)
            mBinding.checkTextBhc.text = "闭合差: "+mBinding.checkTableInfo.bhc
            (activity as RelativeHDiffActivity).mIsUpdateData = true
        }

        //计算
        mBinding.checkButJs.setOnClickListener {
            mBinding.checkTableInfo.calculate(false)
            (activity as RelativeHDiffActivity).mIsUpdateData = true
        }

        //添加测点
        mBinding.checkButTjcd.setOnClickListener {
            mBinding.checkTableInfo.addPoint()
            (activity as RelativeHDiffActivity).mIsUpdateData = true
        }

    }

    /**
     * 添加组和测点名
     */
    fun addPointBean(group:String,point:String,annotName: String){
        var itemList = ArrayList<RelativeHDiffPointBean.Item>()
        var item = RelativeHDiffPointBean.Item()
        item.name =point
        item.annotName = annotName
        itemList.add(item)
        var relativeHDiffPointBean = RelativeHDiffPointBean().also {
            it.name = group
            it.menu = itemList
        }
        mBinding.checkTableInfo.addPointBean(relativeHDiffPointBean)
    }

    /**
     * 编辑组和测点名
     * return  true 编辑  false 添加
     */
    fun addOrEditPointBean(group:String,point:String,randomColor:String,annotName: String):Pair<Boolean,String>{
        LogUtils.d("addOrEditPointBean： ${group}   ${point}  ${randomColor} ${annotName}")
        var currentColor = randomColor
        var isMatch = false
        mBinding.checkTableInfo.pointBean.forEach { g->
            LogUtils.d("g: "+g.name+" ; "+g.colorBg)
            if(group == g.name){
                currentColor = g.colorBg
            }
            g.menu.forEach { p->
                LogUtils.d("p: "+p.name)
                if(annotName == p.annotName){
                    isMatch = true
                    p.name = point
                    g.name = group
                }
            }
        }
        LogUtils.d("isMatch: "+isMatch)
        if(!isMatch){
            var itemList = ArrayList<RelativeHDiffPointBean.Item>()
            var item = RelativeHDiffPointBean.Item()
            item.name =point
            item.annotName = annotName
            itemList.add(item)
            var addPointColor = randomColor
            mBinding.checkTableInfo.pointBean.forEach { g->
                if(group == g.name){
                    addPointColor = g.colorBg
                }
            }
            currentColor = addPointColor
            var relativeHDiffPointBean = RelativeHDiffPointBean().also {
                it.name = group
                it.colorBg = addPointColor
                it.menu = itemList
            }
            mBinding.checkTableInfo.addPointBean(relativeHDiffPointBean)
        }
        return Pair(isMatch,currentColor)
    }

    /**
     * 删除mark 和添加的组和名
     */
    fun removePointBean(annotName: String){
        mBinding.checkTableInfo.remove(annotName)
    }

    fun getPointBean():List<RelativeHDiffPointBean>{
        return mBinding.checkTableInfo.pointBean
    }

    /**
     * 初始化选择窗口图纸数据
     */
    fun initFloorDrawData(checkHDiffMainBean: List<CheckHDiffMainBean>) {
        mDrawingV3Bean!!.clear()
        checkHDiffMainBean.forEach {
            mDrawingV3Bean!!.addAll(it.drawing!!)
        }
        if (mDrawingV3Bean != null && mDrawingV3Bean!!.size > 0) {
            mBinding.checkSelectIndex.text = mDrawingV3Bean!![0].fileName
        }
        LogUtils.d("mDrawingV3Bean"+mDrawingV3Bean)
    }

    /**
     * 根据是否为null来设置数据
     */
    fun resetView(damageV3Bean: List<DamageV3Bean>?) {
        LogUtils.d("resetView: "+damageV3Bean)
        mBinding.checkTableInfo.setDatas(damageV3Bean).build()
        if(damageV3Bean != null && damageV3Bean.size>0) {
            mBinding.checkBzEdit.setText(damageV3Bean.get(0).note)
            mBinding.checkTextBhc.text =  "闭合差: "+damageV3Bean.get(0).closeDiff
        }
    }

    fun getPDFView(): PDFLayoutView {
        return mBinding.pdfLayout
    }

    fun getPDFParentView(): RelativeLayout {
        return mBinding.pdfRoot
    }

}