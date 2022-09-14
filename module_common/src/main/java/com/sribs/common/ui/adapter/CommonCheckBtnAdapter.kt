package com.sribs.common.ui.adapter

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.cbj.sdk.libui.mvp.BindingViewHolder
import com.cbj.sdk.libui.mvp.adapter.BaseListAdapter
import com.cbj.sdk.libui.mvp.newBindingViewHolder
import com.sribs.common.bean.CheckBtnAddBean
import com.sribs.common.bean.CheckBtnBaseBean
import com.sribs.common.bean.CheckBtnInputBean
import com.sribs.common.bean.CheckBtnNormalBean
import com.sribs.common.databinding.ItemCommonAddBinding
import com.sribs.common.databinding.ItemCommonBtnBinding
import com.sribs.common.databinding.ItemCommonEtBinding

/**
 * @date 2021/7/2
 * @author elijah
 * @Description
 */
class CommonCheckBtnAdapter(var bShowCustom:Boolean,var mCb:OnCheckedChangedListener?=null): BaseListAdapter<CheckBtnBaseBean,ViewBinding>() {

    var mCustomAddTip:String?=null

    interface OnCheckedChangedListener{
        fun onCheckedChanged(all:ArrayList<String>)
    }

    fun setOnCheckedChangedListener(cb:OnCheckedChangedListener){
        mCb = cb
    }

    override fun getItemViewType(position: Int): Int  = mList!![position].type?:0

    override fun init(bind: ViewBinding, bean: CheckBtnBaseBean, pos: Int) {
        when(bean.type){
            CheckBtnBaseBean.TYPE_BTN-> initBtn(bind as ItemCommonBtnBinding,bean as CheckBtnNormalBean,pos)
            CheckBtnBaseBean.TYPE_INPUT->initInput(bind as ItemCommonEtBinding,bean as CheckBtnInputBean,pos)
            CheckBtnBaseBean.TYPE_ADD->initAdd(bind as ItemCommonAddBinding,bean as CheckBtnAddBean,pos)
        }
    }

    private fun initBtn(vb:ItemCommonBtnBinding,b:CheckBtnNormalBean,pos:Int){

        vb.commonBtn.text = b.title
        vb.commonBtn.isChecked = b.isChecked
        vb.commonBtn.isEnabled = b.enable


        vb.commonBtn.setOnClickListener {
            if (mList!![pos] is CheckBtnNormalBean){
                var bean = mList!![pos] as CheckBtnNormalBean
                bean.isChecked = !bean.isChecked
                checkBtnDisable(bean)
                mCb?.onCheckedChanged(getAllSel())
            }

        }
    }

    private fun checkBtnDisable(clickBtn:CheckBtnNormalBean){
        when(clickBtn.title){
            "无人"->{
                enableOtherBtn(listOf("不让进","未发现明显损伤"),false,!clickBtn.isChecked)
            }
            "不让进"->{
                enableOtherBtn(listOf("无人","未发现明显损伤"),false,!clickBtn.isChecked)
            }
            "未发现明显损伤"->{
                enableOtherBtn(listOf("不让进","无人"),false,!clickBtn.isChecked)
            }
        }
    }

    private fun enableOtherBtn(titles:List<String>,isChecked:Boolean,enable:Boolean){

        titles.forEach { t->
            var pos = mList!!.indexOfFirst {
                if (it is CheckBtnNormalBean){
                    it.title == t
                }else{
                    false
                }
            }
            if (pos==null || pos<0) {
                //do nothing
            }else{
                if (mList!![pos] is CheckBtnNormalBean){
                    var other = mList!![pos] as CheckBtnNormalBean
                    other.isChecked = isChecked
                    other.enable = enable
                    notifyItemChanged(pos)
                }
            }
        }
    }


    private fun initInput(vb:ItemCommonEtBinding,b:CheckBtnInputBean,pos:Int){
        if (!bShowCustom)return
        mList!!.filter { it.type == CheckBtnBaseBean.TYPE_INPUT }.forEach {
            var inputBean = it as CheckBtnInputBean
            vb.commonEt.removeTextChangedListener(inputBean.titleListener)

        }
        vb.commonEt.hint = b.hint
        var idx = -1
        if(vb.commonEt.text.toString() == b.title){
            idx = vb.commonEt.selectionStart
        }else if(vb.commonEt.text.isNullOrEmpty() && !b.title.isNullOrEmpty()){
            idx = b.title.length
        }
        vb.commonEt.setText(b.title)
        if(idx >=0){
            vb.commonEt.setSelection(idx) //设置光标位置
        }
        vb.commonEt.isSelected = b.isSelect
        vb.commonEt.addTextChangedListener(b.titleListener)
        b.mCb = object :CheckBtnInputBean.TextChangedListener{
            override fun onTextSelectChanged() {
                notifyItemChanged(pos)
                mCb?.onCheckedChanged(getAllSel())
            }
        }
    }

    fun getAllSel():ArrayList<String>{
        return ArrayList(mList?.filter {
            when (it) {
                is CheckBtnNormalBean -> it.isChecked
                is CheckBtnInputBean -> it.isSelect
                else -> false
            }
        }?.map {
            when(it){
                is CheckBtnNormalBean -> it.title
                is CheckBtnInputBean -> it.title
                else -> ""
            }
        })
    }


    private fun initAdd(vb:ItemCommonAddBinding,b:CheckBtnAddBean,pos:Int){
        if (!bShowCustom)return
        if (!b.mTip.isNullOrEmpty()) vb.commonAdd.text = b.mTip
        vb.commonAdd.setOnClickListener {
            addCustom("")
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ViewBinding> {
        return when(viewType){
            CheckBtnBaseBean.TYPE_BTN-> newBindingViewHolder<ItemCommonBtnBinding>(parent) as BindingViewHolder<ViewBinding>
            CheckBtnBaseBean.TYPE_INPUT-> newBindingViewHolder<ItemCommonEtBinding>(parent)as BindingViewHolder<ViewBinding>
            CheckBtnBaseBean.TYPE_ADD-> newBindingViewHolder<ItemCommonAddBinding>(parent) as BindingViewHolder<ViewBinding>
            else-> newBindingViewHolder(parent)
        }
    }
    private fun addCustom(s:String){
        if (!bShowCustom)return
        var idx = mList!!.size-1
        var b = CheckBtnInputBean(s,"请输入")
        if (!s.isNullOrEmpty()) b.isSelect = true
        mList!!.add(idx,b)
        notifyItemInserted(idx)
        notifyDataSetChanged()
    }
    fun setData(l:ArrayList<CheckBtnBaseBean>){
        mList = l
        if (bShowCustom){
            mList!!.add(CheckBtnAddBean(mCustomAddTip))
        }
        notifyDataSetChanged()
    }

    fun selData(l:ArrayList<String>){
        var isChanged = false
        mList!!.filter {
            it.type == CheckBtnBaseBean.TYPE_BTN
        }.filter {
            l.contains( (it as CheckBtnNormalBean).title)
        }.map {
            if (!(it as CheckBtnNormalBean).isChecked){
               it.isChecked = true
                isChanged = true
            }
        }
        if (isChanged) notifyDataSetChanged()
        var configList = mList!!.filter {
            it.type == CheckBtnBaseBean.TYPE_BTN ||
                    it.type == CheckBtnBaseBean.TYPE_INPUT
        }.map {
            when(it){
                is CheckBtnNormalBean -> it.title
                is CheckBtnInputBean  -> it.title
                else -> "error"
            }
        }
        l.filter {
            !configList.contains(it)
        }.forEach {
            addCustom(it)
        }

        var pos = mList!!.indexOfFirst {
            if (it is CheckBtnNormalBean){
                (it.title == "无人" && it.isChecked) || (it.title == "不让进" && it.isChecked)
                        || (it.title == "未发现明显损伤" && it.isChecked)
            }else{
                false
            }
        }
        if (pos >=0 ){
            checkBtnDisable(mList!![pos] as CheckBtnNormalBean)
        }
    }


}