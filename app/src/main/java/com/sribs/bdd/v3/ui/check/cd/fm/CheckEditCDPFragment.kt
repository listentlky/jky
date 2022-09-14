package com.sribs.bdd.v3.ui.check.cd.fm

import android.widget.ArrayAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.cbj.sdk.libui.mvp.BaseFragment
import com.cbj.sdk.libui.mvp.bindView
import com.sribs.bdd.R
import com.sribs.bdd.databinding.FragmentCheckComponentdetectionBeamEditBinding
import com.sribs.bdd.databinding.FragmentCheckComponentdetectionPlateEditBinding
import com.sribs.bdd.databinding.FragmentCheckComponentdetectionWallEditBinding
import com.sribs.bdd.v3.module.CheckMenuModule
import com.sribs.common.ARouterPath

@Route(path = ARouterPath.CHECK_COMPONENT_DETECTION_PLATE_FRAGMENT)
class CheckEditCDPFragment : BaseFragment(R.layout.fragment_check_componentdetection_plate_edit) {

    private val mBinding: FragmentCheckComponentdetectionPlateEditBinding by bindView()

    private var mMenuList: ArrayList<CheckMenuModule>? = ArrayList()

    private var mLeftSpinner1Adapter: ArrayAdapter<String>? = null
    private var mLeftSpinner2Adapter: ArrayAdapter<String>? = null


    override fun deinitView() {

    }

    override fun initView() {


        /* mLeftSpinner1Adapter = ArrayAdapter<String>(
             (context as CheckComponentDetectionActivity),
             R.layout.spinner_select_item
         )
         mLeftSpinner2Adapter = ArrayAdapter<String>(
             (context as CheckComponentDetectionActivity),
             R.layout.spinner_select_item
         )


         mBinding.checkCpdLeftSpinner1.adapter = mLeftSpinner1Adapter

         mBinding.checkCpdLeftSpinner1.setOnItemClickListener { parent, view, position, id ->
             Log.e("checkCpdLeftSpinner1", "" + position)
         }

         mBinding.checkCpdLeftSpinner2.adapter = mLeftSpinner2Adapter

         mBinding.checkCpdLeftSpinner2.setOnItemClickListener { parent, view, position, id ->

             Log.e("checkCpdLeftSpinner2", "" + position)


         }

         mBinding.checkCpdLeftSpinner2.setOnClickListener {
             Log.e("checkCpdLeftSpinner2", "hhh")

         }*/

    }


}