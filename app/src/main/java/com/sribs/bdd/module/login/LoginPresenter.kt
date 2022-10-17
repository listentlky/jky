package com.sribs.bdd.module.login

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.exception.MsgThrowable
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libnet.http.HttpManager
import com.cbj.sdk.libnet.http.helper.HeaderInterceptor
import com.sribs.common.module.BasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.sribs.bdd.Config
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.db.InspectorBean
import com.sribs.common.bean.db.LeaderBean
import com.sribs.common.bean.db.UserBean
import com.sribs.common.bean.net.LoginReq
import com.sribs.common.bean.net.RoleUserListReq
import com.sribs.common.net.HttpApi
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.Util


/**
 * @date 2021/8/4
 * @author elijah
 * @Description
 */
class LoginPresenter:BasePresenter(),ILoginContrast.IPresenter {

    private var mView:ILoginContrast.IVew?=null

//    @SingleClick
    override fun login(account: String, pwd: String) {
        if (Config.isNetAvailable){
            println("leon LoginPresenter loginRemote")
            loginRemote(account, pwd)
        }else{
            println("leon LoginPresenter loginLocal")
            loginLocal(account, pwd)
        }
    }

    private fun loginRemote(account: String, pwd: String){
        LOG.I("123","loginRemote")
        var instance = HttpManager.instance
        instance.mHost = "http://106.15.205.38:80"
        LogUtils.d("host=${instance.mHost}")
        addDisposable(instance.getHttpService<HttpApi>()
            .login(LoginReq(account,pwd))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                checkResult(it)
                HeaderInterceptor.TOKEN = it.data?.token
                getUserInfoRemote()
            },{
                mView?.onMsg(checkError(it))
            }))
    }

    private fun loginLocal(account: String, pwd: String){
        LOG.I("123","loginLocal")
        val srv = ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService
       addDisposable( srv.getUserBean(account).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isEmpty())throw MsgThrowable("用户名或密码错误")
                var find = false
                it.forEach { b->
                    var salt = b.salt
                    var md5Pwd = Util.md5("$salt@bdd@$pwd")
                    if (md5Pwd == b.password){
                        mView?.onLogin()
                        find = true
                        return@forEach
                    }
                }
                if (!find) mView?.onMsg("用户名或密码错误")
            },{
                mView?.onMsg(checkError(it))
            }))
    }

    private fun getUserInfoRemote(){
        val srv = ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB).navigation() as IDatabaseService
        var instance = HttpManager.instance
        instance.mHost = "http://106.15.205.38:80"
        var ob1 = instance.getHttpService<HttpApi>()
            .userInfos()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .flatMap {
                checkResult(it)
                srv.updateUserBean(it.data!!.records.map { b-> UserBean(
                    b.id,
                    b.name,
                    b.account,
                    b.password,
                    b.mobile,
                    b.salt
                ) })
            }

        var ob2 = instance.getHttpService<HttpApi>()
            .getRoleUserList(RoleUserListReq("admin"))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .flatMap {
                checkResult(it)
                srv.updateLeader(it.data!!.records.map { b-> LeaderBean(
                    b.id,
                    b.name
                ) })
            }
        var ob3 = instance.getHttpService<HttpApi>()
            .getRoleUserList(RoleUserListReq("inspector"))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .flatMap {
                checkResult(it)
                srv.updateInspector(it.data!!.records.map { b-> InspectorBean(
                    b.id,
                    b.name
                ) })
            }
        var ob4 = instance.getHttpService<HttpApi>()
            .userInfo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                checkResult(it)
                com.sribs.bdd.action.Config.sUserName = it.data!!.name!!
                com.sribs.bdd.action.Config.sUserId = it.data!!.id
                Observable.create<Long> { o4-> o4.onNext(0) }
            }

        var obList = ArrayList<Observable<Long>>()
        obList.add(ob1)
        obList.add(ob2)
        obList.add(ob3)
        obList.add(ob4)
        addDisposable(Observable.zip(obList){ arrs->
            LOG.I("123","update userSize=$arrs ")
            true
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","insert db size=$it")
                mView?.onLogin()
            },{
                it.printStackTrace()
            }))

    }


    override fun bindView(v: IBaseView) {
        mView = v as ILoginContrast.IVew
    }

    override fun unbindView() {
        dispose()
        mView
    }
}