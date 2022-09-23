package com.sribs.bdd.ui.main

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.databinding.LayoutBaseListMvpBinding
import com.cbj.sdk.libui.mvp.BaseListFragment
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.bean.MainProjectBean
import com.sribs.bdd.databinding.ItemMainListBinding
import com.sribs.bdd.module.main.IMainListContrast
import com.sribs.bdd.module.main.MainListPresenter
import com.sribs.bdd.ui.adapter.MainListAdapter
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.DrawingBean
import com.sribs.common.bean.db.UnitBean
import com.sribs.common.ui.widget.CommonGridDividerItemDecoration
import kotlin.collections.ArrayList

/**
 * @date 2021/6/23
 * @author elijah
 * @Description
 */
@Route(path = com.sribs.common.ARouterPath.MAIN_LIST_FGT)
class MainListFragment :
    BaseListFragment<MainProjectBean, ItemMainListBinding>(R.layout.layout_base_list_mvp),
    IMainListContrast.IView {

    private val mPresenter by lazy { MainListPresenter() }

    private val mBinding: LayoutBaseListMvpBinding by bindView()

    private val mModuleMap by lazy { ModuleHelper.moduleMap }

    private var mCurProjectType: String? = null

    //二期用以判断是否配置了居民类项目
    private var mUnitListInProjects: ArrayList<UnitBean>? = null

    //二期用来判断是否配置了非居民类项目
    private var mDrawingListInProjects: ArrayList<DrawingBean>? = null

    private val mAdapter: MainListAdapter by lazy {
        MainListAdapter(object : MainListAdapter.ICallback {
            override fun onMoreChecked(beanMain: MainProjectBean, pos: Int, checked: Boolean) {
                (activity as DamageMainActivity).showBottomDialog(checked, pos, beanMain)
            }

            override fun onCardSelect(beanMain: MainProjectBean, pos: Int) {
                LogUtils.d("选中："+beanMain.toString())
                LogUtils.d("leader："+beanMain.leader+" ; "+"inspector: "+beanMain.inspector)



                ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CREATE_ATY_FLOOR_LIST)
                    .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE, beanMain.address)
                    .withLong(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID, beanMain.localId)
                    .withLong(com.sribs.common.ARouterPath.VAL_PROJECT_ID, beanMain.localId)
                    .withString(com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID, beanMain.remoteId)
                    .withString(com.sribs.common.ARouterPath.VAL_COMMON_LEADER,beanMain.leader)
                    .withString(com.sribs.common.ARouterPath.VAL_COMMON_INSPECTOR,beanMain.inspector)
                    .navigation()

                /*

                (activity as DamageMainActivity).selectProject(beanMain)

                val unitsList = mUnitListInProjects?.filter { unit ->
                    unit.projectId == beanMain.localId
                }
                  LogUtils.d(unitsList.toString())

                  if (beanMain.localId<0){
                       showToast(getString(R.string.error_no_local))
                       return
                   }

                   (activity as DamageMainActivity).selectProject(beanMain)

                   val unitsList =  mUnitListInProjects?.filter { unit->
                       unit.projectId == beanMain.localId
                   }

                   println("leon onCardSelect unitsList=" + unitsList.toString())*/
                //已经配置了单元，转到HOUSE_UNIT_LIST_ATY
                /*    if(unitsList?.size!! > 0) {
                        // ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_UNIT_LIST_ATY)
                        ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CREATE_ATY_FLOOR_LIST)
                            .withLong(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID,
                                beanMain?.localId!!)
                            .withString(com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID,
                                beanMain.remoteId)
                            .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE, beanMain.address)
                            .navigation()
                    }else//只有projectId，没有配置单元，如非居民类项目
                    {
                        AlertDialog.Builder(requireContext()).setTitle(R.string.dialog_need_config_title)
                            .setMessage(R.string.dialog_need_config_inhab_content).setPositiveButton(R.string.dialog_ok) { dialog, which ->
                                //ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CREATE_ATY)
                                ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CREATE_ATY_TYPE)
                                    .withLong(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID,beanMain?.localId!!)
                                    .navigation()
                            }.setNegativeButton(R.string.dialog_cancel
                            ) { dialog, which ->

                            }
                            .show()
                    }*/
                /*   ARouter.getInstance().build(com.sribs.common.ARouterPath.CHECK_OBLIQUE_DEFORMATION_ACTIVITY)
                       .navigation()*/


                //    mPresenter.getAllUnitsInProject(beanMain.localId)

                /*         var from:String = (activity as DamageMainActivity).mFrom
                         println("leon MainListFragment from=${from}")
             //            mCurProjectType = from
                         if(from.equals(mModuleMap.get("BLD_TYPE_INHAB"))) {//居民入户损伤

                             (activity as DamageMainActivity).selectProject(beanMain)
             //                var beanMain: MainProjectBean? = (activity as DamageMainActivity).getProject()

                             val unitsList =  mUnitListInProjects?.filter { unit->
                                 unit.projectId == beanMain.localId
                             }

                             println("leon onCardSelect unitsList=" + unitsList.toString())
                             //已经配置了单元，转到HOUSE_UNIT_LIST_ATY
                             if(unitsList?.size!! > 0) {
                                // ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_UNIT_LIST_ATY)
                                 ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CREATE_ATY_FLOOR_LIST)
                                     .withLong(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID,
                                         beanMain?.localId!!)
                                     .withString(com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID,
                                         beanMain.remoteId)
                                     .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE, beanMain.address)
                                     .navigation()
                             }else//只有projectId，没有配置单元，如非居民类项目
                             {
                                 AlertDialog.Builder(requireContext()).setTitle(R.string.dialog_need_config_title)
                                     .setMessage(R.string.dialog_need_config_inhab_content).setPositiveButton(R.string.dialog_ok) { dialog, which ->
                                         //ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CREATE_ATY)
                                         ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CREATE_ATY_TYPE)
                                             .withLong(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID,beanMain?.localId!!)
                                             .withString("from", from)
                                             .navigation()
                                     }.setNegativeButton(R.string.dialog_cancel
                                     ) { dialog, which ->

                                     }
                                     .show()
                             }

                         }else if(from.equals(mModuleMap.get("BLD_TYPE_NONINHAB"))) {//非居民入户损伤
                             println("leon MainListFragment will navigate to FLOOR_DAMAGE_LIST_ATY beanMain=${beanMain.toString()}")

                             val drawingsList =  mDrawingListInProjects?.filter { drawing->
                                 drawing.projectId == beanMain.localId
                             }

                             (activity as DamageMainActivity).selectProject(beanMain)

                             if(drawingsList?.size!! > 0) {
                                 ARouter.getInstance().build(com.sribs.common.ARouterPath.BLD_DAMAGE_LIST_ATY)
                                     .withLong(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID,
                                         beanMain.localId)
                                     .withString(com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID,
                                         beanMain.remoteId)
                                     .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE, beanMain.address)
             //                        .withString(com.sribs.common.ARouterPath.VAL_BUILDING_NO,)
                                     .navigation()
                             }
                             else{
                                 AlertDialog.Builder(requireContext()).setTitle(R.string.dialog_need_config_title)
                                     .setMessage(R.string.dialog_need_config_non_inhab_content).setPositiveButton(R.string.dialog_ok) { dialog, which ->
                                         //ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CREATE_ATY)
                                         ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CREATE_ATY_TYPE)
                                             .withInt(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID,beanMain?.localId!!.toInt())
                                             .withString("from", from)
                                             .navigation()
                                     }.setNegativeButton(R.string.dialog_cancel
                                     ) { dialog, which ->

                                     }
                                     .show()
                             }
                         }
                         else
                             println("leon MainListFragment not go anywhere")*/

            }
        })
    }

    override fun getAdapter(): BaseListAdapter<MainProjectBean, ItemMainListBinding> = mAdapter
    override fun getLayoutManager(): RecyclerView.LayoutManager = GridLayoutManager(context, 3)
    override fun getListRecyclerView(): RecyclerView = mBinding.baseListRv
    override fun getListSwipeRefreshLayout(): SwipeRefreshLayout = mBinding.baseListSrl
    override fun isEnableRefresh(): Boolean = true
    override fun isEnableScroll(): Boolean = true
    override fun onRefresh() {
        super.onRefresh()
        mPresenter.getProjectList()
    }

    override fun onLoad(curPage: Int) {
        super.onLoad(curPage)
    }

    override fun onProjectList(l: ArrayList<MainProjectBean>) {
        LogUtils.d("onProjectList")

        LogUtils.d("数据源: "+l.size)

        mAdapter.setData(l)
        refreshFinish()

        // 三期居民类与非居民类损伤后置
       /* l?.run {
            mUnitListInProjects?.clear()
            mDrawingListInProjects?.clear()
            l.forEach {
                mPresenter.getAllUnitsInProject(it?.localId)
            }

            l.forEach {
                mPresenter.getAllDrawingInProject(it?.localId)
            }
        }*/
    }

    override fun onAllUnitsInProject(r: ArrayList<UnitBean>) {
        println("leon onAllUnitsInProject r.size=${r.size}")

        mUnitListInProjects = mUnitListInProjects ?: ArrayList<UnitBean>()

        r?.run {
            if (r.size > 0)//已经配置了居民类损伤
            {
                mUnitListInProjects?.addAll(r)
            } else {//二期暂时算作非居民类

            }
        }
    }

    override fun onAllDrawingsInProject(r: ArrayList<DrawingBean>) {

        mDrawingListInProjects = mDrawingListInProjects ?: ArrayList<DrawingBean>()

        r?.run {
            if (r.size > 0)//已经配置了非居民类损伤图纸
            {
                mDrawingListInProjects?.addAll(r)
            } else {//二期暂时算作非居民类

            }
        }
    }

    override fun bindPresenter() {
        mPresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg ?: "")
    }

    override fun onNetError(msg: String?) {
        showToast(msg ?: "")
    }

    override fun unbindPresenter() {
        mPresenter.unbindView()
    }


    override fun initView() {
        super.initView()
        mListRv?.addItemDecoration(
            CommonGridDividerItemDecoration(
                resources.getDimensionPixelOffset(R.dimen.main_item_margin),
                resources.getDimensionPixelOffset(R.dimen.main_item_margin)
            )
        )
        bindPresenter()

        mCurProjectType = (activity as DamageMainActivity).mFrom
        println("leon MainListFragment mCurProjectType = ${mCurProjectType}")

        mPresenter.getProjectList()
    }

    override fun deinitView() {
        super.deinitView()
        unbindPresenter()
    }

    fun menuUnSel(pos: Int) {
        mAdapter.menuUnSel(pos)
    }

    override fun getContext(): Context? {
        return super.getContext()
    }

    fun search(time: ArrayList<String>) {
        mAdapter.setSearch(time)
    }

    fun search(keyword: String) {
        mAdapter.setSearch(keyword)
    }

}