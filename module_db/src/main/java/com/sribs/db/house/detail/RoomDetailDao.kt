package com.sribs.db.house.detail

import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Single
import com.sribs.db.house.HouseStatusBean

/**
 * @date 2021/7/30
 * @author elijah
 * @Description
 */
@Dao
interface RoomDetailDao {
    @Query("Select * from room_detail")
    fun getAllRoomDetail(): Flowable<List<RoomDetailBean>>

    @Query("Select * from room_detail where id = :id")
    fun getRoomDetail(id:Long):Flowable<List<RoomDetailBean>>

    @Query("Select * from room_detail where config_id = :configId")
    fun getRoomDetailByConfig(configId:Long):Flowable<List<RoomDetailBean>>

    @Query("Select * from room_detail where config_id = :configId")
    fun getRoomDetailByConfigOnce(configId:Long):Single<List<RoomDetailBean>>

    @Query("Select * from room_detail where config_id = :configId and name = :name")
    fun getRoomDetailByRoomOnce(configId: Long,name:String):Single<List<RoomDetailBean>>

    @Query("Select * from room_detail where unit_id = :unitId")
    fun getRoomDetailByUnitOnce(unitId: Long):Single<List<RoomDetailBean>>


    @Query("Select * from room_detail where config_id = :configId and name = :name")
    fun getRoomDetailByRoom(configId: Long,name:String):Flowable<List<RoomDetailBean>>

    @Query("Select * from room_detail where config_id = :configId and name = :name and damage_path = :damagePath")
    fun getRoomDetailByPath(configId: Long,name: String,damagePath:String):Single<List<RoomDetailBean>>

    @Query("Select * from room_detail where config_id = :configId and name = :name and damage_path like '%' || :damagePath || '%'")
    fun getRoomDetailOtherByPath(configId: Long,name: String,damagePath:String):Single<List<RoomDetailBean>>


    @Query("Select * from room_detail where config_id = :configId and name = :name and damage_path = :damagePath and damage_idx = :damageIdx")
    fun getRoomDetailByPath(configId: Long,name: String,damagePath:String,damageIdx:Int): Single<List<RoomDetailBean>>

    @Query("Select * from room_detail where config_id = :configId and name = :name and damage_path = :damagePath and description = :des")
    fun getRoomDetailByDescription(configId: Long,name: String,damagePath:String,des:String): Single<List<RoomDetailBean>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRoomDetail(bean:RoomDetailBean):Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateRoomDetail(bean:RoomDetailBean):Int

    @Delete
    fun deleteRoomDetail(vararg bean:RoomDetailBean):Int

    @Query("Delete from room_detail where project_id = :projectId")
    fun deleteRoomDetailByProject(projectId:Long)

    @Query("Delete from room_detail where unit_id = :unitId")
    fun deleteRoomDetailByUnit(unitId: Long)

    @Query("Delete from room_detail where config_id = :configId")
    fun deleteRoomDetailByConfig(configId: Long)


    @Query("Delete from room_detail where config_id = :configId and name =:name")
    fun deleteRoomDetailByRoom(configId: Long,name:String)

    @Query("Delete from room_detail where config_id = :configId and name =:name and damage_path like '%' || :likePos || '%'")
    fun deleteRoomDetailOtherByPath(configId: Long,name:String,likePos:String)

    @Query("Delete from room_detail where id = :id")
    fun deleteRoomDetail(id: Long)
}