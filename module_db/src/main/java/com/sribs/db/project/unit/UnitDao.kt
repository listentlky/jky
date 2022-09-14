package com.sribs.db.project.unit

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
interface UnitDao {
    @Query("Select * from project_unit")
    fun getAllUnit(): Flowable<List<UnitBean>>

    @Query("Select * from project_unit where id = :id")
    fun getUnit(id:Long):Flowable<List<UnitBean>>

    @Query("Select * from project_unit where id = :id")
    fun getUnitOnce(id:Long): Single<List<UnitBean>>

    @Query("Select * from project_unit where project_id = :projectId ")
    fun getUnitByProjectId(projectId:Long):Flowable<List<UnitBean>>

    @Query("Select * from project_unit where project_id = :projectId ")
    fun getUnitByProjectIdOnce(projectId:Long):Single<List<UnitBean>>

    @Query("Select * from project_unit where remote_id = :remoteId ")
    fun getUnitByRemoteIdOnce(remoteId:String):Single<List<UnitBean>>

    @Insert(onConflict = REPLACE)
    fun insertUnit(bean:UnitBean):Long

    @Update(onConflict = REPLACE)
    fun updateUnit(bean:UnitBean):Int

    @Delete
    fun deleteUnit(bean:UnitBean):Int

    @Delete
    fun deleteUnits(vararg bean:UnitBean):Int

    @Query("Delete from project_unit where project_id = :projectId")
    fun deleteUnit(projectId: Long)
}