package com.sribs.bdd.ui.house

import android.annotation.SuppressLint
import android.net.Uri
import android.view.View
import cc.shinichi.library.ImagePreview
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.cbj.sdk.libbase.rxbus.RxBus
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.sribs.bdd.R
import com.sribs.bdd.bean.data.DamageEditDataBean
import com.sribs.bdd.databinding.FragmentHouseDamageEditBinding
import com.sribs.bdd.module.house.IHouseContrast
import com.sribs.bdd.module.house.RoomDetailPresenter
import com.sribs.bdd.utils.DescriptionPicHelper
import com.sribs.bdd.utils.DescriptionPositionHelper
import com.sribs.common.bean.db.RoomDetailBean
import com.sribs.common.utils.TakePhotoUtil
import java.io.File

/**
 * @date 2021/7/19
 * @author elijah
 * @Description
 */
@Route(path= com.sribs.common.ARouterPath.HOUSE_DAMAGE_EDIT_FGT)
class DamageEditFragment:BaseFragment(R.layout.fragment_house_damage_edit),IHouseContrast.IRoomDetailView {

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_DAMAGE_TYPE)
    var mType = 1  //1 普通  2裂缝   3接缝


    private val mPresenter by lazy { RoomDetailPresenter() }

    private var mPos:ArrayList<String>?=null
    private var mPic:ArrayList<String>?=null

    private val mBinding : FragmentHouseDamageEditBinding by bindView()

    private var mData:DamageEditDataBean?=null

    private var mResultList:ArrayList<String>?=null

    override fun deinitView() {
        unbindPresenter()
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun initView() {
        LOG.I("123","edit fgt")
        bindPresenter()
        var arr = requireContext().resources.getStringArray(R.array.house_damage_split)
        var parent =    activity as HouseDamageDescriptionActivity
        mBinding.view2.damageEditSplit.setSpinnerEntries(arr)

        mBinding.damageEditDel.setOnClickListener {
            mPresenter.deleteRoomDetail{
                parent.doCleanSel()
                parent.doEditFinish()
            }
            mData?.clean()

            mResultList = null
        }
        mBinding.damageEditCancel.setOnClickListener {
            parent.doEditFinish()
            mData?.clean()
            mResultList = null
        }
        mBinding.damageEditConfirm.setOnClickListener {
            parent.updateInspector()
            createReport{
                parent.doEditFinish()
            }

            mData?.clean()
            mResultList = null
        }

        mBinding.view1.also {
            it.damagePicAddBtn.setOnClickListener {
                if (mData?.damagePic.isNullOrEmpty()) {
                    TakePhotoUtil.takePhoto(activity!!, 2, com.sribs.common.RxConstants.DAMAGE_PHOTO)
                }else{
                    showBigImage()
                }
            }
            it.damagePhotoDelBtn.setOnClickListener { v->
                v.visibility = View.GONE
                it.damagePhoto.setImageDrawable(null)
                mData?.damagePic = ""
            }

        }
        mBinding.view2.also {
            it.damagePicAddBtn.setOnClickListener {
                if (mData?.damagePic.isNullOrEmpty()){
                    TakePhotoUtil.takePhoto(activity!!,2, com.sribs.common.RxConstants.DAMAGE_PHOTO)
                }else{
                    showBigImage()
                }

            }
            it.damagePhotoDelBtn.setOnClickListener { v->
                v.visibility = View.GONE
                it.damagePhoto.setImageDrawable(null)
                mData?.damagePic = ""
            }

        }
        addDisposable(RxBus.getDefault().toObservableWithCode(com.sribs.common.RxConstants.DAMAGE_PHOTO,String::class.java)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","path=$it")
                mData?.damagePic = it
                if (mType==1){
                    mBinding.view1.damagePhotoDelBtn.visibility = View.VISIBLE
                }else{
                    mBinding.view2.damagePhotoDelBtn.visibility = View.VISIBLE
                }

            },{
                it.printStackTrace()
            }))


    }


    @SuppressLint("UseRequireInsteadOfGet")
    private fun initData(){

        mData = DamageEditDataBean()
        mData!!.bindEditText(mData!!::damageDes,mBinding.view1.damageEt)
        mData!!.bindImageView(mData!!::damagePic,if (mType==1)mBinding.view1.damagePhoto
        else mBinding.view2.damagePhoto)
        if (mType == 2){
            mData!!.bindTagEditView(mData!!::splitNum,mBinding.view2.damageEditSplit)
            mData!!.splitNum = "1条"
        }else if(mType ==3){
            mData!!.bindTagEditView(mData!!::seamNum,mBinding.view2.damageEditSplit)
            mData!!.seamNum = "1条"
        }
        mData!!.bindTagEditView(mData!!::splitWidth,mBinding.view2.damageEditWidth)
        mData!!.bindTagEditView(mData!!::splitLen,mBinding.view2.damageEditLen)
        mData!!.bindTagRadioView(mData!!::splitType,mBinding.view2.damageSplitType)
        mData!!.setOnDataChangedListener { name, s ->
            when(name){
                mData!!::damagePic.name-> {
                    var opt = RequestOptions()
                        .placeholder(R.drawable.ic_null)
                        .error(R.drawable.ic_null)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)


                    if(s.isNullOrEmpty()){
                        if (mType==1){
                            mBinding.view1.damagePhotoDelBtn.visibility = View.GONE
//                            mBinding.view1.damagePicAddBtn.isEnabled = true
                        }else{
                            mBinding.view2.damagePhotoDelBtn.visibility = View.GONE
//                            mBinding.view2.damagePicAddBtn.isEnabled = true
                        }
                        mBinding.view1.damagePhoto.setImageResource(R.color.transparent)
                        mBinding.view2.damagePhoto.setImageResource(R.color.transparent)
                    }else{
                        Glide.with(context!!)
                            .load(Uri.fromFile(File(s)))
                            .apply(opt)
                            .into(if (mType==1)mBinding.view1.damagePhoto else mBinding.view2.damagePhoto)

                        if (mType==1){
                            mBinding.view1.damagePhotoDelBtn.visibility = View.VISIBLE
                        }else{
                            mBinding.view2.damagePhotoDelBtn.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

    }

    fun setViewType(pos:ArrayList<String>,picPos:ArrayList<String>?){
        mPos = pos
        mPic = picPos
        mType = DescriptionPositionHelper.getDetailType(pos)
        LOG.I("123","pos  set type=$mType")
        when(mType){
            1->{
                mBinding.view1.root.visibility = View.VISIBLE
                mBinding.view2.root.visibility = View.GONE
            }
            2-> {
                mBinding.view1.root.visibility = View.GONE
                mBinding.view2.root.visibility = View.VISIBLE
                mBinding.view2.damageEditSplit.setTagText("裂缝数量")
                mBinding.view2.damageEditLenLl.visibility = View.VISIBLE
                mBinding.view2.damageEditWidthLl.visibility = View.VISIBLE
                mBinding.view2.damageSplitType.visibility = View.VISIBLE
            }
            3->{
                mBinding.view1.root.visibility = View.GONE
                mBinding.view2.root.visibility = View.VISIBLE
                mBinding.view2.damageEditSplit.setTagText("接缝数量")
                mBinding.view2.damageEditLenLl.visibility = View.GONE
                mBinding.view2.damageEditWidthLl.visibility = View.GONE
                mBinding.view2.damageSplitType.visibility = View.GONE
            }
        }
        initData()
        parseReport()
        getLocalData()
    }

    fun setViewOther(pos:ArrayList<String>,des:String){
        mPos = pos
        mType = 1
        mBinding.view1.root.visibility = View.VISIBLE
        mBinding.view2.root.visibility = View.GONE
        initData()
        parseReport()
        getLocalOtherData(des)
    }


    fun parseReport(){
        if(mPos?.contains("其他损伤")==true){
            mData?.damageDes = ""
        }else{
            mResultList = DescriptionPicHelper.parseDescription(mPos,mPic)
            if(mResultList?.size?:0>1){ //去掉第二项重复的
                if (mResultList!![1].contains(mResultList!![0])){
                    mResultList!![1] = mResultList!![1].replace(mResultList!![0],"")
                }
            }
            var str = mResultList?.joinToString(separator = "")?:""
            str = str.replace("顶板","顶板板底")
//            str = str.replace("地坪","地坪板顶")
            str = str.replace("墙涂料","墙墙面涂料")
            str = str.replace("墙纸","墙面墙纸")
            str = str.replace("护墙板","墙面护墙板")
            mData?.damageDes = str
            LOG.I("123","parseReport   damageDes=${mData?.damageDes}")
        }

    }

    private fun getLocalData(){
        var configId = (activity as HouseDamageDescriptionActivity).mConfigId
        var name = (activity as HouseDamageDescriptionActivity).mDamagePos
        var path = mPos?.joinToString(separator="-")
        var idx = if(!mPic.isNullOrEmpty()) mPic!![0].toIntOrNull()  else  -1
        LOG.I("123","getLocalData  $configId  $name  $path   $idx")
        mPresenter.getRoomDetail(configId,name,path?:"",idx?:-1)
    }

    private fun getLocalOtherData(des:String){
        var configId = (activity as HouseDamageDescriptionActivity).mConfigId
        var name = (activity as HouseDamageDescriptionActivity).mDamagePos
        var path = mPos?.joinToString(separator="-")
        mPresenter.getRoomDetail(configId, name,path?:"",des)
    }

    private fun showBigImage(){
        if (mData?.damagePic.isNullOrEmpty())return
        ImagePreview.getInstance().setContext(requireContext())
            .setImage(mData?.damagePic!!)
            .start()
    }

    fun createReport(cb:(Long)->Unit){
        LOG.I("123","pos=$mPos    pic=$mPic  data=$mData   $mResultList")
        var resultStr = mData?.damageDes
        when(mType){
            2->{
                if (mData?.splitType == "是" && mResultList?.size?:0 >=3){
                    mResultList!![2] = mResultList!![2].replace("斜裂缝","贯穿斜裂缝")
                }

                var lastIndex = (mResultList?.size?:0)-1
                if (lastIndex<0)lastIndex = 0
                LOG.I("123","lastIndex=$lastIndex")
                mResultList?.add(lastIndex,mData?.splitNum?:"1条")
                try {
                    var s = ""
                    if (!mData?.splitWidth.isNullOrEmpty()){
                        s += ",δ≈${String.format("%.1f",mData?.splitWidth?.toFloatOrNull())}mm"
                    }
                    if (!mData?.splitLen.isNullOrEmpty()){
                        s += ",L≈${ String.format("%.1f",mData?.splitLen?.toFloatOrNull())}m"
                    }
                    if (!s.isNullOrEmpty()) mResultList?.add(s)

                }catch (e:Exception){
                    e.printStackTrace()
                }
                resultStr = mResultList?.joinToString(separator = "")
            }
            3->{
                var lastIndex = (mResultList?.size?:0)-1
                if (lastIndex<0)lastIndex = 0
                mResultList?.add(lastIndex,mData?.seamNum?:"1条")
                resultStr = mResultList?.joinToString(separator = "")
            }
            else->{

            }

        }
        LOG.I("123","report=$mResultList   str=$resultStr")
        if(resultStr.isNullOrEmpty()){
            showToast("请填写损伤描述")
            return
        }


        var a = (activity as HouseDamageDescriptionActivity)


        mPresenter.updateRoomDetail(RoomDetailBean(
            projectId = a.mProjectId,
            unitId = a.mUnitId,
            configId = a.mConfigId,
            name = a.mDamagePos,
            damagePath =  mPos?.joinToString(separator = "-"),
            damageIdx = if (mPic.isNullOrEmpty()) -1 else mPic!![0].toIntOrNull(),
        ).also {
            it.description = resultStr
            if (!mData?.damagePic.isNullOrEmpty()){
                it.picPath = mData?.damagePic
            }
            if (!mData?.splitNum.isNullOrEmpty()){
                it.splitNum = mData?.splitNum
            }
            if (!mData?.splitWidth.isNullOrEmpty()){
                it.splitWidth = mData?.splitWidth
            }
            if (!mData?.splitLen.isNullOrEmpty()){
                it.splitLen = mData?.splitLen
            }
            if (!mData?.splitType.isNullOrEmpty()){
                it.splitType = if (mData?.splitType == "是") 1 else 0
            }
            if (!mData?.seamNum.isNullOrEmpty()){
                it.seamNum = mData?.seamNum
            }

        },cb)
    }



    override fun onRoomDetail(l: ArrayList<RoomDetailBean>) {
        LOG.I("123","onRoomDetail $l")
        if (l.isEmpty())return
        var b = l[0]
        when(mType){
            1->{
                mData?.damageDes = b.description?:""
                mData?.damagePic = b.picPath?:""
            }
            2->{
                mData?.splitNum = b.splitNum?:""
                mData?.splitLen = b.splitLen?:""
                mData?.splitWidth = b.splitWidth?:""
                mData?.splitType = if(b.splitType==1)"是" else "否"
                mData?.damagePic = b.picPath?:""
            }
            3->{
                mData?.seamNum = b.seamNum?:""
                mData?.damagePic = b.picPath?:""
            }
        }
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


}