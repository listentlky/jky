package com.sribs.bdd.module.house

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.sribs.common.module.BasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.sribs.common.bean.db.ReportBean
import com.sribs.common.bean.db.RoomDetailBean
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.TimeUtil
import java.io.File

/**
 * @date 2021/8/3
 * @author elijah
 * @Description
 */
class ReportPresenter:BasePresenter(),IHouseContrast.IReportPresenter {
    private var mView:IHouseContrast.IReportView?=null

    private var mData = ReportBean()


    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService
    }

    override fun getRoomDetail(configId: Long) {
        var list = ArrayList<RoomDetailBean>()
        addDisposable(mDb.getHouseStatusByConfigOnce(configId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                if (it.isNotEmpty()){
                    var b = it[0]
                    var desList = ArrayList<String>()
                    if (!b.houseStatus.isNullOrEmpty()){
                        desList.add("${b.name}现状：${b.houseStatus}")
                    }
                    if (b.houseFurnishTime!=null){
                        desList.add("装修时间：${TimeUtil.date2YMD(b.houseFurnishTime)}")
                    }
                    list.add(RoomDetailBean().also { d->
                        d.name = b.name
                        d.description = if (desList.isNotEmpty()) desList.joinToString(separator = "。")
                        else "${b.name}现状：无"
                    })
                }
                mDb.getRoomStatusOnce(configId).toObservable()
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                it.forEach { b->
                    var desList = ArrayList<String>()
                    if (!b.roomStatus.isNullOrEmpty()){
                        desList.add("${b.name}现状：${b.roomStatus}")
                    }
                    if (b.roomFurnishTime!=null){
                        desList.add("装修时间：${TimeUtil.date2YMD(b.roomFurnishTime)}")
                    }
                    if (!b.roomNote.isNullOrEmpty()){
                        desList.add("实际用途：${b.roomNote}")
                    }
                    list.add(RoomDetailBean().also { d->
                        d.name = b.name
                        d.description = if (desList.isNotEmpty()) desList.joinToString(separator = "。")
                        else "${b.name}现状：无"
                    })
                }
                mDb.getRoomDetailOnce(configId).toObservable()
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                if (it.isNotEmpty()) list.addAll(ArrayList(it))
                Observable.create<ArrayList<RoomDetailBean>> { o->
                    o.onNext(list)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mView?.onRoomDetail(it)
            },{
                it.printStackTrace()
            })
        )
    }

    override fun getReport(configId: Long) {
        addDisposable(mDb.getReport(configId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isNotEmpty()){
                    mData = it[0]
                }
                mView?.onReport(ArrayList(it))
            },{
                mView?.onReport(ArrayList())
                it.printStackTrace()
            })
        )
    }

    override fun updateReport(
        projectId: Long,
        unitId: Long,
        configId: Long,
        isSave: Int?,
        report: String?,
        signPath: String?
    ) {
        if (isSave==null && report==null && signPath==null)return
        var isChange = false
        if (isSave!=null && isSave != mData.isSave)isChange = true
        if (report!=null && report != mData.report)isChange = true
        if (signPath!=null && signPath != mData.signPath)isChange = true
        if (!isChange)return
        mData.also {
           it.projectId = projectId
           it.unitId = unitId
           it.configId = configId
           if (isSave?:-1>=0){
               it.isSave = isSave
           }
           if (!report.isNullOrEmpty()){
               it.report = report
           }
           if (signPath!=null){
               if (signPath == "" && !it.signPath.isNullOrEmpty()){
                   // delete file
                   deleteSignFile(it.signPath!!)
               }
               it.signPath = signPath
           }
           it.updateTime = null
       }
        addDisposable(mDb.updateReport(mData)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","updateReport  id=$it")
            },{
                it.printStackTrace()
            }))

    }
    private fun deleteSignFile(path:String){
        var f = File(path)
        if (!f.exists())return
        f.delete()
    }

    override fun bindView(v: IBaseView) {
        mView = v as IHouseContrast.IReportView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}