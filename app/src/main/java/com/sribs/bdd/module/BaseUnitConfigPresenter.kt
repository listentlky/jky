package com.sribs.bdd.module

import android.content.Context
import android.graphics.BitmapFactory
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libnet.http.HttpManager
import com.sribs.common.module.BasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.sribs.bdd.action.Dict
import com.sribs.bdd.bean.UnitConfigType
import com.sribs.bdd.bean.data.DamageDescriptionDataBean
import com.sribs.common.bean.db.*
import com.sribs.common.bean.net.*
import com.sribs.common.net.HttpApi
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.FileUtil
import com.sribs.common.utils.TimeUtil
import com.sribs.common.utils.Util
import java.sql.Date

/**
 * @date 2021/8/18
 * @author elijah
 * @Description
 */
abstract class BaseUnitConfigPresenter :BasePresenter(){
    protected val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    protected fun updateConfig(projectId: Long, bldId: Long, unitId: Long, configFloors:List<ConfigFloor>, createDate: Date?, updateDate: Date?): Observable<Boolean> {
        return mDb.getUnitConfigOnce(unitId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                var obList = ArrayList<Observable<Long>>()
                var remoteConfigList = ArrayList<ConfigBean>()
                //step 1  remoteConfig 转 本地类型
                configFloors.forEach { remoteFloor->
                    if (!remoteFloor.floorNo.isNullOrEmpty()
                        &&
                        (!remoteFloor.configCorridor?.corridorNo.isNullOrEmpty() || !remoteFloor.configCorridor?.corridorConfigs.isNullOrEmpty()
                                || !remoteFloor.configPlatform?.platformNo.isNullOrEmpty() || !remoteFloor.configPlatform?.platformConfigs.isNullOrEmpty()
                                )
                    ) {
                        remoteConfigList.add(
                            ConfigBean(
                                projectId = projectId,
                                bldId = bldId,
                                unitId = unitId,
                                configId = -1,
                                floorIdx = remoteFloor.floorIdx,
                                configType = when (remoteFloor.floorType) {
                                    "bottom" -> UnitConfigType.CONFIG_TYPE_FLOOR_BOTTOM.value
                                    "top" -> UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value
                                    "standard"-> UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL.value
                                    else-> UnitConfigType.CONFIG_TYPE_ERROR.value
                                },
                                floorNum = remoteFloor.floorNo,
                                createTime = createDate,
                                updateTime = updateDate
                            ).also { b->
                                b.corridorNum = remoteFloor.configCorridor?.corridorNo
                                b.corridorConfig =
                                    remoteFloor.configCorridor?.corridorConfigs?.joinToString(separator = ",") {
                                            c -> c.name
                                    }
                                b.platformNum = remoteFloor.configPlatform?.platformNo
                                b.platformConfig =
                                    remoteFloor.configPlatform?.platformConfigs?.joinToString(separator = ","){
                                            c-> c.name
                                    }
                            })
                    }

                    remoteFloor.configRooms?.forEach { remoteRoom->
                        if (!remoteRoom.roomConfigs.isNullOrEmpty() || !remoteRoom.duplexRoomConfigs.isNullOrEmpty() ){
                            remoteConfigList.add(
                                ConfigBean(
                                    projectId = projectId,
                                    bldId = bldId,
                                    unitId = unitId,
                                    configId = -1,
                                    floorIdx = remoteFloor.floorIdx,
                                    neighborIdx = remoteRoom.roomIdx,
                                    configType = when (remoteFloor.floorType) {
                                        "bottom" -> UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM.value
                                        "top" -> UnitConfigType.CONFIG_TYPE_UNIT_TOP.value
                                        "standard"-> UnitConfigType.CONFIG_TYPE_UNIT_NORMAL.value
                                        else-> UnitConfigType.CONFIG_TYPE_ERROR.value
                                    },
                                    floorNum = remoteFloor.floorNo,
                                    neighborNum = remoteRoom.roomNo,
                                    createTime = createDate,
                                    updateTime = updateDate,
                                ).also { b->
                                    b.config1 = remoteRoom.roomConfigs?.joinToString(separator = ","){
                                            c-> c.name
                                    }
                                    if (remoteFloor.floorType=="top" && remoteRoom.roomType == "duplex"){
                                        b.unitType = 1
                                        b.config2 = remoteRoom.duplexRoomConfigs?.joinToString(separator = ",") { c ->
                                            c.name
                                        }
                                    }
                                })
                        }
                    }
                }
                //step 2 与本地相同的赋值configId
                remoteConfigList.map { remoteC->
                    it.find { localC-> localC.floorIdx == remoteC.floorIdx &&
                            localC.neighborIdx == remoteC.neighborIdx }.also { c->
                        if (c!=null){
                            remoteC.configId = c.configId
                        }
                    }
                    remoteC
                }.forEach { c-> //update
                    obList.add(updateConfigAndHouseStatus(c))
                }
                //delete
                obList.add(
                    deleteConfigAndHouse( it.filter { localC->
                        remoteConfigList.find { remoteC-> remoteC.floorIdx==localC.floorIdx &&
                                remoteC.neighborIdx == localC.neighborIdx}==null
                    }.map { c->
                        c.configId!!
                    }))
//                LOG.I("123","updateConfig obList size=${obList.size}")
//                Observable.merge(obList)
                defaultOb(obList)
                Observable.zip(obList) { args ->
//                    LOG.I("123","updateConfig  args=$args")
                    args.size == obList.size
                }
            }
    }

    abstract fun ConfigBean(
        projectId: Long,
        bldId:Long,
        unitId: Long,
        configId: Long,
        floorIdx: Int?,
        configType: Int?,
        floorNum: String,
        createTime: Date?,
        updateTime: Date?
    ): ConfigBean

    private fun deleteConfigAndHouse(configIds:List<Long>):Observable<Long>{
        return mDb.deleteConfig(configIds)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                var obList = ArrayList<Observable<Boolean>>()
                configIds.forEach { id->
                    obList.add(mDb.deleteHouseStatusByConfig(id))
                    obList.add(mDb.deleteRoomStatusByConfig(id))
                    obList.add(mDb.deleteRoomDetailByConfig(id))
                    obList.add(mDb.deleteReportByConfig(id))
                }
//                LOG.I("123","del obListsize=${obList.size}")
                if(obList.isEmpty()){
                    obList.add(Observable.create { o->
                        o.onNext(true)
                    })
                }
                Observable.zip(obList){ arr->
//                    LOG.I("123","del arr size=${arr.size}")
                    arr.size.toLong()
                }
            }
    }

    private fun updateConfigAndHouseStatus(c:ConfigBean):Observable<Long>{
        return mDb.updateConfig(c)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .map {
                if (c.configId?:-1>0) c.configId else it
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                c.configId = it
                mDb.getHouseStatusByConfigOnce(it).toObservable()
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                var name = createPartNo(c)
                var name1 = if (!c.corridorNum.isNullOrEmpty()){
                    name+"${c.corridorNum}楼梯间"
                } else {
                    name
                }
                var name2 = if(!c.platformNum.isNullOrEmpty()){
                    name+createPlatform(c.platformNum)
                }else{
                    null
                }
                var nameList = ArrayList<String>()
                nameList.add(name1)
                if (name2!=null)nameList.add(name2)
                var obList = ArrayList<Observable<Long>>()
                nameList.forEach { name->
                    var localHouseStatusBean = it.find { b->b.name == name }?:
                    HouseStatusBean(
                        projectId = c.projectId,
                        unitId = c.unitId,
                        configId = c.configId,
                        name = name,
                        houseType = when{
                            name.contains("室")->2
                            name.contains("楼梯间")->0
                            name.contains("平台")->1
                            else ->null
                        }
                    )
                    localHouseStatusBean.status = 4
                    LOG.I("123","updateHouse Status ${localHouseStatusBean?.configId} updateConfigAndHouseStatus")
                    obList.add(mDb.updateHouseStatus(localHouseStatusBean))

                }
//                LOG.I("123","obList size=${obList.size}")
                defaultOb(obList)
                Observable.zip(obList){ arr->
//                    LOG.I("123","arrSize=${arr.size}")
                    arr.size.toLong()
                }
            }
    }


    protected fun updateUnitAndConfig(projectId: Long,bldId: Long, unitId:Long?,b: ConfigUnit):Observable<Long>{
        var createDate =  TimeUtil.time2Date(b.createTime?:"")
        var updateDate =  TimeUtil.time2Date(b.updateTime?:"")
        var localUnitId=-1L
        return mDb.updateUnit(
            UnitBean(
                projectId,
                bldId,
                unitId,
                b.unitNo,
                b.floorCount,
                b.roomCount,
                if(b.staircaseType=="double") 1 else 0,
                createDate,
                updateDate,
                b.unitId,
                b.version)
        )
            .subscribeOn(Schedulers.single())
            .map {
                LOG.I("123","updateUnitAndConfig  updateConfig finish id=$it  ${b.unitNo}")
                if (unitId?:-1>0) unitId!! else it
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                LOG.I("123","updateUnitAndConfig  updateConfig id=$it  ${b.unitNo}")
                localUnitId = it
                updateConfig(projectId,bldId,it,b.configFloors,createDate,updateDate)
            }
            .map {
//                LOG.I("123","updateConfig finish $it")
                if (it){
                    localUnitId
                }else{
                    -1L
                }
            }
    }

    protected fun deleteConfig(unitId:Long):Observable<Boolean>{
        return mDb.deleteConfigByUnitId(unitId)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                mDb.deleteHouseStatusByUnit(unitId)
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                mDb.deleteRoomStatusByUnit(unitId)
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                mDb.deleteRoomDetailByUnit(unitId)
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                mDb.deleteReportByUnit(unitId)
            }
    }

    protected fun checkUnitConfig(projectId:Long, bldId:Long, configUnits:List<ConfigUnit>):Observable<Boolean>{
        return mDb.getAllUnitOnce(projectId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                //远程数据插入或修改
                var obList = ArrayList<Observable<Boolean>>()
                configUnits.forEach { u->
                    var b = it.find { localUnit -> localUnit.unitNo.toString() == u.unitNo  }
                    LOG.I("123","updateUnitAndConfig add2 obList  ${u.unitNo}")
                    obList.add(updateUnitAndConfig(projectId,bldId, b?.unitId,u).map { localUnitId->
                        localUnitId>0
                    })
                }
                //本地多余数据删除
                var l = it.filter { localUnit->
                    configUnits.find { remoteUnit-> localUnit.unitNo.toString() == remoteUnit.unitNo  } == null
                }.map { localUnit-> localUnit.unitId!! }
                l.forEach { uId->
                    obList.add(deleteConfig(uId))
                }
                if (l.isNotEmpty()){
                    obList.add(mDb.deleteUnit(l).map { true })
                }
                if(obList.isEmpty()){
                    obList.add(Observable.create { o->
                        o.onNext(true)
                    })
                }



                LOG.I("123","updateUnitAndConfig  checkUnit size=${obList.size}")
                Observable.zip(obList) { args ->
                    LOG.I("123","updateConfig  args=$args")
                    args.size == obList.size
                }



            }

    }
    private fun createPartNo(b:ConfigBean):String = when(b.configType) {
        UnitConfigType.CONFIG_TYPE_FLOOR_BOTTOM.value -> Util.formatNoZeroNum(b.floorNum) + "层"
        UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL.value -> Util.formatNoZeroNum(b.floorNum) + "层"
        UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value -> Util.formatNoZeroNum(b.floorNum) + "层"
        UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM.value -> Util.formatNoZeroNum(b.floorNum) + b.neighborNum + "室"
        UnitConfigType.CONFIG_TYPE_UNIT_NORMAL.value -> Util.formatNoZeroNum(b.floorNum) + b.neighborNum + "室"
        UnitConfigType.CONFIG_TYPE_UNIT_TOP.value -> Util.formatNoZeroNum(b.floorNum) + b.neighborNum + "室"
        else -> ""
    }


    private fun createPlatform(platformNumStr:String?):String{
        if (platformNumStr==null) return "休息平台"
        var platformStr = Util.formatNoZeroNum(platformNumStr)
        var platFormNum = platformStr?.toIntOrNull()?:return "休息平台"
        return "${platFormNum}-${platFormNum+1}休息平台"
    }


    private fun createHouseType(partType:String):Int = when(partType){
        "corridor"-> 0
        "platform"->1
        "room"->2
        else->2
    }



    protected fun checkConfigRecord(projectId: Long,unitId:Long, remoteParts: List<Parts>,cT:String,uT:String)
            :Observable<Long>{
        return mDb.getUnitConfigOnce(unitId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                var ct = TimeUtil.time2Date(cT)
                var ut = TimeUtil.time2Date(uT)
                var obList = ArrayList<Observable<Long>>()
                remoteParts.forEach { r->
                    var localConfigBean = it.find { local->
                        var localPartNo = createPartNo(local)
                        if (r.partNo.contains("室")){
                            r.partNo == localPartNo
                        } else {
                            var remotePartNo = r.partNo.substringBefore("层")+"层"
                            remotePartNo == localPartNo
                        }

//                        r.partNo.contains(createPartNo(local))
                    }
                    if (localConfigBean!=null){
                        var configId = localConfigBean.configId


                        if (r.partType == "room"){

                            // 更新report
                            obList.add(updateHouseReport(projectId,unitId,configId!!,r,ct,ut))
                            // 更新 room status
                            obList.add(updateRoomStatus(projectId,unitId,configId!!,r.damageDescription.records,ct,ut))
                        }
                        //更新houseStatus
                        obList.add(updateHouseStatus(projectId,unitId,configId!!,r,ct,ut))

                        // 更新 room detail
                        obList.add(updateRoomDetail(projectId,unitId,configId!!,r.damageDescription.records,ct,ut))


                    } else { //error 有记录缺没有配置
                        LOG.E("123","error 有记录没有配置 ${r.partNo}")

                    }
                }

                defaultOb(obList)
                Observable.zip(obList){ arr->
                    arr.size.toLong()
                }
            }
    }

    protected fun updateHouseStatusWithConfigChange(configId: Long,b:ConfigBean){
        var finishedMap = HashMap<Long,List<String>>()
        var doingMap = HashMap<Long,List<String>>()
        addDisposable(mDb.getRoomStatusOnce(configId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {

                when(b.configType){
                    UnitConfigType.CONFIG_TYPE_FLOOR_BOTTOM.value,UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL.value,
                    UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value->{
                        var corridorConfigSize = b.corridorConfig?.split(",")?.size?:0
                        var platformConfigSize = b.platformConfig?.split(",")?.size?:0
                        var corridorName = Util.formatNoZeroNum(b.floorNum)+"层"+Util.formatNoZeroNum(b.corridorNum)+"楼梯间"
                        var platformNum = Util.formatNoZeroNum(b.platformNum)?.toIntOrNull()?:0
                        var platName = Util.formatNoZeroNum(b.floorNum)+"层" +"${platformNum}-${(platformNum+1)}"+"休息平台"
//                        doingMap[configId] = listOf(corridorName,platName)
                        //TODO  nothing to do 记录本身没有房间层级 配置与记录无关

                    }
                    UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM.value,UnitConfigType.CONFIG_TYPE_UNIT_NORMAL.value->{
                        var str =  b.config1?.split(",")
                        var roomSize = str?.size?:0
                        var roomName = Util.formatNoZeroNum(b.floorNum)+b.neighborNum+"室"
                        var roomStatusSize = it.size
                        var isNotDefault = it.find { s->s.isFinish?:0 !=0  }!=null
                        if (roomSize>roomStatusSize && isNotDefault){ //新增
                            doingMap[configId] = listOf(roomName)
                        }
                        LOG.I("123","roomSize=$roomSize  roomStatusSize")
                        if (roomSize<=roomStatusSize && isNotDefault){ //减少了
//                            minusMap[configId] = listOf(roomName)
                            var isAllFinish = it.filter { s-> str?.contains(s.name?:"")?:false }
                                .find { s->s.isFinish!=1 } == null
                            if (isAllFinish) finishedMap[configId]= listOf(roomName)
                        }
                    }
                    UnitConfigType.CONFIG_TYPE_UNIT_TOP.value->{
                        var str1 = b.config1?.split(",")
                        var str2 = b.config2?.split(",")
                        var roomSize = (str1?.size?:0) + (str2?.size?:0)
                        var roomName = Util.formatNoZeroNum(b.floorNum)+b.neighborNum+"室"
                        var roomStatusSize = it.size
                        var isNotDefault = it.find { s->s.isFinish?:0 !=0  }!=null
                        if (roomSize>roomStatusSize && isNotDefault){ //新增
                            doingMap[configId] = listOf(roomName)
                        }
                        if (roomSize<=roomStatusSize && isNotDefault){ //减少了
//                            minusMap[configId] = listOf(roomName)
                            var str1Name = str1?.map { s->"一层$s" }
                            var str2Name = str2?.map { s->"二层$s" }
                            var strList = ArrayList<String>()
                            if (str1Name?.size?:0>0) strList.addAll(ArrayList(str1Name))
                            if (str2Name?.size?:0>0) strList.addAll(ArrayList(str2Name))
                            var isAllFinish = it.filter { s-> strList.contains(s.name?:"") }
                                .find { s->s.isFinish!=1 } == null
                            if (isAllFinish) finishedMap[configId]= listOf(roomName)
                        }
                    }
                }
                mDb.getHouseStatusByConfigOnce(configId).toObservable()
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                var obList = ArrayList<Observable<Long>>()
                if (doingMap[configId]?.size?:0>0){
                    var pList = doingMap[configId]!!
                    pList.forEach { name->
                        var b = it.find { s->s.name == name }
                        if (b!=null && b.isFinish?:0==1){
                            b.isFinish = 2
                            LOG.I("123","updateHouse Status ${b.configId} updateHouseStatusWithConfigChange ")
                            obList.add( mDb.updateHouseStatus(b))
                        }
                    }
                }
                if (finishedMap[configId]?.size?:0>0){
                    var pList = finishedMap[configId]!!
                    pList.forEach { name->
                        var b = it.find { s->s.name == name }
                        if (b!=null && b.isFinish?:0==2){
                            b.isFinish = 1
                            obList.add( mDb.updateHouseStatus(b))
                        }
                    }
                }
                if (obList.isEmpty()){
                    obList.add(Observable.create { o->o.onNext(-1) })
                }
                Observable.merge(obList)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","update house status it=$it")
            },{
                it.printStackTrace()
            })
        )
    }

    protected fun updateHouseStatus(projectId: Long,unitId: Long,configId:Long,b:Parts,ct:Date?,ut:Date?):Observable<Long>{
        return mDb.getHouseStatusByConfigOnce(configId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                var localHouse = when {
                    it.size > 1 -> it.find { house->house.name == b.partNo  }?:
                    HouseStatusBean(
                        projectId = projectId, unitId = unitId, configId = configId,
                        name = b.partNo,
                        houseType = createHouseType(b.partType)//always 2  只有room 才有houseSuatus
                    )
                    it.size==1 -> it[0]
                    else ->
                        HouseStatusBean(
                            projectId = projectId, unitId = unitId, configId = configId,
                            name = b.partNo,
                            houseType = createHouseType(b.partType)//always 2  只有room 才有houseSuatus
                        )

                }
                localHouse.status = 3
                //状态
                if (b.partNow!=null){
                    localHouse.houseStatus = b.partNow!!.status
                    if (!b.partNow!!.newlyDate.isNullOrEmpty()){
                        localHouse.houseFurnishTime = TimeUtil.time2Date(b.partNow!!.newlyDate!!)
                    }
                }
                //检测员
                localHouse.inspector = Dict.getInspectorNames(b.inspectorId)
                //是否完成
                localHouse.isFinish = b.isCompleted
                //版本号
                localHouse.version = b.version
                //时间
                localHouse.createTime = ct
                localHouse.updateTime = ut
                LOG.I("123","updateHouse Status ${localHouse.configId} updateHouseStatus ${b.isCompleted}  inspectorId=${b.inspectorId} inspectorName=${localHouse.inspector}")
                mDb.updateHouseStatus(localHouse)
            }
    }

    protected fun updateHouseReport(projectId: Long,unitId: Long,configId: Long,b:Parts,ct:Date?,ut:Date?):Observable<Long>{
        var signatureResPath = ""
        return downloadPic(b.damageReport.signatureResUrl)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                signatureResPath = it
                mDb.getReportOnce(configId).toObservable()
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                var localReport = if (it.isNotEmpty()) it[0] else
                    ReportBean(
                        projectId = projectId,
                        unitId = unitId,
                        configId = configId
                    )
                localReport.report = b.damageReport.feedback
                localReport.signPath = signatureResPath
                localReport.signResId = b.damageReport.signatureResId
                localReport.signResUrl = b.damageReport.signatureResUrl
                localReport.createTime = ct
                localReport.updateTime = ut
                mDb.updateReport(localReport)
            }
    }

    protected fun downloadPic( url:String?):Observable<String>{
        if (url.isNullOrEmpty())return Observable.create { it.onNext("") }
//        var t = HttpManager.instance.mUploadClient?.readTimeoutMillis()
//        LOG.E("123","t=$t")
        return HttpManager.instance.getUploadService<HttpApi>()
            .download(url)
            .subscribeOn(Schedulers.trampoline())
            .observeOn(Schedulers.trampoline())
            .flatMap {

                var b = it.bytes()
                var bitmap = BitmapFactory.decodeByteArray(b,0,b.size)
                var f = FileUtil.getTempPhotoFile(getContext())
                FileUtil.saveTempPhoto(getContext(),f,bitmap)

                Observable.create { o->
//                    o.onNext("")
                    o.onNext(f!!.absolutePath)
                }
            }
    }

    private fun updateRoomStatus(projectId: Long,unitId: Long,configId: Long,roomId:Long?,remote:DamageDescription,ct:Date?,ut:Date?)
            :Observable<Long>{
        var b = RoomStatusBean(
            projectId=projectId,
            unitId=unitId,
            configId=configId,
            roomId=roomId,
            name=remote.roomName,
            isFinish = remote.isCompleted,
            createTime = ct,
            updateTime = ut
        ).also {
            if (remote.roomNow!=null){
                it.roomStatus = remote.roomNow!!.status
                if (!remote.roomNow!!.newlyDate.isNullOrEmpty()){
                    it.roomFurnishTime = TimeUtil.time2Date(remote.roomNow!!.newlyDate!!)
                }
                if (!remote.roomNow!!.actualUse.isNullOrEmpty()){
                    it.roomNote = remote.roomNow!!.actualUse
                }

            }
        }
        return mDb.updateRoomStatus(b)
    }

    protected fun updateRoomStatus(projectId: Long,unitId: Long,configId: Long,l:List<DamageDescription>,ct:Date?,ut:Date?)
            :Observable<Long>
            = mDb.getRoomStatusOnce(configId).toObservable()
        .subscribeOn(Schedulers.computation())
        .observeOn(Schedulers.computation())
        .flatMap {
            var obList=ArrayList<Observable<Long>>()
            l.forEach { remote->
                var b = it.find { local->local.name == remote.roomName }
                obList.add(updateRoomStatus(projectId,unitId,configId,b?.roomId,remote,ct,ut))
            }
            var delList = it.filter { local->
                l.find { remote->remote.roomName == local.name } == null
            }.map { local->local.roomId!! }

            obList.add(mDb.deleteRoomStatusById(delList).map { res->res.toLong() })

            defaultOb(obList)
            Observable.zip(obList){ arr->
                arr.size.toLong()
            }
        }

    private fun updateRoomDetail(projectId: Long,unitId: Long,configId: Long,detailId:Long?,
                                 name:String,d:Description,ct:Date?,ut:Date?):Observable<Long>{
        return downloadPic(d.photoResUrl)
//            .subscribeOn(Schedulers.io()) //todo 2021/12/3 by cbj 去掉，否则下载任务在io线程顺序进行
            .observeOn(Schedulers.computation())
            .flatMap {
                var picPath = it
                var b = RoomDetailBean(
                    projectId=projectId,
                    unitId=unitId,
                    configId=configId,
                    roomDetailId = detailId,
                    createTime = ct,
                    updateTime = ut
                ).also { r->
                    r.name = name
                    r.damagePath = d.position
                    r.damageIdx = d.graph.toIntOrNull()
                    r.description = d.detail
                    if (!d.text.isNullOrEmpty()){
                        var d = DamageDescriptionDataBean().parseJsonStr(d.text) as DamageDescriptionDataBean
                        r.splitNum = d.splitNum
                        r.splitWidth =d.splitWidth
                        r.splitLen =d.splitLen
                        r.splitType = d.splitType.toIntOrNull()
                        r.seamNum = d.seamNum
                    }
                    r.picId = d.photoResId
                    r.picUrl = d.photoResUrl
                    r.picPath = picPath
                }
                mDb.updateRoomDetail(b)
            }
    }

    protected fun updateRoomDetail(projectId: Long,unitId: Long,configId: Long,remoteList:List<DamageDescription>,ct:Date?,ut:Date?)
            :Observable<Long>{
        var l = ArrayList<Pair<String,Description>>()
        remoteList.forEach {
            var name = it.roomName
            it.description.forEach { b->
                l.add(Pair(name,b))
            }
        }

        return mDb.getRoomDetailOnce(configId).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                var obList = ArrayList<Observable<Long>>()
                l.forEach { remote->
                    var b = it.find { local ->remote.first == local.name && remote.second.position == local.damagePath }
                    obList.add(updateRoomDetail(projectId,unitId,configId,b?.roomDetailId,remote.first,remote.second,ct,ut))
                }
                var delList = it.filter { local ->
                    l.find { remote->remote.first == local.name && remote.second.position == local.damagePath} == null
                }.map { local->local.roomDetailId!! }
                obList.add(mDb.deleteRoomDetail(delList).map { d->
                    LOG.E("123","deleteRoomDetail=$d")
                    d.toLong() })
                defaultOb(obList)
                Observable.zip(obList){ arr->
                    arr.size.toLong()
                }
            }
    }


    abstract fun getContext():Context


    protected fun defaultOb(obList:ArrayList<Observable<Long>>){
        if (obList.isEmpty()){
            obList.add(Observable.create { o->
                o.onNext(-1)
            })
        }
    }

}