package com.sribs.common.bean.db

import com.cbj.sdk.libbase.utils.LOG
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties

/**
 * @date 2021/7/30
 * @author elijah
 * @Description
 */
abstract class BaseDbBean {
    abstract fun getThis(): BaseDbBean

    fun isSame(b:BaseDbBean):Boolean {
        var map = HashMap<String,Any>()
        this::class.declaredMemberProperties.forEach {
            LOG.I("123","prop name= ${it.name}  ${    it.getter.call(getThis())}")
            var value =   it.getter.call(getThis())
            map[it.name] = value as Any
        }
        var isSame = true
        b::class.declaredMemberProperties.forEach {
            var value = it.getter.call(b)
            if (map[it.name] != value){
                isSame = false
                return@forEach
            }
        }
        return isSame
    }

    fun copy():BaseDbBean{
        var map = HashMap<String,Any>()
        this::class.declaredMemberProperties.forEach {
            LOG.I("123","prop name= ${it.name}  ${    it.getter.call(getThis())}")
            var value =   it.getter.call(getThis())
            map[it.name] = value as Any
        }
        var new = getThis()::class.createInstance()
        new::class.declaredMemberProperties.forEach {
            if (it is KMutableProperty<*>){
                it.setter.call(new,map[it.name])
            }
        }
        return new
    }
}