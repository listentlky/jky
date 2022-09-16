package com.sribs.bdd.ui.project

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.R
import com.sribs.bdd.bean.BuildingFloorItem
import com.sribs.bdd.databinding.ActivityProjectFloorDetailBinding
import com.sribs.bdd.module.project.IProjectContrast
import com.sribs.bdd.module.project.ProjectFloorDetailPresent
import com.sribs.bdd.ui.adapter.FloorItemAdapter
import com.sribs.common.utils.TimeUtil
import java.util.*
import kotlin.collections.ArrayList


/**
 * 单栋楼下模块界面
 */
@Route(path = com.sribs.common.ARouterPath.PRO_ITEM_ATY_FLOOR)
class ProjectFloorItemActivity :BaseActivity(), IProjectContrast.IProjectFloorDetailView{

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID)
    var mLocalProjectId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_BUILDING_ID)
    var mBuildingId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    private val mBinding:ActivityProjectFloorDetailBinding by inflate()

    private val mPresent:ProjectFloorDetailPresent by lazy {  ProjectFloorDetailPresent() }

    private val mAdapter:FloorItemAdapter by lazy { FloorItemAdapter() }

    override fun deinitView() {
        unbindPresenter()
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        bindPresenter()
        mBinding.toolbar.tbTitle.text = mTitle
        mBinding.toolbar.tb.setNavigationIcon(R.mipmap.icon_back)
        mBinding.toolbar.tb.setNavigationOnClickListener {
            finish()
        }

        initRecycleView()
        mPresent.getData(mLocalProjectId,mBuildingId)
        mBinding.matchMainFab.setOnClickListener {
            showMutilAlertDialog(it)
        }
    }

    private fun initRecycleView(){
        mBinding.floorItem.layoutManager = LinearLayoutManager(this)
        mAdapter.setItemClickCallback(object :FloorItemAdapter.ItemClickCallback{
            override fun onClick(routing: String,projectId:Long) {
                ARouter.getInstance().build(routing)
                    .withInt(
                        com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID,
                        projectId!!.toInt()
                    )
                    .withString(com.sribs.common.ARouterPath.VAL_COMMON_TITLE,mTitle)
                        .navigation()
            }

        })
        mBinding.floorItem.adapter = mAdapter
    }

    override fun handlItemList(list: ArrayList<BuildingFloorItem>) {
        mAdapter.setData(list)

    }


    override fun getContext(): Context? = this

    override fun bindPresenter() {
       mPresent.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg)
    }

    override fun unbindPresenter() {
        mPresent.unbindView()
    }

    private var alertDialog3: AlertDialog? = null
    private val checkedItems = booleanArrayOf(
        false, false, false, false, false, false

    )
    val items = arrayOf(
        "建筑结构复核",  "倾斜测量",  "相对高差测量",  "构建测量",  "居民类检测测量",  "非居民类检测测量"
    )


    fun showMutilAlertDialog(view: View?) {
        var choseType = 0
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle("请选择新建模块")
        /**
         * 第一个参数:弹出框的消息集合，一般为字符串集合
         * 第二个参数：默认被选中的，布尔类数组
         * 第三个参数：勾选事件监听
         */

        alertBuilder.setSingleChoiceItems(items,0
        ) { _, which -> choseType = which }

        alertBuilder.setPositiveButton(
            "确定"
        ) { _, i ->
            mAdapter.addItem(BuildingFloorItem(mLocalProjectId,mBuildingId,choseType+1, TimeUtil.YMD_HMS.format(Date())))
            alertDialog3?.dismiss();

        }
        alertBuilder.setNegativeButton(
            "取消"
        ) { _, i -> alertDialog3!!.dismiss() }
        alertDialog3 = alertBuilder.create()
        alertDialog3!!.show()
        val listView: ListView = (alertDialog3 as AlertDialog).listView
        //设置dialog的宽高
        listView.addOnLayoutChangeListener(View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            val height = v.height
            val maxHeight = resources.displayMetrics.heightPixels * 5 / 10
            if (height > maxHeight) {
                val layoutParams: ViewGroup.LayoutParams = listView.getLayoutParams()
                layoutParams.height = maxHeight
                listView.layoutParams = layoutParams
            }
        })
    }


}