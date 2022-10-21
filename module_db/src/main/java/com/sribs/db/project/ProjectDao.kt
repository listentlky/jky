package com.sribs.db.project

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
interface ProjectDao {
    @Query("Select * from project")
    fun getAllProject(): Flowable<List<ProjectBean>>

    @Query("Select * from project where id = :id")
    fun getProject(id:Long):Flowable<List<ProjectBean>>

    @Query("Select * from project where id = :id")
    fun getProjectOnce(id:Long): Single<List<ProjectBean>>

    @Query("Select * from project where name = :name and building_no = :buildNo")
    fun getProjectOnce(name:String,buildNo:String): Single<List<ProjectBean>>

    @Query("Select * from project where name = :name")
    fun getProjectOnce(name:String): Single<List<ProjectBean>>

    @Insert(onConflict = REPLACE)
    fun insertProject(bean:ProjectBean):Long

    @Update(onConflict = REPLACE)
    fun updateProject(bean:ProjectBean):Int

    @Query("UPDATE project SET isChanged = :isChanged ,status = :status WHERE id = :id")
    fun updateProject(id:Long,isChanged:Int,status:Int): Int

    @Delete
    fun deleteProject(bean:ProjectBean):Int

}