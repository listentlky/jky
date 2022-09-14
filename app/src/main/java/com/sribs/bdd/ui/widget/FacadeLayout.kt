package com.sribs.bdd.ui.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.cbj.sdk.libbase.utils.LOG
import com.sribs.bdd.bean.UnitConfigType
import com.sribs.bdd.bean.data.ProjectConfigBean
import com.sribs.bdd.databinding.ItemFloorFacadeTableBinding
import com.sribs.bdd.ui.project.IConfigListener
import com.sribs.common.bean.db.ConfigBean

/**
 * @date 2022/3/5
 * @author elijah
 * @Description
 */
class FacadeLayout:BaseConfigLayout {

    private var mBinding: ItemFloorFacadeTableBinding?=null

    override fun onFloorNum(floorNum: String) {
        super.onFloorNum(floorNum)
        mFloorNum = floorNum
    }

    var mFloorIdx:Int = -1
        set(value){
            if (value == field)return
            field = value
            mNeighborList?.forEachIndexed { index, it ->
                it.updateIndex(value,index)
            }
            findConfigId()
        }

    //层号
    var mFloorNum:String = ""

    var mFloorStr:String = ""

    override fun initView() {
        mBinding = ItemFloorFacadeTableBinding.inflate(LayoutInflater.from(context),this,true)
    }

    override fun copyBtnView(): View? = null

    override fun hasDataView(): View? = null

    override fun publicFloorView(): View? = null

    override fun configFilter(b: ConfigBean): Boolean
        = b.floorIdx == mFloorIdx && b.neighborIdx==null

    var mNeighborList:ArrayList<UnitNeighborLayout> = ArrayList()

    private fun initData(floor:String){
        when(floor){
            "底层"->{
                if (mData==null) mData = ProjectConfigBean(UnitConfigType.CONFIG_TYPE_FLOOR_BOTTOM.value)
                else if (mData!!.configType != UnitConfigType.CONFIG_TYPE_FLOOR_BOTTOM.value)
                    LOG.E("123","initData error mData!=null && type!=bottom")
            }
            "顶层"->{
                if (mData==null) mData = ProjectConfigBean(UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value)
                else if(mData!!.configType!=UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value )
                    LOG.E("123","initData error mData!=null && type!=top")
            }
            "标准层"->{
                if (mData==null) mData = ProjectConfigBean(UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL.value)
                else if(mData!!.configType != UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL.value)
                    LOG.E("123","initData error mData!=null && type!=normal")
            }
        }
        mNeighborList?.forEach {
            it.initData(floor)
        }
    }

    constructor(c:Context,parentHashCode:String,number:String,floor:String,bCopy:Boolean,neighborSize:Int,cb:IConfigListener?=null) : super(c) {
        this.mFloorNum = number
        this.bCopy = bCopy// and has Data 与数据
        this.mFloorStr = floor
        this.mCb = cb

        mBinding?.tvFacade?.setOnClickListener {
            LOG.I("123","FacadeLayout click mCb=$mCb")

        }
    }
}