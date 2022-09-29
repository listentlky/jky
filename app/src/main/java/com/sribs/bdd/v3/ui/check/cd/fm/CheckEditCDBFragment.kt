package com.sribs.bdd.v3.ui.check.cd.fm

import android.graphics.Color
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.*
import com.sribs.bdd.v3.popup.FloorDrawingSpinnerPopupWindow
import com.sribs.bdd.v3.ui.check.cd.CheckComponentDetectionActivity
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean
import kotlinx.android.synthetic.main.fragment_check_componentdetection_column_right_design_edit.view.*
import java.util.*

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

    private var mTypeLeftList: ArrayList<String>? = ArrayList()
    private var mTypeLeftList2: ArrayList<String>? = ArrayList()
    private var mTypeRightList: ArrayList<String>? = ArrayList()
    private var mTypeRightList2: ArrayList<String>? = ArrayList()

    private var mPicList: ArrayList<String>? = ArrayList()

    private var leftRealSectionType: String = ""
    private var leftDesignSectionType: String = ""
    private var leftRealSectionTypeParamsList: ArrayList<String> = ArrayList()
    private var leftDesignSectionTypeParamsList: ArrayList<String> = ArrayList()


    private var rightRealSectionTypeParamsList: ArrayList<String> = ArrayList()
    private var rightDesignSectionTypeParamsList: ArrayList<String> = ArrayList()


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

    override fun deinitView() {
    }


    override fun initView() {

        mTypeLeftList!!.addAll(Arrays.asList("总高", "净高"))
        mTypeLeftList2!!.addAll(Arrays.asList("矩形", "H型", "T型", "其他"))
        mPicList!!.addAll(Arrays.asList("1", "2"))
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

        leftRealView.checkCpdLeftRealSpinner1.setText(mTypeLeftList!!.get(0))
        leftRealView.checkCpdLeftRealSpinner1.setSelect(0)

        leftRealView.checkCpdLeftRealSpinner1.setSpinnerData(mTypeLeftList)
            .setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback {
                currentLeftRealType = it

            }.build()

        leftRealView.checkCpdLeftRealSpinner2.setSpinnerData(mTypeLeftList2)
            .setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback {
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
            .setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback {
                currentLeftDesignType = it
            }.build()

        leftDesignView.checkCpdLeftRealSpinner2.setSpinnerData(mTypeLeftList2)
            .setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback {
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
        ).setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback {
            currentRightDesignType3 = it
        }.build()

        rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setSelect(0)
        rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setText(
            mTypeRightList2!!.get(0)
        )

        rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setSpinnerData(
            mPicList
        ).setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback {
            currentRightDesignType4 = it
        }.build()

        rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setSelect(0)
        rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setText(
            mPicList!!.get(
                0
            )
        )

        mBinding.checkCpdBeamRightRealUi.checkCpdLeftRealDesignTv.checkCpdLeftRealSpinner2.setSpinnerData(
            mTypeRightList
        ).setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback {
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
        ).setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback {
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



        rightRealSingleParamsView.checkCpdLeftRealSpinner.setText(mPicList!!.get(0))
        rightRealSingleParamsView.checkCpdLeftRealSpinner.setSelect(0)
        rightRealSingleParamsView.checkCpdLeftRealSpinner.setSpinnerData(mPicList)
            .setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback {
                currentRightRealType2 = it
            }.build()

        rightDesignSingleParamsView.checkCpdLeftRealSpinner.setText(mPicList!!.get(0))
        rightDesignSingleParamsView.checkCpdLeftRealSpinner.setSelect(0)
        rightDesignSingleParamsView.checkCpdLeftRealSpinner.setSpinnerData(mPicList)
            .setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback {
                currentRightDesignType2 = it
            }.build()

        rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setText(
            mTypeRightList2!!.get(
                0
            )
        )
        rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setSelect(0)
        rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setSpinnerData(
            mTypeRightList2
        ).setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback {
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

        rightRealView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.setSpinnerData(
            mPicList
        ).setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback {
            currentRightRealType4 = it
        }.build()





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


        rightRealView!!.checkCpdLeftRealRemarkContent.setOnTouchListener(View.OnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        })




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
                    if (leftRealRectangleView.checkCpdLeftRectangle1.text.toString()
                            .isNullOrEmpty() ||
                        leftRealRectangleView.checkCpdLeftRectangle2.text.toString().isNullOrEmpty()
                    ) {
                        showToast("请输入 实测截面类型-矩形")
                        return@setOnClickListener
                    } else {
                        leftRealSectionTypeParamsList.add(leftRealRectangleView.checkCpdLeftRectangle1.text.toString())
                        leftRealSectionTypeParamsList.add(leftRealRectangleView.checkCpdLeftRectangle2.text.toString())
                    }
                }
                1 -> {
                    if (leftRealHView.checkCpdLeftRectangle1.text.toString().isNullOrEmpty() ||
                        leftRealHView.checkCpdLeftRectangle2.text.toString().isNullOrEmpty() ||
                        leftRealHView.checkCpdLeftRectangle3.text.toString().isNullOrEmpty() ||
                        leftRealHView.checkCpdLeftRectangle4.text.toString().isNullOrEmpty()
                    ) {
                        showToast("请输入 实测截面类型-H型")
                        return@setOnClickListener
                    } else {
                        leftRealSectionTypeParamsList.add(leftRealHView.checkCpdLeftRectangle1.text.toString())
                        leftRealSectionTypeParamsList.add(leftRealHView.checkCpdLeftRectangle2.text.toString())
                        leftRealSectionTypeParamsList.add(leftRealHView.checkCpdLeftRectangle3.text.toString())
                        leftRealSectionTypeParamsList.add(leftRealHView.checkCpdLeftRectangle4.text.toString())
                    }
                }
                2 -> {
                    if (leftRealTView.checkCpdLeftRectangle1.text.toString().isNullOrEmpty() ||
                        leftRealTView.checkCpdLeftRectangle2.text.toString().isNullOrEmpty() ||
                        leftRealTView.checkCpdLeftRectangle3.text.toString().isNullOrEmpty() ||
                        leftRealTView.checkCpdLeftRectangle4.text.toString().isNullOrEmpty()
                    ) {
                        showToast("请输入 实测截面类型-T型")
                        return@setOnClickListener
                    } else {
                        leftRealSectionTypeParamsList.add(leftRealTView.checkCpdLeftRectangle1.text.toString())
                        leftRealSectionTypeParamsList.add(leftRealTView.checkCpdLeftRectangle2.text.toString())
                        leftRealSectionTypeParamsList.add(leftRealTView.checkCpdLeftRectangle3.text.toString())
                        leftRealSectionTypeParamsList.add(leftRealTView.checkCpdLeftRectangle4.text.toString())
                    }
                }
                3 -> {
                    if (leftRealAnotherView.checkCpdLeftDesignAnotherName.text.toString()
                            .isNullOrEmpty() ||
                        leftRealAnotherView.checkCpdLeftDesignAnotherDescribe.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 实测截面类型-其他")
                        return@setOnClickListener
                    } else {
                        leftRealSectionTypeParamsList.add(leftRealAnotherView.checkCpdLeftDesignAnotherName.text.toString())
                        leftRealSectionTypeParamsList.add(leftRealAnotherView.checkCpdLeftDesignAnotherDescribe.text.toString())
                    }
                }
            }

            leftDesignSectionTypeParamsList!!.clear()
            when (currentLeftDesignType2) {
                0 -> {
                    if (leftDesignRectangleView.checkCpdLeftRectangle1.text.toString()
                            .isNullOrEmpty() ||
                        leftDesignRectangleView.checkCpdLeftRectangle2.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 设计面类型-矩形")
                        return@setOnClickListener
                    } else {
                        leftDesignSectionTypeParamsList.add(leftDesignRectangleView.checkCpdLeftRectangle1.text.toString())
                        leftDesignSectionTypeParamsList.add(leftDesignRectangleView.checkCpdLeftRectangle2.text.toString())
                    }
                }
                1 -> {
                    if (leftDesignHView.checkCpdLeftRectangle1.text.toString().isNullOrEmpty() ||
                        leftDesignHView.checkCpdLeftRectangle2.text.toString().isNullOrEmpty() ||
                        leftDesignHView.checkCpdLeftRectangle3.text.toString().isNullOrEmpty() ||
                        leftDesignHView.checkCpdLeftRectangle4.text.toString().isNullOrEmpty()
                    ) {
                        showToast("请输入 设计截面类型-H型")
                        return@setOnClickListener
                    } else {
                        leftDesignSectionTypeParamsList.add(leftDesignHView.checkCpdLeftRectangle1.text.toString())
                        leftDesignSectionTypeParamsList.add(leftDesignHView.checkCpdLeftRectangle2.text.toString())
                        leftDesignSectionTypeParamsList.add(leftDesignHView.checkCpdLeftRectangle3.text.toString())
                        leftDesignSectionTypeParamsList.add(leftDesignHView.checkCpdLeftRectangle4.text.toString())
                    }
                }
                2 -> {
                    if (leftDesignTView.checkCpdLeftRectangle1.text.toString().isNullOrEmpty() ||
                        leftDesignTView.checkCpdLeftRectangle2.text.toString().isNullOrEmpty() ||
                        leftDesignTView.checkCpdLeftRectangle3.text.toString().isNullOrEmpty() ||
                        leftDesignTView.checkCpdLeftRectangle4.text.toString().isNullOrEmpty()
                    ) {
                        showToast("请输入 设计截面类型-T型")
                        return@setOnClickListener
                    } else {
                        leftDesignSectionTypeParamsList.add(leftDesignTView.checkCpdLeftRectangle1.text.toString())
                        leftDesignSectionTypeParamsList.add(leftDesignTView.checkCpdLeftRectangle2.text.toString())
                        leftDesignSectionTypeParamsList.add(leftDesignTView.checkCpdLeftRectangle3.text.toString())
                        leftDesignSectionTypeParamsList.add(leftDesignTView.checkCpdLeftRectangle4.text.toString())
                    }
                }
                3 -> {
                    if (leftDesignAnotherView.checkCpdLeftDesignAnotherName.text.toString()
                            .isNullOrEmpty() ||
                        leftDesignAnotherView.checkCpdLeftDesignAnotherDescribe.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 设计截面类型-其他")
                        return@setOnClickListener
                    } else {
                        leftDesignSectionTypeParamsList.add(leftDesignAnotherView.checkCpdLeftDesignAnotherName.text.toString())
                        leftDesignSectionTypeParamsList.add(leftDesignAnotherView.checkCpdLeftDesignAnotherDescribe.text.toString())
                    }
                }
            }

            if (leftRealView.checkCpdLeftRealRemarkContent.text.isNullOrEmpty()) {
                showToast("请输入 实测截面尺寸-备注")
                return@setOnClickListener
            }

            if (leftDesignView.checkCpdLeftDesignRemarkContent.text.isNullOrEmpty()) {
                showToast("请输入 设计截面尺寸-备注")
                return@setOnClickListener
            }
            rightRealSectionTypeParamsList!!.clear()
            when (currentRightRealType) {
                0 -> {
                    if (rightRealSingleParamsView.checkEdit1.text.toString().isNullOrEmpty() ||
                        rightRealSingleParamsView.checkEdit3.text.toString().isNullOrEmpty() ||
                        rightRealSingleParamsView.checkEdit4.text.toString().isNullOrEmpty()
                    ) {
                        showToast("请输入 实测纵筋-单排钢筋")
                        return@setOnClickListener
                    } else {
                        rightRealSectionTypeParamsList.add(rightRealSingleParamsView.checkEdit1.text.toString())
                        rightRealSectionTypeParamsList.add(mPicList!!.get(currentRightRealType2))
                        rightRealSectionTypeParamsList.add(rightRealSingleParamsView.checkEdit3.text.toString())
                        rightRealSectionTypeParamsList.add(rightRealSingleParamsView.checkEdit4.text.toString())
                    }
                }
                1 -> {
                    if (rightRealDoubleParamsView.checkEdit1.text.toString().isNullOrEmpty() ||
                        rightRealDoubleParamsView.checkEdit2.text.toString().isNullOrEmpty() ||
                        rightRealDoubleParamsView.checkEdit3.text.toString().isNullOrEmpty() ||
                        rightRealDoubleParamsView.checkEdit4.text.toString().isNullOrEmpty()
                    ) {
                        showToast("请输入 实测纵筋-双排钢筋")
                        return@setOnClickListener
                    } else {
                        rightRealSectionTypeParamsList.add(rightRealDoubleParamsView.checkEdit1.text.toString())
                        rightRealSectionTypeParamsList.add(rightRealDoubleParamsView.checkEdit2.text.toString())
                        rightRealSectionTypeParamsList.add(rightRealDoubleParamsView.checkEdit3.text.toString())
                        rightRealSectionTypeParamsList.add(rightRealDoubleParamsView.checkEdit4.text.toString())
                    }
                }
            }
            rightDesignSectionTypeParamsList!!.clear()
            when (currentRightDesignType) {
                0 -> {
                    if (rightDesignSingleParamsView.checkEdit1.text.toString().isNullOrEmpty() ||
                        rightDesignSingleParamsView.checkEdit3.text.toString().isNullOrEmpty() ||
                        rightDesignSingleParamsView.checkEdit4.text.toString().isNullOrEmpty()
                    ) {
                        showToast("请输入 设计纵筋-单排钢筋")
                        return@setOnClickListener
                    } else {
                        rightDesignSectionTypeParamsList.add(rightDesignSingleParamsView.checkEdit1.text.toString())
                        rightDesignSectionTypeParamsList.add(mPicList!!.get(currentRightDesignType2))
                        rightDesignSectionTypeParamsList.add(rightDesignSingleParamsView.checkEdit3.text.toString())
                        rightDesignSectionTypeParamsList.add(rightDesignSingleParamsView.checkEdit4.text.toString())
                    }
                }
                1 -> {
                    if (rightDesignDoubleParamsView.checkEdit1.text.toString().isNullOrEmpty() ||
                        rightDesignDoubleParamsView.checkEdit2.text.toString().isNullOrEmpty() ||
                        rightDesignDoubleParamsView.checkEdit3.text.toString().isNullOrEmpty() ||
                        rightDesignDoubleParamsView.checkEdit4.text.toString().isNullOrEmpty()
                    ) {
                        showToast("请输入 设计纵筋-双排钢筋")
                        return@setOnClickListener
                    } else {
                        rightDesignSectionTypeParamsList.add(rightDesignDoubleParamsView.checkEdit1.text.toString())
                        rightDesignSectionTypeParamsList.add(rightDesignDoubleParamsView.checkEdit2.text.toString())
                        rightDesignSectionTypeParamsList.add(rightDesignDoubleParamsView.checkEdit3.text.toString())
                        rightDesignSectionTypeParamsList.add(rightDesignDoubleParamsView.checkEdit4.text.toString())
                    }
                }
            }

            when (currentRightRealType3) {
                1 -> {
                    if (rightRealView.checkCpdBeamRightRealEncrypted.checkEdit.text.toString()
                            .isNullOrEmpty() ||
                        rightRealView.checkCpdBeamRightRealEncrypted.checkEdit2.text.toString()
                            .isNullOrEmpty() ||
                        rightRealView.checkCpdBeamRightRealEncrypted.checkEdit3.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 实测箍筋-加密区")
                        return@setOnClickListener
                    }
                }
            }
            if (rightRealView.checkCpdBeamRightRealNonEncrypted.checkEdit.text.toString()
                    .isNullOrEmpty() ||
                rightRealView.checkCpdBeamRightRealNonEncrypted.checkEdit2.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 实测箍筋-非加密区")
                return@setOnClickListener
            }
            if (rightRealView.checkCpdBeamRightRealProtect.checkEditProtect.text.toString()
                    .isNullOrEmpty() ||
                rightRealView.checkCpdBeamRightRealProtect.checkEditProtect2.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 实测保护层厚度")
                return@setOnClickListener
            }
            if (rightRealView.checkCpdLeftRealRemarkContent.text.toString().isNullOrEmpty()) {
                showToast("请输入 实测配筋-备注")
                return@setOnClickListener
            }

            if (rightDesignView.checkCpdBeamRightRealMeasured.checkEdit2.text.toString()
                    .isNullOrEmpty() ||
                rightDesignView.checkEdit.text.toString().isNullOrEmpty()
            ) {
                showToast("请输入 设计箍筋")
                return@setOnClickListener
            }
            if (rightDesignView.checkCpdLeftRealRemarkContent.text.toString().isNullOrEmpty()) {
                showToast("请输入 设计配筋-备注")
                return@setOnClickListener
            }


            var damage = DamageV3Bean(
                -1,
                (activity as CheckComponentDetectionActivity).mCurrentDrawing!!.drawingID,
                "梁",
                0,
                mAddAnnotReF,
                "",
                mDamageCreateTime,
                mBinding.checkCpdSubtitle1.checkEdit.text.toString(),
                mBinding.checkCpdSubtitle2Second.checkEdit.text.toString(),
                arrayListOf(
                    mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis1.text.toString(),
                    mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis2.text.toString(),
                    mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis3.text.toString()
                ),
                arrayListOf(
                    mTypeLeftList!!.get(currentLeftRealType),
                    mTypeLeftList2!!.get(currentLeftRealType2)
                ),
                leftRealSectionTypeParamsList,
                leftRealView.checkCpdLeftRealRemarkContent.text.toString(),
                arrayListOf(
                    mTypeLeftList!!.get(currentLeftDesignType),
                    mTypeLeftList2!!.get(currentLeftDesignType2)
                ),
                leftDesignSectionTypeParamsList,
                leftDesignView.checkCpdLeftDesignRemarkContent.text.toString(),
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
                "照片",
                mTypeRightList!!.get(currentRightDesignType),
                rightDesignSectionTypeParamsList,
                arrayListOf(
                    mTypeRightList2!!.get(currentRightDesignType3),
                    mPicList!!.get(currentRightDesignType4),
                    rightDesignView.checkCpdBeamRightRealMeasured.checkEdit2.text.toString(),
                    rightDesignView.checkEdit.text.toString()
                ),
                rightDesignView.checkCpdLeftRealRemarkContent.text.toString(),
                "照片"
            )
            LogUtils.d(damage.toBeamString())
            (context as CheckComponentDetectionActivity).saveDamage(damage)
        }

    }

    fun resetView(damageV3Bean: DamageV3Bean?) {

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


            mBinding.checkCpdSubtitle1.checkEdit.setText("")
            mBinding.checkCpdSubtitle2.content.visibility = View.VISIBLE
            mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis1.setText("")
            mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis2.setText("")
            mBinding.checkCpdSubtitle2.checkCdpPlateMenuAxis3.setText("")
            mBinding.checkCpdSubtitle2Second.content.visibility = View.INVISIBLE
            mBinding.checkCpdSubtitle2Second.checkEdit.setText("")

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

            leftRealView.checkCpdLeftRealRemarkContent.setText("")
            leftDesignView.checkCpdLeftDesignRemarkContent.setText("")


            rightRealView.content.visibility = View.VISIBLE
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

            rightRealView.checkCpdBeamPic.setBackgroundResource(R.color.red)

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
            rightDesignView.checkCpdBeamPic.setBackgroundResource(R.color.red)
        } else {
            LogUtils.d("子控件进去的Damage梁bean"+damageV3Bean.toBeamString())
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
            leftRealView.content.visibility = View.VISIBLE
            leftRealView.checkCpdLeftRealSpinner1.setText(damageV3Bean.beamLeftRealTypeList!!.get(0))
            leftRealView.checkCpdLeftRealSpinner1.setSelect(
                mTypeLeftList!!.indexOf(
                    damageV3Bean.beamLeftRealTypeList!!.get(
                        0
                    )
                )
            )

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
                }
            }


            leftDesignView.content.visibility = View.INVISIBLE
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
                }
            }
            leftRealView.checkCpdLeftRealRemarkContent.setText(damageV3Bean.beamLeftRealNote)
            leftDesignView.checkCpdLeftDesignRemarkContent.setText(damageV3Bean.beamLeftDesignNote)



            rightRealView.checkCpdLeftRealDesignTv.checkCpdLeftRealSpinner2.setText(damageV3Bean.beamRightRealSectionType)
            rightRealView.checkCpdLeftRealDesignTv.checkCpdLeftRealSpinner2.setSelect(mTypeRightList!!.indexOf(damageV3Bean.beamRightRealSectionType))


            rightDesignView.checkCpdLeftRealDesignTv.checkCpdLeftRealSpinner2.setText(damageV3Bean.beamRightDesignSectionType)
            rightDesignView.checkCpdLeftRealDesignTv.checkCpdLeftRealSpinner2.setSelect(mTypeRightList!!.indexOf(damageV3Bean.beamRightDesignSectionType))

            rightRealView.checkCpdBeamRightRealSingle


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

            if (mTypeRightList!!.get(0).equals(damageV3Bean.beamRightRealSectionType)){
                rightRealSingleParamsView.content.visibility = View.VISIBLE
                rightRealSingleParamsView.checkEdit1.setText(damageV3Bean.beamRightRealSectionParamsList!!.get(0))
                rightRealSingleParamsView.checkEdit3.setText(damageV3Bean.beamRightRealSectionParamsList!!.get(2))
                rightRealSingleParamsView.checkEdit4.setText(damageV3Bean.beamRightRealSectionParamsList!!.get(3))
                rightRealSingleParamsView.checkCpdLeftRealSpinner.setSelect(mPicList!!.indexOf(damageV3Bean.beamRightRealSectionParamsList!!.get(1)))
                rightRealSingleParamsView.checkCpdLeftRealSpinner.setText(damageV3Bean.beamRightRealSectionParamsList!!.get(1))

                rightRealDoubleParamsView.content.visibility = View.INVISIBLE
                rightRealDoubleParamsView.checkEdit1.setText("")
                rightRealDoubleParamsView.checkEdit2.setText("")
                rightRealDoubleParamsView.checkEdit3.setText("")
                rightRealDoubleParamsView.checkEdit4.setText("")
            }else{

                rightRealDoubleParamsView.content.visibility = View.VISIBLE
                rightRealDoubleParamsView.checkEdit1.setText(damageV3Bean.beamRightRealSectionParamsList!!.get(0))
                rightRealDoubleParamsView.checkEdit2.setText(damageV3Bean.beamRightRealSectionParamsList!!.get(1))
                rightRealDoubleParamsView.checkEdit3.setText(damageV3Bean.beamRightRealSectionParamsList!!.get(2))
                rightRealDoubleParamsView.checkEdit4.setText(damageV3Bean.beamRightRealSectionParamsList!!.get(3))

                rightRealSingleParamsView.content.visibility = View.INVISIBLE
                rightRealSingleParamsView.checkEdit1.setText("")
                rightRealSingleParamsView.checkEdit3.setText("")
                rightRealSingleParamsView.checkEdit4.setText("")
                rightRealSingleParamsView.checkCpdLeftRealSpinner.setSelect(0)
                rightRealSingleParamsView.checkCpdLeftRealSpinner.setText(mPicList!!.get(0))
            }



            if (mTypeRightList!!.get(0).equals(damageV3Bean.beamRightDesignSectionType)){
                rightDesignSingleParamsView.content.visibility = View.VISIBLE
                rightDesignSingleParamsView.checkEdit1.setText(damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(0))
                rightDesignSingleParamsView.checkEdit3.setText(damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(2))
                rightDesignSingleParamsView.checkEdit4.setText(damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(3))
                rightDesignSingleParamsView.checkCpdLeftRealSpinner.setSelect(mPicList!!.indexOf(damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(1)))
                rightDesignSingleParamsView.checkCpdLeftRealSpinner.setText(damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(1))

                rightDesignDoubleParamsView.content.visibility = View.INVISIBLE
                rightDesignDoubleParamsView.checkEdit1.setText("")
                rightDesignDoubleParamsView.checkEdit2.setText("")
                rightDesignDoubleParamsView.checkEdit3.setText("")
                rightDesignDoubleParamsView.checkEdit4.setText("")
            }else{
                rightDesignDoubleParamsView.content.visibility = View.VISIBLE
                rightDesignDoubleParamsView.checkEdit1.setText(damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(0))
                rightDesignDoubleParamsView.checkEdit2.setText(damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(1))
                rightDesignDoubleParamsView.checkEdit3.setText(damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(2))
                rightDesignDoubleParamsView.checkEdit4.setText(damageV3Bean.beamRightDesignSectionTypeParamsList!!.get(3))

                rightDesignSingleParamsView.content.visibility = View.INVISIBLE
                rightDesignSingleParamsView.checkEdit1.setText("")
                rightDesignSingleParamsView.checkEdit3.setText("")
                rightDesignSingleParamsView.checkEdit4.setText("")
                rightDesignSingleParamsView.checkCpdLeftRealSpinner.setSelect(0)
                rightDesignSingleParamsView.checkCpdLeftRealSpinner.setText(mPicList!!.get(0))
            }

            //TODO 图片

            rightDesignView.checkEdit.setText(damageV3Bean.beamRightDesignStirrupsTypeList!!.get(3))

            rightDesignView.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.setSelect(
                mTypeRightList2!!.indexOf(damageV3Bean.beamRightDesignStirrupsTypeList!!.get(0))
            )
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


}