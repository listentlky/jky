package com.sribs.bdd.v3.ui.check.cd

import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.adapter.BasePagerAdapter
import com.cbj.sdk.libui.mvp.inflate
import com.radaee.pdf.Global
import com.sribs.bdd.R
import com.sribs.bdd.databinding.ActivityCheckComponentDetectionBinding

/**
 *    author :
 *    e-mail :
 *    date   : 2022/9/611:15
 *    desc   :
 *    version: 1.0
 */
@Route(path = com.sribs.common.ARouterPath.CHECK_COMPONENT_DETECTION_ACTIVITY)
class CheckComponentDetectionActivity : BaseActivity() {

    private val mBinding: ActivityCheckComponentDetectionBinding by inflate()

    private val mFragments by lazy {
        listOf(
            ARouter.getInstance()
                .build(com.sribs.common.ARouterPath.CHECK_COMPONENT_DETECTION_FRAGMENT)
                .navigation() as BaseFragment,
            ARouter.getInstance()
                .build(com.sribs.common.ARouterPath.CHECK_COMPONENT_DETECTION_BEAM_FRAGMENT)
                .navigation() as BaseFragment,
            ARouter.getInstance()
                .build(com.sribs.common.ARouterPath.CHECK_COMPONENT_DETECTION_COLUMN_FRAGMENT)
                .navigation() as BaseFragment,
            ARouter.getInstance()
                .build(com.sribs.common.ARouterPath.CHECK_COMPONENT_DETECTION_WALL_FRAGMENT)
                .navigation() as BaseFragment,
            ARouter.getInstance()
                .build(com.sribs.common.ARouterPath.CHECK_COMPONENT_DETECTION_PLATE_FRAGMENT)
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
        Global.Init(this)

    }

    /**
     * @Description 初始化toolbar
     */
    private fun initToolbar() {
        mBinding.toolbarTitle.text = "项目名称"
        mBinding.toolbar.setNavigationIcon(R.mipmap.icon_back)
        mBinding.toolbar.showOverflowMenu()
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mBinding.toolbar.setNavigationOnClickListener {
            if (mBinding.checkVp.currentItem != 0) {
                mBinding.checkVp.currentItem = 0
            } else {
                finish()
            }
        }


    }

    private fun initViewPager() {
        mBinding.checkVp.setSmooth(false)
        mBinding.checkVp.setScroll(false)
        mBinding.checkVp.adapter = BasePagerAdapter(supportFragmentManager, mFragments)
        mBinding.checkVp.offscreenPageLimit = 4
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
                setVpCurrentItem(0)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun setVpCurrentItem(item: Int) {
        mBinding.checkVp.currentItem = item
    }


}