package com.sribs.common.server

import com.alibaba.android.arouter.facade.template.IProvider
import com.sribs.common.bean.db.*
import com.sribs.common.bean.db.v3.project.v3ProjectDbBean
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

/**
 * @date 2021/7/8
 * @author elijah
 * @Description
 */
interface IDatabaseService:IProvider {

    fun getAllProject():Flowable<List<ProjectBean>>

    fun getProject(id:Long):Flowable<List<ProjectBean>>

    fun getProjectOnce(id:Long):Single<List<ProjectBean>>

    fun getProjectOnce(name:String,buildNo:String):Single<List<ProjectBean>>

    fun getProjectOnce(name:String):Single<List<ProjectBean>>

    fun updateProject(b:ProjectBean):Observable<Long>

    fun deleteProject(b:ProjectBean):Observable<Int>

    /**
     * 3期项目start
     */

    fun getv3AllProject():Flowable<List<v3ProjectDbBean>>

    fun getv3Project(id:Long):Flowable<List<v3ProjectDbBean>>

    fun getv3ProjectOnce(name:String,buildNo:String):Flowable<List<v3ProjectDbBean>>

    fun getv3ProjectOnce(name:String):Flowable<List<v3ProjectDbBean>>

    fun updatev3Project(b:v3ProjectDbBean):Observable<Long>

    fun deletev3Project(b:v3ProjectDbBean):Observable<Int>

    /**
     * 3期项目end
     */



    fun getAllUnit(projectId: Long):Flowable<List<UnitBean>>

    fun getAllUnitOnce(projectId: Long):Single<List<UnitBean>>

    fun getUnit(unitId:Long):Flowable<List<UnitBean>>

    fun getUnitOnce(unitId:Long):Single<List<UnitBean>>

    fun getUnitOnce(remoteId:String):Single<List<UnitBean>>

    fun updateUnit(b:UnitBean):Observable<Long>

    fun deleteUnit(ids:List<Long>):Observable<Int>

    fun deleteUnit(projectId:Long):Observable<Boolean>

    fun updateConfig(b:ConfigBean):Observable<Long>

    fun getConfig(projectId:Long,unitId:Long):Flowable<List<ConfigBean>>

    fun getConfig(configId:Long):Flowable<List<ConfigBean>>

    fun getConfigOnce(configId:Long): Single<List<ConfigBean>>

    fun getConfigOnce(unitId: Long,floorIdx:Int): Single<List<ConfigBean>>

    fun getAllConfig(projectId:Long):Flowable<List<ConfigBean>>

    fun getAllConfigOnce(projectId: Long):Single<List<ConfigBean>>

    fun getUnitConfig(unitId:Long):Flowable<List<ConfigBean>>

    fun getUnitConfigOnce(unitId:Long):Single<List<ConfigBean>>

    fun deleteConfig(ids:List<Long>):Observable<Int>

    fun deleteConfig(projectId: Long):Observable<Boolean>

    fun deleteConfigByUnitId(unitId: Long):Observable<Boolean>

    fun getHouseStatusByUnit(unitId: Long):Flowable<List<HouseStatusBean>>

    fun getHouseStatusByUnitOnce(unitId: Long):Single<List<HouseStatusBean>>

    fun getHouseStatusByProjectOnce(projectId: Long):Single<List<HouseStatusBean>>

    fun getHouseStatus(configId: Long):Flowable<List<HouseStatusBean>>

    fun getHouseStatusByConfigOnce(configId:Long):Single<List<HouseStatusBean>>

    fun updateHouseStatus(b:HouseStatusBean):Observable<Long>

    fun deleteHouseStatusByProject(projectId: Long):Observable<Boolean>

    fun deleteHouseStatusByUnit(unitId: Long):Observable<Boolean>

    fun deleteHouseStatusByConfig(configId: Long):Observable<Boolean>


    fun getRoomStatus(configId: Long):Flowable<List<RoomStatusBean>>

    fun getRoomStatus(configId: Long,name:String):Flowable<List<RoomStatusBean>>

    fun getRoomStatusOnce(configId: Long,name:String):Single<List<RoomStatusBean>>

    fun getRoomStatusOnce(configId:Long):Single<List<RoomStatusBean>>

    fun getRoomStatusByUnitOnce(unitId: Long):Single<List<RoomStatusBean>>

    fun updateRoomStatus(b:RoomStatusBean):Observable<Long>

    fun deleteRoomStatusByProject(projectId: Long):Observable<Boolean>

    fun deleteRoomStatusByUnit(unitId: Long):Observable<Boolean>

    fun deleteRoomStatusByConfig(configId: Long):Observable<Boolean>

    fun deleteRoomStatusByConfig(configId:Long,name:String):Observable<Boolean>

    fun deleteRoomStatusById(ids:List<Long>):Observable<Int>

    fun getRoomDetailById(id:Long):Flowable<List<RoomDetailBean>>

    fun getRoomDetail(configId: Long):Flowable<List<RoomDetailBean>>

    fun getRoomDetail(configId: Long,name:String):Flowable<List<RoomDetailBean>>

    fun getRoomDetailOnce(configId:Long):Single<List<RoomDetailBean>>

    fun getRoomDetailOnce(configId: Long,name:String):Single<List<RoomDetailBean>>

    fun getRoomDetailOnce(configId: Long,name: String,damagePath:String):Single<List<RoomDetailBean>>

    fun getRoomDetailOnce(configId: Long,name: String,damagePath: String,damageIdx:Int):Single<List<RoomDetailBean>>

    fun getRoomDetailOnce(configId: Long,name:String,damagePath: String,des:String):Single<List<RoomDetailBean>>

    fun getRoomDetailByUnitOnce(unitId: Long):Single<List<RoomDetailBean>>

    fun updateRoomDetail(b:RoomDetailBean):Observable<Long>

    fun deleteRoomDetailByProject(projectId: Long):Observable<Boolean>

    fun deleteRoomDetailByUnit(unitId: Long):Observable<Boolean>

    fun deleteRoomDetailByConfig(configId: Long):Observable<Boolean>

    fun deleteRoomDetailByConfig(configId: Long,name:String):Observable<Boolean>

    fun deleteRoomDetailByPath(configId: Long,name:String,likePos:String):Observable<Boolean>

    fun deleteRoomDetailById(id:Long):Observable<Boolean>

    fun deleteRoomDetail(ids:List<Long>):Observable<Int>

    fun getReport(configId:Long):Flowable<List<ReportBean>>

    fun getReportOnce(configId:Long):Single<List<ReportBean>>

    fun getReportByUnitOnce(unitId: Long):Single<List<ReportBean>>

    fun updateReport(b:ReportBean):Observable<Long>

    fun deleteReportByProjectId(projectId:Long):Observable<Boolean>

    fun deleteReportByUnit(unitId: Long):Observable<Boolean>

    fun deleteReportByConfig(configId:Long):Observable<Boolean>

    fun getUserBean(mobile:String, passwordMd5:String):Maybe<List<UserBean>>

    fun getUserBean(account:String):Maybe<List<UserBean>>

    fun updateUserBean(b:UserBean):Observable<Long>

    fun updateUserBean(bs:List<UserBean>):Observable<Long>

    fun getLeader():Maybe<List<LeaderBean>>

    fun updateLeader(bs:List<LeaderBean>):Observable<Long>

    fun deleteLeader(bs:List<String>):Observable<Int>

    fun getInspector():Maybe<List<InspectorBean>>

    fun updateInspector(bs:List<InspectorBean>):Observable<Long>

    fun deleteInspector(bs:List<String>):Observable<Int>

    //leon add start
    fun getLocalBuildingOnce(id:Long):Single<List<BuildingBean>>
    fun createLocalBuilding(bb: BuildingBean):Observable<Long>
    fun updateLocalProject(projectBean:ProjectBean, bldList: List<BuildingBean>?, floorList: List<FloorBean>?, drawingList: List<DrawingBean>?):Observable<Long>

    fun createLocalFloor(bb: FloorBean):Observable<Long>
    fun getLocalFloorsInTheBuilding(bldId: Long): Flowable<List<FloorBean>>

    fun createLocalDrawing(dw: DrawingBean):Observable<Long>
//    fun getLocalFloorsInTheBuilding(bldId: Long): Flowable<List<FloorBean>>
    fun getBuildingIdByProjectId(proId: Long): Observable<Long>
    fun getLocalDrawingListInBuilding(proId:Long, bldId:Long): Maybe<List<DrawingBean>>
    fun getLocalDamageListInDrawing(drwingId:Long): Flowable<List<com.sribs.common.bean.db.DamageBean>>
    fun createDamageInDrawing(dmg:DamageBean): Observable<Long>
    fun updateDamageInDrawing(dmg:DamageBean): Observable<Int>
    fun getDrawingByProjectId(prjId: Long): Flowable<List<DrawingBean>>
    fun getNonInhabitProjects(): Flowable<List<RelationBean>>
    fun removeDamageInDrawing(drwId:Long):Observable<Boolean>
    //leon add end
}