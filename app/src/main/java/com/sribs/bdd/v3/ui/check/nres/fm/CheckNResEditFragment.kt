package com.sribs.bdd.v3.ui.check.nres.fm

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.donkingliang.imageselector.utils.ImageSelector
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckNonResEditBinding
import com.sribs.bdd.v3.ui.check.nres.CheckNonResidentsActivity
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.bdd.v3.util.SpannableUtils
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.utils.FileUtil
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * create time: 2022/11/15
 * author: bruce
 * description:
 */
@Route(path = com.sribs.common.ARouterPath.CHECK_NON_RESIDENTS_EDIT_FRAGMENT)
class CheckNResEditFragment: BaseFragment(R.layout.fragment_check_non_res_edit){

    private val mBinding: FragmentCheckNonResEditBinding by bindView()

    private var mJCList = Arrays.asList("石膏饼","刻痕")

    private var mSelectPosition = 0

    private var mDamagePicSrc: String = ""

    private var mDamageJCDPicSrc: String = ""

    private var mCurrentDamageType:String?= ""

    /**
     * 创建损伤时间
     */
    private var mDamageCreateTime = -1L

    override fun deinitView() {

    }

    override fun initView() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mBinding.checkZx.text = SpannableUtils.setTextColor(mBinding.checkZx.text.toString(),
            0,1, Color.RED)

        mBinding.checkCrackHLayout.checkEditName.text = "裂缝长度"
        mBinding.checkCrackHLayout.checkEdit.hint = "请输入裂缝长度"

        mBinding.checkCrackJcbhLayout.checkEditName.text = "监测编号"
        mBinding.checkCrackJcbhLayout.checkEdit.hint = "请输入裂缝编号"

        mBinding.checkCrackJccdLayout.checkEditName.text = "刻痕长度"
        mBinding.checkCrackJccdLayout.checkEdit.hint = "请输入刻痕长度"

        mBinding.checkCrackJckdLayout.checkEditName.text = "刻痕宽度"
        mBinding.checkCrackJckdLayout.checkEdit.hint = "请输入刻痕宽度"

        mBinding.checkSpinner.setSpinnerData(mJCList)
            .setSpinnerTextGravity(Gravity.CENTER)
            .setSpinnerBgRes(R.mipmap.icon_popup_bg)
            .setSpinnerCallback { position->
                mSelectPosition = position
                mBinding.checkCrackJccdLayout.checkEdit.setText("")
                if(position == 0){
                    mBinding.checkCrackJccdLayout.checkEditName.isEnabled = false
                    mBinding.checkCrackJccdLayout.checkEdit.isEnabled = false
                    mBinding.checkCrackJccdLayout.checkEditFh.isEnabled = false

                    mBinding.checkCrackJckdLayout.checkEditName.isEnabled = false
                    mBinding.checkCrackJckdLayout.checkEdit.isEnabled = false
                    mBinding.checkCrackJckdLayout.checkEditFh.isEnabled = false

                }else{
                    if(mBinding.checkBoxLfjcd.isChecked) {
                        mBinding.checkCrackJccdLayout.checkEditName.isEnabled = true
                        mBinding.checkCrackJccdLayout.checkEdit.isEnabled = true
                        mBinding.checkCrackJccdLayout.checkEditFh.isEnabled = true

                        mBinding.checkCrackJckdLayout.checkEditName.isEnabled = true
                        mBinding.checkCrackJckdLayout.checkEdit.isEnabled = true
                        mBinding.checkCrackJckdLayout.checkEditFh.isEnabled = true
                    }
                }
            }.build()

        mBinding.checkPhotoLayout.damagePhoto.setOnClickListener {  //添加照片
            openImgSelector((activity as CheckNonResidentsActivity).ADD_PHOTO_CODE)
        }

        mBinding.checkJcdPhotoLayout.damagePhoto.setOnClickListener {  //添加照片
            openImgSelector((activity as CheckNonResidentsActivity).ADD_LFJCD_PHOTO_CODE)
        }

        mBinding.checkBoxLf.setOnCheckedChangeListener { buttonView, isChecked ->
            setLfChecked(isChecked)
        }

        mBinding.checkBoxLfjcd.setOnCheckedChangeListener { buttonView, isChecked ->
            setLfJCDChecked(isChecked)
        }

        mBinding.checkCancel.setOnClickListener {
            (activity as CheckNonResidentsActivity).setVpCurrentItem(0)
        }

        mBinding.checkConfirm.setOnClickListener {
            if(mBinding.checkZxEdit.text.isNullOrEmpty()){
                showToast("请输入轴线")
                return@setOnClickListener
            }
            var damage = DamageV3Bean(
                -1,
                (activity as CheckNonResidentsActivity).mCurrentDrawing!!.drawingID!!,
                mCurrentDamageType,
                if (mDamageCreateTime < 0) System.currentTimeMillis() else mDamageCreateTime,
                mBinding.checkZxEdit.text.toString(),
                mBinding.checkNoteEdit.text.toString(),
                if(mDamagePicSrc.isNullOrEmpty()) ArrayList() else arrayListOf(FileUtil.getFileName(mDamagePicSrc)?:"",mDamagePicSrc),
                mBinding.checkBoxLf.isChecked,
                mBinding.checkCrackWLayout.checkEdit.text.toString(),
                mBinding.checkCrackHLayout.checkEdit.text.toString(),
                mBinding.checkBoxLfjcd.isChecked,
                mBinding.checkCrackJcbhLayout.checkEdit.text.toString(),
                mJCList[mSelectPosition],
                mSelectPosition,
                mBinding.checkCrackJccdLayout.checkEdit.text.toString(),
                mBinding.checkCrackJckdLayout.checkEdit.text.toString(),
                if(mDamageJCDPicSrc.isNullOrEmpty()) ArrayList() else arrayListOf(FileUtil.getFileName(mDamageJCDPicSrc)?:"",mDamageJCDPicSrc),
            )

            LogUtils.d("保存非居民损伤" + damage.toString())
            (context as CheckNonResidentsActivity).saveDamage(damage)
        }

    }

    private fun setLfChecked(isChecked:Boolean){

        mBinding.checkCrackWLayout.checkEditName.isEnabled = isChecked
        mBinding.checkCrackWLayout.checkEdit.isEnabled = isChecked
        mBinding.checkCrackWLayout.checkEdit.setText("")
        mBinding.checkCrackWLayout.checkEditFh.isEnabled = isChecked

        mBinding.checkCrackHLayout.checkEditName.isEnabled = isChecked

        mBinding.checkCrackHLayout.checkEdit.isEnabled = isChecked
        mBinding.checkCrackHLayout.checkEdit.setText("")
        mBinding.checkCrackHLayout.checkEditFh.text = "m"
        mBinding.checkCrackHLayout.checkEditFh.isEnabled = isChecked

    }

    private fun setLfJCDChecked(isChecked:Boolean){

        mBinding.checkCrackJcbhLayout.checkEditName.isEnabled = isChecked

        mBinding.checkCrackJcbhLayout.checkEdit.isEnabled = isChecked
        mBinding.checkCrackJcbhLayout.checkEdit.setText("")
        mBinding.checkCrackJcbhLayout.checkEditFh.visibility = View.GONE

        mBinding.checkJcffHint.isEnabled = isChecked
        mBinding.checkSpinner.setIsEnable(isChecked)

        if(!isChecked){
            mBinding.checkCrackJccdLayout.checkEditName.isEnabled = false
            mBinding.checkCrackJccdLayout.checkEdit.setText("")
            mBinding.checkCrackJccdLayout.checkEdit.isEnabled = false
            mBinding.checkCrackJccdLayout.checkEditFh.isEnabled = false

            mBinding.checkCrackJckdLayout.checkEditName.isEnabled = false
            mBinding.checkCrackJckdLayout.checkEdit.isEnabled = false
            mBinding.checkCrackJckdLayout.checkEdit.setText("")
            mBinding.checkCrackJckdLayout.checkEditFh.isEnabled = false
        }else{
            if(mSelectPosition == 1){
                mBinding.checkCrackJccdLayout.checkEditName.isEnabled = true
                mBinding.checkCrackJccdLayout.checkEdit.setText("")
                mBinding.checkCrackJccdLayout.checkEdit.isEnabled = true
                mBinding.checkCrackJccdLayout.checkEditFh.isEnabled = true

                mBinding.checkCrackJckdLayout.checkEditName.isEnabled = true
                mBinding.checkCrackJckdLayout.checkEdit.isEnabled = true
                mBinding.checkCrackJckdLayout.checkEdit.setText("")
                mBinding.checkCrackJckdLayout.checkEditFh.isEnabled = true
            }else{
                mBinding.checkCrackJccdLayout.checkEditName.isEnabled = false
                mBinding.checkCrackJccdLayout.checkEdit.setText("")
                mBinding.checkCrackJccdLayout.checkEdit.isEnabled = false
                mBinding.checkCrackJccdLayout.checkEditFh.isEnabled = false

                mBinding.checkCrackJckdLayout.checkEditName.isEnabled = false
                mBinding.checkCrackJckdLayout.checkEdit.isEnabled = false
                mBinding.checkCrackJckdLayout.checkEdit.setText("")
                mBinding.checkCrackJckdLayout.checkEditFh.isEnabled = false
            }
        }

        mBinding.checkJcdPicHint.isEnabled = isChecked
        mBinding.checkJcdPhotoLayout.damagePhoto.isEnabled = isChecked

    }

    /**
     * 根据是否为null来设置数据
     */
    fun resetView(damageV3Bean: DamageV3Bean?,type:String?) {
        this.mCurrentDamageType = type
        if(damageV3Bean == null){
            mBinding.checkZxEdit.setText("")
            mBinding.checkNoteEdit.setText("")
            mDamagePicSrc = ""
            mBinding.checkPhotoLayout.damagePhoto.setImageDrawable(null)
            mBinding.checkPhotoLayout.damagePhotoBg.visibility = View.VISIBLE
            mBinding.checkBoxLf.isChecked = false
            setLfChecked(false)
            mBinding.checkBoxLfjcd.isChecked = false
            mSelectPosition = 0
            mBinding.checkSpinner.setText(mJCList[mSelectPosition])
            setLfJCDChecked(false)
            mDamageJCDPicSrc = ""
            mBinding.checkJcdPhotoLayout.damagePhoto.setImageDrawable(null)
            mBinding.checkJcdPhotoLayout.damagePhotoBg.visibility = View.VISIBLE
            mDamageCreateTime = -1L
        }else{
            mBinding.checkZxEdit.setText(damageV3Bean.noResAxisNote)
            mBinding.checkNoteEdit.setText(damageV3Bean.noResNote)
            if(damageV3Bean.noResDamagePicList?.size!!>0){
                mDamagePicSrc = damageV3Bean.noResDamagePicList?.get(1)?:""
                mBinding.checkPhotoLayout.damagePhoto.setImageURI(Uri.fromFile(File(mDamagePicSrc)))
                mBinding.checkPhotoLayout.damagePhotoBg.visibility = View.GONE
            }else{
                mBinding.checkPhotoLayout.damagePhoto.setImageDrawable(null)
                mBinding.checkPhotoLayout.damagePhotoBg.visibility = View.VISIBLE
            }
            mBinding.checkBoxLf.isChecked = damageV3Bean.noResCrackBox?:false
            setLfChecked(mBinding.checkBoxLf.isChecked)
            mBinding.checkCrackWLayout.checkEdit.setText(damageV3Bean.noResCrackWidth)
            mBinding.checkCrackHLayout.checkEdit.setText(damageV3Bean.noResCrackHeight)

            mBinding.checkBoxLfjcd.isChecked = damageV3Bean.noResCrackPointBox?:false
            mSelectPosition = damageV3Bean.noResCrackPointMethodIndex?:0
            setLfJCDChecked(mBinding.checkBoxLfjcd.isChecked)
            mBinding.checkCrackJcbhLayout.checkEdit.setText(damageV3Bean.noResCrackPointId)
            mBinding.checkSpinner.setText(mJCList[mSelectPosition])
            mBinding.checkCrackJccdLayout.checkEdit.setText(damageV3Bean.noResCrackPointNickHeight)
            mBinding.checkCrackJckdLayout.checkEdit.setText(damageV3Bean.noResCrackPointNickWidth)

            if(damageV3Bean.noResCrackPointPicList?.size!!>0){
                mDamageJCDPicSrc = damageV3Bean.noResCrackPointPicList?.get(1)?:""
                mBinding.checkJcdPhotoLayout.damagePhoto.setImageURI(Uri.fromFile(File(mDamageJCDPicSrc)))
                mBinding.checkJcdPhotoLayout.damagePhotoBg.visibility = View.GONE
            }else{
                mBinding.checkJcdPhotoLayout.damagePhoto.setImageDrawable(null)
                mBinding.checkJcdPhotoLayout.damagePhotoBg.visibility = View.VISIBLE
            }
            mDamageCreateTime = damageV3Bean.createTime
        }
    }

    fun openImgSelector(requestCode: Int) {
        ImageSelector
            .builder()
            .onlyTakePhoto(false)
            .canPreview(true)
            .setSingle(true)
            .start(activity as CheckNonResidentsActivity, requestCode)

    }

    fun setImageBitmap(filePath: String, type: Int) {
        when (type) {
            (activity as CheckNonResidentsActivity).ADD_PHOTO_CODE -> {
                mBinding.checkPhotoLayout.damagePhoto.setImageURI(Uri.fromFile(File(filePath)))
                mBinding.checkPhotoLayout.damagePhotoBg.visibility = View.GONE
                mDamagePicSrc = filePath
            }

            (activity as CheckNonResidentsActivity).ADD_LFJCD_PHOTO_CODE -> {
                mBinding.checkJcdPhotoLayout.damagePhoto.setImageURI(Uri.fromFile(File(filePath)))
                mBinding.checkJcdPhotoLayout.damagePhotoBg.visibility = View.GONE
                mDamageJCDPicSrc = filePath
            }

        }
    }
}