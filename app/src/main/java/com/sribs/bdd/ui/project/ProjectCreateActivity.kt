package com.sribs.bdd.ui.project

//import com.naim.androd11supportedfilemanager.model.SupportedFile
//import com.naim.androd11supportedfilemanager.picker.FilePickerLifeCycleObserver
import android.content.Intent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.adapter.BasePagerAdapter
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.R
import com.sribs.bdd.databinding.ActivityProjectCreateBinding
import com.sribs.bdd.ui.building.BuildingStoreyFragment
import com.sribs.bdd.ui.building.IFragmentActionListener
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.common.databinding.LayoutCommonToolbarBinding
import com.sribs.common.utils.FileUtil
import java.io.File


/**
 * @date 2021/6/29
 * @author elijah
 * @Description
 */
@Route(path = com.sribs.common.ARouterPath.PRO_CREATE_ATY)
//class ProjectCreateActivity:BaseActivity(), ActivityResultRegistryOwner {
class ProjectCreateActivity:BaseActivity(), IFragmentActionListener {
    @JvmField
    @Autowired(name = "x")
    var x:Int=0

    @JvmField
    @Autowired(name = "y")
    var y:Int=0

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID)
    var mLocalProjectId = -1

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID)
    var mRemoteProjectId = ""

    @JvmField
    @Autowired(name = "from")
    var mFrom:String=""

    private val mBinding :ActivityProjectCreateBinding by inflate()

    private val mToolbarBinding : LayoutCommonToolbarBinding by lazy {
        LayoutCommonToolbarBinding.bind(mBinding.toolbar.root)
    }

    private val mFragments by lazy {
        listOf(
            ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CREATE_FGT)
                .setTag("new")
                .withInt(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID, mLocalProjectId)
                .navigation() as BaseFragment,
            ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CREATE_DETAIL)
                .setTag("detail")
                .withInt(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID, mLocalProjectId)
                .navigation() as BaseFragment,
            ARouter.getInstance().build(com.sribs.common.ARouterPath.PRO_CREATE_STOREY)
                .withInt(com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID, mLocalProjectId)
                .setTag("storey")
                .navigation() as BaseFragment
        )
    }

    private val mModuleMap by lazy {  ModuleHelper.moduleMap }

    override fun deinitView() {
    }

    override fun getView(): View = mBinding.root

    override fun initView() {

        initToolbar()
        initViewPager()
    }

    override fun dispose(): Unit? {
        mFragments.forEach {
            it.dispose()
            it.deinitView()
        }
        return super.dispose()
    }

    private fun initToolbar(){

        var path = getExternalFilesDir("") as File
        println("leon cache path=${path.absolutePath} , original cache api=${FileUtil.getCacheDir(this)}")

        println("leon ProjectCreateActivity mLocalProjectId=${mLocalProjectId}")

        mToolbarBinding.tbTitle.text = if (mLocalProjectId==-1 && mRemoteProjectId.isNullOrEmpty()) "新建项目" else "编辑项目"
        mToolbarBinding.tb.setNavigationOnClickListener {


//            DialogUtil.showMsgDialog(this,"确定要保存到本地",{
                if(mFrom.equals(mModuleMap.get("BLD_TYPE_INHAB"))){
                }else if(mFrom.equals(mModuleMap.get("BLD_TYPE_NONINHAB"))){
                    (mFragments[2] as BuildingStoreyFragment).onBackButtonClicked()
//                    finish()
                }

//            }){
//                finish()
//            }
        }
        mToolbarBinding.tb.setNavigationIcon(R.mipmap.icon_back)

        (mFragments[2] as BuildingStoreyFragment).setFragmentActionListener(this)
    }

    private fun initViewPager(){
        mBinding.projectVp.setSmooth(false)
        mBinding.projectVp.setScroll(false)
        mBinding.projectVp.adapter = BasePagerAdapter(supportFragmentManager,mFragments)
        mBinding.projectVp.offscreenPageLimit = 3
//        mBinding.projectVp.offscreenPageLimit = 2
        println("leon mLocalProjectId=${mLocalProjectId}, from=${mFrom}")
        if(mFrom.equals(mModuleMap.get("BLD_TYPE_INHAB"))) {
            if (mLocalProjectId > 0) {//编辑既有居民类损伤
                (mFragments[1] as ProjectUnitFragment).initLocalData(mLocalProjectId)
                mBinding.projectVp.currentItem = 1
            } else {//新建居民类项目
                mBinding.projectVp.currentItem = 0
            }
        }
        else
        {

            if (mLocalProjectId > 0) {//编辑既有居民类损伤
//                (mFragments[2] as BuildingStoreyFragment).initLocalData(mLocalProjectId)
                mBinding.projectVp.currentItem = 2
            } else {//新建非居民类项目
                mBinding.projectVp.currentItem = 0
            }
        }
    }

    fun next(projectId:Int){
        mLocalProjectId = projectId
        LOG.I("leon","from="+mFrom)
        if(mFrom.equals(mModuleMap.get("BLD_TYPE_INHAB"))){
            (mFragments[1] as ProjectUnitFragment).initLocalData(projectId)
            mBinding.projectVp.currentItem = 1
        }else if(mFrom.equals(mModuleMap.get("BLD_TYPE_NONINHAB"))){
            (mFragments[2] as BuildingStoreyFragment).initLocalData(projectId)
            mBinding.projectVp.currentItem = 2
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onPause() {
        super.onPause()
        LOG.I("leon", "ProjectCreateActivity onPause")
    }

    override fun onResume() {
        super.onResume()
        LOG.I("leon", "ProjectCreateActivity onResume")
    }

    override fun onStop() {
        super.onStop()
        LOG.I("leon", "ProjectCreateActivity onStop")
    }

    override fun onFragmentAction(action: String) {

    }


}