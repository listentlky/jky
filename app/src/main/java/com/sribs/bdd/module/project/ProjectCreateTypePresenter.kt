package com.sribs.bdd.module.project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.cbj.sdk.utils.NumberUtil
import com.radaee.util.CommonUtil
import com.sribs.bdd.Config
import com.sribs.bdd.action.Dict
import com.sribs.bdd.bean.BuildingFloorBean
import com.sribs.bdd.bean.BuildingFloorPictureBean
import com.sribs.bdd.bean.Floor
import com.sribs.bdd.ui.adapter.CreateFloorAdapter
import com.sribs.bdd.ui.adapter.CreateFloorPictureAdapter
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.bdd.utils.UUIDUtil
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.bean.db.FloorBean
import com.sribs.common.bean.net.v3.V3BuildingSaveReq
import com.sribs.common.module.BasePresenter
import com.sribs.common.net.HttpApi
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.FileUtil
import com.sribs.common.utils.TimeUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ProjectCreateTypePresenter : BasePresenter(), IProjectContrast.IProjectCreateTypePresenter,
    CreateFloorAdapter.ICallback {
    private var mView: IProjectContrast.IProjectCreateTypeView? = null

    private var array: ArrayList<BuildingFloorBean>? = null

    private var picList: ArrayList<BuildingFloorPictureBean>? = null

    private var above = ArrayList<BuildingFloorBean>()
    private var before = ArrayList<BuildingFloorBean>()

    private var mProLeader: String? = ""

    var mAboveOldIndex = 0

     var mBeforeOldIndex = 0

    private val flourAdapter by lazy {
        CreateFloorAdapter(this)
    }

    private val picAdapter by lazy {
        CreateFloorPictureAdapter()
    }

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun addAboveFlourList(num: Int) {
        LogUtils.d("addAboveFlourList： " + num)

        if (num == 0) {
            array?.removeAll(above)
            flourAdapter.notifyDataSetChanged()
        } else if (num > 0) {
            array?.removeAll(above)

            if (mAboveOldIndex == 0 && above.size < 1) {
                for (i in 0 until num) {
                    var name = NumberUtil.num2Chinese(i + 1)
                    var buildingFloorBean = BuildingFloorBean(name + "层", arrayListOf(), "地上",1)
                    above.add(buildingFloorBean)
                }
                array?.addAll(above)
                flourAdapter.notifyDataSetChanged()
                mAboveOldIndex = num
                return
            }

            if (mAboveOldIndex > 0 && mAboveOldIndex == num) {
                array?.addAll(above)
                flourAdapter.notifyDataSetChanged()
                mAboveOldIndex = num
                return
            }

            if (mAboveOldIndex > 0 && mAboveOldIndex > num) {
                for (i in 0 until mAboveOldIndex - num) {
                    above.removeAt(above.size - 1)
                }
                array?.addAll(above)
                flourAdapter.notifyDataSetChanged()
           //     flourAdapter.notifyItemChanged(above.size-1)
                mAboveOldIndex = num
                return

            }

            if (mAboveOldIndex > 0 && mAboveOldIndex < num) {
                for (i in mAboveOldIndex until num) {
                    var name = NumberUtil.num2Chinese(1 + i)
                    var buildingFloorBean = BuildingFloorBean(name + "层", arrayListOf(), "地上",1)
                    above.add(buildingFloorBean)
                }
                array?.addAll(above)
                flourAdapter.notifyDataSetChanged()
              //  flourAdapter.notifyItemInserted(before.size+above.size)
              //  flourAdapter.notifyItemRangeChanged(before.size+above.size-1,before.size+above.size)
                mAboveOldIndex = num
                return
            }
        }else {
            mView?.onMsg("请输入正确的数字")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun addAfterFlourList(num: Int) {
        LogUtils.d("addAfterFlourList： " + num)
        if (num == 0) {
            array?.removeAll(before)
            flourAdapter.notifyDataSetChanged()
        } else if (num > 0) {
            array?.clear()

            if (mBeforeOldIndex == 0 && before.size < 1) {
                for (i in 0 until num) {
                    var name = NumberUtil.num2Chinese(i + 1)
                    var buildingFloorBean = BuildingFloorBean("负"+name + "层", arrayListOf(), "地下",0)
                    before.add(buildingFloorBean)
                }
                array?.addAll(before)
                array?.addAll(above)
                flourAdapter.notifyDataSetChanged()
                mBeforeOldIndex = num
                return
            }

            if (mBeforeOldIndex > 0 && mBeforeOldIndex == num) {
                array?.addAll(before)
                array?.addAll(above)
                flourAdapter.notifyDataSetChanged()
                mBeforeOldIndex = num
                return
            }

            if (mBeforeOldIndex > 0 && mBeforeOldIndex > num) {
                for (i in 0 until mBeforeOldIndex - num) {
                    before.removeAt(before.size - 1)
                }
                array?.addAll(before)
                array?.addAll(above)
                flourAdapter.notifyDataSetChanged()
             //   flourAdapter.notifyItemChanged(before.size-1)
                mBeforeOldIndex = num
                return

            }

            if (mBeforeOldIndex > 0 && mBeforeOldIndex < num) {
                for (i in mBeforeOldIndex until num) {
                    var name = NumberUtil.num2Chinese(1 + i)
                    var buildingFloorBean = BuildingFloorBean("负"+name + "层", arrayListOf(), "地下",0)
                    before.add(buildingFloorBean)
                }
                array?.addAll(before)
                array?.addAll(above)
                flourAdapter.notifyDataSetChanged()
             //   flourAdapter.notifyItemInserted(before.size)
                mBeforeOldIndex = num
                return
            }
        } else {
            mView?.onMsg("请输入正确的数字")
        }
    }


    fun initFlourLits() {
        var manager = LinearLayoutManager(mView?.getContext())
        mView?.getFlourRecycleView()?.layoutManager = manager
        array = ArrayList()

        array!!.addAll(before)
        array!!.addAll(above)
    //    flourAdapter.setHasStableIds(true)
        flourAdapter.setData(array!!)
        mView?.getFlourRecycleView()?.adapter = flourAdapter

    }

    fun refeshData() {
        flourAdapter.notifyDataSetChanged()
    }

    fun initPicLits() {
        var manager = LinearLayoutManager(mView?.getContext())
        mView?.getPicRecycleView()?.layoutManager = manager
        picList = ArrayList()
        picAdapter.setData(picList!!)
        mView?.getPicRecycleView()?.adapter = picAdapter

    }

    fun refreshPicList(mData: ArrayList<BuildingFloorPictureBean>) {
        picList?.addAll(mData)
        picAdapter.notifyDataSetChanged()

    }

    private var mBldId: Long?=-1

    private var mBldUUID:String? = ""

    fun createLocalBuilding(activity: Activity, mLocalProjectId:Int,mLocalProjectUUID:String,mProjectRemoteId:String,
                            mBuildingId:Long, name:String,leader:String,inspector:String,version:Long): Long? {

        if (array==null||array?.size==0){
            mView?.onMsg("楼层不能为空")
            return -1
        }

        array!!.forEach {
            if (it.pictureList==null||it.pictureList!!.size==0){
                mView?.onMsg("楼层图纸不能为空")
                return -1
            }
        }

        if (picList==null||picList?.size==0){
            mView?.onMsg("图纸不能为空")
            return -1
        }
        //赋值楼层名作为图纸上级目录

        mCurDrawingsDir = "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + mProName + "/" + name + "/"

        LogUtils.d("创建本地楼: mBldId=${mBuildingId}")

        createLocalFacadesDrawingInTheBuilding(activity,mLocalProjectId, mBldId!!)

        LogUtils.d("楼图纸："+mAppFacadeDrawingList.toString())
        mBldUUID = UUIDUtil.getUUID(name)
        mDb.getLocalBuildingOnce(mBuildingId.toLong()?:-1).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                var curTime: Long = System.currentTimeMillis()
                var dbBldBean: com.sribs.common.bean.db.BuildingBean? = null
                dbBldBean = com.sribs.common.bean.db.BuildingBean(
                    -1,
                    mBldUUID!!,
                    mProjectRemoteId,
                    mLocalProjectUUID,
                    mLocalProjectId.toLong(),
                    name,
                    "all",
                    TimeUtil.stampToDate(""+curTime),
                    TimeUtil.stampToDate(""+curTime),
                    "",
                    0,
                    leader,
                    inspector,
                    version,
                    0,
                     System.currentTimeMillis(),
                    "",
                    0,
                    mAppFacadeDrawingList,
                    above.size,
                    before.size,
                    1
                )
                mDb.createLocalBuilding(dbBldBean)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({
                mBldId = it.toLong()
                LogUtils.d("leon createLocalBuilding new building id=${mBldId}")
                //cache floors info to sqlite
                createLocalFloorsInTheBuilding(activity,mLocalProjectId, mBldId!!)
                //cache building drawings info to sqlite


    /*            addDisposable(mDb.getAllBuilding()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        var list = ArrayList(it.map { b->BuildingBean(
                            id = b.id?:-1,
                            uuid=b.UUID,
                            bldName = b.bldName!!,
                            bldType = b.bldType!!,
                            createTime = b.createTime!!,
                            updateTime = b.updateTime!!,
                            deleteTime = b.deleteTime!!,
                            leader = b.leader,
                            inspectorName = b.inspectorName!!,
                            remoteId= b.remoteId?:"",
                            version = b.version!!,
                            status = b.status!!,
                            drawing = b.drawing!!
                        )})
                        LogUtils.d("获取本地数据库楼表: "+list.toString())
                    })

                addDisposable(mDb.getAllFloor()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        var list = ArrayList(it.map { b->FloorBean(
                            id = b.id?:-1,
                            projectId = b.projectId,
                            bldId = b.bldId,
                            unitId = b.unitId,
                            floorId = b.floorId,
                            floorName = b.floorName,
                            createTime = b.createTime,
                            updateTime = b.updateTime,
                            deleteTime = b.deleteTime,
                            inspectorName = b.inspectorName,
                            remoteId= b.remoteId?:"",
                            version = b.version,
                            status = b.status,
                            drawing = b.drawing
                        )})
                        LogUtils.d("获取本地数据库楼层表: "+list.toString())
                    })*/

   /*             *//**
                 * 有网 走网络创建
                 *//*
                var floorDrawingsMap: HashMap<String?, Any?> = HashMap()
                var inspectorList: List<String> =
                    if (inspector.contains("、")) inspector.split("、") else Arrays.asList(inspector)
                if(Config.isNetAvailable){
                    LogUtils.d("有网进行云端楼创建: ")
                    addDisposable(HttpManager.instance.getHttpService<HttpApi>()
                        .saveV3Building(V3BuildingSaveReq().also {
                            it.projectId = mLocalProjectUUID
                            it.buildingId = mBldUUID!!
                            it.buildName = name
                            it.buildingType = ""
                            it.leaderId = Dict.getLeaderId(leader)!!
                            it.leaderName = leader
                            it.aboveGroundNumber = ""+above.size
                            it.underGroundNumber = ""+before.size
                            it.drawings = ArrayList() // 遗留图纸上传
                            it.floorDrawings = floorDrawingsMap // 遗留图纸上传
                            it.inspectors = inspectorList
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            LogUtils.d("创建云端楼成功: " + it.toString())
                            mView?.createBuildingSuccess()
                        },{
                            LogUtils.d("创建云端楼失败: " + it.toString())
                            mView?.onMsg(checkError(it))
                            mView?.createBuildingSuccess()
                        }))
                }else{
                    LogUtils.d("无网直接返回: ")
                    mView?.createBuildingSuccess()
                }*/

               /* addDisposable(mDb.getAllDrawing()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        var list = ArrayList(it.map { b->DrawingBean(
                            id = b.id?:-1,
                            projectId = b.projectId,
                            bldId = b.bldId,
                            unitId = b.unitId,
                            floorId = b.floorId,
                            floorName = b.floorName,
                            fileName = b.fileName,
                            drawingType = b.drawingType,
                            fileType = b.fileType,
                            localAbsPath = b.localAbsPath,
                            remoteAbsPath = b.remoteAbsPath,
                            createTime = b.createTime!!,
                            updateTime = b.updateTime!!,
                            deleteTime = b.deleteTime!!,
                            inspectorName = b.inspectorName!!,
                            remoteId= b.remoteId?:"",
                            version = b.version!!,
                            status = b.status!!
                        )})
                        LogUtils.d("获取本地数据库图纸表: "+list.toString())
                    })*/
              /*  if(Config.isNetAvailable){ // 有网，网络创建

                }else {*/
                    mView?.createBuildingSuccess()
         //       }

            },{
                it.printStackTrace()

            })
        return mBldId
    }

    private fun createLocalFloorsInTheBuilding(activity:Activity,mLocalProjectId:Int,mBuildingId:Long){
        println("leon createLocalFloorsInTheBuilding mBldId=${mBuildingId}")
        getFloorList(activity,mLocalProjectId,mBuildingId)
        if(floorList != null){
            var floorId:Long = -1
            var floorName:String? = null
            addDisposable(Observable.fromIterable(floorList)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMap{
                    LogUtils.d("楼层图纸："+ it.drawingsV3List.toString())
                    floorId = it.floorId?.toLong() ?: 0
                    floorName = it.floorName
                    println("leon 00 floorid=${floorId}, floorName=${floorName}")
                    var appFloor:FloorBean = FloorBean(
                        -1,
                        mLocalProjectId.toLong(),
                        mBuildingId.toLong(),
                        -1,
                        it.floorId?.toLong(),
                        it.floorName,
                        it.floorType,
                        TimeUtil.stampToDate(""+it.createTime),
                        TimeUtil.stampToDate(""+it.createTime),
                        "",
                        "",
                        1,
                        "",
                        0,
                        it.drawingsV3List,
                        above.size,
                        before.size,
                    )
                    mDb.createLocalFloor(appFloor)
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe({
                    print("leon createLocalFloorsInTheBuilding ret floor id=$it")
                },{
                    it.printStackTrace()
                }))
        }
    }

    private var mAppFacadeDrawingList: ArrayList<DrawingV3Bean> = ArrayList<DrawingV3Bean>()

    private fun createLocalFacadesDrawingInTheBuilding(activity: Activity, mLocalProjectId:Int,mBuildingId:Long){
        println("leon createLocalFloorsInTheBuilding mBldId=${mBldId}")
        mAppFacadeDrawingList!!.clear()
        if (picList!=null&&picList!!.size>0){
            var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)

            copyDrawingsToLocalCache(activity,picList!!,null,cacheRootDir)

            picList!!.forEach {
                var name =""
                if(!it.name.endsWith("pdf")){
                    name = it.name.replace(".","")+".pdf"
                }else{
                    name = it.name
                }
                var cacheFilePath = File(cacheRootDir + mCurDrawingsDir,name)

                var drawingV3ToBuild = DrawingV3Bean(
                    -1,
                    name,
                    FileUtil.getFileExtension(name),
                    "overall",
                    cacheFilePath.absolutePath,
                    "",
                    ArrayList()
                )
                mAppFacadeDrawingList!!.add(drawingV3ToBuild)
            }
        }
    }


    var floorList: ArrayList<Floor> = ArrayList<Floor>()

    fun getFloorList(activity:Activity,mLocalProjectId:Int,mBuildingId:Long){
        if (array!=null&& array!!.size>0){
            for (i in array!!.indices) {
               var  item = array!![i]
                var floor = Floor(
                    -1,
                    mBuildingId.toLong(),
                    mLocalProjectId.toLong(),
                    (i+1).toLong(),
                    item.name,
                    item.floorType
                )
                floor.drawingsV3List = getDrawingList(activity,item,mLocalProjectId,mBuildingId)
                LogUtils.d("楼层图纸集合: "+floor.drawingsV3List)
                floorList.add(floor)
            }
        }
    }

    fun getDrawingList(activity:Activity,floorBean:BuildingFloorBean,mLocalProjectId:Int,mBuildingId:Long):ArrayList<DrawingV3Bean>?{
        if (floorBean.pictureList!=null&& floorBean.pictureList!!.size>0){
            var originList = floorBean.pictureList
            var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)
            var floorDrawingsList: ArrayList<DrawingV3Bean> = ArrayList<DrawingV3Bean>()

            copyDrawingsToLocalCache(activity,originList!!,floorBean.name,cacheRootDir)

            for (i in originList!!.indices) {
                var  item = originList!![i]

                var name =""
                if(!item.name.endsWith("pdf")){
                    name = item.name.replace(".","")+".pdf"
                }else{
                    name = item.name
                }

                var cacheFilePath = File(cacheRootDir + mCurDrawingsDir+floorBean.name,name)
                var drawingV3ToBuild = DrawingV3Bean(
                    -1,
                    name,
                    FileUtil.getFileExtension(name),
                    "floor",
                    cacheFilePath.absolutePath,
                    "",
                    ArrayList()
                )
                floorDrawingsList.add(drawingV3ToBuild)
            }
            return floorDrawingsList

        }

        return null
    }

    private fun copyDrawingsToLocalCache(activity:Activity,
                                         pictureBean:ArrayList<BuildingFloorPictureBean>,
                                         floorName:String?,
                                         cacheRootDir:String){
        LogUtils.d("copyDrawingsToLocalCache: "+pictureBean.size)
        /*var filters = pictureBean.filter {
         //   it.uri != null
        }*/
        var name= ""
        var needToPDF = false
        var cacheFileParent =File("")
        var cacheFile = File("")
        addDisposable(Observable.fromIterable(pictureBean)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .flatMap{
                 cacheFileParent = File(cacheRootDir + mCurDrawingsDir)
                if(!floorName.isNullOrEmpty()) { // 不为空认为是楼图纸   为空认为是楼层图纸
                    cacheFileParent = File(cacheRootDir + mCurDrawingsDir + floorName)
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
                            FileUtil.copyFileTo(activity, Uri.parse(it.uri),cacheFile.absolutePath)
                        }
                    }

                LogUtils.d("图纸缓存目录： "+cacheFile.toString())
                Observable.just("Done")
            }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({

                LogUtils.d("复制图纸到缓存目录: ${it}")
            },{
                it.printStackTrace()
            }))
    }

    private lateinit var mPrefs: SharedPreferences
    private var mCurDrawingsDir: String? = ""
    private var mProName: String? = ""
    private var mBldNo: String? = ""

    override fun bindView(v: IBaseView) {
        mView = v as IProjectContrast.IProjectCreateTypeView
        mPrefs = mView!!.getContext()!!.getSharedPreferences("createProject", Context.MODE_PRIVATE)
        mProLeader = mPrefs.getString(ModuleHelper.CUR_PRO_LEADER,"")
        mProName = mPrefs.getString(ModuleHelper.CUR_PRO_NAME,"")
        mBldNo = mPrefs.getString(ModuleHelper.CUR_BLD_NO,"")
        mCurDrawingsDir = "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + mProName + "/" + mBldNo + "/"
        initFlourLits()
        initPicLits()
    }

    override fun unbindView() {
       mView = null
    }



    override fun chosePic(bean: BuildingFloorBean) {
        mView?.chosePic(bean)
    }

    override fun takePhoto(bean: BuildingFloorBean) {
        mView?.takePhone(bean)
    }

    override fun showWiite(bean: BuildingFloorBean) {
        mView?.choseWhite(bean)
    }

    override fun deleteBuildingFloor(bean: BuildingFloorBean, position: Int) {
        when (bean.floor) {
            "地上" -> {
                above.removeAt(position - before.size)
            }
            "地下" -> {
                before.removeAt(position)
            }
        }

        array!!.clear()
        array!!.addAll(before)
        array!!.addAll(above)
        flourAdapter.notifyDataSetChanged()
        Log.e("TAG", "deleteBuildingFloor: "+array )
        mView?.deleteBuildingFloor(bean.floor!!, above.size, before.size)
    }
}