package com.sribs.bdd.v3.ui.check.bs

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
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
import com.sribs.bdd.databinding.ActivityCheckBuildStructureBinding
import com.sribs.bdd.v3.bean.CheckBSMainBean
import com.sribs.bdd.v3.event.RefreshPDFEvent
import com.sribs.bdd.v3.ui.check.bs.fm.CheckBSFloorFragment
import com.sribs.bdd.v3.ui.check.bs.fm.CheckBSFragment
import com.sribs.bdd.v3.ui.check.bs.fm.CheckBSGridFragment
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * ??????????????????
 */
@Route(path = com.sribs.common.ARouterPath.CHECK_BUILD_STRUCTURE_ACTIVITY)
class CheckBuildStructureActivity : BaseActivity(), ICheckBSContrast.ICheckBSView,
    ILayoutView.PDFLayoutListener, PDFLayoutView.V3SelectDamageTypeCallback {

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

    /**
     * ?????????????????????
     */
    var mCurrentDamageType = Arrays.asList("??????", "??????")

    /**
     * ??????????????????
     */
    var mIsUpdateData:Boolean = false

    private val mPresenter by lazy { CheckBSPresenter() }

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
        unbindPresenter()
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
            cancelDamageMark()
        }
        mPresenter.getModuleInfo(mLocalProjectId, mBuildingId, mModuleId, mRemoteId)
        Global.Init(this)
        RxBus.getDefault().toObservable<RefreshPDFEvent>(RefreshPDFEvent::class.java)
            .subscribe {
                if (it.isRefresh) {
                    openPDF(mCurrentDrawing!!)
                }
            }
    }

    /**
     * @Description ?????????toolbar
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
                cancelDamageMark()
            } else {
                exitToSave()
            }
        }
    }

    /**
     * ???????????????
     */
    fun startFabPDF() {
        if (mCurrentLocalPDF.isNullOrEmpty()) {
            showToast("?????????????????????")
            return
        }
        if (mPDFNoteModified) {
            AlertDialog.Builder(this).setTitle("??????")
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_damage_save -> { // ??????
                AlertDialog.Builder(this).setTitle("??????")
                    .setMessage(R.string.is_save_hint)
                    .setPositiveButton(R.string.dialog_ok) { dialog, which ->
                        mController?.savePDF()
                        saveDamageDrawingToDb();
                    }.setNegativeButton(
                        R.string.dialog_cancel
                    ) { dialog, which ->
                    }
                    .show()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun setVpCurrentItem(item: Int) {
        mBinding.checkVp.currentItem = item
        if(item == 0){
            cancelDamageMark()
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
     * ???????????????pdf
     */
    var mCurrentLocalPDF = ""

    fun openPDF(drawingV3Bean: DrawingV3Bean) {
        var pdfPath = drawingV3Bean.localAbsPath!!
        if (!pdfPath.endsWith("pdf") &&
            !pdfPath.endsWith("PDF")
        ) {
            showToast("?????????????????????pdf???????????????")
            return
        }
        this.mView = (mFragments[0] as CheckBSFragment).getPDFView()
        mView!!.setV3Version(true)
        this.mViewParent = (mFragments[0] as CheckBSFragment).getPDFParentView()
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
                if (mController == null) {
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
                }
            }, {
                LogUtils.d(" not granted ${Permission.MANAGE_EXTERNAL_STORAGE}")
            })
    }

    /**
     * ???????????????
     */
    var mPDFNoteModified: Boolean = false


    fun getFileState(): Int {
        return mController?.getFileState() ?: PDFViewController.NOT_MODIFIED
    }


    fun isPDFModifiedNotSaved(): Boolean {
        return (getFileState() == PDFViewController.MODIFIED_NOT_SAVED || mPDFNoteModified)
    }

    override fun onBackPressed() {
        exitToSave()
    }

    /**
     * ?????????????????????
     */
    private fun exitToSave() {
        if(mIsUpdateData) {
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
        }else{
            finish()
        }
    }

    var mCheckBSMainBeanList: ArrayList<CheckBSMainBean>? = ArrayList()

    /**
     * ????????????????????????
     *  pdf??????
     *  ??????????????????
     */
    var mDamageBeanList: HashMap<String?, ArrayList<DamageV3Bean>?>? = HashMap()

    /**
     * ?????????????????????
     */
    var mCurrentDrawing: DrawingV3Bean? = null

    var isRun = true

    /**
     * ????????????
     */
    override fun onModuleInfo(checkMainBean: List<CheckBSMainBean>) {
        this.mCheckBSMainBeanList!!.clear()
        mCheckBSMainBeanList!!.addAll(checkMainBean)
        LogUtils.d("?????????view?????????")

        /**
         * ????????????????????????
         */
        mCurrentDrawing = checkMainBean[0].drawing!![0]

        /**
         * ?????????????????????
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

        LogUtils.d("??????????????????: " + mDamageBeanList)

        /**
         * fm????????????activity ????????????
         */
        Timer().schedule(object :TimerTask(){
            override fun run() {
                while (isRun){
                    if((mFragments[0] as CheckBSFragment).mIsViewCreated){
                        LogUtils.d("fm??????????????? ????????????")
                        isRun = false

                        runOnUiThread {
                            /**
                             * ?????????????????????
                             */
                            if (mCurrentDrawing != null) {
                                openPDF(mCurrentDrawing!!)
                            }
                            /**
                             * ?????????????????????
                             *
                             */
                            resetDamageList()

                            /**
                             * ??????????????????????????????
                             */
                            (mFragments[0] as CheckBSFragment).initFloorDrawData(checkMainBean)
                        }

                    }else{
                        LogUtils.d("fm???????????? ??????10????????????")
                    }
                }
            }

        },10)
    }

    /**
     * ????????????????????????????????????
     */
    fun resetDamageInfo(damageV3Bean: DamageV3Bean?, type: String?,isAddDamageMark:Boolean,isEditDamageMark:Boolean) {
        LogUtils.d("resetDamageInfo??? " + damageV3Bean + " ; type:" + type)
      /*  if (mBinding.checkMenuLayout.root.visibility == View.VISIBLE) {
            AlertDialog.Builder(this).setTitle("??????")
                .setMessage("??????????????????????????????????????????")
                .setPositiveButton(R.string.dialog_ok) { dialog, which ->
                    mBinding.checkMenuLayout.root.visibility = View.GONE
                    when (type) {
                        mCurrentDamageType[0] -> { // ??????
                            (mFragments[1] as CheckBSFloorFragment).resetView(damageV3Bean)
                            mBinding.checkVp.currentItem = 1
                            addPDFDamageMark = isAddDamageMark
                            isEditDamage = isEditDamageMark
                        }
                        mCurrentDamageType[1] -> { //??????
                            (mFragments[2] as CheckBSGridFragment).resetView(damageV3Bean)
                            mBinding.checkVp.currentItem = 2
                            addPDFDamageMark = isAddDamageMark
                            isEditDamage = isEditDamageMark
                        }
                    }
                }.setNegativeButton(
                    R.string.dialog_cancel
                ) { dialog, which ->

                }
                .show()
        } else {*/
        if (mBinding.checkMenuLayout.root.visibility == View.VISIBLE) {
            mBinding.checkMenuLayout.root.visibility = View.GONE
        }
            when (type) {
                mCurrentDamageType[0] -> { // ??????
                    (mFragments[1] as CheckBSFloorFragment).resetView(damageV3Bean)
                    mBinding.checkVp.currentItem = 1
                    addPDFDamageMark = isAddDamageMark
                    isEditDamage = isEditDamageMark
                }
                mCurrentDamageType[1] -> { //??????
                    (mFragments[2] as CheckBSGridFragment).resetView(damageV3Bean)
                    mBinding.checkVp.currentItem = 2
                    addPDFDamageMark = isAddDamageMark
                    isEditDamage = isEditDamageMark
                }
            }
     //   }
    }

    /**
     * ??????????????????
     */
    fun removeDamage(damageV3Bean: DamageV3Bean?, type: String?) {
        var exitDamageBeanList = mDamageBeanList!!.get(mCurrentLocalPDF)
        LogUtils.d("?????????????????????: " + exitDamageBeanList)

        var mTotalDamageBeanList = exitDamageBeanList!!.filter {
            damageV3Bean!!.createTime != it.createTime
        }
        exitDamageBeanList.clear()
        exitDamageBeanList.addAll(mTotalDamageBeanList)

        mDamageBeanList!!.put(mCurrentLocalPDF, exitDamageBeanList)
        resetDamageList()
        /* try {
             mView?.removeCurrentNone(damageV3Bean!!.annotX,damageV3Bean!!.annotY)
         }catch (e:Exception){

         }*/
    }

    /**
     * ????????????????????????
     */
    fun saveDamage(damageInfo: DamageV3Bean) {

        var exitDamageBeanList = mDamageBeanList!!.get(mCurrentLocalPDF)


        LogUtils.d("???????????????" + damageInfo)
        if (exitDamageBeanList == null) {
            exitDamageBeanList = ArrayList()
        }
        LogUtils.d("????????????????????????" + exitDamageBeanList)

        /**
         * ?????????????????????
         */
        var totleDamageBranList = exitDamageBeanList!!.filter {
            it.createTime != damageInfo.createTime
        }

        LogUtils.d("????????????????????????" + totleDamageBranList)

        /***
         * ?????????
         */
        exitDamageBeanList.clear()
        exitDamageBeanList.addAll(totleDamageBranList)
        exitDamageBeanList!!.add(damageInfo)

        LogUtils.d("????????????????????????" + totleDamageBranList)

        mDamageBeanList!!.put(mCurrentLocalPDF, exitDamageBeanList)
        resetDamageList()
        mBinding.checkVp.currentItem = 0

        if(addPDFDamageMark) {
            if(isEditDamage) {
                mView?.PDFRemoveAnnot()
            }
            addDamageMark(damageInfo)
        }
        mIsUpdateData = true
    }

    /**
     * ??????????????????
     */
    fun saveDamageDrawingToDb() {
        mCheckBSMainBeanList?.forEach {
            it.drawing?.forEach { b ->
                b.damage = mDamageBeanList?.get(b.localAbsPath)
            }
            mPresenter.saveDamageToDb(it)
        }
        mIsUpdateData = false
    }

    /**
     * ???????????????????????????
     */
    fun resetDamageList() {
        //?????????????????????
        (mFragments[0] as CheckBSFragment).setDamage(mDamageBeanList!!.get(mCurrentLocalPDF))
    }

    /**
     * ??????pdf??????
     */
    fun choosePDF(data: DrawingV3Bean) {
        mController?.savePDF()
        (mFragments[0] as CheckBSFragment).mBinding.checkSelectIndex.text =
            data.fileName
        openPDF(data)
        resetDamageList()
       /*
        if (isPDFModifiedNotSaved()) {
            AlertDialog.Builder(this).setTitle("??????")
                .setMessage(R.string.save_pdf_message)
                .setPositiveButton(R.string.dialog_ok) { dialog, which ->
                    mController?.savePDF()
                    mPDFNoteModified = false
                    (mFragments[0] as CheckBSFragment).mBinding.checkSelectIndex.text =
                        data.fileName
                    openPDF(data)
                    resetDamageList()
                }.setNegativeButton(
                    R.string.dialog_cancel
                ) { dialog, which ->
                    mPDFNoteModified = false
                    (mFragments[0] as CheckBSFragment).mBinding.checkSelectIndex.text =
                        data.fileName
                    openPDF(data)
                    resetDamageList()
                }
                .show()
        } else {
            (mFragments[0] as CheckBSFragment).mBinding.checkSelectIndex.text = data.fileName
            openPDF(data)
            resetDamageList()
        }*/
    }

    var mScaleDamageIndex = 0

    /**
     * ????????????????????????
     */
    fun scaleDamageInfo(index: Int) {
        mScaleDamageIndex = index
        mBinding.checkMenuLayout.root.visibility = View.VISIBLE
        mBinding.checkVp.currentItem = 0
        cancelDamageMark()
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

    override fun OnPDFAnnotTapped(pno: Int, annot: Page.Annotation?) {
        LogUtils.d("OnPDFAnnotTapped")
        if (annot != null) {
            RadaeePluginCallback.getInstance().onAnnotTapped(annot)
            if (!mView!!.PDFCanSave() && annot.GetType() != 2) return
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
     * ??????mark
     */
    var mCurrentAddAnnotReF = -1L

    var mCurrentAddAnnotX = 0

    var mCurrentAddAnnotY = 0

    fun setAddAnnotReF(addAnnotReF: Long) {
        mCurrentAddAnnotReF = addAnnotReF
    }

    override fun onPDFNoteAdded(annotPoint: String?) {
        LogUtils.d("onPDFNoteAdded " + annotPoint)
      /*  var damageBean = Gson().fromJson(annotPoint, DamageV3Bean::class.java)
        mCurrentAddAnnotReF = damageBean.annotRef
        mCurrentAddAnnotX = damageBean.annotX
        mCurrentAddAnnotY = damageBean.annotY
        resetDamageInfo(null, damageBean.type)
        when (damageBean.type) {
            mCurrentDamageType[0] -> {
                mBinding.checkVp.currentItem = 1
            }
            mCurrentDamageType[1] -> {
                mBinding.checkVp.currentItem = 2
            }
        }*/
    }

    override fun onPDFNoteEdited(annotPoint: String?) {
        LogUtils.d("onPDFNoteEdited " + annotPoint)
        // ??????mark??????????????????????????? annotRef
        var damageBean: DamageV3Bean = Gson().fromJson(annotPoint!!, DamageV3Bean::class.java)
        when (damageBean.action) {
            Constant.BUTTON_POPMENU_EDIT -> {
                /**
                 * ?????????????????????????????????,????????????????????? ??????????????????
                 */
                var isMatch = false
                mDamageBeanList!!.get(mCurrentLocalPDF)!!.forEach {
                    LogUtils.d("????????????: "+(it.type+it.createTime))
                    if (damageBean.annotName == (it.type+it.createTime)) {
                        LogUtils.d("????????????: "+damageBean.annotName)
                        isMatch = true
                        resetDamageInfo(it, it.type,true,true)
                    }
                }
                LogUtils.d("isMatch??? " + isMatch)
                if (!isMatch) {
                    AlertDialog.Builder(this).setTitle("??????")
                        .setMessage("????????????????????????????????????????????????")
                        .setPositiveButton(R.string.dialog_ok) { dialog, which ->
                            try {
                                mView?.PDFRemoveAnnot()
                            } catch (e: Exception) {
                            }
                        }
                        .show()
                }
            }
            Constant.BUTTON_POPMENU_DEL -> {
                /**
                 * ?????????????????????????????????
                 */
                var exitDamageBeanList = mDamageBeanList!!.get(mCurrentLocalPDF)
                LogUtils.d("?????????????????????: " + exitDamageBeanList)

                var mTotalDamageBeanList = exitDamageBeanList!!.filter {
                    damageBean.annotName != (it.type+it.createTime)
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

    /**
     * ??????mark??????
     */
    fun addDamageMark(damageInfo: DamageV3Bean){
        val view: View =
            LayoutInflater.from(this).inflate(R.layout.damage_checkbuildstructure_mark_layout, null)
        val damageType = view.findViewById<TextView>(R.id.damage_type)
        val damageText = view.findViewById<TextView>(R.id.damage_text)
        damageText.background = getTextBgDrawableRes()
        damageType.setTextSize(TypedValue.COMPLEX_UNIT_PX,resources.getDimensionPixelSize(R.dimen._6ssp)*mView?.PDFGetZoom()!!)
        damageText.setTextSize(TypedValue.COMPLEX_UNIT_PX,resources.getDimensionPixelSize(R.dimen._4ssp)*mView?.PDFGetZoom()!!)
        damageType.text = ""+damageInfo.type
        var damageAxisNote = damageInfo.axisNote
        if(damageAxisNote.isNullOrEmpty()){
            damageInfo.axisNoteList?.forEachIndexed { index, s ->
                if(index == 0){
                    damageAxisNote+=s
                }else if(index == 2){
                    damageAxisNote+="/"+s
                }else{
                    damageAxisNote+="-"+s
                }
            }
        }
        damageText.text = damageAxisNote
        var size = (resources.getDimensionPixelSize(R.dimen._25sdp)*mView?.PDFGetZoom()!!).toInt()
        mView!!.layoutView(view, size, size/2)
        var bitmap = PDFLayoutView.getViewBitmap(view)
        mView!!.PDFSetStamp(1,bitmap,size.toFloat(),(size/2).toFloat(),damageInfo.type+damageInfo.createTime)
    }

     fun getTextBgDrawableRes(): GradientDrawable? {
        val drawable = GradientDrawable()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val padding: Int = (resources.getDimensionPixelSize(R.dimen._2sdp) * mView?.PDFGetZoom()!!).toInt()
            drawable.setPadding(padding, 0, padding, 0)
        }
        drawable.cornerRadius = (resources.getDimensionPixelSize(R.dimen._2sdp) * mView?.PDFGetZoom()!!)
        drawable.setStroke((2 * mView?.PDFGetZoom()!!).toInt(), Color.parseColor("#FF005B82"))
        drawable.setColor(Color.WHITE)
        return drawable
    }

    /**
     * ??????mark??????
     */
    fun cancelDamageMark(){
        mView?.PDFCancelStamp()
    }

    var addPDFDamageMark = false

    var isEditDamage = false

    /**
     * ??????????????????????????????
     */
    override fun onSelect(type: String?) {
        LogUtils.d("onSelect???"+type)
        resetDamageInfo(null,type,true,false)
    }
}