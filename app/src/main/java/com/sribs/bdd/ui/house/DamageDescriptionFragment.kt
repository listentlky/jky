package com.sribs.bdd.ui.house

import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.bean.data.DamagePosDataBean
import com.sribs.bdd.databinding.FragmentHouseDamageDescriptionBinding
import com.sribs.bdd.utils.DescriptionPositionHelper
import com.sribs.common.bean.CommonBtnBean
import com.sribs.common.server.IDatabaseService
import com.sribs.common.ui.adapter.CommonBtnAdapter
import com.sribs.common.ui.widget.CommonLinearDividerItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @date 2021/7/19
 * @author elijah
 * @Description
 */
@Route(path = com.sribs.common.ARouterPath.HOUSE_DAMAGE_DES_FGT)
class DamageDescriptionFragment :BaseFragment(R.layout.fragment_house_damage_description){

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_HOUSE_POS)
    var mDamagePos = ""

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_HOUSE_TYPE)
    var mHouseType = -1

    @JvmField
    @Autowired(name= com.sribs.common.ARouterPath.VAL_UNIT_CONFIG_ID)
    var mConfigId = -1L

    private val mBinding : FragmentHouseDamageDescriptionBinding by bindView()

    private val mMap1 by lazy {  DescriptionPositionHelper.damageMap[damagePosFilter()] }

    private var mMap2:Map<String,Any?>?=null

    private var mList3:List<String>?=null

    private var mData:DamagePosDataBean ?=null

    private fun damagePosFilter() = when {
        mDamagePos.contains("一层")->mDamagePos.replace("一层","")
        mDamagePos.contains("二层")->mDamagePos.replace("二层","")
        else->mDamagePos
    }

    private val mAdapter1 by lazy { CommonBtnAdapter(true).also {
        it.mCustomItemBackground = R.drawable.sel_house_damage_des_btn_bk
        it.mCustomTextColor = R.color.sel_house_damage_des_btn_text
//        it.mCb = object:CommonBtnAdapter.OnClickListener{
//            override fun onClick(b: CommonBtnBean) {
//                val selStr = b.title
//                mData?.pos1 = selStr
//                if (selStr == "其他损伤") {
//                    mAdapter2.setData(null)
//                    mAdapter3.setData(null)
//                    return
//                }
//                var map = mMap1!![selStr]
//                if (map!=null){
//                    mMap2 = mMap1!![selStr] as Map<String,Any?>
//                    var list2 = mMap2?.keys
//                    mAdapter2.setData(ArrayList(list2?.filter { s->
//                        !s.isNullOrEmpty()
//                    }?.mapIndexed { index, s -> CommonBtnBean(index,s,false) } ).also { l->
//                        l.add(CommonBtnBean(-1,"其他损伤",false))
//                    })
//
//                }else{
//
//                    mAdapter2.setData(null)
//                    //todo all sel
//
//                    doAllSel()
//                }
//                mAdapter3.setData(null)
//            }
//        }
    } }

    private val mAdapter2 by lazy { CommonBtnAdapter(true).also {
        it.mCustomItemBackground = R.drawable.sel_house_damage_des_btn_bk
        it.mCustomTextColor = R.color.sel_house_damage_des_btn_text
//        it.mCb = object:CommonBtnAdapter.OnClickListener{
//            override fun onClick(b: CommonBtnBean) {
//                val selStr = b.title
//                mData?.pos2 = selStr
//                if (selStr == "其他损伤") {
//                    mAdapter3.setData(null)
//                    return
//                }
//
//
//                var l = mMap2!![selStr]
//                if (l!=null){
//                    mList3 =  mMap2!![selStr] as List<String>
//                    mAdapter3.setData(ArrayList(mList3?.filter { s->
//                        !s.isNullOrEmpty() }?.mapIndexed { index, s ->
//                        CommonBtnBean(index,s,false) }))
//                }else{
//                    mAdapter3.setData(null)
//                    //todo all sel
//                    doAllSel()
//                }
//            }
//        }
    } }

    private val mAdapter3 by lazy { CommonBtnAdapter(true).also {
        it.mCustomItemBackground = R.drawable.sel_house_damage_des_btn_bk
        it.mCustomTextColor = R.color.sel_house_damage_des_btn_text
//        it.mCb = object:CommonBtnAdapter.OnClickListener{
//            override fun onClick(b: CommonBtnBean) {
//                //todo all sel
//                val selStr = b.title
//                mData?.pos3 = selStr
//                doAllSel()
//            }
//        }
    } }


    override fun deinitView() {

    }

    override fun initView() {
        mBinding.houseDecorationRv1.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = mAdapter1
            it.addItemDecoration(CommonLinearDividerItemDecoration(
                0,
                resources.getDimensionPixelOffset(R.dimen.house_item_margin)
            ))
        }
        mBinding.houseDecorationRv2.also {
            it.layoutManager =  LinearLayoutManager(context)
            it.adapter = mAdapter2
            it.addItemDecoration(CommonLinearDividerItemDecoration(
                0,
                resources.getDimensionPixelOffset(R.dimen.house_item_margin)
            ))
        }
        mBinding.houseDecorationRv3.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = mAdapter3
            it.addItemDecoration(CommonLinearDividerItemDecoration(
                0,
                resources.getDimensionPixelOffset(R.dimen.house_item_margin)
            ))
        }

        var list1 = mMap1?.keys
        if (mHouseType == 2){
            mAdapter1.setData(ArrayList(list1?.mapIndexed { index, s ->
                CommonBtnBean(index,s,false) }?: emptyList() ).also { l->
                l.add(CommonBtnBean(-1,"其他损伤",false))
            })
        } else {
            getList1ByConfig()
        }

        mData = DamagePosDataBean()
        mData!!.setOnDataChangedListener { prop, selStr ->
            when(prop){
                mData!!::pos1.name->{
                    mAdapter1.setSelect(selStr)
                    mData!!.pos2 = ""
                    mData!!.pos3 = ""
                    if (selStr == "其他损伤") {
                        mAdapter2.setData(null)
                        mAdapter3.setData(null)
                        (activity as HouseDamageDescriptionActivity).doOtherList()
                        return@setOnDataChangedListener
                    }
                    var map = mMap1!![selStr]
                    if (map!=null){
                        mMap2 = mMap1!![selStr] as Map<String,Any?>
                        var list2 = mMap2?.keys
                        mAdapter2.setData(ArrayList(list2?.filter { s->
                            !s.isNullOrEmpty()
                        }?.mapIndexed { index, s -> CommonBtnBean(index,s,false) } ).also { l->
                            l.add(CommonBtnBean(-1,"其他损伤",false))
                        })

                    }else{
                        mAdapter2.setData(null)
                    }
                    mAdapter3.setData(null)
                    doAllSel(null)
                }
                mData!!::pos2.name->{
                    mAdapter2.setSelect(selStr)
                    mData!!.pos3 = ""
                    if (selStr == "其他损伤"|| selStr == "其他损伤") {
                        mAdapter3.setData(null)
                        (activity as HouseDamageDescriptionActivity).doOtherList()
                        return@setOnDataChangedListener
                    }
                    var l = mMap2!![selStr]
                    if (l!=null){
                        mList3 =  mMap2!![selStr] as List<String>
                        mAdapter3.setData(ArrayList(mList3?.filter { s->
                            !s.isNullOrEmpty() }?.mapIndexed { index, s ->
                            CommonBtnBean(index,s,false) }))
                    }else{
                        mAdapter3.setData(null)


                    }
                    doAllSel(null)
                }
                mData!!::pos3.name->{
                    mAdapter3.setSelect(selStr)
                    doAllSel(null)
                }
            }
        }
        mData!!.bindPos(mData!!::pos1,mBinding.houseDecorationRv1)
        mData!!.bindPos(mData!!::pos2,mBinding.houseDecorationRv2)
        mData!!.bindPos(mData!!::pos3,mBinding.houseDecorationRv3)
    }

    fun doAllSel(cur:ArrayList<String>?){
        var pos1 = mData?.pos1
        var pos2 = mData?.pos2
        var pos3 = mData?.pos3
        var l = getAllSel()
        var s = l.joinToString(separator = "-")
        if (pos1?.contains("其他")==true ||
            pos2?.contains("其他")==true){
            (activity as HouseDamageDescriptionActivity).filterRecord(s)
            return
        }

        LOG.I("123","do all sel l=$l")
        (activity as HouseDamageDescriptionActivity).doPic(l,cur)


        LOG.E("123","filter s = $s")
        (activity as HouseDamageDescriptionActivity).filterRecord(s)
    }

    fun getAllSel():ArrayList<String>{
        var pos1 = mData?.pos1
        var pos2 = mData?.pos2
        var pos3 = mData?.pos3
        var l = ArrayList<String>()
        if (!pos1.isNullOrEmpty()){
            l.add(pos1)
        }
        if (!pos2.isNullOrEmpty()){
            l.add(pos2)
        }
        if (!pos3.isNullOrEmpty()){
            l.add(pos3)
        }
        return l
    }

    fun lock(){
        mAdapter1.enable(false)
        mAdapter2.enable(false)
        mAdapter3.enable(false)
    }

    fun unlock(){
        mAdapter1.enable(true)
        mAdapter2.enable(true)
        mAdapter3.enable(true)
    }

    fun selRecord(pos:ArrayList<String>,cur:ArrayList<String>?){
        if (pos.size>0)mData?.pos1 = pos[0]
        if (pos.size>1)mData?.pos2 = pos[1]
        if (pos.size>2)mData?.pos3 = pos[2]
        doAllSel(cur)
    }

    fun getList1ByConfig(){
        var mDb =  ARouter.getInstance().build(com.sribs.common.ARouterPath.SRV_DB)
            .navigation() as IDatabaseService
        mDb.getConfigOnce(mConfigId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isNotEmpty()){
                    var configStr = when (mHouseType) {
                        0 -> it[0].corridorConfig
                        1 -> it[0].platformConfig
                        else -> ""
                    }
                    var list1 = configStr?.split(",")
                    mAdapter1.setData(ArrayList(list1?.mapIndexed { index, s ->
                        CommonBtnBean(index,s,false) }?: emptyList() ).also { l->
                        l.add(CommonBtnBean(-1,"其他损伤",false))
                    })
                }
            },{
                it.printStackTrace()
            })
    }
}