package com.sribs.bdd.v3.ui.check.obd.fm

import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckObliquedeformationEditBinding
import com.sribs.bdd.v3.ui.check.bs.CheckBuildStructureActivity
import com.sribs.bdd.v3.ui.check.obd.CheckObliqueDeformationActivity
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.DamageV3Bean

@Route(path = ARouterPath.CHECK_OBLIQUE_DEFORMATION_Edit_FRAGMENT)
class CheckEditOBDFragment : BaseFragment(R.layout.fragment_check_obliquedeformation_edit){

    private val mBinding : FragmentCheckObliquedeformationEditBinding by bindView()

    /**
     * 创建损伤时间
     */
    private var mDamageCreateTime = -1L

    /**
     * 当前添加的mark
     */
    var mAddAnnotReF:Long = -1L

    override fun deinitView() {

    }

    override fun initView() {
        mBinding.checkEditH1.checkEditName.text="测量高度1(m)"
        mBinding.checkEditH1.checkEdit.hint="请输入测量高度"
        mBinding.checkEditH2.checkEditName.text="测量高度2(m)"
        mBinding.checkEditH2.checkEdit.hint="请输入测量高度"
        mBinding.checkEditQx1.checkEditName.text="倾斜量1(mm)"
        mBinding.checkEditQx1.checkEdit.hint="请输入倾斜量"
        mBinding.checkEditQx2.checkEditName.text="倾斜量2(mm)"
        mBinding.checkEditQx2.checkEdit.hint="请输入倾斜量"

        mBinding.checkObdDoubleRadio.isSelected = true
        mBinding.checkObdOnlyRadio.isSelected = false

        mBinding.checkObdMenuLayout.checkObdMenuScale.setOnClickListener {
            (activity as CheckObliqueDeformationActivity).scaleDamageInfo(1)
        }
        mBinding.checkObdMenuLayout.checkObdMenuClose.setOnClickListener {
            (activity as CheckObliqueDeformationActivity).setVpCurrentItem(0)
        }
        mBinding.checkObdOnlyRadio.setOnClickListener {
            mBinding.checkObdOnlyRadio.isSelected = true
            mBinding.checkObdDoubleRadio.isSelected = false
            mBinding.checkEditH2.checkEdit.setText("")
            mBinding.checkEditH2.checkEdit.isEnabled = false
            mBinding.checkEditQx2.checkEdit.setText("")
            mBinding.checkEditQx2.checkEdit.isEnabled = false
        }
        mBinding.checkObdOnlyText.setOnClickListener {
            mBinding.checkObdOnlyRadio.isSelected = true
            mBinding.checkObdDoubleRadio.isSelected = false
            mBinding.checkEditH2.checkEdit.setText("")
            mBinding.checkEditH2.checkEdit.isEnabled = false
            mBinding.checkEditQx2.checkEdit.setText("")
            mBinding.checkEditQx2.checkEdit.isEnabled = false
        }
        mBinding.checkObdDoubleRadio.setOnClickListener {
            mBinding.checkObdOnlyRadio.isSelected = false
            mBinding.checkObdDoubleRadio.isSelected = true
            mBinding.checkEditH2.checkEdit.setText("")
            mBinding.checkEditH2.checkEdit.isEnabled = true
            mBinding.checkEditQx2.checkEdit.isEnabled = true
            mBinding.checkEditQx2.checkEdit.setText("")
        }
        mBinding.checkObdDoubleText.setOnClickListener {
            mBinding.checkObdOnlyRadio.isSelected = false
            mBinding.checkObdDoubleRadio.isSelected = true
            mBinding.checkEditH2.checkEdit.setText("")
            mBinding.checkEditH2.checkEdit.isEnabled = true
            mBinding.checkEditQx2.checkEdit.isEnabled = true
            mBinding.checkEditQx2.checkEdit.setText("")
        }
        /**
         * 生成损伤记录
         */
        mBinding.checkObdConfirm.setOnClickListener {
            if(mBinding.checkObdOnlyRadio.isSelected){
                if(mBinding.checkEditH1.checkEdit.text.isNullOrEmpty()){
                    showToast("请输入测量高度")
                    return@setOnClickListener
                }
                if( mBinding.checkEditQx1.checkEdit.text.isNullOrEmpty()){
                    showToast("请输入倾斜量")
                    return@setOnClickListener
                }
            }else if(mBinding.checkObdDoubleRadio.isSelected){
                if(mBinding.checkEditH1.checkEdit.text.isNullOrEmpty()){
                    showToast("请输入测量高度")
                    return@setOnClickListener
                }
                if(mBinding.checkEditQx1.checkEdit.text.isNullOrEmpty()){
                    showToast("请输入倾斜量")
                    return@setOnClickListener
                }
                if(mBinding.checkEditH2.checkEdit.text.isNullOrEmpty()){
                    showToast("请输入测量高度")
                    return@setOnClickListener
                }
                if( mBinding.checkEditQx2.checkEdit.text.isNullOrEmpty()){
                    showToast("请输入倾斜量")
                    return@setOnClickListener
                }
            }
            if(mBinding.checkEditPoint.checkEdit.text.isNullOrEmpty()){
                showToast("请输入点位名称")
                return@setOnClickListener
            }
            if(mBinding.checkObdHintText.text.isNullOrEmpty()){
                showToast("请添加房屋倾斜点备注")
                return@setOnClickListener
            }
            mAddAnnotReF =  (activity as CheckObliqueDeformationActivity).mCurrentAddAnnotReF
            var damage = DamageV3Bean(
                -1,
                (activity as CheckObliqueDeformationActivity).mCurrentDrawing!!.drawingID,
                "点位",
                0,
                mAddAnnotReF,
                mBinding.checkObdHintText.text.toString(),
                if(mDamageCreateTime<0) System.currentTimeMillis() else mDamageCreateTime,
                "方向 还未添加方向指示",
                mBinding.checkEditPoint.checkEdit.text.toString(),
                mBinding.checkEditH1.checkEdit.text.toString(),
                mBinding.checkEditH2.checkEdit.text.toString(),
                mBinding.checkEditQx1.checkEdit.text.toString(),
                mBinding.checkEditQx2.checkEdit.text.toString()
            )
            (context as CheckObliqueDeformationActivity).saveDamage(damage)
        }
    }

    /**
     * 根据是否为null来设置数据
     */
    fun resetView(damageV3Bean: DamageV3Bean?) {
        LogUtils.d("resetView "+damageV3Bean)
        if(damageV3Bean == null){
            mBinding.checkObdOnlyRadio.isSelected = false
            mBinding.checkObdDoubleRadio.isSelected = true
            mBinding.checkEditH1.checkEdit.setText("")
            mBinding.checkEditQx1.checkEdit.setText("")
            mBinding.checkEditH2.checkEdit.setText("")
            mBinding.checkEditQx2.checkEdit.setText("")
            mBinding.checkEditH2.checkEdit.isEnabled = true
            mBinding.checkEditQx2.checkEdit.isEnabled = true

            mBinding.checkEditPoint.checkEdit.setText("")

            mBinding.checkObdHintText.setText("")
            mDamageCreateTime = -1L
        }else{

            if(!damageV3Bean.measure2Height.isNullOrEmpty() && !damageV3Bean.tilt2.isNullOrEmpty()){
                mBinding.checkObdOnlyRadio.isSelected = false
                mBinding.checkObdDoubleRadio.isSelected = true
                mBinding.checkEditH2.checkEdit.isEnabled = true
                mBinding.checkEditQx2.checkEdit.isEnabled = true
                mBinding.checkEditH1.checkEdit.setText(damageV3Bean.measure1Height)
                mBinding.checkEditQx1.checkEdit.setText(damageV3Bean.tilt1)
                mBinding.checkEditH2.checkEdit.setText(damageV3Bean.measure2Height)
                mBinding.checkEditQx2.checkEdit.setText(damageV3Bean.tilt2)
            }else{
                mBinding.checkObdOnlyRadio.isSelected = true
                mBinding.checkObdDoubleRadio.isSelected = false
                mBinding.checkEditH2.checkEdit.isEnabled = false
                mBinding.checkEditQx2.checkEdit.isEnabled = false
                mBinding.checkEditH1.checkEdit.setText(damageV3Bean.measure1Height)
                mBinding.checkEditQx1.checkEdit.setText(damageV3Bean.tilt1)
                mBinding.checkEditH2.checkEdit.setText("")
                mBinding.checkEditQx2.checkEdit.setText("")
            }
            mBinding.checkEditPoint.checkEdit.setText(damageV3Bean.pointName)
            mBinding.checkObdHintText.setText(damageV3Bean.note)
            mDamageCreateTime = damageV3Bean.createTime
        }
    }
}