package com.sribs.bdd.v3.ui.check.cd.fm

import android.graphics.Color
import android.net.Uri
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.donkingliang.imageselector.utils.ImageSelector
import com.radaee.util.CommonUtil
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckBuildStructureFloorBinding
import com.sribs.bdd.databinding.FragmentCheckComponentdetectionBeamEditBinding
import com.sribs.bdd.databinding.FragmentCheckComponentdetectionPlateEditBinding
import com.sribs.bdd.databinding.FragmentCheckComponentdetectionWallEditBinding
import com.sribs.bdd.v3.module.CheckMenuModule
import com.sribs.bdd.v3.popup.FloorDrawingSpinnerPopupWindow
import com.sribs.bdd.v3.ui.check.cd.CheckComponentDetectionActivity
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean
import java.util.*
import kotlin.collections.ArrayList

@Route(path = ARouterPath.CHECK_COMPONENT_DETECTION_WALL_FRAGMENT)
class CheckEditCDWFragment : BaseFragment(R.layout.fragment_check_componentdetection_wall_edit),
    FloorDrawingSpinnerPopupWindow.FloorDrawItemClickCallback {


    private val mBinding: FragmentCheckComponentdetectionWallEditBinding by bindView()

    private var mtypePicList: ArrayList<String>? = ArrayList()

    private var currentRealPicType = ""
    private var currentRealPicType2 = ""

    private var currentDesignPicType = ""
    private var currentDesignPicType2 = ""

    private val REQUEST_WALL_REAL_TAKE_PHOTO = 20 //选择图片
    private val REQUEST_WALL_DESIGN_TAKE_PHOTO = 21 //选择图片

    private var mRightRealPicSrc: String = ""
    private var mRightDesignPicSrc: String = ""
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
        mtypePicList!!.addAll(Arrays.asList("Φ", "二级钢筋"))

        currentRealPicType = mtypePicList!!.get(0)
        currentRealPicType2 = mtypePicList!!.get(0)
        currentDesignPicType = mtypePicList!!.get(0)
        currentDesignPicType2 = mtypePicList!!.get(0)

        mBinding.checkCdpSubtitle1.checkEditName.text = "墙名称"
        mBinding.checkCdpSubtitle1.checkEdit.hint = "请输入墙名称"

        mBinding.checkCdpSubtitle2.checkEditName.text = "轴线"
        mBinding.checkCdpSubtitle2Second.checkEditName.text = "轴线"
        mBinding.checkCdpSubtitle2Second.checkEdit.hint = "请输入轴线"

        mBinding.checkCdpPlateLeftRealUi.checkCdpLeftMenu.checkCpdLeftMenu1.text = "实测墙厚度"
        mBinding.checkCdpPlateLeftRealUi.checkCdpLeftMenu.checkCpdLeftMenu2.text = "设计墙厚度"

        mBinding.checkCdpPlateLeftDesignUi.checkCdpLeftMenu.checkCpdLeftMenu1.text = "实测墙厚度"
        mBinding.checkCdpPlateLeftDesignUi.checkCdpLeftMenu.checkCpdLeftMenu2.text = "设计墙厚度"


        mBinding.checkCdpPlateLeftRealUi.checkCdpPlateLeftRealTv.text = "实测 墙厚度"
        mBinding.checkCdpPlateLeftDesignUi.checkCdpPlateLeftDesignTv.text = "设计 墙厚度"

        mBinding.checkCdpPlateRightRealUi.checkCdpRightMenu.checkCpdLeftMenu1.text = "实测配筋"
        mBinding.checkCdpPlateRightRealUi.checkCdpRightMenu.checkCpdLeftMenu2.text = "设计配筋"

        mBinding.checkCdpPlateRightDesignUi.checkCdpRightMenu.checkCpdLeftMenu1.text = "实测配筋"
        mBinding.checkCdpPlateRightDesignUi.checkCdpRightMenu.checkCpdLeftMenu2.text = "设计配筋"

        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEditName.text =
            "实测 竖向钢筋"

        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkCpdLeftRealSpinner2.setSpinnerData(
            mtypePicList
        ).setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback { position: Int ->
            LogUtils.d("当前选择：$position")
            currentRealPicType = mtypePicList!!.get(position)
        }.build()


        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEditName.text =
            "实测 水平钢筋"

        mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkCpdLeftRealSpinner2.setSpinnerData(
            mtypePicList
        ).setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback { position: Int ->
            LogUtils.d("当前选择：$position")
            currentRealPicType2 = mtypePicList!!.get(position)
        }.build()

        mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkEditName.text =
            "设计 竖向钢筋"
        mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkCpdLeftRealSpinner2.setSpinnerData(
            mtypePicList
        ).setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback { position: Int ->
            LogUtils.d("当前选择：$position")
            currentDesignPicType = mtypePicList!!.get(position)
        }.build()

        mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkEditName.text =
            "设计 水平钢筋"
        mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkCpdLeftRealSpinner2.setSpinnerData(
            mtypePicList
        ).setSpinnerTextGravity(Gravity.CENTER_VERTICAL).setSpinnerCallback { position: Int ->
            LogUtils.d("当前选择：$position")
            currentDesignPicType2 = mtypePicList!!.get(position)
        }.build()


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
            (context as CheckComponentDetectionActivity).scaleDamageInfo(3)
        }

        mBinding.checkCdpPlateRightRealUi.checkCdpPlatePic.setOnClickListener {
            openImgSelector(REQUEST_WALL_REAL_TAKE_PHOTO)
        }

        mBinding.checkCdpPlateRightDesignUi.checkCdpPlatePic.setOnClickListener {
            openImgSelector(REQUEST_WALL_DESIGN_TAKE_PHOTO)
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

        //完成
        mBinding.checkCdpSubtitleConfirm.setOnClickListener {


            if (mBinding.checkCdpSubtitle1.checkEdit.text.toString().isNullOrEmpty()) {
                showToast("请输入墙名称")
                return@setOnClickListener
            }


            if (mBinding.checkCdpSubtitle2.content.visibility == View.VISIBLE) {
                if (mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis1.text.isNullOrEmpty() ||
                    mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis2.text.isNullOrEmpty() ||
                    mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis3.text.isNullOrEmpty()
                ) {
                    showToast("请完善轴线")
                    return@setOnClickListener
                }
                mAxisNoteList!!.add(mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis1.text.toString())
                mAxisNoteList.add(mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis2.text.toString())
                mAxisNoteList.add(mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis3.text.toString())

            } else {
                if (mBinding.checkCdpSubtitle2Second.checkEdit.text.isNullOrEmpty()) {
                    showToast("请输入轴线")
                    return@setOnClickListener
                }
                mAxisNote = mBinding.checkCdpSubtitle2Second.checkEdit.text.toString()
            }


            if (mBinding.checkCdpPlateLeftRealUi.checkCdpPlateLeftRealContent.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 实测 墙厚度")
                return@setOnClickListener
            }

            if (mBinding.checkCdpPlateLeftDesignUi.checkCdpPlateLeftDesignContent.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 设计 墙厚度")
                return@setOnClickListener
            }

            if (mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit2.text.toString()
                    .isNullOrEmpty() ||
                mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit3.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 实测 竖向钢筋")
                return@setOnClickListener
            }

            if (mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkEdit2.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 设计 竖向钢筋")
                return@setOnClickListener
            }


            if (mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit2.text.toString()
                    .isNullOrEmpty() ||
                mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit3.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 实测 水平钢筋")
                return@setOnClickListener
            }


            if (mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkEdit2.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 设计 水平钢筋")
                return@setOnClickListener
            }


            if (mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealProtect.checkEditName.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 实测 保护层厚度")
                return@setOnClickListener
            }

            if (mBinding.checkCdpPlateRightRealUi.checkCdpLeftRealRemarkContent.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 实测 备注")
                return@setOnClickListener
            }

            if (mBinding.checkCdpPlateRightDesignUi.checkCdpLeftRealRemarkContent.text.toString()
                    .isNullOrEmpty()
            ) {
                showToast("请输入 设计 备注")
                return@setOnClickListener
            }


            mAddAnnotReF = (activity as CheckComponentDetectionActivity).mCurrentAddAnnotReF
            //TODO 图片加载

            var damage = DamageV3Bean(
                -1,
                (activity as CheckComponentDetectionActivity).mCurrentDrawing!!.drawingID,
                "墙",
                0,
                mAddAnnotReF,
                mBinding.checkCdpPlateRightRealUi.checkCdpLeftRealRemarkContent.text.toString(),
                if (mDamageCreateTime < 0) System.currentTimeMillis() else mDamageCreateTime,
                mBinding.checkCdpPlateLeftRealUi.checkCdpPlateLeftRealContent.text.toString(),
                mBinding.checkCdpPlateLeftDesignUi.checkCdpPlateLeftDesignContent.text.toString(),
                mBinding.checkCdpSubtitle1.checkEdit.text.toString(),
                mAxisNote,
                mAxisNoteList!!,
                arrayListOf(
                    currentRealPicType,
                    mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit2.text.toString(),
                    mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit3.text.toString()
                ),
                arrayListOf(
                    currentRealPicType2,
                    mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit2.text.toString(),
                    mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit3.text.toString()
                ),
                mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealProtect.checkEditProtect.text.toString(),
                mBinding.checkCdpPlateRightRealUi.checkCdpLeftRealRemarkContent.text.toString(),
                "图片",
                arrayListOf(
                    currentDesignPicType,
                    mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkEdit2.text.toString()
                ),
                arrayListOf(
                    currentDesignPicType2,
                    mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkEdit2.text.toString()
                ),
                mBinding.checkCdpPlateRightDesignUi.checkCdpLeftRealRemarkContent.text.toString(),
                "图片"
            )

            (context as CheckComponentDetectionActivity).saveDamage(damage)


        }


    }

    /**
     * 根据是否为null来设置数据
     */
    fun resetView(damageV3Bean: DamageV3Bean?) {

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
            mBinding.checkCdpSubtitle1.checkEdit.setText("")
            mBinding.checkCdpSubtitle2.content.visibility = View.VISIBLE
            mBinding.checkCdpSubtitle2Second.content.visibility = View.INVISIBLE

            mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis1.setText("")
            mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis2.setText("")
            mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis3.setText("")
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

            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkCpdLeftRealSpinner2.setSelect(
                0
            )
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkCpdLeftRealSpinner2.setText(
                mtypePicList!!.get(0)
            )
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit2.setText("")
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit3.setText("")

            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealProtect.checkEditProtect.setText(
                ""
            )
            mRightRealPicSrc = ""
            mRightDesignPicSrc = ""
            mBinding.checkCdpPlateRightRealUi.checkCdpPlatePic.setImageResource(R.color.gray)
            mBinding.checkCdpPlateRightDesignUi.checkCdpPlatePic.setImageResource(R.color.gray)
            mBinding.checkCdpPlateRightRealUi.checkCdpLeftRealRemarkContent.setText("")
            mBinding.checkCdpPlateRightRealUi.checkCdpPlatePic.setBackgroundColor(Color.GRAY)

            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkCpdLeftRealSpinner2.setSelect(
                0
            )
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkCpdLeftRealSpinner2.setText(
                mtypePicList!!.get(0)
            )
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkEdit2.setText("")

            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkCpdLeftRealSpinner2.setSelect(
                0
            )
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkCpdLeftRealSpinner2.setText(
                mtypePicList!!.get(0)
            )
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkEdit2.setText("")

            mBinding.checkCdpPlateRightDesignUi.checkCdpLeftRealRemarkContent.setText("")
            mBinding.checkCdpPlateRightDesignUi.checkCdpPlatePic.setBackgroundColor(Color.GRAY)

            mDamageCreateTime = -1L
        } else {

            mBinding.checkCdpPlateLeftRealUi.parent.visibility = View.VISIBLE


            currentRealPicType = damageV3Bean.realEastWestRebarList!!.get(0)
            currentRealPicType2 = damageV3Bean.realNorthSouthRebarList!!.get(0)
            currentDesignPicType = damageV3Bean.designEastWestRebarList!!.get(0)
            currentDesignPicType2 = damageV3Bean.designNorthSouthRebarList!!.get(0)

            mBinding.checkCdpSubtitle1.checkEdit.setText(damageV3Bean.plateName)
            if (!damageV3Bean.axisPlateNoteList!!.isNullOrEmpty() && !damageV3Bean.axisPlateNoteList!!.get(0).isNullOrEmpty()){
                mBinding.checkCdpSubtitle2.content.visibility = View.VISIBLE
                mBinding.checkCdpSubtitle2Second.content.visibility = View.INVISIBLE
                mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis1.setText(damageV3Bean.axisPlateNoteList!!.get(0))
                mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis2.setText(damageV3Bean.axisPlateNoteList!!.get(1))
                mBinding.checkCdpSubtitle2.checkCdpPlateMenuAxis3.setText(damageV3Bean.axisPlateNoteList!!.get(2))
            }else{
                mBinding.checkCdpSubtitle2.content.visibility = View.INVISIBLE
                mBinding.checkCdpSubtitle2Second.content.visibility = View.VISIBLE
                mBinding.checkCdpSubtitle2Second.checkEdit.setText(damageV3Bean.axisSingleNote)
            }

            mBinding.checkCdpPlateLeftRealUi.checkCdpPlateLeftRealContent.setText(damageV3Bean.realPlateThickness)

            mBinding.checkCdpPlateLeftDesignUi.checkCdpPlateLeftDesignContent.setText(damageV3Bean.designPlateThickness)


            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkCpdLeftRealSpinner2.setText(damageV3Bean.realEastWestRebarList!!.get(0))
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkCpdLeftRealSpinner2.setSelect(mtypePicList!!.indexOf(damageV3Bean.realEastWestRebarList!!.get(0)))
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit2.setText(damageV3Bean.realEastWestRebarList!!.get(1))
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealSingle.checkEdit3.setText(damageV3Bean.realEastWestRebarList!!.get(2))


            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkCpdLeftRealSpinner2.setText(damageV3Bean.designEastWestRebarList!!.get(0))
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkCpdLeftRealSpinner2.setSelect(mtypePicList!!.indexOf(damageV3Bean.designEastWestRebarList!!.get(0)))
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinSingle.checkEdit2.setText(damageV3Bean.designEastWestRebarList!!.get(1))

            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkCpdLeftRealSpinner2.setText(damageV3Bean.realNorthSouthRebarList!!.get(0))
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkCpdLeftRealSpinner2.setSelect(mtypePicList!!.indexOf(damageV3Bean.realNorthSouthRebarList!!.get(0)))
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit2.setText(damageV3Bean.realNorthSouthRebarList!!.get(1))
            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealMeasured.checkEdit3.setText(damageV3Bean.realNorthSouthRebarList!!.get(2))

            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkCpdLeftRealSpinner2.setText(damageV3Bean.designNorthSouthRebarList!!.get(0))
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkCpdLeftRealSpinner2.setSelect(mtypePicList!!.indexOf(damageV3Bean.designNorthSouthRebarList!!.get(0)))
            mBinding.checkCdpPlateRightDesignUi.checkCdpBeamRightDesinMeasured.checkEdit2.setText(damageV3Bean.designNorthSouthRebarList!!.get(1))

            mBinding.checkCdpPlateRightRealUi.checkCdpPlateRightRealProtect.checkEditProtect.setText(damageV3Bean.realProtectThickness)

            mBinding.checkCdpPlateRightRealUi.checkCdpLeftRealRemarkContent.setText(damageV3Bean.realNote)
            mBinding.checkCdpPlateRightDesignUi.checkCdpLeftRealRemarkContent.setText(damageV3Bean.designNote)

                 mBinding.checkCdpPlateRightRealUi.checkCdpPlatePic.setImageURI(Uri.parse(damageV3Bean.realPicture))
                 mBinding.checkCdpPlateRightDesignUi.checkCdpPlatePic.setImageURI(Uri.parse(damageV3Bean.designPicture))

            mDamageCreateTime = damageV3Bean.createTime
        }
    }

    fun openImgSelector(requestCode:Int) {
        ImageSelector
            .builder()
            .onlyTakePhoto(false)
            .canPreview(true)
            .setSingle(true)
            .start(activity as CheckComponentDetectionActivity, requestCode)
    }

    fun setImgaeBitmap(uri: Uri, type:Int){
        when(type){
            REQUEST_WALL_REAL_TAKE_PHOTO->
            {
                mBinding.checkCdpPlateRightRealUi.checkCdpPlatePic.setImageURI(uri)
                mRightRealPicSrc = uri.toString()
            }
            REQUEST_WALL_DESIGN_TAKE_PHOTO->
            {
                mBinding.checkCdpPlateRightDesignUi.checkCdpPlatePic.setImageURI(uri)
                mRightDesignPicSrc = uri.toString()
                CommonUtil.imageToPDF(mRightDesignPicSrc,"/storage/emulated/0/Android/data/com.sribs.bdd/files/图纸/1.pdf")
            }
        }
    }

    override fun onClick(data: DrawingV3Bean?) {
        (activity as CheckComponentDetectionActivity).choosePDF(data!!)
    }
}