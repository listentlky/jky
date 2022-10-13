package com.sribs.bdd.ui.project

import android.app.Dialog
import android.content.Context
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.forEachIndexed
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.Config
import com.sribs.bdd.R
import com.sribs.bdd.bean.BuildingMainBean
import com.sribs.bdd.databinding.ActivityFloorListBinding
import com.sribs.bdd.module.building.BuildingListPresenter
import com.sribs.bdd.module.building.IBuildingContrast
import com.sribs.bdd.ui.adapter.BuildingListAdapter
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.ui.widget.CommonGridDividerItemDecoration
import com.sribs.common.utils.DialogUtil

/**
 * 楼号/单元列表界面
 */
@Route(path = com.sribs.common.ARouterPath.PRO_CREATE_ATY_FLOOR_LIST)
class ProjectFloorActivity : BaseActivity(), IBuildingContrast.IBuildingListView,
    BuildingListAdapter.ICallback,
    SwipeRefreshLayout.OnRefreshListener {

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_PROJECT_UUID)
    var mLocalProjectUUID = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_PROJECT_NAME)
    var mProjectName = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_PROJECT_ID)
    var mLocalProjectId = -1L

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID)
    var mRemoteId = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_VERSION)
    var mVersion = 0

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_LEADER)
    var mLeader = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_INSPECTOR)
    var mInspector = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    private val mPresenter: BuildingListPresenter by lazy { BuildingListPresenter() }
    private val mBinding: ActivityFloorListBinding by inflate()

    override fun deinitView() {
        mPresenter.unbindView()
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        bindPresenter()
        initToolbar()
        initData()
        initRecycle()
        initSearchBar()
    }

    /**
     * @Description search
     */
    private fun initSearchBar() {
        var searchView = mBinding.mainSearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                buildingAdapter?.setSearch(query ?: "")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    buildingAdapter?.setSearch("")
                }
                return false
            }

        })
    }

    private fun initToolbar() {
        mBinding.toolbar.tb.setNavigationIcon(R.mipmap.icon_back)
        mBinding.toolbar.tbTitle.text = mTitle
        mBinding.toolbar.tb.showOverflowMenu()
        setSupportActionBar(mBinding.toolbar.tb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mBinding.toolbar.tb.setNavigationOnClickListener {
            finish()
        }

        mBinding.matchMainFab.setOnClickListener {
            ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CREATE_ATY_TYPE)
                .withLong(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID, mLocalProjectId)
                .withString(com.sribs.common.ARouterPath.VAL_PROJECT_UUID, mLocalProjectUUID)
                .withString(com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID, mRemoteId)
                .withString(com.sribs.common.ARouterPath.VAL_COMMON_LEADER, mLeader)
                .withString(com.sribs.common.ARouterPath.VAL_COMMON_INSPECTOR, mInspector)
                .navigation()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_bld, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_bld_refresh->{ //从云端获取更新
                if(mRemoteId.isNullOrEmpty()){
                    showToast("云端无该项目，请先上传")
                    return super.onOptionsItemSelected(item)
                }
                mPresenter.getBuildingRemote(mRemoteId,mVersion,dataList)
            }

          /*  R.id.menu_bld_upload -> { //上传
                var data = buildingAdapter?.getData()
                if (data == null || data.size <= 0) {
                    showToast("请先创建楼")
                    return super.onOptionsItemSelected(item)
                }

                var listLocalData = data.filter {
                    it.bldId!! > 0
                }
                
                if(listLocalData.isEmpty()){
                    showToast("请先下载或创建本地楼再上传")
                    return super.onOptionsItemSelected(item)
                }

                var items = Array<String>(listLocalData.size+1) {""}

                var checkedList = ArrayList<String>()

                items[0] = "全选"
                listLocalData.forEachIndexed { index, buildingMainBean ->
                    items[index+1] = buildingMainBean.bldName!!
                }
                var alertList: ListView?=null
                var alert = AlertDialog.Builder(this).setTitle("上传配置")
                    .setMultiChoiceItems(items, null) { dialog, which, isChecked ->
                        if(which == 0){
                            if(isChecked){
                                alertList?.forEachIndexed { index, view ->
                                    alertList?.setItemChecked(index,true)
                                }
                                checkedList.addAll(items.filter {
                                    !it.equals("全选")
                                })
                            }else{
                                alertList?.forEachIndexed { index, view ->
                                    alertList?.setItemChecked(index,false)
                                }
                                checkedList.clear()
                            }
                        }else{
                            if(isChecked){
                                checkedList.add(items[which])
                            }else{
                                checkedList.remove(items[which])
                            }
                        }
                    }.setNegativeButton("取消"){ dialog, which ->

                    }.setPositiveButton("上传"){ dialog, which ->
                        //此处处理上传
                        LogUtils.d("checkedList: "+checkedList)
                        listLocalData.forEachIndexed { index, buildingMainBean ->
                            if(checkedList.contains(buildingMainBean.bldName)){
                                mPresenter.uploadBuilding(buildingMainBean)
                            }
                        }
                    }.create()
                alertList = alert?.listView
                alert?.show()
            }
            R.id.menu_bld_download_config -> { //下载配置

                var data = buildingAdapter?.getData()
                if (data == null || data.size <= 0) {
                    showToast("请先创建楼")
                    return super.onOptionsItemSelected(item)
                }

                var listRemoteData = data.filter {
                    !it.remoteId.isNullOrEmpty()
                }

                if(listRemoteData.isEmpty()){
                    showToast("云端无配置,请先上传再下载")
                    return super.onOptionsItemSelected(item)
                }

                var items = Array<String>(listRemoteData.size+1) {""}

                var checkedList = ArrayList<String>()

                items[0] = "全选"
                listRemoteData.forEachIndexed { index, buildingMainBean ->
                    items[index+1] = buildingMainBean.bldName!!
                }
                var alertList: ListView?=null
                var alert = AlertDialog.Builder(this).setTitle("下载配置")
                    .setMultiChoiceItems(items, null) { dialog, which, isChecked ->
                        if(which == 0){
                            if(isChecked){
                                alertList?.forEachIndexed { index, view ->
                                    alertList?.setItemChecked(index,true)
                                }
                                checkedList.addAll(items.filter {
                                    !it.equals("全选")
                                })
                            }else{
                                alertList?.forEachIndexed { index, view ->
                                    alertList?.setItemChecked(index,false)
                                }
                                checkedList.clear()
                            }
                        }else{
                            if(isChecked){
                                checkedList.add(items[which])
                            }else{
                                checkedList.remove(items[which])
                            }
                        }
                    }.setNegativeButton("取消"){ dialog, which ->

                    }.setPositiveButton("下载"){ dialog, which ->
                        //此处处理上传
                        LogUtils.d("checkedList: "+checkedList)

                        listRemoteData.forEachIndexed { index, buildingMainBean ->
                            if(checkedList.contains(buildingMainBean.bldName)){
                             //   mPresenter.downloadBuilding(buildingMainBean)
                            }
                        }

                    }.create()
                alertList = alert?.listView
                alert?.show()
            }*/
        }
        return super.onOptionsItemSelected(item)
    }


    private fun initData() {
        mPresenter.getAllBuilding(mLocalProjectId,mLocalProjectUUID)
    }

    private var buildingAdapter: BuildingListAdapter? = null

    private var dataList: ArrayList<BuildingMainBean> = ArrayList<BuildingMainBean>()

    private fun initRecycle() {
        mBinding.recyclerView?.addItemDecoration(
            CommonGridDividerItemDecoration(
                resources.getDimensionPixelOffset(R.dimen.main_item_margin),
                resources.getDimensionPixelOffset(R.dimen.main_item_margin)
            )
        )

        buildingAdapter = BuildingListAdapter(this).also {
            it.setData(dataList)
        }

        var m = GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false)
        m.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return buildingAdapter!!.getSpanSize(position)
            }
        }
        mBinding.recyclerView.layoutManager = m

        mBinding.recyclerView.adapter = buildingAdapter

        mBinding.baseListSrl.setOnRefreshListener(this)
    }

    override fun onAllBuilding(l: List<BuildingMainBean>) {
        dataList.clear()
        dataList.addAll(l)
        LogUtils.d("楼栋数据为: " + dataList.toString())
        buildingAdapter?.setData(dataList)
        mBinding.baseListSrl.isRefreshing = false
    }

    override fun getContext(): Context = this

    override fun bindPresenter() {
        mPresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg)
    }

    override fun unbindPresenter() {
        mPresenter.unbindView()
    }

    var mBottomDialog: Dialog? = null

    fun showBottomDialog(show: Boolean, pos: Int, beanMain: BuildingMainBean) {
        if (show) {
            if (mBottomDialog?.isShowing == true) return
            mBottomDialog =
                DialogUtil.showBottomDialog(this, R.layout.dialog_common_bottom_building_select, true) {
                    when (it) {
                        0->{ //上传楼配置
                            if(beanMain.bldId!! < 0){
                                showToast("请先下载再上传")
                                return@showBottomDialog
                            }
                            mPresenter.uploadBuilding(beanMain)

                        }
                        1 -> { //下载配置
                            if(beanMain.remoteId.isNullOrEmpty()){
                                showToast("非云端项目，请先上传再下载")
                                return@showBottomDialog
                            }
                            doDownload(beanMain)
                        }
                        2 -> { // 删除
                            DialogUtil.showMsgDialog(this, "是否确认删除楼?", {
                                mPresenter.deleteBuilding(beanMain)
                            })
                        }
                        else -> {
                            showFab(true)
                            buildingAdapter!!.menuUnSel(pos)
                        }
                    }
                }
            showFab(false)
        } else {
            if (mBottomDialog == null || mBottomDialog?.isShowing == false) {
                mBottomDialog = null
                return
            }
            mBottomDialog?.dismiss()
            mBottomDialog = null
        }
    }

    /**
     * 下载配置
     */
    private fun doDownload(beanMain: BuildingMainBean){
        if (beanMain.remoteId.isNullOrEmpty()){
            showToast(getString(R.string.error_no_remote))
            return
        }
        if (!Config.isNetAvailable){
            showToast(getString(R.string.error_no_network))
            return
        }
        mPresenter.getV3BuildingVersionHistory(mRemoteId,beanMain.remoteId!!){ versionList->
            if (versionList.isNullOrEmpty()){
                showToast(getString(R.string.error_no_history))
                return@getV3BuildingVersionHistory
            }
            LogUtils.d("版本个数: ${versionList.size}")
            com.sribs.bdd.utils.DialogUtil.showDownloadV3ProjectDialog(this,null,beanMain.updateTime!!,
                versionList!!.toTypedArray()){ l->
                if (l.isEmpty())return@showDownloadV3ProjectDialog
                LOG.I("123","${l[0]}")
                var idx = l[0]!!
                DialogUtil.showMsgDialog(this,"覆盖本地版本？",{
                    showFab(true)
                    mPresenter.downloadBuildingConfig(
                        beanMain.remoteId!!,
                        versionList[idx].version,
                    ){
                        if(it){
                            showToast("下载成功")
                            beanMain.also { b->b.status = resources.getStringArray(R.array.main_project_status)[4] }
                        }else{
                            showToast("下载失败")
                        }
                        showFab(false)
                    }
                },{})

            }
        }
    }

    private fun showFab(b: Boolean) {
        if (b) {
            mBinding.matchMainFab.visibility = View.VISIBLE
        } else {
            mBinding.matchMainFab.visibility = View.GONE
        }
    }

    override fun onMoreChecked(beanMain: BuildingMainBean, pos: Int, checked: Boolean) {
        showBottomDialog(checked, pos, beanMain)
    }

    override fun onCardSelect(beanMain: BuildingMainBean, pos: Int) {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_ITEM_ATY_FLOOR)
            .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE, mTitle)
            .withLong(com.sribs.common.ARouterPath.VAL_PROJECT_ID, mLocalProjectId)
            .withString(com.sribs.common.ARouterPath.VAL_PROJECT_UUID, mLocalProjectUUID)
            .withLong(com.sribs.common.ARouterPath.VAL_BUILDING_ID, beanMain.bldId!!)
            .withString(com.sribs.common.ARouterPath.VAL_BUILDING_UUID, beanMain.bldUUID!!)
            .withString(com.sribs.common.ARouterPath.VAL_COMMON_LEADER, mLeader)
            .withInt(com.sribs.common.ARouterPath.VAL_COMMON_VERSION, beanMain.version)
            .withString(com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID, beanMain.remoteId)
            .withString(com.sribs.common.ARouterPath.VAL_COMMON_INSPECTOR,beanMain.inspectorName)
            .withString(com.sribs.common.ARouterPath.VAL_PROJECT_NAME, mProjectName)
            .withString(com.sribs.common.ARouterPath.VAL_BUILDING_NAME, beanMain.bldName)
            .navigation()
    }

    override fun onRefresh() {
        initData()
    }

}