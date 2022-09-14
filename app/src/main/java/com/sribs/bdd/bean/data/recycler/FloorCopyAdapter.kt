package com.sribs.bdd.bean.data.recycler

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckedTextView
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.common.databinding.ItemCommonSpinnerBinding

/**
 * @date 2021/7/6
 * @author elijah
 * @Description
 */
class FloorCopyAdapter(var mCb: OnCheckedListener):BaseListAdapter<ProjectFloorConfigCopyBean,ItemCommonSpinnerBinding>() {
    override fun init(bind: ItemCommonSpinnerBinding, bean: ProjectFloorConfigCopyBean, pos: Int) {
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
        fun onChecked( bean: ProjectFloorConfigCopyBean, pos: Int,isChecked:Boolean)
    }

    fun setData(l:ArrayList<ProjectFloorConfigCopyBean>){
        mList = l
        notifyDataSetChanged()
    }

    fun setChecked(name:String,b:Boolean){
        var idx = mList?.indexOfFirst { it.name == name }?:return
        if (idx<0)return
        mList!![idx].isChecked = b
        notifyItemChanged(idx)
    }

    fun setChecked(floorIdx:Int){
        mList?.filter { it.id.toInt()!=floorIdx }
            ?.forEach {
                it.isChecked = true
                mCb?.onChecked(it,-1,true)
            }
        notifyDataSetChanged()
    }

    fun getAllSel():ArrayList<ProjectFloorConfigCopyBean>{
        LOG.I("123","getAllSel=${mList}")
        return ArrayList(mList?.filter { it.isChecked })
    }

    fun selAll(b:Boolean){
        LOG.I("leon","FloorCopyAdapter selAll in")
        mList!!.forEachIndexed { index, it ->
            if(it.isChecked!=b) {
                it.isChecked = b
                mCb?.onChecked(it, index, b)
            }
        }
        notifyDataSetChanged()
    }
}