package com.sribs.bdd.ui.house

import android.app.Dialog
import android.view.Menu
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.databinding.LayoutBaseListMvpBinding
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseListActivity
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.inflate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.sribs.bdd.R
import com.sribs.bdd.bean.RoomItemBean
import com.sribs.bdd.bean.UnitConfigType
import com.sribs.bdd.bean.data.*
import com.sribs.bdd.databinding.ActivityHouseListBinding
import com.sribs.bdd.ui.adapter.RoomListAdapter
import com.sribs.common.server.IDatabaseService
import com.sribs.common.ui.widget.CommonGridDividerItemDecoration
import java.text.SimpleDateFormat

/**
 * @date 2021/7/13
 * @author elijah
 * @Description
 */
@Deprecated("使用 UnitListActivity")
@Route(path= com.sribs.common.ARouterPath.HOUSE_LIST_ATY)
class HouseListActivity:BaseListActivity<RoomItemBean,ViewBinding>() {

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID)
    var mLocalProjectId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID)
    var mRemoteProjectId = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    private val mBinding: ActivityHouseListBinding by inflate()


    private val mRvBinding by lazy { LayoutBaseListMvpBinding.bind(mBinding.rvLl) }

    private val mAdapter by lazy { RoomListAdapter(object :RoomListAdapter.ICallback{
        override fun onMoreChecked(beanMain: RoomItemBean, pos: Int, checked: Boolean) {
            showBottomDialog(checked,pos,beanMain)
        }

        override fun onCardSelect(beanMain: RoomItemBean, pos: Int) {
        }
    })
    }

    override fun getAdapter(): BaseListAdapter<RoomItemBean, ViewBinding> = mAdapter

    override fun getLayoutManager(): RecyclerView.LayoutManager  = GridLayoutManager(this,3)

    override fun getListRecyclerView(): RecyclerView = mRvBinding.baseListRv

    override fun getListSwipeRefreshLayout(): SwipeRefreshLayout = mRvBinding.baseListSrl

    override fun getView(): View = mBinding.root

    override fun isEnableRefresh(): Boolean = true

    override fun isEnableScroll(): Boolean = true

    override fun initView() {
        super.initView()
        initToolbar()
        mListRv?.addItemDecoration(
            CommonGridDividerItemDecoration(
                resources.getDimensionPixelOffset(R.dimen.main_item_margin),
                resources.getDimensionPixelOffset(R.dimen.main_item_margin)
            )
        )
        initData()
    }

    private fun initToolbar(){
        mBinding.toolbar.setNavigationIcon(R.mipmap.icon_back)
        mBinding.toolbarTitle.text = mTitle
        mBinding.toolbar.showOverflowMenu()
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mBinding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_room, menu)
        return true
    }
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        var item = menu?.findItem(R.id.menu_main_pop_local)
        item?.icon?.setBounds(20,50,0,0)
        return true
    }

    override fun onRefresh() {
        super.onRefresh()
        initData()
    }

    override fun onLoad(curPage: Int) {
        super.onLoad(curPage)
    }


    private fun initData(){
        if (mLocalProjectId==-1L){

        }else{
            getLocalConfigs()
        }
    }

    private fun getLocalConfigs(){
        val srv = ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService
        var sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
        var statusArr = resources.getStringArray(R.array.main_project_status)
        var unitList:List<com.sribs.common.bean.db.UnitBean>?=null
        srv.getAllUnit(mLocalProjectId!!.toLong())
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                unitList = it
                srv.getAllConfig(mLocalProjectId!!.toLong())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                var l = it.sortedWith(compareBy ({b->b.unitId},{b->b.floorIdx},{b->b.neighborIdx})).map { b->
                    LOG.I("123","$b")
                    var unitNo = unitList?.firstOrNull { ub->ub.unitId == b.unitId }?.unitNo?.toString()?:""
                    if (unitNo.isNotEmpty()) unitNo = String.format("%02d",unitNo.toIntOrNull())
                    var floorNeighborStr = if(b.neighborIdx==null){
                        if(!b.floorNum.isNullOrEmpty()) "${b.floorNum}层" else "${String.format("%02d",b.floorIdx!!+1)}层"
                    }else{
                        if(!b.floorNum.isNullOrEmpty()) "${b.floorNum}${b.neighborNum}室"
                        else "${b.floorIdx!!+1}${String.format("%02d",(b.neighborIdx!!+1))}室"
                    }
                    RoomItemBean(
                        mLocalProjectId!!.toLong(),
                        "",
                        sdf.format(b.updateTime),
                        0,
                        unitNo,
                        floorNeighborStr?:"",
                        "",
                        ""
                    ).also { item->
                        item.dataBean = when(b.configType){
                            UnitConfigType.CONFIG_TYPE_FLOOR_BOTTOM.value-> ProjectConfigBottomFloorDataBean().also { pdb->
                                pdb.configId = b.configId!!
                                pdb.floorNum = b.floorNum?:""
                                pdb.corridorNum = b.corridorNum?:""
                                pdb.corridorConfig = b.corridorConfig?:""
                                pdb.platformNum = b.platformNum?:""
                                pdb.platformConfig = b.platformConfig?:""
                            }
                            UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL.value->ProjectConfigNormalFloorDataBean().also { pdb->
                                pdb.configId = b.configId!!
                                pdb.floorNum = b.floorNum?:""
                                pdb.corridorNum = b.corridorNum?:""
                                pdb.corridorConfig = b.corridorConfig?:""
                                pdb.platformNum = b.platformNum?:""
                                pdb.platformConfig = b.platformConfig?:""
                            }
                            UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value->ProjectConfigTopFloorDataBean().also { pdb->
                                pdb.configId = b.configId!!
                                pdb.floorNum = b.floorNum?:""
                                pdb.corridorNum = b.corridorNum?:""
                                pdb.corridorConfig = b.corridorConfig?:""
                            }
                            UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM.value->ProjectConfigBottomNeighborDataBean().also { pdb->
                                pdb.configId = b.configId!!
                                pdb.floorNum = b.floorNum?:""
                                pdb.neighborConfig = b.config1?:""
                            }
                            UnitConfigType.CONFIG_TYPE_UNIT_NORMAL.value->ProjectConfigNormalNeighborDataBean().also { pdb->
                                pdb.configId = b.configId!!
                                pdb.floorNum = b.floorNum?:""
                                pdb.neighborConfig = b.config1?:""
                            }
                            UnitConfigType.CONFIG_TYPE_UNIT_TOP.value->ProjectConfigTopNeighborDataBean().also { pdb->
                                pdb.configId = b.configId!!
                                pdb.floorNum = b.floorNum?:""
                                pdb.neighborType = if(b.unitType==0) "非复式" else "复式"
                            }
                            else->null
                        }
                    }
                }
                mAdapter.setData(ArrayList(l))
                refreshFinish()
            },{
                refreshFinish()
                it.printStackTrace()
            })
    }
    var mBottomDialog: Dialog?=null
    private fun showBottomDialog(show:Boolean,pos:Int,beanMain: RoomItemBean){
        if(show){
            if (mBottomDialog?.isShowing == true)return
            mBottomDialog = com.sribs.common.utils.DialogUtil.showBottomDialog(this,R.layout.dialog_common_bottom_select2,true){
                when(it){
                    0->{
                        ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_STATUS_ATY)
                            .withLong(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_ID,beanMain.dataBean?.configId?:-1)
                            .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE,"房屋现状")
                            .withString(com.sribs.common.ARouterPath.VAL_HOUSE_POS,"房屋")
                            .navigation()
                    }
                    1->{
                        var title = "${beanMain.unitNum}单元${beanMain.floorNeighborNum}${beanMain.floorNeighborNumEx}"
                        ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_DAMAGE_LIST_ATY)
                            .withLong(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_ID,beanMain.dataBean?.configId?:-1)
                            .withString(com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID,"")
                            .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE,title)
                            .navigation()
                    }
                    2->{
                        if (beanMain.localId<0){
                            showToast("无法编辑云端配置，请先下载到本地，修改完再上传")
                            return@showBottomDialog
                        }

                        LOG.I("123","id = ${beanMain.dataBean!!.configId}")

                        var jsonData = beanMain.dataBean!!.toJsonStr()
                        var title = "${beanMain.unitNum}单元${beanMain.floorNeighborNum}室配置"
                        ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CONFIG_ATY)
                            .withInt(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_TYPE,beanMain.dataBean!!.type!!.value)
                            .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE,title)
                            .withLong(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_ID,beanMain.dataBean!!.configId)
                            .withString(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_DATA,jsonData)
                            .navigation()
                    }
                    else->{
                        mAdapter.menuUnSel(pos)
                    }
                }
            }
        }else{
            if (mBottomDialog==null || mBottomDialog?.isShowing == false){
                mBottomDialog = null
                return
            }
            mBottomDialog?.dismiss()
            mBottomDialog = null
        }
    }

}