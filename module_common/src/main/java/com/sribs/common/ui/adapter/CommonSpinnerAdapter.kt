package com.sribs.common.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatCheckedTextView
import com.cbj.sdk.libbase.utils.LOG

import com.sribs.common.R
import com.sribs.common.bean.HistoryBean
import com.sribs.common.utils.TimeUtil

/**
 * @date 2021/6/30
 * @author elijah
 * @Description
 */
class CommonSpinnerAdapter<T>(var mContext: Context, var list:Array<T>, @LayoutRes var layoutRes:Int,
                           var mCb:AdapterView.OnItemSelectedListener):BaseAdapter() {

    var mLocalTime:String?=null

    var mIsSingle = false

    override fun getCount(): Int = list?.size

    override fun getItem(position: Int): T = list!![position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v = convertView ?: LayoutInflater.from(mContext).inflate(layoutRes,parent,false)
        var ctv = v!!.findViewById<AppCompatCheckedTextView>(R.id.item_ctv)
        var newTv = v!!.findViewById<TextView>(R.id.item_new_tv)
        var b = getItem(position)
        when(b){
            is HistoryBean->{
                ctv.text = (getItem(position) as HistoryBean).let {
                    newTv.visibility = if (!mLocalTime.isNullOrEmpty() && TimeUtil.isBefore(mLocalTime!!,it.createTime?:"")){
                         View.VISIBLE
                    }else{
                         View.INVISIBLE
                    }
                    it.createTime+" "+it.userName
                }
                ctv.isChecked = b.isCheck
            }
            is String->{
                ctv.text = b
                newTv?.visibility = View.GONE
            }
        }
        ctv.setOnClickListener {
            if (b is HistoryBean){
                list.forEachIndexed { index, it ->
                    (it as HistoryBean).isCheck = false
                    if (index == position){
                        it.isCheck = !ctv.isChecked
                    }
                }
            }

            ctv.isChecked = !ctv.isChecked
            LOG.I("123","isCheck=${ctv.isChecked}")
            mCb.onItemSelected(null,v,position,getItemId(position))
            notifyDataSetChanged()
        }
        return v!!
    }

    fun notifyUpdate(){
        notifyDataSetChanged()
    }

}