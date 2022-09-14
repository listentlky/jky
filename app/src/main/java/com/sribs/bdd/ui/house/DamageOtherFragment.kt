package com.sribs.bdd.ui.house

import android.view.View.OVER_SCROLL_NEVER
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.databinding.LayoutBaseListMvpBinding
import com.cbj.sdk.libui.mvp.BaseListFragment
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.bindView
import com.cbj.sdk.utils.UriUtil
import com.sribs.bdd.R
import com.sribs.bdd.bean.DamageOtherBean
import com.sribs.bdd.databinding.FragmentHouseDamageOtherBinding
import com.sribs.bdd.databinding.ItemDamageOtherBinding
import com.sribs.bdd.module.house.IHouseContrast
import com.sribs.bdd.module.house.RoomDetailPresenter
import com.sribs.bdd.ui.adapter.DamageOtherListAdapter
import com.sribs.common.bean.db.RoomDetailBean

/**
 * @date 2021/7/19
 * @author elijah
 * @Description
 */
@Route(path= com.sribs.common.ARouterPath.HOUSE_DAMAGE_OTHER_FGT)
class DamageOtherFragment:BaseListFragment<DamageOtherBean,ItemDamageOtherBinding>(R.layout.fragment_house_damage_other),
IHouseContrast.IRoomDetailView{
    private val mAdapter by lazy {
        DamageOtherListAdapter(object:DamageOtherListAdapter.IOnItemClick{
            override fun onRemoved(pos: Int, b: DamageOtherBean) {
                mPresenter.deleteRoomDetail(b.id){
                    refresh()
                }

            }

        })
    }

    private val mPresenter by lazy { RoomDetailPresenter() }

    private val mBinding :FragmentHouseDamageOtherBinding by bindView()

    private val mListBinding by lazy { LayoutBaseListMvpBinding.bind(mBinding.damageOtherRvLl) }

    override fun getAdapter(): BaseListAdapter<DamageOtherBean, ItemDamageOtherBinding> = mAdapter

    override fun getLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(context)

    override fun getListRecyclerView(): RecyclerView = mListBinding.baseListRv

    override fun getListSwipeRefreshLayout(): SwipeRefreshLayout = mListBinding.baseListSrl

    override fun isEnableRefresh(): Boolean = false

    override fun isEnableScroll(): Boolean = false

    override fun deinitView() {
        super.deinitView()
        unbindPresenter()
    }

    override fun initView() {
        super.initView()
        bindPresenter()
        mListRv?.overScrollMode = OVER_SCROLL_NEVER
//        test()

        mBinding.damageOtherAddBtn.setOnClickListener {
            (activity as HouseDamageDescriptionActivity).doOtherAdd()
        }
        getDetailList()
    }

    private fun test(){
        var l = ArrayList<DamageOtherBean>()
        for (i in 0..10){
            l.add(DamageOtherBean("南北墙右下方,南北墙右下方,南北墙右下方",UriUtil.getRandomImageUrl()))
        }
        mAdapter.setData(l)
    }

    private fun getDetailList(){
        var configId = (activity as HouseDamageDescriptionActivity).mConfigId
        var name  = (activity as HouseDamageDescriptionActivity).mDamagePos
        mPresenter.getRoomDetail(configId,name,"其他损伤")
    }

    override fun onRoomDetail(l: ArrayList<RoomDetailBean>) {
        mAdapter.setData(ArrayList(l.map {
            DamageOtherBean(it.description?:"",it.picPath?:"").also { b->
                b.id = it.roomDetailId?:-1
            }
        }))
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

    fun refresh(){
        getDetailList()
    }



}