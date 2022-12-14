package com.sribs.bdd.module.project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.cbj.sdk.utils.NumberUtil
import com.radaee.util.CommonUtil
import com.sribs.bdd.bean.BuildingFloorBean
import com.sribs.bdd.bean.BuildingFloorPictureBean
import com.sribs.bdd.bean.Floor
import com.sribs.bdd.ui.adapter.CreateFloorAdapter
import com.sribs.bdd.ui.adapter.CreateFloorPictureAdapter
import com.sribs.bdd.ui.adapter.CreateNonResidentBuildingAdapter
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.bdd.utils.UUIDUtil
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.bean.db.FloorBean
import com.sribs.common.module.BasePresenter
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.FileUtil
import com.sribs.common.utils.TimeUtil
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ProjectCreateTypePresenter : BasePresenter(), IProjectContrast.IProjectCreateTypePresenter,
    CreateFloorAdapter.ICallback,CreateNonResidentBuildingAdapter.ICallback {
    private var mView: IProjectContrast.IProjectCreateTypeView? = null

    private var array: ArrayList<BuildingFloorBean>? = null

    private var picList: ArrayList<BuildingFloorPictureBean>? = null

    private var nonResidentpicList: ArrayList<BuildingFloorBean>? = null

    private var above = ArrayList<BuildingFloorBean>()
    private var before = ArrayList<BuildingFloorBean>()

    private var oldAboveList = ArrayList<BuildingFloorBean>()
    private var oldBeforeList = ArrayList<BuildingFloorBean>()

    private var mProLeader: String? = ""

    var mAboveOldIndex = 0

     var mBeforeOldIndex = 0

    private val flourAdapter by lazy {
        CreateFloorAdapter(this,mView?.getContext())
    }

    private val  nonResidentfloorAdapter by lazy {
        CreateNonResidentBuildingAdapter(this,mView?.getContext())
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
        LogUtils.d("addAboveFlourList??? " + num)

        if (num == 0) {
            array?.removeAll(above)
            above.clear()
            flourAdapter.notifyDataSetChanged()
        } else if (num > 0) {
            array?.removeAll(above)
            above.clear()
            for (i in 0 until num) {
                var name = NumberUtil.num2Chinese(i + 1)
                var buildingFloorBean = BuildingFloorBean(name + "???", arrayListOf(), "??????",1,i)

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
            flourAdapter.notifyDataSetChanged()
            oldAboveList.clear()
            oldAboveList.addAll(above)

        }else {
            mView?.onMsg("????????????????????????")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun addAfterFlourList(num: Int) {
        LogUtils.d("addAfterFlourList??? " + num)
        if (num == 0) {
            array?.removeAll(before)
            before.clear()
            flourAdapter.notifyDataSetChanged()
        } else if (num > 0) {
            array?.clear()
            before.clear()
            for (i in 0 until num) {
                var name = NumberUtil.num2Chinese(i + 1)
                var buildingFloorBean = BuildingFloorBean("???"+name + "???", arrayListOf(), "??????",0,i)
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
            flourAdapter.notifyDataSetChanged()

            oldBeforeList.clear()
            oldBeforeList.addAll(before)


        } else {
            mView?.onMsg("????????????????????????")
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

    fun initNonResidentPicList(){
        var manager = LinearLayoutManager(mView?.getContext())
        mView?.getNonResidentRecycleView()?.layoutManager = manager
        nonResidentpicList = ArrayList()
        nonResidentpicList!!.clear()

        nonResidentpicList!!.add(BuildingFloorBean("?????????", arrayListOf(), "??????",-1,-1))
        nonResidentpicList!!.add(BuildingFloorBean("?????????", arrayListOf(), "??????",-1,-1))
        nonResidentpicList!!.add(BuildingFloorBean("?????????", arrayListOf(), "??????",-1,-1))
        nonResidentpicList!!.add(BuildingFloorBean("?????????", arrayListOf(), "??????",-1,-1))
        nonResidentpicList!!.add(BuildingFloorBean("?????????", arrayListOf(), "??????",-1,-1))

        nonResidentfloorAdapter.setData(nonResidentpicList!!)
        mView?.getNonResidentRecycleView()?.adapter = nonResidentfloorAdapter





    }

    fun refeshNonResidentListData() {
        nonResidentfloorAdapter.notifyDataSetChanged()
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
            mView?.onMsg("??????????????????")
            return -1
        }

        array!!.forEach {
            if (it.pictureList==null||it.pictureList!!.size==0){
                mView?.onMsg("????????????????????????")
                return -1
            }
        }

        if (picList==null||picList?.size==0){
            mView?.onMsg("??????????????????")
            return -1
        }

        nonResidentpicList!!.forEach {
            if (it.pictureList ==null || it.pictureList!!.size==0){
                mView?.onMsg("???????????????/???????????????????????????")
                return -1
            }
        }

        //???????????????????????????????????????

        mCurDrawingsDir = "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + mProName + "/" + name + "/"

        createLocalFacadesDrawingInTheBuilding(activity,mLocalProjectId, mBldId!!)

        LogUtils.d("????????????"+mAppFacadeDrawingList.toString())
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
                LogUtils.d("?????????: id=${mBldId}")
                LogUtils.d("leon createLocalBuilding new building id=${mBldId}")
                createLocalFloorsInTheBuilding(activity,mLocalProjectId, mBldId!!)
                mView?.createBuildingSuccess()
            },{
                it.printStackTrace()

            })
        return mBldId
    }

    private fun createLocalFloorsInTheBuilding(activity:Activity,mLocalProjectId:Int,mBuildingId:Long){
        println("leon createLocalFloorsInTheBuilding mBldId=${mBuildingId}")
        getFloorList(activity,mLocalProjectId,mBuildingId)
        if(floorList != null){
            addDisposable(Observable.fromIterable(floorList)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMap{
                    LogUtils.d("???????????????"+ it.drawingsV3List.toString())
                    var appFloor:FloorBean = FloorBean(
                        -1,
                        mLocalProjectId.toLong(),
                        mBuildingId.toLong(),
                        -1,
                        it.floorId,
                        it.floorName,
                        it.floorType,
                        it.floorIndex,
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
                    LogUtils.d("????????????: id=$it")
                },{
                    LogUtils.d("??????????????????: $it")
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

            nonResidentpicList!!.forEach {
                copyDrawingsToLocalCache(activity,it.pictureList!!,null,cacheRootDir)
            }

            picList!!.forEach {
                var name =""
                if(!it.name.endsWith("pdf")){
                    name = it.name.replace(".","")+".pdf"
                }else{
                    name = it.name
                }
                var cacheFilePath = File(cacheRootDir + mCurDrawingsDir,name)

                var drawingV3ToBuild = DrawingV3Bean(
                    UUIDUtil.getUUID(name),
                    name,
                    FileUtil.getFileExtension(name),
                    "overall",
                    cacheFilePath.absolutePath,
                    "",
                    "",
                    ArrayList()
                )
                mAppFacadeDrawingList!!.add(drawingV3ToBuild)
            }

            nonResidentpicList!!.forEachIndexed { index, buildingFloorBean ->
                buildingFloorBean.pictureList!!.forEach {
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
                    var drawingV3ToBuild = DrawingV3Bean(
                        UUIDUtil.getUUID(name),
                        name,
                        FileUtil.getFileExtension(name),
                        drawingType,
                        cacheFilePath.absolutePath,
                        "",
                        buildingFloorBean.name,
                        ArrayList()
                    )
                    mAppFacadeDrawingList!!.add(drawingV3ToBuild)
                }
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
                    UUIDUtil.getUUID(item.name),
                    item.name,
                    item.floorType,
                    item.floorIndex
                )
                floor.drawingsV3List = getDrawingList(activity,item,mLocalProjectId,mBuildingId)
                LogUtils.d("??????????????????: "+floor.drawingsV3List)
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
                    UUIDUtil.getUUID(name),
                    name,
                    FileUtil.getFileExtension(name),
                    "floor",
                    cacheFilePath.absolutePath,
                    "",
                    floorBean.name,
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
                if(!floorName.isNullOrEmpty()) { // ????????????????????????   ???????????????????????????
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

                LogUtils.d("????????????????????? "+cacheFile.toString())
                Observable.just("Done")
            }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({

                LogUtils.d("???????????????????????????: ${it}")
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
        initNonResidentPicList()
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
            "??????" -> {
                above.removeAt(position - before.size)
            }
            "??????" -> {
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

    override fun choseNonResidentPic(bean: BuildingFloorBean) {
        mView?.choseNonResidentPic(bean)
    }

    override fun takeNonResidentPhoto(bean: BuildingFloorBean) {
        mView?.takeNonResidentPhoto(bean)
    }

    override fun showNonResidentWhite(bean: BuildingFloorBean) {
        mView?.choseNonResidentWhite(bean)
    }
}