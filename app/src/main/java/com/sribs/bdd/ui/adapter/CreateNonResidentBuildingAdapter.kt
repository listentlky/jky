package com.sribs.bdd.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.bdd.bean.BuildingFloorBean
import com.sribs.bdd.databinding.ItemNonResidentListBinding


class CreateNonResidentBuildingAdapter(var iCallback: ICallback?, var context: Context?):BaseListAdapter<BuildingFloorBean,ItemNonResidentListBinding>() {


    override fun init(bind: ItemNonResidentListBinding, bean: BuildingFloorBean, pos: Int) {
        bind.floorName.getEditText().setHint(bean.name)
       /* bind.floorName.setTextCallback(object :TagEditView.ITextChanged{
            override fun onTextChange(s: Editable?) {
                if (s!=null&&s.toString()!=""){
                   bean.name = s.toString()
                }

            }

        })*/

        var adapter = ShowFloorPictureAdapter()


        bind.recyclerView.layoutManager = LinearLayoutManager(context)
        adapter.setData(bean.pictureList!!)
        bind.recyclerView.adapter = adapter
        bind.recyclerView.visibility = View.VISIBLE

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

    fun setData(li:ArrayList<BuildingFloorBean>){
        mList = li
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
        fun choseNonResidentPic(bean: BuildingFloorBean)
        fun takeNonResidentPhoto(bean: BuildingFloorBean)
        fun showNonResidentWhite(bean: BuildingFloorBean)
    }

}