package com.sribs.bdd.ui.adapter

import android.view.ViewGroup

import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.bdd.R
import com.sribs.bdd.bean.BuildingModule
import com.sribs.bdd.databinding.ItemFloorDetailBinding
import com.sribs.bdd.v3.util.LogUtils


class FloorItemAdapter : BaseListAdapter<BuildingModule, ItemFloorDetailBinding>() {

    private val icons = listOf(
        R.drawable.ic_main_local,
        R.drawable.ic_main_local_upload,
        R.drawable.ic_main_cloud,
        R.drawable.ic_main_download,
        R.drawable.ic_main_download_config
    )

    override fun init(bind: ItemFloorDetailBinding, bean: BuildingModule, pos: Int) {

        var routing: String? = null
        var configRouting: String =
            com.sribs.common.ARouterPath.CHECK_MODULE_CONFIG_TYPE_BUILDING_ACTIVITY
        var text: String
        when (bean.moduleName) {
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
                configRouting = com.sribs.common.ARouterPath.CHECK_MODULE_CONFIG_TYPE_FLOOR_ACTIVITY
            }
            "居民类检测测量" -> {
                text = "居民类检测测量"
                routing = com.sribs.common.ARouterPath.BLD_DAMAGE_LIST_ATY
            }
            "非居民类检测测量" -> {
                text = "非居民类检测测量"
                routing = com.sribs.common.ARouterPath.BLD_DRW_DMG_DIAL_REC_FGT
            }
            else -> {
                text = ""
            }
        }

        bind.status.setImageResource(icons[bean.status])

        bind.name.text = text
        bind.time.text = bean.createTime
        bind.more.setOnClickListener {
            mItemClickCallback?.onMore(bean)
        }
        bind.edit.setOnClickListener {
            if (routing != null && mItemClickCallback != null) {
                mItemClickCallback?.onEdit(text, routing, bean.moduleid!!,bean.status == 2)
            }
        }
        bind.config.setOnClickListener {
            //todo 跳转对应配置页面
            if (routing != null && mItemClickCallback != null) {
                mItemClickCallback?.onConfig(text, configRouting, bean.moduleid!!,bean.status==2)
            }
        }

    }

    fun setData(list: ArrayList<BuildingModule>) {
        if (mList == null) {
            mList = ArrayList<BuildingModule>()

        }
        mList?.clear()
        mList?.addAll(list)
        notifyDataSetChanged()
    }

    fun getData():ArrayList<BuildingModule>?{
        return mList
    }

    fun addItem(item: BuildingModule) {
        mList?.add(item)
        notifyItemChanged(item.moduleid!!.toInt())
    }

    fun removeItem(item: BuildingModule) {
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
            if (name.equals(mList!!.get(i).moduleName))
                return true
        }
        return false
    }

    interface ItemClickCallback {
        fun onEdit(moduleName: String, routing: String, moduleId: Long,isLocal:Boolean);

        fun onConfig(moduleName: String, routing: String, moduleId: Long,isLocal:Boolean);

        fun onMore(bean: BuildingModule);
    }
}