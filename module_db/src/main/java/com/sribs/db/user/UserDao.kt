package com.sribs.db.user

import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Maybe
import com.sribs.db.report.ReportBean

/**
 * @date 2021/8/4
 * @author elijah
 * @Description
 */
@Dao
interface UserDao {
    @Query("Select * from user")
    fun getUser(): Maybe<List<UserBean>>

    @Query("Select * from user where account =:account and password=:password")
    fun getUser(account:String,password:String): Maybe<List<UserBean>>

    @Query("Select * from user where account =:account")
    fun getUser(account:String): Maybe<List<UserBean>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(bean:UserBean):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(vararg bean:UserBean)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateUser(bean:UserBean):Int

    @Delete
    fun deleteUser(vararg bean:UserBean):Int

}