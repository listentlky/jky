package com.sribs.bdd.v3.ui.check.cd

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Canvas
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.sribs.bdd.R
import com.sribs.bdd.databinding.ActivityCheckComponentDetectionBinding
import com.sribs.bdd.v3.bean.CheckBSMainBean
import com.sribs.bdd.v3.bean.CheckCDMainBean
import com.sribs.bdd.v3.event.RefreshPDFEvent
import com.sribs.bdd.v3.ui.check.cd.fm.*

import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 *    author :
 *    e-mail :
 *    date   : 2022/9/6 11:15
 *    desc   :
 *    version: 1.0
 */
@Route(path = com.sribs.common.ARouterPath.CHECK_COMPONENT_DETECTION_ACTIVITY)
class CheckComponentDetectionActivity : BaseActivity(), ICheckCDContrast.ICheckCDView,
    ILayoutView.PDFLayoutListener, PDFLayoutView.V3SelectDamageTypeCallback {

    private val mBinding: ActivityCheckComponentDetectionBinding by inflate()


    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
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

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID)
    var mRemoteId = ""

    var mCheckCDMainBeanList: ArrayList<CheckCDMainBean>? = ArrayList()

    /**
     * 所有损伤信息集合
     *  pdf图纸
     *  对应损伤集合
     */
    var mDamageBeanList: HashMap<String?, ArrayList<DamageV3Bean>?>? = HashMap()

    /**
     * 当前层图纸数据
     */
    var mCurrentDrawing: DrawingV3Bean? = null

    /**
     * 当前页损伤类型
     */
    var mCurrentDamageType = Arrays.asList("梁", "柱", "墙", "板")

    private val mPresenter by lazy { CheckCDPresenter() }

    private val mFragments by lazy {
        listOf(
            ARouter.getInstance()
                .build(com.sribs.common.ARouterPath.CHECK_COMPONENT_DETECTION_FRAGMENT)
                .navigation() as BaseFragment,
            ARouter.getInstance()
                .build(com.sribs.common.ARouterPath.CHECK_COMPONENT_DETECTION_BEAM_FRAGMENT)
                .navigation() as BaseFragment,
            ARouter.getInstance()
                .build(com.sribs.common.ARouterPath.CHECK_COMPONENT_DETECTION_COLUMN_FRAGMENT)
                .navigation() as BaseFragment,
            ARouter.getInstance()
                .build(com.sribs.common.ARouterPath.CHECK_COMPONENT_DETECTION_WALL_FRAGMENT)
                .navigation() as BaseFragment,
            ARouter.getInstance()
                .build(com.sribs.common.ARouterPath.CHECK_COMPONENT_DETECTION_PLATE_FRAGMENT)
                .navigation() as BaseFragment,
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

    override fun getView(): View = mBinding.root

    override fun initView() {
        bindPresenter()
        initToolbar()
        initViewPager()


        mBinding.checkMenuLayout.checkObdMenuScale.setBackgroundResource(R.mipmap.icon_check_damage_zoom)
        mBinding.checkMenuLayout.checkObdMenuScale.setOnClickListener {
            mBinding.checkMenuLayout.root.visibility = View.GONE
            mBinding.checkVp.currentItem = mScaleDamageIndex
        }
        mBinding.checkMenuLayout.checkObdMenuClose.setOnClickListener {
            mBinding.checkMenuLayout.root.visibility = View.GONE
        }

        Global.Init(this)
        mPresenter.getModuleInfo(mLocalProjectId, mBuildingId, mModuleId, mRemoteId)
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
            if (mBinding.checkVp.currentItem != 0) {
                mBinding.checkVp.currentItem = 0
            } else {
                ExitToSave()
            }
        }

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

    /**
     * 是否改动了
     */
    var mPDFNoteModified: Boolean = false

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

    fun getFileState(): Int {
        return mController?.getFileState() ?: PDFViewController.NOT_MODIFIED
    }


    fun isPDFModifiedNotSaved(): Boolean {
        return (getFileState() == PDFViewController.MODIFIED_NOT_SAVED || mPDFNoteModified)
    }

    private fun initViewPager() {
        mBinding.checkVp.setSmooth(false)
        mBinding.checkVp.setScroll(false)
        mBinding.checkVp.adapter = BasePagerAdapter(supportFragmentManager, mFragments)
        mBinding.checkVp.offscreenPageLimit = 4
        mBinding.checkVp.currentItem = 0
    }


    /**
     * 设置损伤页面详情，并展示
     */
    fun resetDamageInfo(damageV3Bean: DamageV3Bean?, type: String?) {
        if (mBinding.checkMenuLayout.root.visibility == View.VISIBLE) {
            AlertDialog.Builder(this).setTitle("提示")
                .setMessage("当前有缩小详情页，是否移除？")
                .setPositiveButton(R.string.dialog_ok) { dialog, which ->
                    mBinding.checkMenuLayout.root.visibility = View.GONE
                    when (type) {

                        mCurrentDamageType[0] -> { //梁
                              (mFragments[1] as CheckEditCDBFragment).resetView(damageV3Bean)
                            mBinding.checkVp.currentItem = 1
                        }

                        mCurrentDamageType[1] -> { //柱
                            (mFragments[2] as CheckEditCDCFragment).resetView(damageV3Bean)
                            mBinding.checkVp.currentItem = 2
                        }
                        mCurrentDamageType[2] -> { //墙
                            (mFragments[3] as CheckEditCDWFragment).resetView(damageV3Bean)
                            mBinding.checkVp.currentItem = 3
                        }
                        mCurrentDamageType[3] -> { //板
                            (mFragments[4] as CheckEditCDPFragment).resetView(damageV3Bean)
                            mBinding.checkVp.currentItem = 4
                        }

                    }
                }.setNegativeButton(
                    R.string.dialog_cancel
                ) { dialog, which ->

                }
                .show()
        } else {
            when (type) {
                mCurrentDamageType[0] -> { //梁
                    (mFragments[1] as CheckEditCDBFragment).resetView(damageV3Bean)
                    mBinding.checkVp.currentItem = 1
                }
                mCurrentDamageType[1] -> { //柱
                    (mFragments[2] as CheckEditCDCFragment).resetView(damageV3Bean)
                    mBinding.checkVp.currentItem = 2
                }
                mCurrentDamageType[2] -> { //墙
                    (mFragments[3] as CheckEditCDWFragment).resetView(damageV3Bean)
                    mBinding.checkVp.currentItem = 3
                }
                mCurrentDamageType[3] -> { //板
                    (mFragments[4] as CheckEditCDPFragment).resetView(damageV3Bean)
                    mBinding.checkVp.currentItem = 4
                }
            }
        }
    }

    /**
     * 删除某个损伤
     */
    fun removeDamage(damageV3Bean: DamageV3Bean?, type: String?) {
        var exitDamageBeanList = mDamageBeanList!!.get(mCurrentLocalPDF)
        LogUtils.d("删除前损伤数据: " + exitDamageBeanList)

        var mTotalDamageBeanList = exitDamageBeanList!!.filter {
            damageV3Bean!!.createTime != it.createTime
        }
        exitDamageBeanList.clear()
        exitDamageBeanList.addAll(mTotalDamageBeanList)

        mDamageBeanList!!.put(mCurrentLocalPDF, exitDamageBeanList)
        resetDamageList()
    }

    /**
     * 保存损伤到数据库
     */
    fun saveDamage(damageInfo: DamageV3Bean) {

        var exitDamageBeanList = mDamageBeanList!!.get(mCurrentLocalPDF)


        LogUtils.d("当前数据：" + damageInfo)

        LogUtils.d("过滤前损伤数据：" + exitDamageBeanList)
        /**
         * 先过滤相同损伤
         */
        var totleDamageBranList = exitDamageBeanList!!.filter {
            it.createTime != damageInfo.createTime
        }

        LogUtils.d("过滤后损伤数据：" + totleDamageBranList)

        /***
         * 再添加
         */
        exitDamageBeanList.clear()
        exitDamageBeanList.addAll(totleDamageBranList)
        exitDamageBeanList!!.add(damageInfo)

        LogUtils.d("再添加损伤数据：" + totleDamageBranList)

        mDamageBeanList!!.put(mCurrentLocalPDF, exitDamageBeanList)
        resetDamageList()
        mBinding.checkVp.currentItem = 0

    }

    /**
     * 更新损伤数据
     */
    fun saveDamageDrawingToDb() {
        mCheckCDMainBeanList!!.forEach {
            it.drawing!!.forEach { b ->
                b.damage = mDamageBeanList!!.get(b.localAbsPath)!!
            }
            mPresenter.saveDamageToDb(it.drawing!!, it.id!!)
        }
    }

    /**
     * 重置损伤列表和详情
     */
    fun resetDamageList() {
        //添加到损伤模块
        (mFragments[0] as CheckCDFragment).setDamage(mDamageBeanList!!.get(mCurrentLocalPDF))
    }

    /**
     * 选择pdf图片
     */
    fun choosePDF(data: DrawingV3Bean) {
        if (isPDFModifiedNotSaved()) {
            AlertDialog.Builder(this).setTitle("提示")
                .setMessage(R.string.save_pdf_message)
                .setPositiveButton(R.string.dialog_ok) { dialog, which ->
                    mController?.savePDF()
                    mPDFNoteModified = false
                    (mFragments[0] as CheckCDFragment).mBinding.checkSelectIndex.text =
                        data.fileName
                    openPDF(data)
                    resetDamageList()
                }.setNegativeButton(
                    R.string.dialog_cancel
                ) { dialog, which ->
                    mPDFNoteModified = false
                    (mFragments[0] as CheckCDFragment).mBinding.checkSelectIndex.text =
                        data.fileName
                    openPDF(data)
                    resetDamageList()
                }
                .show()
        } else {
            (mFragments[0] as CheckCDFragment).mBinding.checkSelectIndex.text = data.fileName
            openPDF(data)
            resetDamageList()
        }
    }

    fun openPDF(drawingV3Bean: DrawingV3Bean) {
        var pdfPath = drawingV3Bean.localAbsPath!!
        if (!pdfPath.endsWith("pdf") &&
            !pdfPath.endsWith("PDF")
        ) {
            showToast("该图纸类型不是pdf，无法加载")
            return
        }
        this.mView = (mFragments[0] as CheckCDFragment).getPDFView()
        mView!!.setV3Version(true)
        this.mViewParent = (mFragments[0] as CheckCDFragment).getPDFParentView()
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
                mView?.setAnnotMenu(UIAnnotMenu(mViewParent))
                mView?.setV3SelectDamageCallback(this)
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

    var mScaleDamageIndex = 0

    /**
     * 当前缩小损伤页面
     */
    fun scaleDamageInfo(index: Int) {
        mScaleDamageIndex = index
        mBinding.checkMenuLayout.root.visibility = View.VISIBLE
        mBinding.checkVp.currentItem = 0
    }

    /**
     * 退出提示保存框
     */
    private fun ExitToSave() {
        AlertDialog.Builder(this).setTitle(getString(R.string.drawing_edit_exit_dialog_title))
            .setMessage(R.string.is_save_hint)
            .setPositiveButton(R.string.dialog_ok) { dialog, which ->
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_check, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        var item = menu?.findItem(R.id.menu_check_pop)

        item?.icon?.setBounds(20, 50, 0, 0)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item?.itemId) {
            R.id.menu_check_pop -> { // 菜单111
                setVpCurrentItem(0)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun setVpCurrentItem(item: Int) {
        mBinding.checkVp.currentItem = item
    }

    override fun onModuleInfo(checkMainBean: List<CheckCDMainBean>) {
        this.mCheckCDMainBeanList!!.clear()
        mCheckCDMainBeanList!!.addAll(checkMainBean)
        LogUtils.d("回调到view层数据")

        /**
         * 初始化并加载第一张图纸
         */
        mCurrentDrawing = checkMainBean[0].drawing!![0]
        if (mCurrentDrawing != null) {
            openPDF(mCurrentDrawing!!)
        }

        /**
         * 初始化损伤列表
         *
         */

        checkMainBean.forEach { a ->
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
        (mFragments[0] as CheckCDFragment).initFloorDrawData(checkMainBean)
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

    override fun onSelect(type: String?) {

    }

    /**
     * call when page changed.
     * @param pageno
     */
    override fun OnPDFPageModified(pageno: Int) {
        mPDFNoteModified = true
        mController?.onPageModified(pageno)
    }

    /**
     * call when page scrolling.
     * @param pageno
     */
    override fun OnPDFPageChanged(pageno: Int) {
        LogUtils.d("OnPDFPageChanged")
        m_cur_page = pageno
        if (mController != null) mController!!.OnPageChanged(pageno)
        RadaeePluginCallback.getInstance().didChangePage(pageno)
    }

    /**
     * call when annotation tapped.
     * @param pno
     * @param annot
     */
    override fun OnPDFAnnotTapped(pno: Int, annot: Page.Annotation?) {
        LogUtils.d("OnPDFAnnotTapped")
        if (annot != null) {
            RadaeePluginCallback.getInstance().onAnnotTapped(annot)
            if (!mView!!.PDFCanSave() && annot.GetType() != 2) return
        }
        if (mController != null) mController!!.OnAnnotTapped(pno, annot)
    }

    /**
     * call when blank tapped on page, this mean not annotation tapped.
     */
    override fun OnPDFBlankTapped(pagebo: Int) {
        LogUtils.d("OnPDFBlankTapped")
        if (mController != null)
            mController!!.OnBlankTapped()
        RadaeePluginCallback.getInstance().onBlankTapped(pagebo)
    }

    /**
     * call select status end.
     * @param text selected text string
     */
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
        resetDamageInfo(null, damageBean.type)
        when (damageBean.type) {
            mCurrentDamageType[0] -> {
                mBinding.checkVp.currentItem = 1
            }
            mCurrentDamageType[1] -> {
                mBinding.checkVp.currentItem = 2
            }
            mCurrentDamageType[2] -> {
                mBinding.checkVp.currentItem = 3
            }
            mCurrentDamageType[3] -> {
                mBinding.checkVp.currentItem = 4
            }
        }
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
                        resetDamageInfo(it, it.type)
                        when (it.type) {
                            mCurrentDamageType[0] -> {
                                mBinding.checkVp.currentItem = 1
                            }
                            mCurrentDamageType[1] -> {
                                mBinding.checkVp.currentItem = 2
                            }
                            mCurrentDamageType[2] -> {
                                mBinding.checkVp.currentItem = 3
                            }
                            mCurrentDamageType[3] -> {
                                mBinding.checkVp.currentItem = 4
                            }
                        }
                    }
                }
                if (!isMatch) {
                    resetDamageInfo(null, damageBean.type)
                    when (damageBean.type) {
                        mCurrentDamageType[0] -> {
                            mBinding.checkVp.currentItem = 1
                        }
                        mCurrentDamageType[1] -> {
                            mBinding.checkVp.currentItem = 2
                        }
                        mCurrentDamageType[2] -> {
                            mBinding.checkVp.currentItem = 3
                        }
                        mCurrentDamageType[3] -> {
                            mBinding.checkVp.currentItem = 4
                        }
                    }
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