package com.sribs.bdd.v3.adapter

import android.view.ViewGroup
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.bdd.bean.BuildingFloorPictureBean
import com.sribs.bdd.bean.data.ModuleFloorPictureBean
import com.sribs.bdd.databinding.ItemPicSelectBinding

class CreateChoseModuleFloorPicAdapter:BaseListAdapter<ModuleFloorPictureBean,ItemPicSelectBinding>() {


    override fun init(bind: ItemPicSelectBinding, bean: ModuleFloorPictureBean, pos: Int) {
            bind.imageName.text = bean.name
            bind.imageCheck.isChecked = bean.chose
            bind.imageCheck.setOnCheckedChangeListener { buttonView, isChecked ->
                bean.chose = isChecked
            }
    }

    fun setData(list:ArrayList<ModuleFloorPictureBean>){
        mList = list
    }


    fun choseAll(){
        mList?.forEach {
            it.chose = true
        }
        notifyDataSetChanged()
    }

    fun choseNone(){
        mList?.forEach {
            it.chose = false
        }
        notifyDataSetChanged()
    }


    fun getChosedList():ArrayList<ModuleFloorPictureBean>{
        var chosed = ArrayList<ModuleFloorPictureBean>()

        mList?.forEach {
            if (it.chose){
                chosed.add(it)
            }
        }
        return chosed
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemPicSelectBinding> = newBindingViewHolder(parent)

    interface ICallback{
        fun choseImage(boolean: Boolean,bean: BuildingFloorPictureBean)
    }
}