package com.sribs.bdd.module.building

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.Config
import com.sribs.bdd.R
import com.sribs.bdd.action.Dict
import com.sribs.bdd.bean.BuildingMainBean
import com.sribs.bdd.bean.MainProjectBean
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.FloorBean
import com.sribs.common.bean.net.v3.V3BuildingSaveReq
import com.sribs.common.module.BasePresenter
import com.sribs.common.net.HttpApi
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.TimeUtil
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
                        bldName = b.bldName!!,
                        bldType = b.bldType!!,
                        leader = b.leader,
                        inspectorName = b.inspectorName,
                        remoteId = null,
                        createTime = if (b.createTime==null)"" else TimeUtil.YMD_HMS.format(b.createTime),
                        updateTime = if (b.updateTime==null)"" else TimeUtil.YMD_HMS.format(b.updateTime),
                        version = b.version!!,
                        status = mStateArr[b.status?:0],
                        aboveGroundNumber = b.aboveGroundNumber,
                        underGroundNumber = b.underGroundNumber
                    )
                })
                LogUtils.d("获取本地数据库楼表: " + list.toString())

                if(!Config.isNetAvailable){
                    LogUtils.d("无网络 直接展示本地数据: ")
                    mView!!.onAllBuilding(ArrayList(list.sortedByDescending { b->b.updateTime }))
                }else{
                    LogUtils.d("有网络 获取云端楼: ")
                    getBuildingRemote(projectUUID,list)
                }
            })
    }

    /**
     * 获取云端楼
     */
    fun getBuildingRemote(projectUUID: String,localList:ArrayList<BuildingMainBean>){
        LogUtils.d("请求云端楼数据项目ID: ${projectUUID}")
        addDisposable(HttpManager.instance.getHttpService<HttpApi>()
            .getV3BuildingList(projectUUID)
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

    override fun bindView(v: IBaseView) {
        mView = v as IBuildingContrast.IBuildingListView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}