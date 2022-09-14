package com.sribs.bdd.ui.house

import android.content.Context
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.adapter.BasePagerAdapter
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.R
import com.sribs.bdd.databinding.ActivityUnitListBinding
import com.sribs.bdd.databinding.ItemTabBinding
import com.sribs.bdd.module.house.IUnitListContrast
import com.sribs.bdd.module.house.UnitListPresenter
import com.sribs.bdd.utils.DialogUtil
import com.sribs.common.bean.db.UnitBean

@Route(path= com.sribs.common.ARouterPath.HOUSE_UNIT_LIST_ATY)
class UnitListActivity:BaseActivity(),IUnitListContrast.IView {

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID)
    var mLocalProjectId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    var mTabTitleList:Array<String>?=null

    var mFragments:ArrayList<Fragment>?=null

    private val mPresenter by lazy { UnitListPresenter() }

    private val mBinding :ActivityUnitListBinding by inflate()

    override fun deinitView() {
        unbindPresenter()
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        bindPresenter()
        initData()
        initToolbar()
    }

    private fun initToolbar(){
        mBinding.tb.setNavigationIcon(R.mipmap.icon_back)
        mBinding.tbTitle.text = mTitle
        mBinding.tb.showOverflowMenu()
        setSupportActionBar( mBinding.tb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mBinding.tb.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_unit_vp, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.menu_unit_upload->{
                var idx = mBinding.unitListVp.currentItem
                if (mFragments?.size?:0 > idx){
                    var f = mFragments!![idx] as UnitListFragment
                    if (f.mLocalUnitId<0){
                        showToast("请先下载云端单元")
                        return true
                    }

                    com.sribs.common.utils.DialogUtil.showMsgDialog(this,"是否覆盖","不覆盖","覆盖",{
                        showPb(true)
                        mPresenter.uploadUnit(f.mProjectId,f.mLocalUnitId,f.mUnitNo,false)
                    },{
                        showPb(true)
                        mPresenter.uploadUnit(f.mProjectId,f.mLocalUnitId,f.mUnitNo,true)
                    })


                }
            }
            R.id.menu_unit_download_config->{
                var idx = mBinding.unitListVp.currentItem
                if (mFragments?.size?:0 > idx){
                    var f = mFragments!![idx] as UnitListFragment
                    mPresenter.unitGetConfigHistory(f.mProjectId,f.mLocalUnitId,f.mUnitNo.toString()){ history, localUpdateTime->
                        if (history.isNullOrEmpty()){
                            showToast(getString(R.string.error_no_history))
                            return@unitGetConfigHistory
                        }
                        DialogUtil.showDownloadProjectDialog(this,null,localUpdateTime,history){ l->
                            if (l.isEmpty())return@showDownloadProjectDialog
                            var idx = l[0]!!
                            com.sribs.common.utils.DialogUtil.showMsgDialog(this,"覆盖本地版本？",{
                                showPb(true)
                                mPresenter.unitDownloadConfig(
                                    history[idx].unitId!!, f.mProjectId,1,
                                    if (f.mLocalUnitId>0) f.mLocalUnitId else null
                                ){
                                    showPb(false)
                                }
                            })
                        }
                    }
                }
            }
            R.id.menu_unit_download_all->{
                var idx = mBinding.unitListVp.currentItem

                if (mFragments?.size?:0>idx){
                    var f = mFragments!![idx] as UnitListFragment
                    mPresenter.unitGetRecordHistory(f.mProjectId,f.mLocalUnitId,f.mUnitNo.toString()){ history,localUpdatTime->
                        if (history.isNullOrEmpty()){
                            showToast(getString(R.string.error_no_history))
                            return@unitGetRecordHistory
                        }
                        DialogUtil.showDownloadProjectDialog(this,null,localUpdatTime,history){ l->
                            if (l.isEmpty())return@showDownloadProjectDialog
                            com.sribs.common.utils.DialogUtil.showMsgDialog(this,"覆盖本地版本？",{
                                showPb(true)
                                LOG.I("123","idx=${l[0]}    historySize=${history.size}")
                                mPresenter.unitDownloadRecord(
                                    history[l[0]].unitId!!, f.mProjectId,
                                    if (f.mLocalUnitId>0) f.mLocalUnitId else null
                                ){
                                    showPb(false)
                                }
                            })
                        }
                    }


                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class MyAdapter(list: ArrayList<Fragment>, var tabTitle: Array<String>?) :
        BasePagerAdapter(supportFragmentManager, list) {

        override fun getPageTitle(position: Int): CharSequence? {
            if (tabTitle == null) return null
            if (position > tabTitle?.size ?: 0) return null
            return tabTitle!![position]
        }

        fun updateData(list: ArrayList<Fragment>,  tabTitle: Array<String>?){
            this.list = list
            this.tabTitle = tabTitle
            notifyDataSetChanged()
        }
    }

    private fun initData(){
        mPresenter.getAllUnit(mLocalProjectId)

    }


    private fun initViewPager(){
        if (mFragments?.isNullOrEmpty()?:true)return
        var tabTitle = mTabTitleList
        mBinding.unitListVp.setSmooth(true)
        mBinding.unitListVp.setScroll(true)
        if( mBinding.unitListVp.adapter == null){
            mBinding.unitListVp.adapter = MyAdapter(mFragments!!,tabTitle)
        }else{
            (mBinding.unitListVp.adapter as MyAdapter)
                .updateData(mFragments!!,tabTitle)
        }

        mBinding.unitListVp.offscreenPageLimit = mFragments!!.size
    }

    private fun initTabLayout(){
        var tabLayout = mBinding.unitListTab
        tabLayout.setupWithViewPager(mBinding.unitListVp,true)

        for (i in 0 until tabLayout.tabCount){
            var tab = tabLayout.getTabAt(i)

            var tvBinding = ItemTabBinding.inflate(LayoutInflater.from(this),null,false)
            tvBinding.tabTv.text = tab?.text


            tvBinding.tabTv.layoutParams = ViewGroup.LayoutParams(
                resources.getDimensionPixelOffset(R.dimen._67sdp),
                resources.getDimensionPixelOffset(R.dimen._20sdp))
            tab?.customView = tvBinding.root

        }
    }

    override fun getContext(): Context = this

    override fun onAllUnit(l: List<UnitBean>) {
        mTabTitleList = l.map { b-> "${b.unitNo}" }.toTypedArray()
        if(mFragments==null) mFragments = ArrayList()
        l.forEachIndexed { i, b ->
            if (i>=mFragments!!.size){
                mFragments!!.add(
                    ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_UNIT_LIST_FGT)
                        .withLong(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID,b.unitId?:-1)
                        .withLong(com.sribs.common.ARouterPath.VAL_PROJECT_ID,mLocalProjectId)
                        .withString(com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID,b.remoteId)
                        .withString (com.sribs.common.ARouterPath.VAL_UNIT_NO,b.unitNo!!)
                        .navigation() as UnitListFragment
                )
            }else{
                var f = mFragments!![i]
                if (f is UnitListFragment){
                    f.updateData(mLocalProjectId,b.unitId?:-1L,b.remoteId?:"",b.unitNo!!)
                }else{
                    mFragments!![i] =
                        ARouter.getInstance().build(com.sribs.common.ARouterPath.HOUSE_UNIT_LIST_FGT)
                            .withLong(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID,b.unitId?:-1)
                            .withLong(com.sribs.common.ARouterPath.VAL_PROJECT_ID,mLocalProjectId)
                            .withString(com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID,b.remoteId)
                            .withString (com.sribs.common.ARouterPath.VAL_UNIT_NO,b.unitNo!!)
                            .navigation() as UnitListFragment
                }
            }
        }
        initViewPager()
        initTabLayout()
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

    override fun onUpdate(b: Boolean) {
        showPb(false)
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

    fun showNoListDialog(){
        AlertDialog.Builder(this).setTitle(R.string.dialog_need_config_title)
            .setMessage(R.string.dialog_create_unit_hint_content).setPositiveButton(R.string.dialog_ok) { dialog, which ->
                ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CREATE_ATY_TYPE)
                    .navigation()
            }.setNegativeButton(R.string.dialog_cancel
            ) { dialog, which ->

            }
            .show()
    }
}