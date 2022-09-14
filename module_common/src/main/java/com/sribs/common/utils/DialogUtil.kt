package com.sribs.common.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.LayoutRes
import androidx.core.view.children
import com.cbj.sdk.libbase.utils.LOG
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import com.sribs.common.R
import com.sribs.common.ui.adapter.CommonSpinnerAdapter
import java.util.concurrent.TimeUnit

/**
 * @date 2021/6/25
 * @author elijah
 * @Description
 */
object DialogUtil {
    fun showBottomDialog(c: Context, @LayoutRes layoutId:Int,showStatusBtn:Boolean, cb: (Int)->Unit): Dialog {
        var dialog = BottomSheetDialog(c)
        var v = LayoutInflater.from(c).inflate(layoutId, null)
        var ll = v.findViewById<LinearLayout>(R.id.bottom_ll)
        ll.children.forEachIndexed { index, v ->
            if(!showStatusBtn  && v.id == R.id.bottom_status_btn){
                v.visibility = View.GONE
            }
            v.setOnClickListener {
                Observable.timer(200,TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        dialog.dismiss()
                        cb(index)
                    }
            }
        }
        dialog.setCancelable(true)
        dialog.setOnDismissListener {
            LOG.I("123","onDismiss")
            cb(-1)
        }
        dialog.setContentView(v)
        (v.parent as ViewGroup).setBackgroundResource(R.color.transparent)
        dialog.show()
        return dialog
    }


    fun showMsgDialog(c:Context, msg:String, okCb:()->Unit, cancelCb: (() -> Unit)? =null){
        var dialog = AlertDialog.Builder(c,R.style.msgDialog).create()
        var v = LayoutInflater.from(c).inflate(R.layout.dialog_common_msg,null)
        var msgTv = v.findViewById<TextView>(R.id.dialog_msg_tv)
        msgTv?.text = msg
        var cancelBtn = v.findViewById<Button>(R.id.dialog_cancel_btn)
        cancelBtn.setOnClickListener {
            dialog?.dismiss()
            cancelCb?.invoke()
        }
        if (cancelCb == null){
            cancelBtn.visibility = View.GONE
        }

        var okBtn = v.findViewById<Button>(R.id.dialog_confirm_btn)
        okBtn.setOnClickListener {
            okCb()
            dialog?.dismiss()
        }
        dialog?.setView(v)
        dialog?.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    fun showMsgDialog(c:Context, msg:String,okStr:String,cancelStr:String, okCb:()->Unit, cancelCb: (() -> Unit)? =null){
        var dialog = AlertDialog.Builder(c,R.style.msgDialog).create()
        var v = LayoutInflater.from(c).inflate(R.layout.dialog_common_msg,null)
        var msgTv = v.findViewById<TextView>(R.id.dialog_msg_tv)
        msgTv?.text = msg
        var cancelBtn = v.findViewById<Button>(R.id.dialog_cancel_btn)
        cancelBtn.text = cancelStr
        cancelBtn.setOnClickListener {
            dialog?.dismiss()
            cancelCb?.invoke()
        }
        var okBtn = v.findViewById<Button>(R.id.dialog_confirm_btn)
        okBtn.text = okStr
        okBtn.setOnClickListener {
            okCb()
            dialog?.dismiss()
        }
        dialog?.setView(v)
        dialog?.setCanceledOnTouchOutside(true)
        dialog.show()
    }


    fun showSpinnerDialog(c:Context,arr:Array<String>,cb: (Int) -> Unit):Dialog{
        var dialog = AlertDialog.Builder(c,R.style.msgDialog).create()
        var v = LayoutInflater.from(c).inflate(R.layout.dialog_common_spinner,null)
        var listview = v.findViewById<ListView>(R.id.dialog_list)
        listview.adapter = CommonSpinnerAdapter(c,arr,R.layout.item_common_spinner,
            object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Observable.timer(500,TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            cb(position)
                        }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            })
        dialog?.setView(v)
        dialog?.setCanceledOnTouchOutside(true)
        dialog.show()
        return dialog
    }

}