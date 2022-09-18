package com.sribs.bdd.module.main

import android.content.Context
import com.cbj.sdk.libbase.exception.MsgThrowable
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.R
import com.sribs.bdd.bean.MainProjectBean
import com.sribs.bdd.bean.UnitConfigType
import com.sribs.bdd.module.BaseUnitConfigPresenter
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.HistoryBean
import com.sribs.common.bean.db.ConfigBean
import com.sribs.common.bean.db.ProjectBean
import com.sribs.common.bean.db.UnitBean
import com.sribs.common.bean.net.*
import com.sribs.common.net.HttpApi
import com.sribs.common.utils.TimeUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.sql.Date

/**
 * @date 2021/8/5
 * @author elijah
 * @Description
 */
class MainPresenter:BaseUnitConfigPresenter(),IMainListContrast.IMainPresenter {
    private var mView:IMainListContrast.IMainView?=null

    override fun projectGetConfigHistory(remoteProjectId: String, cb: (ArrayList<HistoryBean>?) -> Unit) {
        addDisposable(HttpManager.instance.getHttpService<HttpApi>()
            .getConfigHistoryList(HistoryListReq(projectId = remoteProjectId))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                checkResult(it)
                LOG.I("123","records ${it.data!!.records}")
                cb(ArrayList(it.data!!.records.map { b->
                    HistoryBean(
                        b.historyId,null,null,b.updateTime,b.userName
                    )
                }))
            },{
                mView?.onMsg(ERROR_HTTP)
                cb(null)
                it.printStackTrace()
            }))
    }

    override fun projectGetRecordHistory(remoteProjectId: String, cb: (ArrayList<HistoryBean>?) -> Unit) {
        addDisposable(HttpManager.instance.getHttpService<HttpApi>()
            .getRecordHistoryList(HistoryListReq(projectId = remoteProjectId))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                checkResult(it)
                cb(ArrayList(it.data!!.records.map { b->
                    HistoryBean(
                        b.configHistoryId,b.recordHistoryId,null,b.updateTime,b.userName
                    )
                }))
            },{
                mView?.onMsg(ERROR_HTTP)
                cb(null)
                it.printStackTrace()
            }))
    }

    private fun deleteProjectAllInfo(localProjectId: Long, cb: (Boolean) -> Unit) {
        addDisposable(mDb.deleteProject(ProjectBean(id = localProjectId))
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                mDb.deleteConfig(localProjectId)
            }
            .flatMap {
                mDb.deleteConfig(localProjectId)
            }
            .flatMap {
                mDb.deleteHouseStatusByProject(localProjectId)
            }
            .flatMap {
                mDb.deleteRoomStatusByProject(localProjectId)
            }
            .flatMap {
                mDb.deleteRoomDetailByProject(localProjectId)
            }
            .flatMap {
                mDb.deleteReportByProjectId(localProjectId)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                cb(it)
            },{
                cb(false)
                it.printStackTrace()
            })
        )
    }

    private fun updateProject(bean: MainProjectBean):Observable<Long>{
        var b = bean.remoteData?:throw NullPointerException("remoteData==null")
        return mDb.updateProject( ProjectBean(
            name = b!!.projectName,
            leader = b.leaderName,
            inspector = b.inspectors?.joinToString(separator = "、"),
            buildNo = "",
            createTime = Date(TimeUtil.YMD_HMS.parse(b.createTime).time),
            updateTime = Date(TimeUtil.YMD_HMS.parse(b.updateTime).time),
            remoteId = b.projectId
        ).also {
            if (bean.localId>0){
                it.id = bean.localId
            }
            var status =  mView?.getContext()?.resources?.getStringArray(R.array.main_project_status)?.indexOf(bean.status)
            if (status?:-1<0) status = null
            if (status?:-1>=0){
                it.status = status
            }
        })
    }

    override fun projectDownLoadConfig(
        historyId: String,
        bean: MainProjectBean,
        cb: (Boolean) -> Unit
    ) {
        var resConfig:ProjectConfigDownloadRes?=null
        var resNo = 0;
        addDisposable(HttpManager.instance.getHttpService<HttpApi>()
            .projectConfigDownload(ProjectDownloadReq(historyId))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .concatMap { //更新Project
                checkResult(it)
                resConfig = it.data!!
                updateProject(bean)

            }
            .map {
                if (bean.localId>0)bean.localId else it
            }
            .observeOn(Schedulers.computation())
            .concatMap { //更新 unit
                checkUnitConfig(it,1,resConfig!!.configUnits)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                cb(it)
            },{
                mView?.onMsg(checkError(it))
                cb(false)
            })
        )
    }

    override fun ConfigBean(
        projectId: Long,
        bldId: Long,
        unitId: Long,
        configId: Long,
        floorIdx: Int?,
        configType: Int?,
        floorNum: String,
        createTime: Date?,
        updateTime: Date?,
    ): ConfigBean {
        TODO("Not yet implemented")
    }

    private fun createPartNo(b:ConfigBean):String = when(b.configType) {
        UnitConfigType.CONFIG_TYPE_FLOOR_BOTTOM.value -> b.floorNum + "层"
        UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL.value -> b.floorNum + "层"
        UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value -> b.floorNum + "层"
        UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM.value -> b.floorNum + b.neighborNum + "室"
        UnitConfigType.CONFIG_TYPE_UNIT_NORMAL.value -> b.floorNum + b.neighborNum + "室"
        UnitConfigType.CONFIG_TYPE_UNIT_TOP.value -> b.floorNum + b.neighborNum + "室"
        else -> ""
    }

    private fun updateHouseStatus(){

    }


    private fun searchLocalConfig(projectId: Long, unitId: Long, remoteUnit:RecordUnit){
        mDb.getUnitConfigOnce(unitId).toObservable()
            .subscribe({
                LOG.I("123","it=$it")
                remoteUnit.parts.forEach { r->//查找configId
                    var localConfigBean = it.find { local-> r.partNo.contains(createPartNo(local)) }
                    if (localConfigBean!=null){
                        var configId = localConfigBean.configId



                    }else{ //error 有记录缺没有配置
                    }
                }
            },{
                it.printStackTrace()
            },{

            })
    }

    private fun searchLocalUnit(units:List<RecordUnit>):Observable<Long>{
        var obList = ArrayList<Observable<List<UnitBean>>>()
        units.forEach {
            LOG.I("123","remoteId=${it.unitId}")
            obList.add(mDb.getUnitOnce(it.unitId).toObservable())
        }
        return Observable.merge(obList)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                if (it.isEmpty()){
                    throw MsgThrowable("记录的单元id与配置的单元id不符")
                }
                var localUnit = it[0]
                var projectId = localUnit.projectId!!
                var unitId = localUnit.unitId!!
                var remoteUnit = units.find { r-> r.unitId == localUnit.remoteId }
                checkConfigRecord(projectId,unitId,remoteUnit!!.parts,remoteUnit.createTime,remoteUnit.updateTime)
            }
    }

    private fun downloadProjectRecord(historyId: String,  cb:(Boolean)->Unit){
        var unitIdx = 0
        addDisposable(HttpManager.instance.getHttpService<HttpApi>()
            .projectRecordDownload(ProjectDownloadReq(historyId))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .flatMap {
                checkResult(it)
                searchLocalUnit(it.data!!.units)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                unitIdx++
                LOG.I("123","单元 idx:$unitIdx  更新:$it")
                cb(true)//更新了至少一个单元
            },{
                mView?.onMsg(checkError(it))
                cb(false)
            })


        )
    }

    override fun projectDownLoadRecord(
        configHistoryId: String,
        recordHistoryId: String,
        bean: MainProjectBean,
        cb:(Boolean)->Unit
    ) {
        projectDownLoadConfig(configHistoryId, bean){
            LOG.I("123","downLoadProjectConfig  $it")
            downloadProjectRecord(recordHistoryId,cb)
        }
    }

    override fun projectGetLocalSummary(projectId: Long, cb: (ProjectSummaryRes) -> Unit) {
        var allCount = 0

        mDb.getAllUnitOnce(projectId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                it.forEach { u->
                    var t = u.floorSize?.times(u.neighborSize?:0)
                    allCount += t?:0
                }
                mDb.getHouseStatusByProjectOnce(projectId).toObservable()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                var roomList = it.filter { s->s.houseType == 2 }
                var noAccess = roomList.filter { s-> s.houseStatus?.contains("不让进")?:false && !(s.houseStatus?.contains("无人")?:false) }.count()
                var noOne = roomList.filter { s-> !(s.houseStatus?.contains("不让进")?:false) && s.houseStatus?.contains("无人")?:false }.count()
                var both = roomList.filter { s-> s.houseStatus?.contains("不让进")?:false && s.houseStatus?.contains("无人")?:false }.count()
                var finish = roomList.filter { s-> s.isFinish == 1}.count()
                var res = ProjectSummaryRes(
                    allCount,
                    finish,
                    noAccess+both,
                    noOne+both,
                    (finish-noAccess-noOne-both).toFloat()*100f / allCount
                )
                cb(res)
            },{
                it.printStackTrace()
            })
    }

    override fun projectGetSummary(remoteProjectId: String, cb: (ProjectSummaryRes) -> Unit) {
        HttpManager.instance.getHttpService<HttpApi>()
            .getProjectSummary(ProjectSummaryReq(remoteProjectId))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                checkResult(it)
                cb(it.data!!)
            },{
                mView?.onMsg(checkError(it))
            })
    }

    override fun projectDelete(projectId: Long) {
        //删除本地数据库
        LogUtils.d("删除本地数据")
        var obList = ArrayList<Observable<Boolean>>()
        obList.add(mDb.deleteProject(ProjectBean(id=projectId)).map { true })
        obList.add(mDb.deleteUnit(projectId))
        obList.add(mDb.deleteConfig(projectId))
        obList.add(mDb.deleteHouseStatusByProject(projectId))
        obList.add(mDb.deleteRoomStatusByProject(projectId))
        obList.add(mDb.deleteRoomDetailByProject(projectId))
        obList.add(mDb.deleteReportByProjectId(projectId))
        var count = 0
        Observable.merge(obList)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                count++
                LOG.I("123","delete project count=$count  $it")
            },{
                it.printStackTrace()
            })
        //删除网络数据
        LogUtils.d("删除网络数据")
        addDisposable(HttpManager.instance.getHttpService<HttpApi>()
            .deleteProject(projectId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                checkResult(it)
                LogUtils.d("删除远端项目成功："+it.toString())
                mView?.onMsg("删除项目成功")
            },{
                LogUtils.d("删除远端项目失败")
                mView?.onMsg(checkError(it))
            }))

    }


    override fun bindView(v: IBaseView) {
        mView = v as IMainListContrast.IMainView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }

    override fun getContext(): Context = mView?.getContext()!!
}