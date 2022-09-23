package com.sribs.bdd.ui.project

import android.app.Dialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.R
import com.sribs.bdd.bean.BuildingMainBean
import com.sribs.bdd.databinding.ActivityFloorListBinding
import com.sribs.bdd.module.building.BuildingListPresenter
import com.sribs.bdd.module.building.IBuildingContrast
import com.sribs.bdd.ui.adapter.BuildingListAdapter
import com.sribs.bdd.ui.adapter.FloorAdapter
import com.sribs.bdd.ui.main.MainListFragment
import com.sribs.bdd.ui.main.MainMapFragment
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
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID)
    var mLocalProjectId = -1L


    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_PROJECT_ID)
    var mLocalProjectId2 = -1L

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID)
    var mRemoteId = ""

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
                .withString(com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID, mRemoteId)
                .withString(com.sribs.common.ARouterPath.VAL_COMMON_LEADER, mLeader)
                .withString(com.sribs.common.ARouterPath.VAL_COMMON_INSPECTOR, mInspector)
                .navigation()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_floor, menu)
        return true
    }


    private fun initData() {
        mPresenter.getAllBuilding(mLocalProjectId)
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
        if (l.isNotEmpty()) {
            dataList.clear()
            dataList.addAll(l)
            LogUtils.d("楼栋数据为: " + dataList.toString())
            buildingAdapter?.setData(dataList)
        }
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
                        1->{ //上传楼配置

                        }
                        2 -> {
                            DialogUtil.showMsgDialog(this, "是否确认删除楼?", {
                                //TODO del project
                                /*  if (beanMain.localId>0){
                                      mPresenter.projectDelete(beanMain.localId)
                                  }else{
                                      showToast("无法删除云端楼")
                                  }*/
                                //    mPresenter.projectDelete(beanMain.localId)
                            })
                        }
                        3 -> { // 下载楼配置

                        }
                        4 -> { //下载所有楼配置

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
            .withLong(com.sribs.common.ARouterPath.VAL_PROJECT_ID, mLocalProjectId2)
            .withLong(com.sribs.common.ARouterPath.VAL_BUILDING_ID, beanMain.bldId!!)
            .withString(com.sribs.common.ARouterPath.VAL_COMMON_LEADER, mLeader)
            .withString(com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID, mRemoteId)
            .withString(com.sribs.common.ARouterPath.VAL_COMMON_INSPECTOR,beanMain.inspectorName)
            .navigation()
    }

    override fun onRefresh() {
        initData()
    }

}