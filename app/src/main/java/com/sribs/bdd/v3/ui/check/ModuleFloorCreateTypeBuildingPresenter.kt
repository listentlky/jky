package com.sribs.bdd.v3.ui.check

import android.content.Context
import android.content.SharedPreferences
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.moudles.IBasePresenter
import com.sribs.common.module.BasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView

import com.sribs.bdd.bean.*
import com.sribs.bdd.bean.data.ModuleFloorBean
import com.sribs.bdd.bean.data.ModuleFloorPictureBean
import com.sribs.bdd.module.project.IProjectContrast
import com.sribs.bdd.v3.adapter.CreateModuleFloorPictureAdapter
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.common.bean.db.DrawingV3Bean
import com.sribs.common.bean.db.v3.project.v3BuildingModuleDbBean
import com.sribs.common.server.IDatabaseService
import io.reactivex.schedulers.Schedulers
import kotlin.collections.ArrayList

class ModuleFloorCreateTypeBuildingPresenter : BasePresenter(), IBasePresenter {
    private var mView: IProjectContrast.IModuleCreateTypeBuildingView? = null

    private var array: ArrayList<ModuleFloorBean>? = null

    private var picList: ArrayList<ModuleFloorPictureBean>? = null

    private var mProLeader: String? = ""

    private val picAdapter by lazy {
        CreateModuleFloorPictureAdapter()
    }

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }


    fun initPicLits() {
        var manager = LinearLayoutManager(mView?.getContext())
        mView?.getPicRecycleView()?.layoutManager = manager
        picList = ArrayList()
        picAdapter.setData(picList!!)
        mView?.getPicRecycleView()?.adapter = picAdapter

    }

    fun refreshPicList(mData: ArrayList<ModuleFloorPictureBean>) {
        picList?.addAll(mData)
        picAdapter.notifyDataSetChanged()

    }

    private var mBldId: Long? = -1

    fun createLocalModule(
        mModuleId: Long,
    ) {
        var bean = v3BuildingModuleDbBean(
            drawings = listOf("nihao222"),
            id = mModuleId
            )

        addDisposable(mDb.updatev3BuildingModuleOneData(bean)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe({
                mView?.onMsg("success")
                mView?.createModuleConfigSuccess()

            }, {
                mView?.onMsg("gg")
                it.printStackTrace()
            })
        )
    }

    private var mAppFacadeDrawingList: ArrayList<DrawingV3Bean>? = ArrayList<DrawingV3Bean>()


    var floorList: ArrayList<Floor> = ArrayList<Floor>()





    private lateinit var mPrefs: SharedPreferences
    private var mCurDrawingsDir: String? = ""
    private var mProName: String? = ""
    private var mBldNo: String? = ""

    override fun bindView(v: IBaseView) {
        mView = v as IProjectContrast.IModuleCreateTypeBuildingView
        mPrefs = mView!!.getContext()!!.getSharedPreferences("createProject", Context.MODE_PRIVATE)
        mProLeader = mPrefs.getString(ModuleHelper.CUR_PRO_LEADER, "")
        mProName = mPrefs.getString(ModuleHelper.CUR_PRO_NAME, "")
        mBldNo = mPrefs.getString(ModuleHelper.CUR_BLD_NO, "")
        mCurDrawingsDir =
            "/" + ModuleHelper.DRAWING_CACHE_FOLDER + "/" + mProName + "/" + mBldNo + "/"
        initPicLits()
    }

    override fun unbindView() {
        mView = null
    }


}