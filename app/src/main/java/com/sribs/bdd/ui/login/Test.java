package com.sribs.bdd.ui.login;

import com.cbj.sdk.libbase.utils.LOG;
import com.sribs.bdd.aop.SingleClick;


/**
 * @author elijah
 * @date 2021/8/24
 * @Description
 */
public class Test {
    @SingleClick
    public static void doTest(){
        Test t = new Test();
        t.test2();
    }

    @SingleClick
    public void test2(){
        LOG.INSTANCE.I("123","test2");
    }

}
