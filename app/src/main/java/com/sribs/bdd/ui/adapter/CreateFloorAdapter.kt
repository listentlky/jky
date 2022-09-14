package com.sribs.bdd.ui.adapter

import android.text.Editable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.bdd.bean.BuildingFloorBean
import com.sribs.bdd.databinding.ItemFloorListBinding
import com.sribs.common.ui.widget.TagEditView


class CreateFloorAdapter(var iCallback: ICallback?):BaseListAdapter<BuildingFloorBean,ItemFloorListBinding>() {



    override fun init(bind: ItemFloorListBinding, bean: BuildingFloorBean, pos: Int) {
        bind.floorName.getEditText().hint = bean.name
        bind.floorName.setTextCallback(object :TagEditView.ITextChanged{
            override fun onTextChange(s: Editable?) {
                if (s!=null&&s.toString()!=""){
                    bean.name = s.toString()
                }

            }

        })

        var adapter = ShowFloorPictureAdapter()
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
            iCallback?.showWiite(bean)
        }
    }

    fun setData(li:ArrayList<BuildingFloorBean>){
        mList = li
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemFloorListBinding> = newBindingViewHolder(parent)

    interface ICallback{
        fun chosePic(bean: BuildingFloorBean)
        fun takePhoto(bean: BuildingFloorBean)
        fun showWiite(bean: BuildingFloorBean)
    }
}