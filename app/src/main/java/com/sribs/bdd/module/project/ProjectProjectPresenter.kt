package com.sribs.bdd.module.project

import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libnet.http.HttpManager
import com.sribs.common.module.BasePresenter
import com.cbj.sdk.libui.mvp.moudles.IBaseView
import com.sribs.bdd.Config
import com.sribs.bdd.action.Dict
import com.sribs.bdd.utils.UUIDUtil
import com.sribs.bdd.v3.util.LogUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.math.NumberUtils
import com.sribs.common.bean.db.ProjectBean
import com.sribs.common.bean.net.ProjectCreateReq
import com.sribs.common.net.HttpApi
import com.sribs.common.server.IDatabaseService
import com.sribs.common.utils.TimeUtil
import java.sql.Date
import java.util.*

/**
 * @date 2021/7/13
 * @author elijah
 * @Description 项目相关 数据操作
 */
class ProjectProjectPresenter : BasePresenter(), IProjectContrast.IProjectPresenter {
    private var mView: IProjectContrast.IView? = null

    private var mBean = ProjectBean()

    private val mDb by lazy {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
    }

    override fun getLocalProjectInfo(projectId: Long, res: (ProjectBean) -> Unit) {
        addDisposable(
            mDb.getProject(projectId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.isNotEmpty()) {
                        mBean = it[0]
                        res(it[0])
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    override fun createLocalProject(
        projectId: Long?,
        name: String,
        leader: String,
        buildNo: String,
        onResult: (Long) -> Unit
    ) {
        if (mBean.isSame(name, leader, buildNo) && mBean.id != null) {
            onResult(mBean.id!!)
            return
        }
        var myBuildNo = if (NumberUtils.isNumber(buildNo)) "${buildNo}号楼" else buildNo

        mBean.also {
            it.id = projectId
            it.name = name
            it.leader = leader
            it.buildNo = myBuildNo
            it.updateTime = TimeUtil.stampToDate(""+System.currentTimeMillis())
        }
        addDisposable(mDb.getProjectOnce(name, myBuildNo).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                LOG.I("123", "getProjectOnce  name=$name  myBuildNo=$myBuildNo")
                Observable.create<ProjectBean> { o ->
                    if (it.isEmpty()) {
                    } else {
                        mBean.id = it[0].id
                        mBean.createTime = it[0].createTime
                        mView?.onMsg("已存在项目")
                    }
                    o.onNext(mBean)
                }
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                mDb.updateProject(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                onResult(it)
            }, {
                it.printStackTrace()
            })
        )
    }


    // 3.0 新增无楼号模式
    fun createNewLocalProject(
        projectId: Long?,
        name: String,
        leader: String,
        inspector: String,
        projectIDUUID:String,
        onResult: (Long) -> Unit
    ) {
        mBean.also {
            it.id = projectId
            it.uuid = projectIDUUID
            it.name = name
            it.leader = leader
            it.inspector = inspector
            it.updateTime = TimeUtil.stampToDate(""+System.currentTimeMillis())
            it.isChanged = 1
        }
        mDb.getProjectOnce(name).toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .flatMap {
                Observable.create<ProjectBean> { o ->
                    if (it.isEmpty()) {
                    } else {
                        mBean.id = it[0].id
                        mBean.uuid = it[0].uuid
                        mBean.createTime = it[0].createTime
                        mView?.onMsg("已存在项目")
                    }
                    o.onNext(mBean)
                }
            }
            .observeOn(Schedulers.computation())
            .flatMap {
                LogUtils.d("本地创建项目")
                mDb.updateProject(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LogUtils.d("本地创建项目成功")
                onResult(it)
            }, {
                it.printStackTrace()
            })
    }

    /**
     * 3.0新建项目
     */
    fun createProject(
        projectId: Long?,
        name: String,
        leader: String,
        inspector: String,
        onResult: (Long) -> Unit
    ) {
        var projectIDUUID = UUIDUtil.getUUID(name)

  //      if (!Config.isNetAvailable) {
            createNewLocalProject(projectId, name, leader, inspector,projectIDUUID, onResult)
   /*     } else {
            LogUtils.d("网络创建项目")
            LogUtils.d("输入的： " + inspector)

            var inspectorList: List<String> =
                if (inspector.contains("、")) inspector.split("、") else Arrays.asList(inspector)

            LogUtils.d("转换的： " + inspectorList.toString())

            addDisposable(HttpManager.instance.getHttpService<HttpApi>()
                .createOrUpdateProject(ProjectCreateReq().also {
                    it.inspectors = inspectorList
                    it.leaderId = Dict.getLeaderId(leader)!!
                    it.leaderName = leader
                    it.projectName = name
                    it.projectId = projectIDUUID
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    LogUtils.d("网络创建项目成功: " + it.toString())
                    LogUtils.d("网络创建项目成功  本地同步创建")
                    createNewLocalProject(projectId, name, leader, inspector,projectIDUUID, onResult)
                }, {
                    LogUtils.d("网络创建项目失败 单本地创建" + it.toString())
                    mView?.onMsg(checkError(it))
                    createNewLocalProject(projectId, name, leader, inspector,projectIDUUID, onResult)
                }
                )
            )
        }*/
    }

    override fun delLocalProject(projectId: Long) {

        addDisposable(
            mDb.deleteProject(ProjectBean(id = projectId))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                }, {
                    it.printStackTrace()
                })
        )
        addDisposable(
            mDb.deleteUnit(projectId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                }, {
                    it.printStackTrace()
                })
        )
        addDisposable(
            mDb.deleteConfig(projectId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                }, {
                    it.printStackTrace()
                })
        )
    }

    override fun bindView(v: IBaseView) {
        mView = v as IProjectContrast.IView
    }

    override fun unbindView() {
        dispose()
        mView = null
    }
}