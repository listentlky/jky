package com.sribs.bdd.v3.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import com.cbj.sdk.libui.mvp.inflate
import com.sribs.bdd.R
import com.sribs.bdd.databinding.DialogCheckRhdEditBinding

/**
 * create time: 2022/9/7
 * author: bruce
 * description:
 */
class checkRHDDialog(context: Context) : Dialog(context, R.style.transDialog) {

    val mBinding : DialogCheckRhdEditBinding by inflate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        initWindow()
        mBinding.checkRhdRightLayout.setOnClickListener {
            hide()
        }
    }

    private fun initWindow() {
       /* window?.navigationBarColor = Color.WHITE
        window?.attributes.also {
          WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }*/
        window?.setWindowAnimations(R.style.transDialog_animation)
        window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
        window?.setGravity(Gravity.LEFT)
        window?.setDimAmount(0f);
    }
}