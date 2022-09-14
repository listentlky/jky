package com.sribs.bdd.ui.project

import android.annotation.SuppressLint
import android.util.ArrayMap
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.forEachIndexed
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.google.gson.Gson
import com.sribs.bdd.R
import com.sribs.bdd.bean.data.ProjectConfigBean
import com.sribs.bdd.bean.data.ProjectUnitDetailDataBean
import com.sribs.bdd.databinding.FragmentProjectUnitBinding
import com.sribs.bdd.module.project.IProjectContrast
import com.sribs.bdd.module.project.ProjectConfigPresenter
import com.sribs.bdd.module.project.ProjectUnitPresenter
import com.sribs.bdd.ui.widget.UnitFloorLayout
import com.sribs.common.bean.db.ConfigBean
import com.sribs.common.bean.db.UnitBean
import com.sribs.common.utils.DialogUtil

/**
 * @date 2021/6/30
 * @author elijah
 * @Description 单元页面
 */
@Route(path= com.sribs.common.ARouterPath.PRO_DETAIL_UNIT)
class ProjectUnitRoomFragment:BaseFragment(R.layout.fragment_project_unit),IProjectContrast.IConfigView,IProjectContrast.IUnitView {
    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_UNIT_IDX)
    var mIdx = 0

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_SHOW_DEL)

    var mShowDelBtn = false

    var mEnable = true

    var mLocalUnitId = -1L

    private val mConfigPresenter by lazy {
        ProjectConfigPresenter()
    }

    private val mUnitPresenter by lazy {
        ProjectUnitPresenter()
    }

    private var mFloorList:ArrayList<UnitFloorLayout> = ArrayList()
    private var mNeighborSize:Int = 0

    private var mDeleteConfigList:ArrayList<Long> = ArrayList()

    val mMyCode :String by lazy {
        "${this.hashCode()}"
    }

    val mData:ProjectUnitDetailDataBean by lazy {
        ProjectUnitDetailDataBean(mIdx)
    }

    private val mBinding:FragmentProjectUnitBinding by bindView()

    override fun deinitView() {
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun initView() {
        mBinding.unitDelBtn.visibility = if(mShowDelBtn) View.VISIBLE else View.GONE
        initData()

        mBinding.unitDelBtn.setOnClickListener {
            (parentFragment as ProjectUnitFragment)
                .removeUnit(getCurIndex())
        }
        mBinding.unitUploadBtn.setOnClickListener {
            DialogUtil.showMsgDialog(context!!,"是否覆盖","不覆盖","覆盖",{
                (parentFragment as ProjectUnitFragment).showPb(true)
                mUnitPresenter.uploadUnit(mLocalUnitId,false)
            },{
                (parentFragment as ProjectUnitFragment).showPb(true)
                mUnitPresenter.uploadUnit(mLocalUnitId,true)
            })
        }


        mBinding.unitCopyUnitBtn.setOnClickListener {
            copyUnit()
        }
        bindPresenter()
    }

    private fun initData(){
        mData.bindTagEditView(mData::unitNumber,mBinding.unitNumber)
            .bindTagEditView(mData::totalFloor,mBinding.unitBuilderTotalFloor)
            .bindTagEditView(mData::totalNeighbor,mBinding.unitMaxNeighbor)
            .bindTagRadioView(mData::floorType,mBinding.unitFloorType)
            .setOnDataChangedListener { field, new ->
                when(field){
                    mData::unitNumber.name-> {
                        LOG.I("123","$this ${mIdx} unitNumber change $new")
//                        var unitNo = if (NumberUtils.isNumber(new)) "单元${new}" else new
                        var unitNo = new //fixme 20211028 不自动填充单元号
                        mUnitPresenter.updateLocalUnit(unitNo, null, null, null)
                        (parentFragment as ProjectUnitFragment)
                            .updateTagView(mIdx,unitNo)
                    }
                    mData::totalFloor.name -> {
                        LOG.I("123","floor name = ${new}  toInt=${new.toIntOrNull()}")
                        if (new.isNullOrEmpty() )return@setOnDataChangedListener
                        mUnitPresenter.updateLocalUnit(null, new.toIntOrNull(), null, null)
                        setFloor(new.toIntOrNull()?:0)
                        setTableBackground()
                    }
                    mData::totalNeighbor.name -> {
                        if (new.isNullOrEmpty())return@setOnDataChangedListener
                        mUnitPresenter.updateLocalUnit(null, null, new.toIntOrNull(), null)
                        setNeighbor(new.toIntOrNull()?:0)
                        setTableBackground()
                    }
                    mData::floorType.name->{
                        if (new.isNullOrEmpty())return@setOnDataChangedListener
                        mUnitPresenter.updateLocalUnit(null, null, null,
                        if (new=="双楼梯间") 1 else 0)
                    }
                }
            }
    }

    private fun getCurIndex():Int{
        return  (parentFragment as ProjectUnitFragment).getVpCurIndex()
    }

    fun showDelBtn(b:Boolean){
        mShowDelBtn = b
        try {
            mBinding.unitDelBtn.visibility = if(b) View.VISIBLE else View.GONE
        }catch (e:Exception){}
    }

    private fun setFloor(num:Int){
        val parent = mBinding.unitFloorLl
        val cont = parent.childCount
        when {
            num-cont>0 -> { // 添加
                // 修改原来的顶层
//                var lastIndex = mFloorList.lastIndex
//                if (lastIndex>0) {
//                    var last = mFloorList.lastOrNull()
//                    last?.floor = "标准层"
//                    last?.bCopy = true
//                }
                // 从原来 cont-1开始加
                while (parent.childCount < num){

                    var i = when(parent.childCount){
                        0 -> 0
                        1 -> 1
                        else -> parent.childCount - 1
                    }
                    var floorName = when(i){
                        0->"底层"
                        1->{
                            if (parent.childCount - 1 == 1){
                                "标准层"
                            }else{
                                "顶层"
                            }
                        }
                        num-1 ->"顶层"
                        else->"标准层"
                    }
                    LOG.I("123","i=$i")
                    val floorLayout = UnitFloorLayout(requireContext(),mMyCode,"${i+1}",floorName,true,mNeighborSize,
                        object :IConfigListener{
                            override fun onConfigClick(
                                floorNum: String,
                                neighborNum: String?,
                                des: String,
                                dataBean: ProjectConfigBean?,
                            ) {

                                LOG.I("123","onConfigClick  title=${des}配置   id=$id  type=${dataBean?.configType?:-1}")
                                ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CONFIG_ATY)
                                    .withInt(
                                        com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_TYPE,
                                        dataBean?.configType?:-1)
                                    .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE,"${des}配置")
                                    .withString(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_DATA,parseConfigBeanJson(dataBean))
                                    .withString(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_FLOOR_NUM, floorNum)
                                    .withString(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_NEIGHBOR_NUM,neighborNum?:"")
                                    .navigation()
                            }

                            override fun onCopyClick(
                                des: String,
                                dataBean: ProjectConfigBean?,
                            ) {

                                ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_COPY_ATY)
                                    .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE,"复制${des}配置")
                                    .withString(com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_DATA,parseConfigBeanJson(dataBean))
                                    .withString(
                                        com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_COPY_LIST,
                                        parseCopyListJson(dataBean?.neighborIdx == -1))
                                    .withInt(
                                        com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_TYPE,
                                        dataBean?.configType?:-1)
                                    .navigation()
                            }
                            override fun onDrawingsChangeClick(
                                floorId: String
                            ) {

                            }
                        }).also {

                            it.mFloorIdx = i
                            it.unitId = mLocalUnitId
                    }
                    var p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                    parent.addView(floorLayout,i,p)
                    mFloorList.add(i,floorLayout)
                }
                //添加完毕后修改 顶层号
                var lastIndex = mFloorList.lastIndex
                if (lastIndex>0){
                    var last = mFloorList.lastOrNull()
                    last?.mFloorNum = mFloorList.size.toString()
                    last?.mFloorStr = "顶层"
                    last?.mFloorIdx = mFloorList.size -1

                }
            }
            num-cont<0 -> { // 减少

                while(parent.childCount > num){
                    var i = when(parent.childCount){
                        1 -> 0
                        2 -> 1
                        else -> parent.childCount - 2
                    }
                    var floor = mFloorList.removeAt(i)
                    if(floor.mData?.configId?:0>0){
                        mDeleteConfigList.add(floor.mData!!.configId)
                    }
                    floor.mNeighborList.forEach {
                        if (it.mData?.configId?:0>0){
                            mDeleteConfigList.add(it.mData!!.configId)
                        }
                    }
                    floor.remove()

                    parent.removeView(floor)
                }

                var lastIndex = mFloorList.lastIndex
                if (lastIndex>0){
                    var last = mFloorList.lastOrNull()
                    last?.mFloorNum = mFloorList.size.toString()
                    last?.mFloorStr = "顶层"
                    last?.mFloorIdx = mFloorList.size -1
                }

            }
        }
    }

    private fun parseConfigBeanJson(b:ProjectConfigBean?):String?{
        if (b==null)return ""
        b.projectId = (activity as ProjectCreateActivity).mLocalProjectId.toLong()
        b.unitId = mLocalUnitId
        return b.toJsonStr()
    }

    private fun parseCopyListJson(isFloor:Boolean):String{
        var map = ArrayMap<String,String>()
        mFloorList?.forEachIndexed { i, unitFloorLayout ->
            map[unitFloorLayout.mFloorNum+"层"] = parseConfigBeanJson(unitFloorLayout.getConfigData())
            unitFloorLayout.mNeighborList.forEachIndexed { j, unitNeighborLayout ->
                map["${unitNeighborLayout.mFloorNumber}${unitNeighborLayout.mNeighborNumber}室"] =
                    parseConfigBeanJson(unitNeighborLayout.getConfigData())
            }
        }
        return Gson().toJson(map,Map::class.java)
    }


    private fun setNeighbor(num:Int){
        mNeighborSize = num
        mFloorList?.forEach {
            it.neighborSize = num
        }
    }

    private val tableColors = arrayOf(
        R.color.black_600_2,
        R.color.black_600_4
    )
    private fun setTableBackground(){
        val parent = mBinding.unitFloorLl
        val cont = parent.childCount
        var i = 0
        parent.forEachIndexed { index, view ->
            i+=1
            if (view is LinearLayout) {
                view.findViewById<LinearLayout>(R.id.unit_floor_ll)?.setBackgroundColor(
                    requireContext().resources.getColor(
                        if (i % 2 == 0) tableColors[1] else tableColors[0]
                    )
                )
                var floorLayout = view.findViewById<LinearLayout>(R.id.unit_floor_neighbor_ll)
                floorLayout?.forEachIndexed { childIndex, childView ->
                    if (childView is LinearLayout){
                        i+=1
                        childView?.setBackgroundColor(
                            requireContext().resources.getColor(
                                if (i % 2 == 0) tableColors[1] else tableColors[0]
                            )
                        )
                    }
                }
            }
        }
    }


    override fun onLocalUnit(l: List<UnitBean>) {
        if (l.isEmpty())return
        var b = l[0]
        LOG.I("123","onLocalUnit  b=$b")
        mData.unitId = b.unitId?:-1
        mData.projectId = b.projectId?:-1
        mData.unitNumber = "${b.unitNo?:0}"
        mData.totalFloor = "${b.floorSize?:0}"
        mData.totalNeighbor = "${b.neighborSize?:0}"
        mData.floorType = if(b.floorType==1) "双楼梯间" else "单楼梯间"
    }

    override fun onLocalConfig(l: List<ConfigBean>) {

    }

    override fun onCopyError(msg: String) {
    }


    override fun onUnitUpdate(b: Boolean) {
        if (b) showToast("上传后台成功")
        (parentFragment as ProjectUnitFragment).showPb(false)
    }

    override fun bindPresenter() {
        mConfigPresenter.bindView(this)
//        mPresenter.init(mMyCode)
        mUnitPresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg)
    }

    override fun unbindPresenter() {
        mConfigPresenter.unbindView()
        mUnitPresenter.unbindView()
    }




    private fun copyUnit(){
        LOG.I("123","copyUnit $mLocalUnitId")
        mUnitPresenter.copyUnit(mLocalUnitId){
            LOG.I("123","new unit id = $it")
            mConfigPresenter.copyUnit(mLocalUnitId,it)
            (parentFragment as ProjectUnitFragment)
                .onCopyUnit(it)
        }
    }
    fun onUnitId(unitId:Long){
        LOG.I("123","on unit id=$unitId")
        if (mLocalUnitId<0)mLocalUnitId = unitId
        if (mLocalUnitId>0 && mLocalUnitId!=unitId)return
        mUnitPresenter.getLocalUnit(unitId)
    }
}