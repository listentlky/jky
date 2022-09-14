package com.sribs.bdd.ui.adapter

import android.net.Uri
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.bdd.R
import com.sribs.bdd.bean.RecordItemBean
import com.sribs.bdd.databinding.ItemRecordBinding
import java.io.File

/**
 * @date 2021/7/23
 * @author elijah
 * @Description
 */
class RecordListAdapter(var mCb:IClickListener):BaseListAdapter<RecordItemBean,ItemRecordBinding>() {
    var opt = RequestOptions()
        .placeholder(R.drawable.ic_common_no_pic_bk)
        .error(R.drawable.ic_common_no_pic_bk)
        .diskCacheStrategy(DiskCacheStrategy.ALL)

    override fun init(bind: ItemRecordBinding, bean: RecordItemBean, pos: Int) {
        LOG.I("123","iv = ${bean.imageUrl}")
        if (!bean.imageUrl.isNullOrEmpty()){
            if (!bean.imageUrl.startsWith("http://") && !bean.imageUrl.startsWith("https://")) {
                //本地文件
                Glide.with(bind.root).load(Uri.fromFile(File(bean.imageUrl))).apply(opt)
                    .into(bind.itemRecordIv)
            } else {
                Glide.with(bind.root).load(bean.imageUrl).apply(opt).into(bind.itemRecordIv)
            }
        }else{
            bind.itemRecordIv.setImageResource(R.mipmap.icon_ic_no_pic)
        }

        bind.itemRecordTv.text = bean.description
        bind.itemRecordRoot.setOnClickListener {
            LOG.I("123","onClick $bean")
            mCb.onClick(bean)
        }
        bind.itemRecordRoot.setOnLongClickListener {
            mCb.onLongClick(bean)
            true
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemRecordBinding> = newBindingViewHolder(parent)

    interface IClickListener{
        fun onClick(b:RecordItemBean)
        fun onLongClick(b:RecordItemBean)
    }

    fun setData(l:ArrayList<RecordItemBean>){
        mList?.clear()
        mList  = ArrayList(l.sortedByDescending { it.updateTime?.time })
        notifyDataSetChanged()
    }



}