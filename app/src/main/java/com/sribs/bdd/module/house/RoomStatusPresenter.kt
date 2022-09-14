package com.sribs.bdd.module.house

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.exception.MsgThrowable
import com.cbj.sdk.libbase.utils.LOG
import com.sribs.common.module.BasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.sribs.bdd.bean.UnitConfigType
import com.sribs.common.bean.db.RoomStatusBean
import com.sribs.common.server.IDatabaseService
import java.io.File
import java.sql.Date
import java.text.SimpleDateFormat

/**
 * @date 2021/7/30
 * @author elijah
 * @Description
 */
class RoomStatusPresenter:BasePresenter(),IHouseContrast.IRoomPresenter {
    private var mView:IHouseContrast.IRoomView?=null

    private var mData = RoomStatusBean()

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService
    }

    override fun getRoomStatus(configId: Long) {
        addDisposable(mDb.getRoomStatus(configId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","getRoomStatus by config $configId  it=$it")
                mView?.onRoomStatus(ArrayList(it))
            },{
                it.printStackTrace()
            }))
    }

    override fun getRoomStatus(configId: Long, name: String) {
        addDisposable(mDb.getRoomStatus(configId,name)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","getRoomStatus by    configId=$configId  name=$name  it=$it ")
                if (it.isNotEmpty()){
                    mData = it[0]
                }
                mView?.onRoomStatus(ArrayList(it))
            },{
                it.printStackTrace()
            }))
    }
    private fun getData(configId: Long,name: String):Observable<List<RoomStatusBean>>
     = mDb.getRoomStatusOnce(configId,name).toObservable()


    override fun updateRoomStatus(
        projectId: Long,
        unitId: Long,
        configId: Long,
        name: String,
        status: String,
        furnishTime: String?,
        note: String?,
        cb: () -> Unit?
    ) {
        addDisposable(getData(configId, name)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                mData = it.find { b->b.name == name }?:
                RoomStatusBean(
                    projectId = projectId,
                    unitId = unitId,
                    configId = configId,
                    name = name
                )
                var d =  try {
                    var date =
                        if (furnishTime?.length=="yyyy-MM-dd".length){
                            SimpleDateFormat("yyyy-MM-dd").parse(furnishTime)
                        } else if (furnishTime?.length=="yyyy-MM".length){
                            SimpleDateFormat("yyyy-MM").parse(furnishTime)
                        } else {
                            throw Exception("日期格式错误")
                        }
                   Date(date.time)
                }catch (e:Exception){
                    e.printStackTrace()
                    null
                }



                if (mData.roomStatus?:"" == status?:"" && mData.roomFurnishTime == d && mData.roomNote?:"" == note?:""){
                    throw MsgThrowable("no need update")
                }

                mData.also { r->
                    r.roomStatus = status
                    r.roomFurnishTime = d
                    if (!note.isNullOrEmpty()){
                        r.roomNote = note
                    }
                    r.updateTime = null
                    if (status.contains("不让进") || status.contains("无法入内") || status.contains("未发现明显损伤")){
                        r.isFinish = 1
                    }else{
                        r.isFinish = 2
                    }
                }
                mDb.updateRoomStatus(mData)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","updateRoomStatus  id=$it")
            },{
                it.printStackTrace()
            })
        )
    }

    override fun finishRoomStatus(
        projectId: Long,
        unitId: Long,
        configId: Long,
        name: String,
        isFinish: Int?
    ) {
        mData.also {
            it.projectId = projectId
            it.unitId = unitId
            it.configId = configId
            it.name = name
            it.isFinish = isFinish
        }
        addDisposable(mDb.updateRoomStatus(mData)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            },{
                it.printStackTrace()
            }))
    }

    override fun allFinish(projectId: Long, unitId: Long, configId: Long) {
        var nameList =  ArrayList<String>()
        addDisposable(mDb.getConfigOnce(configId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                if (it.isEmpty())throw MsgThrowable("错误配置")
                var configBean = it[0]
                if (configBean.configType != UnitConfigType.CONFIG_TYPE_UNIT_TOP.value) {
                    nameList.addAll(configBean.config1?.split(",") ?: emptyList())
                }else if(configBean.unitType == 0){
                    nameList.addAll(configBean.config1?.split(",") ?: emptyList())
                }else{
                    nameList.addAll(configBean.config1?.split(",")?.map { c->"一层$c" }?: emptyList())
                    nameList.addAll(configBean.config2?.split(",")?.map { c->"二层$c" }?: emptyList())
                }
                mDb.getRoomStatusOnce(configId).toObservable()
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                var obList = ArrayList<Observable<Long>>()
                it.forEach { b->
                    b.isFinish = 1
                    obList.add(mDb.updateRoomStatus(b))
                }
                nameList.filter { name-> it.find { b->b.name==name }==null }
                    .forEach { name->
                        var b = RoomStatusBean(
                            projectId = projectId,
                            unitId = unitId,
                            configId = configId,
                            name = name,
                            isFinish = 1,
                        )
                        obList.add(mDb.updateRoomStatus(b))
                    }
                if (obList.isEmpty()){
                    obList.add(Observable.create { o->o.onNext(-1) })
                }
                Observable.merge(obList)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","更新房间状态 id=$it")
            },{
                it.printStackTrace()
            })
        )
    }

    override fun clearRoom(configId: Long, name: String) {
        LOG.I("123","clearRoom $configId  $name")
        mDb.getRoomStatusOnce(configId,name).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                var need = when {
                    it.isEmpty() -> false
                    it[0].isFinish !=1 -> false
                    else -> true
                }
                if (need){
                    var b = it[0]
                    b.isFinish = 0
                    LOG.I("123","updateHouse Status ${b.configId} clear room")
                    mDb.updateRoomStatus(b)
                }else{
                    Observable.create {o->
                        o.onNext(-1)
                    }
                }
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                mDb.getRoomDetailOnce(configId,name).toObservable()
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                LOG.I("123","room detial $it")
                if (it.isEmpty())throw MsgThrowable("没有房间详情，无需清空")
                it.forEach { b->
                    if (!b.picPath.isNullOrEmpty()){
                        var f = File(b.picPath)
                        if (f.exists() && f.isFile){
                            f.delete()
                        }
                    }
                }
                mDb.deleteRoomDetail(it.map { b->b.roomDetailId!! })
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","clear room detail $it")
            },{
                it.printStackTrace()
            })

    }


    override fun bindView(v: IBaseView) {
        mView = v as IHouseContrast.IRoomView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}
