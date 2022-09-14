package com.sribs.bdd.ui.adapter

import android.view.View
import android.view.ViewGroup
import com.cbj.sdk.libbase.utils.LOG
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.bdd.R
import com.sribs.bdd.bean.MainProjectBean
import com.sribs.bdd.databinding.ItemMainListBinding

/**
 * @date 2021/6/23
 * @author elijah
 * @Description
 */
class MainListAdapter(private val mCb:ICallback) :BaseListAdapter<MainProjectBean,ItemMainListBinding>(){

    var mKeyword:String?=null

    var mTime:ArrayList<String>?=null

    var mAll:ArrayList<MainProjectBean>?=null

    private val icons = listOf(
        R.drawable.ic_main_local,
        R.drawable.ic_main_local_upload,
        R.drawable.ic_main_cloud,
        R.drawable.ic_main_download,
        R.drawable.ic_main_download_config
    )


    override fun init(bind: ItemMainListBinding, beanMain: MainProjectBean, pos: Int) {
        bind.itemMainTimeTv.text = beanMain.updateTimeYMD
        bind.itemMainHasNew.visibility = if (beanMain.hasNewer) View.VISIBLE else View.GONE
        bind.itemMainAddressTv.text = beanMain.address
        bind.itemMainAddressTv.isSelected = true
        bind.itemMainManagerTv.text = "项目负责人："+beanMain.leader
        bind.itemMainMemberTv.text = "检验员："+beanMain.inspector
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
        fun onMoreChecked(beanMain:MainProjectBean, pos:Int, checked:Boolean)
        fun onCardSelect(beanMain:MainProjectBean, pos:Int)
    }

    fun setData(l:ArrayList<MainProjectBean>){
        mAll = ArrayList(l)
        mList = l
        if (!mKeyword.isNullOrEmpty()){
            mList = ArrayList(mAll!!.filter { it.leader.contains(mKeyword!!) ||
                    it.address.contains(mKeyword!!) })
        }
        if (!mTime.isNullOrEmpty()){
            mList = ArrayList(mAll!!.filter { inTimeRange(it.updateTimeYMD,mTime!!) })
        }

        notifyDataSetChanged()
    }

    fun setSearch(keyword:String?){
        mKeyword = keyword
        if (mAll.isNullOrEmpty())return
        mList = ArrayList(mAll!!.filter { it.leader.contains(mKeyword!!) ||
                it.address.contains(mKeyword!!) || mKeyword.isNullOrEmpty()})
        notifyDataSetChanged()
    }

    fun setSearch(time:ArrayList<String>){
        mTime = time
        if (mAll.isNullOrEmpty())return
        mList = ArrayList(mAll!!.filter { inTimeRange(it.updateTimeYMD,time) })
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