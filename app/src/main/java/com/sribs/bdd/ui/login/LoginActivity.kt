package com.sribs.bdd.ui.login

import android.content.Intent
import android.text.InputType
import android.view.View
import android.widget.EditText
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.ARouterPath
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.Config
import com.sribs.bdd.bean.data.LoginDataBean
import com.sribs.bdd.databinding.ActivityLoginBinding
import com.sribs.bdd.module.login.ILoginContrast
import com.sribs.bdd.module.login.LoginPresenter
import com.sribs.common.ui.widget.TagEditView
import com.sribs.common.utils.SpUtil

/**
 * @date 2021/6/21
 * @author elijah
 * @Description
 */
@Route(path = ARouterPath.LOGIN_ATY)
class LoginActivity:BaseActivity(),ILoginContrast.IVew {

    private val mBinding: ActivityLoginBinding by inflate()

    private val mData by lazy { LoginDataBean() }

    private val mPresenter by lazy { LoginPresenter() }

    override fun deinitView() {
        unbindPresenter()
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
//        setStateBarColor(resources.getColor(R.color.blue_800_10))

        bindPresenter()

        mBinding.loginPwd.setCheckBoxCallback(object :TagEditView.ICheckBoxChanged{
            override fun onCheckedChange(et: EditText, isChecked: Boolean) {
                et.inputType = if(isChecked)  InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                else InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
                et.setSelection(et.text.length)
            }
        })

        mData
            .bindTagEditView(mData::account,mBinding.loginPhone)
            .bindTagEditView(mData::pwd,mBinding.loginPwd)
        mBinding.loginBtn.setOnClickListener {
            doLogin()
//            Test.doTest()
//            var t = KTest()
//            t.KTest()
        }
        if (autoLogin())return
    }

    private fun autoLogin():Boolean{
        val (account,pwd) = SpUtil.getUser(this)
        LOG.I("123","autoLogin  account=$account  pwd=$pwd")
        if (!account.isNullOrEmpty() && !pwd.isNullOrEmpty()){
            mData.account = account
            mData.pwd = pwd
            doLogin()
            return true
        }
        return false
    }


    override fun onLogin() {
        SpUtil.saveUser(this,mData.account,mData.pwd)

        /*ARouter.getInstance().build(com.sribs.common.ARouterPath.MAIN_ATY)
            .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            .navigation()*/

        ARouter.getInstance().build(com.sribs.common.ARouterPath.DAMAGE_MAIN_ATY)
            .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            .navigation()
        finish()
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

    fun doLogin(){
        LOG.I("123","mData = $mData")
        var account = mData.account
        var pwd = mData.pwd
        if(Config.BUILD_LEVEL!=2){
            if(account.isNullOrEmpty()) account = "admin"
            if (pwd.isNullOrEmpty())   pwd = "111111"

        }
        if (account.isNullOrEmpty()){
            showToast("请输入用户名")
            return
        }
        if (pwd.isNullOrEmpty()){
            showToast("请输入密码")
            return
        }
//            if (!NumberUtil.isPwd(pwd)){
//                showToast("密码不正确，请输入6-16位数子和字母组合")
//                return@setOnClickListener
//            }


        mPresenter.login(account,pwd)
     //       onLogin()
    }
}