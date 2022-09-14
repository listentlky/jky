package com.sribs.bdd.aop
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SingleClick(val timeInterval:Long = 600)