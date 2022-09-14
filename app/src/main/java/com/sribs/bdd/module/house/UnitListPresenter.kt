package com.sribs.bdd.module.house

import android.content.Context
import android.util.ArrayMap
import com.cbj.sdk.libbase.exception.MsgThrowable
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libnet.http.bean.ListBean
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.sribs.bdd.R
import com.sribs.bdd.action.Config
import com.sribs.bdd.action.Dict
import com.sribs.bdd.bean.UnitConfigType
import com.sribs.bdd.bean.UnitRecordTmpBean
import com.sribs.bdd.bean.UploadPicTmpBean
import com.sribs.bdd.bean.data.DamageDescriptionDataBean
import com.sribs.bdd.module.BaseUnitConfigPresenter
import com.sribs.bdd.utils.DescriptionPositionHelper
import com.sribs.common.bean.HistoryBean
import com.sribs.common.bean.db.*
import com.sribs.common.bean.net.*
import com.sribs.common.net.HttpApi
import com.sribs.common.utils.TimeUtil
import java.io.File
import java.sql.Date
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UnitListPresenter:BaseUnitConfigPresenter(),IUnitListContrast.IPresenter {

    private var mView:IUnitListContrast.IView?=null

    override fun getAllUnit(projectId: Long) {
        addDisposable(mDb.getAllUnit(projectId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (com.sribs.bdd.Config.isNetAvailable){
                    LOG.E("123","getAllUnit net")
                    getRemoteUnit(projectId,1,it)
                }else{
                    LOG.E("123","getAllUnit no net")
                    mView?.onAllUnit(it)
                }
            },{
                it.printStackTrace()
            }))
    }

    private fun updateUnitRemoteId(localList:List<UnitBean>,remoteList:List<UnitListRes>){
        var obList = ArrayList<Observable<Long>>()
        localList.forEach {
            var remoteId = remoteList.find { r->r.unitNo == it.unitNo.toString() }?.unitId?:""
            if (!remoteId.isNullOrEmpty()){
                it.remoteId = remoteId
                obList.add(mDb.updateUnit(it))
            }else{
                LOG.I("123","getRemoteUnit nofind  local no=${it.unitNo}  id=${it.unitId} remoteId=${it.remoteId}  remoteList=$remoteList")
            }
        }
        if (obList.isEmpty()){
            obList.add(Observable.create { o->o.onNext(-1) })
        }

        addDisposable(Observable.merge(obList)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","更新unit remoteId unitId=$it")
            },{
                it.printStackTrace()
            }))
    }

    private fun getRemoteUnit(projectId: Long, bldId:Long, localList:List<UnitBean>){
        LOG.I("123","getRemoteUnit  localList=$localList")
        addDisposable(mDb.getProjectOnce(projectId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.io())
            .flatMap {
                if (it.isEmpty())throw MsgThrowable("获取本地项目失败")
                var localProject = it[0]
                if (localProject.remoteId.isNullOrEmpty()) throw MsgThrowable("")
                HttpManager.instance.getHttpService<HttpApi>()
                    .getUnitList(UnitListReq(localProject.remoteId!!))
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                checkResult(it)
                var allList = ArrayList<UnitBean>()
                var l = it.data!!.records.distinctBy { b-> b.unitNo }
                var remoteMoreList =  l.filter { remote->
                    localList.find { local->local.remoteId == remote.unitId } == null
                }
                var needUpdateRemoteId = remoteMoreList.filter { remote ->
                    localList.find { local->local.unitNo.toString() == remote.unitNo }!=null
                }
                if (needUpdateRemoteId.isNotEmpty()) {
                    updateUnitRemoteId(localList, needUpdateRemoteId)
                }
                var remoteList = remoteMoreList.filter { remote ->
                    localList.find { local->local.unitNo.toString() == remote.unitNo }==null
                }.map { b->
                    UnitBean(
                        projectId = projectId,
                        bldId = bldId,
                        unitId = -1,
                        unitNo = b.unitNo,
                        createTime = TimeUtil.time2Date(b.createTime),
                        updateTime = TimeUtil.time2Date(b.updateTime),
                        remoteId = b.unitId,
                        version = b.version
                    )
                }
                allList.addAll(localList)
                if (remoteList.isNotEmpty()) allList.addAll(remoteList)
                Observable.create<List<UnitBean>> { o->
                    o.onNext(allList)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.E("123","onAllUnit getRemoteUnit has remote ${it.map { e->"no:${e.unitNo}  id:${e.unitId}  remoteId=${e.remoteId}" }}")
                mView?.onAllUnit(it)
            },{
                LOG.E("123","onAllUnit getRemoteUnit just local")
                mView?.onAllUnit(localList)
                mView?.onMsg(checkError(it))
            }))
    }


    private fun checkRemoteId(localProject:ProjectBean,localUnit:UnitBean):Observable<Triple<String,String,Int>>{
        return Observable.create<Pair<String?,String?>> {
            it.onNext(Pair(localProject.remoteId,localUnit.remoteId))
        }
            .observeOn(Schedulers.computation())
            .flatMap { ids->
                if (ids.first.isNullOrEmpty()){
                    HttpManager.instance.getHttpService<HttpApi>()
                        .updateProject(ProjectUpdateReq(
                            null,
                            localProject!!.name?:"",
                            Dict.getLeaderId(localProject!!.leader?:"")?:"",
                            localProject!!.buildNo?:""
                        ))
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .flatMap {
                            checkResult(it)
                            localProject!!.remoteId = it.data!!.projectId
                            mDb.updateProject(localProject!!)
                        }
                        .flatMap {
                            Observable.create<Pair<String,String?>> { o->
                                o.onNext(Pair(localProject.remoteId!!,localUnit.remoteId))
                            }
                        }
                }else{
                    Observable.create { o->
                        o.onNext(Pair(localProject.remoteId!!,localUnit.remoteId))
                    }
                }
            }
            .observeOn(Schedulers.computation())
            .flatMap { ids->
                if (ids.second.isNullOrEmpty()){
                    updateUnitConfigGetRemoteId(localUnit.unitId!!,false)
                        .subscribeOn(Schedulers.computation())
                        .flatMap {
                            localUnit.remoteId = it.first
                            localUnit.version = it.second
                            Observable.create { o->
                                o.onNext(Triple(localProject.remoteId!!,localUnit.remoteId!!,localUnit!!.version!!))
                            }
                        }
                }else{
                    Observable.create { o->
                        o.onNext(Triple(localProject.remoteId!!,localUnit.remoteId!!,localUnit!!.version?:1))
                    }
                }
            }
    }


    private fun updateUnitConfigGetRemoteId(unitId: Long, bCover: Boolean):Observable<Pair<String,Int>>{
        var localProject:ProjectBean?=null
        var localUnit:UnitBean?=null
        // step 0 获取 unit
        return mDb.getUnitOnce(unitId).toObservable()
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
                    TimeUtil.date2Time(localProject!!.updateTime!!)?:""
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
            }.flatMap {
                Observable.create { o->
                    o.onNext(Pair(localUnit!!.remoteId!!,localUnit!!.version!!))
                }
            }

    }


    private fun updatePic(unitId: Long):Observable<Boolean>{
        var ob1 = mDb.getRoomDetailByUnitOnce(unitId).toObservable().map {
                b-> b as List<BaseDbBean>
        }
        var ob2 = mDb.getReportByUnitOnce(unitId).toObservable().map {
                b-> b as List<BaseDbBean>
        }
        var allSize = 0
        var updatePicSize = 0
        return Observable.zip(ob1,ob2){ o1,o2->
            var l1:List<RoomDetailBean>? = o1 as List<RoomDetailBean>
            var l2:List<ReportBean>?= o2 as List<ReportBean>

            var needUploadList = ArrayList<BaseDbBean>()

            l1?.filter { b->
                !b.picPath.isNullOrEmpty() && b.picId.isNullOrEmpty()
            }?.forEach { b->
                needUploadList.add(b)
            }
            l2?.filter { b->
                !b.signPath.isNullOrEmpty() && b.signResId.isNullOrEmpty()
            }?.forEach { b->
                needUploadList.add(b)
            }

            var obList = ArrayList<Observable<UploadPicTmpBean>>()
            needUploadList.forEach { b->
                var path = when (b) {
                    is RoomDetailBean -> b.picPath
                    is ReportBean -> b.signPath
                    else -> ""
                }
                var fileName = path!!.substring(path.lastIndexOf("/")+1)
                var fileSuffix = path.substring(path.lastIndexOf(".")+1)
                var textBody = RequestBody.create(MediaType.parse("text/plain"),fileSuffix)
                var fileBody = RequestBody.create(MediaType.parse("image/*"), File(path))
                var filePart = MultipartBody.Part.createFormData("file",fileName,fileBody)
                obList.add(HttpManager.instance.getHttpService<HttpApi>()
                    .fileUpload(filePart,textBody)
                    .map { res->
                        UploadPicTmpBean(b,res)
                    }
                )

            }
            obList
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.io())
            .flatMap {
                allSize = it.size
                if (allSize==0){
                    Observable.create { o->
                        o.onNext(UploadPicTmpBean(null,null))
                    }
                }else{
                    Observable.merge(it)
                }
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                if (allSize==0){
                    Observable.create { o->
                        o.onNext(0L)
                    }
                }else{
                    checkResult(it.resBean!!)
                    if (it.dbBean is RoomDetailBean) {
                        var bean = it.dbBean as RoomDetailBean
                        bean.picId = it.resBean!!.data!!.resId
                        bean.picUrl = it.resBean!!.data!!.resUrl
                        mDb.updateRoomDetail(bean)
                    } else {
                        var bean = it.dbBean as ReportBean
                        bean.signResId = it.resBean!!.data!!.resId
                        bean.signResUrl = it.resBean!!.data!!.resUrl
                        mDb.updateReport(bean)
                    }
                }
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                updatePicSize++
                Observable.create<Boolean> {
                    it.onNext(updatePicSize>=allSize)
                }
            }.filter {
                it
            }
    }

    override fun uploadUnit(projectId: Long, unitId: Long, unitNo: String,willCover:Boolean) {
        if (projectId<0 || unitId<0){
            mView?.onMsg("未获取到本地数据库 projectId=$projectId  unitId=$unitId")
            return
        }
        var localProject:ProjectBean?=null
        var localUnit:UnitBean?=null
        var remoteProjectId:String?=null
        var remoteUnitId:String?=null
        //step 0 获取 remoteProjectId  remoteUnitId
        addDisposable(updatePic(unitId)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                var ob1 = mDb.getProjectOnce(projectId).toObservable()
                var ob2 = mDb.getUnitOnce(unitId).toObservable()
                Observable.zip(ob1,ob2){ o1,o2->
                    if (o1.size!=1 || o2.size!=1){
                        throw MsgThrowable("本地数据库错误 projectSize=${o1.size} projectId=$projectId  unitSize=${o2.size} unitId=$unitId")
                    }
                    localProject = o1[0]
                    localUnit = o2[0]
                }
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                checkRemoteId(localProject!!,localUnit!!)
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                remoteProjectId = it.first
                remoteUnitId = it.second
                localUnit!!.version = it.third
                if (remoteProjectId.isNullOrEmpty() ||
                    remoteUnitId.isNullOrEmpty()) throw MsgThrowable("请先上传单元配置")

                var ob1 = mDb.getHouseStatusByUnitOnce(localUnit!!.unitId!!).toObservable().map {
                        b-> b as List<BaseDbBean>
                }
                var ob2 = mDb.getRoomStatusByUnitOnce(localUnit!!.unitId!!).toObservable().map {
                        b-> b as List<BaseDbBean>
                }
                var ob3 = mDb.getRoomDetailByUnitOnce(localUnit!!.unitId!!).toObservable().map {
                        b-> b as List<BaseDbBean>
                }
                var ob4 = mDb.getReportByUnitOnce(localUnit!!.unitId!!).toObservable().map {
                        b-> b as List<BaseDbBean>
                }
                var obList = ArrayList<Observable<List<BaseDbBean>>>()
                obList.add(ob1)
                obList.add(ob2)
                obList.add(ob3)
                obList.add(ob4)
                Observable.zip(obList){ arr->
                    var hStatusList:List<HouseStatusBean>? = arr[0] as List<HouseStatusBean>
                    var rStatusList:List<RoomStatusBean>? = arr[1] as List<RoomStatusBean>
                    var rDetailList:List<RoomDetailBean>? = arr[2] as List<RoomDetailBean>
                    var reportList:List<ReportBean>?= arr[3] as List<ReportBean>

                    var housemap = HashMap<String, UnitRecordTmpBean>()
                    hStatusList?.forEach { b->
                        when (b.houseType) {
                            2 -> {
                                if (housemap[b.configId!!.toString()] == null) housemap[b.configId!!.toString()] =
                                    UnitRecordTmpBean()
                                housemap[b.configId!!.toString()]!!.houseStatus = b
                            }
                            0 -> {
                                if (housemap["${b.configId}楼梯间"] == null) housemap["${b.configId}楼梯间"] =
                                    UnitRecordTmpBean()
                                housemap["${b.configId}楼梯间"]!!.houseStatus = b
                            }
                            1 -> {
                                if (housemap["${b.configId}休息平台"] == null) housemap["${b.configId}休息平台"] =
                                    UnitRecordTmpBean()
                                housemap["${b.configId}休息平台"]!!.houseStatus = b
                            }
                        }
                    }
                    rStatusList?.forEach { b->
                        if (housemap[b.configId!!.toString()]==null)housemap[b.configId!!.toString()] = UnitRecordTmpBean()
                        housemap[b.configId!!.toString()]!!.roomStatusList.add(b)
                    }
                    rDetailList?.forEach { b->
                        when (b.name) {
                            "楼梯间" -> {
                                if (housemap["${b.configId}楼梯间"]==null)housemap["${b.configId}楼梯间"] = UnitRecordTmpBean()
                                housemap["${b.configId}楼梯间"]!!.roomDetailList.add(b)
                            }
                            "休息平台" -> {
                                if (housemap["${b.configId}休息平台"]==null)housemap["${b.configId}休息平台"] = UnitRecordTmpBean()
                                housemap["${b.configId}休息平台"]!!.roomDetailList.add(b)
                            }
                            else -> {
                                if (housemap[b.configId!!.toString()]==null)housemap[b.configId!!.toString()] = UnitRecordTmpBean()
                                housemap[b.configId!!.toString()]!!.roomDetailList.add(b)
                            }
                        }
                    }
                    reportList?.forEach { b->
                        if (housemap[b.configId!!.toString()]==null)housemap[b.configId!!.toString()] = UnitRecordTmpBean()
                        housemap[b.configId!!.toString()]!!.houseReport = b
                    }
                    var parts = ArrayList<Parts>()
                    housemap.forEach { (configId, b) ->

                        var damageDescriptionList = ArrayList<DamageDescription>()
                        var damageItemList = ArrayList<Item>()
                        var roomMap = b.getRoom()
                        roomMap.forEach { (name, rTmp) ->
                            var d = DamageDescription(
                                name,
                                if (rTmp.status==null)
                                    null
                                else
                                    PartNow(rTmp.status!!.roomStatus, TimeUtil.date2Time(rTmp.status!!.roomFurnishTime),rTmp.status!!.roomNote),
                                rTmp.detail?.map { roomDetailBean ->
                                    Description(
                                        roomDetailBean.damagePath?:"",
                                        roomDetailBean.description?:"",
                                        DamageDescriptionDataBean().also { d->
                                            d.seamNum = roomDetailBean.seamNum?:""
                                            d.splitLen = roomDetailBean.splitLen?:""
                                            d.splitNum = roomDetailBean.splitNum?:""
                                            d.splitType = if (roomDetailBean.splitType==null)"" else roomDetailBean.splitType.toString()
                                            d.splitWidth = roomDetailBean.splitWidth?:""
                                        }
                                            .toJsonStr(),
                                        roomDetailBean.damageIdx?.toString()?:"",
                                        roomDetailBean.picId?.toString()?:"",
                                        null
                                    )
                                }?: emptyList(),
                                rTmp.status?.isFinish?:0
                            )
                            damageDescriptionList.add(d)
                            //report item
                            var i = Item(
                                name,
                                rTmp.detail?.map { roomDetailBean ->
                                    Detail(roomDetailBean.description?:"",
                                        roomDetailBean.picId?.toString()?:"",
                                        null
                                    )
                                }?: emptyList()
                            )
                            damageItemList.add(i)
                        }
                        var damageDescription = ListBean(
                            damageDescriptionList.size,damageDescriptionList
                        )
                        var damageReport = DamageReport(
                            damageItemList,
                            b.houseReport?.report,
                            b.houseReport?.signResId.toString(),
                            null
                        )
                        var inspectorId = if(b.houseStatus?.inspector.isNullOrEmpty()){
                            Config.sUserId
                        }else{
                            var name = if(b.houseStatus?.inspector?.contains("、")==true){
                                b.houseStatus?.inspector?.substringAfterLast("、")
                            }else{
                                b.houseStatus?.inspector
                            }
                            Dict.getInspectorId(name?:"")
                        }

                        var p = Parts(
                            when(b.houseStatus?.houseType){
                                0-> "corridor"
                                1-> "platform"
                                2-> "room"
                                else->""
                            },
                            b.houseStatus?.name?:"",
                            PartNow(b.houseStatus?.houseStatus,
                                TimeUtil.date2Time(b.houseStatus?.houseFurnishTime)),
                            inspectorId?:"",
                            b.houseStatus?.isFinish?:0,
                            damageDescription,
                            damageReport,
                            b.houseStatus?.version?:1
                        )
                        if(!b.houseStatus?.name.isNullOrEmpty()){
                            parts.add(p)
                        }
                    }
                    UploadUnitRecordReq(
                        remoteProjectId!!,
                        remoteUnitId,
                        unitNo,
                        parts,
                        localUnit?.version?:1,
                        TimeUtil.date2Time(localUnit!!.updateTime!!)?:"",
                        if (willCover) 1 else 0
                    )
                }
            }
            .observeOn(Schedulers.io())
            .flatMap {
                it.parts.forEach { p->
                    p.damageDescription.records =
                    p.damageDescription.records.sortedWith(compareBy{s->
                        DescriptionPositionHelper.roomSortMap[s.roomName]?:100})
                }

                HttpManager.instance.getHttpService<HttpApi>()
                    .unitRecordUpload(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                checkResult(it)
                updateUnitHouseStatus(unitId)
                mView?.onMsg("上传单元记录成功")
                mView?.onUpdate(true)
            },{
                mView?.onMsg(checkError(it))
                mView?.onUpdate(false)
            })
        )
    }

    private fun updateUnitHouseStatus(unitId: Long){
        mDb.getHouseStatusByUnitOnce(unitId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                var obList = ArrayList<Observable<Long>>()
                it.forEach { b->

                    b.status = 1
                    LOG.I("123","updateHouse Status ${b.configId} updateUnitHouseStatus   b=$b")
                    obList.add(mDb.updateHouseStatus(b))
                }
                Observable.zip(obList){ arr->
                    arr.size
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","houseStatus update size=$it")
            },{
                it.printStackTrace()
            })

    }



    override fun unitGetConfigHistory(projectId: Long, unitId: Long?, unitNo: String, history:(Array<HistoryBean>, String)->Unit) {
        var obList = ArrayList<Observable<List<BaseDbBean>>>()
        var ob1 = mDb.getProjectOnce(projectId).toObservable().map { b->
            b as List<BaseDbBean>
        }
        obList.add(ob1)
        if (unitId?:-1>0){
            var ob2 = mDb.getUnitOnce(unitId!!).toObservable().map { b->
                b as List<BaseDbBean>
            }
            obList.add(ob2)
        }

        var unitUpdateTime = ""
        var remoteProjectId = ""
        addDisposable(Observable.zip(obList){ arr->
            LOG.I("123","arr=$arr")
            if (arr.isEmpty() ){
                throw MsgThrowable("没有本地文件")
            }
            var pList = arr[0] as List<ProjectBean>
            var localProject = pList[0]
            var uList = if(arr.size<2) null else (arr[1] as List<UnitBean>)
            var localUnit = uList?.get(0)
            unitUpdateTime = TimeUtil.date2Time(localUnit?.updateTime)?:""
            if (localProject.remoteId.isNullOrEmpty()){
                throw MsgThrowable(mView?.getContext()?.getString(R.string.error_no_remote)?:"")
            }
            remoteProjectId = localProject.remoteId!!
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.io())
            .flatMap {
                HttpManager.instance.getHttpService<HttpApi>()
                    .getConfigHistoryList(HistoryListReq(remoteProjectId,unitNo))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                checkResult(it)
                history(it.data?.records?.map { h->
                    HistoryBean(h.historyId,null,h.unitId,h.updateTime,h.userName)
                }?.toTypedArray()?: emptyArray(),unitUpdateTime)
            },{
                mView?.onMsg(checkError(it))
            })
        )
    }

    override fun unitDownloadConfig(
        historyUnitId: String,
        projectId: Long,
        bldId:Long,
        unitId: Long?,
        cb: (Long) -> Unit
    ) {

        addDisposable( HttpManager.instance.getHttpService<HttpApi>()
            .unitConfigDownload(UnitDownloadReq(historyUnitId))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .flatMap {
                checkResult(it)
                updateUnitAndConfig(projectId,bldId,unitId,it.data!! )
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","updateUnitAndConfig it=$it ")
                cb(it)
            },{
                mView?.onMsg(checkError(it))
                cb(-1)
            })
        )
    }

    override fun unitGetRecordHistory(
        projectId: Long,
        unitId: Long?,
        unitNo: String,
        history: (Array<HistoryBean>, String) -> Unit
    ) {
        var obList = ArrayList<Observable<List<BaseDbBean>>>()
        var ob1 = mDb.getProjectOnce(projectId).toObservable().map { b->
            b as List<BaseDbBean>
        }
        obList.add(ob1)
        if (unitId?:-1>0){
            var ob2 = mDb.getHouseStatusByUnitOnce(unitId!!).toObservable().map { b->
                b as List<BaseDbBean>
            }
            obList.add(ob2)
        }
        var houseUpdateTime = ""
        var remoteProjectId = ""

        Observable.zip(obList){ arr->
            if (arr.isEmpty() ){
                throw MsgThrowable("没有本地文件")
            }
            var pList = arr[0] as List<ProjectBean>
            var localProject = pList[0]
            if (localProject.remoteId.isNullOrEmpty()){
                throw MsgThrowable(mView?.getContext()?.getString(R.string.error_no_remote)?:"")
            }
            remoteProjectId = localProject.remoteId!!
            var hList = if(arr.size<2) null else (arr[1] as List<HouseStatusBean>)
            var updateTime = hList?.map { it.updateTime }?.maxByOrNull { it?.time?:0 }
            houseUpdateTime = TimeUtil.date2Time(updateTime)?:""

        }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                HttpManager.instance.getHttpService<HttpApi>()
                    .getRecordHistoryList(HistoryListReq(remoteProjectId,unitNo))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                checkResult(it)
                history(it.data?.records?.map { h->
                    HistoryBean(null,null,h.unitId,h.updateTime,h.userName)
                }?.toTypedArray()?: emptyArray(),houseUpdateTime)
            },{
                mView?.onMsg(checkError(it))
            })
    }

    override fun unitDownloadRecord(
        historyUnitId: String,
        projectId: Long,
        unitId: Long?,
        cb: (Long) -> Unit
    ) {
        unitDownloadConfig(historyUnitId, projectId, 1, unitId){
            if (it>=0) {
                downloadRecord(historyUnitId, projectId, it, cb)
            }else{
                cb(-1)
            }
        }
    }




    private fun downloadRecord(historyUnitId: String, projectId: Long,unitId:Long, cb: (Long) -> Unit){
        HttpManager.instance.getHttpService<HttpApi>()
            .unitRecordDownload(UnitDownloadReq(historyUnitId))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .flatMap {
                checkResult(it)
                checkConfigRecord(projectId,unitId,it.data!!.parts,it.data!!.createTime,it.data!!.updateTime)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","downloadRecord  update db count = $it")
                mView?.onMsg("下载成功")
                cb(it)
            },{
                mView?.onMsg(checkError(it))
                cb(-1)
            })
    }


    override fun bindView(v: IBaseView) {
        mView = v as IUnitListContrast.IView
    }

    override fun unbindView() {
        dispose()
        mView = null
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
}