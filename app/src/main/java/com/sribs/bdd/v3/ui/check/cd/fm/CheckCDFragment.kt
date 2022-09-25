package com.sribs.bdd.v3.ui.check.cd.fm

import android.graphics.Canvas
import android.util.Log
import android.view.View
import cc.shinichi.library.tool.ui.ToastUtil
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.radaee.pdf.*
import com.radaee.reader.PDFGLLayoutView
import com.radaee.util.PDFAssetStream
import com.radaee.view.ILayoutView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckObliquedeformationBinding
import com.sribs.bdd.v3.module.CheckMenuModule
import com.sribs.bdd.v3.ui.check.cd.CheckComponentDetectionActivity
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.bdd.v3.view.CheckMenuView2
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.DamageV3Bean


@Route(path = ARouterPath.CHECK_COMPONENT_DETECTION_FRAGMENT)
class CheckCDFragment : BaseFragment(R.layout.fragment_check_componentdetection),
    ILayoutView.PDFLayoutListener {

    private val mBinding: FragmentCheckObliquedeformationBinding by bindView()

    private var mMenuList: ArrayList<CheckMenuModule>? = ArrayList()
    private var m_doc = Document()
    private var m_asset_stream = PDFAssetStream()

    private lateinit var mPdfView: PDFGLLayoutView

    override fun deinitView() {

    }


    fun initPdf() {
        Global.Init(activity  as CheckComponentDetectionActivity)

        m_asset_stream.open((activity as CheckComponentDetectionActivity).assets, "111.pdf")
        m_doc = Document()
        val ret: Int = m_doc.OpenStream(m_asset_stream, null)
        if (ret == 0) {
            mBinding.pdfLayout.PDFOpen(m_doc,this)
        }
    }


    override fun initView() {
        mMenuList?.add(CheckMenuModule().also {
            it.name = "构件检测列表"
            it.menu?.add(CheckMenuModule.Item().also {
                it.name = "梁"
                it.item?.add(CheckMenuModule.Item.Mark().also {
                    it.name = "轴线"
                })
            })
            it.menu?.add(CheckMenuModule.Item().also {
                it.name = "柱"
            })
            it.menu?.add(CheckMenuModule.Item().also {
                it.name = "墙"
            })
            it.menu?.add(CheckMenuModule.Item().also {
                it.name = "板"
            })
        })
        Log.d("addItemView", "mMenuList: " + mMenuList.toString());
        mBinding.toolLayout.setMenuModuleList(mMenuList!!)
            .setCheckMenuCallback(object : CheckMenuView2.CheckMenuCallback {
                override fun onClick(v: View?, damageType: String?) {
                    when (String) {
                      /*  0 -> (activity as CheckComponentDetectionActivity).setVpCurrentItem(1)
                        1 -> (activity as CheckComponentDetectionActivity).setVpCurrentItem(2)
                        2 -> (activity as CheckComponentDetectionActivity).setVpCurrentItem(3)
                        3 -> (activity as CheckComponentDetectionActivity).setVpCurrentItem(4)*/
                    }
                }

                override fun onMarkClick(v: View?, damage: DamageV3Bean?, damageType: String?) {
                    TODO("Not yet implemented")
                }

                override fun onMarkRemoveClick(v: View?, damage: DamageV3Bean?, damageType: String?) {
                    TODO("Not yet implemented")
                }
            })!!.build()

        initPdf()
    }



                /**
     * call when page changed.
     * @param pageno
     */
    override fun OnPDFPageModified(pageno: Int) {
    }

    /**
     * call when page scrolling.
     * @param pageno
     */
    override fun OnPDFPageChanged(pageno: Int) {
        LogUtils.d("D")
    }

    /**
     * call when annotation tapped.
     * @param pno
     * @param annot
     */
    override fun OnPDFAnnotTapped(pno: Int, annot: Page.Annotation?) {
        LogUtils.d("D")
    }

    /**
     * call when blank tapped on page, this mean not annotation tapped.
     */
    override fun OnPDFBlankTapped(pagebo: Int) {LogUtils.d("D")
    }

    /**
     * call select status end.
     * @param text selected text string
     */
    override fun OnPDFSelectEnd(text: String?) {LogUtils.d("D")
    }

    override fun OnPDFOpenURI(uri: String?) {LogUtils.d("D")
    }

    override fun OnPDFOpenJS(js: String?) {LogUtils.d("D")
    }

    override fun OnPDFOpenMovie(path: String?) {LogUtils.d("D")
    }

    override fun OnPDFOpenSound(paras: IntArray?, path: String?) {LogUtils.d("D")
    }

    override fun OnPDFOpenAttachment(path: String?) {LogUtils.d("D")
    }

    override fun OnPDFOpenRendition(path: String?) {LogUtils.d("D")
    }

    override fun OnPDFOpen3D(path: String?) {LogUtils.d("D")
    }

    /**
     * call when zoom start.
     */
    override fun OnPDFZoomStart() {LogUtils.d("D")
    }

    /**
     * call when zoom end
     */
    override fun OnPDFZoomEnd() {LogUtils.d("D")
    }

    override fun OnPDFDoubleTapped(pagebo: Int, x: Float, y: Float): Boolean {
      /*  var mVapge =mBinding.pdfLayout.m_layout.vGetPage(pagebo)
        val page = m_doc.GetPage(mVapge.GetPageNo())
        page.ObjsStart()
        var pt = floatArrayOf(x,y)
        var pt2 = floatArrayOf(x+100f,y+100f)
        if (  page.AddAnnotRect(pt,
                mVapge.ToPDFSize(Global.g_rect_annot_width),
                Global.g_rect_annot_color,
                Global.g_rect_annot_fill_color)){

            mBinding.pdfLayout.m_layout.vRenderAsync(mVapge)
            ToastUtil.getInstance()._short(activity,"Success")

        }else{
        ToastUtil.getInstance()._short(activity,"GG")
        }*/


       /*  var mPageConent=PageContent()
        mPageConent.Create()
        mPageConent.DrawText("hello")
        Log.e("TAG", "OnPDFDoubleTapped: " )
        var pt = floatArrayOf(100f,100f)
        var dib=DIB()
        m_doc.GetPage(0).ObjsStart()
        dib.CreateOrResize(10,10)
        val matrix = Matrix(x, y, 100f, 10f)

        m_doc.GetPage(0).Render(dib,matrix)
        //     m_doc.GetPage(0).ObjsStart()
        if (m_doc.GetPage(0).AddAnnot(100L)){
            m_doc.GetPage(0).GetAnnotCount()
            Log.e("TAG", "OnPDFDoubleTapped: "+  m_doc.GetPage(0).GetAnnotCount() )
            var mAnnotation: Page.Annotation = m_doc.GetPage(0).GetAnnot( m_doc.GetPage(0).GetAnnotCount()-1)
            mAnnotation.SetPopupText("ssss")
            m_doc.GetPage(0).Close()
            m_doc.Save()
            ToastUtil.getInstance()._short(activity, " nice"+m_doc.GetPage(0).GetAnnot(m_doc.GetPage(0).GetAnnotCount()-1))

        }else{
            ToastUtil.getInstance()._short(activity, " gg")

        }*/
        return true
    }


    fun test(pagebo: Int,document:Document){
        /*val bitmap = BitmapFactory.decodeFile(file.getAbsolutePath())
        val page: Page = document.GetPage(0)
        val pageContent = PageContent()
        pageContent.Create()
        pageContent.GSSave()
        val mat = Matrix(200f, 112.5f, 100f, 300f)
        pageContent.GSSetMatrix(mat)
        mat.Destroy()
        val docimage: Document.DocImage = document.NewImage(bitmap, true)
        val rimage = page.AddResImage(docimage)
        pageContent.DrawImage(rimage)
        pageContent.GSRestore()
        page.AddContent(pageContent, false)
        pageContent.Destroy()
        page.Close()
        document.Save()*/
    }
    override fun OnPDFLongPressed(pagebo: Int, x: Float, y: Float) {LogUtils.d("D")
    }

    /**
     * call when search finished. each search shall call back each time.
     * @param found
     */
    override fun OnPDFSearchFinished(found: Boolean) {LogUtils.d("D")
    }

    /**
     * call when page displayed on screen.
     * @param canvas
     * @param vpage
     */
    override fun OnPDFPageDisplayed(canvas: Canvas?, vpage: ILayoutView.IVPage?) {LogUtils.d("D")
    }

    /**
     * call when page is rendered by backing thread.
     * @param vpage
     */
    override fun OnPDFPageRendered(vpage: ILayoutView.IVPage?) {

        LogUtils.d("HHHHHHHH")
    }

    override fun onPDFNoteTapped(jstring: String?) {LogUtils.d("D")
    }

    override fun onPDFNoteAdded(annotPoint: String?) {LogUtils.d("D")
    }

    override fun onPDFNoteEdited(annotPoint: String?) {LogUtils.d("D")
    }

    override fun onPDFNoteDeleted(annotPoint: String?) {LogUtils.d("D")
    }

    override fun onButtonPrevPressed() {LogUtils.d("D")
    }

    override fun onButtonNextPressed() {LogUtils.d("D")
    }

}