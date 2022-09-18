package com.sribs.db.v3.project

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * create time: 2022/9/16
 * author: bruce
 * description:
 */
@Dao
interface v3ProjectDao {

    @Query("Select * from project")
    fun getAllProject(): Flowable<List<v3ProjectRoom>>

    @Query("Select * from project where id = :id")
    fun getProject(id:Long):Flowable<List<v3ProjectRoom>>

    @Query("Select * from project where name = :name and building_no = :buildNo")
    fun getProjectOnce(name:String,buildNo:String): Flowable<List<v3ProjectRoom>>

    @Query("Select * from project where name = :name")
    fun getProjectOnce(name:String): Flowable<List<v3ProjectRoom>>

    @Insert(onConflict = REPLACE)
    fun insertProject(bean:v3ProjectRoom):Long

    @Update(onConflict = REPLACE)
    fun updateProject(bean:v3ProjectRoom):Int

    @Delete
    fun deleteProject(bean:v3ProjectRoom):Int

}