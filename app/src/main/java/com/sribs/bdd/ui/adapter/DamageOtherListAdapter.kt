package com.sribs.bdd.ui.adapter

import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.bdd.R
import com.sribs.bdd.bean.DamageOtherBean
import com.sribs.bdd.databinding.ItemDamageOtherBinding

/**
 * @date 2021/7/20
 * @author elijah
 * @Description
 */
class DamageOtherListAdapter(var mCb:IOnItemClick):BaseListAdapter<DamageOtherBean,ItemDamageOtherBinding>() {
    interface IOnItemClick{
        fun onRemoved(pos:Int,b:DamageOtherBean)
    }

    var opt = RequestOptions()
        .placeholder(R.drawable.ic_common_no_pic_bk)
        .error(R.drawable.ic_common_no_pic_bk)
        .diskCacheStrategy(DiskCacheStrategy.ALL)

    override fun init(bind: ItemDamageOtherBinding, bean: DamageOtherBean, pos: Int) {
        bind.damageOtherName.text = bean.name
        Glide.with(bind.root).load(bean.picUrl).apply(opt).into(bind.damageOtherPic)
        bind.damageOtherDel.setOnClickListener {
            mList?.removeAt(pos)
            notifyItemRemoved(pos)
            notifyItemRangeChanged(pos,mList?.size?:0)
            mCb?.onRemoved(pos,bean)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemDamageOtherBinding> = newBindingViewHolder(parent)

    fun setData(l:ArrayList<DamageOtherBean>){
        mList = l
        notifyDataSetChanged()
    }
}