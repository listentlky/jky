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
class BuildingDamageSubPresenter :BasePresenter(), IBuildingContrast.IBuildingPresenter{
    private var mView:IBuildingContrast.IBuildingView?=null

    override fun getLocalBuilding(projectId: Long) {

    }

    override fun createLocalBuilding(proId: Long, bb: Building) {

    }

    override fun getLocalDrawingListInBuilding(proId: Long, bldId: Long) {

    }

    override fun getLocalDamageDetail(dmg: DamageBean?) {

    }

    override fun removeDamageInDrawing(dmg: DamageBean) {

    }

    override fun bindView(v: IBaseView) {
        mView = v as IBuildingContrast.IBuildingView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}