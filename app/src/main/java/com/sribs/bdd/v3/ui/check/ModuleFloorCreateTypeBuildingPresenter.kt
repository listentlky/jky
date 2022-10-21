package com.sribs.bdd.v3.ui.check

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.moudles.IBasePresenter
import com.sribs.common.module.BasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.radaee.util.CommonUtil

import com.sribs.bdd.bean.*
import com.sribs.bdd.bean.data.ModuleFloorBean
import com.sribs.bdd.bean.data.ModuleFloorPictureBean
import com.sribs.bdd.module.project.IProjectContrast
import com.sribs.bdd.v3.adapter.CreateModuleFloorPictureAdapter
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.bean.db.v3.project.v3BuildingModuleDbBean
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.FileUtil
import com.sribs.common.utils.TimeUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ModuleFloorCreateTypeBuildingPresenter : BasePresenter(), IBasePresenter {

    private var mView: IProjectContrast.IModuleCreateTypeBuildingView? = null

    private var array: ArrayList<ModuleFloorBean>? = null

    private var picList: ArrayList<ModuleFloorPictureBean>? = null

    private var mProLeader: String? = ""

    var beanList: ArrayList<ModuleFloorBean>? = ArrayList()

    var modulePicBeanList: ArrayList<ModuleFloorPictureBean>? = ArrayList()

    private var currentBean = v3BuildingModuleDbBean()

    private val picAdapter by lazy {
        CreateModuleFloorPictureAdapter()
    }

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    fun initPicLits() {
        var manager = LinearLayoutManager(mView?.getContext())
        mView?.getPicRecycleView()?.layoutManager = manager
        picList = ArrayList()
        array = ArrayList()
        picAdapter.setData(picList!!)
        mView?.getPicRecycleView()?.adapter = picAdapter

    }

    fun getFloorList(activity: Activity, mLocalProjectId: Int, mBuildingId: Long) {

    }

    fun refreshPicList(mData: ArrayList<ModuleFloorPictureBean>) {
        picList?.addAll(mData)
        picAdapter.notifyDataSetChanged()

    }

    private var mBldId: Long? = -1

    fun createLocalBuildingsInTheModule(
        activity: Activity,
        mLocalProjectId: Int,
        moduleName: String,
        mBuildingId: Long,
        mModuleId: Long,
        remoteId: String
    ) {
        if (picList.isNullOrEmpty()) {
            mView?.onMsg("图纸不能为空")
            return
        }

        createLocalFacadesDrawingInTheBuilding(activity, mLocalProjectId, mBuildingId, moduleName)

/*
        var bean = v3BuildingModuleDbBean()

        bean.id = mModuleId

        bean.drawings = mAppFacadeDrawingList
        bean.buildingId = mBuildingId
        bean.projectId = mLocalProjectId.toLong()
        bean.moduleName = moduleName

        bean.updateTime = TimeUtil.YMD_HMS.format(Date())
        bean.remoteId = remoteId
        bean.isChanged = 1
*/

        addDisposable(
            mDb.updateBuildingModule(mModuleId,mAppFacadeDrawingList?:ArrayList(),1)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mView?.onMsg("success")
                    mView?.createModuleConfigSuccess()

                }, {
                    mView?.onMsg("gg")
                    it.printStackTrace()
                })
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun copyDrawingsToLocalCache(
        activity: Activity,
        pictureBean: ArrayList<ModuleFloorPictureBean>,
        cacheRootDir: String
    ) {
      /*  var filters = pictureBean.filter {
            it.uri != null
        }
        LogUtils.d("filters: " + filters)*/
        var index = -1

        var name= ""
        var needToPDF = false
        var cacheFileParent =File("")
        var cacheFile = File("")

        addDisposable(Observable.fromIterable(pictureBean)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .flatMap {
                ++index
                 cacheFileParent = File(cacheRootDir + mCurDrawingsDir+index)
                cacheFileParent.mkdirs()
               /*  cacheFile = File(cacheFileParent, it.name)

                if (cacheFile.isFile){
                    cacheFile.delete()
                }

                LogUtils.d("图纸缓存目录： " + cacheFile.toString())
                if (cacheFile != null) {
                    if(!it.uri.isNullOrEmpty()) {
                        FileUtil.copyFileTo(activity, Uri.parse(it.uri), cacheFile.absolutePath)
                    }else{
                        FileUtil.copyTo(File(it.url),cacheFile)
                    }
                }*/

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


    private fun createLocalFacadesDrawingInTheBuilding(
        activity: Activity,
        mLocalProjectId: Int,
        mBuildingId: Long,
        moduleName: String
    ) {
        println("leon createLocalFloorsInTheBuilding mBldId=${mBldId}")
        mAppFacadeDrawingList!!.clear()


        //赋值模块名作为图纸上级目录
        mCurDrawingsDir =
            "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + mProName + "/" + moduleName + "/"


        if (picList != null && picList!!.size > 0) {
            var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)

            copyDrawingsToLocalCache(activity, picList!!, cacheRootDir)

            picList!!.forEachIndexed { index, it ->

                var name =""
                if(!it.name.endsWith("pdf")){
                    name = it.name.replace(".","")+".pdf"
                }else{
                    name = it.name
                }


                var cacheFilePath = File(cacheRootDir + mCurDrawingsDir+index, name)

                LogUtils.d("absolutePath "+ cacheFilePath.absolutePath)
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


    fun initLocalData(mModuleId: Long) {
        beanList!!.clear()
        picList!!.clear()
        addDisposable(
            mDb.getv3BuildingModule(mModuleId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.drawings?.forEach { b ->
                        var bean = ModuleFloorPictureBean(
                            name = b.fileName!!,
                            uri = "",
                            url = b.localAbsPath,
                        )
                        LogUtils.d(bean.name + b.localAbsPath)
                        picList!!.add(bean)
                    }
                    currentBean = it

                    LogUtils.d(picList!!.get(0).name)
                    picAdapter.setData(picList!!)


                }, {
                    mView?.onMsg("gg")
                    it.printStackTrace()
                })
        )
    }


    private var mAppFacadeDrawingList: ArrayList<DrawingV3Bean>? = ArrayList<DrawingV3Bean>()


    var floorList: ArrayList<Floor> = ArrayList<Floor>()


    private lateinit var mPrefs: SharedPreferences
    private var mCurDrawingsDir: String? = ""
    private var mProName: String? = ""
    private var mBldNo: String? = ""

    override fun bindView(v: IBaseView) {
        mView = v as IProjectContrast.IModuleCreateTypeBuildingView
        mPrefs = mView!!.getContext()!!.getSharedPreferences("createProject", Context.MODE_PRIVATE)
        mProLeader = mPrefs.getString(ModuleHelper.CUR_PRO_LEADER, "")
        mProName = mPrefs.getString(ModuleHelper.CUR_PRO_NAME, "")
        mBldNo = mPrefs.getString(ModuleHelper.CUR_BLD_NO, "")
        mCurDrawingsDir =
            "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + mProName + "/" + mBldNo + "/"
        initPicLits()
    }

    override fun unbindView() {
        mView = null
    }


}