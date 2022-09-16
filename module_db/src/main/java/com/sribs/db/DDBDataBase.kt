package com.sribs.db

import android.annotation.SuppressLint
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sribs.db.house.HouseStatusBean
import com.sribs.db.house.HouseStatusDao
import com.sribs.db.house.RoomStatusBean
import com.sribs.db.house.RoomStatusDao
import com.sribs.db.house.detail.RoomDetailBean
import com.sribs.db.house.detail.RoomDetailDao
import com.sribs.db.inspector.InspectorBean
import com.sribs.db.inspector.InspectorDao
import com.sribs.db.leader.LeaderBean
import com.sribs.db.leader.LeaderDao
import com.sribs.db.project.ProjectBean
import com.sribs.db.project.ProjectDao
import com.sribs.db.project.building.BuildingBean
import com.sribs.db.project.building.BuildingDao
import com.sribs.db.project.damage.DamageBean
import com.sribs.db.project.damage.DamageDao
import com.sribs.db.project.drawing.DrawingBean
import com.sribs.db.project.drawing.DrawingDao
import com.sribs.db.project.floor.FloorBean
import com.sribs.db.project.floor.FloorDao
import com.sribs.db.project.unit.UnitBean
import com.sribs.db.project.unit.UnitDao
import com.sribs.db.project.unit.config.ConfigBean
import com.sribs.db.project.unit.config.ConfigDao
import com.sribs.db.report.ReportBean
import com.sribs.db.report.ReportDao
import com.sribs.db.user.UserBean
import com.sribs.db.user.UserDao
import com.sribs.db.v3.project.v3ProjectDao
import com.sribs.db.v3.project.v3ProjectRoom

/**
 * @date 2021/7/8
 * @author elijah
 * @Description
 */
//@Database(entities = [
//    ProjectBean::class,
//    UnitBean::class,
//    ConfigBean::class,
//    HouseStatusBean::class,
//    RoomStatusBean::class,
//    RoomDetailBean::class,
//    ReportBean::class,
//    UserBean::class,
//    LeaderBean::class,
//    InspectorBean::class,
//],version = 1,exportSchema = false)
//@TypeConverters(com.sribs.db.Converters::class)
//abstract class DDBDataBase: RoomDatabase() {
//    abstract fun projectDao(): ProjectDao
//    abstract fun unitDao():UnitDao
//    abstract fun configDao():ConfigDao
//    abstract fun houseStatusDao():HouseStatusDao
//    abstract fun roomStatusDao():RoomStatusDao
//    abstract fun roomDetailDao():RoomDetailDao
//    abstract fun reportDao():ReportDao
//    abstract fun userDao():UserDao
//    abstract fun leaderDao():LeaderDao
//    abstract fun inspectorDao():InspectorDao
//}

@SuppressLint("RestrictedApi")
@Database(
    entities = [
        ProjectBean::class,
        UnitBean::class,
        ConfigBean::class,
        HouseStatusBean::class,
        RoomStatusBean::class,
        RoomDetailBean::class,
        ReportBean::class,
        UserBean::class,
        LeaderBean::class,
        InspectorBean::class,
        BuildingBean::class,
        DrawingBean::class,
        FloorBean::class,
        DamageBean::class,
        //3期
        v3ProjectRoom::class
    ],
    version = 1,
    exportSchema = false,
//    autoMigrations = [AutoMigration (from = 1,to = 2)]
    )
@TypeConverters(com.sribs.db.Converters::class)
abstract class DDBDataBase: RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun unitDao():UnitDao
    abstract fun configDao():ConfigDao
    abstract fun houseStatusDao():HouseStatusDao
    abstract fun roomStatusDao():RoomStatusDao
    abstract fun roomDetailDao():RoomDetailDao
    abstract fun reportDao():ReportDao
    abstract fun userDao():UserDao
    abstract fun leaderDao():LeaderDao
    abstract fun inspectorDao():InspectorDao
    abstract fun buildingDao(): BuildingDao
    abstract fun floorDao(): FloorDao
    abstract fun drawingDao(): DrawingDao
    abstract fun damageDao(): DamageDao
    //3期
    abstract fun v3ProjectDao(): v3ProjectDao
}