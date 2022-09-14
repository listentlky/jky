package com.sribs.db.leader

import androidx.room.*
import io.reactivex.Maybe
import com.sribs.db.user.UserBean

/**
 * @date 2021/8/9
 * @author elijah
 * @Description
 */
@Dao
interface LeaderDao {
    @Query("Select * from leader")
    fun getLeader(): Maybe<List<LeaderBean>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLeader(vararg bean:LeaderBean)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateLeader(bean:LeaderBean):Int

    @Delete
    fun deleteLeader(vararg bean:LeaderBean):Int
}