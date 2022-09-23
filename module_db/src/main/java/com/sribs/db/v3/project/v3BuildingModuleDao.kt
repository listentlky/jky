package com.sribs.db.v3.project

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * create time: 2022/9/17
 * author:
 * description:
 */
@Dao
interface v3BuildingModuleDao {

    @Query("Select * from v3_building_module")
    fun getAllProject(): Flowable<List<v3BuildingModuleRoom>>

    @Query("Select * from v3_building_module where project_id = :projectId and building_id = :buildingId")
    fun getProject(projectId: Long,buildingId: String): Flowable<List<v3BuildingModuleRoom>>

    @Query("Select * from v3_building_module where building_id = :buildingId")
    fun getProjectOnce(buildingId: String): Single<List<v3BuildingModuleRoom>>

    @Query("Select * from v3_building_module where project_id = :projectId and building_id = :buildingId")
    fun getProjectOnce(projectId: Long, buildingId: String): Single<List<v3BuildingModuleRoom>>

    @Query("Select * from v3_building_module where project_id = :projectId")
    fun getProjectOnce(projectId: Long): Single<List<v3BuildingModuleRoom>>

    @Insert(onConflict = REPLACE)
    fun insertProject(bean: v3BuildingModuleRoom): Long

    @Update(onConflict = REPLACE)
    fun updateProject(bean: v3BuildingModuleRoom): Int

    @Delete
    fun deleteProject(bean: v3BuildingModuleRoom): Int


    @Query("UPDATE v3_building_module SET drawings = :drawings WHERE id =:moduleId")
    fun updateProjectOneData(moduleId: Long,drawings:List<String>): Int
}