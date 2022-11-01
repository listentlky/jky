package com.sribs.bdd.v3.ui.check.cd.fm

import android.graphics.Typeface
import android.net.Uri
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.donkingliang.imageselector.utils.ImageSelector
import com.sribs.bdd.R
import com.sribs.bdd.databinding.*
import com.sribs.bdd.v3.popup.FloorDrawingSpinnerPopupWindow
import com.sribs.bdd.v3.ui.check.cd.CheckComponentDetectionActivity
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.utils.FileUtil
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

@Route(path = ARouterPath.CHECK_COMPONENT_DETECTION_BEAM_FRAGMENT)
class CheckEditCDBFragment : BaseFragment(R.layout.fragment_check_componentdetection_beam_edit_new),
    FloorDrawingSpinnerPopupWindow.FloorDrawItemClickCallback {

    val mBinding: FragmentCheckComponentdetectionBeamEditNewBinding by bindView()

    private var currentLeftRealType = 0
    private var currentLeftRealType2 = 0

    private var currentLeftDesignType = 0
    private var currentLeftDesignType2 = 0

    private var currentRightRealType = 0
    private var currentRightRealType2 = 0
    private var currentRightRealType3 = 0
    private var currentRightRealType4 = 0

    private var currentRightDesignType = 0
    private var currentRightDesignType2 = 0
    private var currentRightDesignType3 = 0
    private var currentRightDesignType4 = 0

    private var mPicLeftRealList: ArrayList<String>? = ArrayList()
    private var mPicLeftDesignList: ArrayList<String>? = ArrayList()

    private var mTypeLeftList: ArrayList<String>? = ArrayList()
    private var mTypeLeftList2: ArrayList<String>? = ArrayList()
    private var mTypeRightList: ArrayList<String>? = ArrayList()
    private var mTypeRightList2: ArrayList<String>? = ArrayList()

    private var mPicList: ArrayList<String>? = ArrayList()

    private var mRightRealPicSrc: String = ""
    private var mRightDesignPicSrc: String = ""
    private var leftRealSectionTypeParamsList: ArrayList<String> = ArrayList()
    private var leftDesignSectionTypeParamsList: ArrayList<String> = ArrayList()


    private var rightRealSectionTypeParamsList: ArrayList<String> = ArrayList()
    private var rightDesignSectionTypeParamsList: ArrayList<String> = ArrayList()

    private  var mCheckLeftDesignStatus = false
    private  var mCheckRightDesignStatus = false
    private  var mCheckRightRealStatus = false

    private lateinit var leftRealView: FragmentCheckComponentdetectionBeamLeftRealEditBinding
    private lateinit var leftDesignView: FragmentCheckComponentdetectionBeamLeftDesignEditBinding
    private lateinit var rightRealView: FragmentCheckComponentdetectionBeamRightRealEditBinding
    private lateinit var rightDesignView: FragmentCheckComponentdetectionBeamRightDesignEditBinding


    /**
     * 创建损伤时间
     */
    private var mDamageCreateTime = -1L

    /**
     * 当前添加的mark
     */
    var mAddAnnotReF: Long = -1L


    lateinit var leftRealRectangleView: CheckComponentDetectionBeamLeftRectangleItemBinding
    lateinit var leftRealHView: ItemComponentDetectionBeamLeftHItemBinding
    lateinit var leftRealTView: ItemComponentDetectionBeamLeftTItemBinding
    lateinit var leftRealAnotherView: ItemComponentDetectionBeamLeftAnotherItemBinding

    lateinit var leftDesignRectangleView: CheckComponentDetectionBeamLeftRectangleItemBinding
    lateinit var leftDesignHView: ItemComponentDetectionBeamLeftHItemBinding
    lateinit var leftDesignTView: ItemComponentDetectionBeamLeftTItemBinding
    lateinit var leftDesignAnotherView: ItemComponentDetectionBeamLeftAnotherItemBinding

    lateinit var rightRealSingleParamsView: ItemComponentDetectionBeamRightRealSingleRowSteelBinding
    lateinit var rightRealDoubleParamsView: ItemComponentDetectionBeamRightRealSingleRowSteel2Binding

    lateinit var rightDesignSingleParamsView: ItemComponentDetectionBeamRightRealSingleRowSteelBinding
    lateinit var rightDesignDoubleParamsView: ItemComponentDetectionBeamRightRealSingleRowSteel2Binding


    private val REQUEST_BEAM_REAL_TAKE_PHOTO = 12 //
    private val REQUEST_CODE_BEAN_REAL_WHITE_FLLOR = 14 //实测梁-草图
    private val REQUEST_CODE_BEAN_DESIGN_WHITE_FLLOR = 15 //设计梁-草图

    override fun deinitView() {
    }


    override fun initView() {

        mTypeLeftList!!.addAll(Arrays.asList("总高", "净高"))
        mTypeLeftList2!!.addAll(Arrays.asList("矩形", "H型", "T型", "其他"))
        mPicList!!.addAll(Arrays.asList("A", "B"))
        mTypeRightList!!.addAll(Arrays.asList("单排钢筋", "双排钢筋"))
        mTypeRightList2!!.addAll(Arrays.asList("无加密", "有加密"))

        mBinding.checkCpdSubtitle1.checkEditName.setText("梁名称")
        mBinding.checkCpdSubtitle1.checkEdit.hint = "请输入梁名称"




        leftRealView = mBinding.checkCpdBeamLeftRealUi
        leftDesignView = mBinding.checkCpdBeamLeftDesignUi
        rightRealView = mBinding.checkCpdBeamRightRealUi
        rightDesignView = mBinding.checkCpdBeamRightDesignUi




        leftRealRectangleView = leftRealView.checkCpdLeftRealRectangleTv
        leftRealHView = leftRealView.checkCpdLeftRealHTv
        leftRealTView = leftRealView.checkCpdLeftRealTTv
        leftRealAnotherView = leftRealView.checkCpdLeftRealAnotherTv

        leftDesignRectangleView = leftDesignView.checkCpdLeftDesignRectangleTv
        leftDesignHView = leftDesignView.checkCpdLeftDesignHTv
        leftDesignTView = leftDesignView.checkCpdLeftDesignTTv
        leftDesignAnotherView = leftDesignView.checkCpdLeftDesignAnotherTv

        rightRealSingleParamsView = rightRealView.checkCpdBeamRightRealSingle
        rightRealDoubleParamsView = rightRealView.checkCpdBeamRightRealSingle2

        rightDesignSingleParamsView = rightDesignView.checkCpdBeamRightRealSingle
        rightDesignDoubleParamsView = rightDesignView.checkCpdBeamRightRealSingle2


        rightRealView.checkCpdLeftMenu.checkCpdLeftMenu1.setText("实测配筋")
        rightRealView.checkCpdLeftMenu.checkCpdLeftMenu2.setText("设计配筋")

        rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu1.setText("实测配筋")
        rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu2.setText("设计配筋")

        rightRealView.checkCpdLeftMenu.checkCpdLeftMenu4.visibility = View.VISIBLE
        rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu4.visibility = View.VISIBLE

        leftRealView.checkCpdLeftRealSpinner1.setText(mTypeLeftList!!.get(0))
        leftRealView.checkCpdLeftRealSpinner1.setSelect(0)

        leftRealView.checkCpdLeftRealSpinner1.setSpinnerData(mTypeLeftList)
            .setSpinnerTextGravity(Gravity.CENTER).setSpinnerCallback {
                currentLeftRealType = it

            }.build()

        leftRealView.checkCpdLeftRealSpinner2.setSpinnerData(mTypeLeftList2)
            .setSpinnerTextGravity(Gravity.CENTER).setSpinnerCallback {
                currentLeftRealType2 = it
                when (currentLeftRealType2) {
                    0 -> {
                        leftRealRectangleView.content.visibility = View.VISIBLE
                        leftRealHView.content.visibility = View.INVISIBLE
                        leftRealTView.content.visibility = View.INVISIBLE
                        leftRealAnotherView.content.visibility = View.INVISIBLE
                    }
                    1 -> {
                        leftRealRectangleView.content.visibility = View.INVISIBLE
                        leftRealHView.content.visibility = View.VISIBLE
                        leftRealTView.content.visibility = View.INVISIBLE
                        leftRealAnotherView.content.visibility = View.INVISIBLE
                    }
                    2 -> {
                        leftRealRectangleView.content.visibility = View.INVISIBLE
                        leftRealHView.content.visibility = View.INVISIBLE
                        leftRealTView.content.visibility = View.VISIBLE
                        leftRealAnotherView.content.visibility = View.INVISIBLE
                    }
                    3 -> {
                        leftRealRectangleView.content.visibility = View.INVISIBLE
                        leftRealHView.content.visibility = View.INVISIBLE
                        leftRealTView.content.visibility = View.INVISIBLE
                        leftRealAnotherView.content.visibility = View.VISIBLE
                    }
                }
            }.build()

        leftDesignView.checkCpdLeftRealSpinner1.setSpinnerData(mTypeLeftList)
            .setSpinnerTextGravity(Gravity.CENTER).setSpinnerCallback {
                currentLeftDesignType = it
            }.build()

        leftDesignView.checkCpdLeftRealSpinner2.setSpinnerData(mTypeLeftList2)
            .setSpinnerTextGravity(Gravity.CENTER).setSpinnerCallback {
                currentLeftDesignType2 = it
                when (currentLeftDesignType2) {
                    0 -> {
                        leftDesignRectangleView.content.visibility = View.VISIBLE
                        leftDesignHView.content.visibility = View.INVISIBLE
                        leftDesignTView.content.visibility = View.INVISIBLE
                        leftDesignAnotherView.content.visibility = View.INVISIBLE
                    }
                    1 -> {
                        leftDesignRectangleView.content.visibility = View.INVISIBLE
                        leftDesignHView.content.visibility = View.VISIBLE
                        leftDesignTView.content.visibility = View.INVISIBLE
                        leftDesignAnotherView.content.visibility = View.INVISIBLE
                    }
                    2 -> {
                        leftDesignRectangleView.content.visibility = View.INVISIBLE
                        leftDesignHView.content.visibility = View.INVISIBLE
                        leftDesignTView.content.visibility = View.VISIBLE
                        leftDesignAnotherView.content.visibility = View.INVISIBLE
                    }
                    3 -> {
                        leftDesignRectangleView.content.visibility = View.INVISIBLE
                        leftDesignHView.content.visibility = View.INVISIBLE
                        leftDesignTView.content.visibility = View.INVISIBLE
                        leftDesignAnotherView.content.visibility = View.VISIBLE
                    }
                }

            }.build()




        rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setSpinnerData(
            mTypeRightList2
        ).setSpinnerTextGravity(Gravity.CENTER).setSpinnerCallback {
            currentRightDesignType3 = it
            if (it==1){
                rightDesignView.ll11.visibility = View.VISIBLE
            }else{
                rightDesignView.ll11.visibility = View.GONE
            }
        }.build()

        rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setSelect(0)
        rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setText(
            mTypeRightList2!!.get(0)
        )

        rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setSpinnerData(
            mPicList
        ).setSpinnerTextGravity(Gravity.CENTER).setSpinnerCallback {
            currentRightDesignType4 = it
        }.setTypeface(Typeface.createFromAsset(activity?.assets, "fonts/SJQY.cb6e0829.TTF"))
            .build()

        rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setSelect(0)
        rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setTypeface(
            Typeface.createFromAsset(
                activity?.assets,
                "fonts/SJQY.cb6e0829.TTF"
            )
        )
        rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setText(
            mPicList!!.get(
                0
            )
        )

        mBinding.checkCpdBeamRightRealUi.checkCpdLeftRealDesignTv.checkCpdLeftRealSpinner2.setSpinnerData(
            mTypeRightList
        ).setSpinnerTextGravity(Gravity.CENTER).setSpinnerCallback {
            currentRightRealType = it
            when (currentRightRealType) {
                0 -> {
                    rightRealSingleParamsView.content.visibility = View.VISIBLE
                    rightRealDoubleParamsView.content.visibility = View.INVISIBLE
                }
                1 -> {
                    rightRealSingleParamsView.content.visibility = View.INVISIBLE
                    rightRealDoubleParamsView.content.visibility = View.VISIBLE
                }
            }
        }.build()

        mBinding.checkCpdBeamRightDesignUi.checkCpdLeftRealDesignTv.checkCpdLeftRealSpinner2.setSpinnerData(
            mTypeRightList
        ).setSpinnerTextGravity(Gravity.CENTER).setSpinnerCallback {
            currentRightDesignType = it
            when (currentRightDesignType) {
                0 -> {
                    rightDesignSingleParamsView.content.visibility = View.VISIBLE
                    rightDesignDoubleParamsView.content.visibility = View.INVISIBLE
                }
                1 -> {
                    rightDesignSingleParamsView.content.visibility = View.INVISIBLE
                    rightDesignDoubleParamsView.content.visibility = View.VISIBLE
                }
            }
        }.build()


        rightRealSingleParamsView.checkCpdLeftRealSpinner.setTypeface(
            Typeface.createFromAsset(
                activity?.assets,
                "fonts/SJQY.cb6e0829.TTF"
            )
        )
        rightRealSingleParamsView.checkCpdLeftRealSpinner.setText(mPicList!!.get(0))
        rightRealSingleParamsView.checkCpdLeftRealSpinner.setSelect(0)
        rightRealSingleParamsView.checkCpdLeftRealSpinner.setSpinnerData(mPicList)
            .setSpinnerTextGravity(Gravity.CENTER).setSpinnerCallback {
                currentRightRealType2 = it
            }.setTypeface(Typeface.createFromAsset(activity?.assets, "fonts/SJQY.cb6e0829.TTF"))
            .build()

        rightDesignSingleParamsView.checkCpdLeftRealSpinner.setTypeface(
            Typeface.createFromAsset(
                activity?.assets,
                "fonts/SJQY.cb6e0829.TTF"
            )
        )
        rightDesignSingleParamsView.checkCpdLeftRealSpinner.setText(mPicList!!.get(0))
        rightDesignSingleParamsView.checkCpdLeftRealSpinner.setSelect(0)
        rightDesignSingleParamsView.checkCpdLeftRealSpinner.setSpinnerData(mPicList)
            .setSpinnerTextGravity(Gravity.CENTER).setSpinnerCallback {
                currentRightDesignType2 = it
            }.setTypeface(Typeface.createFromAsset(activity?.assets, "fonts/SJQY.cb6e0829.TTF"))
            .build()

        rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setText(
            mTypeRightList2!!.get(
                0
            )
        )
        rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setSelect(0)
        rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setSpinnerData(
            mTypeRightList2
        ).setSpinnerTextGravity(Gravity.CENTER).setSpinnerCallback {
            currentRightRealType3 = it
            when (currentRightRealType3) {
                0 -> {
                    rightRealView.checkCpdBeamRightRealEncrypted.content.visibility = View.GONE
                    rightRealView.checkCpdBeamRightRealEncryptedText.visibility = View.GONE
                }
                1 -> {
                    rightRealView.checkCpdBeamRightRealEncrypted.content.visibility = View.VISIBLE
                    rightRealView.checkCpdBeamRightRealEncryptedText.visibility = View.VISIBLE
                }
            }
        }.build()

        rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setTypeface(
            Typeface.createFromAsset(
                activity?.assets,
                "fonts/SJQY.cb6e0829.TTF"
            )
        )
        rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setText(
            mPicList!!.get(
                0
            )
        )
        rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setSpinnerData(
            mPicList
        ).setSpinnerTextGravity(Gravity.CENTER).setSpinnerCallback {
            currentRightRealType4 = it
        }.setTypeface(Typeface.createFromAsset(activity?.assets, "fonts/SJQY.cb6e0829.TTF"))
            .build()


        leftRealAnotherView.checkCpdSubtitleConfirm.setOnClickListener {
            ARouter.getInstance().build(com.sribs.common.ARouterPath.DRAW_WHITE)
                .navigation(activity, REQUEST_CODE_BEAN_REAL_WHITE_FLLOR)
        }

        leftDesignAnotherView.checkCpdSubtitleConfirm.setOnClickListener {
            ARouter.getInstance().build(com.sribs.common.ARouterPath.DRAW_WHITE)
                .navigation(activity, REQUEST_CODE_BEAN_DESIGN_WHITE_FLLOR)
        }

        leftRealView.checkCpdLeftMenu.checkCpdLeftMenu2.setOnClickListener {
            leftRealView.content.visibility = View.INVISIBLE
            leftDesignView.content.visibility = View.VISIBLE
        }

        leftDesignView.checkCpdLeftMenu.checkCpdLeftMenu1.setOnClickListener {
            leftRealView.content.visibility = View.VISIBLE
            leftDesignView.content.visibility = View.INVISIBLE
        }

        rightRealView.checkCpdLeftMenu.checkCpdLeftMenu2.setOnClickListener {
            rightRealView.content.visibility = View.INVISIBLE
            rightDesignView.content.visibility = View.VISIBLE
        }

        rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu1.setOnClickListener {
            rightRealView.content.visibility = View.VISIBLE
            rightDesignView.content.visibility = View.INVISIBLE
        }

        checkLeftDesignStatus(false)
        checkRightDesignStatus(false)
        checkRightRealStatus(true)


        rightRealView!!.checkCpdLeftRealRemarkContent.setOnTouchListener(View.OnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        })


        rightRealView.checkCpdBeamPic.setOnClickListener {
            openImgSelector(REQUEST_BEAM_REAL_TAKE_PHOTO)
        }


        mBinding.checkCpdSubtitle2Second.checkEdit.hint = "请输入轴线"
        mBinding.checkCpdSubtitle2Second.checkEditName.hint = "轴线"


        /**
         * menu关闭
         */
        mBinding.checkColumnMenuLayout.checkObdMenuClose.setOnClickListener {
            (context as CheckComponentDetectionActivity).setVpCurrentItem(0)
        }

        /**
         * menu 缩小
         */
        mBinding.checkColumnMenuLayout.checkObdMenuScale.setOnClickListener {
            (context as CheckComponentDetectionActivity).scaleDamageInfo(1)
        }

        mBinding.checkCpdSubtitleChange.setOnClickListener {
            if (mBinding.checkCpdSubtitle2.content.visibility == View.VISIBLE) {
                mBinding.checkCpdSubtitle2.content.visibility = View.INVISIBLE
                mBinding.checkCpdSubtitle2Second.content.visibility = View.VISIBLE
            } else {
                mBinding.checkCpdSubtitle2.content.visibility = View.VISIBLE
                mBinding.checkCpdSubtitle2Second.content.visibility = View.INVISIBLE
            }
        }

        leftRealView.checkCpdLeftMenu.checkCpdLeftMenu4.animation = null
        leftRealView.checkCpdLeftMenu.checkCpdLeftMenu4.setOnCheckedChangeListener { buttonView, isChecked ->
            leftDesignView.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = isChecked
            checkLeftDesignStatus(isChecked)
        }

        leftDesignView.checkCpdLeftMenu.checkCpdLeftMenu4.animation = null
        leftDesignView.checkCpdLeftMenu.checkCpdLeftMenu4.setOnCheckedChangeListener { buttonView, isChecked ->
            leftRealView.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = isChecked
            checkLeftDesignStatus(isChecked)
        }

        rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu4.animation = null
        rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu4.setOnCheckedChangeListener { buttonView, isChecked ->
            rightRealView.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = isChecked
            checkRightDesignStatus(isChecked)
        }

        rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu4.animation = null
        rightRealView.checkCpdLeftMenu.checkCpdLeftMenu4.setOnCheckedChangeListener { buttonView, isChecked ->
            rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = isChecked
            checkRightDesignStatus(isChecked)
        }

        rightRealView.checkCpdLeftMenu.checkCpdLeftMenu3.animation = null
        rightRealView.checkCpdLeftMenu.checkCpdLeftMenu3.setOnCheckedChangeListener { buttonView, isChecked ->
            rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu3.isChecked = isChecked
            checkRightRealStatus(isChecked)
        }

        rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu3.animation = null
        rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu3.setOnCheckedChangeListener { buttonView, isChecked ->
            rightRealView.checkCpdLeftMenu.checkCpdLeftMenu3.isChecked = isChecked
            checkRightRealStatus(isChecked)
        }



        mBinding.checkCpdSubtitleConfirm.setOnClickListener {
            if (mBinding.checkCpdSubtitle1.checkEdit.text.toString().isNullOrEmpty()) {
                showToast("请输入梁名称")
                return@setOnClickListener
            }

            if (mBinding.checkCpdSubtitle2.content.visibility == View.VISIBLE) {
                if (mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis1.text.toString()
                        .isNullOrEmpty() ||
                    mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis2.text.toString()
                        .isNullOrEmpty() ||
                    mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis3.text.toString()
                        .isNullOrEmpty()
                ) {
                    showToast("请输入轴线")
                    return@setOnClickListener
                }
            } else {
                if (mBinding.checkCpdSubtitle2Second.checkEdit.text.toString().isNullOrEmpty()) {
                    showToast("请输入轴线")
                    return@setOnClickListener
                }
            }


            leftRealSectionTypeParamsList.clear()
            when (currentLeftRealType2) {
                0 -> {
                    leftRealSectionTypeParamsList.add(leftRealRectangleView.checkCpdLeftRectangle1.text.toString())
                    leftRealSectionTypeParamsList.add(leftRealRectangleView.checkCpdLeftRectangle2.text.toString())
                }
                1 -> {
                    leftRealSectionTypeParamsList.add(leftRealHView.checkCpdLeftRectangle1.text.toString())
                    leftRealSectionTypeParamsList.add(leftRealHView.checkCpdLeftRectangle2.text.toString())
                    leftRealSectionTypeParamsList.add(leftRealHView.checkCpdLeftRectangle3.text.toString())
                    leftRealSectionTypeParamsList.add(leftRealHView.checkCpdLeftRectangle4.text.toString())
                }
                2 -> {
                    leftRealSectionTypeParamsList.add(leftRealTView.checkCpdLeftRectangle1.text.toString())
                    leftRealSectionTypeParamsList.add(leftRealTView.checkCpdLeftRectangle2.text.toString())
                    leftRealSectionTypeParamsList.add(leftRealTView.checkCpdLeftRectangle3.text.toString())
                    leftRealSectionTypeParamsList.add(leftRealTView.checkCpdLeftRectangle4.text.toString())
                }
                3 -> {
                    leftRealSectionTypeParamsList.add(leftRealAnotherView.checkCpdLeftDesignAnotherName.text.toString())
                    leftRealSectionTypeParamsList.add(leftRealAnotherView.checkCpdLeftDesignAnotherDescribe.text.toString())
                }
            }

            leftDesignSectionTypeParamsList!!.clear()
            when (currentLeftDesignType2) {
                0 -> {
                    leftDesignSectionTypeParamsList.add(leftDesignRectangleView.checkCpdLeftRectangle1.text.toString())
                    leftDesignSectionTypeParamsList.add(leftDesignRectangleView.checkCpdLeftRectangle2.text.toString())
                }
                1 -> {
                    leftDesignSectionTypeParamsList.add(leftDesignHView.checkCpdLeftRectangle1.text.toString())
                    leftDesignSectionTypeParamsList.add(leftDesignHView.checkCpdLeftRectangle2.text.toString())
                    leftDesignSectionTypeParamsList.add(leftDesignHView.checkCpdLeftRectangle3.text.toString())
                    leftDesignSectionTypeParamsList.add(leftDesignHView.checkCpdLeftRectangle4.text.toString())
                }
                2 -> {
                    leftDesignSectionTypeParamsList.add(leftDesignTView.checkCpdLeftRectangle1.text.toString())
                    leftDesignSectionTypeParamsList.add(leftDesignTView.checkCpdLeftRectangle2.text.toString())
                    leftDesignSectionTypeParamsList.add(leftDesignTView.checkCpdLeftRectangle3.text.toString())
                    leftDesignSectionTypeParamsList.add(leftDesignTView.checkCpdLeftRectangle4.text.toString())
                }
                3 -> {
                    leftDesignSectionTypeParamsList.add(leftDesignAnotherView.checkCpdLeftDesignAnotherName.text.toString())
                    leftDesignSectionTypeParamsList.add(leftDesignAnotherView.checkCpdLeftDesignAnotherDescribe.text.toString())
                }
            }


            rightRealSectionTypeParamsList!!.clear()
            when (currentRightRealType) {
                0 -> {
                    rightRealSectionTypeParamsList.add(rightRealSingleParamsView.checkEdit1.text.toString())
                    rightRealSectionTypeParamsList.add(mPicList!!.get(currentRightRealType2))
                    rightRealSectionTypeParamsList.add(rightRealSingleParamsView.checkEdit3.text.toString())
                    rightRealSectionTypeParamsList.add(rightRealSingleParamsView.checkEdit4.text.toString())
                }
                1 -> {
                    rightRealSectionTypeParamsList.add(rightRealDoubleParamsView.checkEdit1.text.toString())
                    rightRealSectionTypeParamsList.add(rightRealDoubleParamsView.checkEdit2.text.toString())
                    rightRealSectionTypeParamsList.add(rightRealDoubleParamsView.checkEdit3.text.toString())
                    rightRealSectionTypeParamsList.add(rightRealDoubleParamsView.checkEdit4.text.toString())
                }
            }

            rightDesignSectionTypeParamsList!!.clear()
            when (currentRightDesignType) {
                0 -> {
                    rightDesignSectionTypeParamsList.add(rightDesignSingleParamsView.checkEdit1.text.toString())
                    rightDesignSectionTypeParamsList.add(mPicList!!.get(currentRightDesignType2))
                    rightDesignSectionTypeParamsList.add(rightDesignSingleParamsView.checkEdit3.text.toString())
                    rightDesignSectionTypeParamsList.add(rightDesignSingleParamsView.checkEdit4.text.toString())
                }
                1 -> {
                    rightDesignSectionTypeParamsList.add(rightDesignDoubleParamsView.checkEdit1.text.toString())
                    rightDesignSectionTypeParamsList.add(rightDesignDoubleParamsView.checkEdit2.text.toString())
                    rightDesignSectionTypeParamsList.add(rightDesignDoubleParamsView.checkEdit3.text.toString())
                    rightDesignSectionTypeParamsList.add(rightDesignDoubleParamsView.checkEdit4.text.toString())
                }
            }

            var axis:String
            var axisList:ArrayList<String> = ArrayList()
            if (mBinding.checkCpdSubtitle2Second.content.visibility ==View.VISIBLE){
                axis = mBinding.checkCpdSubtitle2Second.checkEdit.text.toString()
                axisList = arrayListOf("","","")
            }else{
                axis = ""
                axisList.addAll(arrayListOf( mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis1.text.toString(),
                    mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis2.text.toString(),
                    mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis3.text.toString()))
            }

            var damage = DamageV3Bean(
                -1,
                (activity as CheckComponentDetectionActivity).mCurrentDrawing!!.drawingID!!,
                "梁",
                0,
                mAddAnnotReF,
                "",
                if (mDamageCreateTime < 0) System.currentTimeMillis() else mDamageCreateTime,
                mBinding.checkCpdSubtitle1.checkEdit.text.toString(),
                axis,
                axisList,
                arrayListOf(
                    mTypeLeftList!!.get(currentLeftRealType),
                    mTypeLeftList2!!.get(currentLeftRealType2)
                ),
                leftRealSectionTypeParamsList,
                leftRealView.checkCpdLeftRealRemarkContent.text.toString(),
                mPicLeftRealList!!,
                arrayListOf(
                    mTypeLeftList!!.get(currentLeftDesignType),
                    mTypeLeftList2!!.get(currentLeftDesignType2)
                ),
                leftDesignSectionTypeParamsList,
                leftDesignView.checkCpdLeftDesignRemarkContent.text.toString(),
                mPicLeftDesignList!!,
                mTypeRightList!!.get(currentRightRealType),
                rightRealSectionTypeParamsList,
                arrayListOf(
                    mTypeRightList2!!.get(currentRightRealType3),
                    mPicList!!.get(currentRightRealType4),
                    rightRealView.checkCpdBeamRightRealMeasured.checkEdit2.text.toString()
                ),
                arrayListOf(
                    rightRealView.checkCpdBeamRightRealEncrypted.checkEdit.text.toString(),
                    rightRealView.checkCpdBeamRightRealEncrypted.checkEdit2.text.toString(),
                    rightRealView.checkCpdBeamRightRealEncrypted.checkEdit3.text.toString()
                ),
                arrayListOf(
                    rightRealView.checkCpdBeamRightRealNonEncrypted.checkEdit.text.toString(),
                    rightRealView.checkCpdBeamRightRealNonEncrypted.checkEdit2.text.toString()
                ),
                arrayListOf(
                    rightRealView.checkCpdBeamRightRealProtect.checkEditProtect.text.toString(),
                    rightRealView.checkCpdBeamRightRealProtect.checkEditProtect2.text.toString()
                ),
                rightRealView.checkCpdLeftRealRemarkContent.text.toString(),
                arrayListOf(
                    FileUtil.getFileName(mRightRealPicSrc) ?: "",
                    mRightRealPicSrc
                ),
                mTypeRightList!!.get(currentRightDesignType),
                rightDesignSectionTypeParamsList,
                arrayListOf(
                    mTypeRightList2!!.get(currentRightDesignType3),
                    mPicList!!.get(currentRightDesignType4),
                    rightDesignView.checkCpdBeamRightRealMeasured.checkEdit2.text.toString(),
                    rightDesignView.checkEdit.text.toString(),
                    rightDesignView.checkEditEncrypt.text.toString()
                ),
                rightDesignView.checkCpdLeftRealRemarkContent.text.toString(),
                arrayListOf(
                    FileUtil.getFileName(mRightDesignPicSrc) ?: "",
                    mRightDesignPicSrc
                ),
                arrayListOf(mCheckLeftDesignStatus.toString(),mCheckRightRealStatus.toString(),mCheckRightDesignStatus.toString())

            )
            LogUtils.d("保存梁损伤" + damage.toString())
            (context as CheckComponentDetectionActivity).saveDamage(damage)
        }

    }

    fun checkLeftDesignStatus(isEnable: Boolean) {

        mCheckLeftDesignStatus = isEnable

        leftDesignRectangleView.checkCpdLeftRectangle1.isEnabled = isEnable
        leftDesignRectangleView.checkCpdLeftRectangle2.isEnabled = isEnable


        leftDesignHView.checkCpdLeftRectangle1.isEnabled = isEnable
        leftDesignHView.checkCpdLeftRectangle2.isEnabled = isEnable
        leftDesignHView.checkCpdLeftRectangle3.isEnabled = isEnable
        leftDesignHView.checkCpdLeftRectangle4.isEnabled = isEnable

        leftDesignTView.checkCpdLeftRectangle1.isEnabled = isEnable
        leftDesignTView.checkCpdLeftRectangle2.isEnabled = isEnable
        leftDesignTView.checkCpdLeftRectangle3.isEnabled = isEnable
        leftDesignTView.checkCpdLeftRectangle4.isEnabled = isEnable

        leftDesignAnotherView.checkCpdLeftDesignAnotherDescribe.isEnabled = isEnable
        leftDesignAnotherView.checkCpdLeftDesignAnotherName.isEnabled = isEnable

        leftDesignView.checkCpdLeftRealSpinner1.isEnabled = isEnable
        leftDesignView.checkCpdLeftRealSpinner1.isClickable = isEnable
        leftDesignView.checkCpdLeftRealSpinner2.isEnabled = isEnable
        leftDesignView.checkCpdLeftRealSpinner2.isClickable = isEnable

        leftDesignAnotherView.checkCpdSubtitleConfirm.isEnabled = isEnable
        leftDesignAnotherView.checkCpdSubtitleConfirm.isClickable = isEnable


        leftDesignView.checkCpdLeftDesignRemarkContent.isEnabled = isEnable
    }


    fun checkRightRealStatus(isEnable: Boolean) {

        mCheckRightRealStatus = isEnable

        rightRealSingleParamsView.checkEdit1.isEnabled = isEnable
        rightRealSingleParamsView.checkEdit3.isEnabled = isEnable
        rightRealSingleParamsView.checkEdit4.isEnabled = isEnable

        rightRealSingleParamsView.checkCpdLeftRealSpinner.isClickable = isEnable
        rightRealSingleParamsView.checkCpdLeftRealSpinner.isEnabled = isEnable


        rightRealDoubleParamsView.checkEdit1.isEnabled = isEnable
        rightRealDoubleParamsView.checkEdit2.isEnabled = isEnable
        rightRealDoubleParamsView.checkEdit3.isEnabled = isEnable
        rightRealDoubleParamsView.checkEdit4.isEnabled = isEnable

        rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.isClickable = isEnable
        rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.isEnabled = isEnable

        rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.isClickable = isEnable
        rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.isEnabled = isEnable

        rightRealView.checkCpdBeamRightRealMeasured.checkEdit2.isEnabled = isEnable

        rightRealView.checkCpdBeamRightRealNonEncrypted.checkEdit.isEnabled = isEnable
        rightRealView.checkCpdBeamRightRealNonEncrypted.checkEdit2.isEnabled = isEnable

        rightRealView.checkCpdBeamRightRealEncrypted.checkEdit.isEnabled = isEnable
        rightRealView.checkCpdBeamRightRealEncrypted.checkEdit2.isEnabled = isEnable
        rightRealView.checkCpdBeamRightRealEncrypted.checkEdit3.isEnabled = isEnable

        rightRealView.checkCpdBeamRightRealProtect.checkEditProtect.isEnabled = isEnable
        rightRealView.checkCpdBeamRightRealProtect.checkEditProtect2.isEnabled = isEnable

        rightRealView.checkCpdLeftRealRemarkContent.isEnabled = isEnable

    }

    //TODO hhh
    fun checkRightDesignStatus(isEnable: Boolean) {

        mCheckRightDesignStatus = isEnable

        rightDesignSingleParamsView.checkEdit1.isEnabled = isEnable
        rightDesignSingleParamsView.checkEdit3.isEnabled = isEnable
        rightDesignSingleParamsView.checkEdit4.isEnabled = isEnable

        rightDesignSingleParamsView.checkCpdLeftRealSpinner.isClickable = isEnable
        rightDesignSingleParamsView.checkCpdLeftRealSpinner.isEnabled = isEnable

        rightDesignDoubleParamsView.checkEdit1.isEnabled = isEnable
        rightDesignDoubleParamsView.checkEdit2.isEnabled = isEnable
        rightDesignDoubleParamsView.checkEdit3.isEnabled = isEnable
        rightDesignDoubleParamsView.checkEdit4.isEnabled = isEnable


        rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.isClickable = isEnable
        rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.isEnabled = isEnable
        rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.isClickable = isEnable
        rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.isEnabled = isEnable

        rightDesignView.checkCpdBeamRightRealMeasured.checkEdit2.isEnabled = isEnable


        rightDesignView.checkEditEncrypt.isEnabled = isEnable


        rightDesignView.checkEdit.isEnabled = isEnable

        rightDesignView.checkCpdLeftRealRemarkContent.isEnabled = isEnable

    }

    fun resetView(damageV3Bean: DamageV3Bean?) {
        LogUtils.d("resetView:///damageV3Bean" +damageV3Bean.toString())
        mBinding.checkCpdSubtitle1.checkEdit.setText((context as CheckComponentDetectionActivity).mCurrentDrawing!!.floorName + "梁")
        if (damageV3Bean == null) {


            mDamageCreateTime = -1L
            currentLeftRealType = 0
            currentLeftRealType2 = 0

            currentLeftDesignType = 0
            currentLeftDesignType2 = 0

            currentRightRealType = 0
            currentRightRealType2 = 0
            currentRightRealType3 = 0
            currentRightRealType4 = 0

            currentRightDesignType = 0
            currentRightDesignType2 = 0
            currentRightDesignType3 = 0
            currentRightDesignType4 = 0

            mRightRealPicSrc = ""
            mRightDesignPicSrc = ""



            mPicLeftRealList = ArrayList()
            mPicLeftDesignList= ArrayList()

            mBinding.checkCpdSubtitle2.content.visibility = View.VISIBLE
            mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis1.setText("")
            mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis2.setText("")
            mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis3.setText("")
            mBinding.checkCpdSubtitle2Second.content.visibility = View.INVISIBLE
            mBinding.checkCpdSubtitle2Second.checkEdit.setText("")

            leftRealView.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = false
            leftDesignView.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = false

            rightRealView.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = false
            rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = false
            rightRealView.checkCpdLeftMenu.checkCpdLeftMenu3.isChecked = true
            rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu3.isChecked = true

            checkLeftDesignStatus(false)
            checkRightDesignStatus(false)
            checkRightRealStatus(true)



            leftRealView.content.visibility = View.VISIBLE
            leftRealView.checkCpdLeftRealSpinner1.setText(mTypeLeftList!!.get(0))
            leftRealView.checkCpdLeftRealSpinner1.setSelect(0)
            leftRealView.checkCpdLeftRealSpinner2.setText(mTypeLeftList2!!.get(0))
            leftRealView.checkCpdLeftRealSpinner2.setSelect(0)

            leftRealRectangleView.content.visibility = View.VISIBLE
            leftRealRectangleView.checkCpdLeftRectangle1.setText("")
            leftRealRectangleView.checkCpdLeftRectangle2.setText("")

            leftRealTView.content.visibility = View.INVISIBLE
            leftRealTView.checkCpdLeftRectangle1.setText("")
            leftRealTView.checkCpdLeftRectangle2.setText("")
            leftRealTView.checkCpdLeftRectangle3.setText("")
            leftRealTView.checkCpdLeftRectangle4.setText("")

            leftRealHView.content.visibility = View.INVISIBLE
            leftRealHView.checkCpdLeftRectangle1.setText("")
            leftRealHView.checkCpdLeftRectangle2.setText("")
            leftRealHView.checkCpdLeftRectangle3.setText("")
            leftRealHView.checkCpdLeftRectangle4.setText("")

            leftRealAnotherView.content.visibility = View.INVISIBLE
            leftRealAnotherView.checkCpdLeftDesignAnotherName.setText("")
            leftRealAnotherView.checkCpdLeftDesignAnotherDescribe.setText("")
            leftRealAnotherView.checkCpdAnotherPicStatus.setText("未绘制")

            leftDesignView.content.visibility = View.INVISIBLE

            leftDesignView.checkCpdLeftRealSpinner1.setText(mTypeLeftList!!.get(0))
            leftDesignView.checkCpdLeftRealSpinner1.setSelect(0)
            leftDesignView.checkCpdLeftRealSpinner2.setText(mTypeLeftList2!!.get(0))
            leftDesignView.checkCpdLeftRealSpinner2.setSelect(0)

            leftDesignRectangleView.content.visibility = View.VISIBLE
            leftDesignRectangleView.checkCpdLeftRectangle1.setText("")
            leftDesignRectangleView.checkCpdLeftRectangle2.setText("")

            leftDesignTView.content.visibility = View.INVISIBLE
            leftDesignTView.checkCpdLeftRectangle1.setText("")
            leftDesignTView.checkCpdLeftRectangle2.setText("")
            leftDesignTView.checkCpdLeftRectangle3.setText("")
            leftDesignTView.checkCpdLeftRectangle4.setText("")

            leftDesignHView.content.visibility = View.INVISIBLE
            leftDesignHView.checkCpdLeftRectangle1.setText("")
            leftDesignHView.checkCpdLeftRectangle2.setText("")
            leftDesignHView.checkCpdLeftRectangle3.setText("")
            leftDesignHView.checkCpdLeftRectangle4.setText("")

            leftDesignAnotherView.content.visibility = View.INVISIBLE
            leftDesignAnotherView.checkCpdLeftDesignAnotherName.setText("")
            leftDesignAnotherView.checkCpdLeftDesignAnotherDescribe.setText("")
            leftDesignAnotherView.checkCpdAnotherPicStatus.setText("未绘制")

            leftRealView.checkCpdLeftRealRemarkContent.setText("")
            leftDesignView.checkCpdLeftDesignRemarkContent.setText("")


            rightRealView.content.visibility = View.VISIBLE

            rightRealView.checkCpdLeftMenu.checkCpdLeftMenu3.visibility = View.VISIBLE
            rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu3.visibility = View.VISIBLE
            rightDesignView.content.visibility = View.INVISIBLE
            rightRealView.checkCpdLeftRealDesignTv.checkCpdLeftRealSpinner2.setText(
                mTypeRightList!!.get(
                    0
                )
            )
            rightRealView.checkCpdLeftRealDesignTv.checkCpdLeftRealSpinner2.setSelect(0)

            rightRealSingleParamsView.content.visibility = View.VISIBLE
            rightRealSingleParamsView.checkEdit1.setText("")
            rightRealSingleParamsView.checkEdit3.setText("")
            rightRealSingleParamsView.checkEdit4.setText("")
            rightRealSingleParamsView.checkCpdLeftRealSpinner.setSelect(0)
            rightRealSingleParamsView.checkCpdLeftRealSpinner.setText(mPicList!!.get(0))


            rightRealDoubleParamsView.content.visibility = View.INVISIBLE
            rightRealDoubleParamsView.checkEdit1.setText("")
            rightRealDoubleParamsView.checkEdit2.setText("")
            rightRealDoubleParamsView.checkEdit3.setText("")
            rightRealDoubleParamsView.checkEdit4.setText("")


            rightDesignView.checkCpdLeftRealDesignTv.checkCpdLeftRealSpinner2.setText(
                mTypeRightList!!.get(
                    0
                )
            )
            rightDesignView.checkCpdLeftRealDesignTv.checkCpdLeftRealSpinner2.setSelect(0)

            rightDesignSingleParamsView.content.visibility = View.VISIBLE
            rightDesignSingleParamsView.checkEdit1.setText("")
            rightDesignSingleParamsView.checkCpdLeftRealSpinner.setText(mPicList!!.get(0))
            rightDesignSingleParamsView.checkCpdLeftRealSpinner.setSelect(0)
            rightDesignSingleParamsView.checkEdit3.setText("")
            rightDesignSingleParamsView.checkEdit4.setText("")

            rightDesignDoubleParamsView.content.visibility = View.INVISIBLE
            rightDesignDoubleParamsView.checkEdit1.setText("")
            rightDesignDoubleParamsView.checkEdit2.setText("")
            rightDesignDoubleParamsView.checkEdit3.setText("")
            rightDesignDoubleParamsView.checkEdit4.setText("")



            rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setText(
                mTypeRightList2!!.get(0)
            )
            rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setSelect(0)
            rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setText(
                mPicList!!.get(
                    0
                )
            )
            rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setSelect(0)
            rightRealView.checkCpdBeamRightRealMeasured.checkEdit2.setText("")

            rightRealView.checkCpdBeamRightRealEncrypted.content.visibility = View.GONE
            rightRealView.checkCpdBeamRightRealEncryptedText.visibility = View.GONE
            rightRealView.checkCpdBeamRightRealEncrypted.checkEdit.setText("")
            rightRealView.checkCpdBeamRightRealEncrypted.checkEdit2.setText("")
            rightRealView.checkCpdBeamRightRealEncrypted.checkEdit3.setText("")

            rightRealView.checkCpdBeamRightRealNonEncrypted.checkEdit.setText("")
            rightRealView.checkCpdBeamRightRealNonEncrypted.checkEdit2.setText("")

            rightRealView.checkCpdBeamRightRealProtect.checkEditProtect.setText("")
            rightRealView.checkCpdBeamRightRealProtect.checkEditProtect2.setText("")

            rightRealView.checkCpdLeftRealRemarkContent.setText("")

            rightRealView.checkCpdBeamPic.setImageResource(R.color.gray)

            rightDesignView.checkCpdBeamRightRealMeasured.checkEdit2.setText("")
            rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setSelect(0)
            rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setText(
                mTypeRightList2!!.get(0)
            )
            rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setSelect(0)
            rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setText(
                mPicList!!.get(
                    0
                )
            )
            rightDesignView.checkEdit.setText("")
            rightDesignView.checkCpdLeftRealRemarkContent.setText("")

            rightDesignView.ll11.visibility = View.GONE
            rightDesignView.checkEditEncrypt.setText("")
        } else {

            mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis1.setText("")
            mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis2.setText("")
            mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis3.setText("")
            mBinding.checkCpdSubtitle2Second.checkEdit.setText("")
            mPicLeftRealList = damageV3Bean.beamLeftRealPicList

            mPicLeftDesignList = damageV3Bean.beamLeftDesignPicList


            mBinding.checkCpdSubtitle1.checkEdit.setText(damageV3Bean.beamName)
            mBinding.checkCpdSubtitle2Second.checkEditName.text = "轴线"
            if (!damageV3Bean.beamAxisNoteList!!.isNullOrEmpty() && !damageV3Bean.beamAxisNoteList!!.get(
                    0
                ).isNullOrEmpty()
            ) {
                mBinding.checkCpdSubtitle2.content.visibility = View.VISIBLE
                mBinding.checkCpdSubtitle2Second.content.visibility = View.INVISIBLE
                mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis1.setText(
                    damageV3Bean.beamAxisNoteList!!.get(
                        0
                    )
                )
                mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis2.setText(
                    damageV3Bean.beamAxisNoteList!!.get(
                        1
                    )
                )
                mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis3.setText(
                    damageV3Bean.beamAxisNoteList!!.get(
                        2
                    )
                )
            } else {
                mBinding.checkCpdSubtitle2.content.visibility = View.INVISIBLE
                mBinding.checkCpdSubtitle2Second.content.visibility = View.VISIBLE
            }
            mBinding.checkCpdSubtitle2Second.checkEdit.setText(damageV3Bean.beamAxisNote)

            rightRealView.checkCpdLeftMenu.checkCpdLeftMenu3.visibility = View.VISIBLE
            rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu3.visibility = View.VISIBLE


            if ("true".equals(damageV3Bean.beamCheckStatus!!.get(0))){
                leftRealView.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = true
                leftDesignView.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = true
                checkLeftDesignStatus(true)
            }else{
                leftRealView.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = false
                leftDesignView.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = false
                checkLeftDesignStatus(false)
            }

            if ("true".equals(damageV3Bean.beamCheckStatus!!.get(1))){
                rightRealView.checkCpdLeftMenu.checkCpdLeftMenu3.isChecked = true
                rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu3.isChecked = true
                checkRightRealStatus(true)
            }else{
                checkRightRealStatus(false)
                rightRealView.checkCpdLeftMenu.checkCpdLeftMenu3.isChecked = false
                rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu3.isChecked = false
            }

            if ("true".equals(damageV3Bean.beamCheckStatus!!.get(2))){
                rightRealView.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = true
                rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = true
                checkRightDesignStatus(true)

            }else{
                checkRightDesignStatus(false)
                rightRealView.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = false
                rightDesignView.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = false
            }

            leftRealView.content.visibility = View.VISIBLE
            leftDesignView.content.visibility = View.INVISIBLE

            rightRealView.content.visibility = View.VISIBLE
            rightDesignView.content.visibility =View.INVISIBLE

            leftRealView.checkCpdLeftRealSpinner1.setText(damageV3Bean.beamLeftRealTypeList!!.get(0))
            leftRealView.checkCpdLeftRealSpinner1.setSelect(
                mTypeLeftList!!.indexOf(damageV3Bean.beamLeftRealTypeList!!.get(0))
            )


            currentLeftRealType =
                mTypeLeftList!!.indexOf(damageV3Bean.beamLeftRealTypeList!!.get(0))
            currentLeftRealType2 =
                mTypeLeftList2!!.indexOf(damageV3Bean.beamLeftRealTypeList!!.get(1))

            currentLeftDesignType =
                mTypeLeftList!!.indexOf(damageV3Bean.beamLeftDesignTypeList!!.get(0))
            currentLeftDesignType2 =
                mTypeLeftList2!!.indexOf(damageV3Bean.beamLeftDesignTypeList!!.get(1))


            currentRightRealType = mTypeRightList!!.indexOf(damageV3Bean.beamRightRealSectionType)
            currentRightRealType2 =
                mPicList!!.indexOf(damageV3Bean.beamRightRealSectionParamsList!!.get(1))
            if (currentRightRealType2 < 0) {
                currentRightRealType2 = 0
            }
            currentRightRealType4 =
                mPicList!!.indexOf(damageV3Bean.beamRightRealStirrupsTypeList!!.get(1))


            currentRightDesignType =
                mTypeRightList!!.indexOf(damageV3Bean.beamRightDesignSectionType)
            currentRightDesignType2 =
                mPicList!!.indexOf(damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(1))
            if (currentRightDesignType2 < 0) {
                currentRightDesignType2 = 0
            }
            currentRightDesignType3 =
                mTypeRightList2!!.indexOf(damageV3Bean.beamRightDesignStirrupsTypeList!!.get(0))
            currentRightDesignType4 =
                mPicList!!.indexOf(damageV3Bean.beamRightDesignStirrupsTypeList!!.get(1))



            leftRealView.checkCpdLeftRealSpinner2.setText(damageV3Bean.beamLeftRealTypeList!!.get(1))
            leftRealView.checkCpdLeftRealSpinner2.setSelect(
                mTypeLeftList!!.indexOf(
                    damageV3Bean.beamLeftRealTypeList!!.get(
                        1
                    )
                )
            )



            when (damageV3Bean.beamLeftRealTypeList!!.get(1)) {
                "矩形" -> {
                    leftRealRectangleView.content.visibility = View.VISIBLE
                    leftRealHView.content.visibility = View.INVISIBLE
                    leftRealTView.content.visibility = View.INVISIBLE
                    leftRealAnotherView.content.visibility = View.INVISIBLE
                    leftRealRectangleView.checkCpdLeftRectangle1.setText(
                        damageV3Bean.beamLeftRealParamsList!!.get(
                            0
                        )
                    )
                    leftRealRectangleView.checkCpdLeftRectangle2.setText(
                        damageV3Bean.beamLeftRealParamsList!!.get(1)
                    )
                }
                "H型" -> {
                    leftRealRectangleView.content.visibility = View.INVISIBLE
                    leftRealHView.content.visibility = View.VISIBLE
                    leftRealTView.content.visibility = View.INVISIBLE
                    leftRealAnotherView.content.visibility = View.INVISIBLE
                    leftRealHView.checkCpdLeftRectangle1.setText(
                        damageV3Bean.beamLeftRealParamsList!!.get(
                            0
                        )
                    )
                    leftRealHView.checkCpdLeftRectangle2.setText(
                        damageV3Bean.beamLeftRealParamsList!!.get(
                            1
                        )
                    )
                    leftRealHView.checkCpdLeftRectangle3.setText(
                        damageV3Bean.beamLeftRealParamsList!!.get(
                            2
                        )
                    )
                    leftRealHView.checkCpdLeftRectangle4.setText(
                        damageV3Bean.beamLeftRealParamsList!!.get(
                            3
                        )
                    )
                }
                "T型" -> {
                    leftRealRectangleView.content.visibility = View.INVISIBLE
                    leftRealHView.content.visibility = View.INVISIBLE
                    leftRealTView.content.visibility = View.VISIBLE
                    leftRealAnotherView.content.visibility = View.INVISIBLE
                    leftRealTView.checkCpdLeftRectangle1.setText(
                        damageV3Bean.beamLeftRealParamsList!!.get(
                            0
                        )
                    )
                    leftRealTView.checkCpdLeftRectangle2.setText(
                        damageV3Bean.beamLeftRealParamsList!!.get(
                            1
                        )
                    )
                    leftRealTView.checkCpdLeftRectangle3.setText(
                        damageV3Bean.beamLeftRealParamsList!!.get(
                            2
                        )
                    )
                    leftRealTView.checkCpdLeftRectangle4.setText(
                        damageV3Bean.beamLeftRealParamsList!!.get(
                            3
                        )
                    )
                }
                "其他" -> {
                    leftRealRectangleView.content.visibility = View.INVISIBLE
                    leftRealHView.content.visibility = View.INVISIBLE
                    leftRealTView.content.visibility = View.INVISIBLE
                    leftRealAnotherView.content.visibility = View.VISIBLE
                    leftRealAnotherView.checkCpdLeftDesignAnotherName.setText(
                        damageV3Bean.beamLeftRealParamsList!!.get(
                            0
                        )
                    )
                    leftRealAnotherView.checkCpdLeftDesignAnotherDescribe.setText(
                        damageV3Bean.beamLeftRealParamsList!!.get(
                            1
                        )
                    )
                    leftRealAnotherView.checkCpdAnotherPicStatus.setText("已绘制")
                }
            }


            leftDesignView.checkCpdLeftRealSpinner1.setText(
                damageV3Bean.beamLeftDesignTypeList!!.get(
                    0
                )
            )
            leftDesignView.checkCpdLeftRealSpinner1.setSelect(
                mTypeLeftList!!.indexOf(
                    damageV3Bean.beamLeftDesignTypeList!!.get(
                        0
                    )
                )
            )

            leftDesignView.checkCpdLeftRealSpinner2.setText(
                damageV3Bean.beamLeftDesignTypeList!!.get(
                    1
                )
            )
            leftDesignView.checkCpdLeftRealSpinner2.setSelect(
                mTypeLeftList!!.indexOf(
                    damageV3Bean.beamLeftDesignTypeList!!.get(
                        1
                    )
                )
            )



            when (damageV3Bean.beamLeftDesignTypeList!!.get(1)) {
                "矩形" -> {
                    leftDesignRectangleView.content.visibility = View.VISIBLE
                    leftDesignHView.content.visibility = View.INVISIBLE
                    leftDesignTView.content.visibility = View.INVISIBLE
                    leftDesignAnotherView.content.visibility = View.INVISIBLE
                    leftDesignRectangleView.checkCpdLeftRectangle1.setText(
                        damageV3Bean.beamLeftDesignParamsList!!.get(
                            0
                        )
                    )
                    leftDesignRectangleView.checkCpdLeftRectangle2.setText(
                        damageV3Bean.beamLeftDesignParamsList!!.get(
                            1
                        )
                    )
                }
                "H型" -> {
                    leftDesignRectangleView.content.visibility = View.INVISIBLE
                    leftDesignHView.content.visibility = View.VISIBLE
                    leftDesignTView.content.visibility = View.INVISIBLE
                    leftDesignAnotherView.content.visibility = View.INVISIBLE
                    leftDesignHView.checkCpdLeftRectangle1.setText(
                        damageV3Bean.beamLeftDesignParamsList!!.get(
                            0
                        )
                    )
                    leftDesignHView.checkCpdLeftRectangle2.setText(
                        damageV3Bean.beamLeftDesignParamsList!!.get(
                            1
                        )
                    )
                    leftDesignHView.checkCpdLeftRectangle3.setText(
                        damageV3Bean.beamLeftDesignParamsList!!.get(
                            2
                        )
                    )
                    leftDesignHView.checkCpdLeftRectangle4.setText(
                        damageV3Bean.beamLeftDesignParamsList!!.get(
                            3
                        )
                    )

                }
                "T型" -> {
                    leftDesignRectangleView.content.visibility = View.INVISIBLE
                    leftDesignHView.content.visibility = View.INVISIBLE
                    leftDesignTView.content.visibility = View.VISIBLE
                    leftDesignAnotherView.content.visibility = View.INVISIBLE

                    leftDesignTView.checkCpdLeftRectangle1.setText(
                        damageV3Bean.beamLeftDesignParamsList!!.get(
                            0
                        )
                    )
                    leftDesignTView.checkCpdLeftRectangle2.setText(
                        damageV3Bean.beamLeftDesignParamsList!!.get(
                            1
                        )
                    )
                    leftDesignTView.checkCpdLeftRectangle3.setText(
                        damageV3Bean.beamLeftDesignParamsList!!.get(
                            2
                        )
                    )
                    leftDesignTView.checkCpdLeftRectangle4.setText(
                        damageV3Bean.beamLeftDesignParamsList!!.get(
                            3
                        )
                    )
                }
                "其他" -> {
                    leftDesignRectangleView.content.visibility = View.INVISIBLE
                    leftDesignHView.content.visibility = View.INVISIBLE
                    leftDesignTView.content.visibility = View.INVISIBLE
                    leftDesignAnotherView.content.visibility = View.VISIBLE
                    leftDesignAnotherView.checkCpdLeftDesignAnotherName.setText(
                        damageV3Bean.beamLeftDesignParamsList!!.get(
                            0
                        )
                    )
                    leftDesignAnotherView.checkCpdLeftDesignAnotherDescribe.setText(
                        damageV3Bean.beamLeftDesignParamsList!!.get(
                            1
                        )
                    )
                    leftDesignAnotherView.checkCpdAnotherPicStatus.setText("已绘制")
                }
            }
            leftRealView.checkCpdLeftRealRemarkContent.setText(damageV3Bean.beamLeftRealNote)
            leftDesignView.checkCpdLeftDesignRemarkContent.setText(damageV3Bean.beamLeftDesignNote)



            rightRealView.checkCpdLeftRealDesignTv.checkCpdLeftRealSpinner2.setText(damageV3Bean.beamRightRealSectionType)
            rightRealView.checkCpdLeftRealDesignTv.checkCpdLeftRealSpinner2.setSelect(
                mTypeRightList!!.indexOf(
                    damageV3Bean.beamRightRealSectionType
                )
            )


            rightDesignView.checkCpdLeftRealDesignTv.checkCpdLeftRealSpinner2.setText(damageV3Bean.beamRightDesignSectionType)
            rightDesignView.checkCpdLeftRealDesignTv.checkCpdLeftRealSpinner2.setSelect(
                mTypeRightList!!.indexOf(damageV3Bean.beamRightDesignSectionType)
            )



            rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setText(
                damageV3Bean.beamRightRealStirrupsTypeList!!.get(1)
            )
            rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setSelect(
                mPicList!!.indexOf(
                    damageV3Bean.beamRightRealStirrupsTypeList!!.get(1)
                )
            )
            rightRealView.checkCpdBeamRightRealMeasured.checkEdit2.setText(
                damageV3Bean.beamRightRealStirrupsTypeList!!.get(
                    2
                )
            )


            if (!damageV3Bean.beamRightRealStirrupsTypeEncryptList!!.isNullOrEmpty() && !damageV3Bean.beamRightRealStirrupsTypeEncryptList!!.get(
                    0
                ).isNullOrEmpty()
            ) {
                currentRightRealType3 = 1

                rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setText(
                    mTypeRightList2!!.get(1)
                )
                rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setSelect(1)

                rightRealView.checkCpdBeamRightRealEncrypted.content.visibility = View.VISIBLE
                rightRealView.checkCpdBeamRightRealEncryptedText.visibility = View.VISIBLE
                rightRealView.checkCpdBeamRightRealEncrypted.checkEdit.setText(
                    damageV3Bean.beamRightRealStirrupsTypeEncryptList!!.get(
                        0
                    )
                )
                rightRealView.checkCpdBeamRightRealEncrypted.checkEdit2.setText(
                    damageV3Bean.beamRightRealStirrupsTypeEncryptList!!.get(
                        1
                    )
                )
                rightRealView.checkCpdBeamRightRealEncrypted.checkEdit3.setText(
                    damageV3Bean.beamRightRealStirrupsTypeEncryptList!!.get(
                        2
                    )
                )
            } else {
                currentRightRealType3 = 0
                rightRealView.checkCpdBeamRightRealEncrypted.content.visibility = View.GONE
                rightRealView.checkCpdBeamRightRealEncryptedText.visibility = View.GONE

                rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setText(
                    mTypeRightList2!!.get(0)
                )
                rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setSelect(0)

                rightRealView.checkCpdBeamRightRealEncrypted.checkEdit.setText("")
                rightRealView.checkCpdBeamRightRealEncrypted.checkEdit2.setText("")
                rightRealView.checkCpdBeamRightRealEncrypted.checkEdit3.setText("")
            }

            rightRealView.checkCpdBeamRightRealNonEncrypted.checkEdit.setText(
                damageV3Bean.beamRightRealStirrupsTypeNonEncryptList!!.get(
                    0
                )
            )
            rightRealView.checkCpdBeamRightRealNonEncrypted.checkEdit2.setText(
                damageV3Bean.beamRightRealStirrupsTypeNonEncryptList!!.get(
                    1
                )
            )

            rightRealView.checkCpdBeamRightRealProtect.checkEditProtect.setText(
                damageV3Bean.beamRightRealProtectList!!.get(
                    0
                )
            )
            rightRealView.checkCpdBeamRightRealProtect.checkEditProtect2.setText(
                damageV3Bean.beamRightRealProtectList!!.get(
                    1
                )
            )

            rightRealView.checkCpdLeftRealRemarkContent.setText(damageV3Bean.beamRightRealNote)




            rightRealView.checkCpdLeftRealDesignTv.checkCpdLeftRealSpinner2.setText(
                mTypeRightList!!.get(
                    0
                )
            )

            if (mTypeRightList!!.get(0).equals(damageV3Bean.beamRightRealSectionType)) {
                rightRealSingleParamsView.content.visibility = View.VISIBLE
                rightRealSingleParamsView.checkEdit1.setText(
                    damageV3Bean.beamRightRealSectionParamsList!!.get(
                        0
                    )
                )
                rightRealSingleParamsView.checkEdit3.setText(
                    damageV3Bean.beamRightRealSectionParamsList!!.get(
                        2
                    )
                )
                rightRealSingleParamsView.checkEdit4.setText(
                    damageV3Bean.beamRightRealSectionParamsList!!.get(
                        3
                    )
                )
                rightRealSingleParamsView.checkCpdLeftRealSpinner.setSelect(
                    mPicList!!.indexOf(
                        damageV3Bean.beamRightRealSectionParamsList!!.get(1)
                    )
                )
                rightRealSingleParamsView.checkCpdLeftRealSpinner.setText(
                    damageV3Bean.beamRightRealSectionParamsList!!.get(
                        1
                    )
                )

                rightRealDoubleParamsView.content.visibility = View.INVISIBLE
                rightRealDoubleParamsView.checkEdit1.setText("")
                rightRealDoubleParamsView.checkEdit2.setText("")
                rightRealDoubleParamsView.checkEdit3.setText("")
                rightRealDoubleParamsView.checkEdit4.setText("")
            } else {

                rightRealDoubleParamsView.content.visibility = View.VISIBLE
                rightRealDoubleParamsView.checkEdit1.setText(
                    damageV3Bean.beamRightRealSectionParamsList!!.get(
                        0
                    )
                )
                rightRealDoubleParamsView.checkEdit2.setText(
                    damageV3Bean.beamRightRealSectionParamsList!!.get(
                        1
                    )
                )
                rightRealDoubleParamsView.checkEdit3.setText(
                    damageV3Bean.beamRightRealSectionParamsList!!.get(
                        2
                    )
                )
                rightRealDoubleParamsView.checkEdit4.setText(
                    damageV3Bean.beamRightRealSectionParamsList!!.get(
                        3
                    )
                )

                rightRealSingleParamsView.content.visibility = View.INVISIBLE
                rightRealSingleParamsView.checkEdit1.setText("")
                rightRealSingleParamsView.checkEdit3.setText("")
                rightRealSingleParamsView.checkEdit4.setText("")
                rightRealSingleParamsView.checkCpdLeftRealSpinner.setSelect(0)
                rightRealSingleParamsView.checkCpdLeftRealSpinner.setText(mPicList!!.get(0))
            }



            if (mTypeRightList!!.get(0).equals(damageV3Bean.beamRightDesignSectionType)) {
                rightDesignSingleParamsView.content.visibility = View.VISIBLE
                rightDesignSingleParamsView.checkEdit1.setText(
                    damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(
                        0
                    )
                )
                rightDesignSingleParamsView.checkEdit3.setText(
                    damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(
                        2
                    )
                )
                rightDesignSingleParamsView.checkEdit4.setText(
                    damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(
                        3
                    )
                )
                rightDesignSingleParamsView.checkCpdLeftRealSpinner.setSelect(
                    mPicList!!.indexOf(
                        damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(1)
                    )
                )
                rightDesignSingleParamsView.checkCpdLeftRealSpinner.setText(
                    damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(
                        1
                    )
                )

                rightDesignDoubleParamsView.content.visibility = View.INVISIBLE
                rightDesignDoubleParamsView.checkEdit1.setText("")
                rightDesignDoubleParamsView.checkEdit2.setText("")
                rightDesignDoubleParamsView.checkEdit3.setText("")
                rightDesignDoubleParamsView.checkEdit4.setText("")
            } else {
                rightDesignDoubleParamsView.content.visibility = View.VISIBLE
                rightDesignDoubleParamsView.checkEdit1.setText(
                    damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(
                        0
                    )
                )
                rightDesignDoubleParamsView.checkEdit2.setText(
                    damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(
                        1
                    )
                )
                rightDesignDoubleParamsView.checkEdit3.setText(
                    damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(
                        2
                    )
                )
                rightDesignDoubleParamsView.checkEdit4.setText(
                    damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(
                        3
                    )
                )

                rightDesignSingleParamsView.content.visibility = View.INVISIBLE
                rightDesignSingleParamsView.checkEdit1.setText("")
                rightDesignSingleParamsView.checkEdit3.setText("")
                rightDesignSingleParamsView.checkEdit4.setText("")
                rightDesignSingleParamsView.checkCpdLeftRealSpinner.setSelect(0)
                rightDesignSingleParamsView.checkCpdLeftRealSpinner.setText(mPicList!!.get(0))
            }

            mRightRealPicSrc = damageV3Bean.beamRightRealPic?.get(1) ?: ""
            mRightDesignPicSrc = damageV3Bean.beamRightDesignPic?.get(1) ?: ""

            rightRealView.checkCpdBeamPic.setImageURI(Uri.fromFile(File(mRightRealPicSrc)))


            rightDesignView.checkEdit.setText(damageV3Bean.beamRightDesignStirrupsTypeList!!.get(3))





            rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setSelect(
                mTypeRightList2!!.indexOf(damageV3Bean.beamRightDesignStirrupsTypeList!!.get(0))
            )

            if (currentRightDesignType3==1){
                rightDesignView.ll11.visibility =View.VISIBLE
                rightDesignView.checkEditEncrypt.setText(damageV3Bean.beamRightDesignStirrupsTypeList!!.get(4))
            }else{
                rightDesignView.ll11.visibility = View.GONE
            }

            rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setText(
                damageV3Bean.beamRightDesignStirrupsTypeList!!.get(0)
            )
            rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setText(
                damageV3Bean.beamRightDesignStirrupsTypeList!!.get(1)
            )
            rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setSelect(
                mPicList!!.indexOf(damageV3Bean.beamRightDesignStirrupsTypeList!!.get(1))
            )
            rightDesignView.checkCpdBeamRightRealMeasured.checkEdit2.setText(
                damageV3Bean.beamRightDesignStirrupsTypeList!!.get(
                    2
                )
            )

            rightDesignView.checkCpdLeftRealRemarkContent.setText(damageV3Bean.beamRightDesignNote)

            mDamageCreateTime = damageV3Bean.createTime
        }
    }

    override fun onClick(data: DrawingV3Bean?) {
        (activity as CheckComponentDetectionActivity).choosePDF(data!!)
    }

    fun setRealPicList(picList: ArrayList<String>) {
        mPicLeftRealList?.clear()
        mPicLeftRealList?.addAll(picList)
        if (mPicLeftRealList.isNullOrEmpty()) {
            leftRealAnotherView.checkCpdAnotherPicStatus.setText("未绘制")
        } else {
            leftRealAnotherView.checkCpdAnotherPicStatus.setText("已绘制")
        }
    }

    fun setDesignPicList(picList: ArrayList<String>) {
        mPicLeftDesignList?.clear()
        mPicLeftDesignList?.addAll(picList)
        if (mPicLeftDesignList.isNullOrEmpty()) {
            leftDesignAnotherView.checkCpdAnotherPicStatus.setText("未绘制")
        } else {
            leftDesignAnotherView.checkCpdAnotherPicStatus.setText("已绘制")
        }
    }

    fun openImgSelector(requestCode: Int) {

        //仅拍照
        ImageSelector
            .builder()
            .onlyTakePhoto(false)
            .canPreview(true)
            .setSingle(true)
            .start(activity as CheckComponentDetectionActivity, requestCode)


    }

    fun setImageBitmap(filePath: String, type: Int) {
        when (type) {
            REQUEST_BEAM_REAL_TAKE_PHOTO -> {
                rightRealView.checkCpdBeamPic.setImageURI(Uri.fromFile(File(filePath)))
                mRightRealPicSrc = filePath
            }

        }
    }
}



