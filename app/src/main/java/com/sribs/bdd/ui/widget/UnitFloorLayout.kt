package com.sribs.bdd.ui.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.cbj.sdk.libbase.utils.LOG
import com.sribs.bdd.bean.UnitConfigType
import com.sribs.bdd.bean.data.ProjectConfigBean
import com.sribs.bdd.databinding.LayoutUnitTableFloorBinding
import com.sribs.bdd.ui.project.IConfigListener
import com.sribs.common.bean.db.ConfigBean
import com.sribs.common.utils.Util

/**
 * @date 2021/7/1
 * @author elijah
 * @Description
 */
class UnitFloorLayout:BaseConfigLayout {

    private var mBinding: LayoutUnitTableFloorBinding?=null

    override fun onUnitId(unitId: Long) {
        super.onUnitId(unitId)
        mNeighborList.forEach {
            it.unitId = unitId
        }
    }

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
        set(value) {
//            var v = Util.formatZeroNum(value)?:return
            var v = Util.formatNoZeroNum(value)?:return
            if (v == field)return
            field = v
            mBinding?.unitFloorNumber?.text = v
            mNeighborList?.forEach {
                it.updateFloorNumber(value)
            }
        }
    var mFloorStr:String = ""
        set(value) {
            if (value == field)return
            field = value
            mBinding?.unitFloorName?.text = mFloorStr
            initData(value)
        }

    override fun initView() {
        mBinding = LayoutUnitTableFloorBinding.inflate(LayoutInflater.from(context),this,true)
    }

    override fun copyBtnView(): View? = mBinding?.unitFloorCopy

    override fun hasDataView(): View? = mBinding?.unitFloorName

    override fun publicFloorView(): View? = mBinding?.unitFloorPublic

    override fun configFilter(b: ConfigBean): Boolean
        = b.floorIdx == mFloorIdx && b.neighborIdx==null

    var neighborSize = 0
        set(value) {
            if (value==field)return
            field = value
            changeNeighbor(value)
        }

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

//    constructor(context: Context) : super(context) {
//        mBinding = LayoutUnitTableFloorBinding.inflate(LayoutInflater.from(context),this,true)
//    }
//    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
//        mBinding = LayoutUnitTableFloorBinding.inflate(LayoutInflater.from(context),null,false)
//
//    }
    constructor(c:Context,parentHashCode:String,number:String,floor:String,bCopy:Boolean,neighborSize:Int,cb:IConfigListener?=null) : super(c) {
        this.mFloorNum = number
        this.bCopy = bCopy// and has Data 与数据
        this.mFloorStr = floor
        this.neighborSize = neighborSize
        this.mCb = cb

        mBinding?.unitFloorConfig?.setOnClickListener {
            LOG.I("123","unitFloorConfig click  mCb=$mCb")
            mCb?.onConfigClick(
                this.mFloorNum,
                null,
                "第"+this.mFloorNum+"层",
                getConfigData()
            )
        }
        mBinding?.unitFloorCopy?.setOnClickListener {

            mCb?.onCopyClick("第"+this.mFloorNum+"层",  getConfigData())
        }
    }

    fun getConfigData():ProjectConfigBean?{
        mData?.configId = configId
        mData?.floorIdx = mFloorIdx
        mData?.neighborIdx = -1
//        mData?.floorNum =Util.formatZeroNum(mFloorNum)
        mData?.floorNum =Util.formatNoZeroNum(mFloorNum)
        mData?.neighborNum = null
        return mData
    }

    private fun changeNeighbor(num:Int){
        val parent = mBinding?.unitFloorNeighborLl
        val size = mNeighborList.size
        when{
            num>size->{ // 添加
                for (i in size+1..num ){
                    val neighborLayout = UnitNeighborLayout(mBinding!!.root.context,mFloorNum,String.format("%02d",i),
                        object :IConfigListener{
                            override fun onConfigClick(
                                floorNum: String,
                                neighborNum: String?,
                                des: String,
                                dataBean: ProjectConfigBean?,

                                ) {
                                mCb?.onConfigClick(
                                    floorNum,
                                    neighborNum,
                                    des,
                                    dataBean,
                                )
                            }

                            override fun onCopyClick(
                                des: String,
                                dataBean: ProjectConfigBean?,
                            ) {
                                mCb?.onCopyClick( des, dataBean)
                            }

                            override fun onDrawingsChangeClick(floorId: String) {
                                TODO("Not yet implemented")
                            }

                        }).also {
                        it.initData(mFloorStr)
                        it.updateIndex(mFloorIdx,i-1)
                        it.unitId = unitId
//                        it.updateFloorNumber(String.format("%02d",number.toInt()))
                    }

                    var p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                    parent?.addView(neighborLayout,p)

                    mNeighborList.add(neighborLayout)
                }
            }
            num<size->{ //删除
                for (idx in size-1 downTo num){
                    var floor = mNeighborList.removeAt(idx)
                    floor.remove()
                    parent?.removeView(floor)
                }
            }
        }
    }

    override fun remove(){
        super.remove()
        mNeighborList.forEach {
            it.remove()
        }
    }

}