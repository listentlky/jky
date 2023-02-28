package com.sribs.bdd.utils

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.action.Dict
import com.sribs.bdd.bean.data.ProjectCreateDataBean
import com.sribs.bdd.databinding.DialogCreateProjectBinding
import com.sribs.bdd.module.project.IProjectContrast
import com.sribs.bdd.module.project.ProjectProjectPresenter
import com.sribs.bdd.ui.project.ProjectCreateActivity
import com.sribs.bdd.v3.util.LogUtils

class CreateDialog(context:Context,var mLocalProjectId:Int = -1,var onResult: (projectId: Int) -> Unit):Dialog(context), IProjectContrast.IView {


    private val mBinding:DialogCreateProjectBinding by inflate()

    val mData by lazy { ProjectCreateDataBean() }

    lateinit var mPrefs: SharedPreferences

    private val mProjectPresenter by lazy { ProjectProjectPresenter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        bindPresenter()
        setCanceledOnTouchOutside(true)
        mData.bindTagEditView(mData::name,mBinding.projectName)
            .bindTagEditView(mData::leader,mBinding.projectManager)
            .bindTagEditView(mData::inspector,mBinding.projectBuilderNumber)

        //cache current project name and building
        mPrefs = context.getSharedPreferences("createProject", Context.MODE_PRIVATE)
        mBinding.projectName.setEditText(mPrefs.getString(ModuleHelper.CUR_PRO_NAME,"")!!)
        mBinding.projectManager.setEditText(mPrefs.getString(ModuleHelper.CUR_PRO_LEADER,"")!!)
        mBinding.projectBuilderNumber.setEditText(mPrefs.getString(ModuleHelper.CUR_BLD_INS,"")!!)

        println("leon cache project name:${mData.name}")

        mBinding.projectManager.setSpinnerEntries(Dict.getLeaders())

        mBinding.confirmBtn.setOnClickListener {
            if (mData.name?.isNullOrEmpty()){
                showToast("请输入项目名称")
                return@setOnClickListener
            }
            if (mData.leader?.isNullOrEmpty()){
                showToast("请选择负责人")
                return@setOnClickListener
            }
            if (mData.inspector?.isNullOrEmpty()){
                showToast("请输入检测员,多名检测员以、分割")
                return@setOnClickListener
            }

            //cache current project name and building
            var editor = mPrefs.edit()
            editor.putString(ModuleHelper.CUR_PRO_NAME, mData.name)
            editor.putString(ModuleHelper.CUR_BLD_INS, mData.inspector)
            editor.putString(ModuleHelper.CUR_PRO_LEADER, mData.leader)
            editor.commit()
            println("leon cache project name:${mData.name}")
//        mPrefs.getString(CUR_PRO_NAME,"")

            createLocal {
                LOG.I("123","createLocal  id=$it")
                dismiss()
                onResult.invoke(mLocalProjectId)
               // (activity as ProjectCreateActivity).next(mLocalProjectId)
            }
            LOG.I("123","mData=$mData")
        }

        mBinding.cancelButton.setOnClickListener {
            dismiss()
        }

        if (mLocalProjectId>0)
            getLocalInfo(mLocalProjectId.toLong())


    }

    private fun getLocalInfo(projectId:Long){
        mProjectPresenter.getLocalProjectInfo(projectId){
            mData.name = it.name?:""
            mData.leader = it.leader?:""
            mData.inspector = it.inspector?:""
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
        if (mData.inspector.isNullOrEmpty()){
            showToast("检测员不能为空")
            return
        }

        mProjectPresenter.createProject(
            if(mLocalProjectId>-1) mLocalProjectId.toLong() else null,
            mData.name,
            mData.leader,
            mData.inspector
        ){
            if (mLocalProjectId<0) {
                mLocalProjectId = it.toInt()
            }
            LogUtils.d("创建项目返回")
            onResult(it)
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mProjectPresenter.unbindView()
    }




    private fun showToast(msg:String){
    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
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