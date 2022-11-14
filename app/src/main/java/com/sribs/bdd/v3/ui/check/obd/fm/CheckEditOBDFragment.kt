package com.sribs.bdd.v3.ui.check.obd.fm

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckObliquedeformationEditBinding
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.bdd.v3.ui.check.obd.CheckObliqueDeformationActivity
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.bdd.v3.util.SpannableUtils
import com.sribs.bdd.v3.view.DrawAndTextView
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.utils.FileUtil
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat


@Route(path = ARouterPath.CHECK_OBLIQUE_DEFORMATION_Edit_FRAGMENT)
class CheckEditOBDFragment : BaseFragment(R.layout.fragment_check_obliquedeformation_edit) {

    private val mBinding: FragmentCheckObliquedeformationEditBinding by bindView()

    /**
     * 创建损伤时间
     */
    private var mDamageCreateTime = -1L

    /**
     * 当前添加的mark
     */
    var mAddAnnotReF: Long = -1L

    var pointScalePath: String? = ""

    var isViewCreated = false

    override fun deinitView() {

    }

    override fun initView() {

    }

    override fun onDestroyView() {
        isViewCreated = false
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
        mBinding.checkEditPoint.checkEditName.text = SpannableUtils.setTextColor("*点位名称",
            0,1, Color.RED)
        mBinding.checkEditH1.checkEditName.text = SpannableUtils.setTextColor("*测量高度1(m)",
            0,1, Color.RED)
        mBinding.checkEditH1.checkEdit.hint = "请输入测量高度"
        mBinding.checkEditH2.checkEditName.text = SpannableUtils.setTextColor("*测量高度2(m)",
            0,1, Color.RED)
        mBinding.checkEditH2.checkEdit.hint = "请输入测量高度"
        mBinding.checkEditQx1.checkEditName.text = SpannableUtils.setTextColor("*倾斜量1(mm)",
            0,1, Color.RED)
        mBinding.checkEditQx1.checkEdit.hint = "请输入倾斜量"
        mBinding.checkEditQx2.checkEditName.text = SpannableUtils.setTextColor("*倾斜量2(mm)",
            0,1, Color.RED)
        mBinding.checkEditQx2.checkEdit.hint = "请输入倾斜量"

        mBinding.checkEditPoint.checkEdit.inputType = InputType.TYPE_CLASS_TEXT

        mBinding.checkEditH1.checkEdit.addTextChangedListener {
            LogUtils.d(" mBinding.checkEditH1.checkEdit：" + it)
            if (it.isNullOrEmpty()) {
                if (mBinding.checkEditQx1.checkEdit.text.isNullOrEmpty()) {
                    mBinding.checkObdQx1Hint.text = ("倾斜量/测量高度1=斜率‰")
                } else {
                    mBinding.checkObdQx1Hint.text =
                        ("" + mBinding.checkEditQx1.checkEdit.text.toString() + "/测量高度1=斜率‰")
                }
            } else if (mBinding.checkEditQx1.checkEdit.text.isNullOrEmpty()) {
                mBinding.checkObdQx1Hint.text = ("倾斜量/" + it + "=斜率‰")
            } else {
                mBinding.checkObdQx1Hint.text =
                    ("" + (mBinding.checkEditQx1.checkEdit.text.toString().toDouble()) + "/" + it
                            + "=" + getNoMoreThanTwoDigits(
                        (mBinding.checkEditQx1.checkEdit.text.toString()
                            .toDouble()) / it.toString().toDouble()
                    ) + "‰")
            }
        }

        mBinding.checkObdUiCaptureView.content1.setContent("方向1")
        mBinding.checkObdUiCaptureView.content1.resetView(0f)

        mBinding.checkObdUiCaptureView.content2.setContent("方向2")
        mBinding.checkObdUiCaptureView.content2.resetView(90f)


        mBinding.checkEditH2.checkEdit.addTextChangedListener {
            LogUtils.d(" mBinding.checkEditH2.checkEdit：" + it)
            if (it.isNullOrEmpty()) {
                if (mBinding.checkEditQx2.checkEdit.text.isNullOrEmpty()) {
                    mBinding.checkObdQx2Hint.text = ("倾斜量/测量高度2=斜率‰")
                } else {
                    mBinding.checkObdQx2Hint.text =
                        ("" + mBinding.checkEditQx2.checkEdit.text.toString() + "/测量高度2=斜率%")
                }
            } else if (mBinding.checkEditQx2.checkEdit.text.isNullOrEmpty()) {
                mBinding.checkObdQx2Hint.text = ("倾斜量/" + it + "=斜率‰")
            } else {
                mBinding.checkObdQx2Hint.text =
                    ("" + mBinding.checkEditQx2.checkEdit.text.toString() + "/" + it
                            + "=" + getNoMoreThanTwoDigits(
                        (mBinding.checkEditQx2.checkEdit.text.toString()
                            .toDouble()) / it.toString().toDouble()
                    ) + "‰")
            }
        }

        mBinding.checkEditQx1.checkEdit.addTextChangedListener {
            LogUtils.d(" mBinding.checkEditQx1.checkEdit：" + it)
            if (it.isNullOrEmpty()) {
                if (mBinding.checkEditH1.checkEdit.text.isNullOrEmpty()) {
                    mBinding.checkObdQx1Hint.text = ("倾斜量/测量高度1=斜率‰")
                } else {
                    mBinding.checkObdQx1Hint.text =
                        ("倾斜量/" + mBinding.checkEditH1.checkEdit.text.toString() + "=斜率‰")
                }
            } else if (mBinding.checkEditH1.checkEdit.text.isNullOrEmpty()) {
                mBinding.checkObdQx1Hint.text = ("" + it.toString() + "/测量高度1" + "=斜率‰")
            } else {
                mBinding.checkObdQx1Hint.text =
                    ("" + it.toString() + "/" + mBinding.checkEditH1.checkEdit.text.toString()
                            + "=" + getNoMoreThanTwoDigits(
                        (it.toString()
                            .toDouble()) / mBinding.checkEditH1.checkEdit.text.toString()
                            .toDouble()
                    ) + "‰")
            }
        }

        mBinding.checkEditQx2.checkEdit.addTextChangedListener {
            LogUtils.d(" mBinding.checkEditQx2.checkEdit：" + it)
            if (it.isNullOrEmpty()) {
                if (mBinding.checkEditH2.checkEdit.text.isNullOrEmpty()) {
                    mBinding.checkObdQx2Hint.text = ("倾斜量/测量高度2=斜率‰")
                } else {
                    mBinding.checkObdQx2Hint.text =
                        ("倾斜量/" + mBinding.checkEditH2.checkEdit.text.toString() + "=斜率‰")
                }
            } else if (mBinding.checkEditH2.checkEdit.text.isNullOrEmpty()) {
                mBinding.checkObdQx2Hint.text = ("" + it.toString() + "/测量高度2" + "=斜率‰")
            } else {
                mBinding.checkObdQx2Hint.text =
                    ("" + it.toString() + "/" + mBinding.checkEditH2.checkEdit.text.toString()
                            + "=" + getNoMoreThanTwoDigits(
                        (it.toString()
                            .toDouble()) / mBinding.checkEditH2.checkEdit.text.toString()
                            .toDouble()
                    ) + "‰")
            }
        }

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
            mBinding.checkObdUiCaptureView.content2.visibility = View.GONE
            mBinding.checkObdDoubleRadio.isSelected = false
            mBinding.checkEditH2.checkEdit.setText("")
            mBinding.checkEditH2.checkEdit.isEnabled = false
            mBinding.checkEditQx2.checkEdit.setText("")
            mBinding.checkEditQx2.checkEdit.isEnabled = false
        }
        mBinding.checkObdOnlyText.setOnClickListener {
            mBinding.checkObdOnlyRadio.isSelected = true
            mBinding.checkObdDoubleRadio.isSelected = false
            mBinding.checkObdUiCaptureView.content2.visibility = View.GONE
            mBinding.checkEditH2.checkEdit.setText("")
            mBinding.checkEditH2.checkEdit.isEnabled = false
            mBinding.checkEditQx2.checkEdit.setText("")
            mBinding.checkEditQx2.checkEdit.isEnabled = false
        }
        mBinding.checkObdDoubleRadio.setOnClickListener {
            mBinding.checkObdOnlyRadio.isSelected = false
            mBinding.checkObdDoubleRadio.isSelected = true
            mBinding.checkObdUiCaptureView.content2.resetView(90f)
            mBinding.checkObdUiCaptureView.content2.visibility = View.VISIBLE
            mBinding.checkEditH2.checkEdit.setText("")
            mBinding.checkEditH2.checkEdit.isEnabled = true
            mBinding.checkEditQx2.checkEdit.isEnabled = true
            mBinding.checkEditQx2.checkEdit.setText("")
        }
        mBinding.checkObdDoubleText.setOnClickListener {
            mBinding.checkObdOnlyRadio.isSelected = false
            mBinding.checkObdUiCaptureView.content2.resetView(90f)
            mBinding.checkObdUiCaptureView.content2.visibility = View.VISIBLE
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

            LogUtils.d("获取旋转角度" + mBinding.checkObdUiCaptureView.content.rotation)

            LogUtils.d("获取旋转角度22" + mBinding.checkObdUiCaptureView.content2.rotation)

            if (mBinding.checkObdOnlyRadio.isSelected) {
                if (mBinding.checkEditH1.checkEdit.text.isNullOrEmpty()) {
                    showToast("请输入测量高度")
                    return@setOnClickListener
                }
                if (mBinding.checkEditQx1.checkEdit.text.isNullOrEmpty()) {
                    showToast("请输入倾斜量")
                    return@setOnClickListener
                }
            } else if (mBinding.checkObdDoubleRadio.isSelected) {
                if (mBinding.checkEditH1.checkEdit.text.isNullOrEmpty()) {
                    showToast("请输入测量高度")
                    return@setOnClickListener
                }
                if (mBinding.checkEditQx1.checkEdit.text.isNullOrEmpty()) {
                    showToast("请输入倾斜量")
                    return@setOnClickListener
                }
                if (mBinding.checkEditH2.checkEdit.text.isNullOrEmpty()) {
                    showToast("请输入测量高度")
                    return@setOnClickListener
                }
                if (mBinding.checkEditQx2.checkEdit.text.isNullOrEmpty()) {
                    showToast("请输入倾斜量")
                    return@setOnClickListener
                }
            }
            if (mBinding.checkEditPoint.checkEdit.text.isNullOrEmpty()) {
                showToast("请输入点位名称")
                return@setOnClickListener
            }
            /*  if (mBinding.checkObdHintText.text.isNullOrEmpty()) {
                  showToast("请添加房屋倾斜点备注")
                  return@setOnClickListener
              }*/
            mAddAnnotReF = (activity as CheckObliqueDeformationActivity).mCurrentAddAnnotReF

            if ((activity as CheckObliqueDeformationActivity).addPDFDamageMark &&
                !(activity as CheckObliqueDeformationActivity).isEditDamage &&
                (activity as CheckObliqueDeformationActivity).scaleBitmap != null
            ) {
                var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(context!!)

                var mCurDrawingsDir =
                    "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + (activity as CheckObliqueDeformationActivity).mProjectName +
                            "/" + (activity as CheckObliqueDeformationActivity).mBldName + "倾斜测量/damage/点位"

                var name = mBinding.checkEditPoint.checkEdit.text.toString()
                    .replace(" ", "") + System.currentTimeMillis() + ".jpg";
                pointScalePath = File(cacheRootDir + mCurDrawingsDir, name).absolutePath
                FileUtil.saveBitmap(
                    (activity as CheckObliqueDeformationActivity).scaleBitmap!!,
                    File(cacheRootDir + mCurDrawingsDir),
                    name
                )
                (activity as CheckObliqueDeformationActivity).scaleBitmap?.recycle()
            }

            var damage = DamageV3Bean(
                -1,
                (activity as CheckObliqueDeformationActivity).mCurrentDrawing!!.drawingID!!,
                "点位",
                0,
                mAddAnnotReF,
                mBinding.checkObdHintText.text.toString(),
                if (mDamageCreateTime < 0) System.currentTimeMillis() else mDamageCreateTime,
                ((activity as CheckObliqueDeformationActivity)).mGuideText,
                ((activity as CheckObliqueDeformationActivity)).mGuideRotate,
                if (pointScalePath.isNullOrEmpty()) ArrayList() else arrayListOf(
                    FileUtil.getFileName(
                        pointScalePath!!
                    ) ?: "", pointScalePath!!
                ),
                if (mBinding.checkObdOnlyRadio.isSelected) 1 else 2,
                mBinding.checkEditPoint.checkEdit.text.toString(),
                mBinding.checkEditH1.checkEdit.text.toString(),
                mBinding.checkEditH2.checkEdit.text.toString(),
                mBinding.checkEditQx1.checkEdit.text.toString(),
                mBinding.checkEditQx2.checkEdit.text.toString(),
                mBinding.checkObdUiCaptureView.content1.oriRotation % 360,
                mBinding.checkObdUiCaptureView.content2.oriRotation % 360,
                getDirection((mBinding.checkObdUiCaptureView.content1.oriRotation % 360).toInt()),
                getDirection((mBinding.checkObdUiCaptureView.content2.oriRotation % 360).toInt()),
                mBinding.checkObdQx1Hint.text.toString().split("=")[1],
                if (mBinding.checkObdDoubleRadio.isSelected) mBinding.checkObdQx2Hint.text.toString()
                    .split("=")[1] else "",
            )

            var zoom = (activity as CheckObliqueDeformationActivity).mView?.PDFGetZoom()!!

            var viewHeight = (resources.getDimensionPixelSize(R.dimen._60sdp)*zoom).toInt()

            var cardViewHeight = (resources.getDimensionPixelSize(R.dimen._15sdp)*zoom).toInt()

            var layout: View
            if (mBinding.checkObdOnlyRadio.isSelected) {

                layout =
                    LayoutInflater.from(activity).inflate(R.layout.damage_mark_index_layout_2, null)

                var cardView = layout.findViewById<CardView>(R.id.damage_type_cardView)
                var cardViewLayoutParams = RelativeLayout.LayoutParams(cardViewHeight,cardViewHeight)
                cardViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                cardView.layoutParams = cardViewLayoutParams

                val drawAndTextView = layout.findViewById<(DrawAndTextView)>(R.id.content_1)

                val content = layout.findViewById<TextView>(R.id.point_text)
                content.setTextSize(TypedValue.COMPLEX_UNIT_PX,resources.getDimensionPixelSize(R.dimen._3ssp)*zoom)
                content.text = mBinding.checkEditPoint.checkEdit.text

                drawAndTextView.setTextViewHeight(viewHeight)
                drawAndTextView.setZoom(zoom)
                drawAndTextView.resetView(mBinding.checkObdUiCaptureView.content1.oriRotation)
                drawAndTextView.setMarkView(mBinding.checkEditQx1.checkEdit.text.toString())
                drawAndTextView.addTopView(mBinding.checkObdQx1Hint.text.toString())

            } else {
                layout =
                    LayoutInflater.from(activity).inflate(R.layout.damage_mark_index_layout_3, null)

                var cardView = layout.findViewById<CardView>(R.id.damage_type_cardView)
                var cardViewLayoutParams = RelativeLayout.LayoutParams(cardViewHeight,cardViewHeight)
                cardViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                cardView.layoutParams = cardViewLayoutParams

                val drawAndTextView = layout.findViewById<(DrawAndTextView)>(R.id.content_1)
                val drawAndTextView2 = layout.findViewById<(DrawAndTextView)>(R.id.content_2)
                val content = layout.findViewById<TextView>(R.id.point_text)
                content.setTextSize(TypedValue.COMPLEX_UNIT_PX,resources.getDimensionPixelSize(R.dimen._3ssp)*zoom)
                content.text = mBinding.checkEditPoint.checkEdit.text

                drawAndTextView.setTextViewHeight(viewHeight)
                drawAndTextView.setZoom(zoom)
                drawAndTextView.resetView(mBinding.checkObdUiCaptureView.content1.oriRotation)
                drawAndTextView.setMarkView(mBinding.checkEditQx1.checkEdit.text.toString())
                drawAndTextView.addTopView(mBinding.checkObdQx1Hint.text.toString())


                drawAndTextView2.setTextViewHeight(viewHeight)
                drawAndTextView2.setZoom(zoom)
                drawAndTextView2.resetView(mBinding.checkObdUiCaptureView.content2.oriRotation)
                drawAndTextView2.setMarkView(mBinding.checkEditQx2.checkEdit.text.toString())
                drawAndTextView2.addTopView(mBinding.checkObdQx2Hint.text.toString())

            }

            (context as CheckObliqueDeformationActivity).saveDamage(damage, layout)

        }

    }

    /**
     * 转换方向
     */
    fun getDirection(direction: Int): String {

        if (direction > 0) {
            if (direction < 90) {
                return "东北"+direction+"°"
            } else if (direction == 90) {
                return "东"
            } else if (direction > 90 && direction < 180) {
                return "东南"+direction+"°"
            }else if(direction == 180){
                return "南"
            }else if(direction > 180 && direction < 270){
                return "西南"+(360-direction)+"°"
            }else if(direction == 270){
                return "西"
            }else if(direction > 270 && direction < 360){
                return "西北"+(360-direction)+"°"
            }
        } else if (direction < 0) {
            var direction2 = Math.abs(direction)
            if (direction2 < 90) {
                return "西北"+direction2+"°"
            } else if (direction2 == 90) {
                return "西"
            } else if (direction2 > 90 && direction2 < 180) {
                return "西南"+direction2+"°"
            }else if(direction2 == 180){
                return "南"
            }else if(direction2 > 180 && direction2 < 270){
                return "东南"+(360-direction2)+"°"
            }else if(direction2 == 270){
                return "东"
            }else if(direction2 > 270 && direction2 < 360){
                return "东北"+(360-direction2)+"°"
            }
        }
        return "北"
    }

    /**
     * 保留两位小数
     */
    fun getNoMoreThanTwoDigits(number: Double): String {
        val format = DecimalFormat("0.#")
        format.roundingMode = RoundingMode.FLOOR
        return format.format(number)
    }

    /**
     * 根据是否为null来设置数据
     */
    fun resetView(damageV3Bean: DamageV3Bean?) {
        LogUtils.d("isViewCreated " + isViewCreated)
        LogUtils.d("resetView " + damageV3Bean)

        mBinding.checkObdIndex.text =
            "当前图纸: " + (context as CheckObliqueDeformationActivity).mCurrentPDFName
        if (damageV3Bean == null) {
            mBinding.checkObdOnlyRadio.isSelected = false
            mBinding.checkObdDoubleRadio.isSelected = true
            mBinding.checkEditH1.checkEdit.setText("")
            mBinding.checkEditQx1.checkEdit.setText("")
            mBinding.checkEditH2.checkEdit.setText("")
            mBinding.checkEditQx2.checkEdit.setText("")
            mBinding.checkEditH2.checkEdit.isEnabled = true
            mBinding.checkEditQx2.checkEdit.isEnabled = true

            mBinding.checkEditPoint.checkEdit.setText("")

            mBinding.checkObdUiCaptureView.content2.visibility = View.VISIBLE

            mBinding.checkObdHintText.setText("")
            mDamageCreateTime = -1L

            if ((context as CheckObliqueDeformationActivity).scaleBitmap != null) {
                mBinding.checkObdPointMark.visibility = View.GONE
                mBinding.checkObdUi.setImageBitmap((context as CheckObliqueDeformationActivity).scaleBitmap)
            } else {
                mBinding.checkObdPointMark.visibility = View.VISIBLE
                mBinding.checkObdUi.setImageDrawable(null)
            }

            mBinding.checkObdUiCaptureView.content1.resetView(0f)

            mBinding.checkObdUiCaptureView.content2.resetView(90f)

        } else {

            if (!damageV3Bean.measure2Height.isNullOrEmpty() && !damageV3Bean.tilt2.isNullOrEmpty()) {
                mBinding.checkObdOnlyRadio.isSelected = false
                mBinding.checkObdDoubleRadio.isSelected = true
                mBinding.checkEditH2.checkEdit.isEnabled = true
                mBinding.checkEditQx2.checkEdit.isEnabled = true
                mBinding.checkEditH1.checkEdit.setText(damageV3Bean.measure1Height)
                mBinding.checkEditQx1.checkEdit.setText(damageV3Bean.tilt1)
                mBinding.checkEditH2.checkEdit.setText(damageV3Bean.measure2Height)
                mBinding.checkEditQx2.checkEdit.setText(damageV3Bean.tilt2)

                mBinding.checkObdUiCaptureView.content1.resetView(damageV3Bean.tiltRotate1!!)

                mBinding.checkObdUiCaptureView.content2.visibility = View.VISIBLE
                mBinding.checkObdUiCaptureView.content2.resetView(damageV3Bean.tiltRotate2!!)

            } else {
                mBinding.checkObdOnlyRadio.isSelected = true
                mBinding.checkObdDoubleRadio.isSelected = false
                mBinding.checkEditH2.checkEdit.isEnabled = false
                mBinding.checkEditQx2.checkEdit.isEnabled = false
                mBinding.checkEditH1.checkEdit.setText(damageV3Bean.measure1Height)
                mBinding.checkEditQx1.checkEdit.setText(damageV3Bean.tilt1)
                mBinding.checkEditH2.checkEdit.setText("")
                mBinding.checkEditQx2.checkEdit.setText("")

                mBinding.checkObdUiCaptureView.content1.resetView(damageV3Bean.tiltRotate1!!)
                mBinding.checkObdUiCaptureView.content2.visibility = View.GONE

            }
            mBinding.checkEditPoint.checkEdit.setText(damageV3Bean.pointName)
            mBinding.checkObdHintText.setText(damageV3Bean.note)
            mDamageCreateTime = damageV3Bean.createTime

            if (!damageV3Bean.scalePath.isNullOrEmpty()) {
                mBinding.checkObdPointMark.visibility = View.GONE
                pointScalePath = damageV3Bean.scalePath?.get(1)
                mBinding.checkObdUi.setImageURI(Uri.fromFile(File(pointScalePath)))
            } else {
                mBinding.checkObdPointMark.visibility = View.VISIBLE
                mBinding.checkObdUi.setImageDrawable(null)
            }
        }
    }

}