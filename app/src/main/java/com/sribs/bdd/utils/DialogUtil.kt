package com.sribs.bdd.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.sribs.bdd.R
import com.sribs.common.bean.HistoryBean
import com.sribs.common.bean.V3VersionBean
import com.sribs.common.ui.adapter.CommonSpinnerAdapter

/**
 * @date 2021/6/28
 * @author elijah
 * @Description
 */
object DialogUtil {

    fun showAllInfoDialog(c: Context,title:String,all:String,finish:String,inaccessible:String,noBody:String,pct:String) {
        var dialog = AlertDialog.Builder(c, R.style.msgDialog).create()
        var v = LayoutInflater.from(c).inflate(R.layout.dialog_all_info,null)
        var closeBtn = v.findViewById<ImageView>(R.id.dialog_close)
        closeBtn.setOnClickListener { dialog?.dismiss() }
        v.findViewById<TextView>(R.id.dialog_title).text = title
        v.findViewById<TextView>(R.id.dialog_all_tv).text = all
        v.findViewById<TextView>(R.id.dialog_finish_tv).text = finish
        v.findViewById<TextView>(R.id.dialog_inasscee_tv).text = inaccessible
        v.findViewById<TextView>(R.id.dialog_nobody_tv).text = noBody
        v.findViewById<TextView>(R.id.dialog_all_per_tv).text = pct
        dialog?.setView(v)
        dialog?.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    fun showDownloadV3ProjectDialog(c: Context,title:String?=null,localTime:String,items:Array<V3VersionBean>,cb:(List<Int>)->Unit){
        var dialog = AlertDialog.Builder(c, R.style.msgDialog).create()
        var v = LayoutInflater.from(c).inflate(R.layout.dialog_download_project,null)
        var selSet = HashSet<Int>()
        if (!title.isNullOrEmpty()){
            v.findViewById<TextView>(R.id.dialog_title).text = title
        }

        v.findViewById<Button>(R.id.dialog_cancel_btn).setOnClickListener {
            dialog?.dismiss()
        }
        v.findViewById<Button>(R.id.dialog_confirm_btn).setOnClickListener {
            cb(selSet.toList())
            dialog?.dismiss()
        }
        var listView = v.findViewById<ListView>(R.id.dialog_list)
        listView.adapter = CommonSpinnerAdapter(c,items,R.layout.item_common_spinner,
            object :AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (selSet.contains(position)){
                        selSet.remove(position)
                    }else{
                        selSet.clear()
                        selSet.add(position)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }).also {
            it.mLocalTime = localTime
        }
        dialog?.setView(v)
        dialog?.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    fun showDownloadProjectDialog(c: Context,title:String?=null,localTime:String,items:Array<HistoryBean>,cb:(List<Int>)->Unit){
        var dialog = AlertDialog.Builder(c, R.style.msgDialog).create()
        var v = LayoutInflater.from(c).inflate(R.layout.dialog_download_project,null)
        var selSet = HashSet<Int>()
        if (!title.isNullOrEmpty()){
            v.findViewById<TextView>(R.id.dialog_title).text = title
        }

        v.findViewById<Button>(R.id.dialog_cancel_btn).setOnClickListener {
            dialog?.dismiss()
        }
        v.findViewById<Button>(R.id.dialog_confirm_btn).setOnClickListener {
            cb(selSet.toList())
            dialog?.dismiss()
        }
        var listView = v.findViewById<ListView>(R.id.dialog_list)
        listView.adapter = CommonSpinnerAdapter(c,items,R.layout.item_common_spinner,
        object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (selSet.contains(position)){
                    selSet.remove(position)
                }else{
                    selSet.clear()
                    selSet.add(position)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }).also {
            it.mLocalTime = localTime
        }
        dialog?.setView(v)
        dialog?.setCanceledOnTouchOutside(true)
        dialog.show()
    }

}