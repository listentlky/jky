package com.sribs.bdd.ui.house

import android.content.res.Configuration
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.adapter.BasePagerAdapter
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.R
import com.sribs.bdd.bean.RecordItemBean
import com.sribs.bdd.databinding.ActivityDamageDescriptionBinding
import com.sribs.bdd.module.house.HouseStatusPresenter
import com.sribs.bdd.module.house.IHouseContrast
import com.sribs.bdd.module.house.RoomStatusPresenter
import com.sribs.common.bean.db.HouseStatusBean
import com.sribs.common.bean.db.RoomStatusBean

/**
 * @date 2021/7/16
 * @author elijah
 * @Description
 */
@Route(path = com.sribs.common.ARouterPath.HOUSE_DAMAGE_DES_ATY)
class HouseDamageDescriptionActivity:BaseActivity(),IHouseContrast.IRoomView,IHouseContrast.IHouseStatusView{

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_PROJECT_ID)
    var mProjectId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_ID)
    var mUnitId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_HOUSE_ROOM_ID)
    var mHouseRoomId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_HOUSE_TYPE)
    var mHouseType = -1

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_ID)
    var mConfigId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_HOUSE_POS)
    var mDamagePos = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_PART_NO)
    var mPartNo= ""


    private val mPresenter by lazy { RoomStatusPresenter() }

    private val mHouseStatusPresenter by lazy { HouseStatusPresenter() }

    private val mBinding :ActivityDamageDescriptionBinding by inflate()

    private val mSelectFragment by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_DAMAGE_DES_FGT)
            .withString(com.sribs.common.ARouterPath.VAL_HOUSE_POS,mDamagePos)
            .withInt(com.sribs.common.ARouterPath.VAL_HOUSE_TYPE,mHouseType)
            .withLong(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_ID,mConfigId)
            .navigation() as DamageDescriptionFragment
    }

    private val mFragments by lazy {
        listOf(
            ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_DAMAGE_RECORD_FGT)
                .navigation() as BaseFragment,
            ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_DAMAGE_EDIT_FGT)
                .navigation() as BaseFragment
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){//横屏
            LOG.I("123", "横屏")

        }else if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){//纵屏
            LOG.I("123", "纵屏")

        }
        super.onConfigurationChanged(newConfig)
    }


    fun updateInspector(){
        mHouseStatusPresenter.updateHouseInspector(mProjectId,mUnitId,mConfigId,mPartNo,mHouseType)
    }

    override fun deinitView() {
        unbindPresenter()
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        bindPresenter()
        initToolbar()
        initFragment()
        initViewPager()
        mPresenter.getRoomStatus(mConfigId,mDamagePos)
        mHouseStatusPresenter.getHouseStatus(mConfigId)
    }

    fun initToolbar(){
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
        menuInflater.inflate(R.menu.menu_damage, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.menu_house_finish -> {
                if (mHouseType == 2) {
                    mHouseStatusPresenter.updateHouseInspector(
                        mProjectId,
                        mUnitId,
                        mConfigId,
                        mPartNo,
                        mHouseType
                    )
                    mPresenter.finishRoomStatus(mProjectId, mUnitId, mConfigId, mDamagePos, 1)
                } else {
                    mHouseStatusPresenter.updateHouseInspector(
                        mProjectId,
                        mUnitId,
                        mConfigId,
                        mPartNo,
                        mHouseType,
                        true
                    )
//                    mPresenter.finishRoomStatus(mProjectId, mUnitId, mConfigId, mDamagePos, 1)
                }
                showToast("完成记录")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.house_des_sel_ll,mSelectFragment)
            .commit()
    }



    private fun initViewPager(){
        mBinding.houseVp.setSmooth(false)
        mBinding.houseVp.setScroll(false)
        mBinding.houseVp.adapter = BasePagerAdapter(supportFragmentManager,mFragments)
        mBinding.houseVp.offscreenPageLimit = 2

    }

    fun doEdit(pos:ArrayList<String>,picPos:ArrayList<String>){
        LOG.I("123","pos=$pos")
        (mFragments[1] as DamageEditFragment).also {
            it.setViewType(pos,picPos)
        }
        mBinding.houseVp.currentItem = 1
        mSelectFragment.lock()

    }

    fun doEditOther(pos:ArrayList<String>,des:String){
        (mFragments[1] as DamageEditFragment).also {
            it.setViewOther(pos,des)
        }
        mBinding.houseVp.currentItem = 1
        mSelectFragment.lock()
    }


    fun doPic(pos:ArrayList<String>,cur:ArrayList<String>?){
        (mFragments[0] as DamageRecordFragment).also {
            it.showPic(pos,cur)
        }
        mBinding.houseVp.currentItem = 0
    }

    fun filterRecord(filterStr:String){
        (mFragments[0] as DamageRecordFragment).also {
            it.setFilter(filterStr)
        }
    }

    fun doOtherList(){
        (mFragments[0] as DamageRecordFragment).also {
            it.showOtherList()
        }
        mBinding.houseVp.currentItem = 0
    }

    fun doOtherAdd(){
        (mFragments[1] as DamageEditFragment).also {
            var posList = mSelectFragment.getAllSel()
            it.setViewType(posList, ArrayList())
        }
        mBinding.houseVp.currentItem = 1
        mSelectFragment.lock()
    }

    fun doEditFinish(){
        mSelectFragment.unlock()
        mBinding.houseVp.currentItem = 0
        //

        (mFragments[0] as DamageRecordFragment).refreshPic()

    }

    fun doCleanSel(){
        (mFragments[0] as DamageRecordFragment).cleanSel()
    }

    fun selRecord(b:RecordItemBean,cur:ArrayList<String>?){
        if (!b.posList.isNullOrEmpty())  mSelectFragment.selRecord(b.posList!!,cur)

    }

    override fun onRoomStatus(l: ArrayList<RoomStatusBean>) {
    }

    override fun onHouseStatus(l: ArrayList<HouseStatusBean>) {
    }

    override fun bindPresenter() {
        mPresenter.bindView(this)
        mHouseStatusPresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg)
    }

    override fun unbindPresenter() {
       mPresenter.unbindView()
        mHouseStatusPresenter.unbindView()
    }


}