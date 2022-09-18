package com.sribs.bdd.ui.project

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.R
import com.sribs.bdd.bean.BuildingFloor
import com.sribs.bdd.databinding.ActivityFloorListBinding
import com.sribs.bdd.module.house.IUnitListContrast
import com.sribs.bdd.module.house.UnitListPresenter
import com.sribs.bdd.module.project.IProjectContrast
import com.sribs.bdd.module.project.ProjectFloorPresenter
import com.sribs.bdd.ui.adapter.FloorAdapter
import com.sribs.bdd.ui.adapter.RoomListAdapter
import com.sribs.bdd.ui.main.MainListFragment
import com.sribs.bdd.ui.main.MainMapFragment
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.UnitBean
import kotlinx.android.synthetic.main.activity_floor_list.view.*
import kotlinx.android.synthetic.main.activity_unit_list.view.*

/**
 * 楼号/单元列表界面
 */
@Route(path = com.sribs.common.ARouterPath.PRO_CREATE_ATY_FLOOR_LIST)
class ProjectFloorActivity:BaseActivity(), IUnitListContrast.IView,FloorAdapter.ICallback {

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID)
    var mLocalProjectId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID)
    var mRemoteId = null

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    private val mPresenter:UnitListPresenter by lazy { UnitListPresenter() }
    private val mBinding:ActivityFloorListBinding by inflate()

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
    private fun initSearchBar(){
        var searchView = mBinding.mainSearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                floorAdapter?.setSearch(query?:"")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()){
                    floorAdapter?.setSearch("")
                }
                return false
            }

        })

    }

    private fun initToolbar(){
        mBinding.toolbar.tb.setNavigationIcon(R.mipmap.icon_back)
        mBinding.toolbar.tbTitle.text = mTitle
        mBinding.toolbar.tb.showOverflowMenu()
        setSupportActionBar( mBinding.toolbar.tb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mBinding.toolbar.tb.setNavigationOnClickListener {
            finish()
        }

        mBinding.matchMainFab.setOnClickListener {
            ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CREATE_ATY_TYPE)
                .withLong(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID, mLocalProjectId)
                .withString(com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID, mRemoteId)
                .navigation()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_floor, menu)
        return true
    }


    private fun initData(){
        mPresenter.getAllUnit(mLocalProjectId)
    }

    private var floorAdapter:FloorAdapter? = null

    private var dataList:ArrayList<BuildingFloor> = ArrayList<BuildingFloor>()

    private fun initRecycle(){
        //todo 添加测试数据
        dataList.add(BuildingFloor(11,22,33,"11","22", 1,
            -1,"","","",null,0,0,0))


        mBinding.recyclerView.addItemDecoration(SplitGridDividerItemDecoration())

        floorAdapter = FloorAdapter(this).also {
            it.setData(dataList)
        }

        var m = GridLayoutManager(this,3, LinearLayoutManager.VERTICAL,false)
        m.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return floorAdapter!!.getSpanSize(position)
            }
        }
        mBinding.recyclerView.layoutManager = m

        mBinding.recyclerView.adapter = floorAdapter

    }

    override fun getContext(): Context = this

    @SuppressLint("NotifyDataSetChanged")
    override fun onAllUnit(l: List<UnitBean>) {//列表
        if (l.isNotEmpty()){
            dataList.clear()
            l.forEach {
                dataList.add(BuildingFloor.copyFromUnitBean(it))
            }
            LogUtils.d("楼栋数据为: "+dataList.toString())
            floorAdapter?.setData(dataList)
        }

    }

    override fun onUpdate(b: Boolean) {

    }



    override fun bindPresenter() {
        mPresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg)
    }

    override fun unbindPresenter() {
        mPresenter.unbindView()
    }

    override fun onMoreChecked(beanMain: BuildingFloor, pos: Int, checked: Boolean) {

    }

    override fun onCardSelect(beanMain: BuildingFloor, pos: Int) {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_ITEM_ATY_FLOOR)
            .withLong(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID, mLocalProjectId)
            .withString(com.sribs.common.ARouterPath.VAL_BUILDING_ID,beanMain.remoteId)
            .navigation()
    }

    inner class SplitGridDividerItemDecoration:RecyclerView.ItemDecoration(){
        private var mDivider: Drawable?=null
        private var mDividerHeight = 0
        private var space = 0
        init {
            mDivider = ColorDrawable(resources.getColor(R.color.gray_600))
            mDividerHeight = resources.getDimensionPixelOffset(R.dimen._1sdp)
            space = resources.getDimensionPixelOffset(R.dimen.main_item_margin)
        }

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            var manager = parent.layoutManager as GridLayoutManager
            var span = manager.spanCount
            var pos = parent.getChildAdapterPosition(view)
            if(pos<0){
                view.visibility = View.GONE //adapter中数据已删除
                return
            }
            view.visibility = View.VISIBLE
            var adapter = parent.adapter as FloorAdapter
            if (view is TextView){ // tag
//                outRect.left =  space
//                outRect.top =  space
                outRect.right =  space/2
                outRect.bottom = space/2
            }else if(view is CardView){
                var contentIndexList = adapter.getSameGroupContentIndexes(pos)
                var offset = pos - contentIndexList[0]
                when{
                    offset % span == 0 -> {//最左
                        outRect.left = space
                        outRect.right = space/2
                    }
                    offset % span == (span -1) -> {//最右
                        outRect.right = space
                        outRect.left = space/2
                    }
                    else -> {
                        outRect.left = space/2
                        outRect.right = space/2
                    }
                }
            }
            if (parent.getChildAdapterPosition(view) >= span){ //中间
                outRect.top = space/2
                outRect.bottom =  space/2
            } else {                                               //第一排
                outRect.top = space/2
                outRect.bottom =  space/2
            }
        }

        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDrawOver(c, parent, state)
            drawHorizontal(c,parent)
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDraw(c, parent, state)
        }

        private fun drawHorizontal(c: Canvas, parent: RecyclerView){
            for (i in 0 until parent.childCount){
                val child: View = parent.getChildAt(i)
                val realPos =  parent.getChildAdapterPosition(child)
                if (child is TextView){
                    val left = space
                    val right = parent.measuredWidth-space
                    var centerOffset = child.bottom - child.top
                    val top = child.top+centerOffset  //- resources.getDimensionPixelOffset(R.dimen._9sdp)
                    val bottom = top + mDividerHeight
                    mDivider?.setBounds(left, top, right, bottom)
                    mDivider?.draw(c)
                }
//                if ((realPos == parent.adapter!!.itemCount-1) && child is FrameLayout){ //最后一个
//                    val left = 0
//                    val right = parent.measuredWidth
//                    val top = child.bottom + resources.getDimensionPixelOffset(R.dimen._8sdp)
//                    val bottom =  top + mDividerHeight
//                    mDivider?.setBounds(left, top, right, bottom)
//                    mDivider?.draw(c)
//                }
            }
        }

    }

}