package com.sribs.bdd.v3.ui.check.bs

import android.graphics.Canvas
import android.graphics.Color
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.adapter.BasePagerAdapter
import com.cbj.sdk.libui.mvp.inflate
import com.radaee.annotui.UIAnnotMenu
import com.radaee.pdf.*
import com.radaee.pdf.Document.DocFont
import com.radaee.reader.PDFLayoutView
import com.radaee.reader.PDFViewController
import com.radaee.util.PDFAssetStream
import com.radaee.util.PDFHttpStream
import com.radaee.view.ILayoutView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.ActivityCheckBuildStructureBinding
import com.sribs.bdd.v3.util.LogUtils

@Route(path = com.sribs.common.ARouterPath.CHECK_BUILD_STRUCTURE_ACTIVITY)
class CheckBuildStructureActivity : BaseActivity() {

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    private val mBinding: ActivityCheckBuildStructureBinding by inflate()

    private val mFragments by lazy {
        listOf(
            ARouter.getInstance()
                .build(com.sribs.common.ARouterPath.CHECK_BUILD_STRUCTURE_FRAGMENT)
                .navigation() as BaseFragment,
            ARouter.getInstance()
                .build(com.sribs.common.ARouterPath.CHECK_BUILD_STRUCTURE_FLOOR_FRAGMENT)
                .navigation() as BaseFragment,
            ARouter.getInstance()
                .build(com.sribs.common.ARouterPath.CHECK_BUILD_STRUCTURE_GRID_FRAGMENT)
                .navigation() as BaseFragment
        )
    }

    override fun deinitView() {

    }

    override fun dispose(): Unit? {
        mFragments.forEach {
            it.dispose()
            it.deinitView()
        }
        return super.dispose()
    }

    override fun getView(): View {
        return mBinding.root
    }

    override fun initView() {
        initToolbar()
        initViewPager()
        Global.Init(this)

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
            if(mBinding.checkVp.currentItem != 0){
                mBinding.checkVp.currentItem = 0
            }else{
                finish()
            }
        }
    }

    private fun initViewPager() {
        mBinding.checkVp.setSmooth(false)
        mBinding.checkVp.setScroll(false)
        mBinding.checkVp.adapter = BasePagerAdapter(supportFragmentManager, mFragments)
        mBinding.checkVp.offscreenPageLimit = 2
        mBinding.checkVp.currentItem = 0
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_check, menu)
        return true
    }

    var mMenuMapView: View? = null
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        var item = menu?.findItem(R.id.menu_check_pop)

        item?.icon?.setBounds(20, 50, 0, 0)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item?.itemId) {
            R.id.menu_check_pop -> { // 菜单111

            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun setVpCurrentItem(item: Int) {
        mBinding.checkVp.currentItem = item
    }

    private var mDoc: Document = Document()

    private var mAssetStream: PDFAssetStream? = PDFAssetStream()

    private var mHttpStream: PDFHttpStream? = null

    private var mController: PDFViewController? = null

    fun openPDF(layout: RelativeLayout, pdfView:PDFLayoutView, pdfPath:String){
        mAssetStream?.open(assets,pdfPath)
        var open = mDoc?.OpenStream(mAssetStream, null)
        pdfView?.setReadOnly(false)
        pdfView?.setAnnotMenu(UIAnnotMenu(layout))
     //   var open = mDoc?.Open(pdfPath, null)
        when(open){
            0->{
                LogUtils.d("打开成功")
            }
            1->{
                LogUtils.d("需要输入密码")
            }
            2->{
                LogUtils.d("未知加密")
            }
            3->{
                LogUtils.d("格式损坏或无效")
            }
            10->{
                LogUtils.d("访问被拒绝或文件路径无效")
            }
        }
        pdfView.PDFOpen(mDoc,object :ILayoutView.PDFLayoutListener{
            override fun OnPDFPageModified(pageno: Int) {
                LogUtils.d("OnPDFPageModified")
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
                LogUtils.d("OnPDFDoubleTapped ${x} ; ${y}")
   /*             var getPage = mDoc!!.GetPage(pagebo)
                if(getPage != null){
                    val mat: Matrix = CreateInvertMatrix(
                        x,
                        y
                    )
                }
                pdfView.dr*/
                addText(pagebo,x,y)
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

        mController = PDFViewController(layout,
            pdfView,
            pdfPath,
            mAssetStream != null || mHttpStream != null
        )
    }

    private fun addText(pageno: Int, x: Float, y: Float) {
        LogUtils.d("mDoc: ${mDoc} ;pageno:${pageno} ;x:${x} ;y:${y}")

        var page = mDoc.GetPage(pageno)
        LogUtils.d("page: ${page}")
        if(page != null){

            val matrix = Matrix(x, y, 100f, 10f)
            var floatArray = floatArrayOf(50f,50f,50f)

            page.AddAnnotMarkup(matrix,floatArray,1)


     /*       val content = PageContent()
            content.Create()
            content.TextBegin()
            content.TextMove(100f, 300f)
            content.TextSetFont(rfont, 16f)
            content.SetFillColor(Color.BLUE)
            content.DrawText("啊啊啊啊啊啊啊")
            content.TextEnd()
            page.AddContent(content, true)

            content.GSSetMatrix(matrix)
       //     content.GSRestore()
            content.Destroy()*/
            page.Close()

        }
    }
}