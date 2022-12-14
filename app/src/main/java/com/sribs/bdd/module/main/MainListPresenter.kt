package com.sribs.bdd.module.main

import android.annotation.SuppressLint
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libnet.http.HttpManager
import com.sribs.common.module.BasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.sribs.bdd.Config
import com.sribs.bdd.R
import com.sribs.bdd.bean.MainProjectBean
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.DrawingBean
import com.sribs.common.bean.db.UnitBean
import com.sribs.common.net.HttpApi
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.TimeUtil
import kotlin.collections.ArrayList

/**
 * @date 2021/6/24
 * @author elijah
 * @Description
 */
class MainListPresenter:BasePresenter(),IMainListContrast.IPresenter {
    var mView:IMainListContrast.IView?=null

    private val mStateArr by lazy {
        mView?.getContext()?.resources?.getStringArray(R.array.main_project_status)?: emptyArray()
    }

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    /**
     * 本地获取项目列表
     */
    override fun getProjectList() {
        mDb.getAllProject()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                var list = ArrayList(it.map { b->MainProjectBean(
                    localId= b.id?:-1,
                    localUUID = b.uuid?:"",
                    remoteId= b.remoteId?:"",
                    status = b.status!!,
                    address = b.name+"",
                    leader = b.leader?:"",
                    inspector = b.inspector?:"",
                    parentVersion = b.parentVersion!!,
                    version = b.version!!,
                    isChanged = b.isChanged
                ).also { _b->
                    _b.updateTime = b.updateTime?:""
                    _b.createTime = b.createTime?:""
                    _b.name = b.name
                } })

                LogUtils.d("读取本地项目: "+list.toString())

                if(!Config.isNetAvailable){
                    LogUtils.d("无网络 直接展示本地数据: ")
//                    mView?.onProjectList(ArrayList(list.sortedWith(compareBy({b->b.name},{b->b.sortedBuildNo}))))
                    mView?.onProjectList(ArrayList(list.sortedByDescending { b->b.updateTime }))

                }else{
                    getProjectRemote(list)
                }


            },{
                mView?.onProjectList(ArrayList())
                it.printStackTrace()
            })

    }

    /**
     * 从网络获取项目列表
     */
    @SuppressLint("CheckResult")
    private fun getProjectRemote(localList:ArrayList<MainProjectBean>){
        LogUtils.d("有网络 请求网络数据: ")
        HttpManager.instance.getHttpService<HttpApi>()
            .getV3ProjectList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtils.d("请求网络数据: "+it.toString())
                checkResult(it)
                var l = ArrayList<MainProjectBean>()
                // 远程项目在本地中已有 remoteId相同，更新状态
                if(it.data == null){
                    mView?.onProjectList(ArrayList(localList.sortedByDescending { b->b.createTime }))
                    return@subscribe
                }
                it.data!!.forEach { remoteBean->
                    var i = localList.indexOfFirst { localBean->
                        !localBean.remoteId.isNullOrEmpty() && localBean.remoteId == remoteBean.projectId
                    }

                    if (i>=0) {
                        var localBean = localList[i]
                        //判断时间
                        if (TimeUtil.isBefore(localBean.createTime, TimeUtil.stampToDate(remoteBean.createTime))) {
                            localList[i].hasNewer = true
                        }
                        localBean.localUUID = remoteBean.projectId
                        localBean.version = remoteBean.version
                        localBean.remoteData = remoteBean
                    }
                }
                // 远程项目在本地中已有 本地remoteId为空  项目名称、楼号相同，更新状态
                it.data!!.forEach {  remoteBean->
                    var i = localList.indexOfFirst { localBean->
                        localBean.remoteId.isNullOrEmpty() &&
                                localBean.name == remoteBean.projectName
                    }
                    if (i>=0) {
                        var localBean = localList[i]
                        //判断时间
                        if (TimeUtil.isBefore(localBean.createTime , TimeUtil.stampToDate(remoteBean.createTime))) {
                            localList[i].hasNewer = true
                        }
                        localBean.remoteData = remoteBean
                        localBean.version = remoteBean.version
                        localBean.remoteId = remoteBean.projectId
                    }
                }

            /*    it.data!!.records.forEach {  remoteBean->
                    var i = localList.indexOfFirst { localBean->
                        localBean.localUUID == remoteBean.projectId
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
                var onlyRemoteList =  it.data!!.filter { remoteBean->
                    localList.find { localBean->
                        (!localBean.remoteId.isNullOrEmpty() && localBean.remoteId == remoteBean.projectId) ||
                                (localBean.name == remoteBean.projectName && localBean.remoteId.isNullOrEmpty())
                    }==null
                }?.map { b -> MainProjectBean(
                    localId = -1,
                    localUUID = b.projectId,
                    remoteId= b.projectId,
                    status = 2,
                    parentVersion = b.parentVersion,
                    version = b.version,
                    address = b.projectName,
                    leader = b.leaderName?:"",
                    inspector = b.inspectors?.joinToString(separator = "、")?:""
                ).also { _b->
                    _b.createTime = TimeUtil.stampToDate(b.createTime)
                    _b.name = b.projectName
                    _b.remoteData = b
                } }
                l.addAll(localList)
                l.addAll(onlyRemoteList!!)
//                mView?.onProjectList(ArrayList(l.sortedWith(compareBy({b->b.name},{b->b.sortedBuildNo})) ))
                mView?.onProjectList(ArrayList(l.sortedByDescending { b->b.createTime }))

            },{
                mView?.onMsg(checkError(it))
//                mView?.onProjectList(ArrayList(localList.sortedWith(compareBy({b->b.name},{b->b.sortedBuildNo}))))
                mView?.onProjectList(ArrayList(localList.sortedByDescending { b->b.createTime }))

            })
    }

    override fun getAllUnitsInProject(projectId: Long) {
        addDisposable( mDb.getAllUnit(projectId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mView?.onAllUnitsInProject(it.toCollection(ArrayList<UnitBean>()))
            },{
                it.printStackTrace()
            }))
    }

    override fun getAllDrawingInProject(projectId: Long) {
        addDisposable(mDb.getDrawingByProjectId(projectId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mView?.onAllDrawingsInProject(it.toCollection(ArrayList<DrawingBean>()))
            },{
                it.printStackTrace()
            }))
    }

    override fun bindView(v: IBaseView) {
        mView = v as IMainListContrast.IView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}