package com.sribs.bdd.ui.project

import android.content.Context
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.sribs.bdd.R
import com.sribs.bdd.bean.ProjectUnitConfigCopyBean
import com.sribs.bdd.bean.UnitConfigType
import com.sribs.bdd.bean.data.*
import com.sribs.bdd.databinding.ActivityUnitCopyBinding
import com.sribs.bdd.databinding.ItemUnitCopyChipBinding
import com.sribs.bdd.module.project.IProjectContrast
import com.sribs.bdd.module.project.ProjectConfigPresenter
import com.sribs.bdd.ui.adapter.ConfigCopyAdapter
import com.sribs.common.bean.db.ConfigBean
import com.sribs.common.databinding.LayoutCommonToolbarBinding
import com.sribs.common.utils.DialogUtil

/**
 * @date 2021/7/5
 * @author elijah
 * @Description
 */
@Route(path= com.sribs.common.ARouterPath.PRO_COPY_ATY)
class ProjectCopyActivity:BaseActivity(),IProjectContrast.IConfigView {

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_DATA)
    var mDataJsonStr = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_COPY_LIST)
    var mCopyListJsonStr = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_TYPE)
    var mConfigType = -1

    private val mPresenter by lazy {
        ProjectConfigPresenter()
    }

    var mData: ProjectConfigBean?=null

    var mAllList:List<ProjectConfigBean> ?=null

    private var mIsFloor = false

    private val mBinding : ActivityUnitCopyBinding by inflate()

    private val mTitleBinding: LayoutCommonToolbarBinding by lazy {
        LayoutCommonToolbarBinding.bind(mBinding.toolbar.root)
    }

    private val mAdapter:ConfigCopyAdapter by lazy {
        ConfigCopyAdapter(object :ConfigCopyAdapter.OnCheckedListener{
            override fun onChecked(bean: ProjectUnitConfigCopyBean, pos: Int, isChecked: Boolean) {
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
        bindPresenter()
        initToolbar()
        initClip()
        initData()
        initSelRv()

        initSel()
        mBinding.unitCopySelectAllCb.setOnClickListener {
            var v = it as AppCompatCheckBox
            mAdapter.selAll(v.isChecked)
        }
    }

    private fun initSelRv(){
        var l = ArrayList<ProjectUnitConfigCopyBean>()
        var map = Gson().fromJson<ArrayMap<String,String>>(mCopyListJsonStr,ArrayMap::class.java)
        mAllList = map.map {
            ProjectConfigBean.parseJsonStr(it.value)
        }.toList()

        map.filter { if(mIsFloor)it.key.contains("层") else !it.key.contains("层") }.forEach {
            l.add(ProjectUnitConfigCopyBean(it.value,it.key,false))
        }
        mBinding.unitCopySelectRv.layoutManager = LinearLayoutManager(this)
        mBinding.unitCopySelectRv.adapter = mAdapter
        LOG.I("123","l=$l")
        mAdapter.setData(l)
        LOG.I("123","map=$map")
    }
    private fun initSel(){
        if(!mIsFloor){
            var myNeighborIdx = mData?.neighborIdx!!
            var myFloorIdx = mData?.floorIdx!!
            mAdapter.setChecked(myFloorIdx,myNeighborIdx)
        }
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

        if (mConfigType<0)return
        if (mDataJsonStr.isNullOrEmpty())return
        mData = ProjectConfigBean.parseJsonStr(mDataJsonStr)

        mIsFloor = when(mConfigType){
            UnitConfigType.CONFIG_TYPE_FLOOR_BOTTOM.value->{
                true
            }
            UnitConfigType.CONFIG_TYPE_FLOOR_NORMAL.value->{
                true
            }
            UnitConfigType.CONFIG_TYPE_FLOOR_TOP.value   ->{
                true
            }
            UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM.value ->{
                false
            }
            UnitConfigType.CONFIG_TYPE_UNIT_NORMAL.value ->{
                false
            }
            UnitConfigType.CONFIG_TYPE_UNIT_TOP.value    ->{
                false
            }
            else                                         ->{
                false
            }
        }

    }

    private fun addClip(s:String){
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
                if (mData==null){
                    showToast("复制失败，数据存在错误")
                    finish()
                }
                var l = mAdapter.getAllSel().map {
                    ProjectConfigBean.parseJsonStr(it.id)
                }.toList()
                if(mIsFloor){
                    var floorIdxs = l.map { it.floorIdx }
                    var des = ArrayList<Pair<ProjectConfigBean,ArrayList<ProjectConfigBean>>>()
                    l.forEach {
                        des.add(Pair(it,ArrayList()))
                    }
                    mAllList?.filter {  floorIdxs.contains(it.floorIdx) &&
                            (it.configType == UnitConfigType.CONFIG_TYPE_UNIT_BOTTOM.value ||
                                    (it.configType == UnitConfigType.CONFIG_TYPE_UNIT_NORMAL.value) ||
                                    (it.configType == UnitConfigType.CONFIG_TYPE_UNIT_TOP.value))
                    }?.forEach {
                        des.first{ d->d.first.floorIdx == it.floorIdx }.second
                            .add(it)

                    }
                    var floorL = des.map { it.first }
                    var roomL = des.map { it.second }.flatten()
                    var checkL = ArrayList<ProjectConfigBean>().apply {
                        this.addAll(floorL)
                        this.addAll(roomL)
                    }
                    mPresenter.checkConfig(checkL){b,msg->
                        if (b){
                            mPresenter.copyFloor(mData!!,des)
                            finish()
                        }else{
                            DialogUtil.showMsgDialog(this,msg!!,"覆盖","取消",{
                                mPresenter.copyFloor(mData!!,des)
                                finish()
                            })
                        }
                    }
                }else{
                    mPresenter.checkConfig(l){ b, msg->
                        if(b){
                            mPresenter.copyRoom(mData!!.configId,l)
                            finish()
                        }else{
                            DialogUtil.showMsgDialog(this,msg!!,"覆盖","取消",{
                                mPresenter.copyRoom(mData!!.configId,l)
                                finish()
                            })
                        }
                    }
                }
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


}