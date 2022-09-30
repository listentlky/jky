package com.sribs.bdd.ui.project

import android.content.Context
import android.content.DialogInterface
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import cc.shinichi.library.tool.ui.ToastUtil
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
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.utils.TimeUtil
import java.util.*
import kotlin.collections.ArrayList


/**
 * 单栋楼下模块界面
 */
@Route(path = com.sribs.common.ARouterPath.PRO_ITEM_ATY_FLOOR)
class ProjectFloorItemActivity : BaseActivity(), IProjectContrast.IProjectFloorDetailView {

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_PROJECT_ID)
    var mLocalProjectId = -1L

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_PROJECT_NAME)
    var mProjectName = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_BUILDING_ID)
    var mBuildingId = -1L

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_BUILDING_NAME)
    var mBldName = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID)
    var mRemoteId = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_INSPECTOR)
    var mInspector = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_LEADER)
    var mLeaderName = ""

    private val mBinding: ActivityProjectFloorDetailBinding by inflate()

    private val mPresent: ProjectFloorDetailPresent by lazy { ProjectFloorDetailPresent() }

    private val mAdapter: FloorItemAdapter by lazy { FloorItemAdapter() }

    override fun deinitView() {
        unbindPresenter()
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        bindPresenter()
        mBinding.toolbar.tbTitle.text = mTitle
        mBinding.toolbar.tb.setNavigationIcon(R.mipmap.icon_back)
        mBinding.toolbar.tb.showOverflowMenu()
        setSupportActionBar(mBinding.toolbar.tb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mBinding.toolbar.tb.setNavigationOnClickListener {
            finish()
        }

        initRecycleView()

        //    if (mRemoteId.isNullOrEmpty()) {
        //TODO 查询本地数据库
        mPresent.getLocalModule(mLocalProjectId, mBuildingId)
        /*     } else {
                 //TODO 查询网络接口
                 mPresent.getRemoteModule(mLocalProjectId, mBuildingId)
             }*/

        mBinding.matchMainFab.setOnClickListener {
            showMutilAlertDialog(it)
        }
    }

    private fun initRecycleView() {
        mBinding.floorItem.layoutManager = LinearLayoutManager(this)
        mAdapter.setItemClickCallback(object : FloorItemAdapter.ItemClickCallback {

            override fun onEdit(moduleName: String, routing: String, moduleId: Long) {
                ARouter.getInstance().build(routing)
                    .withLong(
                        com.sribs.common.ARouterPath.VAL_PROJECT_ID,
                        mLocalProjectId
                    )
                    .withLong(
                        com.sribs.common.ARouterPath.VAL_BUILDING_ID,
                        mBuildingId
                    )
                    .withLong(
                        com.sribs.common.ARouterPath.VAL_MODULE_ID,
                        moduleId
                    )
                    .withString(
                        com.sribs.common.ARouterPath.VAL_MODULE_NAME,
                        moduleName
                    )
                    .withString(
                        com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID,
                        mRemoteId
                    ).withString(
                        com.sribs.common.ARouterPath.VAL_COMMON_INSPECTOR,
                        mInspector
                    ).withString(
                        com.sribs.common.ARouterPath.VAL_COMMON_TITLE,
                        mTitle
                    ).withString(
                        com.sribs.common.ARouterPath.VAL_COMMON_LEADER,
                        mLeaderName
                    )
                    .navigation()
            }

            override fun onConfig(moduleName: String, routing: String, moduleId: Long) {
                ARouter.getInstance().build(routing)
                    .withLong(
                        com.sribs.common.ARouterPath.VAL_PROJECT_ID,
                        mLocalProjectId
                    )
                    .withLong(
                        com.sribs.common.ARouterPath.VAL_BUILDING_ID,
                        mBuildingId
                    )
                    .withLong(
                        com.sribs.common.ARouterPath.VAL_MODULE_ID,
                        moduleId
                    )
                    .withString(
                        com.sribs.common.ARouterPath.VAL_MODULE_NAME,
                        moduleName
                    )
                    .withString(
                        com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID,
                        mRemoteId
                    ).withString(
                        com.sribs.common.ARouterPath.VAL_COMMON_INSPECTOR,
                        mInspector
                    ).withString(
                        com.sribs.common.ARouterPath.VAL_COMMON_TITLE,
                        mTitle
                    ).withString(
                        com.sribs.common.ARouterPath.VAL_COMMON_LEADER,
                        mLeaderName
                    )
                    .navigation()

            }

            override fun onDelete(moduleId: Long) {
                mPresent.deleteModule(mLocalProjectId, mBuildingId, moduleId) {
                    ToastUtil.getInstance()._short(getContext(), "本地module删除成功")
                }

            }

        })
        mBinding.floorItem.adapter = mAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_unit_vp, menu)
        return true
    }

    var alert:AlertDialog?= null



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_unit_upload -> { //上传
                var data = mAdapter.getData()
                if (data == null || data.size <= 0) {
                    showToast("请先创建模块")
                    return super.onOptionsItemSelected(item)
                }
                var items = Array<String>(data.size+1) {""}

                var checkedList = ArrayList<String>()

                items[0] = "全选"
                data.forEachIndexed { index, buildingFloorItem ->
                    items[index+1] = buildingFloorItem.name!!
                }
                var alertList:ListView?=null
                var alert = AlertDialog.Builder(this).setTitle("上传配置")
                    .setMultiChoiceItems(items, null) { dialog, which, isChecked ->
                        if(which == 0){
                            if(isChecked){
                                alertList?.forEachIndexed { index, view ->
                                    alertList?.setItemChecked(index,true)
                                }
                                checkedList.addAll(items.filter {
                                    !it.equals("全选")
                                })
                            }else{
                                alertList?.forEachIndexed { index, view ->
                                    alertList?.setItemChecked(index,false)
                                }
                                checkedList.clear()
                            }
                        }else{
                            if(isChecked){
                                checkedList.add(items[which])
                            }else{
                                checkedList.remove(items[which])
                            }
                        }
                    }.setNegativeButton("取消"){ dialog, which ->

                    }.setPositiveButton("上传"){ dialog, which ->
                        //此处处理上传
                        LogUtils.d("checkedList: "+checkedList)
                    }.create()
                alertList = alert?.listView
                alert?.show()
            }
            R.id.menu_unit_download_config -> { //下载配置


            }
            R.id.menu_unit_download_all -> { //下载所有配置


            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun handlItemList(list: ArrayList<BuildingFloorItem>) {
        mAdapter.setData(list)
    }

    override fun addItem(bean: BuildingFloorItem) {
        mAdapter.addItem(bean)
    }

    override fun removeItem(bean: BuildingFloorItem) {
        mAdapter.removeItem(bean)
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
        "建筑结构复核", "倾斜测量", "相对高差测量", "构建检测", "居民类检测测量", "非居民类检测测量"
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

        alertBuilder.setSingleChoiceItems(
            items, 0
        ) { _, which -> choseType = which }



        alertBuilder.setPositiveButton(
            "确定"
        ) { _, i ->

            if (mAdapter.hasSameName(items.get(choseType))) {
                onMsg("模块请勿重复创建~")

            } else {

                mPresent.createOrSaveModule(
                    mLocalProjectId,
                    mBuildingId,
                    mRemoteId,
                    mProjectName,
                    mBldName,
                    items.get(choseType)
                ) {
                    LogUtils.d("模块ID：" + it)
                    mAdapter.addItem(
                        (BuildingFloorItem(
                            it,
                            mBuildingId,
                            items.get(choseType),
                            TimeUtil.YMD_HMS.format(Date())
                        ))
                    )
                    LogUtils.d("llf传递数据" + TimeUtil.YMD_HMS.format(Date()))

                }
            }

            /*mAdapter.addItem(
                BuildingFloorItem(
                    mLocalProjectId,
                    mBuildingId,
                    choseType + 1,
                    TimeUtil.YMD_HMS.format(Date())
                )
            )*/
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