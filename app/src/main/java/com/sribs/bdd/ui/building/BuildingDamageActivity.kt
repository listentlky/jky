package com.sribs.bdd.ui.building

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.view.Menu
import android.view.View
import android.widget.*
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

import com.radaee.constant.Constant.BUTTON_POPMENU_DEL
import com.radaee.constant.Constant.BUTTON_POPMENU_EDIT
import com.radaee.pdf.*
import com.radaee.reader.PDFLayoutView
import com.radaee.reader.PDFPagesAct
import com.radaee.reader.PDFViewController
import com.radaee.util.PDFAssetStream
import com.radaee.util.PDFHttpStream
import com.radaee.util.RadaeePluginCallback
import com.radaee.view.ILayoutView
import com.radaee.view.ILayoutView.PDFLayoutListener
import com.sribs.bdd.R
import com.sribs.bdd.bean.AnnotDamageBean

import com.sribs.bdd.bean.Floor
import com.sribs.bdd.bean.data.DamageDrawingDataBean
import com.sribs.bdd.databinding.ActivityFloorDamageListBinding


import com.sribs.bdd.module.building.BuildingDamagePresenter
import com.sribs.bdd.module.building.IBuildingContrast
import com.sribs.bdd.rx.ButtonClickEvent
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.common.bean.db.DrawingBean

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlin.collections.ArrayList

@Route(path= com.sribs.common.ARouterPath.BLD_DAMAGE_LIST_ATY)
class BuildingDamageActivity:BaseActivity(), IBuildingContrast.IBuildingView, PDFLayoutListener, IAnnotSwicthLister {

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID)
    var mLocalProjectId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_BUILDING_NO)
    var mLocalBldNo = ""

    private var mPdfFilePath:String = ""
    private val mBinding : ActivityFloorDamageListBinding by inflate()
    private var mAssetStream: PDFAssetStream? = null
    private var mHttpStream: PDFHttpStream? = null
    private var mDoc: Document? = null

    private var mLayout: RelativeLayout? = null
    private var mView: PDFLayoutView? = null
    private var mController: PDFViewController? = null
    private val mPresenter by lazy { BuildingDamagePresenter() }
    private var mDrawingList: ArrayList<DrawingBean> = ArrayList<DrawingBean>()

    //used in BldDrwDmgMngMainFragment left column to sync the damage type, follow such order: floor, drawing, type, damageMap
    private var mCurDrawing:com.sribs.common.bean.db.DrawingBean? = null
    private var mCurDamage:com.sribs.common.bean.db.DamageBean? = null

    private var mCurBuildingDamageList:ArrayList<com.sribs.common.bean.db.DamageBean>? = null

    //used to sync local sqlite
    private var mLocalBldId: Long = -1//楼ID
//    private var mLocalBldNo: String = ""//楼号
    private var mNewDamageMonitorId: String = ""//监测点编号
    private var mSelFloor: String? = ""//当前楼层
    private var mSelFloorDrawingList: ArrayList<DrawingBean>? = null //当前楼层图纸地址list,上下切换图纸用到
    private var mPath: String? = ""//当前已经打开的pdf文件路径
    private var mSelDrawingName: String = ""//当前图纸
    private var mAction:String = ""

    private var mCurDamageDetailOriginalString:String? = ""
    private var mPDFNoteModified:Boolean = false

    private val mFragments by lazy {
        listOf(
            ARouter.getInstance().build(com.sribs.common.ARouterPath.BLD_DRW_DMG_MNG_MAIN_FGT)
                .setTag("main")
//                .withInt(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID, mLocalProjectId)
                .navigation() as BaseFragment,
            ARouter.getInstance().build(com.sribs.common.ARouterPath.BLD_DRW_DMG_MNG_SUB_FGT)
                .setTag("sub")
//                .withInt(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID, mLocalProjectId)
                .navigation() as BaseFragment
        )
    }

    private val mFragments2 by lazy {
        listOf(
            ARouter.getInstance().build(com.sribs.common.ARouterPath.BLD_DRW_DMG_DIAL_REC_FGT)
                .setTag("main")
                .withString(com.sribs.common.ARouterPath.VAL_BUILDING_NO, mLocalBldNo)
                .withString(com.sribs.common.ARouterPath.VAL_DAMAGE_MONITOR_ID, mNewDamageMonitorId)
//                .withInt(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID, mLocalProjectId)
                .navigation() as BaseFragment
        )
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        println("leon FloorDamageListActivity onCreate in")
    }

    private fun init_pdf_callback() {
        RadaeePluginCallback.getInstance().setActivityListener {
            onClose(false)
            finish()
        }
        RadaeePluginCallback.getInstance().willShowReader()
    }

    override fun deinitView() {
        unbindPresenter()
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        println("leon FloorDamageListActivity initView")

        mBinding.pdfEdit.setOnClickListener {
            mFragments[1] as BldDrwDmgMngSubFragment
            mBinding.bldLeftFragment.currentItem = 1
        }

        mBinding.pdfView.setOnClickListener {
            println("leon clicked pdfView")
        }

        mBinding.selectFloor.bringToFront()

        Global.Init(this)

        bindPresenter()
        initData()
        initToolbar()
        initViewPager()

    }

    private fun initToolbar(){
        mBinding.tb.setNavigationIcon(R.mipmap.icon_back)
        mBinding.tbTitle.text = mTitle
        mBinding.tb.showOverflowMenu()
        setSupportActionBar( mBinding.tb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mBinding.tb.setNavigationOnClickListener {
            println("leon BuildingDamageActivity initToolbar 000")
            //check if pdf modified and save when press back
            if(mBinding.bldLeftFragment.currentItem == 1) {
                (mFragments[1] as BldDrwDmgMngSubFragment).onBackButtonClicked()
                mBinding.bldLeftFragment.currentItem = 0
                println("leon BuildingDamageActivity initToolbar 111")
            }else if(mBinding.bldLeftFragment.currentItem == 0) {
                if(mBinding.bldDamageDetailFragment.visibility == View.VISIBLE){
                    println("leon BuildingDamageActivity initToolbar 222")
                    mBinding.pdfView.visibility = View.VISIBLE
                    mBinding.selectFloor.visibility = View.VISIBLE
                    mBinding.bldLeftFragment.visibility = View.VISIBLE
                    mBinding.bldDamageDetailFragment.visibility = View.GONE
                    mBinding.pdfEdit.visibility = View.VISIBLE
                }
                else {
                    println("leon BuildingDamageActivity initToolbar 333")

                    promptToSavePdf(true)
                }
            }
        }
    }

    private fun promptToSavePdf(ifExit: Boolean){
        if (isPDFModifiedNotSaved()) {
            AlertDialog.Builder(this).setTitle(R.string.drawing_edit_exit_dialog_title)
                .setMessage(R.string.save_pdf_message).setPositiveButton(R.string.dialog_ok) { dialog, which ->
                    //cache damage list to local sqlite
                    mDoc?.let { it1 -> saveDamageData() }
                    mController?.savePDF()
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
            }
            else
                saveDamageDataToDbStarted()
        }
    }

    private fun getAllDamagesWhenChangeFloor(){
        println("leon getAllDamagesWhenChangeFloor mCurDrawing=${mCurDrawing.toString()}")
        if(mDrawingList != null){
            mSelFloorDrawingList = mDrawingList.filter { s -> s.floorName == mSelFloor } as ArrayList<DrawingBean>

            if(mSelFloorDrawingList?.size!! > 0) {
                println("leon getAllDamagesWhenChangeFloor mSelFloorDrawingsList=${mSelFloorDrawingList.toString()}")
                mSelDrawingName = mSelFloorDrawingList?.get(0)?.fileName.toString()
                mPdfFilePath = mSelFloorDrawingList?.get(0)?.localAbsPath.toString() ?: ""
                mCurDrawing = mSelFloorDrawingList?.get(0)

            }
            println("leon getAllDamagesWhenChangeFloor mCurDrawing=${mCurDrawing.toString()}")
            (mFragments[0] as BldDrwDmgMngMainFragment).initLocalDamages(mCurDrawing!!, mCurBuildingDamageList)
            println("leon getAllDamagesWhenChangeFloor mPdfFilePath=" + mPdfFilePath?.toString())
        }
    }

    private fun saveDamageData(){

        println("leon BuildingDamageActivity saveDamageData mCurBuildingDamageList=${mCurBuildingDamageList.toString()}")
        println("leon BuildingDamageActivity saveDamageData mCurDrawing=${mCurDrawing.toString()}")
//        saveDamageDataToDbStarted()

        var curDrawingDamageList = mCurBuildingDamageList?.filter {
            it.drawingId == mCurDrawing?.id
        } as ArrayList<com.sribs.common.bean.db.DamageBean>

        println("leon BuildingDamageActivity saveDamageData curDrawingDamageList=${curDrawingDamageList.toString()}")
        curDrawingDamageList.forEach {
            println("leon BuildingDamageActivity saveDamageData id=${it.id.toString()}, damage=${it.toString()}")
        }

        curDrawingDamageList?.run{
            mPresenter.saveDamageDataToDb(curDrawingDamageList)
        }
//        (mFragments[0] as BldDrwDmgMngMainFragment).saveDamageData(doc)

    }

    private fun initViewPager(){
        mBinding.bldLeftFragment.setSmooth(false)
        mBinding.bldLeftFragment.setScroll(false)
        mBinding.bldLeftFragment.adapter = BasePagerAdapter(supportFragmentManager,mFragments)
        mBinding.bldLeftFragment.offscreenPageLimit = 2

        mBinding.bldDamageDetailFragment.setSmooth(false)
        mBinding.bldDamageDetailFragment.setScroll(false)
        mBinding.bldDamageDetailFragment.adapter = BasePagerAdapter(supportFragmentManager,mFragments2)
        mBinding.bldDamageDetailFragment.offscreenPageLimit = 1
        mBinding.bldDamageDetailFragment.visibility = View.GONE
    }

    public fun recoverViewPager(dmg: DamageDrawingDataBean?){
        mBinding.bldLeftFragment.visibility = View.VISIBLE
        mBinding.pdfView.visibility = View.VISIBLE
        mBinding.bldDamageDetailFragment.visibility = View.GONE
        mBinding.selectFloor.visibility = View.VISIBLE
        mBinding.pdfEdit.visibility = View.VISIBLE

        if(dmg != null)
            updateDamageList(dmg)
    }

    private fun onClose(onBackPressed: Boolean) {
        if (isPDFModifiedNotSaved()) {
            if (intent.getBooleanExtra("AUTOMATIC_SAVE", false)) {
                mController?.savePDF()
                if (onBackPressed) super.onBackPressed()
            } else {
                AlertDialog.Builder(this).setTitle(R.string.exiting)
                    .setMessage(R.string.save_msg).setPositiveButton(R.string.yes
                    ) { dialog, which ->
                        mController?.savePDF()
                        if (onBackPressed) super.onBackPressed()
                    }.setNegativeButton(R.string.no
                    ) { dialog, which -> if (onBackPressed) super.onBackPressed() }
                    .show()
            }
        } else if (onBackPressed) super.onBackPressed()
    }

    @SuppressLint("InlinedApi")
    override fun onDestroy() {
        RadaeePluginCallback.getInstance().willCloseReader()
        val vctrl = mController
        val doc = mDoc
        val view = mView
        val astr = mAssetStream
        val hstr = mHttpStream
        mController = null
        mDoc = null
        mView = null
        mAssetStream = null
        mHttpStream = null
        view?.PDFCloseOnUI()
        object : Thread() {
            override fun run() {
                vctrl?.onDestroy()
                if (view != null) view.PDFClose()
                doc?.Close()
                astr?.close()
                hstr?.close()
                Global.RemoveTmp()
//                synchronized(this) { this.notify() }
            }
        }.start()
        synchronized(this) {
            try {
                Thread.sleep(1500)
            } catch (ignored: Exception) {
            }
        }
        super.onDestroy()
//        RadaeePluginCallback.getInstance().didCloseReader()
    }

    public fun getController(){mController}

    fun getFileState(): Int {
        return mController?.getFileState() ?: PDFViewController.NOT_MODIFIED
    }

    fun isPDFModifiedNotSaved():Boolean{
        println("leon BuildingDamageActivity mPDFNoteModified=${mPDFNoteModified}, getFileState()=${getFileState()}")
        return (getFileState() == PDFViewController.MODIFIED_NOT_SAVED || mPDFNoteModified)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_unit_vp, menu)
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        println("leon requestCode=${requestCode}")
        if(permissions != null){
            for(i in 0..permissions.size-1){
                println("leon permissions[${i}]=${permissions.get(i)}")
            }
        }
        if(grantResults != null){
            for(i in 0..grantResults.size-1){
                println("leon grantResults[${i}]=${grantResults.get(i)}")
            }
        }
    }

    private fun initData(){

        RxBus.getDefault().toObservable(ButtonClickEvent::class.java).subscribe { msg ->
            println("leon will process ${msg.id}, checked=${msg.checked}")

            //处理消息
        }

        mPresenter.getLocalBuilding(mLocalProjectId)

    }

    private fun onFail(msg: String) //treat open failed.
    {
        mDoc!!.Close()
        mDoc = null
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun getContext(): Context = this

    override fun initBuildingFloors(bldId:Long, floors: ArrayList<Floor>) {
        println("leon initBuildingFloors floors=" + floors.toString())

        mLocalBldId = bldId

        println("leon initBuildingFloors mLocalBldId=" + mLocalBldId)

        val selectFloorSpinner: Spinner = mBinding.selectFloor as Spinner
        var dataset = arrayListOf<String>()

        if(floors != null){
            dataset = ArrayList<String>()
            floors.forEach {
                it.floorName?.let { it1 -> dataset.add(it1) }
            }
        }

        mSelFloor = dataset?.get(0)?.toString()

        println("leon dataset=" + dataset.toString())
        selectFloorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                mSelFloor = dataset.get(position)
                println("leon niceSpinner onItemSelected mSelFloor=${mSelFloor}")

                mAction = "switchFloor"


                promptToSavePdf(false)

                if(!mPath.equals(""))
                    closePdf()
                if(!mPdfFilePath.equals(""))
                    openPdf(mPdfFilePath)
            }
        }

        //数据源手动添加
        var spinnerAdapter = ArrayAdapter<String>(this,
            R.layout.abacus_spinner_text_item, dataset);
        selectFloorSpinner.setAdapter(spinnerAdapter);

        //设置下拉选项的方式
        spinnerAdapter.setDropDownViewResource(R.layout.abacus_spinner_dropdown_item);
        selectFloorSpinner.setAdapter(spinnerAdapter);
    }

    override fun initBuildingDrawing(drawings: ArrayList<DrawingBean>) {

        mDrawingList.addAll(drawings)
        println("leon initBuildingDrawing mDrawingList=" + mDrawingList.toString())

        mDrawingList?.run{
            for(drw in mDrawingList){
                mPresenter.getAllDamagesInDrawing(drw)
            }
        }

        if(mDrawingList != null) {
            mSelFloorDrawingList = mDrawingList.filter { s -> s.floorName == mSelFloor } as ArrayList<DrawingBean>
            mSelDrawingName = mSelFloorDrawingList?.get(0)?.fileName.toString()
            mPdfFilePath = mSelFloorDrawingList?.get(0)?.localAbsPath.toString()?:""
            mCurDrawing = mSelFloorDrawingList?.get(0)
            println("leon initBuildingDrawing mCurDrawing=" + mCurDrawing.toString())

            if(!mPath.equals(""))
                closePdf()

            if(!mPdfFilePath.equals(""))
                openPdf(mPdfFilePath)
        }
    }

    override fun initDrawingDamages(damages: List<com.sribs.common.bean.db.DamageBean>) {

        println("leon initDrawingDamages in damage=${damages.toString()}")

        mCurBuildingDamageList = mCurBuildingDamageList?: ArrayList<com.sribs.common.bean.db.DamageBean>()

//        mCurBuildingDamageList?.clear()
        var foundOld: Boolean = false
        if(!damages.isNullOrEmpty()){
            var dmgType:String? = null
            damages.forEach { new->
                foundOld = false
                println("leon initDrawingDamages damage.id=${new.id.toString()}, damage=${new.toString()}")
//                mCurBuildingDamageList?.add(it)
                mCurBuildingDamageList?.forEach { old->
                    println("leon initDrawingDamages olddamage.id=${old.id.toString()}, olddamage=${old.toString()}")
                    if(old.isSame(new.drawingId, new.annotRef)){
                        old.id = new.id
                        foundOld = true
                    }
                    println("leon initDrawingDamages after update olddamage.id=${old.id.toString()}, after update olddamage=${old.toString()}")
                }
                if(!foundOld){
                    mCurBuildingDamageList?.add(new)
                }
            }
        }

//        println("leon initDrawingDamages mCurBuildingDamageList=${mCurBuildingDamageList.toString()}")

        if(mCurDrawing != null)
            (mFragments[0] as BldDrwDmgMngMainFragment).initLocalDamages(mCurDrawing!!, mCurBuildingDamageList)
        else
            println("leon initDrawingDamages mCurDrawing is NULL")
    }

    override fun saveDamageDataToDbStarted() {
        println("leon saveDamageDataToDbStarted mAction=${mAction.toString()}")

        mCurDamageDetailOriginalString = ""
        mPDFNoteModified = false

        if(mAction.equals("switchFloor")){
            getAllDamagesWhenChangeFloor()

            if(!mPath.equals(""))
                closePdf()
            if(!mPdfFilePath.equals(""))
                openPdf(mPdfFilePath)
        }else if(mAction.equals("switchPrev")){
            if(mSelFloorDrawingList != null){
                var pos: Int = -1
                var bb:DrawingBean? = null

                var len = mSelFloorDrawingList!!.size
                if(len == 1){
                    onMsg(mSelFloor + "，只有一张图纸！")
                }else{
                    for(i in 0..len-1){
                        bb = mSelFloorDrawingList!!.get(i)
                        if(bb.fileName.equals(mSelDrawingName)) {
                            pos = i
                            break
                        }
                    }
                    if((pos == 0))
                        pos = len-1
                    else
                        pos -= 1

                    mCurDrawing = mSelFloorDrawingList?.get(pos)
                    mSelDrawingName = mSelFloorDrawingList?.get(pos)?.fileName.toString()
                    mPdfFilePath = mSelFloorDrawingList?.get(pos)?.localAbsPath.toString()?:""
                    println("leon onButtonPrevPressed mPdfFilePath=" + mPdfFilePath.toString())

                    (mFragments[0] as BldDrwDmgMngMainFragment).initLocalDamages(mCurDrawing!!, mCurBuildingDamageList)

                    if(!mPath.equals(""))
                        closePdf()
                    if(!mPdfFilePath.equals(""))
                        openPdf(mPdfFilePath)
                }
            }
        }else if(mAction.equals("switchNext")) {
            println("leon onButtonNextPressed mSelFloorDrawingList=" + mSelFloorDrawingList.toString())
            if(mSelFloorDrawingList != null){
                var pos: Int = -1
                var bb:DrawingBean? = null

                var len = mSelFloorDrawingList!!.size
                if(len == 1){
                    onMsg("当前选择是："+ mSelFloor + ", 只有一张图纸！")
                }else{
                    for(i in 0..len-1){
                        bb = mSelFloorDrawingList!!.get(i)
                        if(bb.fileName.equals(mSelDrawingName)) {
                            pos = i
                            break
                        }
                    }
                    if((pos+1) == len)
                        pos = 0
                    else
                        pos += 1

                    mCurDrawing = mSelFloorDrawingList?.get(pos)
                    mSelDrawingName = mSelFloorDrawingList?.get(pos)?.fileName.toString()
                    mPdfFilePath = mSelFloorDrawingList?.get(pos)?.localAbsPath.toString()?:""
                    println("leon onButtonNextPressed mPdfFilePath=" + mPdfFilePath.toString())

                    (mFragments[0] as BldDrwDmgMngMainFragment).initLocalDamages(mCurDrawing!!, mCurBuildingDamageList)

                    if(!mPath.equals(""))
                        closePdf()
                    if(!mPdfFilePath.equals(""))
                        openPdf(mPdfFilePath)
                }
            }
        }

        mAction = ""
    }

    override fun updateLocalDamageDetail(dmg: com.sribs.common.bean.db.DamageBean?) {
        println("leon BuildingDamageActivity updateLocalDamageDetail dmg=${dmg.toString()}")
        (mFragments2[0] as BuildingDamageDetailRecFragment).updateLocalDamageDetail(dmg)
    }

    override fun onRemoveDamageInDrawing(dmg:com.sribs.common.bean.db.DamageBean) {

        mCurDamage?.run{
            //remove from mCurBuildingDamageList
            mCurBuildingDamageList?.run{
                mCurBuildingDamageList?.forEach {
                    if(dmg.id == it.id){
                        println("leon BuildingDamageActivity mCurBuildingDamageList 00 = ${mCurBuildingDamageList.toString()}")
                        println("leon BuildingDamageActivity onRemoveDamageInDrawing will delete ${it.toString()}")
                        mCurBuildingDamageList?.remove(it)
                        println("leon BuildingDamageActivity mCurBuildingDamageList 11 = ${mCurBuildingDamageList.toString()}")
                    }
                }
            }
            //sync to BldDrwDmgMngMainFragment
            (mFragments[0] as BldDrwDmgMngMainFragment).initLocalDamages(mCurDrawing!!, mCurBuildingDamageList)
        }
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

    override fun dispose(): Unit? {

        closePdf()

        mFragments.forEach {
            it.dispose()
            it.deinitView()
        }
        return super.dispose()
    }

    override fun OnPDFPageModified(pageno: Int) {
        println("leon FloorDamageListActivity OnPDFPageModified pageno=" + pageno)
        mController?.onPageModified(pageno)
    }

    private var m_cur_page = 0
    override fun OnPDFPageChanged(pageno: Int) {
        m_cur_page = pageno
        if (mController != null) mController!!.OnPageChanged(pageno)
        RadaeePluginCallback.getInstance().didChangePage(pageno)
    }

    override fun OnPDFAnnotTapped(pageno: Int, annot: Page.Annotation?) {
        println("leon BuildingDamageActivity OnPDFAnnotTapped pageno=${pageno}")
        if (annot != null) {
            RadaeePluginCallback.getInstance().onAnnotTapped(annot)
            if (!mView!!.PDFCanSave() && annot.GetType() != 2) return
        }
        if (mController != null) mController!!.OnAnnotTapped(pageno, annot)
    }

    override fun OnPDFBlankTapped(pageno: Int) {
        println("leon FloorDamageListActivity OnPDFBlankTapped in")
        if (mController != null)
            mController!!.OnBlankTapped()
        else
            println("leon FloorDamageListActivity OnPDFBlankTapped mController is null")
        RadaeePluginCallback.getInstance().onBlankTapped(pageno)
    }

    override fun OnPDFSelectEnd(text: String?) {
        println("leon FloorDamageListActivity OnPDFSelectEnd in")
    }

    override fun OnPDFOpenURI(uri: String?) {
        println("leon FloorDamageListActivity OnPDFOpenURI in")
    }

    override fun OnPDFOpenJS(js: String?) {
        println("leon FloorDamageListActivity OnPDFOpenJS in")
    }

    override fun OnPDFOpenMovie(path: String?) {
        println("leon FloorDamageListActivity OnPDFOpenMovie in")
    }

    override fun OnPDFOpenSound(paras: IntArray?, path: String?) {
        println("leon FloorDamageListActivity OnPDFOpenSound in")
    }

    override fun OnPDFOpenAttachment(path: String?) {
        println("leon FloorDamageListActivity OnPDFOpenAttachment in")
    }

    override fun OnPDFOpenRendition(path: String?) {
        println("leon FloorDamageListActivity OnPDFOpenRendition in")
    }

    override fun OnPDFOpen3D(path: String?) {
        println("leon FloorDamageListActivity OnPDFOpen3D in")
    }

    override fun OnPDFZoomStart() {

    }

    override fun OnPDFZoomEnd() {

    }

    override fun OnPDFDoubleTapped(pageno: Int, x: Float, y: Float): Boolean {
        val mCurZoomLevel = mView!!.PDFGetZoom()
        if (mView!!.PDFGetScale() <= mView!!.PDFGetMinScale()) Global.g_zoom_step = 1f
        if (mCurZoomLevel > Global.g_layout_zoom_level && Global.g_zoom_step > 0 ||
            mCurZoomLevel == 1f && Global.g_zoom_step < 0
        ) //reverse zoom step
            Global.g_zoom_step *= -1f

        mView!!.PDFSetZoom(x.toInt(),
            y.toInt(), mView!!.PDFGetPos(x.toInt(), y.toInt()), mCurZoomLevel + Global.g_zoom_step)
        RadaeePluginCallback.getInstance().onDoubleTapped(pageno, x, y)
        return true
    }

    override fun OnPDFLongPressed(pageno: Int, x: Float, y: Float) {

        println("leon FloorDamageListActivity OnPDFLongPressed in, pageno=" + pageno)

        RadaeePluginCallback.getInstance().onLongPressed(pageno, x, y)
    }

    override fun OnPDFSearchFinished(found: Boolean) {
        TODO("Not yet implemented")
    }

    override fun OnPDFPageDisplayed(canvas: Canvas?, vpage: ILayoutView.IVPage?) {

    }

    private var show_progress = true
    private var mDidShowReader = false
    override fun OnPDFPageRendered(vpage: ILayoutView.IVPage?) {
        if (!mDidShowReader) {
            RadaeePluginCallback.getInstance().didShowReader()
            mDidShowReader = true
        }
//        if (show_progress) {
//            findViewById<View>(R.id.progress).visibility = View.GONE
//            show_progress = false
//        }
    }

    override fun onPDFNoteTapped(jsonstring: String) {
        mFragments[1] as BldDrwDmgMngSubFragment
        mBinding.bldLeftFragment.currentItem = 1
    }

    override fun onPDFNoteAdded(annotPoint: String?) {
        println("leon BuildingDamageActivity onPDFNoteAdded annotPoint=${annotPoint}")
        var annot: AnnotDamageBean = Gson().fromJson(annotPoint!!, AnnotDamageBean::class.java)

        var act:Int = annot.action!!
        var ref:Long = annot.ref!!
        var axis:String? = annot.axis
        var type:String? = annot.type
        var content:String? = annot.content

        var curTime: Long = System.currentTimeMillis()

        println("leon onPDFNoteAdded mCurBuildingDamageList=${mCurBuildingDamageList.toString()}")
        var curDrawingDamageList = mCurBuildingDamageList?.filter {
            it.drawingId == mCurDrawing?.id
        } as ArrayList<com.sribs.common.bean.db.DamageBean>

        println("leon onPDFNoteAdded curDrawingDamageList=${curDrawingDamageList.toString()}")
        var size:Int = curDrawingDamageList.size
        println("leon onPDFNoteAdded size=${size}")
        size++
        mNewDamageMonitorId = size.toString()

        println("leon mNewDamageMonitorId=${mNewDamageMonitorId}")

        mCurDamage = com.sribs.common.bean.db.DamageBean(
            mCurDrawing?.id,
            type,
            ref,
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            curTime,
            0,
            0L,
            "",
            "",
            0,
            0
            )

        showDamageDetailPage(null)
    }

    override fun onPDFNoteEdited(annotPoint: String?) {
        println("leon BuildingDamageActivity onPDFNoteEdited annotPoint=${annotPoint}")


        var annot: AnnotDamageBean = Gson().fromJson(annotPoint!!, AnnotDamageBean::class.java)

        var act:Int = annot.action!!
        var ref:Long = annot.ref!!
        var axis:String? = annot.axis
        var type:String? = annot.type
        var content:String? = annot.content

        mCurBuildingDamageList?.forEach{
            println("leon BuildingDamageActivity onPDFNoteEdited damage=${it.toString()}")
            if(it.annotRef ==  ref){
                mCurDamage = it
                println("leon BuildingDamageActivity onPDFNoteEdited mCurDamage=${mCurDamage.toString()}")
            }
        }
        println("leon BuildingDamageActivity onPDFNoteEdited mCurDamage=${mCurDamage.toString()}")
        if(act == BUTTON_POPMENU_EDIT) {
            mCurDamageDetailOriginalString = mCurDamage?.toDescString()
            showDamageDetailPage(mCurDamage)
        }else if(act == BUTTON_POPMENU_DEL){
            mCurDamage?.let { mPresenter.removeDamageInDrawing(it) }
        }

    }

    private fun showDamageDetailPage(dmg:com.sribs.common.bean.db.DamageBean?){

        println("leon showDamageDetailPage dmg=${dmg.toString()}")

        mBinding.bldDamageDetailFragment.currentItem = 0
        mBinding.bldDamageDetailFragment.visibility = View.VISIBLE
        mBinding.bldLeftFragment.visibility = View.GONE
        mBinding.pdfView.visibility = View.GONE
        mBinding.selectFloor.visibility = View.GONE

        mBinding.pdfEdit.visibility = View.GONE

        (mFragments2[0] as BuildingDamageDetailRecFragment).mNewDamageMonitorId = mNewDamageMonitorId
        (mFragments2[0] as BuildingDamageDetailRecFragment).mBuildingNo = mLocalBldNo

        mPresenter.getLocalDamageDetail(dmg)

    }

    override fun onPDFNoteDeleted(annotPoint: String?) {
        println("leon BuildingDamageActivity onPDFNoteDeleted")
    }

    override fun onButtonPrevPressed() {
        println("leon BuildingDamageActivity onButtonPrevPressed")

        mAction = "switchPrev"

        promptToSavePdf(false)

    }

    override fun onButtonNextPressed() {
        println("leon BuildingDamageActivity onButtonNextPressed")

        mAction = "switchNext"

        promptToSavePdf(false)


    }


    private fun openPdf(pdfPath: String){
        Observable.create<Boolean> { o ->
            o.onNext(XXPermissions.isGranted(this, Permission.MANAGE_EXTERNAL_STORAGE))
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                println("leon check grant result: ${it}")
                var ret: Int? = -20
                when(it){
                    true->{
//                        mDoc!!.GetPagesMaxSize()
                        if (!TextUtils.isEmpty(pdfPath)){
                            mDoc = Document()
                            ret = mDoc!!.Open(mPdfFilePath, "")
                            println("leon initData ret=${ret}")
                            mPath = mPdfFilePath
                            (mFragments[0] as BldDrwDmgMngMainFragment).setCurPdfFileAbsPath(mPath!!)
                        }
                        else
                            println("leon not valid path")
                    }
                    false->{
                        println("leon not granted read and write permission")
                    }

                }
                Observable.create<Int> { o ->
                    o.onNext(ret?:-10)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                println("leon before ProcessOpenResult flow in=${it}")

                mView = mBinding.pdfView as PDFLayoutView

                mView?.PDFOpen(mDoc, this)
                mView?.setReadOnly(intent.getBooleanExtra("READ_ONLY", false))
                mLayout = mBinding.root

                mView?.setAnnotMenu(UIAnnotMenu(mLayout))

                mController = PDFViewController(mLayout,
                    mView,
                    mPath,
                    mAssetStream != null || mHttpStream != null
                )
                mController!!.SetPagesListener(View.OnClickListener {
                    val intent = Intent()
                    intent.setClass(this, PDFPagesAct::class.java)
                    PDFPagesAct.SetTranDoc(mDoc)
                    startActivityForResult(intent, 10000)
                })

                val gotoPage = intent.getIntExtra("GOTO_PAGE", -1)
                if (gotoPage > 0) mView?.PDFGotoPage(gotoPage)
            },{
                println("leon not granted ${Permission.MANAGE_EXTERNAL_STORAGE}")
            })
    }

    private fun closePdf(){
        println("leon closePdf in")

        RadaeePluginCallback.getInstance().willCloseReader()

        val vctrl: PDFViewController? = mController
        val doc: Document? = mDoc
        val view: PDFLayoutView? = mView
        val astr: PDFAssetStream? = mAssetStream
        val hstr: PDFHttpStream? = mHttpStream
        mController = null
        mDoc = null
        mView = null
        mAssetStream = null
        mHttpStream = null
        if (view != null) view.PDFCloseOnUI()
        object : Thread() {
            override fun run() {
                if (vctrl != null) vctrl.onDestroy()
                if (view != null) view.PDFClose()
                if (doc != null) doc.Close()
                if (astr != null) astr.close()
                if (hstr != null) hstr.close()
                Global.RemoveTmp()
                synchronized(this) {  }
            }
        }.start()
        synchronized(this) {
            try {
                Thread.sleep(1500)
            } catch (ignored: java.lang.Exception) {
            }
        }
//        super.onDestroy()
        RadaeePluginCallback.getInstance().didCloseReader()
    }

    public fun getSelectedFloorName(): String?{
        return mSelFloor
    }

    private fun updateDamageList(dmg: DamageDrawingDataBean){

        dmg?.apply {

        }
        var curTime:Long = System.currentTimeMillis()
        mCurDamage?.apply {
            this.axis = dmg.axis
            this.dmDesc = dmg.damageDes
            this.pohtoPath = dmg.damagePic
            this.dDetailType = dmg.damageType
            this.leakLength = dmg.crackLength
            this.leakWidth = dmg.crackWidth
            this.mntWay = dmg.monitorWay
            this.mntId = dmg.monitorPointNo
            this.monitorLength = dmg.nickLength
            this.monitorWidth = dmg.nickWidth
            this.monitorPhotoPath = dmg.damageCrackMonitorPic
            this.updateTime = curTime
        }

        var latestCurDamageDetailDescString = mCurDamage?.toDescString()
        if(!latestCurDamageDetailDescString.equals(mCurDamageDetailOriginalString))
            mPDFNoteModified = true

        println("leon mCurDamageDetailOriginalString=${mCurDamageDetailOriginalString}")
        println("leon latestCurDamageDetailDescString=${latestCurDamageDetailDescString}")
        println("leon updateDamageList mCurDamage = ${mCurDamage.toString()}")
        //加入到本栋建筑物损伤列表，切换图纸或退出时，凭此list更新本地数据库
        mCurBuildingDamageList?.add(mCurDamage!!)
        println("leon updateDamageList mCurBuildingDamageList=${mCurBuildingDamageList.toString()}")

        //更新左侧损伤类型对应的损伤明细
        (mFragments[0] as BldDrwDmgMngMainFragment).updateDamages(mCurDamage)
    }

    override fun onAnnotClicked(annotId: String, checked: Boolean) {
        when(annotId){
            ModuleHelper.DRAWING_DMG_LINE_BUTTON_CLICKED->{
                if(checked)
                    mView?.PDFSetLine(0)
                else
                    mView?.PDFSetLine(1)
            }
            ModuleHelper.DRAWING_DMG_RECT_BUTTON_CLICKED->{

                if(checked)
                    mView?.PDFSetRect(0)
                else
                    mView?.PDFSetRect(1)
            }
            ModuleHelper.DRAWING_DMG_CIRCLE_BUTTON_CLICKED->{

                if(checked)
                    mView?.PDFSetEllipse(0)
                else
                    mView?.PDFSetEllipse(1)
            }
            ModuleHelper.DRAWING_DMG_TEXT_BUTTON_CLICKED->{

                if(checked)
                    mView?.PDFSetEditbox(0)
                else
                    mView?.PDFSetEditbox(1)
            }
            ModuleHelper.DRAWING_DMG_MAN_BUTTON_CLICKED->{

                if(checked)
                    mView?.PDFSetInk(0)
                else
                    mView?.PDFSetInk(1)
            }
        }
    }

}