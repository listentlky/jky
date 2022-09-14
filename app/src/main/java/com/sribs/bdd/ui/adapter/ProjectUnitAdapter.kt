package com.sribs.bdd.ui.adapter

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.cbj.sdk.libbase.utils.LOG

/**
 * @date 2021/6/30
 * @author elijah
 * @Description
 */
class ProjectUnitAdapter(fm: FragmentManager,var list:List<Fragment>):FragmentStatePagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    var views:HashMap<Int, View> = HashMap()

    fun updateList(l:List<Fragment>){
        list = l
        notifyDataSetChanged()
    }

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): Fragment = list[position]

    override fun getItemPosition(obj: Any): Int {
        var idx = list.indexOf(obj)
        LOG.I("123","getItemPosition idx=$idx")
        return if (idx == -1) POSITION_NONE else idx
//        return POSITION_NONE
    }

//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        //super.instantiateItem(container, position) as Fragment
//
//
//    }
//
//    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        //super.destroyItem(container, position, `object`)
//    }

}