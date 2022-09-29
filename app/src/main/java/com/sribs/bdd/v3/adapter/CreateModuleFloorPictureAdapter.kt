package com.sribs.bdd.v3.adapter

import android.text.Editable
import android.view.ViewGroup
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.bdd.bean.BuildingFloorPictureBean
import com.sribs.bdd.bean.data.ModuleFloorBean
import com.sribs.bdd.bean.data.ModuleFloorPictureBean
import com.sribs.bdd.databinding.ItemFloorPicBinding
import com.sribs.common.ui.widget.TagEditView

class CreateModuleFloorPictureAdapter :
    BaseListAdapter<ModuleFloorPictureBean, ItemFloorPicBinding>() {

    override fun init(bind: ItemFloorPicBinding, bean: ModuleFloorPictureBean, pos: Int) {
        bind.pictureName.setEditText(bean.name)
        bind.pictureName.setTextCallback(object : TagEditView.ITextChanged {
            override fun onTextChange(s: Editable?) {
                if (s != null && s.toString() != "") {
                    bean.name = s.toString()
                }
            }

        })

        bind.delete.setOnClickListener {
            mList?.remove(bean)
            notifyDataSetChanged()
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemFloorPicBinding> = newBindingViewHolder(parent)


    fun setData(list: ArrayList<ModuleFloorPictureBean>) {
        mList = list
        notifyDataSetChanged()
    }
}