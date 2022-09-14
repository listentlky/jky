package com.sribs.db.house

import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Single
import com.sribs.db.project.unit.config.ConfigBean

/**
 * @date 2021/7/30
 * @author elijah
 * @Description
 */
@Dao
interface HouseStatusDao {
    @Query("Select * from house_status")
    fun getAllHouseStatus(): Flowable<List<HouseStatusBean>>

    @Query("Select * from house_status where project_id = :projectId")
    fun getHouseStatusByProject(projectId:Long):Flowable<List<HouseStatusBean>>

    @Query("Select * from house_status where unit_id = :unitId")
    fun getHouseStatusByUnit(unitId:Long):Flowable<List<HouseStatusBean>>

    @Query("Select * from house_status where unit_id = :unitId")
    fun getHouseStatusByUnitOnce(unitId:Long): Single<List<HouseStatusBean>>


    @Query("Select * from house_status where project_id = :projectId")
    fun getHouseStatusByProjectOnce(projectId: Long): Single<List<HouseStatusBean>>

    @Query("Select * from house_status where config_id = :configId")
    fun getHouseStatusByConfig(configId:Long):Flowable<List<HouseStatusBean>>

    @Query("Select * from house_status where config_id = :configId")
    fun getHouseStatusByConfigOnce(configId:Long):Single<List<HouseStatusBean>>


    @Query("Select * from house_status where id = :id")
    fun getHouseStatus(id:Long):Flowable<List<HouseStatusBean>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHouseStatus(bean:HouseStatusBean):Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateHouseStatus(bean:HouseStatusBean):Int

    @Delete
    fun deleteHouseStatus(vararg bean:HouseStatusBean):Int

    @Query("Delete from house_status where project_id = :projectId")
    fun deleteHouseStatusByProject(projectId: Long)

    @Query("Delete from house_status where unit_id = :unitId")
    fun deleteHouseStatusByUnit(unitId: Long)

    @Query("Delete from house_status where config_id = :configId")
    fun deleteHouseStatusByConfig(configId: Long)

    @Query("Delete from house_status where id = :id")
    fun deleteHouseStatus(id: Long)
}