package com.sribs.db.v3.project

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface v3ModuleFloorDao {
    @Query("Select * from V3_MODULE_FLOOR")
    fun getAllFloor(): Flowable<List<v3ModuleFloorRoom>>

    @Query("Select * from V3_MODULE_FLOOR where id = :id")
    fun getFloor(id: Long): Flowable<List<v3ModuleFloorRoom>>

    @Query("Select * from V3_MODULE_FLOOR where id = :id")
    fun getFloorOnce(id: Long): Single<List<v3ModuleFloorRoom>>


    @Query("Select * from V3_MODULE_FLOOR where project_id = :projectId and building_id =:buildingId and module_id =:moduleId and status == 0")
    fun getFloorByProjectIdOnce(
        projectId: Long,
        buildingId: Long,
        moduleId: Long
    ): Flowable<List<v3ModuleFloorRoom>>


    @Query("Select * from V3_MODULE_FLOOR where project_id = :floorId and status == 0")
    fun getFloorByProjectId(floorId: Long): Flowable<List<v3ModuleFloorRoom>>

    @Query("Select * from V3_MODULE_FLOOR where project_id = :floorId and status == 0")
    fun getFloorByProjectIdOnce(floorId: Long): Single<List<v3ModuleFloorRoom>>

    @Query("Select * from V3_MODULE_FLOOR where remote_id = :remoteId")
    fun getFloorByRemoteIdOnce(remoteId: String): Single<List<v3ModuleFloorRoom>>

    @Query("Select * from V3_MODULE_FLOOR where building_id = :bldId and status == 0 order by floor_id asc ")
    fun getLocalFloorsInTheBuilding(bldId: Long): Flowable<List<v3ModuleFloorRoom>>

    @Insert(onConflict = REPLACE)
    fun insertFloor(bean: v3ModuleFloorRoom): Long

    @Update(onConflict = REPLACE)
    fun updateFloor(bean: v3ModuleFloorRoom): Int

    @Delete
    fun deleteFloor(bean: v3ModuleFloorRoom): Int

    @Delete
    fun deleteFloors(vararg bean: v3ModuleFloorRoom): Int

    @Query("Delete from V3_MODULE_FLOOR where module_id = :moduleId and project_id = :projectId and building_id =:buildingId ")
    fun deleteFloor(projectId: Long, buildingId: Long, moduleId: Long)

    @Query("Delete from V3_MODULE_FLOOR where module_id = :moduleId and project_id = :projectId and building_id =:buildingId  and  floor_id =:floorId ")
    fun deleteFloorOnce(projectId: Long, buildingId: Long, moduleId: Long, floorId: Long)
}