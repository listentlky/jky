package com.sribs.bdd.v3.ui.draw


import android.content.Intent
import android.graphics.Canvas
import android.text.TextUtils
import android.view.View
import androidx.appcompat.view.menu.MenuAdapter
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.radaee.annotui.UIAnnotMenu
import com.radaee.pdf.Document
import com.radaee.pdf.Global
import com.radaee.pdf.Page
import com.radaee.reader.PDFLayoutView
import com.radaee.reader.PDFPagesAct
import com.radaee.reader.PDFViewController
import com.radaee.util.PDFAssetStream
import com.radaee.util.PDFHttpStream
import com.radaee.util.RadaeePDFManager
import com.radaee.util.RadaeePluginCallback
import com.radaee.view.ILayoutView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.ActivityDrawPdfactivityBinding
import com.sribs.bdd.ui.building.BldDrwDmgMngMainFragment
import com.sribs.bdd.v3.adapter.DrawPDFMenuAdapter
import com.sribs.bdd.v3.module.DrawPDFMenuModule
import com.sribs.bdd.v3.util.LogUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Route(path = com.sribs.common.ARouterPath.DRAW_PDF_ACTIVITY)
class DrawPDFActivity : BaseActivity() {

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    private val mBinding: ActivityDrawPdfactivityBinding by inflate()

    private var mMenuList = ArrayList<DrawPDFMenuModule>()

    private var mMenuAdapter: DrawPDFMenuAdapter? = null

    override fun deinitView() {

    }

    override fun getView(): View {
        return mBinding.root
    }

    override fun initView() {
        initToolbar()
        mMenuList.add(DrawPDFMenuModule().also {
            it.icon = R.mipmap.btn_line
            it.text = "直线"
        })
        mMenuList.add(DrawPDFMenuModule().also {
            it.icon = R.mipmap.btn_rect
            it.text = "矩形"
        })
        mMenuList.add(DrawPDFMenuModule().also {
            it.icon = R.mipmap.btn_circle
            it.text = "圆形"
        })
        mMenuList.add(DrawPDFMenuModule().also {
            it.icon = R.mipmap.btn_text
            it.text = "文字"
        })
        mMenuList.add(DrawPDFMenuModule().also {
            it.icon = R.mipmap.btn_manual
            it.text = "手绘"
        })
        mMenuAdapter = DrawPDFMenuAdapter(this, mMenuList)
        mMenuAdapter!!.setItemClickListener {
            LogUtils.d("当前选中position: " + it)
            when (it) {
                0 -> {
             //       mBinding.pdfView.PDFSetRect(1)
              /*      mBinding.pdfView.PDFSetEllipse(1)
                    mBinding.pdfView.PDFSetInk(1)*/

                    mBinding.pdfView.PDFSetLine(0)

                    //   mBinding.pdfView.PDFSetNote(1)
                }
                1 -> {
              //      mBinding.pdfView.PDFSetLine(1)
           //         mBinding.pdfView.PDFSetEllipse(1)
                    //     mBinding.pdfView.PDFSetNote(1)
          //          mBinding.pdfView.PDFSetInk(1)
                    mBinding.pdfView.PDFSetRect(0)
                }
                2 -> {
           /*         mBinding.pdfView.PDFSetLine(1)
                    mBinding.pdfView.PDFSetRect(1)
                    //      mBinding.pdfView.PDFSetNote(1)
                    mBinding.pdfView.PDFSetInk(1)*/
                    mBinding.pdfView.PDFSetEllipse(0)
                }
                3 -> {
                    mBinding.pdfView.PDFSetEditbox(0)
                    //       mBinding.pdfView.PDFSetNote(0)
                    /*    mBinding.pdfView.PDFSetLine(1)
                        mBinding.pdfView.PDFSetRect(1)
                        mBinding.pdfView.PDFSetEllipse(1)
                        mBinding.pdfView.PDFSetInk(1)*/
                }
                4 -> {
                /*    mBinding.pdfView.PDFSetLine(1)
                    mBinding.pdfView.PDFSetRect(1)
                    mBinding.pdfView.PDFSetEllipse(1)*/
                    mBinding.pdfView.PDFSetInk(0)
                    //     mBinding.pdfView.PDFSetNote(1)

                }
            }
        }
        mBinding.leftLayout.gridview.numColumns = 3
        mBinding.leftLayout.gridview.adapter = mMenuAdapter

        mBinding.leftLayout.cancel.setOnClickListener { //取消选中
            mMenuAdapter!!.notifyItem(-1)
            mBinding.pdfView.PDFCancelAnnot()
        }

        mBinding.leftLayout.ok.setOnClickListener { //完成绘制


        }

        Global.Init(this)
        openPdf("111.pdf")
    }

    /**
     * @Description 初始化toolbar
     */
    private fun initToolbar() {
        mBinding.toolbarTitle.text = mTitle
        mBinding.toolbar.setNavigationIcon(R.mipmap.icon_back)
        mBinding.toolbar.showOverflowMenu()
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mBinding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private var mDoc: Document = Document()

    private var mAssetStream: PDFAssetStream? = PDFAssetStream()

    private var mHttpStream: PDFHttpStream? = null

    private var mController: PDFViewController? = null

    private fun openPdf(pdfPath: String) {
        Observable.create<Boolean> { o ->
            o.onNext(XXPermissions.isGranted(this, Permission.MANAGE_EXTERNAL_STORAGE))
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                var ret: Int? = -20
                when (it) {
                    true -> {
                        mDoc = Document()
                        mAssetStream?.open(assets, pdfPath)
                        ret = mDoc!!.OpenStream(mAssetStream, "")
                    }
                }
                Observable.create<Int> { o ->
                    o.onNext(ret ?: -10)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mBinding.pdfView.PDFOpen(mDoc, object : ILayoutView.PDFLayoutListener {
                    override fun OnPDFPageModified(pageno: Int) {
                        LogUtils.d("OnPDFPageModified "+mDoc.CanSave())
                        mDoc.Save()
                    }

                    override fun OnPDFPageChanged(pageno: Int) {
                        LogUtils.d("OnPDFPageChanged")
                    }

                    override fun OnPDFAnnotTapped(pno: Int, annot: Page.Annotation?) {
                        LogUtils.d("OnPDFAnnotTapped")
                    }

                    override fun OnPDFBlankTapped(pagebo: Int) {
                        LogUtils.d("OnPDFBlankTapped")
                    }

                    override fun OnPDFSelectEnd(text: String?) {
                        LogUtils.d("OnPDFSelectEnd")
                    }

                    override fun OnPDFOpenURI(uri: String?) {
                        LogUtils.d("OnPDFOpenURI")
                    }

                    override fun OnPDFOpenJS(js: String?) {
                        LogUtils.d("OnPDFOpenJS")
                    }

                    override fun OnPDFOpenMovie(path: String?) {
                        LogUtils.d("OnPDFOpenMovie")
                    }

                    override fun OnPDFOpenSound(paras: IntArray?, path: String?) {
                        LogUtils.d("OnPDFOpenSound")
                    }

                    override fun OnPDFOpenAttachment(path: String?) {
                        LogUtils.d("OnPDFOpenAttachment")
                    }

                    override fun OnPDFOpenRendition(path: String?) {
                        LogUtils.d("OnPDFOpenRendition")
                    }

                    override fun OnPDFOpen3D(path: String?) {
                        LogUtils.d("OnPDFOpen3D")
                    }

                    override fun OnPDFZoomStart() {
                        LogUtils.d("OnPDFZoomStart")
                    }

                    override fun OnPDFZoomEnd() {
                        LogUtils.d("OnPDFZoomEnd")
                    }

                    override fun OnPDFDoubleTapped(pagebo: Int, x: Float, y: Float): Boolean {
                        LogUtils.d("OnPDFDoubleTapped")
                        return true
                    }

                    override fun OnPDFLongPressed(pagebo: Int, x: Float, y: Float) {
                        LogUtils.d("OnPDFLongPressed")
                    }

                    override fun OnPDFSearchFinished(found: Boolean) {
                        LogUtils.d("OnPDFSearchFinished")
                    }

                    override fun OnPDFPageDisplayed(canvas: Canvas?, vpage: ILayoutView.IVPage?) {
                        LogUtils.d("OnPDFPageDisplayed")
                    }

                    override fun OnPDFPageRendered(vpage: ILayoutView.IVPage?) {
                        LogUtils.d("OnPDFPageRendered")
                    }

                    override fun onPDFNoteTapped(jstring: String?) {
                        LogUtils.d("onPDFNoteTapped")
                    }

                    override fun onPDFNoteAdded(annotPoint: String?) {
                        LogUtils.d("onPDFNoteAdded")
                    }

                    override fun onPDFNoteEdited(annotPoint: String?) {
                        LogUtils.d("onPDFNoteEdited")
                    }

                    override fun onPDFNoteDeleted(annotPoint: String?) {
                        LogUtils.d("onPDFNoteDeleted")
                    }

                    override fun onButtonPrevPressed() {
                        LogUtils.d("onButtonPrevPressed")
                    }

                    override fun onButtonNextPressed() {
                        LogUtils.d("onButtonNextPressed")
                    }

                })

                /* mController = PDFViewController(mLayout,
                     mView,
                     mPath,
                     mAssetStream != null || mHttpStream != null
                 )
                 mController!!.SetPagesListener(View.OnClickListener {
                     val intent = Intent()
                     intent.setClass(this, PDFPagesAct::class.java)
                     PDFPagesAct.SetTranDoc(mDoc)
                     startActivityForResult(intent, 10000)
                 })*/
            }, {
                LogUtils.d(" not granted ${Permission.MANAGE_EXTERNAL_STORAGE}")
            })
    }


}