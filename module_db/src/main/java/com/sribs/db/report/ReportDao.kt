package com.sribs.db.report

import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Single
import com.sribs.db.house.RoomStatusBean

/**
 * @date 2021/8/3
 * @author elijah
 * @Description
 */
@Dao
interface ReportDao {
    @Query("Select * from house_report")
    fun getReport(): Flowable<List<ReportBean>>

    @Query("Select * from house_report where config_id = :configId")
    fun getReport(configId:Long): Flowable<List<ReportBean>>

    @Query("Select * from house_report where config_id = :configId")
    fun getReportOnce(configId:Long): Single<List<ReportBean>>

    @Query("Select * from house_report where unit_id = :unitId")
    fun getReportByUnitOnce(unitId: Long): Single<List<ReportBean>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReport(bean:ReportBean):Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateReport(bean:ReportBean):Int


    @Delete
    fun deleteReport(vararg bean:ReportBean):Int

    @Query("Delete from house_report where project_id = :projectId")
    fun deleteReportByProject(projectId: Long)

    @Query("Delete from house_report where unit_id = :unitId")
    fun deleteReportByUnit(unitId: Long)

    @Query("Delete from house_report where config_id = :configId")
    fun deleteReportByConfig(configId: Long)

    @Query("Delete from house_report where id = :id")
    fun deleteReport(id: Long)

}