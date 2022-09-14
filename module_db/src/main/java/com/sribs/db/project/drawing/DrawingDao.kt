package com.sribs.db.project.drawing

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.sribs.common.bean.db.RelationBean
import com.sribs.db.project.damage.DamageBean
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * @date 2021/7/7
 * @author elijah
 * @Description
 */
@Dao
interface DrawingDao {
    @Query("Select * from project_drawing")
    fun getAllDrawing(): Flowable<List<DrawingBean>>

    @Query("Select * from project_drawing where id = :id")
    fun getDrawing(id:Long):Flowable<List<DrawingBean>>

    @Query("Select * from project_drawing where id = :id")
    fun getDrawingOnce(id:Long): Single<List<DrawingBean>>

    @Query("Select * from project_drawing where project_id = :pjtId and status == 0")
    fun getDrawingByProjectId(pjtId:Long):Flowable<List<DrawingBean>>

    @Query("Select * from project_drawing where project_id = :drwId and status == 0")
    fun getDrawingByProjectIdOnce(drwId:Long):Single<List<DrawingBean>>

    @Query("Select * from project_drawing where remote_id = :remoteId ")
    fun getDrawingByRemoteIdOnce(remoteId:String):Single<List<DrawingBean>>

    @Query("Select * from project_drawing where project_id = :proId and bld_id = :bldId and status == 0 order by create_time asc")
    fun getLocalDrawingListInBuilding(proId:Long, bldId:Long):Maybe<List<DrawingBean>>

    @Insert(onConflict = REPLACE)
    fun insertDrawing(bean:DrawingBean):Long

    @Update(onConflict = REPLACE)
    fun updateDrawing(bean:DrawingBean):Int

    @Delete
    fun deleteDrawing(bean:DrawingBean):Int

    @Delete
    fun deleteDrawing(vararg bean:DrawingBean):Int

    @Query("Delete from project_drawing where id = :drwId")
    fun deleteDrawing(drwId: Long)

//    @Query("select a.* from project_drawing a inner join damage_detail b on a.id = b.drawing_id")
//    fun getNonInhabitProjects():Flowable<List<DrawingBean>>

    @Query("select a.project_id as projectId, a.bld_id as bldId, a.id as drawingId, b.id as damageId from project_drawing a inner join damage_detail b on a.id = b.drawing_id")
    fun getNonInhabitProjects():Flowable<List<RelationBean>>
}