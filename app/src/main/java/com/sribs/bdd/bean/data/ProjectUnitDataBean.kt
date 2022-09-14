package com.sribs.bdd.bean.data

import com.cbj.sdk.libbase.utils.LOG

/**
 * @date 2021/6/30
 * @author elijah
 * @Description 项目 单元列表数据
 */
class ProjectUnitDataBean: com.sribs.common.bean.BaseDataBean() {
    override fun getThis(): com.sribs.common.bean.BaseDataBean = this
    override fun setData(b: com.sribs.common.bean.BaseDataBean) {
        if (b !is ProjectUnitDataBean)return
        LOG.I("123","ProjectUnitDataBean  set data $b")
        b.unitList.forEach {
            addUnitWithName(it.first,it.second)
        }
    }

    var unitList = ArrayList<Pair<Long?,String>>()

    fun addUnitWithName(unitId:Long?,name:String){
        unitList.add(Pair(unitId,name))
        callbackMap[::addUnit.name]?.invoke(arrayOf(name))
    }

    fun addUnit(unitId:Long?=null):Long?{
        var id = unitId
        if(unitList.find { it.first!=null && it.first ==unitId }.also { id=it?.first }!=null){
            return id
        }
        var n = unitList.size + 1
        var unitName = "单元$n"
        unitList.add(Pair(unitId,unitName))
        callbackMap[::addUnit.name]?.invoke(arrayOf(unitName))
        return unitId
    }

    fun getNextAutoUnit():Int = unitList.size+1

    fun updateUnit(pos:Int,num:Int){
        if (pos < 0 || pos > unitList.size-1)return
        var tag = "单元$num"
        var p = unitList[pos]
        unitList[pos] = Pair(p.first,tag)
        callbackMap[::updateUnit.name]?.invoke(arrayOf("$pos",tag))
    }

    fun delUnit(pos:Int):Long{
        LOG.I("123","delUnit pos=$pos   unitListSize=${unitList.size}")
        if (pos < 0 || pos > unitList.size-1)return -1
        var b = unitList.removeAt(pos)
        callbackMap[::delUnit.name]?.invoke(arrayOf("$pos"))
        return b.first?:-1
    }

    fun bindAddFun(cb:(Array<out String>)->Unit):ProjectUnitDataBean{
        bindCallback(::addUnit.name,cb)
        return this
    }

    fun bindUpdateFun(cb:(Array<out String>)->Unit):ProjectUnitDataBean{
        bindCallback(::updateUnit.name,cb)
        return this
    }

    fun bindRemoveFun(cb:(Array<out String>)->Unit):ProjectUnitDataBean{
        bindCallback(::delUnit.name,cb)
        return this
    }

    override fun copy(): com.sribs.common.bean.BaseDataBean? =
        ProjectUnitDataBean().also { it.setData(this) }
}