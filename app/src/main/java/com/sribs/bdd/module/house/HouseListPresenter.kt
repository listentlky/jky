package com.sribs.bdd.module.house

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libnet.http.bean.ListBean
import com.cbj.sdk.libnet.http.bean.ResultBean
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.Config
import com.sribs.bdd.bean.HouseConfigItemBean
import com.sribs.bdd.bean.RoomItemBean
import com.sribs.bdd.bean.UnitConfigType
import com.sribs.bdd.bean.data.*
import com.sribs.common.bean.db.ConfigBean
import com.sribs.common.bean.net.PartListReq
import com.sribs.common.bean.net.PartListRes
import com.sribs.common.module.BasePresenter
import com.sribs.common.net.HttpApi
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.TimeUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat

/**
 * @date 2021/7/29
 * @author elijah
 * @Description
 */
class HouseListPresenter:BasePresenter(),IHouseContrast.IHouseListPresenter {
    private var mView:IHouseContrast.IHouseView?=null

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService
    }



    override fun getHouseList(projectId: Long, unitId: Long?, unitNo: String, remoteUnitId: String?) {
//        houseListI++
//        LOG.E("123","getHouseList $houseListI")
        var localList : ArrayList<RoomItemBean>?=null
        var remote:ResultBean<ListBean<PartListRes>>?=null
        getRemotePart(remoteUnitId)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .flatMap {
                remote = if (it.code == 404) null else it
                getLocalConfig(projectId, unitId, unitNo)
            }
            .observeOn(Schedulers.computation())
            .flatMap {
//                localConfigUpdateI++
//                LOG.E("123","localConfigUpdateI  $localConfigUpdateI")
                localList = ArrayList(it)
                if (Config.isNetAvailable && !remoteUnitId.isNullOrEmpty()){
                    getRemotePart(remoteUnitId,localList?: ArrayList(),remote)
                }else{
                    Observable.create { o->
                        o.onNext(localList!!)
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mView?.onLocalConfig(ArrayList(it))
            },{
                mView?.onLocalConfig(localList?: ArrayList())
                mView?.onMsg(checkError(it))
            })
    }

    private fun getRemotePart(remoteUnitId: String?):Observable<ResultBean<ListBean<PartListRes>>>{
        var obList = ArrayList<Observable<ResultBean<ListBean<PartListRes>>>>()
        if (remoteUnitId.isNullOrEmpty() || !Config.isNetAvailable){
            obList.add(Observable.create { o->o.onNext(ResultBean(404,"",null)) })
        }else{
            obList.add(HttpManager.instance.getHttpService<HttpApi>()
                .getPartList(PartListReq(remoteUnitId)))
        }
        return Observable.merge(obList)
    }

    private fun getRemotePart(remoteUnitId: String,localList:ArrayList<RoomItemBean>,remote:ResultBean<ListBean<PartListRes>>?):Observable<List<RoomItemBean>>{
        var sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
//        i++
//        LOG.E("123","getRemotePart i=$i")

        var obList = ArrayList<Observable<ResultBean<ListBean<PartListRes>>>>()
        if (remote==null){
            obList.add(HttpManager.instance.getHttpService<HttpApi>()
                .getPartList(PartListReq(remoteUnitId)))
        }else{
            obList.add(Observable.create { o->
                o.onNext(remote)
            })
        }
        return Observable.merge(obList)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .flatMap {
                checkResult(it)
                var allList = ArrayList<RoomItemBean>()
                var remoteList = it.data!!.records.filter { remote->
                    var b = localList.find { local->
                        var neighborNum = if(local.houseType==2 && local.floorNeighborNum.startsWith("0")){
                            local.floorNeighborNum.substring(1)
                        }else{
                            local.floorNeighborNum
                        }
                        neighborNum+local.floorNeighborNumEx == remote.partNo }
//                    LOG.I("123","getRemotePart b=$b")
                    b==null
                } .map { b->

                    var floorStr = Array<String>(2) { "" }
                    var houseType = when(b.partType){
                        "corridor"-> 0
                        "platform"->1
                        "room"-> 2
                        else-> 1
                    }
                    if (b.partNo.contains("层")){
                        var arr = b.partNo.split("层")
                        floorStr[0] = arr[0]+"层"
                        floorStr[1] = arr[1]
//                        houseType = if(arr[1].contains("楼梯间")) 0 else 1
                    } else {
                        floorStr[0] = b.partNo
//                        houseType = 2
                    }

                    RoomItemBean(
                        localId = -1,
                        remoteId = "${b.unitId}-${b.partNo}",
                        updateTime = b.updateTime,
                        status = 2,
                        unitNum = b.unitNo,
                        floorNeighborNum =floorStr[0],
                        floorNeighborNumEx = floorStr[1],
                        inspector = b.inspectorName,
                    ).also { bean->
                        bean.isFinish = b.isCompleted==1
                        bean.houseType = houseType
                        bean.houseFinishStatus = when(b.isCompleted){
                            0->"未完成"
                            1->"已完成"
                            2->"进行中"
                            else->"未完成"
                        }
                    }
                }
                allList.addAll(localList)
                if (remoteList.isNotEmpty()) allList.addAll(remoteList)
                Observable.create<List<RoomItemBean>> { o->
                    o.onNext(allList)
                }
            }
    }

    private fun getLocalConfig(projectId: Long, unitId: Long?, unitNo: String):Observable<List<RoomItemBean>> {
        if (unitId?:-1<=0){
            return Observable.create { it.onNext(emptyList()) }
        }

        var sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
        return mDb.getUnitConfig(unitId!!).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                var l = it.sortedWith(compareBy ({b->b.unitId},{b->b.floorIdx},{b->b.neighborIdx})).map { b->
//                    LOG.I("123","$b")
//                    var unitNo = unitList?.firstOrNull { ub->ub.unitId == b.unitId }?.unitNo?.toString()?:""
//                    var unitNo = String.format("%02d",unitNo)
                    var floorNeighborStr = if(b.neighborIdx==null){ //公共区域
                        if(!b.floorNum.isNullOrEmpty()) "${b.floorNum}层" else "${String.format("%02d",b.floorIdx!!+1)}层"
                    }else{
                        var str = if(!b.floorNum.isNullOrEmpty()) "${b.floorNum}${b.neighborNum}室"
                        else "${b.floorIdx!!+1}${String.format("%02d",(b.neighborIdx!!+1))}室"
                        if (str.startsWith("0")) str = str.substring(1) //去0
                        str
                    }
                    RoomItemBean(
                        projectId,
                        "",
                        TimeUtil.date2Time(b.updateTime)?:"",
                        0,
                        unitNo,
                        floorNeighborStr?:"",
                        "",
                        ""
                    ).also { item->
                        item.dataBean = when(b.configType){
                            UnitConfigType.CONFIG_TYPE_FLOOR_BOTTOM.value-> ProjectConfigBottomFloorDataBean().also { pdb->
                                pdb.configId = b.configId!!
                                pdb.floorNum = b.floorNum?:""
                                pdb.corridorNum = b.corridorNum?:""
                                pdb.corridorConfig = b.corridorConfig?:""
                                pdb.platformNum = b.platformNum?:""
                                pdb.platformConfig = b.platformConfig?:""
                            }
                            UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL.value-> ProjectConfigNormalFloorDataBean().also { pdb->
                                pdb.configId = b.configId!!
                                pdb.floorNum = b.floorNum?:""
                                pdb.corridorNum = b.corridorNum?:""
                                pdb.corridorConfig = b.corridorConfig?:""
                                pdb.platformNum = b.platformNum?:""
                                pdb.platformConfig = b.platformConfig?:""
                            }
                            UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value-> ProjectConfigTopFloorDataBean().also { pdb->
                                pdb.configId = b.configId!!
                                pdb.floorNum = b.floorNum?:""
                                pdb.corridorNum = b.corridorNum?:""
                                pdb.corridorConfig = b.corridorConfig?:""
                            }
                            UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM.value-> ProjectConfigBottomNeighborDataBean().also { pdb->
                                pdb.configId = b.configId!!
                                pdb.floorNum = b.floorNum?:""
                                pdb.neighborConfig = b.config1?:""
                            }
                            UnitConfigType.CONFIG_TYPE_UNIT_NORMAL.value-> ProjectConfigNormalNeighborDataBean().also { pdb->
                                pdb.configId = b.configId!!
                                pdb.floorNum = b.floorNum?:""
                                pdb.neighborConfig = b.config1?:""
                            }
                            UnitConfigType.CONFIG_TYPE_UNIT_TOP.value-> ProjectConfigTopNeighborDataBean().also { pdb->
                                pdb.configId = b.configId!!
                                pdb.floorNum = b.floorNum?:""
                                pdb.neighborType = if(b.unitType==0) "非复式" else "复式"
                            }
                            else->null
                        }

                        item.configBean = ProjectConfigBean(b.configType?:0).also { config->
                            config.projectId = b.projectId?:-1
                            config.unitId = b.unitId?:-1
                            config.configId = b.configId?:-1
                            config.floorIdx = b.floorIdx?:-1
                            config.neighborIdx = b.neighborIdx?:-1
                            config.floorNum = b.floorNum
                            config.neighborNum = b.neighborNum
                        }

                    }
                }
                var list = ArrayList(l)
                var i = 0
                while (i<list.size){
                    var b = list[i]
                    var itemData = b.dataBean
                    when(itemData){
                        is ProjectConfigBottomFloorDataBean -> {
                            if(itemData.hasCorridor() && itemData.hasPlatform()){
                                var nextB = list[i].copy()
                                list[i].floorNeighborNumEx = "${itemData.corridorNum}楼梯间"
                                list[i].houseType = 0
                                var n = itemData.platformNum.toIntOrNull()
                                nextB.floorNeighborNumEx = if(n!=null) "${n}-${n+1}休息平台" else "休息平台"
                                nextB.houseType = 1
                                i++
                                list.add(i,nextB)

                            }else if (itemData.hasCorridor()){
                                list[i].floorNeighborNumEx = "${itemData.corridorNum}楼梯间"
                                list[i].houseType = 0
                            }else if(itemData.hasPlatform()){
                                var n = itemData.platformNum.toIntOrNull()
                                list[i].floorNeighborNumEx = if(n!=null) "${n}-${n+1}休息平台" else "休息平台"
                                list[i].houseType = 1
                            }
                        }
                        is ProjectConfigNormalFloorDataBean -> {
                            if(itemData.hasCorridor() && itemData.hasPlatform()){
                                var nextB = list[i].copy()
                                list[i].floorNeighborNumEx = "${itemData.corridorNum}楼梯间"
                                var n = itemData.platformNum.toIntOrNull()
                                nextB.floorNeighborNumEx = if(n!=null) "${n}-${n+1}休息平台" else "休息平台"
                                i++
                                list.add(i,nextB)

                            }else if (itemData.hasCorridor()){
                                list[i].floorNeighborNumEx = "${itemData.corridorNum}楼梯间"
                                list[i].houseType = 0
                            }else if(itemData.hasPlatform()){
                                var n = itemData.platformNum.toIntOrNull()
                                list[i].floorNeighborNumEx = if(n!=null) "${n}-${n+1}休息平台" else "休息平台"
                                list[i].houseType = 1
                            }
                        }
                        is ProjectConfigTopFloorDataBean -> {
                            if (itemData.hasCorridor()){
                                list[i].floorNeighborNumEx = "${itemData.corridorNum}楼梯间"
                                list[i].houseType = 0
                            }
                        }
                        else -> {
                            list[i].houseType = 2
                        }
                    }
                    i++
                }
                Observable.create { o->
                    o.onNext(list)
                }
//                mView?.onLocalConfig(list)
            }

    }

    override fun getLocalConfig(configId: Long, houseType: Int) {
        addDisposable(mDb.getConfig(configId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isEmpty())return@subscribe
                var config = it[0]
                var list = ArrayList<HouseConfigItemBean>()
                when(houseType){
                    0-> config.corridorConfig?.split(",")?.forEach { s->
                        list.add(newHouseListItemBean(s,config))
                    }
                    1-> config.platformConfig?.split(",")?.forEach { s->
                        list.add(newHouseListItemBean(s,config))
                    }
                    2-> if (config.unitType?:0==0){
                        if(!config.config1.isNullOrEmpty()){
                            config.config1!!.split(",").forEach { s->
                                list.add(newHouseListItemBean(s,config))
                            }
                        }
                    }else{
                        if(!config.config1.isNullOrEmpty()){
                            config.config1!!.split(",").forEach { s->
                                var str = "一层$s"
                                list.add(newHouseListItemBean(str,config))
                            }
                        }
                        if(!config.config2.isNullOrEmpty()){
                            config.config2!!.split(",").forEach { s->
                                var str = "二层$s"
                                list.add(newHouseListItemBean(str,config))
                            }
                        }
                    }

                }
                mView?.onHouseConfig(list)
            },{
                mView?.onHouseConfig(ArrayList())
                mView?.onMsg(checkError(it))
            }))
    }



    private fun newHouseListItemBean(s:String, config:ConfigBean):HouseConfigItemBean
            = HouseConfigItemBean(s,false).also {
        it.projectId = config.projectId
        it.unitId = config.unitId
        it.configId = config.configId
    }




    override fun bindView(v: IBaseView) {
        mView = v as IHouseContrast.IHouseView
    }

    override fun unbindView() {
        dispose()
        mView=null
    }

    companion object {
//        var i =0
//        var houseListI=0
//        var localConfigUpdateI =0
    }
}