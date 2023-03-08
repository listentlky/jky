package com.sribs.db.project.building

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
interface BuildingDao {
    @Query("Select * from project_building")
    fun getAllBuilding(): Flowable<List<BuildingBean>>

    @Query("Select * from project_building where id = :id")
    fun getBuilding(id:Long):Flowable<List<BuildingBean>>

    @Query("Select * from project_building where id = :id")
    fun getBuildingOnce(id:Long): Single<List<BuildingBean>>

    @Query("Select * from project_building where id = :bldId ")
    fun getBuildingByBldId(bldId:Long):Flowable<List<BuildingBean>>

    @Query("Select * from project_building where project_id = :proId")
    fun getBuildingByProjectId(proId:Long):Flowable<List<BuildingBean>>

    @Query("Select * from project_building where project_id = :proId")
    fun getBuildingOnceByProjectId(proId:Long):Single<List<BuildingBean>>

    //仅在二期使用，三期开始将弃用，因为同一个项目id有多个buildingId
    @Query("Select max(id) from project_building where project_id = :proId")
    fun getBuildingIdByProjectId(proId:Long):Long

    @Query("Select * from project_building where remote_id = :remoteId ")
    fun getBuildingByRemoteId(remoteId:String):Flowable<List<BuildingBean>>

    @Insert(onConflict = REPLACE)
    fun insertBuilding(bean:BuildingBean):Long

    @Update(onConflict = REPLACE)
    fun updateBuilding(bean:BuildingBean):Int

    @Query("UPDATE project_building SET isChanged = :isChanged,status = :status WHERE id = :id")
    fun updateBuilding(id:Long,isChanged:Int,status:Int): Int

    @Query("UPDATE project_building SET isChanged = :isChanged,status = :status WHERE project_id = :projectId")
    fun updateBuildingByProjectId(projectId:Long,isChanged:Int,status:Int): Int

    @Query("UPDATE project_building SET leader = :leader,inspector_name = :inspectorName WHERE project_id = :projectId")
    fun updateLeaderByProjectId(projectId:Long,leader:String,inspectorName:String): Int

    @Delete
    fun deleteBuilding(bean:BuildingBean):Int

    @Delete
    fun deleteBuildings(vararg bean:BuildingBean):Int

    @Query("Delete from project_building where id = :bldId")
    fun deleteBuilding(bldId: Long)

    @Query("Delete from project_building where project_id = :projectId")
    fun deleteBuildingByProjectId(projectId: Long)

}