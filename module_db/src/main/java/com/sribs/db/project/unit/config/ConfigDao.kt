package com.sribs.db.project.unit.config

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Flowable
import io.reactivex.Single
import java.sql.Date

/**
 * @date 2021/7/7
 * @author elijah
 * @Description
 */
@Dao
interface ConfigDao {
    @Query("Select * from project_unit_config")
    fun getAllConfig(): Flowable<List<ConfigBean>>

    @Query("Select * from project_unit_config where id = :id")
    fun getConfig(id:Long):Flowable<List<ConfigBean>>

    @Query("Select * from project_unit_config where id = :id")
    fun getConfigOnce(id:Long):Single<List<ConfigBean>>

    @Query("Select * from project_unit_config where project_id = :projectId and unit_id = :unitId")
    fun getConfigById(projectId:Long,unitId:Long):Flowable<List<ConfigBean>>

    @Query("Select * from project_unit_config where project_id = :projectId")
    fun getConfigById(projectId:Long):Flowable<List<ConfigBean>>


    @Query("Select * from project_unit_config where project_id = :projectId")
    fun getConfigByIdOnce(projectId:Long):Single<List<ConfigBean>>

    @Query("Select * from project_unit_config where unit_id = :unitId")
    fun getConfigByUnit(unitId: Long):Flowable<List<ConfigBean>>

    @Query("Select * from project_unit_config where unit_id = :unitId")
    fun getConfigByUnitOnce(unitId: Long):Single<List<ConfigBean>>

    @Query("Select * from project_unit_config where unit_id = :unitId and floor_idx = :floorIdx")
    fun getConfigByUnitFloorOnce(unitId: Long,floorIdx:Int):Single<List<ConfigBean>>


    @Insert(onConflict = REPLACE)
    fun insertConfig(bean:ConfigBean):Long

    @Update(onConflict = REPLACE)
    fun updateConfig(bean:ConfigBean):Int


    @Query("Update project_unit_config set floor_num = :floorNum, " +
            "corridor_num = :corridorNum, " +
            "corridor_config = :corridorConfig, " +
            "platform_num = :platformNum, " +
            "platform_config = :platformConfig, " +
            "config1 = :config1, " +
            "config2 = :config2, " +
            "unit_type = :unitType, " +
            "update_time = :updateTime "+
            "where id = :configId")
    fun updateConfig(configId:Long,floorNum:String?=null,corridorNum:String?=null,corridorConfig:String?=null,
                     platformNum:String?=null,platformConfig:String?=null,config1:String?=null,config2:String?=null,
                     unitType:Int?=null,
                     updateTime:Date?=Date(java.util.Date().time)
    )






    @Delete
    fun deleteConfig(bean:ConfigBean)

    @Delete
    fun deleteConfigs(vararg bean:ConfigBean):Int

    @Query("Delete from project_unit_config where project_id = :projectId")
    fun deleteConfig(projectId: Long)

    @Query("Delete from project_unit_config where unit_id = :unitId")
    fun deleteConfigByUnit(unitId: Long)
}