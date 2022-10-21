package com.sribs.bdd.v3.ui.check.cd.fm

import android.net.Uri
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
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
import kotlinx.android.synthetic.main.fragment_check_componentdetection_column_right_design_edit.view.*
import java.io.File
import java.util.*


@Route(path = ARouterPath.CHECK_COMPONENT_DETECTION_COLUMN_FRAGMENT)
class CheckEditCDCFragment : BaseFragment(R.layout.fragment_check_componentdetection_column_edit),
    FloorDrawingSpinnerPopupWindow.FloorDrawItemClickCallback {

    val mBinding: FragmentCheckComponentdetectionColumnEditBinding by bindView()


    private var mTypeList: ArrayList<String>? = ArrayList()
    private var mTypeRightList: ArrayList<String>? = ArrayList()
    private var mPicList: ArrayList<String>? = ArrayList()
    private var mTypeRight2List: ArrayList<String>? = ArrayList()

    private var leftRealSectionTypeParamsList: ArrayList<String> = ArrayList()
    private var leftDesignSectionTypeParamsList: ArrayList<String> = ArrayList()
    private var rightRealSectionTypeParamsList: ArrayList<String> = ArrayList()
    private var rightDesignSectionTypeParamsList: ArrayList<String> = ArrayList()

    private var currentLeftRealType = 0
    private var currentLeftDesignType = 0

    private var currentRightRealType = 0
    private var currentRightRealType2 = 0
    private var currentRightRealType3 = 0
    private var currentRightRealType4 = 0

    private var currentRightDesignType = 0
    private var currentRightDesignType2 = 0
    private var currentRightDesignType3 = 0
    private var currentRightDesignType4 = 0

    private var mRightRealPicSrc: String = ""
    private var mRightDesignPicSrc: String = ""
    private val REQUEST_CODE_COLUMN_REAL_WHITE_FLLOR = 16 //
    private val REQUEST_CODE_COLUMN_DESIGN_WHITE_FLLOR = 17 //
    private val REQUEST_COLUMN_REAL_TAKE_PHOTO = 18 //选择图片
    private val REQUEST_COLUMN_DESIGN_TAKE_PHOTO = 19 //选择图片

    private var mPicLeftRealList: ArrayList<String>? = ArrayList()
    private var mPicLeftDesignList: ArrayList<String>? = ArrayList()

    var leftRealView: FragmentCheckComponentdetectionColumnLeftRealEditBinding? = null
    var leftDesignView: FragmentCheckComponentdetectionColumnLeftDesignEditBinding? = null
    var rightRealView: FragmentCheckComponentdetectionColumnRightRealEditBinding? = null
    var rightDesignView: FragmentCheckComponentdetectionColumnRightDesignEditBinding? = null

    //实测截面尺寸- 五种类型
    var leftRealRectangleView: CheckComponentDetectionBeamLeftRectangleItemBinding? = null
    var leftRealCircleView: ItemComponentDetectionColumnLeftItemBinding? = null
    var leftRealCirCleTubeView: CheckComponentDetectionBeamLeftRectangleItemBinding? = null
    var leftRealHView: ItemComponentDetectionBeamLeftHItemBinding? = null
    var leftRealAnotherView:ItemComponentDetectionBeamLeftAnotherItemBinding? = null


    var leftDesignRectangleView: CheckComponentDetectionBeamLeftRectangleItemBinding? = null
    var leftDesignCircleView: ItemComponentDetectionColumnLeftItemBinding? = null
    var leftDesignCirCleTubeView: CheckComponentDetectionBeamLeftRectangleItemBinding? = null
    var leftDesignHView: ItemComponentDetectionBeamLeftHItemBinding? = null
    var leftDesignAnotherView: ItemComponentDetectionBeamLeftAnotherItemBinding? = null

    var rightRealRowSteelView: ItemComponentDetectionColumnRightRealRowSteelBinding? = null
    var rightRealRowSteelParamsView: ItemComponentDetectionColumnRightRealRowSteelParamsBinding? =
        null
    var rightRealRowSteelCircleView: ItemComponentDetectionColumnRightRealRowSteelParams2Binding? =
        null
    var rightRealRowSteelAnotherView: ItemComponentDetectionColumnRightRealRowSteelParams3Binding? =
        null


    var rightDesignRowSteelView: ItemComponentDetectionColumnRightRealRowSteelBinding? = null
    var rightDesignRowSteelParamsView: ItemComponentDetectionColumnRightRealRowSteelParamsBinding? =
        null
    var rightDesignRowSteelCircleView: ItemComponentDetectionColumnRightRealRowSteelParams2Binding? =
        null
    var rightDesignRowSteelAnotherView: ItemComponentDetectionColumnRightRealRowSteelParams3Binding? =
        null


    var rightRealMeasuredView: ItemComponentDetectionBeamRightRealMeasuredStirrupsEditBinding? =
        null
    var rightDesignMeasuredView: ItemComponentDetectionBeamRightRealMeasuredStirrupsEditBinding? =
        null

    var rightRealEncryptedView: ItemComponentDetectionBeamRightRealEncryptedEditBinding? = null

    var rightRealNonEncryptedView: ItemComponentDetectionBeamRightRealNonEncryptedEditBinding? =
        null


    var rightRealProtectView: ItemComponentDetectionBeamRightRealProtectEditBinding? = null


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
            (context as CheckComponentDetectionActivity).scaleDamageInfo(2)
        }

        leftRealView =
            mBinding.checkCpdColumnLeftRealUi
        leftDesignView =
            mBinding.checkCpdColumnLeftDesignUi
        rightRealView =
            mBinding.checkCpdColumnRightRealUi
        rightDesignView =
            mBinding.checkCpdColumnRightDesignUi


        rightRealView!!.checkCpdLeftRealRemarkContent.setOnTouchListener(OnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        })


        //实测截面尺寸- 五种类型
        leftRealRectangleView = leftRealView!!.checkCpdLeftDesignRectangleTv
        leftRealCircleView = leftRealView!!.checkCpdLeftRealSingleTv
        leftRealCirCleTubeView = leftRealView!!.checkCpdLeftRealRectangle2Tv

        leftRealCirCleTubeView!!.editName.setText("圆管")
        leftRealHView = leftRealView!!.checkCpdLeftRealHTv
        leftRealAnotherView = leftRealView!!.checkCpdLeftRealAnotherTv


        leftDesignRectangleView = leftDesignView!!.checkCpdLeftDesignRectangleTv
        leftDesignCircleView = leftDesignView!!.checkCpdLeftRealSingleTv
        leftDesignCirCleTubeView = leftDesignView!!.checkCpdLeftRealRectangle2Tv
        leftDesignCirCleTubeView!!.editName.setText("圆管")
        leftDesignHView = leftDesignView!!.checkCpdLeftRealHTv
        leftDesignAnotherView= leftDesignView!!.checkCpdLeftRealAnotherTv

        rightRealRowSteelView = (rightRealView!!).checkCpdColumnRightRealRowSteel
        rightRealRowSteelParamsView = rightRealView!!.checkCpdBeamRightRealSingle
        rightRealRowSteelCircleView = rightRealView!!.checkCpdBeamRightRealCicrcle
        rightRealRowSteelAnotherView = rightRealView!!.checkCpdBeamRightRealAnother


        rightDesignRowSteelView = (rightDesignView!!).checkCpdColumnRightRealRowSteel
        rightDesignRowSteelParamsView = rightDesignView!!.checkCpdBeamRightRealSingle
        rightDesignRowSteelCircleView = rightDesignView!!.checkCpdBeamRightRealCicrcle
        rightDesignRowSteelAnotherView = rightDesignView!!.checkCpdBeamRightRealAnother


        rightRealMeasuredView = rightRealView!!.checkCpdBeamRightRealMeasured
        rightDesignMeasuredView = rightDesignView!!.checkCpdBeamRightRealMeasured

        rightRealEncryptedView = rightRealView!!.checkCpdBeamRightRealEncrypted

        rightRealNonEncryptedView = rightRealView!!.checkCpdBeamRightRealNonEncrypted


        rightRealProtectView = rightRealView!!.checkCpdBeamRightRealProtect


        mTypeList!!.addAll(Arrays.asList("矩形", "圆形", "圆管", "H型", "其他"))
        mTypeRightList!!.addAll(Arrays.asList("矩形", "圆形", "其他"))
        mPicList!!.addAll(Arrays.asList("1", "2"))
        mTypeRight2List!!.addAll(Arrays.asList("无加密", "有加密"))

        /**
         * 默认选中净高  装饰面板厚度不可输入
         */

        mBinding.checkCpdSubtitle1.checkEditName.text = "柱名称"
        mBinding.checkCpdSubtitle1.checkEdit.hint = "请输入柱名称"
        mBinding.checkCpdSubtitle2Second.checkEditName.text = "轴线"
        mBinding.checkCpdSubtitle2Second.checkEdit.hint = "请输入轴线"
        rightDesignRowSteelView!!.checkEditName2.text = "钢筋规格"
        rightRealRowSteelView!!.checkEditName2.text = "钢筋规格"
        rightDesignMeasuredView!!.checkEditName2.text = "钢筋规格"
        rightRealMeasuredView!!.checkEditName2.text = "钢筋规格"

        leftRealView!!.checkCpdLeftRealDesignTv.setText("实测截面类型")

        mBinding.checkCpdSubtitleChange.setOnClickListener {
            if (mBinding.checkCpdSubtitle2.content.visibility == View.VISIBLE) {
                mBinding.checkCpdSubtitle2.content.visibility = View.INVISIBLE
                mBinding.checkCpdSubtitle2Second.content.visibility = View.VISIBLE
            } else {
                mBinding.checkCpdSubtitle2.content.visibility = View.VISIBLE
                mBinding.checkCpdSubtitle2Second.content.visibility = View.INVISIBLE
            }

        }

        leftRealView!!.checkCpdLeftMenu.checkCpdLeftMenu2.setOnClickListener {
            leftRealView!!.content.visibility = View.INVISIBLE
            leftDesignView!!.content.visibility = View.VISIBLE
        }

        leftDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu1.setOnClickListener {
            leftRealView!!.content.visibility = View.VISIBLE
            leftDesignView!!.content.visibility = View.INVISIBLE
        }

        rightRealView!!.checkCpdLeftMenu.checkCpdLeftMenu2.setOnClickListener {
            rightRealView!!.content.visibility = View.INVISIBLE
            rightDesignView!!.content.visibility = View.VISIBLE
        }

        rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu1.setOnClickListener {
            rightRealView!!.content.visibility = View.VISIBLE
            rightDesignView!!.content.visibility = View.INVISIBLE
        }


        leftRealAnotherView!!.checkCpdSubtitleConfirm.setOnClickListener {
            ARouter.getInstance().build(com.sribs.common.ARouterPath.DRAW_WHITE).navigation(activity,REQUEST_CODE_COLUMN_REAL_WHITE_FLLOR)
        }

        leftDesignAnotherView!!.checkCpdSubtitleConfirm.setOnClickListener {
            ARouter.getInstance().build(com.sribs.common.ARouterPath.DRAW_WHITE).navigation(activity,REQUEST_CODE_COLUMN_DESIGN_WHITE_FLLOR)
        }


        leftRealView!!.checkCpdLeftRealSpinner1.setSpinnerData(mTypeList)
            .setSpinnerTextGravity(
                Gravity.CENTER_VERTICAL
            ).setSpinnerCallback { position: Int ->
                currentLeftRealType = position
                LogUtils.d("实测截面类型" + mTypeList!!.get(currentLeftRealType))
                when (position) {
                    0 -> {
                        leftRealRectangleView!!.content.visibility =
                            View.VISIBLE
                        leftRealCircleView!!.content.visibility =
                            View.INVISIBLE
                        leftRealCirCleTubeView!!.content.visibility =
                            View.INVISIBLE
                        leftRealHView!!.content.visibility =
                            View.INVISIBLE
                        leftRealAnotherView!!.content.visibility =
                            View.INVISIBLE
                    }
                    1 -> {
                        leftRealRectangleView!!.content.visibility =
                            View.INVISIBLE
                        leftRealHView!!.content.visibility =
                            View.INVISIBLE
                        leftRealCirCleTubeView!!.content.visibility =
                            View.INVISIBLE
                        leftRealCircleView!!.content.visibility =
                            View.VISIBLE
                        leftRealAnotherView!!.content.visibility =
                            View.INVISIBLE
                    }
                    2 -> {
                        leftRealRectangleView!!.content.visibility =
                            View.INVISIBLE
                        leftRealHView!!.content.visibility =
                            View.INVISIBLE
                        leftRealCirCleTubeView!!.content.visibility =
                            View.VISIBLE
                        leftRealCircleView!!.content.visibility =
                            View.INVISIBLE
                        leftRealAnotherView!!.content.visibility =
                            View.INVISIBLE
                    }
                    3 -> {
                        leftRealRectangleView!!.content.visibility =
                            View.INVISIBLE
                        leftRealHView!!.content.visibility =
                            View.VISIBLE
                        leftRealCirCleTubeView!!.content.visibility =
                            View.INVISIBLE
                        leftRealCircleView!!.content.visibility =
                            View.INVISIBLE
                        leftRealAnotherView!!.content.visibility =
                            View.INVISIBLE
                    }
                    4 -> {
                        leftRealRectangleView!!.content.visibility =
                            View.INVISIBLE
                        leftRealHView!!.content.visibility =
                            View.INVISIBLE
                        leftRealCirCleTubeView!!.content.visibility =
                            View.INVISIBLE
                        leftRealCircleView!!.content.visibility =
                            View.INVISIBLE
                        leftRealAnotherView!!.content.visibility =
                            View.VISIBLE
                    }

                }
            }.build()

        leftDesignView!!.checkCpdLeftRealSpinner1.setSpinnerData(mTypeList)
            .setSpinnerTextGravity(
                Gravity.CENTER_VERTICAL
            ).setSpinnerCallback { position: Int ->
                currentLeftDesignType = position
                LogUtils.d("设计截面类型" + mTypeList!!.get(currentLeftDesignType))
                when (position) {
                    0 -> {
                        (leftDesignRectangleView!! as CheckComponentDetectionBeamLeftRectangleItemBinding).content.visibility =
                            View.VISIBLE
                        (leftDesignHView!! as ItemComponentDetectionBeamLeftHItemBinding).content.visibility =
                            View.INVISIBLE
                        (leftDesignCirCleTubeView!! as CheckComponentDetectionBeamLeftRectangleItemBinding).content.visibility =
                            View.INVISIBLE
                        leftDesignCircleView!!.content.visibility =
                            View.INVISIBLE
                        leftDesignAnotherView!!.content.visibility =
                            View.INVISIBLE
                    }
                    1 -> {
                        leftDesignRectangleView!!.content.visibility =
                            View.INVISIBLE
                        leftDesignHView!!.content.visibility =
                            View.INVISIBLE
                        leftDesignCirCleTubeView!!.content.visibility =
                            View.INVISIBLE
                        (leftDesignCircleView!! as ItemComponentDetectionColumnLeftItemBinding).content.visibility =
                            View.VISIBLE
                        (leftDesignAnotherView!! as ItemComponentDetectionBeamLeftAnotherItemBinding).content.visibility =
                            View.INVISIBLE
                    }
                    2 -> {
                        leftDesignRectangleView!!.content.visibility =
                            View.INVISIBLE
                        leftDesignHView!!.content.visibility =
                            View.INVISIBLE
                        leftDesignCirCleTubeView!!.content.visibility =
                            View.VISIBLE
                        leftDesignCircleView!!.content.visibility =
                            View.INVISIBLE
                        leftDesignAnotherView!!.content.visibility =
                            View.INVISIBLE
                    }
                    3 -> {
                        leftDesignRectangleView!!.content.visibility =
                            View.INVISIBLE
                        leftDesignHView!!.content.visibility =
                            View.VISIBLE
                        leftDesignCirCleTubeView!!.content.visibility =
                            View.INVISIBLE
                        leftDesignCircleView!!.content.visibility =
                            View.INVISIBLE
                        leftDesignAnotherView!!.content.visibility =
                            View.INVISIBLE
                    }
                    4 -> {
                        leftDesignRectangleView!!.content.visibility =
                            View.INVISIBLE
                        leftDesignHView!!.content.visibility =
                            View.INVISIBLE
                        leftDesignCirCleTubeView!!.content.visibility =
                            View.INVISIBLE
                        leftDesignCircleView!!.content.visibility =
                            View.INVISIBLE
                        leftDesignAnotherView!!.content.visibility =
                            View.VISIBLE
                    }

                }
            }.build()


        rightRealView!!.checkCpdLeftMenu.checkCpdLeftMenu1.text = "实测配筋"
        rightRealView!!.checkCpdLeftMenu.checkCpdLeftMenu2.text = "设计配筋"
        rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu1.text = "实测配筋"
        rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu2.text = "设计配筋"


        rightRealView!!.checkCpdBeamPic.setOnClickListener {
            openImgSelector(REQUEST_COLUMN_REAL_TAKE_PHOTO)
        }

        rightDesignView!!.checkCpdBeamPic.setOnClickListener {
            openImgSelector(REQUEST_COLUMN_DESIGN_TAKE_PHOTO)
        }

        rightRealRowSteelView!!.checkCpdLeftRealSpinner2.setSpinnerData(
            mTypeRightList
        )
            .setSpinnerTextGravity(
                Gravity.CENTER_VERTICAL
            )
            .setSpinnerCallback { position: Int ->
                currentRightRealType = position
                LogUtils.d("实测纵筋类型" + mTypeRightList!!.get(currentRightRealType))
                when (position) {

                    0 -> {
                        (rightRealRowSteelParamsView!! as ItemComponentDetectionColumnRightRealRowSteelParamsBinding).content.visibility =
                            View.VISIBLE
                        (rightRealRowSteelCircleView!! as ItemComponentDetectionColumnRightRealRowSteelParams2Binding).content.visibility =
                            View.INVISIBLE
                        (rightRealRowSteelAnotherView!! as ItemComponentDetectionColumnRightRealRowSteelParams3Binding).content.visibility =
                            View.INVISIBLE
                    }
                    1 -> {
                        rightRealRowSteelParamsView!!.content.visibility =
                            View.INVISIBLE
                        rightRealRowSteelCircleView!!.content.visibility =
                            View.VISIBLE
                        rightRealRowSteelAnotherView!!.content.visibility =
                            View.INVISIBLE
                    }
                    2 -> {
                        rightRealRowSteelParamsView!!.content.visibility =
                            View.INVISIBLE
                        rightRealRowSteelCircleView!!.content.visibility =
                            View.INVISIBLE
                        rightRealRowSteelAnotherView!!.content.visibility =
                            View.VISIBLE
                    }
                }

            }.build()


        rightRealRowSteelView!!.checkCpdLeftRealSpinner3.setSpinnerData(
            mPicList
        )
            .setSpinnerTextGravity(
                Gravity.CENTER_VERTICAL
            ).setSpinnerCallback { position: Int ->
                currentRightRealType2 = position
                LogUtils.d("实测纵筋符号" + currentRightRealType2)
            }.build()

        rightDesignRowSteelView!!.checkCpdLeftRealSpinner3.setSpinnerData(
            mPicList
        )
            .setSpinnerTextGravity(
                Gravity.CENTER_VERTICAL
            ).setSpinnerCallback { position: Int ->
                currentRightDesignType2 = position
                LogUtils.d("设计纵筋符号" + currentRightDesignType2)

            }.build()


        (rightRealMeasuredView!! as ItemComponentDetectionBeamRightRealMeasuredStirrupsEditBinding).checkCpdLeftRealSpinner2.setSpinnerData(
            mTypeRight2List
        )
            .setSpinnerTextGravity(Gravity.CENTER_VERTICAL)
            .setSpinnerCallback { position: Int ->
                currentRightRealType3 = position
                LogUtils.d("实测箍筋类型" + mTypeRight2List!!.get(currentRightRealType3))
                when (position) {
                    0 ->{
                        (rightRealEncryptedView!!).content.visibility = View.GONE
                        rightRealView!!.checkCpdBeamRightRealEncryptedText.visibility = View.GONE
                    }

                    1 ->{
                        rightRealEncryptedView!!.content.visibility = View.VISIBLE
                        rightRealView!!.checkCpdBeamRightRealEncryptedText.visibility = View.VISIBLE
                    }

                }
            }.build()

        rightRealMeasuredView!!.checkCpdLeftRealSpinner3.setSpinnerData(
            mPicList
        )
            .setSpinnerTextGravity(Gravity.CENTER_VERTICAL)
            .setSpinnerCallback { position: Int ->
                currentRightRealType4 = position
                LogUtils.d("实测箍筋符号：" + mPicList!!.get(currentRightRealType4))
            }.build()


        rightDesignRowSteelView!!.checkCpdLeftRealSpinner2.setSpinnerData(
            mTypeRightList
        )
            .setSpinnerTextGravity(
                Gravity.CENTER_VERTICAL
            )
            .setSpinnerCallback { position: Int ->
                currentRightDesignType = position
                LogUtils.d("设计纵筋类型：" + mTypeRightList!!.get(currentRightDesignType))
                when (position) {

                    0 -> {
                        (rightDesignRowSteelParamsView!! as ItemComponentDetectionColumnRightRealRowSteelParamsBinding).content.visibility =
                            View.VISIBLE
                        (rightDesignRowSteelCircleView!! as ItemComponentDetectionColumnRightRealRowSteelParams2Binding).content.visibility =
                            View.INVISIBLE
                        (rightDesignRowSteelAnotherView!! as ItemComponentDetectionColumnRightRealRowSteelParams3Binding).content.visibility =
                            View.INVISIBLE
                    }
                    1 -> {
                        rightDesignRowSteelParamsView!!.content.visibility =
                            View.INVISIBLE
                        rightDesignRowSteelCircleView!!.content.visibility =
                            View.VISIBLE
                        rightDesignRowSteelAnotherView!!.content.visibility =
                            View.INVISIBLE
                    }
                    2 -> {
                        rightDesignRowSteelParamsView!!.content.visibility =
                            View.INVISIBLE
                        rightDesignRowSteelCircleView!!.content.visibility =
                            View.INVISIBLE
                        rightDesignRowSteelAnotherView!!.content.visibility =
                            View.VISIBLE
                    }
                }
            }.build()

        rightDesignRowSteelView!!.checkEditName.text =
            "设计纵筋"
        (rightDesignMeasuredView!! as ItemComponentDetectionBeamRightRealMeasuredStirrupsEditBinding).checkEditName.text =
            "设计箍筋"

        rightDesignMeasuredView!!.checkCpdLeftRealSpinner2.setSpinnerData(
            mTypeRight2List
        )
            .setSpinnerTextGravity(Gravity.CENTER_VERTICAL)
            .setSpinnerCallback { position: Int ->
                currentRightDesignType3 = position
                LogUtils.d("设计箍筋类型" + mTypeRight2List!!.get(currentRightDesignType3))
            }.build()

        rightDesignMeasuredView!!.checkCpdLeftRealSpinner3.setSpinnerData(
            mPicList
        )
            .setSpinnerTextGravity(Gravity.CENTER_VERTICAL)
            .setSpinnerCallback { position: Int ->
                currentRightDesignType4 = position
                LogUtils.d("设计箍筋符号：" + mPicList!!.get(currentRightDesignType4))
            }.build()

        mBinding.checkCpdSubtitleConfirm.setOnClickListener {
            if (mBinding.checkCpdSubtitle1.checkEdit.text.toString().isNullOrEmpty()) {
                showToast("请输入柱名称")
                return@setOnClickListener
            }
            if (mBinding.checkCpdSubtitle2.content.visibility == View.VISIBLE) {
                if (mBinding.checkCpdSubtitle2.checkCpdBeamMenuAxis1.text.toString()
                        .isNullOrEmpty() ||
                    mBinding.checkCpdSubtitle2.checkCpdBeamMenuAxis2.text.toString().isNullOrEmpty()
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

            when (currentLeftRealType) {
                0 -> {
                    if (leftRealRectangleView!!.checkCpdLeftRectangle1.text.toString()
                            .isNullOrEmpty() ||
                        leftRealRectangleView!!.checkCpdLeftRectangle2.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 实测截面尺寸-矩形尺寸")
                        return@setOnClickListener
                    }
                }
                1 -> {
                    if (leftRealCircleView!!.checkCpdLeftDesignAnotherName.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 实测截面尺寸-圆形尺寸")
                        return@setOnClickListener
                    }
                }
                2 -> {
                    if (leftRealCirCleTubeView!!.checkCpdLeftRectangle1.text.toString()
                            .isNullOrEmpty() ||
                        leftRealCirCleTubeView!!.checkCpdLeftRectangle2.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 实测截面尺寸-圆管尺寸")
                        return@setOnClickListener
                    }
                }
                3 -> {
                    if (leftRealHView!!.checkCpdLeftRectangle1.text.toString()
                            .isNullOrEmpty() ||
                        leftRealHView!!.checkCpdLeftRectangle2.text.toString()
                            .isNullOrEmpty() ||
                        leftRealHView!!.checkCpdLeftRectangle3.text.toString()
                            .isNullOrEmpty() ||
                        leftRealHView!!.checkCpdLeftRectangle4.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 实测截面尺寸-H型尺寸")
                        return@setOnClickListener
                    }
                }4->{
                    if(leftRealAnotherView!!.checkCpdLeftDesignAnotherName.text.toString().isNullOrEmpty()||
                            leftRealAnotherView!!.checkCpdLeftDesignAnotherDescribe.text.toString().isNullOrEmpty()||
                            mPicLeftRealList.isNullOrEmpty()){
                        showToast("请输入 实测截面尺寸-其他")
                        return@setOnClickListener
                    }
                }
            }

            if (leftRealView!!.checkCpdLeftRealRemarkContent.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 实测截面尺寸-备注")
                return@setOnClickListener
            }


            //左侧设计界面判断
            when (currentLeftDesignType) {
                0 -> {
                    if (leftDesignRectangleView!!.checkCpdLeftRectangle1.text.toString()
                            .isNullOrEmpty() ||
                        leftDesignRectangleView!!.checkCpdLeftRectangle2.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 设计截面尺寸-矩形尺寸")
                        return@setOnClickListener
                    }
                }
                1 -> {
                    if (leftDesignCircleView!!.checkCpdLeftDesignAnotherName.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 设计截面尺寸-圆形尺寸")
                        return@setOnClickListener
                    }
                }
                2 -> {
                    if (leftDesignCirCleTubeView!!.checkCpdLeftRectangle1.text.toString()
                            .isNullOrEmpty() ||
                        leftDesignCirCleTubeView!!.checkCpdLeftRectangle2.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 设计截面尺寸-圆管尺寸")
                        return@setOnClickListener
                    }
                }
                3 -> {
                    if (leftDesignHView!!.checkCpdLeftRectangle1.text.toString()
                            .isNullOrEmpty() ||
                        leftDesignHView!!.checkCpdLeftRectangle2.text.toString()
                            .isNullOrEmpty() ||
                        leftDesignHView!!.checkCpdLeftRectangle3.text.toString()
                            .isNullOrEmpty() ||
                        leftDesignHView!!.checkCpdLeftRectangle4.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 设计截面尺寸-H型尺寸")
                        return@setOnClickListener
                    }
                }4->{
                if(leftDesignAnotherView!!.checkCpdLeftDesignAnotherName.text.toString().isNullOrEmpty()||
                    leftDesignAnotherView!!.checkCpdLeftDesignAnotherDescribe.text.toString().isNullOrEmpty()||
                    mPicLeftDesignList.isNullOrEmpty()){
                    showToast("请输入 设计截面尺寸-其他")
                    return@setOnClickListener
                }
            }
            }

            if (leftDesignView!!.checkCpdLeftRealRemarkContent.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 设计截面尺寸-备注")
                return@setOnClickListener
            }

            if (rightRealRowSteelView!!.checkEdit.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 实测纵筋-钢筋规格")
                return@setOnClickListener
            }

            when (currentRightRealType) {
                0 -> {
                    if (rightRealRowSteelParamsView!!.checkEdit1.text.toString()
                            .isNullOrEmpty() ||
                        rightRealRowSteelParamsView!!.checkEdit2.text.toString()
                            .isNullOrEmpty() ||
                        rightRealRowSteelParamsView!!.checkEdit3.text.toString()
                            .isNullOrEmpty() ||
                        rightRealRowSteelParamsView!!.checkEdit4.text.toString()
                            .isNullOrEmpty() ||
                        rightRealRowSteelParamsView!!.checkEdit5.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 实测纵筋-矩形参数")
                        return@setOnClickListener
                    }
                }
                1 -> {
                    if (rightRealRowSteelCircleView!!.checkEdit.text.toString()
                            .isNullOrEmpty() ||
                        rightRealRowSteelCircleView!!.checkEdit2.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 实测纵筋-圆形参数")
                        return@setOnClickListener
                    }
                }
                2 -> {
                    if (rightRealRowSteelAnotherView!!.checkEdit.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 实测纵筋-其他参数")
                        return@setOnClickListener
                    }
                }
            }
            if (rightRealMeasuredView!!.checkEdit2.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 实测箍筋-钢筋规格")
                return@setOnClickListener
            }

            when (currentRightRealType3) {
                1 -> {
                    if (rightRealEncryptedView!!.checkEdit.text.toString()
                            .isNullOrEmpty() ||
                        rightRealEncryptedView!!.checkEdit2.text.toString()
                            .isNullOrEmpty() ||
                        rightRealEncryptedView!!.checkEdit3.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 实测箍筋-加密区参数")
                        return@setOnClickListener
                    }
                }
            }

            if ((rightRealNonEncryptedView!! as ItemComponentDetectionBeamRightRealNonEncryptedEditBinding).checkEdit.text.toString()
                    .isNullOrEmpty() ||
                rightRealNonEncryptedView!!.checkEdit2.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 实测箍筋-非加密区参数")
                return@setOnClickListener
            }

            if (rightRealView!!.checkCpdBeamRightRealProtect.checkEditProtect.text.toString()
                    .isNullOrEmpty() ||
                rightRealProtectView!!.checkEditProtect2.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 实测保护层厚度")
                return@setOnClickListener
            }


            if (rightRealView!!.checkCpdLeftRealRemarkContent.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入实测配筋-备注")
                return@setOnClickListener
            }


            //右侧设计页面判断
            if (rightDesignRowSteelView!!.checkEdit.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 设计纵筋-钢筋规格")
                return@setOnClickListener
            }

            when (currentRightDesignType) {
                0 -> {
                    if (rightDesignRowSteelParamsView!!.checkEdit1.text.toString()
                            .isNullOrEmpty() ||
                        rightDesignRowSteelParamsView!!.checkEdit2.text.toString()
                            .isNullOrEmpty() ||
                        rightDesignRowSteelParamsView!!.checkEdit3.text.toString()
                            .isNullOrEmpty() ||
                        rightDesignRowSteelParamsView!!.checkEdit4.text.toString()
                            .isNullOrEmpty() ||
                        rightDesignRowSteelParamsView!!.checkEdit5.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 设计纵筋-矩形参数")
                        return@setOnClickListener
                    }
                }
                1 -> {
                    if (rightDesignRowSteelCircleView!!.checkEdit.text.toString()
                            .isNullOrEmpty() ||
                        rightDesignRowSteelCircleView!!.checkEdit2.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 设计纵筋-圆形参数")
                        return@setOnClickListener
                    }
                }
                2 -> {
                    if (rightDesignRowSteelAnotherView!!.checkEdit.text.toString()
                            .isNullOrEmpty()
                    ) {
                        showToast("请输入 设计纵筋-其他参数")
                        return@setOnClickListener
                    }
                }
            }
            if (rightDesignView!!.checkCpdBeamRightRealMeasured.checkEdit2.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 设计箍筋-钢筋规格")
                return@setOnClickListener
            }
            if (rightDesignView!!.ll1.check_edit.text.toString().isNullOrEmpty()) {
                showToast("请输入设计箍筋-间距")
                return@setOnClickListener
            }


            if (rightDesignView!!.checkCpdLeftRealRemarkContent.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入设计配筋-备注")
                return@setOnClickListener
            }

            mAddAnnotReF = (activity as CheckComponentDetectionActivity).mCurrentAddAnnotReF



            generateParamsList()


            var damage = DamageV3Bean(
                -1,
                (activity as CheckComponentDetectionActivity).mCurrentDrawing!!.drawingID,
                "柱",
                0,
                mAddAnnotReF,
                "",
                if (mDamageCreateTime < 0) System.currentTimeMillis() else mDamageCreateTime,
                mBinding.checkCpdSubtitle1.checkEdit.text.toString(),
                mBinding.checkCpdSubtitle2Second.checkEdit.text.toString(),
                arrayListOf<String>(
                    mBinding.checkCpdSubtitle2.checkCpdBeamMenuAxis1.text.toString(),
                    mBinding.checkCpdSubtitle2.checkCpdBeamMenuAxis2.text.toString()
                ),
                mTypeList!!.get(currentLeftRealType),
                leftRealSectionTypeParamsList,
                leftRealView!!.checkCpdLeftRealRemarkContent.text.toString(),
                mPicLeftRealList!!,
                mTypeList!!.get(currentLeftDesignType),
                leftDesignSectionTypeParamsList,
                leftDesignView!!.checkCpdLeftRealRemarkContent.text.toString(),
                mPicLeftDesignList!!,
                //右侧界面
                arrayListOf(
                    mTypeRightList!!.get(currentRightRealType),
                    mPicList!!.get(currentRightRealType2),
                    rightRealRowSteelView!!.checkEdit.text.toString()
                ),
                rightRealSectionTypeParamsList,
                arrayListOf(
                    mTypeRight2List!!.get(currentRightRealType3),
                    mPicList!!.get(currentRightRealType4),
                    rightRealMeasuredView!!.checkEdit2.text.toString()
                ),
                arrayListOf(
                    rightRealEncryptedView!!.checkEdit.text.toString(),
                    rightRealEncryptedView!!.checkEdit2.text.toString(),
                    rightRealEncryptedView!!.checkEdit3.text.toString()
                ),
                arrayListOf(
                    rightRealNonEncryptedView!!.checkEdit.text.toString(),
                    rightRealNonEncryptedView!!.checkEdit2.text.toString()
                ),
                arrayListOf(
                    rightRealProtectView!!.checkEditProtect.text.toString(),
                    rightRealProtectView!!.checkEditProtect2.text.toString()
                ),
                rightRealView!!.checkCpdLeftRealRemarkContent.text.toString(),
                arrayListOf(FileUtil.getFileName(mRightRealPicSrc)?:"",mRightRealPicSrc),
                //右侧设计
                arrayListOf(
                    mTypeRightList!!.get(currentRightDesignType),
                    mPicList!!.get(currentRightDesignType2),
                    rightDesignRowSteelView!!.checkEdit.text.toString()
                ),
                rightDesignSectionTypeParamsList,
                arrayListOf(
                    mTypeRight2List!!.get(currentRightDesignType3),
                    mPicList!!.get(currentRightDesignType4),
                    rightDesignMeasuredView!!.checkEdit2.text.toString(),
                    rightDesignView!!.ll1.check_edit.text.toString()
                ),
                rightDesignView!!.checkCpdLeftRealRemarkContent.text.toString(),
                arrayListOf(FileUtil.getFileName(mRightDesignPicSrc)?:"",mRightDesignPicSrc)
            )
            (context as CheckComponentDetectionActivity).saveDamage(damage)
        }
    }

    fun generateParamsList() {
        leftRealSectionTypeParamsList!!.clear()
        when (currentLeftRealType) {
            0 -> {
                leftRealSectionTypeParamsList!!.add(leftRealRectangleView!!.checkCpdLeftRectangle1.text.toString())
                leftRealSectionTypeParamsList!!.add(leftRealRectangleView!!.checkCpdLeftRectangle2.text.toString())
            }
            1 -> {
                leftRealSectionTypeParamsList!!.add(leftRealCircleView!!.checkCpdLeftDesignAnotherName.text.toString())
            }
            2 -> {
                leftRealSectionTypeParamsList!!.add(leftRealCirCleTubeView!!.checkCpdLeftRectangle1.text.toString())
                leftRealSectionTypeParamsList!!.add(leftRealCirCleTubeView!!.checkCpdLeftRectangle2.text.toString())
            }
            3 -> {
                leftRealSectionTypeParamsList!!.add(leftRealHView!!.checkCpdLeftRectangle1.text.toString())
                leftRealSectionTypeParamsList!!.add(leftRealHView!!.checkCpdLeftRectangle2.text.toString())
                leftRealSectionTypeParamsList!!.add(leftRealHView!!.checkCpdLeftRectangle3.text.toString())
                leftRealSectionTypeParamsList!!.add(leftRealHView!!.checkCpdLeftRectangle4.text.toString())
            }
            4 -> {
                leftRealSectionTypeParamsList!!.add(leftRealAnotherView!!.checkCpdLeftDesignAnotherName.text.toString())
                leftRealSectionTypeParamsList!!.add(leftRealAnotherView!!.checkCpdLeftDesignAnotherDescribe.text.toString())
            }
        }
        leftDesignSectionTypeParamsList!!.clear()
        when (currentLeftDesignType) {
            0 -> {
                leftDesignSectionTypeParamsList!!.add(leftDesignRectangleView!!.checkCpdLeftRectangle1.text.toString())
                leftDesignSectionTypeParamsList!!.add(leftDesignRectangleView!!.checkCpdLeftRectangle2.text.toString())
            }
            1 -> {
                leftDesignSectionTypeParamsList!!.add(leftDesignCircleView!!.checkCpdLeftDesignAnotherName.text.toString())
            }
            2 -> {
                leftDesignSectionTypeParamsList!!.add(leftDesignCirCleTubeView!!.checkCpdLeftRectangle1.text.toString())
                leftDesignSectionTypeParamsList!!.add(leftDesignCirCleTubeView!!.checkCpdLeftRectangle2.text.toString())
            }
            3 -> {
                leftDesignSectionTypeParamsList!!.add(leftDesignHView!!.checkCpdLeftRectangle1.text.toString())
                leftDesignSectionTypeParamsList!!.add(leftDesignHView!!.checkCpdLeftRectangle2.text.toString())
                leftDesignSectionTypeParamsList!!.add(leftDesignHView!!.checkCpdLeftRectangle3.text.toString())
                leftDesignSectionTypeParamsList!!.add(leftDesignHView!!.checkCpdLeftRectangle4.text.toString())
            }
            4 -> {
                leftDesignSectionTypeParamsList!!.add(leftDesignAnotherView!!.checkCpdLeftDesignAnotherName.text.toString())
                leftDesignSectionTypeParamsList!!.add(leftDesignAnotherView!!.checkCpdLeftDesignAnotherDescribe.text.toString())
                leftDesignSectionTypeParamsList!!.add("图片")
            }
        }
        rightRealSectionTypeParamsList!!.clear()
        when (currentRightRealType) {
            0 -> {
                rightRealSectionTypeParamsList!!.add(rightRealView!!.checkCpdBeamRightRealSingle.checkEdit1.text.toString())
                rightRealSectionTypeParamsList!!.add(rightRealView!!.checkCpdBeamRightRealSingle.checkEdit2.text.toString())
                rightRealSectionTypeParamsList!!.add(rightRealView!!.checkCpdBeamRightRealSingle.checkEdit3.text.toString())
                rightRealSectionTypeParamsList!!.add(rightRealView!!.checkCpdBeamRightRealSingle.checkEdit4.text.toString())
                rightRealSectionTypeParamsList!!.add(rightRealView!!.checkCpdBeamRightRealSingle.checkEdit5.text.toString())
            }
            1 -> {
                rightRealSectionTypeParamsList!!.add(rightRealView!!.checkCpdBeamRightRealCicrcle.checkEdit.text.toString())
                rightRealSectionTypeParamsList!!.add(rightRealView!!.checkCpdBeamRightRealCicrcle.checkEdit2.text.toString())
            }
            2 -> {
                rightRealSectionTypeParamsList!!.add(rightRealView!!.checkCpdBeamRightRealAnother.checkEdit.text.toString())
                rightRealSectionTypeParamsList!!.add("图片")
            }
        }
        rightDesignSectionTypeParamsList!!.clear()
        when (currentRightDesignType) {
            0 -> {
                rightDesignSectionTypeParamsList!!.add(rightDesignView!!.checkCpdBeamRightRealSingle.checkEdit1.text.toString())
                rightDesignSectionTypeParamsList!!.add(rightDesignView!!.checkCpdBeamRightRealSingle.checkEdit2.text.toString())
                rightDesignSectionTypeParamsList!!.add(rightDesignView!!.checkCpdBeamRightRealSingle.checkEdit3.text.toString())
                rightDesignSectionTypeParamsList!!.add(rightDesignView!!.checkCpdBeamRightRealSingle.checkEdit4.text.toString())
                rightDesignSectionTypeParamsList!!.add(rightDesignView!!.checkCpdBeamRightRealSingle.checkEdit5.text.toString())
            }
            1 -> {
                rightDesignSectionTypeParamsList!!.add(rightDesignView!!.checkCpdBeamRightRealCicrcle.checkEdit.text.toString())
                rightDesignSectionTypeParamsList!!.add(rightDesignView!!.checkCpdBeamRightRealCicrcle.checkEdit2.text.toString())
            }
            2 -> {
                rightDesignSectionTypeParamsList!!.add(rightDesignView!!.checkCpdBeamRightRealAnother.checkEdit.text.toString())
                rightDesignSectionTypeParamsList!!.add("图片")
            }
        }

    }

    fun resetView(damageV3Bean: DamageV3Bean?) {
        if (damageV3Bean == null) {

            currentLeftRealType = 0
            currentLeftDesignType = 0
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

            mPicLeftRealList!!.clear()
            mPicLeftDesignList!!.clear()

            mBinding.checkCpdSubtitle2.content.visibility = View.VISIBLE
            mBinding.checkCpdSubtitle2Second.content.visibility = View.INVISIBLE
            mBinding.checkCpdSubtitle1.checkEdit.setText("")

            leftRealView!!.content.visibility = View.VISIBLE
            leftDesignView!!.content.visibility = View.INVISIBLE

            leftRealView!!.checkCpdLeftRealSpinner1.setSelect(0)
            leftRealView!!.checkCpdLeftRealSpinner1.setText(mTypeList!!.get(0))

            leftRealRectangleView!!.content.visibility = View.VISIBLE
            leftRealRectangleView!!.checkCpdLeftRectangle1.setText("")
            leftRealRectangleView!!.checkCpdLeftRectangle2.setText("")

            leftRealHView!!.content.visibility = View.INVISIBLE
            leftRealHView!!.checkCpdLeftRectangle1.setText("")
            leftRealHView!!.checkCpdLeftRectangle2.setText("")
            leftRealHView!!.checkCpdLeftRectangle3.setText("")
            leftRealHView!!.checkCpdLeftRectangle4.setText("")

            leftRealCirCleTubeView!!.content.visibility = View.INVISIBLE
            leftRealCirCleTubeView!!.checkCpdLeftRectangle1.setText("")
            leftRealCirCleTubeView!!.checkCpdLeftRectangle2.setText("")

            leftRealCircleView!!.content.visibility = View.INVISIBLE
            leftRealCircleView!!.checkCpdLeftDesignAnotherName.setText("")

            leftRealAnotherView!!.content.visibility = View.INVISIBLE
            leftRealAnotherView!!.checkCpdLeftDesignAnotherDescribe.setText("")
            leftRealAnotherView!!.checkCpdLeftDesignAnotherName.setText("")


            leftDesignView!!.checkCpdLeftRealSpinner1.setSelect(0)
            leftDesignView!!.checkCpdLeftRealSpinner1.setText(mTypeList!!.get(0))
            leftDesignRectangleView!!.content.visibility = View.VISIBLE
            leftDesignRectangleView!!.checkCpdLeftRectangle1.setText("")
            leftDesignRectangleView!!.checkCpdLeftRectangle2.setText("")


            leftDesignHView!!.content.visibility = View.INVISIBLE
            leftDesignHView!!.checkCpdLeftRectangle1.setText("")
            leftDesignHView!!.checkCpdLeftRectangle2.setText("")
            leftDesignHView!!.checkCpdLeftRectangle3.setText("")
            leftDesignHView!!.checkCpdLeftRectangle4.setText("")

            leftDesignCirCleTubeView!!.content.visibility = View.INVISIBLE
            leftDesignCirCleTubeView!!.checkCpdLeftRectangle1.setText("")
            leftDesignCirCleTubeView!!.checkCpdLeftRectangle2.setText("")


            leftDesignCircleView!!.content.visibility = View.INVISIBLE
            leftDesignCircleView!!.checkCpdLeftDesignAnotherName.setText("")

            leftDesignAnotherView!!.content.visibility = View.INVISIBLE
            leftDesignAnotherView!!.checkCpdLeftDesignAnotherDescribe.setText("")
            leftDesignAnotherView!!.checkCpdLeftDesignAnotherName.setText("")

            leftRealView!!.checkCpdLeftRealRemarkContent.setText("")
            leftDesignView!!.checkCpdLeftRealRemarkContent.setText("")

            //右侧
            rightRealView!!.content.visibility = View.VISIBLE
            rightDesignView!!.content.visibility = View.INVISIBLE

            leftDesignAnotherView!!.checkCpdAnotherPicStatus.setText("未绘制")
            leftRealAnotherView!!.checkCpdAnotherPicStatus.setText("未绘制")

            ////右侧-纵筋类型
            rightRealRowSteelView!!.checkCpdLeftRealSpinner2.setSelect(0)
            rightRealRowSteelView!!.checkCpdLeftRealSpinner2.setText(mTypeRightList!!.get(0))

            rightRealRowSteelView!!.checkCpdLeftRealSpinner3.setSelect(0)
            rightRealRowSteelView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(0))
            rightRealRowSteelView!!.checkEdit.setText("")

            rightRealRowSteelParamsView!!.content.visibility = View.VISIBLE
            rightRealRowSteelParamsView!!.checkEdit1.setText("")
            rightRealRowSteelParamsView!!.checkEdit2.setText("")
            rightRealRowSteelParamsView!!.checkEdit3.setText("")
            rightRealRowSteelParamsView!!.checkEdit4.setText("")
            rightRealRowSteelParamsView!!.checkEdit5.setText("")

            rightRealRowSteelCircleView!!.content.visibility = View.INVISIBLE
            rightRealRowSteelCircleView!!.checkEdit.setText("")
            rightRealRowSteelCircleView!!.checkEdit2.setText("")

            rightRealRowSteelAnotherView!!.content.visibility = View.INVISIBLE
            rightRealRowSteelAnotherView!!.checkEdit.setText("")

            rightRealView!!.checkCpdBeamPic.setImageResource(R.color.gray)
            rightDesignView!!.checkCpdBeamPic.setImageResource(R.color.gray)

            //右侧设计-纵筋类型
            rightDesignRowSteelView!!.checkCpdLeftRealSpinner2.setSelect(0)
            rightDesignRowSteelView!!.checkCpdLeftRealSpinner2.setText(mTypeRightList!!.get(0))
            rightDesignRowSteelView!!.checkCpdLeftRealSpinner3.setSelect(0)
            rightDesignRowSteelView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(0))
            rightDesignRowSteelView!!.checkEdit.setText("")

            rightDesignRowSteelParamsView!!.content.visibility = View.VISIBLE
            rightDesignRowSteelParamsView!!.checkEdit1.setText("")
            rightDesignRowSteelParamsView!!.checkEdit2.setText("")
            rightDesignRowSteelParamsView!!.checkEdit3.setText("")
            rightDesignRowSteelParamsView!!.checkEdit4.setText("")
            rightDesignRowSteelParamsView!!.checkEdit5.setText("")

            rightDesignRowSteelCircleView!!.content.visibility = View.INVISIBLE
            rightDesignRowSteelCircleView!!.checkEdit.setText("")
            rightDesignRowSteelCircleView!!.checkEdit2.setText("")

            rightDesignRowSteelAnotherView!!.content.visibility = View.INVISIBLE
            rightDesignRowSteelAnotherView!!.checkEdit.setText("")

            //右侧实测-实测箍筋
            rightRealMeasuredView!!.checkCpdLeftRealSpinner3.setSelect(0)
            rightRealMeasuredView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(0))

            rightRealMeasuredView!!.checkCpdLeftRealSpinner2.setSelect(0)
            rightRealMeasuredView!!.checkCpdLeftRealSpinner2.setText(mTypeRight2List!!.get(0))
            rightRealMeasuredView!!.checkEdit2.setText("")

            //右侧设计-实测箍筋
            rightDesignMeasuredView!!.checkCpdLeftRealSpinner3.setSelect(0)
            rightDesignMeasuredView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(0))
            rightDesignMeasuredView!!.checkCpdLeftRealSpinner2.setSelect(0)
            rightDesignMeasuredView!!.checkCpdLeftRealSpinner2.setText(mTypeRight2List!!.get(0))
            rightDesignMeasuredView!!.checkEdit2.setText("")
            rightDesignView!!.ll1.check_edit.setText("")

            //右侧实测-加密区
            rightRealEncryptedView!!.content.visibility = View.GONE
            rightRealView!!.checkCpdBeamRightRealEncryptedText.visibility = View.GONE
            rightRealEncryptedView!!.checkEdit.setText("")
            rightRealEncryptedView!!.checkEdit2.setText("")
            rightRealEncryptedView!!.checkEdit3.setText("")

            //右侧实测-非加密
            rightRealNonEncryptedView!!.checkEdit.setText("")
            rightRealNonEncryptedView!!.checkEdit2.setText("")

            //右侧实测-实测保护层厚度
            rightRealProtectView!!.checkEditProtect.setText("")
            rightRealProtectView!!.checkEditProtect2.setText("")

            //右侧实测-备注
            rightRealView!!.checkCpdLeftRealRemarkContent.setText("")

            //右侧实测-备注
            rightDesignView!!.checkCpdLeftRealRemarkContent.setText("")
            mDamageCreateTime = -1L
        } else {

            currentLeftRealType =  mTypeList!!.indexOf(damageV3Bean.leftRealSectionType)
            currentLeftDesignType =  mTypeList!!.indexOf(damageV3Bean.leftDesignSectionType)
            currentRightRealType = mTypeRightList!!.indexOf(damageV3Bean.rightRealSectionTypeList!!.get(0))
            currentRightRealType2 = mPicList!!.indexOf(damageV3Bean.rightRealSectionTypeList!!.get(1))
            currentRightRealType3 = mTypeRight2List!!.indexOf(damageV3Bean.rightRealStirrupsTypeList!!.get(0))
            currentRightRealType4 = mPicList!!.indexOf(damageV3Bean.rightRealStirrupsTypeList!!.get(1))

            currentRightDesignType = mTypeRightList!!.indexOf(damageV3Bean.rightDesignSectionTypeList!!.get(0))
            currentRightDesignType2 =mPicList!!.indexOf(damageV3Bean.rightDesignSectionTypeList!!.get(1))
            currentRightDesignType3 = mTypeRight2List!!.indexOf(damageV3Bean.rightDesignStirrupsTypeList!!.get(0))
            currentRightDesignType4 = mPicList!!.indexOf(damageV3Bean.rightDesignStirrupsTypeList!!.get(1))

            leftRealView!!.content.visibility = View.VISIBLE
            leftDesignView!!.content.visibility = View.INVISIBLE

            rightRealView!!.content.visibility = View.VISIBLE
            rightDesignView!!.content.visibility = View.INVISIBLE

            mBinding.checkCpdSubtitle1.checkEdit.setText(damageV3Bean.columnName)
            if (!damageV3Bean.columnAxisNote.isNullOrEmpty()) {
                mBinding.checkCpdSubtitle2.content.visibility = View.INVISIBLE
                mBinding.checkCpdSubtitle2Second.content.visibility = View.VISIBLE
                mBinding.checkCpdSubtitle2Second.checkEdit.setText(damageV3Bean.columnAxisNote)
            }
            mBinding.checkCpdSubtitle2.checkCpdBeamMenuAxis1.setText(
                damageV3Bean.columnAxisNoteList!!.get(0)
            )
            mBinding.checkCpdSubtitle2.checkCpdBeamMenuAxis2.setText(
                damageV3Bean.columnAxisNoteList!!.get(1)
            )


            rightRealView!!.checkCpdBeamPic.setImageURI(Uri.fromFile(File(damageV3Bean.columnRightRealPic?.get(0))))
            rightDesignView!!.checkCpdBeamPic.setImageURI(Uri.fromFile(File(damageV3Bean.columnRightDesignPic?.get(0))))

            when (damageV3Bean.leftRealSectionType) {
                "矩形" -> {
                    leftRealView!!.checkCpdLeftRealSpinner1.setSelect(0)
                    leftRealView!!.checkCpdLeftRealSpinner1.setText(mTypeList!!.get(0))
                    leftRealRectangleView!!.content.visibility = View.VISIBLE
                    leftRealHView!!.content.visibility = View.INVISIBLE
                    leftRealCircleView!!.content.visibility = View.INVISIBLE
                    leftRealCirCleTubeView!!.content.visibility = View.INVISIBLE
                    leftRealAnotherView!!.content.visibility = View.INVISIBLE
                    leftRealRectangleView!!.checkCpdLeftRectangle1.setText(
                        damageV3Bean.leftRealSectionTypeParamsList!!.get(0)
                    )
                    leftRealRectangleView!!.checkCpdLeftRectangle2.setText(
                        damageV3Bean.leftRealSectionTypeParamsList!!.get(1)
                    )
                }
                "圆形" -> {
                    leftRealView!!.checkCpdLeftRealSpinner1.setSelect(1)
                    leftRealView!!.checkCpdLeftRealSpinner1.setText(mTypeList!!.get(1))
                    leftRealRectangleView!!.content.visibility = View.INVISIBLE
                    leftRealHView!!.content.visibility = View.INVISIBLE
                    leftRealCircleView!!.content.visibility = View.VISIBLE
                    leftRealCirCleTubeView!!.content.visibility = View.INVISIBLE
                    leftRealAnotherView!!.content.visibility = View.INVISIBLE
                    leftRealCircleView!!.checkCpdLeftDesignAnotherName.setText(
                        damageV3Bean.leftRealSectionTypeParamsList!!.get(0)
                    )
                }
                "圆管" -> {
                    leftRealView!!.checkCpdLeftRealSpinner1.setSelect(2)
                    leftRealView!!.checkCpdLeftRealSpinner1.setText(mTypeList!!.get(2))
                    leftRealRectangleView!!.content.visibility = View.INVISIBLE
                    leftRealHView!!.content.visibility = View.INVISIBLE
                    leftRealCircleView!!.content.visibility = View.INVISIBLE
                    leftRealCirCleTubeView!!.content.visibility = View.VISIBLE
                    leftRealAnotherView!!.content.visibility = View.INVISIBLE
                    leftRealCirCleTubeView!!.checkCpdLeftRectangle1.setText(
                        damageV3Bean.leftRealSectionTypeParamsList!!.get(0)
                    )
                    leftRealCirCleTubeView!!.checkCpdLeftRectangle2.setText(
                        damageV3Bean.leftRealSectionTypeParamsList!!.get(1)
                    )
                }
                "H型" -> {
                    leftRealView!!.checkCpdLeftRealSpinner1.setSelect(3)
                    leftRealView!!.checkCpdLeftRealSpinner1.setText(mTypeList!!.get(3))
                    leftRealRectangleView!!.content.visibility = View.INVISIBLE
                    leftRealHView!!.content.visibility = View.VISIBLE
                    leftRealCircleView!!.content.visibility = View.INVISIBLE
                    leftRealCirCleTubeView!!.content.visibility = View.INVISIBLE
                    leftRealAnotherView!!.content.visibility = View.INVISIBLE
                    leftRealHView!!.checkCpdLeftRectangle1.setText(
                        damageV3Bean.leftRealSectionTypeParamsList!!.get(0)
                    )
                    leftRealHView!!.checkCpdLeftRectangle2.setText(
                        damageV3Bean.leftRealSectionTypeParamsList!!.get(1)
                    )
                    leftRealHView!!.checkCpdLeftRectangle3.setText(
                        damageV3Bean.leftRealSectionTypeParamsList!!.get(2)
                    )
                    leftRealHView!!.checkCpdLeftRectangle4.setText(
                        damageV3Bean.leftRealSectionTypeParamsList!!.get(3)
                    )
                }
                "其他" -> {
                    leftRealView!!.checkCpdLeftRealSpinner1.setSelect(4)
                    leftRealView!!.checkCpdLeftRealSpinner1.setText(mTypeList!!.get(4))
                    leftRealRectangleView!!.content.visibility = View.INVISIBLE
                    leftRealHView!!.content.visibility = View.INVISIBLE
                    leftRealCircleView!!.content.visibility = View.INVISIBLE
                    leftRealCirCleTubeView!!.content.visibility = View.INVISIBLE
                    leftRealAnotherView!!.content.visibility = View.VISIBLE
                    leftRealAnotherView!!.checkCpdLeftDesignAnotherName.setText(
                        damageV3Bean.leftRealSectionTypeParamsList!!.get(0)
                    )
                    leftRealAnotherView!!.checkCpdLeftDesignAnotherDescribe.setText(
                        damageV3Bean.leftRealSectionTypeParamsList!!.get(1)
                    )
                    leftRealAnotherView!!.checkCpdAnotherPicStatus.setText("已绘制")

                }
            }

            when (damageV3Bean.leftDesignSectionType) {
                "矩形" -> {
                    leftDesignRectangleView!!.content.visibility = View.VISIBLE
                    leftDesignHView!!.content.visibility = View.INVISIBLE
                    leftDesignCircleView!!.content.visibility = View.INVISIBLE
                    leftDesignCirCleTubeView!!.content.visibility = View.INVISIBLE
                    leftDesignAnotherView!!.content.visibility = View.INVISIBLE
                    leftDesignView!!.checkCpdLeftRealSpinner1.setSelect(0)
                    leftDesignView!!.checkCpdLeftRealSpinner1.setText(mTypeList!!.get(0))
                    leftDesignRectangleView!!.checkCpdLeftRectangle1.setText(
                        damageV3Bean.leftDesignSectionTypeParamsList!!.get(0)
                    )
                    leftDesignRectangleView!!.checkCpdLeftRectangle2.setText(
                        damageV3Bean.leftDesignSectionTypeParamsList!!.get(1)
                    )
                }
                "圆形" -> {
                    leftDesignView!!.checkCpdLeftRealSpinner1.setSelect(1)
                    leftDesignView!!.checkCpdLeftRealSpinner1.setText(mTypeList!!.get(1))
                    leftDesignRectangleView!!.content.visibility = View.INVISIBLE
                    leftDesignHView!!.content.visibility = View.INVISIBLE
                    leftDesignCircleView!!.content.visibility = View.VISIBLE
                    leftDesignCirCleTubeView!!.content.visibility = View.INVISIBLE
                    leftDesignAnotherView!!.content.visibility = View.INVISIBLE
                    leftDesignCircleView!!.checkCpdLeftDesignAnotherName.setText(
                        damageV3Bean.leftDesignSectionTypeParamsList!!.get(0)
                    )
                }
                "圆管" -> {
                    leftDesignView!!.checkCpdLeftRealSpinner1.setSelect(2)
                    leftDesignView!!.checkCpdLeftRealSpinner1.setText(mTypeList!!.get(2))
                    leftDesignRectangleView!!.content.visibility = View.INVISIBLE
                    leftDesignHView!!.content.visibility = View.INVISIBLE
                    leftDesignCircleView!!.content.visibility = View.INVISIBLE
                    leftDesignCirCleTubeView!!.content.visibility = View.VISIBLE
                    leftDesignAnotherView!!.content.visibility = View.INVISIBLE
                    leftDesignCirCleTubeView!!.checkCpdLeftRectangle1.setText(
                        damageV3Bean.leftDesignSectionTypeParamsList!!.get(0)
                    )
                    leftDesignCirCleTubeView!!.checkCpdLeftRectangle2.setText(
                        damageV3Bean.leftDesignSectionTypeParamsList!!.get(1)
                    )
                }
                "H型" -> {
                    leftDesignView!!.checkCpdLeftRealSpinner1.setSelect(3)
                    leftDesignView!!.checkCpdLeftRealSpinner1.setText(mTypeList!!.get(3))
                    leftDesignRectangleView!!.content.visibility = View.INVISIBLE
                    leftDesignHView!!.content.visibility = View.VISIBLE
                    leftDesignCircleView!!.content.visibility = View.INVISIBLE
                    leftDesignCirCleTubeView!!.content.visibility = View.INVISIBLE
                    leftDesignAnotherView!!.content.visibility = View.INVISIBLE
                    leftDesignHView!!.checkCpdLeftRectangle1.setText(
                        damageV3Bean.leftDesignSectionTypeParamsList!!.get(0)
                    )
                    leftDesignHView!!.checkCpdLeftRectangle2.setText(
                        damageV3Bean.leftDesignSectionTypeParamsList!!.get(1)
                    )
                    leftDesignHView!!.checkCpdLeftRectangle3.setText(
                        damageV3Bean.leftDesignSectionTypeParamsList!!.get(2)
                    )
                    leftDesignHView!!.checkCpdLeftRectangle4.setText(
                        damageV3Bean.leftDesignSectionTypeParamsList!!.get(3)
                    )
                }
                "其他" -> {
                    leftDesignView!!.checkCpdLeftRealSpinner1.setSelect(4)
                    leftDesignView!!.checkCpdLeftRealSpinner1.setText(mTypeList!!.get(4))
                    leftDesignRectangleView!!.content.visibility = View.INVISIBLE
                    leftDesignHView!!.content.visibility = View.INVISIBLE
                    leftDesignCircleView!!.content.visibility = View.INVISIBLE
                    leftDesignCirCleTubeView!!.content.visibility = View.INVISIBLE
                    leftDesignAnotherView!!.content.visibility = View.VISIBLE
                    leftDesignAnotherView!!.checkCpdLeftDesignAnotherName.setText(
                        damageV3Bean.leftDesignSectionTypeParamsList!!.get(0)
                    )
                    leftDesignAnotherView!!.checkCpdLeftDesignAnotherDescribe.setText(
                        damageV3Bean.leftDesignSectionTypeParamsList!!.get(1)
                    )
                    leftDesignAnotherView!!.checkCpdAnotherPicStatus.setText("已绘制")
                }
            }

            leftRealView!!.checkCpdLeftRealRemarkContent.setText(damageV3Bean.leftRealNote)

            leftDesignView!!.checkCpdLeftRealRemarkContent.setText(damageV3Bean.leftDesignNote)


            //TODO lulf符号判断
            if ("1".equals(damageV3Bean.rightRealSectionTypeList!!.get(1))) {
                rightRealRowSteelView!!.checkCpdLeftRealSpinner3.setSelect(0)
                rightRealRowSteelView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(0))
            } else {
                rightRealRowSteelView!!.checkCpdLeftRealSpinner3.setSelect(1)
                rightRealRowSteelView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(1))
            }
            rightRealRowSteelView!!.checkEdit.setText(damageV3Bean.rightRealSectionTypeList!!.get(2))

            if ("1".equals(damageV3Bean.rightDesignSectionTypeList!!.get(1))) {
                rightDesignRowSteelView!!.checkCpdLeftRealSpinner3.setSelect(0)
                rightDesignRowSteelView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(0))
            } else {
                rightDesignRowSteelView!!.checkCpdLeftRealSpinner3.setSelect(1)
                rightDesignRowSteelView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(1))
            }
            rightDesignRowSteelView!!.checkEdit.setText(
                damageV3Bean.rightDesignSectionTypeList!!.get(
                    2
                )
            )

            //实测纵筋
            when (damageV3Bean.rightRealSectionTypeList!!.get(0)) {
                "矩形" -> {
                    rightRealRowSteelView!!.checkCpdLeftRealSpinner2.setSelect(0)
                    rightRealRowSteelView!!.checkCpdLeftRealSpinner2.setText(mTypeRightList!!.get(0))
                    rightRealRowSteelParamsView!!.content.visibility = View.VISIBLE
                    rightRealRowSteelCircleView!!.content.visibility = View.INVISIBLE
                    rightRealRowSteelAnotherView!!.content.visibility = View.INVISIBLE
                    rightRealRowSteelParamsView!!.checkEdit1.setText(
                        damageV3Bean.rightRealSectionTypeParamsList!!.get(0)
                    )
                    rightRealRowSteelParamsView!!.checkEdit2.setText(
                        damageV3Bean.rightRealSectionTypeParamsList!!.get(1)
                    )
                    rightRealRowSteelParamsView!!.checkEdit3.setText(
                        damageV3Bean.rightRealSectionTypeParamsList!!.get(2)
                    )
                    rightRealRowSteelParamsView!!.checkEdit4.setText(
                        damageV3Bean.rightRealSectionTypeParamsList!!.get(3)
                    )
                    rightRealRowSteelParamsView!!.checkEdit5.setText(
                        damageV3Bean.rightRealSectionTypeParamsList!!.get(4)
                    )
                }
                "圆形" -> {
                    rightRealRowSteelView!!.checkCpdLeftRealSpinner2.setSelect(1)
                    rightRealRowSteelView!!.checkCpdLeftRealSpinner2.setText(mTypeRightList!!.get(1))
                    rightRealRowSteelParamsView!!.content.visibility = View.INVISIBLE
                    rightRealRowSteelCircleView!!.content.visibility = View.VISIBLE
                    rightRealRowSteelAnotherView!!.content.visibility = View.INVISIBLE
                    rightRealRowSteelCircleView!!.checkEdit.setText(
                        damageV3Bean.rightRealSectionTypeParamsList!!.get(0)
                    )
                    rightRealRowSteelCircleView!!.checkEdit2.setText(
                        damageV3Bean.rightRealSectionTypeParamsList!!.get(1)
                    )
                }
                "其他" -> {
                    rightRealRowSteelView!!.checkCpdLeftRealSpinner2.setSelect(2)
                    rightRealRowSteelView!!.checkCpdLeftRealSpinner2.setText(mTypeRightList!!.get(2))
                    rightRealRowSteelParamsView!!.content.visibility = View.INVISIBLE
                    rightRealRowSteelCircleView!!.content.visibility = View.INVISIBLE
                    rightRealRowSteelAnotherView!!.content.visibility = View.VISIBLE
                    rightRealRowSteelAnotherView!!.checkEdit.setText(
                        damageV3Bean.rightRealSectionTypeParamsList!!.get(0)
                    )
                }
            }

            //设计纵筋
            when (damageV3Bean.rightDesignSectionTypeList!!.get(0)) {
                "矩形" -> {
                    rightDesignRowSteelView!!.checkCpdLeftRealSpinner2.setSelect(0)
                    rightDesignRowSteelView!!.checkCpdLeftRealSpinner2.setText(mTypeRightList!!.get(0))
                    rightDesignRowSteelParamsView!!.content.visibility = View.VISIBLE
                    rightDesignRowSteelCircleView!!.content.visibility = View.INVISIBLE
                    rightDesignRowSteelAnotherView!!.content.visibility = View.INVISIBLE
                    rightDesignRowSteelParamsView!!.checkEdit1.setText(
                        damageV3Bean.rightDesignSectionTypeParamsList!!.get(0)
                    )
                    rightDesignRowSteelParamsView!!.checkEdit2.setText(
                        damageV3Bean.rightDesignSectionTypeParamsList!!.get(1)
                    )
                    rightDesignRowSteelParamsView!!.checkEdit3.setText(
                        damageV3Bean.rightDesignSectionTypeParamsList!!.get(2)
                    )
                    rightDesignRowSteelParamsView!!.checkEdit4.setText(
                        damageV3Bean.rightDesignSectionTypeParamsList!!.get(3)
                    )
                    rightDesignRowSteelParamsView!!.checkEdit5.setText(
                        damageV3Bean.rightDesignSectionTypeParamsList!!.get(4)
                    )
                }
                "圆形" -> {
                    rightDesignRowSteelView!!.checkCpdLeftRealSpinner2.setSelect(1)
                    rightDesignRowSteelView!!.checkCpdLeftRealSpinner2.setText(mTypeRightList!!.get(1))
                    rightDesignRowSteelParamsView!!.content.visibility = View.INVISIBLE
                    rightDesignRowSteelCircleView!!.content.visibility = View.VISIBLE
                    rightDesignRowSteelAnotherView!!.content.visibility = View.INVISIBLE
                    rightDesignRowSteelCircleView!!.checkEdit.setText(
                        damageV3Bean.rightDesignSectionTypeParamsList!!.get(0)
                    )
                    rightDesignRowSteelCircleView!!.checkEdit2.setText(
                        damageV3Bean.rightDesignSectionTypeParamsList!!.get(1)
                    )
                }
                "其他" -> {
                    rightDesignRowSteelView!!.checkCpdLeftRealSpinner2.setSelect(2)
                    rightDesignRowSteelView!!.checkCpdLeftRealSpinner2.setText(mTypeRightList!!.get(2))
                    rightDesignRowSteelParamsView!!.content.visibility = View.INVISIBLE
                    rightDesignRowSteelCircleView!!.content.visibility = View.INVISIBLE
                    rightDesignRowSteelAnotherView!!.content.visibility = View.VISIBLE
                    rightDesignRowSteelAnotherView!!.checkEdit.setText(
                        damageV3Bean.rightDesignSectionTypeParamsList!!.get(0)
                    )
                }
            }


            //TODO  lulf
            //实测箍筋
            if ("1".equals(damageV3Bean.rightRealStirrupsTypeList!!.get(1))) {
                rightRealMeasuredView!!.checkCpdLeftRealSpinner3.setSelect(0)
                rightRealMeasuredView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(0))
            } else {
                rightRealMeasuredView!!.checkCpdLeftRealSpinner3.setSelect(1)
                rightRealMeasuredView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(1))
            }
            rightRealMeasuredView!!.checkEdit2.setText(
                damageV3Bean.rightRealStirrupsTypeList!!.get(2)
            )

            if ("1".equals(damageV3Bean.rightDesignStirrupsTypeList!!.get(1))) {
                rightDesignMeasuredView!!.checkCpdLeftRealSpinner3.setSelect(0)
                rightDesignMeasuredView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(0))
            } else {
                rightDesignMeasuredView!!.checkCpdLeftRealSpinner3.setSelect(1)
                rightDesignMeasuredView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(1))
            }
            rightDesignMeasuredView!!.checkEdit2.setText(
                damageV3Bean.rightDesignStirrupsTypeList!!.get(2
                )
            )
            rightDesignView!!.ll1.check_edit.setText(damageV3Bean.rightDesignStirrupsTypeList!!.get(3))


            //实测箍筋-加密区
            if (!damageV3Bean.rightRealStirrupsTypeEncryptList.isNullOrEmpty() && !damageV3Bean.rightRealStirrupsTypeEncryptList!!.get(0).isNullOrEmpty()) {
                rightRealEncryptedView!!.content.visibility = View.VISIBLE
                rightRealView!!.checkCpdBeamRightRealEncryptedText.visibility = View.VISIBLE
                rightRealEncryptedView!!.checkEdit2.setText(
                    damageV3Bean.rightRealStirrupsTypeEncryptList!!.get(
                        1
                    )
                )
                rightRealEncryptedView!!.checkEdit3.setText(
                    damageV3Bean.rightRealStirrupsTypeEncryptList!!.get(
                        2
                    )
                )
            }else{
                rightRealEncryptedView!!.content.visibility = View.GONE
                rightRealView!!.checkCpdBeamRightRealEncryptedText.visibility = View.GONE
            }

            rightRealNonEncryptedView!!.checkEdit.setText(
                damageV3Bean.rightRealStirrupsTypeNonEncryptList!!.get(
                    0
                )
            )
            rightRealNonEncryptedView!!.checkEdit2.setText(
                damageV3Bean.rightRealStirrupsTypeNonEncryptList!!.get(
                    1
                )
            )

            rightRealProtectView!!.checkEditProtect.setText(
                damageV3Bean.rightRealProtectList!!.get(
                    0
                )
            )
            rightRealProtectView!!.checkEditProtect2.setText(
                damageV3Bean.rightRealProtectList!!.get(
                    1
                )
            )

            rightRealView!!.checkCpdLeftRealRemarkContent.setText(damageV3Bean.rightRealNote)
            rightDesignView!!.checkCpdLeftRealRemarkContent.setText(damageV3Bean.rightDesignNote)
            mDamageCreateTime = damageV3Bean.createTime

        }
    }

    override fun onClick(data: DrawingV3Bean?) {
        (activity as CheckComponentDetectionActivity).choosePDF(data!!)
    }


    fun setRealPicList(picList: ArrayList<String>){
        mPicLeftRealList?.clear()
        mPicLeftRealList?.addAll(picList)
        if (mPicLeftRealList.isNullOrEmpty()){
            leftRealAnotherView!!.checkCpdAnotherPicStatus.setText("未绘制")
        }else{
            leftRealAnotherView!!.checkCpdAnotherPicStatus.setText("已绘制")
        }
    }

    fun setDesignPicList(picList: ArrayList<String>){
        mPicLeftDesignList?.clear()
        mPicLeftDesignList?.addAll(picList)
        if (mPicLeftDesignList.isNullOrEmpty()){
            leftDesignAnotherView!!.checkCpdAnotherPicStatus.setText("未绘制")
        }else{
            leftDesignAnotherView!!.checkCpdAnotherPicStatus.setText("已绘制")
        }
    }



    fun openImgSelector(requestCode:Int) {

        //仅拍照
        ImageSelector
            .builder()
            .onlyTakePhoto(false)
            .canPreview(true)
            .setSingle(true)
            .start(activity as CheckComponentDetectionActivity, requestCode)


    }

    fun setImageBitmap(filePath: String, type:Int){
        when(type){
            REQUEST_COLUMN_REAL_TAKE_PHOTO->
            {
                rightRealView!!.checkCpdBeamPic.setImageURI(Uri.fromFile(File(filePath)))
                mRightRealPicSrc = filePath
            }
            REQUEST_COLUMN_DESIGN_TAKE_PHOTO->
            {
                rightDesignView!!.checkCpdBeamPic.setImageURI(Uri.fromFile(File(filePath)))
                mRightDesignPicSrc = filePath
            }
        }
    }


}
