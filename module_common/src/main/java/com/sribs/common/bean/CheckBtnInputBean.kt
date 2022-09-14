package com.sribs.common.bean

import android.text.Editable
import android.text.TextWatcher

/**
 * @date 2021/7/2
 * @author elijah
 * @Description
 */
class CheckBtnInputBean(
    var title:String,var hint:String
):CheckBtnBaseBean() {
    override val type:Int = TYPE_INPUT

    var isSelect = false

    var titleListener = object:TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            title = s.toString()
            if (isSelect != !title.isNullOrEmpty()){
                isSelect = !title.isNullOrEmpty()
                mCb?.onTextSelectChanged()
            }


        }
    }

    interface TextChangedListener{
        fun onTextSelectChanged()

    }

    var mCb:TextChangedListener?=null
}