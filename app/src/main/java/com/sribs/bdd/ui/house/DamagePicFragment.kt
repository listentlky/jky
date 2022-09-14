package com.sribs.bdd.ui.house

import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.ui.widget.DamagePicLayout
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentHouseDamagePicBinding
import com.sribs.bdd.module.house.IHouseContrast
import com.sribs.bdd.module.house.RoomDetailPresenter

import com.sribs.common.ARouterPath
import com.sribs.common.bean.db.RoomDetailBean

/**
 * @date 2021/7/19
 * @author elijah
 * @Description
 */
@Route(path = ARouterPath.HOUSE_DAMAGE_PIC_FGT)
class DamagePicFragment:BaseFragment(R.layout.fragment_house_damage_pic),IHouseContrast.IRoomDetailView {

    private val mPresenter by lazy { RoomDetailPresenter() }

    private val mBinding:FragmentHouseDamagePicBinding by bindView()

    private var mPos:ArrayList<String>?=null

    private var mPic:ArrayList<String>?=null

    private var mCurPic:ArrayList<String>?=null

    override fun deinitView() {
        unbindPresenter()
    }

    override fun initView() {
        LOG.I("123","damagePic initView")
        bindPresenter()
        mBinding.damagePicFl.mCb = object: DamagePicLayout.IClickListener{
            override fun onClick(picIndex: String) {
                if (!mPos.isNullOrEmpty() && !picIndex.isNullOrEmpty()){
                    (activity as HouseDamageDescriptionActivity).doEdit(mPos!!, ArrayList(listOf(picIndex)))
                }
            }
        }
    }

    fun setDamagePos(pos:ArrayList<String>,cur:ArrayList<String>?){
        LOG.I("123","setDamagePos  pos=$pos  cur=$cur")
        mPos = pos
        mBinding.damagePicFl.setPos(pos)
        // read from db
        mCurPic = cur
        showDetailPicPos()
    }

    fun setDamagePic(pic:ArrayList<String>,sel:ArrayList<String>?){
        mPic = pic
        mBinding.damagePicFl.cleanPic()
        mBinding.damagePicFl.setPic(pic)
        if (!sel.isNullOrEmpty()){
            mBinding.damagePicFl.setPicSel(sel!!)
        }
    }



    fun refresh(){
        showDetailPicPos()
    }

    fun cleanSel(){
        mCurPic = null
    }

    private fun showDetailPicPos(){
        //single read
        if (mPos.isNullOrEmpty())return
        var configId = (activity as HouseDamageDescriptionActivity).mConfigId
        var name = (activity as HouseDamageDescriptionActivity).mDamagePos
        var path = mPos!!.joinToString(separator = "-")
        mPresenter.getRoomDetail(configId,name,path)
    }

    override fun onRoomDetail(l: ArrayList<RoomDetailBean>) {
        LOG.I("123","onRoomDetail l=$l")
        var picList = l.map { "${it.damageIdx}" }
        setDamagePic(ArrayList(picList),mCurPic)
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