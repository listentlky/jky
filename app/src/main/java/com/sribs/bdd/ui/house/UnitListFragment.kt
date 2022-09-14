package com.sribs.bdd.ui.house

import android.app.Dialog
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.databinding.LayoutBaseListMvpBinding
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseListFragment
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.bean.HouseConfigItemBean
import com.sribs.bdd.bean.RoomItemBean
import com.sribs.bdd.bean.data.*
import com.sribs.bdd.module.house.HouseListPresenter
import com.sribs.bdd.module.house.HouseStatusPresenter
import com.sribs.bdd.module.house.IHouseContrast
import com.sribs.bdd.ui.adapter.RoomListAdapter
import com.sribs.common.bean.db.HouseStatusBean


@Route(path= com.sribs.common.ARouterPath.HOUSE_UNIT_LIST_FGT)
class UnitListFragment:BaseListFragment<RoomItemBean,ViewBinding>(R.layout.layout_base_list_mvp)
    ,IHouseContrast.IHouseView,IHouseContrast.IHouseStatusView {
    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID)
    var mLocalUnitId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_PROJECT_ID)
    var mProjectId = -1L


    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID)
    var mRemoteUnitId = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_NO)
    var mUnitNo = ""

    private val mListPresenter by lazy { HouseListPresenter() }

    private val mStatusPresenter by lazy { HouseStatusPresenter() }


    private val mBinding : LayoutBaseListMvpBinding by bindView()

    private val mAdapter by lazy { RoomListAdapter(object : RoomListAdapter.ICallback{
        override fun onMoreChecked(beanMain: RoomItemBean, pos: Int, checked: Boolean) {
            showBottomDialog(checked,pos,beanMain)
        }

        override fun onCardSelect(beanMain: RoomItemBean, pos: Int) {
            if(beanMain.localId <0 ){
                showToast("请先下载配置到本地")
                return
            }

            var title = "${beanMain.unitNum}单元${beanMain.floorNeighborNum}${beanMain.floorNeighborNumEx}"
            var partNo = "${beanMain.floorNeighborNum}${beanMain.floorNeighborNumEx}"
            if (beanMain.floorNeighborNumEx.isNullOrEmpty()){
                ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_DAMAGE_LIST_ATY)
                    .withLong(com.sribs.common.ARouterPath.VAL_PROJECT_ID,beanMain.configBean?.projectId?:-1)
                    .withLong(com.sribs.common.ARouterPath.VAL_UNIT_ID,beanMain.configBean?.unitId?:-1)
                    .withLong(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_ID,beanMain.configBean?.configId?:-1)
                    .withInt(com.sribs.common.ARouterPath.VAL_HOUSE_TYPE,beanMain.houseType)
                    .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE,title)
                    .withString(com.sribs.common.ARouterPath.VAL_PART_NO,partNo)
                    .navigation()
            }else{
                var name =  resources.getStringArray(R.array.house_type).first {  beanMain.floorNeighborNumEx.contains(it) }
                ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_DAMAGE_DES_ATY)
                    .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE,title)
                    .withString(com.sribs.common.ARouterPath.VAL_HOUSE_POS,name)
                    .withLong(com.sribs.common.ARouterPath.VAL_PROJECT_ID,beanMain.configBean?.projectId?:-1)
                    .withLong(com.sribs.common.ARouterPath.VAL_UNIT_ID,beanMain.configBean?.unitId?:-1)
                    .withLong(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_ID,beanMain.configBean?.configId?:-1)
                    .withLong(com.sribs.common.ARouterPath.VAL_HOUSE_ROOM_ID,beanMain.houseStatusId)
                    .withInt(com.sribs.common.ARouterPath.VAL_HOUSE_TYPE,beanMain.houseType)
                    .withString(com.sribs.common.ARouterPath.VAL_PART_NO,partNo)
                    .navigation()
            }
        }
    })
    }

    override fun getAdapter(): BaseListAdapter<RoomItemBean, ViewBinding> = mAdapter

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        var m = GridLayoutManager(context,3, LinearLayoutManager.VERTICAL,false)
        m.spanSizeLookup = object :GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return mAdapter.getSpanSize(position)
            }

        }
        return m
    }

    override fun getListRecyclerView(): RecyclerView = mBinding.baseListRv

    override fun getListSwipeRefreshLayout(): SwipeRefreshLayout = mBinding.baseListSrl

    override fun isEnableRefresh(): Boolean = true

    override fun isEnableScroll(): Boolean = true

    override fun initView() {
        super.initView()
        bindPresenter()
        mListRv?.addItemDecoration(
            SplitGridDividerItemDecoration()
        )
        initData()
        LOG.I("123","UnitListFragment projectId=$mProjectId   unitId=$mLocalUnitId")
    }

    fun updateData(projectId:Long,unitId:Long,remoteId:String,unitNo:String){
        var isChange = false
        if (mProjectId!=projectId){
            mProjectId = projectId
            isChange = true
        }
        if (mLocalUnitId!=unitId){
            mLocalUnitId = unitId
            isChange = true
        }
        if (mRemoteUnitId!=remoteId){
            mRemoteUnitId = remoteId
            isChange = true
        }
        if (unitNo!=mUnitNo){
            mUnitNo = unitNo
            isChange = true
        }
        if (isChange){
            initData()
        }
    }





    override fun onRefresh() {
        super.onRefresh()
        initData()
    }

    override fun onLoad(curPage: Int) {
        super.onLoad(curPage)
    }

    private fun initData(){
        LOG.I("123","initData  $mLocalUnitId  $mRemoteUnitId")
        var localUnitId = if(mLocalUnitId?:-1>0) mLocalUnitId else null
        val remoteUnitId = if(mRemoteUnitId.isNullOrEmpty()) null else mRemoteUnitId
        mListPresenter.getHouseList(mProjectId,localUnitId,mUnitNo,remoteUnitId)
    }

    var mBottomDialog: Dialog?=null
    private fun showBottomDialog(show:Boolean,pos:Int,beanMain: RoomItemBean){
        if(show){
            if (mBottomDialog?.isShowing == true)return
            mBottomDialog = com.sribs.common.utils.DialogUtil.showBottomDialog(requireContext(),R.layout.dialog_common_bottom_select2,
                beanMain.floorNeighborNumEx.isNullOrEmpty()){
                when(it){
                    0->{
                        if (beanMain.localId<0){
                            showToast("无法编辑云端配置，请先下载到本地，修改完再上传")
                            return@showBottomDialog
                        }
                        ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_STATUS_ATY)
                            .withLong(com.sribs.common.ARouterPath.VAL_PROJECT_ID,beanMain.configBean?.projectId?:-1)
                            .withLong(com.sribs.common.ARouterPath.VAL_UNIT_ID,beanMain.configBean?.unitId?:-1)
                            .withLong(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_ID,beanMain.configBean?.configId?:-1)
                            .withString(com.sribs.common.ARouterPath.VAL_HOUSE_NAME,beanMain.floorNeighborNum+beanMain.floorNeighborNumEx)
                            .withInt(com.sribs.common.ARouterPath.VAL_HOUSE_TYPE,beanMain.houseType)
                            .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE,"房屋现状")
                            .withString(com.sribs.common.ARouterPath.VAL_HOUSE_POS,"房屋")
                            .navigation()
                    }
                    1->{
                        if (beanMain.localId<0){
                            showToast("无法编辑云端配置，请先下载到本地，修改完再上传")
                            return@showBottomDialog
                        }
                        var title = "${beanMain.unitNum}单元${beanMain.floorNeighborNum}${beanMain.floorNeighborNumEx}"
                        var partNo = "${beanMain.floorNeighborNum}${beanMain.floorNeighborNumEx}"
                        if (beanMain.floorNeighborNumEx.isNullOrEmpty()){

                            ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_DAMAGE_LIST_ATY)
                                .withLong(com.sribs.common.ARouterPath.VAL_PROJECT_ID,beanMain.configBean?.projectId?:-1)
                                .withLong(com.sribs.common.ARouterPath.VAL_UNIT_ID,beanMain.configBean?.unitId?:-1)
                                .withLong(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_ID,beanMain.configBean?.configId?:-1)
                                .withInt(com.sribs.common.ARouterPath.VAL_HOUSE_TYPE,beanMain.houseType)
                                .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE,title)
                                .withString(com.sribs.common.ARouterPath.VAL_PART_NO,partNo)
                                .navigation()
                        }else{
                            var name =  resources.getStringArray(R.array.house_type).first { b-> beanMain.floorNeighborNumEx.contains(b) }

                            ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_DAMAGE_DES_ATY)
                                .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE,title)
                                .withString(com.sribs.common.ARouterPath.VAL_HOUSE_POS,name)
                                .withLong(com.sribs.common.ARouterPath.VAL_PROJECT_ID,beanMain.configBean?.projectId?:-1)
                                .withLong(com.sribs.common.ARouterPath.VAL_UNIT_ID,beanMain.configBean?.unitId?:-1)
                                .withLong(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_ID,beanMain.configBean?.configId?:-1)
                                .withLong(com.sribs.common.ARouterPath.VAL_HOUSE_ROOM_ID,beanMain.houseStatusId)
                                .withInt(com.sribs.common.ARouterPath.VAL_HOUSE_TYPE,beanMain.houseType)
                                .withString(com.sribs.common.ARouterPath.VAL_PART_NO,partNo)
                                .navigation()
                        }
                    }
                    2->{
                        if (beanMain.localId<0){
                            showToast("无法编辑云端配置，请先下载到本地，修改完再上传")
                            return@showBottomDialog
                        }

                        LOG.I("123","id = ${beanMain.dataBean!!.configId}")

                        var title = "${beanMain.unitNum}单元${beanMain.floorNeighborNum}配置"
                        ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CONFIG_ATY)
                            .withInt(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_TYPE,beanMain.dataBean!!.type!!.value)
                            .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE,title)
                            .withString(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_DATA,beanMain.configBean?.toJsonStr()?:"")
                            .withString(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_FLOOR_NUM, beanMain.configBean?.floorNum?:"")
                            .withString(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_NEIGHBOR_NUM,beanMain.configBean?.neighborNum?:"")
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


    override fun onLocalConfig(l: ArrayList<RoomItemBean>) {
        refreshFinish()
        if (l.isEmpty())return
        mAdapter.setData(l)
        mStatusPresenter.getAllHouseStatus(mLocalUnitId)
    }

    override fun onHouseConfig(l: ArrayList<HouseConfigItemBean>) {
    }

    override fun onHouseStatus(l: ArrayList<HouseStatusBean>) {
        if (l.isEmpty())return
        mAdapter.updateStatus(l)
    }

    override fun bindPresenter() {
        mListPresenter.bindView(this)
        mStatusPresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg)
    }

    override fun unbindPresenter() {
        mListPresenter.unbindView()
        mStatusPresenter.unbindView()
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
            var adapter = parent.adapter as RoomListAdapter
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