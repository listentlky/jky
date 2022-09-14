package com.sribs.bdd.ui.house

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.R
import com.sribs.bdd.bean.data.ReportDataBean
import com.sribs.bdd.databinding.ActivityReportBinding
import com.sribs.bdd.databinding.ItemReportBinding
import com.sribs.bdd.module.house.HouseStatusPresenter
import com.sribs.bdd.module.house.IHouseContrast
import com.sribs.bdd.module.house.ReportPresenter
import com.sribs.common.bean.db.HouseStatusBean
import com.sribs.common.bean.db.ReportBean
import com.sribs.common.bean.db.RoomDetailBean
import com.sribs.common.ui.widget.LinePathView
import com.sribs.common.utils.FileUtil


@Route(path= com.sribs.common.ARouterPath.REPORT_ATY)
class ReportActivity:BaseActivity() ,IHouseContrast.IReportView,IHouseContrast.IHouseStatusView{

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""


    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_PROJECT_ID)
    var mProjectId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_ID)
    var mUnitId = -1L


    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_ID)
    var mConfigId = -1L


    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_HOUSE_TYPE)
    var mHouseType = -1


    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_PART_NO)
    var mPartNo= ""


    private val mHouseStatusPresenter by lazy { HouseStatusPresenter() }

    private var mData:ReportDataBean?=null

    private val mPresenter by lazy { ReportPresenter() }

    private val mBinding: ActivityReportBinding by inflate()

    private val tableColors = arrayOf(
        R.color.black_600_2,
        R.color.black_600_4
    )

    override fun deinitView() {
        unbindPresenter()
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        bindPresenter()
        initToolbar()
        initSign()
//        initReport()
        initData()
        mPresenter.getRoomDetail(mConfigId)
        mPresenter.getReport(mConfigId)
        mHouseStatusPresenter.getHouseStatus(mConfigId)
    }


    private fun initToolbar(){
        mBinding.tb.tbTitle.text = mTitle
        mBinding.tb.tb.setNavigationIcon(R.mipmap.icon_back)
        mBinding.tb.tb.setNavigationOnClickListener { finish() }
    }

    private fun initSign(){
        mBinding.reportSign.setTouch(object:LinePathView.Touch{
            override fun OnTouch(isTouch: Boolean) {
                mBinding.reportNsv.scrollable = !isTouch
            }
        })
        mBinding.reportCleanSignBtn.setOnClickListener {
            mBinding.reportSign.clear()
            mPresenter.updateReport(mProjectId,mUnitId,mConfigId,null,null,"")
        }
        mBinding.reportSaveSignBtn.setOnClickListener {
            var path = FileUtil.getTempPhotoFile(this)!!.absolutePath
            mBinding.reportSign.save(path)
            LOG.I("123","save sign path=$path")
            mPresenter.updateReport(mProjectId,mUnitId,mConfigId,null,null,path)
        }
    }

    private fun initReport(){
        var parent = mBinding.reportList
        parent.removeAllViews()
        for(i in 0..10){
            var v = ItemReportBinding.inflate(LayoutInflater.from(this),null,false)
            v.itemReportPosTv.text = "厨房"
            v.itemReportDesTv.text = "西墙窗左侧1条贯穿北倾斜裂缝，裂缝L≈1.00m,δ≈0.10mm"
            v.root.setBackgroundColor(  resources.getColor(
                if (i % 2 == 0) tableColors[1] else tableColors[0]
            ))


            var p =  LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelOffset(R.dimen._32sdp))
            v.root.layoutParams = p
            v.itemReportLl.orientation = LinearLayoutCompat.HORIZONTAL

            parent.addView(v.root)
        }
    }

    private fun initData(){
        mBinding.reportCb
        mData = ReportDataBean()
        mData!!.bindEditText(mData!!::report,mBinding.reportEt)
        mData!!.bindCheckBox(mData!!::isSave,mBinding.reportCb)
        mData!!.setOnDataChangedListener { name, new ->
            //change ui
            LOG.E("123","onDataChanged")
            if (name == mData!!::signPath.name){
                mBinding.reportSign.setImage(new)
            }


            //save
            mPresenter.updateReport(
                mProjectId,mUnitId,mConfigId,
                if(name == mData!!::isSave.name) new.toIntOrNull() else null,
                if(name == mData!!::report.name) new else null,
                if(name == mData!!::signPath.name) new else null
            )
            mHouseStatusPresenter.updateHouseInspector(mProjectId,mUnitId,mConfigId,mPartNo,mHouseType)
        }
    }

    override fun onRoomDetail(l: ArrayList<RoomDetailBean>) {
        var parent = mBinding.reportList
        parent.removeAllViews()
        if (l.isEmpty())return
        var list = l.sortedBy { it.name }
        list.forEachIndexed { i, b ->
            var v = ItemReportBinding.inflate(LayoutInflater.from(this),null,false)
            v.itemReportPosTv.text = b.name
            if (i>0 && b.name == list[i-1].name){
                v.itemReportPosTv.text = ""
            }
            v.itemReportDesTv.text = b.description
            v.root.setBackgroundColor(  resources.getColor(
                if (i % 2 == 0) tableColors[1] else tableColors[0]
            ))


            var p =  LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelOffset(R.dimen._32sdp))
            v.root.layoutParams = p
            v.itemReportLl.orientation = LinearLayoutCompat.HORIZONTAL

            parent.addView(v.root)
        }
    }

    override fun onReport(l: ArrayList<ReportBean>) {
        if (l.isNullOrEmpty())return
        var b = l[0]
        mData?.isSave = "${b.isSave}"
        mData?.report = b.report?:""
        mData?.signPath = b.signPath?:""
    }

    override fun onHouseStatus(l: ArrayList<HouseStatusBean>) {
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


}