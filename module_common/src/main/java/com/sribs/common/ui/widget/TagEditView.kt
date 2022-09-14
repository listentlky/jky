package com.sribs.common.ui.widget

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.cbj.sdk.libbase.utils.LOG
import com.sribs.common.R
import com.sribs.common.databinding.LayoutCommonTagEtBinding
import com.sribs.common.utils.DialogUtil
import java.util.*

/**
 * @date 2021/6/21
 * @author elijah
 * @Description
 */
class TagEditView: ConstraintLayout {

    private var mBinding:LayoutCommonTagEtBinding?=null


    private var tagText:String?  = null
    private var tagPadding:Int? = null
    private var tagMust:Boolean? = null
    private var tagWidth:Int?=null
    private var editText:String? = null
    private var editHint:String? = null
    private var editEnable:Boolean ?= null
    private var editWidth:Int? = null
    private var editBtnEnable:Boolean? = null
    private var editBtnBackground:Int? = null
    private var inputType:Int? = null
    private var maxLen:Int?    = null
    private var gravity:Int?   = null
    private var editBtnSize:Int? = null
    private var isSpinner:Boolean?=null
    private var isEditSpinner:Boolean?=null
    private var isDate:Boolean?=null
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mBinding = LayoutCommonTagEtBinding.inflate(LayoutInflater.from(context),this,true)
        val a = context.obtainStyledAttributes(attrs, R.styleable.TagEditView)
        tagText = a.getString(R.styleable.TagEditView_TagText)
        tagPadding = a.getDimensionPixelSize(R.styleable.TagEditView_TagPadding,0)
        tagMust = a.getBoolean(R.styleable.TagEditView_TagMust,false)
        tagWidth = a.getDimensionPixelSize(R.styleable.TagEditView_TagWidth,-1)
        editText = a.getString(R.styleable.TagEditView_EditText)
        editHint = a.getString(R.styleable.TagEditView_EditHint)
        editEnable = a.getBoolean(R.styleable.TagEditView_EditEnable,true)
        editWidth = a.getDimensionPixelSize(R.styleable.TagEditView_EditWidth,-1)
        editBtnEnable = a.getBoolean(R.styleable.TagEditView_EditBtnEnable,false)
        editBtnBackground = a.getResourceId(R.styleable.TagEditView_EditBtnBackground,-1)
        editBtnSize = a.getDimensionPixelSize(R.styleable.TagEditView_EditBtnSize,-1)
        isSpinner = a.getBoolean(R.styleable.TagEditView_EditIsSpinner,false)
        isEditSpinner = a.getBoolean(R.styleable.TagEditView_EditIsEditSpinner,false)
        isDate = a.getBoolean(R.styleable.TagEditView_EditIsDate,false)
        inputType = a.getInt(
            R.styleable.TagEditView_android_inputType,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        )
        gravity = a.getInt(R.styleable.TagEditView_android_gravity,-1)


        maxLen = a.getInt(R.styleable.TagEditView_android_maxLength,0)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mBinding?.commonTagEtTag?.text = tagText
        if (tagPadding?:0>0){
            mBinding?.commonTagEtTag?.setPadding(0,0,tagPadding!!,0)
        }
        if (tagWidth?:0>0){
            var p = mBinding?.commonTagLl?.layoutParams as LinearLayout.LayoutParams
            p.width = tagWidth!!
            mBinding?.commonTagLl?.layoutParams = p
        }

        mBinding?.commonTagEtMust?.visibility = if (tagMust == true) View.VISIBLE else View.INVISIBLE
        if (!editText.isNullOrEmpty()){
            mBinding?.commonTagEtEt?.setText(editText)
        }
        if (!editHint.isNullOrEmpty()){
            mBinding?.commonTagEtEt?.hint = editHint
        }
        if (editEnable==true){
            mBinding?.commonTagEtEt?.isEnabled = true
            mBinding?.commonTagEtEt?.addTextChangedListener(object:TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
//                    LOG.I("123","tagEt  start=$start  count=$count   after=$after")
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    mTextCb?.onTextChange(s)
                }
            })
        }else{
            mBinding?.commonTagEtEt?.isEnabled = false
        }


        if (editBtnEnable==true){
            mBinding?.commonTagEtCb?.visibility =  View.VISIBLE
            mBinding?.commonTagEtCb?.setOnCheckedChangeListener { _, b ->
                mCheckBoxCb?.onCheckedChange(mBinding?.commonTagEtEt!!,b)
                LOG.I("123","cb click  isEditSpinner=$isEditSpinner  isSpinner=$isSpinner")
                if (isSpinner==true || isEditSpinner==true){
                    showSpinnerDialog()
                }
                if (isDate==true){
                    showDateDialog()
                }
            }
        }else{
            mBinding?.commonTagEtCb?.visibility = View.GONE
        }
        if (editBtnBackground?:0>0){
            mBinding?.commonTagEtCb?.setBackgroundResource(editBtnBackground!!)
        }
        if (editBtnSize?:0>0){
            var p = mBinding?.commonTagEtCb?.layoutParams as LinearLayout.LayoutParams
            p.width = editBtnSize!!
            p.height = editBtnSize!!
            mBinding?.commonTagEtCb?.layoutParams = p
        }

        if (inputType!=null){
            mBinding?.commonTagEtEt?.setSingleLine()
            mBinding?.commonTagEtEt?.inputType = inputType!!
        }
        if (maxLen?:0>0){
            mBinding?.commonTagEtEt?.filters = arrayOf<InputFilter>(LengthFilter(maxLen!!))
        }
        if (gravity?:0>0){
            mBinding?.commonTagEtRoot?.gravity = gravity!!
            mBinding?.commonTagLl?.gravity = gravity!! or Gravity.CENTER_VERTICAL
        }
        if (editWidth?:0>0){
            var p = mBinding?.commonTagEtLl?.layoutParams as LinearLayout.LayoutParams
            p.width = editWidth!!
            p.weight = 0f
            LOG.E("123","editWidth =$editWidth")
            mBinding?.commonTagEtLl?.layoutParams = p
        }
        if (isSpinner == true){
            mBinding?.commonTagEtEt?.setOnTouchListener { v, event ->
                showSpinnerDialog()
                true
            }
        }

        if (isDate == true){
            mBinding?.commonTagEtEt?.setOnTouchListener { v, event ->
                showDateDialog()
                true
            }
        }

    }

    var mCheckBoxCb:ICheckBoxChanged?=null
    var mTextCb:ITextChanged?=null
    fun setCheckBoxCallback(cb:ICheckBoxChanged):TagEditView{
        if (editBtnEnable==false) return this
        mCheckBoxCb = cb
        return this
    }
    fun setTextCallback(cb:ITextChanged):TagEditView{
        mTextCb = cb
        return this
    }

    interface ICheckBoxChanged{
        fun onCheckedChange(et:EditText,isChecked:Boolean)
    }

    interface ITextChanged{
        fun onTextChange(s:Editable?)
    }

    fun setEditText(s:String):TagEditView{
        if (editEnable == false)return this
        if (mBinding?.commonTagEtEt?.text?.toString()?:"" == s)return this
        var old = mBinding?.commonTagEtEt?.text?.toString()
        mBinding?.commonTagEtEt?.setText(s)
        if (old?.startsWith("0") == true && s==old.substring(1)){
            mBinding?.commonTagEtEt?.setSelection(s.length)
        }
        return this
    }
    fun getEditText():EditText{
        return mBinding?.commonTagEtEt!!
    }

    private var mSpinnerArr:Array<String>?=null

    private var mDialog : Dialog?=null

    private fun showSpinnerDialog(){
        if (isSpinner!=true && isEditSpinner!=true){
            LOG.E("123","isSpinner=$isSpinner  isEditSpinner=$isEditSpinner")
            return
        }
        if (mSpinnerArr.isNullOrEmpty()){
            LOG.E("123","mSpinnerArr isEmpty")
            Toast.makeText(mBinding!!.root.context,"没有可选项",Toast.LENGTH_LONG).show()
            return
        }
        if (mDialog?.isShowing == true){
            LOG.E("123","isShowing")
            return
        }
        mDialog = DialogUtil.showSpinnerDialog(mBinding!!.root.context,mSpinnerArr!!){
            var str = mSpinnerArr!![it]
            mBinding!!.commonTagEtEt.setText(str)
            mDialog?.dismiss()
        }
    }

    private val calendar = Calendar.getInstance()
    private fun showDateDialog(){
        if (isDate!=true)return
        if (mDialog?.isShowing == true)return
        mDialog = DatePickerDialog(mBinding!!.root.context, { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR,year)
            calendar.set(Calendar.MONTH,month)
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
//            mBinding?.commonTagEtEt?.setText(String.format("%04d-%02d-%02d",year,month+1,dayOfMonth))
            mBinding?.commonTagEtEt?.setText(String.format("%04d-%02d",year,month+1))
        },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        ).also {
            it.show()
        }
    }

    fun setSpinnerEntries(arr:Array<String>?){
        mSpinnerArr = arr
    }

    fun setTagText(s:String):TagEditView{
        mBinding?.commonTagEtTag?.text = s
        return this
    }
}