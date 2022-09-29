package com.sribs.bdd.v3.ui.check.rhd

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Canvas
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.rxbus.RxBus
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.adapter.BasePagerAdapter
import com.cbj.sdk.libui.mvp.inflate
import com.google.gson.Gson
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.radaee.annotui.UIAnnotMenu
import com.radaee.constant.Constant
import com.radaee.pdf.Document
import com.radaee.pdf.Global
import com.radaee.pdf.Page
import com.radaee.reader.PDFLayoutView
import com.radaee.reader.PDFPagesAct
import com.radaee.reader.PDFViewController
import com.radaee.util.PDFAssetStream
import com.radaee.util.PDFHttpStream
import com.radaee.util.RadaeePluginCallback
import com.radaee.view.ILayoutView
import com.radaee.view.RHDiffAddPointPopupWindow
import com.sribs.bdd.R
import com.sribs.bdd.databinding.ActivityRelativeHdiffBinding
import com.sribs.bdd.v3.bean.CheckHDiffMainBean
import com.sribs.bdd.v3.dialog.checkRHDDialog
import com.sribs.bdd.v3.event.RefreshPDFEvent
import com.sribs.bdd.v3.ui.check.rhd.fm.RelativeHDiffFragment
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 相对高差
 */
@Route(path = com.sribs.common.ARouterPath.CHECK_RELATIVE_H_DIFF_ACTIVITY)
class RelativeHDiffActivity : BaseActivity() ,ICheckRHDiffContrast.ICheckRHDiffView,
    ILayoutView.PDFLayoutListener {

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_PROJECT_ID)
    var mLocalProjectId = -1L

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_MODULE_ID)
    var mModuleId = -1L

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_BUILDING_ID)
    var mBuildingId = -1L

    private val mBinding: ActivityRelativeHdiffBinding by inflate()

    private val mPresenter by lazy { CheckRHDiffPresenter() }

    private val mFragments by lazy {
        listOf(
            ARouter.getInstance()
                .build(com.sribs.common.ARouterPath.CHECK_RELATIVE_H_DIFF_FRAGMENT)
                .navigation() as BaseFragment,
        )
    }

    override fun deinitView() {
        unbindPresenter()
    }

    override fun dispose(): Unit? {
        mFragments.forEach {
            it.dispose()
            it.deinitView()
        }
        return super.dispose()
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        bindPresenter()
        initToolbar()
        initViewPager()
        Global.Init(this)
        mPresenter.getModuleInfo(mLocalProjectId,mBuildingId,mModuleId)
        RxBus.getDefault().toObservable<RefreshPDFEvent>(RefreshPDFEvent::class.java)
            .subscribe {
                if (it.isRefresh) {
                    openPDF(mCurrentDrawing!!)
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
            if(mBinding.checkVp.currentItem != 0){
                mBinding.checkVp.currentItem = 0
            }else{
                ExitToSave()
            }
        }
    }

    /**
     * 跳转编辑页
     */
    fun startFabPDF() {
        if (mCurrentLocalPDF.isNullOrEmpty()) {
            showToast("当前图纸不可用")
            return
        }
        if (mPDFNoteModified) {
            AlertDialog.Builder(this).setTitle("提示")
                .setMessage(R.string.save_pdf_message)
                .setPositiveButton(R.string.dialog_ok) { dialog, which ->
                    mController?.savePDF()
                    mPDFNoteModified = false
                    ARouter.getInstance().build(ARouterPath.DRAW_PDF_ACTIVITY)
                        .withString(ARouterPath.VAL_COMMON_LOCAL_CURRENT_PDF, mCurrentLocalPDF)
                        .withString(ARouterPath.VAL_COMMON_TITLE, mTitle)
                        .navigation()
                }.setNegativeButton(
                    R.string.dialog_cancel
                ) { dialog, which ->
                    ARouter.getInstance().build(ARouterPath.DRAW_PDF_ACTIVITY)
                        .withString(ARouterPath.VAL_COMMON_LOCAL_CURRENT_PDF, mCurrentLocalPDF)
                        .withString(ARouterPath.VAL_COMMON_TITLE, mTitle)
                        .navigation()
                }
                .show()
            return
        }
        ARouter.getInstance().build(ARouterPath.DRAW_PDF_ACTIVITY)
            .withString(ARouterPath.VAL_COMMON_LOCAL_CURRENT_PDF, mCurrentLocalPDF)
            .withString(ARouterPath.VAL_COMMON_TITLE, mTitle)
            .navigation()
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

    fun showEditDialog(){
        checkRHDDialog(this).show()
    }

    var mCheckHDiffMainBean:ArrayList<CheckHDiffMainBean>? = ArrayList()

    /**
     * 当前层图纸数据
     */
    var mCurrentDrawing: DrawingV3Bean? = null

    /**
     * 所有损伤信息集合
     *  pdf图纸
     *  对应损伤集合
     */
    var mDamageBeanList: HashMap<String?, ArrayList<DamageV3Bean>?>? = HashMap()

    /**
     * 回调view层数据
     */
    override fun onModuleInfo(checkHDiffMainBean: List<CheckHDiffMainBean>) {
        this.mCheckHDiffMainBean!!.clear()
        mCheckHDiffMainBean!!.addAll(checkHDiffMainBean)

        /**
         * 初始化并加载第一张图纸
         */
        mCurrentDrawing = checkHDiffMainBean[0].drawing!![0]
        if (mCurrentDrawing != null) {
            openPDF(mCurrentDrawing!!)
        }

        /**
         * 初始化损伤列表
         *
         */

        checkHDiffMainBean.forEach { a ->
            a.drawing!!.forEach { b ->
                mDamageBeanList!!.put(
                    b.localAbsPath,
                    if (b.damage.isNullOrEmpty()) ArrayList() else b.damage
                )
            }
        }

        LogUtils.d("损伤列表详情: " + mDamageBeanList)

        resetDamageList()

        /**
         * 初始化选择窗图纸列表
         */
        (mFragments[0] as RelativeHDiffFragment).initFloorDrawData(checkHDiffMainBean)

    }

    /**
     * 保存当前图纸损伤
     */
    fun saveDamage(){
        var damage = DamageV3Bean(
            -1,
            mCurrentDrawing!!.drawingID,
            "相对高差测量",
            0,
            -1,
            (mFragments[0] as RelativeHDiffFragment).mBinding.checkBzEdit.text.toString(),
            System.currentTimeMillis(),
            (mFragments[0] as RelativeHDiffFragment).mBinding.checkTableInfo.tableInfo,
            (mFragments[0] as RelativeHDiffFragment).mBinding.checkTableInfo.pointBean
        )

        var exitDamageBeanList = mDamageBeanList!!.get(mCurrentLocalPDF)
        if(exitDamageBeanList == null){
            exitDamageBeanList = ArrayList()
        }else{
            exitDamageBeanList.clear()
        }
        exitDamageBeanList.add(damage)
        mDamageBeanList!!.put(mCurrentLocalPDF, exitDamageBeanList)

        LogUtils.d("saveDamage： "+mDamageBeanList)
    }

    /**
     * 更新损伤数据
     */
    fun saveDamageDrawingToDb() {
        mCheckHDiffMainBean!!.forEach {
            it.drawing!!.forEach { b->
                if(mDamageBeanList!!.size>0){
                    b.damage = mDamageBeanList?.get(b.localAbsPath)!!
                }else{
                    b.damage = ArrayList()
                }
            }
            mPresenter.saveDamageToDb(it.drawing!!,it.moduleId!!)
        }
    }

    /**
     * 重置损伤列表和详情
     */
    fun resetDamageList() {
        //添加到损伤模块
        LogUtils.d("mCurrentLocalPDF: "+mCurrentLocalPDF)
        (mFragments[0] as RelativeHDiffFragment).resetView(mDamageBeanList!!.get(mCurrentLocalPDF))
    }

    /**
     * 选择pdf图片
     */
    fun choosePDF(data: DrawingV3Bean) {
        LogUtils.d("choosePDF: "+data)
        saveDamage()
        mController?.savePDF()
        (mFragments[0] as RelativeHDiffFragment).mBinding.checkSelectIndex.text =
            data.fileName
        openPDF(data)
        resetDamageList()
    }

    /**
     * 退出提示保存框
     */
    private fun ExitToSave() {
        AlertDialog.Builder(this).setTitle(getString(R.string.drawing_edit_exit_dialog_title))
            .setMessage(R.string.is_save_hint)
            .setPositiveButton(R.string.dialog_ok) { dialog, which ->
                saveDamage()
                mController?.savePDF()
                saveDamageDrawingToDb();
                finish()
            }.setNegativeButton(
                R.string.dialog_cancel
            ) { dialog, which ->
                finish()
            }
            .show()
    }

    private var mDoc: Document = Document()

    private var mAssetStream: PDFAssetStream? = PDFAssetStream()

    private var mHttpStream: PDFHttpStream? = null

    private var mController: PDFViewController? = null

    private var m_cur_page = 0

    private var mView: PDFLayoutView? = null

    private var mViewParent: RelativeLayout? = null
    /**
     * 当前编辑的pdf
     */
    var mCurrentLocalPDF = ""

    fun openPDF(drawingV3Bean: DrawingV3Bean) {
        var pdfPath = drawingV3Bean.localAbsPath!!
        if (!pdfPath.endsWith("pdf") &&
            !pdfPath.endsWith("PDF")
        ) {
            showToast("该图纸类型不是pdf，无法加载")
            return
        }
        this.mView = (mFragments[0] as RelativeHDiffFragment).getPDFView()
        mView!!.setV3Version(true)
        mView!!.setCurrentModuleType(mView!!.mModuleType.get(2))
        this.mViewParent = (mFragments[0] as RelativeHDiffFragment).getPDFParentView()
        this.mCurrentLocalPDF = pdfPath
        this.mCurrentDrawing = drawingV3Bean
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
     /*           mView?.setAnnotMenu(UIAnnotMenu(mViewParent))*/
                mController = PDFViewController(
                    mViewParent,
                    mView,
                    pdfPath,
                    mAssetStream != null || mHttpStream != null
                )
                mController!!.SetPagesListener(View.OnClickListener {
                    val intent = Intent()
                    intent.setClass(this, PDFPagesAct::class.java)
                    PDFPagesAct.SetTranDoc(mDoc)
                    startActivityForResult(intent, 10000)
                })
            }, {
                LogUtils.d(" not granted ${Permission.MANAGE_EXTERNAL_STORAGE}")
            })
    }

    /**
     * 是否改动了
     */
    var mPDFNoteModified: Boolean = false


    fun getFileState(): Int {
        return mController?.getFileState() ?: PDFViewController.NOT_MODIFIED
    }


    fun isPDFModifiedNotSaved(): Boolean {
        return (getFileState() == PDFViewController.MODIFIED_NOT_SAVED || mPDFNoteModified)
    }

    override fun bindPresenter() {
        mPresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg)
    }

    override fun unbindPresenter() {
        mPresenter.unbindView()
    }

    override fun OnPDFPageModified(pageno: Int) {
        mPDFNoteModified = true
        mController?.onPageModified(pageno)
    }

    override fun OnPDFPageChanged(pageno: Int) {
        LogUtils.d("OnPDFPageChanged")
        m_cur_page = pageno
        if (mController != null) mController!!.OnPageChanged(pageno)
        RadaeePluginCallback.getInstance().didChangePage(pageno)
    }

    var rHDiffAddPointPopupWindow: RHDiffAddPointPopupWindow? = null

    override fun OnPDFAnnotTapped(pno: Int, annot: Page.Annotation?) {
        LogUtils.d("OnPDFAnnotTapped")
        if (annot != null) {
            LogUtils.d("OnPDFAnnotTapped annot: "+annot!!.GetRef()+" ; "+annot.GetRect()[0]+" "+annot.GetRect()[1]+" "+annot.GetRect()[2]+" "+annot.GetRect()[3])

            LogUtils.d("getCurrentRect: "+mView!!.currentRect[0]+" ; "+mView!!.currentRect[1]+" ; "+mView!!.currentRect[2]+" ; "+mView!!.currentRect[3])

            var pointBean = (mFragments[0] as RelativeHDiffFragment).getPointBean()
            var group:String?= null
            var pointName:String? = null
            LogUtils.d("annot： "+annot.GetRef())
            LogUtils.d("pointBean： "+pointBean)
            pointBean.forEach {
                it.menu.forEach { b->
                    if(b.annotRef == annot.GetRef()){
                        group = it.name
                        pointName = b.name
                    }
                }
            }
            LogUtils.d("group： ${group}  ${pointName}")
            if(rHDiffAddPointPopupWindow == null){
                rHDiffAddPointPopupWindow = RHDiffAddPointPopupWindow(this,getResources()
                    .getDimensionPixelOffset(R.dimen._200sdp),annot.GetRef(),object :RHDiffAddPointPopupWindow.RHDiffAddPointCallback{
                    override fun onAddPoint(group: String, point: String,annotRef: Long) {
                        LogUtils.d("group： ${group}  ${pointName}  ${annotRef}")
                        (mFragments[0] as RelativeHDiffFragment).addPointBean(group,point,annotRef)
                    }

                    override fun onDeletePoint(group: String, point: String,annotRef: Long) {
                        mView?.PDFRemoveAnnot()
                        (mFragments[0] as RelativeHDiffFragment).removePointBean(group,point,annotRef)
                    }

                })
                if(!group.isNullOrEmpty() && !pointName.isNullOrEmpty()){
                    rHDiffAddPointPopupWindow!!.setInfo(group,pointName,annot.GetRef())
                }
            }else{
                if(!group.isNullOrEmpty() && !pointName.isNullOrEmpty()){
                    rHDiffAddPointPopupWindow!!.setInfo(group,pointName,annot.GetRef())
                }else{
                    rHDiffAddPointPopupWindow!!.setInfo("","",annot.GetRef())
                }
            }
            rHDiffAddPointPopupWindow!!.showAtLocation(mViewParent,Gravity.NO_GRAVITY,calculateX(mView!!.currentRect).toInt(),calculateY(mView!!.currentRect).toInt())
            RadaeePluginCallback.getInstance().onAnnotTapped(annot)
            if (!mView!!.PDFCanSave() && annot.GetType() != 2) return
        }
        if (mController != null) mController!!.OnAnnotTapped(pno, annot)
    }

    private fun calculateX(annot_rect: FloatArray): Float {
        val screenWidth: Float =
            getResources().getDisplayMetrics().widthPixels.toFloat()
        val annotWidth = annot_rect[2] - annot_rect[0]
        val menuWidth: Float = getResources()
            .getDimensionPixelOffset(R.dimen._200sdp).toFloat()
        var x = annot_rect[0] + (annotWidth - menuWidth) / 2
        if (x + menuWidth >= screenWidth) x -= x + menuWidth - screenWidth +
            getResources()
            .getDimensionPixelOffset(com.radaee.viewlib.R.dimen.annot_menu_bar_margin) else if (x <= 0) x =
            getResources()
                .getDimensionPixelOffset(com.radaee.viewlib.R.dimen.annot_menu_bar_margin).toFloat()
        return x
    }

    private fun calculateY(annot_rect: FloatArray): Float {
        var y: Float
        y = annot_rect[1]- getResources().getDimensionPixelOffset(R.dimen._10sdp)
        if (y < 0) {
            y = annot_rect[3] + getResources()
                .getDimensionPixelOffset(R.dimen._10sdp)
        }
        return y
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
    }

    override fun OnPDFSearchFinished(found: Boolean) {
        LogUtils.d("OnPDFSearchFinished")
    }

    override fun OnPDFPageDisplayed(canvas: Canvas?, vpage: ILayoutView.IVPage?) {
        LogUtils.d("OnPDFPageDisplayed")
    }

    private var mDidShowReader = false
    override fun OnPDFPageRendered(vpage: ILayoutView.IVPage?) {
        LogUtils.d("OnPDFPageRendered" + vpage)
        if (!mDidShowReader) {
            RadaeePluginCallback.getInstance().didShowReader()
            mDidShowReader = true
        }
    }

    override fun onPDFNoteTapped(jstring: String?) {
        LogUtils.d("onPDFNoteTapped")
    }

    /**
     * 当前mark
     */
    var mCurrentAddAnnotReF = -1L

    fun setAddAnnotReF(addAnnotReF: Long) {
        mCurrentAddAnnotReF = addAnnotReF
    }

    override fun onPDFNoteAdded(annotPoint: String?) {
        LogUtils.d("onPDFNoteAdded " + annotPoint)
        var damageBean = Gson().fromJson(annotPoint, DamageV3Bean::class.java)
        mCurrentAddAnnotReF = damageBean.annotRef
      /*  resetDamageInfo(null, damageBean.type)
        when (damageBean.type) {
            mCurrentDamageType[0] -> {
                mBinding.checkVp.currentItem = 1
            }
        }*/
    }

    override fun onPDFNoteEdited(annotPoint: String?) {
        LogUtils.d("onPDFNoteEdited " + annotPoint)
        // 添加mark返回的信息，需保存 annotRef
        var damageBean: DamageV3Bean = Gson().fromJson(annotPoint!!, DamageV3Bean::class.java)
        when (damageBean.action) {
            Constant.BUTTON_POPMENU_EDIT -> {
                /**
                 * 查询标记对应的损伤信息,查询到就设置上 进入编辑页面
                 */
                var isMatch = false
                mDamageBeanList!!.get(mCurrentLocalPDF)!!.forEach {
                    if (damageBean.annotRef == it.annotRef) {
                        isMatch = true
                       /* resetDamageInfo(it, it.type)
                        when (it.type) {
                            mCurrentDamageType[0] -> {
                                mBinding.checkVp.currentItem = 1
                            }
                        }*/
                    }
                }
                if(!isMatch){
                 //   resetDamageInfo(null, mCurrentDamageType[0])
                    mBinding.checkVp.currentItem = 1
                }
            }
            Constant.BUTTON_POPMENU_DEL -> {
                /**
                 * 删除标记对应的损伤信息
                 */
                var exitDamageBeanList = mDamageBeanList!!.get(mCurrentLocalPDF)
                LogUtils.d("删除前损伤数据: " + exitDamageBeanList)

                var mTotalDamageBeanList = exitDamageBeanList!!.filter {
                    damageBean.annotRef != it.annotRef
                }
                exitDamageBeanList.clear()
                exitDamageBeanList.addAll(mTotalDamageBeanList)

                mDamageBeanList!!.put(mCurrentLocalPDF, exitDamageBeanList)
                resetDamageList()
            }
        }
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
}