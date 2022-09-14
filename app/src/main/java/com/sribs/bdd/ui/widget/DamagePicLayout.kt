package com.sribs.bdd.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.cbj.sdk.libbase.utils.LOG
import com.sribs.bdd.R
import com.sribs.bdd.databinding.LayoutDamagePicBinding

/**
 * @date 2021/7/20
 * @author elijah
 * @Description 图形描述
 */
class DamagePicLayout : ConstraintLayout, View.OnClickListener {

    private var mBinding:LayoutDamagePicBinding?=null

    var mWidth = -1

    var mHeight = -1

    var posList:ArrayList<String>?=null

    var mPosType = 0

    var mPicType = 0

    var mMap = HashMap<View,Int>()

    var picList:ArrayList<String>?=null

    var mCb:IClickListener?=null


    constructor(context: Context) : super(context) {
        mBinding = LayoutDamagePicBinding.inflate(LayoutInflater.from(context),this,true)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mBinding = LayoutDamagePicBinding.inflate(LayoutInflater.from(context),this,true)
    }



    override fun onFinishInflate() {
        super.onFinishInflate()
        mBinding!!.layoutDamageRoot.viewTreeObserver.addOnGlobalLayoutListener(object :ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                mWidth = mBinding!!.layoutDamageRoot.width
                mHeight = mBinding!!.layoutDamageRoot.height
                LOG.I("123","w=$mWidth  h=$mHeight")
                mBinding!!.layoutDamageRoot.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })
        mBinding!!.layoutDamageDoor.setOnTouchListener { _, _ -> true }
        mBinding!!.layoutDamageWindow.setOnTouchListener { _, _ -> true }
        mBinding!!.layoutDamageRoot.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN){
                LOG.E("123","x=${event.x}   y=${event.y}")
            }
            true
        }

        mBinding!!.layoutDamageGroup2.root.visibility = View.VISIBLE
        mBinding!!.layoutDamageRoot.setBackgroundResource(R.drawable.ic_damage_pic3)
        initListener()
    }

    fun setPos(pos:ArrayList<String>){
        posList = pos
        if ((pos.contains("顶板")||pos.contains("地坪")) && pos.contains("裂缝")){
            mPosType=4
            mBinding!!.layoutDamageRoot.setBackgroundResource(R.drawable.ic_damage_pic4)
            mBinding!!.layoutDamageGroup1.root.visibility = View.GONE
            mBinding!!.layoutDamageGroup2.root.visibility = View.GONE
            mBinding!!.layoutDamageGroup3.root.visibility = View.GONE
            mBinding!!.layoutDamageGroup4.root.visibility = View.VISIBLE
            mBinding!!.layoutDamageSoutheastNorthwest.visibility = View.VISIBLE
            showSoutheastNorthwestSmallMargin(pos)
        }else if(pos.contains("裂缝")){
            mPosType=1
            mBinding!!.layoutDamageRoot.setBackgroundResource(R.drawable.ic_damage_pic1)
            mBinding!!.layoutDamageGroup1.root.visibility = View.VISIBLE
            mBinding!!.layoutDamageGroup2.root.visibility = View.GONE
            mBinding!!.layoutDamageGroup3.root.visibility = View.GONE
            mBinding!!.layoutDamageGroup4.root.visibility = View.GONE
            mBinding!!.layoutDamageSoutheastNorthwest.visibility = View.GONE
            showSoutheastNorthwestSmallMargin(pos)
        }
        else if(pos.contains("接缝")){
            mPosType=2
//            mBinding!!.layoutDamageRoot.setBackgroundResource(R.drawable.ic_damage_pic2)
            mBinding!!.layoutDamageRoot.setBackgroundResource(R.mipmap.ic_damage_pic2_2)
            mBinding!!.layoutDamageGroup1.root.visibility = View.GONE
            mBinding!!.layoutDamageGroup2.root.visibility = View.GONE
            mBinding!!.layoutDamageGroup3.root.visibility = View.VISIBLE
            mBinding!!.layoutDamageGroup4.root.visibility = View.GONE
            mBinding!!.layoutDamageSoutheastNorthwest.visibility = View.VISIBLE
            showSoutheastNorthwestSmallMargin(pos)
            if (pos.contains("地坪")){
                mBinding!!.layoutDamageGroup3.p1.visibility = View.GONE
                mBinding!!.layoutDamageGroup3.p2.visibility = View.GONE
                mBinding!!.layoutDamageGroup3.p3.visibility = View.GONE
                mBinding!!.layoutDamageGroup3.p4.visibility = View.GONE
            }else{
                mBinding!!.layoutDamageGroup3.p1.visibility = View.VISIBLE
                mBinding!!.layoutDamageGroup3.p2.visibility = View.VISIBLE
                mBinding!!.layoutDamageGroup3.p3.visibility = View.VISIBLE
                mBinding!!.layoutDamageGroup3.p4.visibility = View.VISIBLE
            }
            if(pos[0].contains("墙")){
                mBinding!!.layoutDamageSoutheastNorthwest.visibility = View.GONE
                if (pos.contains("水平接缝")){
                    mBinding!!.layoutDamageGroup3.p1.visibility = View.GONE
                    mBinding!!.layoutDamageGroup3.p3.visibility = View.GONE
                    mBinding!!.layoutDamageGroup3.p6.visibility = View.GONE
                    mBinding!!.layoutDamageGroup3.p2.visibility = View.VISIBLE
                    mBinding!!.layoutDamageGroup3.p4.visibility = View.VISIBLE
                    mBinding!!.layoutDamageGroup3.p5.visibility = View.VISIBLE
                }else if(pos.contains("竖向接缝")){
                    mBinding!!.layoutDamageGroup3.p2.visibility = View.GONE
                    mBinding!!.layoutDamageGroup3.p4.visibility = View.GONE
                    mBinding!!.layoutDamageGroup3.p5.visibility = View.GONE
                    mBinding!!.layoutDamageGroup3.p1.visibility = View.VISIBLE
                    mBinding!!.layoutDamageGroup3.p3.visibility = View.VISIBLE
                    mBinding!!.layoutDamageGroup3.p6.visibility = View.VISIBLE
                }else if(pos.contains("门洞封堵接缝") || pos.contains("窗洞封堵接缝")|| pos.contains("气窗缝")){
                    mBinding!!.layoutDamageGroup3.p1.visibility = View.GONE
                    mBinding!!.layoutDamageGroup3.p2.visibility = View.GONE
                    mBinding!!.layoutDamageGroup3.p3.visibility = View.GONE
                    mBinding!!.layoutDamageGroup3.p4.visibility = View.GONE
                    mBinding!!.layoutDamageGroup3.p5.visibility = View.VISIBLE
                    mBinding!!.layoutDamageGroup3.p6.visibility = View.VISIBLE
                }else{
                    mBinding!!.layoutDamageGroup3.p1.visibility = View.VISIBLE
                    mBinding!!.layoutDamageGroup3.p2.visibility = View.VISIBLE
                    mBinding!!.layoutDamageGroup3.p3.visibility = View.VISIBLE
                    mBinding!!.layoutDamageGroup3.p4.visibility = View.VISIBLE
                    mBinding!!.layoutDamageGroup3.p5.visibility = View.VISIBLE
                    mBinding!!.layoutDamageGroup3.p6.visibility = View.VISIBLE
                }


            }
        }else if(pos.contains("渗水")||pos.contains("水渍")){
            mPosType=3
            mBinding!!.layoutDamageRoot.setBackgroundResource(R.drawable.ic_damage_pic3)
            mBinding!!.layoutDamageGroup1.root.visibility = View.GONE
            mBinding!!.layoutDamageGroup2.root.visibility = View.VISIBLE
            mBinding!!.layoutDamageGroup3.root.visibility = View.GONE
            mBinding!!.layoutDamageGroup4.root.visibility = View.GONE
            mBinding!!.layoutDamageSoutheastNorthwest.visibility = View.GONE
            showSoutheastNorthwestSmallMargin(pos)
        }else {
            mPosType=0
            mBinding!!.layoutDamageRoot.setBackgroundResource(R.drawable.ic_damage_pic3)
            mBinding!!.layoutDamageGroup1.root.visibility = View.GONE
            mBinding!!.layoutDamageGroup2.root.visibility = View.VISIBLE
            mBinding!!.layoutDamageGroup3.root.visibility = View.GONE
            mBinding!!.layoutDamageGroup4.root.visibility = View.GONE
            mBinding!!.layoutDamageSoutheastNorthwest.visibility = View.GONE
            showSoutheastNorthwestSmallMargin(pos)
        }
        when {
            pos.contains("有窗") -> {
                mBinding!!.layoutDamageWindow.visibility = View.VISIBLE
                mBinding!!.layoutDamageDoor.visibility = View.GONE
            }
            pos.contains("有门") -> {
                mBinding!!.layoutDamageWindow.visibility = View.GONE
                mBinding!!.layoutDamageDoor.visibility = View.VISIBLE
            }
            else -> {
                mBinding!!.layoutDamageWindow.visibility = View.GONE
                mBinding!!.layoutDamageDoor.visibility = View.GONE
            }
        }
        initListener()
    }
    private fun showSoutheastNorthwestSmallMargin(pos:ArrayList<String>){
        mBinding!!.layoutDamageSoutheastNorthwest2.visibility =
            if ((pos.contains("顶板") || pos.contains("地坪")) &&!pos.contains("裂缝") && !pos.contains("接缝")) View.VISIBLE
            else View.GONE
    }


    fun setPic(pic:ArrayList<String>){
//        LOG.I("123","pic=$pic")
        picList = pic
        mMap.forEach { (k, v) ->
//            LOG.I("123","k,v=$k   $v")
            if (pic.contains("$v")){
//                LOG.I("123","set green  $v    $pic")
                (k as ImageView).setImageResource(R.drawable.ic_bk_damage_point_green)


            }else{
//                LOG.E("123","set gray  $v  $pic")
                (k as ImageView).setImageResource(R.drawable.ic_bk_damage_point_gray)
            }
        }

    }

    fun setPicSel(pic:ArrayList<String>){

        mMap.forEach { (k, v) ->
            if (pic!!.contains("$v")){
                (k as ImageView).setImageResource(R.drawable.ic_bk_damage_point_red_ring)
            }else{
            }
        }
    }

    fun cleanPic(){
        mMap.forEach { (k, v) ->
            (k as ImageView).setImageResource(R.drawable.ic_bk_damage_point_gray)
        }
    }

    private fun initListener(){
        mMap.clear()
        if (mBinding!!.layoutDamageGroup1.root.visibility == View.VISIBLE) {
            mBinding!!.layoutDamageGroup1.root.children.forEachIndexed { i, view ->
                if (view is ConstraintLayout) {
                    var count = view.childCount
                    view.children.forEachIndexed { j, v ->
                        mMap[v] = i * count + j
                        v.setOnClickListener(this)
                    }
                }
            }
        }
        if (mBinding!!.layoutDamageGroup2.root.visibility == View.VISIBLE) {
            mBinding!!.layoutDamageGroup2.root.children.forEachIndexed { i, view ->
                if (view is ConstraintLayout) {
                    var count = view.childCount
                    view.children.forEachIndexed { j, v ->
                        mMap[v] = i * count + j
                        v.setOnClickListener(this)
                    }
                }
            }
        }
        if (mBinding!!.layoutDamageGroup3.root.visibility == View.VISIBLE) {
            mBinding!!.layoutDamageGroup3.root.children.forEachIndexed { i, v ->
                mMap[v] = i
                v.setOnClickListener(this)
            }
        }
        if (mBinding!!.layoutDamageGroup4.root.visibility == View.VISIBLE) {
            mBinding!!.layoutDamageGroup4.root.children.forEachIndexed { i, view ->
                if (view is ConstraintLayout) {
                    view.children.forEachIndexed { j, v ->
                        mMap[v] = i + j
                        v.setOnClickListener(this)
                    }
                }
            }
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }

    override fun onClick(v: View?) {
        var index = mMap[v]
        mCb?.onClick("$index")
        LOG.I("123","onClick index=$index")
    }

    interface IClickListener{
        fun onClick(picIndex:String)
    }
}