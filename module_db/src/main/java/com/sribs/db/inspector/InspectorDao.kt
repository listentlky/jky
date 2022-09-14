package com.sribs.db.inspector

import androidx.room.*
import io.reactivex.Maybe
import com.sribs.db.user.UserBean

/**
 * @date 2021/8/9
 * @author elijah
 * @Description
 */
@Dao
interface InspectorDao {
    @Query("Select * from inspector")
    fun getInspector(): Maybe<List<InspectorBean>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInspector(vararg bean:InspectorBean)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateInspector(bean:InspectorBean):Int

    @Delete
    fun deleteInspector(vararg bean:InspectorBean):Int
}