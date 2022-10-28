package com.sribs.bdd.v3.ui.check.obd.fm

import android.graphics.Canvas
import android.graphics.Rect
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.radaee.pdf.Document
import com.radaee.pdf.Page
import com.radaee.reader.PDFLayoutView
import com.radaee.util.PDFThumbGrid.mDoc
import com.radaee.view.ILayoutView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckObliquedeformationEditBinding
import com.sribs.bdd.v3.ui.check.obd.CheckObliqueDeformationActivity
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.bdd.v3.view.DrawAndTextView
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.DamageV3Bean
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.math.RoundingMode
import java.text.DecimalFormat


@Route(path = ARouterPath.CHECK_OBLIQUE_DEFORMATION_Edit_FRAGMENT)
class CheckEditOBDFragment : BaseFragment(R.layout.fragment_check_obliquedeformation_edit),
    ILayoutView.PDFLayoutListener {

    private val mBinding: FragmentCheckObliquedeformationEditBinding by bindView()

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
        mView?.setIntercept(false)
        mBinding.checkEditH1.checkEditName.text = "测量高度1(m)"
        mBinding.checkEditH1.checkEdit.hint = "请输入测量高度"
        mBinding.checkEditH2.checkEditName.text = "测量高度2(m)"
        mBinding.checkEditH2.checkEdit.hint = "请输入测量高度"
        mBinding.checkEditQx1.checkEditName.text = "倾斜量1(mm)"
        mBinding.checkEditQx1.checkEdit.hint = "请输入倾斜量"
        mBinding.checkEditQx2.checkEditName.text = "倾斜量2(mm)"
        mBinding.checkEditQx2.checkEdit.hint = "请输入倾斜量"

        mBinding.checkEditPoint.checkEdit.inputType = InputType.TYPE_CLASS_TEXT

        mBinding.checkEditH1.checkEdit.addTextChangedListener {
            LogUtils.d(" mBinding.checkEditH1.checkEdit：" + it)
            if (it.isNullOrEmpty()) {
                if(mBinding.checkEditQx1.checkEdit.text.isNullOrEmpty()) {
                    mBinding.checkObdQx1Hint.text = ("倾斜量/测量高度1=斜率‰")
                }else{
                    mBinding.checkObdQx1Hint.text = (""+mBinding.checkEditQx1.checkEdit.text.toString()+"/测量高度1=斜率‰")
                }
            } else if (mBinding.checkEditQx1.checkEdit.text.isNullOrEmpty()) {
                mBinding.checkObdQx1Hint.text = ("倾斜量/" + it + "=斜率‰")
            } else {
                mBinding.checkObdQx1Hint.text =
                    (""+(mBinding.checkEditQx1.checkEdit.text.toString().toDouble()) + "/" + it
                            + "=" + getNoMoreThanTwoDigits((mBinding.checkEditQx1.checkEdit.text.toString()
                        .toDouble()) / it.toString().toDouble()) + "‰")
            }
        }

        mBinding.checkObdUiCaptureView.content1.setContent("方向1")
        mBinding.checkObdUiCaptureView.content1.setTextViewHeight(500)
        mBinding.checkObdUiCaptureView.content1.init()
        mBinding.checkObdUiCaptureView.content1.requestFocus()
        mBinding.checkObdUiCaptureView.content2.setContent("方向2")
        mBinding.checkObdUiCaptureView.content2.setTextViewHeight(500)
        mBinding.checkObdUiCaptureView.content2.setDrawViewType(0)
        mBinding.checkObdUiCaptureView.content2.setOriRotation(90f)
        mBinding.checkObdUiCaptureView.content2.init()


        mBinding.checkEditH2.checkEdit.addTextChangedListener {
            LogUtils.d(" mBinding.checkEditH2.checkEdit：" + it)
            if (it.isNullOrEmpty()) {
                if(mBinding.checkEditQx2.checkEdit.text.isNullOrEmpty()) {
                    mBinding.checkObdQx2Hint.text = ("倾斜量/测量高度2=斜率‰")
                }else{
                    mBinding.checkObdQx2Hint.text = (""+mBinding.checkEditQx2.checkEdit.text.toString()+"/测量高度2=斜率%")
                }
            } else if (mBinding.checkEditQx2.checkEdit.text.isNullOrEmpty()) {
                mBinding.checkObdQx2Hint.text = ("倾斜量/" + it + "=斜率‰")
            } else {
                mBinding.checkObdQx2Hint.text =
                    (""+mBinding.checkEditQx2.checkEdit.text.toString() + "/" + it
                            + "=" + getNoMoreThanTwoDigits((mBinding.checkEditQx2.checkEdit.text.toString()
                        .toDouble()) / it.toString().toDouble()) + "‰")
            }
        }

        mBinding.checkEditQx1.checkEdit.addTextChangedListener {
            LogUtils.d(" mBinding.checkEditQx1.checkEdit：" + it)
            if (it.isNullOrEmpty()) {
                if(mBinding.checkEditH1.checkEdit.text.isNullOrEmpty()){
                    mBinding.checkObdQx1Hint.text = ("倾斜量/测量高度1=斜率‰")
                }else{
                    mBinding.checkObdQx1Hint.text = ("倾斜量/"+mBinding.checkEditH1.checkEdit.text.toString()+"=斜率‰")
                }
            } else if (mBinding.checkEditH1.checkEdit.text.isNullOrEmpty()) {
                mBinding.checkObdQx1Hint.text =(""+it.toString() + "/测量高度1" + "=斜率‰")
            } else {
                mBinding.checkObdQx1Hint.text =
                    (""+it.toString() + "/" + mBinding.checkEditH1.checkEdit.text.toString()
                            + "=" + getNoMoreThanTwoDigits((it.toString()
                        .toDouble()) / mBinding.checkEditH1.checkEdit.text.toString()
                        .toDouble()) + "‰")
            }
        }

        mBinding.checkEditQx2.checkEdit.addTextChangedListener {
            LogUtils.d(" mBinding.checkEditQx2.checkEdit：" + it)
            if (it.isNullOrEmpty()) {
                if(mBinding.checkEditH2.checkEdit.text.isNullOrEmpty()){
                    mBinding.checkObdQx2Hint.text = ("倾斜量/测量高度2=斜率‰")
                }else{
                    mBinding.checkObdQx2Hint.text = ("倾斜量/"+mBinding.checkEditH2.checkEdit.text.toString()+"=斜率‰")
                }
            } else if (mBinding.checkEditH2.checkEdit.text.isNullOrEmpty()) {
                mBinding.checkObdQx2Hint.text = (""+it.toString() + "/测量高度2" + "=斜率‰")
            } else {
                mBinding.checkObdQx2Hint.text =
                    (""+it.toString() + "/" + mBinding.checkEditH2.checkEdit.text.toString()
                            + "=" + getNoMoreThanTwoDigits((it.toString()
                        .toDouble()) / mBinding.checkEditH2.checkEdit.text.toString()
                        .toDouble()) + "‰")
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
            mBinding.checkObdUiCaptureView.content2.visibility = View.VISIBLE
            mBinding.checkEditH2.checkEdit.setText("")
            mBinding.checkEditH2.checkEdit.isEnabled = true
            mBinding.checkEditQx2.checkEdit.isEnabled = true
            mBinding.checkEditQx2.checkEdit.setText("")
        }
        mBinding.checkObdDoubleText.setOnClickListener {
            mBinding.checkObdOnlyRadio.isSelected = false
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
            if (mBinding.checkObdHintText.text.isNullOrEmpty()) {
                showToast("请添加房屋倾斜点备注")
                return@setOnClickListener
            }
            mAddAnnotReF = (activity as CheckObliqueDeformationActivity).mCurrentAddAnnotReF
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
                mBinding.checkEditPoint.checkEdit.text.toString(),
                mBinding.checkEditH1.checkEdit.text.toString(),
                mBinding.checkEditH2.checkEdit.text.toString(),
                mBinding.checkEditQx1.checkEdit.text.toString(),
                mBinding.checkEditQx2.checkEdit.text.toString(),
                mBinding.checkObdUiCaptureView.content1.oriRotation.toInt(),
                mBinding.checkObdUiCaptureView.content2.oriRotation.toInt(),
            )
            var layout:View
            if (mBinding.checkObdOnlyRadio.isSelected) {

                layout= LayoutInflater.from(activity).inflate(R.layout.damage_mark_index_layout_2, null)
                val drawAndTextView = layout.findViewById< (DrawAndTextView)>(R.id.content_1)

                drawAndTextView .setContent("倾斜1")
                drawAndTextView.setTextSize(6)
                drawAndTextView.setDrawViewWidth(-1)
                drawAndTextView.setDrawViewHeight(-1)
                drawAndTextView.setTextViewHeight(500)

                drawAndTextView.setTopText(mBinding.checkObdQx1Hint.text.toString())

                drawAndTextView.init()

                drawAndTextView.drawView.setIsShowTopText(true)
                drawAndTextView.drawView.setAngle(mBinding.checkObdUiCaptureView.content1.oriRotation%360)
                drawAndTextView.addMarkView(mBinding.checkObdUiCaptureView.content1.oriRotation%360);
                drawAndTextView.addTopView(mBinding.checkObdUiCaptureView.content1.oriRotation,false)
                drawAndTextView.requestFocus()




            }else{
                layout= LayoutInflater.from(activity).inflate(R.layout.damage_mark_index_layout_3, null)
                val drawAndTextView = layout.findViewById< (DrawAndTextView)>(R.id.content_1)
                val drawAndTextView2 = layout.findViewById< (DrawAndTextView)>(R.id.content_2)

                drawAndTextView .setContent("倾斜1")
                drawAndTextView.setTextSize(6)
                drawAndTextView.setDrawViewWidth(-1)
                drawAndTextView.setDrawViewHeight(-1)
                drawAndTextView.setTextViewHeight(500)

                drawAndTextView.setTopText(mBinding.checkObdQx1Hint.text.toString())

                drawAndTextView.init()

                drawAndTextView.drawView.setIsShowTopText(true)
                drawAndTextView.drawView.setAngle(mBinding.checkObdUiCaptureView.content1.oriRotation%360)
                drawAndTextView.addMarkView(mBinding.checkObdUiCaptureView.content1.oriRotation%360);
                drawAndTextView.addTopView(mBinding.checkObdUiCaptureView.content1.oriRotation,false)
                drawAndTextView.requestFocus()



                drawAndTextView2 .setContent("倾斜2")
                drawAndTextView2.setTextSize(6)
                drawAndTextView2.setDrawViewWidth(-1)
                drawAndTextView2.setDrawViewHeight(-1)
                drawAndTextView2.setTextViewHeight(500)

                drawAndTextView2.setTopText(mBinding.checkObdQx2Hint.text.toString())

                drawAndTextView2.init()

                drawAndTextView2.drawView.setIsShowTopText(true)
                drawAndTextView2.drawView.setAngle(mBinding.checkObdUiCaptureView.content2.oriRotation%360)
                drawAndTextView2.addMarkView(mBinding.checkObdUiCaptureView.content2.oriRotation%360);
                drawAndTextView2.addTopView(mBinding.checkObdUiCaptureView.content2.oriRotation,false)

                LogUtils.d("角度"+mBinding.checkObdUiCaptureView.content1.oriRotation+"//"+mBinding.checkObdUiCaptureView.content2.oriRotation)
                drawAndTextView2.requestFocus()
            }
            (context as CheckObliqueDeformationActivity).saveDamage(damage,layout)

            }

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
        LogUtils.d("resetView " + damageV3Bean)
        mView?.setIntercept(false)
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

            mBinding.checkObdUiCaptureView.content2.resetView(90f)
            mBinding.checkObdUiCaptureView.content1.resetView(0f)

            mBinding.checkObdHintText.setText("")
            mDamageCreateTime = -1L
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

                mBinding.checkObdUiCaptureView.content1.resetView((damageV3Bean.tiltRotate1)!!.toFloat())
                mBinding.checkObdUiCaptureView.content2.resetView((damageV3Bean.tiltRotate2)!!.toFloat())

            } else {
                mBinding.checkObdOnlyRadio.isSelected = true
                mBinding.checkObdDoubleRadio.isSelected = false
                mBinding.checkEditH2.checkEdit.isEnabled = false
                mBinding.checkEditQx2.checkEdit.isEnabled = false
                mBinding.checkEditH1.checkEdit.setText(damageV3Bean.measure1Height)
                mBinding.checkEditQx1.checkEdit.setText(damageV3Bean.tilt1)
                mBinding.checkEditH2.checkEdit.setText("")
                mBinding.checkEditQx2.checkEdit.setText("")

                mBinding.checkObdUiCaptureView.content1.resetView((damageV3Bean.tiltRotate1)!!.toFloat())
                mBinding.checkObdUiCaptureView.content2.visibility = View.GONE
                mBinding.checkObdUiCaptureView.content2.resetView(90f)
            }
            mBinding.checkEditPoint.checkEdit.setText(damageV3Bean.pointName)
            mBinding.checkObdHintText.setText(damageV3Bean.note)
            mDamageCreateTime = damageV3Bean.createTime
        }
    }

    private var mView: PDFLayoutView? = null

    fun openPDF(pdfPath: String) {
        if (!pdfPath.endsWith("pdf") &&
            !pdfPath.endsWith("PDF")
        ) {
            showToast("该图纸类型不是pdf，无法加载")
            return
        }
        this.mView = mBinding.checkObdUi
        mView!!.setShowDamage(false)
        mView?.setIntercept(true)

       // mView!!.PDFSetZoom(, mView!!.testY, mView?.PDFGetPos(mView!!.testX,mView!!.testY), 2f);

        Observable.create<Boolean> { o ->
            o.onNext(XXPermissions.isGranted(activity, Permission.MANAGE_EXTERNAL_STORAGE))
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                var ret: Int? = -20
                when (it) {
                    true -> {
                        mDoc = Document()
                        ret = mDoc!!.Open(pdfPath, "")
                    }
                }
                Observable.create<Int> { o ->
                    o.onNext(ret ?: -10)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mView?.PDFOpen(mDoc, this)
                mView?.setReadOnly(false)
            }, {
                LogUtils.d(" not granted ${Permission.MANAGE_EXTERNAL_STORAGE}")
            })
    }

    override fun OnPDFPageModified(pageno: Int) {
    }

    override fun OnPDFPageChanged(pageno: Int) {
    }

    override fun OnPDFAnnotTapped(pno: Int, annot: Page.Annotation?) {
    }

    override fun OnPDFBlankTapped(pagebo: Int) {
    }

    override fun OnPDFSelectEnd(text: String?) {
    }

    override fun OnPDFOpenURI(uri: String?) {
    }

    override fun OnPDFOpenJS(js: String?) {
    }

    override fun OnPDFOpenMovie(path: String?) {
    }

    override fun OnPDFOpenSound(paras: IntArray?, path: String?) {
    }

    override fun OnPDFOpenAttachment(path: String?) {
    }

    override fun OnPDFOpenRendition(path: String?) {
    }

    override fun OnPDFOpen3D(path: String?) {
    }

    override fun OnPDFZoomStart() {
    }

    override fun OnPDFZoomEnd() {
    }

    override fun OnPDFDoubleTapped(pagebo: Int, x: Float, y: Float): Boolean {
        return true
    }

    override fun OnPDFLongPressed(pagebo: Int, x: Float, y: Float) {
    }

    override fun OnPDFSearchFinished(found: Boolean) {
    }

    override fun OnPDFPageDisplayed(canvas: Canvas?, vpage: ILayoutView.IVPage?) {
    }

    override fun OnPDFPageRendered(vpage: ILayoutView.IVPage?) {

        var rect= (activity as CheckObliqueDeformationActivity).mView?.m_rects

        LogUtils.d("(rect?.get(0)!!+((rect?.get(2) - rect?.get(0))/2)) "+rect?.get(0))

        LogUtils.d("(rect?.get(1)!!+((rect?.get(3) - rect?.get(1))/2)) "+rect?.get(1))

        LogUtils.d("(rect?.get(1)!!+((rect?.get(3) - rect?.get(1))/2)) "+mView?.layoutWidth)

        LogUtils.d("(rect?.get(1)!!+((rect?.get(3) - rect?.get(1))/2)) "+mView?.layoutHieght)


        LogUtils.d("(rect?.get(1)!!+((rect?.get(3) - rect?.get(1))/2)) "+(activity as CheckObliqueDeformationActivity).mView?.layoutWidth)

        LogUtils.d("(rect?.get(1)!!+((rect?.get(3) - rect?.get(1))/2)) "+(activity as CheckObliqueDeformationActivity).mView?.layoutHieght)


        LogUtils.d("(rect?.get(1)!!+((rect?.get(3) - rect?.get(1))/2))3333 "+(mView?.layoutWidth!!.toDouble() / (activity as CheckObliqueDeformationActivity).mView?.layoutWidth!!.toDouble()))

        var x =  rect?.get(0)!! * (mView?.layoutWidth!!.toDouble() / (activity as CheckObliqueDeformationActivity).mView?.layoutWidth!!.toDouble())

        var y = rect?.get(1)!! * (mView?.layoutHieght!!.toDouble() / (activity as CheckObliqueDeformationActivity).mView?.layoutHieght!!.toDouble())

        LogUtils.d("${x}   ${y}")
        var page = mDoc.GetPage(vpage!!.GetPageNo())
        LogUtils.d("page: ${page}")
      /*  if(page != null){
            var annot =  page.GetAnnot(page.GetAnnotCount()-1)
            var rect = annot.GetRect()
            LogUtils.d("${rect[0]}   ${rect[1]}  ${rect[2]}  ${rect[3]}")

            mView!!.PDFSetZoom(rect[0].toInt(), rect[1].toInt(), mView?.PDFGetPos(rect[0].toInt(),rect[1].toInt()), )
        }*/

        LogUtils.d("9999999999 "+x+" ; "+y)

        LogUtils.d("1111111111 "+(activity as CheckObliqueDeformationActivity).mView?.PDFGetPos(x.toInt(),y.toInt()))

        LogUtils.d("2222222222 "+mView?.PDFGetPos(rect?.get(0).toInt(),rect?.get(1).toInt()))


     //   LogUtils.d("2222222222 "+(((activity as CheckObliqueDeformationActivity).mView?.layoutWidth!!)/mView?.layoutWidth!!))

        mView!!.PDFSetZoom(x.toInt(), y.toInt(), mView?.PDFGetPos(x.toInt(),y.toInt()), 2f);

  /*      val mCurZoomLevel = mView!!.PDFGetZoom()
        if (mView!!.PDFGetScale() <= mView!!.PDFGetMinScale()) Global.g_zoom_step = 1f
        if (mCurZoomLevel > Global.g_layout_zoom_level && Global.g_zoom_step > 0 ||
            mCurZoomLevel == 1f && Global.g_zoom_step < 0
        ) //reverse zoom step
            Global.g_zoom_step = 2f

        LogUtils.d("OnPDFPageRendered： " + mCurZoomLevel + Global.g_zoom_step)*/

        //    mView!!.PDFSetZoom(x, y, mView!!.PDFGetPos(x, y), 5f)
        //   mView!!.PDFSetZoom(770,383, mView?.PDFGetPos(770,383), 5f)
    //    var vx = (activity as CheckObliqueDeformationActivity).mView?.PDFGetX()
    //    mView!!.PDFSetZoom(vpage!!.GetVX(x), vpage!!.GetVY(y), (activity as CheckObliqueDeformationActivity).mView?.markPDFPos, 1f)
    }

    override fun onPDFNoteTapped(jstring: String?) {
    }

    override fun onPDFNoteAdded(annotPoint: String?) {
    }

    override fun onPDFNoteEdited(annotPoint: String?) {
    }

    override fun onPDFNoteDeleted(annotPoint: String?) {
    }

    override fun onButtonPrevPressed() {
    }

    override fun onButtonNextPressed() {
    }
}