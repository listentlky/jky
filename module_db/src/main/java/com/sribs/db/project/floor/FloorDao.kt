package com.sribs.db.project.floor

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * @date 2021/7/7
 * @author elijah
 * @Description
 */
@Dao
interface FloorDao {
    @Query("Select * from project_floor")
    fun getAllFloor(): Flowable<List<FloorBean>>

    @Query("Select * from project_floor where id = :id")
    fun getFloor(id:Long):Flowable<List<FloorBean>>

    @Query("Select * from project_floor where id = :id")
    fun getFloorOnce(id:Long): Single<List<FloorBean>>

    @Query("Select * from project_floor where project_id = :floorId and status == 0")
    fun getFloorByProjectId(floorId:Long):Flowable<List<FloorBean>>

    @Query("Select * from project_floor where project_id = :floorId and status == 0")
    fun getFloorByProjectIdOnce(floorId:Long):Single<List<FloorBean>>

    @Query("Select * from project_floor where remote_id = :remoteId")
    fun getFloorByRemoteIdOnce(remoteId:String):Single<List<FloorBean>>

    @Query("Select * from project_floor where bld_id = :bldId and status == 0 order by floor_id asc ")
    fun getLocalFloorsInTheBuilding(bldId:Long):Flowable<List<FloorBean>>

    @Insert(onConflict = REPLACE)
    fun insertFloor(bean:FloorBean):Long

    @Update(onConflict = REPLACE)
    fun updateFloor(bean:FloorBean):Int

    @Delete
    fun deleteFloor(bean:FloorBean):Int

    @Delete
    fun deleteFloors(vararg bean:FloorBean):Int

    @Query("Delete from project_floor where id = :floorId")
    fun deleteFloor(floorId: Long)

    @Query("Delete from project_floor where project_id = :projectId")
    fun deleteFloorByProjectId(projectId: Long)

    @Query("Delete from project_floor where bld_id = :bldId")
    fun deleteFloorByBuildingId(bldId: Long)
}