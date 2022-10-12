package com.sribs.db.house

import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Single
import com.sribs.db.project.unit.UnitBean

/**
 * @date 2021/7/30
 * @author elijah
 * @Description
 */
@Dao
interface RoomStatusDao {
/*    @Query("Select * from room_status")
    fun getAllRoomStatus(): Flowable<List<RoomStatusBean>>*/

/*    @Query("Select * from room_status where project_id = :projectId")
    fun getRoomStatusByProject(projectId:Long):Flowable<List<RoomStatusBean>>*/

/*    @Query("Select * from room_status where unit_id = :unitId")
    fun getRoomStatusByUnit(unitId:Long):Flowable<List<RoomStatusBean>>*/

    @Query("Select * from room_status where unit_id = :unitId")
    fun getRoomStatusByUnitOnce(unitId:Long): Single<List<RoomStatusBean>>

    @Query("Select * from room_status where config_id = :configId")
    fun getRoomStatusByConfig(configId:Long):Flowable<List<RoomStatusBean>>

    @Query("Select * from room_status where config_id = :configId and name = :name")
    fun getRoomStatusByConfig(configId:Long,name:String):Flowable<List<RoomStatusBean>>

    @Query("Select * from room_status where config_id = :configId and name = :name")
    fun getRoomStatusByConfigOnce(configId:Long,name:String):Single<List<RoomStatusBean>>

    @Query("Select * from room_status where config_id = :configId")
    fun getRoomStatusByConfigOnce(configId:Long):Single<List<RoomStatusBean>>

/*    @Query("Select * from house_status where id = :id")
    fun getRoomStatus(id:Long):Flowable<List<RoomStatusBean>>*/


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRoomStatus(bean:RoomStatusBean):Long

/*    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateRoomStatus(bean:RoomStatusBean):Int*/

    @Delete
    fun deleteRoomStatus(vararg bean:RoomStatusBean):Int

    @Query("Delete from room_status where project_id = :projectId")
    fun deleteRoomStatusByProject(projectId: Long)

    @Query("Delete from room_status where unit_id = :unitId")
    fun deleteRoomStatusByUnit(unitId: Long)

    @Query("Delete from room_status where config_id = :configId")
    fun deleteRoomStatusByConfig(configId: Long)


    @Query("Delete from room_status where config_id = :configId and name = :name")
    fun deleteRoomStatusByConfig(configId: Long,name:String)

/*    @Query("Delete from room_status where id = :id")
    fun deleteRoomStatus(id: Long)*/


}