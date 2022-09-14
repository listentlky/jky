package com.sribs.bdd.ui.building

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.sribs.bdd.R
import com.sribs.bdd.bean.data.*
import com.sribs.bdd.bean.data.recycler.*
import com.sribs.bdd.databinding.ActivityUnitCopyBinding
import com.sribs.bdd.databinding.ItemUnitCopyChipBinding
import com.sribs.bdd.module.project.IProjectContrast
import com.sribs.bdd.module.project.ProjectConfigPresenter
import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.ConfigBean
import com.sribs.common.databinding.LayoutCommonToolbarBinding
import com.sribs.common.utils.DialogUtil

/**
 * @date 2021/7/5
 * @author elijah
 * @Description
 */
class BuildingFloorCopyActivity:BaseActivity(),IProjectContrast.IConfigView {

    private var mTitle: String? = ""
    private var mDrawingsList: String? = ""
    private var mCopyListJsonStr: String? = ""
    private var mFloorId: String? = ""
    private var mFloorTotalNum: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        mTitle = intent?.getStringExtra(ARouterPath.VAL_COMMON_TITLE)?:""
//        mFloorId = intent?.getStringExtra(ARouterPath.VAL_UNIT_CONFIG_FLOOR_NUM)?:""
//        mFloorTotalNum = intent?.getStringExtra(ARouterPath.FLOOR_TOTAL_NUM)?:""

        LOG.I("leon","FloorCopyActivity onCreate mFloorTotalNum=" + mFloorTotalNum)
    }

    private val mPresenter by lazy {
        ProjectConfigPresenter()
    }

    var mData: ProjectConfigBean?=null

//    var mAllList:ArrayList<FloorConfigBean> = ArrayList<FloorConfigBean>()

    private var mIsFloor = false

    private val mBinding : ActivityUnitCopyBinding by inflate()

    private val mTitleBinding: LayoutCommonToolbarBinding by lazy {
        LayoutCommonToolbarBinding.bind(mBinding.toolbar.root)
    }

    private val mAdapter: FloorCopyAdapter by lazy {
        FloorCopyAdapter(object :FloorCopyAdapter.OnCheckedListener{
            override fun onChecked(bean: ProjectFloorConfigCopyBean, pos: Int, isChecked: Boolean) {
                LOG.I("leon","onChecked in")
                if (isChecked){
                    addClip(bean.name)
                }else{
                    removeClip(bean.name)
                }
            }
        })
    }

    override fun deinitView() {
        unbindPresenter()
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        initSelRv()
        bindPresenter()
        initToolbar()
        initClip()
        initData()
        initSel()

        mBinding.unitCopySelectAllCb.setOnCheckedChangeListener {buttonView, isChecked ->
            if (isChecked) {
                //Do Whatever you want in isChecked
            }
            LOG.I("leon","mBinding.unitCopySelectAllCb.setOnCheckedChangeListener in")
        }
        mBinding.unitCopySelectAllCb.setOnClickListener {
            var v = it as AppCompatCheckBox
            LOG.I("leon","mBinding.unitCopySelectAllCb.setOnClickListener in")
            mAdapter.selAll(v.isChecked)
        }
    }

    private fun initSelRv(){

        mTitle = intent?.getStringExtra(ARouterPath.VAL_COMMON_TITLE)?:""
        mFloorId = intent?.getStringExtra(ARouterPath.VAL_UNIT_CONFIG_FLOOR_NUM)?:""
        mFloorTotalNum = intent?.getStringExtra(ARouterPath.FLOOR_TOTAL_NUM)?:""

        var list = ArrayList<ProjectFloorConfigCopyBean>()
//        var map = Gson().fromJson<ArrayMap<String, String>>(mCopyListJsonStr, ArrayMap::class.java)

        LOG.I("leon","mFloorTotalNum=" + mFloorTotalNum)
        var floorConfigBean: ProjectFloorConfigCopyBean
        for(i in 1..mFloorTotalNum!!.toInt()){
            floorConfigBean = ProjectFloorConfigCopyBean(i.toString(), false, i.toString() + "层")
            list.add(floorConfigBean)
        }

        mBinding.unitCopySelectRv.layoutManager = LinearLayoutManager(this)
        mBinding.unitCopySelectRv.adapter = mAdapter
        LOG.I("123","l=$list")
        mAdapter.setData(list)
//        LOG.I("123","map=$map")
    }

    private fun initSel(){
//        if(!mIsFloor){
//            var myNeighborIdx = mData?.neighborIdx!!
//            var myFloorIdx = mData?.floorIdx!!
        mAdapter.setChecked(mFloorId!!.toInt())
//        }
    }

    private fun initClip(){
        mBinding.unitCopyCg.removeAllViews()
        mBinding.unitCopySelectCb.setOnCheckedChangeListener { buttonView, isChecked ->
            mBinding.unitCopySelectRv.visibility = if (isChecked)View.VISIBLE else View.GONE
        }
        mBinding.unitCopyCgLl.setOnClickListener {
            mBinding.unitCopySelectCb.isChecked =  !mBinding.unitCopySelectCb.isChecked
            mBinding.unitCopySelectRv.visibility = if (mBinding.unitCopySelectCb.isChecked)View.VISIBLE else View.GONE
        }
    }

    private fun initData(){

    }

    private fun addClip(s:String){
        LOG.I("leon","addClip s=" + s)
        var chipText = ItemUnitCopyChipBinding.inflate(LayoutInflater.from(this),null,false)
        chipText.itemUnitCopyChip.text = s
        chipText.itemUnitCopyChip.setOnCloseIconClickListener {

            mAdapter?.setChecked(  (it as Chip).text.toString(),false)
            removeClip(it)
        }
        mBinding.unitCopyCg.addView(chipText.root)
    }

    private fun removeClip(s:String){
        mBinding.unitCopyCg.children.forEachIndexed{index, view ->
            if (view is Chip && view.text==s){
                mBinding.unitCopyCg.removeViewAt(index)
                mBinding.unitCopySelectAllCb.isChecked = false
                return@forEachIndexed
            }
        }
    }

    private fun removeClip(v:View){
        mBinding.unitCopyCg.removeView(v)
        mBinding.unitCopySelectAllCb.isChecked = false
    }

    private fun initToolbar(){
        mTitleBinding.tbTitle.text = mTitle
        mTitleBinding.tb.setNavigationOnClickListener {
            DialogUtil.showMsgDialog(this,"确认复制配置？",{
                // save
                var list: ArrayList<ProjectFloorConfigCopyBean> = mAdapter.getAllSel() as ArrayList<ProjectFloorConfigCopyBean>
                val gson = Gson()
                val jsonFloorSelList: String = gson.toJson(list)
                LOG.I("leon","selected floor list=" + jsonFloorSelList)
                val result = Intent().apply {
                    putExtra("selFloorList", jsonFloorSelList)
                }
                setResult(Activity.RESULT_OK, result)
                finish()
            }){
                finish()
            }
        }
        mTitleBinding.tb.setNavigationIcon(R.mipmap.icon_back)
    }

    override fun onLocalConfig(l: List<ConfigBean>) {
    }

    override fun getContext(): Context? = this

    override fun bindPresenter() {
        mPresenter.bindView(this)
    }

    override fun onCopyError(msg: String) {
        DialogUtil.showMsgDialog(this,msg,{})
    }

    override fun onMsg(msg: String) {
        showToast(msg)
    }

    override fun unbindPresenter() {
        mPresenter.unbindView()
    }

    companion object {
        const val key_sel_floor_case = "selFloorList"
    }
}

