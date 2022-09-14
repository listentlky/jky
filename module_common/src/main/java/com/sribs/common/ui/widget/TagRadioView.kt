package com.sribs.common.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import com.sribs.common.R
import com.sribs.common.databinding.LayoutCommonTagRadioBinding

/**
 * @date 2021/7/1
 * @author elijah
 * @Description
 */
class TagRadioView:LinearLayout {

    private var mBinding: LayoutCommonTagRadioBinding?=null

    private var tagText:String?  = null
    private var tagPadding:Int? = null
    private var tagMust:Boolean? = null
    private var tagWidth:Int? = null
    private var editWidth:Int? = null
    private var gravity:Int?   = null
    private var radioArr:Int?  = null
    private var radioMargin:Int? = null
    private var radioCheckedIdx:Int? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mBinding = LayoutCommonTagRadioBinding.inflate(LayoutInflater.from(context),this,true)
        val a = context.obtainStyledAttributes(attrs, R.styleable.TagRadioView)
        tagText = a.getString(R.styleable.TagRadioView_RadioTagText)
        tagPadding = a.getDimensionPixelSize(R.styleable.TagRadioView_RadioTagPadding,0)
        tagMust = a.getBoolean(R.styleable.TagRadioView_RadioTagMust,false)
        tagWidth = a.getDimensionPixelSize(R.styleable.TagRadioView_RadioTagWidth,-1)
        editWidth = a.getDimensionPixelSize(R.styleable.TagRadioView_RadioGroupWidth,-1)
        gravity = a.getInt(R.styleable.TagRadioView_android_gravity,-1)
        radioArr = a.getResourceId(R.styleable.TagRadioView_RadioArr,-1)
        radioMargin = a.getDimensionPixelSize(R.styleable.TagRadioView_RadioMargin,-1)
        radioCheckedIdx = a.getInt(R.styleable.TagRadioView_RadioCheckedIdx,-1)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mBinding?.commonTagTag?.text = tagText
        if (tagPadding?:0>0){
            mBinding?.commonTagTag?.setPadding(0,0,tagPadding!!,0)
        }
        mBinding?.commonTagMust?.visibility = if (tagMust == true) View.VISIBLE else View.INVISIBLE
        if (tagWidth?:0>0){
            var p = mBinding?.commonTagRadioLl?.layoutParams as LinearLayout.LayoutParams
            p.width = tagWidth!!
            p.weight = 0f
            mBinding?.commonTagRadioLl?.layoutParams = p
        }

        if (gravity?:0>0){
            mBinding?.commonTagRadioRoot?.gravity = gravity!!
            mBinding?.commonTagRadioLl?.gravity = gravity!! or Gravity.CENTER_VERTICAL
        }
        if (editWidth?:0>0){
            var p = mBinding?.tagRadioGroup?.layoutParams as LinearLayout.LayoutParams
            p.width = editWidth!!
            p.weight = 0f
            mBinding?.tagRadioGroup?.layoutParams = p
        }
        if (radioArr?:0>0){
            var arr = mBinding?.root?.resources?.getStringArray(radioArr!!)
            if (arr?.size?:0>1){
                mBinding?.tagRadio1?.text = arr!![0]
                mBinding?.tagRadio2?.text = arr!![1]
            }
        }
        if (radioMargin?:0>0){
            mBinding?.tagRadio1?.setPadding(0,0,radioMargin!!,0)
        }
        if (radioCheckedIdx?:0>0){
            var id = when(radioCheckedIdx){
                1-> mBinding?.tagRadio1?.id
                2-> mBinding?.tagRadio2?.id
                else -> -1
            }
            if (id?:0>0){

                mBinding?.tagRadioGroup?.check(id!!)
            }
        }

        mBinding?.tagRadioGroup?.setOnCheckedChangeListener { group, checkedId ->
            var radio = mBinding!!.root.findViewById<RadioButton>(checkedId)
            mCheckCb?.onChecked(radio.text.toString())
            mCheckCbs?.forEach {
                it.onChecked(radio.text.toString())
            }
        }

    }
    private var mCheckCbs:ArrayList<IRadioChecked>?=null
    private var mCheckCb:IRadioChecked?=null
    interface IRadioChecked{
        fun onChecked(s:String)
    }

    fun addRadioListener(cb:IRadioChecked):TagRadioView{
        if (mCheckCbs==null)mCheckCbs = ArrayList()
        mCheckCbs!!.add(cb)
        return this
    }

    fun clearRadioListener(){
        mCheckCb = null
        mCheckCbs?.clear()
    }

    fun setRadioListener(cb:IRadioChecked):TagRadioView{
        mCheckCb = cb
        return this
    }

    fun setChecked(s:String):TagRadioView{
        var id = when {
            mBinding?.tagRadio1?.text == s -> {
                mBinding?.tagRadio1?.id
            }
            mBinding?.tagRadio2?.text == s -> {
                mBinding?.tagRadio2?.id
            }
            else -> -1
        }
        if (id?:0 > 0){
            mBinding?.tagRadioGroup?.check(id!!)
        }
        return this
    }

    fun setRadioText(s1:String,s2:String):TagRadioView{
        mBinding?.tagRadio1?.text = s1
        mBinding?.tagRadio2?.text = s2
        return this
    }

    fun setTagText(s:String):TagRadioView{
        mBinding?.commonTagTag?.text = s
        return this
    }
}