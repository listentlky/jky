package com.sribs.bdd.ui.adapter

import android.view.ViewGroup

import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.bdd.bean.BuildingFloorItem
import com.sribs.bdd.databinding.ItemFloorDetailBinding


class FloorItemAdapter : BaseListAdapter<BuildingFloorItem, ItemFloorDetailBinding>() {


    override fun init(bind: ItemFloorDetailBinding, bean: BuildingFloorItem, pos: Int) {

        var routing: String? = null
        var configRouting: String =
            com.sribs.common.ARouterPath.CHECK_MODULE_CONFIG_TYPE_BUILDING_ACTIVITY
        var text: String
        when (bean.name) {
            "建筑结构复核" -> {
                text = "建筑结构复核"
                routing = com.sribs.common.ARouterPath.CHECK_BUILD_STRUCTURE_ACTIVITY
                configRouting = com.sribs.common.ARouterPath.CHECK_MODULE_CONFIG_TYPE_FLOOR_ACTIVITY
            }
            "倾斜测量" -> {
                text = "倾斜测量"
                routing = com.sribs.common.ARouterPath.CHECK_OBLIQUE_DEFORMATION_ACTIVITY
            }
            "相对高差测量" -> {
                text = "相对高差测量"
                routing = com.sribs.common.ARouterPath.CHECK_RELATIVE_H_DIFF_ACTIVITY
            }
            "构建检测" -> {
                text = "构建检测"
                routing = com.sribs.common.ARouterPath.CHECK_COMPONENT_DETECTION_ACTIVITY
            }
            "居民类检测测量" -> {
                text = "居民类检测测量"
                routing = com.sribs.common.ARouterPath.BLD_DAMAGE_LIST_ATY
            }
            "非居民类检测测量" -> {
                text = "非居民类检测测量"
            }
            else -> {
                text = ""
            }
        }
        bind.name.text = text
        bind.time.text = bean.updateTime
        bind.delete.setOnClickListener {
            mList?.remove(bean)
            notifyDataSetChanged()
            mItemClickCallback?.onDelete(bean.moduleid!!)

        }
        bind.edit.setOnClickListener {
            if (routing != null && mItemClickCallback != null) {
                mItemClickCallback?.onEdit(text, routing, bean.moduleid!!)

            }
        }
        bind.config.setOnClickListener {
            //todo 跳转对应配置页面
            if (routing != null && mItemClickCallback != null) {
                mItemClickCallback?.onConfig(text, configRouting, bean.moduleid!!)
            }
        }

    }

    fun setData(list: ArrayList<BuildingFloorItem>) {

        if (mList == null) {
            mList = ArrayList<BuildingFloorItem>()

        }
        mList?.clear()
        mList?.addAll(list)
        notifyDataSetChanged()
    }

    fun getData():ArrayList<BuildingFloorItem>?{
        return mList
    }

    fun addItem(item: BuildingFloorItem) {
        mList?.add(item)
        notifyItemChanged(item.moduleid!!.toInt())
    }

    fun removeItem(item: BuildingFloorItem) {
        mList?.remove(item)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemFloorDetailBinding> = newBindingViewHolder(parent)

    var mItemClickCallback: ItemClickCallback? = null
    fun setItemClickCallback(callback: ItemClickCallback) {
        this.mItemClickCallback = callback
    }

    fun hasSameName(name: String): Boolean {
        if (mList.isNullOrEmpty()) {
            return false
        }
        for (i in 0 until mList!!.size) {
            if (name.equals(mList!!.get(i).name))
                return true
        }
        return false
    }

    interface ItemClickCallback {
        fun onEdit(moduleName: String, routing: String, moduleId: Long);

        fun onConfig(moduleName: String, routing: String, moduleId: Long);

        fun onDelete(moduleId: Long);
    }
}