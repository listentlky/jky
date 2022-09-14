package com.sribs.bdd.bean.data;

import androidx.annotation.NonNull;

public class DamageSectionDetail {
    String desc;
    Long ref;

    public DamageSectionDetail(@NonNull final String desc, @NonNull final Long ref) {
        this.desc = desc;
        this.ref = ref;
    }

    public String getDamageDesc(){
        return desc;
    }

    public void setDamageDesc(String ds){
        desc = ds;
    }

    public Long getDamageRef(){
        return ref;
    }

    public void setDamageRef(Long r){
        ref = r;
    }

    public String toString(){
        return "desc=" + desc + ", ref=" + ref;
    }
}
