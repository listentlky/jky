package com.sribs.bdd.ui.building

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView

import com.radaee.pdf.Document
import com.radaee.pdf.Page
import com.sribs.bdd.R

import com.sribs.bdd.bean.DamageBean
import com.sribs.bdd.bean.Floor
import com.sribs.bdd.bean.data.DamageSectionDetail
import com.sribs.bdd.bean.data.NonInFloorDetailDataBean

import com.sribs.bdd.databinding.FragmentDrawingTypeMainBinding
import com.sribs.bdd.module.building.BuildingDamageMainPresenter
import com.sribs.bdd.module.building.IBuildingContrast

import com.sribs.bdd.utils.ModuleHelper.CUR_BLD_NO
import com.sribs.bdd.utils.ModuleHelper.CUR_PRO_LEADER
import com.sribs.bdd.utils.ModuleHelper.CUR_PRO_NAME
import com.sribs.bdd.utils.ModuleHelper.DRAWING_CACHE_FOLDER
import com.sribs.common.bean.db.DrawingBean
import com.sribs.common.utils.FileUtil
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionAdapter
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import org.apache.commons.collections4.map.ListOrderedMap


/**
 * @date 2022/3/28
 * @author leon
 * @Description 按每楼图纸管理损伤
 */
@Route(path= com.sribs.common.ARouterPath.BLD_DRW_DMG_MNG_MAIN_FGT)
class BldDrwDmgMngMainFragment:BaseFragment(R.layout.fragment_drawing_type_main), IBuildingContrast.IBuildingView, DamageDetailSection.ClickListener {

//    private val facadeDrawingslist by lazy { view!!.findViewById<RecyclerView>(R.id.fragment_list) }

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID)
    var mLocalProjectId = -1

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_BUILDING_ID)
    var mBuildingId = -1

    private val TAG = "leon"

    private val mPresenter by lazy { BuildingDamageMainPresenter() }

    private val mBinding : FragmentDrawingTypeMainBinding by bindView()

    private var mSelFloorName: String? = ""//(activity as BuildingDamageActivity).getSelectedFloorName()

    var mDamageDescList: ArrayList<DamageDesc>? = null

    private var mCurDrwDmgMap: HashMap<String, HashMap<Long, String?>>? = null //type, damageMap
    private var mCurPdfFilePath:String = ""

    private lateinit var mPrefs: SharedPreferences
    private var mProName: String? = ""
    private var mBldNo: String? = ""
    private var mProLeader: String? = ""
    private var mCurDrawingsDir: String? = ""
    private var mEngineer: String? = ""
    private var mSectionedAdapter: SectionedRecyclerViewAdapter? = null
    private var mDamageDetailSectionMap: HashMap<String, ArrayList<DamageSectionDetail>?>? = null//String is section header, i.e. damage type
    private var mDamageSectionDetailList: ArrayList<DamageSectionDetail>? = null
    private var mSectionList:ArrayList<DamageDetailSection>? = null
    private var mDamageType:Array<String>? = null

    val mData: NonInFloorDetailDataBean by lazy {
        NonInFloorDetailDataBean(mLocalProjectId)
    }

    override fun deinitView() {
        unbindPresenter()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initView() {
        LOG.I("leon","BldDrwDmgMngMainFragment initView")
        initData()
        bindPresenter()
    }

    private fun initData(){
        //
        LOG.I("leon","BldDrwDmgMngMainFragment initData")
        mDamageType = requireActivity().resources.getStringArray(R.array.drawing_damage_type)

        if(mDamageDetailSectionMap.isNullOrEmpty()){
            mDamageDetailSectionMap = HashMap<String, ArrayList<DamageSectionDetail>?>()
        }

        if(mSectionedAdapter == null)
            mSectionedAdapter = SectionedRecyclerViewAdapter()

        if(!mDamageType.isNullOrEmpty()){
            if(mSectionList.isNullOrEmpty())
                mSectionList = ArrayList<DamageDetailSection>()

            var dmgSection: DamageDetailSection? = null
            mDamageType?.forEach { item->
//                dmgSection = DamageDetailSection(item.toString(), dmgDetailList, this);
                var dmgDetailList: ArrayList<DamageSectionDetail> = ArrayList<DamageSectionDetail>()
                dmgSection = DamageDetailSection(item.toString(), dmgDetailList, this);
                mSectionedAdapter!!.addSection(dmgSection)
                mSectionList!!.add(dmgSection!!)
//                mDamageDetailSectionMap!!.put(item.toString(), null)
            }

        }
        else
            println("leon dmgTypeArray is null")

        val recyclerView: RecyclerView = mBinding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mSectionedAdapter

        var ind:Int = 0
        var secs: ListOrderedMap<String?, Section?>? = mSectionedAdapter!!.getSections()
        secs?.forEach { item ->
            {
                println("leon mSectionedAdapter No.${ind} header header = ${item.key}, section=${item.value}")
                ind++
            }
        }
        println("leon mSectionedAdapter is not null")
    }

    fun initLocalData(projectId:Int){
        mLocalProjectId = projectId

        LOG.I("leon","BldDrwDmgMngMainFragment initLocalData mLocalProjectId=" + mLocalProjectId)
        mPrefs = requireActivity().getSharedPreferences("createProject", Context.MODE_PRIVATE)
        mProName = mPrefs.getString(CUR_PRO_NAME,"")
        mBldNo = mPrefs.getString(CUR_BLD_NO,"")
        mProLeader = mPrefs.getString(CUR_PRO_LEADER,"")

        mCurDrawingsDir = "/" + DRAWING_CACHE_FOLDER + "/" + mProName + "/" + mBldNo + "/"
        FileUtil.makeRecurDirs(requireActivity(), mCurDrawingsDir!!)

        println("leon project name=${mProName} and building No=${mBldNo}")

    }

    override fun initBuildingFloors(bldId:Long, floors: ArrayList<Floor>) {

    }

    override fun initBuildingDrawing(drawings: ArrayList<DrawingBean>) {

    }

    override fun initDrawingDamages(damages: List<com.sribs.common.bean.db.DamageBean>) {

    }

    override fun saveDamageDataToDbStarted() {

    }

    override fun updateLocalDamageDetail(dmg: com.sribs.common.bean.db.DamageBean?) {

    }

    override fun onRemoveDamageInDrawing(dmg: com.sribs.common.bean.db.DamageBean) {
        println("leon BldDrwDmgMngMainFragment onRemoveDamageInDrawing dmg=${dmg.toString()}")

    }

    override fun bindPresenter() {
//       mPresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg)
    }

    override fun unbindPresenter() {
//       mPresenter.unbindView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LOG.I("leon", " resultCode=" + resultCode)
        LOG.I("leon", " requestCode=" + requestCode)
        LOG.I("leon", " data=" + data.toString())
    }

    override fun onPause() {
        super.onPause()
        LOG.I("leon", "BldDrwDmgMngMainFragment onPause")
    }

    override fun onResume() {
        super.onResume()
        LOG.I("leon", "BldDrwDmgMngMainFragment onResume")
    }

    override fun onStop() {
        super.onStop()
        LOG.I("leon", "BldDrwDmgMngMainFragment onStop")
    }

    public fun setCurPdfFileAbsPath(path:String){
        mCurPdfFilePath = path
    }

    override fun onHeaderRootViewClicked(section: DamageDetailSection) {
//        LOG.I("leon", "BldDrwDmgMngMainFragment onHeaderRootViewClicked clicked")
        val sectionAdapter: SectionAdapter = mSectionedAdapter!!.getAdapterForSection(section)

        // store info of current section state before changing its state

        // store info of current section state before changing its state
        val wasExpanded = section.isExpanded
        val previousItemsTotal = section.contentItemsTotal
        println("leon onHeaderRootViewClicked previousItemsTotal=${previousItemsTotal}, wasExpanded=${wasExpanded}")

        section.isExpanded = !wasExpanded
        sectionAdapter.notifyHeaderChanged()

        if (wasExpanded) {
            sectionAdapter.notifyItemRangeRemoved(0, previousItemsTotal)
        } else {
            sectionAdapter.notifyAllItemsInserted()
        }
    }

    override fun onItemRootViewClicked(section: DamageDetailSection, itemAdapterPosition: Int) {
        LOG.I("leon", "BldDrwDmgMngMainFragment onItemRootViewClicked clicked")
    }

    //初始化图纸损伤,在页面加载或图纸切换时
//    public fun initLocalDamages(drawingDamages: HashMap<String, ArrayList<com.sribs.common.bean.db.DamageBean>>?){
    public fun initLocalDamages(curDrawing: com.sribs.common.bean.db.DrawingBean, buildingDamages: ArrayList<com.sribs.common.bean.db.DamageBean>?){
        println("leon BldDrwDmgMngMainFragment initLocalDamages in, curDrawing=${curDrawing.toString()}")
        println("leon BldDrwDmgMngMainFragment initLocalDamages in, buidingDamages=${buildingDamages.toString()}")
        mSelFloorName = (activity as BuildingDamageActivity).getSelectedFloorName()

        mSectionList?.forEach { it->
            it.damageDetailList.clear()
        }
        mSectionedAdapter!!.notifyDataSetChanged()

        if(!buildingDamages.isNullOrEmpty()) {
            var dmgSectionDetail: DamageSectionDetail
            var foundMonitor: Boolean = false
            var dmgDetailList: ArrayList<DamageSectionDetail>? = null
            var dmgMonitorList: ArrayList<DamageSectionDetail>? = null
            var sectionList: ArrayList<DamageDetailSection>? = null
            var sectionMntList: ArrayList<DamageDetailSection>? = null
            var crackMntType: String = activity?.resources?.getString(R.string.crack_monitor_damage)!!
            var crackMntTypeEng: String = activity?.resources?.getString(R.string.damage_type_crack)!!
            var curDrawingDamageList: ArrayList<com.sribs.common.bean.db.DamageBean>? = null

            curDrawingDamageList = buildingDamages.filter {
                it.drawingId == curDrawing.id
            } as ArrayList<com.sribs.common.bean.db.DamageBean>

            println("leon initLocalDamages curDrawingDamageList=${curDrawingDamageList.toString()}")
            //先取得裂缝监测点对应损伤列表
            for(section in mSectionList!!) {
                if (section.title == crackMntType) {
                    dmgMonitorList =
                        section?.damageDetailList as ArrayList<DamageSectionDetail>
                    break
                }
                println("leon initLocalDamages dmgMonitorList=${dmgMonitorList.toString()}")
            }

            curDrawingDamageList?.run{
                for(damage in curDrawingDamageList) {
                    println("leon initLocalDamages damage=${damage.toString()}")

                    for(section in mSectionList!!) {
                        if (section.title == damage.dTypeName) {
                            dmgDetailList =
                                section?.damageDetailList as ArrayList<DamageSectionDetail>
                            dmgSectionDetail =
                                DamageSectionDetail(mSelFloorName + damage?.axis + damage?.dmDesc!!,
                                    damage?.annotRef!!)
                            dmgDetailList?.add(dmgSectionDetail)
                        }
                        println("leon initLocalDamages section.title=${section.title} damage.dDetailType=${damage.dDetailType}")
                    }
                    if(damage.dDetailType == crackMntTypeEng){

                        var desc: String? = ""

                        damage?.leakWidth?.let {
                            if (desc.isNullOrEmpty())
                                desc = "裂缝宽度：" + it + "mm"
                        }
                        damage?.leakLength?.let {
                            if (desc.isNullOrEmpty())
                                desc = "裂缝长度：" + it + "m"
                            else
                                desc += "；裂缝长度：" + it + "m"
                        }
                        damage?.mntId?.let {
                            if (desc.isNullOrEmpty())
                                desc = "监测点编号：" + it
                            else
                                desc += "；监测点编号：" + it
                        }
                        damage?.mntWay?.let {
                            if (desc.isNullOrEmpty())
                                desc = "监测方法：" + it
                            else
                                desc += "；监测方法：" + it
                        }
                        damage?.monitorLength?.let {
                            if (desc.isNullOrEmpty())
                                desc = "刻痕长度：" + it + "mm"
                            else
                                desc += "；刻痕长度：" + it + "mm"
                        }
                        damage?.monitorWidth?.let {
                            if (desc.isNullOrEmpty())
                                desc = "刻痕宽度：" + it + "mm"
                            else
                                desc += "；刻痕宽度：" + it + "mm"
                        }
                        println("leon desc=${desc}")
                        dmgSectionDetail =
                            com.sribs.bdd.bean.data.DamageSectionDetail(desc!!, damage.annotRef!!)
                        dmgMonitorList?.add(dmgSectionDetail)
                        println("leon initLocalDamages  dmgMonitorList = ${dmgMonitorList.toString()}")
                    }
                }
            }

            mSectionedAdapter!!.notifyDataSetChanged()
              mSectionList!!.forEach {
                println("leon BldDrwDmgMngMainFragment initLocalDamages title=${it.getTitle()}, list=${it.damageDetailList.toString()} ")
            }
        }

    }

    //初始化图纸损伤
    public fun updateDamages(dmg: com.sribs.common.bean.db.DamageBean?){
        println("leon BldDrwDmgMngMainFragment updateDamages in, dmg=${dmg.toString()}")
        if(dmg != null) {

            var dmgType = dmg.dTypeName
            var ref: Long? = dmg.annotRef
            var dmgSectionDetail: DamageSectionDetail

            var monitorType = dmg.dDetailType

            println("leon BldDrwDmgMngMainFragment updateDamages type=${dmgType} monitorType=${monitorType}")

            var dmgDetailList: ArrayList<DamageSectionDetail>? = null
            var dmgMonitorDetailList: ArrayList<DamageSectionDetail>? = null
            var found: Boolean = false
            var foundMonitor: Boolean = false

            mSelFloorName = (activity as BuildingDamageActivity).getSelectedFloorName()

            mSectionList!!.forEach {
                println("leon BldDrwDmgMngMainFragment contains title=${it.getTitle()}")

                if (dmgType == it.getTitle()) {
                    dmgDetailList = it.damageDetailList as ArrayList<DamageSectionDetail>?
                    println("leon BldDrwDmgMngMainFragment 111 dmgDetailList=${dmgDetailList.toString()} ")
                    if (dmgDetailList != null) {
                        dmgDetailList?.forEach {
                            if (ref == it.damageRef) {
                                found = true
                                it.damageDesc = mSelFloorName+dmg.axis+dmg.dmDesc!!
                            }
                        }
                    }
                    if (!found) {

                        dmgSectionDetail =
                            com.sribs.bdd.bean.data.DamageSectionDetail(mSelFloorName+dmg.axis+dmg.dmDesc!!,
                                dmg.annotRef!!)
                        dmgDetailList?.add(dmgSectionDetail)
                    }
                }

                if(it.getTitle() == "裂缝监测点" && monitorType == "crack"){

                    var desc:String? = ""

                    dmg?.leakWidth?.let{
                        if(desc.isNullOrEmpty())
                            desc = "裂缝宽度：" + it + "mm"
                    }
                    dmg?.leakLength?.let{
                        if(desc.isNullOrEmpty())
                            desc = "裂缝长度：" + it + "m"
                        else
                            desc += "；裂缝长度：" + it + "m"
                    }
                    dmg?.mntId?.let{
                        if(desc.isNullOrEmpty())
                            desc = "监测点编号：" + it
                        else
                            desc += "；监测点编号：" + it
                    }
                    dmg?.mntWay?.let{
                        if(desc.isNullOrEmpty())
                            desc = "监测方法：" + it
                        else
                            desc += "；监测方法：" + it
                    }
                    dmg?.monitorLength?.let{
                        if(desc.isNullOrEmpty())
                            desc = "刻痕长度：" + it + "mm"
                        else
                            desc += "；刻痕长度：" + it + "mm"
                    }
                    dmg?.monitorWidth?.let{
                        if(desc.isNullOrEmpty())
                            desc = "刻痕宽度：" + it + "mm"
                        else
                            desc += "；刻痕宽度：" + it + "mm"
                    }
                    println("leon desc=${desc}")

                    dmgMonitorDetailList = it.damageDetailList as ArrayList<DamageSectionDetail>?
                    if (dmgMonitorDetailList != null) {
                        dmgMonitorDetailList?.forEach {
                            if (ref == it.damageRef) {
                                foundMonitor = true
                                it.damageDesc = desc
                            }
                        }
                    }
                    if (!foundMonitor) {

                        dmgSectionDetail =
                            com.sribs.bdd.bean.data.DamageSectionDetail(desc!!,
                                dmg.annotRef!!)
                        dmgMonitorDetailList?.add(dmgSectionDetail)
                    }
                }
            }

            mSectionedAdapter!!.notifyDataSetChanged()
        }

    }

}


data class localPage(var pNo:Int, var page:Page){
    var mNo:Int = pNo
    var mPage:Page = page
}

data class Annot(
    var ref: Long? = -1,//annot ref
    var dmgDesc: String? = null, //损伤描述
)

data class DamageDesc(
    var type: String? = null,
    var annotList: ArrayList<Annot>? = null,
)

