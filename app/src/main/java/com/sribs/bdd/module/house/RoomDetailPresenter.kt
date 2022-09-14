package com.sribs.bdd.module.house

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.exception.MsgThrowable
import com.cbj.sdk.libbase.utils.LOG
import com.sribs.common.module.BasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.sribs.common.bean.db.RoomDetailBean
import com.sribs.common.bean.db.RoomStatusBean
import com.sribs.common.server.IDatabaseService

/**
 * @date 2021/7/30
 * @author elijah
 * @Description
 */
class RoomDetailPresenter:BasePresenter(),IHouseContrast.IRoomDetailPresenter {
    private var mView:IHouseContrast.IRoomDetailView?=null

    private var mData = RoomDetailBean()

    override fun getRoomDetail(configId: Long, name: String) {
        val srv = ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService
        addDisposable(srv.getRoomDetail(configId,name)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mView?.onRoomDetail(ArrayList(it))
            },{
                mView?.onRoomDetail(ArrayList())
                it.printStackTrace()
            }))
    }

    override fun getRoomDetail(configId: Long, name: String, damagePath: String) {
        val srv = ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService
        addDisposable(srv.getRoomDetailOnce(configId, name, damagePath).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","getRoomDetail  $configId  $name  $damagePath    $it")
                mView?.onRoomDetail(ArrayList(it))
            },{
                mView?.onRoomDetail(ArrayList())
                it.printStackTrace()
            }))
    }

    override fun getRoomDetail(configId: Long, name: String, damagePath: String, damageIdx: Int) {
        val srv = ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService
        addDisposable(srv.getRoomDetailOnce(configId, name, damagePath, damageIdx).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mData = if (it.isNotEmpty()){
                    it[0]
                }else{
                    RoomDetailBean()
                }
                mView?.onRoomDetail(ArrayList(it))
            },{
                mData = RoomDetailBean()
                mView?.onRoomDetail(ArrayList())
                it.printStackTrace()
            }))
    }

    override fun getRoomDetail(configId: Long, name: String, damagePath: String, description: String) {
        val srv = ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService
        addDisposable(srv.getRoomDetailOnce(configId, name, damagePath, description).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mData = if (it.isNotEmpty()){
                    it[0]
                }else{
                    RoomDetailBean()
                }
                mView?.onRoomDetail(ArrayList(it))
            },{
                mData = RoomDetailBean()
                mView?.onRoomDetail(ArrayList())
                it.printStackTrace()
            }))
    }

    override fun updateRoomDetail(b: RoomDetailBean, cb: (Long) -> Unit) {
        val srv = ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService
        b.also {
            b.roomDetailId = mData.roomDetailId
            b.createTime = mData.createTime
        }
        addDisposable(srv.updateRoomDetail(b)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","updateRoomDetail id=$it")
                cb(it)
                roomFinish(b,2)
            },{
                it.printStackTrace()
            }))
    }

    private fun roomFinish(b: RoomDetailBean,n:Int){
        val srv = ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService

        srv.getRoomStatusOnce(b.configId!!,b.name!!).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                LOG.E("123","roomFinish 2  name  ${b.name}   $it")
                if (b.name == "休息平台" || b.name == "楼梯间"){
                    Observable.create { o->o.onNext(-1) }
                }else{
                    var status = if (it.isEmpty()){
                        RoomStatusBean(
                            projectId = b.projectId,
                            unitId = b.unitId,
                            configId = b.configId,
                            name = b.name,
                            isFinish = n,
                        )
                    }else{
                        it[0]
                    }
                    status.isFinish = n
                    LOG.I("123","updateHouse Status ${status?.configId} roomFinish")
                    srv.updateRoomStatus(status)
                }
            }
            .flatMap {
                srv.getHouseStatusByConfigOnce(b.configId!!).toObservable()
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                LOG.I("123","get house status it=$it")
                var status = when(it.size){
                    0-> throw MsgThrowable("没有房间状态")
                    1-> it[0]
                    else-> it.firstOrNull { houseStatusBean-> houseStatusBean.name?.contains(b.name?:"null")?:false  }?:
                    throw MsgThrowable("没有${b.name}房间状态")
                }
                status.isFinish = n

                srv.updateHouseStatus(status)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","update house status id =$it")
            },{
                mView?.onMsg(checkError(it))
            })


    }


    override fun deleteRoomDetail(cb: (Boolean) -> Unit) {
        if (mData.roomDetailId?:-1<0){
            return
        }
        val srv = ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService
        addDisposable(srv.deleteRoomDetailById(mData.roomDetailId!!)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","deleteRoomDetail  $it")
                cb(it)
            },{
                it.printStackTrace()
            }))
    }

    override fun deleteRoomDetail(id: Long,cb:(Boolean)->Unit) {
        val srv = ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService
        addDisposable(srv.deleteRoomDetailById(id)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","updateRoomDetail id=$it")
            },{
                it.printStackTrace()
            }))
    }

    override fun bindView(v: IBaseView) {
        mView = v as IHouseContrast.IRoomDetailView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}