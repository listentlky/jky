package com.sribs.bdd.v3.ui.check


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cc.shinichi.library.tool.ui.ToastUtil
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.donkingliang.imageselector.utils.ImageSelector
import com.sribs.bdd.R
import com.sribs.bdd.bean.data.ModuleFloorPictureBean
import com.sribs.bdd.databinding.ActivityCreateModuleTypeBuildingBinding
import com.sribs.bdd.module.project.IProjectContrast
import com.sribs.bdd.utils.ChoseModulePicDialog
import com.sribs.bdd.utils.UUIDUtil
import com.sribs.bdd.v3.util.LogUtils

import com.sribs.common.utils.FileUtil
import java.util.*
import kotlin.collections.ArrayList

/**
 *
 */
@Route(path = com.sribs.common.ARouterPath.CHECK_MODULE_CONFIG_TYPE_BUILDING_ACTIVITY)
class ModuleCreateByTypeBuildingActivity : BaseActivity(),
    IProjectContrast.IModuleCreateTypeBuildingView {

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_PROJECT_ID)
    var mLocalProjectId = -1L

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_BUILDING_ID)
    var mBuildingId = -1L


    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID)
    var mRemoteId = ""


    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_LEADER)
    var mLeaderName = ""


    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_INSPECTOR)
    var mInspector = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_MODULE_ID)
    var mModuleId = -1L


    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_MODULE_NAME)
    var mModuleName = ""

    @JvmField
    @Autowired(name = com.sribs.common.ARouterPath.VAL_COMMON_TITLE)
    var mTitle = ""


    private val mBinding: ActivityCreateModuleTypeBuildingBinding by inflate()

    private val moduleCreateTypeBuildingPresenter by lazy { ModuleConfigCreateTypePresenter() }

    private var selected = ArrayList<String>()
    private var selectedPic = ArrayList<ModuleFloorPictureBean>()

    private val REQUEST_CODE = 12 //上传图片(pdf/img)

    private val REQUEST_CODE_PIC_BUIlDING = 15 //基于楼拍照

    private val REQUEST_CODE_WHITE_BUILDING = 16 //基于楼的白板


    override fun deinitView() {

    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        bindPresenter()
        mBinding.toolbar.tb.setNavigationIcon(R.mipmap.icon_back)
        mBinding.toolbar.tb.setNavigationOnClickListener {
            finish()
        }
        mBinding.toolbar.tbTitle.text = mTitle

        moduleCreateTypeBuildingPresenter.initLocalData(mModuleId)

        mBinding.chosePic.setOnClickListener {//选择图片
            openPdfOrImgSelector()
        }


        mBinding.chosePicList.setOnClickListener {

            if (selected.size == 0) {
                ToastUtil.getInstance()._short(getContext(), "请先加载图纸")
                return@setOnClickListener
            }
            selectedPic.clear()
            selected.forEach {
                var name = FileUtil.uriToFileName(Uri.parse(it), this)
                name = name ?: it
                selectedPic.add(ModuleFloorPictureBean(UUIDUtil.getUUID(name),name, it, null))
            }

            var dialog = ChoseModulePicDialog(this, selectedPic,selected) {
                moduleCreateTypeBuildingPresenter.refreshPicList(it)
            }
            dialog.show()
        }

        mBinding.choseList.setOnClickListener {
            if (selected.size == 0) {
                ToastUtil.getInstance()._short(getContext(), "请先加载图纸")
                return@setOnClickListener
            }

            selectedPic.clear()
            selected.forEach {
                var name = FileUtil.uriToFileName(Uri.parse(it), this)
                name = name ?: it
                selectedPic.add(ModuleFloorPictureBean(UUIDUtil.getUUID(name),name, it, null))
            }

            var dialog = ChoseModulePicDialog(this, selectedPic,selected) {

            }
            dialog.show()
        }

        mBinding.chosePhotoList.setOnClickListener {
            //仅拍照
            ImageSelector
                .builder()
                .onlyTakePhoto(true)  // 仅拍照，不打开相册
                .start(this, REQUEST_CODE_PIC_BUIlDING)
        }

        mBinding.choseWhiteList.setOnClickListener {
            ARouter.getInstance().build(com.sribs.common.ARouterPath.DRAW_WHITE)
                .navigation(this, REQUEST_CODE_WHITE_BUILDING)
        }

        mBinding.createComplete.setOnClickListener {//保存到数据库中Building
            moduleCreateTypeBuildingPresenter.createLocalBuildingsInTheModule(
                this,
                mLocalProjectId.toInt(),
                mModuleName,
                mBuildingId,
                mModuleId,
                mRemoteId
            )

        }
    }

    fun openPdfOrImgSelector() {
        val supportedMimeTypes = arrayOf("application/pdf", "image/*")
        var intent: Intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.type = if (supportedMimeTypes.size === 1) supportedMimeTypes[0] else "*/*"
            if (supportedMimeTypes.size > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, supportedMimeTypes)
            }
        } else {
            var mimeTypes = ""
            for (mimeType in supportedMimeTypes) {
                mimeTypes += "$mimeType|"
            }
            intent.type = mimeTypes.substring(0, mimeTypes.length - 1)
        }
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtils.d("onActivityResult：requestCode=${requestCode}  data=${data}")
        if (requestCode == REQUEST_CODE && data != null) { //上传图片
            var uri = data.data
            selected.add(uri.toString())
        } else if (requestCode == REQUEST_CODE_PIC_BUIlDING && data != null) {
            var isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false)
            if (isCameraImage) {
                var images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT)
                if (images != null && images.size > 0) {
                    var name = FileUtil.getFileName(images[0])
                    name = name ?: images[0]
                    LogUtils.d("基于楼拍照返回: " + images[0])
                    moduleCreateTypeBuildingPresenter.refreshPicList(
                        arrayListOf(
                            ModuleFloorPictureBean(UUIDUtil.getUUID(name!!),name!!, null, images[0])
                        )
                    )
                }
            }
        } else if (requestCode == REQUEST_CODE_WHITE_BUILDING && data != null) {//
            var file = data.getStringExtra("File")
            LogUtils.d("基于楼白板：" + file)
            if (file != null) {
                var name = FileUtil.getFileName(file)
                name = name ?: file
                moduleCreateTypeBuildingPresenter.refreshPicList(
                    arrayListOf(
                        ModuleFloorPictureBean(
                            UUIDUtil.getUUID(name),
                            name,
                            null,
                            file
                        )
                    )
                )
            }
        }

    }

    override fun initLocalData(any: Any) {

    }


    override fun getPicRecycleView(): RecyclerView = mBinding.picRecycleview


    override fun createModuleConfigSuccess() {
        finish()
    }


    override fun getContext(): Context? = this

    override fun bindPresenter() {
        moduleCreateTypeBuildingPresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg)
    }

    override fun unbindPresenter() {
        moduleCreateTypeBuildingPresenter.unbindView()
    }
}