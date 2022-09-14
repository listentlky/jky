package com.sribs.bdd.ui.house

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.R
import com.sribs.bdd.bean.data.HouseStatusDataBean
import com.sribs.bdd.databinding.ActivityHouseStatusBinding
import com.sribs.bdd.module.house.HouseStatusPresenter
import com.sribs.bdd.module.house.IHouseContrast
import com.sribs.bdd.module.house.RoomStatusPresenter
import com.sribs.common.bean.db.HouseStatusBean
import com.sribs.common.bean.db.RoomStatusBean
import com.sribs.common.databinding.LayoutCommonToolbarBinding
import java.text.SimpleDateFormat

/**
 * @date 2021/7/14
 * @author elijah
 * @Description
 */
@Route(path= com.sribs.common.ARouterPath.HOUSE_STATUS_ATY)
class HouseStatusActivity :BaseActivity(),IHouseContrast.IHouseStatusView,IHouseContrast.IRoomView{

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_PROJECT_ID)
    var mProjectId:Long = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_ID)
    var mUnitId:Long = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_ID)
    var mConfigId:Long = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_HOUSE_TYPE)
    var mHouseType = -1

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_HOUSE_NAME)
    var mHouseName = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_HOUSE_POS)
    var mHousePos = ""


    val mStat1PosArr by lazy { resources.getStringArray(R.array.house_status1_pos) }

    var mData :HouseStatusDataBean?=null

    private val mHousePresenter by lazy { HouseStatusPresenter() }

    private val mRoomPresenter by lazy { RoomStatusPresenter() }


    private val mBinding :ActivityHouseStatusBinding by inflate()

    private val mToolbarBinding by lazy {
        LayoutCommonToolbarBinding.bind(mBinding.toolbar.root)
    }

    override fun deinitView() {
        unbindPresenter()
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        bindPresenter()
        initToolbar()
        when {
            mHousePos == "房屋" -> {
                var arr = resources.getStringArray(R.array.house_house_status)
                mBinding.houseNowStatus.showCustom(true,"其他")
                mBinding.houseNowStatus.setDefaultConfigArr(ArrayList(arr.toList()))
                mBinding.houseUsedLl.visibility = View.GONE
            }
            mStat1PosArr.indexOf(mHousePos)>=0 -> {
                var arr = resources.getStringArray(R.array.house_position_status1)
                mBinding.houseNowStatus.setDefaultConfigArr(ArrayList(arr.toList()))
                mBinding.houseUsedLl.visibility = View.VISIBLE
            }
            else -> {
                var arr = resources.getStringArray(R.array.house_position_status2)
                mBinding.houseNowStatus.setDefaultConfigArr(ArrayList(arr.toList()))
                mBinding.houseUsedLl.visibility = View.VISIBLE
            }
        }
        mBinding.houseNowStatus.setCheckTagText("${mHousePos}现状")
        mBinding.houseDecorationDate.visibility = View.GONE

        mData = HouseStatusDataBean().also {
            it.bindTagCheckView(it::status,mBinding.houseNowStatus)
            it.bindTagEditView(it::date,mBinding.houseDecorationDate)
            it.bindEditText(it::note,mBinding.houseUsed)
            it.bindStatus { arr->
                if (arr.isEmpty())return@bindStatus
                mBinding.houseDecorationDate.visibility =
                    if(arr[0].split(",").contains("新装修")) View.VISIBLE
                    else View.GONE


            }
            it.setOnDataChangedListener{ name,new->
                LOG.I("123","onDataChange  updateStatus  name=$name  new=$new")
                updateStatus()
            }
        }
        initData()
    }

    private fun updateStatus(){
        if (mHousePos=="房屋"){
            mHousePresenter.updateHouseStatus(
                mProjectId, mUnitId, mConfigId,
                mHouseName, mHouseType, mData?.status?:"", mData?.date
            )

        } else {
            mRoomPresenter.updateRoomStatus(
                mProjectId, mUnitId, mConfigId,
                mHousePos, mData?.status?:"", mData?.date, mData?.note
            ){}
            mHousePresenter.updateHouseInspector(mProjectId, mUnitId, mConfigId, mHouseName, mHouseType)
        }
    }

    private fun initToolbar(){
        mToolbarBinding.tb.setNavigationIcon(R.mipmap.icon_back)
        mToolbarBinding.tb.setNavigationOnClickListener {
            finish()
//            com.sribs.common.utils.DialogUtil.showMsgDialog(this,"保存当前现状",{
//                finish()
//            }){
//                finish()
//            }

        }
        if (mHousePos!="房屋"){
            mToolbarBinding.tbTitle.text = "${mHousePos}现状"
        }else{
            mToolbarBinding.tbTitle.text = "${mHouseName}现状"
        }

    }

    private fun initData(){
        if (mHousePos == "房屋"){
            mHousePresenter.getHouseStatus(mConfigId)
        } else {
//            mHousePresenter.getHouseStatus(mConfigId)
            mRoomPresenter.getRoomStatus(mConfigId,mHousePos)
        }
    }



    override fun onHouseStatus(l: ArrayList<HouseStatusBean>) {
        if (mHousePos!="房屋")return
        if (l.isEmpty())return
        var b = l[0]
        mData?.status = b.houseStatus?:""
        if (b.houseFurnishTime!=null){
            mData?.date = SimpleDateFormat("yyyy-MM").format(b.houseFurnishTime)
        }
    }

    override fun onRoomStatus(l: ArrayList<RoomStatusBean>) {
        if (mHousePos=="房屋")return
        if (l.isEmpty())return
        var b = l[0]
        mData?.status = b.roomStatus?:""
        if (b.roomFurnishTime!=null){
            mData?.date =  SimpleDateFormat("yyyy-MM").format(b.roomFurnishTime)
        }
        if (!b.roomNote.isNullOrEmpty()){
            mData?.note = b.roomNote!!

        }
    }

    override fun bindPresenter() {
        mHousePresenter.bindView(this)
        mRoomPresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg)
    }

    override fun unbindPresenter() {
        mHousePresenter.unbindView()
        mRoomPresenter.unbindView()
    }
}