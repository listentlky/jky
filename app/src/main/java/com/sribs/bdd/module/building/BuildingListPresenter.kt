package com.sribs.bdd.module.building

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.Config
import com.sribs.bdd.R
import com.sribs.bdd.action.Dict
import com.sribs.bdd.bean.BuildingMainBean
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.V3VersionBean
import com.sribs.common.bean.net.v3.*
import com.sribs.common.module.BasePresenter
import com.sribs.common.net.HttpApi
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.TimeUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * create time: 2022/9/19
 * author: bruce
 * description:
 */
class BuildingListPresenter : BasePresenter(), IBuildingContrast.IBuildingListPresent {

    private var mView: IBuildingContrast.IBuildingListView? = null

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    private val mStateArr by lazy {
        mView?.getContext()?.resources?.getStringArray(R.array.main_project_status)?: emptyArray()
    }

    /**
     * 获取本地楼
     */
    override fun getAllBuilding(localProject: Long,projectUUID:String) {
        addDisposable(mDb.getBuildingByProjectId(localProject)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                var list = ArrayList(it.map { b ->
                    com.sribs.bdd.bean.BuildingMainBean(
                        projectUUID = b.projectUUID,
                        projectId = localProject,
                        bldUUID = b.UUID,
                        bldId = b.id ?: -1,
                        bldName = b.bldName?:"",
                        bldType = b.bldType?:"",
                        leader = b.leader?:"",
                        inspectorName = b.inspectorName?:"",
                        remoteId = null,
                        createTime = if (b.createTime==null)"" else TimeUtil.YMD_HMS.format(b.createTime),
                        updateTime = if (b.updateTime==null)"" else TimeUtil.YMD_HMS.format(b.updateTime),
                        parentVersion= b.parentVersion!!,
                        version = b.version!!,
                        status = mStateArr[b.status?:0],
                        aboveGroundNumber = b.aboveGroundNumber?:0,
                        underGroundNumber = b.underGroundNumber?:0
                    )
                })
                LogUtils.d("获取本地数据库楼表: " + list.toString())
                mView!!.onAllBuilding(ArrayList(list.sortedByDescending { b->b.updateTime }))

               /* if(!Config.isNetAvailable){
                    LogUtils.d("无网络 直接展示本地数据: ")

                }else{
                    LogUtils.d("有网络 获取云端楼: ")
                    getBuildingRemote(projectUUID,list)
                }*/
            })
    }

    /**
     * 获取云端楼
     */
    fun getBuildingRemote(projectRemoteId: String,version:Int,localList:ArrayList<BuildingMainBean>){
        LogUtils.d("请求云端楼数据项目ID: ${projectRemoteId}")
        addDisposable(HttpManager.instance.getHttpService<HttpApi>()
            .getV3BuildingList(projectRemoteId,version)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtils.d("请求云端楼数据: "+it.toString())
                checkResult(it)
                var buildingMainBean = ArrayList<BuildingMainBean>()

                if(it.data == null){
                    LogUtils.d("云端楼数据为空 直接返回")
                    mView?.onAllBuilding(ArrayList(localList.sortedByDescending { b->b.updateTime }))
                    return@subscribe
                }

             /*   it.data!!.records.forEach { remoteBean->
                    var i = localList.indexOfFirst { localBean->
                        !localBean.remoteId.isNullOrEmpty() && localBean.remoteId == remoteBean.projectId
                    }

                    if (i>=0) {
                        var localBean = localList[i]
                        //判断时间
                        if (TimeUtil.isBefore(localBean.updateTime, remoteBean.updateTime)) {
                            localList[i].hasNewer = true
                        }
                        localBean.bldUUID = remoteBean.projectId
                        localBean.remoteData = remoteBean
                    }
                }*/

              /*  it.data!!.records.forEach {  remoteBean->
                    var i = localList.indexOfFirst { localBean->
                        localBean.bldUUID == remoteBean.projectId
                    }
                    if (i>=0) {
                        var localBean = localList[i]
                        //判断时间
                        if (TimeUtil.isBefore(localBean.updateTime , remoteBean.updateTime)) {
                            localList[i].hasNewer = true
                        }
                        localBean.remoteData = remoteBean
                        localBean.remoteId = remoteBean.projectId
                    }
                }*/


                // 本地中没有
             /*   var onlyRemoteList =  it.data!!.records.filter { remoteBean->
                    localList.find { localBean->
                        (!localBean.remoteId.isNullOrEmpty() && localBean.remoteId == remoteBean.projectId) ||
                                (localBean.bldName == remoteBean.projectName && localBean.remoteId.isNullOrEmpty())
                    }==null
                }?.map { b -> BuildingMainBean(
                    localId = -1,
                    localUUID = b.projectId,
                    remoteId= b.projectId,
                    updateTimeYMD = TimeUtil.time2YMD(b.updateTime),
                    status = mStateArr[2],
                    address = b.projectName,
                    leader = b.leaderName?:"",
                    inspector = b.inspectors?.joinToString(separator = "、")?:""
                ).also { _b->
                    _b.updateTime = b.updateTime
                    _b.createTime = b.createTime
                    _b.name = b.projectName
                    _b.remoteData = b
                } }*/
                buildingMainBean.addAll(localList)
            //    buildingMainBean.addAll(onlyRemoteList)
                mView?.onAllBuilding(ArrayList(buildingMainBean.sortedByDescending { b->b.updateTime }))

            },{
                LogUtils.d("${it.message}")
                mView?.onMsg(checkError(it))
                mView?.onAllBuilding(ArrayList(localList.sortedByDescending { b->b.updateTime }))
            }))
    }

    /**
     * 上传楼配置
     */
    fun uploadBuilding(beanMain: BuildingMainBean){
        var floorDrawingsMap: HashMap<String?, Any?> = HashMap()
        var inspectorList: List<String> =
            if (beanMain.inspectorName!!.contains("、")) beanMain.inspectorName!!.split("、") else Arrays.asList(beanMain.inspectorName!!)
        LogUtils.d("上传楼配置")

        addDisposable(mDb.getLocalFloorsInTheBuilding(beanMain.bldId!!)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({

                addDisposable(HttpManager.instance.getHttpService<HttpApi>()
                    .saveV3Building(V3BuildingSaveReq().also {
                        it.projectId = beanMain.projectUUID!!
                        it.buildingId = beanMain.bldUUID!!
                        it.buildName = beanMain.bldName!!
                        it.buildingType = ""
                        it.leaderId = Dict.getLeaderId(beanMain.leader!!)!!
                        it.leaderName = beanMain.leader!!
                        it.aboveGroundNumber = ""+beanMain.aboveGroundNumber
                        it.underGroundNumber = ""+beanMain.underGroundNumber
                        it.drawings = ArrayList() // 遗留图纸上传
                        it.floorDrawings = floorDrawingsMap // 遗留图纸上传
                        it.inspectors = inspectorList
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        LogUtils.d("上传楼配置成功: " + it.toString())

                    },{
                        LogUtils.d("上传楼配置失败: " + it.toString())
                        mView?.onMsg(checkError(it))

                    }))

            },{
                LogUtils.d("查询楼层表异常: "+it)
            }))
    }

    /**
     * 获取楼版本列表
     */
    fun getV3BuildingVersionHistory(projectRemoteId: String,buildingRemoteId:String,cb: (ArrayList<V3VersionBean>?) -> Unit){
        addDisposable(HttpManager.instance.getHttpService<HttpApi>()
            .getV3BuildingVersionList(V3VersionReq(projectRemoteId,buildingRemoteId))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                checkResult(it)
                LogUtils.d("查询楼版本列表："+it)
                cb(ArrayList(it.data!!.records.map {
                    V3VersionBean(
                        it.projectId,
                        it.projectName,
                        it.leaderName,
                        it.leaderId,
                        it.inspectors,
                        it.parentVersion,
                        it.version,
                        it.createTime
                    )
                }))
            },{
                LogUtils.d("查询楼版本列表失败："+it)
                mView?.onMsg(ERROR_HTTP)
                cb(null)
                it.printStackTrace()
            }))
    }

    /**
     * 下载指定版本楼
     */
    fun downloadBuildingConfig(
        remoteBuildingId:String,
        version:Int,
        cb: (Boolean) -> Unit
    ){
        addDisposable(HttpManager.instance.getHttpService<HttpApi>()
            .downloadV3BuildingVersionList(V3VersionDownloadReq(remoteBuildingId,version))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .subscribe({
                LogUtils.d("下载的楼版本数据: "+it)
                cb(true)
            },{
                cb(false)
            }))
        /*.concatMap {
            LogUtils.d("下载的项目版本数据")

        }*/

    }

    /**
     * 删除楼
     */
    fun deleteBuilding(beanMain: BuildingMainBean){
        //本地楼
        if(beanMain.bldId >0){
            var obList = ArrayList<Observable<Boolean>>()
            var bldId = beanMain.bldId
            obList.add(mDb.deleteBuilding(bldId))
            obList.add(mDb.deleteFloorByBuildingId(bldId))
            obList.add(mDb.deleteBuildingModuleByBuildingId(bldId))
            obList.add(mDb.deleteModuleFloorByBuildingId(bldId))

            var count = 0
            Observable.merge(obList)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    count++
                    LogUtils.d("删除楼 count=$count  $it")
                }, {
                    it.printStackTrace()
                })
        }

        if(!beanMain.remoteId.isNullOrEmpty()){
            LogUtils.d("删除网络数据")
            addDisposable(HttpManager.instance.getHttpService<HttpApi>()
                .deleteBuilding(
                    V3VersionDeleteReq(
                        beanMain.remoteId!!,
                        beanMain.parentVersion,
                        beanMain.version
                    )
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    checkResult(it)
                    LogUtils.d("删除远端楼成功：" + it.toString())
                    mView?.onMsg("删除楼成功")
                }, {
                    mView?.onMsg("删除远端楼失败" + checkError(it))
                })
            )
        }
    }

    override fun bindView(v: IBaseView) {
        mView = v as IBuildingContrast.IBuildingListView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}