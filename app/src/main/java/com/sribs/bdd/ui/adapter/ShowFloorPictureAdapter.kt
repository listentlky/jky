package com.sribs.bdd.ui.adapter

import android.text.Editable
import android.view.View
import android.view.ViewGroup
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.bdd.bean.BuildingFloorPictureBean
import com.sribs.bdd.databinding.ItemFloorPicBinding
import com.sribs.common.ui.widget.TagEditView

class ShowFloorPictureAdapter:BaseListAdapter<BuildingFloorPictureBean,ItemFloorPicBinding>() {

    override fun init(bind: ItemFloorPicBinding, bean: BuildingFloorPictureBean, pos: Int) {
        bind.pictureName.setEditText("图片名"+bean.name)
        bind.pictureName.setTextCallback(object :TagEditView.ITextChanged{
            override fun onTextChange(s: Editable?) {
                if (s!=null&&s.toString()!=""){
                    bean.name = s.toString()
                }
            }

        })

        bind.delete.visibility = View.GONE


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemFloorPicBinding> = newBindingViewHolder(parent)


    fun setData(list:ArrayList<BuildingFloorPictureBean>){
        mList = list
        notifyDataSetChanged()
    }
}