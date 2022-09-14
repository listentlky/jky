package com.sribs.bdd.ui.main

import android.content.pm.PackageManager
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.sribs.bdd.R
import com.sribs.bdd.databinding.ActivityMainBinding
import com.sribs.bdd.utils.ModuleHelper

/**
 * @date 2021/6/23
 * @author elijah
 * @Description
 */
@Route(path= com.sribs.common.ARouterPath.MAIN_ATY)
//class MainActivity :BaseActivity(),IMainListContrast.IMainView{
class MainActivity :BaseActivity(){

    private val mBinding:ActivityMainBinding by inflate()

    private val mModuleMap by lazy {  ModuleHelper.moduleMap }

    override fun deinitView() {
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        initToolbar()

        mBinding.inhabit.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN){
                ARouter.getInstance().build(com.sribs.common.ARouterPath.DAMAGE_MAIN_ATY)
                    .withInt("x",event.x.toInt())
                    .withInt("y",event.y.toInt())
                    .withString("from", mModuleMap.get("BLD_TYPE_INHAB"))
                    .navigation()
            }

            false
        }

        mBinding.noInhabit.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN){
                ARouter.getInstance().build(com.sribs.common.ARouterPath.DAMAGE_MAIN_ATY)
                    .withInt("x",event.x.toInt())
                    .withInt("y",event.y.toInt())
                    .withString("from", mModuleMap.get("BLD_TYPE_NONINHAB"))
                    .navigation()
            }

            false
        }



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
        mBinding.toolbarTitle.text = "建筑损伤记录数字化协同平台"
        mBinding.toolbar.setNavigationIcon(R.mipmap.icon_avatar)
        mBinding.toolbar.showOverflowMenu()
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        mBinding.toolbar.setNavigationOnClickListener {

            ARouter.getInstance().build(com.sribs.common.ARouterPath.MAIN_SETTING_ATY)
                .navigation()
        }
    }

    private var mLastClick = 0L

    override fun onBackPressed() {
        var cur = System.currentTimeMillis()
        if (cur - mLastClick < 2000) {
            super.onBackPressed()
        } else {
            showToast("再次点击退出")
            mLastClick = cur
        }
    }

}