package com.sribs.bdd.module.project

import android.util.ArrayMap
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.exception.MsgThrowable
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.action.Dict
import com.sribs.bdd.bean.UnitConfigType
import com.sribs.common.bean.db.ConfigBean
import com.sribs.common.bean.db.ProjectBean
import com.sribs.common.bean.db.UnitBean
import com.sribs.common.bean.net.*
import com.sribs.common.module.BasePresenter
import com.sribs.common.net.HttpApi
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.TimeUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @date 2021/7/27
 * @author elijah
 * @Description
 */
class ProjectUnitPresenter:BasePresenter(),IProjectContrast.IUnitPresenter {
    private var mView:IProjectContrast.IUnitView?=null

    private var mData:UnitBean?=null

    private val mDb by lazy { ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService }

    override fun getLocalUnit(unitId: Long) {
        addDisposable(mDb.getUnit(unitId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                if (it.isNotEmpty()){
                    mData = it[0]
                }
                mView?.onLocalUnit(it)
            },{
                it.printStackTrace()
            }))
    }

    override fun getLocalUnit(projectId: Long,res:(List<UnitBean>)->Unit) {
        addDisposable(mDb.getAllUnit(projectId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                res(it)
                mView?.onLocalUnit(it)
            },{
                it.printStackTrace()
            })
        )

    }

    override fun createLocalUnit(projectId: Long,bldId:Long, unitNo:String?, res: (Long) -> Unit) {
        println("leon createLocalUnit")
        addDisposable(mDb.updateUnit(UnitBean(projectId=projectId, bldId, unitNo = unitNo))
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                res(it)

            },{
                it.printStackTrace()
            }))
    }

    override fun updateLocalUnit(unitNo: String?, floorSize: Int?, neighborSize: Int?, floorType: Int?) {
        println("leon updateLocalUnit")
        if (mData==null)return
        if (mData!!.isSame(unitNo, floorSize, neighborSize, floorType))return

        addDisposable(mDb.updateUnit(mData!!.also { b->
            if (unitNo!=null){
                b.unitNo = unitNo
            }
            if (floorSize?:-1>=0){
                b.floorSize = floorSize
            }
            if (neighborSize?:-1>=0){
                b.neighborSize = neighborSize
            }
            if (floorType!=null){
                b.floorType = floorType
            }
        }).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            },{
                it.printStackTrace()
            }))
    }

    override fun delLocalUnit(unitId: Long) {

        addDisposable(mDb.deleteUnit(listOf(unitId))
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                LOG.I("123","del unit  it=$it")
                mDb.deleteConfigByUnitId(unitId)
            }
            .subscribe({
                LOG.I("123","del unit config it=$it")
            },{
                it.printStackTrace()
            }))
    }

    override fun getUnitConfig(unitId: Long) {
        addDisposable(mDb.getUnitConfig(unitId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mView?.onLocalConfig(it)
            },{
                it.printStackTrace()
            }))
    }

    override fun copyUnit(unitId: Long, res: (Long) -> Unit) {
        addDisposable(mDb.getUnitOnce(unitId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                var src = it[0]
                var des = src.copy()
                des.unitNo = src.unitNo+"-副本"
                des.unitId = null
                des.createTime = null
                des.updateTime = null
                des.remoteId = null
                mDb.updateUnit(des)
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                res(it)
            },{
                it.printStackTrace()
            }))
    }

    override fun uploadUnit(unitId: Long, bCover: Boolean) {

        var localProject:ProjectBean?=null
        var localUnit:UnitBean?=null
        // step 0 获取 unit
        mDb.getUnitOnce(unitId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {  //step 1 获取project
                localUnit = it[0]
                mDb.getProjectOnce(localUnit!!.projectId!!).toObservable()
            }
            .observeOn(Schedulers.io())
            .flatMap { //step2 更新project
                localProject = it[0]
                HttpManager.instance.getHttpService<HttpApi>()
                    .updateProject(ProjectUpdateReq(
                        localProject!!.remoteId,
                        localProject!!.name?:"",
                        Dict.getLeaderId(localProject!!.leader?:"")?:"",
                        localProject!!.buildNo?:""
                    ))
            }
            .observeOn(Schedulers.computation())
            .flatMap { // step 3 remote projectId 保存数据库
                checkResult(it)
                localProject!!.remoteId = it.data!!.projectId
                mDb.updateProject(localProject!!)
            }
            .observeOn(Schedulers.computation())
            .flatMap { // step 4 获取 所有config
                mDb.getUnitConfigOnce(unitId).toObservable()
            }
            .observeOn(Schedulers.io())
            .flatMap { // step 5 更新服务器
                var floorMap = ArrayMap<Int,ArrayList<ConfigBean>>()
                it.forEach { b->
                    if (floorMap[b.floorIdx] == null){
                        floorMap[b.floorIdx] = ArrayList()
                    }
                    if(b.configType == UnitConfigType.CONFIG_TYPE_FLOOR_BOTTOM.value ||
                        b.configType == UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL.value ||
                        b.configType == UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value ){
                        floorMap[b.floorIdx]!!.add(0,b)
                    }else{
                        floorMap[b.floorIdx]!!.add(b)
                    }

                }
                var floors = ArrayList<ConfigFloor>()
                floorMap.forEach { (floorIdx, l) ->
                    var b = l[0]
                    var corridor = if(b.corridorNum.isNullOrEmpty() && b.corridorConfig.isNullOrEmpty()){
                        null
                    } else {
                        var corridorConfigs =  b.corridorConfig?.split(",")?.map { c->
                            Config(c,if(Dict.isConfigCustom(mView!!.getContext()!!,b.configType!!,c)) 1 else 0)
                        }
                        ConfigCorridor(b.corridorNum, corridorConfigs)
                    }
                    var platform = if(b.platformNum.isNullOrEmpty() && b.platformConfig.isNullOrEmpty()){
                        null
                    } else {
                        var platformConfigs = b.platformConfig?.split(",")?.map { c->
                            Config(c,if(Dict.isConfigCustom(mView!!.getContext()!!,b.configType!!,c)) 1 else 0)
                        }
                        ConfigPlatform(b.platformNum,platformConfigs)
                    }
                    var rooms = ArrayList<ConfigRoom>()
                    l.forEach { r->
                        var room = if(r.config1.isNullOrEmpty() && r.config2.isNullOrEmpty()){
                            null
                        } else {
                            var roomConfigs = r.config1?.split(",")?.map { c->

                                Config(c,if(Dict.isConfigCustom(mView!!.getContext()!!,r.configType!!,c)) 1 else 0)
                            }
                            var duplexRoomConfigs = r.config2?.split(",")?.map{ c->
                                Config(c,if(Dict.isConfigCustom(mView!!.getContext()!!,r.configType!!,c)) 1 else 0)
                            }
                            ConfigRoom(r.neighborIdx,r.neighborNum?:"",
                                if(r.unitType == 1) "duplex" else "normal",
                                roomConfigs,duplexRoomConfigs)
                        }
                        if(room!=null){
                            rooms.add(room)
                        }
                    }
                    var cf = ConfigFloor(
                        floorIdx,
                        b.floorNum?:"",
                        when(b.configType){
                            UnitConfigType.CONFIG_TYPE_FLOOR_BOTTOM.value->"bottom"
                            UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM.value->"bottom"
                            UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL.value->"standard"
                            UnitConfigType.CONFIG_TYPE_UNIT_NORMAL.value->"standard"
                            UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value->"top"
                            UnitConfigType.CONFIG_TYPE_UNIT_TOP.value->"top"
                            else ->""
                        },
                        corridor,
                        platform,
                        rooms
                    )

                    floors.add(cf)
                }
                var req = UploadUnitConfigReq(
                    localProject!!.remoteId?:"",
                    localUnit!!.remoteId,
                    localUnit!!.unitNo.toString(),
                    localUnit!!.floorSize?:0,
                    localUnit!!.neighborSize?:0,
                    if(localUnit!!.floorType == 1)"double" else "single",
                    floors,
                    localUnit!!.version?:1,
                    if(bCover) 1 else 0,
                    ""+TimeUtil.dateToStamp(localProject!!.updateTime!!)
                )
                HttpManager.instance.getHttpService<HttpApi>()
                    .unitUpload(req)
            }
            .observeOn(Schedulers.computation())
            .flatMap {  // step 6 更新 remote unit Id
                checkResult(it)
                localUnit!!.remoteId = it.data!!.unitId
                localUnit!!.version = it.data!!.version
                localUnit!!.status = 1
                mDb.updateUnit(localUnit!!)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mView?.onUnitUpdate(true)
                updateProjectStatus(localProject!!)
            },{
                mView?.onMsg(checkError(it))
                mView?.onUnitUpdate(false)
            })
    }

    private fun updateProjectStatus(localProject:ProjectBean){
        addDisposable(mDb.getAllUnitOnce(localProject.id!!).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                var hasNoUpload = (it.find { u->u.status == 0 }!=null)
                if (hasNoUpload){
                    if (localProject.status!=0){
                        localProject.status = 0
                        mDb.updateProject(localProject)
                    }else{
                        throw MsgThrowable("no need update")
                    }
                }else{
                    if (localProject.status!=1){
                        localProject.status = 1
                        mDb.updateProject(localProject)
                    }else{
                        throw MsgThrowable("no need update")
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","更新project 状态  id=$it")
            },{
                it.printStackTrace()
            }))
    }


    override fun bindView(v: IBaseView) {
        mView = v as IProjectContrast.IUnitView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}