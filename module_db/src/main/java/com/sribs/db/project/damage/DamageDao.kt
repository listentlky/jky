package com.sribs.db.project.damage

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
interface DamageDao {
    @Query("Select * from damage_detail")
    fun getAllFloor(): Flowable<List<DamageBean>>

    @Query("Select * from damage_detail where id = :id")
    fun getDamageDetail(id:Long):Flowable<List<DamageBean>>

    @Query("Select * from damage_detail where id = :id")
    fun getDamageDetailOnce(id:Long): Single<List<DamageBean>>

    @Query("Select * from damage_detail where drawing_id = :drawingId and status == 0")
    fun getDamageByDrawingId(drawingId:Long):Flowable<List<DamageBean>>

    @Query("Select * from damage_detail where drawing_id = :drawingId and status == 0")
    fun getDamageByDrawingIdOnce(drawingId:Long):Single<List<DamageBean>>

    @Query("Select * from damage_detail where remote_id = :remoteId ")
    fun getDamageByRemoteIdOnce(remoteId:String):Single<List<DamageBean>>

    @Insert(onConflict = REPLACE)
    fun insertDamage(bean:DamageBean):Long

    @Update(onConflict = REPLACE)
    fun updateDamage(bean:DamageBean):Int

    @Delete
    fun deleteDamage(bean:DamageBean):Int

    @Delete
    fun deleteDamages(vararg bean:DamageBean):Int

    @Query("Update damage_detail Set status = 1 where id = :dmgId")//1,删除；0，正常
    fun deleteDamages(dmgId: Long)

}