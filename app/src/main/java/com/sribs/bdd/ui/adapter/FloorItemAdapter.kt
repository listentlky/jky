package com.sribs.bdd.ui.adapter

import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.launcher.ARouter
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.bdd.bean.BuildingFloorItem
import com.sribs.bdd.databinding.ItemFloorDetailBinding
import com.sribs.bdd.v3.util.LogUtils
import com.sribs.common.utils.TimeUtil

class FloorItemAdapter : BaseListAdapter<BuildingFloorItem, ItemFloorDetailBinding>() {


    override fun init(bind: ItemFloorDetailBinding, bean: BuildingFloorItem, pos: Int) {
        var routing: String?= null
        var text:String
        when (bean.type) {
            1 -> {
                text = "建筑结构复核"
                routing = com.sribs.common.ARouterPath.CHECK_BUILD_STRUCTURE_ACTIVITY
            }
            2 -> {
                text = "倾斜测量"
                routing = com.sribs.common.ARouterPath.CHECK_OBLIQUE_DEFORMATION_ACTIVITY
            }
            3 -> {
                text = "相对高差测量"
                routing = com.sribs.common.ARouterPath.CHECK_RELATIVE_H_DIFF_ACTIVITY
            }
            4 -> {
                text = "构建测量"
                routing = com.sribs.common.ARouterPath.CHECK_COMPONENT_DETECTION_ACTIVITY
            }
            5 -> {
                text = "居民类检测测量"
                routing = com.sribs.common.ARouterPath.BLD_DAMAGE_LIST_ATY
            }
            6 -> {
                text = "非居民类检测测量"
            }
            else -> {
                text = ""
            }
        }
        bind.name.text = text
        bind.time.text = bean.time
        bind.delete.setOnClickListener {
            mList?.remove(bean)
            notifyDataSetChanged()
        }
        bind.edit.setOnClickListener {
            if(routing != null && mItemClickCallback != null) {
                mItemClickCallback!!.onClick(routing)

            }
        }
        bind.config.setOnClickListener {
            //todo 跳转对应配置页面
            LogUtils.d("暂未配置配置跳转页")
        }

    }

    fun setData(list: ArrayList<BuildingFloorItem>) {
        mList = list
        notifyDataSetChanged()
    }

    fun addItem(item: BuildingFloorItem) {
        mList?.add(item)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemFloorDetailBinding> = newBindingViewHolder(parent)

    var mItemClickCallback:ItemClickCallback?=null
    fun setItemClickCallback(callback:ItemClickCallback){
        this.mItemClickCallback = callback
    }

    interface ItemClickCallback{
        fun onClick(routing:String);
    }
}