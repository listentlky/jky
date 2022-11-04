package com.sribs.bdd.v3.view

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.forEachIndexed
import com.sribs.bdd.R
import com.sribs.bdd.v3.module.CheckMenuModule
import com.sribs.common.bean.db.DamageV3Bean

/**
 * create time: 2022/9/5
 * author: bruce
 * description:
 */
class CheckMenuView2 : LinearLayout {

    private val MENU_TYPE: Int? = 0
    private val ITEM_TYPE: Int? = 1
    private val MARK_TYPE: Int? = 2
    private var menuModuleList: ArrayList<CheckMenuModule>? = ArrayList()
    private var mContext: Context? = null
    private var mIsAdd = true

    constructor(context: Context) : super(context) {
        this.mContext = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.mContext = context
    }


    fun setMenuModuleList(menuList: ArrayList<CheckMenuModule>): CheckMenuView2 {
        this.menuModuleList = menuList
        return this
    }

    fun setIsAdd(isAdd:Boolean): CheckMenuView2 {
        this.mIsAdd = isAdd
        return this
    }

    fun build() {
        removeAllViews()
        mContext ?: context
        orientation = VERTICAL
        menuModuleList!!.forEachIndexed { index, it ->
            addView(addItemView(MENU_TYPE, it.name, null, ""))
            addView(addLine())

            var scrollView = ScrollView(mContext)
            scrollView.overScrollMode = OVER_SCROLL_NEVER
            addView(scrollView)

            var layout = LinearLayout(mContext)
            layout.orientation = VERTICAL
            scrollView.addView(layout)

            it.menu!!.forEachIndexed { index, b ->
                layout.addView(addItemView(ITEM_TYPE, b.name, null, b.name))
                layout.addView(addLine())
                b.item!!.forEachIndexed { index, c ->
                    layout.addView(addItemView(MARK_TYPE, c.name, c.damage, b.name))
                    layout.addView(addLine())
                }
            }
        }
    }

    private fun addItemView(
        type: Int?,
        name: String?,
        damage: DamageV3Bean?,
        damageType: String?
    ): View? {
        when (type) {
            MENU_TYPE -> {
                val tv = TextView(mContext)
                tv.text = name
                tv.gravity = Gravity.CENTER_VERTICAL
                tv.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    mContext!!.resources.getDimension(R.dimen._10ssp)
                )
                tv.setTextColor(Color.parseColor("#FFFFFF"))
                val tvParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    mContext!!.resources.getDimensionPixelOffset(R.dimen._26sdp)
                )
                tvParams.leftMargin = mContext!!.resources.getDimensionPixelOffset(R.dimen._6sdp)
                tv.layoutParams = tvParams
                return tv
            }
            ITEM_TYPE -> {
                val itemLayout = RelativeLayout(mContext)
                val padding = context.resources.getDimensionPixelOffset(R.dimen._12sdp)
                itemLayout.setPadding(padding, 0, padding / 2, 0)
                val itemLayoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    mContext!!.resources.getDimensionPixelOffset(R.dimen._22sdp)
                )

                val tv2 = TextView(mContext)
                tv2.text = name
                tv2.gravity = Gravity.CENTER_VERTICAL
                tv2.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    context.resources.getDimension(R.dimen._8ssp)
                )
                tv2.setTextColor(Color.parseColor("#FFFFFF"))
                val tv2Params = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                tv2Params.addRule(RelativeLayout.CENTER_VERTICAL)
                itemLayout.addView(tv2, tv2Params)

                if(mIsAdd) {

                    val tv3 = TextView(mContext)
                    tv3.text = "+"
                    tv3.gravity = Gravity.CENTER_VERTICAL
                    tv3.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        mContext!!.resources.getDimension(R.dimen._8ssp)
                    )
                    tv3.setTextColor(Color.parseColor("#FFFFFF"))
                    val tv3Params = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    tv3Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    tv3Params.addRule(RelativeLayout.CENTER_VERTICAL)
                    itemLayout.addView(tv3, tv3Params)

                    itemLayout.setOnClickListener { v ->
                        if (mCheckMenuCallback != null)
                            mCheckMenuCallback!!.onClick(v, damageType)
                    }
                }

                itemLayout.layoutParams = itemLayoutParams

                return itemLayout
            }
            MARK_TYPE -> {
             /*   val tv4 = TextView(mContext)
                tv4.text = name
                tv4.gravity = Gravity.CENTER_VERTICAL
                tv4.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    mContext!!.resources.getDimension(R.dimen._8ssp)
                )
                tv4.setTextColor(Color.parseColor("#FFFFFF"))
                val tv4Params = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    mContext!!.resources.getDimensionPixelOffset(R.dimen._22sdp)
                )
                tv4Params.leftMargin = mContext!!.resources.getDimensionPixelOffset(R.dimen._18sdp)
                tv4.layoutParams = tv4Params
                tv4.setOnClickListener { v ->
                    if (mCheckMenuCallback != null)
                        mCheckMenuCallback!!.onMarkClick(v, damage, damageType)
                }
                return tv4
                */

                val itemLayout = RelativeLayout(mContext)
                val padding = context.resources.getDimensionPixelOffset(R.dimen._18sdp)
                itemLayout.setPadding(padding, 0, 0, 0)
                val itemLayoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    mContext!!.resources.getDimensionPixelOffset(R.dimen._22sdp)
                )

                val tv2 = TextView(mContext)
                tv2.setLines(2)
                tv2.ellipsize = TextUtils.TruncateAt.END
                tv2.text = name
                tv2.gravity = Gravity.CENTER_VERTICAL
                tv2.setPadding(0,0,padding,0)
                tv2.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    context.resources.getDimension(R.dimen._7ssp)
                )
                tv2.setTextColor(Color.parseColor("#FFFFFF"))
                val tv2Params = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                tv2Params.addRule(RelativeLayout.CENTER_VERTICAL)
                itemLayout.addView(tv2, tv2Params)

                val tv3 = TextView(mContext)
                tv3.text = "-"
                tv3.gravity = Gravity.CENTER
                tv3.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    mContext!!.resources.getDimension(R.dimen._8ssp)
                )
                tv3.setPadding(padding, 0, padding / 2, 0)
                tv3.setTextColor(Color.parseColor("#FFFFFF"))
                val tv3Params = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                tv3Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                tv3Params.addRule(RelativeLayout.CENTER_VERTICAL)
                itemLayout.addView(tv3, tv3Params)
                tv3.setOnClickListener { v->
                        mCheckMenuCallback!!.onMarkRemoveClick(v,damage, damageType)
                }

                itemLayout.layoutParams = itemLayoutParams
                itemLayout.setOnClickListener { v ->
                        mCheckMenuCallback!!.onMarkClick(v, damage, damageType)
                }
                return itemLayout

            }
        }
        return null
    }

    private fun addLine(): View? {
        val line = View(mContext)
        line.setBackgroundColor(Color.parseColor("#FFFFFF"))
        val lineParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            2
        )
        line.layoutParams = lineParams
        return line
    }


    private var mCheckMenuCallback: CheckMenuCallback? = null

    fun setCheckMenuCallback(mCheckMenuCallback: CheckMenuCallback?): CheckMenuView2? {
        this.mCheckMenuCallback = mCheckMenuCallback
        return this
    }

    interface CheckMenuCallback {
        fun onClick(v: View?, damageType: String?)

        fun onMarkClick(v: View?, damage: DamageV3Bean?, damageType: String?)

        fun onMarkRemoveClick(v: View?, damage: DamageV3Bean?, damageType: String?)

    }

}
