package com.sribs.bdd.module.project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.sribs.common.module.BasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.cbj.sdk.utils.NumberUtil
import com.sribs.bdd.Config
import com.sribs.bdd.bean.*
import com.sribs.bdd.ui.adapter.CreateFloorAdapter
import com.sribs.bdd.ui.adapter.CreateFloorPictureAdapter
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.DrawingBean
import com.sribs.common.bean.db.FloorBean
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.FileUtil
import com.sribs.common.utils.TimeUtil
import com.sribs.db.project.building.BuildingBean
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File

class ProjectCreateTypePresenter: BasePresenter(),IProjectContrast.IProjectCreateTypePresenter
    ,CreateFloorAdapter.ICallback {
    private var mView:IProjectContrast.IProjectCreateTypeView?=null

    private var array:ArrayList<BuildingFloorBean>?=null

    private var picList:ArrayList<BuildingFloorPictureBean>?=null

    private var above = ArrayList<BuildingFloorBean>()
    private var before = ArrayList<BuildingFloorBean>()

    private var mProLeader: String? = ""

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
        if (num==0){
            array?.removeAll(above)
            above.clear()
            flourAdapter.notifyDataSetChanged()
        }else if (num>0){
            array?.removeAll(above)
            for (i in 0 until num){
                var name = NumberUtil.num2Chinese(i+1)
                var buildingFloorBean = BuildingFloorBean(name+"层", arrayListOf())
                above.add(buildingFloorBean)
            }
           array?.addAll(above)
           flourAdapter.notifyDataSetChanged()
        }else{
            mView?.onMsg("请输入正确的数字")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun addAfterFlourList(num: Int) {
        if (num==0){
            array?.removeAll(before)
            before.clear()
            flourAdapter.notifyDataSetChanged()
        }else if (num>0){
            array?.removeAll(before)
            for (i in 0 until num){
                var name = NumberUtil.num2Chinese(i+1)
                var buildingFloorBean = BuildingFloorBean("负"+name+"层", arrayListOf())
                before.add(buildingFloorBean)
            }
            array?.addAll(before)
            flourAdapter.notifyDataSetChanged()
        }else{
            mView?.onMsg("请输入正确的数字")
        }
    }


    fun initFlourLits(){
        var manager = LinearLayoutManager(mView?.getContext())
        mView?.getFlourRecycleView()?.layoutManager = manager
        array = ArrayList()
        flourAdapter.setData(array!!)
        mView?.getFlourRecycleView()?.adapter = flourAdapter

    }

    fun refeshData(){
        flourAdapter.notifyDataSetChanged()
    }

    fun initPicLits(){
        var manager = LinearLayoutManager(mView?.getContext())
        mView?.getPicRecycleView()?.layoutManager = manager
        picList = ArrayList()
        picAdapter.setData(picList!!)
        mView?.getPicRecycleView()?.adapter = picAdapter

    }

    fun refreshPicList(mData:ArrayList<BuildingFloorPictureBean>){
        picList?.addAll(mData)
        picAdapter.notifyDataSetChanged()

    }

    private var mBldId: Long?=-1

    fun createLocalBuilding(activity: Activity, mLocalProjectId:Int,
                            mBuildingId:Long, name:String,leader:String,inspector:String): Long? {

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

        LogUtils.d("leon createLocalBuilding mBldId=${mBuildingId}")

        mDb.getLocalBuildingOnce(mBuildingId.toLong()?:-1).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                var curTime: Long = System.currentTimeMillis()
                var dbBldBean: com.sribs.common.bean.db.BuildingBean? = null
                dbBldBean = com.sribs.common.bean.db.BuildingBean(
                    -1,
                    mLocalProjectId.toLong(),
                    name,
                    "all",
                    curTime,
                    curTime,
                    0L,
                    leader,
                    inspector,
                    1,
                    "",
                    0,
                )
                mDb.createLocalBuilding(dbBldBean)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mBldId = it.toLong()
                LogUtils.d("leon createLocalBuilding new building id=${mBldId}")
                //cache floors info to sqlite
                createLocalFloorsInTheBuilding(activity,mLocalProjectId, mBldId!!)
                //cache building drawings info to sqlite
                createLocalFacadesDrawingInTheBuilding(activity,mLocalProjectId, mBldId!!)

                addDisposable(mDb.getAllBuilding()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        var list = ArrayList(it.map { b->BuildingBean(
                            id = b.id?:-1,
                            bldName = b.bldName!!,
                            bldType = b.bldType!!,
                            createTime = b.createTime!!,
                            updateTime = b.updateTime!!,
                            deleteTime = b.deleteTime!!,
                            leader = b.leader,
                            inspectorName = b.inspectorName!!,
                            remoteId= b.remoteId?:"",
                            version = b.version!!,
                            status = b.status!!
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
                            createTime = b.createTime!!,
                            updateTime = b.updateTime!!,
                            deleteTime = b.deleteTime!!,
                            inspectorName = b.inspectorName!!,
                            remoteId= b.remoteId?:"",
                            version = b.version!!,
                            status = b.status!!
                        )})
                        LogUtils.d("获取本地数据库楼层表: "+list.toString())
                    })

                addDisposable(mDb.getAllDrawing()
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
                    })
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
        var curTime: Long = System.currentTimeMillis()
        if(floorList != null){
            var floorId:Long = -1
            var floorName:String? = null
            var appFloorDrawingList: ArrayList<Drawing>? = null
            addDisposable(Observable.fromIterable(floorList)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMap{
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
                        it.createTime,
                        it.createTime,
                        0L,
                        "",
                        1,
                        "",
                        0
                    )
                    appFloorDrawingList = it.drawingsList
                    mDb.createLocalFloor(appFloor)
                }
                .observeOn(Schedulers.computation())
                .flatMap{

                    var floor: Floor? = null
                    for(i in 0..floorList?.size?.minus(1)!!){
                        floor = floorList?.get(i)
                        if(floor?.id!! <0){
                            floor.id = it
                            break
                        }
                    }

                    var drawingList: ArrayList<Drawing>? = null
                    if(floor != null){
                        if(floor.drawingsList != null){
                            drawingList = floor.drawingsList
                        }
                    }
                    Observable.fromIterable(drawingList)
                }.observeOn(Schedulers.computation())
                .flatMap{
                    println("leon 11 floorid=${floorId}, floorName=${floorName}")
                    var drawing:com.sribs.common.bean.db.DrawingBean = com.sribs.common.bean.db.DrawingBean(
                        -1,
                        mLocalProjectId.toLong(),
                        mBuildingId,
                        -1,
                        it.floorId,
                        it.floorName,
                        it.fileName,
                        it.drawingType,
                        it.fileType,
                        it.cacheAbsPath,
                        "",
                        it.createTime?:curTime,
                        it.updateTime?:curTime,
                        0,
                        "",
                        "",
                        1,
                        0
                    )
                    mDb.createLocalDrawing(drawing)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    print("leon createLocalFloorsInTheBuilding ret drawing id=$it")
                },{
                    it.printStackTrace()
                }))
        }
    }

    private var mAppFacadeDrawingList: ArrayList<Drawing>? = null

    private fun createLocalFacadesDrawingInTheBuilding(activity: Activity, mLocalProjectId:Int,mBuildingId:Long){
        println("leon createLocalFloorsInTheBuilding mBldId=${mBldId}")
        var curTime: Long = System.currentTimeMillis()
        getPicList(activity,mLocalProjectId,mBuildingId)
        if(picList != null){
            addDisposable(Observable.fromIterable(mAppFacadeDrawingList)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMap{it ->
                    var drawing:com.sribs.common.bean.db.DrawingBean = com.sribs.common.bean.db.DrawingBean(
                        -1,
                        mLocalProjectId.toLong(),
                        mBldId,
                        -1,
                        it.floorId,
                        it.floorName,
                        it.fileName,
                        it.drawingType,
                        it.fileType,
                        it.cacheAbsPath,
                        "",
                        it.createTime?:curTime,
                        it.updateTime?:curTime,
                        0L,
                        "",
                        "",
                        1,
                        0
                    )
                    mDb.createLocalDrawing(drawing)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    print("leon createLocalFloorsInTheBuilding ret drawing id=$it")
                },{
                    it.printStackTrace()
                }))
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
                    item.name
                )
                floor.drawingsList = getDrawingList(activity,item,mLocalProjectId,mBuildingId)
                floorList.add(floor)
            }
        }
    }

    fun getDrawingList(activity:Activity,floorBean:BuildingFloorBean,mLocalProjectId:Int,mBuildingId:Long):ArrayList<Drawing>?{
        if (floorBean.pictureList!=null&& floorBean.pictureList!!.size>0){
            var cachePath:String
            var originList = floorBean.pictureList
            var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)
            var floorDrawingsList: ArrayList<Drawing> = ArrayList<Drawing>()
            var drawingItem :Drawing
            var curTime: Long = System.currentTimeMillis()

            copyDrawingsToLocalCache(activity,originList!!,cacheRootDir)

            for (i in originList!!.indices) {
                var  item = originList!![i]

                cachePath = cacheRootDir + mCurDrawingsDir +  item.name
                LogUtils.d("楼层图纸缓存目录："+cachePath)

                drawingItem = Drawing(
                    -1,
                    mLocalProjectId.toLong(),
                    mBuildingId.toLong(),
                    -1,
                    (i+1).toLong(),
                    floorBean.name,
                    item.name,
                    "floor",
                    null,
                    FileUtil.getFileExtension(item.name),
                    if(item.url != null) item.url else item.uri,
                    if(item.url != null) item.url else cachePath,
                    curTime,
                    curTime,
                    mProLeader!!,
                    "",
                    1,
                    0
                )
                floorDrawingsList.add(drawingItem)
            }
            return floorDrawingsList

        }

        return null
    }

    private fun copyDrawingsToLocalCache(activity:Activity,pictureBean:ArrayList<BuildingFloorPictureBean>,cacheRootDir:String){
        LogUtils.d("copyDrawingsToLocalCache: "+pictureBean.size)
        var filters = pictureBean.filter {
            it.uri != null
        }
        LogUtils.d("过滤uri不等于null后: "+filters.size)

        addDisposable(Observable.fromIterable(filters)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .flatMap{
                val cacheFileParent = File(cacheRootDir + mCurDrawingsDir)
                cacheFileParent.mkdirs()
                val cacheFilePath = cacheFileParent.absolutePath+"/"+it.name
                LogUtils.d("图纸缓存目录: ${cacheFilePath}")
                if (cacheFilePath != null) {
                    FileUtil.copyFileTo(activity, Uri.parse(it.uri),cacheFilePath)
                }
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

   private fun getPicList(activity: Activity,mLocalProjectId:Int,mBuildingId:Long){
       if (picList!=null&&picList!!.size>0){
           var cachePath:String
           var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)
           var curTime: Long = System.currentTimeMillis()

           copyDrawingsToLocalCache(activity,picList!!,cacheRootDir)

           mAppFacadeDrawingList = arrayListOf()
           picList!!.forEach {
               cachePath = cacheRootDir + mCurDrawingsDir +  it.name
               LogUtils.d("楼图纸缓存目录："+cachePath)
               var drawing = Drawing(
                   -1,
                   mLocalProjectId.toLong(),
                   mBuildingId,
                   -1,
                   -1,
                  "",
                   it.name,
                   "overall",
                   null,
                   FileUtil.getFileExtension(it.name),
                   if(it.url != null) it.url else it.uri,
                   if(it.url != null) it.url else cachePath,
                   curTime,
                   curTime,
                   mProLeader!!,
                   "",
                   1,
                   0
               )
           }
       }

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
}