package com.sribs.bdd.v3.ui.check

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
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
import com.sribs.bdd.bean.data.ModuleFloorBean
import com.sribs.bdd.bean.data.ModuleFloorPictureBean
import com.sribs.bdd.databinding.ActivityCreateModuleTypeFloorBinding
import com.sribs.bdd.module.project.IProjectContrast
import com.sribs.bdd.utils.ChoseModulePicDialog
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.bean.v3.v3ModuleFloorDbBean
import com.sribs.common.ui.widget.TagEditView
import com.sribs.common.utils.FileUtil

/**
 *    author :
 *    e-mail :
 *    date   : 2022/9/20 161:46
 *    desc   : 模块配置 楼层页面
 *    version: 1.0
 */

@Route(path = com.sribs.common.ARouterPath.CHECK_MODULE_CONFIG_TYPE_FLOOR_ACTIVITY)
class ModuleCreateByTypeFloorActivity : BaseActivity(), IProjectContrast.IModuleCreateTypeView {

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

    private val mBinding: ActivityCreateModuleTypeFloorBinding by inflate()

    private val moduleCreateTypePresenter by lazy { ModuleCreateTypePresenter() }

    private var selected = ArrayList<String>()

    private var selectedPic = ArrayList<ModuleFloorPictureBean>()

    private val REQUEST_CODE = 12 //上传图片(pdf/img)

    private val REQUEST_CODE_PIC_FLOOR = 13 //基于楼层拍照

    private val REQUEST_CODE_BEAN_WHITE_FLLOR = 14 //基于楼层的白板

    private var aboveNumber = 0

    private var afterNumber = 0

    private var currentBean: ModuleFloorBean? = null

    private var isDeleteModuleFloor = false

    override fun deinitView() {
        unbindPresenter()
    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        bindPresenter()
        initToolbar()
        if (mLocalProjectId == -1L) {
            ToastUtil.getInstance()._short(this, "网络请求module图纸")
        } else {
            moduleCreateTypePresenter.initLocalData(mLocalProjectId, mBuildingId, mModuleId)
        }
        mBinding.aboveNumber.setTextCallback(object : TagEditView.ITextChanged {
            override fun onTextChange(s: Editable?) {

                if (isDeleteModuleFloor){
                    isDeleteModuleFloor = false
                    if ((s == null) || (s.toString() == "")){
                        moduleCreateTypePresenter.addAboveFlourList(0)
                    }else{
                        moduleCreateTypePresenter.mAboveOldIndex = s.toString().toInt()

                    }
                    return
                }

                if ((s == null) || (s.toString() == "")) {
                    moduleCreateTypePresenter.addAboveFlourList(0)
                    return
                }
                val num: Int = s.toString().toInt()
                if (num >= 0) {//执行
                    moduleCreateTypePresenter.addAboveFlourList(num)
                }
                aboveNumber = num
            }
        })

        mBinding.afterNumber.setTextCallback(object : TagEditView.ITextChanged {
            override fun onTextChange(s: Editable?) {


                if (isDeleteModuleFloor){
                    isDeleteModuleFloor = false
                    if ((s == null) || (s.toString() == "")){
                        moduleCreateTypePresenter.addAfterFlourList(0)
                    }else{
                        moduleCreateTypePresenter.mBeforeOldIndex = s.toString().toInt()

                    }
                    return
                }


                if ((s == null) || (s.toString() == "")) {
                    moduleCreateTypePresenter.addAfterFlourList(0)
                    return
                }
                val num: Int = s.toString().toInt()
                if (num >= 0) {//执行
                    moduleCreateTypePresenter.addAfterFlourList(num)
                }
                afterNumber = num
            }

        })


        mBinding.chosePic.setOnClickListener {//选择图片
            openPdfOrImgSelector()
        }

        mBinding.createComplete.setOnClickListener {

            moduleCreateTypePresenter.createLocalModule(
                this,
                mLocalProjectId.toInt(),
                mBuildingId,
                mModuleId,
                mModuleName,
            )
        }

    }


    /**
     * @Description 初始化toolbar
     */
    private fun initToolbar() {
        mBinding.toolbar.tb.setNavigationIcon(R.mipmap.icon_back)
        mBinding.toolbar.tb.setNavigationOnClickListener {
            finish()
        }
        mBinding.toolbar.tbTitle.text = mTitle
    }


    /**
     * Dispatch incoming result to the correct fragment.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtils.d("onActivityResult：requestCode=${requestCode}  data=${data}")
        if (requestCode == REQUEST_CODE && data != null) { //上传图片
            var uri = data.data
            selected.add(uri.toString())
        } else if (requestCode == REQUEST_CODE_PIC_FLOOR && data != null) {
            var isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false)
            if (isCameraImage) {
                var images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT)
                if (images != null && images.size > 0) {
                    var name = FileUtil.getFileName(images[0])
                    name = name ?: images[0]
                    LogUtils.d("基于楼层选择图片返回: " + images[0])
                    currentBean?.pictureList?.add(
                        ModuleFloorPictureBean(
                            name!!,
                            null,
                            images[0]
                        ).also {
                        })
                    moduleCreateTypePresenter.refeshData()
                }
            }
        } else if (requestCode == REQUEST_CODE_BEAN_WHITE_FLLOR && data != null) {
            var file = data.getStringExtra("File")
            LogUtils.d("基于楼层白板：" + file)
            if (file != null) {
                var name = FileUtil.getFileName(file)
                name = name ?: file
                currentBean?.pictureList?.add(ModuleFloorPictureBean(name!!, null, file))
                moduleCreateTypePresenter.refeshData()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_check, menu)
        return true
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


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        var item = menu?.findItem(R.id.menu_check_pop)

        item?.icon?.setBounds(20, 50, 0, 0)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item?.itemId) {
            R.id.menu_check_pop -> { // 菜单111
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun initLocalData(beanList: List<v3ModuleFloorDbBean>) {
        mBinding.aboveNumber.setEditText(""+beanList.get(0).aboveNumber)
        mBinding.afterNumber.setEditText(""+beanList.get(0).afterNumber)
        moduleCreateTypePresenter.setData(beanList)

    }


    override fun getFloorRecycleView(): RecyclerView = mBinding.flourRecycleview


    override fun chosePic(bean: ModuleFloorBean) {
        if (selected.size == 0) {
            ToastUtil.getInstance()._short(getContext(), "请先上传图片")
            return
        }
        selectedPic.clear()
        selected.forEach {
            var name = FileUtil.uriToFileName(Uri.parse(it), this)
            name = name ?: it
            selectedPic.add(ModuleFloorPictureBean(name, it, null))
        }

        var dialog = ChoseModulePicDialog(this, selectedPic) {
            bean.pictureList?.addAll(it)
        }
        dialog.show()

    }

    override fun takePhoto(bean: ModuleFloorBean) {
        currentBean = bean
        ImageSelector
            .builder()
            .onlyTakePhoto(true)  // 仅拍照，不打开相册
            .start(this, REQUEST_CODE_PIC_FLOOR)
    }

    override fun choseWhite(bean: ModuleFloorBean) {
        currentBean = bean
        ARouter.getInstance().build(com.sribs.common.ARouterPath.DRAW_WHITE)
            .navigation(this, REQUEST_CODE_BEAN_WHITE_FLLOR)

    }

    override fun deleteModuleFloor(floorType: String, aboveSize: Int, beforeSize: Int) {
        currentFocus?.clearFocus()
        isDeleteModuleFloor = true
        when(floorType){
            "地上"->{

                mBinding.aboveNumber.setEditText(aboveSize.toString())

            }
            "地下"->{
                mBinding.afterNumber.setEditText(beforeSize.toString())
            }
        }
    }

    override fun createModuleConfigSuccess() {
        showToast("配置创建成功")
        finish()
    }

    override fun getContext(): Context = this

    override fun bindPresenter() {
        moduleCreateTypePresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg)
    }

    override fun unbindPresenter() {
        moduleCreateTypePresenter.bindView(this)
    }


}