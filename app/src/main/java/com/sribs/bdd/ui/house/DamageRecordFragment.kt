package com.sribs.bdd.ui.house

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.adapter.BasePagerAdapter
import com.cbj.sdk.libui.mvp.bindView
import com.cbj.sdk.utils.UriUtil
import com.sribs.bdd.R
import com.sribs.bdd.bean.RecordItemBean
import com.sribs.bdd.bean.data.DamageEditDataBean
import com.sribs.bdd.databinding.FragmentHouseDamageRecordBinding
import com.sribs.bdd.module.house.IHouseContrast
import com.sribs.bdd.module.house.RoomDetailPresenter
import com.sribs.bdd.ui.adapter.RecordListAdapter
import com.sribs.common.bean.db.RoomDetailBean

/**
 * @date 2021/7/19
 * @author elijah
 * @Description
 */
@Route(path = com.sribs.common.ARouterPath.HOUSE_DAMAGE_RECORD_FGT)
class DamageRecordFragment:BaseFragment(R.layout.fragment_house_damage_record), IHouseContrast.IRoomDetailView {

    private var mFilterPos = ""

    private var  mAll :ArrayList<RecordItemBean> ?=null

    private val mPresenter by lazy { RoomDetailPresenter() }

    private val mBinding : FragmentHouseDamageRecordBinding by bindView()

    private val mFragments by lazy {
        listOf(
            ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_DAMAGE_PIC_FGT)
                .navigation() as BaseFragment,
            ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_DAMAGE_OTHER_FGT)
                .navigation() as BaseFragment
        )
    }

    private val mAdapter by lazy { RecordListAdapter(object:RecordListAdapter.IClickListener{
        override fun onClick(b: RecordItemBean) {
            LOG.I("123","b=$b")
            (activity as HouseDamageDescriptionActivity).selRecord(b,b.picList!!)

        }

        override fun onLongClick(b: RecordItemBean) {
            (activity as HouseDamageDescriptionActivity).selRecord(b,b.picList!!)
            if(!b.posList!!.contains("其他损伤")){
                (activity as HouseDamageDescriptionActivity).doEdit(b.posList!!,b.picList!!)
            }else{
                (activity as HouseDamageDescriptionActivity).doEditOther(b.posList!!,b.description!!)
            }

        }
    }) }

    override fun deinitView() {
        unbindPresenter()
    }

    override fun initView() {
        bindPresenter()
        initViewPage()
        initRecordList()
        initData()
    }

    private fun initViewPage(){
        mBinding.damageVp.setSmooth(false)
        mBinding.damageVp.setScroll(false)
        mBinding.damageVp.adapter = BasePagerAdapter(childFragmentManager,mFragments)
        mBinding.damageVp.offscreenPageLimit = 2


    }

    private fun initRecordList(){

        mBinding.damageRv.adapter = mAdapter
        mBinding.damageRv.layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)

        //test data
//        testRv()
    }

    private fun initData(){
        var configId = (activity as HouseDamageDescriptionActivity).mConfigId
        var name = (activity as HouseDamageDescriptionActivity).mDamagePos
        mPresenter.getRoomDetail(configId,name)
    }

    fun showOtherList(){
        mBinding.damageVp.currentItem = 1
    }

    fun showPic(pos:ArrayList<String>,cur:ArrayList<String>?){
        (mFragments[0] as DamagePicFragment).also {
            it.setDamagePos(pos,cur)
        }
        mBinding.damageVp.currentItem = 0
    }

    private fun testRv(){
        var l = ArrayList<RecordItemBean>()
        for (i in 0..10){
            var d = "餐厅东墙窗左侧1条贯穿北倾斜裂缝，L≈1.0mm，δ≈0.1m"
            l.add(RecordItemBean(UriUtil.getRandomImageUrl(),d).also {
                it.posList = ArrayList(listOf("东墙","裂缝","有窗"))
                it.picList = ArrayList(listOf("12"))
                it.data = DamageEditDataBean().also { b->
                    b.damageDes = d
                    b.splitNum = "1条"
                    b.splitType = "是"
                    b.splitLen = "1.0"
                    b.splitWidth = "0.1"
                }
            })
        }
        mAdapter.setData(l)
        mBinding.damageNoPicTv.visibility = if (mAdapter.itemCount==0) View.VISIBLE else View.GONE
    }



    override fun onRoomDetail(l: ArrayList<RoomDetailBean>) {
        if(l.isEmpty()){
            mBinding.damageNoPicTv.visibility = View.VISIBLE
            mAdapter.setData(ArrayList())
            return
        }
        var list = l.map { RecordItemBean(
            it.picPath?:"",it.description?:"",it.updateTime
        ).also { b->
            b.posList = ArrayList(it.damagePath?.split("-"))
            b.picList = ArrayList(listOf("${it.damageIdx?:-1}"))
            b.data = DamageEditDataBean().also { d->
                d.damageDes = it.description?:""
                d.splitNum = if(!it.splitNum.isNullOrEmpty()){
                    it.splitNum!!
                } else if(!it.seamNum.isNullOrEmpty()){
                    it.seamNum!!
                } else {
                    ""
                }
                d.splitType = if(it.splitType == 1)"是" else "否"
                d.splitLen = it.splitLen?:""
                d.splitWidth = it.splitWidth?:""
            }
        } }
        mAll = ArrayList(list)
        mAdapter.setData(filterRecord(mAll!!))
        mBinding.damageNoPicTv.visibility = if (mAdapter.itemCount==0) View.VISIBLE else View.GONE
    }

    private fun filterRecord(src:ArrayList<RecordItemBean>):ArrayList<RecordItemBean>{
        if (mFilterPos.isNullOrEmpty())return ArrayList(src)
        return ArrayList(src.filter { it.posList?.joinToString(separator = "-")?.contains(mFilterPos)?:false })
    }

    fun setFilter(s:String){
        mFilterPos = s
        if (!mAll.isNullOrEmpty()){
            var l = filterRecord(mAll!!)
            mAdapter.setData(l)
        }else{
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

    fun refreshPic(){

       var pic =  mFragments[0] as DamagePicFragment
        pic.refresh()
       var other =  mFragments[1] as DamageOtherFragment
        other.refresh()
    }

    fun cleanSel(){
        var pic =  mFragments[0] as DamagePicFragment
        pic.cleanSel()
    }

}