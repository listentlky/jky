package com.sribs.bdd.ui.house

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.databinding.LayoutBaseListMvpBinding
import com.cbj.sdk.libui.mvp.BaseListActivity
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.R
import com.sribs.bdd.bean.HouseConfigItemBean
import com.sribs.bdd.bean.RoomItemBean
import com.sribs.bdd.bean.UnitConfigType
import com.sribs.bdd.databinding.ActivityDamageListBinding
import com.sribs.bdd.databinding.ItemHouseConfigListBinding
import com.sribs.bdd.module.house.HouseListPresenter
import com.sribs.bdd.module.house.HouseStatusPresenter
import com.sribs.bdd.module.house.IHouseContrast
import com.sribs.bdd.module.house.RoomStatusPresenter
import com.sribs.bdd.ui.adapter.HouseListAdapter
import com.sribs.common.bean.db.HouseStatusBean
import com.sribs.common.bean.db.RoomStatusBean
import com.sribs.common.utils.DialogUtil

/**
 * @date 2021/7/14
 * @author elijah
 * @Description
 */
@Route(path = com.sribs.common.ARouterPath.HOUSE_DAMAGE_LIST_ATY)
class HouseDamageListActivity :BaseListActivity<HouseConfigItemBean, ItemHouseConfigListBinding>()
    ,IHouseContrast.IHouseView ,IHouseContrast.IRoomView,IHouseContrast.IHouseStatusView{

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_PROJECT_ID)
    var mProjectId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_ID)
    var mUnitId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_HOUSE_TYPE)
    var mHouseType = -1

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_PART_NO)
    var mPartNo= ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_ID)
    var mLocalConfigId = -1L

    private val mBinding: ActivityDamageListBinding by inflate()


    private val mRvBinding by lazy { LayoutBaseListMvpBinding.bind(mBinding.listLl) }


    private val mPresenter by lazy { HouseListPresenter() }

    private val mRoomPresenter by lazy { RoomStatusPresenter() }

    private val mHouseStatusPresenter by lazy { HouseStatusPresenter() }


    private val mAdapter by lazy { HouseListAdapter(object:HouseListAdapter.IHouseListListener{
        override fun onStatusClick(bean: HouseConfigItemBean) {
            if (mLocalConfigId<0){
                showToast("请先下载")
                return
            }

            ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_STATUS_ATY)
                .withLong(com.sribs.common.ARouterPath.VAL_PROJECT_ID,bean.projectId?:-1)
                .withLong(com.sribs.common.ARouterPath.VAL_UNIT_ID,bean.unitId?:-1)
                .withLong(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_ID,mLocalConfigId)
                .withString(com.sribs.common.ARouterPath.VAL_HOUSE_NAME,mPartNo)
                .withInt(com.sribs.common.ARouterPath.VAL_HOUSE_TYPE,2)
                .withString(com.sribs.common.ARouterPath.VAL_HOUSE_POS,bean.name)
                .navigation()
        }

        override fun onDamageClick(bean: HouseConfigItemBean) {
            if (mLocalConfigId<0){
                showToast("请先下载")
                return
            }
            ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_DAMAGE_DES_ATY)
                .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE,"${mTitle}${bean.name}")
                .withString(com.sribs.common.ARouterPath.VAL_HOUSE_POS,bean.name)
                .withLong(com.sribs.common.ARouterPath.VAL_PROJECT_ID,bean.projectId?:-1)
                .withLong(com.sribs.common.ARouterPath.VAL_UNIT_ID,bean.unitId?:-1)
                .withLong(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_ID,mLocalConfigId)
                .withLong(com.sribs.common.ARouterPath.VAL_HOUSE_ROOM_ID,bean.roomStatusId?:-1)
                .withInt(com.sribs.common.ARouterPath.VAL_HOUSE_TYPE,2)
                .withString(com.sribs.common.ARouterPath.VAL_PART_NO,mPartNo)
                .navigation()
        }

        override fun onAllFinish(b: Boolean) {
            mHouseStatusPresenter.updateHouseFinish(
                mProjectId,
                mUnitId,
                mLocalConfigId,
                mPartNo,
                mHouseType,
                b
            )
        }

        override fun onClearClick(bean: HouseConfigItemBean) {
            DialogUtil.showMsgDialog(this@HouseDamageListActivity,"是否确认清空记录？",{
                mRoomPresenter.clearRoom(mLocalConfigId, bean.name)
            },{})

        }
    }) }

    override fun getAdapter(): BaseListAdapter<HouseConfigItemBean, ItemHouseConfigListBinding> = mAdapter

    override fun getLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(this)

    override fun getListRecyclerView(): RecyclerView = mRvBinding.baseListRv

    override fun getListSwipeRefreshLayout(): SwipeRefreshLayout = mRvBinding.baseListSrl

    override fun getView(): View = mBinding.root

    override fun isEnableRefresh(): Boolean = true

    override fun isEnableScroll(): Boolean = true

    override fun initView() {
        super.initView()
        bindPresenter()
        initToolbar()
        initData()
    }

    override fun deinitView() {
        super.deinitView()
        unbindPresenter()
    }

    private fun initToolbar(){
        mBinding.tb.setNavigationIcon(R.mipmap.icon_back)
        mBinding.tbTitle.text = mTitle
        mBinding.tb.showOverflowMenu()
        setSupportActionBar( mBinding.tb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mBinding.tb.setNavigationOnClickListener {
            finish()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_house, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.menu_house_finish -> {
                mRoomPresenter.allFinish(mProjectId, mUnitId, mLocalConfigId)
            }
            R.id.menu_house_review -> {
                ARouter.getInstance().build(com.sribs.common.ARouterPath.REPORT_ATY)
                    .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE,mTitle)
                    .withLong(com.sribs.common.ARouterPath.VAL_PROJECT_ID,mProjectId)
                    .withLong(com.sribs.common.ARouterPath.VAL_UNIT_ID,mUnitId)
                    .withLong(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_ID,mLocalConfigId)
                    .withString(com.sribs.common.ARouterPath.VAL_PART_NO,mPartNo)
                    .withInt(com.sribs.common.ARouterPath.VAL_HOUSE_TYPE,mHouseType)
                    .navigation()

            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun initData(){
        if (mLocalConfigId>0){
            getLocalConfig()
            mHouseStatusPresenter.getHouseStatus(mLocalConfigId)
        }
    }

    private fun isFloor(configType:Int?):Boolean{
        return when(configType){
            UnitConfigType.CONFIG_TYPE_FLOOR_BOTTOM.value->true
            UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL.value->true
            UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value->true
            UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM.value->false
            UnitConfigType.CONFIG_TYPE_UNIT_NORMAL.value->false
            UnitConfigType.CONFIG_TYPE_UNIT_TOP.value->false
            else->true
        }
    }

    private fun getLocalConfig(){
        var houseType =  resources.getStringArray(R.array.house_type).indexOfFirst { mTitle.contains(it) }
        mPresenter.getLocalConfig(mLocalConfigId,houseType)
    }

    override fun onRefresh() {
        super.onRefresh()
        getLocalConfig()
    }

    override fun getContext(): Context? = this

    override fun onLocalConfig(l: ArrayList<RoomItemBean>) {
    }

    override fun onHouseConfig(l: ArrayList<HouseConfigItemBean>) {
        refreshFinish()
        if (l.isEmpty())return
        mAdapter.setData(l)
        mRoomPresenter.getRoomStatus(mLocalConfigId)
    }

    override fun onRoomStatus(l: ArrayList<RoomStatusBean>) {
        if (l.isEmpty())return
        mAdapter.updateStatus(l)

    }

    override fun onHouseStatus(l: ArrayList<HouseStatusBean>) {

    }

    override fun bindPresenter() {
        mPresenter.bindView(this)
        mRoomPresenter.bindView(this)
        mHouseStatusPresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg)
    }

    override fun unbindPresenter() {
        mPresenter.unbindView()
        mRoomPresenter.unbindView()
        mHouseStatusPresenter.unbindView()
    }

}