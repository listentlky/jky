package com.sribs.bdd.ui.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.cbj.sdk.libbase.utils.LOG
import com.sribs.bdd.bean.data.ProjectConfigBean
import com.sribs.bdd.bean.UnitConfigType
import com.sribs.bdd.databinding.LayoutUnitTableNeighborBinding
import com.sribs.bdd.ui.project.IConfigListener
import com.sribs.common.bean.db.ConfigBean
import com.sribs.common.utils.Util

/**
 * @date 2021/7/2
 * @author elijah
 * @Description
 */
class UnitNeighborLayout:BaseConfigLayout {

    private var mBinding: LayoutUnitTableNeighborBinding?=null

    var mFloorNumber = ""

    var mNeighborNumber = ""

    var mFloorIdx:Int = -1

    var mNeighborIdx:Int = -1


    constructor(context: Context,floorNumber:String,neighborNumber:String,cb:IConfigListener):super(context){
        mFloorNumber = Util.formatNoZeroNum(floorNumber)!!
        mNeighborNumber = neighborNumber
        mBinding?.unitNeighborName?.text = mFloorNumber+mNeighborNumber
        mBinding?.unitNeighborConfig?.setOnClickListener {
            mCb?.onConfigClick(
                mFloorNumber,
                mNeighborNumber,
                "$mFloorNumber"+"$mNeighborNumber"+"室",
                getConfigData()
            )
        }
        mBinding?.unitNeighborCopy?.setOnClickListener {
            mCb?.onCopyClick(
                "$mFloorNumber"+"$mNeighborNumber"+"室",
                getConfigData()
            )
        }
        mCb = cb
        bCopy = false
        mBinding?.unitNeighborCopy?.isEnabled = false
    }


    fun getConfigData():ProjectConfigBean?{
        mData?.configId = configId
        mData?.floorIdx = mFloorIdx
        mData?.neighborIdx = mNeighborIdx
        mData?.floorNum = mFloorNumber
        mData?.neighborNum = mNeighborNumber
        return mData
    }

    fun initData(floor:String){
        when(floor){
            "底层"->{
                if (mData==null) mData = ProjectConfigBean(UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM.value)
                else if(mData!!.configType != UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM.value)
                    LOG.E("123","iniData error mData!=null && type!=bottom")
            }
            "顶层"->{
                if (mData==null) mData = ProjectConfigBean(UnitConfigType.CONFIG_TYPE_UNIT_TOP.value)
                else if(mData!!.configType != UnitConfigType.CONFIG_TYPE_UNIT_TOP.value)
                    LOG.E("123","iniData error mData!=null && type!=top")
            }
            "标准层"->{
                if (mData==null) mData = ProjectConfigBean(UnitConfigType.CONFIG_TYPE_UNIT_NORMAL.value)
                else if(mData!!.configType != UnitConfigType.CONFIG_TYPE_UNIT_NORMAL.value)
                    LOG.E("123","iniData error mData!=null && type!=normal")
            }
        }
    }



    fun updateFloorNumber(floorNumber:String){

        this.mFloorNumber = Util.formatNoZeroNum(floorNumber)!!

        mBinding?.unitNeighborName?.text = this.mFloorNumber+mNeighborNumber
    }

    fun updateIndex(floorIdx:Int,neighborIdx:Int){
        mFloorIdx = floorIdx
        mNeighborIdx = neighborIdx
        findConfigId()
    }

    override fun initView() {
        mBinding = LayoutUnitTableNeighborBinding.inflate(LayoutInflater.from(context),this,true)
    }

    override fun copyBtnView(): View? =  mBinding?.unitNeighborCopy

    override fun hasDataView(): View? = mBinding?.unitNeighborName

    override fun publicFloorView(): View? = null

    override fun configFilter(b: ConfigBean): Boolean
        = b.floorIdx == mFloorIdx && b.neighborIdx == mNeighborIdx
}