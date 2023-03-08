package com.sribs.common.server

import com.alibaba.android.arouter.facade.template.IProvider
import com.sribs.common.bean.db.*
import com.sribs.common.bean.db.v3.project.v3BuildingModuleDbBean
import com.sribs.common.bean.db.v3.project.v3ProjectDbBean
import com.sribs.common.bean.v3.v3ModuleFloorDbBean
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

    fun getv3AllBuildingModule(): Flowable<List<v3BuildingModuleDbBean>>

    fun getv3BuildingModuleByProjectId(projectId: Long): Flowable<List<v3BuildingModuleDbBean>>

    fun getv3BuildingModuleByBuildingId(buildingId: Long): Flowable<List<v3BuildingModuleDbBean>>

    fun getv3BuildingModule(projectId: Long,buildingId: Long): Flowable<List<v3BuildingModuleDbBean>>

    fun getv3BuildingModule(projectId: Long,buildingId: String): Flowable<List<v3BuildingModuleDbBean>>

    fun getv3BuildingModule(moduleId: Long): Single<v3BuildingModuleDbBean>

    fun getv3BuildingModuleOnce(buildingId: String): Single<List<v3BuildingModuleDbBean>>

    fun getv3BuildingModuleOnce(
        projectId: Long,
        buildingId: Long
    ): Single<List<v3BuildingModuleDbBean>>

    fun getv3BuildingModuleOnce(
        projectId: Long,
        buildingId: Long,
        moduleId: Long
    ): Single<List<v3BuildingModuleDbBean>>

    fun getv3BuildingModuleOnce(
        projectId: Long,
        buildingId: Long,
        moduleName: String
    ): Single<List<v3BuildingModuleDbBean>>

    fun getv3BuildingModuleOnce(moduleId: Long): Single<List<v3BuildingModuleDbBean>>

    fun updatev3BuildingModule(b: v3BuildingModuleDbBean): Observable<Long>

    fun deleteBuildingModuleByProjectId(projectId: Long):Observable<Boolean>

    fun deleteBuildingModuleByBuildingId(buildingId: Long):Observable<Boolean>

    fun updatev3BuildingModuleDrawing(b: v3BuildingModuleDbBean): Observable<Int>

    fun updatev3BuildingModuleDrawing(id:Long,drawingList:List<DrawingV3Bean>): Observable<Int>

    fun updateBuildingModule(moduleId: Long, drawings:List<DrawingV3Bean>, isChanged:Int): Observable<Int>

    fun deletev3BuildingModule(b: v3BuildingModuleDbBean): Observable<Int>

    fun updatev3ModuleFloor(b:v3ModuleFloorDbBean):Observable<Long>

    fun getv3ModuleFloor(projectId: Long,buildingId: Long,moduleId: Long): Flowable<List<v3ModuleFloorDbBean>>

    fun getModuleFloorByProjectId(project_id: Long): Flowable<List<v3ModuleFloorDbBean>>

    fun getModuleFloorByBuilding(buildingId: Long):Flowable<List<v3ModuleFloorDbBean>>

    fun getModuleFloorByModule(moduleId: Long): Flowable<List<v3ModuleFloorDbBean>>

    fun deletev3ModuleFloor(projectId:Long,buildingId:Long,moduleId:Long): Observable<Boolean>

    fun deletev3ModuleFloorOnce(projectId:Long,buildingId:Long,moduleId:Long,floorId: Long): Observable<Boolean>

    fun deleteModuleFloorByProjectId(projectId: Long): Observable<Boolean>

    fun deleteModuleFloorByBuildingId(buildingId: Long): Observable<Boolean>

    fun deleteModuleFloorByModuleId(moduleId: Long):Observable<Boolean>

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
    fun deleteFloorByProjectId(projectId: Long):Observable<Boolean>
    fun deleteFloorByBuildingId(bldId:Long):Observable<Boolean>

    fun createLocalDrawing(dw: DrawingBean):Observable<Long>
//    fun getLocalFloorsInTheBuilding(bldId: Long): Flowable<List<FloorBean>>
    fun getBuildingIdByProjectId(proId: Long): Observable<Long>

    fun deleteBuildingByProjectId(projectId: Long): Observable<Boolean>
    fun deleteBuilding(bld: Long): Observable<Boolean>
    fun getLocalDrawingListInBuilding(proId:Long, bldId:Long): Maybe<List<DrawingBean>>
    fun getLocalDamageListInDrawing(drwingId:Long): Flowable<List<com.sribs.common.bean.db.DamageBean>>
    fun createDamageInDrawing(dmg:DamageBean): Observable<Long>
    fun updateDamageInDrawing(dmg:DamageBean): Observable<Int>
    fun getDrawingByProjectId(prjId: Long): Flowable<List<DrawingBean>>
    fun getNonInhabitProjects(): Flowable<List<RelationBean>>
    fun removeDamageInDrawing(drwId:Long):Observable<Boolean>
    //leon add end

    fun getAllFloor(): Flowable<List<FloorBean>>

    fun getFloorByProjectId(projectId:Long):Flowable<List<FloorBean>>

    fun getFloorByBuildingId(buildingId:Long):Flowable<List<FloorBean>>

    fun getAllBuilding(): Flowable<List<BuildingBean>>

    fun getBuildingByProjectId(projectId:Long):Flowable<List<BuildingBean>>

    fun getAllDrawing(): Flowable<List<DrawingBean>>

    /**
     * 更新模块层表图纸数据
     */
    fun updateModuleFloorDrawing(drawingList:List<DrawingV3Bean>, id: Long):Observable<Int>

    fun updateProject(id:Long,isChanged:Int,status:Int): Observable<Long>

    fun updateBuilding(id:Long,isChanged:Int,status:Int): Observable<Long>

    fun updateBuildingByProjectId(projectId:Long,isChanged:Int,status:Int): Observable<Long>

    fun updateBuildingModule(id:Long,isChanged:Int,status:Int): Observable<Long>

    fun updateBuildingModule(id:Long,isChanged:Int): Observable<Long>

    fun updateBuildingModuleByProjectId(projectId:Long,isChanged:Int,status:Int): Observable<Long>

    fun updateBuildingModuleByBuildingId(buildingId :Long,isChanged:Int,status:Int): Observable<Long>

    fun updateBuildingModuleVersion(id:Long, version:Long):Observable<Long>

    fun getModuleFloor(id: Long):Flowable<List<v3ModuleFloorDbBean>>

    fun updateBuildingLeaderByProjectId(projectId:Long,leader:String,inspectorName:String): Observable<Long>

    fun updateBuildingFloorLeaderByProjectId(projectId:Long,leader:String,inspectorName:String): Observable<Long>

    fun updateBuildingModuleLeaderByProjectId(projectId:Long,leader:String,inspectorName:String): Observable<Long>

}