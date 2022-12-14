package com.sribs.bdd.v3.ui.check

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.sribs.common.module.BasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.cbj.sdk.utils.NumberUtil
import com.radaee.util.CommonUtil
import com.sribs.bdd.bean.data.ModuleFloor
import com.sribs.bdd.bean.data.ModuleFloorBean
import com.sribs.bdd.bean.data.ModuleFloorPictureBean
import com.sribs.bdd.module.project.IProjectContrast

import com.sribs.bdd.v3.adapter.CreateModuleFloorAdapter
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.bdd.utils.UUIDUtil
import com.sribs.bdd.v3.adapter.CreateNonResidentModuleAdapter
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.bean.db.v3.project.v3BuildingModuleDbBean
import com.sribs.common.bean.v3.v3ModuleFloorDbBean
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.FileUtil
import com.sribs.common.utils.TimeUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ModuleFloorConfigCreateTypePresenter : BasePresenter(), IProjectContrast.IProjectCreateTypePresenter,
    CreateModuleFloorAdapter.ICallback,CreateNonResidentModuleAdapter.ICallback {
    private var mView: IProjectContrast.IModuleCreateTypeView? = null

    private var array: ArrayList<ModuleFloorBean>? = null

    private var nonResidentList: ArrayList<ModuleFloorBean>? = null

    private var above = ArrayList<ModuleFloorBean>()
    private var before = ArrayList<ModuleFloorBean>()

    private var mProLeader: String? = ""

    var mAboveOldIndex = 0

    var mBeforeOldIndex = 0

    var isFirstCome :Boolean = true //????????????????????????????????? ??????????????????????????????
    var isFirstCome2 :Boolean = true //????????????????????????????????? ??????????????????????????????

    var finalNonResidentDrawingList = arrayListOf<DrawingV3Bean>()



    var beanList: ArrayList<ModuleFloorBean>? = ArrayList()

    var isNonResident =false

    private var oldAboveList = ArrayList<ModuleFloorBean>()
    private var oldBeforeList = ArrayList<ModuleFloorBean>()

    private val floorAdapter by lazy {
        CreateModuleFloorAdapter(this,mView?.getContext())
    }

    private val nonResidentAdapter by lazy {
        CreateNonResidentModuleAdapter(this,mView?.getContext())
    }

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun addAboveFlourList(num: Int) {

        if (isFirstCome){
            isFirstCome = false
            return
        }

        LogUtils.d("addAboveFlourList??? " + num)

        if (num == 0) {
            array?.removeAll(above)
            above.clear()
            floorAdapter.notifyDataSetChanged()
        } else if (num > 0) {
            array?.removeAll(above)
            above.clear()
            for (i in 0 until num) {
                var name = NumberUtil.num2Chinese(i + 1)
                var buildingFloorBean = ModuleFloorBean(UUIDUtil.getUUID(name + "???"),name + "???", arrayListOf(), "??????",1,i)

                for (j in 0 until oldAboveList.size){
                    if (buildingFloorBean.floorIndex ==oldAboveList.get(j).floorIndex)
                    {
                        buildingFloorBean.pictureList =  oldAboveList.get(j).pictureList
                        buildingFloorBean.name =  oldAboveList.get(j).name
                    }
                }
                above.add(buildingFloorBean)
            }
            array?.addAll(above)
            floorAdapter.notifyDataSetChanged()
            oldAboveList.clear()
            oldAboveList.addAll(above)

        }else {
            mView?.onMsg("????????????????????????")
        }
    }


    //??????????????????
    @SuppressLint("NotifyDataSetChanged")
    override fun addAfterFlourList(num: Int) {

        if (isFirstCome2){
            isFirstCome2 = false
            return
        }

        LogUtils.d("addAfterFlourList??? " + num)
        if (num == 0) {
            array?.removeAll(before)
            before.clear()
            floorAdapter.notifyDataSetChanged()
        } else if (num > 0) {
            array?.clear()
            before.clear()

            for (i in 0 until num) {
                var name = NumberUtil.num2Chinese(i + 1)
                var buildingFloorBean = ModuleFloorBean(UUIDUtil.getUUID("???"+name + "???"),"???"+name + "???", arrayListOf(), "??????",0,i)
                for (j in 0 until oldBeforeList.size){
                    if (buildingFloorBean.floorIndex ==oldBeforeList.get(j).floorIndex)
                    {
                        buildingFloorBean.pictureList =  oldBeforeList.get(j).pictureList
                        buildingFloorBean.name =  oldBeforeList.get(j).name
                    }
                }
                before.add(buildingFloorBean)
            }
            array?.addAll(before)
            array?.addAll(above)
            floorAdapter.notifyDataSetChanged()

            oldBeforeList.clear()
            oldBeforeList.addAll(before)

        } else {
            mView?.onMsg("????????????????????????")
        }
    }

    fun setIsNonResident(isNonResident:Boolean ){
        this.isNonResident = isNonResident
    }

    fun initFlourLits() {
        var manager = LinearLayoutManager(mView?.getContext())
        mView?.getFloorRecycleView()?.layoutManager = manager
        array = ArrayList()
        array!!.addAll(before)
        array!!.addAll(above)

        floorAdapter.setData(array!!)
        mView?.getFloorRecycleView()?.adapter = floorAdapter

    }

    private fun initNonResidentPicList() {
        var manager = LinearLayoutManager(mView?.getContext())
        mView?.getNonResidentRecycleView()?.layoutManager = manager
        nonResidentList = ArrayList()

        nonResidentAdapter.setData(nonResidentList!!)
        mView?.getNonResidentRecycleView()?.adapter = nonResidentAdapter

    }

    fun setNonResidentData(list: List<v3ModuleFloorDbBean>){
        nonResidentList!!.clear()
        list.forEachIndexed { index, it ->
            if (index==0){


            var beanPicList: ArrayList<ModuleFloorPictureBean>? = ArrayList()
            it.drawingsList?.filter { "east".equals(it.drawingType) }?.forEach { b ->
                beanPicList!!.add(
                    ModuleFloorPictureBean(
                        drawingId = b.drawingID,
                        name = b.fileName!!,
                        uri = "",
                        url = b.localAbsPath,
                    )
                )
            }
            nonResidentList!!.add(
                ModuleFloorBean(
                    floorId = it.floorId!!,
                    name = "?????????",
                    pictureList = beanPicList,
                    floor = "",
                    floorType = it.floorType,
                    floorIndex = it.floorIndex
                )
            )
            beanPicList = arrayListOf()
            beanPicList!!.clear()
            it.drawingsList?.filter { "west".equals(it.drawingType) }?.forEach { b ->
                beanPicList!!.add(
                    ModuleFloorPictureBean(
                        drawingId = b.drawingID,
                        name = b.fileName!!,
                        uri = "",
                        url = b.localAbsPath,
                    )
                )
            }
            nonResidentList!!.add(
                ModuleFloorBean(
                    floorId = it.floorId!!,
                    name = "?????????",
                    pictureList = beanPicList,
                    floor = "",
                    floorType = it.floorType,
                    floorIndex = it.floorIndex
                )
            )

            beanPicList = arrayListOf()
            beanPicList!!.clear()
            it.drawingsList?.filter { "south".equals(it.drawingType) }?.forEach { b ->
                beanPicList!!.add(
                    ModuleFloorPictureBean(
                        drawingId = b.drawingID,
                        name = b.fileName!!,
                        uri = "",
                        url = b.localAbsPath,
                    )
                )
            }
            nonResidentList!!.add(
                ModuleFloorBean(
                    floorId = it.floorId!!,
                    name = "?????????",
                    pictureList = beanPicList,
                    floor = "",
                    floorType = it.floorType,
                    floorIndex = it.floorIndex
                )
            )
            beanPicList = arrayListOf()
            beanPicList!!.clear()
            it.drawingsList?.filter { "north".equals(it.drawingType) }?.forEach { b ->
                beanPicList!!.add(
                    ModuleFloorPictureBean(
                        drawingId = b.drawingID,
                        name = b.fileName!!,
                        uri = "",
                        url = b.localAbsPath,
                    )
                )
            }
            nonResidentList!!.add(
                ModuleFloorBean(
                    floorId = it.floorId!!,
                    name = "?????????",
                    pictureList = beanPicList,
                    floor = "",
                    floorType = it.floorType,
                    floorIndex = it.floorIndex
                )
            )

            beanPicList = arrayListOf()
            beanPicList!!.clear()
            it.drawingsList?.filter { "plane".equals(it.drawingType) }?.forEach { b ->
                beanPicList!!.add(
                    ModuleFloorPictureBean(
                        drawingId = b.drawingID,
                        name = b.fileName!!,
                        uri = "",
                        url = b.localAbsPath,
                    )
                )
            }
            nonResidentList!!.add(
                ModuleFloorBean(
                    floorId = it.floorId!!,
                    name = "?????????",
                    pictureList = beanPicList,
                    floor = "",
                    floorType = it.floorType,
                    floorIndex = it.floorIndex
                )
            )
        }
        }
        refeshNonResidentData()

    }

    fun setData(list: List<v3ModuleFloorDbBean>) {
        beanList!!.clear()
        list.forEach {
            var beanPicList: ArrayList<ModuleFloorPictureBean>? = ArrayList()
            it.drawingsList?.filter { "floor".equals(it.drawingType)}?.forEach { b ->
                beanPicList!!.add(
                    ModuleFloorPictureBean(
                        drawingId = b.drawingID,
                        name = b.fileName!!,
                        uri = "",
                        url = b.localAbsPath,
                    )
                )
            }
            beanList!!.add(
                ModuleFloorBean(
                    floorId = it.floorId!!,
                    name = it.floorName!!,
                    pictureList = beanPicList,
                    floor = "",
                    floorType = it.floorType,
                    floorIndex = it.floorIndex
                )
            )
        }

        array?.clear()
        before.addAll(beanList!!.filter {
            it.floorType == 0
        })

        oldBeforeList.addAll(beanList!!.filter {
            it.floorType == 0
        })
        above.addAll(beanList!!.filter {
            it.floorType == 1
        })
        oldAboveList.addAll(beanList!!.filter {
            it.floorType == 1
        })

        array?.addAll(before)
        array?.addAll(above)

        refeshData()


    }

    fun refeshData() {
        floorAdapter.notifyDataSetChanged()
    }

    fun refeshNonResidentData() {
        nonResidentAdapter.notifyDataSetChanged()
    }

   private fun initLocalData(projectId: Long, buildingId: Long, moduleId: Long) {
        addDisposable(
            mDb.getv3ModuleFloor(projectId, buildingId, moduleId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    LogUtils.d("caocaocaocao1111")
                    if (it != null)
                        mView?.initLocalData(it)

                }, {

                    it.printStackTrace()
                })
        )
    }

    fun initLocalData(projectId: Long, buildingId: Long, moduleId: Long,isNonResident:Boolean){
        if (isNonResident){
            addDisposable(
                mDb.getv3BuildingModuleOnce(projectId, buildingId, "??????????????????")
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation())
                    .subscribe({

                        var list = arrayListOf<v3ModuleFloorDbBean>()
                        it.forEach {
                            var v3ModuleFloorDbBean = v3ModuleFloorDbBean(
                                floorId ="null",
                                floorName = "null",
                                floorType = -1,
                                floorIndex = -1,
                                drawingsList = it.drawings
                            )
                            list.add(v3ModuleFloorDbBean)
                        }
                        LogUtils.d("caocaocaocao3333")
                        LogUtils.d("??????????????????????????????????????????11111 " + it.toString())
                        LogUtils.d("??????????????????????????????????????????22222 " + list.toString())
                        getModuleFloorInfo(projectId,buildingId,moduleId,list)

                    }, {
                        getModuleFloorInfo(projectId,buildingId,moduleId,ArrayList())
                        LogUtils.d("?????????????????????????????????????????? ${it}")

                    })
            )
        }
        else {
        initLocalData(projectId, buildingId, moduleId)
             }
        }

     fun getModuleFloorInfo(
        localProjectId: Long,
        localBuildingId: Long,
        localModuleId: Long,
        localList:ArrayList<v3ModuleFloorDbBean>
    ) {
        LogUtils.d("getModuleFloorInfo ${localProjectId}  ${localBuildingId}   ${localModuleId}")
        addDisposable(mDb.getv3ModuleFloor(localProjectId,localBuildingId,localModuleId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                localList.addAll(it)
                LogUtils.d("caocaocaocao22222")
                LogUtils.d("?????????????????????????????? "+localList.toString())

                mView?.initLocalData(localList)
                LogUtils.d("caocaocaocao44444")
                dispose()
            },{
                dispose()
                mView?.onMsg(it.toString())
                LogUtils.d("???????????????????????????????????? ${it}")
            }))
    }


    fun createLocalModule(
        activity: Activity,
        mLocalProjectId: Int,
        mBuildingId: Long,
        mModuleId: Long,
        moduleName: String,
    ) {

        if (array.isNullOrEmpty()) {
            mView?.onMsg("??????????????????")
            return
        }

        array!!.forEach {
            if (it.pictureList.isNullOrEmpty()) {
                mView?.onMsg("????????????????????????")
                return
            }
        }

        if (isNonResident) {
            nonResidentList!!.forEach {
                if (it.pictureList == null || it.pictureList!!.size == 0) {
                    mView?.onMsg("???????????????/???????????????????????????")
                    return
                }
            }
        }

        //???????????????????????????????????????
        mCurDrawingsDir =
            "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + mProName + "/" + moduleName + "/"

        addDisposable(mDb.getModuleFloorByModule(mModuleId)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({ moduleFloorList ->
                dispose()
                getFloorList(
                    activity,
                    mLocalProjectId,
                    mBuildingId,
                    mModuleId,
                    above.size,
                    before.size,
                    moduleFloorList
                )
            if (isNonResident) {
                mDb.getv3BuildingModule(mModuleId)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation())
                    .subscribe({
                        LogUtils.d("???????????????" + it.toString())
                        getNonResidentDrawingList(activity, it)
                        LogUtils.d(""+finalNonResidentDrawingList.hashCode()+"????????????"+finalNonResidentDrawingList.toString())
                        dispose()
                        mDb.deletev3ModuleFloor(mLocalProjectId.toLong(), mBuildingId, mModuleId)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(Schedulers.computation())
                            .subscribe({
                                LogUtils.d(""+finalNonResidentDrawingList.hashCode()+"????????????333"+finalNonResidentDrawingList.toString())
                                LogUtils.d("createLocalModule new building id=${mModuleId}")
                                createLocalFloorsInTheModule(
                                    mBuildingId,
                                    mModuleId,
                                    moduleName
                                )
                                dispose()
                            }, {
                                dispose()
                            })
                    }, {
                        dispose()
                    })
            }
            },{
                dispose()
            })
        )



    }

    private fun createLocalFloorsInTheModule(
        mBuildingId: Long,
        moduleId: Long,
        moduleName:String
    ) {
        println("createLocalFloorsInTheBuilding mBldId=${mBuildingId}")

        var index = 0
        floorList.forEach {
            var bean = v3ModuleFloorDbBean(
                projectId = it.projectId,
                bldId = it.bldId,
                moduleId = it.moduleId,
                floorId = it.floorId,
                floorName = it.floorName,
                floorType = it.floorType,
                floorIndex = it.floorIndex,
                drawingsList = it.floorList,
                deleteTime = "",
                aboveNumber = it.aboveNumber,
                afterNumber = it.afterNumber,
                createTime = TimeUtil.YMD_HMS.format(Date()),
                updateTime = TimeUtil.YMD_HMS.format(Date()),
                status = 0,
            )

            mDb.updatev3ModuleFloor(bean)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe({
                    LogUtils.d("?????????????????????: ${it}")
                    index++
                    if(index == floorList.size){
                        mDb.updateBuildingModule(moduleId,1)
                        if (isNonResident){
                            dispose()
                            updateNonResidentBuildingModule(moduleId)
                        }else{
                            mView?.createModuleConfigSuccess()
                            dispose()
                        }
                    }
                },{
                    mView?.onMsg("??????????????????????????????: "+it)
                    dispose()
                    it.printStackTrace()
                })
        }
    }


    private fun updateNonResidentBuildingModule(moduleId: Long){
        LogUtils.d("updateNonResidentBuildingModule:"+finalNonResidentDrawingList!!.toString())
        mDb.updateBuildingModule(moduleId,finalNonResidentDrawingList!!,1)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({
                    mView?.createModuleConfigSuccess()
                dispose()
                },{
                dispose()
                mView?.onMsg("??????????????????????????????: "+it)
                it.printStackTrace()
            })
    }

    var floorList: ArrayList<ModuleFloor> = ArrayList<ModuleFloor>()

    fun getFloorList(
        activity: Activity,
        mLocalProjectId: Int,
        mBuildingId: Long,
        moduleId: Long,
        aboveGroundNumber: Int,
        underGroundNumber: Int,
        v3ModuleFloorDbBean:List<v3ModuleFloorDbBean>
    ) {

        LogUtils.d("????????????????????????????????????: "+v3ModuleFloorDbBean)

        LogUtils.d("?????????????????????: "+array)

        if (array != null && array!!.size > 0) {
            for (i in array!!.indices) {
                var item = array!![i]

                var moduleFloor = ModuleFloor(
                    id = -1,
                    projectId = mLocalProjectId.toLong(),
                    bldId = mBuildingId,
                    moduleId = moduleId,
                    floorId = item.floorId,
                    floorName = item.name,
                    floorType = item.floorType,
                    floorIndex = item.floorIndex,
                    aboveNumber = aboveGroundNumber,
                    afterNumber = underGroundNumber,
                )

                moduleFloor.floorList = getDrawingList(activity, item,v3ModuleFloorDbBean)
                floorList.add(moduleFloor)
            }
        }
    }

    fun getDrawingList(
        activity: Activity,
        floorBean: ModuleFloorBean,
        v3ModuleFloorDbBean:List<v3ModuleFloorDbBean>
    ): ArrayList<DrawingV3Bean>? {
        if (floorBean.pictureList != null && floorBean.pictureList!!.size > 0) {
            var cachePath: String
            var originList = floorBean.pictureList
            var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)
            var floorDrawingsList: ArrayList<DrawingV3Bean> = ArrayList<DrawingV3Bean>()
            var drawingItem: DrawingV3Bean
            var curTime: Long = System.currentTimeMillis()

            copyDrawingsToLocalCache(activity, originList!!,floorBean.name, cacheRootDir)

            originList.forEachIndexed { index, item ->
                var name =""
                if(!item.name.endsWith("pdf")){
                    name = item.name.replace(".","")+".pdf"
                }else{
                    name = item.name
                }

                var cacheFilePath = File(cacheRootDir + mCurDrawingsDir+floorBean.name+"/"+index,name)
                LogUtils.d("???????????????????????????" + cacheFilePath)

                var damageList = ArrayList<DamageV3Bean>()

                LogUtils.d("?????????xinxi : "+item)

                v3ModuleFloorDbBean.forEach { moduleFloor->
                    if(floorBean.floorId == moduleFloor.floorId){
                        LogUtils.d("???????????????: "+floorBean.floorId)
                        moduleFloor.drawingsList?.forEach { drawing->
                            if(item.drawingId == drawing.drawingID){
                                LogUtils.d("?????????????????????: "+item.drawingId)
                                damageList = drawing.damage?:ArrayList()
                                LogUtils.d("?????????: "+damageList)
                            }
                        }
                    }
                }

                drawingItem = DrawingV3Bean(
                    item.drawingId!!,
                    name,
                    FileUtil.getFileExtension(name),
                    "floor",
                    cacheFilePath.absolutePath,
                    "",
                    floorBean.name,
                    damageList
                )
                floorDrawingsList.add(drawingItem)
            }
            return floorDrawingsList
        }

        return ArrayList()
    }



    fun getNonResidentDrawingList( activity: Activity,v3BuildingModuleDbBean: v3BuildingModuleDbBean){
        finalNonResidentDrawingList!!.clear()
        var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)
        nonResidentList!!.forEach {
            copyDrawingsToLocalCache(activity,it.pictureList!!,null,cacheRootDir)
        }

        nonResidentList!!.forEachIndexed { index, moduleFloorBean ->
            moduleFloorBean.pictureList!!.forEachIndexed { picIndex,it->
                var name =""
                if(!it.name.endsWith("pdf")){
                    name = it.name.replace(".","")+".pdf"
                }else{
                    name = it.name
                }
                var cacheFilePath = File(cacheRootDir + mCurDrawingsDir,name)
                var drawingType = ""
                when (index){
                    0-> drawingType = "east"
                    1-> drawingType = "west"
                    2-> drawingType = "south"
                    3-> drawingType = "north"
                    4-> drawingType = "plane"
                }

                var damageList = ArrayList<DamageV3Bean>()

                v3BuildingModuleDbBean.drawings!!.forEach {
                    if (it.drawingID!!.equals(moduleFloorBean.pictureList!!.get(picIndex))){
                        damageList = it.damage?:ArrayList()
                    }
                }

                var drawingV3ToBuilding = DrawingV3Bean(
                    it.drawingId!!,
                    name,
                    FileUtil.getFileExtension(name),
                    drawingType,
                    cacheFilePath.absolutePath,
                    "",
                    "",
                    damageList
                )
                finalNonResidentDrawingList!!.add(drawingV3ToBuilding)
             //   mAppFacadeDrawingList!!.add(drawingV3ToBuild)
            }
        }
    }







    @RequiresApi(Build.VERSION_CODES.O)
    private fun copyDrawingsToLocalCache(
        activity: Activity,
        pictureBean: ArrayList<ModuleFloorPictureBean>,
        floorName:String?,
        cacheRootDir: String
    ) {
        LogUtils.d("copyDrawingsToLocalCache: " + pictureBean.size)
      /*  var filters = pictureBean.filter {
            it.uri != null
        }
        LogUtils.d("??????uri?????????null???: " + filters)*/

        var name= ""
        var needToPDF = false
        var cacheFileParent =File("")
        var cacheFile = File("")

        pictureBean.forEachIndexed { index, it ->

            var cacheFileParent = File(cacheRootDir + mCurDrawingsDir)
            if(!floorName.isNullOrEmpty()) { // ????????????????????????   ??????????????????????????????
                cacheFileParent = File(cacheRootDir + mCurDrawingsDir + floorName+"/"+index)
            }
            cacheFileParent.mkdirs()

            if(!it.name.endsWith("pdf")){
                name = it.name.replace(".","")+".pdf"
                needToPDF =true

            }else{
                name = it.name
                needToPDF =false
            }
            cacheFile = File(cacheFileParent,name)
            if (cacheFile != null) {
                if (needToPDF){
                    if (it.uri==null){
                        //  FileUtil.copyTo(File(it.url),File(cacheFileParent,it.name))
                        CommonUtil.imageToPDF(it.url,cacheFile.absolutePath)

                        LogUtils.d("url: "+cacheFile.absolutePath)
                    }else{
                        //   CommonUtil.imageToPDF(,cacheFile.absolutePath)
                        FileUtil.copyFileTo(activity, Uri.parse(it.uri),File(cacheFileParent,it.name).absolutePath)
                        CommonUtil.imageToPDF(File(cacheFileParent,it.name).absolutePath,cacheFile.absolutePath)
                    }
                    //       CommonUtil.imageToPDF(File(defaultName).absolutePath,cacheFile.absolutePath)

                }else{
                    if(!it.uri.isNullOrEmpty()) {
                        FileUtil.copyFileTo(activity, Uri.parse(it.uri), cacheFile.absolutePath)
                    }else{
                        FileUtil.copyTo(File(it.url),cacheFile)
                    }
                }

            }
            LogUtils.d("???????????????????????????: ${cacheFile}")
        }

    }


    private lateinit var mPrefs: SharedPreferences
    private var mCurDrawingsDir: String? = ""
    private var mProName: String? = ""
    private var mBldNo: String? = ""

    override fun bindView(v: IBaseView) {
        mView = v as IProjectContrast.IModuleCreateTypeView
        mPrefs = mView!!.getContext()!!.getSharedPreferences("createProject", Context.MODE_PRIVATE)
        mProLeader = mPrefs.getString(ModuleHelper.CUR_PRO_LEADER, "")
        mProName = mPrefs.getString(ModuleHelper.CUR_PRO_NAME, "")
        mBldNo = mPrefs.getString(ModuleHelper.CUR_BLD_NO, "")
        mCurDrawingsDir =
            "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + mProName + "/" + mBldNo + "/"
        initFlourLits()

        if (isNonResident) {
            initNonResidentPicList()
        }
        //   initPicLits()
    }


    override fun unbindView() {
        mView = null
    }

    override fun choseNonResidentPic(bean: ModuleFloorBean) {
        mView?.choseNonResidentPic(bean)
    }

    override fun takeNonResidentPhoto(bean: ModuleFloorBean) {
        mView?.takeNonResidentPhoto(bean)
    }

    override fun showNonResidentWhite(bean: ModuleFloorBean) {
        mView?.choseNonResidentWhite(bean)
    }

    override fun chosePic(bean: ModuleFloorBean) {
        mView?.chosePic(bean)
    }

    override fun takePhoto(bean: ModuleFloorBean) {
        mView?.takePhoto(bean)
    }

    override fun showWhite(bean: ModuleFloorBean) {
        mView?.choseWhite(bean)
    }


    override fun deleteModuleFloor(bean: ModuleFloorBean, position: Int) {

        when (bean.floorType) {
            1 -> {
                above.removeAt(position - before.size)
            }
            0 -> {
                before.removeAt(position)
            }
        }
        array!!.clear()
        array!!.addAll(before)
        array!!.addAll(above)

        floorAdapter.notifyDataSetChanged()
        Log.e("TAG", "deleteBuildingFloor: "+array )
        mView?.deleteModuleFloor(bean.floorType, above.size, before.size)
    }




}