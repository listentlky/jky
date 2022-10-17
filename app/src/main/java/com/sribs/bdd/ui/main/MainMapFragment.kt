package com.sribs.bdd.ui.main

import android.Manifest
import android.location.Geocoder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.utils.CoordinateConverter
import com.cbj.sdk.databinding.LayoutBaseListMvpBinding
import com.cbj.sdk.libbase.rxbus.RxBus
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseListFragment
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.bean.MainProjectBean
import com.sribs.bdd.databinding.FragmentMainMapBinding
import com.sribs.bdd.databinding.ItemMainListBinding
import com.sribs.bdd.module.main.IMainListContrast
import com.sribs.bdd.module.main.MainListPresenter
import com.sribs.bdd.ui.adapter.MainListAdapter
import com.sribs.bdd.v3.event.RefreshProjectListEvent
import com.sribs.common.bean.db.DrawingBean
import com.sribs.common.bean.db.UnitBean
import com.sribs.common.ui.widget.CommonLinearDividerItemDecoration
import com.tbruyelle.rxpermissions2.RxPermissions

/**
 * @date 2021/6/23
 * @author elijah
 * @Description
 */
@Route(path = com.sribs.common.ARouterPath.MAIN_MAP_FGT)
class MainMapFragment:BaseListFragment<MainProjectBean, ItemMainListBinding>(R.layout.fragment_main_map),
    IMainListContrast.IView{

    private val mBinding : FragmentMainMapBinding by bindView()

    private val mListBinding by lazy {
        LayoutBaseListMvpBinding.bind(mBinding.mainMapListLl)
    }

    private var mCurProjectType:String? = null

    private val mPresenter by lazy { MainListPresenter() }

    private val mAdapter : MainListAdapter by lazy { MainListAdapter(object: MainListAdapter.ICallback{
        override fun onMoreChecked(beanMain: MainProjectBean, pos: Int, checked: Boolean) {
            (activity as DamageMainActivity).showBottomDialog(checked, pos, beanMain)
        }

        override fun onCardSelect(beanMain: MainProjectBean, pos: Int) {
            (activity as DamageMainActivity).selectProject(beanMain)
            try {
                showMaker(beanMain.address)
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }) }

    override fun onResume() {
        super.onResume()
        mBinding.mainMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mBinding.mainMapView.onPause()
    }


    override fun deinitView() {
        super.deinitView()
        unbindPresenter()
        mLocationClient?.stop()
        mBaiduMap?.isMyLocationEnabled = false
        mBinding?.mainMapView.onDestroy()
    }

    override fun initView() {
        super.initView()
        bindPresenter()

        mCurProjectType = (activity as DamageMainActivity).mFrom
        println("leon MainListFragment mCurProjectType = ${mCurProjectType}")

        initProject()
        RxPermissions(this).request(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ).subscribe {
            if (it){
                initMap()
            }
        }

        RxBus.getDefault().toObservable<RefreshProjectListEvent>(RefreshProjectListEvent::class.java)
            .subscribe {
                if (it.isRefresh) {
                    mPresenter.getProjectList()
                }
            }
    }

    override fun getAdapter(): BaseListAdapter<MainProjectBean, ItemMainListBinding> = mAdapter

    override fun getLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(context)

    override fun getListRecyclerView(): RecyclerView = mListBinding.baseListRv

    override fun getListSwipeRefreshLayout(): SwipeRefreshLayout = mListBinding.baseListSrl

    override fun isEnableRefresh(): Boolean = true

    override fun isEnableScroll(): Boolean = true

    override fun onRefresh() {
        super.onRefresh()
        LOG.I("123","onRefresh")
        mPresenter.getProjectList()
    }

    override fun onLoad(curPage: Int) {
        super.onLoad(curPage)
    }

    override fun onProjectList(l: ArrayList<MainProjectBean>) {
        mAdapter.setData(l)
        refreshFinish()
    }

    override fun onAllUnitsInProject(r: ArrayList<UnitBean>) {

    }

    override fun onAllDrawingsInProject(r: ArrayList<DrawingBean>) {

    }

    override fun bindPresenter() {
        mPresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg?:"")
    }

    override fun onNetError(msg: String?) {
        showToast(msg?:"")
    }

    override fun unbindPresenter() {
        mPresenter.unbindView()
    }

    fun menuUnSel(pos: Int) {
        mAdapter.menuUnSel(pos)
    }

    private fun initProject(){

        mListRv?.addItemDecoration(
            CommonLinearDividerItemDecoration(
                resources.getDimensionPixelOffset(R.dimen.main_item_margin),
                resources.getDimensionPixelOffset(R.dimen.main_item_margin_top)
            )
        )
        mPresenter.getProjectList()
    }
    var mLocationClient :LocationClient?=null
    var mMyGeo :Geocoder?= null
    var mBaiduMap:BaiduMap?=null

    private fun initMap(){
        // TODO STEP 0 地图view
        mBinding.mainMapView.showZoomControls(false)

        // TODO STEP 1 地图
        mBaiduMap = mBinding.mainMapView.map
        mBaiduMap!!.mapType = BaiduMap.MAP_TYPE_NORMAL
        mBaiduMap!!.isMyLocationEnabled = true
        mBaiduMap!!.setIndoorEnable(true)

        //TODO STEP 1 默认获取自己的位置
        mLocationClient = LocationClient(context)
        mLocationClient!!.registerLocationListener(object :BDAbstractLocationListener(){
            override fun onReceiveLocation(b: BDLocation?) {
                LOG.I("123","BDLocation  $b    ${b?.floor}")
                if (b?.floor!=null){
                    LOG.I("123","startInDoorMode  ${b.floor}")
                    mLocationClient?.startIndoorMode()
                }


                var latitude = b?.latitude //纬度
                var longitude = b?.longitude // 经度
                LOG.I("123","纬度$latitude  经度$longitude  地址${b?.addrStr}")
                var locData = MyLocationData.Builder()
                    .accuracy(b!!.radius)
                    .direction(b.direction)
                    .latitude(b.latitude)
                    .longitude(b.longitude)
                    .build()
//                LOG.I("123","set my locData")
                mBaiduMap?.setMyLocationData(locData)
                mBaiduMap?.setMapStatus(MapStatusUpdateFactory.newLatLng(LatLng(b.latitude,b.longitude)))
                mBaiduMap?.setMapStatus(MapStatusUpdateFactory.zoomTo(21f))
            }
        })
        var opt = LocationClientOption()
        opt.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        opt.setCoorType("bd09ll") //百度坐标
        opt.setScanSpan(0) //定为 5分种一次
        opt.isOpenGps = true
        opt.isLocationNotify = false // gps刷新
        opt.setWifiCacheTimeOut(5*60*1000)
        opt.setEnableSimulateGps(false)
        opt.setIsNeedAddress(true)
        opt.setNeedNewVersionRgc(true)//新版本的地址信息
        mLocationClient!!.locOption = opt
        mLocationClient!!.start()
        //TODO STEP 2 初始化地址转换器
        mMyGeo = Geocoder(context)

    }

    var mMarkerOverlay:Overlay?=null

    private fun showMaker(address:String){

            var addr = mMyGeo?.getFromLocationName(address, 1)?.firstOrNull() ?: return
            var srcLatLng = LatLng(addr.latitude, addr.longitude)
            var desLatLng = CoordinateConverter()
                .from(CoordinateConverter.CoordType.GPS)
                .coord(srcLatLng)
                .convert()

            LOG.I("123","addr=$addr")
            LOG.I("123","des  $desLatLng")
            var point = LatLng(desLatLng.latitude,desLatLng.longitude)
            if (mMarkerOverlay!=null){
                mBaiduMap?.removeOverLays(listOf(mMarkerOverlay))
            }
            var bda = BitmapDescriptorFactory.fromResource(R.mipmap.icon_baidu_map_location)

            LOG.I("123","bda=$bda")
            var opt = MarkerOptions()
                .position(point)
                .icon(bda)
            mMarkerOverlay = mBaiduMap?.addOverlay(opt)
            mBaiduMap?.setMapStatus(MapStatusUpdateFactory.newLatLng(LatLng(desLatLng.latitude,desLatLng.longitude)))
            mBaiduMap?.setMapStatus(MapStatusUpdateFactory.zoomTo(21f))


    }

    fun search(time: ArrayList<String>){
        mAdapter.setSearch(time)
    }

    fun search(keyword:String){
        mAdapter.setSearch(keyword)
    }
}