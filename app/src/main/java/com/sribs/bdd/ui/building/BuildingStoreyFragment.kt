package com.sribs.bdd.ui.building

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.launch
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.recyclical.datasource.DataSource
import com.afollestad.recyclical.datasource.emptyDataSource
import com.afollestad.recyclical.itemdefinition.onChildViewClick
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sribs.bdd.R
import com.sribs.bdd.action.Config
import com.sribs.bdd.bean.Building
import com.sribs.bdd.bean.Drawing
import com.sribs.bdd.bean.Floor
import com.sribs.bdd.bean.ProjectBean
import com.sribs.bdd.bean.data.DamageDrawingDataBean
import com.sribs.bdd.bean.data.NonInFloorDetailDataBean
import com.sribs.bdd.bean.data.ProjectCreateDataBean
import com.sribs.bdd.bean.data.ProjectUnitDataBean
import com.sribs.bdd.bean.data.recycler.*
import com.sribs.bdd.databinding.FragmentProjectStoreyBinding
import com.sribs.bdd.module.building.IBuildingContrast
import com.sribs.bdd.module.project.ProjectStoreyPresenter
import com.sribs.bdd.ui.adapter.ProjectUnitAdapter
import com.sribs.bdd.ui.project.ProjectUnitRoomFragment
import com.sribs.bdd.utils.ModuleHelper

import com.sribs.bdd.utils.ModuleHelper.CUR_BLD_NO
import com.sribs.bdd.utils.ModuleHelper.CUR_PRO_LEADER
import com.sribs.bdd.utils.ModuleHelper.CUR_PRO_NAME
import com.sribs.bdd.utils.ModuleHelper.DRAWING_CACHE_FOLDER
import com.sribs.bdd.utils.ModuleHelper.FILE_PICKER_REQUEST_CODE_EAST_FACADE
import com.sribs.bdd.utils.ModuleHelper.FILE_PICKER_REQUEST_CODE_OVERALL_FACADE
import com.sribs.common.ARouterPath.FLOOR_TOTAL_NUM
import com.sribs.common.ARouterPath.VAL_COMMON_TITLE
import com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_FLOOR_NUM
import com.sribs.common.bean.db.DamageBean
import com.sribs.common.bean.db.DrawingBean
import com.sribs.common.utils.DialogUtil
import com.sribs.common.utils.FileUtil
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.File
import kotlin.collections.ArrayList


/**
 * @date 2022/3/2
 * @author leon
 * @Description 非居民类建筑新建项目明细页面
 */
@Route(path= com.sribs.common.ARouterPath.PRO_CREATE_STOREY)
class BuildingStoreyFragment:BaseFragment(R.layout.fragment_project_storey), IBuildingContrast.IBuildingView {

//    private val facadeDrawingslist by lazy { view!!.findViewById<RecyclerView>(R.id.fragment_list) }

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID)
    var mLocalProjectId = -1

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_BUILDING_ID)
    var mBuildingId = -1

    private val TAG = "leon"
    private var mFloorTotalNum: Int = 0
    private val mModuleMap by lazy {  ModuleHelper.moduleMap }
    private var mNewBuildingCreated:Boolean = false;

//    private val mPresenter by lazy { ProjectUnitPresenter() }
    private val mFloorPresenter by lazy { ProjectStoreyPresenter() }

    private val mBinding : FragmentProjectStoreyBinding by bindView()

    private val mFragments = ArrayList<ProjectUnitRoomFragment>()

    private var mCurFloorId: Int = -1

    val mMyCode :String by lazy {
        "${this.hashCode()}"
    }

    val mData: NonInFloorDetailDataBean by lazy {
        NonInFloorDetailDataBean(mLocalProjectId)
    }

    override fun deinitView() {
        unbindPresenter()
    }

    private val dataSource = emptyDataSource()
    private val dataSource2 = emptyDataSource()
    private val floorsDataSource = emptyDataSource()

    private lateinit var mPrefs: SharedPreferences
    private var mProName: String? = ""
    private var mBldNo: String? = ""
    private var mProLeader: String? = ""
    private var mCurDrawingsDir: String? = ""
    private var mEngineer: String? = ""
    private var mBldId:Long? = -1

    private var mFragmentActionListener: IFragmentActionListener? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        //设置东西南北各面墙
        var facadeDrawingItem: DrawingsLauoutItem? = null
        var facedeArray = resources.getStringArray(R.array.facade_drawings_list)
        var id: Int = -1
        for(i in 0..facedeArray.size-1){
            id = FILE_PICKER_REQUEST_CODE_EAST_FACADE + i
            facadeDrawingItem = DrawingsLauoutItem(
                id,
                when(i){0->"东立面" 1->"南立面" 2->"西立面" 3->"北立面" else->""},
                "",
                facedeArray.get(i),
                "",
                null,
                false
            )
            dataSource.add(facadeDrawingItem)
        }

        LOG.I("leon","dataSource size=" + dataSource.size())
        mBinding.facadeDrawingsList.setup {
            withDataSource(dataSource)
            withItem<DrawingsLauoutItem, FacadeDrawingViewHolder>(R.layout.item_floor_facade_table) {
                hasStableIds { it.id }
                onBind(::FacadeDrawingViewHolder) { _, item ->
                    tvFacade.text = item.actionName
                    tvDrawings.text = item.drawingList
                }

                onChildViewClick(FacadeDrawingViewHolder::tvFacade) { _, _ ->
                    LOG.I("leon","Clicked: ${item.actionName} / ${item.drawingList}")
                    browseDocuments(item.id)
                }
            }
        }

        //设置总平面墙
        var overallDrawingItem: DrawingsLauoutItem = DrawingsLauoutItem(
            FILE_PICKER_REQUEST_CODE_OVERALL_FACADE,
            "底层(总平面层)",
            "",
            "上传底层/总平面图纸",
            "",
            null,
            false
        )
        dataSource2.add(overallDrawingItem)
        var facadeOverallItem: DrawingsLauoutItem? = null
        LOG.I("leon","dataSource size=" + dataSource.size())
        mBinding.overallDrawingsList.setup {
            withDataSource(dataSource2)
            withItem<DrawingsLauoutItem, FacadeDrawingViewHolder>(R.layout.item_floor_facade_table) {
                hasStableIds { it.id }
                onBind(::FacadeDrawingViewHolder) { _, item ->
                    tvFacade.text = item.actionName
                    tvDrawings.text = item.drawingList
                }

                onChildViewClick(FacadeDrawingViewHolder::tvFacade) { _, _ ->
                    LOG.I("leon","Clicked: ${item.actionName} / ${item.drawingList}")
                    browseDocuments(item.id)
                }
            }
        }

        //设置各层
        mBinding.floorDrawingsList.setup {
            withDataSource(floorsDataSource)
            withItem<DrawingsLauoutItem, FloorDrawingViewHolder>(R.layout.item_noin_table_floor_detail) {
//                            hasStableIds { it.id }
                onBind(::FloorDrawingViewHolder) { _, item ->
                    tvId.text = item.id.toString()
                    tvDrawings.text = item.drawingList
                    tvUploadDrawing.text = item.actionName
                    tvFloorCopy.text = item.copyFloor
                }

                onChildViewClick(FloorDrawingViewHolder::tvUploadDrawing) { _, _ ->
                    LOG.I("leon","Clicked: ${item.actionName} / ${item.drawingList}")
                    mCurFloorId = item.id.toInt()
                    browseDocuments(ModuleHelper.FILE_PICKER_REQUEST_CODE_FLAT_FACADE + item.id - 1)
                }

                onChildViewClick(FloorDrawingViewHolder::tvFloorCopy) { _, _ ->
                    if(item.drawingList.isNullOrBlank()){
                        showToast("请先选择图纸！")
                        return@onChildViewClick
                    }

                    mCurFloorId = item.id.toInt()
                    copyFloorDrawings.launch()
                    LOG.I("leon","Clicked: ${item.actionName} / ${item.drawingList}")
                }

            }
        }

        mBinding.btnOk.setOnClickListener { it->
            println("leon mBldId 000 =" + mBldId)
            if(!mNewBuildingCreated){
                DialogUtil.showMsgDialog(requireContext(),"确定要保存到本地",{
                    saveLocalProjectToLocalDB()
                }){

                }
            }
        }

        mBinding.btnCancel.setOnClickListener { it->




        }
    }

    fun setFragmentActionListener(fragmentActionListener: IFragmentActionListener){
        mFragmentActionListener = fragmentActionListener
    }

    private val copyFloorDrawings = registerForActivityResult(
        object : ActivityResultContract<Unit, CopyFloorDrawingsResult?>() {
            override fun createIntent(context: Context, input: Unit?): Intent {
                return Intent(context, BuildingFloorCopyActivity::class.java).apply {
                    putExtra(VAL_COMMON_TITLE,"复制第${mCurFloorId}层图纸到")
                    putExtra(FLOOR_TOTAL_NUM, floorsDataSource.size().toString())
                    putExtra(VAL_UNIT_CONFIG_FLOOR_NUM, mCurFloorId.toString())
                }
            }

            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            override fun parseResult(resultCode: Int, intent: Intent?): CopyFloorDrawingsResult? {
                return if (resultCode == Activity.RESULT_OK && intent != null) {
                    LOG.I("leon","parseResult in")
                    CopyFloorDrawingsResult(
                        selFloorList = intent.getStringExtra("selFloorList")
                    )
                } else {
                    null
                }
            }
        }) { result: CopyFloorDrawingsResult? ->
            if (result != null) {
                LOG.I("leon","sel floors result=" + result.selFloorList)
                result.selFloorList?.let { getFloorDrawingsList(it) }
            }
    }

    private fun getFloorDrawingsList(floorsList: String) {
        var floorCopyBeanList: ArrayList<ProjectFloorConfigCopyBean>
        var floor: Floor? = null
        var floorId: Int? = -1

        var uriList: ArrayList<Uri>? = null
        var curDrawingsItem: DrawingsLauoutItem? = null
        var otherDrawingsItem: DrawingsLauoutItem? = null
        var origiFileName: String? = ""
        var newFileName: String? = ""
        var origiFileNameList: ArrayList<String>? = ArrayList<String>()
        var uri: Uri? = null
        var newUri: Uri? = null
        var dotPos: Int = -1
        var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(requireContext())
        var drawingsList: String = ""

        val gson = Gson()
        val arrayType = object : TypeToken<ArrayList<ProjectFloorConfigCopyBean>>() {}.type
        //get dest floors id
        println("leon mCurFloorId=${mCurFloorId}")
        curDrawingsItem = floorsDataSource.get(mCurFloorId - 1) as DrawingsLauoutItem
        uriList = curDrawingsItem.drawingsUriList
        floorCopyBeanList = gson.fromJson(floorsList, arrayType)
        println("leon uriList=${uriList?.toList()}")
        println("leon floorCopyBeanList=${floorCopyBeanList?.toList()}")

        if (!uriList.isNullOrEmpty() && !floorCopyBeanList.isNullOrEmpty()) {
            for ((index, value) in floorCopyBeanList.withIndex()) {
                drawingsList = ""
                println("leon $index -> $value")
                if (value.isChecked) {
                    floorId = value.id.toInt() - 1
                    var newUriList: ArrayList<Uri> = ArrayList<Uri>()
                    otherDrawingsItem = floorsDataSource.get(floorId) as DrawingsLauoutItem
                    for (i in 0..uriList.size - 1) {
                        uri = uriList.get(i)
                        println("leon uri toString=${uri.toString()}")
                        println("leon uri.path =${uri.path}")
                        origiFileName = FileUtil.uriToFileName(uri, requireContext())
                        println("leon origiFileName =${origiFileName}")
                        dotPos = origiFileName.lastIndexOf('.')
                        if (dotPos > 0) {
                            newFileName = origiFileName.substring(
                                0,
                                dotPos
                            ) + "-" + (floorId + 1).toString() + "层" + origiFileName.substring(
                                dotPos,
                                origiFileName.length
                            )
                        } else {
                            newFileName = origiFileName + "-" + (floorId + 1) + "层"
                        }

                        if (drawingsList.isNullOrEmpty())
                            drawingsList += newFileName
                        else
                            drawingsList += ", " + newFileName

                        println("leon newFileName=${newFileName}")
                        newUri = Uri.parse(cacheRootDir + mCurDrawingsDir + newFileName)
                        println("leon newUri =${newUri}")
                        println("leon newUri path=${newUri.path}")
                        newUriList.add(newUri)
                    }
//                    otherDrawingsItem.drawingsUriList = newUriList
                    otherDrawingsItem.drawingsUriList = uriList
                    otherDrawingsItem.drawingList = drawingsList
                }
            }
            println("leon floorsDataSource=${floorsDataSource.toList()}")
        } else {
            println("leon no process to floorsDataSource=${floorsDataSource.toList()}")
        }

        floorsDataSource.set(floorsDataSource.toList())

    }

    public fun onBackButtonClicked(){
        println("leon onBackButtonClicked mBldId=" + mBldId)
        if(!mNewBuildingCreated){
            DialogUtil.showMsgDialog(requireContext(),"确定要保存到本地",{
                saveLocalProjectToLocalDB()
                requireActivity().finish()
            }){

            }
        }
        else
        {
            requireActivity().finish()
        }
    }

    private fun saveLocalProjectToLocalDB(){
        println("leon onBackButtonClicked mBldId=" + mBldId)
        //mBldId > 0 意味着本栋楼已经保存到db
        if(!mNewBuildingCreated) {

            var curTime: Long = System.currentTimeMillis()

            //get cur building drawings list
            var bldDrawingsList: ArrayList<Drawing> = ArrayList<Drawing>()

            //dataSource存储东南西北四面墙
            bldDrawingsList.addAll(getDrawingsListToCache(dataSource))
            //dataSource2存储总平面
            bldDrawingsList.addAll(getDrawingsListToCache(dataSource2))
            //        bldDrawingsList.addAll(getDrawingsListToCache(floorsDataSource))
            //Get each floor's drawing list
            var floorList: ArrayList<Floor> = getFloorBeanList(floorsDataSource)
            println("leon floorList :${floorList.toString()}")
            //get cur building
            var buildId: Long = -1

            val buildingList: ArrayList<Building> = ArrayList<Building>()

            var curBuilding = Building(
                buildId,
                mLocalProjectId.toLong(),
                mBldNo,
                mModuleMap.get("BLD_TYPE_NONINHAB"),
                floorList,//各楼层及其图纸list
                bldDrawingsList,//东西南北+总平面图纸
                curTime,
                Config.sUserName,
                "",
                1,
                0
            )

            buildingList.add(curBuilding)
            //get project
            var projectBean: ProjectBean = ProjectBean(
                mLocalProjectId.toLong(),
                "",
                mProLeader!!,
                buildingList,
                null,
                curTime,
                curTime,
                "",
                1,
                0
            )
            println("leon project detail:${projectBean.toString()}")
            //cache drawing file
            copyDrawingsToLocalCache(projectBean)
            //cache to local db
            cacheLocalProjectToLocalDB(projectBean)

            println("leon project saved detail:${projectBean.toString()}")
            onMsg("保存成功！")
        }
    }

    private fun cacheLocalProjectToLocalDB(projectBean:com.sribs.bdd.bean.ProjectBean){
        mFloorPresenter.updateLocalProject(projectBean)
        mNewBuildingCreated = true
        println("leon mBldId 111 =" + mBldId)
    }

    private fun copyDrawingsToLocalCache(project: ProjectBean){

        var drawingsList:ArrayList<Drawing>? = null
        drawingsList = ArrayList<Drawing>()
        if(project != null){

            if(project.buildingList!=null)
            {
                for(i in 0..project.buildingList!!.size-1){
                    project.buildingList!!.get(i).drawingsList?.let { drawingsList.addAll(it) }
                    if(project.buildingList!!.get(i).bldUnitFloorList != null){
                        for(j in 0..(project.buildingList!!.get(i).bldUnitFloorList?.size?:0).minus(1)){
                            var floor:Floor? = null
                            var floorList:ArrayList<Floor> = project.buildingList!!.get(i).bldUnitFloorList as ArrayList<Floor>
                            floor = floorList.get(j)
                            floor?.let { floor.drawingsList?.let { it1 -> drawingsList.addAll(it1) } }

                        }
                    }
                }
            }
        }
        else
        {
            println("leon project is null")
            return
        }

        addDisposable(Observable.fromIterable(drawingsList)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .flatMap{
//                Log.d(TAG, "Init async for file${index + 1}")
                //path to file temp
                val pathFileTemp: String? = it.cacheAbsPath
                val file = File(pathFileTemp)
                if (pathFileTemp != null) {
                    Log.d(TAG, "it.sourceUri=" + it.sourceUri)
                    FileUtil.copyFileTo(requireActivity(), Uri.parse(it.sourceUri), pathFileTemp)
                }
                Observable.just("Done")
            }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                print("leon copyDrawingsToLocalCache =${it}")
            },{
                it.printStackTrace()
            }))
        }

    private fun getDrawingsListToCache(ds: DataSource<Any>): ArrayList<Drawing>{
        var drawingsList: ArrayList<Drawing> = ArrayList<Drawing>()
        var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(requireContext())
        //cacheRootDir + mCurDrawingsDir + newFileName
        var drawingItem: Drawing
        var item: DrawingsLauoutItem
        var index: Int = 0
        var uriList: ArrayList<Uri>?
        var uri:Uri?
        var origiFileName:String
        var newFileName:String
        var cachePath:String
        var dotPos:Int=0
        var curTime: Long = System.currentTimeMillis()
        for(i in 0..ds.size()-1){
            item = ds.get(i) as DrawingsLauoutItem
            uriList = item.drawingsUriList

            if(!uriList.isNullOrEmpty())
            {
                for(j in 0..uriList.size-1){
                    uri = uriList.get(j)
                    origiFileName = FileUtil.uriToFileName(uri, requireContext())
                    dotPos = origiFileName.indexOf('.')
                    if (dotPos > 0) {
                        newFileName = origiFileName.substring(
                            0,
                            dotPos
                        ) + "-" + item.floorName + origiFileName.substring(
                            dotPos,
                            origiFileName.length
                        )
                    } else {
                        newFileName = origiFileName + "-" + item.floorName
                    }
                    cachePath = cacheRootDir + mCurDrawingsDir + newFileName
                    drawingItem = Drawing(
                        index,
                        mLocalProjectId.toLong(),
                        mBuildingId.toLong(),
                        -1,
                        item.id.toLong(),//楼层
                        item.floorName,//楼层名称
                        newFileName,
                        item.floorName,
                        null,
                        FileUtil.getFileExtension(newFileName),
                        uri.toString(),
                        cachePath,
                        curTime,
                        curTime,
                        mProLeader!!,
                        "",
                        1,
                        0
                    )
                    drawingsList.add(drawingItem)
                }
            }
            index ++
        }
        println("leon 00 drawingsList length:${drawingsList.size}")
        for(i in 0..drawingsList.size-1){
            println("leon no.${i} drawings:${drawingsList.get(i).sourceUri}")
        }
        return drawingsList
    }

    //get all Floors and their drawings
    private fun getFloorBeanList(ds: DataSource<Any>): ArrayList<Floor>{
        var drawingsList: ArrayList<Drawing> = ArrayList<Drawing>()
        var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(requireContext())
        //cacheRootDir + mCurDrawingsDir + newFileName
        var drawingItem: Drawing
        var item: DrawingsLauoutItem
        var index: Int = 0
        var uriList: ArrayList<Uri>?
        var uri:Uri?
        var origiFileName:String
        var newFileName:String
        var cachePath:String
        var dotPos:Int=0
        var curTime: Long = System.currentTimeMillis()
        var floorList:ArrayList<Floor> = ArrayList<Floor>()
        for(i in 0..ds.size()-1){
            item = ds.get(i) as DrawingsLauoutItem

            var floorBean: Floor = Floor(
                -1,
                mBuildingId.toLong(),
                mLocalProjectId.toLong(),
                (i+1).toLong(),
                item.floorName
            )

            uriList = item.drawingsUriList
            println("leon floor name=${item.floorName}")
            if(!uriList.isNullOrEmpty())
            {
                var floorDrawingsList: ArrayList<Drawing> = ArrayList<Drawing>()
                for(j in 0..uriList.size-1){
                    uri = uriList.get(j)
                    origiFileName = FileUtil.uriToFileName(uri, requireContext())
                    dotPos = origiFileName.indexOf('.')
                    if (dotPos > 0) {
                        newFileName = origiFileName.substring(
                            0,
                            dotPos
                        ) + "-" + item.floorName + origiFileName.substring(
                            dotPos,
                            origiFileName.length
                        )
                    } else {
                        newFileName = origiFileName + "-" + item.floorName
                    }
                    cachePath = cacheRootDir + mCurDrawingsDir + newFileName
                    drawingItem = Drawing(
                        -1,
                        mLocalProjectId.toLong(),
                        mBuildingId.toLong(),
                        -1,
                        (i+1).toLong(),
                        item.floorName,
                        newFileName,
                        item.floorName,
                        null,
                        FileUtil.getFileExtension(newFileName),
                        uri.toString(),
                        cachePath,
                        curTime,
                        curTime,
                        mProLeader!!,
                        "",
                        1,
                        0
                    )
                    floorDrawingsList.add(drawingItem)
                }
                floorBean.drawingsList = floorDrawingsList
            }
            floorList.add(floorBean)
            index ++
        }
        println("leon 00 drawingsList length:${floorList.size}")
//        for(i in 0..floorList.size-1){
//            println("leon no.${i} drawings:${floorList.get(i).}")
//        }
        return floorList
    }


    override fun initView() {
        LOG.I("leon","ProjectStoreyFragment initView")
        initData()
        bindPresenter()
    }

    private fun initData(){

        mData.bindTagEditView(mData::totalFloor,mBinding.tevTotalFloor)
        .setOnDataChangedListener { field, new ->
            when(field){
                mData::totalFloor.name -> {
                    LOG.I("leon","total floor = ${new}  toInt=${new.toIntOrNull()}")
                    if (new.isNullOrEmpty() )return@setOnDataChangedListener
                    mFloorTotalNum = new.toInt()
                    setFloor(new.toIntOrNull()?:0)
                }
            }
        }
    }

    fun initLocalData(projectId:Int){
        mLocalProjectId = projectId

        LOG.I("leon","ProjectStoreyFragment initLocalData mLocalProjectId=" + mLocalProjectId)
        mPrefs = requireActivity().getSharedPreferences("createProject", Context.MODE_PRIVATE)
        mProName = mPrefs.getString(CUR_PRO_NAME,"")
        mBldNo = mPrefs.getString(CUR_BLD_NO,"")
        mProLeader = mPrefs.getString(CUR_PRO_LEADER,"")

        mCurDrawingsDir = "/" + DRAWING_CACHE_FOLDER + "/" + mProName + "/" + mBldNo + "/"
        FileUtil.makeRecurDirs(requireActivity(), mCurDrawingsDir!!)

        println("leon project name=${mProName} and building No=${mBldNo}")

    }

    override fun initBuildingFloors(bldId: Long, floors: ArrayList<Floor>) {

    }

    override fun initBuildingDrawing(drawings: ArrayList<DrawingBean>) {

    }

    override fun initDrawingDamages(damages: List<DamageBean>) {

    }

    override fun saveDamageDataToDbStarted() {

    }

    override fun updateLocalDamageDetail(dmg: DamageBean?) {

    }

    override fun onRemoveDamageInDrawing(dmg: DamageBean) {

    }

    override fun bindPresenter() {
//       mPresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg)
    }

    override fun unbindPresenter() {
//       mPresenter.unbindView()
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
        requireActivity().startActivityForResult(intent, requestCode);

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LOG.I("leon", " resultCode=" + resultCode)
        LOG.I("leon", " requestCode=" + requestCode)
        LOG.I("leon", " data=" + data.toString())

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
                            LOG.I("leon 01", "uri=" + uri.toString())

                            uriList.add(uri!!)
                            LOG.I("leon 01", "fileNameString=" + fileNameString)
                            origiNameString = FileUtil.uriToFileName(uri!!, requireContext())
//                            origiNameString = FileUtil.generateNewFileName(origiNameString, mCurFloorId.toString())
                            if (!fileNameString.isNullOrEmpty())
                                fileNameString += ", " + origiNameString
                            else
                                fileNameString = origiNameString
                        }
                        index++
                    }
                } else {
                    // Getting the URI of the selected file and logging into logcat at debug level
                    LOG.I("leon 02","fileNameString=" + fileNameString)
                    uri = data.data
                    uri?.let {
                        println("leon uri 00=: $uri")
                        uriList.add(uri)
                        LOG.I("leon 02","uri=" + uri.toString())
                        origiNameString = FileUtil.uriToFileName(uri, requireContext())
//                        origiNameString = FileUtil.generateNewFileName(origiNameString, mCurFloorId.toString())
                        if(!fileNameString.isNullOrEmpty())
                            fileNameString += ", " + origiNameString
                        else
                            fileNameString = origiNameString
                    }

            }

            var pos = requestCode - ModuleHelper.FILE_PICKER_REQUEST_CODE_EAST_FACADE
            println("leon pos=$pos")

            if(pos in 0..3){//东西南北四面墙
//                mDrawingName?.set(requestCode - ModuleHelper.FILE_PICKER_REQUEST_CODE_EAST_FACADE, fileNameString)
                var item: DrawingsLauoutItem = dataSource.get(pos) as DrawingsLauoutItem
                item.drawingList = fileNameString
                item.drawingsUriList = uriList
                dataSource.set(
                    newItems = dataSource.toList()
                )
                LOG.I("leon","dataSource toString=" + dataSource.toList().toString())
            }
            else if(pos == 4){//底层
                var item: DrawingsLauoutItem = dataSource2.get(0) as DrawingsLauoutItem
                item.drawingList = fileNameString
                item.drawingsUriList = uriList
                dataSource2.set(
                    newItems = dataSource2.toList()
                )
                LOG.I("leon","dataSource2 toString=" + dataSource2.toString())
            }
            else if(pos <= mFloorTotalNum + ModuleHelper.FILE_PICKER_REQUEST_CODE_FLAT_FACADE - ModuleHelper.FILE_PICKER_REQUEST_CODE_EAST_FACADE){//各层
                if(fileNameString.isNullOrEmpty())
                    return
                var index = requestCode - ModuleHelper.FILE_PICKER_REQUEST_CODE_FLAT_FACADE
                LOG.I("leon","fileNameString="+fileNameString+", index="+index)
                var size: Int = floorsDataSource.size()
                if(index < size){
                    var floorDrawingItem: DrawingsLauoutItem = floorsDataSource.get(index) as DrawingsLauoutItem
                    floorDrawingItem.drawingList = fileNameString!!
                    floorDrawingItem.drawingsUriList = uriList

                    floorsDataSource.set(newItems = floorsDataSource.toList())
                    LOG.I("leon","floorsDataSource toList=" + floorsDataSource.toList().toString())
                }
            }
        }
    }
    }

    private fun setFloor(num:Int){
        val parent = mBinding.floorDrawingsList
        val cont = parent.childCount
        parent.removeAllViews()
        when {
            num-cont>0 -> { // 添加
                // 从原来 cont-1开始加
                for(index in cont..num-1){
                    //设置各层图纸
                    var floorDrawingItem: DrawingsLauoutItem = DrawingsLauoutItem(
                        (index+1),
                        (index+1).toString()+"层",
                        "",
                        "上传图纸",
                        "复制本层",
                        null,
                        false
                        )
                    floorsDataSource.add(floorDrawingItem)

                    LOG.I("leon","dataSource size=" + dataSource.size())

                }
            }
            num-cont<0 -> { // 减少
                for(index in cont-1 downTo num)
                {
//                    LOG.I("leon","index="+index+"，num="+num+", cont="+cont)
                    floorsDataSource.removeAt(index)
                    LOG.I("leon","removed index="+index+"，num="+num+", cont="+cont)
//                    dataSource3.set(newItems = dataSource3.toList())
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        LOG.I("leon", "ProjectStoreyFragment onPause")
    }

    override fun onResume() {
        super.onResume()
        LOG.I("leon", "ProjectStoreyFragment onResume")
    }

    override fun onStop() {
        super.onStop()
        LOG.I("leon", "ProjectStoreyFragment onStop")
    }
}

data class CopyFloorDrawingsResult(
    val selFloorList: String?
)