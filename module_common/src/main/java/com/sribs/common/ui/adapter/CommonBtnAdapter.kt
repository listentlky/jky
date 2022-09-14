package com.sribs.common.ui.adapter

import android.view.ViewGroup
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.common.bean.CommonBtnBean
import com.sribs.common.databinding.ItemCommonBtnBinding

/**
 * @date 2021/6/25
 * @author elijah
 * @Description
 */
open class CommonBtnAdapter(private val isSingle:Boolean):BaseListAdapter<CommonBtnBean, ItemCommonBtnBinding>() {

    override fun init(bind: ItemCommonBtnBinding, bean: CommonBtnBean, pos: Int) {
        if (mCustomItemBackground!=null)
            bind.commonBtn.setBackgroundResource(mCustomItemBackground!!)
        if (mCustomTextColor!=null)
            bind.commonBtn.setTextColor(bind.root.context.getColor(mCustomTextColor!!))
        bind.commonBtn.isEnabled = bean.enable
        bind.commonBtn.text = bean.title
        bind.commonBtn.isChecked = bean.isSel
        bind.commonBtn.setOnClickListener {
            mList!![pos].isSel = !mList!![pos].isSel
            if (isSingle &&  mList!![pos].isSel){
                mList!!.forEachIndexed { index, commonBtnBean ->
                    if (index!=pos && commonBtnBean.isSel){
                        commonBtnBean.isSel = false
                        bind.root.post { notifyItemChanged(index) }
                    }
                }
            }
            mCb?.onClick(mList!![pos])
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemCommonBtnBinding> = newBindingViewHolder(parent)

    fun cancel(){
        mList?.forEach { it.isSel = false }
        notifyDataSetChanged()
    }

    fun updateView(){
        notifyDataSetChanged()
    }

    fun getAllSel():ArrayList<CommonBtnBean>{
        return ArrayList(mList?.filter { it.isSel })
    }

    fun setData(l:ArrayList<CommonBtnBean>?){
        mList = l
        notifyDataSetChanged()
    }

    fun setSelect(s:String){
        if (mList.isNullOrEmpty())return
        var idx = mList!!.indexOfFirst { it.title == s }
        if(idx>=0) {
            mList!!.forEach {
                it.isSel = false
            }
            mList!![idx].isSel = true
            notifyDataSetChanged()
        }
    }


    fun enable(b:Boolean){
        mList?.forEach {
            it.enable = b
        }
        notifyDataSetChanged()
    }

    /**
     * @Description
     */
    var mCb :OnClickListener?=null

    /**
     * @Description 按钮背景
     */
    var mCustomItemBackground:Int?=null

    /**
     * @Description 字体颜色
     */
    var mCustomTextColor:Int?=null

    interface OnClickListener{
        fun onClick(b:CommonBtnBean)
    }
}