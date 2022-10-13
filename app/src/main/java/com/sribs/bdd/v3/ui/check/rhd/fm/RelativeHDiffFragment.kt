package com.sribs.bdd.v3.ui.check.rhd.fm

import android.view.View
import android.widget.RelativeLayout
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
import kotlinx.android.synthetic.main.fragment_relative_h_diff.view.*
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

    override fun deinitView() {
        if (mBinding.checkTableInfo != null)
            mBinding.checkTableInfo.removeAllViews()
    }

    override fun initView() {
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
        }

        //计算
        mBinding.checkButJs.setOnClickListener {
            mBinding.checkTableInfo.calculate(false)
        }

        //添加测点
        mBinding.checkButTjcd.setOnClickListener {
            mBinding.checkTableInfo.addPoint()
        }
    }

    /**
     * 添加组和测点名
     */
    fun addPointBean(group:String,point:String,annotRef: Long){
        var itemList = ArrayList<RelativeHDiffPointBean.Item>()
        var item = RelativeHDiffPointBean.Item()
        item.name =point
        item.annotRef = annotRef
        itemList.add(item)
        var relativeHDiffPointBean = RelativeHDiffPointBean().also {
            it.name = group
            it.menu = itemList
        }
        mBinding.checkTableInfo.addPointBean(relativeHDiffPointBean)
    }

    /**
     * 删除mark 和添加的组和名
     */
    fun removePointBean(group:String,point:String,annotRef: Long){
        mBinding.checkTableInfo.remove(group,point,annotRef)
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