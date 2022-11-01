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
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.DamageV3Bean
import com.sribs.common.bean.db.DrawingV3Bean
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

class ModuleFloorConfigCreateTypePresenter : BasePresenter(), IProjectContrast.IProjectCreateTypePresenter,
    CreateModuleFloorAdapter.ICallback {
    private var mView: IProjectContrast.IModuleCreateTypeView? = null

    private var array: ArrayList<ModuleFloorBean>? = null

    private var above = ArrayList<ModuleFloorBean>()
    private var before = ArrayList<ModuleFloorBean>()

    private var mProLeader: String? = ""

    var mAboveOldIndex = 0

    var mBeforeOldIndex = 0

    var beanList: ArrayList<ModuleFloorBean>? = ArrayList()


    var isFirstCome :Boolean = true //首次进入设置楼层数量时 不该进行新建楼层操作
    var isFirstCome2 :Boolean = true //首次进入设置楼层数量时 不该进行新建楼层操作



    private val floorAdapter by lazy {
        CreateModuleFloorAdapter(this)
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
        LogUtils.d("addAboveFlourList： " + num)

        if (num == 0) {
            array?.removeAll(above)
            floorAdapter.notifyDataSetChanged()
        } else if (num > 0) {
            array?.removeAll(above)

            if (mAboveOldIndex == 0 && above.size < 1) {
                for (i in 0 until num) {
                    var name = NumberUtil.num2Chinese(i + 1)
                    var buildingFloorBean = ModuleFloorBean(UUIDUtil.getUUID(name + "层"),name + "层", arrayListOf(), "地上",1)
                    above.add(buildingFloorBean)
                }
                array?.addAll(above)
                floorAdapter.notifyDataSetChanged()
                mAboveOldIndex = num
                return
            }

            if (mAboveOldIndex > 0 && mAboveOldIndex == num) {
                array?.addAll(above)
                floorAdapter.notifyDataSetChanged()
                mAboveOldIndex = num
                return
            }

            if (mAboveOldIndex > 0 && mAboveOldIndex > num) {
                for (i in 0 until mAboveOldIndex - num) {
                    above.removeAt(above.size - 1)
                }
                array?.addAll(above)
                floorAdapter.notifyDataSetChanged()
                //     flourAdapter.notifyItemChanged(above.size-1)
                mAboveOldIndex = num
                return

            }

            if (mAboveOldIndex > 0 && mAboveOldIndex < num) {
                for (i in mAboveOldIndex until num) {
                    var name = NumberUtil.num2Chinese(1 + i)
                    var buildingFloorBean = ModuleFloorBean(UUIDUtil.getUUID(name + "层"),name + "层", arrayListOf(), "地上",1)
                    above.add(buildingFloorBean)
                }
                array?.addAll(above)
                floorAdapter.notifyDataSetChanged()
                //  flourAdapter.notifyItemInserted(before.size+above.size)
                //  flourAdapter.notifyItemRangeChanged(before.size+above.size-1,before.size+above.size)
                mAboveOldIndex = num
                return
            }
        }else {
            mView?.onMsg("请输入正确的数字")
        }
    }


    //地下楼层层数
    @SuppressLint("NotifyDataSetChanged")
    override fun addAfterFlourList(num: Int) {
        if (isFirstCome2){
            isFirstCome2 = false
            return
        }
        LogUtils.d("addAfterFlourList： " + num)
        if (num == 0) {
            array?.removeAll(before)
            floorAdapter.notifyDataSetChanged()
        } else if (num > 0) {
            array?.removeAll(before)
            array?.removeAll(above)

            if (mBeforeOldIndex == 0 && before.size < 1) {
                for (i in 0 until num) {
                    var name = NumberUtil.num2Chinese(i + 1)
                    var buildingFloorBean = ModuleFloorBean(UUIDUtil.getUUID("负"+name + "层"),"负"+name + "层", arrayListOf(), "地下",0)
                    before.add(buildingFloorBean)
                }
                array?.addAll(before)
                array?.addAll(above)
                floorAdapter.notifyDataSetChanged()
                mBeforeOldIndex = num
                return
            }

            if (mBeforeOldIndex > 0 && mBeforeOldIndex == num) {
                array?.addAll(before)
                array?.addAll(above)
                floorAdapter.notifyDataSetChanged()
                mBeforeOldIndex = num
                return
            }

            if (mBeforeOldIndex > 0 && mBeforeOldIndex > num) {
                for (i in 0 until mBeforeOldIndex - num) {
                    before.removeAt(before.size - 1)
                }
                array?.addAll(before)
                array?.addAll(above)
                floorAdapter.notifyDataSetChanged()
                //   flourAdapter.notifyItemChanged(before.size-1)
                mBeforeOldIndex = num
                return

            }

            if (mBeforeOldIndex > 0 && mBeforeOldIndex < num) {
                for (i in mBeforeOldIndex until num) {
                    var name = NumberUtil.num2Chinese(1 + i)
                    var buildingFloorBean = ModuleFloorBean(UUIDUtil.getUUID("负"+name + "层"),"负"+name + "层", arrayListOf(), "地下",0)
                    before.add(buildingFloorBean)
                }
                array?.addAll(before)
                array?.addAll(above)
                floorAdapter.notifyDataSetChanged()
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
        mView?.getFloorRecycleView()?.layoutManager = manager
        array = ArrayList()
        array!!.addAll(before)
        array!!.addAll(above)

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
                    floorType = it.floorType
                )
            )
        }

        array?.clear()
        before.addAll(beanList!!.filter {
            it.floorType == 0
        })
        above.addAll(beanList!!.filter {
            it.floorType == 1
        })
        array?.addAll(before)
        array?.addAll(above)

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

        addDisposable(mDb.getModuleFloorByModule(mModuleId)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({ moduleFloorList->
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
                mDb.deletev3ModuleFloor(mLocalProjectId.toLong(), mBuildingId, mModuleId)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation())
                    .subscribe({
                        dispose()
                        LogUtils.d("createLocalModule new building id=${mModuleId}")
                        createLocalFloorsInTheModule(
                            mBuildingId,
                            mModuleId,
                        )
                    },{

                    })
            },{

            })
        )
    }

    private fun createLocalFloorsInTheModule(
        mBuildingId: Long,
        moduleId: Long,
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
                    LogUtils.d("配置模块层成功: ${it}")
                    index++
                    if(index == floorList.size){
                        mDb.updateBuildingModule(moduleId,1)
                        mView?.createModuleConfigSuccess()
                    }
                },{
                    mView?.onMsg("保存到本地楼层表失败: "+it)
                    it.printStackTrace()
                })
        }
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

        LogUtils.d("获取当前模块下所有层数据: "+v3ModuleFloorDbBean)

        LogUtils.d("当前配置层信息: "+array)

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
                LogUtils.d("楼层图纸缓存目录：" + cacheFilePath)

                var damageList = ArrayList<DamageV3Bean>()

                LogUtils.d("当前层xinxi : "+item)

                v3ModuleFloorDbBean.forEach { moduleFloor->
                    if(floorBean.floorId == moduleFloor.floorId){
                        LogUtils.d("存在同一层: "+floorBean.floorId)
                        moduleFloor.drawingsList?.forEach { drawing->
                            if(item.drawingId == drawing.drawingID){
                                LogUtils.d("存在同一张图纸: "+item.drawingId)
                                damageList = drawing.damage?:ArrayList()
                                LogUtils.d("损伤为: "+damageList)
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

        var name= ""
        var needToPDF = false
        var cacheFileParent =File("")
        var cacheFile = File("")

        pictureBean.forEachIndexed { index, it ->

            var cacheFileParent = File(cacheRootDir + mCurDrawingsDir)
            if(!floorName.isNullOrEmpty()) { // 为空认为是楼图纸   不为空认为是楼层图纸
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
            LogUtils.d("复制图纸到缓存目录: ${cacheFile}")
        }

    }

  /*  private fun getPicList(activity: Activity, mLocalProjectId: Int, mBuildingId: Long) {
        if (picList != null && picList!!.size > 0) {
            var cachePath: String
            var cacheRootDir: String = FileUtil.getDrawingCacheRootDir(mView!!.getContext()!!)
            var curTime: Long = System.currentTimeMillis()

            copyDrawingsToLocalCache(activity, picList!!,null, cacheRootDir)



            picList!!.forEach {


                var name =""
                if(!it.name.endsWith("pdf")){
                    name = it.name.replace(".","")+".pdf"
                }else{
                    name = it.name
                }

                cachePath = cacheRootDir + mCurDrawingsDir + name
                LogUtils.d("楼图纸缓存目录：" + cachePath)
                var drawing = Drawing(
                    -1,
                    mLocalProjectId.toLong(),
                    mBuildingId,
                    -1,
                    -1,
                    "",
                    name,
                    "overall",
                    null,
                    FileUtil.getFileExtension(name),
                    cachePath,
                    cachePath,
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

    }*/

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