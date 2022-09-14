package com.sribs.bdd.module.house

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.exception.MsgThrowable
import com.cbj.sdk.libbase.utils.LOG
import com.sribs.common.module.BasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.sribs.bdd.action.Config
import com.sribs.bdd.action.Dict
import com.sribs.common.bean.db.HouseStatusBean
import com.sribs.common.server.IDatabaseService
import java.sql.Date
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

/**
 * @date 2021/7/30
 * @author elijah
 * @Description
 */
class HouseStatusPresenter:BasePresenter(),IHouseContrast.IHouseStatusPresenter {

    private var mView:IHouseContrast.IHouseStatusView?=null

    private var mData = HouseStatusBean()

    private val mDb by lazy { ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService }

    override fun getAllHouseStatus(unitId: Long) {
        addDisposable(mDb.getHouseStatusByUnit(unitId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mView?.onHouseStatus(ArrayList(it))
            },{
                it.printStackTrace()
            }))

    }

    override fun getHouseStatus(configId: Long) {

        addDisposable(mDb.getHouseStatus(configId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.E("123","getHouseStatus  it=$it")
//                if (it.size>1) throw MsgThrowable("room 应该只有1个house status")
                if (it.size==1){
                    mData = it[0]
                }
                mView?.onHouseStatus(ArrayList(it))
            },{
                it.printStackTrace()
            })
        )
    }

    private fun getData(configId: Long,name:String):Observable<List<HouseStatusBean>>
    =   mDb.getHouseStatusByConfigOnce(configId).toObservable()
//            = if (mData.name!=name){ //原先与当前不是同一个 //可能在另一个实例中被更新了
//        mDb.getHouseStatusByConfigOnce(configId).toObservable()
//    } else {
//        Observable.create {
//            it.onNext(listOf(mData))
//        }
//    }

    override fun updateHouseStatus(
        projectId: Long,
        unitId: Long,
        configId: Long,
        name: String,
        houseType: Int,
        status: String,
        furnishTime: String?
    ) {
        addDisposable( getData(configId,name)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                mData = it.find { b->b.name == name }?:
                        HouseStatusBean(
                            projectId=projectId,
                            unitId=unitId,
                            configId=configId,
                            name = name,
                            houseType = houseType
                        )
                var d = if(furnishTime?.length?:0 == 0) {
                    null
                }else {
                    try {
                        var date = if(furnishTime?.length == "yyyy-MM-dd".length){
                            SimpleDateFormat("yyyy-MM-dd").parse(furnishTime)
                        } else if(furnishTime?.length == "yyyy-MM".length){
                            SimpleDateFormat("yyyy-MM").parse(furnishTime)
                        }  else{
                            throw Exception("日期格式错误:  $furnishTime")
                        }
                        Date(date.time)
                    }catch (e:Exception){
                        e.printStackTrace()
                        null
                    }
                }

                if(mData.houseStatus?:"" == status?:"" && mData.houseFurnishTime == d){
                    throw MsgThrowable("no need update")
                }

                mData.also { s->
                    s.houseStatus = status
                    s.houseFurnishTime = d

                    if (status.contains("无人") || status.contains("不让进")||status.contains("未发现明显损伤")){
                        s.isFinish = 1
                    }else{
                        s.isFinish = 2
                    }
                    if (!s.inspector.isNullOrEmpty()){
                        if (!s.inspector!!.contains(Config.sUserName)){
                            s.inspector = s.inspector+"、${Config.sUserName}"
                        }
                    }else{
                        s.inspector = Config.sUserName
                    }
                }
                LOG.I("123","updateHouse Status ${mData?.configId}  mData updateHouseStatus")
                mDb.updateHouseStatus(mData)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","updateHouseStatus  id=$it")
            },{
                it.printStackTrace()
            })
        )
    }

    override fun updateHouseFinish(
        projectId: Long,
        unitId: Long,
        configId: Long,
        name: String,
        houseType: Int,
        isFinish: Boolean
    ) {
        addDisposable( getData(configId, name)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                mData = it.find { b->b.name == name }?:
                        HouseStatusBean(
                            projectId=projectId,
                            unitId=unitId,
                            configId=configId,
                            name = name,
                            houseType = houseType
                        )
                if (mData.isFinish==1 && isFinish)throw MsgThrowable("no need update all rooms status have finished")
                if (mData.isFinish!=1 && !isFinish)throw MsgThrowable("no need update ${mData.isFinish}   set:${isFinish}")
                if (isFinish){
                    mData.isFinish = 1
                    mData.inspector = Dict.getInspectorName(Config.sUserId)
                }else if(mData.isFinish != 0){
                    mData.isFinish = 2
                    mData.inspector = Dict.getInspectorName(Config.sUserId)
                }
                LOG.I("123","updateHouse Status ${mData?.configId} mData updateHouseFinish")
                mDb.updateHouseStatus(mData)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","updateHouseStatus  id=$it")
            },{
                it.printStackTrace()
            })
        )
    }

    private fun updateProjectStatus(projectId: Long){
        mDb.getProjectOnce(projectId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                if (it.isEmpty())throw MsgThrowable("projectId error")
                var localProject = it[0]
                if (localProject.status == 0) throw MsgThrowable("no need update")
                localProject.status = 0
                if (!localProject.inspector.isNullOrEmpty()){
                    if (!localProject.inspector!!.contains(Config.sUserName)){
                        localProject.inspector = localProject.inspector+"、${Config.sUserName}"
                    }
                }else{
                    localProject.inspector = Config.sUserName
                }
                mDb.updateProject(localProject)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","update localProject status 0")
            },{
                it.printStackTrace()
            })

    }

    override fun updateHouseInspector(
        projectId: Long,
        unitId: Long,
        configId: Long,
        name: String,
        houseType: Int,
        isFinish:Boolean?
    ) {
        addDisposable( getData(configId, name)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                LOG.I("123","get houseStatus  it=$it       name=$name")
                mData = it.find { b->b.name == name }?:
                        HouseStatusBean(
                            projectId=projectId,
                            unitId=unitId,
                            configId=configId,
                            name = name,
                            houseType = houseType
                        )
                if (mData.inspector?.contains(Config.sUserName)==true &&
                    (mData.isFinish ==  (if(isFinish==true) 1 else 0) || isFinish==null) &&
                    mData.status == 0){
                    throw MsgThrowable("no need update")
                }

                mData.also { s->
                    if (!s.inspector.isNullOrEmpty()){
                        if (!s.inspector!!.contains(Config.sUserName)){
                            s.inspector = s.inspector+"、${Config.sUserName}"
                        }
                    }else{
                        s.inspector = Config.sUserName
                    }
                    if (isFinish!=null){
                        s.isFinish = if(isFinish) 1 else 0
                    }
                    s.status = 0
                }
                LOG.I("123","updateHouse Status ${mData?.configId} mData updateHouseInspector")
                mDb.updateHouseStatus(mData)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","updateHouseStatus  id=$it")
                updateProjectStatus(projectId)
            },{
                LOG.E("123","updateHouseInspector error")
                it.printStackTrace()
            }))
    }


    override fun bindView(v: IBaseView) {
        mView = v as IHouseContrast.IHouseStatusView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}