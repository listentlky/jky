package com.sribs.bdd.ui.adapter

import android.view.ViewGroup
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.sribs.bdd.bean.HouseConfigItemBean
import com.sribs.bdd.databinding.ItemHouseConfigListBinding
import com.sribs.common.bean.db.RoomStatusBean

/**
 * @date 2021/7/14
 * @author elijah
 * @Description
 */
class HouseListAdapter(var mCb:IHouseListListener):BaseListAdapter<HouseConfigItemBean,ItemHouseConfigListBinding>() {
    interface IHouseListListener{
        fun onStatusClick( bean: HouseConfigItemBean)
        fun onDamageClick(bean: HouseConfigItemBean)
        fun onClearClick(bean:HouseConfigItemBean)
        fun onAllFinish(b:Boolean)
    }

    override fun init(bind: ItemHouseConfigListBinding, bean: HouseConfigItemBean, pos: Int) {
        bind.itemHouseConfigName.text = bean.name
        bind.itemHouseConfigName.isSelected = bean.isFinish
//        bind.itemHouseConfigName.setOnClickListener {
//            mCb?.onStatusClick(bean)
//        }
        bind.root.setOnClickListener {
            mCb?.onDamageClick(bean)
        }
        bind.itemHouseDamageBtn.setOnClickListener {
            mCb?.onDamageClick(bean)
        }
        bind.itemHouseConfigStatus.setOnClickListener {
            mCb?.onStatusClick(bean)
        }
        bind.itemHouseConfigStatusClear.setOnClickListener {
            mCb?.onClearClick(bean)
        }
    }

    fun setData(l:ArrayList<HouseConfigItemBean>){
        mList = l
        notifyDataSetChanged()
    }

    fun updateStatus(l:ArrayList<RoomStatusBean>){
        if (mList.isNullOrEmpty())return
        if (l.isEmpty())return
        var isAllFinish = true
        Observable.create<Boolean> { o->
            var isChange = false
            LOG.I("123","l=$l")
            LOG.E("123","list=$mList")
            mList!!.forEach { b->
                var id = b.configId
                var name = b.name
                var statusBean = l.firstOrNull { s->s.configId==id && s.name == name }

                LOG.I("123","statusBean=$statusBean    b=$b  id=$id  name=$name")
                if (statusBean!=null){
                    if (b.isFinish != (statusBean.isFinish==1)){
                        isChange = true
                        b.isFinish = (statusBean.isFinish==1)
                    }
                    b.roomStatusId = statusBean.roomId
                }
                if (!b.isFinish){
                    isAllFinish = false
                }
            }
            o.onNext(isChange)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                LOG.I("123","isAllFinish = $isAllFinish")
                if (it)
                    notifyDataSetChanged()

                mCb?.onAllFinish(isAllFinish)
            },{
                it.printStackTrace()
            })
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemHouseConfigListBinding> = newBindingViewHolder(parent)
}