package com.sribs.bdd.ui.adapter

import android.view.View
import android.view.ViewGroup
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.bdd.R
import com.sribs.bdd.bean.BuildingMainBean
import com.sribs.bdd.bean.RoomItemBean
import com.sribs.bdd.databinding.ItemMainListBinding

/**
 * create time: 2022/9/19
 * author: bruce
 * description:
 */
class BuildingListAdapter (private val mCb: ICallback) :
    BaseListAdapter<BuildingMainBean, ItemMainListBinding>(){

    var mKeyword:String?=null

    var mTime:ArrayList<String>?=null

    var mAll:ArrayList<BuildingMainBean>?=null

    private val icons = listOf(
        R.drawable.ic_main_local,
        R.drawable.ic_main_local_upload,
        R.drawable.ic_main_cloud,
        R.drawable.ic_main_download,
        R.drawable.ic_main_download_config
    )

    override fun init(bind: ItemMainListBinding, beanMain: BuildingMainBean, pos: Int) {
        bind.itemMainTimeTv.text = beanMain.updateTime
        bind.itemMainHasNew.visibility = View.GONE
        bind.itemMainAddressTv.text = beanMain.bldName
        bind.itemMainAddressTv.isSelected = true
        bind.itemMainManagerTv.text = "项目负责人："+beanMain.leader
        bind.itemMainMemberTv.text = "检验员："+beanMain.inspectorName
        bind.itemMainCard.isSelected = beanMain.isCardSel
        bind.itemMainCard.setOnClickListener {
            selCard(bind,pos)
            mCb.onCardSelect(mList!![pos],pos)
        }
        bind.itemMainMoreCb.isChecked = beanMain.isMenuChecked
        bind.itemMainMoreCb.setOnClickListener {
            mList!![pos].isMenuChecked =  !mList!![pos].isMenuChecked
            selCard(bind,pos)
            mCb.onMoreChecked(beanMain,pos,mList!![pos].isMenuChecked)
        }
        var resIdx = bind.root.context.resources.getStringArray(R.array.main_project_status).indexOf(beanMain.status)
        if (resIdx in icons.indices){
            bind.itemMainStatusIv.setImageResource(icons[resIdx])
        }
    }

    private fun selCard(bind:ItemMainListBinding,pos:Int){
        if (mList?.size?:0 <= pos) return
        if (!mList!![pos].isCardSel){
            mList!![pos].isCardSel  = true
            updateOthers(bind,pos,true)
            bind.root.post { notifyItemChanged(pos) }
            LOG.I("123","selCard pos=$pos")
        }
    }

    fun getSpanSize(pos:Int):Int = mList!![pos].spanCount

    fun getSameGroupContentIndexes(index:Int):ArrayList<Int>{
        if (mList==null)return ArrayList()
        var isRoom = mList!![index].houseType==2
        return ArrayList(mList!!.mapIndexed { i, roomItemBean -> Pair(i,roomItemBean)  }
            .filter { it.second.type == RoomItemBean.TYPE_CONTENT &&
                    if(isRoom){
                        it.second.houseType == 2
                    }else{
                        it.second.houseType !=2
                    }
            }.map { it.first })
    }

    private fun updateOthers(bind:ItemMainListBinding,pos:Int,b:Boolean){
        if (b){
            mList?.forEachIndexed { index, projectBean ->
                if (pos!=index && projectBean.isCardSel){
                    if (projectBean.isCardSel || projectBean.isMenuChecked){
                        projectBean.isCardSel = false
                        projectBean.isMenuChecked = false
                        bind.root.post { notifyItemChanged(index) }
                    }
                }
            }
        }
    }

    fun menuUnSel(pos: Int){
        if(mList.isNullOrEmpty())return
        if (pos >= mList!!.size || pos < 0)return
        mList!![pos].isMenuChecked =  false
        notifyItemChanged(pos)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemMainListBinding>  = newBindingViewHolder(parent)


    interface ICallback{
        fun onMoreChecked(beanMain:BuildingMainBean, pos:Int, checked:Boolean)
        fun onCardSelect(beanMain:BuildingMainBean, pos:Int)
    }

    fun setData(l:ArrayList<BuildingMainBean>){
        mAll = ArrayList(l)
        mList = l
        if (!mKeyword.isNullOrEmpty()){
            mList = ArrayList(mAll!!.filter {
                    it.bldName!!.contains(mKeyword!!) })
        }
        if (!mTime.isNullOrEmpty()){
            mList = ArrayList(mAll!!.filter { inTimeRange(it.updateTime!!,mTime!!) })
        }

        notifyDataSetChanged()
    }

    fun getData():ArrayList<BuildingMainBean>?{
        return mList
    }

    fun setSearch(keyword:String?){
        mKeyword = keyword
        if (mAll.isNullOrEmpty())return
        mList = ArrayList(mAll!!.filter {
                it.bldName!!.contains(mKeyword!!) || mKeyword.isNullOrEmpty()})
        notifyDataSetChanged()
    }

    private fun inTimeRange(t:String,range:ArrayList<String>):Boolean{
        if (range.isEmpty())return true
        var b = false
        range.forEach {
            if (t.contains(it)){
                b = true
            }
        }
        return b
    }

}