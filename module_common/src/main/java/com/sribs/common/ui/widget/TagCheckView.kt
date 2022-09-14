package com.sribs.common.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.cbj.sdk.libbase.utils.LOG

import com.sribs.common.bean.CheckBtnBaseBean
import com.sribs.common.bean.CheckBtnNormalBean
//import com.sribs.common.databinding.LayoutCommonTagCheckBinding
import com.sribs.common.ui.adapter.CommonCheckBtnAdapter
import com.sribs.common.R
import com.sribs.common.databinding.LayoutCommonTagCheckBinding

/**
 * @date 2021/7/2
 * @author elijah
 * @Description
 */
class TagCheckView:LinearLayout {

    private var mBinding: LayoutCommonTagCheckBinding?= null

    private var tagText:String?  = null
    private var tagPadding:Int? = null
    private var tagMust:Boolean? = null
    private var tagWidth:Int?=null
    private var checkArr:Int?=null
    private var checkMargin:Int?=null
    private var checkSpan:Int?=null
    private var gravity:Int?=null
    private var isShowCustom:Boolean?=null


    private var checkList:ArrayList<String> = ArrayList()


    private val mAdapter :CommonCheckBtnAdapter by lazy {
        CommonCheckBtnAdapter(isShowCustom?:false)
    }

    fun setCheckedBtnListener(cb :CommonCheckBtnAdapter.OnCheckedChangedListener){
        mAdapter?.setOnCheckedChangedListener(cb)
    }


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mBinding = LayoutCommonTagCheckBinding.inflate(LayoutInflater.from(context),this,true)
        val a = context.obtainStyledAttributes(attrs, R.styleable.TagCheckView)
        tagText = a.getString(R.styleable.TagCheckView_CheckTagText)
        tagPadding = a.getDimensionPixelSize(R.styleable.TagCheckView_CheckTagPadding,-1)
        tagMust = a.getBoolean(R.styleable.TagCheckView_CheckTagMust,false)
        tagWidth = a.getDimensionPixelSize(R.styleable.TagCheckView_CheckTagWidth,-1)
        checkArr = a.getResourceId(R.styleable.TagCheckView_CheckArr,-1)
        checkMargin = a.getDimensionPixelSize(R.styleable.TagCheckView_CheckArrMargin,-1)
        checkSpan = a.getInt(R.styleable.TagCheckView_CheckArrSpan,5)
        gravity = a.getInt(R.styleable.TagCheckView_android_gravity,-1)
        isShowCustom= a.getBoolean(R.styleable.TagCheckView_CheckCustomEnable,false)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        mBinding?.commonTagCheckTag?.text = tagText
        LOG.I("123","tagText=${ mBinding?.commonTagCheckTag?.text}")
        if (tagPadding?:0>0){
            mBinding?.commonTagCheckTag?.setPadding(0,0,tagPadding!!,0)
        }
        mBinding?.commonTagCheckMust?.visibility = if (tagMust == true) View.VISIBLE else View.INVISIBLE
        if (tagWidth?:0>0){
            var p = mBinding?.commonTagCheckLl?.layoutParams as LinearLayout.LayoutParams
            p.width = tagWidth!!
            mBinding?.commonTagCheckLl?.layoutParams = p
        }
        if (checkArr?:0>0){
            var array = mBinding?.root?.context?.resources?.getStringArray(checkArr!!)
            checkList = ArrayList(array!!.toList())
        }
        if (gravity?:0>0){
            mBinding?.commonTagCheckRoot?.gravity = gravity!!
            mBinding?.commonTagCheckLl?.gravity = gravity!! or Gravity.CENTER_VERTICAL
        }


        var lm = if (checkSpan?:0>0){
            GridLayoutManager(context,checkSpan!!)
        }else{
            GridLayoutManager(context,5)
        }

        mBinding?.commonTagCheckRv?.layoutManager = lm
        mBinding?.commonTagCheckRv?.adapter = mAdapter
        if (checkMargin?:0>0){
            mBinding?.commonTagCheckRv?.addItemDecoration(CommonGridDividerItemDecoration(checkMargin!!,checkMargin!!,false))
        }

        setDefaultConfigArr(checkList)

    }

    fun showCustom(b:Boolean,tip:String?=null){
        isShowCustom = b
        mAdapter.bShowCustom = b
        mAdapter.mCustomAddTip = tip
    }

    fun setDefaultConfigArr(l:ArrayList<String>){

        var list = ArrayList<CheckBtnBaseBean>()
        l.forEach {
            list.add(CheckBtnNormalBean(it,false))
        }
        mAdapter.setData(list)
    }




    fun setSelectArr(l:ArrayList<String>){
        mAdapter.selData(ArrayList(l.filter { it.isNotEmpty() }))
    }

    fun setCheckTagText(s:String):TagCheckView{
        mBinding?.commonTagCheckTag?.text = s
        return this
    }

   
}