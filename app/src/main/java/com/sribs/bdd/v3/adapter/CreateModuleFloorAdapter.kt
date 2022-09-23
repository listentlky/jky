package com.sribs.bdd.v3.adapter

import android.text.Editable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.bdd.bean.data.ModuleFloorBean
import com.sribs.bdd.bean.data.ModuleFloorPictureBean
import com.sribs.bdd.databinding.ItemFloorListBinding
import com.sribs.common.ui.widget.TagEditView


class CreateModuleFloorAdapter(var iCallback: ICallback?):BaseListAdapter<ModuleFloorBean,ItemFloorListBinding>() {

    var picList= ArrayList<ModuleFloorPictureBean>()


    override fun init(bind: ItemFloorListBinding, bean: ModuleFloorBean, pos: Int) {
        bind.floorName.getEditText().hint = bean.name
        bind.floorName.setTextCallback(object :TagEditView.ITextChanged{
            override fun onTextChange(s: Editable?) {
                if (s!=null&&s.toString()!=""){
                    bean.name = s.toString()
                }

            }

        })
        var adapter = ShowModuleFloorPictureAdapter()
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
        bind.delete.setOnClickListener {
            bean.pictureList!!.clear()
            adapter.notifyDataSetChanged()
            mList?.remove(bean)
            notifyDataSetChanged()
        }

        bind.chosePic.setOnClickListener {
            iCallback?.chosePic(bean)

        }

        bind.takePhoto.setOnClickListener {
            iCallback?.takePhoto(bean)

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

        bind.choseWhite.setOnClickListener {
            iCallback?.showWhite(bean)
        }
    }
    fun setData(list:ArrayList<ModuleFloorBean>){
        mList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemFloorListBinding> = newBindingViewHolder(parent)

    interface ICallback{
        fun chosePic(bean: ModuleFloorBean)
        fun takePhoto(bean: ModuleFloorBean)
        fun showWhite(bean: ModuleFloorBean)
    }
}