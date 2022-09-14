package com.sribs.common.bean

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import com.cbj.sdk.libbase.utils.LOG
import com.google.gson.Gson
import com.sribs.common.ui.adapter.CommonCheckBtnAdapter
import com.sribs.common.ui.widget.TagCheckView
import com.sribs.common.ui.widget.TagEditView
import com.sribs.common.ui.widget.TagRadioView
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties

/**
 * @date 2021/6/22
 * @author elijah
 * @Description
 */
abstract class BaseDataBean {

    protected val fieldMap: HashMap<String,View> = HashMap()

    protected val callbackMap: HashMap<String,(Array<out String>)->Unit> = HashMap()

    private var mCb: com.sribs.common.bean.BaseDataBean.OnDataChangedListener?=null

    protected fun <T>onDataChange(d:KProperty<*>,old: T, new: T){
        if (old == new) {
            return
        }
        var v = fieldMap[d.name]
        if (v!=null && v is TagEditView){
            v.setEditText(new as String)
        }
        if (v!=null && v is TagRadioView){
            v.setChecked(new as String)
        }
        if (v!=null && v is TagCheckView){
            v.setSelectArr(ArrayList((new as String).split(",")))
        }
        if (v!=null && v is EditText){
            v.setText(new as String)
            LOG.I("123","old=$old  new=$new")

//            v.setSelection(new.length)
//            v.setSelection(Util.stringCompare(old as String,new))
        }
        mCb?.onChanged(d.name,new as String)
    }

    fun bindTagEditView(prop:KProperty<*>,view:TagEditView): com.sribs.common.bean.BaseDataBean {
        fieldMap[prop.name] = view
        view.setTextCallback(object :TagEditView.ITextChanged{
            override fun onTextChange(s: Editable?) {
                val reflectProp  = getThis()::class.memberProperties.find { it.name == prop.name}
                if (reflectProp is KMutableProperty<*>){
                    reflectProp.setter.call(getThis(),s.toString())
                }
            }
        })
        return this
    }

    fun bindTagRadioView(prop:KProperty<*>,view: TagRadioView): com.sribs.common.bean.BaseDataBean {
        fieldMap[prop.name] = view
        view.setRadioListener(object :TagRadioView.IRadioChecked{
            override fun onChecked(s: String) {
                val reflectProp  = getThis()::class.memberProperties.find { it.name == prop.name}
                if (reflectProp is KMutableProperty<*>){
                    reflectProp.setter.call(getThis(),s)
                }
            }
        })
        return this
    }

    fun bindTagCheckView(prop:KProperty<*>,view: TagCheckView): com.sribs.common.bean.BaseDataBean {
        fieldMap[prop.name] = view
        view.setCheckedBtnListener(object:CommonCheckBtnAdapter.OnCheckedChangedListener{
            override fun onCheckedChanged(all: ArrayList<String>) {
                val s = all.joinToString(",")
                val reflectProp  = getThis()::class.memberProperties.find { it.name == prop.name}
                if (reflectProp is KMutableProperty<*>){
                    reflectProp.setter.call(getThis(),s)
                }
            }
        })
        return this
    }

    fun bindEditText(prop:KProperty<*>,view:EditText): com.sribs.common.bean.BaseDataBean {
        fieldMap[prop.name] = view
        view.addTextChangedListener(object:TextWatcher{
            var idx = 0//修改后光标位置
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                LOG.I("123","beforeTextChanged start=$start  count=$count  after=$after")
//                idx = after
                if(start==0 && count == after){ // 添加整个字段

                }else if(count > 0 && after ==0){ // 删除
                    idx = start
                }else if(count==0 && after>0){ //添加字段
                    idx = start+after
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                LOG.I("123" ,"onTextChanged  start=$start  before=$before count=$count")

            }

            override fun afterTextChanged(s: Editable?) {
                val reflectProp  = getThis()::class.memberProperties.find { it.name == prop.name}
                if (reflectProp is KMutableProperty<*>){
                    reflectProp.setter.call(getThis(),s.toString())
                    view.setSelection(idx)
                }
            }

        })
        return this
    }

    fun bindCheckBox(prop:KProperty<*>,view:CheckBox): com.sribs.common.bean.BaseDataBean {
        fieldMap[prop.name] = view
        view.setOnCheckedChangeListener { buttonView, isChecked ->
            var s = if(isChecked) "1" else "0"
            val reflectProp  = getThis()::class.memberProperties.find { it.name == prop.name}
            if (reflectProp is KMutableProperty<*>){
                reflectProp.setter.call(getThis(),s)
            }
        }
        return this
    }

    fun bindImageView(prop:KProperty<*>,view:ImageView): com.sribs.common.bean.BaseDataBean {
        fieldMap[prop.name] = view
        return this
    }

    fun bindCallback(method:String,cb:(Array<out String>)->Unit){
        callbackMap[method] = cb
    }

    abstract fun getThis(): com.sribs.common.bean.BaseDataBean

    open fun isEmpty():Boolean{
        return false
    }

    open fun toJsonStr():String{
        if (isEmpty())return ""
        var map = HashMap<String,String>()
        this::class.declaredMemberProperties.forEach {
            LOG.I("123","prop name= ${it.name}  ${    it.getter.call(getThis())}")
            var value =   it.getter.call(getThis())
            if (value is String){
                map[it.name] = value
            }
        }
        LOG.I("123", "toJsonStr map=$map")
        return Gson().toJson(map)
    }

    open fun parseJsonStr(jsonStr:String): com.sribs.common.bean.BaseDataBean {
        if (jsonStr.isNullOrEmpty())return this
        var map = Gson().fromJson(jsonStr,Map::class.java)
        this::class.declaredMemberProperties.forEach {
            if(it.returnType == String::class.createType()){
                if (map[it.name]!=null && it is KMutableProperty<*>){
                    it.setter.call(getThis(),map[it.name])
                    LOG.I("123","set   ${it.name} ${map[it.name]}")
                }
            }
        }
        return this
    }

    fun setOnDataChangedListener(cb: (String,String)->Unit): com.sribs.common.bean.BaseDataBean {
        mCb = object : com.sribs.common.bean.BaseDataBean.OnDataChangedListener {
            override fun onChanged(name: String, data: String) {
                cb(name,data )
            }

        }
        return this
    }

    abstract fun setData(b: com.sribs.common.bean.BaseDataBean)

    interface OnDataChangedListener{
        fun onChanged(name:String,data:String)
    }

    open fun copy(): com.sribs.common.bean.BaseDataBean? = null

}