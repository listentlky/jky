package com.sribs.bdd.ui.project

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.R
import com.sribs.bdd.bean.UnitConfigType
import com.sribs.bdd.bean.UnitConfigType.*
import com.sribs.bdd.bean.data.*
import com.sribs.bdd.databinding.ActivityUnitConfigBinding
import com.sribs.bdd.databinding.LayoutUnitConfig1Binding
import com.sribs.bdd.databinding.LayoutUnitConfig2Binding
import com.sribs.bdd.module.project.IProjectContrast
import com.sribs.bdd.module.project.ProjectConfigPresenter
import com.sribs.common.bean.db.ConfigBean
import com.sribs.common.databinding.LayoutCommonToolbarBinding
import com.sribs.common.ui.widget.TagRadioView
import com.sribs.common.utils.DialogUtil
/**
 * @date 2021/7/2
 * @author elijah
 * @Description 配置页面
 */
@Route(path = com.sribs.common.ARouterPath.PRO_CONFIG_ATY)
class ProjectConfigActivity:BaseActivity(),IProjectContrast.IConfigView {

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_TYPE)
    var mType = CONFIG_TYPE_FLOOR_BOTTOM.value

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_DATA)
    var mDataJsonStr = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_FLOOR_NUM)
    var mFloorNum = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_NEIGHBOR_NUM)
    var mNeighborNum = ""

    private val mBinding :ActivityUnitConfigBinding by inflate()

    private val mPresenter by lazy { ProjectConfigPresenter() }

    private val mTitleBinding: LayoutCommonToolbarBinding by lazy {
        LayoutCommonToolbarBinding.bind(mBinding.toolbar.root)
    }

    private var mConfigBinding:Any ?= null

    private var mProjectConfigBean:ProjectConfigBean?=null

    private var mData:ProjectConfigBaseDataBean?=null

    private var mConfigBean:ConfigBean?=null

    override fun deinitView() {
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        initToolbar()
        when(UnitConfigType.fromValue(mType)){
            CONFIG_TYPE_FLOOR_BOTTOM->{
                mBinding.viewStub1.inflate()
                mConfigBinding = LayoutUnitConfig1Binding.bind(getView().findViewById(R.id.layout_unit_config1))
                mData = ProjectConfigBottomFloorDataBean()
                var bind = (mConfigBinding as LayoutUnitConfig1Binding)
                var data = (mData as ProjectConfigBottomFloorDataBean)
                bind.unitFloorNum.setTagText("底层层号")
                data.bindTagEditView(data::floorNum,bind.unitFloorNum)
                data.bindTagEditView(data::corridorNum,bind.unitCorridorNum)
                data.bindTagCheckView(data::corridorConfig,bind.unitCorridorConfig)
                data.bindTagEditView(data::platformNum,bind.unitPlatformNum)
                data.bindTagCheckView(data::platformConfig,bind.unitPlatformConfig)
                data.setOnDataChangedListener { name, s ->
                    when(name){
                        data::floorNum.name->{
                            data.corridorNum = s
                            data.platformNum = s
                        }
                    }
                }
            }
            CONFIG_TYPE_FLOOR_NORMAL->{
                mBinding.viewStub1.inflate()
                mConfigBinding = LayoutUnitConfig1Binding.bind(getView().findViewById(R.id.layout_unit_config1))
                mData = ProjectConfigNormalFloorDataBean()
                var bind = (mConfigBinding as LayoutUnitConfig1Binding)
                var data = (mData as ProjectConfigNormalFloorDataBean)
                bind.unitFloorNum.setTagText("标准层层号")
                data.bindTagEditView(data::floorNum,bind.unitFloorNum)
                data.bindTagEditView(data::corridorNum,bind.unitCorridorNum)
                data.bindTagCheckView(data::corridorConfig,bind.unitCorridorConfig)
                data.bindTagEditView(data::platformNum,bind.unitPlatformNum)
                data.bindTagCheckView(data::platformConfig,bind.unitPlatformConfig)
                data.setOnDataChangedListener { name, s ->
                    when(name){
                        data::floorNum.name->{
                            data.corridorNum = s
                            data.platformNum = s
                        }
                    }
                }
            }
            CONFIG_TYPE_FLOOR_TOP->{
                mBinding.viewStub1.inflate()
                mConfigBinding = LayoutUnitConfig1Binding.bind(getView().findViewById(R.id.layout_unit_config1))
                mData = ProjectConfigTopFloorDataBean()
                var bind = (mConfigBinding as LayoutUnitConfig1Binding)
                var data = (mData as ProjectConfigTopFloorDataBean)
                bind.unitFloorNum.setTagText("顶层层号")
                bind.unitPlatformNum.visibility = View.GONE
                bind.unitPlatformConfig.visibility = View.GONE
                data.bindTagEditView(data::floorNum,bind.unitFloorNum)
                data.bindTagEditView(data::corridorNum,bind.unitCorridorNum)
                data.bindTagCheckView(data::corridorConfig,bind.unitCorridorConfig)
                data.setOnDataChangedListener { name, s ->
                    when(name){
                        data::floorNum.name->{
                            data.corridorNum = s
                        }
                    }
                }
            }

            CONFIG_TYPE_UNIT_BOTTOM->{
                mBinding.viewStub2.inflate()
                mConfigBinding = LayoutUnitConfig2Binding.bind(getView().findViewById(R.id.layout_unit_config2))
                mData = ProjectConfigBottomNeighborDataBean()
                var bind = (mConfigBinding as LayoutUnitConfig2Binding)
                var data = mData as ProjectConfigBottomNeighborDataBean
                var arr = resources.getStringArray(R.array.unit_config_unit_bottom_unit_config)
                bind.unitUnitTypeLl.visibility = View.GONE
                bind.unitUnitConfig.setDefaultConfigArr(ArrayList(arr.toList()))
                data.bindTagCheckView(data::neighborConfig,bind.unitUnitConfig)
            }
            CONFIG_TYPE_UNIT_NORMAL->{
                mBinding.viewStub2.inflate()
                mConfigBinding = LayoutUnitConfig2Binding.bind(getView().findViewById(R.id.layout_unit_config2))
                mData = ProjectConfigNormalNeighborDataBean()
                var bind = (mConfigBinding as LayoutUnitConfig2Binding)
                var data = mData as ProjectConfigNormalNeighborDataBean
                var arr = resources.getStringArray(R.array.unit_config_unit_normal_unit_config)
                bind.unitUnitTypeLl.visibility = View.GONE
                bind.unitUnitConfig.setDefaultConfigArr(ArrayList(arr.toList()))
                data.bindTagCheckView(data::neighborConfig,bind.unitUnitConfig)
            }
            CONFIG_TYPE_UNIT_TOP->{
                mBinding.viewStub2.inflate()
                mConfigBinding = LayoutUnitConfig2Binding.bind(getView().findViewById(R.id.layout_unit_config2))
                mData = ProjectConfigTopNeighborDataBean()
                var bind = mConfigBinding as LayoutUnitConfig2Binding
                var data = mData as ProjectConfigTopNeighborDataBean
                var arr = resources.getStringArray(R.array.unit_config_unit_normal_unit_config)
                bind.unitUnitTypeLl.visibility = View.VISIBLE
                bind.unitUnitConfig.setDefaultConfigArr(ArrayList(arr.toList()))
                bind.unitUnitConfig2.setDefaultConfigArr(ArrayList(arr.toList()))
                bind.unitUnitType.addRadioListener(
                    object: TagRadioView.IRadioChecked{
                        override fun onChecked(s: String) {
                            if (s=="非复式") {
                                bind.unitUnitConfig.setCheckTagText("配置户型")
                                bind.unitUnitConfig2.visibility = View.GONE
                            } else {
                                bind.unitUnitConfig.setCheckTagText("一层户型")
                                bind.unitUnitConfig2.setCheckTagText("二层户型")
                                bind.unitUnitConfig2.visibility = View.VISIBLE
                            }
                        }

                    })
                data.bindTagRadioView(data::neighborType,bind.unitUnitType)
                data.bindTagCheckView(data::neighborConfig,bind.unitUnitConfig)
                data.bindTagCheckView(data::neighborConfig2,bind.unitUnitConfig2)

            }
        }
        if (mConfigBinding is LayoutUnitConfig1Binding){
            formatNumber0((mConfigBinding as LayoutUnitConfig1Binding).unitFloorNum.getEditText())
            formatNumber0((mConfigBinding as LayoutUnitConfig1Binding).unitCorridorNum.getEditText())
            formatNumber0_next((mConfigBinding as LayoutUnitConfig1Binding).unitPlatformNum.getEditText(),
                (mConfigBinding as LayoutUnitConfig1Binding).unitPlatformNumNext)
        }else if(mConfigBinding is LayoutUnitConfig2Binding){

        }
        if (!mDataJsonStr.isNullOrEmpty()){
            mProjectConfigBean = ProjectConfigBean.parseJsonStr(mDataJsonStr)
        }
        if (!mFloorNum.isNullOrEmpty()){
            if(mData?.getFloor().isNullOrEmpty())
                mData?.setFloor(mFloorNum)
            if (mData?.getCorridorNumVal().isNullOrEmpty())
                mData?.setCorridorNumVal(mFloorNum)
            if (mData?.getPlatformNumVal().isNullOrEmpty())
                mData?.setPlatformNumVal(mFloorNum)
        }
        bindPresenter()
        if(mProjectConfigBean?.configId?:-1>0)
            mPresenter.getLocalConfig(mProjectConfigBean?.configId!!)
    }

    private fun formatNumber0(v:EditText){
        v.addTextChangedListener(object : TextWatcher{
            var startAdd0 = false
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                LOG.I("123","start=$start   count=$count    after=$after")
                startAdd0 = (start == 0 && after == 1 && count==0) ||
                        (start==1 && after==0 && count==1)
            }
            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {}
            override fun afterTextChanged(s: Editable?) {


                if (startAdd0 && s?.get(0)!='0'){
                    startAdd0 = false
                    s?.insert(0,"0")
                }
                if (s?.length==1 && s?.get(0)!='0'){
                    var num = s[0].toString().toInt()
                    if (num<=9){
                        s?.insert(0,"0")
                    }
                }

                if (s?.length?:0>2 && s?.get(0)=='0'){
                    s?.delete(0,1)
                }
            }
        })
    }

    private fun formatNumber0_next(v:EditText,tv: TextView){
        v.addTextChangedListener(object : TextWatcher{
            var startAdd0 = false
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                startAdd0 = (start == 0 && after == 1 && count==0) ||
                        (start==1 && after==0 && count==1)
            }
            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {}
            override fun afterTextChanged(s: Editable?) {
                if (startAdd0 && s?.get(0)!='0'){
                    startAdd0 = false
                    s?.insert(0,"0")
                }
                if (s?.length==1 && s?.get(0)!='0'){
                    var num = s[0].toString().toInt()
                    if (num<=9){
                        s?.insert(0,"0")
                    }
                }

                if (s?.length?:0>2 && s?.get(0)=='0'){
                    s?.delete(0,1)
                }

                var newStr = s.toString()
                if (newStr.isEmpty()){
                    tv.text = ""
                    return
                }
                if (newStr.startsWith("0")&& newStr.length>1)
                    newStr = newStr.substring(1)
                var num = newStr.toInt()
                var next = String.format("%02d",num+1)
                tv.text = "-$next"

            }
        })
    }
    private fun initToolbar(){
        mTitleBinding.tbTitle.text = mTitle
        mTitleBinding.tb.setNavigationOnClickListener {
            DialogUtil.showMsgDialog(this,"保存当前配置",{
                // save
                if (mData?.isEmpty()==true){
                    showToast(mData?.errorMsg)
                    return@showMsgDialog
                }
                var newConfig = ConfigBean(
                    projectId = mProjectConfigBean?.projectId,
                    bldId = 1,
                    unitId = mProjectConfigBean?.unitId,
                    floorIdx = mProjectConfigBean?.floorIdx,
                    neighborIdx = mProjectConfigBean?.neighborIdx,
                    configType = mData?.type?.value,
                    floorNum = mData?.getFloor(),
                    neighborNum = mNeighborNum,
                    corridorNum = mData?.getCorridorNumVal(),
                    corridorConfig = mData?.getCorridorConfigVal(),
                    platformNum = mData?.getPlatformNumVal(),
                    platformConfig = mData?.getPlatformConfigVal(),
                    config1 = mData?.getConfig1Val(),
                    config2 = mData?.getConfig2Val(),
                    unitType = if(mData!!.getNeighborTypeVal()=="复式") 1 else 0,

                    ).also {
                    if (mProjectConfigBean?.bldId?:-1>0){
                        it.bldId = mProjectConfigBean?.bldId
                    }
                    if (mProjectConfigBean?.configId?:-1>0){
                        it.configId = mProjectConfigBean?.configId
                    }
                    if (mProjectConfigBean?.floorIdx?:-1>=0){
                        it.floorIdx = mProjectConfigBean?.floorIdx
                    }
                    if (mProjectConfigBean?.neighborIdx?:-1>=0){
                        it.neighborIdx = mProjectConfigBean?.neighborIdx
                    }
                    if (mConfigBean!=null){
                        it.createTime = mConfigBean!!.createTime
                    }

                }
                if (mConfigBean!=null && mConfigBean!!.isSame(newConfig)){
                    finish()
                    return@showMsgDialog
                }


                LOG.I("123","confidId=${mProjectConfigBean?.configId}")
                mPresenter.updateLocalConfig(newConfig)
                finish()
                LOG.I("123","mdata=$mData")
            }){
                finish()
            }
        }
        mTitleBinding.tb.setNavigationIcon(R.mipmap.icon_back)
    }

    override fun onCopyError(msg: String) {
    }

    override fun getContext(): Context? = this

    override fun onLocalConfig(l: List<ConfigBean>) {
        if (l.isEmpty())return
        var b = l[0]
        LOG.I("123","onLocalConfig  $b")
        mProjectConfigBean?.projectId = b.projectId?:-1
        mProjectConfigBean?.unitId = b.unitId?:-1
        mProjectConfigBean?.configId = b.configId?:-1
        mProjectConfigBean?.floorIdx = b.floorIdx?:-1
        mProjectConfigBean?.neighborIdx = b.neighborIdx?:-1
        mProjectConfigBean?.configType = b.configType?:-1
        mNeighborNum = b.neighborNum?:""
        when(mData){
            is ProjectConfigBottomFloorDataBean -> {
                (mData as ProjectConfigBottomFloorDataBean).also {
                    it.floorNum = b.floorNum?:""
                    it.corridorNum = b.corridorNum?:""
                    it.corridorConfig = b.corridorConfig?:""
                    it.platformNum = b.platformNum?:""
                    it.platformConfig = b.platformConfig?:""
                }
            }
            is ProjectConfigNormalFloorDataBean->{
                (mData as ProjectConfigNormalFloorDataBean).also {
                    it.floorNum = b.floorNum?:""
                    it.corridorNum = b.corridorNum?:""
                    it.corridorConfig = b.corridorConfig?:""
                    it.platformNum = b.platformNum?:""
                    it.platformConfig = b.platformConfig?:""
                }
            }
            is ProjectConfigTopFloorDataBean->{
                (mData as ProjectConfigTopFloorDataBean).also {
                    it.floorNum = b.floorNum?:""
                    it.corridorNum = b.corridorNum?:""
                    it.corridorConfig = b.corridorConfig?:""
                }
            }
            is ProjectConfigBottomNeighborDataBean->{
                (mData as ProjectConfigBottomNeighborDataBean).also {
                    it.floorNum = b.floorNum?:""
                    it.neighborConfig = b.config1?:""
                }
            }
            is ProjectConfigNormalNeighborDataBean->{
                (mData as ProjectConfigNormalNeighborDataBean).also {
                    it.floorNum = b.floorNum?:""
                    it.neighborConfig = b.config1?:""
                }
            }
            is ProjectConfigTopNeighborDataBean -> {
                (mData as ProjectConfigTopNeighborDataBean).also {
                    it.floorNum = b.floorNum?:""
                    it.neighborConfig = b.config1?:""
                    it.neighborConfig2 = b.config2?:""
                    it.neighborType = if (b.unitType == 1 )"复式" else "非复式"
                }
            }

        }


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