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
class BuildingDamagePresenter :BasePresenter(), IBuildingContrast.IBuildingPresenter{
    private var mView:IBuildingContrast.IBuildingView?=null

    private var mBean: ProjectBean?=null

    private var mProId: Long?=-1
    private var mLocalBldId: Long?=-1
    private var mAppFloorList: ArrayList<Floor>? = null
    private var mAppDrawingList: ArrayList<com.sribs.common.bean.db.DrawingBean>? = null

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    override fun getLocalBuilding(projectId: Long) {
        mProId = projectId
        println("leon getLocalBuilding projectId=${projectId}")
        addDisposable( mDb.getBuildingIdByProjectId(projectId?:-1)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                mLocalBldId = it
                mDb.getLocalFloorsInTheBuilding(mLocalBldId?:-1).toObservable()
             }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                println("leon getLocalBuilding floorlist=${it.toString()}")
                var floor: Floor? = null
                if(it != null) {
                    mAppFloorList = ArrayList<Floor>()
                    for (item in it) {
                        floor = Floor(
                            item.id,
                            item.bldId,
                            item.projectId,
                            item.floorId,
                            item.floorName
                        )
                        println("leon getLocalBuilding floorName=${item.floorName}")
                        mAppFloorList?.add(floor!!)
                    }
                }

                if(mAppFloorList != null)
                    mView?.initBuildingFloors(mLocalBldId!!, mAppFloorList!!)

                println("leon projectId=${projectId} mLocalBldId=${mLocalBldId}")
                mDb.getLocalDrawingListInBuilding(projectId!!, mLocalBldId!!).toObservable()
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({
                if(it!=null)
                    mView?.initBuildingDrawing(it.toCollection(ArrayList<com.sribs.common.bean.db.DrawingBean>()))
            },{

            }))
    }

    public fun getAllDamagesInDrawing(dw:com.sribs.common.bean.db.DrawingBean){
        println("leon getAllDamagesInDrawing dw=${dw.toString()}")
        addDisposable( mDb.getLocalDamageListInDrawing(dw.id!!)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                if(it != null)
                    mView?.initDrawingDamages(it)
            },{
                it.printStackTrace()
            }))

    }

    public fun saveDamageDataToDb(dmgList: ArrayList<com.sribs.common.bean.db.DamageBean>?){
        println("leon saveDamageDataToDb in dmgList = " + dmgList.toString())
        var saveStart:Boolean = false
        var times:Int = 0

        dmgList?.run {
            addDisposable(Observable.fromIterable(dmgList)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMap{
                    println("leon saveDamageDataToDb damageBean=${it.toString()}")
                    if(it.id < 0) {
                        mDb.createDamageInDrawing(it)
                    }
                    else{
                        mDb.updateDamageInDrawing(it)
                    }
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(){
                    times++
                    println("leon times=${times}")

                    mView?.saveDamageDataToDbStarted()

                }
            )
        }
    }

    override fun createLocalBuilding(proId: Long, bb: Building) {

    }

    override fun getLocalDrawingListInBuilding(proId:Long, bldId: Long) {
        println("leon getLocalBuildingDrawingList projectId=${proId} bldId=${bldId}")

    }

    override fun getLocalDamageDetail(dmg: DamageBean?) {
        dmg?.run{
            mView?.updateLocalDamageDetail(dmg)
        }
    }

    override fun removeDamageInDrawing(dmg: DamageBean) {
        LOG.I("leon","removeDamageInDrawing dmg.id=${dmg.id}")
        addDisposable(mDb.removeDamageInDrawing(dmg.id)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("leon","removeDamageInDrawing return = $it")
                if(it)
                    mView?.onRemoveDamageInDrawing(dmg)
            },{
                it.printStackTrace()
            }))

    }

    override fun bindView(v: IBaseView) {
        mView = v as IBuildingContrast.IBuildingView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}