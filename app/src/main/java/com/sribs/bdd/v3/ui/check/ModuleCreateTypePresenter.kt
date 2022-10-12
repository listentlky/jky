package com.sribs.bdd.v3.ui.check

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.sribs.common.module.BasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.cbj.sdk.utils.NumberUtil
import com.sribs.bdd.bean.*
import com.sribs.bdd.bean.data.ModuleFloor
import com.sribs.bdd.bean.data.ModuleFloorBean
import com.sribs.bdd.bean.data.ModuleFloorPictureBean
import com.sribs.bdd.module.project.IProjectContrast

import com.sribs.bdd.v3.adapter.CreateModuleFloorAdapter
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.bean.db.v3.project.v3BuildingModuleDbBean
import com.sribs.common.bean.v3.v3ModuleFloorDbBean
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.FileUtil
import com.sribs.common.utils.TimeUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ModuleCreateTypePresenter : BasePresenter(), IProjectContrast.IProjectCreateTypePresenter,
    CreateModuleFloorAdapter.ICallback {
    private var mView: IProjectContrast.IModuleCreateTypeView? = null

    private var array: ArrayList<ModuleFloorBean>? = null

    private var picList: ArrayList<ModuleFloorPictureBean>? = null

    private var above = ArrayList<ModuleFloorBean>()
    private var after = ArrayList<ModuleFloorBean>()

    private var mProLeader: String? = ""


    var beanList: ArrayList<ModuleFloorBean>? = ArrayList()

    var beanFinalPicList: ArrayList<ModuleFloorPictureBean>? = null

    private val floorAdapter by lazy {
        CreateModuleFloorAdapter(this)
    }


    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun addAboveFlourList(num: Int) {
        if (num == 0) {
            array?.removeAll(above)
            array?.removeAll(beanList!!)
            above.clear()
            floorAdapter.notifyDataSetChanged()
        } else if (num > 0) {
            array?.removeAll(above)
            array?.removeAll(beanList!!)
            above.clear()
            for (i in 0 until num) {
                var name = NumberUtil.num2Chinese(i + 1)
                var moduleFloorBean = ModuleFloorBean(name + "层", arrayListOf())
                above.add(moduleFloorBean)
            }
            array?.addAll(above)
            floorAdapter.notifyDataSetChanged()
        } else {
            mView?.onMsg("请输入正确的数字")
        }
    }


    //地下楼层层数
    @SuppressLint("NotifyDataSetChanged")
    override fun addAfterFlourList(num: Int) {
        if (num == 0) {
            array?.removeAll(after)
            after.clear()
            array?.removeAll(beanList!!)
            floorAdapter.notifyDataSetChanged()
        } else if (num > 0) {
            array?.removeAll(after)
            after.clear()
            array?.removeAll(beanList!!)
            for (i in 0 until num) {
                var name = NumberUtil.num2Chinese(i + 1)
                var moduleFloorBean = ModuleFloorBean("负" + name + "层", arrayListOf())
                after.add(moduleFloorBean)
            }
            array?.addAll(after)
            floorAdapter.notifyDataSetChanged()
        } else {
            mView?.onMsg("请输入正确的数字")
        }
    }


    fun initFlourLits() {
        var manager = LinearLayoutManager(mView?.getContext())
        mView?.getFloorRecycleView()?.layoutManager = manager
        array = ArrayList()
        floorAdapter.setData(array!!)
        mView?.getFloorRecycleView()?.adapter = floorAdapter

    }

    fun setData(list: List<v3ModuleFloorDbBean>) {
        beanList!!.clear()
        list.forEach {
            var beanPicList: ArrayList<ModuleFloorPictureBean>? = ArrayList()
            it.drawingsList?.forEach { b ->
                beanPicList!!.add(
                    ModuleFloorPictureBean(
                        name = b.fileName!!,
                        uri = "",
                        url = b.localAbsPath,
                    )
                )
            }
            beanList!!.add(
                ModuleFloorBean(
                    name = it.floorName!!,
                    pictureList = beanPicList
                )
            )
        }

        array?.clear()
        array?.addAll(beanList!!)

        refeshData()


    }

    fun refeshData() {
        floorAdapter.notifyDataSetChanged()
    }


    fun initLocalData(projectId: Long, buildingId: Long, moduleId: Long) {
        addDisposable(
            mDb.getv3ModuleFloor(projectId, buildingId, moduleId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it != null)

                        mView?.initLocalData(it)
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun createLocalModule(
        activity: Activity,
        mLocalProjectId: Int,
        mBuildingId: Long,
        mModuleId: Long,
        remoteId: String,
        moduleName: String,
    ) {

        if (array.isNullOrEmpty()) {
            mView?.onMsg("楼层不能为空")
            return
        }

        array!!.forEach {
            if (it.pictureList.isNullOrEmpty()) {
                mView?.onMsg("楼层图纸不能为空")
                return
            }
        }

        //赋值模块名作为图纸上级目录
        mCurDrawingsDir = "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + mProName + "/" + moduleName + "/"


      /*  var bean = v3BuildingModuleDbBean(
            id = mModuleId,
            buildingId = mBuildingId,
            projectId = mLocalProjectId.toLong(),
            moduleName = moduleName,
            updateTime = TimeUtil.YMD_HMS.format(Date()),
            drawings = ArrayList(),
            aboveGroundNumber = above.size,
            underGroundNumber = after.size,
            )*/

        addDisposable(mDb.deletev3ModuleFloor(mLocalProjectId.toLong(), mBuildingId, mModuleId)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
       /*     .flatMap {

                mDb.updatev3BuildingModule(bean)
            }
            .observeOn(AndroidSchedulers.mainThread())*/
            .subscribe({
                LogUtils.d("llf createLocalModule new building id=${mModuleId}")
                createLocalFloorsInTheModule(
                    activity,
                    mLocalProjectId,
                    mBuildingId,
                    mModuleId,
                    above.size,
                    after.size
                )
            }, {
                mView?.onMsg("gg")
                it.printStackTrace()
            })
        )
    }

    private fun createLocalFloorsInTheModule(
        activity: Activity,
        mLocalProjectId: Int,
        mBuildingId: Long,
        moduleId: Long,
        aboveGroundNumber: Int,
        underGroundNumber: Int
    ) {
        println("leon createLocalFloorsInTheBuilding mBldId=${mBuildingId}")
        getFloorList(
            activity,
            mLocalProjectId,
            mBuildingId,
            moduleId,
            aboveGroundNumber,
            underGroundNumber
        )
        var curTime: Long = System.currentTimeMillis()
        if (floorList != null) {
            var floorId: Long = -1
            var floorName: String? = null
            addDisposable(Observable.fromIterable(floorList)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMap {

                    floorId = it.floorId?.toLong() ?: 0
                    floorName = it.floorName
                    println("leon 00 floorid=${floorId}, floorName=${floorName}")


                    var bean = v3ModuleFloorDbBean(

                        projectId = it.projectId,
                        bldId = it.bldId,
                        moduleId = it.moduleId,
                        floorId = it.floorId,
                        floorName = it.floorName,
                        drawingsList = it.floorList,
                        deleteTime = "",
                        aboveNumber = it.aboveNumber,
                        afterNumber = it.afterNumber,
                        createTime = TimeUtil.YMD_HMS.format(Date()),
                        updateTime = TimeUtil.YMD_HMS.format(Date()),
                        status = 0
                    )
                    mDb.updatev3ModuleFloor(bean)
                }
                .observeOn(Schedulers.computation())
                .subscribe({
                    mView?.createModuleConfigSuccess()
                }, {
                    mView?.onMsg("保存到本地楼层表失败")
                    it.printStackTrace()
                })
            )
        }
    }

    var floorList: ArrayList<ModuleFloor> = ArrayList<ModuleFloor>()

    fun getFloorList(
        activity: Activity,
        mLocalProjectId: Int,
        mBuildingId: Long,
        moduleId: Long,
        aboveGroundNumber: Int,
        underGroundNumber: Int
    ) {
        if (array != null && array!!.size > 0) {
            for (i in array!!.indices) {
                var item = array!![i]


                var moduleFloor = ModuleFloor(
                    id = -1,
                    projectId = mLocalProjectId.toLong(),
                    bldId = mBuildingId,
                    moduleId = moduleId,
                    floorId = (i + 1).toLong(),
                    floorName = item.name,
                    aboveNumber = aboveGroundNumber,
                    afterNumber = underGroundNumber
                )

                moduleFloor.floorList = getDrawingList(activity, item, mLocalProjectId, mBuildingId)
                floorList.add(moduleFloor)
            }
        }
    }

    fun getDrawingList(
        activity: Activity,
        floorBean: ModuleFloorBean,
        mLocalProjectId: Int,
        mBuildingId: Long,
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
                var cacheFilePath = File(cacheRootDir + mCurDrawingsDir+floorBean.name+"/"+index, item.name)
                LogUtils.d("楼层图纸缓存目录：" + cacheFilePath)

                drawingItem = DrawingV3Bean(
                    -1,
                    item.name,
                    FileUtil.getFileExtension(item.name),
                    "floor",
                    cacheFilePath.absolutePath,
                    "",
                    ArrayList()
                )
                floorDrawingsList.add(drawingItem)
            }
            return floorDrawingsList
        }

        return null
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
        LogUtils.d("过滤uri不等于null后: " + filters)*/

        var index = -1

        addDisposable(Observable.fromIterable(pictureBean)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .flatMap {
                ++index
                var cacheFileParent = File(cacheRootDir + mCurDrawingsDir)
                if(!floorName.isNullOrEmpty()) { // 为空认为是楼图纸   不为空认为是楼层图纸
                    cacheFileParent = File(cacheRootDir + mCurDrawingsDir + floorName+"/"+index)
                }
                cacheFileParent.mkdirs()
                var cacheFile = File(cacheFileParent,it.name)
                LogUtils.d("图纸缓存目录： "+cacheFile.toString())
                if (cacheFile != null) {
                    if(!it.uri.isNullOrEmpty()) {
                        FileUtil.copyFileTo(activity, Uri.parse(it.uri), cacheFile.absolutePath)
                    }else{
                        FileUtil.copyTo(File(it.url),cacheFile)
                    }
                }
                Observable.just("Done")
            }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                LogUtils.d("复制图纸到缓存目录: ${it}")
            }, {
                it.printStackTrace()
            })
        )
    }

    private fun getPicList(activity: Activity, mLocalProjectId: Int, mBuildingId: Long) {
        if (picList != null && picList!!.size > 0) {
            var cachePath: String
            var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)
            var curTime: Long = System.currentTimeMillis()

            copyDrawingsToLocalCache(activity, picList!!,null, cacheRootDir)

            picList!!.forEach {
                cachePath = cacheRootDir + mCurDrawingsDir + it.name
                LogUtils.d("楼图纸缓存目录：" + cachePath)
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
                    if (it.url != null) it.url else it.uri,
                    if (it.url != null) it.url else cachePath,
                    curTime,
                    curTime,
                    mProLeader!!,
                    "",
                    1,
                    0
                )
                //   mAppFacadeDrawingList!!.add(drawing)
            }
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
        //   initPicLits()
    }

    override fun unbindView() {
        mView = null
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

}