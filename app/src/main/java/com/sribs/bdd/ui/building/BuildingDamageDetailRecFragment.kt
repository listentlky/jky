package com.sribs.bdd.ui.building

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.annotation.Nullable
import cc.shinichi.library.ImagePreview
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cbj.sdk.libbase.rxbus.RxBus
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.bean.Floor
import com.sribs.bdd.bean.data.*
import com.sribs.bdd.databinding.FragmentBuildingDamageDetailBinding

import com.sribs.bdd.module.building.IBuildingContrast

import com.sribs.bdd.utils.ModuleHelper.CUR_BLD_NO
import com.sribs.common.bean.db.DamageBean
import com.sribs.common.bean.db.DrawingBean
import com.sribs.common.utils.TakePhotoUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import kotlin.collections.ArrayList


/**
 * @date 2022/3/2
 * @author leon
 * @Description 非居民类建筑新建项目明细页面
 */
@Route(path= com.sribs.common.ARouterPath.BLD_DRW_DMG_DIAL_REC_FGT)
class BuildingDamageDetailRecFragment:BaseFragment(R.layout.fragment_building_damage_detail), IBuildingContrast.IBuildingView {

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID)
    var mLocalProjectId = -1

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_BUILDING_ID)
    var mBuildingId = -1

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_BUILDING_NO)
    var mBuildingNo = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_DAMAGE_MONITOR_ID)
    var mNewDamageMonitorId = ""

    private val TAG = "leon"

    private var mData: DamageDrawingDataBean?=null

    private val mBinding : FragmentBuildingDamageDetailBinding by bindView()

    private var mCacheDamage: DamageBean? = null

    private lateinit var mPrefs: SharedPreferences

    private var mBldNo: String? = ""

    private var mMonitorWay: String? = ""

    private var mViewInitialized:Boolean = false

    override fun deinitView() {
        unbindPresenter()
    }

    override fun initView() {
        println("leon BuildingDamageDetailRecFragment initView")

        initData()

        setMonitorStatus(false)
        setCrackStatus(false)
        clearCbxStatus()

        setView(mCacheDamage)

        var arr = requireContext().resources.getStringArray(R.array.damage_monitor_way)
        val monitorWaySpinner: Spinner = mBinding.spnMonitorWay as Spinner
        var dataset = arrayListOf<String>()

        for(item in arr){
            dataset!!.add(item.toString())
        }
        //数据源手动添加
        var spinnerAdapter = ArrayAdapter<String> (requireContext(),
            R.layout.abacus_spinner_text_item, dataset)
        monitorWaySpinner.setAdapter(spinnerAdapter)

        //设置下拉选项的方式
        spinnerAdapter.setDropDownViewResource(R.layout.abacus_spinner_dropdown_item)
        monitorWaySpinner.setAdapter(spinnerAdapter)

        monitorWaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mMonitorWay = dataset.get(position)
                println("leon niceSpinner onItemSelected mMonitorWay=${mMonitorWay}")
            }
        }

        mBinding.damagePicAddBtn.also {
            it.setOnClickListener {
                if (mData?.damagePic.isNullOrEmpty()) {
                    TakePhotoUtil.takePhoto(requireActivity()!!,
                        2,
                        com.sribs.common.RxConstants.DAMAGE_PHOTO)
                } else {
                    showBigImage(mData?.damagePic!!)
                }
            }
        }

        mBinding.damagePhotoDelBtn.also {
            it.setOnClickListener { v->
                v.visibility = View.GONE
                mData?.damagePic = ""
                (v as ImageView).setImageDrawable(null)
                mBinding.damagePhoto.setImageDrawable(null)
            }
        }

        addDisposable(RxBus.getDefault().toObservableWithCode(com.sribs.common.RxConstants.DAMAGE_PHOTO,String::class.java)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("leon","BuildingDamageDetailRecFragment path=$it")

                mData?.damagePic = it
                mBinding.damagePhotoDelBtn.visibility = View.VISIBLE

            },{
                it.printStackTrace()
            }))

        addDisposable(RxBus.getDefault().toObservableWithCode(com.sribs.common.RxConstants.DAMAGE_MONITOR_PHOTO,String::class.java)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("leon","BuildingDamageDetailRecFragment 2 path=$it")

                mData?.damageCrackMonitorPic = it
                mBinding.damageMonitorPhotoDelBtn.visibility = View.VISIBLE

            },{
                it.printStackTrace()
            })
        )

        mBinding.btnOk.setOnClickListener { it->

            if(mData?.axis.isNullOrEmpty() || mData?.damageDes.isNullOrEmpty()){
                AlertDialog.Builder(requireContext()).setTitle(R.string.drawing_edit_exit_dialog_title)
                    .setMessage(R.string.pdf_damage_miss_axis_desc).setPositiveButton(R.string.dialog_ok) { dialog, which ->
                        var temp: DamageDrawingDataBean? = null
                        returnPdfView(temp)
                    }.setNegativeButton(R.string.dialog_cancel
                    ) { dialog, which ->

                    }
                    .show()
            }
            else {

                if(mMonitorWay.isNullOrEmpty())
                    mMonitorWay = dataset.get(0).toString()

                mData?.monitorWay = mMonitorWay
                if(mData?.damageType.isNullOrEmpty())
                    mData?.damageType = "common"

                println("leon btn_ok mData=" + mData.toString())
                var temp: DamageDrawingDataBean = DamageDrawingDataBean()
                mData?.let {
                    temp.setData(mData!!)
                }
                mData?.clean()

                returnPdfView(temp)
            }

        }

        mBinding.btnCancel.setOnClickListener { it->

            println("leon btn_cancel mData=" + mData.toString())
            var temp:DamageDrawingDataBean? = null

            mData?.clean()

            (activity as BuildingDamageActivity).recoverViewPager(temp)

        }

        mBinding.cbxCrackInfo.setOnCheckedChangeListener { compoundButton, isChecked ->
            if(isChecked){
                mData?.damageType = "crack"
                setCrackStatus(true)
            }
            else{
                if(!mBinding.cbxLeakMonitorInfo.isChecked)
                    mData?.damageType = "common"
                setCrackStatus(false)
            }
        }

        mBinding.cbxLeakMonitorInfo.setOnCheckedChangeListener { compoundButton, isChecked ->
            if(isChecked) {
                mData?.damageType = "crack"
                setMonitorStatus(true)

                //自动生成监测点编号
                if(mBinding.etMonitorId.text.toString() == ""){
                    var mntId:String = "L" + mBldNo + "-" + mNewDamageMonitorId
                    mBinding.etMonitorId.text = Editable.Factory.getInstance().newEditable(mntId)
                }

                mBinding.damageMonitorPicAddBtn.also {
                    it.setOnClickListener {
                        if (mData?.damageCrackMonitorPic.isNullOrEmpty()) {
                            TakePhotoUtil.takePhoto(requireActivity()!!,
                                2,
                                com.sribs.common.RxConstants.DAMAGE_MONITOR_PHOTO)
                        } else {
                            showBigImage(mData?.damageCrackMonitorPic!!)
                        }
                    }
                }

                mBinding.damageMonitorPhotoDelBtn.also {
                    it.setOnClickListener { v->
                        v.visibility = View.GONE
                        mData?.damageCrackMonitorPic = ""
                        (v as ImageView).setImageDrawable(null)
                        mBinding.damageMonitorPhoto.setImageDrawable(null)
                    }
                }
            }
            else {
                if(!mBinding.cbxCrackInfo.isChecked)
                    mData?.damageType = "common"

                setMonitorStatus(false)

                mBinding.etMonitorId.text = Editable.Factory.getInstance().newEditable(mData?.monitorPointNo.toString())

                mBinding.damageMonitorPicAddBtn.also {
                    it.setOnClickListener {

                    }
                }

                mBinding.damageMonitorPhotoDelBtn.also {
                    it.setOnClickListener { v->

                    }
                }
            }
        }

        mViewInitialized = true
    }

    private fun returnPdfView(temp:DamageDrawingDataBean?){
        setMonitorStatus(false)
        setCrackStatus(false)
        clearCbxStatus()
        (activity as BuildingDamageActivity).recoverViewPager(temp)
    }

    private fun setMonitorStatus(status: Boolean){
        mBinding.damageMonitorPhoto.isEnabled = status
        mBinding.etMonitorId.isEnabled = status
        mBinding.spnMonitorWay.isEnabled = status
        mBinding.etNickLength.isEnabled = status
        mBinding.etNickWidth.isEnabled = status
    }

    private fun setCrackStatus(status: Boolean){
        mBinding.etCrackLength.isEnabled = status
        mBinding.etCrackWidth.isEnabled = status
    }

    private fun clearCbxStatus(){
        mBinding.cbxCrackInfo.isChecked = false
        mBinding.cbxLeakMonitorInfo.isChecked = false
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initData(){
        println("leon BuildingDamageDetailRecFragment initData mData=${mData.toString()}")

        mPrefs = requireActivity().getSharedPreferences("createProject", Context.MODE_PRIVATE)
        mBldNo = mPrefs.getString(CUR_BLD_NO,"")

        mData = mData?:DamageDrawingDataBean()
        mData!!.bindEditText(mData!!::axis, mBinding.etAxis)
        mData!!.bindEditText(mData!!::damageDes, mBinding.etDesc)
        mData!!.bindImageView(mData!!::damagePic, mBinding.damagePhoto)
        mData!!.bindEditText(mData!!::crackLength, mBinding.etCrackLength)
        mData!!.bindEditText(mData!!::crackWidth, mBinding.etCrackWidth)
        mData!!.bindEditText(mData!!::monitorPointNo, mBinding.etMonitorId)
        mData!!.bindEditText(mData!!::nickLength, mBinding.etNickLength)
        mData!!.bindEditText(mData!!::nickWidth, mBinding.etNickWidth)

        mData!!.setOnDataChangedListener { name, s ->
//            println("leon BuildingDamageDetailRecFragment name=${name}, s=${s}")
//            println("leon BuildingDamageDetailRecFragment name=${mData!!::damagePic.name}")
            when(name){
                mData!!::damagePic.name-> {
                    var opt = RequestOptions()
                        .placeholder(R.drawable.ic_null)
                        .error(R.drawable.ic_null)

                    if(s.isNullOrEmpty()){
                        mBinding.damagePhotoDelBtn.visibility = View.GONE
                        mBinding.damagePhoto.setImageResource(R.color.transparent)
                    }else{
                        var uri:Uri? = Uri.fromFile(File(s))
                        if(uri == null)
                            println("leon BuildingDamageDetailRecFragment uri is NULL")
                        else
                            println("leon BuildingDamageDetailRecFragment uri is NOT NULL")

                        Glide.with(requireActivity()!!)
                            .load(uri)
                            .apply(opt)
                            .into(mBinding.damagePhoto)

                        mBinding.damagePhoto.bringToFront()
                        mBinding.damagePhotoDelBtn.visibility = View.VISIBLE

                    }
                }
                mData!!::damageCrackMonitorPic.name-> {
                    var opt = RequestOptions()
                        .placeholder(R.drawable.ic_null)
                        .error(R.drawable.ic_null)

                    if(s.isNullOrEmpty()){
                        mBinding.damageMonitorPhotoDelBtn.visibility = View.GONE
                        mBinding.damageMonitorPhoto.setImageResource(R.color.transparent)
                    }else{
                        var uri:Uri? = Uri.fromFile(File(s))

                        Glide.with(requireActivity()!!)
                            .load(uri)
                            .apply(opt)
                            .into(mBinding.damageMonitorPhoto)

                        mBinding.damageMonitorPhoto.bringToFront()
                        mBinding.damageMonitorPhotoDelBtn.visibility = View.VISIBLE
                    }
                }
                else->{

                }

            }
        }
    }

    override fun initBuildingFloors(bldId:Long, floors: ArrayList<Floor>) {

    }

    override fun initBuildingDrawing(drawings: ArrayList<DrawingBean>) {

    }

    override fun initDrawingDamages(damages: List<DamageBean>) {

    }

    override fun saveDamageDataToDbStarted() {

    }

    private fun setView(dmg:DamageBean?) {

        dmg?.apply {
            mData?.axis = dmg?.axis
            mData?.damageDes = dmg?.dmDesc
            mData?.damagePic = dmg?.pohtoPath
            mData?.crackLength = dmg?.leakLength
            mData?.crackWidth = dmg?.leakWidth
            mData?.monitorPointNo = dmg?.mntId
            mData?.monitorWay = dmg?.mntWay
            mData?.nickLength = dmg?.monitorLength
            mData?.nickWidth = dmg?.monitorWidth
            mData?.damageCrackMonitorPic = dmg?.monitorPhotoPath
            mData?.damageType = dmg?.dDetailType
        }

        println("leon setView mData=${mData.toString()}")
        println("leon setView mViewInitialized=${mViewInitialized}")
        if(mViewInitialized) {
            if (!mData?.crackLength.isNullOrEmpty() || !mData?.crackWidth.isNullOrEmpty()) {
                mBinding.cbxCrackInfo.isChecked = true
            } else
                println("leon mData?.crackLength=${mData?.crackLength.toString()}")

            if (!mData?.monitorPointNo.isNullOrEmpty() || !mData?.nickLength.isNullOrEmpty() || !mData?.nickWidth.isNullOrEmpty()) {
                mBinding.cbxLeakMonitorInfo.isChecked = true
            } else
                println("leon mData?.monitorPointNo=${mData?.monitorPointNo.toString()}")
        }
    }

    override fun updateLocalDamageDetail(dmg: DamageBean?) {
        println("leon updateLocalDamageDetail dmg=${dmg.toString()}")

        mCacheDamage = dmg
        println("leon updateLocalDamageDetail mCacheDamage=${mCacheDamage.toString()}")
        setView(mCacheDamage)
    }

    override fun onRemoveDamageInDrawing(dmg: DamageBean) {

    }

    override fun bindPresenter() {
//       mPresenter.bindView(this)
    }

    override fun onMsg(msg: String) {
        showToast(msg)
    }

    override fun unbindPresenter() {
//       mPresenter.unbindView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


    }

    private fun showBigImage(photoPath: String){
        println("leon BuildingDamageDetailRecFragment showBigImage")
        if (photoPath.isNullOrEmpty())return
        ImagePreview.getInstance().setContext(requireContext())
            .setImage(photoPath!!)
            .start()
    }

    override fun onPause() {
        super.onPause()
        LOG.I("leon", "ProjectStoreyFragment onPause")
    }

    override fun onResume() {
        super.onResume()
        LOG.I("leon", "ProjectStoreyFragment onResume")
    }

    override fun onStop() {
        super.onStop()
        LOG.I("leon", "ProjectStoreyFragment onStop")
    }
}