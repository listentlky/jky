package com.sribs.bdd.ui.building

//import com.naim.androd11supportedfilemanager.model.SupportedFile
//import com.naim.androd11supportedfilemanager.picker.FilePickerLifeCycleObserver
//import com.naim.androd11supportedfilemanager.util.SupportedFileAnnotationType

import android.content.SharedPreferences
import com.afollestad.recyclical.datasource.emptyDataSource
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libbase.rxbus.RxBus
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.bean.Floor
import com.sribs.bdd.bean.data.NonInFloorDetailDataBean
import com.sribs.bdd.databinding.FragmentDrawingTypeSubBinding
import com.sribs.bdd.module.building.BuildingDamageMainPresenter
import com.sribs.bdd.module.building.BuildingDamageSubPresenter
import com.sribs.bdd.module.building.IBuildingContrast
import com.sribs.bdd.rx.ButtonClickEvent
import com.sribs.bdd.utils.ModuleHelper
import com.sribs.common.bean.db.DamageBean
import com.sribs.common.bean.db.DrawingBean


/**
 * @date 2022/3/28
 * @author leon
 * @Description 按每楼图纸管理损伤
 */
@Route(path= com.sribs.common.ARouterPath.BLD_DRW_DMG_MNG_SUB_FGT)
class BldDrwDmgMngSubFragment:BaseFragment(R.layout.fragment_drawing_type_sub), IBuildingContrast.IBuildingView {

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_COMMON_LOCAL_ID)
    var mLocalProjectId = -1

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_BUILDING_ID)
    var mBuildingId = -1

    private val TAG = "leon"

    private val mBinding : FragmentDrawingTypeSubBinding by bindView()
    private var mBtnChecked: Boolean = false
    private var mIAnnotSwicthLister: IAnnotSwicthLister? = null

    val mData: NonInFloorDetailDataBean by lazy {
        NonInFloorDetailDataBean(mLocalProjectId)
    }

    override fun deinitView() {
        unbindPresenter()
    }

    private val mPresenter  by lazy { BuildingDamageSubPresenter() }

    override fun initView() {
        LOG.I("leon","ProjectStoreyFragment initView")
        initData()
        bindPresenter()
    }

    private fun initData(){

        mIAnnotSwicthLister = activity as IAnnotSwicthLister

        mBinding.tvLine.setOnClickListener {
            if(!mBtnChecked){
                mBinding.tvLine.setBackgroundResource(R.drawable.ic_drawing_pen_sel_background)
                mBinding.tvRect.isEnabled = false;
                mBinding.tvCircle.isEnabled = false;
                mBinding.tvText.isEnabled = false;
                mBinding.tvManual.isEnabled = false;
            }
            else{
                mBinding.tvLine.setBackgroundResource(R.color.white)
                mBinding.tvRect.isEnabled = true;
                mBinding.tvCircle.isEnabled = true;
                mBinding.tvText.isEnabled = true;
                mBinding.tvManual.isEnabled = true;
            }
            mBtnChecked = !mBtnChecked
            mBinding.tvRect.setBackgroundResource(R.color.white)
            mBinding.tvCircle.setBackgroundResource(R.color.white)
            mBinding.tvText.setBackgroundResource(R.color.white)
            mBinding.tvManual.setBackgroundResource(R.color.white)

//            RxBus.getDefault().post(ButtonClickEvent(ModuleHelper.DRAWING_DMG_LINE_BUTTON_CLICKED, mBtnChecked))
            println("leon sent ${ModuleHelper.DRAWING_DMG_LINE_BUTTON_CLICKED}, checked=${mBtnChecked}")
//            mPresenter.annotSwitched(ModuleHelper.DRAWING_DMG_LINE_BUTTON_CLICKED, mBtnChecked)
            mIAnnotSwicthLister?.onAnnotClicked(ModuleHelper.DRAWING_DMG_LINE_BUTTON_CLICKED, mBtnChecked)

        }
        mBinding.tvRect.setOnClickListener {
            if(!mBtnChecked){
                mBinding.tvRect.setBackgroundResource(R.drawable.ic_drawing_pen_sel_background)
                mBinding.tvLine.isEnabled = false;
                mBinding.tvCircle.isEnabled = false;
                mBinding.tvText.isEnabled = false;
                mBinding.tvManual.isEnabled = false;
            }
            else{
                mBinding.tvRect.setBackgroundResource(R.color.white)
                mBinding.tvLine.isEnabled = true;
                mBinding.tvCircle.isEnabled = true;
                mBinding.tvText.isEnabled = true;
                mBinding.tvManual.isEnabled = true;
            }
            mBtnChecked = !mBtnChecked
            mBinding.tvLine.setBackgroundResource(R.color.white)
            mBinding.tvCircle.setBackgroundResource(R.color.white)
            mBinding.tvText.setBackgroundResource(R.color.white)
            mBinding.tvManual.setBackgroundResource(R.color.white)

//            RxBus.getDefault().post(ButtonClickEvent(ModuleHelper.DRAWING_DMG_RECT_BUTTON_CLICKED, mBtnChecked))
//            mPresenter.annotSwitched(ModuleHelper.DRAWING_DMG_RECT_BUTTON_CLICKED, mBtnChecked)
            mIAnnotSwicthLister?.onAnnotClicked(ModuleHelper.DRAWING_DMG_RECT_BUTTON_CLICKED, mBtnChecked)
            println("leon sent ${ModuleHelper.DRAWING_DMG_RECT_BUTTON_CLICKED}, checked=${mBtnChecked}")
        }
        mBinding.tvCircle.setOnClickListener {
            if(!mBtnChecked){
                mBinding.tvCircle.setBackgroundResource(R.drawable.ic_drawing_pen_sel_background)
                mBinding.tvLine.isEnabled = false;
                mBinding.tvRect.isEnabled = false;
                mBinding.tvText.isEnabled = false;
                mBinding.tvManual.isEnabled = false;
            }
            else{
                mBinding.tvCircle.setBackgroundResource(R.color.white)
                mBinding.tvLine.isEnabled = true;
                mBinding.tvRect.isEnabled = true;
                mBinding.tvText.isEnabled = true;
                mBinding.tvManual.isEnabled = true;
            }
            mBtnChecked = !mBtnChecked

            mBinding.tvLine.setBackgroundResource(R.color.white)
            mBinding.tvRect.setBackgroundResource(R.color.white)
            mBinding.tvText.setBackgroundResource(R.color.white)
            mBinding.tvManual.setBackgroundResource(R.color.white)

//            RxBus.getDefault().post(ButtonClickEvent(ModuleHelper.DRAWING_DMG_CIRCLE_BUTTON_CLICKED, mBtnChecked))
//            mPresenter.annotSwitched(ModuleHelper.DRAWING_DMG_CIRCLE_BUTTON_CLICKED, mBtnChecked)
            mIAnnotSwicthLister?.onAnnotClicked(ModuleHelper.DRAWING_DMG_CIRCLE_BUTTON_CLICKED, mBtnChecked)
            println("leon sent ${ModuleHelper.DRAWING_DMG_CIRCLE_BUTTON_CLICKED}")
        }
        mBinding.tvText.setOnClickListener {
            if(!mBtnChecked){
                mBinding.tvText.setBackgroundResource(R.drawable.ic_drawing_pen_sel_background)
                mBinding.tvLine.isEnabled = false;
                mBinding.tvRect.isEnabled = false;
                mBinding.tvCircle.isEnabled = false;
                mBinding.tvManual.isEnabled = false;
            }
            else{
                mBinding.tvText.setBackgroundResource(R.color.white)
                mBinding.tvLine.isEnabled = true;
                mBinding.tvRect.isEnabled = true;
                mBinding.tvCircle.isEnabled = true;
                mBinding.tvManual.isEnabled = true;
            }
            mBtnChecked = !mBtnChecked

            mBinding.tvLine.setBackgroundResource(R.color.white)
            mBinding.tvRect.setBackgroundResource(R.color.white)
            mBinding.tvCircle.setBackgroundResource(R.color.white)
            mBinding.tvManual.setBackgroundResource(R.color.white)
//            RxBus.getDefault().post(ButtonClickEvent(ModuleHelper.DRAWING_DMG_TEXT_BUTTON_CLICKED, mBtnChecked))
//            mPresenter.annotSwitched(ModuleHelper.DRAWING_DMG_TEXT_BUTTON_CLICKED, mBtnChecked)
            mIAnnotSwicthLister?.onAnnotClicked(ModuleHelper.DRAWING_DMG_TEXT_BUTTON_CLICKED, mBtnChecked)
            println("leon sent ${ModuleHelper.DRAWING_DMG_TEXT_BUTTON_CLICKED}")
        }
        mBinding.tvManual.setOnClickListener {
            if(!mBtnChecked){
                mBinding.tvManual.setBackgroundResource(R.drawable.ic_drawing_pen_sel_background)
                mBinding.tvLine.isEnabled = false;
                mBinding.tvRect.isEnabled = false;
                mBinding.tvCircle.isEnabled = false;
                mBinding.tvText.isEnabled = false;
            }
            else{
                mBinding.tvManual.setBackgroundResource(R.color.white)
                mBinding.tvLine.isEnabled = false;
                mBinding.tvRect.isEnabled = false;
                mBinding.tvCircle.isEnabled = false;
                mBinding.tvText.isEnabled = false;
            }
            mBtnChecked = !mBtnChecked

            mBinding.tvLine.setBackgroundResource(R.color.white)
            mBinding.tvRect.setBackgroundResource(R.color.white)
            mBinding.tvText.setBackgroundResource(R.color.white)
            mBinding.tvCircle.setBackgroundResource(R.color.white)
//            RxBus.getDefault().post(ButtonClickEvent(ModuleHelper.DRAWING_DMG_MAN_BUTTON_CLICKED, mBtnChecked))
//            mPresenter.annotSwitched(ModuleHelper.DRAWING_DMG_MAN_BUTTON_CLICKED, mBtnChecked)
            mIAnnotSwicthLister?.onAnnotClicked(ModuleHelper.DRAWING_DMG_MAN_BUTTON_CLICKED, mBtnChecked)
            println("leon sent ${ModuleHelper.DRAWING_DMG_MAN_BUTTON_CLICKED}")
        }
    }

    public fun onBackButtonClicked(){
        if(mBtnChecked){
            mBtnChecked = !mBtnChecked

            if(mBinding.tvLine.isEnabled){
                mBinding.tvLine.setBackgroundResource(R.color.white)
                RxBus.getDefault().post(ButtonClickEvent(ModuleHelper.DRAWING_DMG_LINE_BUTTON_CLICKED, mBtnChecked))
            }else if(mBinding.tvRect.isEnabled){
                mBinding.tvRect.setBackgroundResource(R.color.white)
                RxBus.getDefault().post(ButtonClickEvent(ModuleHelper.DRAWING_DMG_RECT_BUTTON_CLICKED, mBtnChecked))
            }else if(mBinding.tvCircle.isEnabled){
                mBinding.tvCircle.setBackgroundResource(R.color.white)
                RxBus.getDefault().post(ButtonClickEvent(ModuleHelper.DRAWING_DMG_CIRCLE_BUTTON_CLICKED, mBtnChecked))
            }else if(mBinding.tvText.isEnabled){
                mBinding.tvText.setBackgroundResource(R.color.white)
                RxBus.getDefault().post(ButtonClickEvent(ModuleHelper.DRAWING_DMG_TEXT_BUTTON_CLICKED, mBtnChecked))
            }else if(mBinding.tvManual.isEnabled){
                mBinding.tvManual.setBackgroundResource(R.color.white)
                RxBus.getDefault().post(ButtonClickEvent(ModuleHelper.DRAWING_DMG_MAN_BUTTON_CLICKED, mBtnChecked))
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

    override fun updateLocalDamageDetail(dmg: DamageBean?) {

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

interface IAnnotSwicthLister {
    //定义监听方法 根据实际需求来
    fun onAnnotClicked(annotId: String, checked:Boolean)
}