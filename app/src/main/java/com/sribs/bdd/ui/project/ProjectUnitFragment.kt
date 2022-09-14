package com.sribs.bdd.ui.project

import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.get
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.bean.data.ProjectUnitDataBean
import com.sribs.bdd.databinding.FragmentProjectDetailBinding
import com.sribs.bdd.databinding.ItemProjectUnitBinding
import com.sribs.bdd.module.project.IProjectContrast
import com.sribs.bdd.module.project.ProjectUnitPresenter
import com.sribs.bdd.ui.adapter.ProjectUnitAdapter
import com.sribs.common.bean.db.ConfigBean
import com.sribs.common.bean.db.UnitBean

/**
 * @date 2021/6/30
 * @author elijah
 * @Description 标签页面
 */
@Route(path= com.sribs.common.ARouterPath.PRO_CREATE_DETAIL)
class ProjectUnitFragment:BaseFragment(R.layout.fragment_project_detail),IProjectContrast.IUnitView {

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID)
    var mLocalProjectId = -1

    var mLocalBldId: Long = 1

    private val mPresenter by lazy { ProjectUnitPresenter() }

    private val mBinding : FragmentProjectDetailBinding by bindView()

    private val mUnitData: ProjectUnitDataBean by lazy {
        ProjectUnitDataBean()
    }

    private val mFragments = ArrayList<ProjectUnitRoomFragment>()

    private var mDeleteUnitList:ArrayList<Long> = ArrayList()

    private val mAdapter:ProjectUnitAdapter by lazy {
        ProjectUnitAdapter(childFragmentManager,mFragments)
    }

    override fun deinitView() {
        unbindPresenter()
    }

    override fun initView() {
        bindPresenter()
        initViewPager()

        mBinding.projectDetailTabAddBtn.setOnClickListener {
            var no = mUnitData.getNextAutoUnit()
            var bldId: Long = 1
            mPresenter.createLocalUnit(mLocalProjectId.toLong(), bldId,"单元$no"){
            }

        }
        initData()
    }


    private fun initViewPager(){
        mBinding.projectDetailVp.setScroll(false)
        mBinding.projectDetailVp.setSmooth(false)
        mBinding.projectDetailVp.adapter = mAdapter
    }

    private fun initData(){

        // 单元数据绑定
        mUnitData.bindAddFun {
            LOG.I("123","bind add fun addTag")
            addTagView(it[0])
        }
        mUnitData.bindUpdateFun {
            updateTagView(it[0].toInt(),it[1])
        }
        mUnitData.bindRemoveFun {
            removeTagView(it[0].toInt())
        }
    }

    fun initLocalData(projectId:Int){
        mLocalProjectId = projectId

        if (mLocalProjectId<0){
            LOG.E("123","error  mLocalProjectId=$mLocalProjectId")
            mUnitData.addUnit()
        }else {
            initLocalInfo(mLocalProjectId.toLong())
        }
    }

    private fun addTagView(tag:String){
        var parent = mBinding.projectDetailTabLl
        var cont = parent.childCount
        var itemBinding = ItemProjectUnitBinding.inflate(LayoutInflater.from(context),null,false)
        itemBinding.itemUnitTv.text = tag
        itemBinding.itemUnitTv.setOnClickListener {
            var pos = parent.indexOfChild(it)
            selTagView(pos)
        }
        var p = LinearLayout.LayoutParams(-1,requireContext().resources.getDimensionPixelOffset(R.dimen.project_detail_unit_tag_height))
        parent.addView(itemBinding.root,cont -1,p)
        addFragment(cont-1)

        // 默认选中

        selTagView(cont-1)

        // todo 添加fragment

    }

    fun updateTagView(pos:Int,tag:String){
        val parent = mBinding.projectDetailTabLl
        val cont =  parent.childCount
        if (pos<0 || pos>cont-1){
            LOG.E("123","pos<0 || pos>cont-1")
            return
        }
        var itemBinding = ItemProjectUnitBinding.bind(parent[pos])

        itemBinding.itemUnitTv.text = tag
        LOG.I("123","updateTagView   tag=$tag  pos=$pos   ${itemBinding.itemUnitTv}   ")
    }

    fun removeUnit(pos:Int){
        LOG.E("123","pos=$pos")
        var unitId = mUnitData.delUnit(pos)
        LOG.I("123","removeUnit id =$unitId")
        mPresenter.delLocalUnit(unitId)
    }

    /**
     * @Description 逻辑删除
     */
    fun removeTagView(dataPos:Int){
//        val parent = mBinding.projectDetailTabLl
//        parent.removeViewAt(pos)

        var pos = getVpPos(dataPos)
        LOG.I("123","removeTagView  pos=$pos")
        var selIdx = getNextVpPos(dataPos)
        LOG.I("123","removeTagView  selIdx=$selIdx")
        selTagView(selIdx)
        val parent = mBinding.projectDetailTabLl
        val cont =  parent.childCount
        parent.children.forEachIndexed {index, view ->
            if (index == pos) view.visibility = View.GONE
        }
        //todo 逻辑删除fragment
        removeFragment(pos)
    }

    private fun selTagView(pos:Int){
        //tagView
        val parent = mBinding.projectDetailTabLl
        val cont =  parent.childCount
        parent.children.forEachIndexed { index, view ->
            var itemBinding = ItemProjectUnitBinding.bind(view)
            itemBinding.itemUnitTv.isSelected = pos==index
        }
        LOG.I("123","selTagView pos=$pos")
        mBinding.projectDetailVp.currentItem = pos
    }

    private fun addFragment(idx:Int){
        mFragments.add(ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_DETAIL_UNIT)
            .withInt(com.sribs.common.ARouterPath.VAL_UNIT_IDX,idx)
            .navigation() as ProjectUnitRoomFragment)

        mAdapter.updateList(mFragments)
        mBinding.projectDetailVp.offscreenPageLimit = mFragments.size
        var size = mFragments.filter { it.mEnable }.size
        mFragments.filter { it.mEnable }.forEach {
            it.showDelBtn(size>1)
        }
    }

    private fun removeFragment(idx:Int){

        mFragments[idx].mEnable = false
        if (mFragments[idx].mData.unitId>0){
            LOG.I("123","remove unitId=${mFragments[idx].mData.unitId}")
            mDeleteUnitList.add(mFragments[idx].mData.unitId)
        }

        var size = mFragments.filter { it.mEnable }.size
        mFragments.filter { it.mEnable }.forEach {
            it.showDelBtn(size>1)
        }
    }

    fun getVpCurIndex():Int{
        return getDataPos(mBinding.projectDetailVp.currentItem)
    }

    /**
     * @Description
     */
    private fun getDataPos(vpPos:Int):Int{
        var f = mFragments[vpPos]
        return mFragments.filter { it.mEnable }.indexOf(f)
    }

    private fun getVpPos(dataPos:Int):Int{
        var f = mFragments.filter { it.mEnable }[dataPos]
        return mFragments.indexOf(f)
    }

    private fun getNextVpPos(dataPos: Int):Int{

        var l = mFragments.filter { it.mEnable }
        if (dataPos-1>=0 && l.size>dataPos-1){
            var last = l[dataPos-1]
            return mFragments.indexOf(last)
        }
        var idx = getVpPos(dataPos)
        var next = mFragments.filterIndexed { i, b -> i>idx && b.mEnable }.firstOrNull()
        if (next!=null){
            return mFragments.indexOf(next)
        }
        return 0
    }

    fun onCopyUnit(unitId:Long){
        LOG.I("123","onCopyUnit unitId=$unitId")
        var id = mUnitData.addUnit(unitId)
        if (id?:-1>0) mFragments?.last { it.mEnable }.onUnitId(id!!)
    }

    private fun initLocalInfo(projectId: Long){
        mPresenter.getLocalUnit(projectId) {
        }
    }
    override fun onLocalUnit(l: List<UnitBean>) {
        LOG.I("123","onLocalUnit  l=$l")
        if (l.isEmpty()){
            LOG.I("123","createLocal")
            mPresenter.createLocalUnit(mLocalProjectId.toLong(), mLocalBldId, "单元1"){

            }
        }else{
            l.forEach {
                if (it.unitId!=null)
                    onCopyUnit(it.unitId!!)
            }
        }
    }

    override fun onLocalConfig(l: List<ConfigBean>) {

    }

    override fun onUnitUpdate(b: Boolean) {
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

    fun showPb(b:Boolean){
        if (b){
            mBinding.pb.visibility = View.VISIBLE
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }else{
            mBinding.pb.visibility = View.GONE
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}