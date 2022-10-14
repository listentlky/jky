package com.sribs.db.v3.project

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.sribs.common.bean.db.DrawingV3Bean
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface v3BuildingModuleDao {

    @Query("Select * from v3_building_module")
    fun getAllProject(): Flowable<List<v3BuildingModuleRoom>>

    @Query("Select * from v3_building_module where id = :moduleId")
    fun getProject(moduleId: Long): Single<v3BuildingModuleRoom>

    @Query("Select * from v3_building_module where project_id = :projectId and building_id = :buildingId")
    fun getProject(projectId: Long,buildingId: Long): Flowable<List<v3BuildingModuleRoom>>

    @Query("Select * from v3_building_module where project_id = :projectId")
    fun getBuildingModuleByProjectId(projectId: Long): Flowable<List<v3BuildingModuleRoom>>

    @Query("Select * from v3_building_module where building_id = :buildingId")
    fun getBuildingModuleByBuildingId(buildingId: Long): Flowable<List<v3BuildingModuleRoom>>

    @Query("Select * from v3_building_module where project_id = :projectId and building_id = :buildingId")
    fun getProjectOnce(projectId: Long, buildingId: Long): Single<List<v3BuildingModuleRoom>>

    @Query("Select * from v3_building_module where id = :moduleId")
    fun getProjectOnce(moduleId: Long): Single<List<v3BuildingModuleRoom>>

    @Query("Select * from v3_building_module where project_id = :projectId and building_id = :buildingId and id = :moduleId")
    fun getProjectOnce(projectId: Long, buildingId: Long,moduleId:Long): Single<List<v3BuildingModuleRoom>>

    @Insert(onConflict = REPLACE)
    fun insertProject(bean: v3BuildingModuleRoom): Long

    @Update(onConflict = REPLACE)
    fun updateProject(bean: v3BuildingModuleRoom): Int

    @Delete
    fun deleteProject(bean: v3BuildingModuleRoom): Int

    @Query("UPDATE v3_building_module SET drawings = :drawings , isChanged = :isChanged  WHERE id =:moduleId")
    fun updateProjectOneData(moduleId: Long,drawings:List<DrawingV3Bean>,isChanged:Boolean): Int

    @Query("Delete from v3_building_module where project_id = :projectId")
    fun deleteBuildingModuleByProjectId(projectId: Long)

    @Query("Delete from v3_building_module where building_id = :buildingId")
    fun deleteBuildingModuleByBuildingId(buildingId: Long)
}