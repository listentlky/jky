package com.sribs.bdd.ui.project


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.BaseActivity
import com.cbj.sdk.libui.mvp.inflate
import com.donkingliang.imageselector.utils.ImageSelector
import com.sribs.bdd.R
import com.sribs.bdd.bean.BuildingFloorBean
import com.sribs.bdd.bean.BuildingFloorPictureBean
import com.sribs.bdd.databinding.ActivityCreateProjectTypeBinding
import com.sribs.bdd.module.project.IProjectContrast
import com.sribs.bdd.module.project.ProjectCreateTypePresenter
import com.sribs.bdd.utils.ChosePicDialog
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.ui.widget.TagEditView
import com.sribs.common.utils.FileUtil

/**
 * 创建楼栋
 */
@Route(path = com.sribs.common.ARouterPath.PRO_CREATE_ATY_TYPE)
class ProjectCreateByTypeActivity:BaseActivity(), IProjectContrast.IProjectCreateTypeView {

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID)
    var mLocalProjectId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_PROJECT_UUID)
    var mLocalProjectUUID = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_REMOTE_ID)
    var mRemoteId = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_BUILDING_ID)
    var mBuildingId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_LEADER)
    var mLeader = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_INSPECTOR)
    var mInspector = ""

    private val mBinding:ActivityCreateProjectTypeBinding by inflate()

    private val projectCreateTypePresenter by lazy { ProjectCreateTypePresenter() }

    private var selected = ArrayList<String>()
    private var selectedPic = ArrayList<BuildingFloorPictureBean>()

    private val REQUEST_CODE = 12 //上传图片(pdf/img)

    private val REQUEST_CODE_PIC_FLOOR = 13 //基于楼层拍照

    private val REQUEST_CODE_BEAN_WHITE_FLLOR = 14 //基于楼层的白板

    private val REQUEST_CODE_PIC_BUIlDING = 15 //基于楼拍照

    private val REQUEST_CODE_WHITE_BUILDING = 16 //基于楼的白板

    private  var isDeleteBuildingFloor = false
    override fun deinitView() {

    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        bindPresenter()
        mBinding.toolbar.tb.setNavigationIcon(R.mipmap.icon_back)
        mBinding.toolbar.tb.setNavigationOnClickListener {
            projectCreateTypePresenter.mAboveOldIndex = 0
            projectCreateTypePresenter.mBeforeOldIndex = 0
            finish()
        }
       mBinding.toolbar.tbTitle.text = "新建项目"
       mBinding.aboveNumber.setTextCallback(object :TagEditView.ITextChanged{
           override fun onTextChange(s: Editable?) {

               if (isDeleteBuildingFloor){
                   isDeleteBuildingFloor = false
                   if ((s == null) || (s.toString() == "")){
                       projectCreateTypePresenter.addAboveFlourList(0)
                   }else{
                       projectCreateTypePresenter.mAboveOldIndex = s.toString().toInt()

                   }
                   return
               }

               if ((s == null) || (s.toString() == "")){
                   projectCreateTypePresenter.addAboveFlourList(0)
                   return
               }
               val num: Int = s.toString().toInt()
               if (num>=0){//执行
                   projectCreateTypePresenter.addAboveFlourList(num)
               }
           }
       })

        mBinding.afterNumber.setTextCallback(object :TagEditView.ITextChanged{
            override fun onTextChange(s: Editable?) {
                if (isDeleteBuildingFloor){
                    isDeleteBuildingFloor = false
                    if ((s == null) || (s.toString() == "")){
                        projectCreateTypePresenter.addAfterFlourList(0)
                    }else{
                        projectCreateTypePresenter.mBeforeOldIndex = s.toString().toInt()

                    }
                    return
                }
                if ((s == null) || (s.toString() == "")){
                    projectCreateTypePresenter.addAfterFlourList(0)
                    return
                }
                val num: Int = s.toString().toInt()
                if (num>=0){//执行
                    projectCreateTypePresenter.addAfterFlourList(num)
                }
            }

        })

        mBinding.chosePic.setOnClickListener {//选择图片
            //不限数量的多选
         /*   ImageSelector.builder()
                .useCamera(false) // 设置是否使用拍照
                .setSingle(false)  //设置是否单选
                .setMaxSelectCount(0) // 图片的最大选择数量，小于等于0时，不限数量。
                .setSelected(selected) // 把已选的图片传入默认选中。
                .canPreview(true) //是否可以预览图片，默认为true
                .start(this, REQUEST_CODE) // 打开相册*/
            openPdfOrImgSelector()
        }


        mBinding.chosePicList.setOnClickListener {

            if (selected.size==0){
                return@setOnClickListener
            }
            selectedPic.clear()
            selected.forEach {
                var name = FileUtil.uriToFileName(Uri.parse(it),this)
                name = name ?: it
                selectedPic.add(BuildingFloorPictureBean(name,it,null))
            }

            var dialog = ChosePicDialog(this,selectedPic){
                projectCreateTypePresenter.refreshPicList(it)
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
                .navigation(this,REQUEST_CODE_WHITE_BUILDING)
        }

        mBinding.createComplete.setOnClickListener {//保存到数据库中Building
            if (mBinding.builderName.getEditText().text!=null){
                projectCreateTypePresenter.createLocalBuilding(this,
                    mLocalProjectId.toInt(),
                    mLocalProjectUUID,
                    mRemoteId,
                    mBuildingId,
                    mBinding.builderName.getEditText().text.toString()
                    ,mLeader!!
                    ,mInspector!!)
            }else{
                showToast("请输入楼名称")
            }

        }
    }

    fun openPdfOrImgSelector(){
        val supportedMimeTypes = arrayOf("application/pdf", "image/*")
        var intent:Intent = Intent()
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
        }else if(requestCode == REQUEST_CODE_PIC_FLOOR && data != null){
            var  isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false)
            if (isCameraImage){
                var images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT)
                if (images!=null&&images.size>0){
                    var name = FileUtil.getFileName(images[0])
                    name = name ?: images[0]
                    LogUtils.d("基于楼层选择图片返回: "+images[0])
                    currentBean?.pictureList?.add(BuildingFloorPictureBean(name!!,null,images[0]).also {
                    })
                    projectCreateTypePresenter.refeshData()
                }
            }
        }
        else if (requestCode == REQUEST_CODE_PIC_BUIlDING && data != null){
            var  isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false)
            if (isCameraImage){
                var images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT)
                if (images!=null&&images.size>0){
                    var name = FileUtil.getFileName(images[0])
                    name = name ?: images[0]
                    LogUtils.d("基于楼拍照返回: "+images[0])
                    projectCreateTypePresenter.refreshPicList(arrayListOf(BuildingFloorPictureBean(name!!,null,images[0])))
                }
            }
        }else if (requestCode == REQUEST_CODE_WHITE_BUILDING && data != null){//
                var file = data.getStringExtra("File")
            LogUtils.d("基于楼白板："+file)
            if (file != null) {
                var name = FileUtil.getFileName(file)
                name = name ?: file
                projectCreateTypePresenter.refreshPicList(arrayListOf(BuildingFloorPictureBean(name,null,file)))
            }
        }else if (requestCode == REQUEST_CODE_BEAN_WHITE_FLLOR && data != null){
            var file = data.getStringExtra("File")
            LogUtils.d("基于楼层白板："+file)
            if (file!=null){
                var name = FileUtil.getFileName(file)
                name = name ?: file
                currentBean?.pictureList?.add(BuildingFloorPictureBean(name!!,null,file))
                projectCreateTypePresenter.refeshData()
            }
        }

    }

    override fun getFlourRecycleView(): RecyclerView = mBinding.flourRecycleview

    override fun getPicRecycleView(): RecyclerView = mBinding.picRecycleview
    override fun chosePic(bean: BuildingFloorBean) {
        if (selected.size==0){
            return
        }
        selectedPic.clear()
        selected.forEach {
            var name = FileUtil.uriToFileName(Uri.parse(it),this)
            name = name ?: it
            selectedPic.add(BuildingFloorPictureBean(name,it,null))
        }

        var dialog = ChosePicDialog(this,selectedPic){
            bean.pictureList?.addAll(it)
        }
        dialog.show()

    }
    var currentBean: BuildingFloorBean? = null
    override fun takePhone(bean: BuildingFloorBean) {
        //仅拍照
        ImageSelector
            .builder()
            .onlyTakePhoto(true)  // 仅拍照，不打开相册
            .start(this, REQUEST_CODE_PIC_FLOOR)
        currentBean = bean

    }

    override fun choseWhite(bean: BuildingFloorBean) {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.DRAW_WHITE)
            .navigation(this,REQUEST_CODE_BEAN_WHITE_FLLOR)
        currentBean = bean

    }

    override fun deleteBuildingFloor(floorType:String,adboveSize: Int,beforeSize:Int) {
        currentFocus?.clearFocus()
        isDeleteBuildingFloor = true
            when(floorType){
                "地上"->{

                    mBinding.aboveNumber.setEditText(adboveSize.toString())

                }
                "地下"->{
                    mBinding.afterNumber.setEditText(beforeSize.toString())
                }
            }
    }

    override fun createBuildingSuccess() {
        showToast("新建楼成功")
        finish()
    }

    override fun getContext(): Context? = this

    override fun bindPresenter() {
       projectCreateTypePresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
      showToast(msg)
    }

    override fun unbindPresenter() {
       projectCreateTypePresenter.unbindView()
    }
}