package com.sribs.bdd.v3.ui.draw


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libbase.rxbus.RxBus
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.radaee.annotui.UIAnnotMenu
import com.radaee.pdf.Document
import com.radaee.pdf.Global
import com.radaee.pdf.Page
import com.radaee.reader.PDFViewController
import com.radaee.util.PDFAssetStream
import com.radaee.util.PDFHttpStream
import com.radaee.util.RadaeePluginCallback
import com.radaee.view.ILayoutView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.ActivityDrawPdfactivityBinding
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.bdd.v3.adapter.DrawPDFMenuAdapter
import com.sribs.bdd.v3.event.RefreshPDFEvent
import com.sribs.bdd.v3.module.DrawPDFMenuModule
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.utils.FileUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

@Route(path = com.sribs.common.ARouterPath.DRAW_PDF_ACTIVITY)
class DrawPDFActivity : BaseActivity() {

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_CURRENT_PDF)
    var mLocalPDF = ""

    private val mBinding: ActivityDrawPdfactivityBinding by inflate()

    private var mMenuList = ArrayList<DrawPDFMenuModule>()

    private var mMenuAdapter: DrawPDFMenuAdapter? = null

    private var mChecked = false

    override fun deinitView() {

    }

    override fun getView(): View {
        return mBinding.root
    }

    var checkedIndex = -1

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
        mMenuAdapter!!.setItemClickListener {position,checked->
            LogUtils.d("当前选中position: " + position)
            mChecked = checked

            when(checkedIndex){
                0 -> {
                    mBinding.pdfView.PDFSetLine(1)
                }
                1 -> {
                    mBinding.pdfView.PDFSetRect(1)
                }
                2 -> {
                    mBinding.pdfView.PDFSetEllipse(1)
                }
                3 -> {
                    mBinding.pdfView.PDFSetEditbox(1)
                }
                4 -> {
                    mBinding.pdfView.PDFSetInk(1)
                }
            }

            checkedIndex = position

            if(checked) {
                when (position) {
                    0 -> {
                        mBinding.pdfView.PDFSetLine(0)
                    }
                    1 -> {
                        mBinding.pdfView.PDFSetRect(0)
                    }
                    2 -> {
                        mBinding.pdfView.PDFSetEllipse(0)
                    }
                    3 -> {
                        mBinding.pdfView.PDFSetEditbox(0)
                    }
                    4 -> {
                        mBinding.pdfView.PDFSetInk(0)
                    }
                }
            }
        }
        mBinding.leftLayout.gridview.numColumns = 3
        mBinding.leftLayout.gridview.adapter = mMenuAdapter
        mBinding.leftLayout.gridview.horizontalSpacing = 6
        mBinding.leftLayout.gridview.verticalSpacing = 6

        mBinding.leftLayout.cancel.setOnClickListener { //取消选中
            mMenuAdapter!!.notifyChecked(-1)

        }

        mBinding.leftLayout.ok.setOnClickListener { //完成绘制
            mMenuAdapter!!.notifyChecked(-1)
            promptToSavePdf(false)
        }

        Global.Init(this)
       // openPdf("111.pdf")
    //    browseDocuments(0)
        openPdf(mLocalPDF)
    }

    private fun browseDocuments(requestCode:Int) {
        val supportedMimeTypes = arrayOf("application/pdf", "image/*")
        var intent:Intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.type = if (supportedMimeTypes.size === 1) supportedMimeTypes[0] else "*/*"
            if (supportedMimeTypes.size > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, supportedMimeTypes)
            }
        } else {
            var mimeTypes = ""
            for (mimeType in supportedMimeTypes) {
                mimeTypes += "$mimeType|"
            }
            intent.type = mimeTypes.substring(0, mimeTypes.length - 1)
        }
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(intent, requestCode);

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var uri: Uri?
        var uriList: ArrayList<Uri> = ArrayList<Uri>()
        if (resultCode == AppCompatActivity.RESULT_OK) {
            var fileNameString:String = ""
            var origiNameString:String = ""
            if (data != null) {
                // Checking for selection multiple files or single.
                if (data.clipData != null) {
                    // Getting the length of data and logging up the logs using index
                    var index = 0
                    while (index < data.clipData!!.itemCount) {
                        // Getting the URIs of the selected files and logging them into logcat at debug level
                        uri = data.clipData!!.getItemAt(index).uri
                        uri?.let {
                            LogUtils.d("clipData uri="+uri.toString())
                            uriList.add(uri!!)
                            origiNameString = FileUtil.uriToFileName(uri!!, this)
                            LogUtils.d("origiNameString： "+origiNameString)
                            if (!fileNameString.isNullOrEmpty())
                                fileNameString += ", " + origiNameString
                            else
                                fileNameString = origiNameString
                        }
                        index++
                    }
                } else {
                    // Getting the URI of the selected file and logging into logcat at debug level
                    uri = data.data
                    uri?.let {
                        LogUtils.d("uri： "+uri.toString())
                        uriList.add(uri)
                        origiNameString = FileUtil.uriToFileName(uri, this)
                        LogUtils.d("origiNameString： "+origiNameString)
                        if(!fileNameString.isNullOrEmpty())
                            fileNameString += ", " + origiNameString
                        else
                            fileNameString = origiNameString
                    }

                    var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(this)
                    var mCurDrawingsDir = "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/"
                    var cachePath = cacheRootDir + mCurDrawingsDir + System.currentTimeMillis()+".pdf"

                    addDisposable(Observable.fromIterable(uriList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .flatMap{
                            val file = File(cacheRootDir + mCurDrawingsDir)
                            file.mkdirs()
                            LogUtils.d("cachePath file："+file)
                            if (cachePath != null) {
                                FileUtil.copyFileTo(this, Uri.parse(it.toString()), cachePath)
                            }
                            Observable.just("Done")
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe({
                            LogUtils.d("copyDrawingsToLocalCache =${it}")
                            openPdf(cachePath)
                        },{
                            it.printStackTrace()
                        }))
                }
            }
        }
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
         //   finish()
            mMenuAdapter!!.notifyChecked(-1)
            promptToSavePdf(true)
        }
    }

    private var mDoc: Document = Document()

    private var mAssetStream: PDFAssetStream? = PDFAssetStream()

    private var mHttpStream: PDFHttpStream? = null

    private var mController: PDFViewController? = null

    @SuppressLint("CheckResult")
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
                        ret = mDoc!!.Open(pdfPath, "")
                        LogUtils.d("打开pdf: "+ret)
                    }
                }
                Observable.create<Int> { o ->
                    o.onNext(ret ?: -10)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mBinding.pdfView?.setReadOnly(false)
                mBinding.pdfView?.setAnnotMenu(UIAnnotMenu(mBinding.pdfRoot))
                mBinding.pdfView.PDFOpen(mDoc, object : ILayoutView.PDFLayoutListener {
                    override fun OnPDFPageModified(pageno: Int) {
                        mPDFNoteModified = true
                        mController?.onPageModified(pageno)
                    }
                    private var m_cur_page = 0
                    override fun OnPDFPageChanged(pageno: Int) {
                        LogUtils.d("OnPDFPageChanged")
                        m_cur_page = pageno
                        if (mController != null) mController!!.OnPageChanged(pageno)
                        RadaeePluginCallback.getInstance().didChangePage(pageno)
                    }

                    override fun OnPDFAnnotTapped(pno: Int, annot: Page.Annotation?) {
                        LogUtils.d("OnPDFAnnotTapped")
                        if (annot != null) {
                            RadaeePluginCallback.getInstance().onAnnotTapped(annot)
                            if (!mBinding.pdfView!!.PDFCanSave() && annot.GetType() != 2) return
                        }
                        if (mController != null) mController!!.OnAnnotTapped(pno, annot)
                    }

                    override fun OnPDFBlankTapped(pagebo: Int) {
                        LogUtils.d("OnPDFBlankTapped")
                        if (mController != null)
                            mController!!.OnBlankTapped()
                        RadaeePluginCallback.getInstance().onBlankTapped(pagebo)
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
                        RadaeePluginCallback.getInstance().onLongPressed(pagebo, x, y)
                    }

                    override fun OnPDFSearchFinished(found: Boolean) {
                        LogUtils.d("OnPDFSearchFinished")
                    }

                    override fun OnPDFPageDisplayed(canvas: Canvas?, vpage: ILayoutView.IVPage?) {
                        LogUtils.d("OnPDFPageDisplayed")
                    }

                    private var mDidShowReader = false
                    override fun OnPDFPageRendered(vpage: ILayoutView.IVPage?) {
                        LogUtils.d("OnPDFPageRendered" +vpage)
                        if (!mDidShowReader) {
                            RadaeePluginCallback.getInstance().didShowReader()
                            mDidShowReader = true
                        }
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

                 mController = PDFViewController(mBinding.pdfRoot,
                     mBinding.pdfView,
                     pdfPath,
                     mAssetStream != null || mHttpStream != null
                 )
                /* mController!!.SetPagesListener(View.OnClickListener {
                     val intent = Intent()
                     intent.setClass(this, PDFPagesAct::class.java)
                     PDFPagesAct.SetTranDoc(mDoc)
                     startActivityForResult(intent, 10000)
                 })*/
            }, {
                LogUtils.d(" not granted ${Permission.MANAGE_EXTERNAL_STORAGE}")
            })
    }

    /**
     * 是否改动了  测试默认改动
     */
    private var mPDFNoteModified:Boolean = false

    fun getFileState(): Int {
        return mController?.getFileState() ?: PDFViewController.NOT_MODIFIED
    }

    fun isPDFModifiedNotSaved():Boolean{
        return (getFileState() == PDFViewController.MODIFIED_NOT_SAVED || mPDFNoteModified)
    }

    private fun promptToSavePdf(ifExit: Boolean){
        if(mChecked){
            showToast("当前绘制未完成")
            return
        }
        if (isPDFModifiedNotSaved()) {
            AlertDialog.Builder(this).setTitle(if(ifExit)getString(R.string.drawing_edit_exit_dialog_title ) else "提示")
                .setMessage(R.string.save_pdf_message).setPositiveButton(R.string.dialog_ok) { dialog, which ->
                    mController?.savePDF()
                    mPDFNoteModified = false;
                    RxBus.getDefault().post(RefreshPDFEvent(true))
                    if(ifExit)
                        finish()
                }.setNegativeButton(R.string.dialog_cancel
                ) {
                        dialog, which ->
                    if(ifExit)
                        finish()
                }
                .show()
        }
        else
        {
            if(ifExit){
                finish()
            }else{
                showToast("图纸无更改")
            }
        }
    }


}