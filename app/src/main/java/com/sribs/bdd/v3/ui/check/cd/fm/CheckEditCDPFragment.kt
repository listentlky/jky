package com.sribs.bdd.v3.ui.check.cd.fm

import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.donkingliang.imageselector.utils.ImageSelector
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckComponentdetectionPlateEditBinding
import com.sribs.bdd.v3.popup.FloorDrawingSpinnerPopupWindow
import com.sribs.bdd.v3.ui.check.cd.CheckComponentDetectionActivity
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.bdd.v3.util.SpannableUtils
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.utils.FileUtil
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * 构件检测-板
 */
@Route(path = ARouterPath.CHECK_COMPONENT_DETECTION_PLATE_FRAGMENT)
class CheckEditCDPFragment : BaseFragment(R.layout.fragment_check_componentdetection_plate_edit),
    FloorDrawingSpinnerPopupWindow.FloorDrawItemClickCallback {

    private val mBinding: FragmentCheckComponentdetectionPlateEditBinding by bindView()


    private var mtypePicList: ArrayList<String>? = ArrayList()

    private var currentRealPicType = ""
    private var currentRealPicType2 = ""

    private var currentDesignPicType = ""
    private var currentDesignPicType2 = ""

    private val REQUEST_PLATE_REAL_TAKE_PHOTO = 22 //选择图片
    private val REQUEST_PLATE_DESIGN_TAKE_PHOTO = 23 //选择图片

    private var mRightRealPicSrc: String = ""
    private var mRightDesignPicSrc: String = ""

    private  var mCheckLeftDesignStatus = false
    private  var mCheckRightDesignStatus = false
    private  var mCheckRightRealStatus = false

    /**
     * 创建损伤时间
     */
    private var mDamageCreateTime = -1L

    /**
     * 当前添加的mark
     */
    var mAddAnnotReF: Long = -1L


    override fun deinitView() {

    }

    override fun initView() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)  {
        mtypePicList!!.addAll(Arrays.asList("A", "B"))

        currentRealPicType = mtypePicList!!.get(0)
        currentRealPicType2 = mtypePicList!!.get(0)
        currentDesignPicType = mtypePicList!!.get(0)
        currentDesignPicType2 = mtypePicList!!.get(0)

        mBinding.checkCdpSubtitle1.checkEditName.text = SpannableUtils.setTextColor("*板名称",
            0,1, Color.RED)
        mBinding.checkCdpSubtitle1.checkEdit.hint = "请输入板名称"

        mBinding.checkCdpSubtitle2.checkEditName.text = SpannableUtils.setTextColor("*轴线",
            0,1, Color.RED)
        mBinding.checkCdpSubtitle2Second.checkEditName.text = SpannableUtils.setTextColor("*轴线",
            0,1, Color.RED)
        mBinding.checkCdpSubtitle2Second.checkEdit.hint = "请输入轴线"

        mBinding.checkCdpPlateLeftRealUi.checkCdpLeftMenu.checkCpdLeftMenu1.text = "实测板厚度"
        mBinding.checkCdpPlateLeftRealUi.checkCdpLeftMenu.checkCpdLeftMenu2.text = "设计板厚度"

        mBinding.checkCdpPlateLeftDesignUi.checkCdpLeftMenu.checkCpdLeftMenu1.text = "实测板厚度"
        mBinding.checkCdpPlateLeftDesignUi.checkCdpLeftMenu.checkCpdLeftMenu2.text = "设计板厚度"


        mBinding.checkCdpPlateLeftRealUi.checkCdpPlateLeftRealTv.text = "实测 板厚度"
        mBinding.checkCdpPlateLeftDesignUi.checkCdpPlateLeftDesignTv.text = "设计 板厚度"

        mBinding.checkCdpPlateRightRealUi.checkCdpRightMenu.checkCpdLeftMenu1.text = "实测配筋"
        mBinding.checkCdpPlateRightRealUi.checkCdpRightMenu.checkCpdLeftMenu2.text = "设计配筋"

        mBinding.checkCdpPlateRightDesignUi.checkCdpRightMenu.checkCpdLeftMenu1.text = "实测配筋"
        mBinding.checkCdpPlateRightDesignUi.checkCdpRightMenu.checkCpdLeftMenu2.text = "设计配筋"

        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEditName.text =
            "实测板底东西向钢筋"

        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkCpdLeftRealSpinner2.setSpinnerData(
            mtypePicList
        ).setSpinnerTextGravity(Gravity.CENTER).setSpinnerCallback { position: Int ->
            LogUtils.d("当前选择：$position")
            currentRealPicType = mtypePicList!!.get(position)
        }.setTypeface(Typeface.createFromAsset(activity?.assets, "fonts/SJQY.cb6e0829.TTF"))
            .build()


        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEditName.text =
            "实测板底南北向钢筋"

        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkCpdLeftRealSpinner2.setSpinnerData(
            mtypePicList
        ).setSpinnerTextGravity(Gravity.CENTER).setSpinnerCallback { position: Int ->
            LogUtils.d("当前选择：$position")
            currentRealPicType2 = mtypePicList!!.get(position)
        }.setTypeface(Typeface.createFromAsset(activity?.assets, "fonts/SJQY.cb6e0829.TTF"))
            .build()

        mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkEditName.text =
            "设计板底东西向钢筋"
        mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkCpdLeftRealSpinner2.setSpinnerData(
            mtypePicList
        ).setSpinnerTextGravity(Gravity.CENTER).setSpinnerCallback { position: Int ->
            LogUtils.d("当前选择：$position")
            currentDesignPicType = mtypePicList!!.get(position)
        }.setTypeface(Typeface.createFromAsset(activity?.assets, "fonts/SJQY.cb6e0829.TTF"))
            .build()

        mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkEditName.text =
            "设计板底南北向钢筋"
        mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkCpdLeftRealSpinner2.setSpinnerData(
            mtypePicList
        ).setSpinnerTextGravity(Gravity.CENTER).setSpinnerCallback { position: Int ->
            LogUtils.d("当前选择：$position")
            currentDesignPicType2 = mtypePicList!!.get(position)
        }.setTypeface(Typeface.createFromAsset(activity?.assets, "fonts/SJQY.cb6e0829.TTF"))
            .build()


        /**
         * menu关闭
         */
        mBinding.checkMenuLayout.checkObdMenuClose.setOnClickListener {
            (context as CheckComponentDetectionActivity).setVpCurrentItem(0)
        }

        /**
         * menu 缩小
         */
        mBinding.checkMenuLayout.checkObdMenuScale.setOnClickListener {
            (context as CheckComponentDetectionActivity).scaleDamageInfo(4)
        }

        //左侧-实测界面
        mBinding.checkCdpPlateLeftRealUi.checkCdpLeftMenu.checkCpdLeftMenu2.setOnClickListener {
            mBinding.checkCdpPlateLeftRealUi.parent.visibility = View.INVISIBLE
            mBinding.checkCdpPlateLeftDesignUi.parent.visibility = View.VISIBLE
        }


        mBinding.checkCdpPlateLeftDesignUi.checkCdpLeftMenu.checkCpdLeftMenu1.setOnClickListener {
            mBinding.checkCdpPlateLeftRealUi.parent.visibility = View.VISIBLE
            mBinding.checkCdpPlateLeftDesignUi.parent.visibility = View.INVISIBLE
        }

        mBinding.checkCdpPlateRightRealUi.checkCdpRightMenu.checkCpdLeftMenu2.setOnClickListener {
            mBinding.checkCdpPlateRightRealUi.parent.visibility = View.INVISIBLE
            mBinding.checkCdpPlateRightDesignUi.parent.visibility = View.VISIBLE
        }

        mBinding.checkCdpPlateRightDesignUi.checkCdpRightMenu.checkCpdLeftMenu1.setOnClickListener {
            mBinding.checkCdpPlateRightRealUi.parent.visibility = View.VISIBLE
            mBinding.checkCdpPlateRightDesignUi.parent.visibility = View.INVISIBLE
        }


        mBinding.checkCdpSubtitleChange.setOnClickListener {

            if (mBinding.checkCdpSubtitle2.content.visibility == View.VISIBLE) {
                mBinding.checkCdpSubtitle2.content.visibility = View.INVISIBLE
                mBinding.checkCdpSubtitle2Second.content.visibility = View.VISIBLE
            } else {
                mBinding.checkCdpSubtitle2.content.visibility = View.VISIBLE
                mBinding.checkCdpSubtitle2Second.content.visibility = View.INVISIBLE
            }
        }


        var mAxisNote: String = "" // 轴线
        var mAxisNoteList: ArrayList<String>? = ArrayList() // 多轴线

        mBinding.checkCdpPlateRightRealUi.checkCdpPlatePic.setOnClickListener {
            openImgSelector(REQUEST_PLATE_REAL_TAKE_PHOTO)
        }

        /* mBinding.checkCdpPlateRightDesignUi.checkCdpPlatePic.setOnClickListener {
             openImgSelector(REQUEST_PLATE_DESIGN_TAKE_PHOTO)
         }*/

        mBinding.checkCdpPlateRightRealUi.checkCdpRightMenu.checkCpdLeftMenu3.visibility = View.VISIBLE
        mBinding.checkCdpPlateRightDesignUi.checkCdpRightMenu.checkCpdLeftMenu3.visibility = View.VISIBLE





        checkLeftDesignStatus(false)
        checkRightDesignStatus(false)
        checkRightRealStatus(true)

        mBinding.checkCdpPlateLeftRealUi.checkCdpLeftMenu.checkCpdLeftMenu4.setOnCheckedChangeListener { buttonView, isChecked ->
            mBinding.checkCdpPlateLeftDesignUi.checkCdpLeftMenu.checkCpdLeftMenu4.isChecked = isChecked
            checkLeftDesignStatus(isChecked)
        }

        mBinding.checkCdpPlateLeftDesignUi.checkCdpLeftMenu.checkCpdLeftMenu4.setOnCheckedChangeListener { buttonView, isChecked ->
            mBinding.checkCdpPlateLeftRealUi.checkCdpLeftMenu.checkCpdLeftMenu4.isChecked = isChecked
            checkLeftDesignStatus(isChecked)
        }

        mBinding.checkCdpPlateRightDesignUi.checkCdpRightMenu.checkCpdLeftMenu4.setOnCheckedChangeListener { buttonView, isChecked ->
            mBinding.checkCdpPlateRightRealUi.checkCdpRightMenu.checkCpdLeftMenu4.isChecked = isChecked
            checkRightDesignStatus(isChecked)
        }


        mBinding.checkCdpPlateRightRealUi.checkCdpRightMenu.checkCpdLeftMenu4.setOnCheckedChangeListener { buttonView, isChecked ->
            mBinding.checkCdpPlateRightDesignUi.checkCdpRightMenu.checkCpdLeftMenu4.isChecked = isChecked
            checkRightDesignStatus(isChecked)
        }


        mBinding.checkCdpPlateRightRealUi.checkCdpRightMenu.checkCpdLeftMenu3.setOnCheckedChangeListener { buttonView, isChecked ->
            mBinding.checkCdpPlateRightDesignUi.checkCdpRightMenu.checkCpdLeftMenu3.isChecked = isChecked
            checkRightRealStatus(isChecked)
        }

        mBinding.checkCdpPlateRightDesignUi.checkCdpRightMenu.checkCpdLeftMenu3.setOnCheckedChangeListener { buttonView, isChecked ->
            mBinding.checkCdpPlateRightRealUi.checkCdpRightMenu.checkCpdLeftMenu3.isChecked = isChecked
            checkRightRealStatus(isChecked)
        }



        //完成
        mBinding.checkCdpSubtitleConfirm.setOnClickListener {

            if (mBinding.checkCdpSubtitle1.checkEdit.text.toString().isNullOrEmpty()) {
                showToast("请输入板名称")
                return@setOnClickListener
            }


            if (mBinding.checkCdpSubtitle2.content.visibility == View.VISIBLE) {
                if (mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis1.text.isNullOrEmpty() ||
                    mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis2.text.isNullOrEmpty() ||
                    mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis3.text.isNullOrEmpty() ||
                    mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis4.text.isNullOrEmpty()
                ) {
                    showToast("请完善轴线")
                    return@setOnClickListener
                }

            } else {
                if (mBinding.checkCdpSubtitle2Second.checkEdit.text.isNullOrEmpty()) {
                    showToast("请输入轴线")
                    return@setOnClickListener
                }
                mAxisNote = mBinding.checkCdpSubtitle2Second.checkEdit.text.toString()
            }


            mAddAnnotReF = (activity as CheckComponentDetectionActivity).mCurrentAddAnnotReF
            //TODO 图片加载


            var axis:String
            var axisList: java.util.ArrayList<String>
            if (mBinding.checkCdpSubtitle2Second.content.visibility ==View.VISIBLE){
                axis = mBinding.checkCdpSubtitle2Second.checkEdit.text.toString()
                axisList = arrayListOf("","","","")
            }else{
                axis = ""
                axisList = arrayListOf(
                    mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis1.text.toString(),
                    mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis2.text.toString(),
                    mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis3.text.toString(),
                    mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis4.text.toString()
                )
            }


            var damage = DamageV3Bean(
                -1,
                (activity as CheckComponentDetectionActivity).mCurrentDrawing!!.drawingID!!,
                "板",
                0,
                mAddAnnotReF,
                mBinding.checkCdpPlateRightRealUi.checkCdpLeftRealRemarkContent.text.toString(),
                if (mDamageCreateTime < 0) System.currentTimeMillis() else mDamageCreateTime,
                mBinding.checkCdpPlateLeftRealUi.checkCdpPlateLeftRealContent.text.toString(),
                mBinding.checkCdpPlateLeftDesignUi.checkCdpPlateLeftDesignContent.text.toString(),
                mBinding.checkCdpSubtitle1.checkEdit.text.toString(),
                axis,
                axisList,
                arrayListOf(
                    currentRealPicType,
                    mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit4.text.toString(),
                    mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit2.text.toString(),
                    mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit3.text.toString(),

                ),
                arrayListOf(
                    currentRealPicType2,
                    mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit4.text.toString(),
                    mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit2.text.toString(),
                    mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit3.text.toString(),

                ),
                mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealProtect.checkEditProtect.text.toString(),
                mBinding.checkCdpPlateRightRealUi.checkCdpLeftRealRemarkContent.text.toString(),
                arrayListOf(FileUtil.getFileName(mRightRealPicSrc) ?: "", mRightRealPicSrc),
                arrayListOf(
                    currentDesignPicType,
                    mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkEdit3.text.toString(),
                    mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkEdit2.text.toString()

                ),
                arrayListOf(
                    currentDesignPicType2,
                    mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkEdit3.text.toString(),
                    mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkEdit2.text.toString()

                ),
                mBinding.checkCdpPlateRightDesignUi.checkCdpLeftRealRemarkContent.text.toString(),
                arrayListOf(FileUtil.getFileName(mRightDesignPicSrc) ?: "", mRightDesignPicSrc),
                arrayListOf(mCheckLeftDesignStatus.toString(),mCheckRightRealStatus.toString(),mCheckRightDesignStatus.toString())
            )

            (context as CheckComponentDetectionActivity).saveDamage(damage)


        }


    }


    fun checkLeftDesignStatus(isEnable: Boolean) {

        mCheckLeftDesignStatus = isEnable

        mBinding.checkCdpPlateLeftDesignUi.checkCdpPlateLeftDesignContent.isEnabled = isEnable

    }


    fun checkRightRealStatus(isEnable: Boolean) {

        mCheckRightRealStatus = isEnable

        mBinding.checkCdpPlateRightRealUi.checkCdpLeftRealRemarkContent.isEnabled = isEnable

        mBinding.checkCdpPlateRightRealUi.checkCdpPlatePic.isEnabled = isEnable
        mBinding.checkCdpPlateRightRealUi.checkCdpPlatePic.isClickable = isEnable

        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkCpdLeftRealSpinner2.isEnabled = isEnable
        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkCpdLeftRealSpinner2.isClickable = isEnable
        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit2.isEnabled = isEnable
        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit3.isEnabled = isEnable
        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit4.isEnabled = isEnable


        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkCpdLeftRealSpinner2.isEnabled = isEnable
        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkCpdLeftRealSpinner2.isClickable = isEnable
        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit2.isEnabled = isEnable
        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit3.isEnabled = isEnable
        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit4.isEnabled = isEnable

        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealProtect.checkEditProtect.isEnabled = isEnable
    }

    fun checkRightDesignStatus(isEnable: Boolean) {

        mCheckRightDesignStatus = isEnable

       mBinding.checkCdpPlateRightDesignUi.checkCdpLeftRealRemarkContent.isEnabled =isEnable
       mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkEdit2.isEnabled = isEnable
       mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkEdit3.isEnabled = isEnable

        mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkEdit2.isEnabled = isEnable
        mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkEdit3.isEnabled = isEnable

    }



    /**
     * 根据是否为null来设置数据
     */
    fun resetView(damageV3Bean: DamageV3Bean?) {
        mBinding.checkCdpSubtitle1.checkEdit.setText((context as CheckComponentDetectionActivity).mCurrentDrawing!!.floorName + "板")
        LogUtils.d("重新resetView：" + damageV3Bean)

        mBinding.checkCdpPlateLeftRealUi.parent.visibility = View.VISIBLE
        mBinding.checkCdpPlateLeftDesignUi.parent.visibility = View.INVISIBLE

        mBinding.checkCdpPlateRightRealUi.parent.visibility = View.VISIBLE
        mBinding.checkCdpPlateRightDesignUi.parent.visibility = View.INVISIBLE

        if (damageV3Bean == null) {
            currentRealPicType = mtypePicList!!.get(0)
            currentRealPicType2 = mtypePicList!!.get(0)
            currentDesignPicType = mtypePicList!!.get(0)
            currentDesignPicType2 = mtypePicList!!.get(0)
            mBinding.checkCdpSubtitle2.content.visibility = View.VISIBLE
            mBinding.checkCdpSubtitle2Second.content.visibility = View.INVISIBLE

            mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis1.setText("")
            mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis2.setText("")
            mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis3.setText("")
            mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis4.setText("")
            mBinding.checkCdpSubtitle2Second.checkEdit.setText("")


            mBinding.checkCdpPlateLeftRealUi.checkCdpPlateLeftRealContent.setText("")
            mBinding.checkCdpPlateLeftDesignUi.checkCdpPlateLeftDesignContent.setText("")

            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkCpdLeftRealSpinner2.setSelect(
                0
            )
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkCpdLeftRealSpinner2.setText(
                mtypePicList!!.get(0)
            )
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit2.setText("")
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit3.setText("")
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit4.setText("")

            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkCpdLeftRealSpinner2.setSelect(
                0
            )
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkCpdLeftRealSpinner2.setText(
                mtypePicList!!.get(0)
            )
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit2.setText("")
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit3.setText("")
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit4.setText("")

            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealProtect.checkEditProtect.setText(
                ""
            )

            mRightRealPicSrc = ""
            mRightDesignPicSrc = ""
            mBinding.checkCdpPlateRightRealUi.checkCdpLeftRealRemarkContent.setText("")
            mBinding.checkCdpPlateRightRealUi.checkCdpPlatePic.setImageResource(R.color.gray)

            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkCpdLeftRealSpinner2.setSelect(
                0
            )
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkCpdLeftRealSpinner2.setText(
                mtypePicList!!.get(0)
            )
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkEdit2.setText("")
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkEdit3.setText("")

            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkCpdLeftRealSpinner2.setSelect(
                0
            )
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkCpdLeftRealSpinner2.setText(
                mtypePicList!!.get(0)
            )
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkEdit2.setText("")
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkEdit3.setText("")

            mBinding.checkCdpPlateRightDesignUi.checkCdpLeftRealRemarkContent.setText("")
            //     mBinding.checkCdpPlateRightDesignUi.checkCdpPlatePic.setImageResource(R.color.gray)


            checkLeftDesignStatus(false)
            checkRightDesignStatus(false)
            checkRightRealStatus(true)

            mBinding.checkCdpPlateRightRealUi.checkCdpRightMenu.checkCpdLeftMenu3.visibility = View.VISIBLE
            mBinding.checkCdpPlateRightDesignUi.checkCdpRightMenu.checkCpdLeftMenu3.visibility = View.VISIBLE


            mBinding.checkCdpPlateLeftRealUi.checkCdpLeftMenu.checkCpdLeftMenu4.isChecked = false
            mBinding.checkCdpPlateLeftDesignUi.checkCdpLeftMenu.checkCpdLeftMenu4.isChecked = false

            mBinding.checkCdpPlateRightRealUi.checkCdpRightMenu.checkCpdLeftMenu4.isChecked = false
            mBinding.checkCdpPlateRightDesignUi.checkCdpRightMenu.checkCpdLeftMenu4.isChecked = false


            mDamageCreateTime = -1L
        } else {

            mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis1.setText("")
            mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis2.setText("")
            mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis3.setText("")
            mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis4.setText("")
            mBinding.checkCdpSubtitle2Second.checkEdit.setText("")

            if ("true".equals(damageV3Bean.plateCheckStatus!!.get(0))){
                mBinding.checkCdpPlateLeftRealUi.checkCdpLeftMenu.checkCpdLeftMenu4.isChecked = true
                mBinding.checkCdpPlateLeftDesignUi.checkCdpLeftMenu.checkCpdLeftMenu4.isChecked = true
                checkLeftDesignStatus(true)
            }else{
                mBinding.checkCdpPlateLeftRealUi.checkCdpLeftMenu.checkCpdLeftMenu4.isChecked = false
                mBinding.checkCdpPlateLeftDesignUi.checkCdpLeftMenu.checkCpdLeftMenu4.isChecked = false
                checkLeftDesignStatus(false)
            }

            if ("true".equals(damageV3Bean.plateCheckStatus!!.get(1))){
                mBinding.checkCdpPlateRightRealUi.checkCdpRightMenu.checkCpdLeftMenu3.isChecked = true
                mBinding.checkCdpPlateRightDesignUi.checkCdpRightMenu.checkCpdLeftMenu3.isChecked = true
                checkRightRealStatus(true)
            }else{
                checkRightRealStatus(false)
                mBinding.checkCdpPlateRightRealUi.checkCdpRightMenu.checkCpdLeftMenu3.isChecked = false
                mBinding.checkCdpPlateRightDesignUi.checkCdpRightMenu.checkCpdLeftMenu3.isChecked = false
            }

            if ("true".equals(damageV3Bean.plateCheckStatus!!.get(2))){
                mBinding.checkCdpPlateRightRealUi.checkCdpRightMenu.checkCpdLeftMenu4.isChecked = true
                mBinding.checkCdpPlateRightDesignUi.checkCdpRightMenu.checkCpdLeftMenu4.isChecked = true
                checkRightDesignStatus(true)

            }else{
                checkRightDesignStatus(false)
                mBinding.checkCdpPlateRightRealUi.checkCdpRightMenu.checkCpdLeftMenu4.isChecked = false
                mBinding.checkCdpPlateRightDesignUi.checkCdpRightMenu.checkCpdLeftMenu4.isChecked = false
            }


            mBinding.checkCdpPlateLeftRealUi.parent.visibility = View.VISIBLE


            currentRealPicType = damageV3Bean.realEastWestRebarList!!.get(0)
            currentRealPicType2 = damageV3Bean.realNorthSouthRebarList!!.get(0)
            currentDesignPicType = damageV3Bean.designEastWestRebarList!!.get(0)
            currentDesignPicType2 = damageV3Bean.designNorthSouthRebarList!!.get(0)

            mBinding.checkCdpSubtitle1.checkEdit.setText(damageV3Bean.plateName)
            if (!damageV3Bean.axisPlateNoteList!!.isNullOrEmpty() && !damageV3Bean.axisPlateNoteList!!.get(
                    0
                ).isNullOrEmpty()
            ) {
                mBinding.checkCdpSubtitle2.content.visibility = View.VISIBLE
                mBinding.checkCdpSubtitle2Second.content.visibility = View.INVISIBLE
                mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis1.setText(
                    damageV3Bean.axisPlateNoteList!!.get(
                        0
                    )
                )
                mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis2.setText(
                    damageV3Bean.axisPlateNoteList!!.get(
                        1
                    )
                )
                mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis3.setText(
                    damageV3Bean.axisPlateNoteList!!.get(
                        2
                    )
                )
                mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis4.setText(
                    damageV3Bean.axisPlateNoteList!!.get(
                        3
                    )
                )
            } else {
                mBinding.checkCdpSubtitle2.content.visibility = View.INVISIBLE
                mBinding.checkCdpSubtitle2Second.content.visibility = View.VISIBLE
                mBinding.checkCdpSubtitle2Second.checkEdit.setText(damageV3Bean.axisSingleNote)
            }

            mBinding.checkCdpPlateLeftRealUi.checkCdpPlateLeftRealContent.setText(damageV3Bean.realPlateThickness)

            mBinding.checkCdpPlateLeftDesignUi.checkCdpPlateLeftDesignContent.setText(damageV3Bean.designPlateThickness)


            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkCpdLeftRealSpinner2.setText(
                damageV3Bean.realEastWestRebarList!!.get(0)
            )
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkCpdLeftRealSpinner2.setSelect(
                mtypePicList!!.indexOf(damageV3Bean.realEastWestRebarList!!.get(0))
            )
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit2.setText(
                damageV3Bean.realEastWestRebarList!!.get(2)
            )
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit3.setText(
                damageV3Bean.realEastWestRebarList!!.get(3)
            )
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit4.setText(
                damageV3Bean.realEastWestRebarList!!.get(1)
            )


            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkCpdLeftRealSpinner2.setText(
                damageV3Bean.designEastWestRebarList!!.get(0)
            )
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkCpdLeftRealSpinner2.setSelect(
                mtypePicList!!.indexOf(damageV3Bean.designEastWestRebarList!!.get(0))
            )
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkEdit2.setText(
                damageV3Bean.designEastWestRebarList!!.get(2)
            )
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkEdit3.setText(
                damageV3Bean.designEastWestRebarList!!.get(1)
            )

            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkCpdLeftRealSpinner2.setText(
                damageV3Bean.realNorthSouthRebarList!!.get(0)
            )
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkCpdLeftRealSpinner2.setSelect(
                mtypePicList!!.indexOf(damageV3Bean.realNorthSouthRebarList!!.get(0))
            )
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit2.setText(
                damageV3Bean.realNorthSouthRebarList!!.get(2)
            )
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit3.setText(
                damageV3Bean.realNorthSouthRebarList!!.get(3)
            )
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit4.setText(
                damageV3Bean.realNorthSouthRebarList!!.get(1)
            )

            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkCpdLeftRealSpinner2.setText(
                damageV3Bean.designNorthSouthRebarList!!.get(0)
            )
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkCpdLeftRealSpinner2.setSelect(
                mtypePicList!!.indexOf(damageV3Bean.designNorthSouthRebarList!!.get(0))
            )
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkEdit2.setText(
                damageV3Bean.designNorthSouthRebarList!!.get(2)
            )
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkEdit3.setText(
                damageV3Bean.designNorthSouthRebarList!!.get(1)
            )

            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealProtect.checkEditProtect.setText(
                damageV3Bean.realProtectThickness
            )

            mBinding.checkCdpPlateRightRealUi.checkCdpLeftRealRemarkContent.setText(damageV3Bean.realNote)
            mBinding.checkCdpPlateRightDesignUi.checkCdpLeftRealRemarkContent.setText(damageV3Bean.designNote)


            mRightRealPicSrc = damageV3Bean.realPicture?.get(1) ?: ""
            mRightDesignPicSrc = damageV3Bean.designPicture?.get(1) ?: ""

            mBinding.checkCdpPlateRightRealUi.checkCdpPlatePic.setImageURI(
                Uri.fromFile(
                    File(
                        mRightRealPicSrc
                    )
                )
            )
            /*  mBinding.checkCdpPlateRightDesignUi.checkCdpPlatePic.setImageURI(
                  Uri.fromFile(
                      File(
                          mRightDesignPicSrc
                      )
                  )
              )*/

            mDamageCreateTime = damageV3Bean.createTime
        }
    }

    override fun onClick(data: DrawingV3Bean?) {
        (activity as CheckComponentDetectionActivity).choosePDF(data!!)
    }


    fun openImgSelector(requestCode: Int) {

        ImageSelector
            .builder()
            .onlyTakePhoto(false)
            .canPreview(true)
            .setSingle(true)
            .start(activity as CheckComponentDetectionActivity, requestCode)


    }

    fun setImageBitmap(filePath: String, type: Int) {
        when (type) {
            REQUEST_PLATE_REAL_TAKE_PHOTO -> {
                mBinding.checkCdpPlateRightRealUi.checkCdpPlatePic.setImageURI(
                    Uri.fromFile(
                        File(
                            filePath
                        )
                    )
                )
                mRightRealPicSrc = filePath
            }
            /*REQUEST_PLATE_DESIGN_TAKE_PHOTO -> {
                mBinding.checkCdpPlateRightDesignUi.checkCdpPlatePic.setImageURI(
                    Uri.fromFile(
                        File(
                            filePath
                        )
                    )
                )
                mRightDesignPicSrc = filePath
            }*/
        }
    }

}