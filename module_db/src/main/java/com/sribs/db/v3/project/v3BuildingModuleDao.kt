package com.sribs.db.v3.project

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.sribs.common.bean.db.DrawingV3Bean
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface v3BuildingModuleDao {

    @Query("Select * from v3_building_module")
    fun getAllBuildingModule(): Flowable<List<v3BuildingModuleRoom>>

    @Query("Select * from v3_building_module where id = :moduleId")
    fun getBuildingModule(moduleId: Long): Single<v3BuildingModuleRoom>

    @Query("Select * from v3_building_module where project_id = :projectId and building_id = :buildingId")
    fun getBuildingModule(projectId: Long, buildingId: Long): Flowable<List<v3BuildingModuleRoom>>

    @Query("Select * from v3_building_module where project_id = :projectId")
    fun getBuildingModuleByProjectId(projectId: Long): Flowable<List<v3BuildingModuleRoom>>

    @Query("Select * from v3_building_module where building_id = :buildingId")
    fun getBuildingModuleByBuildingId(buildingId: Long): Flowable<List<v3BuildingModuleRoom>>

    @Query("Select * from v3_building_module where project_id = :projectId and building_id = :buildingId")
    fun getBuildingModuleOnce(projectId: Long, buildingId: Long): Single<List<v3BuildingModuleRoom>>

    @Query("Select * from v3_building_module where id = :moduleId")
    fun getBuildingModuleOnce(moduleId: Long): Single<List<v3BuildingModuleRoom>>

    @Query("Select * from v3_building_module where project_id = :projectId and building_id = :buildingId and id = :moduleId")
    fun getBuildingModuleOnce(projectId: Long, buildingId: Long, moduleId:Long): Single<List<v3BuildingModuleRoom>>

    @Insert(onConflict = REPLACE)
    fun insertBuildingModule(bean: v3BuildingModuleRoom): Long

    @Update(onConflict = REPLACE)
    fun updateBuildingModule(bean: v3BuildingModuleRoom): Int

    @Query("UPDATE v3_building_module SET isChanged = :isChanged,status = :status WHERE id = :id")
    fun updateBuildingModule(id:Long,isChanged:Int,status:Int): Int

    @Query("UPDATE v3_building_module SET isChanged = :isChanged WHERE id = :id")
    fun updateBuildingModule(id:Long,isChanged:Int): Int

    @Query("UPDATE v3_building_module SET isChanged = :isChanged,status = :status WHERE project_id = :projectId")
    fun updateBuildingModuleByProjectId(projectId:Long,isChanged:Int,status:Int): Int

    @Query("UPDATE v3_building_module SET isChanged = :isChanged,status = :status WHERE building_id = :buildingId")
    fun updateBuildingModuleByBuildingId(buildingId :Long,isChanged:Int,status:Int): Int

    @Delete
    fun deleteBuildingModule(bean: v3BuildingModuleRoom): Int

    @Query("UPDATE v3_building_module SET drawings = :drawings , isChanged = :isChanged  WHERE id =:moduleId")
    fun updateBuildingModuleOneData(moduleId: Long, drawings:List<DrawingV3Bean>, isChanged:Int): Int

    @Query("UPDATE v3_building_module SET drawings = :drawings , isChanged = :isChanged  WHERE id =:moduleId")
    fun updateBuildingModule(moduleId: Long, drawings:List<DrawingV3Bean>, isChanged:Int): Int

    @Query("Delete from v3_building_module where project_id = :projectId")
    fun deleteBuildingModuleByProjectId(projectId: Long)

    @Query("Delete from v3_building_module where building_id = :buildingId")
    fun deleteBuildingModuleByBuildingId(buildingId: Long)
}