package com.sribs.bdd.ui.project


import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
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
import com.sribs.common.ui.widget.TagEditView
import com.sribs.common.utils.FileUtil

@Route(path = com.sribs.common.ARouterPath.PRO_CREATE_ATY_TYPE)
class ProjectCreateByTypeActivity:BaseActivity(), IProjectContrast.IProjectCreateTypeView {

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID)
    var mLocalProjectId = -1L

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_BUILDING_ID)
    var mBuildingId = -1L


    private val mBinding:ActivityCreateProjectTypeBinding by inflate()

    private val projectCreateTypePresenter by lazy { ProjectCreateTypePresenter() }

    private var selected = ArrayList<String>()
    private var selectedPic = ArrayList<BuildingFloorPictureBean>()

    private val REQUEST_CODE = 12

    private val REQUEST_CODE_PIC = 13

    private val REQUEST_CODE_WHITE = 14
    private val REQUEST_CODE_BEAN_WHITE = 15

    override fun deinitView() {

    }

    override fun getView(): View = mBinding.root

    override fun initView() {
        bindPresenter()
        mBinding.toolbar.tb.setNavigationIcon(R.mipmap.icon_back)
        mBinding.toolbar.tb.setNavigationOnClickListener {
            finish()
        }
       mBinding.toolbar.tbTitle.text = "新建项目"
       mBinding.aboveNumber.setTextCallback(object :TagEditView.ITextChanged{
           override fun onTextChange(s: Editable?) {
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
            ImageSelector.builder()
                .useCamera(false) // 设置是否使用拍照
                .setSingle(false)  //设置是否单选
                .setMaxSelectCount(0) // 图片的最大选择数量，小于等于0时，不限数量。
                .setSelected(selected) // 把已选的图片传入默认选中。
                .canPreview(true) //是否可以预览图片，默认为true
                .start(this, REQUEST_CODE); // 打开相册
        }


        mBinding.chosePicList.setOnClickListener {

            if (selected.size==0){
                return@setOnClickListener
            }
            selectedPic.clear()
            selected.forEach {
                var name = FileUtil.getFileName(it)
                name = name ?: it
                selectedPic.add(BuildingFloorPictureBean(name,it))
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
                .start(this, REQUEST_CODE_PIC)
        }

        mBinding.choseWhiteList.setOnClickListener {
            ARouter.getInstance().build(com.sribs.common.ARouterPath.DRAW_WHITE)
                .navigation(this,REQUEST_CODE_WHITE)
        }

        mBinding.createComplete.setOnClickListener {//保存到数据库中Building
            if (mBinding.builderName.getEditText().text!=null){
                projectCreateTypePresenter.createLocalBuilding(mLocalProjectId.toInt(),mBuildingId,mBinding.builderName.getEditText().text.toString())
            }else{
                showToast("请输入楼名称")
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && data != null) {
            /**
             * 是否是来自于相机拍照的图片，
             * 只有本次调用相机拍出来的照片，返回时才为true。
             * 当为true时，图片返回的结果有且只有一张图片。
             */
            var  isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false)
            //获取选择器返回的数据
            var images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT)
            if (!isCameraImage){
                if (images != null) {
                    selected.clear()
                    selected.addAll(images)
                    return
                }
            }else{
                if (images!=null&&images.size>0){
                    var name = FileUtil.getFileName(images[0])
                    name = name ?: images[0]
                    currentBean?.pictureList?.add(BuildingFloorPictureBean(name!!,images[0]))
                    projectCreateTypePresenter.refeshData()
                }
                }
        }else if (requestCode == REQUEST_CODE_PIC && data != null){
            var  isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false)
            if (isCameraImage){
                var images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT)
                if (images!=null&&images.size>0){
                    var name = FileUtil.getFileName(images[0])
                    name = name ?: images[0]
                    projectCreateTypePresenter.refreshPicList(arrayListOf(BuildingFloorPictureBean(name!!,images[0])))
                }
            }
        }else if (requestCode == REQUEST_CODE_WHITE && data != null){//
                var file = data.getStringExtra("File")
            if (file != null) {
                var name = FileUtil.getFileName(file)
                name = name ?: file
                projectCreateTypePresenter.refreshPicList(arrayListOf(BuildingFloorPictureBean(name,file)))
            }
        }else if (requestCode == REQUEST_CODE_BEAN_WHITE && data != null){
            var file = data.getStringExtra("File")
            if (file!=null){
                var name = FileUtil.getFileName(file)
                name = name ?: file
                currentBean?.pictureList?.add(BuildingFloorPictureBean(name!!,file))
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
            var name = FileUtil.getFileName(it)
            name = name ?: it
            selectedPic.add(BuildingFloorPictureBean(name,it))
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
            .start(this, REQUEST_CODE)
        currentBean = bean

    }

    override fun choseWhite(bean: BuildingFloorBean) {
        ARouter.getInstance().build(com.sribs.common.ARouterPath.DRAW_WHITE)
            .navigation(this,REQUEST_CODE_BEAN_WHITE)
        currentBean = bean

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