package com.sribs.bdd.module.building

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.exception.MsgThrowable
import com.cbj.sdk.libbase.utils.LOG
import com.sribs.common.module.BasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.action.Config
import com.sribs.bdd.bean.Building
import com.sribs.bdd.bean.Drawing
import com.sribs.bdd.bean.Floor
import com.sribs.bdd.bean.ProjectBean
import com.sribs.bdd.module.project.IProjectContrast
import com.sribs.bdd.utils.DescriptionPositionHelper.floor
import com.sribs.common.bean.db.DamageBean
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.math.NumberUtils

import com.sribs.common.server.IDatabaseService
import com.sribs.db.project.building.BuildingBean
import com.sribs.db.project.drawing.DrawingBean
import com.sribs.db.project.floor.FloorBean
import io.reactivex.ObservableSource
import java.sql.Date
import java.time.LocalDateTime

/**
 * @date 2022/3/28
 * @author leon
 * @Description 独栋建筑损伤列表
 */
class BuildingDamageMainPresenter :BasePresenter(), IBuildingContrast.IBuildingPresenter{
    private var mView:IProjectContrast.IView?=null

    private var mBean: ProjectBean?=null

    private var mProId: Long?=-1
    private var mBldId: Long?=-1
    private var mAppFloorList: ArrayList<Floor>? = null
    private var mAppFacadeDrawingList: ArrayList<Drawing>? = null

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    fun updateLocalProject(pb:com.sribs.bdd.bean.ProjectBean){

        if(pb != null)
        {
            pb.projectId?.let{
                mProId = pb.projectId!!
                var curTime: Date = Date(java.util.Date().time)
                var appBldList: ArrayList<Building>? = null
                var dbBldList:ArrayList<com.sribs.common.bean.db.BuildingBean>? = null
                var bb: Building? = null
                if(pb.buildingList != null) {
                    appBldList = pb.buildingList
                    var appBld: Building? = null
                    if (appBldList != null) {
                        for(i in 0..appBldList.size-1){
                            appBld = appBldList.get(i)
                            if(appBld.buildingId!! > 0) {//existed building
                                println("leon found existed building, bldId=${appBld.buildingId}")
                                continue
                            }

                            bb = appBld
                            println("leon create new building, building=${appBld.toString()}")

                        }
                    }
                }

                if (bb != null) {
                    createLocalBuilding(mProId!!, bb)
                    mAppFloorList = bb.bldUnitFloorList as ArrayList<Floor>
                    mAppFacadeDrawingList = bb.drawingsList
                    println("leon mAppFloorList.size=${mAppFloorList?.size}")

                }
                else
                {
                    println("leon updateLocalProject new building not existed!")
                }
            }
        }
        else
        {
            return
        }

    }

    override fun getLocalBuilding(projectId: Long) {

    }

    override fun createLocalBuilding(proId: Long, bb: Building) {

        println("leon createLocalBuilding mBldId=${mBldId}")
        mDb.getLocalBuildingOnce(mBldId?:-1).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                var curTime: Long = System.currentTimeMillis()
                var dbBldBean: com.sribs.common.bean.db.BuildingBean? = null
                dbBldBean = com.sribs.common.bean.db.BuildingBean(
                    -1,
                    proId,
                    bb.buildingName,
                    bb.bldType,
                    curTime,
                    curTime,
                    0L,
                    "",
                    "",
                    1,
                    "",
                    0
                )
                mDb.createLocalBuilding(dbBldBean)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mBldId = it.toLong()
                println("leon createLocalBuilding new building id=${mBldId}")
                //cache floors info to sqlite
                createLocalFloorsInTheBuilding()
                //cache building drawings info to sqlite
                createLocalFacadesDrawingInTheBuilding()
            },{
                it.printStackTrace()
            })
    }

    override fun getLocalDrawingListInBuilding(proId: Long, bldId: Long) {

    }

    override fun getLocalDamageDetail(dmg: DamageBean?) {

    }

    override fun removeDamageInDrawing(dmg: DamageBean) {

    }

    private fun createLocalFloorsInTheBuilding(){
        println("leon createLocalFloorsInTheBuilding mBldId=${mBldId}")
        var curTime: Long = System.currentTimeMillis()
        if(mAppFloorList != null){
            var floorId:Long = -1
            var floorName:String? = null
            var appFloorDrawingList: ArrayList<Drawing>? = null
            addDisposable(Observable.fromIterable(mAppFloorList)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMap{
                    floorId = it.floorId?.toLong() ?: 0
                    floorName = it.floorName
                    println("leon 00 floorid=${floorId}, floorName=${floorName}")
                    var appFloor:com.sribs.common.bean.db.FloorBean = com.sribs.common.bean.db.FloorBean(
                        -1,
                        mProId,
                        mBldId,
                        -1,
                        it.floorId?.toLong(),
                        it.floorName,
                        it.createTime,
                        it.createTime,
                        0L,
                        "",
                        1,
                        "",
                        0
                    )
                    appFloorDrawingList = it.drawingsList
                    mDb.createLocalFloor(appFloor)
                }
                .observeOn(Schedulers.computation())
                .flatMap{

                    var floor:Floor? = null
                    for(i in 0..mAppFloorList?.size?.minus(1)!!){
                        floor = mAppFloorList?.get(i)
                        if(floor?.id!! <0){
                            floor.id = it
                            break
                        }
                    }

                    var drawingList: ArrayList<Drawing>? = null
                    if(floor != null){
                        if(floor.drawingsList != null){
                            drawingList = floor.drawingsList
                        }
                    }

                    Observable.fromIterable(drawingList)
                }
                .observeOn(Schedulers.computation())
                .flatMap{
                    println("leon 11 floorid=${floorId}, floorName=${floorName}")
                    var drawing:com.sribs.common.bean.db.DrawingBean = com.sribs.common.bean.db.DrawingBean(
                        -1,
                        mProId,
                        mBldId,
                        -1,
                        it.floorId,
                        it.floorName,
                        it.fileName,
                        it.drawingType,
                        it.fileType,
                        it.cacheAbsPath,
                        "",
                        it.createTime?:curTime,
                        it.updateTime?:curTime,
                        0,
                        "",
                        "",
                        1,
                        0
                    )
                    mDb.createLocalDrawing(drawing)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    print("leon createLocalFloorsInTheBuilding ret drawing id=$it")
                },{
                    it.printStackTrace()
                }))
        }
    }

    private fun createLocalFacadesDrawingInTheBuilding(){
        println("leon createLocalFloorsInTheBuilding mBldId=${mBldId}")
        var curTime: Long = System.currentTimeMillis()
        if(mAppFloorList != null){
            var floorId:Long = -1
            var floorName:String? = null
            var appFloorDrawingList: ArrayList<Drawing>? = null
            addDisposable(Observable.fromIterable(mAppFacadeDrawingList)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMap{it ->
                    println("leon ")
                    var drawing:com.sribs.common.bean.db.DrawingBean = com.sribs.common.bean.db.DrawingBean(
                        -1,
                        mProId,
                        mBldId,
                        -1,
                        it.floorId,
                        it.floorName,
                        it.fileName,
                        it.drawingType,
                        it.fileType,
                        it.cacheAbsPath,
                        "",
                        it.createTime?:curTime,
                        it.updateTime?:curTime,
                        0L,
                        "",
                        "",
                        1,
                        0
                    )
                    mDb.createLocalDrawing(drawing)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    print("leon createLocalFloorsInTheBuilding ret drawing id=$it")
                },{
                    it.printStackTrace()
                }))
        }
    }

    override fun bindView(v: IBaseView) {
        mView = v as IProjectContrast.IView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}