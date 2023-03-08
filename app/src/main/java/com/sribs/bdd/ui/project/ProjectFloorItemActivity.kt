package com.sribs.bdd.ui.project

import android.app.Dialog
import android.content.Context
import android.view.*
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.forEachIndexed
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.Config
import com.sribs.bdd.R
import com.sribs.bdd.bean.BuildingModule
import com.sribs.bdd.databinding.ActivityProjectFloorDetailBinding
import com.sribs.bdd.module.project.IProjectContrast
import com.sribs.bdd.module.project.ProjectFloorDetailPresent
import com.sribs.bdd.ui.adapter.FloorItemAdapter
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.utils.DialogUtil
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
    @Autowired(name = com.sribs.common.ARouterPath.VAL_PROJECT_UUID)
    var mLocalProjectUUID = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_PROJECT_NAME)
    var mProjectName = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_BUILDING_ID)
    var mBuildingId = -1L

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_BUILDING_UUID)
    var mBuildingUUID = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_BUILDING_NAME)
    var mBldName = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_VERSION)
    var mVersion = 0L

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID)
    var mRemoteId = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_STATUS)
    var mStatus = 0

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

    private val mPresenter: ProjectFloorDetailPresent by lazy { ProjectFloorDetailPresent() }

    private val mAdapter: FloorItemAdapter by lazy { FloorItemAdapter() }

    override fun deinitView() {
        unbindPresenter()
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        bindPresenter()
        mBinding.toolbar.tbTitle.text = mTitle+"/"+mBldName
        mBinding.toolbar.tb.setNavigationIcon(R.mipmap.icon_back)
        mBinding.toolbar.tb.showOverflowMenu()
        setSupportActionBar(mBinding.toolbar.tb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mBinding.toolbar.tb.setNavigationOnClickListener {
            finish()
        }

        initRecycleView()

        mPresenter.getLocalModule(mLocalProjectId, mBuildingId)

        mBinding.matchMainFab.setOnClickListener {
            showMutilAlertDialog(it)
        }
    }

    private fun initRecycleView() {
        mBinding.floorItem.layoutManager = LinearLayoutManager(this)
        mAdapter.setItemClickCallback(object : FloorItemAdapter.ItemClickCallback {

            override fun onEdit(
                moduleName: String,
                routing: String,
                moduleId: Long,
                isRemote: Boolean,
                isNonResident:Boolean
            ) {
                if (isRemote) {
                    showToast(getString(R.string.error_no_local))
                    return
                }
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
                    ).withString(
                        com.sribs.common.ARouterPath.VAL_PROJECT_NAME,
                        mProjectName
                    ).withString(
                        com.sribs.common.ARouterPath.VAL_BUILDING_NAME,
                        mBldName
                    ).withBoolean(com.sribs.common.ARouterPath.VAL_NON_RESIDENT,isNonResident)
                    .navigation()
            }

            override fun onConfig(
                moduleName: String,
                routing: String,
                moduleId: Long,
                isRemote: Boolean,
                isNonResident:Boolean
            ) {
                if (isRemote) {
                    showToast(getString(R.string.error_no_local))
                    return
                }
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
                        mProjectName+"/"+mBldName
                    ).withString(
                        com.sribs.common.ARouterPath.VAL_COMMON_LEADER,
                        mLeaderName
                    ).withBoolean(com.sribs.common.ARouterPath.VAL_NON_RESIDENT,isNonResident)
                    .navigation()

            }

            override fun onMore(bean: BuildingModule) {
                showBottomDialog(true, bean)
            }
        })
        mBinding.floorItem.adapter = mAdapter
    }

    var mBottomDialog: Dialog? = null

    fun showBottomDialog(show: Boolean, beanMain: BuildingModule) {
        if (show) {
            if (mBottomDialog?.isShowing == true) return
            mBottomDialog =
                DialogUtil.showBottomDialog(
                    this,
                    R.layout.dialog_common_bottom_building_select,
                    true
                ) {
                    when (it) {
                        0 -> { //上传配置
                            if (mStatus == 0) {
                                showToast("请先上传楼")
                                return@showBottomDialog
                            }

                            if (beanMain.status == 2) {
                                showToast(getString(R.string.error_no_local))
                                return@showBottomDialog
                            }

                            showPb(true)
                            mPresenter.uploadModule(beanMain) {
                                showPb(false)
                            }
                        }
                        1 -> { //下载配置
                            /* if(beanMain.remoteId.isNullOrEmpty()){
                                 showToast("非云端项目，请先上传再下载")
                                 return@showBottomDialog
                             }*/
                            doDownload(beanMain)
                        }
                        2 -> { // 删除
                            DialogUtil.showMsgDialog(this, "是否确认删除模块?", {
                                mPresenter.deleteModule(beanMain)
                            })

                        }
                        else -> {
                            showFab(true)
                        }
                    }
                }
            showFab(false)
        } else {
            if (mBottomDialog == null || mBottomDialog?.isShowing == false) {
                mBottomDialog = null
                return
            }
            mBottomDialog?.dismiss()
            mBottomDialog = null
        }
    }

    private fun showFab(b: Boolean) {
        if (b) {
            mBinding.matchMainFab.visibility = View.VISIBLE
        } else {
            mBinding.matchMainFab.visibility = View.GONE
        }
    }

    private fun showPb(b: Boolean) {
        runOnUiThread(object : Runnable {
            override fun run() {
                if (b) {
                    mBinding.pb.visibility = View.VISIBLE
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                } else {
                    mBinding.pb.visibility = View.GONE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        })
    }

    private fun doDownload(beanMain: BuildingModule) {
        if (mStatus == 0) {
            showToast("请先上传楼")
            return
        }
        if (!Config.isNetAvailable) {
            showToast(getString(R.string.error_no_network))
            return
        }

        mPresenter.getV3ModuleVersionHistory(
            beanMain.buildingUUID!!,
            beanMain.moduleUUID!!
        ) { versionList ->
            if (versionList.isNullOrEmpty()) {
                showToast(getString(R.string.error_no_history))
                return@getV3ModuleVersionHistory
            }
            LogUtils.d("版本个数: ${versionList.size}")
            com.sribs.bdd.utils.DialogUtil.showDownloadV3ProjectDialog(
                this, null, beanMain.updateTime!!,
                versionList!!.toTypedArray()
            ) { l ->
                if (l.isEmpty()) return@showDownloadV3ProjectDialog
                LOG.I("123", "${l[0]}")
                var idx = l[0]!!
                DialogUtil.showMsgDialog(this, "覆盖本地版本？", {
                    showPb(true)
                    mPresenter.downloadModuleConfig(
                        mProjectName,
                        mBldName,
                        beanMain,
                        beanMain.moduleUUID!!,
                        versionList[idx].version,
                    ) { isSuccess, msg ->
                        showToast(msg)
                        if (isSuccess) {
                            beanMain.also { b -> b.status = 4 }
                        }
                        showPb(false)
                    }
                }, {})

            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_module, menu)
        return true
    }

    var alert: AlertDialog? = null


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_module_refresh -> { //从远端获取更新
                /*if(mRemoteId.isNullOrEmpty()){
                    showToast("请先上传楼")
                    return super.onOptionsItemSelected(item)
                }*/
                mPresenter.getRemoteModule(
                    mLocalProjectId,
                    mLocalProjectUUID,
                    mBuildingUUID,
                    mBuildingId,
                    mVersion,
                    localList
                )
            }
            /*   R.id.menu_bld_upload -> { //上传
                   var data = mAdapter?.getData()
                   if (data == null || data.size <= 0) {
                       showToast("请先创建模块")
                       return super.onOptionsItemSelected(item)
                   }

                   var listLocalData = data.filter {
                       it.moduleid!! >0
                   }

                   if(listLocalData.isEmpty()){
                       showToast("请先下载或创建本地模块再上传")
                       return super.onOptionsItemSelected(item)
                   }

                   var items = Array<String>(listLocalData.size+1) {""}

                   var checkedList = ArrayList<String>()

                   items[0] = "全选"
                   listLocalData.forEachIndexed { index, buildingMainBean ->
                       items[index+1] = buildingMainBean.moduleName!!
                   }
                   var alertList: ListView?=null
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
                           listLocalData.forEachIndexed { index, buildingModuleBean ->
                               if(checkedList.contains(buildingModuleBean.moduleName)){
                                   mPresenter.uploadModule(buildingModuleBean)
                               }
                           }
                       }.create()
                   alertList = alert?.listView
                   alert?.show()
               }
               R.id.menu_bld_download_config -> { //下载配置

                   var data = mAdapter?.getData()
                   if (data == null || data.size <= 0) {
                       showToast("请先创建模块")
                       return super.onOptionsItemSelected(item)
                   }

                   var listRemoteData = data.filter {
                       !it.remoteId.isNullOrEmpty()
                   }

                   if(listRemoteData.isEmpty()){
                       showToast("云端无配置,请先上传再下载")
                       return super.onOptionsItemSelected(item)
                   }

                   var items = Array<String>(listRemoteData.size+1) {""}

                   var checkedList = ArrayList<String>()

                   items[0] = "全选"
                   listRemoteData.forEachIndexed { index, buildingMainBean ->
                       items[index+1] = buildingMainBean.moduleName!!
                   }
                   var alertList: ListView?=null
                   var alert = AlertDialog.Builder(this).setTitle("下载配置")
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

                       }.setPositiveButton("下载"){ dialog, which ->
                           //此处处理下载
                           LogUtils.d("checkedList: "+checkedList)

                           listRemoteData.forEachIndexed { index, buildingModule ->
                               if(checkedList.contains(buildingModule.moduleName)){
                                  mPresenter.downloadModule(buildingModule)
                               }
                           }

                       }.create()
                   alertList = alert?.listView
                   alert?.show()
               }*/
        }
        return super.onOptionsItemSelected(item)
    }

    var localList = ArrayList<BuildingModule>()

    override fun handlItemList(list: ArrayList<BuildingModule>) {
        LogUtils.d("获取模块数据: " + list)
        if (localList.size > 0) {
            localList.clear()
        }
        localList.addAll(list)
        localList.addAll(remoteList)
        mAdapter.setData(ArrayList(localList.sortedByDescending { b -> b.createTime }))
        setNullHint()
    }

    var remoteList = ArrayList<BuildingModule>()

    override fun handlRemoteItemList(list: ArrayList<BuildingModule>) {
        LogUtils.d("获取云端模块数据: " + list)
        remoteList.clear()
        remoteList.addAll(list)
        localList.addAll(remoteList)
        mAdapter.setData(ArrayList(localList.sortedByDescending { b -> b.createTime }))
        setNullHint()
    }

    fun setNullHint() {
        if (localList.size <= 0) {
            mBinding.nullHint.visibility = View.VISIBLE
        } else {
            mBinding.nullHint.visibility = View.GONE
        }
    }

    override fun addItem(bean: BuildingModule) {
        mAdapter.addItem(bean)
    }

    override fun removeItem(bean: BuildingModule) {
        mAdapter.removeItem(bean)
    }


    override fun getContext(): Context? = this

    override fun bindPresenter() {
        mPresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg)
    }

    override fun unbindPresenter() {
        mPresenter.unbindView()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        showPb(false)
    }

    private var alertDialog3: AlertDialog? = null
    private val checkedItems = booleanArrayOf(
        false, false, false, false, false, false

    )
    val items = arrayOf(
        "全选", "建筑结构复核", "倾斜测量", "相对高差测量", "构件检测"/*, "居民类检测"*/, "非居民类检测"
    )

    fun showMutilAlertDialog(view: View?) {

        var checkedList = ArrayList<String>()

        var checkBooleanList = booleanArrayOf(
            false, false, false, false, false,false
        )
        localList.forEach {
            items.forEachIndexed { index, s ->
                if (it.moduleName == s) {
                    checkedList.add(s)
                    checkBooleanList.set(index, true)
                }
            }
        }

        var alertList: ListView? = null
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle("添加检测模块")
        /**
         * 第一个参数:弹出框的消息集合，一般为字符串集合
         * 第二个参数：默认被选中的，布尔类数组
         * 第三个参数：勾选事件监听
         */

        alertBuilder.setMultiChoiceItems(items, checkBooleanList) { dialog, which, isChecked ->
            if (which == 0) {
                if (isChecked) {
                    alertList?.forEachIndexed { index, view ->
                        alertList?.setItemChecked(index, true)
                    }
                    checkedList.clear()
                    checkedList.addAll(items.filter {
                        !it.equals("全选")
                    })
                } else {
                    alertList?.forEachIndexed { index, view ->
                        alertList?.setItemChecked(index, false)
                    }
                    checkedList.clear()
                }
            } else {
                if (isChecked) {
                    if(!checkedList.contains(items[which])){
                        checkedList.add(items[which])
                    }
                } else {
                    if(checkedList.contains(items[which])){
                        checkedList.remove(items[which])
                    }
                }
            }
        }

        alertBuilder.setPositiveButton(
            "确定"
        ) { _, i ->
            var isSame = false
            /*  var moduleName = ""
              checkedList.forEach {
                  isSame = mAdapter.hasSameName(it)
                  if(isSame) {
                      if (moduleName.isNullOrEmpty()) {
                          moduleName += it
                      } else {
                          moduleName += "," + it
                      }
                  }
              }
              if (!moduleName.isNullOrEmpty()) {
                  showToast(moduleName + "已存在，请勿重复创建")
                  return@setPositiveButton
              }*/

            var filterList=ArrayList<String>()

            checkedList.forEach {
                isSame = mAdapter.hasSameName(it)
                if(!isSame){
                    filterList.add(it)
                }
            }

            LogUtils.d("过滤后的模块: "+filterList)

            if(filterList.size<=0){
                return@setPositiveButton
            }
            mPresenter.createOrSaveModule(
                mLocalProjectId,
                mLocalProjectUUID,
                mBuildingId,
                mBuildingUUID,
                mRemoteId,
                mProjectName,
                mBldName,
                filterList,
            )
            alertDialog3?.dismiss();

        }
        alertBuilder.setNegativeButton(
            "取消"
        ) { _, i -> alertDialog3!!.dismiss() }
        alertDialog3 = alertBuilder.create()
        alertList = alertDialog3?.listView
        alertDialog3!!.show()
    }
}