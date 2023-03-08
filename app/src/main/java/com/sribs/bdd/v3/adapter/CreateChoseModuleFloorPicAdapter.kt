package com.sribs.bdd.v3.adapter

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.bdd.bean.BuildingFloorPictureBean
import com.sribs.bdd.bean.data.ModuleFloorPictureBean
import com.sribs.bdd.databinding.ItemPicSelectBinding
import com.sribs.common.utils.FileUtil

class CreateChoseModuleFloorPicAdapter:BaseListAdapter<ModuleFloorPictureBean,ItemPicSelectBinding>() {

    var mSelected = ArrayList<String>()

    var mContext: Context?= null

    override fun init(bind: ItemPicSelectBinding, bean: ModuleFloorPictureBean, pos: Int) {
            bind.imageName.text = bean.name
            bind.imageCheck.isChecked = bean.chose
            bind.imageCheck.setOnCheckedChangeListener { buttonView, isChecked ->
                bean.chose = isChecked
            }
        bind.imageDelete.setOnClickListener {
            mList?.remove(bean)
            var iterator = mSelected.iterator()
            while (iterator.hasNext()){
                var next = iterator.next()
                var name = FileUtil.uriToFileName(Uri.parse(next), mContext!!)
                if(name.equals(bean.name)){
                    iterator.remove()
                }
            }
            notifyDataSetChanged()
        }
    }

    fun setData(list:ArrayList<ModuleFloorPictureBean>){
        mList = list
    }

    fun setSelected(selected: ArrayList<String>) {
        mSelected = selected
    }

    fun setContext(context:Context){
        mContext = context
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