package com.sribs.bdd.ui.project

import android.content.Context
import android.content.SharedPreferences
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.action.Dict
import com.sribs.bdd.bean.data.ProjectCreateDataBean
import com.sribs.bdd.databinding.FragmentProjectCreateBinding
import com.sribs.bdd.module.project.IProjectContrast
import com.sribs.bdd.module.project.ProjectProjectPresenter
import com.sribs.bdd.utils.ModuleHelper.CUR_BLD_NO
import com.sribs.bdd.utils.ModuleHelper.CUR_PRO_LEADER
import com.sribs.bdd.utils.ModuleHelper.CUR_PRO_NAME

/**
 * @date 2021/6/30
 * @author elijah
 * @Description 新建项目页面
 */
@Route(path= com.sribs.common.ARouterPath.PRO_CREATE_FGT)
class ProjectCreateFragment:BaseFragment(R.layout.fragment_project_create),IProjectContrast.IView {

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID)
    var mLocalProjectId = -1

    private val mBinding:FragmentProjectCreateBinding by bindView()

    val mData by lazy { ProjectCreateDataBean() }

    private val mProjectPresenter by lazy { ProjectProjectPresenter() }

    override fun deinitView() {
        unbindPresenter()
    }

    lateinit var mPrefs: SharedPreferences

    override fun initView() {

        bindPresenter()
        mData.bindTagEditView(mData::name,mBinding.projectName)
            .bindTagEditView(mData::leader,mBinding.projectManager)
            .bindTagEditView(mData::number,mBinding.projectBuilderNumber)

        mBinding.projectManager.setSpinnerEntries(Dict.getLeaders())

        mBinding.projectCreateBtn.setOnClickListener {
            if (mData.name?.isNullOrEmpty()){
                showToast("请输入项目名称+弄")
                return@setOnClickListener
            }
            if (mData.leader?.isNullOrEmpty()){
                showToast("请选择负责人")
                return@setOnClickListener
            }
            if (mData.number?.isNullOrEmpty()){
                showToast("请输入楼号")
                return@setOnClickListener
            }

            //cache current project name and building
            mPrefs = requireActivity().getSharedPreferences("createProject", Context.MODE_PRIVATE)
            var editor = mPrefs.edit()
            editor.putString(CUR_PRO_NAME, mData.name)
            editor.putString(CUR_BLD_NO, mData.number)
            editor.putString(CUR_PRO_LEADER, mData.leader)
            editor.commit()
            println("leon cache project name:${mData.name}")
//        mPrefs.getString(CUR_PRO_NAME,"")

            createLocal {
                LOG.I("123","createLocal  id=$it")
                (activity as ProjectCreateActivity).next(mLocalProjectId)
            }
            LOG.I("123","mData=$mData")
        }
//        testData()
        if (mLocalProjectId>0)
            getLocalInfo(mLocalProjectId.toLong())
    }

    fun getData():ProjectCreateDataBean = mData

    fun testData(){
        mData.name = "国定东路200号"
        mData.leader = "aaa"
        mData.number = "1"
    }

    private fun getLocalInfo(projectId:Long){
        mProjectPresenter.getLocalProjectInfo(projectId){
            mData.name = it.name?:""
            mData.leader = it.leader?:""
            mData.number = it.buildNo?:""
        }
    }

    fun createLocal(onResult:(projectId:Long)->Unit){
        if (mData.name.isNullOrEmpty()){
            showToast("项目名称不能为空")
            return
        }
        if (mData.leader.isNullOrEmpty()){
            showToast("项目负责人不能为空")
            return
        }
        if (mData.number.isNullOrEmpty()){
            showToast("楼号不能为空")
            return
        }

        mProjectPresenter.createLocalProject(
            if(mLocalProjectId>-1) mLocalProjectId.toLong() else null,
            mData.name,
            mData.leader,
            mData.number
        ){
            if (mLocalProjectId<0) {
                mLocalProjectId = it.toInt()
            }
            onResult(it)
        }
    }


    override fun bindPresenter() {
       mProjectPresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
       showToast(msg)
    }

    override fun unbindPresenter() {
        mProjectPresenter.unbindView()
    }

}