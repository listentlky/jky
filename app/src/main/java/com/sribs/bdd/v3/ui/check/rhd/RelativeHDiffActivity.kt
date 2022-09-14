package com.sribs.bdd.v3.ui.check.rhd

import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.adapter.BasePagerAdapter
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.R
import com.sribs.bdd.databinding.ActivityRelativeHdiffBinding
import com.sribs.bdd.v3.dialog.checkRHDDialog

@Route(path = com.sribs.common.ARouterPath.CHECK_RELATIVE_H_DIFF_ACTIVITY)
class RelativeHDiffActivity : BaseActivity() {

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""

    private val mBinding: ActivityRelativeHdiffBinding by inflate()

    private val mFragments by lazy {
        listOf(
            ARouter.getInstance()
                .build(com.sribs.common.ARouterPath.CHECK_RELATIVE_H_DIFF_FRAGMENT)
                .navigation() as BaseFragment,
        )
    }

    override fun deinitView() {

    }

    override fun dispose(): Unit? {
        mFragments.forEach {
            it.dispose()
            it.deinitView()
        }
        return super.dispose()
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        initToolbar()
        initViewPager()
    }

    /**
     * @Description 初始化toolbar
     */
    private fun initToolbar() {
        mBinding.toolbarTitle.text = mTitle
        mBinding.toolbar.setNavigationIcon(R.mipmap.icon_back)
        mBinding.toolbar.showOverflowMenu()
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mBinding.toolbar.setNavigationOnClickListener {
            if(mBinding.checkVp.currentItem != 0){
                mBinding.checkVp.currentItem = 0
            }else{
                finish()
            }
        }
    }

    private fun initViewPager() {
        mBinding.checkVp.setSmooth(false)
        mBinding.checkVp.setScroll(false)
        mBinding.checkVp.adapter = BasePagerAdapter(supportFragmentManager, mFragments)
        mBinding.checkVp.offscreenPageLimit = 2
        mBinding.checkVp.currentItem = 0
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_check, menu)
        return true
    }

    var mMenuMapView: View? = null
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        var item = menu?.findItem(R.id.menu_check_pop)

        item?.icon?.setBounds(20, 50, 0, 0)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item?.itemId) {
            R.id.menu_check_pop -> { // 菜单111

            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun setVpCurrentItem(item: Int) {
        mBinding.checkVp.currentItem = item
    }

    fun showEditDialog(){
        checkRHDDialog(this).show()
    }
}