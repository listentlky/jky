package com.sribs.bdd.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.sribs.bdd.bean.BuildingDrawingsBean
import com.sribs.bdd.bean.data.ProjectConfigBean
import com.sribs.bdd.ui.project.IConfigListener
import com.sribs.common.bean.db.ConfigBean
import com.sribs.common.server.IDatabaseService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * @date 2021/7/28
 * @author elijah
 * @Description
 */
abstract class BaseConfigLayout:LinearLayout {
    protected var mCompositeDisposable: CompositeDisposable?=null

    protected fun addDisposable(subscription: Disposable){
        if (mCompositeDisposable?.isDisposed != false){
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable?.add(subscription)
    }

    protected fun dispose() = mCompositeDisposable?.dispose()

    constructor(context: Context) : super(context) {
        initView()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    var mData: ProjectConfigBean?=null

    var mCb: IConfigListener?=null

    var unitId:Long = -1
        set(value){
            if (value == field)return
            field = value
            getUnitConfig(value)
            onUnitId(value)
        }

    var bCopy:Boolean = false
        set(value) {
            if (value != field){
                field = value
            }
            copyBtnView()?.isEnabled = if(bHasData) value else false
        }

    var bHasData = false
        set(value) {
            if (value == field)return
            field = value
            hasDataView()?.isSelected = value
            publicFloorView()?.isSelected = value
        }

    var configId:Long = -1L
        set(value){
            if (value == field)return
            field = value
            if (value>0){
                bHasData = true
                bCopy = true
            }else{
                bHasData = false
                bCopy = false
            }
        }

    var mConfigList:List<ConfigBean>?=null

    private fun getUnitConfig(unitId:Long?){
        if (unitId?:-1>0){
            dispose()
            val srv = ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService
            addDisposable(srv.getUnitConfig(unitId!!)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    LOG.I("123","unitConfig =$it")
                    mConfigList = it
                    findConfigId()
                },{
                    it.printStackTrace()
                }))
        }
    }

    protected fun findConfigId(){
        if (mConfigList.isNullOrEmpty())return
        var config = mConfigList!!.find { configFilter(it) }?:return
        var id = config.configId?:return
        configId = id!!
        onFloorNum(config.floorNum?:"")
    }

    open fun remove(){
        if (configId>0){
            val srv = ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService
            addDisposable(srv.deleteConfig(listOf(configId))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    dispose()
                },{
                    dispose()
                    it.printStackTrace()
                }))
        }else{
            dispose()
        }
    }

    abstract fun initView()
    abstract fun copyBtnView(): View?
    abstract fun hasDataView(): View?
    abstract fun publicFloorView():View?
    abstract fun configFilter(b:ConfigBean):Boolean
    open fun onUnitId(unitId:Long){}
    open fun onFloorNum(floorNum:String){}

    var mDrawingsList:List<BuildingDrawingsBean>?=null
}