package com.sribs.bdd.ui.adapter

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckedTextView
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.bdd.bean.ProjectUnitConfigCopyBean
import com.sribs.common.databinding.ItemCommonSpinnerBinding

/**
 * @date 2021/7/6
 * @author elijah
 * @Description
 */
class ConfigCopyAdapter(var mCb:OnCheckedListener):BaseListAdapter<ProjectUnitConfigCopyBean,ItemCommonSpinnerBinding>() {
    override fun init(bind: ItemCommonSpinnerBinding, bean: ProjectUnitConfigCopyBean, pos: Int) {
        bind.itemCtv.text = bean.name
        bind.itemCtv.isChecked = bean.isChecked
        bind.itemCtv.setOnClickListener {
            bind.itemCtv.isChecked = !bind.itemCtv.isChecked
            mList!![pos].isChecked = bind.itemCtv.isChecked
            var isChecked = (it as AppCompatCheckedTextView).isChecked
            mCb.onChecked(bean,pos,isChecked)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemCommonSpinnerBinding> = newBindingViewHolder(parent)

    interface OnCheckedListener{
        fun onChecked( bean: ProjectUnitConfigCopyBean, pos: Int,isChecked:Boolean)
    }

    fun setData(l:ArrayList<ProjectUnitConfigCopyBean>){
        mList = l
        notifyDataSetChanged()
    }

    fun setChecked(name:String,b:Boolean){
        var idx = mList?.indexOfFirst { it.name == name }?:return
        if (idx<0)return
        mList!![idx].isChecked = b
        notifyItemChanged(idx)
    }

    fun setChecked(floorIdx:Int, neighborIdx:Int){
        mList?.filter { it.bean?.floorIdx!=floorIdx && it.bean?.neighborIdx == neighborIdx }
            ?.forEach {
                it.isChecked = true
                mCb?.onChecked(it,-1,true)
            }
        notifyDataSetChanged()
    }

    fun getAllSel():ArrayList<ProjectUnitConfigCopyBean>{
        LOG.I("123","getAllSel=${mList}")
        return ArrayList(mList?.filter { it.isChecked })
    }

    fun selAll(b:Boolean){
        mList!!.forEachIndexed { index, it ->
            if(it.isChecked!=b) {
                it.isChecked = b
                mCb?.onChecked(it, index, b)
            }
        }
        notifyDataSetChanged()
    }
}