package com.sribs.bdd.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.bean.data.ModuleFloorPictureBean
import com.sribs.bdd.databinding.DialogChosePicBinding
import com.sribs.bdd.v3.adapter.CreateChoseModuleFloorPicAdapter

class ChoseModulePicDialog(context:Context, var list:ArrayList<ModuleFloorPictureBean>,var selected:ArrayList<String>,
                           var onResult: (chosedLisr: ArrayList<ModuleFloorPictureBean>) -> Unit): Dialog(context) {


    private val mBinding: DialogChosePicBinding by inflate()

    private val adapter: CreateChoseModuleFloorPicAdapter by lazy {
        CreateChoseModuleFloorPicAdapter()
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
        adapter.setContext(context)
        adapter.setData(list)
        adapter.setSelected(selected)
        mBinding.picRecycleview.layoutManager = LinearLayoutManager(context)
        mBinding.picRecycleview.adapter = adapter

    }


}