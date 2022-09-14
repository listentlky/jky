package com.sribs.bdd.module.project

import android.content.Context
import com.cbj.sdk.libbase.exception.MsgThrowable
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.action.Config
import com.sribs.bdd.bean.UnitConfigType
import com.sribs.bdd.bean.data.ProjectConfigBean
import com.sribs.bdd.module.BaseUnitConfigPresenter
import com.sribs.common.bean.db.ConfigBean
import com.sribs.common.utils.Util
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.sql.Date

/**
 * @date 2021/7/28
 * @author elijah
 * @Description
 */
class ProjectConfigPresenter:BaseUnitConfigPresenter(),IProjectContrast.IConfigPresenter {

    var mView:IProjectContrast.IConfigView?=null


    override fun getLocalConfig(configId: Long) {
        addDisposable(mDb.getConfig(configId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mView?.onLocalConfig(it)
            },{
                it.printStackTrace()
            }))

    }

    override fun updateLocalConfig(bean:ConfigBean) { //TODO 删除配置需要改变roomStatus  roomDetail 否则更新记录会错误
        var projectId = bean.projectId
        var unitId = bean.unitId
        var configId = bean.configId

        if (configId?:-1>0){
            mDb.getConfigOnce(configId!!).toObservable()
        }else{
            Observable.create { o->
                o.onNext(listOf(bean))
            }
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap { //step 删除多余的记录
                if (it.isEmpty())throw MsgThrowable("本地配置不存在 configId=$configId")
                var b = it[0]
                var needDelList = ArrayList<String>()
                var obList = ArrayList<Observable<Boolean>>()
                when(b.configType){
                    UnitConfigType.CONFIG_TYPE_FLOOR_BOTTOM.value,
                    UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL.value,
                    UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value->{
                        //只删除room detail
                        var corridorName = Util.formatNoZeroNum(b.floorNum)+"层"+Util.formatNoZeroNum(b.corridorNum)+"楼梯间"
                        var platformNum = Util.formatNoZeroNum(b.platformNum)?.toIntOrNull()?:0
                        var platName = Util.formatNoZeroNum(b.floorNum)+"层" +"${platformNum}-${(platformNum+1)}"+"休息平台"
                        b.corridorConfig?.split(",")
                            ?.filter { s-> !(bean.corridorConfig?.split(",")?.contains(s)?:false)}
                            ?.forEach { pos->
                                obList.add(mDb.deleteRoomDetailByPath(configId!!,"楼梯间",pos))

                            }
                        LOG.I("123","${b.platformConfig}  new=${bean.platformConfig}")
                        b.platformConfig?.split(",")
                            ?.filter { s->!(bean.platformConfig?.split(",")?.contains(s)?:false) }
                            ?.forEach { pos->
                                LOG.I("123","deleteRoomDetailByPath  pos =$pos  configId=${configId}")
                                obList.add(  mDb.deleteRoomDetailByPath(configId!!,"休息平台",pos))

                            }
                    }
                    UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM.value,
                    UnitConfigType.CONFIG_TYPE_UNIT_NORMAL.value->{
                        b.config1?.split(",")
                            ?.filter { s-> !(bean.config1?.split(",")?.contains(s)?:false) }
                            ?.forEach { name->
                                obList.add(mDb.deleteRoomStatusByConfig(configId!!,name))
                                obList.add(mDb.deleteRoomDetailByConfig(configId!!,name))
                            }
                    }
                    UnitConfigType.CONFIG_TYPE_UNIT_TOP.value->{
                        b.config1?.split(",")
                            ?.filter { s-> !(bean.config1?.split(",")?.contains(s)?:false) }
                            ?.map { name-> "一层$name" }
                            ?.forEach { name->
                                obList.add(mDb.deleteRoomStatusByConfig(configId!!,name))
                                obList.add(mDb.deleteRoomDetailByConfig(configId!!,name))
                            }
                        b.config2?.split(",")
                            ?.filter { s-> !(bean.config2?.split(",")?.contains(s)?:false) }
                            ?.map { name-> "二层$name" }
                            ?.forEach { name->
                                obList.add(mDb.deleteRoomStatusByConfig(configId!!,name))
                                obList.add(mDb.deleteRoomDetailByConfig(configId!!,name))
                            }
                    }
                }
                if (obList.isEmpty()){
                    obList.add(Observable.create { o->o.onNext(true) })
                }
                Observable.zip(obList){ o->
                    o.size == obList.size
                }
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                mDb.updateConfig(bean)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                updateUnitProjectStatus(projectId!!,unitId!!)
                updateHouseStatusWithConfigChange(if (bean.configId?:-1>0) bean.configId!! else it,bean)
            },{
                it.printStackTrace()
            })
    }

    private fun updateUnitProjectStatus(projectId:Long,unitId:Long){
        addDisposable(mDb.getUnitOnce(unitId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                if (it.isEmpty())throw MsgThrowable("error unitId $unitId")
                var localBean = it[0]
                if (localBean.status==0) throw MsgThrowable("no need update unit status")
                localBean.status = 0
                mDb.updateUnit(localBean)
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                mDb.getProjectOnce(projectId).toObservable()
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                if (it.isEmpty()) throw MsgThrowable("error projectId $projectId")
                var localProject = it[0]
                if (localProject.status == 0 &&
                    localProject.inspector?.contains(Config.sUserName) == true
                ) throw MsgThrowable("no need update project status")
                localProject.status = 0
                if (localProject.inspector.isNullOrEmpty()){
                    localProject.inspector = Config.sUserName
                }else{
                    if (!localProject.inspector!!.contains(Config.sUserName)){
                        localProject.inspector = localProject.inspector+"、${Config.sUserName}"
                    }
                }
                mDb.updateProject(localProject)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","update project $it")
            },{
                it.printStackTrace()
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

    override fun getContext(): Context = mView?.getContext()!!

    private fun checkHasConfig(desList: List<ProjectConfigBean>):Boolean{
        var l = desList.filter { it.configId>0 }
        if (l.isNotEmpty()){
            var msg = ""
            l.map { "${it.floorNum}${it.neighborNum}室" }.forEachIndexed { index, rStr ->
                var s = when(index){
                    0 -> rStr
                    1 -> "、${rStr}"
                    2 -> "等等"
                    else ->""
                }
                msg += s
            }
            msg += "已存在配置，无法复制,请重新选择"
            mView?.onCopyError(msg)
            return false
        }
        return true
    }

    override fun copyRoom(srcConfigId: Long, desList: List<ProjectConfigBean>) {

        var src :ConfigBean
        addDisposable(mDb.getConfigOnce(srcConfigId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                src = it[0]
                copyConfig(src,desList)
            },{
                it.printStackTrace()
            }))
    }

    override fun checkConfig(desList: List<ProjectConfigBean>, cb: (Boolean, String?) -> Unit) {
        LOG.I("123","desList=$desList")
        var l = desList.filter { it.configId>0 }
            .map {
                when(it.configType){
                    UnitConfigType.CONFIG_TYPE_UNIT_TOP.value,
                        UnitConfigType.CONFIG_TYPE_UNIT_NORMAL.value,
                        UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM.value -> "${it.floorNum}${it.neighborNum}室"
                    UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value,
                        UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL.value,
                        UnitConfigType.CONFIG_TYPE_FLOOR_BOTTOM.value -> "${it.floorNum}层"
                    else ->""
                }
              }
        if (l.isNotEmpty()){
            var msg = ""
            l.forEachIndexed { i, s ->
                msg += when(i){
                    0-> s
                    1 -> "、$s"
                    2 -> "等"
                    else -> ""
                }
            }
            msg += "已有配置是否覆盖？"
            cb(false,msg)
        }else{
            cb(true,null)
        }
    }
    override fun copyFloor(
        src: ProjectConfigBean,
        desList: List<Pair<ProjectConfigBean, List<ProjectConfigBean>>>
    ) {
        LOG.I("123","copyFloor  desList=$desList")

        addDisposable(mDb.getConfigOnce(src.unitId,src.floorIdx)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                var floor = it.firstOrNull { b->
                    b.configType == UnitConfigType.CONFIG_TYPE_FLOOR_BOTTOM.value ||
                            (b.configType == UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL.value) ||
                            (b.configType == UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value)
                }
                var floorList = desList.map { b->b.first }.toList()
                if (floor!=null && !floorList.isNullOrEmpty()){
                    copyConfig(floor,floorList)
                }

                var roomList = desList.map { b->b.second }.flatten()
                it.filter { b->
                    b.configType == UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM.value ||
                            (b.configType == UnitConfigType.CONFIG_TYPE_UNIT_NORMAL.value) ||
                            (b.configType == UnitConfigType.CONFIG_TYPE_UNIT_TOP.value)
                }.forEach { b->
                    var l = roomList.filter { r->r.neighborIdx == b.neighborIdx }
                    copyConfig(b,l)
                }

            },{
                it.printStackTrace()
            }))
    }


    private fun copyConfig(src :ConfigBean, desList: List<ProjectConfigBean>){
        addDisposable(Observable.fromIterable(desList)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                var des = src.copy().also { b->
                    if (it.configId > 0){
                        b.configId = it.configId
                    }else{
                        b.configId = -1
                    }

                    b.floorIdx = it.floorIdx
                    b.neighborIdx = it.neighborIdx
                    b.floorNum = it.floorNum
                    b.neighborNum = it.neighborNum
                    b.configType = it.configType
                    b.updateTime = null
                    if (!b.corridorNum.isNullOrEmpty()){
                        b.corridorNum = it.floorNum
                    }
                    if (!b.platformNum.isNullOrEmpty()){
                        b.platformNum = it.floorNum
                    }


                    if (it.configType == UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value){
                        b.platformConfig = null
                        b.platformNum = null
                    }
                }
                if (it.floorIdx>0){ //复制到高层 去掉庭院
                    des.config1 = des.config1?.split(",")
                        ?.filter { d-> !d.contains("庭院") }
                        ?.joinToString(separator = ",")

                }
                mDb.updateConfig(des)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","copyConfig it=$it")
            },{
                it.printStackTrace()
            }))
    }

    override fun copyUnit(oldUnitId: Long, newUnitId: Long) {
        addDisposable(mDb.getUnitConfigOnce(oldUnitId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                Observable.fromIterable(it)
            }
            .flatMap {
                var des = it.copy().also { b->
                    b.unitId = newUnitId
                    b.configId = null
                    b.createTime = null
                    b.updateTime = null
                }
                mDb.updateConfig(des)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","copy create new config  id=$it")
            },{
                it.printStackTrace()
            })

        )
    }


    override fun bindView(v: IBaseView) {
        mView = v as IProjectContrast.IConfigView

    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}