package com.sribs.bdd.ui.main

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.databinding.LayoutBaseListMvpBinding
import com.cbj.sdk.libui.mvp.BaseListFragment
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentMainDrawerContentBinding
import com.sribs.common.bean.CommonBtnBean
import com.sribs.common.databinding.ItemCommonBtnBinding
import com.sribs.common.ui.adapter.CommonBtnAdapter
import com.sribs.common.ui.widget.CommonGridDividerItemDecoration
import java.util.*

/**
 * @date 2021/6/25
 * @author elijah
 * @Description
 */
@Route(path= com.sribs.common.ARouterPath.MAIN_DRAWER_FGT)
class MainDrawerFragment:BaseListFragment<CommonBtnBean,ItemCommonBtnBinding>(R.layout.fragment_main_drawer_content) {
    private val mAdapter by lazy { CommonBtnAdapter(false) }

    private val mBinding: FragmentMainDrawerContentBinding by bindView()

    private val mListBinding:LayoutBaseListMvpBinding by lazy {
        LayoutBaseListMvpBinding.bind(mBinding.listLl)
    }

    override fun getAdapter(): BaseListAdapter<CommonBtnBean, ItemCommonBtnBinding> = mAdapter

    override fun getLayoutManager(): RecyclerView.LayoutManager = GridLayoutManager(context,3)

    override fun getListRecyclerView(): RecyclerView = mListBinding!!.baseListRv!!

    override fun getListSwipeRefreshLayout(): SwipeRefreshLayout = mListBinding!!.baseListSrl

    override fun isEnableRefresh(): Boolean = false

    override fun isEnableScroll(): Boolean = false

    override fun initView() {
        super.initView()
        mListRv?.addItemDecoration(CommonGridDividerItemDecoration(
            context!!.resources.getDimensionPixelOffset(R.dimen.main_drawer_item_H),
            context!!.resources.getDimensionPixelOffset(R.dimen.main_drawer_item_v)))

        mBinding.restBtn.setOnClickListener {
//            mAdapter?.cancel()
            mAdapter?.setSelect("今年")
            var l = mAdapter?.getAllSel()
            (activity as DamageMainActivity).drawerConfirm(l)
        }

        mBinding.confirmBtn.setOnClickListener {
            var l = mAdapter?.getAllSel()
            (activity as DamageMainActivity).drawerConfirm(l)
//            mAdapter?.cancel()
//            mAdapter?.setSelect("今年")
        }

        test()
    }

    private fun test(){
        var l = ArrayList<CommonBtnBean>()
        var calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.YEAR,-1)
        var y1 = calendar.get(Calendar.YEAR).toString()+"年"
        calendar.add(Calendar.YEAR,-1)
        var y2 = calendar.get(Calendar.YEAR).toString()+"年"
        calendar.add(Calendar.YEAR,-1)
        var y3 = calendar.get(Calendar.YEAR).toString()+"年"

        l.add(CommonBtnBean(0,"今年",true))
        l.add(CommonBtnBean(0,y1,false))
        l.add(CommonBtnBean(0,y2,false))
        l.add(CommonBtnBean(0,y3,false))
//        l.add(CommonBtnBean(0,"2018年",false))
//        l.add(CommonBtnBean(0,"2017年",false))
        mAdapter.setData(l)
    }
}