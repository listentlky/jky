package com.sribs.bdd.v3.adapter

import android.content.Context
import android.text.Editable
import android.view.ContextMenu
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.bdd.bean.BuildingFloorBean
import com.sribs.bdd.bean.data.ModuleFloorBean
import com.sribs.bdd.bean.data.ModuleFloorPictureBean
import com.sribs.bdd.databinding.ItemFloorListBinding
import com.sribs.bdd.databinding.ItemNonResidentListBinding
import com.sribs.common.ui.widget.TagEditView


class CreateNonResidentModuleAdapter(var iCallback: ICallback?,var context: Context?):BaseListAdapter<ModuleFloorBean,ItemNonResidentListBinding>() {

    var picList= ArrayList<ModuleFloorPictureBean>()



    override fun init(bind: ItemNonResidentListBinding, bean: ModuleFloorBean, pos: Int) {
        bind.floorName.getEditText().hint = bean.name
        bind.floorName.setTextCallback(object :TagEditView.ITextChanged{
            override fun onTextChange(s: Editable?) {
                if (s!=null&&s.toString()!=""){
                    bean.name = s.toString()
                }

            }

        })

        var adapter = ShowModuleFloorPictureAdapter()
        if (bean.pictureList==null|| bean.pictureList!!.size==0){
        }else{
            bind.recyclerView.layoutManager = LinearLayoutManager(context)
            adapter.setData(bean.pictureList!!)
            bind.recyclerView.adapter = adapter
            bind.recyclerView.visibility = View.VISIBLE

        }

        bind.showimages.setOnClickListener {
            if (bind.recyclerView.visibility==View.VISIBLE){
                bind.recyclerView.visibility = View.GONE
            }else{
                if (bean.pictureList==null|| bean.pictureList!!.size==0){
                    return@setOnClickListener
                }
                bind.recyclerView.layoutManager = LinearLayoutManager(it.context)
                adapter.setData(bean.pictureList!!)
                bind.recyclerView.adapter = adapter
                bind.recyclerView.visibility = View.VISIBLE
            }
        }
       /* bind.delete.setOnClickListener {
            bean.pictureList!!.clear()
            adapter.notifyDataSetChanged()
            *//* mList?.remove(bean)
             notifyDataSetChanged()*//*
            iCallback?.deleteModuleFloor(bean,pos)
        }*/

        bind.chosePic.setOnClickListener {
            iCallback?.choseNonResidentPic(bean)

        }

        bind.takePhoto.setOnClickListener {
            iCallback?.takeNonResidentPhoto(bean)


           /* if (bind.recyclerView.visibility==View.VISIBLE){
                bind.recyclerView.visibility = View.GONE
            }else{
                if (bean.pictureList==null|| bean.pictureList!!.size==0){
                    return@setOnClickListener
                }
                bind.recyclerView.layoutManager = LinearLayoutManager(it.context)
                adapter.setData(bean.pictureList!!)
                bind.recyclerView.adapter = adapter
                bind.recyclerView.visibility = View.VISIBLE
            }*/

        }

        bind.choseWhite.setOnClickListener {
            iCallback?.showNonResidentWhite(bean)
        }
    }
    fun setData(list:ArrayList<ModuleFloorBean>){
        mList = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ItemNonResidentListBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.setIsRecyclable(false)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemNonResidentListBinding> = newBindingViewHolder(parent)

    interface ICallback{
        fun choseNonResidentPic(bean: ModuleFloorBean)
        fun takeNonResidentPhoto(bean: ModuleFloorBean)
        fun showNonResidentWhite(bean: ModuleFloorBean)
    }
}