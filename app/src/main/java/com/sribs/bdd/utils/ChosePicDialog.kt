package com.sribs.bdd.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.bean.BuildingFloorPictureBean
import com.sribs.bdd.databinding.DialogChosePicBinding
import com.sribs.bdd.databinding.DialogCreateProjectBinding
import com.sribs.bdd.ui.adapter.CreateChoseFloorPicAdapter

class ChosePicDialog(context:Context,var list:ArrayList<BuildingFloorPictureBean>,
                     var onResult: (chosedLisr: ArrayList<BuildingFloorPictureBean>) -> Unit): Dialog(context) {


    private val mBinding: DialogChosePicBinding by inflate()

    private val adapter:CreateChoseFloorPicAdapter by lazy {
        CreateChoseFloorPicAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        setCanceledOnTouchOutside(true)
        mBinding.cancelButton.setOnClickListener {
            dismiss()
        }

        mBinding.confirmBtn.setOnClickListener {
            onResult.invoke(adapter.getChosedList())
            dismiss()
        }

        mBinding.choseAll.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                adapter.choseAll()
            }else{
                adapter.choseNone()
            }
        }
        adapter.setData(list)
        mBinding.picRecycleview.layoutManager = LinearLayoutManager(context)
        mBinding.picRecycleview.adapter = adapter

    }


}