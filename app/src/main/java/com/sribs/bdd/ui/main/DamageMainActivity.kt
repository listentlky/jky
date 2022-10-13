package com.sribs.bdd.ui.main

import android.app.Dialog
import android.content.Context
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.adapter.BasePagerAdapter
import com.cbj.sdk.libui.mvp.inflate
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.sribs.bdd.Config
import com.sribs.bdd.R
import com.sribs.bdd.action.Dict
import com.sribs.bdd.bean.MainProjectBean
import com.sribs.bdd.bean.data.ProjectCreateDataBean
import com.sribs.bdd.databinding.ActivityDamageMainBinding
import com.sribs.bdd.module.main.IMainListContrast
import com.sribs.bdd.module.main.MainPresenter
import com.sribs.bdd.utils.CreateDialog
import com.sribs.common.bean.CommonBtnBean
import com.sribs.common.utils.DialogUtil
import org.aspectj.tools.ajc.Main
import java.util.*

/**
 * @date 2021/6/23
 * @author elijah
 * @Description
 */
@Route(path= com.sribs.common.ARouterPath.DAMAGE_MAIN_ATY)
class DamageMainActivity :BaseActivity(),IMainListContrast.IMainView{

    @JvmField
    @Autowired(name = "from")
    var mFrom:String=""

    private val mBinding:ActivityDamageMainBinding by inflate()

    private val mPresenter by lazy { MainPresenter() }

    private val mFragments by lazy {
        listOf(
            ARouter.getInstance().build(com.sribs.common.ARouterPath.MAIN_LIST_FGT)
                .navigation() as BaseFragment,
            ARouter.getInstance().build(com.sribs.common.ARouterPath.MAIN_MAP_FGT)
                .navigation() as BaseFragment
        )
    }

    private val mDrawerFragment by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.MAIN_DRAWER_FGT)
            .navigation() as MainDrawerFragment
    }

    private var mCurMainProject:MainProjectBean?=null
    var mBottomDialog: Dialog?=null


    private fun initCreateDialog() {
        var createDialog = CreateDialog(this, -1) {

        }
        createDialog.show()

    }


    override fun deinitView() {
        unbindPresenter()
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        bindPresenter()
        initToolbar()
        initViewPager()
        initSearchBar()
        initDrawerLayout()

        mBinding.matchMainFab.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN){
               /* ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CREATE_ATY)
                    .withInt("x",event.x.toInt())
                    .withInt("y",event.y.toInt())
                    .withString("from",mFrom)
                    .navigation()*/
                initCreateDialog()
            }

            false
        }


        Dict.init()

//        val extrsa = intent.extras
//        if (extrsa != null) {
//            mFromModule = extrsa.getString("from").toString()
//        }

        //get permission to open and write pdf
        XXPermissions.with(this) // 适配 Android 11 分区存储这样写
            //.permission(Permission.Group.STORAGE)
            // 不适配 Android 11 分区存储这样写
            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
            .request(object : OnPermissionCallback {

                override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                    if (all) {
                        println("leon 获取读写外部存储权限成功")
                    } else {
                        println("leon 获取读写外部存储权限部分成功")
                    }
                }

                override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                    if (never) {
                        println("leon 被永久拒绝读写外部存储权限") // 如果是被永久拒绝就跳转到应用权限系统设置页面
                    } else {
                        println("leon 获取读写外部存储权限失败")
                    }
                }
            })
    }

    /**
     * @Description toolbar
     */
    private fun initToolbar(){
        mBinding.toolbarTitle.text = "全部项目"
        mBinding.toolbar.setNavigationIcon(R.mipmap.icon_avatar)
        mBinding.toolbar.showOverflowMenu()
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mBinding.toolbar.setNavigationOnClickListener {
            ARouter.getInstance().build(com.sribs.common.ARouterPath.MAIN_SETTING_ATY)
                .navigation()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    var mMenuMapView:View?=null
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        var item = menu?.findItem(R.id.menu_main_pop_local)

        item?.icon?.setBounds(20,50,0,0)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item?.itemId){
            R.id.menu_main_map -> {
                var idx = mBinding.matchMainVp.currentItem + 1
                LOG.I("123","idx=${mBinding.matchMainVp.currentItem}")
                if (idx>1)idx = 0
                mBinding.matchMainVp.currentItem = idx
            }
            R.id.menu_main_info -> {


            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * @Description vp
     */
    private fun initViewPager(){
        mBinding.matchMainVp.setSmooth(false)
        mBinding.matchMainVp.setScroll(false)
        mBinding.matchMainVp.adapter = BasePagerAdapter(supportFragmentManager,mFragments)
        mBinding.matchMainVp.offscreenPageLimit = 2
    }

    fun selectProject(b:MainProjectBean){
        mCurMainProject = b
    }

    public fun getProject():MainProjectBean?{
        return mCurMainProject
    }

    fun showBottomDialog(show:Boolean,pos:Int,beanMain: MainProjectBean){
        if (show){
            if (mBottomDialog?.isShowing == true)return
            mBottomDialog = DialogUtil.showBottomDialog(this,R.layout.dialog_common_bottom_select,true){
                when(it){
                    0->{ // 上传配置
                        if(beanMain.localId < 0){
                            showToast(getString(R.string.error_no_local))
                            return@showBottomDialog
                        }
                        mPresenter.uploadProject(beanMain)
                        /*ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CREATE_ATY)
                            .withInt(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID,beanMain.localId.toInt())
                            .navigation()*/
                      //  initCreateDialog()
                    }
               /*     1->{
                        if (beanMain.remoteId.isNullOrEmpty() || !Config.isNetAvailable){
                            //获取本地汇总

                            mPresenter.projectGetLocalSummary(beanMain.localId){res->
                                com.sribs.bdd.utils.DialogUtil.showAllInfoDialog(
                                    this,
                                    "本地记录汇总",
                                    res.roomCount.toString(),
                                    res.finishedCount.toString(),
                                    res.notAllowedCount.toString(),
                                    res.notAllowedCount.toString(),
                                    String.format("%.0f",res.entryRatio)+"%"
                                )
                            }
                        } else {
                            mPresenter.projectGetSummary(beanMain.remoteId){res->
                                com.sribs.bdd.utils.DialogUtil.showAllInfoDialog(
                                    this,
                                    "记录汇总",
                                    res.roomCount.toString(),
                                    res.finishedCount.toString(),
                                    res.notAllowedCount.toString(),
                                    res.noPersonCount.toString(),
                                    String.format("%.0f",res.entryRatio)+"%"
                                )
                            }
                        }
                    }*/
                    1->{
                        doDownload(beanMain,false)
                    }
                    2-> {
                        doDownload(beanMain,true)
                    }
                    3->{
                        DialogUtil.showMsgDialog(this,"是否确认删除项目?",{
                            //TODO del project
                            /*  if (beanMain.localId>0){
                                  mPresenter.projectDelete(beanMain.localId)
                              }else{
                                  showToast("无法删除云端项目")
                              }*/
                            mPresenter.projectDelete(beanMain.localId)
                        })
                    }
                    else->{
                        showFab(true)
                        mFragments.forEach { f->
                            if (f is MainListFragment) f.menuUnSel(pos)
                            if (f is MainMapFragment) f.menuUnSel(pos)
                        }
                    }
                }
            }
            showFab(false)

        }else{
            if (mBottomDialog==null || mBottomDialog?.isShowing == false){
                mBottomDialog = null
                return
            }
            mBottomDialog?.dismiss()
            mBottomDialog = null
        }
    }

    private fun doDownload(beanMain:MainProjectBean,isAll:Boolean){
        if (beanMain.remoteId.isNullOrEmpty()){
            showToast(getString(R.string.error_no_remote))
            return
        }
        if (!Config.isNetAvailable){
            showToast(getString(R.string.error_no_network))
            return
        }
        if (isAll){
            mPresenter.projectGetRecordHistory(beanMain.remoteId){ history->
                if (history.isNullOrEmpty()){
                    showToast(getString(R.string.error_no_history))
                    return@projectGetRecordHistory
                }
                com.sribs.bdd.utils.DialogUtil.showDownloadProjectDialog(this,null,beanMain.updateTime,
                    history!!.toTypedArray()){ l->
                    if (l.isEmpty())return@showDownloadProjectDialog
                    LOG.I("123","${l[0]}")
                    var idx = l[0]!!
                    DialogUtil.showMsgDialog(this,"覆盖本地版本？",{
                        showPb(true)
                        mPresenter.projectDownLoadRecord(
                            history[idx].remoteConfigHistoryId?:"",
                            history[idx].remoteRecordHistoryId?:"",
                            beanMain.also { b->b.status = resources.getStringArray(R.array.main_project_status)[3] }){
                            showPb(false)
                        }
                    },{})
                }
            }
        }else{
            mPresenter.projectGetConfigHistory(beanMain.remoteId){ history->
                if (history.isNullOrEmpty()){
                    showToast(getString(R.string.error_no_history))
                    return@projectGetConfigHistory
                }
                LOG.I("123","history size=${history.size}")
                com.sribs.bdd.utils.DialogUtil.showDownloadProjectDialog(this,null,beanMain.updateTime,
                    history!!.toTypedArray()){ l->
                    if (l.isEmpty())return@showDownloadProjectDialog
                    LOG.I("123","${l[0]}")
                    var idx = l[0]!!
                    DialogUtil.showMsgDialog(this,"覆盖本地版本？",{
                        showPb(true)
                        mPresenter.projectDownLoadConfig(
                            history[idx].remoteConfigHistoryId?:"",
                            beanMain.also { b->b.status = resources.getStringArray(R.array.main_project_status)[4] },
                        ){
                            showPb(false)
                        }
                    },{})

                }
            }
        }
    }

    private fun showPb(b:Boolean){
        if (b){
            mBinding.pb.visibility = View.VISIBLE
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }else{
            mBinding.pb.visibility = View.GONE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun showFab(b:Boolean){
        if (b){
            var p = mBinding.matchMainFab.layoutParams as CoordinatorLayout.LayoutParams
            p.anchorId = R.id.match_main_vp
            mBinding.matchMainFab.layoutParams = p
            mBinding.matchMainFab.visibility = View.VISIBLE
        }else{
            var p = mBinding.matchMainFab.layoutParams as CoordinatorLayout.LayoutParams
            p.anchorId = View.NO_ID
            mBinding.matchMainFab.layoutParams = p
            mBinding.matchMainFab.visibility = View.GONE
        }
    }

    /**
     * @Description search
     */
    private fun initSearchBar(){
        var searchView = mBinding.mainSearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                (mFragments[0] as MainListFragment).search(query?:"")
                (mFragments[1] as MainMapFragment).search(query?:"")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()){
                    (mFragments[0] as MainListFragment).search("")
                    (mFragments[1] as MainMapFragment).search("")
                }
                return false
            }

        })


    }

    fun drawerConfirm(l:ArrayList<CommonBtnBean>){
        LOG.I("123","选择了 l=${l.map { it.title }}")
        var selList = l.map {
            var y = if (it.title=="今年"){
                var c = Calendar.getInstance()
                c.time = Date()
                c.get(Calendar.YEAR).toString()
            }else{
                it.title
            }
            y.replace("年","")
        }

        (mFragments[0] as MainListFragment).search(ArrayList(selList))
        (mFragments[1] as MainMapFragment).search(ArrayList(selList))

        mBinding.mainDrawerLayout.closeDrawers()
    }

    private fun initDrawerLayout(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_drawer_ll,mDrawerFragment)
            .commit()


        mBinding.mainDrawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {
                showFab(true)
            }

            override fun onDrawerStateChanged(newState: Int) {
            }

        })
        mBinding.mainDrawerBtn.setOnClickListener {
            mBinding.mainDrawerLayout.openDrawer(Gravity.RIGHT)
            showFab(false)
        }
    }

    override fun getContext(): Context = this

    override fun bindPresenter() {
        mPresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg)
    }

    override fun unbindPresenter() {
        mPresenter.unbindView()
    }

    private var mLastClick = 0L

    override fun onBackPressed() {
        if(mBinding.matchMainVp.currentItem != 0){
            mBinding.matchMainVp.currentItem = 0
            return
        }
        var cur = System.currentTimeMillis()
        if (cur - mLastClick < 2000) {
            super.onBackPressed()
        } else {
            showToast("再次点击退出")
            mLastClick = cur
        }
    }
}