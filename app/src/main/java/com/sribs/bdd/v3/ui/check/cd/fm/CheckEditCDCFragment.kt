package com.sribs.bdd.v3.ui.check.cd.fm

import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
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
import com.sribs.bdd.v3.util.SpannableUtils
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.utils.FileUtil
import kotlinx.android.synthetic.main.fragment_check_componentdetection_column_right_design_edit.view.*
import java.io.File
import java.util.*

/**
 * 构件检测-柱
 */
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
    private val REQUEST_CODE_COLUMN_RIGHT_REAL_WHITE_FLLOR = 30 //
    private val REQUEST_CODE_COLUMN_RIGHT_DESIGN_WHITE_FLLOR = 31 //
    private val REQUEST_COLUMN_REAL_TAKE_PHOTO = 18 //选择图片
    private val REQUEST_COLUMN_DESIGN_TAKE_PHOTO = 19 //选择图片

    private var mPicLeftRealList: ArrayList<String>? = ArrayList()
    private var mPicLeftDesignList: ArrayList<String>? = ArrayList()
    private var mPicRightRealList: ArrayList<String>? = ArrayList()
    private var mPicRightDesignList: ArrayList<String>? = ArrayList()

    private  var mCheckLeftDesignStatus = false
    private  var mCheckRightDesignStatus = false
    private  var mCheckRightRealStatus = false
    private  var mIsCircleColumProtect = false


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

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mTypeList!!.addAll(Arrays.asList("矩形", "圆形", "圆管", "H型", "其他"))
        mTypeRightList!!.addAll(Arrays.asList("矩形", "圆形", "其他"))
        mPicList!!.addAll(Arrays.asList("A", "B"))
        mTypeRight2List!!.addAll(Arrays.asList("无加密", "有加密"))


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




        /**
         * 默认选中净高  装饰面板厚度不可输入
         */

        mBinding.checkCpdSubtitle1.checkEditName.text = SpannableUtils.setTextColor("*柱名称",
        0,1, Color.RED)

        mBinding.checkCpdSubtitle1.checkEdit.hint = "请输入柱名称"

        mBinding.checkCpdSubtitle2.checkEditName.text = SpannableUtils.setTextColor("*轴线",
            0,1, Color.RED)
        mBinding.checkCpdSubtitle2Second.checkEditName.text = SpannableUtils.setTextColor("*轴线",
            0,1, Color.RED)
        mBinding.checkCpdSubtitle2Second.checkEdit.hint = "请输入轴线"
        rightDesignRowSteelView!!.checkEditName2.text = "钢筋规格"
        rightRealRowSteelView!!.checkEditName2.text = "钢筋规格"
        rightDesignMeasuredView!!.checkEditName2.text = "钢筋规格"
        rightRealMeasuredView!!.checkEditName2.text = "钢筋规格"

        leftRealView!!.checkCpdLeftRealDesignTv.text ="实测截面类型"



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

        rightRealRowSteelAnotherView!!.checkCpdSubtitleConfirm.setOnClickListener {
            ARouter.getInstance().build(com.sribs.common.ARouterPath.DRAW_WHITE).navigation(activity,REQUEST_CODE_COLUMN_RIGHT_REAL_WHITE_FLLOR)
        }

        rightDesignRowSteelAnotherView!!.checkCpdSubtitleConfirm.setOnClickListener {
            ARouter.getInstance().build(com.sribs.common.ARouterPath.DRAW_WHITE).navigation(activity,REQUEST_CODE_COLUMN_RIGHT_DESIGN_WHITE_FLLOR)
        }


        leftRealView!!.checkCpdLeftRealSpinner1.setSpinnerData(mTypeList)
            .setSpinnerTextGravity(
               Gravity.CENTER
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


                        leftDesignView!!.checkCpdLeftRealSpinner1.setSelect(0)
                        leftDesignView!!.checkCpdLeftRealSpinner1.setText(mTypeList!!.get(0))

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

                        leftDesignView!!.checkCpdLeftRealSpinner1.setSelect(1)
                        leftDesignView!!.checkCpdLeftRealSpinner1.setText(mTypeList!!.get(1))
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

                        leftDesignView!!.checkCpdLeftRealSpinner1.setSelect(2)
                        leftDesignView!!.checkCpdLeftRealSpinner1.setText(mTypeList!!.get(2))

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

                        leftDesignView!!.checkCpdLeftRealSpinner1.setSelect(3)
                        leftDesignView!!.checkCpdLeftRealSpinner1.setText(mTypeList!!.get(3))

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
                        leftDesignView!!.checkCpdLeftRealSpinner1.setSelect(4)
                        leftDesignView!!.checkCpdLeftRealSpinner1.setText(mTypeList!!.get(4))

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

        leftDesignView!!.checkCpdLeftRealSpinner1.setSpinnerData(mTypeList)
            .setSpinnerTextGravity(
               Gravity.CENTER
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

        leftRealRectangleView!!.firstTvName.text = "南北侧"
        leftRealRectangleView!!.secondTvName.text = "东西侧"

        leftDesignRectangleView!!.firstTvName.text = "南北侧"
        leftDesignRectangleView!!.secondTvName.text = "东西侧"

        rightRealProtectView!!.firstTvName.text = "南北侧"
        rightRealProtectView!!.sencondTvName.text = "东西侧"







        rightRealView!!.checkCpdBeamPic.setOnClickListener {
            openImgSelector(REQUEST_COLUMN_REAL_TAKE_PHOTO)
        }

    /*    rightDesignView!!.checkCpdBeamPic.setOnClickListener {
            openImgSelector(REQUEST_COLUMN_DESIGN_TAKE_PHOTO)
        }*/

        rightRealRowSteelView!!.checkCpdLeftRealSpinner2.setSpinnerData(
            mTypeRightList
        )
            .setSpinnerTextGravity(
               Gravity.CENTER
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


                        rightDesignView!!.checkCpdColumnRightRealRowSteel.checkCpdLeftRealSpinner2.setSelect(0)
                        rightDesignView!!.checkCpdColumnRightRealRowSteel.checkCpdLeftRealSpinner2.setText(mTypeRightList!!.get(0))

                        (rightDesignRowSteelParamsView!! as ItemComponentDetectionColumnRightRealRowSteelParamsBinding).content.visibility =
                            View.VISIBLE
                        (rightDesignRowSteelCircleView!! as ItemComponentDetectionColumnRightRealRowSteelParams2Binding).content.visibility =
                            View.INVISIBLE
                        (rightDesignRowSteelAnotherView!! as ItemComponentDetectionColumnRightRealRowSteelParams3Binding).content.visibility =
                            View.INVISIBLE

                        val lp: ViewGroup.LayoutParams
                        lp = rightRealRowSteelParamsView!!.content.getLayoutParams()
                        lp.width = -1
                        lp.height = getResources().getDimensionPixelSize(R.dimen._45sdp)
                        rightRealRowSteelParamsView!!.content.setLayoutParams(lp)

                        val lp2: ViewGroup.LayoutParams
                        lp2 = rightDesignRowSteelParamsView!!.content.getLayoutParams()
                        lp2.width = -1
                        lp2.height = getResources().getDimensionPixelSize(R.dimen._45sdp)
                        rightDesignRowSteelParamsView!!.content.setLayoutParams(lp2)
                        mIsCircleColumProtect = false
                        rightRealProtectView!!.sencondComma.visibility = View.VISIBLE
                        rightRealProtectView!!.sencondLl.visibility =View.VISIBLE
                        rightRealProtectView!!.firstTvName.visibility = View.VISIBLE
                    }
                    1 -> {
                        rightRealRowSteelParamsView!!.content.visibility =
                            View.INVISIBLE
                        rightRealRowSteelCircleView!!.content.visibility =
                            View.VISIBLE
                        rightRealRowSteelAnotherView!!.content.visibility =
                            View.INVISIBLE

                        rightDesignView!!.checkCpdColumnRightRealRowSteel.checkCpdLeftRealSpinner2.setSelect(1)
                        rightDesignView!!.checkCpdColumnRightRealRowSteel.checkCpdLeftRealSpinner2.setText(mTypeRightList!!.get(1))

                        (rightDesignRowSteelParamsView!! as ItemComponentDetectionColumnRightRealRowSteelParamsBinding).content.visibility =
                            View.INVISIBLE
                        (rightDesignRowSteelCircleView!! as ItemComponentDetectionColumnRightRealRowSteelParams2Binding).content.visibility =
                            View.VISIBLE
                        (rightDesignRowSteelAnotherView!! as ItemComponentDetectionColumnRightRealRowSteelParams3Binding).content.visibility =
                            View.INVISIBLE

                        val lp: ViewGroup.LayoutParams
                        lp = rightRealRowSteelParamsView!!.content.getLayoutParams()
                        lp.width = -1
                        lp.height = getResources().getDimensionPixelSize(R.dimen._20sdp)
                        rightRealRowSteelParamsView!!.content.setLayoutParams(lp)

                        val lp2: ViewGroup.LayoutParams
                        lp2 = rightDesignRowSteelParamsView!!.content.getLayoutParams()
                        lp2.width = -1
                        lp2.height = getResources().getDimensionPixelSize(R.dimen._20sdp)
                        rightDesignRowSteelParamsView!!.content.setLayoutParams(lp2)

                        mIsCircleColumProtect = true
                        rightRealProtectView!!.sencondComma.visibility = View.GONE
                        rightRealProtectView!!.sencondLl.visibility =View.GONE
                        rightRealProtectView!!.firstTvName.visibility = View.INVISIBLE
                    }
                    2 -> {
                        rightRealRowSteelParamsView!!.content.visibility =
                            View.INVISIBLE
                        rightRealRowSteelCircleView!!.content.visibility =
                            View.INVISIBLE
                        rightRealRowSteelAnotherView!!.content.visibility =
                            View.VISIBLE

                        rightDesignView!!.checkCpdColumnRightRealRowSteel.checkCpdLeftRealSpinner2.setSelect(2)
                        rightDesignView!!.checkCpdColumnRightRealRowSteel.checkCpdLeftRealSpinner2.setText(mTypeRightList!!.get(2))

                        (rightDesignRowSteelParamsView!! as ItemComponentDetectionColumnRightRealRowSteelParamsBinding).content.visibility =
                            View.INVISIBLE
                        (rightDesignRowSteelCircleView!! as ItemComponentDetectionColumnRightRealRowSteelParams2Binding).content.visibility =
                            View.INVISIBLE
                        (rightDesignRowSteelAnotherView!! as ItemComponentDetectionColumnRightRealRowSteelParams3Binding).content.visibility =
                            View.VISIBLE

                        val lp: ViewGroup.LayoutParams
                        lp = rightRealRowSteelParamsView!!.content.getLayoutParams()
                        lp.width = -1
                        lp.height = getResources().getDimensionPixelSize(R.dimen._20sdp)
                        rightRealRowSteelParamsView!!.content.setLayoutParams(lp)


                        val lp2: ViewGroup.LayoutParams
                        lp2 = rightDesignRowSteelParamsView!!.content.getLayoutParams()
                        lp2.width = -1
                        lp2.height = getResources().getDimensionPixelSize(R.dimen._20sdp)
                        rightDesignRowSteelParamsView!!.content.setLayoutParams(lp2)
                        mIsCircleColumProtect = false

                        rightRealProtectView!!.sencondComma.visibility = View.VISIBLE
                        rightRealProtectView!!.sencondLl.visibility =View.VISIBLE
                        rightRealProtectView!!.firstTvName.visibility = View.VISIBLE
                    }
                }

            }.build()




        rightRealRowSteelView!!.checkCpdLeftRealSpinner3.setSpinnerData(
            mPicList
        )
            .setSpinnerTextGravity(
               Gravity.CENTER
            ).setSpinnerCallback { position: Int ->
                currentRightRealType2 = position
                LogUtils.d("实测纵筋符号" + currentRightRealType2)
            }.setTypeface(Typeface.createFromAsset(activity?.assets,"fonts/SJQY.cb6e0829.TTF"))
            .build()

        rightRealRowSteelView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(1))
        rightRealRowSteelView!!.checkCpdLeftRealSpinner3.setSelect(1)

        rightDesignRowSteelView!!.checkCpdLeftRealSpinner3.setSpinnerData(
            mPicList
        )
            .setSpinnerTextGravity(
               Gravity.CENTER
            ).setSpinnerCallback { position: Int ->
                currentRightDesignType2 = position
                LogUtils.d("设计纵筋符号" + currentRightDesignType2)

            }.setTypeface(Typeface.createFromAsset(activity?.assets,"fonts/SJQY.cb6e0829.TTF"))
            .build()

        rightDesignRowSteelView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(1))
        rightDesignRowSteelView!!.checkCpdLeftRealSpinner3.setSelect(1)

        (rightRealMeasuredView!! as ItemComponentDetectionBeamRightRealMeasuredStirrupsEditBinding).checkCpdLeftRealSpinner2.setSpinnerData(
            mTypeRight2List
        )
            .setSpinnerTextGravity(Gravity.CENTER)
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
            .setSpinnerTextGravity(Gravity.CENTER)
            .setSpinnerCallback { position: Int ->
                currentRightRealType4 = position
                LogUtils.d("实测箍筋符号：" + mPicList!!.get(currentRightRealType4))
            }.setTypeface(Typeface.createFromAsset(activity?.assets,"fonts/SJQY.cb6e0829.TTF"))
            .build()


        rightDesignRowSteelView!!.checkCpdLeftRealSpinner2.setSpinnerData(
            mTypeRightList
        )
            .setSpinnerTextGravity(
               Gravity.CENTER
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

                        val lp: ViewGroup.LayoutParams
                        lp = rightDesignRowSteelParamsView!!.content.getLayoutParams()
                        lp.width = -1
                        lp.height = getResources().getDimensionPixelSize(R.dimen._45sdp)
                        rightDesignRowSteelParamsView!!.content.setLayoutParams(lp)

                    }
                    1 -> {
                        rightDesignRowSteelParamsView!!.content.visibility =
                            View.INVISIBLE
                        rightDesignRowSteelCircleView!!.content.visibility =
                            View.VISIBLE
                        rightDesignRowSteelAnotherView!!.content.visibility =
                            View.INVISIBLE

                        val lp: ViewGroup.LayoutParams
                        lp = rightDesignRowSteelParamsView!!.content.getLayoutParams()
                        lp.width = -1
                        lp.height = getResources().getDimensionPixelSize(R.dimen._20sdp)
                        rightDesignRowSteelParamsView!!.content.setLayoutParams(lp)
                    }
                    2 -> {
                        rightDesignRowSteelParamsView!!.content.visibility =
                            View.INVISIBLE
                        rightDesignRowSteelCircleView!!.content.visibility =
                            View.INVISIBLE
                        rightDesignRowSteelAnotherView!!.content.visibility =
                            View.VISIBLE

                        val lp: ViewGroup.LayoutParams
                        lp = rightDesignRowSteelParamsView!!.content.getLayoutParams()
                        lp.width = -1
                        lp.height = getResources().getDimensionPixelSize(R.dimen._20sdp)
                        rightDesignRowSteelParamsView!!.content.setLayoutParams(lp)
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
            .setSpinnerTextGravity(Gravity.CENTER)
            .setSpinnerCallback { position: Int ->
                currentRightDesignType3 = position
                if (currentRightDesignType3==1){
                    rightDesignView!!.ll11.visibility = View.VISIBLE
                }else{
                    rightDesignView!!.ll11.visibility = View.GONE
                }
                LogUtils.d("设计箍筋类型" + mTypeRight2List!!.get(currentRightDesignType3))
            }.build()

        rightDesignMeasuredView!!.checkCpdLeftRealSpinner3.setSpinnerData(
            mPicList
        )
            .setSpinnerTextGravity(Gravity.CENTER)
            .setSpinnerCallback { position: Int ->
                currentRightDesignType4 = position
                LogUtils.d("设计箍筋符号：" + mPicList!!.get(currentRightDesignType4))
            }.setTypeface(Typeface.createFromAsset(activity?.assets,"fonts/SJQY.cb6e0829.TTF"))
            .build()

        checkLeftDesignStatus(false)
        checkRightDesignStatus(false)
        checkRightRealStatus(true)

        leftRealView!!.checkCpdLeftMenu.checkCpdLeftMenu4.animation = null
        leftRealView!!.checkCpdLeftMenu.checkCpdLeftMenu4.setOnCheckedChangeListener { buttonView, isChecked ->
            leftDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = isChecked
            checkLeftDesignStatus(isChecked)
        }

        leftDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu4.animation = null
        leftDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu4.setOnCheckedChangeListener { buttonView, isChecked ->
            leftRealView!!.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = isChecked
            checkLeftDesignStatus(isChecked)
        }

        rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu4.animation = null
        rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu4.setOnCheckedChangeListener { buttonView, isChecked ->
            rightRealView!!.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = isChecked
            checkRightDesignStatus(isChecked)
        }

        rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu4.animation = null
        rightRealView!!.checkCpdLeftMenu.checkCpdLeftMenu4.setOnCheckedChangeListener { buttonView, isChecked ->
            rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = isChecked
            checkRightDesignStatus(isChecked)
        }

        rightRealView!!.checkCpdLeftMenu.checkCpdLeftMenu3.animation = null
        rightRealView!!.checkCpdLeftMenu.checkCpdLeftMenu3.setOnCheckedChangeListener { buttonView, isChecked ->
            rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu3.isChecked = isChecked
            checkRightRealStatus(isChecked)
        }

        rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu3.animation = null
        rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu3.setOnCheckedChangeListener { buttonView, isChecked ->
            rightRealView!!.checkCpdLeftMenu.checkCpdLeftMenu3.isChecked = isChecked
            checkRightRealStatus(isChecked)
        }

        rightRealView!!.checkCpdColumnRightRealRowSteel.checkEdit.visibility = View.GONE
        rightRealView!!.checkCpdColumnRightRealRowSteel.checkEditName2.visibility = View.GONE
        rightRealView!!.checkCpdColumnRightRealRowSteel.tv2.visibility = View.GONE

        rightDesignView!!.checkCpdColumnRightRealRowSteel.checkEdit.visibility = View.GONE
        rightDesignView!!.checkCpdColumnRightRealRowSteel.checkEditName2.visibility = View.GONE
        rightDesignView!!.checkCpdColumnRightRealRowSteel.tv2.visibility = View.GONE


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

            mAddAnnotReF = (activity as CheckComponentDetectionActivity).mCurrentAddAnnotReF

            rightRealView!!.checkCpdLeftMenu.checkCpdLeftMenu3.visibility = View.VISIBLE
            rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu3.visibility = View.VISIBLE



            generateParamsList()

            var axis:String
            var protectThicknessList:ArrayList<String>
            var axisList:ArrayList<String>
            if (mBinding.checkCpdSubtitle2Second.content.visibility ==View.VISIBLE){
                axis = mBinding.checkCpdSubtitle2Second.checkEdit.text.toString()
                axisList = arrayListOf("","","")
            }else{
                axis = ""
                axisList = arrayListOf(
                    mBinding.checkCpdSubtitle2.checkCpdBeamMenuAxis1.text.toString(),
                    mBinding.checkCpdSubtitle2.checkCpdBeamMenuAxis2.text.toString(),
                )
            }

            if (rightRealProtectView!!.sencondLl.visibility ==View.VISIBLE){
                protectThicknessList = arrayListOf(
                    rightRealProtectView!!.checkEditProtect.text.toString(),
                    rightRealProtectView!!.checkEditProtect2.text.toString()
                )
            }else{
                protectThicknessList = arrayListOf(rightRealProtectView!!.checkEditProtect.text.toString())
            }

            var damage = DamageV3Bean(
                -1,
                (activity as CheckComponentDetectionActivity).mCurrentDrawing!!.drawingID!!,
                "柱",
                0,
                mAddAnnotReF,
                "",
                if (mDamageCreateTime < 0) System.currentTimeMillis() else mDamageCreateTime,
                mBinding.checkCpdSubtitle1.checkEdit.text.toString(),
                axis,
                axisList,
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
                 //   rightRealRowSteelView!!.checkEdit.text.toString()
                ),
                rightRealSectionTypeParamsList,
                mPicRightRealList!!,
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
                protectThicknessList,
                rightRealView!!.checkCpdLeftRealRemarkContent.text.toString(),
                arrayListOf(FileUtil.getFileName(mRightRealPicSrc)?:"",mRightRealPicSrc),
                //右侧设计
                arrayListOf(
                    mTypeRightList!!.get(currentRightDesignType),
                    mPicList!!.get(currentRightDesignType2),
                //    rightDesignRowSteelView!!.checkEdit.text.toString()
                ),
                rightDesignSectionTypeParamsList,
                mPicRightDesignList!!,
                arrayListOf(
                    mTypeRight2List!!.get(currentRightDesignType3),
                    mPicList!!.get(currentRightDesignType4),
                    rightDesignMeasuredView!!.checkEdit2.text.toString(),
                    rightDesignView!!.checkEditEncrypt.text.toString(),
                    rightDesignView!!.ll1.check_edit.text.toString()
                ),
                rightDesignView!!.checkCpdLeftRealRemarkContent.text.toString(),
                arrayListOf(FileUtil.getFileName(mRightDesignPicSrc)?:"",mRightDesignPicSrc),
                arrayListOf(mCheckLeftDesignStatus.toString(),mCheckRightRealStatus.toString(),mCheckRightDesignStatus.toString())
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
                leftRealSectionTypeParamsList!!.addAll(mPicLeftRealList!!)
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
                leftDesignSectionTypeParamsList!!.addAll(mPicLeftDesignList!!)
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
                rightRealSectionTypeParamsList!!.addAll(mPicRightRealList!!)
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
                rightDesignSectionTypeParamsList!!.addAll(mPicRightDesignList!!)
            }
        }

    }

    fun resetView(damageV3Bean: DamageV3Bean?) {
        LogUtils.d("柱 resetView: "+damageV3Bean.toString())
        mBinding.checkCpdSubtitle1.checkEdit.setText((context as CheckComponentDetectionActivity).mCurrentDrawing!!.floorName+"柱")

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


            mPicLeftRealList=null
            mPicLeftDesignList = null
            mPicRightRealList = null
            mPicRightDesignList = null

            mPicLeftRealList = ArrayList()
            mPicLeftDesignList= ArrayList()

            mPicRightRealList = ArrayList()
            mPicRightDesignList = ArrayList()




            mIsCircleColumProtect = false

            rightRealProtectView!!.sencondComma.visibility = View.VISIBLE
            rightRealProtectView!!.sencondLl.visibility =View.VISIBLE
            rightRealProtectView!!.firstTvName.visibility = View.VISIBLE


            mBinding.checkCpdSubtitle2.content.visibility = View.VISIBLE
            mBinding.checkCpdSubtitle2Second.content.visibility = View.INVISIBLE


            leftRealView!!.content.visibility = View.VISIBLE
            leftDesignView!!.content.visibility = View.INVISIBLE

            mBinding.checkCpdSubtitle2.checkCpdBeamMenuAxis1.setText("")
            mBinding.checkCpdSubtitle2.checkCpdBeamMenuAxis2.setText("")
            mBinding.checkCpdSubtitle2Second.checkEdit.setText("")

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

            rightRealRowSteelAnotherView!!.checkCpdAnotherPicStatus.setText("未绘制")
            rightDesignRowSteelAnotherView!!.checkCpdAnotherPicStatus.setText("未绘制")

            ////右侧-纵筋类型
            rightRealRowSteelView!!.checkCpdLeftRealSpinner2.setSelect(0)
            rightRealRowSteelView!!.checkCpdLeftRealSpinner2.setText(mTypeRightList!!.get(0))

            rightRealRowSteelView!!.checkCpdLeftRealSpinner3.setSelect(1)
            rightRealRowSteelView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(1))
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
       //     rightDesignView!!.checkCpdBeamPic.setImageResource(R.color.gray)

            //右侧设计-纵筋类型
            rightDesignRowSteelView!!.checkCpdLeftRealSpinner2.setSelect(0)
            rightDesignRowSteelView!!.checkCpdLeftRealSpinner2.setText(mTypeRightList!!.get(0))
            rightDesignRowSteelView!!.checkCpdLeftRealSpinner3.setSelect(1)
            rightDesignRowSteelView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(1))
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
            rightDesignView!!.ll11.visibility = View.GONE
            rightDesignView!!.checkEditEncrypt.setText("")

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

            rightRealView!!.checkCpdLeftMenu.checkCpdLeftMenu3.visibility = View.VISIBLE
            rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu3.visibility = View.VISIBLE

            leftRealView!!.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = false
            leftDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = false

            rightRealView!!.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = false
            rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = false
            rightRealView!!.checkCpdLeftMenu.checkCpdLeftMenu3.isChecked = true
            rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu3.isChecked = true

            checkLeftDesignStatus(false)
            checkRightDesignStatus(false)
            checkRightRealStatus(true)





            mDamageCreateTime = -1L
        } else {


            mBinding.checkCpdSubtitle2.checkCpdBeamMenuAxis1.setText("")
            mBinding.checkCpdSubtitle2.checkCpdBeamMenuAxis2.setText("")
            mBinding.checkCpdSubtitle2Second.checkEdit.setText("")

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




            if ("true".equals(damageV3Bean.columnCheckStatus!!.get(0))){
                leftRealView!!.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = true
                leftDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = true
                checkLeftDesignStatus(true)
            }else{
                leftRealView!!.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = false
                leftDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = false
                checkLeftDesignStatus(false)
            }

            if ("true".equals(damageV3Bean.columnCheckStatus!!.get(1))){
                rightRealView!!.checkCpdLeftMenu.checkCpdLeftMenu3.isChecked = true
                rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu3.isChecked = true
                checkRightRealStatus(true)
            }else{
                checkRightRealStatus(false)
                rightRealView!!.checkCpdLeftMenu.checkCpdLeftMenu3.isChecked = false
                rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu3.isChecked = false
            }

            if ("true".equals(damageV3Bean.columnCheckStatus!!.get(2))){
                rightRealView!!.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = true
                rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = true
                checkRightDesignStatus(true)

            }else{
                checkRightDesignStatus(false)
                rightRealView!!.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = false
                rightDesignView!!.checkCpdLeftMenu.checkCpdLeftMenu4.isChecked = false
            }



            if (currentRightDesignType3==1){
                rightDesignView!!.ll11.visibility =View.VISIBLE
                rightDesignView!!.checkEditEncrypt.setText(damageV3Bean.rightDesignStirrupsTypeList!!.get(3))
            }else{
                rightDesignView!!.ll11.visibility = View.GONE
            }



       /*

            mPicLeftDesignList = null
            mPicLeftDesignList = ArrayList()

            mPicRightRealList = null
            mPicRightRealList = ArrayList()

            mPicRightDesignList = null
            mPicRightDesignList = ArrayList()*/

            mPicLeftRealList=null
            mPicLeftDesignList = null
            mPicRightRealList = null
            mPicRightDesignList = null

            mPicLeftRealList = damageV3Bean.columnLeftRealPicList



            mPicLeftDesignList = damageV3Bean.columnLeftDesignPicList

            mPicRightRealList = damageV3Bean.rightRealSectionTypeParamsPicList

            mPicRightDesignList = damageV3Bean.rightDesignSectionTypeParamsPicList

            leftRealView!!.content.visibility = View.VISIBLE
            leftDesignView!!.content.visibility = View.INVISIBLE

            rightRealView!!.content.visibility = View.VISIBLE
            rightDesignView!!.content.visibility = View.INVISIBLE

            mBinding.checkCpdSubtitle1.checkEdit.setText(damageV3Bean.columnName)
            if (!damageV3Bean.columnAxisNote.isNullOrEmpty()) {
                mBinding.checkCpdSubtitle2.content.visibility = View.INVISIBLE
                mBinding.checkCpdSubtitle2Second.content.visibility = View.VISIBLE
                mBinding.checkCpdSubtitle2Second.checkEdit.setText(damageV3Bean.columnAxisNote)
            }else{
                mBinding.checkCpdSubtitle2.content.visibility = View.VISIBLE
                mBinding.checkCpdSubtitle2Second.content.visibility = View.INVISIBLE
                mBinding.checkCpdSubtitle2.checkCpdBeamMenuAxis1.setText(
                    damageV3Bean.columnAxisNoteList!!.get(0)
                )
                mBinding.checkCpdSubtitle2.checkCpdBeamMenuAxis2.setText(
                    damageV3Bean.columnAxisNoteList!!.get(1)
                )
            }

            mRightRealPicSrc = damageV3Bean.columnRightRealPic?.get(1)?:""
            mRightDesignPicSrc = damageV3Bean.columnRightDesignPic?.get(1)?:""

            if (mRightRealPicSrc.isNullOrEmpty()){
                rightRealView!!.checkCpdBeamPic.setImageResource( R.color.gray)

            }else{
                rightRealView!!.checkCpdBeamPic.setImageURI(Uri.fromFile(File(mRightRealPicSrc)))

            }
         //   rightDesignView!!.checkCpdBeamPic.setImageURI(Uri.fromFile(File(mRightDesignPicSrc)))

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
                    if (damageV3Bean.columnLeftRealPicList.isNullOrEmpty()){
                        leftRealAnotherView!!.checkCpdAnotherPicStatus.setText("未绘制")

                    }else{
                        leftRealAnotherView!!.checkCpdAnotherPicStatus.setText("已绘制")

                    }
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

                    if (damageV3Bean.columnLeftDesignPicList.isNullOrEmpty()){
                        leftDesignAnotherView!!.checkCpdAnotherPicStatus.setText("未绘制")

                    }else{
                        leftDesignAnotherView!!.checkCpdAnotherPicStatus.setText("已绘制")

                    }
                }
            }

            leftRealView!!.checkCpdLeftRealRemarkContent.setText(damageV3Bean.leftRealNote)

            leftDesignView!!.checkCpdLeftRealRemarkContent.setText(damageV3Bean.leftDesignNote)


            //TODO lulf符号判断
            if ("A".equals(damageV3Bean.rightRealSectionTypeList!!.get(1))) {
                rightRealRowSteelView!!.checkCpdLeftRealSpinner3.setSelect(0)
                rightRealRowSteelView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(0))
            } else {
                rightRealRowSteelView!!.checkCpdLeftRealSpinner3.setSelect(1)
                rightRealRowSteelView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(1))
            }
        //    rightRealRowSteelView!!.checkEdit.setText(damageV3Bean.rightRealSectionTypeList!!.get(2))

            if ("A".equals(damageV3Bean.rightDesignSectionTypeList!!.get(1))) {
                rightDesignRowSteelView!!.checkCpdLeftRealSpinner3.setSelect(0)
                rightDesignRowSteelView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(0))
            } else {
                rightDesignRowSteelView!!.checkCpdLeftRealSpinner3.setSelect(1)
                rightDesignRowSteelView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(1))
            }
         //   rightDesignRowSteelView!!.checkEdit.setText(damageV3Bean.rightDesignSectionTypeList!!.get(2))

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

                    val lp: ViewGroup.LayoutParams
                    lp = rightRealRowSteelParamsView!!.content.getLayoutParams()
                    lp.width = -1
                    lp.height = getResources().getDimensionPixelSize(R.dimen._45sdp)
                    rightRealRowSteelParamsView!!.content.setLayoutParams(lp)

                    mIsCircleColumProtect =false
                    rightRealProtectView!!.sencondComma.visibility = View.VISIBLE
                    rightRealProtectView!!.sencondLl.visibility =View.VISIBLE
                    rightRealProtectView!!.firstTvName.visibility = View.VISIBLE

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

                    val lp: ViewGroup.LayoutParams
                    lp = rightRealRowSteelParamsView!!.content.getLayoutParams()
                    lp.width = -1
                    lp.height = getResources().getDimensionPixelSize(R.dimen._20sdp)
                    rightRealRowSteelParamsView!!.content.setLayoutParams(lp)

                    mIsCircleColumProtect = true
                    rightRealProtectView!!.sencondComma.visibility = View.GONE
                    rightRealProtectView!!.sencondLl.visibility =View.GONE
                    rightRealProtectView!!.firstTvName.visibility = View.INVISIBLE
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

                    val lp: ViewGroup.LayoutParams
                    lp = rightRealRowSteelParamsView!!.content.getLayoutParams()
                    lp.width = -1
                    lp.height = getResources().getDimensionPixelSize(R.dimen._20sdp)
                    rightRealRowSteelParamsView!!.content.setLayoutParams(lp)

                    mIsCircleColumProtect = false
                    rightRealProtectView!!.sencondComma.visibility = View.VISIBLE
                    rightRealProtectView!!.sencondLl.visibility =View.VISIBLE
                    rightRealProtectView!!.firstTvName.visibility = View.VISIBLE


                    if (damageV3Bean.rightRealSectionTypeParamsPicList.isNullOrEmpty()){
                        rightRealRowSteelAnotherView!!.checkCpdAnotherPicStatus.setText("未绘制")

                    }else{
                        rightRealRowSteelAnotherView!!.checkCpdAnotherPicStatus.setText("已绘制")

                    }
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

                    val lp2: ViewGroup.LayoutParams
                    lp2 = rightDesignRowSteelParamsView!!.content.getLayoutParams()
                    lp2.width = -1
                    lp2.height = getResources().getDimensionPixelSize(R.dimen._45sdp)
                    rightDesignRowSteelParamsView!!.content.setLayoutParams(lp2)

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

                    val lp2: ViewGroup.LayoutParams
                    lp2 = rightDesignRowSteelParamsView!!.content.getLayoutParams()
                    lp2.width = -1
                    lp2.height = getResources().getDimensionPixelSize(R.dimen._20sdp)
                    rightDesignRowSteelParamsView!!.content.setLayoutParams(lp2)
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

                    val lp2: ViewGroup.LayoutParams
                    lp2 = rightDesignRowSteelParamsView!!.content.getLayoutParams()
                    lp2.width = -1
                    lp2.height = getResources().getDimensionPixelSize(R.dimen._20sdp)
                    rightDesignRowSteelParamsView!!.content.setLayoutParams(lp2)


                    if (damageV3Bean.rightDesignSectionTypeParamsPicList.isNullOrEmpty()){
                        rightDesignRowSteelAnotherView!!.checkCpdAnotherPicStatus.setText("未绘制")

                    }else{
                        rightDesignRowSteelAnotherView!!.checkCpdAnotherPicStatus.setText("已绘制")

                    }
                }
            }

            //实测箍筋
            if ("A".equals(damageV3Bean.rightRealStirrupsTypeList!!.get(1))) {
                rightRealMeasuredView!!.checkCpdLeftRealSpinner3.setSelect(0)
                rightRealMeasuredView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(0))
            } else {
                rightRealMeasuredView!!.checkCpdLeftRealSpinner3.setSelect(1)
                rightRealMeasuredView!!.checkCpdLeftRealSpinner3.setText(mPicList!!.get(1))
            }
            rightRealMeasuredView!!.checkEdit2.setText(
                damageV3Bean.rightRealStirrupsTypeList!!.get(2)
            )

            if ("A".equals(damageV3Bean.rightDesignStirrupsTypeList!!.get(1))) {
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
            rightDesignView!!.ll1.check_edit.setText(damageV3Bean.rightDesignStirrupsTypeList!!.get(4))


            //实测箍筋-加密区
            if (!damageV3Bean.rightRealStirrupsTypeEncryptList.isNullOrEmpty() && !damageV3Bean.rightRealStirrupsTypeEncryptList!!.get(0).isNullOrEmpty()) {
                rightRealEncryptedView!!.content.visibility = View.VISIBLE
                rightRealView!!.checkCpdBeamRightRealEncryptedText.visibility = View.VISIBLE
                rightRealEncryptedView!!.checkEdit.setText( damageV3Bean.rightRealStirrupsTypeEncryptList!!.get(
                   0
                ))
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

            if (mIsCircleColumProtect){

            }else{
                rightRealProtectView!!.checkEditProtect2.setText(
                    damageV3Bean.rightRealProtectList!!.get(
                        1
                    )
                )
            }



            rightRealView!!.checkCpdLeftRealRemarkContent.setText(damageV3Bean.rightRealNote)
            rightDesignView!!.checkCpdLeftRealRemarkContent.setText(damageV3Bean.rightDesignNote)
            mDamageCreateTime = damageV3Bean.createTime

        }
    }



    fun checkLeftDesignStatus(isEnable: Boolean) {

        mCheckLeftDesignStatus = isEnable

        leftDesignRectangleView!!.checkCpdLeftRectangle1.isEnabled = isEnable
        leftDesignRectangleView!!.checkCpdLeftRectangle2.isEnabled = isEnable

        leftDesignCircleView!!.checkCpdLeftDesignAnotherName.isEnabled =isEnable



        leftDesignCirCleTubeView!!.checkCpdLeftRectangle1.isEnabled = isEnable
        leftDesignCirCleTubeView!!.checkCpdLeftRectangle2.isEnabled = isEnable

        leftDesignHView!!.checkCpdLeftRectangle1.isEnabled = isEnable
        leftDesignHView!!.checkCpdLeftRectangle2.isEnabled = isEnable
        leftDesignHView!!.checkCpdLeftRectangle3.isEnabled = isEnable
        leftDesignHView!!.checkCpdLeftRectangle4.isEnabled = isEnable


        leftDesignAnotherView!!.checkCpdLeftDesignAnotherDescribe.isEnabled = isEnable
        leftDesignAnotherView!!.checkCpdLeftDesignAnotherName.isEnabled = isEnable
        leftDesignAnotherView!!.checkCpdSubtitleConfirm.isEnabled = isEnable
        leftDesignAnotherView!!.checkCpdSubtitleConfirm.isClickable = isEnable


        leftDesignView!!.checkCpdLeftRealSpinner1.isEnabled = isEnable
        leftDesignView!!.checkCpdLeftRealSpinner1.isClickable = isEnable

        leftDesignView!!.checkCpdLeftRealRemarkContent.isEnabled = isEnable

    }

    fun checkRightRealStatus(isEnable: Boolean) {

        mCheckRightRealStatus = isEnable

        rightRealView!!.checkCpdColumnRightRealRowSteel.checkCpdLeftRealSpinner2.isClickable = isEnable
        rightRealView!!.checkCpdColumnRightRealRowSteel.checkCpdLeftRealSpinner2.isEnabled = isEnable
        rightRealView!!.checkCpdColumnRightRealRowSteel.checkCpdLeftRealSpinner3.isClickable = isEnable
        rightRealView!!.checkCpdColumnRightRealRowSteel.checkCpdLeftRealSpinner3.isEnabled = isEnable


        rightRealView!!.checkCpdBeamRightRealSingle.checkEdit1.isEnabled  = isEnable
        rightRealView!!.checkCpdBeamRightRealSingle.checkEdit2.isEnabled  = isEnable
        rightRealView!!.checkCpdBeamRightRealSingle.checkEdit3.isEnabled  = isEnable
        rightRealView!!.checkCpdBeamRightRealSingle.checkEdit4.isEnabled  = isEnable
        rightRealView!!.checkCpdBeamRightRealSingle.checkEdit5.isEnabled  = isEnable


        rightRealView!!.checkCpdBeamRightRealCicrcle.checkEdit.isEnabled = isEnable
        rightRealView!!.checkCpdBeamRightRealCicrcle.checkEdit2.isEnabled = isEnable

        rightRealView!!.checkCpdBeamRightRealAnother.checkEdit.isEnabled = isEnable

        rightRealView!!.checkCpdBeamRightRealAnother.checkCpdSubtitleConfirm.isEnabled =isEnable
        rightRealView!!.checkCpdBeamRightRealAnother.checkCpdSubtitleConfirm.isClickable =isEnable




        rightRealView!!.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.isClickable = isEnable
        rightRealView!!.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.isEnabled = isEnable

        rightRealView!!.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.isClickable = isEnable
        rightRealView!!.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.isEnabled = isEnable

        rightRealView!!.checkCpdBeamRightRealMeasured.checkEdit2.isEnabled = isEnable

        rightRealView!!.checkCpdBeamRightRealNonEncrypted.checkEdit.isEnabled = isEnable
        rightRealView!!.checkCpdBeamRightRealNonEncrypted.checkEdit2.isEnabled = isEnable

        rightRealView!!.checkCpdBeamRightRealEncrypted.checkEdit.isEnabled = isEnable
        rightRealView!!.checkCpdBeamRightRealEncrypted.checkEdit2.isEnabled = isEnable
        rightRealView!!.checkCpdBeamRightRealEncrypted.checkEdit3.isEnabled = isEnable

        rightRealView!!.checkCpdBeamRightRealProtect.checkEditProtect.isEnabled = isEnable
        rightRealView!!.checkCpdBeamRightRealProtect.checkEditProtect2.isEnabled = isEnable

        rightRealView!!.checkCpdLeftRealRemarkContent.isEnabled = isEnable

    }

    //TODO hhh
    fun checkRightDesignStatus(isEnable: Boolean) {

        mCheckRightDesignStatus = isEnable


        rightDesignView!!.checkCpdColumnRightRealRowSteel.checkCpdLeftRealSpinner2.isClickable = isEnable
        rightDesignView!!.checkCpdColumnRightRealRowSteel.checkCpdLeftRealSpinner2.isEnabled = isEnable
        rightDesignView!!.checkCpdColumnRightRealRowSteel.checkCpdLeftRealSpinner3.isClickable = isEnable
        rightDesignView!!.checkCpdColumnRightRealRowSteel.checkCpdLeftRealSpinner3.isEnabled = isEnable


        rightDesignView!!.checkCpdBeamRightRealSingle.checkEdit1.isEnabled  = isEnable
        rightDesignView!!.checkCpdBeamRightRealSingle.checkEdit2.isEnabled  = isEnable
        rightDesignView!!.checkCpdBeamRightRealSingle.checkEdit3.isEnabled  = isEnable
        rightDesignView!!.checkCpdBeamRightRealSingle.checkEdit4.isEnabled  = isEnable
        rightDesignView!!.checkCpdBeamRightRealSingle.checkEdit5.isEnabled  = isEnable


        rightDesignView!!.checkCpdBeamRightRealCicrcle.checkEdit.isEnabled = isEnable
        rightDesignView!!.checkCpdBeamRightRealCicrcle.checkEdit2.isEnabled = isEnable

        rightDesignView!!.checkCpdBeamRightRealAnother.checkEdit.isEnabled = isEnable

        rightDesignView!!.checkCpdBeamRightRealAnother.checkCpdSubtitleConfirm.isEnabled =isEnable
        rightDesignView!!.checkCpdBeamRightRealAnother.checkCpdSubtitleConfirm.isClickable =isEnable



        rightDesignView!!.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.isClickable = isEnable
        rightDesignView!!.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner2.isEnabled = isEnable
        rightDesignView!!.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.isClickable = isEnable
        rightDesignView!!.checkCpdBeamRightRealMeasured.checkCpdLeftRealSpinner3.isEnabled = isEnable

        rightDesignView!!.checkCpdBeamRightRealMeasured.checkEdit2.isEnabled = isEnable


        rightDesignView!!.checkEditEncrypt.isEnabled = isEnable


        rightDesignView!!.ll1.check_edit.isEnabled = isEnable

        rightDesignView!!.checkCpdLeftRealRemarkContent.isEnabled = isEnable

    }


    override fun onClick(data: DrawingV3Bean?) {
        (activity as CheckComponentDetectionActivity).choosePDF(data!!)
    }


    fun setLeftRealPicList(picList: ArrayList<String>){
        mPicLeftRealList?.clear()
        mPicLeftRealList?.addAll(picList)
        if (mPicLeftRealList.isNullOrEmpty()){
            leftRealAnotherView!!.checkCpdAnotherPicStatus.setText("未绘制")
        }else{
            leftRealAnotherView!!.checkCpdAnotherPicStatus.setText("已绘制")
        }
    }

    fun setLeftDesignPicList(picList: ArrayList<String>){
        mPicLeftDesignList?.clear()
        mPicLeftDesignList?.addAll(picList)
        if (mPicLeftDesignList.isNullOrEmpty()){
            leftDesignAnotherView!!.checkCpdAnotherPicStatus.setText("未绘制")
        }else{
            leftDesignAnotherView!!.checkCpdAnotherPicStatus.setText("已绘制")
        }
    }

    fun setRightRealPicList(picList: ArrayList<String>){
        mPicRightRealList?.clear()
        mPicRightRealList?.addAll(picList)
        if (mPicRightRealList.isNullOrEmpty()){
            rightRealRowSteelAnotherView!!.checkCpdAnotherPicStatus.setText("未绘制")
        }else{
            rightRealRowSteelAnotherView!!.checkCpdAnotherPicStatus.setText("已绘制")
        }
    }

    fun setRightDesignPicList(picList: ArrayList<String>){
        mPicRightDesignList?.clear()
        mPicRightDesignList?.addAll(picList)
        if (mPicRightDesignList.isNullOrEmpty()){
            rightDesignRowSteelAnotherView!!.checkCpdAnotherPicStatus.setText("未绘制")
        }else{
            rightDesignRowSteelAnotherView!!.checkCpdAnotherPicStatus.setText("已绘制")
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
           //     rightDesignView!!.checkCpdBeamPic.setImageURI(Uri.fromFile(File(filePath)))
                mRightDesignPicSrc = filePath
            }
        }
    }

}
