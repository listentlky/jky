package com.sribs.bdd.ui.main

import android.content.Intent
import android.net.Uri
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libnet.http.helper.HeaderInterceptor
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.pgyer.pgyersdk.PgyerSDKManager
import com.pgyer.pgyersdk.callback.CheckoutVersionCallBack
import com.pgyer.pgyersdk.model.CheckSoftModel
import com.sribs.bdd.BuildConfig
import com.sribs.bdd.Config
import com.sribs.bdd.R
import com.sribs.bdd.databinding.ActivitySettingBinding
import com.sribs.bdd.module.main.ISettingContrast
import com.sribs.bdd.module.main.SettingPresenter
import com.sribs.common.databinding.LayoutCommonToolbarBinding
import com.sribs.common.utils.DialogUtil.showMsgDialog
import com.sribs.common.utils.SpUtil

/**
 * @date 2021/8/4
 * @author elijah
 * @Description
 */
@Route(path= com.sribs.common.ARouterPath.MAIN_SETTING_ATY)
class SettingActivity:BaseActivity(),ISettingContrast.IView {

    private val mBinding:ActivitySettingBinding by inflate()

    private var mNew = ""



    private val mPresenter by lazy { SettingPresenter() }

    private val mToolbar by lazy {
        LayoutCommonToolbarBinding.bind(mBinding.tb.root)
    }

    override fun deinitView() {
        unbindPresenter()
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        bindPresenter()
//        mPresenter.getVersion()
        checkPgyVersion()
        initToolbar()
        mBinding.settingLogout.setOnClickListener {
            //TODO clear token
            HeaderInterceptor.TOKEN = null
            SpUtil.saveUser(this,"","")
            ARouter.getInstance().build(com.cbj.sdk.libui.ARouterPath.LOGIN_ATY)
                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                .navigation()
        }
        mBinding.settingUpdate.setOnClickListener {
            // 蒲公英

            if(checkVersion(Config.VERSION,mNew)){
                showMsgDialog(this,"当前版本：${Config.VERSION} \n" +
                        "最新版本：$mNew",{
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.pgy)))
                },{})
            }else{
                showMsgDialog(this,"当前版本：${Config.VERSION} \n" +
                        "最新版本：$mNew",{})
            }
        }
    }

    private fun checkPgyVersion(){
        PgyerSDKManager.checkSoftwareUpdate(this,object:CheckoutVersionCallBack{
            override fun onSuccess(p0: CheckSoftModel?) {
                mNew = p0?.buildVersion?:""
            }

            override fun onFail(p0: String?) {
                LOG.I("123","onFail $p0")
            }

        })
    }

    private fun checkVersion(cur:String,new:String):Boolean{
        var curs = cur.split(".")
        var news = new.split(".")
        if (curs.size != news.size || curs.size!=3)return false
        if ( news[0].toInt() > curs[0].toInt() ) return true
        else if (news[0].toInt() < curs[0].toInt() ) return false
        if (news[1].toInt() > curs[1].toInt()) return true
        else if(news[1].toInt() < curs[1].toInt()) return false
        if ( news[2].toInt() > curs[2].toInt()) return true
        else if(news[2].toInt() < curs[2].toInt()) return false
        return false
    }


    private fun initToolbar(){
        mToolbar.tbTitle.text = "个人中心"
        mToolbar.tb.setNavigationIcon(R.mipmap.icon_back)
        mToolbar.tb.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onVersion(v: String) {
        mNew = v
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
}