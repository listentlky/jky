package com.sribs.db.v3.project

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.sribs.common.bean.db.DrawingV3Bean
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface v3ModuleFloorDao {
    @Query("Select * from V3_MODULE_FLOOR")
    fun getAllModuleFloor(): Flowable<List<v3ModuleFloorRoom>>

    @Query("Select * from V3_MODULE_FLOOR where id = :id")
    fun getModuleFloor(id: Long): Flowable<List<v3ModuleFloorRoom>>

    @Query("Select * from V3_MODULE_FLOOR where id = :id")
    fun getModuleFloorOnce(id: Long): Single<List<v3ModuleFloorRoom>>

    @Query("Select * from V3_MODULE_FLOOR where project_id = :projectId and building_id =:buildingId and module_id =:moduleId and status == 0")
    fun getModuleFloorByProjectIdOnce(
        projectId: Long,
        buildingId: Long,
        moduleId: Long
    ): Flowable<List<v3ModuleFloorRoom>>

    @Query("Update V3_MODULE_FLOOR SET drawingsList = :drawingList,isChanged = :isChange  WHERE id = :id and status == 0")
    fun updateModuleFloorDrawing(drawingList:List<DrawingV3Bean>,isChange:Boolean
                                 , id: Long): Int

    @Query("Select * from V3_MODULE_FLOOR where project_id = :project_id")
    fun getModuleFloorByProjectId(project_id: Long): Flowable<List<v3ModuleFloorRoom>>

    @Query("Select * from V3_MODULE_FLOOR where building_id = :buildingId")
    fun getModuleFloorByBuilding(buildingId: Long): Flowable<List<v3ModuleFloorRoom>>

    @Query("Select * from V3_MODULE_FLOOR where remote_id = :remoteId")
    fun getModuleFloorByRemoteIdOnce(remoteId: String): Single<List<v3ModuleFloorRoom>>

    @Query("Select * from V3_MODULE_FLOOR where module_id = :moduleId")
    fun getModuleFloorByModule(moduleId: Long): Flowable<List<v3ModuleFloorRoom>>

    @Insert(onConflict = REPLACE)
    fun insertModuleFloor(bean: v3ModuleFloorRoom): Long

    @Update(onConflict = REPLACE)
    fun updateModuleFloor(bean: v3ModuleFloorRoom): Int

    @Delete
    fun deleteModuleFloor(bean: v3ModuleFloorRoom): Int

    @Delete
    fun deleteModuleFloor(vararg bean: v3ModuleFloorRoom): Int

    @Query("Delete from V3_MODULE_FLOOR where module_id = :moduleId and project_id = :projectId and building_id =:buildingId ")
    fun deleteModuleFloor(projectId: Long, buildingId: Long, moduleId: Long)

    @Query("Delete from V3_MODULE_FLOOR where module_id = :moduleId and project_id = :projectId and building_id =:buildingId  and  floor_id =:floorId ")
    fun deleteModuleFloorOnce(projectId: Long, buildingId: Long, moduleId: Long, floorId: Long)

    @Query("Delete from V3_MODULE_FLOOR where project_id = :projectId")
    fun deleteModuleFloorByProjectId(projectId: Long)

    @Query("Delete from V3_MODULE_FLOOR where building_id = :buildingId")
    fun deleteModuleFloorByBuildingId(buildingId: Long)
}